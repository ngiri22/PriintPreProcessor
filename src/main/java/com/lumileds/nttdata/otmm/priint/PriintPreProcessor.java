package com.lumileds.nttdata.otmm.priint;

import java.io.File;
import java.sql.Connection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lumileds.nttdata.otmm.priint.config.ProcessorConstants;
import com.lumileds.nttdata.otmm.priint.data.AssetMetadata;
import com.lumileds.nttdata.otmm.priint.repository.SQLRepository;
import com.lumileds.nttdata.otmm.priint.util.AssetsXMLWriter;
import com.lumileds.nttdata.otmm.priint.util.CmdLiner;
import com.lumileds.nttdata.otmm.priint.util.FilesUtility;
import com.lumileds.nttdata.otmm.priint.util.OTMMRestClient;
import com.lumileds.nttdata.otmm.priint.util.XMLParser;

public class PriintPreProcessor {

	private static final Logger logger = LoggerFactory.getLogger(PriintPreProcessor.class);

	public static void main(String[] args) {

		logger.debug("******Start of Command Liner*******");

		//fileMover.moveFiles();

		processAssets();

		//Sleep for 3 minutes so that the Bulk Import xml
		//file becomes old enough to be picked up.
		
		try {
			
			TimeUnit.MINUTES.sleep(3);
		
		} catch (InterruptedException intEx) {
			
			logger.error("Interrupted Exception: {}", intEx);
		}
		
		//Call BulkImportUtlity for Drafts
		CmdLiner.runBulkImport(ProcessorConstants.DRAFT_BATCH_EXEC);

		//Call BulkImportUtility for Final version
		CmdLiner.runBulkImport(ProcessorConstants.FINAL_PIS_BATCH_EXEC);
		CmdLiner.runBulkImport(ProcessorConstants.FINAL_PRS_BATCH_EXEC);
		CmdLiner.runBulkImport(ProcessorConstants.FINAL_CPIS_BATCH_EXEC);
		
		logger.debug("******End of Command Liner*******");
	}

	private static void processAssets() {

		FilesUtility filesUtility = new FilesUtility();


		List<File> finalXMLFilesList = filesUtility.getFinalXMLFilesList();


		if (finalXMLFilesList.size() > 0 ) {

			//Backup the files before processing
			
			filesUtility.backupFiles(
					new File(ProcessorConstants.BASE_FOLDER),
					new File(ProcessorConstants.ARCHIVE_FOLDER)
					);
			
			logger.debug("Total number of final list "
					+ "of XML files to be processed : {}",
					finalXMLFilesList.size());

			XMLParser xmlParser = new XMLParser();

			//List<AssetMetadata> totalAssetsList = new ArrayList<AssetMetadata>();
			
			Set<AssetMetadata> totalAssetsSet = new HashSet<AssetMetadata>();

			Set<String> folderSet = new HashSet<String>();
			
			for (File xmlFile : finalXMLFilesList) {

				try {

					//List<AssetMetadata> assetMetadataList = xmlParser.getAssets(xmlFile);
					
					logger.debug("Processing xml file: {} ", xmlFile.getName());

					Set<AssetMetadata> assetMetadataSet = xmlParser.getAssets(xmlFile);
					
					for ( AssetMetadata assetMetadata : assetMetadataSet) {
						
						folderSet.add(assetMetadata.getFolderName());
						
					}
					
					totalAssetsSet.addAll(assetMetadataSet);

				} catch (DocumentException docEx) {

					logger.error("DocumentException: {} ", docEx);

				}

				//Delete the xml file, since its no longer needed.
				xmlFile.delete();

			}
			
			logger.debug("Assets set size is : {}", totalAssetsSet.size());
			
			createBatchXMLs(totalAssetsSet);

			filesUtility.moveFiles(totalAssetsSet);
			
			if ( folderSet.size() > 0) {
				filesUtility.deleteFolders(folderSet);
			}
			
			OTMMRestClient otmmRestClient = new OTMMRestClient();
			
			String otmmAuthToken = otmmRestClient.getSessionToken();

			for (AssetMetadata assetMetadata : totalAssetsSet) {
				
				String latestAssetVersionUoiID = assetMetadata.getLatestOTMMVersionUoiID();
				
				
				// If the asset already exists,
				// add the asset as Version.
				
				if (null != latestAssetVersionUoiID) {
					
					otmmRestClient.checkInAsset(
							latestAssetVersionUoiID,
							otmmAuthToken,
							assetMetadata.getDestinationFolder() +
							ProcessorConstants.BACK_SLASH +
							assetMetadata.getName()
							);
					
				}
				
			}
			
		} 

	}

	private static void createBatchXMLs(Set<AssetMetadata> totalAssetsSet) {

		AssetsXMLWriter xmlWriter = new AssetsXMLWriter();

		Set<AssetMetadata> draftAssetsSet = new HashSet<AssetMetadata>();
		Set<AssetMetadata> pisAssetsSet = new HashSet<AssetMetadata>();
		Set<AssetMetadata> prsAssetsSet = new HashSet<AssetMetadata>();
		Set<AssetMetadata> cpisAssetsSet = new HashSet<AssetMetadata>();

		
		SQLRepository sqlRepository = new SQLRepository();

		//Get the SQL Connection
		Connection conn = sqlRepository.createConnection();

		for (AssetMetadata assetMetadata : totalAssetsSet) {

			String destinationFolder;
			
			String latestOTMMVersionUoiID ;
			
			String assetName = assetMetadata.getName();

			if (assetName.contains(ProcessorConstants.DRAFT_PATTERN)) {

				logger.debug("Adding to Draft FileSet");
				
				latestOTMMVersionUoiID = sqlRepository.getLatestOTMMVersionUoiID(
						conn,
						assetName,
						ProcessorConstants.OTMM_DRAFT_FOLDER_ID);

				if (null != latestOTMMVersionUoiID) {
				
					destinationFolder = ProcessorConstants.DRAFT_FOLDER
							+ ProcessorConstants.BACK_SLASH
							+ ProcessorConstants.VERSIONS ;
					
					assetMetadata.setLatestOTMMVersionUoiID(latestOTMMVersionUoiID);
				}
				else {
					
					destinationFolder = ProcessorConstants.DRAFT_FOLDER
							+ ProcessorConstants.BACK_SLASH
							+ ProcessorConstants.ORIGINAL ;
					
					draftAssetsSet.add(assetMetadata);
				}

			}
			else {

				//Check if PIS,PRS or CPIS
				if (assetMetadata.getName().
						contains(ProcessorConstants.FINAL_CPIS_PATTERN)) {
					
					logger.debug("Adding to Final CPIS FileSet");
					
					latestOTMMVersionUoiID = sqlRepository.getLatestOTMMVersionUoiID(
							conn,
							assetName,
							ProcessorConstants.OTMM_CPIS_FOLDER_ID);

					if (null != latestOTMMVersionUoiID) {
					
						destinationFolder = ProcessorConstants.FINAL_CPIS_FOLDER
								+ ProcessorConstants.BACK_SLASH
								+ ProcessorConstants.VERSIONS ;
						
						assetMetadata.setLatestOTMMVersionUoiID(latestOTMMVersionUoiID);
						
					}
					else {
						
						destinationFolder = ProcessorConstants.FINAL_CPIS_FOLDER
								+ ProcessorConstants.BACK_SLASH
								+ ProcessorConstants.ORIGINAL ;
						
						pisAssetsSet.add(assetMetadata);
					}					

				}
				else if (assetMetadata.getName().
						contains(ProcessorConstants.FINAL_PRS_PATTERN)) {

					logger.debug("Adding to Final PRS FileSet");
					
					latestOTMMVersionUoiID = sqlRepository.getLatestOTMMVersionUoiID(
							conn,
							assetName,
							ProcessorConstants.OTMM_PRS_FOLDER_ID);
					
					logger.debug("Latest version UOI ID: {}", latestOTMMVersionUoiID);

					if (null != latestOTMMVersionUoiID) {
					
						destinationFolder = ProcessorConstants.FINAL_PRS_FOLDER
								+ ProcessorConstants.BACK_SLASH
								+ ProcessorConstants.VERSIONS ;
						
						assetMetadata.setLatestOTMMVersionUoiID(latestOTMMVersionUoiID);
						
					}					
					else {
						
						destinationFolder = ProcessorConstants.FINAL_PRS_FOLDER
								+ ProcessorConstants.BACK_SLASH
								+ ProcessorConstants.ORIGINAL ;
						
						prsAssetsSet.add(assetMetadata);
					}

				}
				else {

					logger.debug("Adding to Final PIS FileSet");
					
					latestOTMMVersionUoiID = sqlRepository.getLatestOTMMVersionUoiID(
							conn,
							assetName,
							ProcessorConstants.OTMM_PIS_FOLDER_ID);

					if (null != latestOTMMVersionUoiID) {
					
						destinationFolder = ProcessorConstants.FINAL_PIS_FOLDER
								+ ProcessorConstants.BACK_SLASH
								+ ProcessorConstants.VERSIONS ;
						
						assetMetadata.setLatestOTMMVersionUoiID(latestOTMMVersionUoiID);
						
					}
					else {
						
						destinationFolder = ProcessorConstants.FINAL_PIS_FOLDER
								+ ProcessorConstants.BACK_SLASH
								+ ProcessorConstants.ORIGINAL ;
						
						cpisAssetsSet.add(assetMetadata);
					}
					
				}

			}

			assetMetadata.setDestinationFolder(destinationFolder);

		} 

		xmlWriter.generateXML(draftAssetsSet, ProcessorConstants.DRAFT_PATTERN);
		xmlWriter.generateXML(cpisAssetsSet, ProcessorConstants.FINAL_CPIS_PATTERN);
		xmlWriter.generateXML(prsAssetsSet, ProcessorConstants.FINAL_PRS_PATTERN);
		xmlWriter.generateXML(pisAssetsSet, ProcessorConstants.FINAL_PIS_PATTERN);
		

	}

}

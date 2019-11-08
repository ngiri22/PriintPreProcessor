package com.lumileds.nttdata.otmm.priint;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lumileds.nttdata.otmm.priint.config.ProcessorConstants;
import com.lumileds.nttdata.otmm.priint.data.AssetMetadata;
import com.lumileds.nttdata.otmm.priint.util.AssetsXMLWriter;
import com.lumileds.nttdata.otmm.priint.util.CmdLiner;
import com.lumileds.nttdata.otmm.priint.util.FilesUtility;
import com.lumileds.nttdata.otmm.priint.util.XMLParser;

public class PriintPreProcessor {

	private static final Logger logger = LoggerFactory.getLogger(PriintPreProcessor.class);

	public static void main(String[] args) {

		logger.debug("******Start of Command Liner*******");

		//fileMover.moveFiles();

		processAssets();

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

		} 

	}

	private static void createBatchXMLs(Set<AssetMetadata> totalAssetsSet) {

		AssetsXMLWriter xmlWriter = new AssetsXMLWriter();

		Set<AssetMetadata> draftAssetsSet = new HashSet<AssetMetadata>();
		Set<AssetMetadata> pisAssetsSet = new HashSet<AssetMetadata>();
		Set<AssetMetadata> prsAssetsSet = new HashSet<AssetMetadata>();
		Set<AssetMetadata> cpisAssetsSet = new HashSet<AssetMetadata>();


		for (AssetMetadata assetMetadata : totalAssetsSet) {


			String destinationFolder;

			if (assetMetadata.getName().
					contains(ProcessorConstants.DRAFT_PATTERN)) {

				logger.debug("Adding to Draft FileSet");

				destinationFolder = ProcessorConstants.DRAFT_FOLDER;

				draftAssetsSet.add(assetMetadata);


			}
			else {

				//Check if PIS,PRS or CPIS
				if (assetMetadata.getName().
						contains(ProcessorConstants.FINAL_CPIS_PATTERN)) {

					destinationFolder = ProcessorConstants.FINAL_CPIS_FOLDER;

					logger.debug("Adding to Final CPIS FileSet");

					pisAssetsSet.add(assetMetadata);

				}
				else if (assetMetadata.getName().
						contains(ProcessorConstants.FINAL_PRS_PATTERN)) {

					destinationFolder = ProcessorConstants.FINAL_PRS_FOLDER;

					logger.debug("Adding to Final PRS FileSet");

					prsAssetsSet.add(assetMetadata);

				}
				else {

					destinationFolder = ProcessorConstants.FINAL_PIS_FOLDER;

					logger.debug("Adding to Final PIS FileSet");

					cpisAssetsSet.add(assetMetadata);

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

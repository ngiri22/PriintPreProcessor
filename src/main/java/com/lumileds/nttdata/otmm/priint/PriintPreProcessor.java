package com.lumileds.nttdata.otmm.priint;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

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

		logger.debug("******Start of Program*******");

		//fileMover.moveFiles();

		processAssets();

		//Call BulkImportUtlity for Drafts
		//CmdLiner.runBulkImport(false);

		//Call BulkImportUtility for Final version
		//CmdLiner.runBulkImport(true);

		logger.debug("******End of Program*******");
	}

	private static void processAssets() {

		FilesUtility filesUtility = new FilesUtility();


		List<File> finalXMLFilesList = filesUtility.getFinalXMLFilesList();


		if (finalXMLFilesList.size() > 0 ) {

			logger.debug("Total number of final list "
					+ "of XML files to be processed : {}",
					finalXMLFilesList.size());

			XMLParser xmlParser = new XMLParser();

			List<AssetMetadata> totalAssetsList = new ArrayList<AssetMetadata>();

			for (File xmlFile : finalXMLFilesList) {

				try {

					List<AssetMetadata> assetMetadataList = xmlParser.getAssets(xmlFile);

					totalAssetsList.addAll(assetMetadataList);

				} catch (DocumentException docEx) {

					logger.error("DocumentException: {} ", docEx);

				}

				//backupFiles();

				filesUtility.backupFiles(
						new File(ProcessorConstants.BASE_FOLDER),
						new File(ProcessorConstants.ARCHIVE_FOLDER)
						);

				createBatchXMLs(totalAssetsList);

				filesUtility.moveFiles(totalAssetsList);

				xmlFile.delete();

			}

		} 

	}

	private static void createBatchXMLs(List<AssetMetadata> totalAssetsList) {

		AssetsXMLWriter xmlWriter = new AssetsXMLWriter();

		List<AssetMetadata> draftAssetsList = new ArrayList<AssetMetadata>();
		List<AssetMetadata> pisAssetsList = new ArrayList<AssetMetadata>();
		List<AssetMetadata> prsAssetsList = new ArrayList<AssetMetadata>();
		List<AssetMetadata> cpisAssetsList = new ArrayList<AssetMetadata>();


		for (AssetMetadata assetMetadata : totalAssetsList) {


			String destinationFolder;

			if (assetMetadata.getName().
					contains(ProcessorConstants.DRAFT_PATTERN)) {

				logger.debug("Moving to Draft Folder");

				destinationFolder = ProcessorConstants.DRAFT_FOLDER;

				draftAssetsList.add(assetMetadata);


			}
			else {

				logger.debug("Moving to Final Folder");

				//Check if PIS,PRS or CPIS
				if (assetMetadata.getName().
						contains(ProcessorConstants.FINAL_PIS_PATTERN)) {

					destinationFolder = ProcessorConstants.FINAL_PIS_FOLDER;

					logger.debug("Moving to Final PIS Folder");

					pisAssetsList.add(assetMetadata);

				}
				else if (assetMetadata.getName().
						contains(ProcessorConstants.FINAL_PRS_PATTERN)) {

					destinationFolder = ProcessorConstants.FINAL_PRS_FOLDER;

					logger.debug("Moving to Final PRS Folder");

					prsAssetsList.add(assetMetadata);

				}
				else {

					destinationFolder = ProcessorConstants.FINAL_CPIS_FOLDER;

					logger.debug("Moving to Final CPIS Folder");

					cpisAssetsList.add(assetMetadata);

				}

			}

			assetMetadata.setDestinationFolder(destinationFolder);

		} 

		xmlWriter.generateXML(draftAssetsList, ProcessorConstants.DRAFT_PATTERN);
		xmlWriter.generateXML(pisAssetsList, ProcessorConstants.FINAL_PIS_PATTERN);
		xmlWriter.generateXML(prsAssetsList, ProcessorConstants.FINAL_PRS_PATTERN);
		xmlWriter.generateXML(cpisAssetsList, ProcessorConstants.FINAL_CPIS_PATTERN);

	}

}

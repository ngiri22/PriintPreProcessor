package com.lumileds.nttdata.otmm.priint.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lumileds.nttdata.otmm.priint.config.ProcessorConstants;
import com.lumileds.nttdata.otmm.priint.data.AssetMetadata;


public class FileMover {

	private final Logger logger = LoggerFactory.getLogger(FileMover.class);

	public void moveFiles() {

		//recursiveMoveFiles();

	}


	/*
	 * Get only xml files to read
	 */
	private File[] getXMLFiles() {

		File directoryName = new File(ProcessorConstants.BASE_FOLDER);

		FilenameFilter fileNameFilter = new FilenameFilter() {

			@Override
			public boolean accept(File directoryName, String fileNamePattern) {

				if( fileNamePattern.lastIndexOf('.') > 0 ) {

					//get last index for '.' char
					int lastIndex = fileNamePattern.lastIndexOf('.');

					//get extension
					String str = fileNamePattern.substring(lastIndex);

					// match path name extension
					if(str.equals(ProcessorConstants.XML_EXTENSION)) {
						return true;
					}

				}

				return false;
			};

		};

		File[] xmlFileList = directoryName.listFiles(fileNameFilter);


		//Sort as per last modified time
		Arrays.sort(xmlFileList, Comparator.comparingLong(File::lastModified));

		return xmlFileList;

	}

	/*
	 * This method checks if any of the XML file's
	 * last modified time is within last 5 minutes
	 * and returns only older files.
	 */
	private List<File> getfinalXMLFilesList(File[] xmlSortedFiles) {

		List<File> finalXMLFilesList = new ArrayList<File>() ;

		long cutOff = System.currentTimeMillis() - ProcessorConstants.TIME_DELAY_IN_MICROSECONDS;

		//boolean checkFileFlag = false ;

		for (File xmlFile : xmlSortedFiles) {

			if ( xmlFile.lastModified() > cutOff ) {

				logger.debug("XML file {} is not older than 5 mins", xmlFile.getName());

				continue;

			}

			finalXMLFilesList.add(xmlFile);

			//checkFileFlag = true ;
		}

		//		if (checkFileFlag) {
		//
		//			return true;
		//
		//		}

		//logger.debug("No files to process, returning false");
		return finalXMLFilesList ;
	}

	public void processXMLFiles() {

		File[] xmlSortedFiles = getXMLFiles();

		if (xmlSortedFiles.length > 0 ) {

			logger.debug("Number of XML files to be processed : {}", xmlSortedFiles.length);

			List<File> finalXMLFilesList = getfinalXMLFilesList(xmlSortedFiles);

			if (finalXMLFilesList.size() > 0 ) {

				logger.debug("Total number of final list of XML files to be processed : {}", finalXMLFilesList.size());

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

					backupFiles(
							new File(ProcessorConstants.BASE_FOLDER),
							new File(ProcessorConstants.ARCHIVE_FOLDER)
							);

					moveFiles(totalAssetsList);

				}

			} 
		}

	}


	private void backupFiles(File sourceFolder, File destinationFolder)
	{

		try {
			//Check if sourceFolder is a directory or file
			//If sourceFolder is file; then copy the file directly to new location
			if (sourceFolder.isDirectory())
			{
				//Verify if destinationFolder is already present; If not then create it
				if (!destinationFolder.exists())
				{
					destinationFolder.mkdir();
					logger.debug("Directory created :: " + destinationFolder);
				}

				//Get all files from source directory
				String files[] = sourceFolder.list();

				//Iterate over all files and copy them to destinationFolder one by one
				for (String file : files)
				{
					File srcFile = new File(sourceFolder, file);
					File destFile = new File(destinationFolder, file);

					//Recursive function call
					backupFiles(srcFile, destFile);
				}
			}
			else
			{
				//Copy the file content from one place to another
				Files.copy(sourceFolder.toPath(), 
						destinationFolder.toPath(),
						StandardCopyOption.REPLACE_EXISTING);
				logger.debug("File copied :: " + destinationFolder);
			}

		} catch (IOException ioEx) {

			logger.error("IOException while copying files: {}", ioEx);

		}

	}


//	private void backupFiles() {
//
//		long cutOff = System.currentTimeMillis() - ProcessorConstants.TIME_DELAY_IN_MICROSECONDS; //(5 * 60 * 1000);
//
//
//		try {
//
//			Files.list(
//					Paths.get(ProcessorConstants.BASE_FOLDER)
//					)
//			.forEach(path -> {
//
//				try {
//
//					if( Files.getLastModifiedTime(path, 
//							new LinkOption[] {LinkOption.NOFOLLOW_LINKS})
//							.to(TimeUnit.MILLISECONDS) < cutOff
//							) {
//
//						logger.debug("Source File Name: " + path.getFileName().toString());
//
//						//Create a copy in archive folder
//						Files.copy(path, 
//								new File(
//										ProcessorConstants.ARCHIVE_FOLDER +
//										ProcessorConstants.BACK_SLASH +
//										path.getFileName()
//										).toPath(),
//								StandardCopyOption.REPLACE_EXISTING
//								);
//					}
//
//				} catch (IOException ioEx) {
//
//					logger.error("IOException while copying files: {}", ioEx);
//
//				}
//
//			});
//		} catch (IOException ioEx) {
//
//			logger.error("IOException while reading files from Base Directory: {}", ioEx);
//
//		}
//	}


	private void moveFiles(List<AssetMetadata> totalAssetsList) {

		try {

			AssetsXMLWriter xmlWriter = new AssetsXMLWriter();

			//			boolean isNewDraft = true;
			//			boolean isNewPIS = true ;
			//			boolean isNewPRS = true;
			//			boolean isNewCPIS = true ;
			//			
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


				Files.move(
						Paths.get(
								ProcessorConstants.BASE_FOLDER +
								ProcessorConstants.BACK_SLASH +
								assetMetadata.getFolderName() +
								ProcessorConstants.BACK_SLASH +
								assetMetadata.getName()
								), 
						new File(
								destinationFolder +
								ProcessorConstants.BACK_SLASH +
								assetMetadata.getName()
								).toPath(),
						StandardCopyOption.REPLACE_EXISTING
						);

				//							    	if (Files.isDirectory(path)) {
				//					            try {
				//					                recursiveDeleteFilesOlderThanNDays(days, path.toString());
				//					            } catch (IOException e) {
				//					                // log here and move on
				//					            }
				//					        } else {
				//					            try {
				//					                if (Files.getLastModifiedTime(path).to(TimeUnit.MILLISECONDS) < cutOff) {
				//					                    Files.delete(path);
				//					                }
			} 

			xmlWriter.generateXML(draftAssetsList, ProcessorConstants.DRAFT_PATTERN);
			xmlWriter.generateXML(pisAssetsList, ProcessorConstants.FINAL_PIS_PATTERN);
			xmlWriter.generateXML(prsAssetsList, ProcessorConstants.FINAL_PRS_PATTERN);
			xmlWriter.generateXML(cpisAssetsList, ProcessorConstants.FINAL_CPIS_PATTERN);

		}catch (IOException ioEx) {

			logger.error("IOException while moving files: {}", ioEx);

		}



	}

}

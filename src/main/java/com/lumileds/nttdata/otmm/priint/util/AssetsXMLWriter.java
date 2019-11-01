package com.lumileds.nttdata.otmm.priint.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.lumileds.nttdata.otmm.priint.config.ProcessorConstants;
import com.lumileds.nttdata.otmm.priint.data.AssetMetadata;

public class AssetsXMLWriter {

	private final Logger logger = LoggerFactory.getLogger(FilesUtility.class);

	public void generateXML(List<AssetMetadata> assetMetadataList, 
			CharSequence folderPattern) {

		String metadataXML ;

		if (folderPattern.toString().contains(ProcessorConstants.DRAFT_PATTERN)) {

			metadataXML = ProcessorConstants.DRAFT_FOLDER + 
					ProcessorConstants.BACK_SLASH +
					ProcessorConstants.OUTPUT_XML_FILE +
					ProcessorConstants.UNDER_SCORE +
					System.currentTimeMillis() + 
					ProcessorConstants.XML_EXTENSION ;

		}
		else if (folderPattern.toString().contains(ProcessorConstants.FINAL_PIS_PATTERN)) {

			metadataXML = ProcessorConstants.FINAL_PIS_FOLDER + 
					ProcessorConstants.BACK_SLASH +
					ProcessorConstants.OUTPUT_XML_FILE +
					ProcessorConstants.UNDER_SCORE +
					System.currentTimeMillis() + 
					ProcessorConstants.XML_EXTENSION ;

		}
		else if (folderPattern.toString().contains(ProcessorConstants.FINAL_PRS_PATTERN)) {

			metadataXML = ProcessorConstants.FINAL_PIS_FOLDER + 
					ProcessorConstants.BACK_SLASH +
					ProcessorConstants.OUTPUT_XML_FILE +
					ProcessorConstants.UNDER_SCORE +
					System.currentTimeMillis() + 
					ProcessorConstants.XML_EXTENSION ;

		}
		else  {

			metadataXML = ProcessorConstants.FINAL_CPIS_FOLDER + 
					ProcessorConstants.BACK_SLASH +
					ProcessorConstants.OUTPUT_XML_FILE +
					ProcessorConstants.UNDER_SCORE +
					System.currentTimeMillis() + 
					ProcessorConstants.XML_EXTENSION ;

		}

		if ( assetMetadataList.size() > 0 ) {

			try {

				ByteArrayOutputStream bos = new ByteArrayOutputStream();

				PrintWriter writer = new PrintWriter(
						new OutputStreamWriter(bos, 
								ProcessorConstants.CHARSET));

				//write out the doctype or include xml prolog if so desired
				writer.println("<?xml version=\"1.0\" encoding=\"" + ProcessorConstants.CHARSET + "\" ?>");

				writer.println("<!DOCTYPE TEAMS_ASSET_FILE [");

				int j = 0 ;
				
				for ( AssetMetadata assetMetadata : assetMetadataList) {

					j = j + 1 ;
					
					writer.println("<!ENTITY file_"+ j +" SYSTEM \""+ 
							assetMetadata.getName() + 
							"\" NDATA application_pdf>");

				}

				writer.println("]>");
				writer.flush();
				writer.close();


				//section create Document
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = dbf.newDocumentBuilder();
				Document doc = builder.newDocument();

				Element teamsAssetFileElement= 
						doc.createElement(ProcessorConstants.XML_TEAMS_ASSET_FILE_ELEMENT);

				Element assetsElement = 
						doc.createElement(ProcessorConstants.XML_ASSETS_ELEMENT);

				

				try {

					int i = 0 ;

					for ( AssetMetadata assetMetadata : assetMetadataList) {

						i = i + 1 ;
						
						Element assetElement = 
								doc.createElement(ProcessorConstants.XML_ASSET_ELEMENT);

						Element metadataElement = 
								doc.createElement(ProcessorConstants.XML_METADATA_ELEMENT);

						Element uoisElement = 
								doc.createElement(ProcessorConstants.XML_UOIS_ELEMENT);

						Element fileInfoElement = 
								doc.createElement(ProcessorConstants.XML_FILE_INFO_ELEMENT);

						Element digitalHubElement = 
								doc.createElement(ProcessorConstants.XML_DIGITAL_HUB_ELEMENT);

						Element mediaInfoTabularElement = 
								doc.createElement(ProcessorConstants.XML_MEDIA_INFO_TAB_ELEMENT);

						Element mediaInfoElement = 
								doc.createElement(ProcessorConstants.XML_MEDIA_INFO_ELEMENT);

						Element securityPolicyUOISElement = 
								doc.createElement(ProcessorConstants.XML_SECURITY_POLICY_ELEMENT);

						Element contentElement = 
								doc.createElement(ProcessorConstants.XML_CONTENT_ELEMENT);

						Element masterElement = 
								doc.createElement(ProcessorConstants.XML_MASTER_ELEMENT);

						uoisElement.setAttribute(
								ProcessorConstants.XML_AUTHOR_ATTRIBUTE, 
								assetMetadata.getAuthor()
								);

						uoisElement.setAttribute(
								ProcessorConstants.XML_SUBJECT_ATTRIBUTE, 
								assetMetadata.getSubject()
								);

						uoisElement.setAttribute(
								ProcessorConstants.XML_MODEL_ID_ATTRIBUTE, 
								assetMetadata.getModelID()
								);

						fileInfoElement.setAttribute(
								ProcessorConstants.XML_ASSET_OWNER_ATTRIBUTE, 
								assetMetadata.getAssetOwner()
								);

						digitalHubElement.setAttribute(
								ProcessorConstants.XML_WCMS_CONFIDENTIALITY_ATTRIBUTE, 
								assetMetadata.getWcmsConfidentiality()
								);

						mediaInfoTabularElement.setAttribute(
								ProcessorConstants.XML_REGIONS_ATTRIBUTE, 
								assetMetadata.getRegions()
								);

						mediaInfoElement.setAttribute(
								ProcessorConstants.XML_ASSET_TYPE_ATTRIBUTE, 
								assetMetadata.getAssetType()
								);

						mediaInfoElement.setAttribute(
								ProcessorConstants.XML_KEYWORDS_ATTRIBUTE, 
								assetMetadata.getKeywords()
								);

						mediaInfoElement.setAttribute(
								ProcessorConstants.XML_BRAND_ATTRIBUTE, 
								assetMetadata.getBrand()
								);

						securityPolicyUOISElement.setAttribute(
								ProcessorConstants.XML_SECURITY_POLICY_ID_ATTRIBUTE,
								assetMetadata.getSecurityPolicyID());

						masterElement.setAttribute(
								ProcessorConstants.XML_FILE_ATTRIBUTE,
								"file_" + i
								);

						contentElement.appendChild(masterElement);

						uoisElement.appendChild(fileInfoElement);
						uoisElement.appendChild(digitalHubElement);
						uoisElement.appendChild(mediaInfoTabularElement);
						uoisElement.appendChild(mediaInfoElement);
						uoisElement.appendChild(securityPolicyUOISElement);

						metadataElement.appendChild(uoisElement);

						assetElement.appendChild(metadataElement);
						assetElement.appendChild(contentElement);

						assetsElement.appendChild(assetElement);

						
						//					transformer.transform(domSource, 
						//							new StreamResult(
						//									new OutputStreamWriter(
						//											bos,ProcessorConstants.CHARSET
						//											)
						//									));
						//
						//					//finally output ensemble of data
						//					byte[] data = bos.toByteArray();
						//					
						//					
						//					System.out.println(new String(data, 
						//							ProcessorConstants.CHARSET));
					}
					
					
					teamsAssetFileElement.appendChild(assetsElement);

					doc.appendChild(teamsAssetFileElement);

					
					//start outputting doc to byteArrayOutputStream
					TransformerFactory transformerFactory = TransformerFactory.newInstance();

					transformerFactory.setAttribute("indent-number", new Integer(2));

					Transformer transformer = transformerFactory.newTransformer();
					transformer.setOutputProperty(
							OutputKeys.INDENT, "yes");

					//OMIT_XML_DECLARATION so it won't make doctype declaration misplaced
					transformer.setOutputProperty(
							OutputKeys.OMIT_XML_DECLARATION, "yes");

					transformer.setOutputProperty(OutputKeys.ENCODING, ProcessorConstants.CHARSET);

					StreamResult streamResult = new StreamResult( 
							new OutputStreamWriter(
									bos, ProcessorConstants.CHARSET)
							);


					DOMSource domSource = new DOMSource(doc);

					transformer.transform(domSource, streamResult);
					
					FileOutputStream fos = new FileOutputStream(new File(metadataXML));
					
					fos.write(bos.toByteArray());
					fos.flush();
					fos.close();
					
					
				}
				catch (TransformerException tEx) {
					logger.debug("Transformer Exception : {}", tEx);
				} catch (FileNotFoundException fileNFEx) {
					logger.debug("File Not Found Exception : {}", fileNFEx);
				} catch (IOException ioEx) {
					logger.debug("IO Exception : {}", ioEx);
				}

			}

			catch (ParserConfigurationException parConfigEx) {
				logger.debug("ParserConfig Exception : {}", parConfigEx);
			}

			catch (UnsupportedEncodingException unSupEncEx) {
				logger.debug("Unsupported Encoding Exception : {}", unSupEncEx);
			}


		}

	}

}

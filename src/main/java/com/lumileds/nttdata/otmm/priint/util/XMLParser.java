package com.lumileds.nttdata.otmm.priint.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lumileds.nttdata.otmm.priint.config.ProcessorConstants;
import com.lumileds.nttdata.otmm.priint.data.AssetMetadata;

public class XMLParser {

	private static Logger logger = LoggerFactory.getLogger(XMLParser.class);

	public List<AssetMetadata> getAssets(File xmlFile) throws DocumentException {

		Document document = null;

		try {
			SAXReader reader = new SAXReader();

			document = reader.read(xmlFile);

		}
		catch (DocumentException docEx) {

			//filesUtility.move(xmlFile, MigrationConstants.FILE_ERROR_LOCATION);

			logger.error("Exception while parsing the xml: {} ", docEx);
		}


		String folderName = xmlFile.getName().substring(11, 25);
		
		logger.debug("Content Folder name is : {} " + folderName);
		
		return processXML(document, folderName);
	}

	private List<AssetMetadata> processXML (Document document, String folderName) throws DocumentException {


		//		logger.debug("Name : {}", document.getDocType().getName());
		//		
		//		logger.debug("Element Name : {}", document.getDocType().getElementName());
		//		
		//		logger.debug("Path : {}", document.getDocType().getPath());
		//		
		//		logger.debug("CData section Node : {}", document.getDocType().CDATA_SECTION_NODE);
		//		
		//		logger.debug("Entity referencen Node : {}", document.getDocType().ENTITY_REFERENCE_NODE);
		//		
		//		logger.debug("Doctype: {}", document.getDocType().getSystemID());

		//logger.debug("Document Path: {}", document.getDocType().);

		//logger.debug("Document {}", document.asXML());


		Element root = document.getRootElement();

		List<AssetMetadata> assetMetadataList = new ArrayList<AssetMetadata>();

		Element assetsElement = root.element(ProcessorConstants.XML_ASSETS_ELEMENT);


		for (Iterator<Element> assetElementIterator = 
				assetsElement.elementIterator(ProcessorConstants.XML_ASSET_ELEMENT);
				assetElementIterator.hasNext();) {

			AssetMetadata assetMetadata = new AssetMetadata();

			Element assetElement = assetElementIterator.next();

			//logger.debug("Asset: " + assetElement.asXML());

			Element metadataElement = assetElement.element(ProcessorConstants.XML_METADATA_ELEMENT);

			Element uoisElement = metadataElement.element(ProcessorConstants.XML_UOIS_ELEMENT);
			assetMetadata.setAuthor(uoisElement.attribute(ProcessorConstants.XML_AUTHOR_ATTRIBUTE).getStringValue());
			assetMetadata.setSubject(uoisElement.attribute(ProcessorConstants.XML_SUBJECT_ATTRIBUTE).getStringValue());
			assetMetadata.setModelID(uoisElement.attribute(ProcessorConstants.XML_MODEL_ID_ATTRIBUTE).getStringValue());
			assetMetadata.setName(uoisElement.attribute(ProcessorConstants.XML_NAME_ATTRIBUTE).getStringValue());


			Element fileInfo = uoisElement.element(ProcessorConstants.XML_FILE_INFO_ELEMENT);
			assetMetadata.setAssetOwner(fileInfo.attribute(ProcessorConstants.XML_ASSET_OWNER_ATTRIBUTE).getStringValue());

			Element digitalHubElement = uoisElement.element(ProcessorConstants.XML_DIGITAL_HUB_ELEMENT);
			assetMetadata.setWcmsConfidentiality(digitalHubElement.attribute(ProcessorConstants.XML_WCMS_CONFIDENTIALITY_ATTRIBUTE).getStringValue());


			Element mediaInfoTabularElement = uoisElement.element(ProcessorConstants.XML_MEDIA_INFO_TAB_ELEMENT);
			assetMetadata.setRegions(mediaInfoTabularElement.attribute(ProcessorConstants.XML_REGIONS_ATTRIBUTE).getStringValue());

			Element mediaInfoElement = uoisElement.element(ProcessorConstants.XML_MEDIA_INFO_ELEMENT);
			assetMetadata.setAssetType(mediaInfoElement.attribute(ProcessorConstants.XML_ASSET_TYPE_ATTRIBUTE).getStringValue());
			assetMetadata.setKeywords(mediaInfoElement.attribute(ProcessorConstants.XML_KEYWORDS_ATTRIBUTE).getStringValue());
			assetMetadata.setBrand(mediaInfoElement.attribute(ProcessorConstants.XML_BRAND_ATTRIBUTE).getStringValue());


			Element securityPolicyUOIS = uoisElement.element(ProcessorConstants.XML_SECURITY_POLICY_ELEMENT);
			assetMetadata.setSecurityPolicyID(securityPolicyUOIS.attribute(ProcessorConstants.XML_SECURITY_POLICY_ID_ATTRIBUTE).getStringValue());

			assetMetadata.setFolderName(folderName);

			logger.debug(assetMetadata.toString());

			assetMetadataList.add(assetMetadata);

		}

		return assetMetadataList;

	}


}

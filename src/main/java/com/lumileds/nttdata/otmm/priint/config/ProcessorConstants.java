package com.lumileds.nttdata.otmm.priint.config;

public class ProcessorConstants {
	
	public static final String DB_URL = Configurations.getInstance().getProperty("db.url");
	public static final String DB_DRIVER = Configurations.getInstance().getProperty("db.driver");
	public static final String DB_USER = Configurations.getInstance().getProperty("db.user");
	public static final String DB_PASSWORD = Configurations.getInstance().getProperty("db.password");
	
	public static final String BASE_FOLDER = Configurations.getInstance().getProperty("base.folder");
	public static final String HOT_FOLDER = Configurations.getInstance().getProperty("hot.folder");
	public static final String DRAFT_FOLDER = Configurations.getInstance().getProperty("draft.hot.folder");
	public static final String FINAL_PIS_FOLDER = Configurations.getInstance().getProperty("final.pis.hot.folder");
	public static final String FINAL_PRS_FOLDER = Configurations.getInstance().getProperty("final.prs.hot.folder");
	public static final String FINAL_CPIS_FOLDER = Configurations.getInstance().getProperty("final.cpis.hot.folder");
	
	public static final String ARCHIVE_FOLDER = Configurations.getInstance().getProperty("archive.folder");
	
	public static final String DRAFT_BATCH_EXEC = Configurations.getInstance().getProperty("draft.file.importer");
	public static final String FINAL_PIS_BATCH_EXEC = Configurations.getInstance().getProperty("final.pis.file.importer");
	public static final String FINAL_PRS_BATCH_EXEC = Configurations.getInstance().getProperty("final.prs.file.importer");
	public static final String FINAL__CPIS_BATCH_EXEC = Configurations.getInstance().getProperty("final.cpis.file.importer");
	
	public static final CharSequence DRAFT_PATTERN = Configurations.getInstance().getProperty("draft.pattern");
	public static final CharSequence FINAL_PIS_PATTERN = Configurations.getInstance().getProperty("final.pis.pattern");
	public static final CharSequence FINAL_PRS_PATTERN = Configurations.getInstance().getProperty("final.prs.pattern");
	public static final CharSequence FINAL_CPIS_PATTERN = Configurations.getInstance().getProperty("final.cpis.pattern");
	
	
	public static final String BACK_SLASH = "\\";
	public static final String DOT_FOR_ARRAY_SPLIT = "\\.";
	public static final String DOT = ".";
	public static final String UNDER_SCORE = "_";
	public static final String XML_EXTENSION = ".xml";
	public static final String CHARSET = "UTF-8";
	
	//public static enum FILE_PATTERN { DRAFT, PIS, PRS, CPIS } ;
	
	public static final String PATH_CONFFILE = "config.properties";
	
	public static final String XML_TEAMS_ASSET_FILE_ELEMENT = "TEAMS_ASSET_FILE";
	public static final String XML_ASSETS_ELEMENT = "ASSETS";
	public static final String XML_ASSET_ELEMENT = "ASSET";
	public static final String XML_METADATA_ELEMENT = "METADATA";
	public static final String XML_UOIS_ELEMENT = "UOIS";
	public static final String XML_FILE_INFO_ELEMENT = "LUM_MD_FILE_INFO";
	public static final String XML_DIGITAL_HUB_ELEMENT = "LUM_MD_DIGI_HUB_INFO";
	public static final String XML_MEDIA_INFO_TAB_ELEMENT = "LUM_MD_MEDIA_INFO_TAB";
	public static final String XML_MEDIA_INFO_ELEMENT = "LUM_MD_MEDIA_INFO";
	public static final String XML_SECURITY_POLICY_ELEMENT = "SECURITY_POLICY_UOIS";
	
	public static final String XML_AUTHOR_ATTRIBUTE = "AUTHOR";
	public static final String XML_SUBJECT_ATTRIBUTE = "SUBJECT";
	public static final String XML_MODEL_ID_ATTRIBUTE = "MODEL_ID";
	
	public static final String XML_ASSET_OWNER_ATTRIBUTE = "ASSET_OWNER";
	
	public static final String XML_WCMS_CONFIDENTIALITY_ATTRIBUTE = "WCMS_CONFIDENTIALITY";
	
	public static final String XML_REGIONS_ATTRIBUTE = "REGIONS";

	public static final String XML_ASSET_TYPE_ATTRIBUTE = "ASSET_TYPE";
	public static final String XML_KEYWORDS_ATTRIBUTE = "KEYWORDS";
	public static final String XML_BRAND_ATTRIBUTE = "BRAND";
	
	public static final String XML_SECURITY_POLICY_ID_ATTRIBUTE = "SEC_POLICY_ID";
	public static final String XML_CONTENT_ELEMENT = "CONTENT";
	public static final String XML_MASTER_ELEMENT = "MASTER";
	public static final String XML_NAME_ATTRIBUTE = "NAME";
	public static final String XML_FILE_ATTRIBUTE = "FILE";
	
	public static final long TIME_DELAY_IN_MICROSECONDS = 300000; //5 minutes
	public static final String OUTPUT_XML_FILE = "OTMM_BATCH";	
	
		
}

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
	public static final String FINAL_CPIS_BATCH_EXEC = Configurations.getInstance().getProperty("final.cpis.file.importer");
	
	public static final CharSequence DRAFT_PATTERN = Configurations.getInstance().getProperty("draft.pattern");
	public static final CharSequence FINAL_PIS_PATTERN = Configurations.getInstance().getProperty("final.pis.pattern");
	public static final CharSequence FINAL_PRS_PATTERN = Configurations.getInstance().getProperty("final.prs.pattern");
	public static final CharSequence FINAL_CPIS_PATTERN = Configurations.getInstance().getProperty("final.cpis.pattern");
	
	
	public static final String BACK_SLASH = "\\";
	public static final String DOT_FOR_ARRAY_SPLIT = "\\.";
	public static final String DOT = ".";
	public static final String UNDER_SCORE = "_";
	public static final String XML_EXTENSION = ".xml";
	public static final String UTF_8 = "UTF-8";
	public static final String CHARSET = "charset";
	
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
	
	//Start of changes for DigitalHub Publish To Microsite and Languages
	
	public static final String XML_DIGI_HUB_INFO_TAB_ELEMENT = "LUM_MD_DIGI_HUB_INFO_TAB";
	public static final String XML_PUB_TO_MICROSITE_ATTRIBUTE = "PUBLISH_TO_MICROSITE";
	
	public static final String XML_LANGUAGES_TAB_ELEMENT = "LUM_MD_LANGUAGES_TAB";
	public static final String XML_LANGUAGES_ATTRIBUTE = "LANGUAGES";	
	
	//End of changes for DigitalHub Publish To Microsite and Languages
	
	
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
	
	//START of changes for CPIS Assets
	public static final String XML_COMM_VISUAL_RIGHTS_APPLICABLE_ELEMENT = "LUM_MD_VISUAL_COPYRIGHTS";
	public static final String XML_COMM_MUSIC_RIGHTS_APPLICABLE_ELEMENT = "LUM_MD_MUSIC_RIGHTS";
	
	public static final String XML_COMM_VISUAL_RIGHTS_APPLICABLE_ATTRIBUTE = "COPYRIGHT_VISUAL_APPLICABLE";
	public static final String XML_COMM_MUSIC_RIGHTS_APPLICABLE_ATTRIBUTE = "COPYRIGHT_MUSIC_APPLICABLE";
	
	public static final String XML_PUB_TO_MICROSITE_INDPIM_VALUE = 
			Configurations.getInstance().getProperty("otmm.metadata.pubToMicrosite.indPIM");
	public static final String XML_PUB_TO_MICROSITE_AMPIM_VALUE = 
			Configurations.getInstance().getProperty("otmm.metadata.pubToMicrosite.amPIM");
	public static final String XML_PUB_TO_MICROSITE_OEMPIM_VALUE = 
			Configurations.getInstance().getProperty("otmm.metadata.pubToMicrosite.oemPIM");
	
	public static final String XML_LANGUAGES_VALUE = 
			Configurations.getInstance().getProperty("otmm.metadata.languages");
	
	//END of changes for CPIS Assets
	
	public static final long TIME_DELAY_IN_MICROSECONDS = 300000; //5 minutes
	public static final String OUTPUT_XML_FILE = "OTMM_BATCH";
	
	
	public static final String OTMM_API_URL = Configurations.getInstance().getProperty("otmm.api.url");
	public static final String SESSIONS = "sessions";
	public static final String FRONT_SLASH = "/";	
	public static final String ASSETS_URL = "assets";
	public static final String STATE_URL = "state";
	public static final String PASSWORD = "password";
	public static final String USERNAME = "username";
	public static final String EQUALS = "=";
	public static final String AMPERSAND = "&";	
	
	public static final String OTMM_USER_NAME = Configurations.getInstance().getProperty("otmm.api.username");
	public static final String OTMM_PASSWORD = Configurations.getInstance().getProperty("otmm.api.password");
	//public static final String MODEL_ID = Configurations.getInstance().getProperty("otmm.metadata.modelID");
	public static final String OTMM_DRAFT_FOLDER_ID = 
			Configurations.getInstance().getProperty("otmm.metadata.draftFolderID");
	public static final String OTMM_CPIS_FOLDER_ID = 
			Configurations.getInstance().getProperty("otmm.metadata.cpisFolderID");
	public static final String OTMM_PIS_FOLDER_ID = 
			Configurations.getInstance().getProperty("otmm.metadata.pisFolderID");
	public static final String OTMM_PRS_FOLDER_ID = 
			Configurations.getInstance().getProperty("otmm.metadata.prsFolderID");
	
	
	
	public static final String POST = "POST";
	public static final String HTTP_PUT = "PUT";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String FORM_URL_ENCODE = "application/x-www-form-urlencoded";
	public static final int CONNECT_TIMEOUT = 30000;
	public static final int READ_TIMEOUT = 30000;
	public static final String INDENT_NUMBER = "indent-number";
	//public static final String OTMM_API_VERSION = "v4";

	public static final String CONTENT_LENGTH = "Content-Length";
	public static final String SESSION_RESOURCE = "session_resource";
	public static final String SESSION = "session";
	public static final String MESSAGE_DIGEST = "message_digest";
	public static final String DELETE_FLAG = "DELETE";
	public static final String PURGE_FLAG = "PURGE";
	public static final String ASSET_STATE_URL_PARAMS = "asset_state_options="
						+ "{ \"asset_state_options_param\":"
						+ " { \"asset_state_options\":"
						+ " { \"apply_to_all_versions\": false"
						+ " } } }";
	
	public static final String OTMM_AUTH_TOKEN = "otmmauthtoken";
	public static final String ACTION = "action";
	public static final String DELETE = "delete";
	public static final String CHECK_OUT = "check_out";
	public static final String LOCK = "lock";
	public static final String JOBS = "jobs";
	
	public static final String IMPORTS = "imports";
	public static final String JOB_HANDLE = "job_handle";
	public static final String JOB_ID = "job_id";
	
	public static final String RENDITIONS = "renditions";
	public static final String MULTIPART_FORM_WITH_BOUNDARY = "multipart/form-data; boundary=";
	public static final String APPLICATION_JSON = "application/json";
	
	public static final String CRLF = "\r\n";
	public static final String IMPORT_JOB_ID = "import_job_id";
	public static final String CHECKINS = "checkins";
	public static final String VERSIONS = "VERSIONS";
	public static final String ORIGINAL = "ORIGINAL";
	public static final String Y_FLAG = "Y";
	public static final String YES_FLAG = "yes";
	public static final String N_FLAG = "N";
	
	public static final String XML_COMM_SECURITY_POLICY_ID = 
			Configurations.getInstance().getProperty("otmm.metadata.comm.securityPolicyID");
	public static final String XML_COMM_MODEL_ID = 
			Configurations.getInstance().getProperty("otmm.metadata.comm.modelID");
	
	public static final String XML_IND_SECURITY_POLICY_ID = 
			Configurations.getInstance().getProperty("otmm.metadata.ind.securityPolicyID");
	public static final String XML_IND_MODEL_ID = 
			Configurations.getInstance().getProperty("otmm.metadata.ind.modelID");
	
	//Changes for v5 otmmapi
	public static final String ID = "id";
	public static final String X_REQUESTED_BY = "X-Requested-By";
	
	
	
	
		
}

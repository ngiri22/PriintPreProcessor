package com.lumileds.nttdata.otmm.priint.config;

import com.lumileds.nttdata.otmm.priint.config.Configurations;

public class ProcessorConstants {
	
	public static final String DB_URL = Configurations.getInstance().getProperty("db.url");
	public static final String DB_DRIVER = Configurations.getInstance().getProperty("db.driver");
	public static final String DB_USER = Configurations.getInstance().getProperty("db.user");
	public static final String DB_PASSWORD = Configurations.getInstance().getProperty("db.password");
	
	public static final String BASE_FOLDER = Configurations.getInstance().getProperty("base.folder");
	public static final String HOT_FOLDER = Configurations.getInstance().getProperty("hot.folder");
	
	public static final String BACK_SLASH = "\\";
	public static final String DOT_FOR_ARRAY_SPLIT = "\\.";
	public static final String DOT = ".";
	public static final String UNDER_SCORE = "_";
	
	
	public static final String PATH_CONFFILE = "config.properties";
	
}

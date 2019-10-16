package com.lumileds.nttdata.otmm.priint.config;

import java.util.Properties;

import com.lumileds.nttdata.otmm.priint.config.Configurations;
import com.lumileds.nttdata.otmm.priint.config.ProcessorConstants;

public class Configurations {
	
	private Properties properties = null;
	private static Configurations instance = null;

	/** Private constructor */
	private Configurations (){
	    this.properties = new Properties();
	    try{
	        properties.load(Thread.currentThread().getContextClassLoader().
	        		getResourceAsStream(ProcessorConstants.PATH_CONFFILE));
	    }catch(Exception ex){
	        ex.printStackTrace();
	    }
	}   

	/** The instance is synchronized to avoid multithreads problems */
	private synchronized static void createInstance () {
	    if (instance == null) { 
	        instance = new Configurations ();
	    }
	}

	/** Get the properties instance. Uses singleton pattern */
	public static Configurations getInstance(){
	    // Uses singleton pattern to guarantee the creation of only one instance
	    if(instance == null) {
	        createInstance();
	    }
	    return instance;
	}

	/** Get a property of the property file */
	public String getProperty(String key){
	    String result = null;
	    if(key !=null && !key.trim().isEmpty()){
	        result = this.properties.getProperty(key);
	    }
	    return result;
	}
	
	/** Override the clone method to ensure the "unique instance" requeriment of this class */
	public Object clone() throws CloneNotSupportedException {
	    throw new CloneNotSupportedException();
	}

}

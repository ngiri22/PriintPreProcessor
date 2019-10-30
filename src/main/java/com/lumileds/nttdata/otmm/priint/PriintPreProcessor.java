package com.lumileds.nttdata.otmm.priint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lumileds.nttdata.otmm.priint.util.CmdLiner;
import com.lumileds.nttdata.otmm.priint.util.FileMover;

public class PriintPreProcessor {

	private static final Logger logger = LoggerFactory.getLogger(PriintPreProcessor.class);
	
	public static void main(String[] args) {
		
		logger.debug("******Start of Program*******");
		
		FileMover fileMover = new FileMover();
		
		//fileMover.moveFiles();
		
		fileMover.processXMLFiles();
		
		//Call BulkImportUtlity for Drafts
		//CmdLiner.runBulkImport(false);
		
		//Call BulkImportUtility for Final version
		//CmdLiner.runBulkImport(true);

		logger.debug("******End of Program*******");
	}

}

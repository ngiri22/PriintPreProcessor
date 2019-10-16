package com.lumileds.nttdata.otmm.priint.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lumileds.nttdata.otmm.priint.config.ProcessorConstants;


public class FileMover {
	
	private final Logger logger = LoggerFactory.getLogger(FileMover.class);
	
	public void moveFiles() {
		
		recursiveMoveFiles();
		
	}
	
	private void recursiveMoveFiles() {
	    
		long cutOff = System.currentTimeMillis() - (10 * 60 * 1000);
		
		try {
		
			Files.list(
				Paths.get(ProcessorConstants.BASE_FOLDER)
				)
			    .forEach(path -> {
			    	
			    	try {
			    	
			    		if( Files.getLastModifiedTime(path, 
			    				new LinkOption[] {LinkOption.NOFOLLOW_LINKS})
			    				.to(TimeUnit.MILLISECONDS) < cutOff
			    				) {
			    			
			    			
			    			
			    			logger.debug("Source File Name: " + path.getFileName().toString());	
			    			
			    			Files.move(path, 
			    					new File(
			    							ProcessorConstants.HOT_FOLDER +
			    							ProcessorConstants.BACK_SLASH +
			    							path.getFileName()
			    							).toPath(),
			    					StandardCopyOption.REPLACE_EXISTING
			    					);
			    			
			    		}
			    	
			    	
//			    	if (Files.isDirectory(path)) {
//	            try {
//	                recursiveDeleteFilesOlderThanNDays(days, path.toString());
//	            } catch (IOException e) {
//	                // log here and move on
//	            }
//	        } else {
//	            try {
//	                if (Files.getLastModifiedTime(path).to(TimeUnit.MILLISECONDS) < cutOff) {
//	                    Files.delete(path);
//	                }
	            } catch (IOException ex) {
//	                // log here and move on
	            }
//	        }
	    });
	} catch (IOException ex) {

    }
	}

	public void move(File file, String dirToMove) {

		logger.info(" Processing completed, file is moved to {}",
				dirToMove + "\\" + file.getName());

		file.renameTo(new File (
				dirToMove + "\\" + file.getName()));

	}
	
}

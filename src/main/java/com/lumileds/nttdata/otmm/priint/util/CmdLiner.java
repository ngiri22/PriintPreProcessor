package com.lumileds.nttdata.otmm.priint.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lumileds.nttdata.otmm.priint.config.ProcessorConstants;

public class CmdLiner {

	private static final Logger logger = LoggerFactory.getLogger(CmdLiner.class);

	public static void runBulkImport(boolean isFinal) {

		logger.debug("Started batch file execution");

		String batchFile = ProcessorConstants.DRAFT_BATCH_EXEC;
		
		if (isFinal) {
			
			batchFile = ProcessorConstants.FINAL_PIS_BATCH_EXEC ;
			
		}
		
		try {

			ProcessBuilder builder = new ProcessBuilder(batchFile);
			
			builder.redirectErrorStream(true);
			
			final Process process = builder.start();

			// Watch the process
			watch(process);
		}
		catch (IOException ioEx) {
			
			logger.error("IOException while executing Batch file: {} ", ioEx);
			
		}
		
		logger.debug("End of batch file execution");
	}

	private static void watch(final Process process) {
		new Thread() {
			public void run() {
				BufferedReader input = new BufferedReader(
						new InputStreamReader(process.getInputStream()));
				String line = null; 
				try {
					while ((line = input.readLine()) != null) {
						logger.debug(line);
					}
				} catch (IOException ioEx) {
					logger.error("IOException while executing Batch file: {} ", ioEx);
				}
			}
		}.start();
	}

}

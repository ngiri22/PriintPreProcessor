# PriintPreProcessor

This code looks into files from priint shared folder.

If there are files found and older than 5 mins:
	
	Makes a copy of the files in ARCHIVE folder.
	Creates batch xml files per each type of folder i.e., DRAFT/FINAL (PRS, PIS, CPIS).
	Accepts files in the order of recently modified and ignores any duplicate files.
	Moves the files into DRAFT and FINAL i.e., PIS, PRS and CPIS Hot folders.
	Invokes the Bulk import Command line utility.
	Bulk Import utility imports the files into DRAFT, PRS, PIS & CPIS folders on OTMM respectively.
	Command to be executed to run the app is "java -cp D:\OpenText\OTMM\HOTFOLDER\conf;D:\OpenText\OTMM\HOTFOLDER\lib\PriintPreProcessor.jar -Dfile.encoding="UTF-8" com.lumileds.nttdata.otmm.priint.PriintPreProcessor  2> D:\OpenText\OTMM\HOTFOLDER\logs\priint_pre_processor_console.log"

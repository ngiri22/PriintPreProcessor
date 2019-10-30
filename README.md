# PriintPreProcessor

This code looks into files from priint shared folder.
If there are files found and older than 10 mins, it moves the files into DRAFT and FINAL i.e., PIS, PRS and CPIS Hot folders.
Also, it makes a copy of the files in ARCHIVE folder.

After the files are moved, it invokes the Bulk import Command line utility.
Bulk Import utility imports the files into DRAFT, PRS, PIS & CPIS folders on OTMM respectively.

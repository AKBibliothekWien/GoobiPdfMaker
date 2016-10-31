package ak.goobi.pdfmaker;


public class Main {

	static String processFolder;
	static String pathToDestination;
	static String processTitle;
	static String identifier;
	static String structureTypes;
	static String prefix;

	
	public static void main(String[] args) throws Exception {

		/*
		System.out.println("Path to process folder: " + args[0]);
		System.out.println("Path to destination: " + args[1]);
		System.out.println("Process title: " + args[2]);
		System.out.println("Structure Types: " + args[3]);
		System.out.println("Prefix: " + args[4]);
		*/
		
		if (args.length < 5) {
			System.out.println("\nError: You have to supply 5 arguments:\n\n" +
					"1. The path to the metadata folder of the Goobi process (e. g. \"/opt/digiverso/goobi/metadata/123/\". You can use {processpath} in a script-step in Goobi.\n" +
					"2. The path where you want to save the article PDFs (e. g. \"/home/userfolder\"). Do not forget the closing slash!\n" +
					"3. The title of the Goobi process (e. g. \"infoeuin_AC05712646_2014_003\"). You can use {processtitle} in a script-step in Goobi.\n" +
					"4. The structure element types which should be parsed as a comma-separated list (e. g. \"Article,Editorial,Review\"). For the correct names of the structure types, look into the METS-file of your OAI-Interface and search for section <mets:structMap TYPE=\"LOGICAL\">. The correct names are the values of the \"TYPE\"-attributes of the \"<mets:div ... >\"-elements.\n" +
					"5. A prefix for the files to make them unique. Could be an acronym of the journal-title, e. g. \"wug_\" for \"Wirtschaft und Gesellschaft\"\n"
					);
		} else {

			processFolder = args[0];
			pathToDestination = args[1];
			processTitle = args[2];
			structureTypes = args[3];
			prefix = args[4];
			
			new PdfsByStructureElements(processFolder, pathToDestination, processTitle, structureTypes, prefix).makeArticlePdfs();
			System.out.println("Article PDFs created at: " + pathToDestination);
		}
	}

}

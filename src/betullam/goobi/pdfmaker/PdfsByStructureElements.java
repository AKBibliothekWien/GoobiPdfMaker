package betullam.goobi.pdfmaker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ak.goobi.oaihelper.classes.Id;
import ak.goobi.oaihelper.main.GoobiOaiHelper;



public class PdfsByStructureElements {

	GoobiOaiHelper goh;
	Document document;
	String pathToProcessFolder;
	String pathToPdfFolder;
	String pathToSourceFolder;
	String pathToDestination;
	String processTitle;
	String prefix;
	String identifier;
	List<Id> ids = new ArrayList<Id>();
	List<StructureElement> structureElements = new ArrayList<StructureElement>();;

	public PdfsByStructureElements(String pathToMetadataFolder, String pathToDestination, String processTitle, String structureTypes, String prefix) throws Exception {
		
		List<String> typesToParse = new ArrayList<String>();
		String[] arrStructureTypes = structureTypes.split(",");
		for (String st : arrStructureTypes) {
			typesToParse.add(st.trim());
		}
		
		this.pathToProcessFolder = pathToMetadataFolder + File.separator; // Must be before getIdentifier()!!!
		this.processTitle = processTitle;
		this.identifier = getIdentifier();
		this.goh = new GoobiOaiHelper();
		this.document = getMetaXmlDocument();
		this.pathToPdfFolder = this.pathToProcessFolder + "ocr" + File.separator + this.processTitle + "_pdf" + File.separator;
		this.pathToSourceFolder = this.pathToProcessFolder + "images" + File.separator + this.processTitle + "_source" + File.separator;
		this.pathToDestination = pathToDestination + File.separator;
		this.prefix = prefix;
		ids = goh.getIds(document, typesToParse);
		
		System.out.println("Process Title: " + this.processTitle);
		System.out.println("Identifier: " + this.identifier);
		System.out.println("Path to PDF-folder: " + this.pathToPdfFolder);
		System.out.println("Path to Source-folder: " + this.pathToSourceFolder);
	}

	
	public void makeArticlePdfs() {
		
		// Check if source-folder exists. If not, stop execution of code:
		File destPath = new File(this.pathToDestination);
		if (destPath.exists() == false && destPath.isDirectory() == false) {
			System.out.println("Source folder does not exist!");
			return;
		}
		
		// Create a PDF for each id (= structure element) in the list:
		for (Id id : ids) {
			
			System.out.println("Processing LogId " + id.getLogId());
			
			List<String> imageNos = null;
			try {
				imageNos = goh.getOrderNoByPhysId(document, id.getPhysIds());
			} catch (XPathExpressionException e1) {
				e1.printStackTrace();
			}
			
			// Make PDF-File
			PDFMergerUtility pdfMU = new PDFMergerUtility();
			for (String imageNo : imageNos) {
				File singlePagePDF = new File(this.pathToPdfFolder + imageNo + ".pdf"); // Get single-page PDF-file
				try {
					pdfMU.addSource(singlePagePDF); // Add single-page PDF to Merger-Utility
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			
			// Get running no:
			int intRunningNo = (ids.indexOf(id)+1);
			String strRunningNo = String.format("%02d", intRunningNo); // Image-number-string with leading zeros by using String.format

			String pdfSingleArticleFilename = this.prefix + strRunningNo + ".pdf";

			// Merge PDF and save it:
			pdfMU.setDestinationFileName(this.pathToDestination + pdfSingleArticleFilename); // Filename for new PDF created from merged single-page PDFs
			try {
				pdfMU.mergeDocuments(null); // Merge single-page PDFs
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	

	public Document getMetaXmlDocument() {
		
		Document document = null;
		
		try {
			File metaxmlFile = new File(this.pathToProcessFolder+"meta.xml");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			document = db.parse(metaxmlFile);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return document;
	}

	public String getIdentifier() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		File metaxmlFile = new File(this.pathToProcessFolder+"meta.xml");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document metaxmlDoc = db.parse(metaxmlFile);
		String identifier = goh.getTextValue(metaxmlDoc, "/mets/dmdSec/mdWrap/xmlData/mods/extension/goobi/metadata[@name='_urn']");
		return identifier;
	}


}

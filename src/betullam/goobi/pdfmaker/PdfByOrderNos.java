package betullam.goobi.pdfmaker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.w3c.dom.Document;

public class PdfByOrderNos {

	List<String> orderNos = new ArrayList<String>();
	Document document;
	String prefix;
	String pathToSourcePdfFolder;
	String pathToDestination;
	String destinationFileName;

	/**
	 * Merge single-page-PDFs to a multi-page-PDF (e. g. a whole Article)
	 * 
	 * @param pathToSourcePdfFolder		a String that represents the path to a single-page PDF source folder (e. g. /opt/digiverso/goobi/metadata/100/ocr/ATS_pdf)
	 * @param orderNos					a List<String> of the order numbers (= file name) of the single-page PDF (for Goobi it is normaly 00000001, 00000002, etc.) 
	 * @param pathToDestination			a String that represents the path to the destination folder where you want to save the PDF (e. g. /home/username/pdf)
	 * @param destinationFileName		a String that represents the name of the newly created PDF file without the ending ".pdf" (e. g. mynewdoc)
	 */
	public PdfByOrderNos(String pathToSourcePdfFolder, List<String> orderNos, String pathToDestination, String destinationFileName) {
		this.pathToSourcePdfFolder = pathToSourcePdfFolder;
		this.orderNos = orderNos;
		this.pathToDestination = pathToDestination;
		this.destinationFileName = destinationFileName;
		makePdfsByOrderNos();
	}

	public void makePdfsByOrderNos() {

		// Make PDF-File
		PDFMergerUtility pdfMU = new PDFMergerUtility();
		for (String orderNo : this.orderNos) {
			File singlePagePDF = new File(stripFileSeperatorFromPath(this.pathToSourcePdfFolder) + File.separator + orderNo + ".pdf"); // Get single-page PDF-file from goobi-source-folder.
			pdfMU.addSource(singlePagePDF); // Add single-page PDF to Merger-Utility
		}

		destinationFileName = destinationFileName + ".pdf";

		// Merge PDF and save it:
		pdfMU.setDestinationFileName(stripFileSeperatorFromPath(this.pathToDestination) + File.separator + destinationFileName); // Filename for new PDF created from merged single-page PDFs
		try {
			pdfMU.mergeDocuments(); // Merge single-page PDFs
		} catch (COSVisitorException | IOException e) {
			e.printStackTrace();
		}
	}


	private String stripFileSeperatorFromPath (String path) {
		if ((path.length() > 0) && (path.charAt(path.length()-1) == File.separatorChar)) {
			path = path.substring(0, path.length()-1);
		}
		return path;
	}


}

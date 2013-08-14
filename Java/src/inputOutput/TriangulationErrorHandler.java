package inputOutput;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class TriangulationErrorHandler extends DefaultHandler {
	private String filename;

	public TriangulationErrorHandler(String filename) {
		this.filename = filename;
	}

	public void warning(SAXParseException e) throws SAXException {
		System.err.println("Warning: Document " + filename
				+ " did not validate against TriangulationSchema.");
		printInfo(e);
	}

	public void error(SAXParseException e) throws SAXException {
		System.err.println("Error: Document " + filename
				+ " did not validate against TriangulationSchema.");
		printInfo(e);
		System.exit(1);
	}

	public void fatalError(SAXParseException e) throws SAXException {
		System.err.println("Fatal Error: Document " + filename
				+ " did not validate against TriangulationSchema.");
		printInfo(e);
		System.exit(1);
	}

	private void printInfo(SAXParseException e) {
		//System.err.println("   Public ID: " + e.getPublicId());
		//System.err.println("   System ID: " + e.getSystemId());
		System.err.println("   Line number: " + e.getLineNumber());
		System.err.println("   Column number: " + e.getColumnNumber());
		System.err.println("   Message: " + e.getMessage());
	}
}
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Jyrki Oraskari
 * @license This work is licensed under a Creative Commons Attribution 3.0 Unported License.
 * http://creativecommons.org/licenses/by/3.0/
 */



public class DescriptionsSAXParser extends DefaultHandler {

	StringBuffer characters = new StringBuffer();

	StringBuffer description = new StringBuffer();

	public DescriptionsSAXParser() {
		super();
	}

	public void startElement(String uri, String name, String qName,
			Attributes atts) {
		if ("".equals(uri)) {

			if (qName.equals("cycleway")) {
				characters.setLength(0);
			}
			if (qName.equals("suburb")) {
				characters.setLength(0);
			}
			if (qName.equals("city")) {
				characters.setLength(0);
			}
			if (qName.equals("neighbourhood")) {
				characters.setLength(0);
			}
			if (qName.equals("road")) {
				characters.setLength(0);
			}
		}
	}

	public void endElement(String uri, String name, String qName) {
		if ("".equals(uri)) {
			if (qName.equals("cycleway")) {
				description.append(" " + characters);
			}
			if (qName.equals("suburb")) {
				description.append(" " + characters);
			}
			if (qName.equals("city")) {
				description.append(" " + characters);
			}
			if (qName.equals("neighbourhood")) {
				description.append(" " + characters);
			}
			if (qName.equals("road")) {
				description.append(" " + characters);
			}
		}
	}

	public void characters(char ch[], int start, int length) {
		for (int i = start; i < start + length; i++) {
			characters.append(ch[i]);
		}
	}

	public String getDescription() {
		StringBuffer correct_txt=new StringBuffer();
		for(int n=0;n<description.length();n++)
		{
			char ch=description.charAt(n);
			if(ch=='Ã')
				; // do nothing
			else
				if(ch=='¤')
					correct_txt.append('ä');
				else if(ch=='¶')
					correct_txt.append('ö');
				else
					correct_txt.append(ch);
		}
		return correct_txt.toString().trim();
	}

}

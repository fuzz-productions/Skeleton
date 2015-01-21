/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */
package fuzz.com.skeleton;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Process xml file and create a value object from it.
 */
public class XMLInspector {
	
	public final File xmlFile;
	public final ArrayList<IdData> ids = new ArrayList<IdData>();

	public XMLInspector(File xmlFile){
		this.xmlFile = xmlFile;
	}
	
	public void parse() throws IOException {
        byte[] data = null;
        BufferedInputStream in = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            in = new BufferedInputStream(new FileInputStream(xmlFile));
            int len;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            data = out.toByteArray();
        } finally {
            if (in != null) {
                in.close();
            }
        }
		String xmlData = new String(data, "UTF-8");
		xmlData = xmlData.replaceAll( "(?s)<!--.*?-->", "" ); // Clear comments
		
		String element;
		int thatThingIndex = xmlData.indexOf("<", 0) + 1;
		
		do{
			element = readElement(xmlData, thatThingIndex);
            String layout = null;
			if("include".equals(element)){
				layout = readLayout(xmlData, thatThingIndex);
			}
			String id = readId(xmlData, thatThingIndex);
			if(id != null && element != null &&
                    !element.equals("fragment") &&
                    !element.equals("android.support.v4.app.Fragment")){
				IdData idData = new IdData();
				idData.element = element;
				idData.name = id;
				idData.layout = layout;
				ids.add(idData);
			}
			thatThingIndex = xmlData.indexOf("<", thatThingIndex) + 1;
			
		}while(thatThingIndex != 0);
	}
	
	private String readLayout(String xmlData, int thatThingIndex) {
		int layoutIndex = xmlData.indexOf("@layout/", thatThingIndex);
		if(layoutIndex == -1){
			throw new IllegalArgumentException("layout not found in include tag");
		}
		int thatOtherThingIndex = xmlData.indexOf("\"", layoutIndex + 5);
		return xmlData.substring(layoutIndex + 8, thatOtherThingIndex);
	}

	private String readId(String xmlData, int startingIndex){
		int thatThingIndex = xmlData.indexOf(">", startingIndex);
		int idIndex = xmlData.indexOf("id=\"@+id/", startingIndex);
		if(idIndex == -1 || thatThingIndex < idIndex){
			return null;
		}
		int thatOtherThingIndex = xmlData.indexOf("\"", idIndex + 5);
		return xmlData.substring(idIndex + 9, thatOtherThingIndex);
		
	}

	private String readElement(String xmlData, int startingIndex) {
		int count = 0;
		char charData;
		
		do{
			charData = xmlData.charAt(startingIndex + count);
			count++;
		}while(count + startingIndex < xmlData.length() && isALatter(charData));
		
		if(count == 1){
			return null;
		}
		
		return xmlData.substring(startingIndex, startingIndex + count - 1);
	}

	private boolean isALatter(char charData) {
		charData = Character.toLowerCase(charData);
		return (charData >= 'a' && charData <= 'z') || (charData >= '0' && charData <= '9')  || charData == '_' || charData == '.';
	}
}

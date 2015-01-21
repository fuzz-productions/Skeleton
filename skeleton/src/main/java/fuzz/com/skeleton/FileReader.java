/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */
package fuzz.com.skeleton;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import fuzz.com.skeleton.loger.Logger;

/**
 * Created by Ilya Gazman on 1/6/15.
 */
public class FileReader {

    private String content = "";
    private int position;

    public FileReader(File file){
        try {
            content = readFile(file, "UTF-8");
        } catch (IOException e) {
            Logger.log("error reading file ", file.getAbsolutePath());
        }
    }

    public boolean skipTo(String key){
        for(int i = position; i < content.length(); i++){
            boolean error = false;
            for (int j = 0; j < key.length(); j++){
                if(key.charAt(j) != content.charAt(i + j)){
                    error = true;
                    break;
                }
            }
            if(!error){
                position = i;
                return true;
            }
        }
        return false;
    }

    public String readBetween(char openMark, char closeMark){
        if(content.charAt(position) != openMark){
            Logger.log(openMark, "mark not found", content.charAt(position), position);
            return null;
        }
        position++;
        int startingIndex = position;
        for(; position < content.length() && content.charAt(position) != closeMark; position++)
            ;
        position++;
        return content.substring(startingIndex, position - 1);

    }

    public void skipWhiteSpacesAndNewLines(){
        for(; position < content.length() && isWhiteSpace(position); position++)
            ;
    }

    private boolean isWhiteSpace(int position) {
        int i = content.charAt(position);
        return i == ' ' || i == '\n' || i == '\t' || i == '\r';
    }

    public static String readFile(File file, String charset)
            throws IOException
    {
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] buffer = new byte[fileInputStream.available()];
        int length = fileInputStream.read(buffer);
        return new String(buffer, 0, length, charset);
    }

    public void reset() {
        position = 0;
    }

    public void skip(int bytes) {
        position = Math.min(content.length() - 1, position + bytes);
    }
}

/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */
package fuzz.com.skeleton;

import java.io.File;
import java.util.ArrayList;

import fuzz.com.skeleton.loger.Logger;

/**
 * Search for xml files to inspect
 */
public class FilesManger {

    private ProjectInspector projectInspector;
    private File root;

    public static final FilesManger instance = new FilesManger();

    private FilesManger() {

    }

    public void init(ProjectInspector projectInspector) {
        this.projectInspector = projectInspector;

        File generateDir = new File(projectInspector.getActiveFlavor(), "java");
        String packageName = projectInspector.getPackageName();
        String generatePackageName = "resources";
        if (projectInspector.hasProperty("skeletonPackagePostfix")) {
            generatePackageName = (String) projectInspector.getProperty("skeletonPackagePostfix");
        }
        generatePackageName = packageName + '.' + generatePackageName;
        root = new File(generateDir, generatePackageName.replace(".", "/"));
        if(root.exists()){
            purgeDirectory(root);
        }
        Logger.log("Root> " + root.getPath());
    }

    public File getRoot() {
        return root;
    }

    public void mkdirs() {
        if(root != null){
            root.mkdirs();
        }
    }

    public ArrayList<String> getAllFiles() {
        ArrayList<String> files = new ArrayList<String>();
        File resDirectory = new File(projectInspector.getActiveFlavor(), "res");
        listFilesForXMLFiles(resDirectory, files);
        Logger.log("Found" + files.size() + " files in" + resDirectory.getAbsolutePath());
        return files;
    }

    private void purgeDirectory(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                purgeDirectory(file);
            }
            file.delete();
        }
    }

    private void listFilesForXMLFiles(final File folder, ArrayList<String> result) {

        File[] listFiles = folder.listFiles();
        if (listFiles == null) {
            return;
        }
        for (final File fileEntry : listFiles) {
            if (fileEntry.isDirectory()) {
                if (fileEntry.getAbsolutePath().contains("layout")) {
                    listFilesForXMLFiles(fileEntry, result);
                }
            } else {
                if (getFileExtension(fileEntry).equals(".xml")) {
                    result.add(fileEntry.getAbsolutePath());
                }
            }
        }
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }
}

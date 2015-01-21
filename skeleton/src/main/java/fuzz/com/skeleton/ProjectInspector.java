/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */
package fuzz.com.skeleton;

import org.gradle.api.Project;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import fuzz.com.skeleton.loger.Logger;

/**
 * Created by Ilya Gazman on 9/4/14.
 *
 * Provide an access for project settings, such as active flavor and package name.
 */
public class ProjectInspector {
    private Project project;
    private ArrayList<File> flavors = new ArrayList<File>();
    private int activeFlavor;
    private int mainIndex;
    private String packageName;
    private String buildType;

    public void init(Project project) {
        this.project = project;
        readProjectConfigurations();
        File projectDirectory = getProjectDirectory();
        File[] listFiles = projectDirectory.listFiles();
        for (File file : listFiles) {
            String fileName = file.getName().toLowerCase();
            if (file.isDirectory() && buildType.contains(fileName)) {
                if (fileName.equals("main")) {
                    mainIndex = flavors.size();
                }
                flavors.add(file);
            }
        }
    }

    private void readProjectConfigurations() {
        File buildDir = project.getBuildDir();
        File file = findFile(buildDir, "BuildConfig.java");
        if (file == null) {
            Logger.log("Error: BuildConfig.java wasn't found at " + buildDir.getPath());
            return;
        }
        Logger.log("Reading package name");
        FileReader fileReader = new FileReader(file);
        String package_name = readStringValue(fileReader, "PACKAGE_NAME");
        if(package_name == null){
            package_name = readStringValue(fileReader, "APPLICATION_ID");
        }
        packageName = package_name.toLowerCase();
        buildType = (readStringValue(fileReader, "BUILD_TYPE") + "main").toLowerCase();
        String flavor = readStringValue(fileReader, "FLAVOR");
        if(flavor != null){
            buildType += flavor;
        }
        Logger.log("Config", buildType, packageName);
    }

    private String readStringValue(FileReader fileReader, String key) {
        fileReader.reset();
        if (!fileReader.skipTo(key)){
            Logger.log(key, "not found");
        }
        if (!fileReader.skipTo("=")){
            Logger.log("=", "not found");
        }
        fileReader.skip(1);
        fileReader.skipWhiteSpacesAndNewLines();
        return fileReader.readBetween('"', '"');
    }


    private File findFile(File root, String name) {
        String rootName = root.getName();
        if (rootName.equalsIgnoreCase(name)) {
            return root;
        }
        ArrayList<File> results = new ArrayList<File>();
        if (root.isDirectory() && !root.getName().equalsIgnoreCase("test")) {
            for (File file : root.listFiles()) {
                File result = findFile(file, name);
                if (result != null) {
                    results.add(result);
                }
            }
        }

        Collections.sort(results, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                return Long.compare(rhs.lastModified(), lhs.lastModified());
            }
        });


        return results.size() > 0 ? results.get(0) : null;
    }

    public File getProjectDirectory() {
        File sourceOutputDir = project.getBuildDir();
        while (!sourceOutputDir.getAbsolutePath().endsWith("build")) {
            sourceOutputDir = sourceOutputDir.getParentFile();
            if (sourceOutputDir == null) {
                throw new IllegalStateException("Root not found");
            }
        }

        sourceOutputDir = new File(sourceOutputDir.getParentFile(), "src");
        Logger.log("Root found at:" + sourceOutputDir.getAbsolutePath());
        return sourceOutputDir;
    }

    public File getActiveFlavor() {
        return flavors.get(activeFlavor);
    }

    public int getFlavorsCount() {
        return flavors.size();
    }

    public void setActiveFlavor(int activeFlavor) {
        this.activeFlavor = activeFlavor;
    }

    public void setMainFlavor() {
        this.activeFlavor = mainIndex;
    }

    public String getPackageName() {
        return packageName;
    }

    public boolean hasProperty(String propertyName) {
        return project.hasProperty(propertyName);
    }

    public Object getProperty(String propertyName) {
        return project.property(propertyName);
    }
}

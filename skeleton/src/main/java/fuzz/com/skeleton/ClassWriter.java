/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package fuzz.com.skeleton;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import fuzz.com.skeleton.class_builder.ClassBuilder;

/**
 * Using ClassBuilder to convert XMLInspector to string and save it on disk in your project.
 */
public class ClassWriter {

    public void write(ProjectInspector projectInspector, ArrayList<XMLInspector> xmlInspectors) throws IOException {
        margeDuplicates(xmlInspectors);
        String packageName = projectInspector.getPackageName();
        String generatePackageName = "resources";
        if (projectInspector.hasProperty("skeletonPackagePostfix")) {
            generatePackageName = (String) projectInspector.getProperty("skeletonPackagePostfix");
        }
        generatePackageName = packageName + '.' + generatePackageName;
        for (XMLInspector xmlInspector : xmlInspectors) {
            ClassBuilder classBuilder = new ClassBuilder(packageName, generatePackageName, xmlInspector);
            saveClass(classBuilder);
        }
    }

    private void margeDuplicates(ArrayList<XMLInspector> xmlInspectors) {
        HashMap<String, XMLInspector> map = new HashMap<String, XMLInspector>();
        for (int i = xmlInspectors.size() - 1; i >= 0; i--) {
            XMLInspector xmlInspector = xmlInspectors.get(i);
            String fileName = ClassBuilder.getFileName(xmlInspector.xmlFile);
            XMLInspector currentXmlInspector = map.get(fileName);
            if(currentXmlInspector != null){
                marge(xmlInspector, currentXmlInspector);
                xmlInspectors.remove(i);
            }
            else {
                map.put(fileName, xmlInspector);
            }

        }
    }

    private void marge(XMLInspector oldInspector, XMLInspector xmlInspector) {
        HashMap<String, IdData> map = new HashMap<String, IdData>();
        for (IdData id : xmlInspector.ids) {
            map.put(id.name, id);
        }
        for (IdData id : oldInspector.ids) {
            if (map.put(id.name, id) == null) {
                xmlInspector.ids.add(id);
            }
        }
    }

    private void saveClass(ClassBuilder classBuilder) throws IOException {
        File file = new File(FilesManger.instance.getRoot(), classBuilder.className + ".java");
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(file));
            out.write(classBuilder.build());
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}

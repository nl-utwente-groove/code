/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id$
 */
package groove.io;

import groove.trans.RuleNameLabel;
import groove.util.Groove;
import groove.view.DefaultGrammarView;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Tom Staijen
 * @version $Revision $
 * 
 * A grammar loader class that can load grammars from Jar files. Inside the
 * Jarfile a directory is expected that is structured as a regular .gps
 * directory. The grammar can be loaded given a URL to the directory in the jar
 * file (including the tailing /), optionaly with the start graph name as
 * "query" and the control name as "ref". Example url's:
 * jar:file:/c:/grammars.jar!/languages/java.gps/?simpleprogram#simplecontrol
 * jar:http://www.someurl.com/some.jar!/java.gps/#scenario1 (default start graph
 * used)
 * 
 */
public class JarGps extends AspectualViewGps {

    /**
     * Create a Jar grammar loader
     * @param layouted indicates whether layouts should be loaded.
     */
    public JarGps(boolean layouted) {
        super(layouted);
    }

    public ExtensionFilter getExtensionFilter() {
        throw new UnsupportedOperationException("Unable to comply");
    }

    @Override
    public DefaultGrammarView unmarshal(URL location, String startGraphName,
            String controlName) throws IOException {

        // the url (externalform) of the jar file
        String baseURL =
            location.toExternalForm().substring(0,
                location.toExternalForm().indexOf("!") + 2);

        // the path in the jar of the gps
        String dir =
            location.getPath().substring(location.getPath().indexOf("!") + 2);

        // create the result
        DefaultGrammarView result =
            createGrammar(FileGps.GRAMMAR_FILTER.stripExtension(dir));

        // connection to the file index in the jar file
        JarFile jarFile =
            ((JarURLConnection) location.openConnection()).getJarFile();

        // PROPERTIES
        JarEntry pe =
            jarFile.getJarEntry(dir + Groove.PROPERTY_NAME
                + Groove.PROPERTY_EXTENSION);
        // backwards compatibility: <grammar name>.properties
        if (pe == null) {
            pe =
                jarFile.getJarEntry(dir + result.getName()
                    + Groove.PROPERTY_EXTENSION);
        }
        if (pe != null) {
            URL propertiesEntry = new URL(baseURL + pe.getName());
            this.loadProperties(result, propertiesEntry);
        }

        // RULES

        // store RuleNameLabels for rulegroup directories
        HashMap<String,RuleNameLabel> pathLabels =
            new HashMap<String,RuleNameLabel>();

        // store the rules
        Map<RuleNameLabel,URL> ruleMap = new HashMap<RuleNameLabel,URL>();

        for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
            JarEntry entry = entries.nextElement();
            if (entry.getName().startsWith(dir)
                && FileGps.RULE_FILTER.hasExtension(entry.getName())) {
                String path =
                    FileGps.RULE_FILTER.stripExtension(entry.getName());
                path = path.substring(dir.length());
                RuleNameLabel label = initLabel(pathLabels, path, true);
                URL ruleURL = new URL(baseURL + entry.getName());
                ruleMap.put(label, ruleURL);
            }
        }

        loadRules(result, ruleMap);

        // init start graph url
        JarEntry je =
            jarFile.getJarEntry(dir + startGraphName + Groove.STATE_EXTENSION);

        if (je != null) {
            URL startGraphEntry = new URL(baseURL + je.getName());
            this.loadStartGraph(result, startGraphEntry);
        }

        // control

        JarEntry ce =
            jarFile.getJarEntry(dir + controlName + Groove.CONTROL_EXTENSION);
        if (ce != null) {
            URL controlEntry = new URL(baseURL + je.getName());
            this.loadControl(result, controlEntry, controlName);
        }

        return result;
    }

    private RuleNameLabel initLabel(Map<String,RuleNameLabel> folders,
            String path, boolean isFile) {
        if (!folders.containsKey(path)) {

            RuleNameLabel label;
            if (path.indexOf('/') != -1) {
                String parent = path.substring(0, path.lastIndexOf('/'));

                RuleNameLabel pLabel = initLabel(folders, parent, false);
                label =
                    new RuleNameLabel(pLabel,
                        path.substring(path.lastIndexOf('/') + 1));
            } else {
                label = new RuleNameLabel(path);
            }
            if( !isFile ) {
                folders.put(path, label);
            }
            return label;

        } else {
            return folders.get(path);
        }
    }

    /**
     * Unmarshall from a grammar url that is built as follows: jar:<path_to_jar>!/grammar/dir/?startGraphName#controlName
     * 
     */
    @Override
    public DefaultGrammarView unmarshal(URL url) throws IOException {
        String startGraphName = url.getQuery();
        if (startGraphName == null) {
            startGraphName = DEFAULT_START_GRAPH_NAME;
        }
        String controlName = url.getRef();
        if (controlName == null) {
            controlName = DEFAULT_CONTROL_NAME;
        }

        if (!url.getProtocol().equals("jar")
            || !url.toExternalForm().contains("!/")) {
            throw new MalformedURLException("Expected jar URL");
        }

        URL jarURL = new URL("jar", null, 1, url.getPath());
        // extract the path of the grammar from the jar file

        return unmarshal(jarURL, startGraphName, controlName);
    }

    public static void main(String args[]) throws IOException {
        long start = System.currentTimeMillis();
        URL url = new URL("jar:file:c:/grammars/cups.jar!/cups.gps/");
        new JarGps(false).unmarshal(url, "start", "cups");
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("From Jar Took " + elapsed + " ms");
    }

}

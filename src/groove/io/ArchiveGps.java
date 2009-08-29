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

import groove.trans.RuleName;
import groove.util.Groove;
import groove.view.DefaultGrammarView;

import java.io.File;
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
 *          A grammar loader class that can load grammars from Jar files. Inside
 *          the Jarfile a directory is expected that is structured as a regular
 *          .gps directory. The grammar can be loaded given a URL to the
 *          directory in the jar file (including the tailing /), optionaly with
 *          the start graph name as "query" and the control name as "ref".
 *          Example url's:
 *          jar:file:/c:/grammars.jar!/languages/java.gps/?simpleprogram
 *          #simplecontrol
 *          jar:http://www.someurl.com/some.jar!/java.gps/#scenario1 (default
 *          start graph used)
 * 
 */
public abstract class ArchiveGps extends AspectualViewGps {

    /**
     * Create a Jar grammar loader
     * @param layouted indicates whether layouts should be loaded.
     */
    protected ArchiveGps(boolean layouted) {
        super(layouted);
    }

    @Override
    protected DefaultGrammarView unmarshal(URL location, String startGraphName,
            String controlName) throws IOException {

        if (!location.getProtocol().equals("jar")
            || !location.toExternalForm().contains("!/")) {
            throw new MalformedURLException("Expected jar URL");
        }

        // the url (externalform) of the jar file
        String baseURL =
            location.toExternalForm().substring(0,
                location.toExternalForm().indexOf("!") + 2);

        // the path in the jar of the gps
        String dir =
            location.getPath().substring(location.getPath().indexOf("!") + 2);

        if (!dir.endsWith("/")) {
            dir += "/";
        }

        // connection to the file index in the jar file
        JarFile jarFile =
            ((JarURLConnection) location.openConnection()).getJarFile();

        // if there is no dir, find it in the jarfile
        if (dir.equals("/")) {
            dir = findGrammar(jarFile);
            if (dir == null) {
                throw new IOException("No grammar found in "
                    + jarFile.getName());
            }
        }

        // create the result
        DefaultGrammarView result =
            createGrammar(FileGps.GRAMMAR_FILTER.stripExtension(new File(dir).getName()));

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
        HashMap<String,RuleName> pathLabels = new HashMap<String,RuleName>();

        // store the rules
        Map<RuleName,URL> ruleMap = new HashMap<RuleName,URL>();
        // store the graphs
        Map<String,URL> graphMap = new HashMap<String,URL>();

        for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
            JarEntry entry = entries.nextElement();
            if (entry.getName().startsWith(dir)
                && FileGps.RULE_FILTER.hasExtension(entry.getName())) {
                String path =
                    FileGps.RULE_FILTER.stripExtension(entry.getName());
                path = path.substring(dir.length());
                RuleName label = initLabel(pathLabels, path, true);
                URL ruleURL = new URL(baseURL + entry.getName());
                ruleMap.put(label, ruleURL);
            } else if (entry.getName().startsWith(dir)
                && FileGps.STATE_FILTER.hasExtension(entry.getName())) {
                String path =
                    FileGps.STATE_FILTER.stripExtension(entry.getName());
                path = path.substring(dir.length());
                if (new File(path).getParent() == null) {
                    // the entry is at the top level within dir
                    URL graphURL = new URL(baseURL + entry.getName());
                    graphMap.put(path, graphURL);
                }
            }
        }

        loadRules(result, ruleMap);
        loadGraphs(result, graphMap);

        // init start graph url
        if (startGraphName == null) {
            startGraphName = DEFAULT_START_GRAPH_NAME;
            if (graphMap.containsKey(startGraphName)) {
                result.setStartGraph(startGraphName);
            }
        }
        // JarEntry je =
        // jarFile.getJarEntry(dir + startGraphName + Groove.STATE_EXTENSION);
        //
        // if (je != null) {
        // URL startGraphEntry = new URL(baseURL + je.getName());
        // this.loadStartGraph(result, startGraphEntry);
        // }

        // control

        if (controlName == null) {
            controlName = DEFAULT_CONTROL_NAME;
        }
        JarEntry ce =
            jarFile.getJarEntry(dir + controlName + Groove.CONTROL_EXTENSION);
        if (ce != null) {
            URL controlEntry = new URL(baseURL + ce.getName());
            this.loadControl(result, controlEntry, controlName);
        }

        jarFile.close();

        return result;
    }

    private String findGrammar(JarFile jarFile) {
        for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            // quickly make sure there is .gps in the path
            if (name.endsWith(Groove.RULE_SYSTEM_EXTENSION + "/")) {
                return name;
            }
        }
        return null;
    }

    private RuleName initLabel(Map<String,RuleName> folders, String path,
            boolean isFile) {
        if (!folders.containsKey(path)) {

            RuleName label;
            if (path.indexOf('/') != -1) {
                String parent = path.substring(0, path.lastIndexOf('/'));

                RuleName pLabel = initLabel(folders, parent, false);
                label =
                    new RuleName(pLabel,
                        path.substring(path.lastIndexOf('/') + 1));
            } else {
                label = new RuleName(path);
            }
            if (!isFile) {
                folders.put(path, label);
            }
            return label;

        } else {
            return folders.get(path);
        }
    }

    @Override
    public URL createURL(File file) {
        try {
            return new URL("jar:" + Groove.toURL(file) + "!/");
        } catch (MalformedURLException e) {
            return null;
        }
    }

    @Override
    public String grammarName(URL grammarURL) {
        // the path in the jar of the gps
        String dir =
            grammarURL.getPath().substring(
                grammarURL.getPath().indexOf("!") + 2);

        if (!dir.endsWith("/")) {
            dir += "/";
        }

        // connection to the file index in the jar file
        try {
            // if there is no dir, find it in the jarfile
            if (dir.equals("/")) {
                JarFile jarFile =
                    ((JarURLConnection) grammarURL.openConnection()).getJarFile();
                dir = findGrammar(jarFile);
                if (dir == null) {
                    throw new IOException("No grammar found in "
                        + jarFile.getName());
                }
            }

            // create the result
            return FileGps.GRAMMAR_FILTER.stripExtension(new File(dir).getName());

        } catch (IOException e) {
            return "";
        }
    }

    @Override
    public String grammarLocation(URL grammarURL) {
        String file = grammarURL.getFile();
        file = file.substring(0, file.indexOf("!"));
        if (file.startsWith("file://")) {
            try {
                File local = Groove.toFile(new URL(file));
                return local.getAbsolutePath();
            } catch (MalformedURLException e) {
                return file;
            }
        } else {
            return file;
        }
    }

}

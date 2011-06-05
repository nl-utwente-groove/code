/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.io.external;

import groove.graph.DefaultGraph;
import groove.graph.GraphRole;
import groove.io.ExtensionFilter;
import groove.io.FileType;
import groove.io.GrooveFileChooser;
import groove.io.external.format.AutFormat;
import groove.io.external.format.ColFormat;
import groove.io.external.format.ExternalFileFormat;
import groove.io.xml.AspectGxl;
import groove.trans.DefaultHostGraph;
import groove.trans.HostGraph;
import groove.util.Duo;
import groove.view.aspect.AspectGraph;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileFilter;

/**
 * Class providing functionality to import a graph from a file in
 * different formats.
 * 
 * @author Eduardo Zambon
 */
public class Importer {

    /** List of the supported native import formats. */
    private final List<FileType> nativeFormats;

    /** List of the supported external import formats. */
    private final List<ExternalFileFormat<?>> externalFormats;

    /** File chooser with native and external import filters. */
    private final GrooveFileChooser allFormatsChooser;

    /** File chooser with only external import filters. */
    private final GrooveFileChooser externalFormatsChooser;

    /** Singleton instance of this class. */
    private static Importer instance = new Importer();

    /** Returns the singleton instance of this class. */
    public static Importer getInstance() {
        return instance;
    }

    private Importer() {
        this.nativeFormats = new ArrayList<FileType>();
        for (FileType nativeFileType : FileType.importNative) {
            this.nativeFormats.add(nativeFileType);
        }

        this.externalFormats = this.getExternalFormatList();

        this.allFormatsChooser =
            GrooveFileChooser.getFileChooser(this.getFilters(true));
        this.allFormatsChooser.setFileFilter(this.getDefaultFilter(true));

        this.externalFormatsChooser =
            GrooveFileChooser.getFileChooser(this.getFilters(false));
        this.externalFormatsChooser.setFileFilter(this.getDefaultFilter(false));
    }

    /** Show the file chooser dialog with the proper title. */
    public int showDialog(Component parent, boolean useNative) {
        return this.getFileChooser(useNative).showDialog(parent, "Import");
    }

    /**
     * Returns a file chooser for importing, lazily creating it first.
     */
    public GrooveFileChooser getFileChooser(boolean useNative) {
        if (useNative) {
            return this.allFormatsChooser;
        } else {
            return this.externalFormatsChooser;
        }
    }

    /** Returns the (modifiable) list of currently supported formats. */
    private List<ExternalFileFormat<?>> getExternalFormatList() {
        List<ExternalFileFormat<?>> result;
        if (this.externalFormats != null) {
            result = this.externalFormats;
        } else {
            result = new ArrayList<ExternalFileFormat<?>>();
            result.add(AutFormat.getInstance());
            result.add(ColFormat.getInstance());
        }
        return result;
    }

    private List<ExtensionFilter> getFilters(boolean useNative) {
        List<ExtensionFilter> result = new ArrayList<ExtensionFilter>();
        if (useNative) {
            for (FileType fileType : this.nativeFormats) {
                result.add(FileType.getFilter(fileType));
            }
        }
        for (ExternalFileFormat<?> format : this.getExternalFormatList()) {
            result.add(format.getFilter());
        }
        return result;
    }

    private ExtensionFilter getNativeFilter(FileType fileType) {
        ExtensionFilter result = null;
        int i;
        if ((i = this.nativeFormats.indexOf(fileType)) >= 0) {
            result = FileType.getFilter(this.nativeFormats.get(i));
        }
        return result;
    }

    private ExtensionFilter getNativeFilter(int index) {
        assert index >= 0 && index < this.nativeFormats.size();
        return FileType.getFilter(this.nativeFormats.get(index));
    }

    private ExtensionFilter getDefaultFilter(boolean useNative) {
        ExtensionFilter result;
        if (useNative) {
            result = this.getNativeFilter(0);
        } else {
            result = this.getExternalFormatList().get(0).getFilter();
        }
        return result;
    }

    /**
     * Returns a file format that accepts the file.
     */
    public ExternalFileFormat<?> getAcceptingFormat(File file) {
        ExternalFileFormat<?> result = null;
        for (ExternalFileFormat<?> format : this.getExternalFormatList()) {
            if (format.getFilter().accept(file)) {
                result = format;
                break;
            }
        }
        return result;
    }

    /**
     * Tries to load the currently selected file as a rule.
     * @return the aspect graph loaded from the file or null the file is not
     *         a rule.
     * @throws IOException if something went wrong when unmarshalling.
     */
    public AspectGraph importRule() throws IOException {
        AspectGraph rule = null;
        File ruleFile = this.allFormatsChooser.getSelectedFile();
        if (ruleFile != null
            && this.allFormatsChooser.getFileFilter().equals(
                this.getNativeFilter(FileType.RULE))) {
            rule = AspectGxl.getInstance().unmarshalGraph(ruleFile);
        }
        return rule;
    }

    /**
     * Tries to load the currently selected file as a host graph.
     * First native files are tried, if they fail, then external file formats
     * are used.
     * @return the aspect graph loaded from the file or null the file is not
     *         a host graph.
     * @throws IOException if something went wrong when unmarshalling.
     */
    @SuppressWarnings("unchecked")
    public AspectGraph importState(boolean useNative) throws IOException {
        AspectGraph graph = null;
        GrooveFileChooser chooser = this.getFileChooser(useNative);
        File graphFile = chooser.getSelectedFile();
        if (graphFile != null) {
            if (useNative
                && chooser.getFileFilter().equals(
                    this.getNativeFilter(FileType.STATE))) {
                graph = AspectGxl.getInstance().unmarshalGraph(graphFile);
            } else {
                ExternalFileFormat<?> extFormat =
                    this.getAcceptingFormat(graphFile);
                if (extFormat != null) {
                    String graphName = ExtensionFilter.getPureName(graphFile);
                    if (extFormat.equals(ColFormat.getInstance())) {
                        HostGraph hostGraph = new DefaultHostGraph(graphName);
                        ((ExternalFileFormat<HostGraph>) extFormat).load(
                            hostGraph, graphFile);
                        graph = hostGraph.toAspectMap().getAspectGraph();
                    } else if (extFormat.equals(AutFormat.getInstance())) {
                        DefaultGraph plainGraph = new DefaultGraph(graphName);
                        ((ExternalFileFormat<DefaultGraph>) extFormat).load(
                            plainGraph, graphFile);
                        plainGraph.setRole(GraphRole.HOST);
                        graph = AspectGraph.newInstance(plainGraph);
                    }
                }
            }
        }
        return graph;
    }

    /**
     * Tries to load the currently selected file as a type graph.
     * @return the aspect graph loaded from the file or null the file is not
     *         a type graph.
     * @throws IOException if something went wrong when unmarshalling.
     */
    public AspectGraph importType() throws IOException {
        AspectGraph type = null;
        File typeFile = this.allFormatsChooser.getSelectedFile();
        if (typeFile != null
            && this.allFormatsChooser.getFileFilter().equals(
                this.getNativeFilter(FileType.TYPE))) {
            type = AspectGxl.getInstance().unmarshalGraph(typeFile);
        }
        return type;
    }

    /**
     * Tries to import the currently selected file as a control program.
     * @return the name of the program and its content as a string.
     * @throws IOException if something went wrong when opening the file.
     */
    public Duo<String> importControl() throws IOException {
        File controlFile = this.allFormatsChooser.getSelectedFile();
        if (controlFile != null
            && this.allFormatsChooser.getFileFilter().equals(
                this.getNativeFilter(FileType.CONTROL))) {
            String name = ExtensionFilter.getPureName(controlFile);
            String program = groove.io.Util.readFileToString(controlFile);
            return new Duo<String>(name, program);
        } else {
            return null;
        }
    }

    /**
     * Tries to import the currently selected file as a prolog program.
     * @return the name of the program and its content as a string.
     * @throws IOException if something went wrong when opening the file.
     */
    public Duo<String> importProlog() throws IOException {
        File prologFile = this.allFormatsChooser.getSelectedFile();
        FileFilter filter = this.allFormatsChooser.getFileFilter();
        if (prologFile != null
            && (filter.equals(this.getNativeFilter(FileType.PROLOG1)) || filter.equals(this.getNativeFilter(FileType.PROLOG2)))) {
            String name = ExtensionFilter.getPureName(prologFile);
            String program = groove.io.Util.readFileToString(prologFile);
            return new Duo<String>(name, program);
        } else {
            return null;
        }
    }

}

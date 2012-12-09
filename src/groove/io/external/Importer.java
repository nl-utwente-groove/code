/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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

import groove.gui.Simulator;
import groove.io.ExtensionFilter;
import groove.io.GrooveFileChooser;
import groove.io.external.FormatImporter.Resource;
import groove.io.external.format.AutPorter;
import groove.io.external.format.ColImporter;
import groove.io.external.format.DotPorter;
import groove.io.external.format.EcorePorter;
import groove.io.external.format.GxlPorter;
import groove.io.external.format.NativePorter;
import groove.trans.ResourceKind;
import groove.view.GrammarModel;
import groove.view.aspect.AspectGraph;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Class to import various files into groove (generally native)
 * @author Harold
 * @version $Revision $
 */
public class Importer {
    private Importer() {
        // Register importers
        this.importers.add(NativePorter.getInstance());
        this.importers.add(AutPorter.getInstance());
        this.importers.add(ColImporter.getInstance());
        this.importers.add(EcorePorter.instance());
        this.importers.add(GxlPorter.instance());
        this.importers.add(DotPorter.getInstance());
        List<ExtensionFilter> filters = new ArrayList<ExtensionFilter>();

        for (FormatImporter ri : this.importers) {
            for (Format f : ri.getSupportedFormats()) {
                filters.add(new FormatFilter(f));
            }
        }

        this.formatChooser = GrooveFileChooser.getFileChooser(filters);
        this.formatChooser.setFileFilter(filters.get(0));
    }

    /**
     * Perform import. Show open dialog, and based on selected format import file.
     * @param simulator Parent of open dialog.
     */
    public void doImport(Simulator simulator, GrammarModel grammar)
        throws IOException {
        int approve =
            this.formatChooser.showDialog(simulator.getFrame(), "Import");
        // now load, if so required
        if (approve == JFileChooser.APPROVE_OPTION) {
            try {
                doChosenImport(simulator, grammar);
            } catch (PortException e) {
                throw new IOException(e);
            }
        }
    }

    private void doChosenImport(Simulator simulator, GrammarModel grammar)
        throws PortException, IOException {
        FormatFilter filter = (FormatFilter) this.formatChooser.getFileFilter();
        FormatImporter ri = (FormatImporter) filter.getFormat().getFormatter();
        File file = this.formatChooser.getSelectedFile();
        ri.setSimulator(simulator);
        Set<Resource> resources =
            ri.doImport(file, filter.getFormat(), grammar);
        if (resources != null) {
            Map<ResourceKind,Collection<AspectGraph>> newGraphs =
                new EnumMap<ResourceKind,Collection<AspectGraph>>(
                    ResourceKind.class);
            Map<ResourceKind,Map<String,String>> newTexts =
                new EnumMap<ResourceKind,Map<String,String>>(ResourceKind.class);
            for (Resource resource : resources) {
                String name = resource.getName();
                ResourceKind kind = resource.getKind();
                if (grammar.getResource(kind, name) == null
                    || confirmOverwrite(simulator.getFrame(), kind, name)) {
                    if (resource.isGraph()) {
                        AspectGraph graph = resource.getGraphResource();
                        Collection<AspectGraph> graphs = newGraphs.get(kind);
                        if (graphs == null) {
                            newGraphs.put(kind, graphs =
                                new ArrayList<AspectGraph>());
                        }
                        graphs.add(graph);
                    } else {
                        String text = resource.getTextResource();
                        Map<String,String> texts = newTexts.get(kind);
                        if (texts == null) {
                            newTexts.put(kind, texts =
                                new HashMap<String,String>());
                        }
                        texts.put(name, text);
                        grammar.getStore().putTexts(resource.getKind(),
                            Collections.singletonMap(name, text));
                    }
                }
            }
            for (Map.Entry<ResourceKind,Collection<AspectGraph>> entry : newGraphs.entrySet()) {
                grammar.getStore().putGraphs(entry.getKey(), entry.getValue(),
                    true);
            }
            for (Map.Entry<ResourceKind,Map<String,String>> entry : newTexts.entrySet()) {
                grammar.getStore().putTexts(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Asks whether a given existing resource, of a given kind,
     * should be replaced by a newly loaded one.
     */
    final protected boolean confirmOverwrite(Component parent,
            ResourceKind resource, String name) {
        int response =
            JOptionPane.showConfirmDialog(
                parent,
                String.format("Replace existing %s '%s'?",
                    resource.getDescription(), name), null,
                JOptionPane.OK_CANCEL_OPTION);
        return response == JOptionPane.OK_OPTION;
    }

    /** List of importers */
    private final List<FormatImporter> importers =
        new ArrayList<FormatImporter>();

    /** File chooser with native and external import filters. */
    private final GrooveFileChooser formatChooser;

    /** Returns the singleton instance of this class. */
    public static Importer instance() {
        return instance;
    }

    /** Singleton instance of this class. */
    private static final Importer instance = new Importer();
}

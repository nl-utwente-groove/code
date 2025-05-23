/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.io.external;

import java.awt.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.io.FileType;
import nl.utwente.groove.io.GrooveFileChooser;
import nl.utwente.groove.io.external.format.AutPorter;
import nl.utwente.groove.io.external.format.ColImporter;
import nl.utwente.groove.io.external.format.EcorePorter;
import nl.utwente.groove.io.external.format.NativeResourcePorter;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Utilities for importers.
 * @author Harold Bruijntjes
 * @version $Revision$
 */
public class Importers {
    /**
     * Perform import. Show open dialog, and based on selected format import file.
     * @param simulator Parent of open dialog.
     */
    public static void doImport(Simulator simulator, GrammarModel grammar) throws IOException {
        int approve = getFormatChooser().showDialog(simulator.getFrame(), "Import");
        // now load, if so required
        if (approve == JFileChooser.APPROVE_OPTION) {
            try {
                doChosenImport(simulator, grammar);
            } catch (PortException | FormatException e) {
                throw new IOException(e);
            }
        }
    }

    private static void doChosenImport(Simulator simulator,
                                       GrammarModel grammar) throws PortException, FormatException,
                                                             IOException {
        FileType fileType = getFormatChooser().getFileType();
        Importer ri = getImporter(fileType);
        ri.setSimulator(simulator);
        var store = simulator.getModel().getStore();
        Set<Imported> resources = new HashSet<>();
        for (var file : getFormatChooser().getSelectedFiles()) {
            resources.addAll(ri.doImport(file, fileType, grammar));
        }
        if (!resources.isEmpty()) {
            Map<ResourceKind,Collection<AspectGraph>> newGraphs = new EnumMap<>(ResourceKind.class);
            Map<ResourceKind,Map<QualName,String>> newTexts = new EnumMap<>(ResourceKind.class);
            for (Imported resource : resources) {
                QualName name = resource.qualName();
                name.getErrors().throwException();
                ResourceKind kind = resource.kind();
                if (grammar.getResource(kind, name) == null
                    || confirmOverwrite(simulator.getFrame(), kind, name)) {
                    if (resource.isGraph()) {
                        AspectGraph graph = resource.graph();
                        Collection<AspectGraph> graphs = newGraphs.get(kind);
                        if (graphs == null) {
                            newGraphs.put(kind, graphs = new ArrayList<>());
                        }
                        graphs.add(graph);
                    } else {
                        String text = resource.text();
                        Map<QualName,String> texts = newTexts.get(kind);
                        if (texts == null) {
                            newTexts.put(kind, texts = new HashMap<>());
                        }
                        texts.put(name, text);
                        store.putTexts(kind, Collections.singletonMap(name, text));
                    }
                }
            }
            for (Map.Entry<ResourceKind,Collection<AspectGraph>> entry : newGraphs.entrySet()) {
                store.putGraphs(entry.getKey(), entry.getValue(), true);
            }
            for (Map.Entry<ResourceKind,Map<QualName,String>> entry : newTexts.entrySet()) {
                store.putTexts(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Asks whether a given existing resource, of a given kind,
     * should be replaced by a newly loaded one.
     */
    private static boolean confirmOverwrite(Component parent, ResourceKind resource,
                                            QualName name) {
        int response = JOptionPane
            .showConfirmDialog(parent, String
                .format("Replace existing %s '%s'?", resource.getDescription(), name), null,
                               JOptionPane.OK_CANCEL_OPTION);
        return response == JOptionPane.OK_OPTION;
    }

    /** Returns the list of all known importers. */
    public static List<Importer> getImporters() {
        if (importers == null) {
            importers = createImporters();
        }
        return importers;
    }

    private static List<Importer> createImporters() {
        List<Importer> result = new ArrayList<>();
        result.add(NativeResourcePorter.getInstance());
        result.add(AutPorter.instance());
        result.add(ColImporter.getInstance());
        result.add(EcorePorter.instance());
        return Collections.unmodifiableList(result);
    }

    /** List of importers */
    private static List<Importer> importers;

    /** Returns the importer for a given file type, if any. */
    public static Importer getImporter(FileType fileType) {
        return getImporterMap().get(fileType);
    }

    /** Returns the mapping from file types to importers supporting them. */
    private static Map<FileType,Importer> getImporterMap() {
        if (importerMap == null) {
            importerMap = createImporterMap();
        }
        return importerMap;
    }

    /** Creates the mapping from file types to importers supporting them. */
    private static Map<FileType,Importer> createImporterMap() {
        Map<FileType,Importer> result = new EnumMap<>(FileType.class);
        for (Importer ri : getImporters()) {
            for (FileType fileType : ri.getFileTypes()) {
                result.put(fileType, ri);
            }
        }
        return result;
    }

    /** Mapping from file types to importers supporting them. */
    private static Map<FileType,Importer> importerMap;

    /** Returns the file chooser for all importers. */
    private static GrooveFileChooser getFormatChooser() {
        if (formatChooser == null) {
            formatChooser = GrooveFileChooser.getInstance(getImporterMap().keySet(), true);
        }
        return formatChooser;
    }

    /** File chooser with native and external import filters. */
    private static GrooveFileChooser formatChooser;
}

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

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import groove.gui.Simulator;
import groove.gui.dialog.ErrorDialog;
import groove.gui.dialog.SaveDialog;
import groove.io.FileType;
import groove.io.GrooveFileChooser;
import groove.io.external.Porter.Kind;
import groove.io.external.format.AutPorter;
import groove.io.external.format.DotPorter;
import groove.io.external.format.EcorePorter;
import groove.io.external.format.FsmExporter;
import groove.io.external.format.GxlPorter;
import groove.io.external.format.NativePorter;
import groove.io.external.format.RasterExporter;
import groove.io.external.format.TikzExporter;
import groove.io.external.format.VectorExporter;
import groove.util.Pair;

/**
 * Class used to export various resources and graphs to external formats.
 * Export gets initiated by ExportAction.
 * @author Harold Bruijntjes
 * @version $Revision $
 */
public class Exporters {
    /**
     * Exports the object contained in an exportable, using an
     * exporter chosen through a dialog.
     * @param exportable Container with object to export
     * @param simulator parent of save dialog; may be {@code null}
     */
    public static void doExport(Exportable exportable, Simulator simulator) {
        // determine the set of suitable file types and exporters
        Map<FileType,Exporter> exporters = new EnumMap<>(FileType.class);
        for (Exporter exporter : getExporters()) {
            for (FileType fileType : exporter.getFileTypes(exportable)) {
                exporters.put(fileType, exporter);
            }
        }
        assert!exporters.isEmpty();
        // choose a file and exporter
        GrooveFileChooser chooser = GrooveFileChooser.getInstance(exporters.keySet());
        chooser.setSelectedFile(exportable.getQualName()
            .toFile());
        File selectedFile =
            SaveDialog.show(chooser, simulator == null ? null : simulator.getFrame(), null);
        // now save, if so required
        if (selectedFile != null) {
            try {
                // Get exporter
                FileType fileType = chooser.getFileType();
                Exporter e = exporters.get(fileType);
                e.setSimulator(simulator);
                e.doExport(exportable, selectedFile, fileType);
            } catch (PortException e) {
                showErrorDialog(simulator == null ? null : simulator.getFrame(),
                    e,
                    "Error while exporting to " + selectedFile);
            }
        }
    }

    /**
     * Creates and shows an {@link ErrorDialog} for a given message and
     * exception.
     */
    private static void showErrorDialog(Component parent, Throwable exc, String message,
        Object... args) {
        new ErrorDialog(parent, String.format(message, args), exc).setVisible(true);
    }

    /**
     * Get suitable exporter for a given file name, based on the extension.
     * Backwards compatibility function for now.
     */
    static public Pair<FileType,Exporter> getAcceptingFormat(String filename) {
        Pair<FileType,Exporter> result = null;
        outer: for (Exporter exporter : getExporters()) {
            if (!exporter.getFormatKinds()
                .contains(Kind.GRAPH)) {
                continue;
            }
            for (FileType fileType : exporter.getSupportedFileTypes()) {
                if (fileType.hasExtension(filename)) {
                    result = Pair.newPair(fileType, exporter);
                    break outer;
                }
            }
        }
        return result;
    }

    /** Returns the exporter for a given file type, if any. */
    public static Exporter getExporter(FileType fileType) {
        return getExporterMap().get(fileType);
    }

    /** Returns the list of all known exporters. */
    public static List<Exporter> getExporters() {
        if (exporters == null) {
            exporters = createExporters();
        }
        return exporters;
    }

    /** Creates the list of all known exporters. */
    private static List<Exporter> createExporters() {
        List<Exporter> result = new ArrayList<>();
        result.add(NativePorter.getInstance());
        result.add(RasterExporter.getInstance());
        result.add(VectorExporter.getInstance());
        result.add(AutPorter.instance());
        result.add(FsmExporter.getInstance());
        result.add(TikzExporter.getInstance());
        result.add(EcorePorter.instance());
        result.add(GxlPorter.instance());
        result.add(DotPorter.instance());
        return result;
    }

    private static List<Exporter> exporters;

    /** Returns the mapping from file types to exporters for those file types. */
    public static Map<FileType,Exporter> getExporterMap() {
        if (exporterMap == null) {
            exporterMap = createExporterMap();
        }
        return exporterMap;
    }

    /** Creates the list of all known dedicated exporters. */
    private static Map<FileType,Exporter> createExporterMap() {
        Map<FileType,Exporter> result = new EnumMap<>(FileType.class);
        for (Exporter exporter : getExporters()) {
            for (FileType fileType : exporter.getSupportedFileTypes()) {
                Exporter oldValue = result.put(fileType, exporter);
                assert oldValue == null : String.format("Duplicate exporter for file type: %s",
                    fileType.name());
            }
        }
        return Collections.unmodifiableMap(result);
    }

    private static Map<FileType,Exporter> exporterMap;
}

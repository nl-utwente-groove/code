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
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.dialog.ErrorDialog;
import nl.utwente.groove.gui.dialog.SaveDialog;
import nl.utwente.groove.io.FileType;
import nl.utwente.groove.io.GrooveFileChooser;
import nl.utwente.groove.io.external.Exporter.ExportKind;
import nl.utwente.groove.io.external.format.AutPorter;
import nl.utwente.groove.io.external.format.EcorePorter;
import nl.utwente.groove.io.external.format.FsmExporter;
import nl.utwente.groove.io.external.format.GraphExportListener.DotListener;
import nl.utwente.groove.io.external.format.LTS2ControlExporter;
import nl.utwente.groove.io.external.format.ListenerExporter;
import nl.utwente.groove.io.external.format.NativeGraphExporter;
import nl.utwente.groove.io.external.format.NativeResourcePorter;
import nl.utwente.groove.io.external.format.RasterExporter;
import nl.utwente.groove.io.external.format.TikzExporter;
import nl.utwente.groove.io.external.format.VectorExporter;
import nl.utwente.groove.util.Factory;

/**
 * Support class for {@link Exporter}s.
 * @author Harold Bruijntjes
 * @version $Revision$
 */
public class Exporters {
    /**
     * Exports the object contained in an exportable, using an
     * exporter chosen through a dialog.
     * @param exportable container with object to export
     * @param simulator parent of save dialog; may be {@code null}
     */
    public static void doExport(Exportable exportable, Simulator simulator) {
        // determine the set of suitable file types and exporters
        Map<FileType,Exporter> exporters = new EnumMap<>(FileType.class);
        for (Exporter exporter : getExporters()) {
            exporter.getFileTypes(exportable).forEach(ft -> exporters.put(ft, exporter));
        }
        assert !exporters.isEmpty();
        // choose a file and exporter
        GrooveFileChooser chooser = GrooveFileChooser.getInstance(exporters.keySet());
        chooser.setSelectedFile(exportable.qualName().toFile());
        File selectedFile = SaveDialog
            .show(chooser, simulator == null
                ? null
                : simulator.getFrame(), null);
        // now save, if so required
        if (selectedFile != null) {
            try {
                // Get exporter
                FileType fileType = chooser.getFileType();
                Exporter e = exporters.get(fileType);
                e.setSimulator(simulator);
                e.doExport(exportable, selectedFile, fileType);
            } catch (PortException e) {
                showErrorDialog(simulator == null
                    ? null
                    : simulator.getFrame(), e, "Error while exporting to " + selectedFile);
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

    /** Returns the exporter for a given filename, if any.
     * Convenience method for {@code getExporter(FileType.getType(filename))},
     * taking {@code null} values into account.
     */
    public static Exporter getExporter(@NonNull ExportKind exportKind, @NonNull String filename) {
        var fileType = FileType.getExtension(filename);
        return fileType == null
            ? null
            : getExporter(exportKind, fileType);
    }

    /** Returns the exporter for a given export kind and file type, if any.
     * Returns {@code null} if the file type is {@code null}.
     */
    public static Exporter getExporter(@NonNull ExportKind exportKind,
                                       @Nullable FileType fileType) {
        return fileType == null
            ? null
            : getExporterMap(exportKind).get(fileType);
    }

    /** Returns the list of all known exporters. */
    public static List<Exporter> getExporters() {
        return exporters.get();
    }

    static private final Factory<List<Exporter>> exporters
        = Factory.lazy(Exporters::createExporters);

    /** Creates the list of all known exporters. */
    private static List<Exporter> createExporters() {
        List<Exporter> result = new ArrayList<>();
        result.add(NativeResourcePorter.getInstance());
        result.add(NativeGraphExporter.getInstance());
        result.add(RasterExporter.getInstance());
        result.add(VectorExporter.getInstance());
        result.add(AutPorter.instance());
        result.add(FsmExporter.getInstance());
        result.add(TikzExporter.getInstance());
        result.add(EcorePorter.instance());
        result.add(ListenerExporter.instance(DotListener.instance()));
        result.add(LTS2ControlExporter.instance());
        return Collections.unmodifiableList(result);
    }

    /** Returns the mapping from file types to exporters for those file types. */
    public static Map<FileType,Exporter> getExporterMap(ExportKind exportKind) {
        return exporterMap.get().get(exportKind);
    }

    private static Factory<Map<ExportKind,Map<FileType,Exporter>>> exporterMap
        = Factory.lazy(Exporters::createExporterMap);

    /** Creates the list of all known dedicated exporters. */
    private static Map<ExportKind,Map<FileType,Exporter>> createExporterMap() {
        Map<ExportKind,Map<FileType,Exporter>> result = new EnumMap<>(ExportKind.class);
        Arrays
            .stream(ExportKind.values())
            .forEach(k -> result.put(k, new EnumMap<>(FileType.class)));
        for (Exporter exporter : getExporters()) {
            var exportKind = exporter.getExportKind();
            var localMap = result.get(exportKind);
            for (FileType fileType : exporter.getFileTypes()) {
                Exporter oldValue = localMap.put(fileType, exporter);
                assert oldValue == null : String
                    .format("Duplicate exporter for export kind %s and file type %s", exportKind,
                            fileType);
            }
        }
        return Collections.unmodifiableMap(result);
    }
}

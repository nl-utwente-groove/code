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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.io.FileType;
import nl.utwente.groove.io.external.format.AutPorter;
import nl.utwente.groove.io.external.format.FsmExporter;
import nl.utwente.groove.io.external.format.GraphExportListener.DotListener;
import nl.utwente.groove.io.external.format.LTS2ControlExporter;
import nl.utwente.groove.io.external.format.ListenerExporter;
import nl.utwente.groove.io.external.format.NativeResourcePorter;
import nl.utwente.groove.io.external.format.RasterExporter;
import nl.utwente.groove.io.external.format.TikzExporter;
import nl.utwente.groove.io.external.format.VectorExporter;
import nl.utwente.groove.util.Factory;

/**
 * Registry of the known {@link Exporter}s.
 * @author Harold Bruijntjes
 * @version $Revision$
 */
public class Exporters {
    /** Returns the first registered exporter supporting a given file type, if any.
     * Returns {@code null} if the file type is {@code null}.
     */
    public static @Nullable Exporter getExporter(@Nullable FileType fileType) {
        if (fileType != null) {
            for (Exporter exporter : getExporters()) {
                if (exporter.getFileTypes().contains(fileType)) {
                    return exporter;
                }
            }
        }
        return null;
    }

    /** Returns the first registered exporter that can export a given exportable
     * to a given file type, if any.
     * Returns {@code null} if the file type is {@code null}.
     */
    public static @Nullable Exporter getExporter(@Nullable FileType fileType,
                                                 Exportable exportable) {
        if (fileType != null) {
            for (Exporter exporter : getExporters()) {
                if (exporter.getFileTypes(exportable).contains(fileType)) {
                    return exporter;
                }
            }
        }
        return null;
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
        result.add(RasterExporter.getInstance());
        result.add(VectorExporter.getInstance());
        result.add(AutPorter.instance());
        result.add(FsmExporter.getInstance());
        result.add(TikzExporter.getInstance());
        result.add(ListenerExporter.instance(DotListener.instance()));
        result.add(LTS2ControlExporter.instance());
        return Collections.unmodifiableList(result);
    }
}

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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.io.external.format.AutPorter;
import nl.utwente.groove.io.external.format.FsmExporter;
import nl.utwente.groove.io.external.format.LTS2ControlExporter;
import nl.utwente.groove.io.external.format.ListenerExporter;
import nl.utwente.groove.io.external.format.NativeResourcePorter;
import nl.utwente.groove.io.external.format.ecore.EcorePorter;
import nl.utwente.groove.io.graph.DotListener;
import nl.utwente.groove.util.Factory;
import nl.utwente.groove.util.io.FileType;

/**
 * Registry of the known {@link Exporter}s.
 * The registry initially contains the exporters of the io framework itself;
 * exporters that require the GUI (because they work by rendering a graph)
 * are added by the GUI at start-up, through {@link #register(Exporter)}.
 * @author Harold Bruijntjes
 * @version $Revision$
 */
@NonNullByDefault
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

    /** Adds an exporter to the registry, if it is not already registered.
     * The registration order is the order in which the exporters are consulted.
     */
    public static void register(Exporter exporter) {
        exporters.get().add(exporter);
    }

    /** Returns the set of all registered exporters, in registration order. */
    public static Set<Exporter> getExporters() {
        return Collections.unmodifiableSet(exporters.get());
    }

    static private final Factory<Set<Exporter>> exporters
        = Factory.lazy(Exporters::createExporters);

    /** Creates the set of exporters of the io framework itself. */
    private static Set<Exporter> createExporters() {
        Set<Exporter> result = new LinkedHashSet<>();
        result.add(NativeResourcePorter.getInstance());
        result.add(EcorePorter.instance());
        result.add(AutPorter.instance());
        result.add(FsmExporter.getInstance());
        result.add(ListenerExporter.instance(DotListener::new));
        result.add(LTS2ControlExporter.instance());
        return result;
    }
}

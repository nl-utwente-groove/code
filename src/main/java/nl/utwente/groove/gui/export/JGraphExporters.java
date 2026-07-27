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
package nl.utwente.groove.gui.export;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.io.external.Exporters;

/**
 * The exporters that work by rendering a {@link JGraphExportable}.
 * As they need the GUI, they are not part of the {@link Exporters} registry
 * by default but have to be contributed by any GUI-based tool at start-up.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class JGraphExporters {
    private JGraphExporters() {
        // empty, to prevent instantiation
    }

    /** Adds the JGraph-based exporters to the {@link Exporters} registry.
     * Repeated invocations have no effect.
     */
    static public void register() {
        Exporters.register(RasterExporter.getInstance());
        Exporters.register(VectorExporter.getInstance());
        Exporters.register(TikzExporter.getInstance());
    }
}

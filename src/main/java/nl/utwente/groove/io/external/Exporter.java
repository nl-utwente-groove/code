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

import java.io.File;
import java.util.Set;

import nl.utwente.groove.grammar.model.ResourceModel;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.gui.jgraph.JGraph;
import nl.utwente.groove.io.FileType;

/**
 * Class used to save {@link Exportable}s to files in a predefined
 * format (determined by {@link FileType}).
 * An {@link Exporter} supports a single {@link ExportKind}, but may not support every
 * {@link Exportable} of that kind; this can be tested using {@link #exports(Exportable)}.
 * If an {@link Exportable} is supported, there may be multiple file types to which it can
 * be exported; these can be obtained through {@link #getFileTypes(Exportable)}.
 * For any combination of {@link ExportKind} and {@link FileType}, there is at most
 * one {@link Exporter} supporting this.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface Exporter extends Porter {
    /** Returns the export kind supported by this exporter. */
    public ExportKind getKind();

    /** Indicates if this exporter supports a given export kind. */
    default public boolean hasKind(ExportKind exportKind) {
        return getKind() == exportKind;
    }

    /**
     * Indicates if this exporter is suitable for processing a given exportable.
     * This is true if and only if {@link #getFileTypes(Exportable)} is non-empty.
     */
    default public boolean exports(Exportable exportable) {
        return !getFileTypes(exportable).isEmpty();
    }

    /** Returns the file types that can be used for a given exportable. */
    public Set<FileType> getFileTypes(Exportable exportable);

    /**
     * Exports a given exportable resource.
     * @param exportable the (non-{@code null}) resource to be exported
     * @param file destination file
     * @param fileType used to determine format and extension
     * @throws PortException if something went wrong during export (typically I/O-related)
     */
    public void doExport(Exportable exportable, File file, FileType fileType) throws PortException;

    /** Kinds of objects that can be exported. */
    enum ExportKind {
        /** Instances of {@link Graph}. */
        GRAPH,
        /** Instances of {@link JGraph}. */
        JGRAPH,
        /** Instances of {@link ResourceModel}. */
        RESOURCE;
    }
}

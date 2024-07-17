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

import java.awt.Frame;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.io.FileType;

/** Abstract superclass for {@link Exporter}s, containing a few helper methods. */
public abstract class AbstractExporter implements Exporter {
    /** Constructor for subclassing. */
    protected AbstractExporter(ExportKind exportKind) {
        this.exportKind = exportKind;
        this.fileTypes = EnumSet.noneOf(FileType.class);
    }

    @Override
    public final ExportKind getExportKind() {
        return this.exportKind;
    }

    /** The export kind of this exporter. */
    private final ExportKind exportKind;

    @Override
    public Set<FileType> getFileTypes() {
        return this.fileTypes;
    }

    /**
     * Registers a file type supported by this exporter.
     * Should only be called from subclasses, during construction time.
     */
    protected final void register(FileType fileType) {
        this.fileTypes.add(fileType);
    }

    /** The set of all supported file types. */
    private final Set<FileType> fileTypes;

    /** This implementation returns the empty set for exportables that do not
     * have the export kind required by this exporter; otherwise it calls {@link #getFileTypes()}.
     */
    @Override
    public Set<FileType> getFileTypes(Exportable exportable) {
        if (exportable.hasExportKind(getExportKind())) {
            return getFileTypes();
        } else {
            return Collections.emptySet();
        }
    }

    /** Returns the parent component for a dialog. */
    protected final Frame getParent() {
        return this.simulator == null
            ? null
            : this.simulator.getFrame();
    }

    /** Returns the simulator on which this exporter works. */
    protected final Simulator getSimulator() {
        return this.simulator;
    }

    @Override
    public void setSimulator(Simulator simulator) {
        this.simulator = simulator;
    }

    private Simulator simulator;
}

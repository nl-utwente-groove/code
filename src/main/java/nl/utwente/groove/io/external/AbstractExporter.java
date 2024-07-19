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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
    public boolean exports(Exportable exportable) {
        return exportable.hasExportKind(getExportKind());
    }

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
        if (exports(exportable)) {
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

    /** Subclass of AbstractExporter containing functionality to write to a PrintWriter. */
    static public abstract class Writer extends AbstractExporter {
        /**
         * Invokes the super constructor.
         */
        protected Writer(ExportKind exportKind) {
            super(exportKind);
        }

        /** Prototypical implementation that first calls
         * #initialise(Exportable,FileType), then opens a PrintWriter on the file
         * and finally calls doExport(
         */
        @Override
        public void doExport(Exportable exportable, File file,
                             FileType fileType) throws PortException {
            initialise(exportable, fileType);
            try (PrintWriter writer = new PrintWriter(file)) {
                this.writer = writer;
                execute();
            } catch (FileNotFoundException e) {
                throw new PortException(e);
            }
        }

        /** Callback method from {@link #doExport(Exportable, File, FileType)} to initialise exporting a given exportable.
         * @throws PortException if this exporter is not compatible with the exportable
         */
        protected abstract void initialise(Exportable exportable,
                                           FileType fileType) throws PortException;

        /** Callback method from {@link #doExport(Exportable, File, FileType)} to
         * do the actual export, based on the initialised values.
         * In particular, the writer has been opened and should be filled by calls
         * to {@link #emit(String)}.
         */
        protected abstract void execute() throws PortException;

        /** Writes a line to the export file. */
        public void emit(String line) {
            this.writer.println(this.indent + line);
        }

        /** Adds an step to the space indentation prefixed to every {@link #emit(String)} line. */
        public void increaseIndent() {
            this.indent.append(INDENT_STEP);
        }

        /** Removes a step from the space indentation prefixed to every {@link #emit(String)} line. */
        public void decreaseIndent() {
            this.indent.delete(0, INDENT_STEP.length());
        }

        /** Indentation prefixed to every {@link #emit(String)} line. */
        private final StringBuffer indent = new StringBuffer("");

        /** Increase to the indent upon invocation of {@link #increaseIndent()}. */
        static private final String INDENT_STEP = "  ";

        private PrintWriter writer;
    }
}

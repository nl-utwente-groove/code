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

import groove.grammar.model.ResourceKind;
import groove.gui.Simulator;
import groove.io.FileType;

import java.awt.Frame;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/** Abstract superclass for {@link Exporter}s, containing a few helper methods. */
public abstract class AbstractExporter implements Exporter {
    /** Constructor for subclassing. */
    protected AbstractExporter(Kind... formatKinds) {
        this.formatKinds = EnumSet.copyOf(Arrays.asList(formatKinds));
        this.fileTypes = EnumSet.noneOf(FileType.class);
        this.fileTypeMap = new EnumMap<>(ResourceKind.class);
        this.resourceKindMap = new EnumMap<>(FileType.class);
    }

    @Override
    public final Set<Kind> getFormatKinds() {
        return this.formatKinds;
    }

    private final EnumSet<Kind> formatKinds;

    /** 
     * Registers a file type supported by this exporter.
     * The file type is assumed to be suitable for exporting graphs.
     * Should only be called from subclasses, during construction time,
     * and only if {@link #getFormatKinds()} equals {@link Porter.Kind#GRAPH}
     * or {@link Porter.Kind#JGRAPH}.
     */
    protected final void register(FileType fileType) {
        assert getFormatKinds().contains(Kind.GRAPH) || getFormatKinds().contains(Kind.JGRAPH);
        this.fileTypes.add(fileType);
    }

    /** 
     * Registers a file type supported by this exporter, to be used for a given resource kind.
     * Should only be called if {@link #getFormatKinds()} equals {@link Porter.Kind#RESOURCE}.
     */
    protected final void register(ResourceKind kind, FileType fileType) {
        assert getFormatKinds().contains(Kind.RESOURCE);
        this.fileTypes.add(fileType);
        this.fileTypeMap.put(kind, fileType);
        this.resourceKindMap.put(fileType, kind);
    }

    /** Returns the file type registered for a given resource kind, if any. */
    protected final FileType getFileType(ResourceKind kind) {
        return this.fileTypeMap.get(kind);
    }

    /** Returns the resource kind associated with a given file type, if any. */
    protected final ResourceKind getResourceKind(FileType fileType) {
        return this.resourceKindMap.get(fileType);
    }

    /** 
     * Registers a resource kind supported by this exporter, with its default file type.
     * Should only be called if {@link #getFormatKinds()} equals {@link Porter.Kind#RESOURCE}.
     */
    protected final void register(ResourceKind kind) {
        register(kind, kind.getFileType());
    }

    private final Set<FileType> fileTypes;
    private final Map<ResourceKind,FileType> fileTypeMap;
    private final Map<FileType,ResourceKind> resourceKindMap;

    @Override
    public Set<FileType> getSupportedFileTypes() {
        return this.fileTypes;
    }

    @Override
    public boolean exports(Exportable exportable) {
        return !getFileTypes(exportable).isEmpty();
    }

    @Override
    public Set<FileType> getFileTypes(Exportable exportable) {
        Set<FileType> result = EnumSet.noneOf(FileType.class);
        boolean supports = false;
        for (Kind porterKind : getFormatKinds()) {
            if (exportable.containsKind(porterKind)) {
                supports = true;
                break;
            }
        }
        if (supports) {
            if (exportable.containsKind(Kind.RESOURCE) && getFormatKinds().contains(Kind.RESOURCE)) {
                // check if the specific resource kind is supported
                FileType fileType = getFileType(exportable.getModel().getKind());
                if (fileType != null) {
                    result.add(fileType);
                }
            } else {
                result.addAll(getSupportedFileTypes());
            }
        }
        return Collections.unmodifiableSet(result);
    }

    /** Returns the parent component for a dialog. */
    protected final Frame getParent() {
        return this.simulator == null ? null : this.simulator.getFrame();
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

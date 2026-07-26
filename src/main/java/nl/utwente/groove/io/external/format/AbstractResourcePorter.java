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
package nl.utwente.groove.io.external.format;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.GraphConverter;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.grammar.model.TextBasedModel;
import nl.utwente.groove.graph.plain.PlainGraph;
import nl.utwente.groove.io.FileType;
import nl.utwente.groove.io.external.AbstractExporter;
import nl.utwente.groove.io.external.Exportable;
import nl.utwente.groove.io.external.Imported;
import nl.utwente.groove.io.external.Importer;
import nl.utwente.groove.io.external.PortException;
import nl.utwente.groove.io.graph.AttrGraph;
import nl.utwente.groove.io.graph.GxlIO;

/**
 * Imports and exports (certain types of) resources.
 * Enforces a (partial) one-to-one mapping of resource kinds and file types.
 * Doubles as the exporter of graphs that are not grammar resources (such as
 * LTSs), which are saved as plain graphs in the native format.
 * @author Harold Bruijntjes
 * @version $Revision$
 */
public class AbstractResourcePorter extends AbstractExporter implements Importer {
    /** Constructor for subclasses. */
    protected AbstractResourcePorter() {
        super(ExportKind.RESOURCE);
        this.fileTypeMap = new EnumMap<>(ResourceKind.class);
        this.resourceKindMap = new EnumMap<>(FileType.class);
        this.graphFileTypes = EnumSet.noneOf(FileType.class);
    }

    /**
     * Registers a resource kind supported by this exporter, with corresponding file type.
     */
    protected final void register(ResourceKind kind, FileType fileType) {
        register(fileType);
        var oldType = this.fileTypeMap.put(kind, fileType);
        assert oldType == null : String
            .format("Duplicate file types %s and %s for file type %s", oldType, fileType, kind);
        var oldKind = this.resourceKindMap.put(fileType, kind);
        assert oldKind == null : String
            .format("Duplicate resource kinds %s and %s for file type %s", oldKind, kind, fileType);
        if (kind.isGraphBased()) {
            this.graphFileTypes.add(fileType);
        }
    }

    /** Returns the registered file types of the graph-based resource kinds. */
    protected final Set<FileType> getGraphFileTypes() {
        return this.graphFileTypes;
    }

    /** The registered file types of the graph-based resource kinds. */
    private final Set<FileType> graphFileTypes;

    /** Returns the file type registered for a given resource kind, if any. */
    protected final @Nullable FileType getFileType(ResourceKind kind) {
        return this.fileTypeMap.get(kind);
    }

    /** Returns the resource kind associated with a given file type, if any. */
    protected final @Nullable ResourceKind getResourceKind(FileType fileType) {
        return this.resourceKindMap.get(fileType);
    }

    private final Map<ResourceKind,FileType> fileTypeMap;
    /** Map from file type the native file type. */
    private final Map<FileType,ResourceKind> resourceKindMap;

    /** This implementation accepts every exportable that either contains a graph
     * (which is saved in the native graph format) or is a resource of a
     * registered kind (which is saved as its source text).
     */
    @Override
    public boolean exports(Exportable exportable) {
        var resourceKind = exportable.getResourceKind();
        return exportable.graph() != null
            || resourceKind != null && getFileType(resourceKind) != null;
    }

    /** This implementation returns the native graph file types for exportables
     * containing a graph, and the file type of the resource kind for the others.
     */
    @Override
    public Set<FileType> getFileTypes(Exportable exportable) {
        if (!exports(exportable)) {
            return Collections.emptySet();
        } else if (exportable.graph() == null) {
            return Collections.singleton(getFileType(exportable.getResourceKind()));
        } else {
            return getGraphFileTypes();
        }
    }

    @Override
    public Set<Imported> doImport(File file, FileType fileType,
                                  GrammarModel grammar) throws PortException {
        Imported result;
        try {
            QualName name = QualName.name(fileType.stripExtension(file.getName()));
            ResourceKind kind = getResourceKind(fileType);
            if (kind == null) {
                throw new PortException(String
                    .format("'%s' is not a grammar resource and hence cannot be imported from %s",
                            name, fileType.getExtension()));
            } else if (kind.isGraphBased()) {
                // read graph from file
                AttrGraph xmlGraph = GxlIO.instance().loadGraph(file);
                xmlGraph.setRole(kind.getGraphRole());
                xmlGraph.setName(name.toString());
                result = new Imported(kind, xmlGraph.toAspectGraph());
            } else {
                String program = nl.utwente.groove.io.Util.readFileToString(file);
                result = new Imported(kind, name, program);
            }
        } catch (IOException e) {
            throw new PortException(e);
        }
        return Collections.singleton(result);
    }

    @Override
    public Set<Imported> doImport(QualName name, InputStream stream, FileType fileType,
                                  GrammarModel grammar) throws PortException {
        ResourceKind kind = getResourceKind(fileType);
        if (kind == null) {
            throw new PortException(String
                .format("'%s' is not a grammar resource and hence cannot be imported from %s", name,
                        fileType.getExtension()));
        } else if (kind.isGraphBased()) {
            throw new PortException(String.format("Cannot import '%s' from stream", name));
        }

        Imported result;
        try {
            String resource = nl.utwente.groove.io.Util.readInputStreamToString(stream);
            result = new Imported(kind, name, resource);
        } catch (IOException e) {
            throw new PortException(e);
        }
        return Collections.singleton(result);
    }

    @Override
    public void doExport(Exportable exportable, File file, FileType fileType) throws PortException {
        var resourceKind = exportable.getResourceKind();
        var graph = exportable.graph();
        if (graph != null && resourceKind == null) {
            // this is a graph outside the grammar, such as an LTS;
            // it is saved as a plain graph
            var plainGraph = graph.getRole().inGrammar()
                ? GraphConverter.toAspect(graph).toPlainGraph()
                : PlainGraph.instance(graph);
            try {
                GxlIO.instance().saveGraph(plainGraph, file);
            } catch (IOException e) {
                throw new PortException(e);
            }
        } else if (resourceKind == null) {
            throw new PortException(String
                .format("'%s' is not a grammar resource and hence cannot be exported as %s",
                        exportable.qualName(), fileType.getExtension()));
        } else if (resourceKind.isGraphBased()) {
            AspectGraph aspectGraph = GraphConverter.toAspect(graph);
            if (resourceKind == ResourceKind.HOST && fileType != FileType.STATE) {
                // we are converting a host graph to a rule or type graph
                // so unwrap any literal labels
                aspectGraph = aspectGraph.unwrap();
            }
            try {
                GxlIO.instance().saveGraph(aspectGraph.toPlainGraph(), file);
            } catch (IOException e) {
                throw new PortException(e);
            }
        } else {
            var textModel = (TextBasedModel<?>) exportable.resourceModel();
            assert textModel != null;
            try (var writer = new FileWriter(file)) {
                writer.write(textModel.getSource());
            } catch (IOException e) {
                throw new PortException(e);
            }
        }
    }
}

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
package groove.io.external.format;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import groove.grammar.QualName;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.aspect.GraphConverter;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.ResourceKind;
import groove.grammar.model.TextBasedModel;
import groove.io.FileType;
import groove.io.external.AbstractExporter;
import groove.io.external.Exportable;
import groove.io.external.Importer;
import groove.io.external.PortException;
import groove.io.graph.AttrGraph;
import groove.io.graph.GxlIO;

/**
 * Import and export resources native to GROOVE, such as type and host graphs, and control programs
 * @author Harold Bruijntjes
 * @version $Revision $
 */
public class NativePorter extends AbstractExporter implements Importer {
    private NativePorter() {
        super(Kind.GRAPH, Kind.RESOURCE);
        register(ResourceKind.TYPE);
        register(ResourceKind.HOST);
        register(ResourceKind.HOST, FileType.RULE);
        register(ResourceKind.HOST, FileType.TYPE);
        register(ResourceKind.RULE);
        register(ResourceKind.CONTROL);
        register(ResourceKind.PROLOG);
        register(ResourceKind.GROOVY);
        register(ResourceKind.CONFIG);
    }

    @Override
    public Set<FileType> getFileTypes(Exportable exportable) {
        Set<FileType> result = EnumSet.noneOf(FileType.class);
        ResourceKind resourceKind = null;
        if (exportable.containsKind(Kind.GRAPH)) {
            resourceKind = ResourceKind.toResource(exportable.getGraph()
                .getRole());
        } else if (exportable.containsKind(Kind.RESOURCE)) {
            resourceKind = exportable.getModel()
                .getKind();
        }
        if (resourceKind == ResourceKind.HOST) {
            // host graphs can be exported to any known graph resource type
            result.addAll(FileType.GRAPHS.getSubTypes());
        } else if (resourceKind != null) {
            FileType fileType = getFileType(resourceKind);
            if (fileType != null) {
                result.add(fileType);
            }
        }
        return Collections.unmodifiableSet(result);
    }

    @Override
    public Set<Resource> doImport(File file, FileType fileType, GrammarModel grammar)
        throws PortException {
        Resource result;
        try {
            QualName name = QualName.name(fileType.stripExtension(file.getName()));
            ResourceKind kind = getResourceKind(fileType);
            if (kind.isGraphBased()) {
                // read graph from file
                AttrGraph xmlGraph = GxlIO.instance()
                    .loadGraph(file);
                xmlGraph.setRole(kind.getGraphRole());
                xmlGraph.setName(name.toString());
                result = new Resource(kind, xmlGraph.toAspectGraph());
            } else {
                String program = groove.io.Util.readFileToString(file);
                result = new Resource(kind, name, program);
            }
        } catch (IOException e) {
            throw new PortException(e);
        }
        return Collections.singleton(result);
    }

    @Override
    public Set<Resource> doImport(QualName name, InputStream stream, FileType fileType,
        GrammarModel grammar) throws PortException {
        ResourceKind kind = getResourceKind(fileType);
        if (kind.isGraphBased()) {
            throw new PortException("Cannot import from stream");
        }

        Resource result;
        try {
            String resource = groove.io.Util.readInputStreamToString(stream);
            result = new Resource(kind, name, resource);
        } catch (IOException e) {
            throw new PortException(e);
        }
        return Collections.singleton(result);
    }

    @Override
    public void doExport(Exportable exportable, File file, FileType fileType) throws PortException {
        ResourceKind kind = exportable.getKind();
        if (kind.isGraphBased()) {
            AspectGraph graph = GraphConverter.toAspect(exportable.getGraph());
            if (kind == ResourceKind.HOST && fileType != FileType.STATE) {
                // we are converting a host graph to a rule or type graph
                // so unwrap any literal labels
                graph = graph.unwrap();
            }
            try {
                GxlIO.instance()
                    .saveGraph(graph.toPlainGraph(), file);
            } catch (IOException e) {
                throw new PortException(e);
            }
        } else {
            TextBasedModel<?> textModel = (TextBasedModel<?>) exportable.getModel();
            try (Writer writer = new FileWriter(file)) {
                writer.write(textModel.getSource());
            } catch (IOException e) {
                throw new PortException(e);
            }
        }
    }

    /** Returns the singleton instance of this class. */
    public static final NativePorter getInstance() {
        return instance;
    }

    private static final NativePorter instance = new NativePorter();
}

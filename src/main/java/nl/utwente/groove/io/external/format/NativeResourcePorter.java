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
import java.util.Set;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.GraphConverter;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.grammar.model.TextBasedModel;
import nl.utwente.groove.io.FileType;
import nl.utwente.groove.io.external.Exportable;
import nl.utwente.groove.io.external.Imported;
import nl.utwente.groove.io.external.PortException;
import nl.utwente.groove.io.graph.AttrGraph;
import nl.utwente.groove.io.graph.GxlIO;

/**
 * Import and export resources native to GROOVE, such as type and host graphs, and control programs
 * @author Harold Bruijntjes
 * @version $Revision$
 */
public class NativeResourcePorter extends AbstractResourcePorter {
    private NativeResourcePorter() {
        register(ResourceKind.TYPE);
        register(ResourceKind.HOST);
        register(ResourceKind.RULE);
        register(ResourceKind.CONTROL);
        register(ResourceKind.PROLOG);
        register(ResourceKind.GROOVY);
        register(ResourceKind.CONFIG);
    }

    /** Registers a resource kind with its default file type. */
    private void register(ResourceKind kind) {
        register(kind, kind.getFileType());
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
        if (resourceKind == null) {
            throw new PortException(String
                .format("'%s' is not a grammar resource and hence cannot be exported as %s",
                        exportable.qualName(), fileType.getExtension()));
        } else if (resourceKind.isGraphBased()) {
            AspectGraph graph = GraphConverter.toAspect(exportable.graph());
            if (resourceKind == ResourceKind.HOST && fileType != FileType.STATE) {
                // we are converting a host graph to a rule or type graph
                // so unwrap any literal labels
                graph = graph.unwrap();
            }
            try {
                GxlIO.instance().saveGraph(graph.toPlainGraph(), file);
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

    /** Returns the singleton instance of this class. */
    public static final NativeResourcePorter getInstance() {
        return instance;
    }

    private static final NativeResourcePorter instance = new NativeResourcePorter();
}

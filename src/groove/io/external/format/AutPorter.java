/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

import groove.grammar.QualName;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.ResourceKind;
import groove.graph.Graph;
import groove.graph.GraphRole;
import groove.graph.plain.PlainGraph;
import groove.io.FileType;
import groove.io.external.AbstractExporter;
import groove.io.external.Exportable;
import groove.io.external.Importer;
import groove.io.external.PortException;
import groove.io.graph.AutIO;

/**
 * Class that implements load/save of graphs in the CADP .aut format.
 * @author Eduardo Zambon
 */
public final class AutPorter extends AbstractExporter implements Importer {
    private AutPorter() {
        super(Kind.GRAPH);
        register(FileType.AUT);
    }

    @Override
    public Set<Resource> doImport(File file, FileType fileType, GrammarModel grammar)
        throws PortException {
        Set<Resource> resources;
        try (FileInputStream stream = new FileInputStream(file)) {
            QualName name = QualName.name(fileType.stripExtension(file.getName()));
            resources = doImport(name, stream, fileType, grammar);
        } catch (IOException e) {
            throw new PortException(e);
        }
        return resources;
    }

    @Override
    public Set<Resource> doImport(QualName name, InputStream stream, FileType fileType,
        GrammarModel grammar) throws PortException {
        try {
            this.io.setGraphName(name.toString());
            this.io.setGraphRole(GraphRole.HOST);
            PlainGraph graph = this.io.loadGraph(stream);
            AspectGraph agraph = AspectGraph.newInstance(graph);
            return Collections.singleton(new Resource(ResourceKind.HOST, agraph));
        } catch (Exception e) {
            throw new PortException(
                String.format("Format error while reading %s: %s", name, e.getMessage()));
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                throw new PortException(e);
            }
        }
    }

    @Override
    public void doExport(Exportable exportable, File file, FileType fileType) throws PortException {
        Graph graph = exportable.getGraph();
        try {
            this.io.saveGraph(graph, file);
        } catch (IOException e) {
            throw new PortException(e);
        }
    }

    private final AutIO io = new AutIO();

    /** Returns the singleton instance of this class. */
    public static final AutPorter instance() {
        return instance;
    }

    private static final AutPorter instance = new AutPorter();

}

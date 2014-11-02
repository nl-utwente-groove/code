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

import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.Resource;
import groove.graph.Graph;
import groove.graph.GraphRole;
import groove.graph.plain.PlainGraph;
import groove.io.FileType;
import groove.io.external.AbstractExporter;
import groove.io.external.Exportable;
import groove.io.external.Importer;
import groove.io.external.PortException;
import groove.io.graph.AutIO;
import groove.util.parse.FormatException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

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
    public Set<Resource> doImport(Path file, FileType fileType, GrammarModel grammar)
        throws PortException {
        Set<Resource> resources;
        String name = FileType.getPureName(file);
        try {
            this.io.setGraphName(name);
            this.io.setGraphRole(GraphRole.HOST);
            PlainGraph graph = this.io.loadGraph(file);
            AspectGraph agraph = AspectGraph.newInstance(graph);
            resources = Collections.singleton(agraph);
        } catch (IOException | FormatException e) {
            throw new PortException(String.format("Format error while reading %s: %s",
                name,
                e.getMessage()));
        }
        return resources;
    }

    @Override
    public void doExport(Exportable exportable, Path file, FileType fileType) throws PortException {
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

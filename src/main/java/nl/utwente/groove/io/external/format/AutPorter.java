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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.plain.PlainGraph;
import nl.utwente.groove.io.FileType;
import nl.utwente.groove.io.external.AbstractExporter;
import nl.utwente.groove.io.external.Exportable;
import nl.utwente.groove.io.external.Imported;
import nl.utwente.groove.io.external.Importer;
import nl.utwente.groove.io.external.PortException;
import nl.utwente.groove.io.graph.AutIO;

/**
 * Class that implements load/save of graphs in the CADP .aut format.
 * @author Eduardo Zambon
 */
public final class AutPorter extends AbstractExporter implements Importer {
    private AutPorter() {
        super(Exportable.Kind.GRAPH);
        register(FileType.AUT);
    }

    @Override
    public Set<Imported> doImport(File file, FileType fileType,
                                  GrammarModel grammar) throws PortException {
        Set<Imported> resources;
        try (FileInputStream stream = new FileInputStream(file)) {
            QualName name = QualName.name(fileType.stripExtension(file.getName()));
            resources = doImport(name, stream, fileType, grammar);
        } catch (IOException e) {
            throw new PortException(e);
        }
        return resources;
    }

    @Override
    public Set<Imported> doImport(QualName name, InputStream stream, FileType fileType,
                                  GrammarModel grammar) throws PortException {
        try {
            this.io.setGraphName(name.toString());
            this.io.setGraphRole(GraphRole.HOST);
            PlainGraph graph = this.io.loadGraph(stream);
            AspectGraph agraph = AspectGraph.newInstance(graph);
            return Collections.singleton(new Imported(ResourceKind.HOST, agraph));
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
        try {
            this.io.saveGraph(exportable.graph(), file);
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

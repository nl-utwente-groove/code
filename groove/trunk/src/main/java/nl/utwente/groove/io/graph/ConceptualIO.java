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
package nl.utwente.groove.io.graph;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.io.FileType;
import nl.utwente.groove.io.external.ConceptualPorter;
import nl.utwente.groove.io.external.Exportable;
import nl.utwente.groove.io.external.Importer.Resource;
import nl.utwente.groove.io.external.PortException;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Reader/writer for type graphs in some conceptual format.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ConceptualIO extends GraphIO<AspectGraph> {
    /**
     * Constructs a reader/writer from a porter of the conceptual format.
     * @param porter the embedded conceptual porter
     * @param fileType the associated file type
     * @param role graph role; either {@link GraphRole#TYPE} for meta-
     * models or {@link GraphRole#HOST} for models
     */
    public ConceptualIO(ConceptualPorter porter, FileType fileType, GraphRole role) {
        this.porter = porter;
        this.fileType = fileType;
        this.role = role;
    }

    @Override
    public boolean canSave() {
        return true;
    }

    @Override
    protected void doSaveGraph(Graph graph, File file) throws IOException {
        try {
            this.porter.doExport(new Exportable(graph), file, this.fileType);
        } catch (PortException e) {
            if (e.getCause() instanceof IOException exc) {
                throw exc;
            } else {
                throw new IOException(e);
            }
        }
    }

    @Override
    public boolean canLoad() {
        return true;
    }

    @Override
    public AspectGraph loadGraph(InputStream in) throws FormatException, IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public AspectGraph loadGraph(File in) throws FormatException, IOException {
        AspectGraph result = null;
        try {
            Set<Resource> resources = this.porter.doImport(in, this.fileType, getGrammar(in));
            for (Resource resource : resources) {
                result = resource.getGraphResource();
                if (result.getRole() == this.role) {
                    break;
                }
            }
        } catch (PortException e) {
            if (e.getCause() instanceof IOException exc) {
                throw exc;
            } else {
                throw new IOException(e);
            }
        }
        return result;
    }

    /**
     * Tries to find the enclosing grammar of the graph file
     */
    private GrammarModel getGrammar(File file) throws IOException {
        File dir = file.getCanonicalFile().getParentFile();
        while (dir != null && !FileType.GRAMMAR.hasExtension(dir)) {
            dir = dir.getParentFile();
        }
        GrammarModel grammar = null;
        if (dir != null) {
            grammar = Groove.loadGrammar(dir.getPath());
        }
        return grammar;
    }

    private final ConceptualPorter porter;
    private final FileType fileType;
    private final GraphRole role;
}

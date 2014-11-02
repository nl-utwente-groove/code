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

import groove.grammar.aspect.AspectGraph;
import groove.grammar.aspect.GraphConverter;
import groove.grammar.host.HostGraph;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.Resource;
import groove.gui.Simulator;
import groove.io.FileType;
import groove.io.external.Importer;
import groove.io.external.PortException;
import groove.io.graph.ColIO;

import java.awt.Frame;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Class that implements loading of graphs in the DIMACS .col graph format.
 * Saving in this format is unsupported.
 *
 * The format is described in
 * <a href="http://mat.gsia.cmu.edu/COLOR/general/ccformat.ps">
 * http://mat.gsia.cmu.edu/COLOR/general/ccformat.ps</a>.
 * See <a href="http://mat.gsia.cmu.edu/COLOR/instances.html">
 * http://mat.gsia.cmu.edu/COLOR/instances.html</a>
 * for example graphs in this format.
 *
 * @author Arend Rensink
 */
public class ColImporter implements Importer {
    private ColImporter() {
        this.fileTypes = EnumSet.of(FileType.COL);
    }

    @Override
    public Set<FileType> getSupportedFileTypes() {
        return this.fileTypes;
    }

    private final Set<FileType> fileTypes;

    @Override
    public Set<Kind> getFormatKinds() {
        return EnumSet.of(Kind.RESOURCE);
    }

    // Methods from FileFormat.

    @Override
    public Set<Resource> doImport(Path file, FileType fileType, GrammarModel grammar)
        throws PortException {
        Set<Resource> resources;
        try (InputStream stream = Files.newInputStream(file)) {
            String name = file.getFileName().toString();
            this.io.setGraphName(name);
            HostGraph graph = this.io.loadGraph(stream);
            AspectGraph aGraph = GraphConverter.toAspect(graph);
            resources = Collections.singleton(aGraph);
        } catch (IOException e) {
            throw new PortException(e);
        }
        return resources;
    }

    /** Returns the parent component for a dialog. */
    protected Frame getParent() {
        return this.parent;
    }

    @Override
    public void setSimulator(Simulator simulator) {
        this.parent = simulator.getFrame();
    }

    private Frame parent;
    /** Reader for the .col format */
    private final ColIO io = new ColIO();

    /** Returns the singleton instance of this class. */
    public static final ColImporter getInstance() {
        return instance;
    }

    private static final ColImporter instance = new ColImporter();
}

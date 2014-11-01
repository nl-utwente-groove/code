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
import groove.io.FileType;
import groove.io.external.AbstractExporter;
import groove.io.external.Exportable;
import groove.io.external.PortException;
import groove.io.external.util.GraphToKth;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Class that implements saving graphs in the KTH file format,
 * used by Marieke et al.
 * Loading in this format is unsupported.
 *
 * @author Eduardo Zambon
 */
public final class KthExporter extends AbstractExporter {
    private KthExporter() {
        super(Kind.GRAPH);
        register(FileType.KTH);
    }

    @Override
    public void doExport(Exportable exportable, Path file, FileType fileType) throws PortException {
        AspectGraph graph = (AspectGraph) exportable.getGraph();
        try {
            Files.write(file, GraphToKth.convertGraph(graph));
        } catch (IOException e) {
            throw new PortException(e);
        }
    }

    /** Returns the singleton instance of this class. */
    public static final KthExporter getInstance() {
        return instance;
    }

    private static final KthExporter instance = new KthExporter();
}

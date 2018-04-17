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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import groove.gui.jgraph.JGraph;
import groove.io.FileType;
import groove.io.external.AbstractExporter;
import groove.io.external.Exportable;
import groove.io.external.PortException;
import groove.io.external.util.GraphToTikz;

/**
 * Class that implements saving graphs in the Tikz format.
 * Loading in this format is unsupported.
 *
 * @author Eduardo Zambon
 */
public final class TikzExporter extends AbstractExporter {
    private static final TikzExporter instance = new TikzExporter();

    /** Returns the singleton instance of this class. */
    public static final TikzExporter getInstance() {
        return instance;
    }

    private TikzExporter() {
        super(Kind.JGRAPH);
        register(FileType.TIKZ);
    }

    @Override
    public void doExport(Exportable exportable, File file, FileType fileType) throws PortException {
        JGraph<?> jGraph = exportable.getJGraph();
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            GraphToTikz.export(jGraph, writer);
        } catch (IOException e) {
            throw new PortException(e);
        }
    }
}

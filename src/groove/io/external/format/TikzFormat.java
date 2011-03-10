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

import groove.graph.Graph;
import groove.gui.jgraph.GraphJGraph;
import groove.io.FileType;
import groove.io.external.export.GraphToTikz;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/** 
 * Class that implements saving graphs in the Tikz format.
 * Loading in this format is unsupported.
 * 
 * @author Eduardo Zambon 
 */
public class TikzFormat extends AbsExternalFileFormat<Graph<?,?>> {

    private static final TikzFormat INSTANCE = new TikzFormat();

    /** Returns the singleton instance of this class. */
    public static final TikzFormat getInstance() {
        return INSTANCE;
    }

    private TikzFormat() {
        super(FileType.TIKZ);
    }

    // Methods from FileFormat.

    @Override
    public void load(Graph<?,?> graph, File file) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(GraphJGraph jGraph, File file) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(file));
        GraphToTikz.export(jGraph, writer);
        writer.close();
    }

    @Override
    public void save(Graph<?,?> graph, File file) throws IOException {
        throw new UnsupportedOperationException();
    }

    // Methods from Xml

    @Override
    public Graph<?,?> createGraph(String graphName) {
        throw new UnsupportedOperationException();
    }

}

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
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.GraphJGraph;
import groove.io.FileType;
import groove.io.external.util.GraphToKth;
import groove.view.aspect.AspectGraph;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/** 
 * Class that implements saving graphs in the KTH file format,
 * used by Marieke et al. 
 * Loading in this format is unsupported.
 * 
 * @author Eduardo Zambon 
 */
public class KthFormat extends AbstractExternalFileFormat<Graph<?,?>> {

    private static final KthFormat INSTANCE = new KthFormat();

    /** Returns the singleton instance of this class. */
    public static final KthFormat getInstance() {
        return INSTANCE;
    }

    private KthFormat() {
        super(FileType.KTH);
    }

    // Methods from FileFormat.

    @Override
    public void load(Graph<?,?> graph, File file) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(GraphJGraph jGraph, File file) throws IOException {
        if (jGraph instanceof AspectJGraph) {
            Graph<?,?> graph = ((AspectJGraph) jGraph).getModel().getGraph();
            this.save(graph, file);
        } else {
            throw new IOException(
                "This exporter can only be used with state graphs");
        }
    }

    @Override
    public void save(Graph<?,?> graph, File file) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(file));
        GraphToKth.export((AspectGraph) graph, writer);
        writer.close();
    }

    // Methods from Xml

    @Override
    public Graph<?,?> createGraph(String graphName) {
        throw new UnsupportedOperationException();
    }

}

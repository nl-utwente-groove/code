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

import groove.algebra.Algebra;
import groove.algebra.AlgebraFamily;
import groove.graph.EdgeRole;
import groove.graph.TypeLabel;
import groove.graph.algebra.ValueNode;
import groove.gui.jgraph.GraphJGraph;
import groove.io.FileType;
import groove.trans.DefaultHostGraph;
import groove.trans.HostGraph;
import groove.trans.HostNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

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
public class ColFormat extends AbstractExternalFileFormat<HostGraph> {

    private static final ColFormat INSTANCE = new ColFormat();

    /** Returns the singleton instance of this class. */
    public static final ColFormat getInstance() {
        return INSTANCE;
    }

    private ColFormat() {
        super(FileType.COL);
    }

    // Methods from FileFormat.

    private static final TypeLabel LABEL = TypeLabel.createBinaryLabel("n");

    @Override
    public void load(HostGraph graph, File file) throws IOException {
        FileInputStream fis = null;
        BufferedReader reader = null;
        try {
            fis = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(fis));
            this.load(graph, reader);
        } catch (FileNotFoundException e) {
            throw new IOException(String.format("File %s not found.", file));
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }

    private void load(HostGraph destGraph, BufferedReader reader)
        throws IOException {
        DefaultHostGraph graph = (DefaultHostGraph) destGraph;
        Algebra<?> intAlgebra = AlgebraFamily.getInstance().getAlgebraFor("0");
        TypeLabel valueLabel = TypeLabel.createBinaryLabel("value");
        for (String nextLine = reader.readLine(); nextLine != null; nextLine =
            reader.readLine()) {
            String[] fragments = nextLine.split(" ");
            if (fragments[0].equals("n")) {
                HostNode node = this.addNode(graph, fragments[1]);
                ValueNode valueNode =
                    graph.addNode(intAlgebra, intAlgebra.getValue(fragments[2]));
                graph.addEdge(node, valueLabel, valueNode);
            } else if (fragments[0].equals("e")) {
                HostNode source = this.addNode(graph, fragments[1]);
                HostNode target = this.addNode(graph, fragments[2]);
                graph.addEdge(source, LABEL, target);
            }
        }
    }

    private HostNode addNode(HostGraph result, String id) {
        HostNode node = result.addNode(Integer.parseInt(id));
        result.addEdge(node, TypeLabel.createLabel(EdgeRole.FLAG, "i" + id),
            node);
        return node;
    }

    @Override
    public void save(HostGraph graph, File file) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(GraphJGraph jGraph, File file) throws IOException {
        throw new UnsupportedOperationException();
    }

    // Methods from Xml

    @Override
    public HostGraph createGraph(String graphName) {
        return new DefaultHostGraph(graphName);
    }

}

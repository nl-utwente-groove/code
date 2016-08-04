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
package groove.io.graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import groove.algebra.Algebra;
import groove.algebra.AlgebraFamily;
import groove.algebra.Sort;
import groove.grammar.host.DefaultHostGraph;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.grammar.type.TypeLabel;
import groove.graph.EdgeRole;
import groove.graph.Graph;

/**
 * Reader for graphs in the DIMACS .col graph format.
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
 * @version $Revision $
 */
public class ColIO extends GraphIO<HostGraph> {
    @Override
    public boolean canSave() {
        return false;
    }

    @Override
    protected void doSaveGraph(Graph graph, File file) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canLoad() {
        return true;
    }

    @Override
    public HostGraph loadGraph(InputStream in) throws IOException {
        DefaultHostGraph result = new DefaultHostGraph(getGraphName());
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            Algebra<?> intAlgebra = AlgebraFamily.getInstance()
                .getAlgebra(Sort.INT);
            TypeLabel valueLabel = TypeLabel.createBinaryLabel("value");
            for (String nextLine = reader.readLine(); nextLine != null; nextLine =
                reader.readLine()) {
                String[] fragments = nextLine.split(" ");
                if (fragments[0].equals("n")) {
                    HostNode node = addNode(result, fragments[1]);
                    HostNode valueNode =
                        result.addNode(intAlgebra, intAlgebra.toValueFromJava(fragments[2]));
                    result.addEdge(node, valueLabel, valueNode);
                } else if (fragments[0].equals("e")) {
                    HostNode source = addNode(result, fragments[1]);
                    HostNode target = addNode(result, fragments[2]);
                    result.addEdge(source, LABEL, target);
                }
            }
        }
        return result;
    }

    private HostNode addNode(HostGraph result, String id) {
        HostNode node = result.getFactory()
            .createNode(Integer.parseInt(id));
        result.addEdge(node, TypeLabel.createLabel(EdgeRole.FLAG, "i" + id), node);
        return node;
    }

    private static final TypeLabel LABEL = TypeLabel.createBinaryLabel("n");
}

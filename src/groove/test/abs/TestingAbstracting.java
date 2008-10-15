/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: TestingAbstracting.java,v 1.1 2007-11-28 15:35:07 iovka Exp $
 */
package groove.test.abs;

import groove.abs.DefaultAbstrGraph;
import groove.abs.ExceptionIncompatibleWithMaxIncidence;
import groove.abs.PatternFamily;
import groove.graph.DefaultEdge;
import groove.graph.DefaultGraph;
import groove.graph.DefaultLabel;
import groove.graph.DefaultNode;
import groove.graph.Graph;
import groove.graph.Node;
import junit.framework.TestCase;

/**
 * Testing the abstraction.
 * 
 * @author Iovka Boneva
 * @version $Revision $
 */
public class TestingAbstracting extends TestCase {

    /** */
    protected DefaultLabel n_label = DefaultLabel.createLabel("n");
    /** */
    protected DefaultLabel c_label = DefaultLabel.createLabel("c");
    /** */
    protected Node[] nodes = new Node[10];

    private boolean init = false;

    /** */
    @SuppressWarnings("unqualified-field-access")
    @Override
    public void setUp() {
        if (this.init) {
            return;
        }
        for (int i = 0; i < 10; i++) {
            this.nodes[i] = DefaultNode.createNode();
        }
        this.init = true;
    }

    /**
     * Abstracting a list.
     * @throws ExceptionIncompatibleWithMaxIncidence
     */
    @SuppressWarnings("unqualified-field-access")
    public void testAbstrList() throws ExceptionIncompatibleWithMaxIncidence,
        AssertionError {
        Graph list4 = new DefaultGraph();
        for (int i = 0; i < 3; i++) {
            list4.addEdge(DefaultEdge.createEdge(this.nodes[i], this.n_label,
                this.nodes[i + 1]));
        }
        for (int i = 0; i < 4; i++) {
            list4.addEdge(DefaultEdge.createEdge(this.nodes[i], this.c_label,
                this.nodes[i]));
        }

        PatternFamily pf = new PatternFamily(1, 10);
        DefaultAbstrGraph s =
            DefaultAbstrGraph.factory(pf, 1).getShapeGraphFor(list4);

        assertEquals(3, s.nodeCount());
        assertEquals(6, s.edgeCount());
    }

}

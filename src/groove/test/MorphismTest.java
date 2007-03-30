// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/* 
 * $Id: MorphismTest.java,v 1.2 2007-03-30 15:50:40 rensink Exp $
 */
package groove.test;

import groove.graph.DefaultEdge;
import groove.graph.DefaultGraph;
import groove.graph.DefaultInjectiveMorphism;
import groove.graph.DefaultMorphism;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.InjectiveMorphism;
import groove.graph.Morphism;
import groove.graph.Node;
import junit.framework.TestCase;

/**
 * @version $Revision: 1.2 $ $Date: 2007-03-30 15:50:40 $
 */
public class MorphismTest extends TestCase {
    public MorphismTest(String name) {
        super(name);
    }

    protected static final int NR_GRAPHS = 3;
    protected static final int[] NR_NODES = {4,4,4};
    protected static final int[] NR_EDGES = {4,3,5};

    protected Node[][] n;
    protected Edge[][] e;

    protected DefaultGraph[] g;
    protected InjectiveMorphism m2To1InTot, m1To0InTot, m1To2InPart;
    protected Morphism m2To0NonInPart;

    /** The setup is depicted in test-graphs.fig */
    @Override
    protected void setUp() {
        g = new DefaultGraph[NR_GRAPHS];
        n = new Node[NR_GRAPHS][];
        e = new Edge[NR_GRAPHS][];

        for (int i = 0; i < NR_GRAPHS; i++) {
            g[i] = new DefaultGraph();
            n[i] = new Node[NR_NODES[i]];
            e[i] = new Edge[NR_EDGES[i]];

            for (int j = 0; j < NR_NODES[i]; j++) {
                n[i][j] = new DefaultNode();
                g[i].addNode(n[i][j]);
            }
        }

        g[1].removeNode(n[1][2]);
        g[2].removeNode(n[1][0]);
        g[2].removeNode(n[1][1]);

        e[0][0] = DefaultEdge.createEdge(n[0][0], "a", n[0][1]);
        e[0][1] = DefaultEdge.createEdge(n[0][1], "b", n[0][1]);
        e[0][2] = DefaultEdge.createEdge(n[0][2], "c", n[0][3]);
        e[0][3] = DefaultEdge.createEdge(n[0][3], "a", n[0][2]);

        e[1][0] = DefaultEdge.createEdge(n[1][0], "b", n[1][1]);
        e[1][1] = DefaultEdge.createEdge(n[1][2], "a", n[1][3]);
        e[1][2] = DefaultEdge.createEdge(n[1][3], "b", n[1][3]);

        e[2][0] = DefaultEdge.createEdge(n[2][0], "a", n[2][1]);
        e[2][1] = DefaultEdge.createEdge(n[2][1], "b", n[2][1]);
        e[2][2] = DefaultEdge.createEdge(n[2][2], "b", n[2][3]);
        e[2][3] = DefaultEdge.createEdge(n[2][0], "b", n[2][2]);
        e[2][4] = DefaultEdge.createEdge(n[2][1], "c", n[2][3]);

        for (int i = 0; i < NR_GRAPHS; i++) {
            for (int j = 0; j < NR_EDGES[i]; j++) 
                g[i].addEdge(e[i][j]);
        }

        m2To1InTot = new DefaultInjectiveMorphism(g[2], g[1]);
        m2To1InTot.putNode(n[2][0], n[1][2]);
        m2To1InTot.putNode(n[2][1], n[1][3]);
        m2To1InTot.putNode(n[2][2], n[1][0]);
        m2To1InTot.putNode(n[2][3], n[1][1]);
        m2To1InTot.putEdge(e[2][1], e[1][2]);

        m1To0InTot = new DefaultInjectiveMorphism(g[1], g[0]);
        m1To0InTot.putNode(n[1][2], n[0][3]);
        m1To0InTot.putNode(n[1][3], n[0][2]);
        m1To0InTot.putNode(n[1][0], n[0][0]);
        m1To0InTot.putNode(n[1][1], n[0][1]);
        m1To0InTot.putEdge(e[1][1], e[0][3]);

        m2To0NonInPart = new DefaultMorphism(g[2], g[0]);
        m2To0NonInPart.putNode(n[2][0], n[0][1]);
        m2To0NonInPart.putNode(n[2][2], n[0][1]);
        m2To0NonInPart.putNode(n[2][3], n[0][2]);
        m2To0NonInPart.putEdge(e[2][3], e[0][1]);

        m1To2InPart = new DefaultInjectiveMorphism(g[1],g[2]);
        m1To2InPart.putNode(n[1][3],n[2][1]);
        m1To2InPart.putNode(n[1][0],n[2][2]);
        m1To2InPart.putEdge(e[1][2],e[2][1]);
    }

    public void testSetup() {
        assertEquals(e[1][2],m2To1InTot.getEdge(e[2][1]));
        assertEquals(null,m2To1InTot.getEdge(e[2][4]));

        assertEquals(null,m1To0InTot.getEdge(e[1][0]));
        assertEquals(e[0][3],m1To0InTot.getEdge(e[1][1]));

        assertEquals(null,m2To0NonInPart.getEdge(e[2][2]));
        assertEquals(e[0][1],m2To0NonInPart.getEdge(e[2][3]));

        assertEquals(null,m1To2InPart.getEdge(e[2][2]));
        assertEquals(e[2][1],m1To2InPart.getEdge(e[1][2]));
    }

    public void testAfter() {
        InjectiveMorphism m2To2InPart = (InjectiveMorphism) m1To2InPart.after(m2To1InTot);
        assertEquals(null,m2To2InPart.getNode(n[2][0]));
        assertEquals(n[2][1],m2To2InPart.getNode(n[2][1]));
        assertEquals(n[2][2],m2To2InPart.getNode(n[2][2]));
        assertEquals(null,m2To2InPart.getNode(n[2][3]));

        assertEquals(m2To2InPart, m2To2InPart.inverse());

        assertEquals(m2To2InPart.inverse(), 
                     m1To2InPart.inverse().then(m2To1InTot.inverse()));

        InjectiveMorphism m2To0InTot = (InjectiveMorphism) m1To0InTot.after(m2To1InTot);
        assertEquals(n[0][3],m2To0InTot.getNode(n[2][0]));
        assertEquals(n[0][2],m2To0InTot.getNode(n[2][1]));
        assertEquals(n[0][0],m2To0InTot.getNode(n[2][2]));
        assertEquals(n[0][1],m2To0InTot.getNode(n[2][3]));

        assertEquals(m2To0InTot.inverse(), 
                     m1To0InTot.inverse().then(m2To1InTot.inverse()));

        Morphism m0To0NonInPart = m2To0NonInPart.after(m2To0InTot.inverse());
        assertEquals(n[0][1],m0To0NonInPart.getNode(n[0][0]));
        assertEquals(n[0][2],m0To0NonInPart.getNode(n[0][1]));
        assertEquals(null,m0To0NonInPart.getNode(n[0][2]));
        assertEquals(n[0][1],m0To0NonInPart.getNode(n[0][3]));
    }
//
//    public void testInverse() {
//        InjectiveMorphism m1To2InTot = m2To1InTot.inverse();
//        for (int i = 0; i < NR_NODES[1]; i++)
//            assertEquals(m1To2InTot.getNode(n[1][i]), m2To1InTot.getInverseElement(n[1][i]));
//
//        InjectiveMorphism m2To1InPart = m1To2InPart.inverse();
//        for (int i = 0; i < NR_NODES[2]; i++)
//            assertEquals(m2To1InPart.getNode(n[2][i]), m1To2InPart.getInverseElement(n[2][i]));
//    }
}
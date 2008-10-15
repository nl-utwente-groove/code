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
 * $Id: MorphismTest.java,v 1.5 2008-01-30 09:33:08 iovka Exp $
 */
package groove.test;

import groove.graph.DefaultEdge;
import groove.graph.DefaultGraph;
import groove.graph.DefaultMorphism;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Morphism;
import groove.graph.Node;
import junit.framework.TestCase;

/**
 * @version $Revision$ $Date: 2008-01-30 09:33:08 $
 */
@SuppressWarnings("all")
public class MorphismTest extends TestCase {
    public MorphismTest(String name) {
        super(name);
    }

    protected static final int NR_GRAPHS = 3;
    protected static final int[] NR_NODES = {4, 4, 4};
    protected static final int[] NR_EDGES = {4, 3, 5};

    protected Node[][] n;
    protected Edge[][] e;

    protected DefaultGraph[] g;
    protected Morphism m2To1InTot, m1To0InTot, m1To2InPart;
    protected Morphism m2To0NonInPart;

    /** The setup is depicted in test-graphs.fig */
    @Override
    protected void setUp() {
        this.g = new DefaultGraph[NR_GRAPHS];
        this.n = new Node[NR_GRAPHS][];
        this.e = new Edge[NR_GRAPHS][];

        for (int i = 0; i < NR_GRAPHS; i++) {
            this.g[i] = new DefaultGraph();
            this.n[i] = new Node[NR_NODES[i]];
            this.e[i] = new Edge[NR_EDGES[i]];

            for (int j = 0; j < NR_NODES[i]; j++) {
                this.n[i][j] = DefaultNode.createNode();
                this.g[i].addNode(this.n[i][j]);
            }
        }

        this.g[1].removeNode(this.n[1][2]);
        this.g[2].removeNode(this.n[1][0]);
        this.g[2].removeNode(this.n[1][1]);

        this.e[0][0] = DefaultEdge.createEdge(this.n[0][0], "a", this.n[0][1]);
        this.e[0][1] = DefaultEdge.createEdge(this.n[0][1], "b", this.n[0][1]);
        this.e[0][2] = DefaultEdge.createEdge(this.n[0][2], "c", this.n[0][3]);
        this.e[0][3] = DefaultEdge.createEdge(this.n[0][3], "a", this.n[0][2]);

        this.e[1][0] = DefaultEdge.createEdge(this.n[1][0], "b", this.n[1][1]);
        this.e[1][1] = DefaultEdge.createEdge(this.n[1][2], "a", this.n[1][3]);
        this.e[1][2] = DefaultEdge.createEdge(this.n[1][3], "b", this.n[1][3]);

        this.e[2][0] = DefaultEdge.createEdge(this.n[2][0], "a", this.n[2][1]);
        this.e[2][1] = DefaultEdge.createEdge(this.n[2][1], "b", this.n[2][1]);
        this.e[2][2] = DefaultEdge.createEdge(this.n[2][2], "b", this.n[2][3]);
        this.e[2][3] = DefaultEdge.createEdge(this.n[2][0], "b", this.n[2][2]);
        this.e[2][4] = DefaultEdge.createEdge(this.n[2][1], "c", this.n[2][3]);

        for (int i = 0; i < NR_GRAPHS; i++) {
            for (int j = 0; j < NR_EDGES[i]; j++) {
                this.g[i].addEdge(this.e[i][j]);
            }
        }

        this.m2To1InTot = new DefaultMorphism(this.g[2], this.g[1]);
        this.m2To1InTot.putNode(this.n[2][0], this.n[1][2]);
        this.m2To1InTot.putNode(this.n[2][1], this.n[1][3]);
        this.m2To1InTot.putNode(this.n[2][2], this.n[1][0]);
        this.m2To1InTot.putNode(this.n[2][3], this.n[1][1]);
        this.m2To1InTot.putEdge(this.e[2][1], this.e[1][2]);

        this.m1To0InTot = new DefaultMorphism(this.g[1], this.g[0]);
        this.m1To0InTot.putNode(this.n[1][2], this.n[0][3]);
        this.m1To0InTot.putNode(this.n[1][3], this.n[0][2]);
        this.m1To0InTot.putNode(this.n[1][0], this.n[0][0]);
        this.m1To0InTot.putNode(this.n[1][1], this.n[0][1]);
        this.m1To0InTot.putEdge(this.e[1][1], this.e[0][3]);

        this.m2To0NonInPart = new DefaultMorphism(this.g[2], this.g[0]);
        this.m2To0NonInPart.putNode(this.n[2][0], this.n[0][1]);
        this.m2To0NonInPart.putNode(this.n[2][2], this.n[0][1]);
        this.m2To0NonInPart.putNode(this.n[2][3], this.n[0][2]);
        this.m2To0NonInPart.putEdge(this.e[2][3], this.e[0][1]);

        this.m1To2InPart = new DefaultMorphism(this.g[1], this.g[2]);
        this.m1To2InPart.putNode(this.n[1][3], this.n[2][1]);
        this.m1To2InPart.putNode(this.n[1][0], this.n[2][2]);
        this.m1To2InPart.putEdge(this.e[1][2], this.e[2][1]);
    }

    public void testSetup() {
        assertEquals(this.e[1][2], this.m2To1InTot.getEdge(this.e[2][1]));
        assertEquals(null, this.m2To1InTot.getEdge(this.e[2][4]));

        assertEquals(null, this.m1To0InTot.getEdge(this.e[1][0]));
        assertEquals(this.e[0][3], this.m1To0InTot.getEdge(this.e[1][1]));

        assertEquals(null, this.m2To0NonInPart.getEdge(this.e[2][2]));
        assertEquals(this.e[0][1], this.m2To0NonInPart.getEdge(this.e[2][3]));

        assertEquals(null, this.m1To2InPart.getEdge(this.e[2][2]));
        assertEquals(this.e[2][1], this.m1To2InPart.getEdge(this.e[1][2]));
    }

    public void testAfter() {
        Morphism m2To2InPart = this.m1To2InPart.after(this.m2To1InTot);
        assertEquals(null, m2To2InPart.getNode(this.n[2][0]));
        assertEquals(this.n[2][1], m2To2InPart.getNode(this.n[2][1]));
        assertEquals(this.n[2][2], m2To2InPart.getNode(this.n[2][2]));
        assertEquals(null, m2To2InPart.getNode(this.n[2][3]));

        Morphism m2To0InTot = this.m1To0InTot.after(this.m2To1InTot);
        assertEquals(this.n[0][3], m2To0InTot.getNode(this.n[2][0]));
        assertEquals(this.n[0][2], m2To0InTot.getNode(this.n[2][1]));
        assertEquals(this.n[0][0], m2To0InTot.getNode(this.n[2][2]));
        assertEquals(this.n[0][1], m2To0InTot.getNode(this.n[2][3]));
    }
    //
    // public void testInverse() {
    // InjectiveMorphism m1To2InTot = m2To1InTot.inverse();
    // for (int i = 0; i < NR_NODES[1]; i++)
    // assertEquals(m1To2InTot.getNode(n[1][i]),
    // m2To1InTot.getInverseElement(n[1][i]));
    //
    // InjectiveMorphism m2To1InPart = m1To2InPart.inverse();
    // for (int i = 0; i < NR_NODES[2]; i++)
    // assertEquals(m2To1InPart.getNode(n[2][i]),
    // m1To2InPart.getInverseElement(n[2][i]));
    // }
}
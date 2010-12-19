/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.abstraction;

import groove.graph.DefaultEdge;
import groove.graph.Node.Factory;
import groove.graph.NodeStore;
import groove.graph.TypeLabel;
import groove.trans.HostFactory;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.RuleToHostMap;
import groove.util.TreeHashSet;

/** Factory class for graph elements. */
public class ShapeFactory extends HostFactory {
    /** Private constructor. */
    private ShapeFactory() {
        // empty
    }

    @Override
    public ShapeNode createNode() {
        return this.nodeStore.createNode();
    }

    @Override
    public ShapeNode createNode(int nr) {
        return this.nodeStore.createNode(nr);
    }

    @Override
    public ShapeEdge createEdge(HostNode source, String text, HostNode target) {
        return createEdge(source, createLabel(text), target);
    }

    @Override
    public ShapeEdge createEdge(HostNode source, TypeLabel label,
            HostNode target) {
        assert source instanceof ShapeNode : "Source node should be ShapeNode";
        assert target instanceof ShapeNode : "Target node should be ShapeNode";
        assert label != null : "Label should not be null";
        ShapeEdge edge =
            new ShapeEdge((ShapeNode) source, label, (ShapeNode) target,
                getEdgeCount());
        ShapeEdge result = this.edgeSet.put(edge);
        if (result == null) {
            result = edge;
        }
        return result;
    }

    @Override
    public int getMaxNodeNr() {
        return this.nodeStore.size();
    }

    @Override
    public int getNodeCount() {
        return this.nodeStore.getNodeCount();
    }

    @Override
    public int getEdgeCount() {
        return this.edgeSet.size();
    }

    @Override
    public void clear() {
        this.edgeSet.clear();
        this.nodeStore.clear();
    }

    @Override
    public ShapeFactory newFactory(HostGraph graph) {
        ShapeFactory result = new ShapeFactory();
        Shape shape = (Shape) graph;
        for (ShapeNode node : shape.nodeSet()) {
            result.nodeStore.addNode(node);
        }
        result.edgeSet.addAll(shape.edgeSet());
        return result;
    }

    @Override
    public ShapeMorphism createMorphism() {
        return new ShapeMorphism(this);
    }

    @Override
    public RuleToHostMap createRuleToHostMap() {
        return new RuleToShapeMap(this);
    }

    /** Store and factory of canonical shape nodes. */
    private NodeStore<ShapeNode> nodeStore = new NodeStore<ShapeNode>(
        new Factory<ShapeNode>() {
            @Override
            public ShapeNode newNode(int nr) {
                return NODE_PROTOTYPE.newNode(nr);
            }
        });

    // ------------------------------------------------------------------------
    // Static Fields
    // ------------------------------------------------------------------------

    /**
     * A identity map, mapping previously created instances of
     * {@link DefaultEdge} to themselves. Used to ensure that edge objects are
     * reused.
     */
    private final TreeHashSet<ShapeEdge> edgeSet =
        new TreeHashSet<ShapeEdge>() {
            /**
             * As {@link DefaultEdge}s test equality by object identity,
             * we need to weaken the set's equality test.
             */
            @Override
            final protected boolean areEqual(ShapeEdge o1, ShapeEdge o2) {
                return o1.source().equals(o2.source())
                    && o1.target().equals(o2.target())
                    && o1.label().equals(o2.label());
            }

            @Override
            final protected boolean allEqual() {
                return false;
            }
        };

    /** Returns the singleton instance of this factory. */
    public static ShapeFactory instance() {
        // initialise lazily to avoid initialisation circularities
        if (instance == null) {
            instance = new ShapeFactory();
        }
        return instance;
    }

    /** Used only as a reference for the constructor. */
    private static final ShapeNode NODE_PROTOTYPE = new ShapeNode(0);
    /** Singleton instance of this factory. */
    private static ShapeFactory instance;
}

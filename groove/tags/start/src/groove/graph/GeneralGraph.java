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
 * $Id: GeneralGraph.java,v 1.1.1.2 2007-03-20 10:42:42 kastenberg Exp $
 */
package groove.graph;

import groove.util.SetView;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This graph changes the storage structure of DefaultGraph,
 * in an attempt to improve performance.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public class GeneralGraph extends AbstractGraph {
    private class ElementSet<E extends Element> extends SetView<E> {
        ElementSet(Class<E> type) {
            super(elementSet);
            this.type = type;
        }

        public int size() {
            if (!isFixed())
                return super.size();
            else if (size < 0)
                size = super.size();
            return size;
        }

        public boolean approves(Object obj) {
            return type.isInstance(obj);
        }

        protected Class<E> type;
        protected int size = -1;
    }

    /**
     * Creates a new, empty graph.
     */
    public GeneralGraph() {
    	// we need an explicit empty constructor
    }

    /** 
     * Constructs a clone of a given Graph.
     * @param graph the DefaultGraph to be cloned
     * @require graph != null
     * @ensure result.equals(graph)
     */
    public GeneralGraph(GraphShape graph) {
        this();
        if (graph instanceof GeneralGraph) {
            elementSet.addAll(((GeneralGraph) graph).elementSet);
        } else {
            elementSet.addAll(graph.edgeSet());
            elementSet.addAll(graph.nodeSet());
        }
    }

    public boolean containsElement(Element elem) {
        reporter.start(CONTAINS_ELEMENT);
        boolean result = elementSet.contains(elem);
        reporter.stop();
        return result;
    }

    // ------------------------ OBJECT OVERRIDES -----------------------

    public Graph clone() {
        reporter.start(CLONE);
        Graph result = new GeneralGraph(this);
        reporter.stop();
        return result;
    }

    public Graph newGraph() {
        return new GeneralGraph();
    }

    // ------------------------- COMMANDS ------------------------------

    public boolean addEdge(Edge edge) {
        reporter.start(ADD_EDGE);
        assert !isFixed() : "Trying to add " + edge + " to unmodifiable graph";
        boolean added = !elementSet.contains(edge);
        if (added) {
            Node[] elemParts = edge.ends();
            for (int i = 0; i < elemParts.length; i++) {
                addNode(elemParts[i]);
            }
            addEdgeWithoutCheck(edge);
        }
        reporter.stop();
        return added;
    }

    public boolean addEdgeWithoutCheck(Edge edge) {
        reporter.start(ADD_EDGE);
        assert !isFixed() : "Trying to add " + edge + " to unmodifiable graph";
        boolean added = elementSet.add(edge);
        if (added) {
            fireAddEdge(edge);
        }
        reporter.stop();
        return added;
    }

    public boolean addNode(Node node) {
        reporter.start(ADD_NODE);
        assert !isFixed() : "Trying to add " + node + " to unmodifiable graph";
        boolean added = elementSet.add(node);
        if (added) {
            assert nodeCount() == new NodeSet(nodeSet()).size() : String.format("Overlapping node number for %s in %s", node, nodeSet());
            fireAddNode(node);
        }
        reporter.stop();
        return added;

    }

    public boolean removeNode(Node node) {
        reporter.start(REMOVE_NODE);
        assert !isFixed() : "Trying to remove " + node + " from unmodifiable graph";
        boolean removed = elementSet.contains(node);
        if (removed) {
            Iterator<Element> edgeIter = elementSet.iterator();
            while (edgeIter.hasNext()) {
            	Element edge = edgeIter.next();
                if (edge instanceof Edge && ((Edge) edge).hasEnd(node)) {
                    edgeIter.remove();
                    fireRemoveEdge((Edge) edge);
                }
            }
            removeNodeWithoutCheck(node);
        }
        reporter.stop();
        return removed;
    }

    public boolean removeNodeWithoutCheck(Node node) {
        reporter.start(REMOVE_NODE);
        assert !isFixed() : "Trying to remove " + node + " from unmodifiable graph";
        boolean removed = elementSet.remove(node);
        if (removed) {
            fireRemoveNode(node);
        }
        reporter.stop();
        return removed;
    }

    public boolean removeEdge(Edge edge) {
        reporter.start(REMOVE_EDGE);
        assert !isFixed() : "Trying to remove " + edge + " from unmodifiable graph";
        boolean removed = elementSet.remove(edge);
        if (removed) {
            fireRemoveEdge(edge);
        }
        reporter.stop();
        return removed;
    }

    public Set<? extends Edge> edgeSet() {
        return Collections.unmodifiableSet(new ElementSet<Edge>(Edge.class));
    }

    public Set<? extends Node> nodeSet() {
        return Collections.unmodifiableSet(new ElementSet<Node>(Node.class));
    }
    
    public Collection<? extends Element> elementSet() {
        return Collections.unmodifiableSet(elementSet);
    }

    /**
     * The set of elements of this graph.
     */
    protected final Set<Element> elementSet = new HashSet<Element>();
}

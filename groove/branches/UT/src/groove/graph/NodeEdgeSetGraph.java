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
 * $Id: NodeEdgeSetGraph.java,v 1.1.1.1 2007-03-20 10:05:35 kastenberg Exp $
 */
package groove.graph;

import groove.util.UnmodifiableSetView;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Graph implementation based on a single set of nodes and edges.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $
 */
public class NodeEdgeSetGraph extends AbstractGraph {
    private class ElementSet<E extends Element> extends UnmodifiableSetView<E> {
        private ElementSet(Class<E> type) {
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
     * Extension of <tt>Set</tt> that invokes the notify methods of the graph
     * when elements are added or deleted
     */
    private class NotifySet extends HashSet<Element> {
        /**
         * Overwrites the method from <tt>Set</tt> to ensure
         * proper notification in case of removal from the iterator.
         */
        public Iterator<Element> iterator() {
            return new Iterator<Element>() {
                public boolean hasNext() {
                    return iterator.hasNext();
                }
                public Element next() {
                    last = iterator.next();
                    return last;
                }
                public void remove() {
                    iterator.remove();
                    if (last instanceof Node) {
                    	fireRemoveNode((Node) last);
                    } else {
                    	fireRemoveEdge((Edge) last);
                    }
                }

                private Iterator<Element> iterator = NotifySet.super.iterator();
                private Element last;
            };
        }

        /**
         * Overwrites the method from <tt>Set</tt> to ensure
         * proper observer notification in all cases. 
         * @require <tt>elem instanceof Element</tt>
         */
        public final boolean add(Element elem) {
            if (super.add(elem)) {
            	if (elem instanceof Node) {
            		fireAddNode((Node) elem);
            	} else {
            		fireAddEdge((Edge) elem);
            	}
                return true;
            } else
                return false;
        }

        /**
         * Overwrites the method from <tt>Set</tt> to ensure
         * proper observer notification in all cases. 
         * @require <tt>elem instanceof Element</tt>
         */
        public final boolean remove(Object elem) {
            if (super.remove(elem)) {
            	if (elem instanceof Node) {
            		fireRemoveNode((Node) elem);
            	} else {
            		fireRemoveEdge((Edge) elem);
            	}
                return true;
            } else
                return false;
        }
    }

    /**
     * Constructs a protytpe object of this class, to be used as a factory
     * for new (default) graphs.
     * @return a prototype <tt>GeneralGraph</tt> instance, only intended to
     * be used for its <tt>newGraph()</tt> method.
     */
    static public Graph getPrototype() {
        return new NodeEdgeSetGraph();
    }

    /**
     * Creates a new, empty graph.
     */
    public NodeEdgeSetGraph() {
    	// we need an explicit empty constructor
    }

    /** 
     * Constructs a clone of a given Graph.
     * @param graph the DefaultGraph to be cloned
     * @require graph != null
     * @ensure result.equals(graph)
     */
    protected NodeEdgeSetGraph(NodeEdgeSetGraph graph) {
        elementSet.addAll(graph.elementSet);
    }

    public boolean containsElement(Element elem) {
        reporter.start(CONTAINS_ELEMENT);
        boolean result = elementSet.contains(elem);
        reporter.stop();
        return result;
    }

    public boolean containsElementSet(Collection<? extends Element> elementSet) {
        reporter.start(CONTAINS_ELEMENT);
        boolean result = this.elementSet.containsAll(elementSet);
        reporter.stop();
        return result;
    }

    // ------------------------- COMMANDS ------------------------------

    public boolean addNode(Node node) {
        reporter.start(ADD_NODE);
        assert !isFixed() : "Trying to add " + node + " to unmodifiable graph";
        boolean added = elementSet.add(node);
        assert nodeCount() == new NodeSet(nodeSet()).size() : String.format("Overlapping node number for %s in %s", node, nodeSet());
        reporter.stop();
        return added;
    }

    public boolean addEdge(Edge edge) {
        reporter.start(ADD_EDGE);
        assert !isFixed() : "Trying to add " + edge + " to unmodifiable graph";
        boolean isNew = !elementSet.contains(edge);
        if (isNew) {
            Node[] elemParts = edge.ends();
            for (int i = 0; i < elemParts.length; i++) {
                elementSet.add(elemParts[i]);
            }
            elementSet.add(edge);
        }
        reporter.stop();
        return isNew;
    }

    public boolean addNodeSet(Collection<? extends Node> nodeSet) {
        reporter.start(ADD_NODE);
        assert !isFixed() : "Trying to add " + nodeSet + " to unmodifiable graph";
        boolean added = elementSet.addAll(nodeSet);
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
                Object edge = edgeIter.next();
                if (edge instanceof Edge && ((Edge) edge).hasEnd(node)) {
                    edgeIter.remove();
                }
            }
            elementSet.remove(node);
        }
        reporter.stop();
        return removed;
    }

    public boolean removeEdge(Edge edge) {
        reporter.start(REMOVE_EDGE);
        boolean removed = elementSet.remove(edge);
        reporter.stop();
        return removed;
    }

    public boolean removeNodeSet(Collection<Node> nodeSet) {
        reporter.start(REMOVE_NODE);
        Iterator<Element> edgeIter = elementSet.iterator();
        while (edgeIter.hasNext()) {
        	Element edge = edgeIter.next();
            if (edge instanceof Edge) {
                boolean edgeRemoved = false;
                Node[] ends = ((Edge) edge).ends();
                for (int i = 0; !edgeRemoved && i < ends.length; i++) {
                    if (nodeSet.contains(ends[i])) {
                        edgeIter.remove();
                        edgeRemoved = true;
                    }
                }
            }
        }
        boolean removed = elementSet.removeAll(nodeSet);
        reporter.stop();
        return removed;
    }

    public boolean removeEdgeSet(Collection<Edge> edgeSet) {
        reporter.start(REMOVE_EDGE);
        boolean removed = elementSet.removeAll(edgeSet);
        reporter.stop();
        return removed;
    }

    // -------------------- PackageGraph methods ---------------------

    public boolean addEdgeWithoutCheck(Edge edge) {
        return elementSet.add(edge);
    }

    public boolean addEdgeSetWithoutCheck(Collection<Edge> edgeSet) {
        return elementSet.addAll(edgeSet);
    }

    public boolean removeNodeWithoutCheck(Node node) {
        return elementSet.remove(node);
    }

    public boolean removeNodeSetWithoutCheck(Collection<Node> nodeSet) {
        return elementSet.removeAll(nodeSet);
    }

    // ------------- general methods (see AbstractGraph) ----------

    public Graph clone() {
        reporter.start(CLONE);
        Graph result = new NodeEdgeSetGraph(this);
        reporter.stop();
        return result;
    }

    public Graph newGraph() {
        return new NodeEdgeSetGraph();
    }

    public Set<? extends Edge> edgeSet() {
        return new ElementSet<Edge>(Edge.class);
    }

    public Set<? extends Node> nodeSet() {
        return new ElementSet<Node>(Node.class);
    }

    protected final Set<Element> elementSet = new NotifySet();
}

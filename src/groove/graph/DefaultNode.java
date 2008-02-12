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
 * $Id: DefaultNode.java,v 1.16 2008-02-12 15:15:31 fladder Exp $
 */
package groove.graph;

import groove.util.Dispenser;

/**
 * Default implementation of a graph node.
 * Default nodes have numbers, but node equality is determined by object identity and
 * not by node number.
 * @author Arend Rensink
 * @version $Revision: 1.16 $
 */
public class DefaultNode implements Node {
    /**
     * Constructs a fresh node, with an explicitly given number.
     * Note that node equality is determined by identity, but it is assumed
     * that never two distinct nodes with the same number will be compared.
     * This is achieved by using one of the <code>createNode</code> methods
     * in preference to this constructor.
     * @param nr the number for this node
     * @see #createNode()
     * @see #createNode(int)
     */
    protected DefaultNode(int nr) {
    	this.nodeNr = nr;
    	this.hashCode = computeHashCode();
    }
    
    /**     * FIXME: added this method as a temporary solution     * as there is no viable constructor (super()) while subclassing DefaultNode     * except by having your own node counter.       *      * See also: new DefaultEdge()     */    protected DefaultNode() {    	this.nodeNr = DefaultNode.nextExtNodeNr();    	this.hashCode = computeHashCode();    }
////    // ---------------- Element and related methods ----------------------
//
//    public Node imageFor(GenericNodeEdgeMap elementMap) {
//    	if( elementMap instanceof NodeEdgeMap ) {
//    		return imageFor((NodeEdgeMap)elementMap);
//    	} else if( elementMap instanceof VarNodeEdgeMultiMap ) {
//    		return imageFor((VarNodeEdgeMultiMap)elementMap);
//    	} return null;
//    }
//    
//    private Node imageFor(VarNodeEdgeMultiMap elementMap) {
//        return elementMap.getNode(this).toArray(new Node[0])[0];
//    }
//
//    private Node imageFor(NodeEdgeMap elementMap) {
//        return elementMap.getNode(this);
//    }

    // ----------------------------- OBJECT OVERRIDES -----------------------------

    /**
     * Returns an alias to this node itself (which is immutable).
     */
    @Override
    public Object clone() {
        return this;
    }

    /**
     * Returns the precomputed hashcode.
     * @see #computeHashCode()
     */
    @Override
    public int hashCode() {
    	return hashCode;
    }
    
    /**
     * Indicates whether this node is the same as another object.
     * This is implemented by object equality, but it is considered 
     * This is considered to be the case if the other object is also a
     * <tt>DefaultNode</tt>, and the node numbers coincide.
     * @param obj the object with which this node is compared
     * @return <tt>true</tt> if <tt>obj</tt> is a <tt>DefaultNode</tt> and
     * <tt>this</tt> and <tt>obj</tt> have the same node numbers
     */
    @Override
    public boolean equals(Object obj) {
        boolean result = (obj == this);        assert result || !(obj instanceof DefaultNode) || (nodeNr != ((DefaultNode) obj).nodeNr) : String.format("Distinct nodes with number %d: " + this + " & " + obj, nodeNr);        return result;
    }
//
//    /**
//     * Invokes the default constructor to create a fresh node.
//     */
//    @Deprecated
//    public Node newNode() {
//        return createNode();
//    }

    /**
     * Returns a string consisting of the letter <tt>'n'</tt> and the node number.
     */
    @Override
    public String toString() {
        return "n" + nodeNr;
    }

    /**
     * Implements the ordering criteria for graph elements from the perspective of {@link Node}s.
     * {@link DefaultNode}s are mutually ordered by their number.
     */
    public int compareTo(Element obj) {
    	int result;
        if (obj instanceof DefaultNode) {
            result = nodeNr - ((DefaultNode) obj).nodeNr;
        } else if (obj instanceof Edge) {
            result = compareTo(((Edge) obj).source());
            if (result == 0) {
            	// nodes come before edges with the same source
            	result = -1;
            }
        } else {
            throw new IllegalArgumentException("Default node "+this+" not comparable with "+obj);
        }
        assert result != 0 || equals(obj) : String.format("Ordering of distinct objects %s and %s yields 0", this, obj);
        return result;
    }
    
    /**
     * Returns the number of this node.
     * @return the {@link #nodeNr}-value
     */
    public int getNumber() {
    	return nodeNr;
    }

    /**
     * Computes the hash code for this node.
     * @return the hashcode for this node.
     */
    protected int computeHashCode() {
        // for the sake of determinism we base the hash code on the node number
//    	return System.identityHashCode(this);
//    	return nodeNr;
//    	int code = nodeNr+2;
//    	code *= code-1;
//    	return (code << 16) + 3*code;
    	// the following is taken from java.util.HashMap
    	int h = nodeNr + 2;
    	h *= h;
        h += ~(h << 14);
        h ^=  (h >>> 19);
        h +=  (h << 9);
        h ^=  (h >>> 15);
        return h;
    }
    
    /**
     * The number of this node.
     */
    private final int nodeNr;
    /**
     * The hashcode of this node.
     * The hashcode is precomputed at creation time using {@link #computeHashCode()}.
     */
    private final int hashCode;

    /** 
     * Factory method to create a default node with a certain number.
     * The idea is to create canonical representatives, so node equality is object equality. 
     */
    static public DefaultNode createNode(int nr) {    	if (nr > MAX_NODE_NUMBER) {    		throw new IllegalArgumentException(String.format("Node number %s too high", nr));    	}        if (nr >= nodes.length) {            int newSize = Math.max((int)(nodes.length*GROWTH_FACTOR), nr+1);            DefaultNode[] newNodes = new DefaultNode[newSize];            System.arraycopy(nodes, 0, newNodes, 0, nodes.length);            nodes = newNodes;        }
        DefaultNode result = nodes[nr];        if (result == null) {            result = nodes[nr] = new DefaultNode(nr);             nextNodeNr = Math.max(nextNodeNr, nr);            nodeCount++;        }
        return result;
    }
    
    /** 
     * Factory method to create a default node with a number obtained from
     * a dispenser.
     * Convenience method for <code>createNode(dispenser.getNumber())</code>.
     */
    static public DefaultNode createNode(Dispenser dispenser) {
        return createNode(dispenser.getNumber());
    }
    
    /** Returns the node with the first currently unused node number. */
    static public DefaultNode createNode() {        return createNode(nextNodeNr());    }

    /**
     * Returns the total number of nodes created.
     * @return the {@link #nodeCount}-value
     */
    static public int getNodeCount() {
        return nodeCount;
    }

    /**
     * Returns the maximum node number created.
     */
    static public int getHighestNodeNr() {
        return nextNodeNr;
    }
    
    /** 
     * Extracts a node number from a node. 
     * The node number is assumed to exist only if the node is a {@link DefaultNode}
     * Returns {@link #NO_NODE_NUMBER} if the number does not exist.
     * @param node the node of which to get the number
     * @return the number of the given node
     */
    static public int getNodeNr(Node node) {
        if (node instanceof DefaultNode) {
            int result = ((DefaultNode) node).getNumber();
            return result < MAX_NODE_NUMBER ? result : NO_NODE_NUMBER;
        } else {
            return NO_NODE_NUMBER;
        }
    }
    
    /**
     * Returns the next free node number, according to the static counter.
     * @return the next node-number
     */
    static private int nextNodeNr() {        while (nextNodeNr < nodes.length && nodes[nextNodeNr] != null) {            nextNodeNr++;        }        return nextNodeNr;    }
    /**     * Returns the fresh node number for subclasses of DefaultNode, and increments the counter.     */    static private int nextExtNodeNr() {    	return ++nextNodeNr;    }    
    /**
     * The total number of nodes in the {@link #nodes} array.
     */
    static private int nodeCount;
    
    /**
     * First (potentially) fresh node number available.
     */
    static private int nextNodeNr;    /** Initial capacity of the nodes array. */
    static private final int INIT_CAPACITY = 100;
    /** Growth factor of the nodes array. */
    static private final float GROWTH_FACTOR = 2.0f;
    /** Array of canonical nodes, such that <code>nodes[i] == 0</code> or
     * <code>nodes[i].getNumber() == i</code> for all <code>i</code>. 
     */
    static private DefaultNode[] nodes = new DefaultNode[INIT_CAPACITY];

    /**
     * The maximal number for {@link DefaultNode}s.
     */    
    public static final int MAX_NODE_NUMBER = 999999999;    /**     * First fresh node number for subclasses van DefaultNode.     */
    static private int nextNodeNrExt = MAX_NODE_NUMBER+1;    
    /**
     * Value indicating an invalid node number.
     */
    public static final int NO_NODE_NUMBER = -1;}

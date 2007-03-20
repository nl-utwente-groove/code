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
 * $Id: DefaultNode.java,v 1.1.1.2 2007-03-20 10:42:41 kastenberg Exp $
 */
package groove.graph;

import groove.util.Dispenser;

/**
 * Default implementation of a graph node.
 * Default nodes have numbers, but node equality is determined by object identity and
 * not by node number.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $ $Date: 2007-03-20 10:42:41 $
 */
public class DefaultNode implements Node {
    /**
     * Returns the total number of nodes created.
     * @return the {@link #nodeCount}-value
     */
    static public int getNodeCount() {
    	return nodeCount;
    }
    
    /**
     * Resets the static node number counter. 
     */
    static public void resetNodeNr() {
    	nextNodeNr = 0;
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
    static private int nextNodeNr() {
        int result = nextNodeNr;
        nextNodeNr++;
        return result;
    }
    
    /**
     * Registers the fact that a certain node number has been used.
     * This affects the fresh node numbers available, as well as the node count.
     * @param nr the node-number to be registered
     */
    static private void registerNode(int nr) {
//    	if (nr > MAX_NODE_NUMBER) {
//    		throw new IllegalArgumentException(String.format("Default node number %s exceeds maximum %s", nr, MAX_NODE_NUMBER));
//    	}
    	if (nr <= MAX_NODE_NUMBER && nextNodeNr <= nr) {
    		nextNodeNr = nr+1;
    	}
    	nodeCount++;
    }

    /**
     * The total number of nodes created during the run time of the program.
     * Used to number nodes uniquely.
     */
    static private int nodeCount;
    
    /**
     * First fresh node number available.
     */
    static private int nextNodeNr;

    /**
     * The maximal number for ordinary graph nodes.
     */
    public static final int MAX_NODE_NUMBER = 999999999;

    /**
     * Value indicating an invalid node number.
     */
    public static final int NO_NODE_NUMBER = -1;

    /**
     * Constructs a fresh node, with a number determined by an internally kept count.
     */
    public DefaultNode() {
        this(nextNodeNr());
    }
    
    /**
     * Constructs a fresh node, with a number determined by a given dispenser.
     * @param dispenser object used to determine the node number
     * @see Dispenser#getNumber()
     */
    public DefaultNode(Dispenser dispenser) {
        this(dispenser.getNumber());
    }
    
    /**
     * Constructs a fresh node, with an explicitly given number.
     * @param nr the number for this node
     */
    public DefaultNode(int nr) {
    	this.nodeNr = nr;
    	this.hashCode = computeHashCode();
    	registerNode(nr);
    }

    // ---------------- Element and related methods ----------------------

    public Node imageFor(NodeEdgeMap elementMap) {
        return elementMap.getNode(this);
    }

    // ----------------------------- OBJECT OVERRIDES -----------------------------

    /**
     * Returns an alias to this node itself (which is immutable).
     */
    public Object clone() {
        return this;
    }

    /**
     * Returns the precomputed hashcode.
     * @see #computeHashCode()
     */
    public int hashCode() {
    	return hashCode;
    }
    
    /**
     * Indicates whether this node is the same as another object.
     * This is considered to be the case if the other object is also a
     * <tt>DefaultNode</tt>, and the node numbers coincide.
     * @param obj the object with which this node is compared
     * @return <tt>true</tt> if <tt>obj</tt> is a <tt>DefaultNode</tt> and
     * <tt>this</tt> and <tt>obj</tt> have the same node numbers
     */
    public boolean equals(Object obj) {
        return this == obj;
    }

    /**
     * Invokes the default constructor to create a fresh node.
     */
    @Deprecated
    public Node newNode() {
        return new DefaultNode();
    }

    /**
     * Returns a string consisting of the letter <tt>'n'</tt> and the node number.
     */
    public String toString() {
        return "n" + nodeNr;
    }

    /**
     * Implements the ordering criteria for graph elements from the perspective of {@link Node}s.
     * {@link DefaultNode}s are mututally ordered by their number.
     */
    public int compareTo(Element obj) {
        if (obj instanceof DefaultNode) {
            return nodeNr - ((DefaultNode) obj).nodeNr;
        } else if (obj instanceof Edge) {
            Node edgeSource = ((Edge) obj).source();
            if (equals(edgeSource)) {
                return -1;
            } else {
                return compareTo(edgeSource);
            }
        } else {
            throw new IllegalArgumentException("Default node "+this+" not comparable with "+obj);
        }
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
    	return System.identityHashCode(this);
//    	return nodeNr;
//    	int code = nodeNr;
//    	code ^= (code << 8);
//    	return code ^ (code << 16);
    }
    
    /**
     * The number of this node.
     */
    protected final int nodeNr;
    /**
     * The hashcode of this node.
     * The hashcode is precomputed at creation time using {@link #computeHashCode()}.
     */
    protected final int hashCode;
}
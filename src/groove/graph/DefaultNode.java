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
 * $Id: DefaultNode.java,v 1.17 2008-02-19 10:35:31 fladder Exp $
 */
package groove.graph;

import groove.trans.RuleNode;
import groove.util.Dispenser;

/**
 * Default implementation of a graph node. Default nodes have numbers, but node
 * equality is determined by object identity and not by node number.
 * @author Arend Rensink
 * @version $Revision$
 */
public class DefaultNode extends AbstractNode implements RuleNode {
    /**
     * Constructs a fresh node, with an explicitly given number. Note that node
     * equality is determined by identity, but it is assumed that never two
     * distinct nodes with the same number will be compared. This is achieved by
     * using one of the <code>createNode</code> methods in preference to this
     * constructor.
     * @param nr the number for this node
     * @see #createNode()
     * @see #createNode(int)
     */
    protected DefaultNode(int nr) {
        super(nr);
    }

    /** Factory constructor. */
    public DefaultNode newNode(int nr) {
        return new DefaultNode(nr);
    }

    /**
     * Returns a string consisting of the letter <tt>'n'</tt> and the node
     * number.
     */
    @Override
    public String getToStringPrefix() {
        return "n";
    }

    /**
     * Factory method to create a default node with a certain number. The idea
     * is to create canonical representatives, so node equality is object
     * equality. For negative numbers, the node is not stored.
     * @param cons is an object that defines the constructor. Subclasses of
     *             <code>DefaultNode</code> may override the method
     *             {@link #newNode(int)} so that the factory creates nodes with
     *             a more specialized type. See, e.g., <code>ShapeNode</code>.
     */
    static public DefaultNode createNode(int nr, DefaultNode cons) {
        DefaultNode result;
        if (nr >= 0) {
            if (nr >= nodes.length) {
                int newSize =
                    Math.max((int) (nodes.length * GROWTH_FACTOR), nr + 1);
                DefaultNode[] newNodes = new DefaultNode[newSize];
                System.arraycopy(nodes, 0, newNodes, 0, nodes.length);
                nodes = newNodes;
            }
            result = nodes[nr];
            if (result == null) {
                result = nodes[nr] = cons.newNode(nr);
                nextNodeNr = Math.max(nextNodeNr, nr);
                nodeCount++;
            }
        } else {
            result = cons.newNode(nr);
        }
        return result;
    }

    /** Default method that uses the DefaultNode constructor. */
    static public DefaultNode createNode(int nr) {
        return createNode(nr, CONS);
    }

    /**
     * Factory method to create a default node with a number obtained from a
     * dispenser. Convenience method for
     * <code>createNode(dispenser.getNumber())</code>.
     */
    static public DefaultNode createNode(Dispenser dispenser) {
        return createNode(dispenser.getNumber());
    }

    /** Returns the node with the first currently unused node number. */
    static public DefaultNode createNode() {
        return createNode(nextNodeNr());
    }

    /** Returns the node with the first currently unused node number. */
    static public DefaultNode createNode(DefaultNode constructor) {
        return createNode(nextNodeNr(), constructor);
    }

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
     * Returns the next free node number, according to the static counter.
     * @return the next node-number
     */
    static private int nextNodeNr() {
        while (nextNodeNr < nodes.length && nodes[nextNodeNr] != null) {
            nextNodeNr++;
        }
        return nextNodeNr;
    }

    /**
     * The total number of nodes in the {@link #nodes} array.
     */
    static private int nodeCount;

    /**
     * First (potentially) fresh node number available.
     */
    static private int nextNodeNr;

    /** Initial capacity of the nodes array. */
    static private final int INIT_CAPACITY = 100;
    /** Growth factor of the nodes array. */
    static private final float GROWTH_FACTOR = 2.0f;
    /**
     * Array of canonical nodes, such that <code>nodes[i] == 0</code> or
     * <code>nodes[i].getNumber() == i</code> for all <code>i</code>.
     */
    static private DefaultNode[] nodes = new DefaultNode[INIT_CAPACITY];

    /**
     * Value indicating an invalid node number.
     */
    public static final int NO_NODE_NUMBER = -1;

    /** Used only as a reference for the constructor */
    public static final DefaultNode CONS = new DefaultNode(NO_NODE_NUMBER);

}

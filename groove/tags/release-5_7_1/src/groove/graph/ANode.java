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
 * $Id$
 */
package groove.graph;

/**
 * Default implementation of a graph node. Default nodes have numbers, but node
 * equality is determined by object identity and not by node number.
 * @author Arend Rensink
 * @version $Revision$
 */
abstract public class ANode implements Node, Cloneable {
    /**
     * Constructs a fresh node, with an explicitly given number. Note that node
     * equality is determined by identity, but it is assumed that never two
     * distinct nodes with the same number will be compared.
     * @param nr the number for this node
     */
    protected ANode(int nr) {
        this.nodeNr = nr;
    }

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
    final public int hashCode() {
        if (this.hashCode == 0) {
            int result = computeHashCode();
            this.hashCode = result == 0 ? 1 : result;
        }
        return this.hashCode;
    }

    /**
     * Indicates whether this node is the same as another object. This is
     * implemented by object equality, but it is considered This is considered
     * to be the case if the other object is also a <tt>DefaultNode</tt>, and
     * the node numbers coincide.
     * @param obj the object with which this node is compared
     * @return <tt>true</tt> if <tt>obj</tt> is a <tt>DefaultNode</tt> and
     *         <tt>this</tt> and <tt>obj</tt> have the same node numbers
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Node)) {
            return false;
        }
        Node other = (Node) obj;
        if (getNumber() != other.getNumber()) {
            return false;
        }
        if (getClass() != other.getClass()) {
            return false;
        }
        return true;
    }

    /**
     * Returns a string consisting of {@link #getToStringPrefix()} and the node
     * number.
     */
    @Override
    public String toString() {
        return getToStringPrefix() + this.nodeNr;
    }

    /** Returns the prefix for the {@link #toString()} methods. */
    abstract protected String getToStringPrefix();

    /**
     * Compares the node numbers of two nodes.
     */
    public int compareTo(Node obj) {
        int result;
        result = getNumber() - obj.getNumber();
        if (result == 0) {
            result = getClass().getName().compareTo(obj.getClass().getName());
        }
        assert result != 0 || equals(obj) : String.format(
            "Ordering of distinct objects %s and %s yields 0", this, obj);
        return result;
    }

    /**
     * Returns the number of this node.
     * @return the {@link #nodeNr}-value
     */
    @Override
    public int getNumber() {
        return this.nodeNr;
    }

    /**
     * Computes the hash code for this node.
     * @return the hashcode for this node.
     */
    protected int computeHashCode() {
        // for the sake of determinism we base the hash code on the node number
        // the following is taken from java.util.HashMap
        int h = (this.nodeNr + 2) ^ getClass().hashCode();
        h *= h;
        h += ~(h << 14);
        h ^= (h >>> 19);
        h += (h << 9);
        h ^= (h >>> 15);
        return h;
    }

    /**
     * The number of this node.
     */
    private final int nodeNr;
    /**
     * The hashcode of this node. The hashcode is precomputed at creation time
     * using {@link #computeHashCode()}.
     */
    private int hashCode;
}

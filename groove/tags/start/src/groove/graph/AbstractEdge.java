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
 * $Id: AbstractEdge.java,v 1.1.1.2 2007-03-20 10:42:40 kastenberg Exp $
 */
package groove.graph;

/**
 * Defines an abstract edge class by extending the abstract composite.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public abstract class AbstractEdge implements Edge {
    /**
     * The maximal number of ends of any edge currently in the system.
     * Note that this may be dynamically updated by any concrete edge class.
     * @invariant <tt>maxEndCount &gtr;= 1</tt>
     */
    static private int maxEndCount;
    
    /**
     * Sets the maximal number of ends of any edge currently in the system.
     * This only has effect if the new number exceeds the previous maximum.
     * @param endCount the new maximum end count
     * @ensure if <tt>getMaxEndCount() &gtr;= endCount</tt>
     * @see #getMaxEndCount()
     */
    static public void setMaxEndCount(int endCount) {
        maxEndCount = Math.max(maxEndCount, endCount);
    }

    /**
     * Returns the maximal number of ends of any edge currently in the system.
     * Note that this may be dynamically increased by any concrete edge class.
     * @return the maximum number of ends of any edge
     * @ensure <tt>result &gtr;= 1</tt>
     */
    static public int getMaxEndCount() {
        return maxEndCount;
    }

    /**
     * Creates an edge with a given source node and label.
     */
    public AbstractEdge(Node source, Label label) {
		this.source = source;
		this.label = label;
//		this.hashCode = computeHashCode();
	}

	public Node source() {
        return source;
    }
	
	public Label label() {
		return label;
	}

    /**
     * Looks up the requested node through <tt>ends()[i]</tt>.
     */
    public Node end(int i) {
        return ends()[i];
    }
    
    /**
     * Computes the end count through <tt>ends().length</tt>.
     */
    public int endCount() {
        return ends().length;
    }

    /**
     * Looks up <tt>node</tt> by comparing it to each <tt>end(i)</tt> in turn,
     * and returning the first <tt>i</tt> for which the comparison holds.
     */
    public int endIndex(Node node) {
        int result = -1;
        for (int i = 0; result < 0 && i < endCount(); i++) {
            if (end(i).equals(node)) {
                result = i;
            }
        }        
        return result;
    }

    /**
     * Looks up <tt>node</tt> by comparing it to each <tt>end(i)</tt> in turn.
     */
    public boolean hasEnd(Node node) {
        boolean result = false;
        for (int i = 0; !result && i < endCount(); i++) {
            result = end(i).equals(node);
        }        
        return result;
    }
    
    /**
     * Yields {@link #source()} if {@link #endCount()} is 1, otherwise 
     * the end at {@link #TARGET_INDEX}.
     */
    public Node opposite() {
        if (endCount() == 1) {
            return source();
        } else {
            return end(Edge.TARGET_INDEX);
        }
    }
    
    // ------------------------------ Object overrides --------------------------
//
//    /**
//     * Implements the ordering rules for {@link Element}s from the perspective
//     * of a {@link Composite}.
//     * @see Element#compareTo(Element)
//     */
//    public int compareTo(Element obj) {
//        if (obj instanceof Node) {
//            // for nodes, we just need to look at the source of this composite
//            if (source().equals(obj)) {
//                return +1;
//            } else {
//                return source().compareTo(obj);
//            }
//        } else if (obj instanceof Edge) {
//            Edge other = (Edge) obj;
//            // for edges, we also just compare source nodes
//            if (source().equals(other.source())) {
//                return -1;
//            } else {
//                return source().compareTo(other.source());
//            }
//        } else if (obj instanceof Composite) {
//            Composite other = (Composite) obj;
//            if (endCount() != other.endCount()) {
//                return endCount() - other.endCount();
//            }
//            for (int i = 0; i < endCount(); i++) {
//                if (!end(i).equals(other.end(i))) {
//                    return end(i).compareTo(other.end(i));
//                }
//            }
//            return 0;
//        } else {
//            throw new IllegalArgumentException("Graph element "+this+" incomparable with "+obj);
//        }
//    }

    /**
     * Since all composites are immutable, the method just returns <code>this</code>.
     */
    public AbstractEdge clone() {
        return this;
    }
//
//    /**
//     * Returns <tt>true</tt> if <tt>obj</tt> is also a composite
//     * with the same label and number of endpoints, and 
//     * equal endpoints at each index.
//     * The actual test is delegated to {@link #isTypeEqual(Object)} and {@link #isEndEqual(Composite)}.
//     * @see #isTypeEqual(Object)
//     * @see #isEndEqual(Composite)
//     */
//    public boolean equals(Object obj) {
//        return isTypeEqual(obj) && isEndEqual((Composite) obj);
//    }
    
    /**
     * Delegates to {@link #computeHashCode()}.
     */
    final public int hashCode() {
    	if (! hashCodeInit) {
    		hashCode = computeHashCode();
    		hashCodeInit = true;
    	}
        return hashCode;
    }
/**
	 * Overwrites the method to add a hash code for the label.
	 */
	protected int computeHashCode() {
	    int result = 0;
	    for (int i = 0; i < endCount(); i++) {
	        result += end(i).hashCode() << (i+1);
	    }
	    result += label().hashCode();
	    return result;
	}

/**
     * Implements the ordering rules for {@link Element}s from the perspective
     * of an {@link Edge}.
     * @see Element#compareTo(Element)
     */
    public int compareTo(Element obj) {
        if (obj instanceof Node) {
            // for nodes, we just need to look at the source of this edge
            if (source().equals(obj)) {
                return +1;
            } else {
                return source().compareTo(obj);
            }
        } else {
            Edge other = (Edge) obj;
            if (!source().equals(other.source())) {
                return source().compareTo(other.source());
            }
            // for other edges, first the end count, then the label, then the other ends
            if (endCount() != other.endCount()) {
                return endCount() - other.endCount();
            }
            if (!label().equals(other.label())) {
                return label().compareTo(other.label());
            }
            for (int i = 1; i < endCount(); i++) {
                if (!end(i).equals(other.end(i))) {
                    return end(i).compareTo(other.end(i));
                }
            }
            return 0;
        }
    }

    /**
     * Returns <tt>true</tt> if <tt>obj</tt> is also an edge
     * with the same label and number of endpoints, and 
     * equal endpoints at each index.
     * The actual test is delegated to {@link #isTypeEqual(Object)} and {@link #isEndEqual(Edge)}.
     * @see #isTypeEqual(Object)
     * @see #isEndEqual(Edge)
     */
    public boolean equals(Object obj) {
        return isTypeEqual(obj) && isEndEqual((Edge) obj) && label().equals(((Edge) obj).label());
    }

    /**
     * Tests if another object is type equal to this one. This implementation insists that the
     * object is an {@link Edge}. Callback method from
     * {@link #equals(Object)}.
     */
    /**
     * This implementation tests if <code>obj instanceof Edge</code>.
     */
    protected boolean isTypeEqual(Object obj) {
        return obj instanceof Edge;
    }

    //
	//    /**
	//     * Tests if another object is type equal to this one. This implementation insists that the
	//     * object is a {@link Composite} and not an {@link Edge}. Callback method from
	//     * {@link #equals(Object)}.
	//     */
	//    protected boolean isTypeEqual(Object obj) {
	//        return obj instanceof Composite && !(obj instanceof Edge);
	//    }
	//    
	    /**
	     * Tests if this composite has the same number of end points as well as
	     * equal end points as another.
	     * Callback method from {@link #equals(Object)}.
	     */
	    protected boolean isEndEqual(Edge other) {
	        boolean result = endCount() == other.endCount();
	        for (int i = 0; result && i < endCount(); i++) {
	            result = end(i).equals(other.end(i));
	        }
	        return result;
	    }

	/**
	 * The source node of this edge. 
	 */
	protected final Node source;
    /** The label of this edge. @invariant label != null */
    protected final Label label;
    /** The pre-computed hash code. */
    protected int hashCode;
    /** Flag to indicate that the hash code has been initialised. */
    protected boolean hashCodeInit;
}
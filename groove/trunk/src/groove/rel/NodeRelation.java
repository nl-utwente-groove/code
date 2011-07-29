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
 * $Id: NodeRelation.java,v 1.3 2008-01-30 09:32:26 iovka Exp $
 */
package groove.rel;

import groove.graph.Edge;
import groove.graph.Node;
import groove.util.Duo;

import java.util.Set;

/**
 * Specifies the algebra of binary relations over nodes. All operations are
 * provided in two versions: one without side effects that returns the result of
 * the operation as its result; and one that performs the operation in-place.
 * The former are called <tt>getOperation</tt> and the latter
 * <tt>doOperation</tt>.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface NodeRelation extends Cloneable {
    /**
     * Returns the set of all related pairs.
     */
    Set<Entry> getAllRelated();

    /**
     * Adds a pair to the relation, consisting of the source and target
     * of a given edge.
     * The return value indicates if the pair was actually added or was already
     * in the relation.
     * @param edge the source of the pair to be added
     * @return <tt>true</tt> if the pair was actually added, <tt>false</tt> if
     *         it was already in the relation.
     */
    boolean addRelated(Edge edge);

    /** 
     * Adds a relation from a given node to itself.
     * The return value indicates if a corresponding entry was already there.
     */
    boolean addSelfRelated(Node node);

    /**
     * Indicates if there are no related elements in the relation.
     * @return <tt>true</tt> if there are no related elements in the relation
     */
    boolean isEmpty();

    /**
     * Returns a copy of this node relation.
     */
    NodeRelation clone();

    /**
     * Returns a fresh, empty node relation over the same universe as this one.
     */
    NodeRelation newInstance();

    /**
     * Has the effect of <tt>getThen(EdgeBasedRelation)</tt>, but modifies
     * <tt>this</tt>.
     * @return <tt>this</tt>
     */
    NodeRelation doThen(NodeRelation other);

    /**
     * Has the effect of <tt>getOr(EdgeBasedRelation)</tt>, but modifies
     * <tt>this</tt>. Returns <tt>true</tt> if this relation was changed as a
     * result of the operation.
     * @return <tt>true</tt> if this relation was changed as a result of the
     *         operation
     */
    boolean doOr(NodeRelation other);

    /**
     * Has the effect of <tt>getTransitiveClosure()</tt>, but modifies
     * <tt>this</tt>. Returns <tt>true</tt> if this relation was changed as a
     * result of the operation.
     * @return <tt>true</tt> if this relation was changed as a result of the
     *         operation
     */
    boolean doTransitiveClosure();

    /**
     * Returns the relation that is the inverse of this one. The new relation
     * consists of all <code>(pre,post)</code> pairs for which
     * <code>(post,pre)</code> is in this relation.
     */
    void doInverse();

    /** Entry in the relation. */
    class Entry extends Duo<Node> {
        /** Constructs a self-entry from a given node. */
        public Entry(Node node) {
            super(node, node);
        }

        /** Constructs an entry between two nodes. */
        protected Entry(Node one, Node two) {
            super(one, two);
        }

        /** Constructs an entry from a given edge. */
        public Entry(Edge edge) {
            this(edge.source(), edge.target());
        }

        /** Constructs the inverse of this entry.
         * This means the two elements of the duo are swapped.
         */
        public Entry invert() {
            return new Entry(two(), one());
        }

        /**
         * Appends another entry to this one.
         * @param other the other entry
         */
        public Entry append(Entry other) {
            assert two().equals(other.one());
            return new Entry(one(), other.two());
        }
    }
}

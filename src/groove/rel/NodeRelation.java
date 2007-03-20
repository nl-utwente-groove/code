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
 * $Id: NodeRelation.java,v 1.1.1.2 2007-03-20 10:42:53 kastenberg Exp $
 */
package groove.rel;

import groove.graph.Edge;
import groove.graph.Node;

import java.util.Collection;
import java.util.Set;

/**
 * Specifies the algebra of binary relations over nodes.
 * All operations are provided in two versions: one without side
 * effects that returns the result of the operation as its result; and
 * one that performs the operation in-place. The former are called 
 * <tt>getOperation</tt> and the latter <tt>doOperation</tt>.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public interface NodeRelation {
    /**
     * Returns the set of all related pairs, as a set of {@link Edge}s.
     */
    Set<? extends Edge> getAllRelated();

    /**
     * Adds a pair to the relation, consisting of a girven source and target.
     * The return value indicates if the pair was actually added 
     * or was already in the relation.
     * @param pre the source of the pair to be added
     * @param post the target of the pair to be added
     * @return <tt>true</tt> if the pair was actually added, 
     * <tt>false</tt> if it was already in the relation.
     */
    boolean addRelated(Node pre, Node post);

    /**
     * Adds a pair to the relation, consisting of the source and target of a given graph edge.
     * The return value indicates if the pair was actually added 
     * or was already in the relation.
     * @param edge the pair to be added to the relation
     * @return <tt>true</tt> if the pair was actually added, 
     * <tt>false</tt> if it was already in the relation.
     * @ensure <tt>isRelated(edge.pre(), edge.post())</tt>.
     */
    boolean addRelated(Edge edge);
//
//    /**
//     * Adds a pair of nodes to the relation.
//     * The return value indicates if the pair was actually added 
//     * or was already in the relation.
//     * @param pre the pre-image of the pair to be added
//     * @param post the post-image of the pair to be added
//     * @return <tt>true</tt> if the pair was actually added, 
//     * <tt>false</tt> if it was already in the relation.
//     * @ensure <tt>isRelated(pre, post)</tt>.
//     */
//    boolean addRelated(Node pre, Node post);
//    
    /**
     * Adds a set of pairs to the relation.
     * The return value indicates if any pair was actually added,
     * or if all of them were already in the relation
     * @param pairSet the set of pairs to be added to the relation
     * @return <tt>true</tt> if the relation was changed as a result of this operation.
     */
    boolean addRelated(Collection<? extends Edge> pairSet);

    /**
     * Relates a node to itself.
     * @param node the node to be reflexively related
     * @return <tt>true</tt> if the reflexive relation was actually added;
     * <tt>false</tt> if <tt>node</tt> was ale=ready related to itself.
     */
    boolean addSelfRelated(Node node);
    
    /**
     * Adds reflexive relations for a set of nodes.
     * @param nodeSet the set of nodes to be reflexively related
     * @return <tt>true</tt> if the relation was changed as a result of this operation
     */
    boolean addSelfRelated(Collection<? extends Node> nodeSet);
//    
//    /**
//     * Returns the collection of post-images to a given set of pre-images.
//     * @see #getAllPostImages()
//     * @see #getAllPreImages(Collection)
//     */
//    Set getAllPostImages(Collection preSet);
//    
//    /**
//     * Returns the collection of all post-images in the relation.
//     * @see #getAllPostImages(Collection)
//     * @see #getAllPreImages()
//     */
//    Set getAllPostImages();
//    
//    /**
//     * Returns the collection of pre-images to a given set of post-images.
//     * @see #getAllPreImages()
//     * @see #getAllPostImages(Collection)
//     */
//    Set getAllPreImages(Collection postSet);
//    
//    /**
//     * Returns the collection of all pre-images in the relation.
//     * @see #getAllPreImages(Collection)
//     * @see #getAllPostImages()
//     */
//    Set getAllPreImages();
//    
//    /**
//     * Returns the set of post-images to a given pre-image.
//     * @see #getAllPostImages(Collection)
//     */
//    Set getPostImages(Node pre);
//
//    /**
//     * Returns the set of pre-images to a given post-image.
//     */
//    Set getPreImages(Node post);
//    
//    /**
//     * Indicates if this relation is injective, i.e., if every post-image is
//     * related to at most one pre-image.
//     * @see #isFunctional()
//     */
//    boolean isInjective();
//    
//    /**
//     * Indicates if this relation is functional, i.e., if every pre-image is
//     * related to at most one post-image.
//     * @see #isInjective()
//     */
//    boolean isFunctional();
    
    /**
     * Indicates if there are no related elements in the relation.
     * @return <tt>true</tt> if there are no related elements in the relation
     */
    boolean isEmpty();
    
    /**
     * Returns a copy of this node relation.
     */
    NodeRelation copy();

    /**
     * Returns a fresh, empty node relation over the same universe as this one. 
     */
    NodeRelation newInstance();

    /**
     * Returns a fresh relation based on a given label.
     */
    NodeRelation newInstance(String label);

    /**
     * Creates an identity relation over the universe of this node relation.
     */
    NodeRelation createIdentityRelation();

    /**
     * Creates a maximal relation over the universe of this node relation.
     * What a maximal relation is, depends on the type of relation: if it is 
     * based on a graph, then in a maximal relation only edge-connected nodes
     * are related.
     */
    NodeRelation createMaximalRelation();

    /**
     * Computes and returns the concatenation of this relation with another.
     * In the result, <tt>areRelated(x,y)x/tt> if there is a <tt>z</tt> such
     * that <tt>this.areRelated(x,z)</tt> and <tt>other.areRelated(z,y)</tt>.
     * @param other the other operand of the concatentation
     * @return the concatenation of <tt>this</tt> and <tt>other</tt>
     * @see #getAfter(NodeRelation)
     */
    NodeRelation getThen(NodeRelation other);
    
    /**
     * Has the effect of <tt>getThen(EdgeBasedRelation)</tt>, but  modifies <tt>this</tt>.
     * @return <tt>this</tt>
     * @see #getThen(NodeRelation)
     */
    NodeRelation doThen(NodeRelation other);
    
    /**
     * Computes and returns the concatenation of this relation after another.
     * In the result, <tt>areRelated(x,y)</tt> if there is a <tt>z</tt> such
     * that <tt>other.areRelated(x,z)</tt> and <tt>this.areRelated(z,y)</tt>.
     * @param other the other operand of the concatentation
     * @return the concatenation of <tt>other</tt> and <tt>this</tt>
     * @see #getThen(NodeRelation)
     */
    NodeRelation getAfter(NodeRelation other);
    
//    /**
//     * Has the effect of <tt>getAfter(EdgeBasedRelation)</tt>, but  modifies <tt>this</tt>.
//     * @return <tt>this</tt>
//     * @see #getAfter(EdgeBasedRelation)
//     */
//    EdgeBasedRelation doAfter(EdgeBasedRelation other);
//    
//    /**
//     * Returns the conjunction of this relation with another.
//     * Has no side-effects upon this relation itself.
//     */
//    NodeRelation getAnd(NodeRelation other);
//    
//    /**
//     * Has the effect of <tt>getAnd(EdgeBasedRelation)</tt>, but  modifies <tt>this</tt>.
//     * Returns <tt>true</tt> if this relation was changed as a result of the operation.
//     * @return <tt>true</tt> if this relation was changed as a result of the operation
//     * @see #getAnd(NodeRelation)
//     */
//    boolean doAnd(NodeRelation other);
//    
    /**
     * Returns the disjunction of this relation with another.
     * Has no side-effects upon this relation itself.
     */
    NodeRelation getOr(NodeRelation other);
    
    /**
     * Has the effect of <tt>getOr(EdgeBasedRelation)</tt>, but  modifies <tt>this</tt>.
      * Returns <tt>true</tt> if this relation was changed as a result of the operation.
     * @return <tt>true</tt> if this relation was changed as a result of the operation
     * @see #getOr(NodeRelation)
     */
    boolean doOr(NodeRelation other);

    /**
     * Returns the transitive closure of this relation.
     * Has no side-effects upon this relation itself.
     */
    NodeRelation getTransitiveClosure();
    
    /**
     * Has the effect of <tt>getTransitiveClosure()</tt>, but  modifies <tt>this</tt>.
     * Returns <tt>true</tt> if this relation was changed as a result of the operation.
     * @return <tt>true</tt> if this relation was changed as a result of the operation
     * @see #getTransitiveClosure()
     */
    boolean doTransitiveClosure();
    
    /**
     * Returns the reflexive closure of this relation.
     * Has no side-effects upon this relation itself.
     */
    NodeRelation getReflexiveClosure();
    
    /**
     * Has the effect of <tt>getReflexiveClosure()</tt>, but  modifies <tt>this</tt>.
     * Returns <tt>true</tt> if this relation was changed as a result of the operation.
     * @return <tt>true</tt> if this relation was changed as a result of the operation
     * @see #getReflexiveClosure()
     */
    boolean doReflexiveClosure();
    
    /**
     * Returns the relation that is the inverse of this one.
     * The new relation consists of all <code>(pre,post)</code> pairs for
     * which <code>(post,pre)</code> is in this relation.
     */
    NodeRelation getInverse();
//    
//    /**
//     * Returns the relation that is the negation of this one.
//     * The new relation consists of all <code>(pre,post)</code> pairs 
//     * that are not in this one.
//     */
//    NodeRelation getNegation();
}

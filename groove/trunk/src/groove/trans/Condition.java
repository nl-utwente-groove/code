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
 * $Id: Condition.java,v 1.1 2007-10-05 08:31:38 rensink Exp $
 */
package groove.trans;

import groove.graph.Graph;
import groove.graph.NodeEdgeMap;
import groove.view.FormatException;

import java.util.Collection;
import java.util.Iterator;

/**
 * Interface for conditions over graphs.
 * Conditions are parts of predicates, effectively constituting disjuncts.
 * @author Arend Rensink
 * @version $Revision: 1.1 $
 */
public interface Condition {
    /**
     * Called to indicate that this predicate is fixed from now on.
     * This means no more graph conditions may be added to it.
     * The conditions themselves are also set to fixed.
     * @see #isFixed()
     */
    public void setFixed();
    
    /**
     * Indicates whether the predicate has been fixed.
     * @return <code>true</code> if the predicate has been fixed
     * @see #setFixed()
     */
    public boolean isFixed();
    
    /** 
     * Returns the name of this predicate.
     * A return value of <code>null</code> indicates that the predicate is unnamed.
     */
    public NameLabel getName();
    
    /**
     * Indicates if this graph predicate is closed, which is to say that
     * it has an empty context.
     * Convenience method for <code>getContext().isEmpty()</code>.
     * @return <code>true</code> if this predicate has an empty context.
     */
    public boolean isGround();
    
    /**
     * Element map from the context of this condition to the condition target.
     */
    public NodeEdgeMap getPatternMap();
    
    /**
     * The codomain of the pattern morphism.
     * Convenience method for <code>getPattern().cod()</code>.
     */
    public Graph getTarget();
    
    /** Returns the secondary properties of this graph condition. */
    public SystemProperties getProperties();
    
    /**
     * Tests if this graph condition is internally consistent.
     * Inconsistencies may arise for instance due to incompatibility of the
     * actual condition and the secondary properties, as returned by {@link #getProperties()}.
     * The method does nothing if this graph condition is consistent, and throws an exception
     * if it is not.
     * @throws FormatException if this graph condition is inconsistent. The exception contains
     * a list of errors. 
     */
    public void testConsistent() throws FormatException;

    /**
     * Returns the collection of sub-conditions of this graph condition.
     * The intended interpretation of the sub-conditions (as conjuncts or disjuncts) 
     * depends on this condition.
     */
    public Collection<? extends Condition> getSubConditions();
    
    /**
     * Adds a sub-condition to this graph condition.
     * @param condition the condition to be added
     * @see #getSubConditions()
     */
    public void addSubCondition(Condition condition);
    
    /** 
     * Tests if this condition is ground and has a match to a given host graph.
     * Convenience method for <code>getMatchIter(host, null).hasNext()</code> 
     */
    public boolean hasMatch(Graph host);
    
	/** 
     * Returns an iterator over all matches for a given host graph, given
     * a matching of the pattern graph.
     * @param host the graph in which the match is to be found
     * @param contextMap a matching of the pattern of this condition; may
     * be <code>null</code> if the condition is ground.
     * @throws IllegalArgumentException if <code>patternMatch</code> is <code>null</code>
     * and the condition is not ground, or if <code>patternMatch</code> is not compatible
     * with the pattern graph
     */
    public Iterator<? extends Match> getMatchIter(Graph host, NodeEdgeMap contextMap);
    
    /** 
     * Returns an iterable wrapping a call to {@link #getMatchIter(Graph, NodeEdgeMap)}.
     */
    public Iterable<? extends Match> getMatches(Graph host, NodeEdgeMap contextMap);
}
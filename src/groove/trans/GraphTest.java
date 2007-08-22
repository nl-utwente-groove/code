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
 * $Id: GraphTest.java,v 1.6 2007-08-22 09:19:44 kastenberg Exp $
 */
package groove.trans;

import groove.graph.Graph;
import groove.rel.VarGraph;
import groove.rel.VarMorphism;

/**
 * Super-interface for tests over graphs.
 * Contains the common functionality of {@link GraphPredicate} and {@link GraphCondition}.
 * @author Arend Rensink
 * @version $Revision: 1.6 $
 */
public interface GraphTest {    
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
     * Returns the context of this predicate.
     * The context is a subgraph that has to be matched already before
     * this predicate can become relevant.
     * If the context is empty, we call the predicate <i>closed</i>.
     * @see #isGround()
     */
    public VarGraph getContext();
    
    /** 
     * Checks if this graph test is satisfied for a given subject.
     * This can only be the case if the subject's domain coincides with this test's context.
     * @throws IllegalArgumentException if <code>subject</code> is not a total morphism
     */
    public boolean matches(VarMorphism subject);
    
    /** 
     * Checks if this graph test is satisfied for a given subject.
     * This can only be the case if this test is ground.
     */
    public boolean matches(Graph graph);
    
    /**
     * Returns the outcome of this test for a given subject morphism.
     * @param subject the morphism to be tested
     * @ensure <code>result.test() == this && result.subject() == morph</code>
     * @throws IllegalArgumentException if <code>! morph.isTotal()</code> or <code>morph.dom() != getContext()</code>
     */
    public GraphTestOutcome<?,?> getOutcome(VarMorphism subject);
    
    public GraphTestOutcome<?,?> getOutcome(Graph subject);
}
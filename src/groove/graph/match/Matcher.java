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
 * $Id: Matcher.java,v 1.3 2007-04-04 07:04:28 rensink Exp $
 */
package groove.graph.match;

import java.util.Collection;
import java.util.Iterator;

import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.NodeEdgeMap;
import groove.util.Reporter;

/**
 * Interface for matching algorithms.
 * Offers functionality for constructing a total mapping (called
 * a <i>refinement</i>) from a given morphism.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface Matcher {
    /** 
     * Returns the domain of the underlying morphism.
     * @see #getMorphism() 
     */
    Graph dom();
    
    /** 
     * Returns the codomain of the underlying morphism. 
     * @see #getMorphism()
     */
    Graph cod();

    /** Returns the morphism that this simulation was initialised with. */
    Morphism getMorphism();
    
    /**
     * Returns a (partial) mapping from domain to codomain elements,
     * which is the basis for constructing the matching.
     */
    NodeEdgeMap getSingularMap();
    
    /**
     * Checks if this simulation has a (functional) refinement.
     * Convenience method for <tt>getRefinement() != null</tt>.
     * @see #getRefinement()
     */
    boolean hasRefinement();

    /**
     * Returns a (functional) refinement of this morphism, if it has any.
     * Returns <tt>null</tt> if the simulation has no refinements.
     * @see #getRefinementSet()
     */
    NodeEdgeMap getRefinement();

    /**
     * Returns the set of all (functional) refinements of this simulation.
     */
    Collection<NodeEdgeMap> getRefinementSet();

    /**
     * Returns an interator over the (functional) refinements of this simulation.
     * The result is the same as <tt>getRefinementSet().iterator()</tt>,
     * but the iterator returned by this method may operate in a lazy fashion.
     * @see #getRefinementSet()
     */
    Iterator<? extends NodeEdgeMap> getRefinementIter();
    
    /** Reporter instance to profile this interface. */
	public static Reporter reporter = new Reporter(Matcher.class);
}

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
 * $Id: Matching.java,v 1.3 2007-10-02 23:06:25 rensink Exp $
 */
package groove.trans;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import groove.graph.Graph;
import groove.graph.Morphism;
import groove.rel.VarMorphism;

/**
 * Interface for the matching morphism from a {@link GraphCondition}.
 * The added functionality is that a matching can be initialised as an
 * empty or partial morphism, and be extended from there to a total
 * morphism (called a <i>total extension</i> here) while checking the
 * nested sub-conditions. 
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 * @deprecated use {@link Match} instead
 */
@Deprecated
public interface Matching extends VarMorphism {
    /**
     * Returns the condition underlying this matching.
     * It is guaranteed that the domain of the matching equals condition's target pattern.
     * @ensure <tt>result.target() == dom()</tt>
     */
    public GraphCondition getCondition();

    /**
     * This method, which is already present in the super-interface {@link Morphism},
     * receives a special meaning here.
     * Since it is called during the matching process, any implementation must return a {@link Matching}
     * for the same {@link GraphCondition} as <code>this</code>. This implies that
     * there is an additional requirement, namely that the intended domain <code>dom</code> must
     * equal the context graph of this condition, given by {@link GraphTest#getContext()}.
     * The method should then act as a convenience method for 
     * <code>createMatching(getCondition(), cod)</code>.
     */
    public Morphism createMorphism(Graph dom, Graph cod);
    
    /**
     * Extends the super implementation by a check of the negated conjunct
     * of the graph condition.
     * @see GraphCondition#getNegConjunct()
     */
    public boolean hasTotalExtensions();

    /**
     * Extends the super implementation by a check of the negated conjunct
     * of the graph condition.
     * @see GraphCondition#getNegConjunct()
     */
    public Morphism getTotalExtension();

    /**
     * Extends the super implementation by a check of the negated conjunct
     * of the graph condition.
     * @see GraphCondition#getNegConjunct()
     */
    public Collection<? extends Matching> getTotalExtensions();

    /**
     * Extends the super implementation by a check of the negated conjunct
     * of the graph condition.
     * @see GraphCondition#getNegConjunct()
     */
    public Iterator<? extends Matching> getTotalExtensionsIter();

    /**
     * Returns a mapping from the total extensions of the underlying
     * morphism to the {@link GraphTestOutcome}s for the negated conjunct
     * of the graph condition.
     * This means that (in contrast to the {@link #getTotalExtensions()} method)
     * this method also records those total extensions of the underlying morphism
     * that <i>do</i> satisfy the negated conjunct.
     */
    public Map<Matching, GraphPredicateOutcome> getTotalExtensionMap();
}

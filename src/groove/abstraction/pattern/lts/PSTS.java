/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
 * $Id$
 */
package groove.abstraction.pattern.lts;

import groove.abstraction.pattern.explore.util.PatternRuleEventApplier;
import groove.abstraction.pattern.explore.util.PatternShapeMatchApplier;
import groove.abstraction.pattern.explore.util.PatternShapeMatchSetCollector;
import groove.abstraction.pattern.shape.PatternGraph;
import groove.abstraction.pattern.shape.PatternShape;
import groove.abstraction.pattern.shape.iso.PatternShapeIsoChecker;
import groove.abstraction.pattern.trans.PatternGraphGrammar;

/**
 * Pattern Shape Transition System.  
 * @author Eduardo Zambon
 */
public final class PSTS extends PGTS {

    /** Constructs a PGTS for the given grammar. */
    public PSTS(PatternGraphGrammar grammar) {
        super(grammar);
    }

    /** Callback factory method for the match applier. */
    @Override
    public PatternRuleEventApplier createMatchApplier() {
        return new PatternShapeMatchApplier(this);
    }

    /** Returns a fresh match collector for the given state. */
    @Override
    public PatternShapeMatchSetCollector createMatchCollector(PatternState state) {
        return new PatternShapeMatchSetCollector(state);
    }

    /** 
     * Returns a copy of the given graph with a fresh element factory.
     * The resulting graph will be used as start graph state.
     */
    @Override
    protected PatternGraph createStartGraph(PatternGraph startGraph) {
        PatternGraph result = new PatternShape(startGraph);
        return result;
    }

    /** Callback factory method for a state set. */
    /*@Override
    protected StateSet createStateSet() {
        return new ShapeStateSet();
    }*/

    // ------------------------------------------------------------------------
    // Inner classes
    // ------------------------------------------------------------------------

    /** Class to store the states of the PSTS. */
    private static final class ShapeStateSet extends PGTS.StateSet {

        /** Default constructor, delegates to super class. */
        ShapeStateSet() {
            super(PatternShapeIsoChecker.getInstance());
        }

    }

}

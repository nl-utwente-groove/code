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
 * $Id: AbstrStateGenerator.java,v 1.4 2008-02-05 13:28:29 rensink Exp $
 */
package groove.abs.lts;

import groove.abs.AbstrGraph;
import groove.abs.Abstraction;
import groove.abs.DefaultAbstrGraph;
import groove.explore.util.ExploreCache;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.StateGenerator;
import groove.trans.RuleEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A version of a {@link StateGenerator} to be used with abstract exploration.
 * @author Iovka Boneva
 * @version $Revision $
 */
public class AbstrStateGenerator extends StateGenerator {

    private Abstraction.Parameters options;

    /**
     * Creates a state generator for a given abstract graph transition system.
     * @param gts
     */
    public AbstrStateGenerator(AGTS gts, Abstraction.Parameters options) {
        super(gts);
        this.options = options;
    }

    /** Has no effect if options were already set. */
    public void setOptions(Abstraction.Parameters options) {
        if (this.options == null) {
            this.options = options;
        }
    }

    @Override
    /**
     * For abstract transformation, the application of a match may result in
     * several (or none) abstract graph states.
     */
    public Set<? extends GraphTransition> applyMatch(GraphState source,
            RuleEvent event, ExploreCache cache) {
        AbstrGraphState abstrSource = (AbstrGraphState) source;
        Set<GraphTransition> result = new HashSet<GraphTransition>();
        Collection<AbstrGraph> transfResult = new ArrayList<AbstrGraph>();
        // TODO this loop can't be right: transfResult is always empty 
        for (AbstrGraph transf : transfResult) {
            GraphTransition trans;
            if (transf != DefaultAbstrGraph.INVALID_AG) {
                AbstrGraphNextState newState =
                    new AbstrGraphNextStateImpl(transf, abstrSource, event);
                AbstrGraphState oldState =
                    (AbstrGraphState) getGTS().addState(newState);
                if (oldState != null) {
                    // the state was not added as an equivalent state existed
                    trans =
                        new AbstrGraphTransitionImpl(abstrSource, event,
                            oldState);
                } else {
                    // the state was added as a next-state
                    trans = newState;
                }
            } else {
                trans =
                    new AbstrGraphTransitionImpl(abstrSource, event,
                        AGTS.INVALID_STATE);
            }
            getGTS().addTransition(trans);
            result.add(trans);
        }
        return result;
    }

    @Override
    /** @require gts is of type {@link AGTS} */
    public void setGTS(GTS gts) {
        assert gts instanceof AGTS : "The transition system should be of type AGTS.";
        super.setGTS(gts);
    }

    @Override
    /** Specialises return type. */
    public AGTS getGTS() {
        return (AGTS) super.getGTS();
    }

}

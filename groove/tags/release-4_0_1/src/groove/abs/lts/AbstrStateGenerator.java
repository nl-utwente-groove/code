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
import groove.abs.AbstrTransformer;
import groove.abs.DefaultAbstrGraph;
import groove.control.Location;
import groove.explore.util.RuleEventApplier;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.RuleEvent;
import groove.trans.RuleMatch;
import groove.trans.SPOEvent;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A version of a {@link RuleEventApplier} to be used with abstract exploration.
 * @author Iovka Boneva
 * @version $Revision $
 */
public class AbstrStateGenerator implements RuleEventApplier {

    /**
     * Creates a state generator for a given abstract graph transition system.
     * @param gts
     */
    public AbstrStateGenerator(AGTS gts) {
        this.gts = gts;
    }

    /**
     * For abstract transformation, the application of a match may result in
     * several (or none) abstract graph states.
     */
    @Override
    public GraphTransition apply(GraphState source, RuleEvent event,
            Location targetLocation) {
        ShapeGraphState abstrSource = (ShapeGraphState) source;
        GraphTransition result = null;
        Collection<AbstrGraph> transfResult = new ArrayList<AbstrGraph>();

        // EDUARDO: Modified this part such that it actually performs the
        // transformation.
        AbstrGraph host = (AbstrGraph) source.getGraph();
        RuleMatch match = event.getMatch(host);
        AbstrTransformer.transform(host, match,
            ((SPOEvent) event).getNodeFactory(), getGTS().getParameters(),
            transfResult);

        for (AbstrGraph transf : transfResult) {
            GraphTransition trans;
            if (transf != DefaultAbstrGraph.INVALID_AG) {
                ShapeGraphNextState newState =
                    new ShapeGraphNextState(transf, abstrSource, event);
                ShapeGraphState oldState =
                    (ShapeGraphState) getGTS().addState(newState);
                if (oldState != null) {
                    // the state was not added as an equivalent state existed
                    trans =
                        new ShapeGraphTransition(abstrSource, event, oldState);
                } else {
                    // the state was added as a next-state
                    trans = newState;
                }
            } else {
                trans =
                    new ShapeGraphTransition(abstrSource, event,
                        AGTS.INVALID_STATE);
            }
            getGTS().addTransition(trans);
            result = trans;
        }
        return result;
    }

    /** Specialises return type. */
    @Override
    public AGTS getGTS() {
        return this.gts;
    }

    private final AGTS gts;
}

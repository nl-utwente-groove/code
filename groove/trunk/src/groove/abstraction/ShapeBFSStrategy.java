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
 * $Id$
 */
package groove.abstraction;

import groove.abstraction.lts.AGTS;
import groove.abstraction.lts.ShapeStateGenerator;
import groove.explore.strategy.AbstractStrategy;
import groove.explore.util.ExploreCache;
import groove.explore.util.RuleEventApplier;
import groove.lts.GraphState;
import groove.trans.RuleEvent;

import java.util.Iterator;

/**
 * Breadth-first search exploration strategy for abstract state spaces.
 * @author Eduardo Zambon
 */
public class ShapeBFSStrategy extends AbstractStrategy {

    /** A step of this strategy completely explores one state. */
    public boolean next() {
        if (this.getAtState() == null) {
            return false;
        }
        ExploreCache cache = this.getCache(false);
        for (RuleEvent match : this.createMatchCollector(cache).getMatchSet()) {
            this.applyEvent(match);
        }
        this.setClosed(this.getAtState());
        this.updateAtState();
        return true;
    }

    @Override
    protected void updateAtState() {
        Iterator<GraphState> stateIter = this.getGTS().getOpenStateIter();
        if (stateIter.hasNext()) {
            this.atState = stateIter.next();
        } else {
            this.atState = null;
        }
    }

    /** Returns the match applier of this strategy. */
    @Override
    protected RuleEventApplier getMatchApplier() {
        assert this.getGTS() instanceof AGTS;
        if (this.applier == null) {
            this.applier = new ShapeStateGenerator((AGTS) this.getGTS());
        }
        return this.applier;
    }

}

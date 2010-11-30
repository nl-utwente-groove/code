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
import groove.explore.strategy.NextOpenStrategy;
import groove.explore.util.RuleEventApplier;

/**
 * Breadth-first search exploration strategy for abstract state spaces.
 * @author Eduardo Zambon
 */
public class ShapeBFSStrategy extends NextOpenStrategy {
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

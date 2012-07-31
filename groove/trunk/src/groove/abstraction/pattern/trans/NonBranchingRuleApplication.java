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
package groove.abstraction.pattern.trans;

import groove.abstraction.pattern.match.PreMatch;
import groove.abstraction.pattern.shape.PatternShape;

import java.util.Collection;
import java.util.Collections;

/**
 * Application of a matched pattern graph transformation rule.
 * 
 * This class only manipulates the multiplicities of the pattern shape, hence
 * the name 'non-branching', i.e., no materialisation is performed. 
 * 
 * @author Eduardo Zambon
 */
public final class NonBranchingRuleApplication implements
        PatternShapeRuleApplication {

    private final PatternShape pShape;
    private final PatternRule pRule;
    @SuppressWarnings("unused")
    private final PreMatch match;

    /** Default constructor. */
    public NonBranchingRuleApplication(PatternShape pShape, PreMatch match) {
        this.pShape = pShape;
        this.match = match;
        this.pRule = match.getRule();
    }

    /** Executes the rule application and returns the result. */
    public Collection<PatternShape> transform() {
        PatternShape result;
        if (!this.pRule.isModifying()) {
            result = this.pShape;
        } else {
            result = transform(this.pShape.clone());
        }
        return Collections.singletonList(result);
    }

    /** Transforms and returns the given pattern shape.*/
    private PatternShape transform(PatternShape host) {
        // EZ says: implementation pending...
        return null;
    }
}

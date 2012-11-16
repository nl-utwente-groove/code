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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Application of a matched pattern graph transformation rule.
 * 
 * @author Eduardo Zambon
 */
public final class MaterialisingRuleApplication {

    private final PatternShape pShape;
    private final PreMatch match;

    /** Default constructor. */
    public MaterialisingRuleApplication(PatternShape pShape, PreMatch match) {
        this.pShape = pShape;
        this.match = match;
    }

    /** Executes the rule application and returns the result. */
    public Collection<PatternShape> transform() {
        List<PatternShape> result = new ArrayList<PatternShape>();
        for (Materialisation mat : Materialisation.getMaterialisations(
            this.pShape, this.match)) {
            result.add(applyMatch(mat));
        }
        return result;
    }

    /** Executes the rule application for the given materialisation. */
    public static PatternShape applyMatch(Materialisation mat) {
        PatternGraphRuleApplication app =
            new PatternGraphRuleApplication(mat.getShape(), mat.getMatch());
        return (PatternShape) app.transform(true);
    }
}

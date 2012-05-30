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

import groove.abstraction.pattern.match.Match;
import groove.abstraction.pattern.shape.PatternGraph;

/**
 * Application of a matched pattern graph transformation rule.
 * 
 * @author Eduardo Zambon
 */
public final class PatternRuleApplication {

    private final PatternGraph pGraph;
    private final PatternRule pRule;
    private final Match match;

    /** Default constructor. */
    public PatternRuleApplication(PatternGraph pGraph, Match match) {
        this.pGraph = pGraph;
        this.match = match;
        this.pRule = match.getRule();
    }

    /** Executes the rule application and returns the result. */
    public PatternGraph transform(boolean inPlace) {
        if (inPlace) {
            return transform(this.pGraph);
        } else {
            return transform(this.pGraph.clone());
        }
    }

    private PatternGraph transform(PatternGraph host) {
        return host;
    }
}

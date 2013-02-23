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
 * $Id$
 */
package groove.abstraction.pattern.lts;

import groove.graph.ALabel;
import groove.lts.RuleTransitionLabel;

/**
 * Class of labels that can appear on pattern rule transitions.
 * 
 * See {@link RuleTransitionLabel}. 
 */
public class PatternTransitionLabel extends ALabel {

    private final String text;
    private final MatchResult match;

    /** Constructs a new label on the basis of a given match. */
    PatternTransitionLabel(MatchResult match) {
        this.match = match;
        this.text = match.getMatch().getRule().getName();
    }

    @Override
    public String text() {
        return this.text;
    }

    @Override
    protected int computeHashCode() {
        final int prime = 31;
        int result = super.computeHashCode();
        result = prime * result + this.match.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PatternTransitionLabel other = (PatternTransitionLabel) obj;
        if (!this.text.equals(other.text)) {
            return false;
        } else if (!this.match.equals(other.match)) {
            return false;
        }
        return true;
    }

}

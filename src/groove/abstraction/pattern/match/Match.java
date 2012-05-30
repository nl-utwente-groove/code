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
package groove.abstraction.pattern.match;

import groove.abstraction.pattern.shape.PatternEdge;
import groove.abstraction.pattern.shape.PatternFactory;
import groove.abstraction.pattern.shape.PatternNode;
import groove.abstraction.pattern.trans.PatternRule;
import groove.abstraction.pattern.trans.RuleEdge;
import groove.abstraction.pattern.trans.RuleNode;
import groove.graph.InversableElementMap;

/**
 * Match result of a pattern graph rule.
 * 
 * @author Eduardo Zambon
 */
public final class Match extends
        InversableElementMap<RuleNode,RuleEdge,PatternNode,PatternEdge> {

    /** Default constructor. */
    public Match(PatternFactory factory) {
        super(factory);
    }

    /** Returns the rule matched in this object. */
    public PatternRule getRule() {
        return null;
    }

}

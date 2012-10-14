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
import groove.abstraction.pattern.shape.PatternGraph;
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
public class Match extends
        InversableElementMap<RuleNode,RuleEdge,PatternNode,PatternEdge> {

    private final PatternRule pRule;
    private final PatternGraph pGraph;

    /** Default constructor. */
    public Match(PatternRule pRule, PatternGraph pGraph) {
        super(pGraph.getFactory());
        this.pRule = pRule;
        this.pGraph = pGraph;
    }

    /** Copying constructor. */
    private Match(Match match) {
        super(match.getFactory());
        this.pRule = match.pRule;
        this.pGraph = match.pGraph;
        putAll(match);
    }

    /** Returns the rule matched in this object. */
    public PatternRule getRule() {
        return this.pRule;
    }

    /** Returns the pattern graph matched. */
    public PatternGraph getGraph() {
        return this.pGraph;
    }

    /** Returns true if the match is complete. */
    public boolean isFinished() {
        return nodeMap().keySet().containsAll(this.pRule.lhs().nodeSet())
            && edgeMap().keySet().containsAll(this.pRule.lhs().edgeSet());
    }

    /** Returns true if this match is injective. */
    public boolean isValid() {
        return isInjective();
    }

    @Override
    public String toString() {
        return "Match of " + this.pRule.getName() + ":\n" + super.toString();
    }

    @Override
    public Match clone() {
        return new Match(this);
    }

}

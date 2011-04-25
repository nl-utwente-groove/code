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
 * $Id: ForallCondition.java,v 1.10 2007-11-29 12:52:09 rensink Exp $
 */
package groove.trans;

import groove.graph.algebra.VariableNode;

/**
 * Universally matched condition.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ForallCondition extends Condition {
    /**
     * Constructs an instance based on a given pattern graph and root map. 
     * @param countNode node specifying the number of matches of this condition.
     */
    public ForallCondition(RuleName name, RuleGraph pattern,
            RuleGraph rootGraph, SystemProperties properties,
            VariableNode countNode) {
        super(name, pattern, rootGraph, properties);
        this.countNode = countNode;
    }

    @Override
    public void addSubCondition(Condition condition) {
        // sub-conditions of universal conditions must be rules or negatives
        assert !(condition instanceof ForallCondition);
        super.addSubCondition(condition);
    }

    @Override
    public String toString() {
        return "Universal " + super.toString();
    }

    /** Returns the match count node of this universal condition, if any. */
    public RuleNode getCountNode() {
        return this.countNode;
    }

    /** Sets this universal condition to positive (meaning that
     * it should have at least one match). */
    public void setPositive() {
        this.positive = true;
    }

    /**
     * Indicates if this condition is positive. A universal condition is
     * positive if it cannot be vacuously fulfilled; i.e., there must always be
     * at least one match.
     */
    public boolean isPositive() {
        return this.positive;
    }

    /** Node capturing the match count of this condition. */
    private final RuleNode countNode;
    /**
     * Flag indicating whether the condition is positive, i.e., cannot be
     * vacuously true.
     */
    private boolean positive;
}

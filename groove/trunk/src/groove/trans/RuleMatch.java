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
 * $Id: RuleMatch.java,v 1.2 2007-10-03 23:10:54 rensink Exp $
 */
package groove.trans;

import groove.graph.NodeFactory;
import groove.rel.VarNodeEdgeMap;

/**
 * Match of an {@link SPORule}.
 * @author Arend Rensink
 * @version $Revision $
 */
public class RuleMatch extends ExistsMatch {
    /** Constructs a match for a given {@link SPORule}. */
    public RuleMatch(SPORule rule, VarNodeEdgeMap elementMap) {
    	super(elementMap);
        this.rule = rule;
    }
    
    /** Returns the rule of which this is a match. */
    public SPORule getRule() {
        return rule;
    }

    /** Creates an event on the basis of this match. */
    public SPOEvent newEvent(NodeFactory nodeFactory, boolean reuse) {
        return new SPOEvent(getRule(), getMatchMap(), nodeFactory, reuse);
    }
    
    /** Equality is determined by rule and element map. */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof RuleMatch
                && ((RuleMatch) obj).getRule().equals(getRule())
                && super.equals(obj);
    }
    
    /** This implementation takes the rule into account. */
    @Override
    protected int computeHashCode() {
        return getRule().hashCode() + super.computeHashCode();
    }

    @Override
    public String toString() {
        return String.format("Match of %s: Nodes %s, edges %s", getRule(), getMatchMap().nodeMap(), getMatchMap().edgeMap());
    }
    
    /** The fixed rule of which this is a match. */
    private final SPORule rule;
}

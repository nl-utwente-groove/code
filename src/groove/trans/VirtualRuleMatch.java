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
 * $$Id$$
 */
package groove.trans;

import groove.graph.Edge;
import groove.graph.Node;
import groove.graph.NodeFactory;
import groove.rel.VarNodeEdgeMap;

import java.util.Collection;

/**
 * Virtual RuleMatch used for aliasing. If a RuleEvent can be reused, it is captures and wrapped
 * in a VirtualRuleMatch together with the source graph, so that strategies can remain unknown
 * of this reuse. SystemRecord will collect the reused event instead of a calling newEvent on the 
 * RuleMatch in {@link SystemRecord#getEvent(RuleMatch)}.
 * 
 * @author Staijen
 *
 */
public class VirtualRuleMatch extends RuleMatch {

	private RuleApplication appl;
	
	public VirtualRuleMatch(RuleApplication appl) {
		super(null,null);
		this.aliasCount++;
		this.appl = appl;
	}
	
	public RuleApplication getApplication() {
		return appl;
	}
	
	public RuleEvent getEvent() {
		return this.appl.getEvent();
	}

	private RuleMatch getMatch() {
		return appl.getMatch();
	}
	
	@Override
	public Collection<RuleMatch> addSubMatchChoice(Iterable<? extends Match> choices) {
		return getMatch().addSubMatchChoice(choices);
	}

	@Override
	protected int computeHashCode() {
		return getMatch().computeHashCode();
	}

	@Override
	protected RuleMatch createMatch() {
		return getMatch().createMatch();
	}

	@Override
	public boolean equals(Object obj) {
		return getMatch().equals(obj);
	}

	@Override
	public SPORule getRule() {
		Rule rule = appl.getRule();
		if( rule instanceof SPORule ) {
			return (SPORule) rule;
		}
		else {
			return getMatch().getRule();
		}
	}

	@Override
	public RuleEvent newEvent(NodeFactory nodeFactory, boolean reuse) {
		return appl.getEvent();
	}

	@Override
	public String toString() {
		return "Virtual match of appl " + appl;
	}

	@Override
	public void addSubMatch(Match match) {
		getMatch().addSubMatch(match);
	}

	@Override
	protected CompositeMatch clone() {
		return getMatch().clone();
	}

	@Override
	public Collection<Edge> getEdgeValues() {
		return getMatch().getEdgeValues();
	}

	@Override
	public VarNodeEdgeMap getElementMap() {
		return getMatch().getElementMap();
	}

	@Override
	public Collection<Node> getNodeValues() {
		return getMatch().getNodeValues();
	}

	@Override
	public Collection<Match> getSubMatches() {
		return getMatch().getSubMatches();
	}

	@Override
	public int hashCode() {
		return getMatch().hashCode();
	}
	
    /** Returns the total number of true application aliases created. */
    public static int getAliasCount() {
        return VirtualRuleMatch.aliasCount;
    }

    /** Counter for the number of true application aliases created. */
    private static int aliasCount = 0;
	
}

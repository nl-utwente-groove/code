package groove.trans;

import java.util.Collection;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.NodeFactory;
import groove.rel.VarNodeEdgeMap;

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
		// TODO Auto-generated method stub
		return getMatch().addSubMatchChoice(choices);
	}

	@Override
	protected int computeHashCode() {
		// TODO Auto-generated method stub
		return getMatch().computeHashCode();
	}

	@Override
	protected RuleMatch createMatch() {
		// TODO Auto-generated method stub
		return getMatch().createMatch();
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return getMatch().equals(obj);
	}

	@Override
	public SPORule getRule() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return "Virtual match of appl " + appl;
	}

	@Override
	public void addSubMatch(Match match) {
		// TODO Auto-generated method stub
		getMatch().addSubMatch(match);
	}

	@Override
	protected CompositeMatch clone() {
		// TODO Auto-generated method stub
		return getMatch().clone();
	}

	@Override
	public Collection<Edge> getEdgeValues() {
		// TODO Auto-generated method stub
		return getMatch().getEdgeValues();
	}

	@Override
	public VarNodeEdgeMap getElementMap() {
		// TODO Auto-generated method stub
		return getMatch().getElementMap();
	}

	@Override
	public Collection<Node> getNodeValues() {
		// TODO Auto-generated method stub
		return getMatch().getNodeValues();
	}

	@Override
	public Collection<Match> getSubMatches() {
		// TODO Auto-generated method stub
		return getMatch().getSubMatches();
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return getMatch().hashCode();
	}
	
    /** Returns the total number of true application aliases created. */
    public static int getAliasCount() {
        return VirtualRuleMatch.aliasCount;
    }

    /** Counter for the number of true application aliases created. */
    private static int aliasCount = 0;
	
}

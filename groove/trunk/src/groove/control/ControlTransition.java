package groove.control;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.lts.Transition;
import groove.trans.Rule;
import groove.trans.RuleNameLabel;

/**
 * @author Staijen
 * 
 * Represents a transition in a control automaton, which is unique by its source, target and associated Rule.
 * 
 */
public class ControlTransition implements Transition {
	
	private Rule rule;
	private ControlState source;
	private ControlState target;
	private String ruleName;
	
	private ControlTransition visibleParent;
	
	/**
	 * @param source
	 * @param target
	 * @param rule is the Rule associated with this transition
	 */
	public ControlTransition(ControlState source, ControlState target, String ruleName)
	{
		this.source = source;
		this.target = target;
		this.ruleName = ruleName;
	}
	
	public String ruleName() {
		return this.ruleName;
	}
	
	public void setRule(Rule rule)
	{
		this.rule = rule;
	}
	
	/**
	 * @return the Rule associated with this transition in the Control Automaton
	 */
	public Rule rule()
	{
		return rule;
	}

	public Label label() {
		if( rule == null )
			return new RuleNameLabel(this.ruleName);
		else
			return rule.getName();
	}
	
	/**
	 * @return priority of this transition, which equals the priority of the associated rule
	 */
	public int getPriority()
	{
		return this.rule.getPriority();
	}
	
	public ControlState source() {
		// TODO Auto-generated method stub
		return source;
	}

	public ControlState target() {
		// TODO Auto-generated method stub
		return target;
	}
	
	public Node end(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	public int endCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int endIndex(Node node) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Node[] ends() {
		// TODO Auto-generated method stub
		return new Node[]{target()};
	}

	public boolean hasEnd(Node node) {
		// TODO Auto-generated method stub
		return false;
	}

	public Edge imageFor(NodeEdgeMap elementMap) {
		throw new UnsupportedOperationException("Transition images are currenty not supported");
	}
	
	public Node opposite() {
		// TODO Auto-generated method stub
		return target();
	}

	public int compareTo(Element obj) {
		if (obj instanceof ControlState) {
            // for states, we just need to look at the source of this transition
            if (source().equals(obj)) {
                return +1;
            } else {
                return source().compareTo(obj);
            }
        } else {
            Edge other = (Edge) obj;
            if (!source().equals(other.source())) {
                return source().compareTo(other.source());
            }
            // for other edges, first the end count, then the label, then the other ends
            if (endCount() != other.endCount()) {
                return endCount() - other.endCount();
            }
            if (!label().equals(other.label())) {
                return label().compareTo(other.label());
            }
            for (int i = 1; i < endCount(); i++) {
                if (!end(i).equals(other.end(i))) {
                    return end(i).compareTo(other.end(i));
                }
            }
            return 0;
        }
	}
	
	public void setVisibleParent(ControlTransition parent) {
		this.visibleParent = parent;
	}
	
	public ControlTransition getVisibleParent() {
		return this.visibleParent;
	}
		
}

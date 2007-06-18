package groove.control;

import groove.graph.Label;
import groove.trans.Rule;

/**
 * A ControlTransition associated with a Rule in a RuleSystem. 
 * @author Staijen
 *
 */
public class RuleControlTransition extends AbstractControlTransition {
	private Rule rule;
	
	/**
	 * @param source
	 * @param target
	 * @param rule is the Rule associated with this transition
	 */
	public RuleControlTransition(ControlState source, ControlState target, Rule rule)
	{
		super(source, target);
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
		return rule.getName();
	}
	
	/**
	 * @return priority of this transition, which equals the priority of the associated rule
	 */
	public int getPriority()
	{
		return this.rule.getPriority();
	}
}

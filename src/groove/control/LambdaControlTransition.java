package groove.control;

import groove.graph.Label;
import groove.trans.NameLabel;

public class LambdaControlTransition extends AbstractControlTransition {

	public LambdaControlTransition(ControlState source, ControlState target)
	{
		super(source, target);
	}
	
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Label label() {
		return new NameLabel(ControlAutomaton.LAMBDA);
	}
	

	
}

package groove.control;

import groove.graph.Label;
import groove.trans.NameLabel;

public class ElseControlTransition extends AbstractControlTransition {

	public ElseControlTransition(ControlState source, ControlState target) {
		super(source, target);
	}
	
	public int getPriority() {
		// TODO Auto-generated method stub
		return -1;
	}

	public Label label() {
		// TODO Auto-generated method stub
		return new NameLabel(ControlAutomaton.ELSE);
	}

}

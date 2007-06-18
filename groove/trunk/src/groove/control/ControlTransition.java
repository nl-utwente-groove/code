package groove.control;

import groove.lts.Transition;

public interface ControlTransition extends Transition {

	public ControlState source();
	public ControlState target();
	
	public int getPriority();
}

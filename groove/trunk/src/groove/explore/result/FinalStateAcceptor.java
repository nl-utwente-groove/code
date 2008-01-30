package groove.explore.result;


import groove.lts.GraphState;
import groove.lts.LTS;
import groove.lts.State;


/**
 * Accepts final states.
 */
public class FinalStateAcceptor extends Acceptor<GraphState> {

	@Override
	public void closeUpdate(LTS gts, State state) {
		if(gts.isFinal(state)) {
			this.getResult().add((GraphState) state);
		}
	}
	
}

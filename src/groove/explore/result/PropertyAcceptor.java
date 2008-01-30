package groove.explore.result;

import groove.lts.GraphState;
import groove.lts.LTS;
import groove.lts.State;
import groove.util.Property;

/**
 * Acceptor that accepts states that satisfy a certain property.
 * 
 * @author Staijen
 *
 * @param <GraphState>
 */
public class PropertyAcceptor extends Acceptor<GraphState> {

	Property<GraphState> property;
	
	public PropertyAcceptor(Property<GraphState> property) {
		super();
		this.property = property;
	}
	
	@Override
	public void closeUpdate(LTS graph, State explored) {
		if( property.isSatisfied((GraphState) explored)) {
			getResult().add((GraphState) explored);
		}
	}

}

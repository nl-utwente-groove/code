/* $Id: StartGraphState.java,v 1.4 2007-09-18 15:14:33 rensink Exp $ */
package groove.lts;

import groove.control.Location;
import groove.graph.Graph;
import groove.graph.GraphInfo;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class StartGraphState extends AbstractGraphState {
	/** 
	 * Creates a start state based on a given graph, with <code>null</code>
	 * control location.
	 */
	public StartGraphState(Graph graph) {
		setFrozenGraph(getCache().computeFrozenGraph(graph));
		this.graph = getCache().getGraph();
		GraphInfo.transfer(graph, this.graph, null);
	}
	
	/** Creates a start state based on a given graph and control location. */
	public StartGraphState(Graph graph, Location control) {
		super(control);
		setFrozenGraph(getCache().computeFrozenGraph(graph));
	}
	
	@Override
	public Graph getGraph() {
		if (graph == null) {
			graph = getCache().getGraph();
		}
		return graph;
	}

	@Override
	protected void updateClosed() {
		// empty
	}
	
	/** The stored graph. */
	private Graph graph;
}

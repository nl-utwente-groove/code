/* $Id: StartGraphState.java,v 1.7 2008-01-30 09:32:19 iovka Exp $ */
package groove.lts;

import groove.control.Location;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.trans.SystemRecord;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class StartGraphState extends AbstractGraphState {
	/** 
	 * Creates a start state based on a given system record and start graph, with <code>null</code>
	 * control location.
	 */
	public StartGraphState(SystemRecord record, Graph graph) {
		this(record, graph, null);
	}
	
	/** Creates a start state based on a given system record, start graph and control location. */
	public StartGraphState(SystemRecord record, Graph graph, Location control) {
		super(StateReference.newInstance(record), control);
		setFrozenGraph(getCache().computeFrozenGraph(graph));
		this.graph = getCache().getGraph();
		GraphInfo.transfer(graph, this.graph, null);
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

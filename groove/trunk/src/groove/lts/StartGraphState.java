/* $Id: StartGraphState.java,v 1.1 2007-04-27 22:06:25 rensink Exp $ */
package groove.lts;

import groove.graph.Graph;

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
	}
	
	/** Creates a start state based on a given graph and control location. */
	public StartGraphState(Graph graph, Object control) {
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
//	
//	DeltaApplier getDelta(final Graph graph) {
//		return new DeltaApplier() {
//
//			public void applyDelta(DeltaTarget target, int mode) {
//				if (mode != EDGES_ONLY) {
//					for (Node node: graph.nodeSet()) {
//						target.addNode(node);
//					}
//				}
//				if (mode != NODES_ONLY) {
//					for (Edge edge: graph.edgeSet()) {
//						target.addEdge(edge);
//					}
//				}
//				
//			}
//
//			public void applyDelta(DeltaTarget target) {
//				for (Node node: graph.nodeSet()) {
//					target.addNode(node);
//				}
//				for (Edge edge: graph.edgeSet()) {
//					target.addEdge(edge);
//				}
//			}
//		};
//	}

	/** The stored graph. */
	private Graph graph;
}

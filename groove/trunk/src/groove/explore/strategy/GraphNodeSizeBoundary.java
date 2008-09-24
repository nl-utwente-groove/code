/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id: GraphNodeSizeBoundary.java,v 1.2 2008-02-20 08:37:54 kastenberg Exp $
 */
package groove.explore.strategy;

import groove.graph.Graph;
import groove.lts.ProductTransition;
import groove.verify.ModelChecking;

/**
 * Implementation of interface {@link Boundary} that
 * bases the boundary on the node-count of the graph reached
 * by the given transition.
 * 
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2008-02-20 08:37:54 $
 */
public class GraphNodeSizeBoundary extends AbstractBoundary {

	/**
	 * {@link GraphNodeSizeBoundary} constructor.
	 * @param graphSizeBoundary value that defined the boundary
	 */
	public GraphNodeSizeBoundary(int graphSizeBoundary, int step) {
		this.graphSizeBoundary = graphSizeBoundary;
		this.step = step;
	}

	/* (non-Javadoc)
	 * @see groove.explore.strategy.Boundary#crossingBoundary(groove.lts.GraphTransition)
	 */
	public boolean crossingBoundary(ProductTransition transition, boolean traverse) {
		boolean result = transition.target().getGraph().nodeCount() > graphSizeBoundary; 
		return result;
	}

	public boolean crossingBoundary(Graph graph) {
		return graph.nodeCount() > graphSizeBoundary;
	}

	/* (non-Javadoc)
	 * @see groove.explore.strategy.Boundary#increase()
	 */
	public void increase() {
		this.graphSizeBoundary += this.step;
	}

	public void increaseDepth() {
		// do nothing
	}

	public void decreaseDepth() {
		// do nothing
	}

	public int currentDepth() {
		// with graph-size boundaries, boundary-crossing
		// transitions are never allowed
		return ModelChecking.CURRENT_ITERATION;
	}

	/** the graph-size of graphs allowed for exploration */
	private int graphSizeBoundary;
	/** the value with which to increase the boundary with */
	private int step;
}

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
 * $Id: AspectualView.java,v 1.6 2007-08-26 07:24:09 rensink Exp $
 */
package groove.view;

import groove.graph.GraphInfo;
import groove.graph.GraphShape;
import groove.graph.NodeEdgeMap;
import groove.trans.RuleNameLabel;
import groove.view.aspect.AspectGraph;


/**
 * View specialisation based on aspect graphs.
 * Apart from the aspect graph itself, an instance of this view maintains a map from
 * the nodes of the aspect graph to nodes in the model. This can be useful for
 * traceability.
 * The model may for instance be a transformation rule, an attributed graph,
 * or a graph condition.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class AspectualView<Model> implements View<Model> {
	/**
	 * Returns the aspect graph representation of this view.
	 */
	abstract public AspectGraph getAspectGraph();
	
	/**
	 * Returns a mapping from the nodes in the aspect graph view to the
	 * corresponding nodes in the model that is being viewed.
	 */
	abstract public NodeEdgeMap getMap();

	/** 
	 * Creates a view from a given aspect graph.
	 * Depending on the role fo the graph, the result is an {@link AspectualRuleView} or
	 * an {@link AspectualGraphView}.
	 * @param aspectGraph the graph to create the view from
	 * @return a graph or rule view based on <code>aspectGraph</code>
	 * @see GraphInfo#getRole(GraphShape)
	 */
	static public AspectualView<?> createView(AspectGraph aspectGraph) {
		if (GraphInfo.hasRuleRole(aspectGraph)) {
			return new AspectualRuleView(aspectGraph, new RuleNameLabel(GraphInfo.getName(aspectGraph)));
		} else {
			return new AspectualGraphView(aspectGraph);
		}
	}
}

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
 * $Id: AttributedRuleGraph.java,v 1.5 2007-03-30 15:50:37 rensink Exp $
 */
package groove.trans.view;

import groove.algebra.Operation;
import groove.algebra.UnknownSymbolException;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphFormatException;
import groove.graph.GraphShape;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.graph.algebra.AlgebraConstants;
import groove.graph.algebra.AttributeEdge;
import groove.graph.algebra.ProductEdge;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.ValueNode;
import groove.trans.NameLabel;
import groove.trans.Rule;
import groove.trans.RuleFactory;

/**
 * Specialization of {@link groove.trans.view.RuleGraph} for creating rules with
 * NACs for attributed graphs.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.5 $ $Date: 2007-03-30 15:50:37 $
 * @deprecated replaced by {@link AspectualRuleView}
 */
@Deprecated
public class AttributedRuleGraph extends RuleGraph {
    /**
     * Constructs a new attributed rule graph on the basis of a given production rule.
     * @param rule the production rule for which a rule graph is to be constructed
     * @require <tt>rule != null</tt>
     * @throws RuleFormatException if <code>rule</code> cannot be displayed as a {@link RuleGraph},
     * for instance because its NACs are nested too deep or not connected
     */
    public AttributedRuleGraph(Rule rule) throws RuleFormatException {
        super(rule);
    }

    /**
     * Constructs a new attributed rule graph from a graph view. 
     * @param graph the graph from which to create the rule
     * @param name the name for this rule
     * @param priority the priority of this rule
     * @param ruleFactory the rule factory for this rule-graph
     * @throws GraphFormatException when the given graph does not fulfill the
     * requirements for making an attributed rule from it
     */
    public AttributedRuleGraph(GraphShape graph, NameLabel name, int priority, RuleFactory ruleFactory) throws GraphFormatException {
		super(graph, name, priority, ruleFactory);
	}

	/* (non-Javadoc)
	 * @see groove.trans.view.RuleGraph#addNode(groove.graph.Graph, groove.trans.view.RuleNode)
	 */
	@Override
	protected Node addNode(Graph graph, RuleNode node) {
		Node result = AlgebraConstants.getAlgebraNode(this, node);
		if (result != null) {
			graph.addNode(result);
			return result;
		}
		// if the node was not an algebra node
		return super.addNode(graph, node);
	}

	@Override
	protected Edge addEdge(Graph graph, Edge edge, NodeEdgeMap elementMap) {
		Node[] ends = images(elementMap.nodeMap(), edge.ends());
		// depending on the types of the source and target nodes we need
		// to create different edges
		Node source = ends[Edge.SOURCE_INDEX];
		Node target = ends[Edge.TARGET_INDEX];

		// edges that are only used for determining the role of nodes are not needed anymore
		if (AlgebraConstants.isAttributeLabel(edge.label()) || AlgebraConstants.isProductLabel(edge.label())) {
			return null;
		}

		// edges that are used for determining the specific data value are also not needed anymore
		if (source instanceof ValueNode && AlgebraConstants.labelType(edge.label()) != AlgebraConstants.NO_TYPE)
			return null;

		if (target instanceof ValueNode) {
			// edges that determine the operation applied on the tuples in product nodes need special care
			if (source instanceof ProductNode) {
				try {
					if (AlgebraConstants.isArgumentLabel(edge.label()) < 0) {
						Edge productEdge = new ProductEdge((ProductNode) source, (ValueNode) target, AlgebraConstants.toOperation(edge.label()));
						graph.addEdge(productEdge);
						return productEdge;
					}
				} catch (UnknownSymbolException use) {
					// this should never happen because arriving at this point
					// means that the graph structure has been approved as
					// checked for in the isGraphStructuredCorrect-method
				}
			}
			else {
				Edge attributeEdge = new AttributeEdge(source, edge.label(), target);
				graph.addEdge(attributeEdge);
				return attributeEdge;
			}
		}

		// all other cases can be dealt with in the default manner
		return super.addEdge(graph, edge, elementMap);
	}

	/* (non-Javadoc)
	 * @see groove.trans.view.RuleGraph#isGraphStructureCorrect(groove.graph.Edge, groove.graph.GraphShape)
	 */
	@Override
	protected boolean isGraphStructureCorrect(Edge edge, GraphShape graph) throws GraphFormatException {
		boolean structureCorrect = true;
		String message = "";
		// depending on the types of the source and target nodes we need
		// to create different edges
		Node source = edge.end(Edge.SOURCE_INDEX);
		Node target = edge.end(Edge.TARGET_INDEX);
		Label label = edge.label();

		// if the given edge denotes a product-node, we have to do some checks...
		if (AlgebraConstants.isProductLabel(label)) {
			// keep track of the 
			int nrOfArguments = 0;
			// arguments of the product-node should be numbered subsequently
			int[] arguments = new int[graph.outEdgeSet(source).size() - 2];
			for (Edge outEdge: graph.outEdgeSet(source)) {
				int argument = AlgebraConstants.isArgumentLabel(outEdge.label());
				if (argument != AlgebraConstants.NO_ARGUMENT) {
					arguments[argument] = 1;
					nrOfArguments++;
				}
			}
			boolean subsequentArguments = true;
			for (int i : arguments) {
				if (!subsequentArguments && (i > 0)) {
					structureCorrect = false;
					message = "The arguments of the product node should be numbered subsequently (argument " + (i - 1) + " is missing).";
				}
				subsequentArguments = (i > 0);
			}

			// if the edge represents the algebraic operation to be applied
			// on the tuple, this operation should be a valid one
			for (Edge outEdge: graph.outEdgeSet(source)) {
				int algebraType = AlgebraConstants.labelType(outEdge.label());
				if (algebraType != AlgebraConstants.NO_TYPE) {
					try {
						Operation operation = AlgebraConstants.toOperation(outEdge.label());
						if (nrOfArguments != operation.arity()) {
							structureCorrect = false;
							message = "The number of arguments should equal the arity of the operation to be applied (" + operation.toString() + " with arity " + operation.arity() + ").";
						}
					} catch (UnknownSymbolException use) {
						structureCorrect = false;
						message = use.getMessage();
					}
				}
			}
		}

		if (AlgebraConstants.labelType(label) != AlgebraConstants.NO_TYPE) {
			try {
				Operation operation = AlgebraConstants.toOperation(label);
				if (operation.arity() == 0) {
					if (!(source.equals(target))) {
						structureCorrect = false;
						message = "Edges representing constant data values must be self-edges (" + edge + ")";
					}
					if (graph.outEdgeSet(source).size() > 1) {
						structureCorrect = false;
						message = "Nodes representing constant data values may not have outgoing edges other that the one determining the constant.";
					}
				}
			} catch (UnknownSymbolException use) {
				structureCorrect = false;
				message = use.getMessage();
			}
		}

		if (!structureCorrect) {
			throw new GraphFormatException(message);
		} else {
			return super.isGraphStructureCorrect(edge, graph);
		}
	}
}

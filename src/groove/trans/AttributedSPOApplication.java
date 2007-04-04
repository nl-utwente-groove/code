// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/* 
 * $Id: AttributedSPOApplication.java,v 1.4 2007-04-04 07:04:20 rensink Exp $
 */
package groove.trans;

import groove.graph.DeltaTarget;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.algebra.ValueNode;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Class representing the application of a {@link groove.trans.AttributedSPORule} to a graph. 
 * @author Harmen Kastenberg
 * @version $Revision: 1.4 $ $Date: 2007-04-04 07:04:20 $
 * @deprecated all functionality now in {@link SPOApplication}
 */
@Deprecated
public class AttributedSPOApplication extends SPOApplication {

	/**
	 * Constructor.
	 * @param event the rule-event on which this rule application is based
	 * @param source the source graph
	 * @param ruleFactory the rule factory used for doing the things correctly
	 */
	AttributedSPOApplication(SPOEvent event, Graph source, RuleFactory ruleFactory) {
		super(event, source);
		removalCandidates = new ArrayList<ValueNode>();
	}

	/**
     * This implementation constructs the target lazily.
     * If the rule is not modifying, the source is aliased.
     */
	@Override
    public Graph getTarget() {
        if (target == null) {
			if (rule.isModifying()) {
				target = computeTarget();
				// AREND why not put this in computeTarget or applyDelta?
				// then getTarget would not need to be overridden
				removeUnreferencedDataNodes();
				target.setFixed();
			} else {
				target = source;
			}
		}
		return target;
    }

    /**
	 * Modifies the super method by not calling {@link Graph#setFixed()} on it.
	 */
    @Override
	protected Graph computeTarget() {
		Graph target = createTarget();
		applyDelta(target);
		return target;
	}
//
//	/**
//     * Modifies the super method by ignoring value nodes.
//     */
//	@Override
//    protected Morphism computeMorphism() {
//    	Morphism result = createMorphism();
//    	NodeEdgeMap mergeMap = getMergeMap();
//    	for (Node node: source.nodeSet()) {
//			Node nodeImage = mergeMap.getNode(node);
//			if (nodeImage != null) {
//				// data-nodes should always be mapped to themselves, also when they are currently
//				// not explicitely in the graph, but since we remove them for practical reasons
//				// (checking for isomorphism would be more involved) we also do not put the deleted
//				// data-nodes to the morphism
//				if (!(getTarget().containsElement(node)) && node instanceof ValueNode) {
////					result.put(node, nodeImage);
//				} else {
//	                assert getTarget().containsElement(nodeImage) : "Node "+nodeImage+" not in node set "+getTarget().nodeSet()+" of "+getTarget();
//					result.putNode(node, nodeImage);
//				}
//			}
//		}
//    	Set<Edge> erasedEdges = getErasedEdges();
//    	for (Edge edge: source.edgeSet()) {
//			if (!erasedEdges.contains(edge)) {
//				Edge edgeImage = edge.imageFor(mergeMap);
//				if (edgeImage != null) {
//                    assert getTarget().containsElement(edgeImage) : "Edge "+edgeImage+" not in edge set "+getTarget().edgeSet()+" of "+getTarget();
//					result.putEdge(edge, edgeImage);
//				}
//			}
//		}
//		return result;
//    }
//
//	/**
//	 * Modifies the super method by also adding 
//	 * the edge target if it is a {@link ValueNode}.
//	 */
//    @Override
//	protected void addEdge(DeltaTarget target, Edge edge) {
//    	super.addEdge(target, edge);
//    	if (edge != null && edge.opposite() instanceof ValueNode) { 
//    		// value nodes may fail to be in the target
//    		target.addNode(edge.opposite());
//    	}
//	}

	/**
	 * In addition to calling the super method,
	 * registers the target node for erasure in case it is a {@link ValueNode}.
	 * @param target the target to which to apply the changes
	 */
	@Override
    protected void eraseEdges(DeltaTarget target) {
        for (Edge erasedEdge: getErasedEdges()) {
        	if (erasedEdge.opposite() instanceof ValueNode) {
	            removalCandidates.add((ValueNode) erasedEdge.opposite());
        	}
            target.removeEdge(erasedEdge);
        }
	}

    /**
     * Removes the data nodes that are not referenced in the target graph.
     * Probably, this can be done more efficient by directly asking for the edges incident
     * to the candidate nodes.
     */
    private void removeUnreferencedDataNodes() {
    	for (ValueNode nextCandidate : removalCandidates) {
			if (getTarget().edgeSet(nextCandidate).isEmpty())
				target.removeNode(nextCandidate);
		}
    }

    /**
	 * A collection of nodes representing algebraic data values that may have
	 * become unreferenced after this rule application
	 */
    private Collection<ValueNode> removalCandidates;
}
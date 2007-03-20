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
 * $Id: AttributedSPOApplication.java,v 1.1.1.1 2007-03-20 10:05:19 kastenberg Exp $
 */
package groove.trans;

import groove.graph.DeltaTarget;
import groove.graph.Edge;
import groove.graph.NodeEdgeMap;
import groove.graph.Graph;
import groove.graph.InternalGraph;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.algebra.AttributeEdge;
import groove.graph.algebra.ValueNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Class representing the application of a {@link groove.trans.AttributedSPORule} to a graph. 
 * @author Harmen Kastenberg
 * @version $Revision: 1.1.1.1 $ $Date: 2007-03-20 10:05:19 $
 */
public class AttributedSPOApplication extends SPOApplication {

	/**
	 * Constructor.
	 * @param event the rule-event on which this rule application is based
	 * @param source the source graph
	 * @param ruleFactory the rule factory used for doing the things correctly
	 */
	AttributedSPOApplication(SPOEvent event, Graph source, RuleFactory ruleFactory) {
		super(event, source, ruleFactory);
		removalCandidates = new ArrayList<ValueNode>();
	}

	/**
     * This implementation constructs the target lazily.
     * If the rule is not modifying, the source is aliased.
     */
    public Graph getTarget() {
        if (target == null) {
			if (rule.isModifying()) {
				target = computeTarget();
				removeUnreferencedDataNodes();
				target.setFixed();
			} else {
				target = source;
			}
		}
		return target;
    }

    /**
	 * Callback factory method to compute a target for this applier.
	 */
	protected Graph computeTarget() {
		Graph target = createTarget();
		applyDelta(target);
		return target;
	}

	/**
     * Constructs the morphism between source and target graph from the application.
     */
    protected Morphism computeMorphism() {
    	Morphism result = createMorphism();
    	NodeEdgeMap mergeMap = getMergeMap();
    	for (Node node: source.nodeSet()) {
			Node nodeImage = mergeMap.getNode(node);
			if (nodeImage != null) {
				// data-nodes should always be mapped to themselves, also when they are currently
				// not explicitely in the graph, but since we remove them for practical reasons
				// (checking for isomorphism would be more involved) we also do not put the deleted
				// data-nodes to the morphism
				if (!(getTarget().containsElement(node)) && node instanceof ValueNode) {
//					result.put(node, nodeImage);
				} else {
	                assert getTarget().containsElement(nodeImage) : "Node "+nodeImage+" not in node set "+getTarget().nodeSet()+" of "+getTarget();
					result.putNode(node, nodeImage);
				}
			}
		}
    	Set<Edge> erasedEdges = getErasedEdges();
    	for (Edge edge: source.edgeSet()) {
			if (!erasedEdges.contains(edge)) {
				Edge edgeImage = edge.imageFor(mergeMap);
				if (edgeImage != null) {
                    assert getTarget().containsElement(edgeImage) : "Edge "+edgeImage+" not in edge set "+getTarget().edgeSet()+" of "+getTarget();
					result.putEdge(edge, edgeImage);
				}
			}
		}
		return result;
    }

	/**
	 * Adds an edge to a delta target, if the edge
	 * is not <code>null</code> and not already in the source graph.
	 * Cannot try to call {@link InternalGraph#addEdgeWithoutCheck(Edge)}
	 * if the target is an {@link InternalGraph} since adding algebra-edges
	 * requires the check whether the value-node is in the graph or not.
	 */
	protected void addEdge(DeltaTarget target, Edge edge) {
		if (!(edge instanceof AttributeEdge)){
			super.addEdge(target, edge);
		} else {
			// algebra-edges require special attention
			// include the check whether the end-points are already
			// in the target
			target.addEdge(edge);
		}
	}

	/**
	 * Performs the edge erasure necessary according to the rule.
	 * @param target the target to which to apply the changes
	 */
    protected void eraseEdges(DeltaTarget target) {
        for (Object erasedEdge: getErasedEdges()) {
        	if (erasedEdge instanceof AttributeEdge) {
	            ValueNode targetNode = (ValueNode) ((AttributeEdge) erasedEdge).target();
	            removalCandidates.add(targetNode);
        	}
            target.removeEdge((Edge) erasedEdge);
        }
	}

    /**
     * Removes the data nodes that are not referenced in the target graph.
     * Probably, this can be done more efficient by directly asking for the edges incident
     * to the candidate nodes.
     */
    private void removeUnreferencedDataNodes() {
    	if (!removalCandidates.isEmpty()) {
    		Iterator<ValueNode> candidatesIter = removalCandidates.iterator();
    		while (candidatesIter.hasNext()) {
    			ValueNode nextCandidate = candidatesIter.next();
    			if (getTarget().edgeSet(nextCandidate).isEmpty())
                    target.removeNode(nextCandidate);
    		}
    	}
    }

    /**
     * A collection of nodes representing algebraic data values that may
     * have become unreferenced after this rule application
     */
    private Collection<ValueNode> removalCandidates;
}
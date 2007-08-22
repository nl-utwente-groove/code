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
 * $Id: NestedApplication2.java,v 1.1 2007-08-22 09:19:48 kastenberg Exp $
 */
package groove.nesting.rule;

import groove.graph.AbstractGraph;
import groove.graph.DefaultMorphism;
import groove.graph.DeltaTarget;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.FilteredDeltaTarget;
import groove.graph.Graph;
import groove.graph.InternalGraph;
import groove.graph.MergeMap;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.graph.algebra.ValueNode;
import groove.nesting.VarNodeEdgeMultiMap;
import groove.trans.RuleApplication;
import groove.trans.SPOEvent;
import groove.util.Reporter;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author kramor
 * @version 0.1 $Revision: 1.1 $ $Date: 2007-08-22 09:19:48 $
 */
public class NestedApplication2 extends NestedApplication {
    /**
     * Returns the number of nodes that were created during rule application.
     */
    static public int getFreshNodeCount() {
        return freshNodeCount;
    }

    /**
     * The total number of nodes (over all rules) created by {@link SPOEvent#createNode()}.
     */
    static int freshNodeCount;

    /**
     * Constructs a new derivation on the basis of a given production rule, host graph and rule factory.
     * @param event the production rule instance involved
     * @param source the host graph to which the rule is to be applied
     */
    public NestedApplication2(NestedEvent event, Graph source) {
    	this.event = event;
        this.rule = event.getRule();
        this.source = source;
        this.anchorMap = event.getAnchorMap();
        assert event.hasMatching(source): String.format("Rule event %s has no matching in %s", event, AbstractGraph.toString(source));
    }

    /*
     * (non-Javadoc)
     * @see groove.trans.Dev#dom()
     */
    @Override
    public Graph getSource() {
        return source;
    }

	@Override
	public NestedRule getRule() {
	    return rule;
	}

	/* (non-Javadoc)
	 * @see groove.trans.Dev#match()
	 */
	@Override
	public VarNodeEdgeMultiMap getAnchorMap() {
	    return anchorMap;
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
			} else {
				target = source;
			}
		}
		return target;
    }
    
    /**
	 * Callback factory method to compute a target for this applier.
	 */
	@Override
	protected Graph computeTarget() {
		Graph target = createTarget();
		applyDelta(target);
		target.setFixed();
		return target;
	}

	@Override
	public Morphism getMatching() {
    	if (match == null) {
    		match = computeMatching(); 
    	}
    	return match;
    }

	/**
	 * Callback method to create the matching from the rule's LHS to the source graph. 
	 * @see #getMatching()
	 */
	@Override
	protected Morphism computeMatching() {
		return getEvent().getMatching(source);
	}

    @Override
    public Morphism getMorphism() {
        if (morphism == null) {
            morphism = computeMorphism();
        }
        return morphism;
    }
    
    /**
     * Constructs the morphism between source and target graph from the application.
     */
    @Override
    protected Morphism computeMorphism() {
    	Morphism result = createMorphism();
    	NodeEdgeMap mergeMap = getMergeMap();
    	for (Node node: source.nodeSet()) {
			Node nodeImage = mergeMap.getNode(node);
			if (nodeImage != null && getTarget().containsElement(nodeImage)) {
				result.putNode(node, nodeImage);
			}
		}
    	Set<Edge> erasedEdges = getErasedEdges();
    	for (Edge edge: source.edgeSet()) {
			if (!erasedEdges.contains(edge)) {
				Edge edgeImage = edge.imageFor(mergeMap);
				if (edgeImage != null && getTarget().containsElement(edgeImage)) {
					result.putEdge(edge, edgeImage);
				}
			}
		}
		return result;
    }

	/**
     * The comatch is constructed in the course of rule application.
     * Returns <code>null</code> is called before any of the 
     * <code>(re)start</code> methods has been invoked.
     */
    @Override
    public VarNodeEdgeMultiMap getCoanchorMap() {
        if (coAnchorMap == null) {
            coAnchorMap = computeCoanchorMap();
        }
        return coAnchorMap;
    }

	/**
	 * Constructs a map from all nodes of the RHS that are endpoints of
	 * creator edges.
	 */
	@Override
	protected VarNodeEdgeMultiMap computeCoanchorMap() {
		final VarNodeEdgeMultiMap result = getEvent().getCoanchorMap().clone();
		// add creator node images
		Node[] coanchor = rule.coanchor();
		int coanchorSize = coanchor.length;
		Element[] coanchorImage = getCoanchorImage();
		int[] occurrences = getEvent().getNodeCreatorOccurrenceCount();
		for (int i = 0, j = 0; i < coanchorSize; i++) {
			for( int cnt = 0 ; cnt < occurrences[i] ; cnt++, j++ ) {
				assert coanchorImage[j] instanceof Node : String.format("Coanchor image at %d is %s",
						j,
						coanchorImage[j]);
				result.putNode(coanchor[i], (Node) coanchorImage[j]);
			}
		}
		return result;
	}

	@Override
	public Node[] getCoanchorImage() {
    	if (coanchorImage == null) {
    		coanchorImage = computeCoanchorImage();
    	}
        return coanchorImage;
    }
    
    /**
	 * Callback factory method to create a coanchor image for this application from a
	 * given match and for a given host graph. The image consists of 
	 * fresh images for the creator nodes of the rule.
	 */
	@Override
	protected Node[] computeCoanchorImage() {
		if (getCoanchorSize() == 0) {
			return EMPTY_COANCHOR_IMAGE;
		} else {
			return getEvent().getCoanchorImage(source);
		}
	}
//    
//    /**
//	 * Callback factory method to create fresh element for the coanchor image
//	 * at a given index. The fresh node is actually created using {@link #getFreshNode(int, Graph)}.
//	 */
//	protected Node computeCoanchorImageAt(int i) {
//		return getFreshNode(i, source);
//	}

	@Override
	public void setCoanchorImage(Node[] image) {
		this.coanchorImage = image;			
	}
	
	/** Convenience method to obtain the size of the coanchor. */
	private int getCoanchorSize() {
		return getRule().coanchor().length;
	}

	@Override
	public void applyDelta(DeltaTarget target) {
        reporter.start(APPLY);
        if (rule.isModifying()) {
            eraseEdges(target);
        	// either merge or erase the LHS nodes
        	if (rule.hasMergers()) {
                reporter.start(MERGING);
                mergeNodes(target);
        	} else {
				reporter.start(ERASING);
				eraseNodes(target);
			}
			reporter.stop();
            reporter.start(CREATING);
            createNodes(target);
            createEdges(target);
            reporter.stop();
            reporter.start(POSTPROCESSING);
            reporter.stop();
        } 
        reporter.stop();
    }

	/**
	 * Wraps <code>target</code> into a {@link FilteredDeltaTarget} and
	 * then calls {@link #applyDelta(DeltaTarget)}.
	 */
    @Override
    public void applyDelta(DeltaTarget target, int mode) {
		applyDelta(new FilteredDeltaTarget(target, mode));
	}

	/**
     * Erases the images of the reader nodes of the rule,
     * together with their incident edges.
     * @param target the target to which to apply the changes
     */
    @Override
    protected void eraseNodes(DeltaTarget target) {
        Set<Node> nodeSet = getErasedNodes();
		// also remove the incident edges of the eraser nodes
        if (!nodeSet.isEmpty()) {
//        	// there is a choice here to query the graph for its incident edge set
//        	// which may be expensive if it hasn't yet been computed
//        	Set<Edge> removedEdges = new HashSet<Edge>();
//        	for (Node node: nodeSet) {
//        		for (Edge edge: source.edgeSet(node)) {
//        			if (removedEdges.add(edge)) {
//        				target.removeEdge(edge);
//						registerErasure(edge);
//        			}
//        		}
//        	}
        	// the alternative is to iterate over all edges of the source graph
        	// currently this seems to be fastest
        	for (Edge edgeMatch: source.edgeSet()) {
                int arity = edgeMatch.endCount();
                boolean removed = false;
                for (int i = 0; !removed && i < arity; i++) {
                	removed = nodeSet.contains(edgeMatch.end(i));
                }
                if (removed) {
                	target.removeEdge(edgeMatch);
                	registerErasure(edgeMatch);
                }
            }
            removeNodeSet(target, nodeSet);
        }
        removeIsolatedValueNodes(target);
	}

	/**
	 * Removes those value nodes whose incoming edges have all been erased.
	 */
	@Override
	protected void removeIsolatedValueNodes(DeltaTarget target) {
		// for efficiency we don't use the getter but test for null
        if (isolatedValueNodes != null) {
        	for (ValueNode node : isolatedValueNodes) {
				target.removeNode(node);
				if (removedValueNodes == null) {
					removedValueNodes = new HashSet<ValueNode>();
				}
				removedValueNodes.add(node);
			}
        }
	}

	/**
	 * Performs the edge erasure necessary according to the rule.
	 * @param target the target to which to apply the changes
	 */
    @Override
    protected void eraseEdges(DeltaTarget target) {
        for (Edge erasedEdge: getErasedEdges()) {
            target.removeEdge(erasedEdge);
        	registerErasure(erasedEdge);
        }
	}
    
    /**
     * Callback method to notify that an edge has been erased.
     * Used to ensure that isolated value nodes are removed from the graph.
     */
    @Override
    protected void registerErasure(Edge edge) {
    	Node target = edge.opposite();
    	if (target instanceof ValueNode) {
    		Set<Edge> edges = getValueNodeEdges((ValueNode) target);
    		edges.remove(edge);
    		if (edges.isEmpty()) {
    			getIsolatedValueNodes().add((ValueNode) target);
    		}
    	}
    }

	/**
	 * Performs the node (and edge) merging.
	 * @param target the target to which to apply the changes
	 */
    @Override
    protected void mergeNodes(DeltaTarget target) {
        if (rule.hasMergers()) {
        	// delete the merged nodes
            MergeMap mergeMap = getMergeMap();
            for (Element mergedElem: mergeMap.nodeMap().keySet()) {
            	if (mergedElem instanceof Node) {
            		removeNode(target, (Node)mergedElem);
            	}
            }
//            removeNodeSet(target, mergeMap.keySet());
            // replace the incident edges of the merged nodes
            Set<Edge> erasedEdges = getErasedEdges();
            for (Edge sourceEdge: source.edgeSet()) {
                if (!erasedEdges.contains(sourceEdge)) {
                    Edge image = sourceEdge.imageFor(mergeMap);
                    if (image != sourceEdge) {
						target.removeEdge(sourceEdge);
						// if the edge is in the source and not erased, it is also already
						// in the target, so we do not have to add it
		                if (image != null && (erasedEdges.contains(image) || !source.containsElement(image))) {
		                	addEdge(target, image);
		                } else {
		                	registerErasure(sourceEdge);
		                }
					}
				}
			}
            removeIsolatedValueNodes(target);
        }
    }

    /**
	 * Adds nodes to the target graph, as dictated by the rule's RHS.
	 * 
	 * @param target
	 *            the target to which to apply the changes
	 */
    @Override
    protected void createNodes(DeltaTarget target) {
        if (rule.hasCreators()) {
            Node[] creatorNodes = rule.coanchor();
            int creatorNodeCount = creatorNodes.length;
            VarNodeEdgeMultiMap coanchorMap = getCoanchorMap();
            for (int i = 0; i < creatorNodeCount; i++) {
            	for( Node newNode : coanchorMap.getNode(creatorNodes[i]) )
                target.addNode(newNode);
            }
        }
    }
    
    /**
     * Adds edges to the target, as dictated by the rule's RHS.
     * @param target the target to which to apply the changes
     */
    @Override
    protected void createEdges(DeltaTarget target) {
        if (rule.hasCreators()) {
            // first add the (pre-computed) simple creator edge images
        	for( Edge edge : getRule().getSimpleCreatorEdges() ) {
	            for (Edge image: getEvent().imagesFor(edge, getCoanchorMap(), getCoanchorImage())) {
	                // only add if not already in the source or just erased
	                if (image != null && ! source.containsElement(image) || getErasedEdges().contains(image)) {
	                    addEdge(target, image);
	                }
	            }
            }
            // now compute and add the complex creator edge images
            for (Edge edge: getRule().getComplexCreatorEdges()) {
                for( Edge image : getEvent().imagesFor(edge, getCoanchorMap(), getCoanchorImage()) ) {
                	// only add if the image exists
                	if (image != null) {
                		addEdge(target, image);
                	}
                }
            }
        }
    }
    
    /**
     * The hash code is based on the identity of the event.
     */
    @Override
    public int hashCode() {
    	return System.identityHashCode(getEvent());
    }
    
    /**
     * Two rule applications are equal if they have the same source and event.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RuleApplication) {
            RuleApplication other = (RuleApplication) obj;
            return equalsEvent(other) && equalsSource(other);
        } else {
            return false;
        }
    }
    
    /**
     * Tests if the rules of two rule applications coincide.
     * Callback method from {@link #equals(Object)}.
     */
    @Override
    protected boolean equalsSource(RuleApplication other) {
        return getSource() == other.getSource();
    }
    
    /**
     * Tests if the rules of two rule applications coincide.
     * Callback method from {@link #equals(Object)}.
     */
    @Override
    protected boolean equalsEvent(RuleApplication other) {
        return getEvent() == other.getEvent();
    }
    
    @Override
	public String toString() {
	    StringBuffer result = new StringBuffer("Derivation for rule " + getRule().getName());
	    result.append("\nMatching:\n  " + anchorMap);
	    return result.toString();
	}

	/**
	 * Returns the set of explicitly erased nodes, i.e., the
	 * images of the LHS eraser nodes.
	 */
    @Override
    protected Set<Node> getErasedNodes() {
        return event.getErasedNodes();
    }

	/**
	 * Returns the set of explicitly erased edges, i.e., the
	 * images of the LHS eraser edges.
	 */
    @Override
    protected Set<Edge> getErasedEdges() {
		return event.getErasedEdges();
	}

	/**
	 * Returns the rule event underlying this applications.
	 */
    @Override
    public NestedEvent getEvent() {
		return event;
	}

	/**
	 * Returns a mapping from source to target graph nodes, dictated by
	 * the merger and eraser nodes in the rules. 
	 * @return an {@link MergeMap} that maps nodes of the
	 * source that are merged away to their merged images, and deleted nodes to <code>null</code>.
	 */
	@Override
	protected MergeMap getMergeMap() {
	    return event.getMergeMap();
    }

	/**
	 * Callback factory method to create a morphism from source to target graph.
	 * Note that this is <i>not</i> the same kind of object as the matching.
	 */
	@Override
	protected DefaultMorphism createMorphism() {
		// do not use the rule factory for this one
		return new DefaultMorphism(source, getTarget());
	}

	/**
     * Callback factory method for creating the target graph of an application.
     * This implementation clones the source.
     * @see Graph#clone()
     */
    @Override
    protected Graph createTarget() {
        return getSource().clone();
    }

    /**
	 * Adds an edge to a delta target, if the edge
	 * is not <code>null</code> and not already in the source graph.
	 * Optimizes by trying to call {@link InternalGraph#addEdgeWithoutCheck(Edge)}
	 * if the target is an {@link InternalGraph}.
	 */
	@Override
	protected void addEdge(DeltaTarget target, Edge edge) {
		Node targetNode = edge.opposite();
		if (targetNode instanceof ValueNode && (!source.containsElement(targetNode) && !getAddedValueNodes().contains(targetNode)) || removedValueNodes != null && removedValueNodes.contains(targetNode)) {
			target.addNode(targetNode);
			boolean nodeAdded = getAddedValueNodes().add((ValueNode) targetNode);
			assert nodeAdded : String.format("%s already contained %s", getAddedValueNodes(), targetNode);
			if (removedValueNodes != null && removedValueNodes.contains(targetNode)) {
				removedValueNodes.remove(targetNode);
			}
		}
		if (target instanceof InternalGraph) {
			((InternalGraph) target).addEdgeWithoutCheck(edge);
		} else {
			// apparently the target wasn't an InternalGraph
			// so we can't do efficient edge addition
			target.addEdge(edge);
		}
	}

//    /**
//	 * Returns a node that is fresh with respect to a given graph. 
//	 * The previously created fresh nodes are tried first (see {@link NestedEvent#getFreshNodes(int)}; 
//	 * only if all of those are already in the graph, a new fresh node is created using
//	 * {@link #createNode()}.
//	 * @param creatorIndex
//	 *            index in the rhsOnlyNodes array indicating the node of the
//	 *            rule for which a new image is to be created
//	 * @param graph
//	 *            the graph to which a node should be added
//	 */
//	public Node getFreshNode(int creatorIndex, Graph graph) {
//		Node result = null;
//		Collection<Node> currentFreshNodes = getEvent().getFreshNodes(creatorIndex);
//		Iterator<Node> freshNodeIter = currentFreshNodes.iterator();
//		while (result == null && freshNodeIter.hasNext()) {
//			Node freshNode = freshNodeIter.next();
//			if (!graph.containsElement(freshNode)) {
//				result = freshNode;
//			}
//		}
//		if (result == null) {
//			result = createNode();
//			currentFreshNodes.add(result);
//		}
//		return result;
//	}
//
//    /**
//     * Callback factory method for a newly constructed node.
//     * This implementation returns a {@link DefaultNode}, with
//     * a node number determined by the grammar's node counter.
//     */
//    protected Node createNode() {
//        freshNodeCount++;
//    	SystemRecord record = getEvent().getRecord();
//    	return record == null ? new DefaultNode() : record.newNode();
//    }

	/**
	 * Removes a node from a delta target. Optimizes by trying to call
	 * {@link InternalGraph#removeNodeWithoutCheck(Node)} if the target is an
	 * {@link InternalGraph}.
	 */
	private void removeNode(DeltaTarget target, Node node) {
	    if (target instanceof InternalGraph) {
	        ((InternalGraph) target).removeNodeWithoutCheck(node);
	    } else {
	        // apparently the target wasn't an InternalGraph
	        // so we can't do efficient edge removal
	    	target.removeNode(node);
	    }
	}

	/**
	 * Removes a set of nodes from a delta target. Optimizes by trying to call
	 * {@link InternalGraph#removeNodeWithoutCheck(Node)} if the target is an
	 * {@link InternalGraph}.
	 */
	private void removeNodeSet(DeltaTarget target, Collection<Node> nodeSet) {
	    if (target instanceof InternalGraph) {
	        ((InternalGraph) target).removeNodeSetWithoutCheck(nodeSet);
	    } else {
	        // apparently the target wasn't an InternalGraph
	        // so we can't do efficient edge removal
            for (Node node: nodeSet) {
                target.removeNode(node);
            }
	    }
	}

	/** 
	 * Lazily creates and returns the set of remaining incident edges of a given
	 * value node.
	 */
	private Set<Edge> getValueNodeEdges(ValueNode node) {
		if (valueNodeEdgesMap == null) {
			valueNodeEdgesMap = new HashMap<ValueNode,Set<Edge>>();
		}
		Set<Edge> result = valueNodeEdgesMap.get(node);
		if (result == null) {
			result = new HashSet<Edge>(source.edgeSet(node));
			valueNodeEdgesMap.put(node, result);
		}
		return result;
	}

	/**
	 * Returns the currently detected set of value nodes that 
	 * have become isolated due to edge erasure.
	 */
	private Set<ValueNode> getIsolatedValueNodes() {
		if (isolatedValueNodes == null) {
			isolatedValueNodes = new HashSet<ValueNode>();
		}
		return isolatedValueNodes;
	}

	/**
	 * Returns the currently detected set of value nodes that 
	 * have become isolated due to edge erasure.
	 */
	private Set<ValueNode> getAddedValueNodes() {
		if (addedValueNodes == null) {
			addedValueNodes = new HashSet<ValueNode>();
		}
		return addedValueNodes;
	}
	
    /**
     * Matching from the rule's lhs to the source graph.
     */
    protected final NestedRule rule;
    /**
     * The source graph of this derivation. May not be <tt>null</tt>. 
     */
    protected final Graph source;
    /**
     * Matching from the rule's lhs to the source graph.
     */
    protected final VarNodeEdgeMultiMap anchorMap;
    /**
     * The event from which we get the rule and anchor image.
     */
    protected final NestedEvent event;
    /**
     * Mapping from selected RHS elements to target graph. 
     * The comatch is constructed in the course of rule application.
     */
    protected VarNodeEdgeMultiMap coAnchorMap;
    /** 
     * The target graph of this derivation, created lazily in {@link #computeTarget()}. 
     */
    protected Graph target;
    /**
     * Matching from the rule's LHS to the source.
     * Created lazily in {@link #getMatching()}.
     */
    protected Morphism match;
    /**
     * Underlying morphism from the source to the target.
     */
    protected Morphism morphism;
    /**
     * The images of the creator nodes.
     * This is part of the information needed to (re)construct the derivation target.
     */
    protected Node[] coanchorImage;
    /**
	 * A mapping from target value nodes of erased edges to their
	 * remaining incident edges, used to judge spurious value nodes.
	 */
    private Map<ValueNode,Set<Edge>> valueNodeEdgesMap;
    /** The set of value nodes that have become isolated due to edge erasure. */
    private Set<ValueNode> isolatedValueNodes;
    /** The set of value nodes that have been added due to edge creation. */
    private Set<ValueNode> addedValueNodes;
    /** The set of value nodes that have been added due to edge creation. */
    private Set<ValueNode> removedValueNodes;
    
    /** Static constant for rules with coanchors. */
    static private final Node[] EMPTY_COANCHOR_IMAGE = new Node[0];
    /** Reporter for prifiling the application class. */
    static public final Reporter reporter = Reporter.register(RuleApplication.class);
    /** Handle for profiling the actual rule application. */
    static public final int APPLY = reporter.newMethod("apply(Matching)");
    /** Handle for profiling the creation phase of the actual rule application. */
    static public final int CREATING = reporter.newMethod("Application: Creating");
    /** Handle for profiling the erasure phase of the actual rule application. */
    static public final int ERASING = reporter.newMethod("Application: Erasing");
    /** Handle for profiling the merging phase of the actual rule application. */
    static public final int MERGING = reporter.newMethod("Application: Merging");
    /** Handle for profiling the postprocessing of the actual rule application. */
    static public final int POSTPROCESSING = reporter.newMethod("Application: Post-processing");

}

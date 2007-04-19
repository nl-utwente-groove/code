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
 * $Id: SPOApplication.java,v 1.6 2007-04-19 11:33:50 rensink Exp $
 */
package groove.trans;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import groove.graph.AbstractGraph;
import groove.graph.DefaultMorphism;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.NodeEdgeMap;
import groove.graph.FilteredDeltaTarget;
import groove.graph.Graph;
import groove.graph.DeltaTarget;
import groove.graph.MergeMap;
import groove.graph.InternalGraph;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.algebra.ValueNode;
import groove.rel.VarNodeEdgeMap;
import groove.util.Reporter;

/**
 * Class representing the application of a {@link groove.trans.SPORule} to a graph. 
 * @author Arend Rensink
 * @version $Revision: 1.6 $ $Date: 2007-04-19 11:33:50 $
 */
public class SPOApplication implements RuleApplication, Derivation {
    /**
     * Returns the number of nodes that were created during rule application.
     */
    static public int getFreshNodeCount() {
        return freshNodeCount;
    }

    /**
     * The total number of nodes (over all rules) created by {@link #createNode()}.
     */
    private static int freshNodeCount;

    /**
     * Constructs a new derivation on the basis of a given production rule, host graph and rule factory.
     * @param event the production rule instance involved
     * @param source the host graph to which the rule is to be applied
     */
    public SPOApplication(SPOEvent event, Graph source) {
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
    public Graph getSource() {
        return source;
    }

    /* (non-Javadoc)
	 * @see groove.trans.Dev#rule()
	 */
	public Rule getRule() {
	    return rule;
	}

	/* (non-Javadoc)
	 * @see groove.trans.Dev#match()
	 */
	public VarNodeEdgeMap getAnchorMap() {
	    return anchorMap;
	}

	/**
     * This implementation constructs the target lazily.
     * If the rule is not modifying, the source is aliased.
     */
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
    
    public boolean isTargetSet() {
        return target != null;
    }

    /**
	 * Callback factory method to compute a target for this applier.
	 */
	protected Graph computeTarget() {
		Graph target = createTarget();
		applyDelta(target);
		target.setFixed();
		return target;
	}

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
	protected Morphism computeMatching() {
		return getEvent().getMatching(source);
	}

    public Morphism getMorphism() {
        if (morphism == null) {
            morphism = computeMorphism();
        }
        return morphism;
    }
    
    /**
     * Constructs the morphism between source and target graph from the application.
     */
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
    public VarNodeEdgeMap getCoanchorMap() {
        if (coAnchorMap == null) {
            coAnchorMap = computeCoanchorMap();
        }
        return coAnchorMap;
    }

	/**
	 * Constructs a map from the reader nodes of the RHS that are endpoints of
	 * creator edges.
	 */
	protected VarNodeEdgeMap computeCoanchorMap() {
		final VarNodeEdgeMap result = getEvent().getCoanchorMap().clone();
		// add creator node images
		Node[] coanchor = rule.coanchor();
		int coanchorSize = coanchor.length;
		Element[] coanchorImage = this.coanchorImage;
		for (int i = 0; i < coanchorSize; i++) {
            Node hint = coanchorImage == null ? null : (Node) coanchorImage[i];
            boolean hintValid = hint != null && !source.containsElement(hint);
            if (hintValid) {
                result.putNode(coanchor[i], hint);
            } else {
                result.putNode(coanchor[i], getFreshNode(i, source));
            }
		}
		return result;
	}

	public Element[] getCoanchorImage() {
    	if (coanchorImage == null) {
    		coanchorImage = computeCoanchorImage();
    	}
        return coanchorImage;
    }
    
    /**
	 * Callback factory method to create a footprint for this application from a
	 * given match and for a given host graph. The footprint consists of the
	 * anchor images of the match and fresh images for the creator nodes of the
	 * rule.
	 */
	protected Element[] computeCoanchorImage() {
        Node[] coanchor = rule.coanchor();
        int coanchorSize = coanchor.length;
		Element[] result = new Element[coanchorSize];
        VarNodeEdgeMap coanchorMap = getCoanchorMap();
		for (int i = 0; i < coanchorSize; i++) {
			result[i] = coanchorMap.getNode(coanchor[i]);
			assert result[i] != null : "Coanchor map "+coanchorMap+" should have image for element "+coanchor[i];
		}
		return result;
	}

	public void setCoanchorImage(Element[] image) {
	    this.coanchorImage = image; 
	}

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
    public void applyDelta(DeltaTarget target, int mode) {
		applyDelta(new FilteredDeltaTarget(target, mode));
	}

	/**
     * Erases the images of the reader nodes of the rule,
     * together with their incident edges.
     * @param target the target to which to apply the changes
     */
    protected void eraseNodes(DeltaTarget target) {
        Set<Node> nodeSet = getErasedNodes();
        removeNodeSet(target, nodeSet);
		// also remove the incident edges of the eraser nodes
        if (!nodeSet.isEmpty()) {
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
        }
        removeIsolatedValueNodes(target);
	}

	/**
	 * Removes those value nodes whose incoming edges have all been erased.
	 */
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
    protected void createNodes(DeltaTarget target) {
        if (rule.hasCreators()) {
            Node[] creatorNodes = rule.coanchor();
            int creatorNodeCount = creatorNodes.length;
            NodeEdgeMap coanchorMap = getCoanchorMap();
            for (int i = 0; i < creatorNodeCount; i++) {
                target.addNode(coanchorMap.getNode(creatorNodes[i]));
            }
        }
    }
    
    /**
     * Adds edges to the target, as dictated by the rule's RHS.
     * @param target the target to which to apply the changes
     */
    protected void createEdges(DeltaTarget target) {
        if (rule.hasCreators()) {
            Edge[] creatorEdges = rule.getCreatorEdges();
            int creatorEdgeCount = creatorEdges.length;
            NodeEdgeMap comatch = getCoanchorMap();
            for (int i = 0; i < creatorEdgeCount; i++) {
                Edge edge = creatorEdges[i];
                Edge image = edge.imageFor(comatch);
                // the edge should only be added if it is not in the source,
                // or if it has been explicitly erased
                if (image != null && (!source.containsElement(image)) || getErasedEdges().contains(image)) {
                	addEdge(target, image);
                }
            }
        }
    }
    
    /**
     * The hash code is based on that of the event.
     */
    @Override
    public int hashCode() {
    	return getEvent().hashCode();
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
    protected boolean equalsSource(RuleApplication other) {
        return getSource() == other.getSource();
    }
    
    /**
     * Tests if the rules of two rule applications coincide.
     * Callback method from {@link #equals(Object)}.
     */
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
    protected Set<Node> getErasedNodes() {
        return event.getErasedNodes();
    }

	/**
	 * Returns the set of explicitly erased edges, i.e., the
	 * images of the LHS eraser edges.
	 */
    protected Set<Edge> getErasedEdges() {
		return event.getErasedEdges();
	}

	/**
	 * Returns the rule event underlying this applications.
	 */
    public SPOEvent getEvent() {
		return event;
	}

	/**
	 * Returns a mapping from source to target graph nodes, dictated by
	 * the merger and eraser nodes in the rules. 
	 * @return an {@link MergeMap} that maps nodes of the
	 * source that are merged away to their merged images, and deleted nodes to <code>null</code>.
	 */
	protected MergeMap getMergeMap() {
	    return event.getMergeMap();
    }

	/**
	 * Callback factory method to create a morphism from source to target graph.
	 * Note that this is <i>not</i> the same kind of object as the matching.
	 */
	protected DefaultMorphism createMorphism() {
		// do not use the rule factory for this one
		return new DefaultMorphism(source, getTarget());
	}

	/**
     * Callback factory method for creating the target graph of an application.
     * This implementation clones the source.
     * @see Graph#clone()
     */
    protected Graph createTarget() {
        return getSource().clone();
    }

    /**
	 * Adds an edge to a delta target, if the edge
	 * is not <code>null</code> and not already in the source graph.
	 * Optimizes by trying to call {@link InternalGraph#addEdgeWithoutCheck(Edge)}
	 * if the target is an {@link InternalGraph}.
	 */
	protected void addEdge(DeltaTarget target, Edge edge) {
		if (target instanceof InternalGraph) {
			((InternalGraph) target).addEdgeWithoutCheck(edge);
		} else {
			// apparently the target wasn't an InternalGraph
			// so we can't do efficient edge addition
			target.addEdge(edge);
		}
		Node targetNode = edge.opposite();
		if (targetNode instanceof ValueNode && (!source.containsElement(targetNode) && !getAddedValueNodes().contains(targetNode)) || removedValueNodes != null && removedValueNodes.contains(targetNode)) {
			target.addNode(targetNode);
			getAddedValueNodes().add((ValueNode) targetNode);
			if (removedValueNodes != null && removedValueNodes.contains(targetNode)) {
				removedValueNodes.remove(targetNode);
			}
		}
	}

    /**
	 * Returns a node that is fresh with respect to a given graph. 
	 * The previously created fresh nodes are tried first (see {@link SPOEvent#getFreshNodes(int)}; 
	 * only if all of those are already in the graph, a new fresh node is created using
	 * {@link #createNode()}.
	 * @param creatorIndex
	 *            index in the rhsOnlyNodes array indicating the node of the
	 *            rule for which a new image is to be created
	 * @param graph
	 *            the graph to which a node should be added
	 */
	public Node getFreshNode(int creatorIndex, Graph graph) {
		Node result = null;
		Iterator<Node> freshNodeIter = getEvent().getFreshNodes(creatorIndex).iterator();
		while (result == null && freshNodeIter.hasNext()) {
			Node freshNode = freshNodeIter.next();
			if (!graph.containsElement(freshNode)) {
				result = freshNode;
			}
		}
		if (result == null) {
			result = createNode();
			getEvent().getFreshNodes(creatorIndex).add(result);
		}
		return result;
	}

    /**
     * Callback factory method for a newly constructed node.
     * This implementation returns a {@link DefaultNode}, with
     * a node number determined by the grammar's node counter.
     */
    protected Node createNode() {
    	Node result;
    	// the following is a stopgap: to ensure node uniqueness we ask the grammar,
    	// but this may be null, in which case we rely on the DefaultNode's capacity
    	// to generate unique node nrs
    	DerivationData record = getEvent().getRecord();
    	if (record == null) {
    		result = new DefaultNode();
    	} else {
    		result = new DefaultNode(record.getNodeCounter());
    	}
        freshNodeCount++;
        return result;
    }

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
    protected final SPORule rule;
    /**
     * The source graph of this derivation. May not be <tt>null</tt>. 
     */
    protected final Graph source;
    /**
     * Matching from the rule's lhs to the source graph.
     */
    protected final VarNodeEdgeMap anchorMap;
    /**
     * The event from which we get the rule and anchor image.
     */
    protected final SPOEvent event;
    /**
     * Mapping from selected RHS elements to target graph. 
     * The comatch is constructed in the course of rule application.
     */
    protected VarNodeEdgeMap coAnchorMap;
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
    protected Element[] coanchorImage;
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
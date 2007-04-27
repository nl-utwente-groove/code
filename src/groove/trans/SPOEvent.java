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
 * $Id: SPOEvent.java,v 1.15 2007-04-27 22:07:01 rensink Exp $
 */
package groove.trans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.GraphShape;
import groove.graph.Morphism;
import groove.graph.NodeEdgeMap;
import groove.graph.Graph;
import groove.graph.MergeMap;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.NodeSet;
import groove.graph.WrapperLabel;
import groove.graph.algebra.ValueNode;
import groove.rel.RegExprLabel;
import groove.rel.VarNodeEdgeHashMap;
import groove.rel.VarNodeEdgeMap;
import groove.util.Groove;
import groove.util.Reporter;
import groove.util.TreeHashSet3;

/**
 * Class representing an instance of a {@link groove.trans.SPORule} for a given
 * anchor map.
 * @author Arend Rensink
 * @version $Revision: 1.15 $ $Date: 2007-04-27 22:07:01 $
 */
public class SPOEvent implements RuleEvent {
	/** 
	 * The start string of the anchor image description.
	 * @see #getAnchorImageString()
	 */
	static public final String ANCHOR_START = "(";
	/** 
	 * The string separating the elements in the anchor image description.
	 * @see #getAnchorImageString()
	 */
	static public final String ANCHOR_SEPARATOR = ",";
	/** 
	 * The end string of the anchor image description.
	 * @see #getAnchorImageString()
	 */
	static public final String ANCHOR_END = ")";
    /**
     * Constructs a new event on the basis of a given production rule and anchor map.
     * The rule is required to be fixed, as inticated by {@link SPORule#isFixed()}.
     * @param rule the production rule involved
     * @param anchorMap the match of the rule's LHS elements to the host graph
     */
    public SPOEvent(SPORule rule, VarNodeEdgeMap anchorMap) {
    	rule.testFixed(true);
        this.rule = rule;
        this.anchorMap = anchorMap;
//		this.ruleFactory = rule.getRuleFactory();
        this.freshNodeList = createFreshNodeList();
    }

    /**
     * Constructs a new event on the basis of a given production rule and anchor map.
     * @param rule the production rule involved
     * @param anchorMap the match of the rule's LHS elements to the host graph
     */
    public SPOEvent(SPORule rule, VarNodeEdgeMap anchorMap, SystemRecord record) {
    	this(rule, anchorMap);
    	this.record = record;
    }
//
//	/**
//	 * Returns the rule factory of this event.
//     */
//    public RuleFactory getRuleFactory() {
//    	return ruleFactory;
//    }
    
    /** 
     * Returns the derivation record associated with this event. 
     * May be <code>null</code>.
     */
    protected SystemRecord getRecord() {
    	return record;
    }

    public SPORule getRule() {
	    return rule;
	}

	public Label getLabel() {
        return createLabel();
    }
    
	/**
	 * Callback method to create a label uniquely identifying this event.
	 * This implementation wraps the event in a {@link WrapperLabel}.
	 */
    protected Label createLabel() {
        return new WrapperLabel<RuleEvent>(this);
    }

    /** Returns the rule name. */
	public NameLabel getName() {
		return getRule().getName();
	}

	public VarNodeEdgeMap getAnchorMap() {
		if (!anchorMapNormalised) {
			anchorMap = computeNormalisedAnchorMap();
			anchorMapNormalised = true;
		}
	    return anchorMap;
	}

	/**
	 * Creates the normalised anchor map from the currently stored anchor map.
	 * The resulting map contains images for the anchor and eraser edges 
	 * and any variables on them.
	 */
    protected VarNodeEdgeMap computeNormalisedAnchorMap() {
    	NodeEdgeMap anchorMap = this.anchorMap;
    	VarNodeEdgeMap result = createVarMap();
    	for (Element key: getRule().anchor()) {
            if (key instanceof Edge) {
            	// store the endpoints and the variable valuations for the edges
                Edge edgeKey = (Edge) key;
            	Edge edgeImage = anchorMap.getEdge(edgeKey);
                int arity = edgeKey.endCount();
                for (int end = 0; end < arity; end++) {
                    result.putNode(edgeKey.end(end), edgeImage.end(end));
                }
                String var = RegExprLabel.getWildcardId(edgeKey.label());
                if (var != null) {
                    result.putVar(var, edgeImage.label());
                }
                result.putEdge(edgeKey, edgeImage);
            } else {
                result.putNode((Node) key, anchorMap.getNode((Node) key));
            }
        }
        // add the eraser edges
        for (Edge eraserEdge: getRule().getEraserNonAnchorEdges()) {
            Edge eraserImage = eraserEdge.imageFor(result);
            result.putEdge(eraserEdge, eraserImage);
        }
        return result;
    }

    /**
     * Returns a string starting with {@link #ANCHOR_START}, separated by
     * {@link #ANCHOR_SEPARATOR} and ending with {@link #ANCHOR_END}.
     */
    public String getAnchorImageString() {
    	return Groove.toString(getAnchorImage(), ANCHOR_START, ANCHOR_END, ANCHOR_SEPARATOR);
	}

	public VarNodeEdgeMap getCoanchorMap() {
        if (coanchorMap == null) {
            coanchorMap = computeCoanchorMap();
        }
        return coanchorMap;
    }
    
	/**
	 * Constructs a map from the reader nodes of the RHS that are endpoints of
	 * creator edges, to the target graph nodes.
	 */
	protected VarNodeEdgeMap computeCoanchorMap() {
		final VarNodeEdgeMap result = createVarMap();
		VarNodeEdgeMap anchorMap = getAnchorMap();
		NodeEdgeMap mergeMap = getRule().hasMergers() ? getMergeMap() : null;
		// add reader node images
		for (Map.Entry<Node,Node> creatorEntry: getRule().getCreatorMap().entrySet()) {
			Node creatorKey = creatorEntry.getKey();
			Node creatorValue = anchorMap.getNode(creatorEntry.getValue());
			if (mergeMap != null) {
				creatorValue = mergeMap.getNode(creatorValue);
			}
			result.putNode(creatorKey, creatorValue);
		}
		// add variable images
		for (String var: getRule().getCreatorVars()) {
			result.putVar(var, anchorMap.getVar(var));
		}
		return result;
	}

	public RuleApplication newApplication(Graph host) {
		return new SPOApplication(this, host);
	}
    
    /**
     * The hash code is based on that of the rule and an initial fragment of the
     * anchor images.
     */
	@Override
    public int hashCode() {
    	if (!hashCodeSet) {
    		hashCode = computeHashCode();
    		hashCodeSet = true;
    	}
    	return hashCode;
    }
    
    /**
     * Callback method to compute the hash code.
     */
    protected int computeHashCode() {
    	reporter.start(HASHCODE);
        int result = getRule().hashCode();
        // we don't use getAnchorImage() because the events are often
        // just created to look up a stored event; then we shouldn't spend too
        // much time on this one
        Element[] anchors = getRule().anchor();
        NodeEdgeMap anchorMap = getAnchorMap();
        int MAX_HASHED_ANCHOR_COUNT = 10;
        int hashedAnchorCount = Math.min(anchors.length, MAX_HASHED_ANCHOR_COUNT);
        for (int i = 0; i < hashedAnchorCount; i++) {
        	Element anchor = anchors[i];
        	if (anchor instanceof Node) {
        		result += anchorMap.getNode((Node) anchor).hashCode() << i;
        	} else {
        		result += anchorMap.getEdge((Edge) anchor).hashCode() << i;
        	}
        }
    	reporter.stop();
        return result;
    }
    
    /**
     * Two rule applications are equal if they have the same rule and anchor images.
     * Note that the source is not tested; do not collect rule applications for different sources!
     */
	@Override
    public boolean equals(Object obj) {
    	boolean result;
    	if (obj == this) {
    		result = true;
    	} else if (obj instanceof SPOEvent) {
        	reporter.start(EQUALS);
        	SPOEvent other = (SPOEvent) obj;
            result = equalsRule(other) && equalsAnchorImage(other);
            reporter.stop();
        } else {
            result = false;
        }
        return result;
    }
    
    /**
     * Tests if the rules of two rule applications coincide.
     * Callback method from {@link #equals(Object)}.
     */
    protected boolean equalsRule(RuleEvent other) {
        return getRule().equals(other.getRule());
    }
    
    /**
     * Tests if anchor images of two rule applications coincide.
     * Callback method from {@link #equals(Object)}.
     */
    protected boolean equalsAnchorImage(SPOEvent other) {
//        boolean result = true;
//        Element[] anchorImage = getAnchorImage();
//        Element[] otherAnchorImage = other.getAnchorImage();
//        int anchorSize = anchorImage.length;
//        for (int i = 0; result && i < anchorSize; i++) {
//            result = anchorImage[i].equals(otherAnchorImage[i]);
//        }
        return Arrays.equals(getAnchorImage(), other.getAnchorImage());
    }
    
	@Override
	public String toString() {
	    StringBuffer result = new StringBuffer(getRule().getName().name());
	    result.append(getAnchorImageString());
	    return result.toString();
	}

	/**
     * Computes a matching to a given graph,
     * based on the precomputed anchor map.
     * Returns <code>null</code> if a matching does not exist.
     */
    public Morphism getMatching(Graph host) {
        Matching result = computeMatcher(host);
        if (result == null) {
        	return result;
        } else {
        	return result.getTotalExtension();
        }
    }

    /**
	 * Computes a matching based on the given anchor images, but without
	 * computing its total extension. Return <code>null</code> if any of the
	 * anchor images is not in the source graph.
	 */
	public boolean hasMatching(Graph host) {
        Matching result = computeMatcher(host);
        if (result == null) {
        	return false;
        } else {
        	return result.hasTotalExtensions();
        }
	}

	/**
	 * Compares two events first on the basis of their rules,
	 * then lexicographically on the basis of thei anchor images.
	 */
	public int compareTo(RuleEvent other) {
		if (other.getRule().equals(getRule())) {
			// we have the same rule
			Element[] anchorImage = getAnchorImage();
			// retrieve the other even't anchor image array
			Element[] otherAnchorImage;
			if (other instanceof SPOEvent) {
				otherAnchorImage = ((SPOEvent) other).getAnchorImage();
			} else {
				// construct anchor image of the other event
				Element[] anchor = getRule().anchor();
				VarNodeEdgeMap otherAnchorMap = other.getAnchorMap();
				otherAnchorImage = new Element[anchor.length];
				for (int i = 0; i < anchor.length; i++) {
					Element anchorElement = anchor[i];
					if (anchorElement instanceof Node) {
						otherAnchorImage[i] = otherAnchorMap.getNode((Node) anchorElement);
					} else {
						otherAnchorImage[i] = otherAnchorMap.getEdge((Edge) anchorElement);
					}
				}
			}
			// now compare
			boolean equal = true;
			// walk over the anchor images
			int i;
			// find the first index in which the anchor images differ
			for (i = 0; equal && i < anchorImage.length; i++) {
				equal = anchorImage[i].equals(otherAnchorImage[i]);
			}
			if (equal) {
				// there was no difference between the anchor images
				return 0;
			} else {
				// there was a difference at index i-1
				return anchorImage[i-1].compareTo(otherAnchorImage[i-1]);
			}
		} else {
			// we have different rules; compare the rules instead
			return getRule().compareTo(other.getRule());
		}
	}

	/**
	 * Computes a matcher for this event in a given graph,
	 * based on the precomputed anchor map.
	 * Returns <code>null</code> if the anchor map does not fit to the host graph.
	 */
	protected Matching computeMatcher(Graph host) {
		reporter.start(GET_PARTIAL_MATCH);
	    Matching result;
	    VarNodeEdgeMap anchorMap = getAnchorMap();
	    boolean correct = true;
	    Iterator<Edge> edgeImageIter = anchorMap.edgeMap().values().iterator();
	    while (correct && edgeImageIter.hasNext()) {
	    	correct = virtuallyContains(host, edgeImageIter.next());
	    }
		if (correct) {
			Iterator<Node> nodeImageIter = anchorMap.nodeMap().values().iterator();
			while (correct && nodeImageIter.hasNext()) {
		    	correct = virtuallyContains(host, nodeImageIter.next());
			}
		}
		result = correct ? createMatcher(host) : null;
	    reporter.stop();
		return result;
	}

	/**
	 * Creates a matcher for this event in a given host graph, based on the rule and anchor map.
	 */
	private Matching createMatcher(Graph host) {
		DefaultMatching result = new DefaultMatching(getRule(), host) {
			@Override
			protected VarNodeEdgeMap createElementMap() {
				return getAnchorMap();
			}
		};
		result.setFixed();
		return result;
//		return getRuleFactory().createMatching(getRule(), getAnchorMap(), host);
	}
    
	/** 
	 * Tests if a graph contains a given element, 
	 * either explicitly (through {@link GraphShape#containsElement(Element)}) 
	 * or implicitly (because it is a {@link ValueNode}). 
	 */
	protected boolean virtuallyContains(Graph graph, Element element) {
		return element instanceof ValueNode || graph.containsElement(element);
	}
	
    /**
     * Returns the set of source elements that form the anchor image.
     */
    protected Element[] getAnchorImage() {
        if (anchorImage == null) {
            anchorImage = computeAnchorImage();
        }
        return anchorImage;
    }
    
    /**
     * Callback method to lazily compute 
     * the set of source elements that form the anchor image.
     */
    protected Element[] computeAnchorImage() {
    	reporter.start(GET_ANCHOR_IMAGE);
        Element[] anchor = getRule().anchor();
        int anchorSize = anchor.length;
        Element[] result = new Element[anchor.length];
        for (int i = 0; i < anchorSize; i++) {
        	if (anchor[i] instanceof Node) {
        		result[i] = anchorMap.getNode((Node) anchor[i]);
        	} else {
        		result[i] = anchorMap.getEdge((Edge) anchor[i]);
        	}
        }
        reporter.stop();
        return result;
    }
    
    /**
     * Returns the set of source elements that form the anchor image.
     */
    protected Set<Element> getAnchorImageSet() {
        if (anchorImageSet == null) {
        	NodeEdgeMap anchorMap = getAnchorMap();
            anchorImageSet = new HashSet<Element>(anchorMap.nodeMap().values());
            anchorImageSet.addAll(anchorMap.edgeMap().values());
        }
        return anchorImageSet;
    }

    public boolean conflicts(RuleEvent other) {
    	boolean result;
    	if (other instanceof SPOEvent) {
    		result = false;
    		// check if the other creates edges that this event erases
			Iterator<Edge> myErasedEdgeIter = getErasedEdges().iterator();
			Set<Edge> otherCreatedEdges = ((SPOEvent) other).getSimpleCreatedEdges();
			while (!result && myErasedEdgeIter.hasNext()) {
				result = otherCreatedEdges.contains(myErasedEdgeIter.next());
			}
			if (!result) {
	    		// check if the other erases edges that this event creates
				Iterator<Edge> myCreatedEdgeIter = getSimpleCreatedEdges().iterator();
				Set<Edge> otherErasedEdges = ((SPOEvent) other).getErasedEdges();
				while (!result && myCreatedEdgeIter.hasNext()) {
					result = otherErasedEdges.contains(myCreatedEdgeIter.next());
				}
			}
		} else {
			result = true;
		}
		return result;
	}

	/**
	 * Indicates if this rule event removes a part of the anchor image of
	 * another. If so, it means that the other event will not match in any graph
	 * reached after this one.
	 * 
	 * @param other
	 *            the event that we want to establish conflict with
	 * @return <code>true</code> if this event disables the other
	 */
    public boolean disables(RuleEvent other) {
        boolean result = false;
        Set<Element> anchorImage = ((SPOEvent) other).getAnchorImageSet();
        Iterator<Node> nodeIter = getErasedNodes().iterator();
        while (!result && nodeIter.hasNext()) {
            result = anchorImage.contains(nodeIter.next());
        }
        Iterator<Edge> edgeIter = getErasedEdges().iterator();
        while (!result && edgeIter.hasNext()) {
            result = anchorImage.contains(edgeIter.next());
        }
        return result;
    }

	/**
	 * Returns the set of explicitly erased nodes, i.e., the images of the LHS
	 * eraser nodes.
	 */
    protected Set<Node> getErasedNodes() {
        if (erasedNodeSet == null) {
            erasedNodeSet = computeErasedNodes();
        }
        return erasedNodeSet;
    }

	/**
	 * Computes the set of explicitly erased nodes, i.e., the
	 * images of the LHS eraser nodes.
	 * Callback method from {@link #getErasedNodes()}.
	 */
	protected Set<Node> computeErasedNodes() {
		NodeEdgeMap anchorMap = getAnchorMap();
		Node[] eraserNodes = getRule().getEraserNodes();
	    Set<Node> erasedNodes = createNodeSet();
	    // register the node erasures
	    for (int i = 0; i < eraserNodes.length; i++) {
	        Node nodeMatch = anchorMap.getNode(eraserNodes[i]);
	        erasedNodes.add(nodeMatch);
	    }
	    return erasedNodes;
	}

    /**
     * Returns the set of explicitly erased edges, i.e., the
     * images of the LHS eraser edges.
     */
    protected Set<Edge> getErasedEdges() {
        if (erasedEdgeSet == null) {
            erasedEdgeSet = computeErasedEdges();
        }
        return erasedEdgeSet;
    }

    /**
     * Computes the set of explicitly erased edges, i.e., the
     * images of the LHS eraser edges.
     * Callback method from {@link #getErasedEdges()}.
     */
    protected Set<Edge> computeErasedEdges() {
        Set<Edge> result = createEdgeSet();
        VarNodeEdgeMap anchorMap = getAnchorMap();
        Edge[] eraserEdges = getRule().getEraserEdges();
        for (int i = 0; i < eraserEdges.length; i++) {
            Edge edge = eraserEdges[i];
            Edge edgeImage = anchorMap.getEdge(edge);
            if (edgeImage == null) {
                edgeImage = edge.imageFor(anchorMap);
                assert edgeImage != null : "Image of "+edge+" cannot be deduced from "+anchorMap;
            }
            result.add(edgeImage);
        }
        return result;
    }

    /**
     * Returns the set of explicitly erased edges, i.e., the
     * images of the LHS eraser edges.
     */
    protected Set<Edge> getSimpleCreatedEdges() {
        if (simpleCreatedEdgeSet == null) {
            simpleCreatedEdgeSet = computeSimpleCreatedEdges();
        }
        return simpleCreatedEdgeSet;
    }

    /**
     * Computes the set of explicitly erased edges, i.e., the
     * images of the LHS eraser edges.
     * Callback method from {@link #getErasedEdges()}.
     */
    private Set<Edge> computeSimpleCreatedEdges() {
        Set<Edge> result = createEdgeSet();
        VarNodeEdgeMap coAnchorMap = getCoanchorMap();
        for (Edge edge: getRule().getSimpleCreatorEdges()) {
            Edge edgeImage = edge.imageFor(coAnchorMap);
            if (edgeImage != null) {
                result.add(edgeImage);
            }
        }
        return result;
    }

    /**
	 * Returns a mapping from source to target graph nodes, dictated by
	 * the merger and eraser nodes in the rules. 
	 * @return an {@link MergeMap} that maps nodes of the
	 * source that are merged away to their merged images, and deleted nodes to <code>null</code>.
	 */
	protected MergeMap getMergeMap() {
		if (mergeMap == null) {
			mergeMap = computeMergeMap();
		}
	    return mergeMap;
    }

    /**
     * Callback method from {@link #getMergeMap()} to compute the merge map. This is constructed on
     * the basis of matching and rule, without reference to the actual target graph, which indeed
     * may not yet be constructed at the time of invoking this method. The map is an
     * {@link MergeMap} to improve performance.
     */
    protected MergeMap computeMergeMap() {
    	VarNodeEdgeMap anchorMap = getAnchorMap();
        MergeMap mergeMap = createMergeMap();
        // integrate the mergings
        // the pre-morphism should be "flat" in the sense that any non-null value of an entry
        // should itself be a fixpoint of the pre-morphism
//        boolean mergersStable = true;
        for (Map.Entry<Node,Node> ruleMergeEntry: getRule().getMergeMap().entrySet()) {
            Node mergeKey = anchorMap.getNode(ruleMergeEntry.getKey());
            Node mergeImage = anchorMap.getNode(ruleMergeEntry.getValue());
            mergeMap.putNode(mergeKey, mergeImage);
//            assert mergeKey != null && mergeImage != null : "Images should be non-null in matching";
//            // the key-image pair should be put in the merge map,
//            // but maybe the key has been merged with a different node already
//            Element mergeKeyFixpoint = getFixpoint(mergeMap, mergeKey);
//            Element mergeImageFixpoint = getFixpoint(mergeMap, mergeImage);
//            if (mergeKeyFixpoint != mergeImageFixpoint) {
//                mergeMap.put(mergeKeyFixpoint, mergeImageFixpoint);
//                // there are overlapping mergings
//                mergersStable = false;
//            }
        }
        // now map the erased nodes to null
        for (Node node: getErasedNodes()) {
            mergeMap.removeNode(node);
//            mergersStable = false;
        }
        // flatten the merge map if necessary
//        if (!mergersStable) {
//        	for (Map.Entry<Element,Element> resultEntry: mergeMap.entrySet()) {
//                Element value = mergeMap.getValue(resultEntry);
//                Element fixpoint = getFixpoint(mergeMap, value);
//                mergeMap.setValue(resultEntry, fixpoint);
//            }
//        }
        return mergeMap;
    }

	/**
	 * Creates an array of lists to store the fresh nodes
	 * created by this rule.
	 */
	protected List<List<Node>> createFreshNodeList() {
		int creatorNodeCount = getRule().coanchor().length;
		List<List<Node>> result = new ArrayList<List<Node>>();
        for (int i = 0; i < creatorNodeCount; i++) {
        	result.add(new ArrayList<Node>());
        }
        return result;
	}

	/**
     * Callback factory method to create a fresh, empty node set.
     */
    protected Set<Node> createNodeSet() {
    	return new NodeSet();
    }

	/**
     * Callback factory method to create a fresh, empty edge set.
     */
    protected Set<Edge> createEdgeSet() {
    	return new TreeHashSet3<Edge>();
    }
    
    /**
     * Callback factory method to create the merge map object for 
     * {@link #computeMergeMap()}.
     * @return a fresh instance of {@link MergeMap}
     */
    protected MergeMap createMergeMap() {
		return new MergeMap();
	}
    
    /**
     * Callback factory method to create the map object for 
     * {@link #computeCoanchorMap()}.
     * @return a fresh instance of {@link VarNodeEdgeHashMap}
     */
    protected VarNodeEdgeMap createVarMap() {
    	return new VarNodeEdgeHashMap();
    }

    /** 
     * Returns a coanhor image suitable for a given graph.
     * This is delegate to the event, which can indeed keep a map of such 
     * images, and so save memory. 
     */
    Node[] getCoanchorImage(Graph host) {
		int coanchorSize = getRule().coanchor().length;
		Node[] result = new Node[coanchorSize];
		for (int i = 0; i < coanchorSize; i++) {
			result[i] = getFreshNode(i, host);
		}
		List<Node> resultAsList = Arrays.asList(result);
		Node[] existingResult = coanchorImageMap.get(resultAsList);
		if (existingResult == null) {
			coanchorImageMap.put(resultAsList, result);
			coanchorImageCount++;
		} else {
			result = existingResult;
			coanchorImageOverlap++;
		}
		return result;
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
		Collection<Node> currentFreshNodes = getFreshNodes(creatorIndex);
		Iterator<Node> freshNodeIter = currentFreshNodes.iterator();
		while (result == null && freshNodeIter.hasNext()) {
			Node freshNode = freshNodeIter.next();
			if (!graph.containsElement(freshNode)) {
				result = freshNode;
			}
		}
		if (result == null) {
			result = createNode();
			currentFreshNodes.add(result);
		}
		return result;
	}

    /**
     * Callback factory method for a newly constructed node.
     * This implementation returns a {@link DefaultNode}, with
     * a node number determined by the grammar's node counter.
     */
    protected Node createNode() {
        SPOApplication.freshNodeCount++;
    	SystemRecord record = getRecord();
    	Node result = record == null ? new DefaultNode() : record.newNode();
    	return result;
    }

	/**
	 * Returns the list of all previously created fresh nodes.
	 */
    protected List<Node> getFreshNodes(int creatorIndex) {
        return freshNodeList.get(creatorIndex);
    }

    /**
	 * Matching from the rule's lhs to the source graph.
	 */
    protected final SPORule rule;
//    /**
//     * The factory to be used to instantiate classes specific for this rule event type.
//     */
//    private final RuleFactory ruleFactory;
    /** The derivation record that has created this event, if any. */
    private SystemRecord record;
    /**
     * Matching from the rule's lhs to the source graph.
     */
    private VarNodeEdgeMap anchorMap;
    /**
     * Flag that indicates if {@linkplain #computeNormalisedAnchorMap()} has been invoked.
     */
    private boolean anchorMapNormalised = false;
    /**
     * Mapping from selected RHS elements to target graph. 
     * The comatch is constructed in the course of rule application.
     */
    private VarNodeEdgeMap coanchorMap;
    /**
     * Minimal mapping from the source graph to target graph to reconstruct the underlying morphism. 
     * The merge map is constructed in the course of rule application.
     */
    private MergeMap mergeMap;
    /**
     * Set of nodes from the source that are to be erased in the target.
     */
    private Set<Node> erasedNodeSet;
    /**
     * Set of edges from the source that are to be erased in the target.
     */
    private Set<Edge> erasedEdgeSet;
    /**
     * Images of the simple creator edges.
     */
    private Set<Edge> simpleCreatedEdgeSet;
//    /**
//     * The footprint of a derivation consists of the anchor images of the match
//     * together with the images of the creator nodes.
//     * This corresponds to is the information needed to (re)construct the derivation target.
//     */
//    private Element[] anchorImage;
    /**
     * Flag to indicate that the {@link #hashCode} variable has been initialized.
     */
    private boolean hashCodeSet;
    /**
     * The set of source elements that form the anchor image.
     */
    private Set<Element> anchorImageSet;
    /**
     * The array of source elements that form the anchor image.
     */
    private Element[] anchorImage;
    /**
	 * The precomputed hash code.
	 */
	private int hashCode;
	/**
	 * The list of nodes created by {@link #createNode()}.
	 */
	private final List<List<Node>> freshNodeList;
	/** Store of previously used coanchor images. */
	private final Map<List<Node>, Node[]> coanchorImageMap = new HashMap<List<Node>, Node[]>();

	/** 
	 * Reports the number of times a stored coanchor image has been recomputed 
	 * for a new rule application.
	 */
	static public int getCoanchorImageOverlap() {
		return coanchorImageOverlap;
	}
	/** 
	 * Reports the total number of coanchor images stored. 
	 */
	static public int getCoanchorImageCount() {
		return coanchorImageCount;
	}
	
	/** Counter for the reuse in coanchor images. */
	static private int coanchorImageOverlap;
	/** Counter for the coanchor images. */
	static private int coanchorImageCount;
	
	static private Reporter reporter = Reporter.register(RuleEvent.class);
	static private int HASHCODE = reporter.newMethod("computeHashCode()");
	static private int EQUALS = reporter.newMethod("equals()");
	static private int GET_PARTIAL_MATCH = reporter.newMethod("getPartialMatch()");
	static private int GET_ANCHOR_IMAGE = reporter.newMethod("getAnchorImage()");
}
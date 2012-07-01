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
 * $Id: SPOEvent.java,v 1.50 2007-12-10 12:00:25 rensink Exp $
 */
package groove.trans;

import groove.graph.DefaultMorphism;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.MergeMap;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.graph.NodeFactory;
import groove.graph.algebra.ValueNode;
import groove.rel.RegExprLabel;
import groove.rel.VarNodeEdgeHashMap;
import groove.rel.VarNodeEdgeMap;
import groove.util.CacheReference;
import groove.util.Groove;
import groove.util.Reporter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class representing an instance of an {@link SPORule} for a given
 * anchor map.
 * @author Arend Rensink
 * @version $Revision: 1.50 $ $Date: 2007-12-10 12:00:25 $
 */
final public class SPOEvent extends AbstractEvent<SPORule, SPOEvent.SPOEventCache> {
    /**
     * Constructs a new event on the basis of a given production rule and anchor map.
     * A further parameter determines whether information should be stored for reuse.
     * This constructor is only meant for rules without co-roots map.
     * @param rule the production rule involved
     * @param anchorMap the match of the rule's anchors to the host graph
     * @param nodeFactory factory for fresh nodes; may be <code>null</code>
     * @param reuse if <code>true</code>, the event should store diverse data structures to optimise for reuse
     */
    public SPOEvent(SPORule rule, VarNodeEdgeMap anchorMap, NodeFactory nodeFactory, boolean reuse) {
    	this(rule, anchorMap, null, nodeFactory, reuse);
    }

    /**
     * Constructs a new event on the basis of a given production rule and anchor map.
     * A further parameter determines whether information should be stored for reuse.
     * @param rule the production rule involved
     * @param anchorMap map from the rule's LHS elements to the host graph
     * @param coContextMap map from the rule's co-roots to the host graph
     * @param nodeFactory factory for fresh nodes; may be <code>null</code>
     * @param reuse if <code>true</code>, the event should store diverse data structures to optimise for reuse
     */
    public SPOEvent(SPORule rule, VarNodeEdgeMap anchorMap, VarNodeEdgeMap coContextMap, NodeFactory nodeFactory, boolean reuse) {
    	super(reference, rule);
    	rule.testFixed(true);
        this.anchorImage = computeAnchorImage(anchorMap);
        if (coContextMap == null) {
        	coRootImage = null;
        } else {
        	int coRootCount = getRule().getCoRootMap().size();
        	if (coRootCount == 0) {
        		coRootImage = EMPTY_ROOT_IMAGE;
        	} else {
				coRootImage = new Node[coRootCount];
				int coRootIx = 0;
				for (Node coRoot : getRule().getCoRootMap().nodeMap().keySet()) {
					coRootImage[coRootIx] = coContextMap.getNode(coRoot);
					coRootIx++;
				}
			}
        }
    	this.nodeFactory = nodeFactory;
    	this.reuse = reuse;
    }

    /** 
     * Returns a map from the rule anchors to elements of the host graph.
     * #see {@link SPORule#anchor()} 
     */
	public VarNodeEdgeMap getAnchorMap() {
	    return getCache().getAnchorMap();
	}

    /**
     * Returns a string starting with {@link #ANCHOR_START}, separated by
     * {@link #ANCHOR_SEPARATOR} and ending with {@link #ANCHOR_END}.
     */
    public String getAnchorImageString() {
    	return Groove.toString(getAnchorImage(), ANCHOR_START, ANCHOR_END, ANCHOR_SEPARATOR);
	}

    @Deprecated
    public VarNodeEdgeMap getSimpleCoanchorMap() {
        return getCache().getCoanchorMap();
    }
    
    /**
     * Constructs a map from the reader nodes of the RHS that are endpoints of
     * creator edges, to the target graph nodes.
     */
    public VarNodeEdgeMap getCoanchorMap() {
        return getCache().getCoanchorMap();
    }
    
    /**
     * The hash code is based on that of the rule and an initial fragment of the
     * anchor images.
     */
	@Override
    public int hashCode() {
    	if (hashCode == 0) {
    		hashCode = computeHashCode();
    		if (hashCode == 0) {
    			hashCode = 1;
    		}
    	}
    	return hashCode;
    }
    
    /**
     * Callback method to compute the hash code.
     */
    private int computeHashCode() {
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
            result = equalsRule(other) && equalsAnchorImage(other) && equalsCoContextMap(other);
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
    private boolean equalsRule(RuleEvent other) {
        return getRule().equals(other.getRule());
    }
    
    /**
     * Tests if anchor images of two rule events coincide.
     * Callback method from {@link #equals(Object)}.
     */
    private boolean equalsAnchorImage(SPOEvent other) {
//        boolean result = true;
//        Element[] anchorImage = getAnchorImage();
//        Element[] otherAnchorImage = other.getAnchorImage();
//        int anchorSize = anchorImage.length;
//        for (int i = 0; result && i < anchorSize; i++) {
//            result = anchorImage[i].equals(otherAnchorImage[i]);
//        }
        return Arrays.equals(getAnchorImage(), other.getAnchorImage());
    }
    
    /**
     * Tests if co-context maps of two rule events coincide.
     * Callback method from {@link #equals(Object)}.
     */
    private boolean equalsCoContextMap(SPOEvent other) {
    	return Arrays.equals(coRootImage, other.coRootImage);
    }
    
	@Override
	public String toString() {
	    StringBuffer result = new StringBuffer(getRule().getName().name());
	    result.append(getAnchorImageString());
	    return result.toString();
	}

	@Deprecated
    public Morphism getMatching(Graph host) {
    	Morphism result = null;
    	RuleMatch match = getMatch(host);
    	if (match != null) {
    		result = new DefaultMorphism(getRule().getTarget(), host, match.getElementMap());
        }
        return result;
    }

	/**
     * Computes a match based on the precomputed anchor map.
     */
    public RuleMatch getMatch(Graph host) {
    	RuleMatch result = null;
        if (isCorrectFor(host)) {
    		Iterator<VarNodeEdgeMap> eventMatchMapIter = getRule().getEventMatcher().getMatchIter(host, getAnchorMap());
        	Iterator<RuleMatch> matchIter = getRule().computeMatchIter(host, eventMatchMapIter);
        	if (matchIter.hasNext()) {
        		result = matchIter.next();
        	}
        }
        return result;
    }

    /**
	 * Tests if there is a matching of this event to a given host graph. A
	 * matching may fail to exist because the anchor map does not map into the
	 * host graph, or because conditions outside the anchor map are not
	 * fulfilled.
	 * @deprecated Use {@link #hasMatch(Graph)} instead
	 */
    @Deprecated
	public boolean hasMatching(Graph host) {
		return hasMatch(host);
	}

	/**
	 * Tests if there is a matching of this event to a given host graph. A
	 * matching may fail to exist because the anchor map does not map into the
	 * host graph, or because conditions outside the anchor map are not
	 * fulfilled.
	 */
	public boolean hasMatch(Graph host) {
        if (isCorrectFor(host)) {
        	Iterator<VarNodeEdgeMap> eventMatchMapIter = getRule().getEventMatcher().getMatchIter(host, getAnchorMap());
        	return getRule().computeMatchIter(host, eventMatchMapIter).hasNext();
        } else {
        	return false;
        }
	}
    
	/**
	 * Compares two events first on the basis of their rules,
	 * then lexicographically on the basis of their anchor images.
	 */
	public int compareTo(RuleEvent other) {
	    int result = getRule().compareTo(other.getRule());
	    if (result != 0) {
	        return result;
	    }
        // we have the same rule (so the other event is also a SPOEvent)
        Element[] anchorImage = getAnchorImage();
        // retrieve the other even't anchor image array
        Element[] otherAnchorImage = ((SPOEvent) other).getAnchorImage();
        // now compare the anchor images
        // find the first index in which the anchor images differ
        int upper = Math.min(anchorImage.length, otherAnchorImage.length);
        for (int i = 0; result == 0 && i < upper; i++) {
            result = anchorImage[i].compareTo(otherAnchorImage[i]);
        }
        if (result == 0) {
            return anchorImage.length - otherAnchorImage.length;
        } else {
            return result;
        }
    }

    /**
     * Tests if the anchor map fits into a given host graph.
     * @param host the graph to be tested
     * @return <code>true</code> if the anchor map images are all in <code>host</code>
     */
    private boolean isCorrectFor(Graph host) {
        reporter.start(GET_PARTIAL_MATCH);
        VarNodeEdgeMap anchorMap = getAnchorMap();
        boolean correct = true;
        Iterator<Edge> edgeImageIter = anchorMap.edgeMap().values().iterator();
        while (correct && edgeImageIter.hasNext()) {
            correct = host.containsElement(edgeImageIter.next());
        }
        if (correct) {
            Iterator<Node> nodeImageIter = anchorMap.nodeMap().values().iterator();
            while (correct && nodeImageIter.hasNext()) {
            	Node nodeImage = nodeImageIter.next();
                correct = nodeImage instanceof ValueNode || host.containsElement(nodeImage);
            }
        }
        reporter.stop();
        return correct;
    }

    /**
     * Returns the set of source elements that form the anchor image.
     */
    Element[] getAnchorImage() {
        return anchorImage;
    }
    
    /**
     * Callback method to lazily compute 
     * the set of source elements that form the anchor image.
     */
    private Element[] computeAnchorImage(VarNodeEdgeMap anchorMap) {
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
        	assert result[i] != null : String.format("No image for %s in anchor map %s", anchor[i], anchorMap);
        }
        reporter.stop();
        return result;
    }
    
    public boolean conflicts(RuleEvent other) {
    	boolean result;
    	if (other instanceof SPOEvent) {
    		result = false;
    		// check if the other creates edges that this event erases
			Iterator<Edge> myErasedEdgeIter = getSimpleErasedEdges().iterator();
			Set<Edge> otherCreatedEdges = ((SPOEvent) other).getSimpleCreatedEdges();
			while (!result && myErasedEdgeIter.hasNext()) {
				result = otherCreatedEdges.contains(myErasedEdgeIter.next());
			}
			if (!result) {
	    		// check if the other erases edges that this event creates
				Iterator<Edge> myCreatedEdgeIter = getSimpleCreatedEdges().iterator();
				Set<Edge> otherErasedEdges = ((SPOEvent) other).getSimpleErasedEdges();
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
        Iterator<Edge> edgeIter = getSimpleErasedEdges().iterator();
        while (!result && edgeIter.hasNext()) {
            result = anchorImage.contains(edgeIter.next());
        }
        return result;
    }

	/**
	 * Returns the set of source elements that form the anchor image.
	 */
	private Set<Element> getAnchorImageSet() {
	    if (anchorImageSet == null) {
	    	NodeEdgeMap anchorMap = getAnchorMap();
	        anchorImageSet = new HashSet<Element>(anchorMap.nodeMap().values());
	        anchorImageSet.addAll(anchorMap.edgeMap().values());
	    }
	    return anchorImageSet;
	}

	/**
	 * Returns the set of explicitly erased nodes, i.e., the images of the LHS
	 * eraser nodes.
	 */
	@Override
    public Set<Node> getErasedNodes() {
		if (reuse) {
			return getCache().getErasedNodes();
		} else {
			return computeErasedNodes();
		}
    }

	/**
	 * Computes the set of explicitly erased nodes, i.e., the
	 * images of the LHS eraser nodes.
	 * Callback method from {@link #getErasedNodes()}.
	 */
    @Override
	Set<Node> computeErasedNodes() {
        Node[] eraserNodes = getRule().getEraserNodes();
        if (eraserNodes.length == 0) {
            return EMPTY_NODE_SET;
        } else {
            Set<Node> result = createNodeSet();
            collectErasedNodes(result);
            return result;
        }
	}

	/**
	 * Adds the set of explicitly erased nodes, i.e., the
	 * images of the LHS eraser nodes, to a given result set.
	 * Callback method from {@link #computeErasedNodes()}.
	 */
	void collectErasedNodes(Set<Node> result) {
		NodeEdgeMap anchorMap = getAnchorMap();
		// register the node erasures
		for (Node node : getRule().getEraserNodes()) {
			result.add(anchorMap.getNode(node));
		}
	}

    /**
	 * Returns the set of explicitly erased edges, i.e., the images of the LHS
	 * eraser edges.
	 */
    public Set<Edge> getSimpleErasedEdges() {
    	if (reuse) {
    		return getCache().getSimpleErasedEdges();
    	} else {
    		return computeSimpleErasedEdges();
    	}
    }

    /**
     * Computes the set of explicitly erased edges, i.e., the
     * images of the LHS eraser edges.
     * Callback method from {@link #getSimpleErasedEdges()}.
     */
    Set<Edge> computeSimpleErasedEdges() {
        Set<Edge> result = createEdgeSet();
        collectSimpleErasedEdges(result);
        return result;
    }

    /**
     * Collects the set of explicitly erased edges, i.e., the
     * images of the LHS eraser edges, into a given result set.
     * Callback method from {@link #computeSimpleErasedEdges()}.
     */
    void collectSimpleErasedEdges(Set<Edge> result) {
        VarNodeEdgeMap anchorMap = getAnchorMap();
        Edge[] eraserEdges = getRule().getEraserEdges();
        for (Edge edge: eraserEdges) {
            Edge edgeImage = anchorMap.getEdge(edge);
            assert edgeImage != null : "Image of "+edge+" cannot be deduced from "+anchorMap;
            result.add(edgeImage);
        }
    }

    public Set<Edge> getSimpleCreatedEdges() {
    	if (reuse) {
    		return getCache().getSimpleCreatedEdges();
    	} else {
    		return computeSimpleCreatedEdges();
    	}
    }

    /**
     * Computes the set of explicitly erased edges, i.e., the
     * images of the LHS eraser edges.
     * Callback method from {@link #getSimpleErasedEdges()}.
     */
    Set<Edge> computeSimpleCreatedEdges() {
        Set<Edge> result = createEdgeSet();
        collectSimpleCreatedEdges(result);
        return result;
    }

    /**
     * Collects the set of explicitly erased edges, i.e., the
     * images of the LHS eraser edges, into a given set.
     * Callback method from {@link #computeSimpleErasedEdges()}.
     */
     void collectSimpleCreatedEdges(Set<Edge> result) {
        VarNodeEdgeMap coAnchorMap = getCoanchorMap();
        for (Edge edge: getRule().getSimpleCreatorEdges()) {
            Edge edgeImage = coAnchorMap.mapEdge(edge);
            if (edgeImage != null) {
                result.add(edgeImage);
            }
        }
    }

 	public Set<Edge> getComplexCreatedEdges(Iterator<Node> createdNodes) {
     	Set<Edge> result = createEdgeSet();
 		collectComplexCreatedEdges(createdNodes, result);
 		return result;
 	}

	void collectComplexCreatedEdges(Iterator<Node> createdNodes, Set<Edge> result) {
		VarNodeEdgeMap coanchorMap = getCoanchorMap().clone();
		// add creator node images
		for (Node creatorNode: getRule().getCreatorNodes()) {
			coanchorMap.putNode(creatorNode, createdNodes.next());
		}
        // now compute and add the complex creator edge images
        for (Edge edge : getRule().getComplexCreatorEdges()) {
            Edge image = coanchorMap.mapEdge(edge);
            // only add if the image exists
            if (image != null) {
                result.add(image);
            }
        }
	}

	/**
	 * Returns a mapping from source to target graph nodes, dictated by
	 * the merger and eraser nodes in the rules. 
	 * @return an {@link MergeMap} that maps nodes of the
	 * source that are merged away to their merged images, and deleted nodes to <code>null</code>.
	 */
	public MergeMap getMergeMap() {
	    return getCache().getMergeMap();
    }

	/**
	 * Creates an array of lists to store the fresh nodes
	 * created by this rule.
	 */
	private List<List<Node>> createFreshNodeList() {
		int creatorNodeCount = getRule().getCreatorNodes().length;
		List<List<Node>> result = new ArrayList<List<Node>>();
        for (int i = 0; i < creatorNodeCount; i++) {
        	result.add(new ArrayList<Node>());
        }
        return result;
	}
    
    public List<Node> getCreatedNodes(Set<? extends Node> hostNodes) {
		List<Node> result = computeCreatedNodes(hostNodes);
		if (reuse) {
		    if (coanchorImageMap == null) {
		        coanchorImageMap = new HashMap<List<Node>, List<Node>>();
		    }
            List<Node> existingResult = coanchorImageMap.get(result);
            if (existingResult == null) {
                coanchorImageMap.put(result, result);
                coanchorImageCount++;
            } else {
                result = existingResult;
                coanchorImageOverlap++;
            }
        }
        return result;
    }

    private List<Node> computeCreatedNodes(Set<? extends Node> currentNodes) {
		List<Node> result;
		int coanchorSize = getRule().getCreatorNodes().length;
		if (coanchorSize == 0) {
			result = EMPTY_COANCHOR_IMAGE;
		} else {
			result = new ArrayList<Node>(coanchorSize);
			collectCreatedNodes(currentNodes, result);
		}
		return result;
    }

    void collectCreatedNodes(Set<? extends Node> currentNodes, List<Node> result) {
		int coanchorSize = getRule().getCreatorNodes().length;
		for (int i = 0; i < coanchorSize; i++) {
			result.add(getFreshNode(i, currentNodes));
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
	 * @param currentNodes
	 *            the existing nodes, which should not contain the fresh node
	 */
	public Node getFreshNode(int creatorIndex, Set<? extends Node> currentNodes) {
		Node result = null;
		Collection<Node> currentFreshNodes = getFreshNodes(creatorIndex);
		if (currentFreshNodes != null) {
            Iterator<Node> freshNodeIter = currentFreshNodes.iterator();
            while (result == null && freshNodeIter.hasNext()) {
                Node freshNode = freshNodeIter.next();
                if (!currentNodes.contains(freshNode)) {
                    result = freshNode;
                }
            }
        }
		if (result == null) {
			result = createNode();
			if (currentFreshNodes != null) {
			    currentFreshNodes.add(result);
			}
		}
		return result;
	}

    /**
     * Callback factory method for a newly constructed node.
     * This implementation returns a {@link DefaultNode}, with
     * a node number determined by the grammar's node counter.
     */
    private Node createNode() {
        DefaultApplication.freshNodeCount++;
    	NodeFactory record = getNodeFactory();
    	Node result = record == null ? DefaultNode.createNode() : record.newNode();
    	return result;
    }

    /** 
     * Returns the derivation record associated with this event. 
     * May be <code>null</code>.
     */
    private NodeFactory getNodeFactory() {
    	return nodeFactory;
    }

	/**
	 * Returns the list of all previously created fresh nodes.
	 * Returns <code>null</code> if the reuse policy is set to <code>false</code>.
	 */
    private List<Node> getFreshNodes(int creatorIndex) {
        if (reuse) {
            if (freshNodeList == null) {
                freshNodeList = createFreshNodeList();
            }
            return freshNodeList.get(creatorIndex);
        } else {
            return null;
        }
    }

    @Override
	protected SPOEventCache createCache() {
		return new SPOEventCache();
	}

	/** The derivation record that has created this event, if any. */
    private final NodeFactory nodeFactory;
    /**
     * Images for the RHS root elements in the target graph. 
     */
    final Node[] coRootImage;
//    /**
//     * The footprint of a derivation consists of the anchor images of the match
//     * together with the images of the creator nodes.
//     * This corresponds to is the information needed to (re)construct the derivation target.
//     */
//    private Element[] anchorImage;
    /**
     * The set of source elements that form the anchor image.
     */
    private Set<Element> anchorImageSet;
    /**
     * The array of source elements that form the anchor image.
     */
    private final Element[] anchorImage;
    /**
	 * The precomputed hash code.
	 */
	private int hashCode;
	/**
	 * The list of nodes created by {@link #createNode()}.
	 */
	private List<List<Node>> freshNodeList;
	/** Store of previously used coanchor images. */
	private Map<List<Node>, List<Node>> coanchorImageMap;
	/** Flag indicating if sets should be stored for reuse. */
	private final boolean reuse;
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
	/** Counter for the reuse in coanchor images. */
	static private int coanchorImageOverlap;
	/** Counter for the coanchor images. */
	static private int coanchorImageCount;
	/** Global empty set of nodes. */
	static private final Set<Node> EMPTY_NODE_SET = Collections.<Node>emptySet();
    /** Global empty list of nodes. */
    static private final List<Node> EMPTY_COANCHOR_IMAGE = Collections.emptyList();
    /** Global empty list of nodes. */
    static private final Node[] EMPTY_ROOT_IMAGE = new Node[0];
    static private final CacheReference<SPOEventCache> reference = CacheReference.<SPOEventCache>newInstance(false);
	static private Reporter reporter = Reporter.register(RuleEvent.class);
	static private int HASHCODE = reporter.newMethod("computeHashCode()");
	static private int EQUALS = reporter.newMethod("equals()");
	static private int GET_PARTIAL_MATCH = reporter.newMethod("getPartialMatch()");
	static private int GET_ANCHOR_IMAGE = reporter.newMethod("getAnchorImage()");
	
    /** Cache holding the anchor map. */
    final class SPOEventCache extends AbstractEvent<SPORule,SPOEventCache>.AbstractEventCache {
		/**
		 * @return Returns the anchorMap.
		 */
		public final VarNodeEdgeMap getAnchorMap() {
			if (anchorMap == null) {
				anchorMap = computeAnchorMap();
			}
			return anchorMap;
		}

		/**
		 * Creates the normalised anchor map from the currently stored anchor map.
		 * The resulting map contains images for the anchor and eraser edges 
		 * and any variables on them.
		 */
	    private VarNodeEdgeMap computeAnchorMap() {
	    	Element[] anchor = getRule().anchor();
	    	Element[] anchorImage = getAnchorImage();
	    	VarNodeEdgeMap result = createVarMap();
	    	for (int i = 0; i < anchor.length; i++) {
	    		Element key = anchor[i];
	    		Element image = anchorImage[i];
	            if (key instanceof Edge) {
	            	// store the endpoints and the variable valuations for the edges
	                Edge edgeKey = (Edge) key;
	            	Edge edgeImage = (Edge) image;
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
	                result.putNode((Node) key, (Node) image);
	            }
	        }
	        // add the eraser edges
	        for (Edge eraserEdge: getRule().getEraserNonAnchorEdges()) {
	            Edge eraserImage = result.mapEdge(eraserEdge);
	            assert eraserImage != null : String.format("Eraser edge %s has no image in anchor map %s", eraserEdge, result);
//	            result.putEdge(eraserEdge, eraserImage);
	        }
	        return result;
	    }

	    /**
	     * Constructs a map from the reader nodes of the RHS that are endpoints of
	     * creator edges, to the target graph nodes.
	     */
	    public final VarNodeEdgeMap getCoanchorMap() {
			if (coanchorMap == null) {
				coanchorMap = computeCoanchorMap();
			}
			return coanchorMap;
		}
	    
		/**
		 * Constructs a map from the reader nodes of the RHS that are endpoints
		 * of creator edges, to the target graph nodes.
		 */
		private VarNodeEdgeMap computeCoanchorMap() {
			final VarNodeEdgeMap result = createVarMap();
			VarNodeEdgeMap anchorMap = getAnchorMap();
			NodeEdgeMap mergeMap = getRule().hasMergers() ? getMergeMap() : null;
			Set<Node> erasedNodes = getErasedNodes();
			// add reader node images
			for (Map.Entry<Node,Node> creatorEntry: getRule().getCreatorMap().nodeMap().entrySet()) {
				Node creatorKey = creatorEntry.getKey();
				Node creatorValue = anchorMap.getNode(creatorEntry.getValue());
				if (mergeMap != null) {
					creatorValue = mergeMap.getNode(creatorValue);
				} else if (erasedNodes.contains(creatorValue)) {
				    creatorValue = null;
				}
				if (creatorValue != null) {
				    result.putNode(creatorKey, creatorValue);
				}
			}
			int coRootIx = 0;
			for (Map.Entry<Node,Node> coRootEntry: getRule().getCoRootMap().nodeMap().entrySet()) {
				result.putNode(coRootEntry.getValue(), coRootImage[coRootIx]);
				coRootIx++;
			}
			// add variable images
			for (String var: getRule().getCreatorVars()) {
				result.putVar(var, anchorMap.getVar(var));
			}
			return result;
		}

		/**
		 * Returns a mapping from source to target graph nodes, dictated by
		 * the merger and eraser nodes in the rules. 
		 * @return an {@link MergeMap} that maps nodes of the
		 * source that are merged away to their merged images, and deleted nodes to <code>null</code>.
		 */
		public final MergeMap getMergeMap() {
			if (mergeMap == null) {
				mergeMap = computeMergeMap();
			}
			return mergeMap;
		}

	    /**
		 * Callback method from {@link #getMergeMap()} to compute the merge map.
		 * This is constructed on the basis of matching and rule, without
		 * reference to the actual target graph, which indeed may not yet be
		 * constructed at the time of invoking this method. The map is an
		 * {@link MergeMap} to improve performance.
		 */
	    private MergeMap computeMergeMap() {
	    	VarNodeEdgeMap anchorMap = getAnchorMap();
	        MergeMap mergeMap = createMergeMap();
	        for (Map.Entry<Node,Node> ruleMergeEntry: getRule().getMergeMap().entrySet()) {
	            Node mergeKey = anchorMap.getNode(ruleMergeEntry.getKey());
	            Node mergeImage = anchorMap.getNode(ruleMergeEntry.getValue());
	            mergeMap.putNode(mergeKey, mergeImage);
	        }
	        // now map the erased nodes to null
	        for (Node node: getErasedNodes()) {
	            mergeMap.removeNode(node);
	        }
	        return mergeMap;
	    }

	    /**
	     * Returns the pre-computed and cached set of explicitly erased edges.
	     */
	    public Set<Edge> getSimpleErasedEdges() {
			if (erasedEdgeSet == null) {
				erasedEdgeSet = computeSimpleErasedEdges();
			}
			return erasedEdgeSet;
		}

	    /**
	     * Returns the pre-computed and cached set of explicitly erased edges.
	     */
	    final public Set<Edge> getSimpleCreatedEdges() {
			if (simpleCreatedEdgeSet == null) {
				simpleCreatedEdgeSet = computeSimpleCreatedEdges();
			}
			return simpleCreatedEdgeSet;
		}

	    /**
		 * Callback factory method to create the merge map object for
		 * {@link #computeMergeMap()}.
		 * 
		 * @return a fresh instance of {@link MergeMap}
		 */
	    private MergeMap createMergeMap() {
			return new MergeMap();
		}
	    /**
	     * Callback factory method to create the map object for 
	     * {@link #computeCoanchorMap()}.
	     * @return a fresh instance of {@link VarNodeEdgeHashMap}
	     */
	    private VarNodeEdgeMap createVarMap() {
	    	return new VarNodeEdgeHashMap();
	    }

        /**
         * Matching from the rule's lhs to the source graph.
         */
    	private VarNodeEdgeMap anchorMap;
        /**
         * Matching from the rule's rhs to the target graph.
         */
    	private VarNodeEdgeMap coanchorMap;
        /**
         * Minimal mapping from the source graph to target graph to reconstruct the underlying morphism. 
         * The merge map is constructed in the course of rule application.
         */
        private MergeMap mergeMap;
        /**
         * Set of edges from the source that are to be erased in the target.
         */
        private Set<Edge> erasedEdgeSet;
        /**
         * Images of the simple creator edges.
         */
        private Set<Edge> simpleCreatedEdgeSet;
    }
}
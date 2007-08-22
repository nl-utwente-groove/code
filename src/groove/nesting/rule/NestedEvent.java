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
 * $Id: NestedEvent.java,v 1.1 2007-08-22 09:19:47 kastenberg Exp $
 */
package groove.nesting.rule;

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
import java.util.TreeSet;

import groove.graph.AbstractBinaryEdge;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.MergeMap;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.graph.NodeSet;
import groove.graph.WrapperLabel;
import groove.nesting.VarNodeEdgeMultiHashMap;
import groove.nesting.VarNodeEdgeMultiMap;
import groove.rel.RegExprGraph;
import groove.rel.RegExprMorphism;
import groove.rel.VarGraph;
import groove.rel.VarMorphism;
import groove.rel.VarNodeEdgeHashMap;
import groove.rel.VarNodeEdgeMap;
import groove.trans.DefaultConditionOutcome;
import groove.trans.GraphCondition;
import groove.trans.GraphConditionOutcome;
import groove.trans.GraphPredicateOutcome;
import groove.trans.GraphTestOutcome;
import groove.trans.Matching;
import groove.trans.Rule;
import groove.trans.RuleApplication;
import groove.trans.RuleEvent;
import groove.trans.RuleNameLabel;
import groove.trans.SPOApplication;
import groove.trans.SPOEvent;
import groove.trans.SPORule;
import groove.trans.SystemRecord;
import groove.util.Groove;
import groove.util.Pair;
import groove.util.Reporter;
import groove.util.TreeHashSet3;

/**
 *
 * @author kramor
 * @version 0.1 $Revision: 1.1 $ $Date: 2007-08-22 09:19:47 $
 */
public class NestedEvent implements RuleEvent {

	/* (non-Javadoc)
	 * @see groove.trans.SPOEvent#getRule()
	 */
	public NestedRule getRule() {
		return this.rule;
	}

	/* (non-Javadoc)
	 * @see groove.trans.SPOEvent#newApplication(groove.graph.Graph)
	 */
	public RuleApplication newApplication(Graph host) {
		return new NestedApplication(this, host);
	}
	
	/** Rule with which this event is associated */
	private NestedRule rule;
    /** Flag to indicate that the {@link #hashCode} variable has been initialized. */
    private boolean hashCodeSet;
    /** The set of source elements that form the anchor image. */
    private Set<Element> anchorImageSet;
    /** The array of source elements that form the anchor image. */
    private Element[] anchorImage;
    /** The anchorMap */
    private VarNodeEdgeMultiMap anchorMap;
    /**
     * Mapping from selected RHS elements to target graph. 
     * The comatch is constructed in the course of rule application.
     */
    private VarNodeEdgeMultiMap coanchorMap;
    /** The precomputed hash code. */
	private int hashCode;
	/** SystemRecord */
	private SystemRecord record;
	/** Toplevel matching for this Event */
	private Matching topLevelMatch;
	/** Outcome of the GraphTest for this Event */
	private GraphTestOutcome<GraphCondition, Matching> outcome;
	/** The list of nodes created by {@link #createNode()}. */
	private final List<List<Node>> freshNodeList;
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
    private Set<Edge> complexCreatorEdgeSet;

    //private Map<String, Set<Matching>> ruleMatchings;
    
    /**
     * Protected constructor which allows NestedEvent 2 to initialize this class
     * without taking up extra space.
     * If NestedEvent2 works better than this one, just remove NestedEvent and rename
     * NestedEvent2 to NestedEvent
     */
    protected NestedEvent() {
    	freshNodeList = null;
    }
    
	/**
	 * Constructs a new NestedEvent
	 * @param rule
	 * @param anchorMap
	 */
	public NestedEvent(NestedRule rule, VarNodeEdgeMultiMap anchorMap, Matching match, GraphTestOutcome<GraphCondition, Matching> outcome, SystemRecord record) {
		rule.setFixed();
		this.rule = rule;
		this.anchorMap = anchorMap;
		this.topLevelMatch = match;
		this.outcome = outcome;
		this.record = record;
		/**
		 * Dit is misschien leuk, maar (nog) niet nodig
		this.ruleMatchings = new HashMap<String, Set<Matching>> ();
		// Add the levels in the rule as keys
		for( String level : rule.getSubruleMap().keySet() ) {
			ruleMatchings.put(level, new HashSet<Matching> ());
		}
		ruleMatchings.get(rule.getLevel()).add(match);
		fillMatchingMap(outcome);
		for( Map.Entry<String, Set<Matching>> entry : ruleMatchings.entrySet() ) {
			if( entry.getValue().isEmpty() ) {
				// TODO: geen ConcurrentModificationException hier??
				ruleMatchings.remove(entry.getKey());
			}
		}
		//System.out.println(ruleMatchings);
		 */
		this.freshNodeList = createFreshNodeList();
	}
	
	/*
	private void fillMatchingMap(GraphTestOutcome<GraphCondition, Matching> outcome) {
		for( GraphCondition cond : outcome.keySet() ) {
			if( cond instanceof ExistentialLevel ) {
				NestedRule ruleAtThisLevel = ((ExistentialLevel)cond).getRule();
				for( Matching match : outcome.get(cond).keySet() ) {
					ruleMatchings.get(ruleAtThisLevel.getLevel()).add(match);
				}
			}
			for( Matching match : outcome.get(cond).keySet() ) {
				fillMatchingMap(outcome.get(cond).get(match));
			}
		}
	}*/
	
	/**
	 * 
	 * @param edge
	 * @return
	 */
	public Set<Edge> imagesFor(Edge edge, VarNodeEdgeMultiMap coanchorMap, Node[] coanchorImage) {
		Set<Edge> result = new HashSet<Edge> ();
		
		//VarNodeEdgeMultiMap coanchorMap = getCoanchorMap();
        // if this edge has an explicit image in the map, use that
        Set<Edge> image = coanchorMap.getEdge(edge);
        if (image != null) {
            return image;
        }
        // Checking if one of its nodes will be deleted...
        Set<Node> tmpSet = coanchorMap.getNode(edge.source());
        if( tmpSet == null ) return Collections.EMPTY_SET; 
        TreeSet<Node> sourceImage = new TreeSet<Node> (tmpSet);
        tmpSet = coanchorMap.getNode(edge.opposite());
        if( tmpSet == null ) return Collections.EMPTY_SET; 
        TreeSet<Node> targetImage = new TreeSet<Node> (tmpSet);
        Label labelImage = coanchorMap.getLabel(edge.label());
        assert targetImage.size() == sourceImage.size() : "Coanchor count for edge ends not equal!";
        Iterator<Node> srcIt = sourceImage.iterator();
        Iterator<Node> tgtIt = targetImage.iterator();
        Node srcImage = null;//srcIt.next();
        Node tgtImage = null;//tgtIt.next();
        do {
        	if( srcIt.hasNext() ) srcImage = srcIt.next();
        	if( tgtIt.hasNext() ) tgtImage = tgtIt.next();
        	if( edge.source() == srcImage && edge.opposite() == tgtImage && edge.label() == labelImage ) {
        		result.add(edge);
        	} else {
        		result.add( ((AbstractBinaryEdge)edge).newEdge(srcImage, labelImage, tgtImage) );
        	}
        } while( srcIt.hasNext() || tgtIt.hasNext() );
		
		// TODO : use coanchorImage to keep things together
		return result;
	}
	
	private Pair<Integer, Integer> findSources(Element node, Element[] search, int[] occurrences) {
		int cnt = 0;
		for( int i = 0 ; i < search.length ; i++ ) {
			if( node == search[i] ) {
				return new Pair<Integer, Integer> (i, cnt);
			}
			cnt += occurrences[i];
		}
		return null;
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
	public RuleNameLabel getName() {
		return getRule().getName();
	}

	public VarNodeEdgeMultiMap getAnchorMap() {
		/*
		if (!anchorMapNormalised) {
			anchorMap = computeNormalisedAnchorMap();
			anchorMapNormalised = true;
		}*/
		// TODO : moet ik dit normaliseren?
	    return anchorMap;
	}
	
    /**
     * Returns a string starting with {@link #ANCHOR_START}, separated by
     * {@link #ANCHOR_SEPARATOR} and ending with {@link #ANCHOR_END}.
     */
    public String getAnchorImageString() {
    	return Groove.toString(getAnchorImage(), SPOEvent.ANCHOR_START, SPOEvent.ANCHOR_END, SPOEvent.ANCHOR_SEPARATOR);
	}
	
	/* (non-Javadoc)
	 * @see groove.trans.SPOEvent#getCoanchorImage(groove.graph.Graph)
	 */
	protected Node[] getCoanchorImage(Graph host) {
		int coanchorSize = getRule().coanchor().length;
		int[] occurrences = getNodeCreatorOccurrenceCount();
		ArrayList<Node> result = new ArrayList<Node> ();
		for ( int i = 0 ; i < coanchorSize; i++) {
			for( int j = 0 ; j < occurrences[i] ; j++ ) {
				result.add(getFreshNode(i, host, result));
			}
		}
		Node[] existingResult = coanchorImageMap.get(result);
		if (existingResult == null) {
			Node[] newResult = result.toArray(new Node[0]);
			coanchorImageMap.put(result, newResult);
			existingResult = newResult;
			coanchorImageCount++;
		} else {
			coanchorImageOverlap++;
		}
		return existingResult;
    }
	
    public boolean conflicts(RuleEvent other) {
    	boolean result;
    	if (other instanceof NestedEvent) {
    		result = false;
    		// check if the other creates edges that this event erases
			Iterator<Edge> myErasedEdgeIter = getErasedEdges().iterator();
			Set<Edge> otherCreatedEdges = ((NestedEvent) other).getSimpleCreatedEdges();
			while (!result && myErasedEdgeIter.hasNext()) {
				result = otherCreatedEdges.contains(myErasedEdgeIter.next());
			}
			if (!result) {
	    		// check if the other erases edges that this event creates
				Iterator<Edge> myCreatedEdgeIter = getSimpleCreatedEdges().iterator();
				Set<Edge> otherErasedEdges = ((NestedEvent) other).getErasedEdges();
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
		VarNodeEdgeMultiMap anchorMap = getAnchorMap();
		Set<Node> erasedNodes = createNodeSet();
		for( NestedRule rule : getRule().getSubrules() ) {
			Node[] eraserNodes = rule.getEraserNodes();
			// register the node erasures
			for (int i = 0; i < eraserNodes.length; i++) {
				Set<Node> nodeMatch = anchorMap.getNode(eraserNodes[i]);
				if( nodeMatch != null ) {
					erasedNodes.addAll(nodeMatch);
				}
			}
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
        VarNodeEdgeMultiMap anchorMap = getAnchorMap();
        for( NestedRule rule : getRule().getSubrules() ) {
	        Edge[] eraserEdges = rule.getEraserEdges();
	        for (int i = 0; i < eraserEdges.length; i++) {
	            Edge edge = eraserEdges[i];
	            Set<Edge> edges = anchorMap.getEdge(edge);
	            if( edges != null ) {
		            for( Edge edgeImage : edges ) {
		            	if (edgeImage == null) {
		                    edgeImage = edge.imageFor(anchorMap);
		                    assert edgeImage != null : "Image of "+edge+" cannot be deduced from "+anchorMap;
		                }
		                result.add(edgeImage);
		            }
	            }
	        }
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
    
    private Set<Edge> computeSimpleCreatedEdges() {
        Set<Edge> result = createEdgeSet();
        for( NestedRule rule : getRule().getSubrules() ) {
        	for( Edge edge : rule.getSimpleCreatorEdges() ) {
        		result.add(edge);
        	}
        }
        /*
        VarNodeEdgeMultiMap coAnchorMap = getCoanchorMap();
        for( NestedRule rule : getRule().getSubrules() ) {
	        for (Edge edge: rule.getSimpleCreatorEdges()) {
	            Edge edgeImage = edge.imageFor(coAnchorMap);
	            if (edgeImage != null) {
	                result.add(edgeImage);
	            }
	        }
        }*/
        return result;
    }
    
    protected Set<Edge> getComplexCreatorEdges() {
        if (complexCreatorEdgeSet == null) {
            complexCreatorEdgeSet = computeComplexCreatorEdges();
        }
        return complexCreatorEdgeSet;
    }
    
    private Set<Edge> computeComplexCreatorEdges() {
        Set<Edge> result = createEdgeSet();
        for( NestedRule rule : getRule().getSubrules() ) {
	        for (Edge edge: rule.getComplexCreatorEdges()) {
	        	result.add(edge);
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
    	VarNodeEdgeMultiMap anchorMap = getAnchorMap();
        MergeMap mergeMap = createMergeMap();
        // integrate the mergings
        // the pre-morphism should be "flat" in the sense that any non-null value of an entry
        // should itself be a fixpoint of the pre-morphism
        for( NestedRule rule : getRule().getSubrules() ) {
	        for (Map.Entry<Node,Node> ruleMergeEntry: rule.getMergeMap().entrySet()) {
	            Set<Node> mergeKey = anchorMap.getNode(ruleMergeEntry.getKey());
	            Set<Node> mergeImage = anchorMap.getNode(ruleMergeEntry.getValue());
	            Iterator<Node> mergeImageIt = mergeImage.iterator();
	            for( Node node : mergeKey ) {
	            	mergeMap.putNode(node, mergeImageIt.next());
	            }
	        }
        }
        // now map the erased nodes to null
        for (Node node: getErasedNodes()) {
            mergeMap.removeNode(node);
        }
        return mergeMap;
    }
    
	public VarNodeEdgeMultiMap getCoanchorMap() {
        if (coanchorMap == null) {
            coanchorMap = computeCoanchorMap();
        }
        return coanchorMap;
    }
    
	/**
	 * Constructs a map from the reader nodes of the RHS that are endpoints of
	 * creator edges, to the target graph nodes.
	 */
	protected VarNodeEdgeMultiMap computeCoanchorMap() {
		final VarNodeEdgeMultiMap result = createVarMap();
		VarNodeEdgeMultiMap anchorMap = getAnchorMap();
		NodeEdgeMap mergeMap = getRule().hasMergers() ? getMergeMap() : null;
		// add reader node images
		//for( NestedRule rule : getRule().getSubrules() ) {
			for (Map.Entry<Node,Node> creatorEntry: getRule().getCreatorMap().entrySet()) {
				Node creatorKey = creatorEntry.getKey();
				Set<Node> creatorValue = anchorMap.getNode(creatorEntry.getValue());
				if( creatorValue != null ) {
					for( Node creatorNode : creatorValue ) {
						if (mergeMap != null) {
							creatorNode = mergeMap.getNode(creatorNode);
						}
						result.putNode(creatorKey, creatorNode);
					}
				}
			}
			// add variable images
			for (String var: getRule().getCreatorVars()) {
				result.putVar(var, anchorMap.getVar(var));
			}
		//}
		return result;
	}
	
	/** Array containing the number of times each creator node occurs */
	private int[] creatorOccurrences;
	/** Array containing the number of times each creator edge occurs */
	private int[] creatorEdgeOccurrences;
	
	/**
	 * Get the array containing the number of times each creator node 
	 * occurs
	 * @return
	 */
	public int[] getNodeCreatorOccurrenceCount() {
		if( creatorOccurrences == null ) {
			creatorOccurrences = computeNodeCreatorOccurrenceCount();
		}
		return creatorOccurrences;
	}
	
	/**
	 * Goes through the GraphTestOutcome to determine how many times a creator
	 * Node occurs
	 * @return an array with number of occurrence
	 */
	protected int[] computeNodeCreatorOccurrenceCount() {
		Node[] coanchor = getRule().coanchor();
		int[] result = new int[coanchor.length];
		for( int i = 0 ; i < coanchor.length ; i++ ) {
			result[i] = computeCreatorOccurrence(coanchor[i], outcome);
		}
		
		return result;
	}
	
	public int[] getEdgeCreatorOccurrenceCount() {
		if( creatorEdgeOccurrences == null ) {
			creatorEdgeOccurrences = computeEdgeCreatorOccurrenceCount();
		}
		return creatorEdgeOccurrences;
	}
	
	/**
	 * 
	 * @return
	 */
	protected int[] computeEdgeCreatorOccurrenceCount() {
		Edge[] creatorEdges = getRule().getCreatorEdges();
		int[] result = new int[creatorEdges.length];
		for( int i = 0 ; i < creatorEdges.length ; i++ ) {
			result[i] = computeCreatorOccurrence(creatorEdges[i], outcome);
		}
		return result;
	}
	
	private int computeCreatorOccurrence(Element e, GraphTestOutcome<GraphCondition, Matching> outcome) {
		int total = 0;
		if( getRule().rhs().containsElement(e) ) {
			// The element is in the top rule, only one should be created!
			return 1;
		}
		for( GraphCondition condition : outcome.keySet() ) {
			boolean foundCreator = false;
			if( condition instanceof ExistentialLevel ) {
				if( ((ExistentialLevel)condition).getRule().rhs().containsElement(e) ) {
					total++;
					foundCreator = true;
				}
			}
			
			if( ! foundCreator ) {
				// If the creator was found, don't count its occurrences on deeper levels!
				for( Matching match : outcome.get(condition).keySet() ) {
					total += computeCreatorOccurrence(e, outcome.get(condition).get(match));
				}
			}
		}
		return total;
	}
	
    /**
	 * Computes a matching based on the given anchor images, but without
	 * computing its total extension. Return <code>null</code> if any of the
	 * anchor images is not in the source graph.
	 */
	public boolean hasMatching(Graph host) {
		GraphConditionOutcome outcome = getRule().getOutcome(host); 
		//System.out.println("NE:Graph used:" + host.hashCode() + ", Rule used:" + getRule().hashCode() + ", Successkeys:"+ outcome.getSuccessKeys().size());
        return ! outcome.getSuccessKeys().isEmpty();
	}
	
	/**
     * Callback factory method for a newly constructed node.
     * This implementation returns a {@link DefaultNode}, with
     * a node number determined by the grammar's node counter.
     */
    protected Node createNode() {
        NestedApplication.freshNodeCount++;
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
	public Node getFreshNode(int creatorIndex, Graph graph, List<Node> notOneOfThese) {
		Node result = null;
		Collection<Node> currentFreshNodes = getFreshNodes(creatorIndex);
		Iterator<Node> freshNodeIter = currentFreshNodes.iterator();
		while (result == null && freshNodeIter.hasNext()) {
			Node freshNode = freshNodeIter.next();
			if (!graph.containsElement(freshNode) && !notOneOfThese.contains(freshNode)) {
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
     * Returns the derivation record associated with this event. 
     * May be <code>null</code>.
     */
    protected SystemRecord getRecord() {
    	return record;
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
        ArrayList<Element> result = new ArrayList<Element> ();
        for (int i = 0; i < anchorSize; i++) {
        	//if( anchorMap.containsKey(anchor[i]) ) {
        	if (anchor[i] instanceof Node) {
        		result.addAll(anchorMap.getNode((Node) anchor[i]));
        	} else {
        		result.addAll(anchorMap.getEdge((Edge) anchor[i]));
        	}
        	//}
        }
        reporter.stop();
        return result.toArray(new Element[0]);
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
			if (other instanceof NestedEvent) {
				otherAnchorImage = ((NestedEvent) other).getAnchorImage();
			} else {
				/**
				// construct anchor image of the other event
				Element[] anchor = getRule().anchor();
				VarNodeEdgeMultiMap otherAnchorMap = other.getAnchorMap();
				otherAnchorImage = new Element[anchor.length];
				for (int i = 0; i < anchor.length; i++) {
					Element anchorElement = anchor[i];
					if (anchorElement instanceof Node) {
						otherAnchorImage[i] = otherAnchorMap.getNode((Node) anchorElement);
					} else {
						otherAnchorImage[i] = otherAnchorMap.getEdge((Edge) anchorElement);
					}
				}*/
				throw new RuntimeException("If we get here.... fix this block");
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
				if( anchorImage.length == 0 ) {
					return new Integer(this.hashCode()).compareTo(other.hashCode());
				}
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
    protected VarNodeEdgeMultiMap createVarMap() {
    	return new VarNodeEdgeMultiHashMap();
    }
    
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


	/* (non-Javadoc)
	 * @see groove.trans.RuleEvent#getMatching(groove.graph.Graph)
	 */
	public Morphism getMatching(Graph source) {
		throw new RuntimeException("Fix this call!");
	}

	/* (non-Javadoc)
	 * @see groove.trans.RuleEvent#getOutcome(groove.graph.Graph)
	 */
	public GraphConditionOutcome getOutcome(Graph source) {
		Map<Matching, GraphPredicateOutcome> map = new HashMap<Matching, GraphPredicateOutcome> ();
		map.put(this.topLevelMatch, (GraphPredicateOutcome)this.outcome);
		VarMorphism m = new RegExprMorphism(new RegExprGraph(), source);
		GraphConditionOutcome outcome = new DefaultConditionOutcome(getRule(), m, map);
		return outcome;
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
    	if( true ) {
    		return System.identityHashCode(this);
    	}
    	reporter.start(HASHCODE);
        int result = getRule().hashCode();
        // we don't use getAnchorImage() because the events are often
        // just created to look up a stored event; then we shouldn't spend too
        // much time on this one
        Element[] anchors = getRule().anchor();
        VarNodeEdgeMultiMap anchorMap = getAnchorMap();
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
    	} else if (obj instanceof NestedEvent) {
        	reporter.start(EQUALS);
        	NestedEvent other = (NestedEvent) obj;
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
    protected boolean equalsAnchorImage(NestedEvent other) {
//        boolean result = true;
//        Element[] anchorImage = getAnchorImage();
//        Element[] otherAnchorImage = other.getAnchorImage();
//        int anchorSize = anchorImage.length;
//        for (int i = 0; result && i < anchorSize; i++) {
//            result = anchorImage[i].equals(otherAnchorImage[i]);
//        }
    	return this.anchorMap.equals(other.anchorMap);
        //return Arrays.equals(getAnchorImage(), other.getAnchorImage());
    }
    


}

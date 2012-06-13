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
 * $Id: SPORule.java,v 1.1.1.2 2007-03-20 10:42:57 kastenberg Exp $
 */
package groove.trans;

import groove.graph.DefaultInjectiveMorphism;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.rel.RegExprLabel;
import groove.rel.VarEdge;
import groove.rel.VarNodeEdgeMap;
import groove.rel.VarGraph;
import groove.util.ExprFormatException;
import groove.util.Groove;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Default model of a graph production rule.
 * This implementation assumes simple graphs, and yields 
 * <tt>DefaultTransformation</tt>s.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public class SPORule extends DefaultGraphCondition implements Rule {

    /** Returns the current anchor factory for all rules. */
    public static AnchorFactory getAnchorFactory() {
        return anchorFactory;
    }

    /**
     * Sets the anchor factory for all rules.
     * Only affects rules created from this moment on.
     */
    public static void setAnchorFactory(AnchorFactory anchorFactory) {
        SPORule.anchorFactory = anchorFactory;
    }

    /**
     * Returns the total time doing transformation-related computations.
     */
    static public long getTransformingTime() {
        return DefaultDeriver.reporter.getTotalTime(DefaultDeriver.GET_DERIVATIONS) - getMatchingTime();
    }
    
    /**
     * Returns the total time doing matching-related computations.
     * This includes time spent in cerftificate calculation.
     */
    static public long getMatchingTime() {
        return DefaultGraphCondition.reporter.getTotalTime(GET_MATCHING);
    }
    
    /**
     * Returns the number of events created in the course of rule application.
     */
    static public int getEventCount() {
    	return eventCount;
    }

    /**
     * The total number of events (over all rules) created in {@link #getEvent(VarNodeEdgeMap)}.
     */
    private static int eventCount;
    
    /**
     * The factory used for creating rule anchors.
     */
    private static AnchorFactory anchorFactory = MinimalAnchorFactory.getInstance(); 

    /**
     * @param morph the morphism on which this production is to be based
     * @param name the name of the new rule
     * @param ruleFactory the factory this rule used to instantiate related classes
     */
    public SPORule(Morphism morph, NameLabel name, int priority, RuleFactory ruleFactory) {
    	this(morph, name, ruleFactory);
    	this.priority = priority;
    }

    /**
     * Constructs a rule on the basis of a given morphism, and with a given name.
     * The priority is initially set to {@link #DEFAULT_PRIORITY}.
     * After constructing the rule, the morphism should not be modified!
     * @param morph the morphism on which this production is to be based.
     * @param name the name of the new rule
     * @ensure morphism().equals(morphism);
     *         lhs().equals(morph.dom());
     *         rhs().equals(morph.cod())
     */
    public SPORule(Morphism morph, NameLabel name, RuleFactory ruleFactory) {
        super((VarGraph) morph.dom().newGraph(), (VarGraph) morph.dom(), name, ruleFactory);
        if (CONSTRUCTOR_DEBUG) {
            Groove.message("Constructing rule: " + name);
            Groove.message("Rule morphism: " + morph);
        }
        this.isInjective = morph.isInjective();
        this.hasCreators = !morph.isSurjective();
        this.morphism = morph;
        this.lhs = (VarGraph) morphism.dom();
        this.rhs = (VarGraph) morphism.cod();

        this.modifying = computeModifying();
        if (CONSTRUCTOR_DEBUG) {
            Groove.message("Rule " + name + ": " + this);
            System.out.println("LHS nodes to be removed:\n" + Arrays.toString(getEraserNodes()));
            System.out.println("LHS edges to be removed:\n" + Arrays.toString(getEraserEdges()));
            System.out.println("LHS node map:\n" + mergeMap);
            System.out.println("Anchors:\n" + anchor());
        }
    }

    public RuleEvent getEvent(VarNodeEdgeMap anchorMap) {
        if (isModifying()) {
            RuleEvent event = createEvent(anchorMap);
            // look if we have an event with the same characteristics
            RuleEvent result = eventMap.get(event);
            if (result == null) {
                // no, the event is new.
                result = event;
                eventMap.put(event, result);
                eventCount++;
            }
            return result;
        } else {
            // there can be at most one event
            if (unmodifyingEvent == null) {
                unmodifyingEvent = createEvent(anchorMap);
                eventCount++;
            }
            return unmodifyingEvent;
        }
    }
    
    /**
     * Clears all event information from the rule.
     * This should be done before a new GTS is developed, so as to
     * avoid node number clashes.
     */
    public void clearEvents() {
    	eventMap.clear();
    }
    
    /**
     * Callback factory method to create a rule event for this rule.
     */
    protected RuleEvent createEvent(VarNodeEdgeMap anchorMap) {
        return getRuleFactory().createRuleEvent(this, anchorMap);
    }
    
    public RuleApplication createApplication(Matching match) {
    	return getRuleFactory().createRuleApplication(getEvent(match.elementMap()), match.cod());
//        return getEvent(match.elementMap()).createApplication(match.cod());
    }

    public Matching createMatching(Graph graph) {
    	return getRuleFactory().createMatching(this, graph);
    }

    public VarGraph lhs() {
        return lhs;
    }

    public VarGraph rhs() {
        return rhs;
    }

    public Morphism getMorphism() {
        return morphism;
    }

    /**
	 * Computes the underlying rule morphism from a given morphism.
	 */
	protected Morphism computeRuleMorphism(Morphism morph) {
		Morphism result = isInjective ? new DefaultInjectiveMorphism(morph) : morph;
		result.setFixed();
	    return result;
	}

	public Element[] anchor() {
        if (anchor == null) {
            anchor = computeAnchor();
        }
        return anchor;
    }

	/**
	 * Callback method creating the anchors of this rule.
	 * Called from the constructor.
	 * This implementation delegates to {@link #getAnchorFactory()}.
	 */
	protected Element[] computeAnchor() {
	    return anchorFactory.newAnchors(this);
	}

	public Node[] coanchor() {
		return getCreatorNodes();
	}

    // -------------------- OBJECT OVERRIDES -----------------------------

	/**
     * @see Object#toString()
     */
    public String toString() {
        String res = "Rule " + getName();
        res +=
            "\nLeft hand side:\n    "
                + lhs
                + "\nRight hand side:\n    "
                + rhs
                + "\nRule morphism:\n    "
                + getMorphism().elementMap();
        if (getInjectionMap() != null && !getInjectionMap().isEmpty()) {
            res += "\nInjection constraints: "+getInjectionMap();
        }
        if (getNegationMap() != null && !getNegationMap().isEmpty()) {
            res += "\nEmbargo edges "+getNegationMap();
        }
        if (hasComplexNegConditions()) {
            res += "\nNegative application conditions:";
            Iterator<NAC> nacIter = ((Set<NAC>) getComplexNegConjunct()).iterator();
            while (nacIter.hasNext()) {
                NAC nextNac = nacIter.next();
                if (nextNac instanceof DefaultNAC) {
                    res += "\n    " + nextNac.toString();
                }
            }
        }
        return res;
    }
    
    /** Compares two rules on the basis of their names. */
    public int compareTo(Rule o) {
        return getName().compareTo(o.getName());
    }

    // ------------------- commands --------------------------

    /** 
     * This method now delegates to {@link #setAndNot(GraphTest)}.
     * @see #setAndNot(GraphTest)
     */
    public void addNAC(NAC nac) {
        setAndNot(nac);
    }

    public int getPriority() {
		return priority;
	}
    
    /* (non-Javadoc)
     * @see groove.trans.Rule#getGrammar()
     */
    public GraphGrammar getGrammar() {
		return grammar;
	}

    /**
     * Sets the associated grammar for this rule.
     * @see #getGrammar()
     */
	public void setGrammar(GraphGrammar grammar) {
		this.grammar = grammar;
	}

	/**
     * Sets the priority of this rule.
     * Should be called at initialization time, before the first application.
     * @param priority the priority of the rule
     */
    public void setPriority(int priority) {
		this.priority = priority;
	}
    
    /**
	 * Indicates if this rule has mergers.
	 * @invariant <tt>result == ! getMergeMap().isEmpty()</tt>
	 */
	final public boolean hasMergers() {
		return ! getMergeMap().isEmpty();
	}

	public boolean isModifying() {
		if (! modifyingSet) {
			modifying = computeModifying();
			modifyingSet = true;
		}
	    return modifying;
	}

	/**
	 * Computes if the rule is modifying or not.
	 */
	protected boolean computeModifying() {
		return this.getEraserEdges().length > 0 || this.getEraserNodes().length > 0 || hasMergers() || hasCreators();
	}

	/**
     * This method checks whether the given graph contains the given element.
     * @see Graph#containsElement(Element)
     * @param source the graph in which to look for the element
     * @param element the element to look for
     * @return <tt>true</tt> if the graph contains the element, <tt>false</tt> otherwise
     */
    protected boolean containsElement(Graph source, Element element) {
    	return source.containsElement(element);
    }

	/**
	 * Overwrites the super method in order to query the {@link GraphGrammar}
	 * (as given by {@link #getGrammar()}) for a {@link GraphGrammar#CONTROL_LABELS}
	 * property; if that is found, creates a schedule factory using 
	 * {@link #createMatchingScheduleFactory(String)}.
	 * Uses the <code>super</code> method otherwise.
	 */
	protected MatchingScheduleFactory computeMatchingScheduleFactory() {
		MatchingScheduleFactory result = super.computeMatchingScheduleFactory();
		GraphGrammar grammar = getGrammar();
		if (grammar != null) {
			String controlLabels = grammar.getProperty(GraphGrammar.CONTROL_LABELS);
			if (controlLabels != null) {
				result = createMatchingScheduleFactory(controlLabels);
			}
		}
		return result;
	}
    
    /**
     * Callback factory method to create a matching schedule factory from a given hint.
     * Returns <code>null</code> if the hint cannot be parsed.
     * This implementation turns the hint into a list of labels and creates a 
     * {@link HintedIndegreeScheduleFactory}.
     */
    protected MatchingScheduleFactory createMatchingScheduleFactory(String controlLabels) {
        try {
            return new HintedIndegreeScheduleFactory(controlLabels);
        } catch (ExprFormatException exc) {
            return createMatchingScheduleFactory();
        }
    }

    /** Returns the eraser (i.e., LHS-only) edges. */
    final Edge[] getEraserEdges() {
    	if (eraserEdges == null) {
    		eraserEdges = computeEraserEdges();
    	}
		return eraserEdges;
	}

    /**
	 * Computes the eraser (i.e., LHS-only) edges.
	 */
	protected Edge[] computeEraserEdges() {
	    Set<Edge> eraserEdgeSet = new HashSet<Edge>(lhs.edgeSet());
	    eraserEdgeSet.removeAll(getMorphism().edgeMap().keySet());
	    // also remove the incident edges of the lhs-only nodes
	    for (Node eraserNode: getEraserNodes()) {
	        eraserEdgeSet.removeAll(lhs.edgeSet(eraserNode));
	    }
	    return eraserEdgeSet.toArray(new Edge[0]);
	}

	/** Returns the eraser edges that are not themselves anchors. */
	final Edge[] getEraserNonAnchorEdges() {
		if (eraserNonAnchorEdges == null) {
			eraserNonAnchorEdges = computeEraserNonAnchorEdges();
		}
		return eraserNonAnchorEdges;
	}

	/**
	 * Computes the array of creator edges that are not themselves anchors.
	 */
	protected Edge[] computeEraserNonAnchorEdges() {
		Set<Edge> eraserNonAnchorEdgeSet = new HashSet<Edge>(Arrays.asList(getEraserEdges()));
		eraserNonAnchorEdgeSet.removeAll(Arrays.asList(anchor()));
		return eraserNonAnchorEdgeSet.toArray(new Edge[0]);
	}

	/**
	 * Returns the LHS nodes that are not mapped to the RHS.
	 */
	final Node[] getEraserNodes() {
		if (eraserNodes == null) {
			eraserNodes = computeEraserNodes();
		}
		return eraserNodes;
	}

	/**
	 * Computes the eraser (i.e., lhs-only) nodes.
	 */
	protected Node[] computeEraserNodes() {
		// construct lhsOnlyNodes
	    Set<Node> eraserNodeSet = new HashSet<Node>(lhs.nodeSet());
	    eraserNodeSet.removeAll(getMorphism().nodeMap().keySet());
	    return eraserNodeSet.toArray(new Node[0]);
	}

	/**
	 * Indicates if the rule creates any nodes or edges.
	 */
	protected boolean hasCreators() {
		return hasCreators;
	}

	/**
	 * Returns the RHS edges that are not images of an LHS edge.
	 */
	final Edge[] getCreatorEdges() {
		if (creatorEdges == null) {
			creatorEdges = computeCreatorEdges();
		}
		return creatorEdges;
	}

	/**
	 * Computes the creator (i.e., RHS-only) edges.
	 */
	protected Edge[] computeCreatorEdges() {
		Set<Edge> result = new HashSet<Edge>(rhs.edgeSet());
	    result.removeAll(getMorphism().edgeMap().values());
		return result.toArray(new Edge[0]);
	}

	/**
	 * Returns the RHS nodes that are not images of an LHS node.
	 */
	final Node[] getCreatorNodes() {
		if (creatorNodes == null) {
			creatorNodes = computeCreatorNodes();
		}
		return creatorNodes;
	}

	/**
	 * Computes the creator (i.e., RHS-only) nodes.
	 */
	protected Node[] computeCreatorNodes() {
	    Set<Node> result = new HashSet<Node>(rhs.nodeSet());
	    result.removeAll(getMorphism().nodeMap().values());
		return result.toArray(new Node[0]);
	}

	/**
	 * Returns the variables that occur in creator edges.
	 * @see #getCreatorEdges()
	 */
	final String[] getCreatorVars() {
		if (creatorVars == null) {
			creatorVars = computeCreatorVars();
		}
		return creatorVars;
	}

	/**
	 * Computes the variables occurrind in RHS edges.
	 */
	protected String[] computeCreatorVars() {
		Set<String> creatorVarSet = new HashSet<String>();
	    for (int i = 0; i < getCreatorEdges().length; i++) {
	        Edge creatorEdge = getCreatorEdges()[i];
	        String creatorVar = RegExprLabel.getWildcardId(creatorEdge.label());
	        if (creatorVar != null) {
	            creatorVarSet.add(creatorVar);
	        }
	    }
	    return creatorVarSet.toArray(new String[0]);
	}

	/** 
	 * Returns a sub-graph of the RHS concisting of the creator nodes and
	 * the creator edges with their endpoints.
	 */
	final Graph getCreatorGraph() {
		if (creatorGraph == null) {
			creatorGraph = computeCreatorGraph();
		}
		return creatorGraph;
	}

	/**
	 * Computes a creator graph, consisting of the 
	 * creator nodes together with the creator edges and their endpoints. 
	 */
	protected Graph computeCreatorGraph() {
		Graph result = rhs.newGraph();
		result.addNodeSet(Arrays.asList(this.getCreatorNodes()));
		result.addEdgeSet(Arrays.asList(this.getCreatorEdges()));
	    if (CONSTRUCTOR_DEBUG) {
	        Groove.message("RHS-only graph: " + result);
	    }
	    return result;
	}

	/** 
	 * Returns a partial map from the nodes of the creator graph (see {@link #getCreatorGraph()})
	 * that are not themselves creator nodes but are the ends of creator edges, to the
	 * corresponding nodes of the LHS.
	 */
	final Map<Node,Node> getCreatorMap() {
		if (creatorMap == null) {
			creatorMap = computeCreatorMap();
		}
		return creatorMap;
	}

	/**
	 * Computes a value for the creator map.
	 * The creator map maps the endpoints of creator edges
	 * that are not themselves creator nodes to one of their pre-images.
	 */
	protected Map<Node, Node> computeCreatorMap() {
		// construct rhsOnlyMap
	    Map<Node, Node> result = new HashMap<Node, Node>();
	    Set<? extends Node> creatorNodes = getCreatorGraph().nodeSet();
	    for (Map.Entry<Node,Node> nodeEntry: getMorphism().elementMap().nodeMap().entrySet()) {
	    	if (creatorNodes.contains(nodeEntry.getValue())) {
	    		result.put(nodeEntry.getValue(), nodeEntry.getKey());
	    	}
	    }
	    return result;
	}

	/**
	 * Returns a map from LHS nodes that are merged to those LHS nodes
	 * they are merged with.
	 */
	final Map<Node, Node> getMergeMap() {
		if (mergeMap == null) {
			mergeMap = computeMergeMap();
		}
		return mergeMap;
	}

	/**
	 * Computes the merge map, which maps each LHS node 
	 * that is merged with others
	 * to the LHS node it is merged with.
	 */
	protected Map<Node, Node> computeMergeMap() {
		Map<Node,Node> result = new HashMap<Node,Node>();
		Map<Node,Node> rhsToLhsMap = new HashMap<Node,Node>();
		for (Map.Entry<Node,Node> nodeEntry: getMorphism().elementMap().nodeMap().entrySet()) {
			Node mergeTarget = rhsToLhsMap.get(nodeEntry.getValue());
			if (mergeTarget == null) {
				mergeTarget = nodeEntry.getKey();
				rhsToLhsMap.put(nodeEntry.getValue(), mergeTarget);
			} else {
				result.put(nodeEntry.getKey(), mergeTarget);
				// the merge target is also merged
				// maybe we do this more than once, but that's negligable
				result.put(mergeTarget, mergeTarget);
			}
		}
		return result;
	}

	/**
	 * Array of LHS edges with variable labels.
	 */
	final VarEdge[] getVarEdges() {
		if (varEdges == null) {
			varEdges = computeVarEdges();
		}
		return varEdges;
	}

	/**
	 * Computes the set of variable edges occurring in the rule.
	 */
	protected VarEdge[] computeVarEdges() {
		return lhs.varEdgeSet().toArray(new VarEdge[0]);
	}

	/**
     * Indicates if this rule is injective.
     * @invariant <tt>isInjective == ruleMorph.isInjective()</tt>
     */
    private final boolean isInjective;
    /**
     * Indicates if this rule has creator edges or nodes.
     * @invariant <tt>hasCreators == ! ruleMorph.isSurjective()</tt>
     */
    private final boolean hasCreators;
    /**
     * Indicates if this rule makes changes to a graph at all.
     */
    private boolean modifying;
    /**
     * Indicates if the {@link #modifying} variable has been computed
     */
    private boolean modifyingSet;
    /** 
     * The underlying production morphism.
     * @invariant ruleMorph : lhs --> rhs
     */
    private final Morphism morphism;
    /** 
     * This production rule's left hand side.
     * @invariant lhs != null
     */
    private final VarGraph lhs;
    /** 
     * This production rule's right hand side.
     * @invariant rhs != null
     */
    private final VarGraph rhs;
    /**
     * The grammar with which this rule is associated; may be <code>null</code>.
     */
    private GraphGrammar grammar;
    /**
     * A sub-graph of the production rule's right hand side,
     * consisting only of the fresh nodes and edges.
     */
    private Graph creatorGraph;
    /**
     * A map from the nodes of <tt>rhsOnlyGraph</tt> to <tt>lhs</tt>,
     * which is the restriction of the inverse of <tt>ruleMorph</tt> to <tt>rhsOnlyGraph</tt>.
     */
    private Map<Node,Node> creatorMap;
    /** 
     * The lhs nodes that are not ruleMorph keys
     * @invariant lhsOnlyNodes \subseteq lhs.nodeSet()
     */
    private Node[] eraserNodes;
    /** 
     * The lhs edges that are not ruleMorph keys
     * @invariant lhsOnlyEdges \subseteq lhs.edgeSet()
     */
    private Edge[] eraserEdges;
    /**
	 * The set of anchors of this rule.
	 */
	private Element[] anchor;
	/** 
     * The lhs edges that are not ruleMorph keys and are not anchors
     * @invariant lhsOnlyEdgeSet \subseteq lhs.edgeSet()
     */
    private Edge[] eraserNonAnchorEdges;
    /** 
     * The {@link VarEdge}s in the left hand side.
     * @invariant lhsOnlyNonAnchorEdges = lhsOnlyEdgeSet \setminus anchors
     */
    private VarEdge[] varEdges;
    /** 
     * The rhs nodes that are not ruleMorph images
     * @invariant rhsOnlyNodeSet \subseteq rhs.nodeSet()
     */
    private Node[] creatorNodes;
    /** 
     * The rhs edges that are not ruleMorph images
     * @invariant rhsOnlyEdgeSet \subseteq rhs.edgeSet()
     */
    private Edge[] creatorEdges;
    /**
     * Variables occurring in the rhsOnlyEdges
     */
    private String[] creatorVars;
    /**
     * A partial mapping from LHS nodes to RHS nodes, indicating
     * which nodes are merged and which nodes are deleted.
     */
    private Map<Node,Node> mergeMap;
    /**
     * The priority of this rule.
     */
    private int priority;
    /**
     * Map from anchor maps to {@link RuleEvent}s.
     */
    private final Map<RuleEvent,RuleEvent> eventMap = new HashMap<RuleEvent,RuleEvent>();

	/**
     * The unique event in case this rule is not modifying.
     * @see #isModifying()
     */
    private RuleEvent unmodifyingEvent;
    /** Debug flag for the constructor. */
    private static final boolean CONSTRUCTOR_DEBUG = false;
}
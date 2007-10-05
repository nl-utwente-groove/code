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
 * $Id: AbstractCondition.java,v 1.3 2007-10-05 08:31:38 rensink Exp $
 */
package groove.trans;

import groove.graph.AbstractMorphism;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.graph.algebra.ValueNode;
import groove.match.ConditionSearchPlanFactory;
import groove.match.MatchStrategy;
import groove.rel.VarNodeEdgeHashMap;
import groove.rel.VarNodeEdgeMap;
import groove.rel.VarSupport;
import groove.util.Reporter;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 */
abstract public class AbstractCondition<M extends Match> implements Condition {
    /**
     * Constructs a (named) graph condition based on a given graph to be matched and root map.
     * @param target the graph to be matched
     * @param rootMap element map from the context to the anchor elements of <code>target</code>;
     * may be <code>null</code> if the condition is ground
     * @param name the name of the condition; may be <code>null</code>
     * @param properties properties for matching the condition
     */
    protected AbstractCondition(Graph target, NodeEdgeMap rootMap, NameLabel name, SystemProperties properties) {
        this.patternMap = rootMap;
        this.target = target;
		this.properties = properties;
        this.name = name;
    }
    
    /**
     * Constructs a (named) ground graph condition based on a given target graph.
     * The name may be <code>null</code>.
     */
    protected AbstractCondition(Graph target, NameLabel name, SystemProperties properties) {
    	this(target, new NodeEdgeHashMap(), name, properties);
    }

    public SystemProperties getProperties() {
		return properties;
	}

    /**
     * This implementation returns <code>this</code>.
     */
    public NodeEdgeMap getPatternMap() {
        return patternMap;
    }

    /**
     * This implementation returns <code>cod()</code>.
     */
    public Graph getTarget() {
        return target;
    }
    
    /**
     * Returns the name set at construction time.
     */
    public NameLabel getName() {
        return name;
    }

    /**
     * Delegates to <code>getContext().isEmpty()</code> as per contract.
     */
    public boolean isGround() {
        return getPatternMap().isEmpty();
    }

    /** 
     * This implementation tests for the use of attributes and the presence of isolated nodes.
     * @see #hasAttributes()
     * @see SystemProperties#isAttributed()
     */
	public void testConsistent() throws FormatException {
		String attributeKey = SystemProperties.ATTRIBUTES_KEY;
		String attributeProperty = getProperties().getProperty(attributeKey);
		if (getProperties().isAttributed()) {
			if (hasIsolatedNodes()) {
				throw new FormatException("Condition tests isolated nodes, conflicting with \"%s=%s\"", attributeKey, attributeProperty);
			}
		} else if (hasAttributes()) {
			if (attributeProperty == null) {
				throw new FormatException("Condition uses attributes, but \"%s\" not declared", attributeKey);
			} else {
				throw new FormatException("Condition uses attributes, violating \"%s=%s\"", attributeKey, attributeProperty);
			}
		}
	}

	/**
	 * Returns <code>true</code> if the target graph of the condition
	 * contains {@link ValueNode}s, or the negative conjunct is attributed.
	 */
	protected boolean hasAttributes() {
		boolean result = ValueNode.hasValueNodes(getTarget());
		if (result) {
            Iterator<AbstractCondition<?>> subConditionIter = getSubConditions().iterator();
            while (!result && subConditionIter.hasNext()) {
                result = subConditionIter.next().hasAttributes();
            }
        }
		return result;
	}

	/**
	 * Tests if the target graph of the condition
	 * contains nodes without incident edges.
	 */
	protected boolean hasIsolatedNodes() {
		boolean result = false;
		// first test if the pattern target has isolated nodes
		Set<Node> freshTargetNodes = new HashSet<Node>(getTarget().nodeSet());
		freshTargetNodes.removeAll(getPatternMap().nodeMap().values());
		Iterator<Node> nodeIter = freshTargetNodes.iterator();
		while (!result && nodeIter.hasNext()) {
			result = getTarget().edgeSet(nodeIter.next()).isEmpty();
		}     
		if (!result) {
            // now recursively test the sub-conditions
            Iterator<AbstractCondition<?>> subConditionIter = getSubConditions().iterator();
            while (!result && subConditionIter.hasNext()) {
                result = subConditionIter.next().hasIsolatedNodes();
            }
        }
		return result;
	}

	public Collection<AbstractCondition<?>> getSubConditions() {
	    if (subConditions == null) {
	        subConditions = new ArrayList<AbstractCondition<?>>();
	    }
        return subConditions;
    }

    public void addSubCondition(Condition condition) {
        assert condition instanceof AbstractCondition : String.format("Condition %s should be an AbstractCondition", condition);
        getSubConditions().add((AbstractCondition<?>) condition);
    }

    /** Fixes the sub-predicate and this morphism. */
    public void setFixed() {
        if (!isFixed()) {
        	getTarget().setFixed();
            for (AbstractCondition<?> subCondition: getSubConditions()) {
                subCondition.setFixed();
            }
            fixed = true;
        }
    }

    public boolean isFixed() {
		return fixed;
	}
    
    final public boolean hasMatch(Graph host) {
        return isGround() && getMatchIter(host, null).hasNext();
	}

    public Iterable<M> getMatches(final Graph host, final NodeEdgeMap contextMap) {
		return new Iterable<M>() {
			public Iterator<M> iterator() {
				return getMatchIter(host, contextMap);
			}
		};
	}

    abstract public Iterator<M> getMatchIter(Graph host, NodeEdgeMap contextMap);
    
    /** 
     * Factors given matching of the condition context through this condition's
     * pattern map, to obtain a matching of {@link #getTarget()}.
     * @return a mapping that, concatenated after this condition's morphism,
     * returns <code>patternMatch</code>; or <code>null</code> if there is
     * no such mapping.
     */
    final protected VarNodeEdgeMap getAnchorMap(NodeEdgeMap contextMap) {
    	VarNodeEdgeMap result;
    	if (contextMap == null) {
    		testGround();
    		result = null;
    	} else try {
    		result = new VarNodeEdgeHashMap();
    		AbstractMorphism.constructInvertConcat(getPatternMap(), contextMap, result);
    		if (contextMap instanceof VarNodeEdgeMap) {
    			Map<String, Label> valuation = ((VarNodeEdgeMap) contextMap).getValuation();
    			for (Map.Entry<String, Label> varEntry : valuation.entrySet()) {
    				String var = varEntry.getKey();
    				if (getTargetVars().contains(var)) {
    					result.putVar(var, varEntry.getValue());
    				}
    			}
    		}
    	} catch (FormatException exc) {
    		throw new IllegalArgumentException(
    				String.format("Pattern match %s incompatible with pattern %s",
    						contextMap,
    						getPatternMap()));
    	}
		return result;
    }
    
    /** Returns the set of variables in the target graph. */
    private Set<String> getTargetVars() {
        if (targetVars == null) {
            targetVars = VarSupport.getAllVars(getTarget());
        }
        return targetVars;
    }
    /**
     * Returns the precomputed matching order for the elements of the target pattern. First creates
     * the order using {@link #createMatchStrategy()} if that has not been done.
     * @see #createMatchStrategy()
     */
    final public MatchStrategy<VarNodeEdgeMap> getMatchStrategy() {
        if (matchStrategy == null) {
            matchStrategy = createMatchStrategy();
        }
        return matchStrategy;
    }

    /**
     * Callback method to create a matching factory.
     * Typically invoked once, at the first invocation of {@link #getMatchStrategy()}.
     * This implementation retrieves its value from {@link #getMatcherFactory()}.
     */
    protected MatchStrategy<VarNodeEdgeMap> createMatchStrategy() {
        setFixed();
        return getMatcherFactory().createMatcher(this);
    }

    /** Returns a matcher factory, tuned to the injectivity of this condition. */
    protected ConditionSearchPlanFactory getMatcherFactory() {
        return groove.match.ConditionSearchPlanFactory.getInstance(getProperties().isInjective());
    }

    /**
     * Tests if the condition is fixed or not.
     * Throws an exception if the fixedness does not coincide with the given value.
     * @param value the expected fixedness state
     * @throws IllegalStateException if {@link #isFixed()} does not yield <code>value</code>
     */
    protected void testFixed(boolean value) throws IllegalStateException {
        if (isFixed() != value) {
        	String message;
        	if (value) {
        		message = "Graph condition should be fixed in this state";
        	} else {
        		message = "Graph condition should not be fixed in this state";
        	}
            throw new IllegalStateException(message);
        }
    }

    /**
     * Tests if the condition can be used to tests on graphs rather than morphisms.
     * This is the case if and only if the condition is ground (i.e., the
     * context graph is empty), as determined by {@link #isGround()}.
     * @throws IllegalStateException if this condition is not ground.
     * @see #isGround()
     */
    private void testGround() throws IllegalStateException {
        if (! isGround()) {
            throw new IllegalStateException("Method only allowed on ground condition");
        }
    }
    
    /**
     * The name of this condition. May be <code>code</code> null.
     */
    protected NameLabel name;
    /**
     * The fixed matching strategy for this graph condition.
     * Initially <code>null</code>; set by {@link #getMatchStrategy()} upon its
     * first invocation.
     */
    private MatchStrategy<VarNodeEdgeMap> matchStrategy;
    /** The variables occurring in edges of the target (i.e., the codomain). */
    private Set<String> targetVars;
    /** The collection of sub-conditions of this condition. */
    private Collection<AbstractCondition<?>> subConditions;
    /** Flag indicating if this condition is now fixed, i.e., unchangeable. */
    boolean fixed;
    /** 
     * The pattern map of this condition, i.e., the element
     * map from the context graph to the target graph.
     */
    private final NodeEdgeMap patternMap;
    /** The target graph of this morphism. */
    private final Graph target;
    /**
     * Factory instance for creating the correct simulation.
     */
    private final SystemProperties properties;
    
    /** Reporter instance for profiling this class. */
    static public final Reporter reporter = Reporter.register(Condition.class);
    /** Handle for profiling {@link #getMatches(Graph,NodeEdgeMap)} and related methods. */
    static public final int GET_MATCHING = reporter.newMethod("getMatching...");
}
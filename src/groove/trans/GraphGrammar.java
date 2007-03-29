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
 * $Id: GraphGrammar.java,v 1.4 2007-03-29 09:59:46 rensink Exp $
 */
package groove.trans;

import groove.graph.DefaultNode;
import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.Node;
import groove.lts.GTS;
import groove.util.DefaultDispenser;

import java.util.Properties;

/**
 * Default model of a graph grammar, consisting of a production rule system
 * and a default start graph.
 * Currently the grammar also keeps track of the GTS generated, which is not
 * really natural.
 * @author Arend Rensink
 * @version $Revision: 1.4 $ $Date: 2007-03-29 09:59:46 $
 */
public class GraphGrammar extends RuleSystem {
    /**
     * Property name of the list of control labels of a graph grammar.
     * The control labels will be first in the matching order.
     */
    static public final String CONTROL_LABELS = "controlLabels";
    /**
     * Property name of the list of common labels of a graph grammar.
     * These will be used to determine the order in which the NACs are checked.
     */
    static public final String COMMON_LABELS = "commonLabels";
    /** 
     * Property that determines if the graph grammar uses attributes.
     * @see #ATTRIBUTES_YES
     */
    static public final String ATTRIBUTE_SUPPORT = "attributeSupport";
    /**
     * Value of {@link #ATTRIBUTES_YES} that means attributes are used.
     */
    static public final String ATTRIBUTES_YES = "1";
//    
//    /**
//     * The default graph factory used for graph grammars.
//     * Unless the factory is explicitly set through {@link #setGraphFactory(GraphFactory)},
//     * this one will be used to create start graphs.
//     */
//    static public final GraphFactory DEFAULT_GRAPH_FACTORY = GraphFactory.getInstance();
    
    /**
     * Constructs a graph grammar on the basis of a given rule system,
     * start graph and name.
     * @param ruleSystem the underlying rule system
     * @param startGraph the start graph; if <code>null</code>, an empty graph is used
     * @param name the name of this grammar; may be <tt>null</tt> if the grammar is anonymous
     * @require ruleSystem != null
     * @ensure ruleSystem()==ruleSystem, 
     *         gts().nodeSet().size() == 1, gts().edgeSet().size() == 0,
     *         getStartGraph().equals(startGraph),
     *         <tt>getName().equals(name)</tt>
     */
    public GraphGrammar(RuleSystem ruleSystem, Graph startGraph, String name) {
        super(ruleSystem);
        if (startGraph != null) {
        	startGraph.setFixed();
        }
        this.startGraph = startGraph;
        this.name = name;
    }
    
    /**
     * Constructs an anonymoud graph grammar on the basis of a given rule system and
     * start graph.
     * @param ruleSystem the underlying rule system
     * @param startGraph the start graph; if <code>null</code>, an empty graph is used
     * @require ruleSystem != null
     * @ensure ruleSystem()==ruleSystem, 
     *         gts().nodeSet().size() == 1, gts().edgeSet().size() == 0,
     *         getStartGraph().equals(startGraph),
     *         <tt>getName().equals(name)</tt>
     */
    public GraphGrammar(RuleSystem ruleSystem, Graph startGraph) {
        this(ruleSystem, startGraph, null);
    }

    /**
     * Constructs a graph grammar on ths basis of a given production system and name.
     * The initial graph is set to empty (created using {@link GraphFactory}).
     * @param ruleSystem the underlying production system
     * @param name name the name of this grammar; may be <tt>null</tt> if the grammar is anonymous
     * @require <tt>ruleSystem != null</tt>
     * @ensure ruleSystem().equals(ruleSystem), 
     *         gts().nodeSet().size() == 1, gts().edgeSet().size() == 0,
     *         getStartGraph().isEmpty()
     */
    public GraphGrammar(RuleSystem ruleSystem, String name) {
        this(ruleSystem, null, name);
    }
    
    /**
     * Constructs an anonymous graph grammar on ths basis of a given production system.
     * The initial graph is set to empty. 
     * @param ruleSystem the underlying production system
     * @require <tt>ruleSystem != null</tt>
     * @ensure ruleSystem().equals(ruleSystem), 
     *         gts().nodeSet().size() == 1, gts().edgeSet().size() == 0,
     *         getStartGraph().isEmpty()
     */
    public GraphGrammar(RuleSystem ruleSystem) {
        this(ruleSystem, (String) null);
    }

    /**
     * Constructs a named graph grammar with empty rule system and 
     * empty start graph.
     */
    public GraphGrammar(String name) {
        this(new RuleSystem(), name);
    }

    /**
     * Constructs an anonymous graph grammar with empty rule system and 
     * empty start graph.
     */
    public GraphGrammar() {
        // explicit empty constructor
    }
    
    /**
     * Returns the GTS of this graph grammar as explored so far.
     * May return <tt>null</tt> if no start state has yet been provided.
     * @ensure startGraph().equals(result.startNode());
     *         result.nodeSet() \subseteq GraphNode;
     *         result.edgeSet() \subseteq DerivationEdge
     */
    public GTS gts() {
        if (gts == null) {
//            setRuleDependencies();
            gts = computeGTS();
            // compute the max node number and set the node dispenser to one higher
            int maxNodeNr = 0;
            for (Node node: getStartGraph().nodeSet()) {
                maxNodeNr = Math.max(maxNodeNr, DefaultNode.getNodeNr(node));
            }
            getNodeCounter().setCount(maxNodeNr+1);
        }
        return gts;
    }
    
    /**
     * Constructs a new GTS on the basis of this rule system and the
     * currently stored start graph.
     */
    protected GTS computeGTS() {
    	return new GTS(this, getStartGraph());
    }
    
    /**
     * Returns the name of this grammar.
     * May be <tt>null</tt> if the grammar is anonymous.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this grammar.
     * @param name the name of the grammar; <tt>null</tt> if the grammar is anonymous
     * @ensure <tt>getName().equals(name)</tt>
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the start graph of this graph grammar.
     * If the graph is not set at the time of invocation, an empty 
     * start graph is created through the graph factory.
     * @return the start graph of this GraphGrammar
     * @ensure <tt>result != null</tt>
     */
    public Graph getStartGraph() {
    	if (startGraph == null) {
    		startGraph = GraphFactory.getInstance().newGraph();
    	}
        return startGraph;
    }
    
    /**
     * Changes or sets the start graph of this graph grammar.
     * Note that this invalidates the previous GTS.
     * @param startGraph the new start graph of this graph grammar
     * @require <tt>startGraph != null</tt>
     * @ensure <tt>getStartGraph().equals(startGraph)</tt>
     */
    public void setStartGraph(Graph startGraph) {
        this.startGraph = startGraph;
        this.startGraph.setFixed();
        invalidateGTS();
    }
    
    /**
     * Sets the properties of this graph grammar by copying a given property mapping.
     * Clears the current properties first.
     * @param properties the new properties mapping
     */
    public void setProperties(Properties properties) {
        Properties currentProperties = getProperties();
        currentProperties.clear();
        currentProperties.putAll(properties);
//        if (properties.containsKey(CONTROL_LABELS)) {
//            setRuleScheduleFactory((String) properties.get(CONTROL_LABELS));
//        }
    }
    
    /**
     * Returns the properties object for this graph grammar.
     * The object is created lazily.
     */
    public Properties getProperties() {
        if (properties == null) {
            properties = createProperties();
        }
        return properties;
    }

    // --------------------------- OBJECT OVERRIDES ------------------------

    /**
     * In adition to delegating the call to the superclass, also
     * sets the matching schedule factory of the rule and 
     * invalidates the GTS. 
     */
    @Override
    public Rule add(Rule rule) {
        Rule result = super.add(rule);
        rule.setGrammar(this);
        invalidateGTS();
        return result;
    }

    /** Tests for equality of the rule system and the start graph. */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof GraphGrammar)
            && getStartGraph().equals(((GraphGrammar) obj).getStartGraph())
            && super.equals(obj);
    }

    /** Combines the hash codes of the rule system and the start graph. */
    @Override
    public int hashCode() {
        return (getStartGraph().hashCode() << 8) ^ super.hashCode();
    }

    @Override
    public String toString() {
        return "Rule system:\n    " + super.toString()
            + "\nStart graph:\n    "   + getStartGraph().toString();
    }

    /**
     * Invalidates the current GTS.
     * This is done due to a change in the rule system or the start graph.
     */
    protected void invalidateGTS() {
        gts = null;
        for (Rule rule: getRules()) {
        	((SPORule) rule).clearEvents();
        }
    }
//
//    /**
//     * Returns the graph factory.
//     * @return the graph factory
//     */
//    public GraphFactory getGraphFactory() {
//    	if (factory == null) {
//    		factory = createGraphFactory();
//    	}
//    	return factory;
//    }
//    
//    /**
//     * Sets the graph factory.
//     * @param factory the new graph factory
//     */
//    public void setGraphFactory(GraphFactory factory) {
//        this.factory = factory;
//    }
//
//    /**
//     * Returns the rule factory.
//     * @return the rule factory
//     */
//    public RuleFactory getRuleFactory() {
//    	return ruleFactory;
//    }
//
//    /**
//     * Sets the rule factory.
//     * @param ruleFactory the new rule factory
//     */
//    public void setRuleFactory(RuleFactory ruleFactory) {
//    	this.ruleFactory = ruleFactory;
//    }
//
//    /**
//     * Returns the default graph factory.
//     * @return the default graph factory
//     */
//    protected GraphFactory createGraphFactory() {
//    	return DEFAULT_GRAPH_FACTORY;
//    }
//    
    /**
     * Returns a dispenser for new node numbers.
     */
    public DefaultDispenser getNodeCounter() {
    	if (nodeCounter == null) {
    		nodeCounter = new DefaultDispenser();
    	}
    	return nodeCounter;
    }
//    
//    /**
//     * Sets the rule matching schedule factory, using a given hint.
//     * The factory is created using {@link #createRuleScheduleFactory(String)}. 
//     * Also passes thefactory on to the rules already in the grammar.
//     */
//    protected void setRuleScheduleFactory(String controlLabels) {
//        this.ruleScheduleFactory = createRuleScheduleFactory(controlLabels);
//        if (ruleScheduleFactory != null) {
//            Iterator ruleIter = getRules().iterator();
//            while (ruleIter.hasNext()) {
//                Rule rule = (Rule) ruleIter.next();
//                if (rule instanceof DefaultGraphCondition) {
//                    ((DefaultGraphCondition) rule).setMatchingScheduleFactory(ruleScheduleFactory);
//                }
//            }
//        }
//    }
//    
//    /**
//     * Returns the rule matching schedule factory used for the rules added to
//     * this graph grammar. May be <code>null</code>.
//     */
//    protected MatchingScheduleFactory getRuleScheduleFactory() {
//    	if (ruleScheduleFactory == null) {
//    		ruleScheduleFactory = initRuleScheduleFactory();
//    	}
//        return ruleScheduleFactory;
//    }
//    
//    /**
//     * Callback method to create and initialise a matching schedule factory
//     * for the rules in this grammar.
//     * If the {@link #CONTROL_LABELS} property of the grammar is set,
//     * this is used as a parameter to {@link #createRuleScheduleFactory(String)}.
//     */
//    protected MatchingScheduleFactory initRuleScheduleFactory() {
//    	MatchingScheduleFactory result = null;
//    	String controlLabels = getProperty(CONTROL_LABELS);
//    	if (controlLabels != null) {
//    		result = createRuleScheduleFactory(controlLabels);
//    	}
//    	return result;
//    }
//    
//    /**
//     * Callback factory method to create a matching schedule factory from a given hint.
//     * Returns <code>null</code> if the hint cannot be parsed.
//     * This implementation turns the hint into a list of labels and creates a 
//     * {@link HintedIndegreeScheduleFactory}.
//     */
//    protected MatchingScheduleFactory createRuleScheduleFactory(String controlLabels) {
//        try {
//            return new HintedIndegreeScheduleFactory(controlLabels);
//        } catch (ExprFormatException exc) {
//            return null;
//        }
//    }

    /**
     * Callback factory method to create an initially empty {@link Properties} object 
     * for this graph grammar.
     */
    protected Properties createProperties() {
        return new Properties();
    }
    
    /**
     * The start Graph of this graph grammar.
     * @invariant <tt>startGraph != null</tt>
     */
    protected Graph startGraph;
    /**
     * The (partial) gts generated by this graph grammar.
     */
    protected GTS gts;
    /**
     * The name of this grammar;
     * <tt>null</tt> if the grammar is anonymous.
     */
    protected String name;
//    /**
//     * The graph factory used to create the initial state.
//     */
//    private GraphFactory factory;
//
//    /**
//     * The rule factory used to create rule related things.
//     */
//    private RuleFactory ruleFactory;
//
//    /**
//     * Mactching schedule factory for the rules added to this grammar.
//     * Set from the properties; possibly <code>null</code>.
//     */
//    private MatchingScheduleFactory ruleScheduleFactory;
    /**
     * The properties bundle of this grammar.
     */
    private Properties properties;
    /** Counter used for determining fresh node numbers. */
    private DefaultDispenser nodeCounter;
}
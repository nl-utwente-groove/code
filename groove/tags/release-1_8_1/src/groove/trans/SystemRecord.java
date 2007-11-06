/* $Id$ */
package groove.trans;

import groove.graph.DefaultNode;
import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.NodeFactory;
import groove.rel.VarNodeEdgeMap;
import groove.util.DefaultDispenser;
import groove.util.Reporter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Usage instance of a given rule system.
 * Stores information gathered during rule application.
 * An instance has an associated rule system, for which it creates
 * fresh node identities (to ensure deterministic and consecutive
 * node numbers) and maintains a map of rule events (to save space
 * and time).
 * @author Arend Rensink
 * @version $Revision $
 */
public class SystemRecord implements NodeFactory {
    /**
     * The total number of events (over all rules) created in {@link #getEvent(Rule, VarNodeEdgeMap)}.
     */
    private static int eventCount;

    /**
     * Returns the number of events created in the course of rule application.
     */
    static public int getEventCount() {
    	return eventCount;
    }

    /** 
     * Constructs a derivation record from a given fixed graph grammar and rule counter, using
     * a given node number for the first fresh node. 
     * @throws IllegalStateException if the grammar is not fixed.
     */
	public SystemRecord(final GraphGrammar grammar) throws IllegalStateException {
		grammar.testFixed(true);
		this.ruleSystem = grammar;
		this.nodeCounter = new DefaultDispenser();
		nodeCounter.setCount(computeHighestNodeNr(grammar.getStartGraph())+1);
	}

	/** Factory method to create a fresh node, based on the internally stored node counter. */
	public Node newNode() {
		return DefaultNode.createNode(nodeCounter);
	}

	/** Returns the stored rule system on which the derivations are based. */
	public RuleSystem getRuleSystem() {
		return ruleSystem;
	}

	/** 
	 * Returns a rule application for a given rule and matching of that rule.
	 * @deprecated use {@link #getApplication(RuleMatch, Graph)} instead 
	 */
	@Deprecated
	public RuleApplication getApplication(Rule rule, Matching matching) {
		return getEvent(rule, matching.elementMap()).newApplication(matching.cod());
	}

	/** Returns a rule application for a given rule and matching of that rule. */
	public RuleApplication getApplication(RuleMatch match, Graph host) {
		return getEvent(match).newApplication(host);
	}

	/** 
	 * Returns an event for a given rule and matching of that rule.
	 * The events are stored internally; an event is reused if it has the
	 * correct rule and anchor map.
	 * @deprecated use {@link #getEvent(RuleMatch)} instead
	 */
	@Deprecated
    public RuleEvent getEvent(Rule rule, VarNodeEdgeMap elementMap) {
    	RuleEvent result;
    	reporter.start(GET_EVENT);
        if (rule.isModifying()) {
            RuleEvent event = rule.newEvent(elementMap, this, reuse);
            if (isReuse()) {
				result = normalEventMap.get(event);
				if (result == null) {
					// no, the event is new.
					result = event;
					normalEventMap.put(event, result);
					eventCount++;
				}
			} else {
				result = event;
            }
        } else {
        	result = unmodifyingEventMap.get(rule);
            // there can be at most one event
            if (result == null) {
                unmodifyingEventMap.put(rule, result = rule.newEvent(elementMap, this, reuse));
                eventCount++;
            }
        }
        reporter.stop();
        return result;
    }

	/** 
	 * Returns an event for a given rule and matching of that rule.
	 * The events are stored internally; an event is reused if it has the
	 * correct rule and anchor map.
	 */
    public RuleEvent getEvent(RuleMatch match) {
    	RuleEvent result;
    	reporter.start(GET_EVENT);
    	Rule rule = match.getRule();
        if (rule.isModifying()) {
            RuleEvent event = match.newEvent(this, reuse);
            if (isReuse()) {
				result = normalEventMap.get(event);
				if (result == null) {
					// no, the event is new.
					result = event;
					normalEventMap.put(event, result);
					eventCount++;
				}
			} else {
				result = event;
            }
        } else {
        	result = unmodifyingEventMap.get(rule);
            // there can be at most one event
            if (result == null) {
                unmodifyingEventMap.put(rule, result = match.newEvent(this, reuse));
                eventCount++;
            }
        }
        reporter.stop();
        return result;
    }

    /** 
	 * Returns the highest node number occurring in a given graph,
	 * taking only true {@link DefaultNode}s into account.
	 */
	private int computeHighestNodeNr(Graph graph) {
		int result = 0;
		for (Node node: graph.nodeSet()) {
			if (node.getClass() == DefaultNode.class) {
				result = Math.max(result, ((DefaultNode) node).getNumber());
			}
		}
		assert result <= DefaultNode.MAX_NODE_NUMBER : String.format("Node number %d too high for a default node", result);
		return result;
	}

	/**
     * Returns the set of rules that may be enabled by a given rule,
     * according to the currently calculated dependencies.
     * @param enabler the (potential) enabler rule
     * @return the set of rules that may be enabled by <code>enabler</code>
     */
    public Set<Rule> getEnabledRules(Rule enabler) {
        return getDependencies().getEnableds(enabler);
    }
    
    /**
     * Returns the set of rules that may be disabled by a given rule,
     * according to the currently calculated dependencies.
     * @param disabler the (potential) disabler rule
     * @return the set of rules that may be disabled by <code>disabler</code>
     */
    public Set<Rule> getDisabledRules(Rule disabler) {
        Set<Rule> result = getDependencies().getDisableds(disabler);
        assert result != null : String.format("Null rule dependencies for %s", disabler.getName());
        return result;
    }
    
    /**
     * Initialises the rule dependencies.
     */
    protected RuleDependencies getDependencies() {
    	if (dependencies == null) {
    		dependencies = new RuleDependencies(ruleSystem);
    	}
    	return dependencies;
    }

    /** Rule dependencies of the rule system. */
    private RuleDependencies dependencies;
	/** The internally stored node counter. */
	private final DefaultDispenser nodeCounter;
	/** The associated rule system. */
	private final RuleSystem ruleSystem;    
	/**
     * Map from events to <i>normal events</i>, which are reused
     * for time and space reasons.
     */
    private final Map<RuleEvent,RuleEvent> normalEventMap = new HashMap<RuleEvent,RuleEvent>();
    /** 
     * Map from unmodifying rules to their (unique) events.
     */
    private final Map<Rule,RuleEvent> unmodifyingEventMap = new HashMap<Rule,RuleEvent>();

    /** 
     * Changes the behaviour of the GTS to reuse previously explored states and rule events.
     * If the reuse property is <code>false</code>, state graph equality is never detected,
     * and backtracking is not supported.
     * Unpredictable behaviour will ensue if this method is called while an existing GTS is being explored.
     * Initially the property is set to <code>true</code>
     * @param reuse if <code>true</code>, results are reused henceforth 
     */
    public void setReuse(boolean reuse) {
        this.reuse = reuse;
    }

    /** 
     * Returns the current value of the reuse property.
     * @return if <code>true</code>, previously found results are reused
     */
    public boolean isReuse() {
        return reuse;
    }

    /** Flag indicating if previous result are reused. */
    private boolean reuse = true;

    static private final Reporter reporter = Reporter.register(RuleEvent.class);
    static private final int GET_EVENT = reporter.newMethod("getEvent");
}

/* $Id: DerivationData.java,v 1.1 2007-04-04 07:04:20 rensink Exp $ */
package groove.trans;

import java.util.HashMap;
import java.util.Map;

import groove.graph.DefaultNode;
import groove.graph.Graph;
import groove.graph.Node;
import groove.rel.VarNodeEdgeMap;
import groove.util.DefaultDispenser;
import groove.util.Dispenser;
import groove.util.Reporter;

/**
 * Type that stores information gathered during rule application.
 * An instance has an associated rule system, for which it creates
 * fresh node identities (to ensure deterministic and consecutive
 * node numbers) and maintains a map of rule events (to save space
 * and time).
 * @author Arend Rensink
 * @version $Revision $
 */
public class DerivationData {
    /**
     * The total number of events (over all rules) created in {@link #getEvent(VarNodeEdgeMap)}.
     */
    private static int eventCount;
    
    /** 
     * Constructs a derivation record from a given rule system and rule counter, using
     * a given node number for the first fresh node. 
     */
	public DerivationData(final GraphGrammar grammar) {
		this.ruleSystem = grammar;
		this.nodeCounter = new DefaultDispenser();
		nodeCounter.setCount(computeHighestNodeNr(grammar.getStartGraph())+1);
	}

	/** Returns a counter for node numbers, used to make fresh nodes unique. */
	public Dispenser getNodeCounter() {
		return nodeCounter;
	}

	/** Returns the stored rule system on which the derivations are based. */
	public RuleSystem getRuleSystem() {
		return ruleSystem;
	}

	/** Returns a rule application for a given rule and matching of that rule. */
	public RuleApplication getApplication(Rule rule, Matching matching) {
		return getRuleFactory().createRuleApplication(getEvent(rule, matching), matching.cod());
	}

	/** 
	 * Returns an event for a given rule and matching of that rule.
	 * The events are stored internally; an event is reused if it has the
	 * correct rule and anchor map.
	 */
    public RuleEvent getEvent(Rule rule, Matching matching) {
    	RuleEvent result;
    	reporter.start(GET_EVENT);
        if (rule.isModifying()) {
            RuleEvent event = createEvent(rule, matching);
//            // look if we have an event with the same characteristics
//            Map<RuleEvent,RuleEvent> eventMap = normalEventMap.get(rule);
//            if (eventMap == null) {
//            	eventMap = new HashMap<RuleEvent,RuleEvent>();
//            	normalEventMap.put(rule, eventMap);
//            }
            result = normalEventMap.get(event);
            if (result == null) {
                // no, the event is new.
                result = event;
                normalEventMap.put(event, result);
                eventCount++;
            }
        } else {
        	result = unmodifyingEventMap.get(rule);
            // there can be at most one event
            if (result == null) {
                unmodifyingEventMap.put(rule, result = createEvent(rule, matching));
                eventCount++;
            }
        }
        reporter.stop();
        return result;
    }
    
	protected RuleEvent createEvent(Rule rule, Matching matching) {
		return getRuleFactory().createRuleEvent(rule, matching.elementMap(), this);
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
	 * Convenienct method to retrieve the rule factory from the rule system.
	 * The rule factory is used to create rule events and applications.
	 */
	private RuleFactory getRuleFactory() {
		return getRuleSystem().getRuleFactory();
	}

	/** The internally stored node counter. */
	private final DefaultDispenser nodeCounter;
	/** The associated rule system. */
	private final RuleSystem ruleSystem;    
	/**
     * Map from events to <i>normal events</i>, which are reused
     * for time and space reasons.
     */
    private final Map<RuleEvent,RuleEvent> normalEventMap = new HashMap<RuleEvent,RuleEvent>();
//    private final Map<Rule,Map<RuleEvent,RuleEvent>> normalEventMap = new HashMap<Rule,Map<RuleEvent,RuleEvent>>();
    /** 
     * Map from unmodifying rules to their (unique) events.
     */
    private final Map<Rule,RuleEvent> unmodifyingEventMap = new HashMap<Rule,RuleEvent>();

    static private final Reporter reporter = SPORule.reporter;
    static private final int GET_EVENT = SPORule.GET_EVENT;
}

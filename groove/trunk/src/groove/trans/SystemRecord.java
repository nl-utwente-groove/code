/* $Id$ */
package groove.trans;

import groove.graph.DefaultNode;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.Node;
import groove.nesting.VarNodeEdgeMultiHashMap;
import groove.nesting.VarNodeEdgeMultiMap;
import groove.nesting.rule.ExistentialLevel;
import groove.nesting.rule.NestedEvent;
import groove.nesting.rule.NestedRule;
import groove.rel.VarNodeEdgeHashMap;
import groove.rel.VarNodeEdgeMap;
import groove.util.DefaultDispenser;
import groove.util.Reporter;

import java.util.HashMap;
import java.util.HashSet;
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
public class SystemRecord {
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
		return new DefaultNode(nodeCounter);
	}

	/** Returns the stored rule system on which the derivations are based. */
	public RuleSystem getRuleSystem() {
		return ruleSystem;
	}

	/** Returns a rule application for a given rule and matching of that rule. */
	public RuleApplication getApplication(Rule rule, Matching matching) {
		return getEvent(rule, matching.elementMap()).newApplication(matching.cod());
	}
	
	/** Returns a rule application for a given rule and matching of that rule. */
	public RuleApplication getApplication(Rule rule, Matching matching, GraphTestOutcome<GraphCondition, Matching> outcome) {
		return getEvent(rule, matching, outcome).newApplication(matching.cod());
	}

	public Set<RuleApplication> getApplications(Rule rule, Matching matching, GraphTestOutcome<GraphCondition, Matching> outcome) {
		Set<RuleEvent> events = getEvents(rule, matching, outcome);
		//System.out.println("Number of events created: " + events.size());
		Set<RuleApplication> applications = new HashSet<RuleApplication> ();
		for( RuleEvent evt : events ) {
			applications.add(evt.newApplication(matching.cod()));
		}
		return applications;
	}
	
	public void outcomePrinter(Matching top, GraphTestOutcome<GraphCondition, Matching> outcome) {
		System.out.println("Matching: " + top.edgeMap());
		outcomePrinter(outcome, 1);
	}
	
	public void outcomePrinter(GraphTestOutcome<GraphCondition, Matching> outcome, int depth) {
		for( GraphCondition cond : outcome.keySet() ) {
			printIndent(depth);
			System.out.println("Condition: " + cond.getName() + " (" + cond.getClass().getName() + ")");
			for( Matching match : outcome.get(cond).keySet() ) {
				printIndent(depth+1);
				System.out.println("Matching: " + match.edgeMap());
				outcomePrinter(outcome.get(cond).get(match), depth+2);
			}
		}
	}
	
	private void printIndent(int depth) {
		for( int i = 0 ; i < depth ; i++ ) {
			System.out.print("  ");
		}
	}
	
	public Set<RuleEvent> getEvents(Rule rule, Matching matching, GraphTestOutcome<GraphCondition, Matching> outcome) {
		//System.out.println("Getting events " + GraphInfo.getName(matching.cod()));
		Set<RuleEvent> events = new HashSet<RuleEvent> ();
		
		outcomePrinter(matching, outcome);
		Set<VarNodeEdgeMultiMap> elementMaps = new HashSet<VarNodeEdgeMultiMap> ();
		VarNodeEdgeMultiMap topMap = new VarNodeEdgeMultiHashMap();
		topMap.putAll(matching.elementMap());
		elementMaps.add(topMap);
		for( GraphCondition cond : outcome.keySet() ) {
			elementMaps = fillElementMaps(elementMaps, cond, outcome.get(cond));
		}
		System.out.println("Number of elementmaps created: " + elementMaps.size());
		for( VarNodeEdgeMultiMap map : elementMaps ) {
			RuleEvent result;
			if( rule.isModifying() ) {
				RuleEvent event = ((NestedRule)rule).newEvent(map, matching, outcome, this);
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
	                unmodifyingEventMap.put(rule, result = ((NestedRule)rule).newEvent(map, matching, outcome, this));
	                eventCount++;
	            }
	        }
			if( result != null ) {
				events.add(result);
			}
		}
		
		return events;
	}

	private Set<VarNodeEdgeMultiMap> fillElementMaps(Set<VarNodeEdgeMultiMap> elementMaps, GraphCondition condition, GraphTestOutcome<Matching, GraphCondition> outcome) {
		Set<VarNodeEdgeMultiMap> results = new HashSet<VarNodeEdgeMultiMap> ();
		if( condition instanceof ExistentialLevel && outcome.size() > 1 ) {
			// Duplicate maps and add choices
			for( Matching choice : outcome.keySet() ) {
				Set<VarNodeEdgeMultiMap> tmpResults = new HashSet<VarNodeEdgeMultiMap> ();
				for( VarNodeEdgeMultiMap map : elementMaps ) {
					VarNodeEdgeMultiHashMap tmpMap = new VarNodeEdgeMultiHashMap(map);
					tmpMap.putAll(choice.elementMap());
					tmpResults.add(tmpMap);
				}
				for( GraphCondition cond : outcome.get(choice).keySet() ) {
					tmpResults = fillElementMaps(tmpResults, cond, outcome.get(choice).get(cond));
				}
				results.addAll(tmpResults);
			}
		} else {
			for( Matching match : outcome.keySet() ) {
				for( VarNodeEdgeMultiMap map : elementMaps ) {
					map.putAll(match.elementMap());
					results.add(map);
				}
				for( GraphCondition cond : outcome.get(match).keySet() ) {
					results = fillElementMaps(results, cond, outcome.get(match).get(cond));
				}
				elementMaps = results;
				results = new HashSet<VarNodeEdgeMultiMap> ();
			}
			results = elementMaps;
		}
		return results;
	}
	
	public RuleEvent getEvent(Rule rule, Matching matching, GraphTestOutcome<GraphCondition, Matching> outcome) {
		RuleEvent result;
		reporter.start(GET_EVENT);
		VarNodeEdgeMultiMap elementMap = new VarNodeEdgeMultiHashMap();
		elementMap.putAll(matching.elementMap());
		for( GraphCondition cond : outcome.keySet() ) {
			fillElementMap(elementMap, outcome.get(cond));
		}
		
		//result = ((NestedRule)rule).newEvent(elementMap, matching, outcome, this);
		//eventCount++;
		//System.out.println("Events created: " + eventCount);
		
		if( rule.isModifying() ) {
			RuleEvent event = ((NestedRule)rule).newEvent(elementMap, matching, outcome, this);
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
                unmodifyingEventMap.put(rule, result = ((NestedRule)rule).newEvent(elementMap, matching, outcome, this));
                eventCount++;
            }
        }
		
		reporter.stop();
		return result;
	}

	private void fillElementMap(VarNodeEdgeMultiMap elementMap, GraphTestOutcome<Matching, GraphCondition> outcome) {
		for( Matching match : outcome.keySet() ) {
			elementMap.putAll(match.elementMap());
			for( GraphCondition cond : outcome.get(match).keySet() ) {
				fillElementMap(elementMap, outcome.get(match).get(cond));
			}
		}
	}
	
	/** 
	 * Returns an event for a given rule and matching of that rule.
	 * The events are stored internally; an event is reused if it has the
	 * correct rule and anchor map.
	 */
    public RuleEvent getEvent(Rule rule, VarNodeEdgeMap elementMap) {
    	RuleEvent result;
    	reporter.start(GET_EVENT);
        if (rule.isModifying()) {
        	//System.out.println("Rule is modifying..."+rule);
            RuleEvent event = rule.newEvent(elementMap, this);
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
        	System.out.println("Rule is not modifying..."+rule);
        	result = unmodifyingEventMap.get(rule);
            // there can be at most one event
            if (result == null) {
                unmodifyingEventMap.put(rule, result = rule.newEvent(elementMap, this));
                eventCount++;
            }
        }
        //System.out.println("Event:"+result.getName());
        reporter.stop();
        return result;
    }
//    
//    /** 
//     * Callback method to create a rule event.
//     * This implementation defers to the rule factory obtained from the rule system.
//     * @see RuleFactory#createRuleEvent(Rule, VarNodeEdgeMap, DerivationData)
//     */
//	protected RuleEvent createEvent(Rule rule, Matching matching) {
//		return getRuleFactory().createRuleEvent(rule, matching.elementMap(), this);
//	}
//	
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
     * Initializes the rule dependencies.
     */
    protected RuleDependencies getDependencies() {
    	if (dependencies == null) {
    		dependencies = new RuleDependencies(ruleSystem.getRules());
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
//    private final Map<Rule,Map<RuleEvent,RuleEvent>> normalEventMap = new HashMap<Rule,Map<RuleEvent,RuleEvent>>();
    /** 
     * Map from unmodifying rules to their (unique) events.
     */
    private final Map<Rule,RuleEvent> unmodifyingEventMap = new HashMap<Rule,RuleEvent>();

    static private final Reporter reporter = Reporter.register(RuleEvent.class);
    static private final int GET_EVENT = reporter.newMethod("getEvent");
}

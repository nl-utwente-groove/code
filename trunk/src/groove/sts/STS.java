package groove.sts;

import groove.algebra.Constant;
import groove.control.Binding;
import groove.control.Binding.Source;
import groove.grammar.Condition;
import groove.grammar.Rule;
import groove.grammar.host.HostEdge;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.grammar.host.ValueNode;
import groove.grammar.rule.AnchorKey;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleGraph;
import groove.grammar.rule.RuleLabel;
import groove.grammar.rule.RuleNode;
import groove.grammar.rule.RuleToHostMap;
import groove.grammar.rule.VariableNode;
import groove.grammar.type.TypeLabel;
import groove.graph.EdgeRole;
import groove.lts.MatchResult;
import groove.transform.RuleEvent;
import groove.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A Symbolic Transition System.
 * This contains an alternative representation of an explored GTS.
 * 
 * @author Vincent de Bruijn
 */
public class STS {

    /**
     * A mapping of generalized graphs to their corresponding location.
     */
    protected Map<HostGraph,Location> locationMap;
    /**
     * A mapping of an identifier object to its corresponding switch relation.
     */
    protected Map<Object,SwitchRelation> switchRelationMap;
    /**
     * A mapping of pairs of variable nodes and its rule to their corresponding
     * interaction variables.
     */
    protected Map<Pair<VariableNode,Rule>,InteractionVariable> interactionVariables;
    /**
     * The gates in this STS.
     */
    protected Set<Gate> gates;

    // The start location of this STS.
    private Location start;
    // The location the STS is currently at.
    private Location current;

    private final Map<Pair<Integer,TypeLabel>,LocationVariable> locationVariables;

    // Singleton class for rule inspection methods
    private RuleInspector ruleInspector;

    /**
     * Creates a new instance.
     */
    public STS() {
        this.locationMap = new HashMap<HostGraph,Location>();
        this.switchRelationMap = new HashMap<Object,SwitchRelation>();
        this.gates = new HashSet<Gate>();
        this.interactionVariables = new HashMap<Pair<VariableNode,Rule>,InteractionVariable>();
        this.locationVariables = new HashMap<Pair<Integer,TypeLabel>,LocationVariable>();
        this.ruleInspector = RuleInspector.getInstance();
    }

    /**
     * Gets the location variable represented by the given edge.
     * 
     * @param edge The edge by which the variable is represented.
     * @return The location variable.
     */
    public LocationVariable getLocationVariable(HostEdge edge) {
        return this.locationVariables.get(new Pair<Integer,TypeLabel>(edge.source().getNumber(),
            edge.label()));
    }

    /**
     * Adds a location variable to this STS.
     * 
     * @param edge The edge by which the variable is represented. Must have a
     *            ValueNode as target.
     * @param init The initial value of the variable.
     * @return The location variable.
     */
    public LocationVariable addLocationVariable(HostEdge edge, Object init) {
        ValueNode node = (ValueNode) edge.target();
        String label = LocationVariable.createLocationVariableLabel(edge);
        LocationVariable v = new LocationVariable(label, node.getSignature(), init);
        this.locationVariables.put(
            new Pair<Integer,TypeLabel>(edge.source().getNumber(), edge.label()), v);
        return v;
    }

    /**
     * Gets the current location of this STS.
     * @return The current location.
     */
    public Location getCurrentLocation() {
        return this.current;
    }

    /**
     * Gets the start location of this STS.
     * @return The start location.
     */
    public Location getStartLocation() {
        return this.start;
    }

    /**
     * Get all the location variables in this STS.
     * @return The location variables.
     */
    public Set<LocationVariable> getLocationVariables() {
        return new HashSet<LocationVariable>(this.locationVariables.values());
    }

    /**
     * Moves this STS to a given location.
     * @param l The location where to move to.
     */
    public void toLocation(Location l) {
        this.current = l;
    }

    /**
     * Gets the SwitchRelation represented by the given triple.
     * @param obj The triple.
     * @return The switch relation.
     */
    public SwitchRelation getSwitchRelation(Object obj) {
        return this.switchRelationMap.get(obj);
    }

    /**
     * Gets the interaction variable represented by the given node and rule.
     * @param node The node by which the variable is represented.
     * @param rule The rule where the node is in.
     * @return The interaction variable.
     */
    public InteractionVariable getInteractionVariable(VariableNode node, Rule rule) {
        return this.interactionVariables.get(new Pair<VariableNode,Rule>(node, rule));
    }

    /**
     * Sets the start location of this STS.
     * @param start The start location.
     */
    public void setStartLocation(Location start) {
        this.start = start;
        toLocation(start);
    }

    /**
     * Adds an interaction variable to this STS.
     * @param node The node by which the variable is represented.
     * @param rule The rule where the node is in.
     * @return The interaction variable.
     */
    public InteractionVariable addInteractionVariable(VariableNode node, Rule rule) {
        String label = InteractionVariable.createInteractionVariableLabel(rule, node);
        InteractionVariable v = new InteractionVariable(label, node.getSignature());
        this.interactionVariables.put(new Pair<VariableNode,Rule>(node, rule), v);
        return v;
    }

    /**
     * Adds a gate to this STS.
     * @param label The label of the gate.
     * @param iVars The interaction variables of the gate.
     * @return The created gate.
     */
    public Gate addGate(String label, Set<InteractionVariable> iVars) {
        Gate gate = new Gate(label, iVars);
        this.gates.add(gate);
        return gate;
    }

    /**
     * Removes a switch relation from this STS.
     */
    public void removeSwitchRelation(SwitchRelation relation) {
        this.switchRelationMap.remove(SwitchRelation.getSwitchIdentifier(relation.getGate(),
            relation.getGuard(), relation.getUpdate()));
    }

    /**
     * Transforms the given graph to a location in this STS.
     * @param graph The graph to transform.
     * @return The location.
     */
    public Location hostGraphToLocation(HostGraph graph) {
        GeneralizedGraph locationGraph = new GeneralizedGraph(graph);
        Location location = this.locationMap.get(locationGraph);
        if (location == null) {
            location = new Location("s" + this.locationMap.size());
            this.locationMap.put(locationGraph, location);
        }
        return location;
    }

    /**
     * Transforms the given graph to starting location of this STS.
     * @param graph The graph to transform.
     * @return The start location.
     */
    public Location hostGraphToStartLocation(HostGraph graph) {
        Location location = hostGraphToLocation(graph);
        setStartLocation(location);
        initializeLocationVariables(graph);
        return location;
    }

    /**
     * Transforms the given rule match to a Switch Relation.
     * @param sourceGraph The graph where the RuleMatch was matched.
     * @param match The rule match.
     * @return The transformed SwitchRelation.
     */
    public SwitchRelation ruleMatchToSwitchRelation(HostGraph sourceGraph, MatchResult match,
            Set<SwitchRelation> higherPriorityRelations) throws STSException {

        RuleEvent event = match.getEvent();

        // Map variable nodes to interaction variables in the LHS of this rule
        // (datatype node labeled as parameter in lhs).
        Map<VariableNode,InteractionVariable> iVarMap =
            new HashMap<VariableNode,InteractionVariable>();
        mapInteractionVariables(event, iVarMap);

        // Map variable nodes to location variables in the LHS of this rule
        Map<VariableNode,LocationVariable> lVarMap = new HashMap<VariableNode,LocationVariable>();
        Map<VariableNode,LocationVariable> lValueMap = new HashMap<VariableNode,LocationVariable>();
        mapLocationVariables(event, sourceGraph, lVarMap, lValueMap);

        // Create the guard
        String guard = createGuard(event, iVarMap, lVarMap, lValueMap, higherPriorityRelations);

        // Create the update for this switch relation
        String update = createUpdate(event, iVarMap, lVarMap);

        // Create the gate and the switch relation
        Gate gate =
            addGate(event.getRule().getTransitionLabel(),
                new HashSet<InteractionVariable>(iVarMap.values()));
        Object obj = SwitchRelation.getSwitchIdentifier(gate, guard, update);
        SwitchRelation switchRelation = this.switchRelationMap.get(obj);
        if (switchRelation == null) {
            switchRelation = new SwitchRelation(gate, guard, update);
            this.switchRelationMap.put(obj, switchRelation);
        }
        return switchRelation;
    }

    /**
     * Creates a JSON formatted string based on this STS.
     * The format is: {start: "label start location", lVars:
     * {<location variable>}, relations: [<switch relation>], gates: {<gate>},
     * iVars: {<interaction variable>}} <location variable> = "label": {type:
     * "variable type", init: initial value} <switch relation> = {source:
     * "label source location", gate: "label gate", target: "label target location",
     * guard: "guard", update: "update mapping"} <gate> = "label": {type: "?/!",
     * iVars: ["label interaction variable"]} <interaction variable> = "label":
     * "variable type" interaction variable label is null for tau transition.
     * 
     * @return The JSON string.
     */
    public String toJSON() {
        String json = "{\"_json\":{\"start\":" + getStartLocation().toJSON() + ",\"lVars\":{";
        Set<LocationVariable> lVars = getLocationVariables();
        for (LocationVariable v : lVars) {
            json += v.toJSON() + ",";
        }
        if (!lVars.isEmpty()) {
            json = json.substring(0, json.length() - 1);
        }
        json += "},\"relations\":[";
        for (Location l : this.locationMap.values()) {
            for (SwitchRelation r : l.getSwitchRelations()) {
                for (Location target : l.getRelationTargets(r)) {
                    json += r.toJSON(l, target) + ",";
                }
            }
        }
        json = json.substring(0, json.length() - 1) + "],\"gates\":{";
        for (Gate g : this.gates) {
            json += g.toJSON() + ",";
        }
        json = json.substring(0, json.length() - 1) + "},\"iVars\":{";
        for (InteractionVariable v : this.interactionVariables.values()) {
            json += v.toJSON() + ",";
        }
        if (!this.interactionVariables.isEmpty()) {
            json = json.substring(0, json.length() - 1);
        }
        return json + "}}}";
    }

    // **************************
    // Private methods start here
    // **************************

    /**
     * Initializes the Location variables in the start graph.
     * @param graph The start graph.
     */
    private void initializeLocationVariables(HostGraph graph) {
        for (HostEdge edge : graph.edgeSet()) {
            HostNode node = edge.target();
            if (node.getType().isDataType() && !isFinal(graph, edge.source())) {
                ValueNode valueNode = (ValueNode) node;
                addLocationVariable(edge,
                    this.ruleInspector.getSymbol((Constant) valueNode.getTerm()));
            }
        }
    }

    /**
     * Checks whether the given HostNode is considered 'final', meaning it's
     * connected data values do not change.
     * @param graph The HostGraph to which the node belongs.
     * @param node The HostNode to check.
     * @return Whether node is final or not.
     */
    private boolean isFinal(HostGraph graph, HostNode node) {
        for (HostEdge e : graph.edgeSet(node)) {
            if (e.getRole().equals(EdgeRole.FLAG) && e.label().text().equals("final")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the given HostNode is considered 'final', meaning it's
     * connected data values do not change.
     * @param graph The HostGraph to which the node belongs.
     * @param node The HostNode to check.
     * @return Whether node is final or not.
     */
    private boolean isFinal(RuleGraph graph, RuleNode node) {
        for (RuleEdge e : graph.edgeSet(node)) {
            if (e.getRole().equals(EdgeRole.FLAG) && e.label().text().equals("final")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Maps the rule variables to location variables in the source graph,
     * using a rule event.
     * @param event The rule event.
     * @param sourceGraph The graph on which the event matches.
     * @param lVarMap A map of nodes to variables to populate.
     * @param lValueMap a map of nodes to constants to populate.
     * @throws STSException Inconsistencies in the model are reported by throwing an STSException.
     */
    private void mapLocationVariables(RuleEvent event, HostGraph sourceGraph,
            Map<VariableNode,LocationVariable> lVarMap, Map<VariableNode,LocationVariable> lValueMap)
        throws STSException {

        RuleGraph lhs = event.getRule().lhs();
        RuleToHostMap ruleMap = event.getMatch(sourceGraph).getPatternMap();

        for (RuleEdge le : lhs.edgeSet()) {
            if (le.getType() != null && le.target() instanceof VariableNode) {
                HostEdge hostEdge = ruleMap.mapEdge(le);
                LocationVariable var = getLocationVariable(hostEdge);
                if (var == null && !isFinal(sourceGraph, hostEdge.source())) {
                    throw new STSException("ERROR: Data edge found not mapped by any variable: "
                        + hostEdge);
                } else if (!lVarMap.containsKey(le.target())) {
                    lVarMap.put((VariableNode) le.target(), var);
                }
            }
        }
        for (RuleNode node : lhs.nodeSet()) {
            if (node instanceof VariableNode) {
                VariableNode n = (VariableNode) node;
                if (n.getConstant() != null) {
                    for (RuleEdge re : lhs.inEdgeSet(node)) {
                        if (re.label().isAtom() || re.label().isSharp()) {
                            HostEdge hostEdge = ruleMap.mapEdge(re);
                            // It is possible that the rule edge has no image.
                            // For example, = edges are in the LHS, but have no
                            // image in the graph.
                            if (hostEdge != null) {
                                LocationVariable var = getLocationVariable(hostEdge);
                                if (var != null) {
                                    lValueMap.put(n, var);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Maps the rule variables to interaction variables in the source graph,
     * using a rule event.
     * @param event The rule event.
     * @param iVarMap A map of nodes to variables to populate.
     * @throws STSException Inconsistencies in the model are reported by throwing an STSException.
     */
    private void mapInteractionVariables(RuleEvent event,
            Map<VariableNode,InteractionVariable> iVarMap) throws STSException {

        Rule rule = event.getRule();

        int end = rule.getSignature().size();
        for (int i = 0; i < end; i++) {
            Binding bind = rule.getParBinding(i);
            assert bind.getSource() == Source.ANCHOR;
            AnchorKey k = rule.getAnchor().get(bind.getIndex());
            if (k instanceof VariableNode) {
                VariableNode v = (VariableNode) k;
                InteractionVariable iVar = addInteractionVariable(v, rule);
                iVarMap.put(v, iVar);
            } else {
                // We don't allow non-variables to be parameters
                throw new STSException("ERROR: non-variable node " + k.toString()
                    + " listed as parameter");
            }
        }
    }

    /**
     * Creates the guard from a rule event.
     * @param event The rule event.
     * @param iVarMap A map of variable nodes to interaction variables. 
     * @param lVarMap A map of variable nodes to location variables. 
     * @param lValueMap A map of constants to location variables.
     * @return The created guard.
     */
    private String createGuard(RuleEvent event, Map<VariableNode,InteractionVariable> iVarMap,
            Map<VariableNode,LocationVariable> lVarMap,
            Map<VariableNode,LocationVariable> lValueMap,
            Set<SwitchRelation> higherPriorityRelations) {

        Rule rule = event.getRule();
        RuleGraph lhs = rule.lhs();

        String guard = "";
        for (VariableNode v : iVarMap.keySet()) {
            guard +=
                this.ruleInspector.parseGuardExpression(rule, v, iVarMap.get(v), iVarMap, lVarMap);
        }
        for (VariableNode v : lVarMap.keySet()) {
            if (!iVarMap.containsKey(v)) {
                guard +=
                    this.ruleInspector.parseGuardExpression(rule, v, lVarMap.get(v), iVarMap,
                        lVarMap);
            }
        }

        if (!guard.isEmpty()) {
            guard += " && ";
        }
        // Do a one time check for expressions resulting in a known value,
        // to allow operator node with variable arguments to true/false output
        List<String> results =
            this.ruleInspector.parseArgumentExpression(rule, lhs, iVarMap, lVarMap);
        for (String s : results) {
            guard += s + " && ";
        }
        String combinedGuard = "";
        for (SwitchRelation higherPriorityRelation : higherPriorityRelations) {
            combinedGuard += higherPriorityRelation.getGuard() + " && ";
        }
        if (!combinedGuard.isEmpty()) {
            guard += "!(" + combinedGuard.substring(0, combinedGuard.length() - 4) + ") && ";
        }
        for (VariableNode v : lValueMap.keySet()) {
            guard +=
                lValueMap.get(v).getLabel() + " == "
                    + this.ruleInspector.getSymbol(v.getConstant()) + " && ";
        }
        if (guard.endsWith(" && ")) {
            guard = guard.substring(0, guard.length() - 4);
        }
        return guard;
    }

    /**
     * Creates the update from a rule event.
     * @param event The rule event.
     * @param iVarMap A map of variable nodes to interaction variables. 
     * @param lVarMap A map of variable nodes to location variables. 
     * @return The created update.
     * @throws STSException Inconsistencies in the model are reported by throwing an STSException.
     */
    private String createUpdate(RuleEvent event, Map<VariableNode,InteractionVariable> iVarMap,
            Map<VariableNode,LocationVariable> lVarMap) throws STSException {

        Rule rule = event.getRule();
        String name = rule.getFullName();
        Condition nac = rule.getCondition();

        String update = "";
        // first find the location variables undergoing an update, by finding
        // eraser edges to these variables
        Map<Pair<RuleNode,RuleLabel>,RuleEdge> possibleUpdates =
            new HashMap<Pair<RuleNode,RuleLabel>,RuleEdge>();
        for (RuleEdge e : rule.getEraserEdges()) {
            if (e.target().getType().isDataType() && !isFinal(rule.lhs(), e.source())) {
                possibleUpdates.put(new Pair<RuleNode,RuleLabel>(e.source(), e.label()), e);
            }
        }

        for (RuleEdge creatorEdge : rule.getCreatorEdges()) {
            if (creatorEdge.target().getType().isDataType()
                && !isFinal(rule.lhs(), creatorEdge.source())) {
                // A creator edge has been detected to a data node,
                // this indicates an update for a location variable.
                RuleEdge eraserEdge =
                    possibleUpdates.remove(new Pair<RuleNode,RuleLabel>(creatorEdge.source(),
                        creatorEdge.label()));
                if (eraserEdge == null) {
                    // Modeling constraint, updates have to be done in
                    // eraser/creator pairs.
                    throw new STSException(
                        "ERROR: no eraser edge found for created location variable "
                            + creatorEdge
                            + "; location variables have to be declared in start location and reference must be deleted");
                }
                Variable var = lVarMap.get(eraserEdge.target());
                if (var == null) {
                    // Data nodes should always be a location variable.
                    throw new STSException("ERROR: no location variable found referenced by "
                        + eraserEdge.target().toString() + " in the LHS or Condition of rule "
                        + name);
                }
                RuleNode node = creatorEdge.target();
                // Parse the resulting value. This can be a variable or an
                // expression over variables and primitive data types.
                String updateValue =
                    this.ruleInspector.parseExpression(rule, nac.getPattern(), node, iVarMap,
                        lVarMap);
                if (updateValue.length() == 0) {
                    // Update can't be empty. This should never happen.
                    throw new STSException("ERROR: Update of " + var.toString() + " in rule "
                        + rule.getFullName() + " is empty where it shouldn't be.");
                }
                update += var.getLabel() + " = " + updateValue + "; ";
            }
        }

        if (!possibleUpdates.isEmpty()) {
            throw new STSException("ERROR: eraser edge found without creator: "
                + possibleUpdates.values().iterator().next());
        }
        return update;
    }
}

package groove.sts;

import groove.algebra.Operator;
import groove.algebra.SignatureKind;
import groove.graph.EdgeRole;
import groove.graph.Node;
import groove.graph.algebra.OperatorNode;
import groove.graph.algebra.ValueNode;
import groove.graph.algebra.VariableNode;
import groove.lts.MatchResult;
import groove.trans.AnchorKey;
import groove.trans.HostEdge;
import groove.trans.HostFactory;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.Rule;
import groove.trans.RuleEdge;
import groove.trans.RuleEvent;
import groove.trans.RuleGraph;
import groove.trans.RuleNode;
import groove.util.Pair;

import java.util.ArrayList;
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
public abstract class STS {

    /**
     * A mapping of generalized graphs to their corresponding location.
     */
    protected Map<GeneralizedGraph,Location> locationMap;
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

    // The start location of this sts.
    private Location start;
    // The location the sts is currently at.
    private Location current;

    /**
     * Initializes the attributes of this STS.
     */
    protected void initialize() {
        this.locationMap = new HashMap<GeneralizedGraph,Location>();
        this.switchRelationMap = new HashMap<Object,SwitchRelation>();
        this.gates = new HashSet<Gate>();
        this.interactionVariables =
            new HashMap<Pair<VariableNode,Rule>,InteractionVariable>();
    }

    /**
     * Gets the current location of this sts.
     * @return The current location.
     */
    public Location getCurrentLocation() {
        return this.current;
    }

    /**
     * Gets the start location of this sts.
     * @return The start location.
     */
    public Location getStartLocation() {
        return this.start;
    }

    /**
     * Moves this sts to a given location.
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
    public InteractionVariable getInteractionVariable(VariableNode node,
            Rule rule) {
        return this.interactionVariables.get(new Pair<VariableNode,Rule>(node,
            rule));
    }

    /**
     * Sets the start location of this sts.
     * @param start The start location.
     */
    public void setStartLocation(Location start) {
        this.start = start;
        toLocation(start);
    }

    /**
     * Adds an interaction variable to this sts.
     * @param node The node by which the variable is represented.
     * @param rule The rule where the node is in.
     * @return The interaction variable.
     */
    public InteractionVariable addInteractionVariable(VariableNode node,
            Rule rule) {
        String label =
            InteractionVariable.createInteractionVariableLabel(rule, node);
        InteractionVariable v =
            new InteractionVariable(label, node.getSignature());
        this.interactionVariables.put(new Pair<VariableNode,Rule>(node, rule),
            v);
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
     * Transforms the given graph to a location in this sts.
     * @param graph The graph to transform.
     * @return The location.
     */
    public Location hostGraphToLocation(HostGraph graph) {
        GeneralizedGraph locationGraph = generalize(graph);
        Location location = this.locationMap.get(locationGraph);
        if (location == null) {
            location = new Location("s" + this.locationMap.size());
            this.locationMap.put(locationGraph, location);
        }
        return location;
    }

    /**
     * Creates interaction variables from a rule event.
     * @param event The rule event.
     * @param iVarMap A map of nodes to variables to populate.
     * @throws STSException Inconsistencies in the model are reported by throwing an STSException.
     */
    protected void createInteractionVariables(RuleEvent event,
            Map<VariableNode,InteractionVariable> iVarMap) throws STSException {

        Rule rule = event.getRule();

        int end = rule.getSignature().size();
        for (int i = 0; i < end; i++) {
            int index = rule.getParBinding(i);
            AnchorKey k = rule.getAnchor().get(index);
            if (k instanceof VariableNode) {
                VariableNode v = (VariableNode) k;
                InteractionVariable iVar = addInteractionVariable(v, rule);
                iVarMap.put(v, iVar);
            } else {
                // We don't allow non-variables to be parameters
                throw new STSException("ERROR: non-variable node "
                    + k.toString() + " listed as parameter");
            }
        }
    }

    /**
     * Transforms the given rule match to a Switch Relation.
     * @param sourceGraph The graph where the RuleMatch was matched.
     * @param match The rule match.
     * @return The transformed SwitchRelation.
     */
    public SwitchRelation ruleMatchToSwitchRelation(HostGraph sourceGraph,
            MatchResult match) throws STSException {

        RuleEvent event = match.getEvent();

        // Find all interaction variables.
        // (datatype node labeled as parameter in lhs).
        Map<VariableNode,InteractionVariable> iVarMap =
            new HashMap<VariableNode,InteractionVariable>();
        createInteractionVariables(event, iVarMap);

        // Map all location variables in the LHS of this rule
        Map<VariableNode,LocationVariable> lVarMap =
            new HashMap<VariableNode,LocationVariable>();
        createLocationVariables(event, sourceGraph, lVarMap);

        // Create the guard
        String guard = createGuard(event, iVarMap, lVarMap);

        // Create the update for this switch relation
        String update = createUpdate(event, iVarMap, lVarMap);

        // Create the gate and the switch relation
        Gate gate =
            addGate(event.getRule().getFullName(),
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
        String json =
            "{\"_json\":{\"start\":" + getStartLocation().toJSON()
                + ",\"lVars\":{";
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
                json += r.toJSON(l, l.getRelationTarget(r)) + ",";
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

    /**
     * Creates the guard from a rule event.
     * @param event The rule event.
     * @param iVarMap A map of variable nodes to interaction variables. 
     * @param lVarMap A map of variable nodes to location variables. 
     * @return The created guard.
     */
    protected String createGuard(RuleEvent event,
            Map<VariableNode,InteractionVariable> iVarMap,
            Map<VariableNode,LocationVariable> lVarMap) {

        Rule rule = event.getRule();
        RuleGraph lhs = rule.lhs();

        String guard = "";
        for (VariableNode v : iVarMap.keySet()) {
            guard +=
                parseGuardExpression(rule, v, iVarMap.get(v), iVarMap, lVarMap);
        }
        for (VariableNode v : lVarMap.keySet()) {
            if (!iVarMap.containsKey(v)) {
                guard +=
                    parseGuardExpression(rule, v, lVarMap.get(v), iVarMap,
                        lVarMap);
            }
        }

        // Do a one time check for expressions resulting in a known value,
        // to allow operator node with variable arguments to true/false output
        List<String> results =
            parseArgumentExpression(rule, lhs, iVarMap, lVarMap);
        for (String s : results) {
            guard += s + " && ";
        }
        return guard;
    }

    /**
     * Parses the guard(s) for Variable v.
     * @param rule The rule.
     * @param vn The variable node.
     * @param v The variable.
     * @param iVarMap A map of variable nodes to interaction variables. 
     * @param lVarMap A map of variable nodes to location variables. 
     * @return The guard.
     */
    protected String parseGuardExpression(Rule rule, VariableNode vn,
            Variable v, Map<VariableNode,InteractionVariable> iVarMap,
            Map<VariableNode,LocationVariable> lVarMap) {

        RuleGraph lhs = rule.lhs();
        String guard = "";
        List<String> results =
            parseAlgebraicExpression(rule, lhs, vn, iVarMap, lVarMap);
        for (String s : results) {
            guard += v.getLabel() + " == " + s + " && ";
        }
        results = parseBooleanExpression(rule, lhs, vn, iVarMap, lVarMap);
        for (String s : results) {
            guard += v.getLabel() + s + " && ";
        }
        if (!guard.isEmpty()) {
            guard = guard.substring(0, guard.length() - 4);
        }
        return guard;
    }

    /**
     * Parses an algebraic expression.
     * @param rule The rule in which the expression is found.
     * @param pattern The graph in which the expression is found.
     * @param variableResult The VariableNode which is the result of the expression.
     * @param iVarMap A map of variable nodes to interaction variables. 
     * @param lVarMap A map of variable nodes to location variables. 
     * @return All expressions found.
     */
    protected List<String> parseAlgebraicExpression(Rule rule,
            RuleGraph pattern, VariableNode variableResult,
            Map<VariableNode,InteractionVariable> iVarMap,
            Map<VariableNode,LocationVariable> lVarMap) {

        List<String> result = new ArrayList<String>();
        for (RuleNode node : pattern.nodeSet()) {
            if (node instanceof OperatorNode) {
                OperatorNode opNode = (OperatorNode) node;
                if (opNode.getTarget().equals(variableResult)) {
                    List<VariableNode> arguments = opNode.getArguments();
                    String[] subExpressions = new String[arguments.size()];
                    for (int i = 0; i < arguments.size(); i++) {
                        String newResult =
                            parseExpression(rule, pattern, arguments.get(i),
                                iVarMap, lVarMap);
                        subExpressions[i] = newResult.toString();
                    }
                    Operator op = opNode.getOperator();
                    result.add("(" + subExpressions[0] + op.getSymbol()
                        + subExpressions[1] + ")");
                }
            }
        }
        return result;
    }

    /**
     * Parses an expression where the VariableNode is an argument of an
     * OperatorNode.
     * @param rule The rule in which the expression is found.
     * @param pattern The graph in which the expression is found.
     * @param iVarMap A map of variable nodes to interaction variables. 
     * @param lVarMap A map of variable nodes to location variables. 
     * @return All expressions found.
     */
    protected List<String> parseArgumentExpression(Rule rule,
            RuleGraph pattern, Map<VariableNode,InteractionVariable> iVarMap,
            Map<VariableNode,LocationVariable> lVarMap) {

        List<String> result = new ArrayList<String>();
        for (RuleNode node : pattern.nodeSet()) {
            String value;
            if (node instanceof OperatorNode) {
                OperatorNode opNode = (OperatorNode) node;
                if ((value = opNode.getTarget().getSymbol()) != null) {
                    // opNode.getArguments().contains(variableResult) &&
                    // getInteractionVariable(variableResult) != null
                    // operatorNode refers to a node with a value
                    List<VariableNode> arguments = opNode.getArguments();
                    String[] subExpressions = new String[arguments.size()];
                    for (int i = 0; i < arguments.size(); i++) {
                        String newResult =
                            parseExpression(rule, pattern, arguments.get(i),
                                iVarMap, lVarMap);
                        subExpressions[i] = newResult.toString();
                    }
                    Operator op = opNode.getOperator();
                    String expr =
                        ("(" + subExpressions[0] + " " + op.getSymbol() + " "
                            + subExpressions[1] + ")");
                    if (!op.getResultType().equals(SignatureKind.BOOL)) {
                        expr = "(" + expr + "== " + value + ")";
                    }
                    result.add(expr);
                }
            }
        }
        return result;
    }

    /**
     * Parses a Boolean expression (edges with no operator node).
     * @param rule The rule in which the expression is found.
     * @param pattern The graph in which the expression is found.
     * @param variableResult The VariableNode which is the result of the expression.
     * @param iVarMap A map of variable nodes to interaction variables. 
     * @param lVarMap A map of variable nodes to location variables. 
     * @return All expressions found.
     */
    protected List<String> parseBooleanExpression(Rule rule, RuleGraph pattern,
            VariableNode variableResult,
            Map<VariableNode,InteractionVariable> iVarMap,
            Map<VariableNode,LocationVariable> lVarMap) {

        List<String> result = new ArrayList<String>();
        for (RuleEdge e : pattern.inEdgeSet(variableResult)) {
            if (isBooleanEdge(e)) {
                String expr =
                    parseExpression(rule, pattern, e.source(), iVarMap, lVarMap);
                result.add(" " + getOperator(e.label().text()) + " " + expr);
            }
        }
        return result;
    }

    /**
     * Parses an expression in a rule.
     * @param rule The rule in which the expression is found.
     * @param pattern The graph in which the expression is found.
     * @param resultValue The Node which is the result of the expression.
     * @param iVarMap A map of variable nodes to interaction variables. 
     * @param lVarMap A map of variable nodes to location variables. 
     * @return The expression.
     */
    protected String parseExpression(Rule rule, RuleGraph pattern,
            Node resultValue, Map<VariableNode,InteractionVariable> iVarMap,
            Map<VariableNode,LocationVariable> lVarMap) {
        VariableNode variableResult = (VariableNode) resultValue;
        // Check if the expression is a primitive value
        String symbol = variableResult.getSymbol();
        if (symbol != null) {
            return symbol;
        }
        // Check if the expression is a known interaction variable
        InteractionVariable iVar = iVarMap.get(variableResult);
        if (iVar != null) {
            return iVar.getLabel();
        }
        // Check if the expression is a known location variable
        Variable lVar = lVarMap.get(variableResult);
        if (lVar != null) {
            return lVar.getLabel();
        }
        // The expression has to be a complex expression.
        List<String> result =
            parseAlgebraicExpression(rule, pattern, variableResult, iVarMap,
                lVarMap);
        if (result.isEmpty()) {
            return "";
        } else {
            return result.get(0);
        }
    }

    /**
     * Generalizes the given graph by stripping its data values.
     * @param graph The graph to strip.
     * @return A GeneralizedGraph representing the stripped graph.
     */
    protected GeneralizedGraph generalize(HostGraph graph) {
        GeneralizedGraph generalizedGraph = new GeneralizedGraph(graph);
        HostFactory factory = generalizedGraph.getFactory();
        List<HostEdge> toRemove = new ArrayList<HostEdge>();
        for (HostEdge edge : generalizedGraph.edgeSet()) {
            HostNode node = edge.target();
            if (node.getType().isDataType()
                && !isFinal(generalizedGraph, edge.source())) {
                toRemove.add(edge);
            }
        }
        for (HostEdge edge : toRemove) {
            ValueNode valueNode = (ValueNode) edge.target();
            Object newValue = null;
            newValue = Variable.getDefaultValue(valueNode.getSignature());

            generalizedGraph.removeNode(valueNode);
            generalizedGraph.removeEdge(edge);
            ValueNode newNode =
                factory.createValueNode(valueNode.getAlgebra(), newValue);
            generalizedGraph.addNode(newNode);
            generalizedGraph.addEdge(factory.createEdge(edge.source(),
                edge.label(), newNode));
        }
        return generalizedGraph;
    }

    /**
     * Checks whether the given HostNode is considered 'final', meaning it's
     * connected data values do not change.
     * @param graph The HostGraph to which the node belongs.
     * @param node The HostNode to check.
     * @return Whether node is final or not.
     */
    protected boolean isFinal(HostGraph graph, HostNode node) {
        for (HostEdge e : graph.edgeSet(node)) {
            if (e.getRole().equals(EdgeRole.FLAG)
                && e.label().text().equals("final")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the correct operator for the switch relation guard/update syntax.
     * @param operator The edge label.
     * @return The correct operator.
     */
    protected String getOperator(String operator) {
        if (operator == "=") {
            return "==";
        } else {
            return operator;
        }
    }

    /**
     * Tests if the edge is a boolean (= or !=) edge.
     * @param edge The edge to test.
     * @return Whether the edge is a boolean edge or not.
     */
    protected static boolean isBooleanEdge(RuleEdge edge) {
        return edge.getType() == null;
    }

    // *****************
    // ABSTRACT METHODS
    // *****************

    /**
     * Get all the location variables in this STS.
     * @return The location variables.
     */
    public abstract Set<LocationVariable> getLocationVariables();

    /**
     * Transforms the given graph to starting location of this sts.
     * @param graph The graph to transform.
     * @return The start location.
     */
    public abstract Location hostGraphToStartLocation(HostGraph graph);

    /**
     * Creates location variables from a rule event.
     * @param event The rule event.
     * @param sourceGraph The graph on which the event matches.
     * @param lVarMap A map of nodes to variables to populate.
     * @throws STSException Inconsistencies in the model are reported by throwing an STSException.
     */
    protected abstract void createLocationVariables(RuleEvent event,
            HostGraph sourceGraph, Map<VariableNode,LocationVariable> lVarMap)
        throws STSException;

    /**
     * Creates the update from a rule event.
     * @param event The rule event.
     * @param iVarMap A map of variable nodes to interaction variables. 
     * @param lVarMap A map of variable nodes to location variables. 
     * @return The created update.
     * @throws STSException Inconsistencies in the model are reported by throwing an STSException.
     */
    protected abstract String createUpdate(RuleEvent event,
            Map<VariableNode,InteractionVariable> iVarMap,
            Map<VariableNode,LocationVariable> lVarMap) throws STSException;
}

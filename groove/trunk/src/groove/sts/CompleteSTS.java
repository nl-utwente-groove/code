/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
 * $Id$
 */
package groove.sts;

import groove.algebra.Operator;
import groove.algebra.SignatureKind;
import groove.graph.Node;
import groove.graph.TypeLabel;
import groove.graph.algebra.OperatorNode;
import groove.graph.algebra.ValueNode;
import groove.graph.algebra.VariableNode;
import groove.lts.MatchResult;
import groove.trans.AnchorKey;
import groove.trans.Condition;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.Rule;
import groove.trans.RuleEdge;
import groove.trans.RuleEvent;
import groove.trans.RuleGraph;
import groove.trans.RuleLabel;
import groove.trans.RuleNode;
import groove.trans.RuleToHostMap;
import groove.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * A self-sufficient Symbolic Transition System. This STS keeps track of all 
 * variables, but poses more modelling constraints.
 * 
 * @author Vincent de Bruijn
 */
public class CompleteSTS extends STS {

    private final Map<Pair<Integer,TypeLabel>,LocationVariable> locationVariables;

    /**
     * Creates a new instance.
     */
    public CompleteSTS() {
        initialize();
        this.locationVariables =
            new HashMap<Pair<Integer,TypeLabel>,LocationVariable>();
    }

    /**
     * Gets the location variable represented by the given edge.
     * 
     * @param edge
     *            The edge by which the variable is represented.
     * @return The location variable.
     */
    public LocationVariable getLocationVariable(HostEdge edge) {
        return this.locationVariables.get(new Pair<Integer,TypeLabel>(
            edge.source().getNumber(), edge.label()));
    }

    /**
     * Adds a location variable to this sts.
     * 
     * @param edge
     *            The edge by which the variable is represented. Must have a
     *            ValueNode as target.
     * @param init
     *            The initial value of the variable.
     * @return The location variable.
     */
    public LocationVariable addLocationVariable(HostEdge edge, Object init) {
        ValueNode node = (ValueNode) edge.target();
        String label = createLocationVariableLabel(edge);
        LocationVariable v =
            new LocationVariable(label, node.getSignature(), init);
        this.locationVariables.put(new Pair<Integer,TypeLabel>(
            edge.source().getNumber(), edge.label()), v);
        return v;
    }

    /**
     * Transforms the given graph to starting location of this sts.
     * 
     * @param graph
     *            The graph to transform.
     * @return The start location.
     */
    @Override
    public Location hostGraphToStartLocation(HostGraph graph) {
        Location location = hostGraphToLocation(graph);
        setStartLocation(location);
        initializeLocationVariables(graph);
        return location;
    }

    /**
     * Transforms the given rule match to a Switch Relation.
     * 
     * @param sourceGraph
     *            The graph where the RuleMatch was matched.
     * @param match
     *            The rule match.
     * @return The transformed SwitchRelation.
     */
    @Override
    public SwitchRelation ruleMatchToSwitchRelation(HostGraph sourceGraph,
            MatchResult match) throws STSException {
        // SwitchRelation switchRelation = switchRelationMapping.get(match);
        // if (switchRelation == null) {

        RuleEvent event = match.getEvent();
        Rule rule = event.getRule();
        RuleToHostMap ruleMap = event.getMatch(sourceGraph).getPatternMap();
        RuleGraph lhs = rule.lhs();
        // RuleGraph rhs = rule.rhs();
        Condition nac = rule.getCondition();

        String name = rule.getFullName();

        // Interaction variable:
        // datatype node labeled as parameter (in lhs).
        List<InteractionVariable> iVars = new ArrayList<InteractionVariable>();
        List<VariableNode> iVarNodes = new ArrayList<VariableNode>();
        int end = rule.getSignature().size();
        for (int i = 0; i < end; i++) {
            int index = rule.getParBinding(i);
            AnchorKey k = rule.getAnchor().get(index);
            if (k instanceof VariableNode) {
                VariableNode v = (VariableNode) k;
                // TODO:this naming scheme is wrong for another rule, where the
                // node.toString() is the same, but the signature is different.
                // temporary fix: add signature to label.
                InteractionVariable iVar = addInteractionVariable(v, rule);
                iVars.add(iVar);
                iVarNodes.add(v);
            } else {
                // We don't allow non-variables to be parameters
                throw new STSException("ERROR: non-variable node "
                    + k.toString() + " listed as parameter");
            }
        }
        // System.out.println(rhs.toString());
        // System.out.println(nac.toString());

        // Map all location variables in the LHS of this rule
        Map<VariableNode,Variable> varMap =
            new HashMap<VariableNode,Variable>();
        for (RuleEdge le : lhs.edgeSet()) {
            if (le.getType() != null && le.target() instanceof VariableNode) {
                HostEdge hostEdge = ruleMap.mapEdge(le);
                LocationVariable var = getLocationVariable(hostEdge);
                if (var == null && !isFinal(sourceGraph, hostEdge.source())) {
                    throw new STSException(
                        "ERROR: Data node found not mapped by any variable: "
                            + hostEdge);
                } else if (!varMap.containsKey(le.target())) {
                    varMap.put((VariableNode) le.target(), var);
                }
            }
        }

        // Create the guard for this switch relation
        // datatype nodes in the lhs are restricted by edges to/from that node
        // in
        // the lhs and nac.
        String guard = "";
        for (VariableNode v : iVarNodes) {
            StringBuffer result = new StringBuffer();
            parseAlgebraicExpression(rule, lhs, v, varMap, result);
            if (result.length() != 0) {
                guard +=
                    createInteractionVariableLabel(v) + " == " + result
                        + " && ";
            }
            result = new StringBuffer();
            parseBooleanExpression(rule, lhs, v, varMap, result);
            if (result.length() != 0) {
                guard += result;
            }
        }
        for (VariableNode v : varMap.keySet()) {
            if (!iVarNodes.contains(v)) {
                StringBuffer result = new StringBuffer();
                parseAlgebraicExpression(rule, lhs, v, varMap, result);
                if (result.length() != 0) {
                    guard +=
                        varMap.get(v).getLabel() + " == " + result + " && ";
                }
                result = new StringBuffer();
                parseBooleanExpression(rule, lhs, v, varMap, result);
                if (result.length() != 0) {
                    guard += result;
                }
            }
        }
        // Do a one time check for expressions resulting in a known value,
        // to allow operator node with variable arguments to true/false output
        StringBuffer result = new StringBuffer();
        parseArgumentExpression(rule, lhs, varMap, result);
        guard += result;
        if (guard.length() > 4) {
            guard = guard.substring(0, guard.length() - 4);
        }

        // Create the update for this switch relation
        // Update of Location variable:
        // edge to datatype node in lhs, same edge, same source to different
        // datatype node in rhs.
        // New location variable:
        // edge to datatype node in rhs, not in lhs. (should be an update,
        // declare in start state?)
        String update = "";

        // first find the location variables undergoing an update, by finding
        // eraser edges to these variables
        Map<Pair<RuleNode,RuleLabel>,RuleEdge> possibleUpdates =
            new HashMap<Pair<RuleNode,RuleLabel>,RuleEdge>();
        for (RuleEdge e : rule.getEraserEdges()) {
            if (e.target().getType().isDataType()) {
                possibleUpdates.put(
                    new Pair<RuleNode,RuleLabel>(e.source(), e.label()), e);
            }
        }

        for (RuleEdge creatorEdge : rule.getCreatorEdges()) {
            if (creatorEdge.target().getType().isDataType()) {
                // A creator edge has been detected to a data node,
                // this indicates an update for a location variable.
                RuleEdge eraserEdge =
                    possibleUpdates.get(new Pair<RuleNode,RuleLabel>(
                        creatorEdge.source(), creatorEdge.label()));
                if (eraserEdge == null) {
                    // modelling constraint for now, updates have to be done in
                    // eraser/creator pairs.
                    throw new STSException(
                        "ERROR: no eraser edge found for created location variable "
                            + creatorEdge
                            + "; location variables have to be declared in start location and reference must be deleted");
                }
                Variable var = varMap.get(eraserEdge.target());
                if (var == null) {
                    // Data nodes should always be a location variable.
                    throw new STSException(
                        "ERROR: no location variable found referenced by "
                            + eraserEdge.target().toString()
                            + " in the LHS or Condition of rule " + name);
                }
                RuleNode node = creatorEdge.target();
                // Parse the resulting value. This can be a variable or an
                // expression over variables and primite data types.
                StringBuffer updateValue = new StringBuffer();
                SignatureKind resultType =
                    parseExpression(rule, nac.getPattern(), node, varMap,
                        updateValue);
                if (updateValue.length() == 0) {
                    // Update can't be empty. This should never happen.
                    throw new STSException("ERROR: Update of " + var.toString()
                        + " in rule " + rule.getFullName()
                        + " is empty where it shouldn't be.");
                }
                if (resultType != ((VariableNode) eraserEdge.target()).getSignature()) {
                    // The result type of the expression should be the same as
                    // the type of the variable. This should never happen.
                    throw new STSException(
                        "ERROR: The result type of the expression "
                            + updateValue
                            + " ("
                            + resultType
                            + ") is not the same as the type of the location variable "
                            + var.getLabel()
                            + " ("
                            + ((VariableNode) eraserEdge.target()).getSignature()
                            + ") in rule " + name);
                }
                update += var.getLabel() + " = " + updateValue + "; ";
            }
        }

        // Create the gate and the switch relation
        Gate gate = addGate(name, iVars);
        Object obj = getSwitchIdentifier(gate, guard, update);
        SwitchRelation switchRelation = this.switchRelationMap.get(obj);
        if (switchRelation == null) {
            switchRelation = new SwitchRelation(gate, guard, update);
            this.switchRelationMap.put(obj, switchRelation);
        }
        // }
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
    @Override
    public String toJSON() {
        String json =
            "{\"_json\":{\"start\":" + getStartLocation().toJSON()
                + ",\"lVars\":{";
        for (LocationVariable v : new HashSet<LocationVariable>(
            this.locationVariables.values())) {
            json += v.toJSON() + ",";
        }
        if (!this.locationVariables.isEmpty()) {
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
     * Creates a label for a LocationVariable based on a HostEdge. Assumed is
     * that the target of the edge is a data node.
     * 
     * @param edge
     *            The edge on which the label is based.
     * @return The variable label.
     */
    private String createLocationVariableLabel(HostEdge edge) {
        return edge.label().text() + "_" + edge.source().getNumber();
    }

    /**
     * Initializes the Location variables in the start graph.
     * 
     * @param graph
     *            The start graph.
     */
    private void initializeLocationVariables(HostGraph graph) {
        for (HostEdge edge : graph.edgeSet()) {
            HostNode node = edge.target();
            if (node.getType().isDataType() && !isFinal(graph, edge.source())) {
                ValueNode valueNode = (ValueNode) node;
                addLocationVariable(edge, valueNode.getValue());
            }
        }
    }

    /**
     * Parses an expression in a rule.
     * 
     * @param rule
     *            The rule in which the expression is found.
     * @param pattern
     *            The graph in which the expression is found.
     * @param resultValue
     *            The Node which is the result of the expression.
     * @param varMap
     *            The LocationVariable mapping.
     * @param result
     *            The resulting expression.
     * @return Returns the result type of the expression.
     */
    private SignatureKind parseExpression(Rule rule, RuleGraph pattern,
            Node resultValue, Map<VariableNode,Variable> varMap,
            StringBuffer result) {
        VariableNode variableResult = (VariableNode) resultValue;
        // Check if the expression is a primitive value
        String symbol = variableResult.getSymbol();
        if (symbol != null) {
            result.append(symbol);
            return variableResult.getSignature();
        }
        // Check if the expression is a known interaction variable
        String iLabel = createInteractionVariableLabel(variableResult);
        InteractionVariable iVar = getInteractionVariable(variableResult, rule);
        if (iVar != null) {
            System.out.println(iLabel + ": " + variableResult.getNumber());
            result.append(iLabel);
            return iVar.getType();
        }
        // Check if the expression is a known location variable
        Variable lVar = varMap.get(variableResult);
        if (lVar != null) {
            result.append(lVar.getLabel());
            return lVar.getType();
        }
        // The expression has to be a complex expression.
        SignatureKind type =
            parseAlgebraicExpression(rule, pattern, variableResult, varMap,
                result);

        return type;
    }

    /**
     * Parses an algebraic expression.
     * 
     * @param rule
     *            The rule in which the expression is found.
     * @param pattern
     *            The graph in which the expression is found.
     * @param variableResult
     *            The VariableNode which is the result of the expression.
     * @param varMap
     *            The LocationVariable mapping.
     * @param result
     *            The resulting expression.
     * @return Returns the result type of the expression.
     */
    private SignatureKind parseAlgebraicExpression(Rule rule,
            RuleGraph pattern, VariableNode variableResult,
            Map<VariableNode,Variable> varMap, StringBuffer result) {
        SignatureKind type = null;
        for (RuleNode node : pattern.nodeSet()) {
            if (node instanceof OperatorNode) {
                OperatorNode opNode = (OperatorNode) node;
                if (opNode.getTarget().equals(variableResult)) {
                    List<VariableNode> arguments = opNode.getArguments();
                    String[] subExpressions = new String[arguments.size()];
                    for (int i = 0; i < arguments.size(); i++) {
                        StringBuffer newResult = new StringBuffer();
                        parseExpression(rule, pattern, arguments.get(i),
                            varMap, newResult);
                        subExpressions[i] = newResult.toString();
                    }
                    Operator op = opNode.getOperator();
                    type = op.getResultType();
                    result.append("(" + subExpressions[0] + op.getSymbol()
                        + subExpressions[1] + ")");
                    break;
                }
            }
        }
        return type;
    }

    /**
     * Parses an expression where the VariableNode is an argument of an
     * OperatorNode.
     * 
     * @param rule
     *            The rule in which the expression is found.
     * @param pattern
     *            The graph in which the expression is found.
     * @param varMap
     *            The LocationVariable mapping.
     * @param result
     *            The resulting expression.
     */
    private void parseArgumentExpression(Rule rule, RuleGraph pattern,
            Map<VariableNode,Variable> varMap, StringBuffer result) {
        SignatureKind type = null;
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
                        StringBuffer newResult = new StringBuffer();
                        parseExpression(rule, pattern, arguments.get(i),
                            varMap, newResult);
                        subExpressions[i] = newResult.toString();
                    }
                    Operator op = opNode.getOperator();
                    type = op.getResultType();
                    result.append("(" + subExpressions[0] + " "
                        + op.getSymbol() + " " + subExpressions[1] + ")");
                    if (type.equals(SignatureKind.BOOL)) {
                        result.append(" && ");
                    } else {
                        result.insert(0, "(");
                        result.append("== " + value + ") && ");
                    }
                }
            }
        }
    }

    /**
     * Parses a Boolean expression.
     * 
     * @param rule
     *            The rule in which the expression is found.
     * @param pattern
     *            The graph in which the expression is found.
     * @param variableResult
     *            The VariableNode which is the result of the expression.
     * @param varMap
     *            The LocationVariable mapping.
     * @param result
     *            The resulting expression.
     */
    private void parseBooleanExpression(Rule rule, RuleGraph pattern,
            VariableNode variableResult, Map<VariableNode,Variable> varMap,
            StringBuffer result) {
        for (RuleEdge e : pattern.inEdgeSet(variableResult)) {
            if (e.getType() == null) {
                StringBuffer expr = new StringBuffer();
                parseExpression(rule, pattern, e.source(), varMap, expr);
                result.append(varMap.get(variableResult).getLabel() + " "
                    + getOperator(e.label().text()) + " " + expr + " && ");
            }
        }
    }

    /**
     * Gets the correct operator for the switch relation guard/update syntax.
     * 
     * @param operator
     *            The edge label.
     * @return The correct operator.
     */
    private String getOperator(String operator) {
        if (operator == "=") {
            return "==";
        } else {
            return operator;
        }
    }

}

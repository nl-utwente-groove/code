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
import groove.graph.algebra.OperatorNode;
import groove.graph.algebra.VariableNode;
import groove.lts.MatchResult;
import groove.trans.AnchorKey;
import groove.trans.HostGraph;
import groove.trans.Rule;
import groove.trans.RuleEdge;
import groove.trans.RuleEvent;
import groove.trans.RuleGraph;
import groove.trans.RuleNode;

import java.util.ArrayList;
import java.util.List;

/**
 * A Symbolic Transition System for on-the-fly testing. This STS does not keep
 * track of location variables and therefore does not create updates for switch
 * relations.
 * @author Vincent de Bruijn
 * @version $Revision $
 */
public class OnTheFlySTS extends STS {

    /**
     * Constructor.
     */
    public OnTheFlySTS() {
        initialize();
    }

    @Override
    public Location hostGraphToStartLocation(HostGraph graph) {
        Location location = hostGraphToLocation(graph);
        setStartLocation(location);
        return location;
    }

    @Override
    public SwitchRelation ruleMatchToSwitchRelation(HostGraph sourceGraph,
            MatchResult match) throws STSException {
        RuleEvent event = match.getEvent();
        Rule rule = event.getRule();
        //RuleToHostMap ruleMap = event.getMatch(sourceGraph).getPatternMap();
        RuleGraph lhs = rule.lhs();
        // RuleGraph rhs = rule.rhs();
        //Condition nac = rule.getCondition();

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

        // Create the guard for this switch relation
        // datatype nodes in the lhs are restricted by edges to/from that node
        // in
        // the lhs and nac.
        String guard = "";
        for (VariableNode v : iVarNodes) {
            StringBuffer result = new StringBuffer();
            parseAlgebraicExpression(rule, lhs, v, result);
            if (result.length() != 0) {
                guard +=
                    createInteractionVariableLabel(v) + " == " + result
                        + " && ";
            }
            result = new StringBuffer();
            parseBooleanExpression(rule, lhs, v, result);
            if (result.length() != 0) {
                guard += result;
            }
        }
        // Do a one time check for expressions resulting in a known value,
        // to allow operator node with variable arguments to true/false output
        StringBuffer result = new StringBuffer();
        parseArgumentExpression(rule, lhs, result);
        guard += result;
        if (guard.length() > 4) {
            guard = guard.substring(0, guard.length() - 4);
        }

        // Create the gate and the switch relation
        Gate gate = addGate(name, iVars);
        Object obj = getSwitchIdentifier(gate, guard, "");
        SwitchRelation switchRelation = this.switchRelationMap.get(obj);
        if (switchRelation == null) {
            switchRelation = new SwitchRelation(gate, guard, "");
            this.switchRelationMap.put(obj, switchRelation);
        }
        // }
        return switchRelation;
    }

    @Override
    public String toJSON() {
        String json =
            "{\"_json\":{\"start\":" + getStartLocation().toJSON()
                + ",\"lVars\":{";
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
     * Parses an expression in a rule.
     * 
     * @param rule
     *            The rule in which the expression is found.
     * @param pattern
     *            The graph in which the expression is found.
     * @param resultValue
     *            The Node which is the result of the expression.
     * @param result
     *            The resulting expression.
     * @return Returns the result type of the expression.
     */
    private SignatureKind parseExpression(Rule rule, RuleGraph pattern,
            Node resultValue, StringBuffer result) {
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
        // The expression has to be a complex expression.
        SignatureKind type =
            parseAlgebraicExpression(rule, pattern, variableResult, result);

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
     * @param result
     *            The resulting expression.
     * @return Returns the result type of the expression.
     */
    private SignatureKind parseAlgebraicExpression(Rule rule,
            RuleGraph pattern, VariableNode variableResult, StringBuffer result) {
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
                            newResult);
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
     * @param result
     *            The resulting expression.
     */
    private void parseArgumentExpression(Rule rule, RuleGraph pattern,
            StringBuffer result) {
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
                            newResult);
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
     * @param result
     *            The resulting expression.
     */
    private void parseBooleanExpression(Rule rule, RuleGraph pattern,
            VariableNode variableResult, StringBuffer result) {
        for (RuleEdge e : pattern.inEdgeSet(variableResult)) {
            if (e.getType() == null) {
                StringBuffer expr = new StringBuffer();
                parseExpression(rule, pattern, e.source(), expr);
                result.append(getInteractionVariable(variableResult, rule).getLabel()
                    + " " + getOperator(e.label().text()) + " " + expr + " && ");
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

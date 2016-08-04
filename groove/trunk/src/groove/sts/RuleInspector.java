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

import groove.algebra.Constant;
import groove.algebra.Operator;
import groove.algebra.Sort;
import groove.grammar.Rule;
import groove.grammar.rule.OperatorNode;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleGraph;
import groove.grammar.rule.RuleNode;
import groove.grammar.rule.VariableNode;
import groove.graph.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class contains methods for rule inspection.
 * Rule inspection is needed when constructing the guards and updates of
 * switch relations. The methods parse the expressions from the rule, given a
 * mapping of nodes to location/interaction variables.
 * @author vincentdebruijn
 * @version $Revision $
 */
public class RuleInspector {
    private static RuleInspector instance;

    // private constructor
    private RuleInspector() {
        // empty
    }

    /**
     * Gets the singleton instance of this class.
     * @return a RuleInspector
     */
    public static RuleInspector getInstance() {
        if (instance == null) {
            instance = new RuleInspector();
        }
        return instance;
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
    public String parseGuardExpression(Rule rule, VariableNode vn, Variable v,
            Map<VariableNode,InteractionVariable> iVarMap,
            Map<VariableNode,LocationVariable> lVarMap) {

        RuleGraph lhs = rule.lhs();
        String guard = "";
        // Check if the variable is a primitive value
        if (vn.hasConstant()) {
            return v.getLabel() + " == " + getSymbol(vn.getConstant());
        }
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
    public List<String> parseAlgebraicExpression(Rule rule, RuleGraph pattern,
            VariableNode variableResult,
            Map<VariableNode,InteractionVariable> iVarMap,
            Map<VariableNode,LocationVariable> lVarMap) {

        List<String> result = new ArrayList<>();
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
                    result.add("(" + subExpressions[0]
                        + getOperator(op.getSymbol()) + subExpressions[1] + ")");
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
    public List<String> parseArgumentExpression(Rule rule, RuleGraph pattern,
            Map<VariableNode,InteractionVariable> iVarMap,
            Map<VariableNode,LocationVariable> lVarMap) {

        List<String> result = new ArrayList<>();
        for (RuleNode node : pattern.nodeSet()) {
            if (node instanceof OperatorNode) {
                OperatorNode opNode = (OperatorNode) node;
                if (opNode.getTarget().hasConstant()) {
                    // opNode.getArguments().contains(variableResult) &&
                    // getInteractionVariable(variableResult) != null
                    // operatorNode refers to a node with a value
                    String value =
                        opNode.getTarget().getConstant().toDisplayString();
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
                    if (!op.getResultType().equals(Sort.BOOL)) {
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
    public List<String> parseBooleanExpression(Rule rule, RuleGraph pattern,
            VariableNode variableResult,
            Map<VariableNode,InteractionVariable> iVarMap,
            Map<VariableNode,LocationVariable> lVarMap) {

        List<String> result = new ArrayList<>();
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
     * Formats a constant to the correct JSON representation of a primitive value.
     * @param constant The constant to format
     * @return The formatted value
     */
    public String getSymbol(Constant constant) {
        String result = constant.toDisplayString();
        if (constant.getSort() == Sort.STRING) {
            // add a layer of \-escapes
            result = result.replace("\\", "\\\\");
            result = result.replace("\"", "\\\"");
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
    public String parseExpression(Rule rule, RuleGraph pattern,
            Node resultValue, Map<VariableNode,InteractionVariable> iVarMap,
            Map<VariableNode,LocationVariable> lVarMap) {
        VariableNode variableResult = (VariableNode) resultValue;
        // Check if the expression is a primitive value
        if (variableResult.hasConstant()) {
            return variableResult.getConstant().toDisplayString();
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

    // ***************
    // Private methods
    // ***************

    /**
     * Tests if the edge is a boolean (= or !=) edge.
     * @param edge The edge to test.
     * @return Whether the edge is a boolean edge or not.
     */
    private boolean isBooleanEdge(RuleEdge edge) {
        return edge.getType() == null;
    }

    /**
     * Gets the correct operator for the switch relation guard/update syntax.
     * @param operator The edge label.
     * @return The correct operator.
     */
    private String getOperator(String operator) {
        if (operator == "=") {
            return "==";
        } else if (operator == "|") {
            return "||";
        } else if (operator == "&") {
            return "&&";
        } else {
            return operator;
        }
    }

}

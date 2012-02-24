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

import groove.graph.TypeLabel;
import groove.graph.algebra.ValueNode;
import groove.graph.algebra.VariableNode;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        String label = LocationVariable.createLocationVariableLabel(edge);
        LocationVariable v =
            new LocationVariable(label, node.getSignature(), init);
        this.locationVariables.put(new Pair<Integer,TypeLabel>(
            edge.source().getNumber(), edge.label()), v);
        return v;
    }

    @Override
    public Location hostGraphToStartLocation(HostGraph graph) {
        Location location = hostGraphToLocation(graph);
        setStartLocation(location);
        initializeLocationVariables(graph);
        return location;
    }

    @Override
    public Set<LocationVariable> getLocationVariables() {
        return new HashSet<LocationVariable>(this.locationVariables.values());
    }

    @Override
    protected void createLocationVariables(RuleEvent event,
            HostGraph sourceGraph, Map<VariableNode,LocationVariable> lVarMap)
        throws STSException {

        RuleGraph lhs = event.getRule().lhs();
        RuleToHostMap ruleMap = event.getMatch(sourceGraph).getPatternMap();

        for (RuleEdge le : lhs.edgeSet()) {
            if (le.getType() != null && le.target() instanceof VariableNode) {
                HostEdge hostEdge = ruleMap.mapEdge(le);
                LocationVariable var = getLocationVariable(hostEdge);
                if (var == null && !isFinal(sourceGraph, hostEdge.source())) {
                    throw new STSException(
                        "ERROR: Data node found not mapped by any variable: "
                            + hostEdge);
                } else if (!lVarMap.containsKey(le.target())) {
                    lVarMap.put((VariableNode) le.target(), var);
                }
            }
        }
    }

    @Override
    protected String createUpdate(RuleEvent event,
            Map<VariableNode,InteractionVariable> iVarMap,
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
                    possibleUpdates.remove(new Pair<RuleNode,RuleLabel>(
                        creatorEdge.source(), creatorEdge.label()));
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
                    throw new STSException(
                        "ERROR: no location variable found referenced by "
                            + eraserEdge.target().toString()
                            + " in the LHS or Condition of rule " + name);
                }
                RuleNode node = creatorEdge.target();
                // Parse the resulting value. This can be a variable or an
                // expression over variables and primitive data types.
                String updateValue =
                    parseExpression(rule, nac.getPattern(), node, iVarMap,
                        lVarMap);
                if (updateValue.length() == 0) {
                    // Update can't be empty. This should never happen.
                    throw new STSException("ERROR: Update of " + var.toString()
                        + " in rule " + rule.getFullName()
                        + " is empty where it shouldn't be.");
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

    /**
     * Initializes the Location variables in the start graph.
     * @param graph The start graph.
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

}

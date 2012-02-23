package groove.sts;

import groove.algebra.SignatureKind;
import groove.graph.EdgeRole;
import groove.graph.algebra.ValueNode;
import groove.graph.algebra.VariableNode;
import groove.lts.MatchResult;
import groove.trans.HostEdge;
import groove.trans.HostFactory;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.Rule;
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
     * The gates in this STS.
     */
    protected Set<Gate> gates;
    /**
     * A mapping of pairs of variable nodes and its rule to their corresponding
     * interaction variables.
     */
    protected Map<Pair<VariableNode,Rule>,InteractionVariable> interactionVariables;

    private Location start;
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
     * Sets the start location of this sts.
     * 
     * @param start
     *            The start location.
     */
    public void setStartLocation(Location start) {
        this.start = start;
        toLocation(start);
    }

    /**
     * Gets the current location of this sts.
     * 
     * @return The current location.
     */
    public Location getCurrentLocation() {
        return this.current;
    }

    /**
     * Gets the start location of this sts.
     * 
     * @return The start location.
     */
    public Location getStartLocation() {
        return this.start;
    }

    /**
     * Moves this sts to a given location.
     * 
     * @param l
     *            The location where to move to.
     */
    public void toLocation(Location l) {
        this.current = l;
    }

    /**
     * Gets the SwitchRelation represented by the given triple.
     * 
     * @param obj
     *            The triple.
     * @return The switch relation.
     */
    public SwitchRelation getSwitchRelation(Object obj) {
        return this.switchRelationMap.get(obj);
    }

    /**
     * Gets a unique identifier object for Switch Relations.
     * 
     * @param gate
     *            The gate of the Switch Relation.
     * @param guard
     *            The guard of the Switch Relation.
     * @param update
     *            The update of the Switch Relation.
     * @return A unique identifier object.
     */
    public Object getSwitchIdentifier(Gate gate, String guard, String update) {
        // TODO: replace with triple
        return gate.getLabel() + guard + update;
    }

    /**
     * Gets the interaction variable represented by the given node and rule.
     * 
     * @param node
     *            The node by which the variable is represented.
     * @param rule
     *            The rule where the node is in.
     * @return The interaction variable.
     */
    public InteractionVariable getInteractionVariable(VariableNode node,
            Rule rule) {
        return this.interactionVariables.get(new Pair<VariableNode,Rule>(node,
            rule));
    }

    /**
     * Adds an interaction variable to this sts.
     * 
     * @param node
     *            The node by which the variable is represented.
     * @param rule
     *            The rule where the node is in.
     * @return The interaction variable.
     */
    public InteractionVariable addInteractionVariable(VariableNode node,
            Rule rule) {
        String label = createInteractionVariableLabel(node);
        InteractionVariable v =
            new InteractionVariable(label, node.getSignature());
        this.interactionVariables.put(new Pair<VariableNode,Rule>(node, rule),
            v);
        return v;
    }

    /**
     * Adds a gate to this STS.
     * 
     * @param label
     *            The label of the gate.
     * @param iVars
     *            The interaction variables of the gate.
     * @return The created gate.
     */
    public Gate addGate(String label, List<InteractionVariable> iVars) {
        Gate gate = new Gate(label, iVars);
        this.gates.add(gate);
        return gate;
    }

    /**
     * Transforms the given graph to a location in this sts.
     * 
     * @param graph
     *            The graph to transform.
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
     * Transforms the given graph to starting location of this sts.
     * 
     * @param graph
     *            The graph to transform.
     * @return The start location.
     */
    public abstract Location hostGraphToStartLocation(HostGraph graph);

    /**
     * Transforms the given rule match to a Switch Relation.
     * 
     * @param sourceGraph
     *            The graph where the RuleMatch was matched.
     * @param match
     *            The rule match.
     * @return The transformed SwitchRelation.
     */
    public abstract SwitchRelation ruleMatchToSwitchRelation(
            HostGraph sourceGraph, MatchResult match) throws STSException;

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
    public abstract String toJSON();

    /**
     * Creates a label for an InteractionVariable based on a VariableNode.
     * 
     * @param node
     *            The node on which the label is based.
     * @return The variable label.
     */
    protected String createInteractionVariableLabel(VariableNode node) {
        return node.toString();
    }

    /**
     * Generalizes the given graph by stripping its data values.
     * 
     * @param graph
     *            The graph to strip.
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
            newValue = getDefaultValue(valueNode.getSignature());

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
     * Gets the default value of a variable with type s.
     * 
     * @param s
     *            The type.
     * @return The default value.
     */
    protected Object getDefaultValue(SignatureKind s) {
        switch (s) {
        case INT:
            return new Integer(0);
        case BOOL:
            return new Boolean(false);
        case REAL:
            return new Double(0.0);
        case STRING:
            return "";
        default:
            return null;
        }
    }

    /**
     * Checks whether the given HostNode is considered 'final', meaning it's
     * connected data values do not change.
     * 
     * @param graph
     *            The HostGraph to which the node belongs.
     * @param node
     *            The HostNode to check.
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
}

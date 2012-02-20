package groove.sts;

import groove.algebra.Operator;
import groove.algebra.SignatureKind;
import groove.graph.EdgeRole;
import groove.graph.Node;
import groove.graph.TypeLabel;
import groove.graph.algebra.OperatorNode;
import groove.graph.algebra.ValueNode;
import groove.graph.algebra.VariableNode;
import groove.lts.MatchResult;
import groove.trans.AnchorKey;
import groove.trans.Condition;
import groove.trans.HostEdge;
import groove.trans.HostFactory;
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
import java.util.Set;

/*
 * A Symbolic Transition System.
 * This contains the sts representing the GTS explored so far.
 */
public class STS {
	
	private Location start;
	private Location current;
	private Map<GeneralizedGraph, Location> locationMap;
	private Map<Object, SwitchRelation> switchRelationMap;
	private Set<Gate> gates;
	// not injective
	private Map<Pair<Integer, TypeLabel>, LocationVariable> locationVariables;
	// injective
	private Map<Pair<VariableNode, Rule>, InteractionVariable> interactionVariables;
	
	/**
	 * Creates a new instance.
	 */
	public STS() {
		this.locationMap = new HashMap<GeneralizedGraph, Location>();
		this.switchRelationMap = new HashMap<Object, SwitchRelation>();
		this.locationVariables = new HashMap<Pair<Integer, TypeLabel>, LocationVariable>();
		this.gates = new HashSet<Gate>();
		this.interactionVariables = new HashMap<Pair<VariableNode, Rule>, InteractionVariable>();
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
	 * Gets the current location of this sts.
	 * @return The current location.
	 */
	public Location getCurrentLocation() {
		return current;
	}
	
	/**
	 * Moves this sts to a given location.
	 * @param l The location where to move to.
	 */
	public void toLocation(Location l) {
		current = l;
	}
	
	/**
	 * Gets the SwitchRelation represented by the given triple.
	 * @param obj The triple.
	 * @return The switch relation.
	 */
	public SwitchRelation getSwitchRelation(Object obj) {
		return switchRelationMap.get(obj);
	}
	
	/**
	 * Gets the location represented by the given graph.
	 * @param graph The graph by which the location is represented.
	 * @return The location.
	 */
	public Location getLocation(GeneralizedGraph graph) {
		return locationMap.get(graph);
	}
	
	/**
	 * Gets the location variable represented by the given edge.
	 * @param edge The edge by which the variable is represented.
	 * @return The location variable.
	 */
	public LocationVariable getLocationVariable(HostEdge edge) {
		return this.locationVariables.get(new Pair<Integer, TypeLabel>(edge.source().getNumber(), edge.label()));
	}
	
	/**
	 * Gets the interaction variable represented by the given node and rule.
	 * @param node The node by which the variable is represented.
	 * @param rule The rule where the node is in.
	 * @return The interaction variable.
	 */
	public InteractionVariable getInteractionVariable(VariableNode node, Rule rule) {
		return this.interactionVariables.get(new Pair<VariableNode, Rule>(node,rule));
	}
	
	/**
	 * Adds a location variable to this sts.
	 * @param edge The edge by which the variable is represented. Must have a ValueNode as target.
	 * @param init The initial value of the variable.
	 * @return The location variable.
	 */
	public LocationVariable addLocationVariable(HostEdge edge, Object init) {
		ValueNode node = (ValueNode)edge.target();
		String label = createLocationVariableLabel(edge);
		LocationVariable v = new LocationVariable(label, node.getSignature(), init);
		this.locationVariables.put(new Pair<Integer, TypeLabel>(edge.source().getNumber(), edge.label()), v);
		return v;
	}
	
	/**
	 * Adds an interaction variable to this sts.
	 * @param node The node by which the variable is represented.
	 * @param rule The rule where the node is in.
	 * @return The interaction variable.
	 */
	public InteractionVariable addInteractionVariable(VariableNode node, Rule rule) {
		String label = createInteractionVariableLabel(node);
		InteractionVariable v = new InteractionVariable(label, node.getSignature());
		this.interactionVariables.put(new Pair<VariableNode, Rule>(node, rule), v);
		return v;
	}
	
	/**
	 * Adds a gate to this STS.
	 * @param label The label of the gate.
	 * @param iVars The interaction variables of the gate.
	 * @return The created gate.
	 */
	public Gate addGate(String label, List<InteractionVariable> iVars) {
		Gate gate = new Gate(label, iVars);
		this.gates.add(gate);
		return gate;
	}
	
	/**
	 * Gets the default value of a variable with type s.
	 * @param s The type.
	 * @return The default value.
	 */
	public Object getDefaultValue(SignatureKind s) {
		switch(s) {
			case INT: return new Integer(0);
			case BOOL: return new Boolean(false);
			case REAL: return new Double(0.0);
			case STRING: return "";
			default: return null;
		}
	}
	
	/**
	 * Creates a label for an InteractionVariable based on a VariableNode.
	 * @param node The node on which the label is based.
	 * @return The variable label.
	 */
	public String createInteractionVariableLabel(VariableNode node) {
		return node.toString();
	}
	
	/**
	 * Creates a label for a LocationVariable based on a HostEdge.
	 * Assumed is that the target of the edge is a data node.
	 * @param edge The edge on which the label is based.
	 * @return The variable label.
	 */
	public String createLocationVariableLabel(HostEdge edge) {
		return edge.label().text()+"_"+edge.source().getNumber();
	}
	
	/**
	 * Transforms the given graph to a location in this sts.
	 * @param graph The graph to transform.
	 * @return The location.
	 */
	public Location hostGraphToLocation(HostGraph graph) {
		GeneralizedGraph locationGraph = generalize(graph);
		Location location = locationMap.get(locationGraph);
		if (location == null) {
			location = new Location("s"+locationMap.size());
			locationMap.put(locationGraph, location);
		}
		return location;
	}
	
	/**
	 * Transforms the given graph to starting location of this sts.
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
	public SwitchRelation ruleMatchToSwitchRelation(HostGraph sourceGraph, MatchResult match) {			
		//SwitchRelation switchRelation = switchRelationMapping.get(match);
		//if (switchRelation == null) {

			RuleEvent event = match.getEvent();
			Rule rule = event.getRule();
			RuleToHostMap ruleMap = event.getMatch(sourceGraph).getPatternMap();
			RuleGraph lhs = rule.lhs();
			RuleGraph rhs = rule.rhs();
			Condition nac = rule.getCondition();

			String name = rule.getFullName();
			
			// Interaction variable:
			// datatype node labeled as parameter (in lhs).
			List<InteractionVariable> iVars = new ArrayList<InteractionVariable>();
			List<VariableNode> iVarNodes = new ArrayList<VariableNode>();
			int end = rule.getSignature().size();
			for (int i = 0; i < end; i++){
				int index = rule.getParBinding(i);
				AnchorKey k = rule.getAnchor().get(index);
				if (k instanceof VariableNode) {
					VariableNode v = (VariableNode)k;
					// TODO:this naming scheme is wrong for another rule, where the node.toString() is the same, but the signature is different.
					// temporary fix: add signature to label.
					InteractionVariable iVar = addInteractionVariable(v,rule);
					iVars.add(iVar);
					iVarNodes.add(v);
				} else {
					// We don't allow non-variables to be parameters
					System.out.println("ERROR: non-variable node "+k.toString()+" listed as parameter");
				}
			}
			//System.out.println(rhs.toString());
			//System.out.println(nac.toString());
			
			// Map all location variables in the LHS of this rule
			Map<VariableNode, Variable> varMap = new HashMap<VariableNode, Variable>();
			for (RuleEdge le : lhs.edgeSet()) {
				if (le.getType() != null && le.target() instanceof VariableNode) {
					HostEdge hostEdge = ruleMap.mapEdge(le);
					LocationVariable var = getLocationVariable(hostEdge);
					if (var == null && !isFinal(sourceGraph, hostEdge.source())) {
						System.out.println("ERROR: Data node found not mapped by any variable: "+hostEdge);
					} else if (!varMap.containsKey(le.target())) {
						varMap.put((VariableNode)le.target(), var);
					}
				}
			}

			// Create the guard for this switch relation
			// datatype nodes in the lhs are restricted by edges to/from that node in
			// the lhs and nac.
			String guard = "";
			for (VariableNode v : iVarNodes) {
				StringBuffer result = new StringBuffer();
				parseAlgebraicExpression(rule, lhs, v, varMap, result);
				if (result.length() != 0) {
					guard+=createInteractionVariableLabel(v)+" == "+result+" && ";
				}
				result = new StringBuffer();
				parseBooleanExpression(rule, lhs, v, varMap, result);
				if (result.length() != 0) {
					guard+=result;
				}
			}
			for (VariableNode v : varMap.keySet()) {
				if (!iVarNodes.contains(v)) {
					StringBuffer result = new StringBuffer();
					parseAlgebraicExpression(rule, lhs, v, varMap, result);
					if (result.length() != 0) {
						guard+=varMap.get(v).getLabel()+" == "+result+" && ";
					}
					result = new StringBuffer();
					parseBooleanExpression(rule, lhs, v, varMap, result);
					if (result.length() != 0) {
						guard+=result;
					}
				}
			}
			// Do a one time check for expressions resulting in a known value,
			// to allow operator node with variable arguments to true/false output
			StringBuffer result = new StringBuffer();
			parseArgumentExpression(rule, lhs, varMap, result);
			guard+=result;
			if (guard.length() > 4)
				guard = guard.substring(0, guard.length()-4);
			
			
			// Create the update for this switch relation
			// Update of Location variable:
			// edge to datatype node in lhs, same edge, same source to different
			// datatype node in rhs.
			// New location variable:
			// edge to datatype node in rhs, not in lhs. (should be an update, declare in start state?)
			String update = "";
			
			// first find the location variables undergoing an update, by finding eraser edges to these variables
			Map<Pair<RuleNode, RuleLabel>, RuleEdge> possibleUpdates = new HashMap<Pair<RuleNode, RuleLabel>, RuleEdge>();
			for (RuleEdge e : rule.getEraserEdges()) {
				if (e.target().getType().isDataType()) {
					possibleUpdates.put(new Pair<RuleNode, RuleLabel>(e.source(), e.label()), e);
				}
			}

			for (RuleEdge creatorEdge : rule.getCreatorEdges()) {
				if (creatorEdge.target().getType().isDataType()) {
					// A creator edge has been detected to a data node,
					// this indicates an update for a location variable.
					RuleEdge eraserEdge = possibleUpdates.get(new Pair<RuleNode, RuleLabel>(creatorEdge.source(), creatorEdge.label()));
					if (eraserEdge == null) {
						// modelling constraint for now, updates have to be done in eraser/creator pairs.
						System.out.println("ERROR: no eraser edge found for created location variable "+creatorEdge+"; location variables have to be declared in start location and reference must be deleted");
					}
					Variable var = varMap.get(eraserEdge.target());
					if (var == null) {
						// Data nodes should always be a location variable.
						System.out.println("ERROR: no location variable found referenced by "+eraserEdge.target().toString()+" in the LHS or Condition of rule "+name);
					}
					RuleNode node = creatorEdge.target();
					// Parse the resulting value. This can be a variable or an expression over variables and primite data types.
					StringBuffer updateValue = new StringBuffer();
					SignatureKind resultType = parseExpression(rule, nac.getPattern(), node, varMap, updateValue);
					if (updateValue.length() == 0) {
						// Update can't be empty. This should never happen.
						System.out.println("ERROR: Update of "+var.toString()+" in rule "+rule.getFullName()+" is empty where it shouldn't be.");
					}
					if (resultType != ((VariableNode)eraserEdge.target()).getSignature()) {
						// The result type of the expression should be the same as the type of the variable. This should never happen.
						System.out.println("ERROR: The result type of the expression "+updateValue+" ("+resultType+") is not the same as the type of the location variable "+var.getLabel()+" ("+((VariableNode)eraserEdge.target()).getSignature()+") in rule "+name);
					}
					update += var.getLabel()+" = "+updateValue+"; ";
				}
			}
			
			// Create the gate and the switch relation
			Gate gate = addGate(name, iVars);
			Object obj = getSwitchIdentifier(gate,guard,update);
			SwitchRelation switchRelation = switchRelationMap.get(obj);
			if (switchRelation == null) {
				switchRelation = new SwitchRelation(gate, guard, update);
				switchRelationMap.put(obj, switchRelation);
			}
		//}
		return switchRelation;
	}
	
	/**
	 * Creates a JSON formatted string based on this STS.
	 * @return The JSON string.
	 */
	public String toJSON() {
		String json = "{\"_json\":{\"start\":"+start.toJSON()+",\"lVars\":{";
		for (LocationVariable v : new HashSet<LocationVariable>(this.locationVariables.values())) {
			json+=v.toJSON()+",";
		}
		if (!this.locationVariables.isEmpty())
			json = json.substring(0, json.length()-1);
		json+="},\"relations\":[";
		for (Location l : locationMap.values()) {
			for (SwitchRelation r : l.getSwitchRelations()) {
				json+=r.toJSON(l, l.getRelationTarget(r))+",";
			}
		}
		json = json.substring(0, json.length()-1)+"],\"gates\":{";
		for (Gate g : this.gates) {
			json += g.toJSON()+",";
		}
		json = json.substring(0, json.length()-1)+"},\"iVars\":{";
		for (InteractionVariable v : this.interactionVariables.values()) {
			json += v.toJSON()+",";
		}
		if (!this.interactionVariables.isEmpty())
			json = json.substring(0, json.length()-1);
		return json+"}}}";
	}
	
	/**
	 * Gets a unique identifier object for Switch Relations.
	 * @param gate The gate of the Switch Relation.
	 * @param guard The guard of the Switch Relation.
	 * @param update The update of the Switch Relation.
	 * @return A unique identifier object.
	 */
	private Object getSwitchIdentifier(Gate gate, String guard, String update) {
		//TODO: replace with triple
		return gate.getLabel()+guard+update;
	}
	
	/**
	 * Generalizes the given graph by stripping its data values.
	 * @param graph The graph to strip.
	 * @return A GeneralizedGraph representing the stripped graph.
	 */
	private GeneralizedGraph generalize(HostGraph graph) {
		GeneralizedGraph generalizedGraph = new GeneralizedGraph(graph);
		HostFactory factory = generalizedGraph.getFactory();
		List<HostEdge> toRemove = new ArrayList<HostEdge>();
		for (HostEdge edge : generalizedGraph.edgeSet()) {
			HostNode node = edge.target();
			if (node.getType().isDataType() && !isFinal(generalizedGraph, edge.source())) {
				toRemove.add(edge);
			}
		}
		for (HostEdge edge : toRemove) {
			ValueNode valueNode = (ValueNode)edge.target();
			Object newValue = null;
			newValue = getDefaultValue(valueNode.getSignature());
			
			generalizedGraph.removeNode(valueNode);
			generalizedGraph.removeEdge(edge);
			ValueNode newNode = factory.createValueNode(valueNode.getAlgebra(), newValue);
			generalizedGraph.addNode(newNode);
			generalizedGraph.addEdge(factory.createEdge(edge.source(), edge.label(), newNode));
		}
		return generalizedGraph;
	}
	
	/**
	 * Initializes the Location variables in the start graph.
	 * @param graph The start graph.
	 */
	private void initializeLocationVariables(HostGraph graph) {
		for (HostEdge edge : graph.edgeSet()) {
			HostNode node = edge.target();
			if (node.getType().isDataType() && !isFinal(graph, edge.source())) {
				ValueNode valueNode = (ValueNode)node;
				addLocationVariable(edge, valueNode.getValue());
			}
		}
	}
	
	/**
	 * Parses an expression in a rule.
	 * @param rule The rule in which the expression is found.
	 * @param pattern The graph in which the expression is found.
	 * @param resultValue The Node which is the result of the expression.
	 * @param varMap The LocationVariable mapping.
	 * @param result The resulting expression.
	 * @return Returns the result type of the expression.
	 */
	private SignatureKind parseExpression(Rule rule, RuleGraph pattern, Node resultValue, Map<VariableNode, Variable> varMap, StringBuffer result) {
		VariableNode variableResult = (VariableNode)resultValue;
		// Check if the expression is a primitive value
		String symbol = variableResult.getSymbol();
		if (symbol != null) {
			result.append(symbol);
			return variableResult.getSignature();
		}
		// Check if the expression is a known interaction variable
		String iLabel = createInteractionVariableLabel(variableResult);
		InteractionVariable iVar = getInteractionVariable(variableResult,rule);
		if (iVar != null) {
			System.out.println(iLabel+": "+variableResult.getNumber());
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
		SignatureKind type = parseAlgebraicExpression(rule, pattern, variableResult, varMap, result);
		
		return type;
	}

	/**
	 * Parses an algebraic expression.
	 * @param rule The rule in which the expression is found.
	 * @param pattern The graph in which the expression is found.
	 * @param variableResult The VariableNode which is the result of the expression.
	 * @param varMap The LocationVariable mapping.
	 * @param result The resulting expression.
	 * @return Returns the result type of the expression.
	 */
	private SignatureKind parseAlgebraicExpression(Rule rule, RuleGraph pattern, VariableNode variableResult, Map<VariableNode, Variable> varMap, StringBuffer result) {
		SignatureKind type = null;
		for (RuleNode node : pattern.nodeSet()) {
			if (node instanceof OperatorNode) {
	           	OperatorNode opNode = (OperatorNode)node;
	           	if (opNode.getTarget().equals(variableResult)) {
	           		List<VariableNode> arguments = opNode.getArguments();
	           		String[] subExpressions = new String[arguments.size()];
	           		for (int i = 0; i < arguments.size(); i++) {
	           			StringBuffer newResult = new StringBuffer();
	           			parseExpression(rule, pattern, arguments.get(i), varMap, newResult);
	           			subExpressions[i] = newResult.toString();
	           		}
	           		Operator op = opNode.getOperator();
	           		type = op.getResultType();
	           		result.append("("+subExpressions[0]+op.getSymbol()+subExpressions[1]+")");
	           		break;
           		}
           	}
		}
		return type;
	}
	
	/**
	 * Parses an expression where the VariableNode is an argument of an OperatorNode.
	 * @param rule The rule in which the expression is found.
	 * @param pattern The graph in which the expression is found.
	 * @param varMap The LocationVariable mapping.
	 * @param result The resulting expression.
	 */
	private void parseArgumentExpression(Rule rule, RuleGraph pattern, Map<VariableNode, Variable> varMap, StringBuffer result) {
		SignatureKind type = null;
		for (RuleNode node : pattern.nodeSet()) {
           	String value;
			if (node instanceof OperatorNode) {
				OperatorNode opNode = (OperatorNode)node;
				if ((value = opNode.getTarget().getSymbol()) != null) {
	       			// opNode.getArguments().contains(variableResult) && getInteractionVariable(variableResult) != null
	       			// operatorNode refers to a node with a value
	       			List<VariableNode> arguments = opNode.getArguments();
	           		String[] subExpressions = new String[arguments.size()];
	           		for (int i = 0; i < arguments.size(); i++) {
	           			StringBuffer newResult = new StringBuffer();
	           			parseExpression(rule, pattern, arguments.get(i), varMap, newResult);
	           			subExpressions[i] = newResult.toString();
	           		}
	           		Operator op = opNode.getOperator();
	           		type = op.getResultType();
	           		result.append("("+subExpressions[0]+" "+op.getSymbol()+" "+subExpressions[1]+")");
	           		if (type.equals(SignatureKind.BOOL)) {
	           			result.append(" && ");
	           		} else {
	           			result.insert(0, "(");
	           			result.append("== "+value+") && ");
	           		}
				}
			}
   		}
	}
	
	/**
	 * Parses a Boolean expression.
	 * @param rule The rule in which the expression is found.
	 * @param pattern The graph in which the expression is found.
	 * @param variableResult The VariableNode which is the result of the expression.
	 * @param varMap The LocationVariable mapping.
	 * @param result The resulting expression.
	 */
	private void parseBooleanExpression(Rule rule, RuleGraph pattern, VariableNode variableResult, Map<VariableNode, Variable> varMap, StringBuffer result) {
		for (RuleEdge e : pattern.inEdgeSet(variableResult)) {
			if (e.getType() == null) {
				StringBuffer expr = new StringBuffer();
				parseExpression(rule, pattern, e.source(), varMap, expr);
				result.append(varMap.get(variableResult).getLabel()+" "+getOperator(e.label().text())+" "+expr+" && ");
			}
		}
	}
	
	/**
	 * Gets the correct operator for the switch relation guard/update syntax.
	 * @param operator The edge label.
	 * @return The correct operator.
	 */
	private String getOperator(String operator) {
		if (operator == "=")
			return "==";
		else
			return operator;				
	}
	
	/**
	 * Checks whether the given HostNode is considered 'final', meaning it's connected data values do not change.
	 * @param graph The HostGraph to which the node belongs.
	 * @param node The HostNode to check.
	 * @return Whether node is final or not.
	 */
	private boolean isFinal(HostGraph graph, HostNode node) {
		for (HostEdge e : graph.edgeSet(node)) {
			if (e.getRole().equals(EdgeRole.FLAG) && e.label().text().equals("final"))
				return true;
		}
		return false;
	}
}

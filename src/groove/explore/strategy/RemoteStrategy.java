/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id$
 */
package groove.explore.strategy;

import groove.algebra.Operator;
import groove.algebra.SignatureKind;
import groove.graph.AbstractEdge;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.TypeLabel;
import groove.graph.TypeNode;
import groove.graph.algebra.OperatorNode;
import groove.graph.algebra.ValueNode;
import groove.graph.algebra.VariableNode;
import groove.graph.iso.IsoChecker;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.MatchResult;
import groove.lts.MatchResultSet;
import groove.lts.RuleTransition;
import groove.rel.LabelVar;
import groove.trans.AnchorKey;
import groove.trans.Condition;
import groove.trans.DefaultHostGraph;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Sends the explored statespace (using the DFS Strategy) to a remote server.
 * TODO: In a later version, the exploration strategy will be obtained from the remote server.
 *
 * JSON format:
 * {start: "label start location", lVars: {<location variable>}, relations: [<switch relation>], gates: {<gate>}, iVars: {<interaction variable>}}
 * <location variable> = "label": {type: "variable type", init: initial value}
 * <switch relation> = {source: "label source location", gate: "label gate", target: "label target location", guard: "guard", update: "update mapping"}
 * <gate> = "label": {type: "?/!", iVars: ["label interaction variable"]}
 * <interaction variable> = "label": "variable type"
 * interaction variable label is null for tau transition.
 *
 * @author Vincent de Bruijn
 * 
 */
public class RemoteStrategy extends AbstractStrategy {

	@Override
	public void prepare(GTS gts, GraphState startState) {
		super.prepare(gts, startState);
		
		doTests(startState.getGraph());
		
		// Initiate the Depth-First strategy
		this.dfsStrategy = new DFSStrategy();
		this.dfsStrategy.prepare(gts, startState);
		
		if (!useDFS)
			connect();
		this.sts = new STS();
		this.sts.hostGraphToStartLocation(startState.getGraph());
	}
	
	@Override
	public boolean next() {
		if (getState() == null) {
			return false;
	    }
		// If the current location is new, determine its outgoing switch relations
		Location current = this.sts.getCurrentLocation();
		// Get current rule matches
		for (MatchResult next : createMatchCollector().getMatchSet()) {
			SwitchRelation sr = this.sts.ruleMatchToSwitchRelation(next);
			if (current.getRelationTarget(sr) == null) {
	        	RuleTransition transition = getMatchApplier().apply(getState(), next);
	            Location l = this.sts.hostGraphToLocation(transition.target().getGraph());
				current.addSwitchRelation(sr, l);
			}
        }
        return updateAtState();
    }

	/**
	 * Set the remote host.
	 * @param host The remote host
	 */
	public void setHost(String host) {
		this.host = host;
	}
	
	public void setUseDFS(boolean use) {
		this.useDFS = use;
	}

	@Override
	protected GraphState getNextState() {
		Location current = this.sts.getCurrentLocation();
		GraphState state = null;
		if (useDFS) {
			// Use the DfsStrategy to decide on the next state.
			state = dfsStrategy.getNextState();
			if (state == null) {
				connect();
				send(this.sts.toJSON());
				disconnect();
			} else {
				this.sts.toLocation(this.sts.hostGraphToLocation(state.getGraph()));
			}
			return state;
		} else {
			// Send the possible switch relations from the current location to the remote server.
			Set<SwitchRelation> relations = current.getSwitchRelations();
			send(createMessage(relations));
			
			// Get the response with the choice from the remote server.
			InstantiatedSwitchRelation isr = receive();
			if (isr == null) {
				disconnect();
			} else {
				this.sts.toLocation(current.getRelationTarget(isr.getSwitchRelation()));
				state = isr.getTransition().target();
			}
		}
		return state;
	}
	
	private String createMessage(Set<SwitchRelation> relations) {
		String message = "[";
		for (SwitchRelation r : relations ) {
			//message += r.toJSON()+",";
		}
		message = message.substring(0, message.length()-1)+"]";
 		return message;
	}

  

	// Connect to the remote server
	private void connect() {
		System.out.println("Connecting...");
		try {
			// Create a URLConnection object for a URL
			URL url = new URL(host);
			conn = (HttpURLConnection)url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setReadTimeout(10000);
			conn.setRequestProperty("Content-Type","application/json");
	
			//conn.connect();
			
			/*BufferedReader error = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			StringBuffer buf = new StringBuffer();
			String line;
			while ((line = error.readLine()) != null) {
				buf.append(line);
			}
			
			if (buf.length() > 0) {
				System.out.println("Error in connection to: "+url);
				System.out.println(buf);
			} else {
				this.out = new OutputStreamWriter(conn.getOutputStream());
				this.in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			}*/
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//this.out = new OutputStreamWriter(System.out);
		//this.in = new BufferedReader(new InputStreamReader(System.in));
	}
	
	// Disconnect from the remote server.
	private void disconnect() {
		System.out.println("Disconnecting...");
		try {
			in.close();
			out.close();
			conn.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Sends a JSON message to the remote server
	// @param message A JSON formatted message
	private void send(String message) {
		System.out.println("Sending JSON message...");
		System.out.println(message);
		try {
			if (out == null) {
				this.out = new OutputStreamWriter(conn.getOutputStream());
			}
			out.write(message);
			out.flush();
			conn.connect();
			if (conn.getResponseCode() != 200) {
				this.in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
				System.out.println("Error in connection to: "+conn.getURL());
			} else if (in == null) {
				this.in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			}
			StringBuffer buf = new StringBuffer();
			String line;
			while ((line = in.readLine()) != null) {
				buf.append(line);
			}
			System.out.println(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Get the response from the remote server.
	private InstantiatedSwitchRelation receive() {
		try {
			String decodedString;
			System.out.println("Server response:");
	
			while ((decodedString = in.readLine()) != null) {
				System.out.println(decodedString);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private DFSStrategy dfsStrategy;
	private String host;
	private STS sts;
	private HttpURLConnection conn;
	private Writer out;
	private BufferedReader in;
	private boolean useDFS = true;
	private IsoChecker<HostNode, HostEdge> isoChecker = IsoChecker.getInstance(true);
	
	/*
	 * A Symbolic Transition System.
	 * This contains the sts representing the GTS explored so far.
	 */
	private class STS {
		
		private Location start;
		private Location current;
		private Map<GeneralizedGraph, Location> locationMap;
		private Map<Object, SwitchRelation> switchRelationMap;
		private Set<Gate> gates;
		// not injective
		private Map<Pair<Integer, TypeLabel>, LocationVariable> locationVariables;
		// injective
		private Map<VariableNode, InteractionVariable> interactionVariables;
		
		public STS() {
			this.locationMap = new HashMap<GeneralizedGraph, Location>();
			this.switchRelationMap = new HashMap<Object, SwitchRelation>();
			this.locationVariables = new HashMap<Pair<Integer, TypeLabel>, LocationVariable>();
			this.gates = new HashSet<Gate>();
			this.interactionVariables = new HashMap<VariableNode, InteractionVariable>();
		}
		
		public void setStartLocation(Location start) {
			this.start = start;
			toLocation(start);
		}
		
		public Location getCurrentLocation() {
			return current;
		}
		
		public void toLocation(Location l) {
			current = l;
		}
		
		public SwitchRelation getSwitchRelation(Object obj) {
			return switchRelationMap.get(obj);
		}
		
		public Location getLocation(HostGraph graph) {
			return locationMap.get(graph);
		}
		
		public LocationVariable getLocationVariable(HostEdge edge) {
			return this.locationVariables.get(new Pair<Integer, TypeLabel>(edge.source().getNumber(), edge.label()));
		}
		
		public InteractionVariable getInteractionVariable(VariableNode node) {
			return this.interactionVariables.get(node);
		}
		
		// @param edge must be a HostEdge pointing to a ValueNode.
		public LocationVariable addLocationVariable(HostEdge edge, Object init) {
			ValueNode node = (ValueNode)edge.target();
			String label = createLocationVariableLabel(edge);
			LocationVariable v = new LocationVariable(label, node.getSignature(), init);
			this.locationVariables.put(new Pair<Integer, TypeLabel>(edge.source().getNumber(), edge.label()), v);
			return v;
		}
		
		public InteractionVariable addInteractionVariable(VariableNode node) {
			String label = createInteractionVariableLabel(node);
			InteractionVariable v = new InteractionVariable(label, node.getSignature());
			this.interactionVariables.put(node, v);
			return v;
		}
		
		public Gate addGate(String label, List<InteractionVariable> iVars) {
			Gate gate = new Gate(label, iVars);
			this.gates.add(gate);
			return gate;
		}
		
		public Object getDefaultValue(SignatureKind s) {
			switch(s) {
				case INT: return new Integer(0);
				case BOOL: return new Boolean(false);
				case REAL: return new Double(0.0);
				case STRING: return "";
				default: return null;
			}
		}
		
		public String createInteractionVariableLabel(VariableNode node) {
			return node.toString();
		}
		
		public String createLocationVariableLabel(HostEdge edge) {
			return edge.label().text()+"_"+edge.source().getNumber();
		}
		
		public Location hostGraphToLocation(HostGraph graph) {
			GeneralizedGraph locationGraph = generalize(graph);
			Location location = locationMap.get(locationGraph);
			if (location == null) {
				location = new RemoteStrategy.Location("s"+locationMap.size());
				locationMap.put(locationGraph, location);
			}
			return location;
		}
		
		public Location hostGraphToStartLocation(HostGraph graph) {
			Location location = hostGraphToLocation(graph);
			setStartLocation(location);
			initializeLocationVariables(graph);
			return location;
		}
		
		public SwitchRelation ruleMatchToSwitchRelation(MatchResult match) {			
			//SwitchRelation switchRelation = switchRelationMapping.get(match);
			//if (switchRelation == null) {

				RuleEvent event = match.getEvent();
				Rule rule = event.getRule();
				RuleToHostMap ruleMap = event.getMatch(getState().getGraph()).getPatternMap();
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
						InteractionVariable iVar = addInteractionVariable(v);
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
						if (var == null) {
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
					parseAlgebraicExpression(lhs, v, varMap, result);
					if (result.length() != 0) {
						guard+=createInteractionVariableLabel(v)+" == "+result+" and ";
					}
					result = new StringBuffer();
					parseBooleanExpression(lhs, v, varMap, result);
					if (result.length() != 0) {
						guard+=result+" and ";
					}
				}
				for (VariableNode v : varMap.keySet()) {
					if (!iVarNodes.contains(v)) {
						StringBuffer result = new StringBuffer();
						parseAlgebraicExpression(lhs, v, varMap, result);
						if (result.length() != 0) {
							guard+=varMap.get(v).getLabel()+" == "+result+" and ";
						}
						result = new StringBuffer();
						parseBooleanExpression(lhs, v, varMap, result);
						if (result.length() != 0) {
							guard+=result+" and ";
						}
					}
				}
				if (guard.length() > 5)
					guard = guard.substring(0, guard.length()-5);
				
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

				Map<RuleEdge, RuleEdge> locationVarUpdateMap = new HashMap<RuleEdge, RuleEdge>();
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
						SignatureKind resultType = parseExpression(nac.getPattern(), node, varMap, updateValue);
						if (updateValue.length() == 0) {
							// Update can't be empty. This should never happen.
							System.out.println("ERROR: Update of "+var.toString()+" in rule "+rule.getFullName()+" is empty where it shouldn't be.");
						}
						if (resultType != ((VariableNode)eraserEdge.target()).getSignature()) {
							// The result type of the expression should be the same as the type of the variable. This should never happen.
							System.out.println("ERROR: The result type of the expression "+updateValue+" ("+resultType+") is not the same as the type of the location variable "+var.getLabel()+" ("+((VariableNode)eraserEdge.target()).getSignature()+") in rule "+name);
						}
						locationVarUpdateMap.put(eraserEdge, creatorEdge);
						update += var.getLabel()+" = "+updateValue+"; ";
					}
				}
				if (update.length() > 2)
					update = update.substring(0, update.length()-2);
				
				// Create the gate and the switch relation
				Gate gate = addGate(name, iVars);
				Object obj = getSwitchIdentifier(gate,guard,update);
				SwitchRelation switchRelation = switchRelationMap.get(obj);
				if (switchRelation == null) {
					switchRelation = new SwitchRelation(gate, guard, update, locationVarUpdateMap);
					switchRelationMap.put(obj, switchRelation);
				}
			//}
			return switchRelation;
		}
		
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
		
		private Object getSwitchIdentifier(Gate gate, String guard, String update) {
			//TODO: replace with triple
			return gate.getLabel()+guard+update;
		}
		
		private GeneralizedGraph generalize(HostGraph graph) {
			GeneralizedGraph generalizedGraph = new GeneralizedGraph(graph);
			HostFactory factory = generalizedGraph.getFactory();
			List<HostEdge> toRemove = new ArrayList<HostEdge>();
			for (HostEdge edge : generalizedGraph.edgeSet()) {
				HostNode node = edge.target();
				if (node.getType().isDataType()) {
					toRemove.add(edge);
				}
			}
			for (HostEdge edge : toRemove) {
				ValueNode valueNode = (ValueNode)edge.target();
				Object newValue = null;
				newValue = getDefaultValue(valueNode.getSignature());
				
				ValueNode newNode = factory.createValueNode(valueNode.getNumber(), valueNode.getAlgebra(), newValue);
				generalizedGraph.addNode(newNode);
				generalizedGraph.removeEdge(edge);
				generalizedGraph.addEdge(factory.createEdge(edge.source(), edge.label(), newNode));
			}
			return generalizedGraph;
		}
		
		private void initializeLocationVariables(HostGraph graph) {
			for (HostEdge edge : graph.edgeSet()) {
				HostNode node = edge.target();
				if (node.getType().isDataType()) {
					ValueNode valueNode = (ValueNode)node;
					addLocationVariable(edge, valueNode.getValue());
				}
			}
		}
		
		private SignatureKind parseExpression(RuleGraph pattern, Node resultValue, Map<VariableNode, Variable> varMap, StringBuffer result) {
			VariableNode variableResult = (VariableNode)resultValue;
			// Check if the expression is a primitive value
			String symbol = variableResult.getSymbol();
			if (symbol != null) {
				result.append(symbol);
				return variableResult.getSignature();
			}
			// Check if the expression is a known interaction variable
			String iLabel = createInteractionVariableLabel(variableResult);
			InteractionVariable iVar = getInteractionVariable(variableResult);
			if (iVar != null) {
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
			SignatureKind type = parseAlgebraicExpression(pattern, variableResult, varMap, result);
			
			return type;
		}
	
		private SignatureKind parseAlgebraicExpression(RuleGraph pattern, VariableNode variableResult, Map<VariableNode, Variable> varMap, StringBuffer result) {
			SignatureKind type = null;
			for (RuleEdge e : pattern.inEdgeSet(variableResult)) {
				if (e.source() instanceof OperatorNode) {
		           	OperatorNode opNode = (OperatorNode)e.source();
		           	if (opNode.getTarget().equals(variableResult)) {
		           		List<VariableNode> arguments = opNode.getArguments();
		           		String[] subExpressions = new String[arguments.size()];
		           		for (int i = 0; i < arguments.size(); i++) {
		           			StringBuffer newResult = new StringBuffer();
		           			parseExpression(pattern, arguments.get(i), varMap, newResult);
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
		
		private void parseBooleanExpression(RuleGraph pattern, VariableNode variableResult, Map<VariableNode, Variable> varMap, StringBuffer result) {
			for (RuleEdge e : pattern.inEdgeSet(variableResult)) {
				if (e.getType() == null) {
					StringBuffer expr = new StringBuffer();
					parseExpression(pattern, e.source(), varMap, expr);
					result.append(varMap.get(variableResult).getLabel()+" "+e.label().text()+" "+expr);
				}
			}
		}
	}

	/*
	 * A location in the STS.
	 */
	private class Location {
		
		private String label;
		private Map<SwitchRelation, Location> relations;
		
		public Location(String label) {
			this.label = label;
			this.relations = new HashMap<SwitchRelation, Location>();
		}
		
		public Set<SwitchRelation> getSwitchRelations() {
			return relations.keySet();
		}
		
		public Location getRelationTarget(SwitchRelation sr) {
			return relations.get(sr);
		}
		
		public void addSwitchRelation(SwitchRelation sr, Location l) {
			relations.put(sr, l);
		}
		
		public String getLabel() {
			return label;
		}
		
		public boolean equals(Object o) {
			if (! (o instanceof Location))
				return false;
			return this.label == ((Location)o).getLabel();
		}
		
		public int hashCode() {
			return getLabel().hashCode();
		}
		
		public String toJSON() {
			return "\""+this.label+"\"";
		}
	
	}
	
	/*
	 * A switch relation in the STS.
	 */
	private class SwitchRelation {
		
		private Gate gate;
		private String guard;
		private String update;
		private Map<RuleEdge, RuleEdge> locationVarUpdateMap;
		
		public SwitchRelation(Gate gate, String guard, String update, Map<RuleEdge, RuleEdge> locationVarUpdateMap) {
			this.gate = gate;
			this.guard = guard;
			this.update = update;
			this.locationVarUpdateMap = locationVarUpdateMap;
		}
		
		public Gate getGate() {
			return this.gate;
		}
		
		public String getGuard() {
			return this.guard;
		}
		
		public String getUpdate() {
			return this.update;
		}
		
		public RuleEdge getUpdateOf(RuleEdge edge) {
			return this.locationVarUpdateMap.get(edge);
		}
		
		public Set<RuleEdge> getUpdates() {
			return this.locationVarUpdateMap.keySet();
		}
		
		public boolean equals(Object o) {
			if (!(o instanceof SwitchRelation))
				return false;
			SwitchRelation other = (SwitchRelation)o;
			return other.getGate() == getGate() && other.getGuard() == getGuard() && other.getUpdate() == getUpdate();
		}
		
		public int hashCode() {
			final int prime = 31;
            int result = 1;
            result =
                prime * result + getGate().hashCode();
            result =
                prime * result + getGuard().hashCode();
            result =
                prime * result + getUpdate().hashCode();
            return result;
		}
		
		public String toJSON(Location source, Location target) {
			return "{\"source\":"+source.toJSON()+",\"gate\":\""+gate.getLabel()+"\",\"target\":"+target.toJSON()+",\"guard\":\""+guard+"\",\"update\":\""+update+"\"}";
		}
		
	}
	
	private class Gate {
		
		private String label;
		private List<InteractionVariable> iVars;
		
		public Gate(String label, List<InteractionVariable> iVars) {
			this.label = label;
			this.iVars = iVars;
		}
		
		public String getLabel() {
			return this.label;
		}
		
		public boolean equals(Object o) {
			if (!(o instanceof Gate))
				return false;
			Gate other = (Gate)o;
			return other.getLabel() == getLabel();
		}
		
		public int hashCode() {
			return getLabel().hashCode();
		}
		
		public String toJSON() {
			String type = "!";
			if (label.contains("?"))
				type = "?";
			String json = "\""+getLabel()+"\":{\"type\":\""+type+"\",\"iVars\":[";
			for (Variable v : iVars) {
				json+="\""+v.getLabel()+"\",";
			}
			return json.substring(0, json.length()-1)+"]}";
		}
		
	}
	
	private class Variable {
		
		protected String label;
		protected SignatureKind type;
		
		public Variable(String label, SignatureKind type) {
			this.label = label;
			this.type = type;
		}
		
		public String getLabel() {
			return this.label;
		}
		
		public SignatureKind getType() {
			return this.type;
		}
		
		public boolean equals(Object o) {
			if (!(o instanceof Variable))
				return false;
			Variable other = (Variable)o;
			return other.getLabel() == getLabel();
		}
		
		public int hashCode() {
			return getLabel().hashCode();
		}
	}
	
	private class InteractionVariable extends Variable {
		
		public InteractionVariable(String label, SignatureKind type) {
			super(label, type);
		}
		
		public String toJSON() {
			return "\""+getLabel()+"\":\""+type+"\"";
		}
		
	}
	
	private class LocationVariable extends Variable {
		
		private Object initialValue;

		public LocationVariable(String identifier, SignatureKind type, Object initialValue) {
			super(identifier, type);
			this.initialValue = initialValue;
		}
		
		public Object getInitialValue() {
			return this.initialValue;
		}
		
		public String toJSON() {
			return "\""+getLabel()+"\":{\"type\":\""+getType()+"\",\"init\":"+getInitialValue().toString()+"}";
		}
		
	}
	
	private class InstantiatedSwitchRelation {
		
		private SwitchRelation relation;
		private RuleTransition transition;
		
		public InstantiatedSwitchRelation(SwitchRelation relation, RuleTransition transition) {
			this.relation = relation;
			this.transition = transition;
		}
		
		public SwitchRelation getSwitchRelation() {
			return this.relation;
		}
		
		public GraphTransition getTransition() {
			return this.transition;
		}
	}
	
	private class GeneralizedGraph extends DefaultHostGraph {
		
		public GeneralizedGraph(HostGraph graph) {
			super(graph);
		}
		
		@Override
		public boolean equals(Object o) {
			HostGraph graph = (HostGraph)o;
			return isoChecker.areIsomorphic(this, graph);
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
            int result = 1;
            result =
                prime * result + nodeCount();
            result =
                prime * result
                		+ edgeCount();
            return result;
		}
	}
	
	//TESTS
	
	public void doTests(HostGraph graph) {
		System.out.println("START TESTS");
		generalizeTest(graph);
		System.out.println("");
	}
	
	public void generalizeTest(HostGraph graph) {
		System.out.println("Started test case generalizeTest");
		STS s = new STS();
		Location l1 = s.hostGraphToLocation(graph);
		Location l2 = s.hostGraphToLocation(graph);
		boolean result = l1.equals(l2);
		System.out.println(result);
		assert result;
	}
	/*
	// Transforms the gts to a simple sts.
	  // Formats each state in the gts to a location, each transition to a switch relation and each parameter on the transitions to an interaction variable.
	  // @return: a JSON formatted string
		private String format_to_simple_sts(GTS gts) {
			String message = "[\"s0\",";
			
			// For each state
			Iterator<? extends GraphState> nodesIter = gts.nodeSet().iterator();
			while(nodesIter.hasNext()) {
				GraphState node = nodesIter.next();
				Set<String> noParamEdges = new HashSet<String>(); // if a label has no parameter edges, future edges with that label will also not have parameters
				
				// For each outgoing edge
				Iterator<RuleTransition> edgeIter = node.getTransitionIter();
				while(edgeIter.hasNext()) {
					GraphTransition edge = edgeIter.next();
					String label = edge.label().text();
					
					// Construct the label type
					String type = null;
					if (label.contains("?"))
						type = "stimulus";
					else
						type = "response";
					
					// Remove possible empty parameter
					boolean notEmpty = true;
					if(label.endsWith("()")) {
						label = label.substring(0,label.length()-2);
						notEmpty = false;
					}
					
					// Remove all '!' and '?' from the label
					label = label.replace("!","").replace("?","");
					
					String guard = "null";
					String iVars = "[]";
					if(notEmpty && !noParamEdges.contains(label)) {
						// Find if the label has parameters
						Pattern pattern = Pattern.compile("\\(.+\\)$");
						Matcher matcher = pattern.matcher(label);
						if(matcher.find()) {
							String paramString = label.substring(matcher.start());
							label = label.substring(0, matcher.start());
						
							// Parse the parameters
							Object[] params = parseParams(paramString);
							
							// Construct the interaction variable declaration
							// TODO: memoize this
							iVars = "[";
							String[] iVarNames = new String[params.length];
							for (int i = 0; i < params.length; i++) {
								iVarNames[i] = label+"_i"+(i+1);
								iVars+="[\""+iVarNames[i]+"\",\""+getType(params[i])+"\"],";
							}
							iVars = iVars.substring(0, iVars.length()-1)+"]";
							
							// Construct the guard
							List<Object[]> l = new ArrayList<Object[]>();
							l.add(params);
							guard = constructGuard(l, iVarNames);
						} else {
							// We do not have to check future edges with this label
							noParamEdges.add(label);
						}
					}
					// Construct the switch relation
					message += "[\"s"+node.getNumber()+"\",[\""+label+"\",\""+type+"\","+iVars+"],\"s"+edge.target().getNumber()+"\","+guard+",null],";
				}
			}
			message = message.substring(0, message.length()-1)+"]";
			return message;
		}

		// Parses a "(x, y, ...)" string to an array of Objects representing x, y, etc.
		private Object[] parseParams(String paramString) {
			String[] split = paramString.substring(1,paramString.length()-1).split(",");
			Object[] params = new Object[split.length];
			for(int i = 0; i < split.length; i++) {
				params[i] = parseValue(split[i]);
			}
			return params;
		}

		// Parses a string with an unknown data value to an Object representing that data value
		private Object parseValue(String valueString) {
			// integer
			Pattern pattern = Pattern.compile("^\\d+$");
			Matcher matcher = pattern.matcher(valueString);
			if (matcher.find()) {
				try {
					return Integer.parseInt(valueString);
				} catch(NumberFormatException e) {
					// This should not happen

				}
			}
			// double
			pattern = Pattern.compile("^\\d+\\.\\d+$");
			matcher = pattern.matcher(valueString);
			if (matcher.find()) {
				try {
					return Double.parseDouble(valueString);
				} catch(NumberFormatException e) {
					// This should not happen

				}
			}
			// string
			return valueString;
		}

		// Construct the guard based on all possible interaction variable valuations
		private String constructGuard(List<Object[]> params, String[] names) {
			String guard = "\"";
			for(int i = 0; i < params.size(); i++) {
				Object[] o = params.get(i);
				guard+="("+names[0]+" == "+o[0];
				for(int j = 1; j < o.length; j++) {
					guard+=" && "+names[j]+" == "+o[j].toString();
				}
				guard+=") ||";
			}
			guard = guard.substring(0, guard.length()-3)+"\"";
			return guard;
		}
		
		// Determines the type of this object and returns a string representation of the type
		private String getType(Object o) {
			String type = o.getClass().toString();
			int index = type.lastIndexOf(".");
			if (index != -1) {
				return type.substring(index+1, type.length()).toLowerCase();
			} else {
				return type.toLowerCase();
			}
		}*/
}

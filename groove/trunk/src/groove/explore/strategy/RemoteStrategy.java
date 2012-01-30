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

import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.MatchResult;
import groove.lts.MatchResultSet;
import groove.lts.RuleTransition;
import groove.trans.Condition;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.Rule;
import groove.trans.RuleGraph;

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
 * [<label start state>:String, <switch relation 1>:Array, <switch relation 2>:Array, ...]
 * <switch relation>: [<label source state>:String, <gate>:Array, <label target state>:String, <guard>:String, <update mapping>:String]
 * <gate>: [<label>:String, <stimulus/response>:String, [<interaction variable 1>:Array, <interaction variable 2>:Array, ...]
 * <interaction variable>: [<interaction variable identifier>:String, <int. variable type>:String]
 * label is null for tau transition
 *
 * @author Vincent de Bruijn
 * 
 */
public class RemoteStrategy extends AbstractStrategy {

	@Override
	public void prepare(GTS gts, GraphState startState) {
		super.prepare(gts, startState);
		
		// Initiate the Depth-First strategy
		this.dfsStrategy = new DFSStrategy();
		this.dfsStrategy.prepare(gts, startState);
		
		//connect();
		this.sts = new STS();
		this.sts.toLocation(this.sts.hostGraphToLocation(startState.getGraph()));
	}
	
	@Override
	public boolean next() {
		if (getState() == null) {
			return false;
	    }
		
		// If the location is new, determine its outgoing switch relations
		Location current = this.sts.getCurrentLocation();
		if (!current.explored()) {
			// Get current rule matches
			Set<Rule> rules = new HashSet<Rule>();
			for (MatchResult next : createMatchCollector().getMatchSet()) {
            	RuleTransition transition = getMatchApplier().apply(getState(), next);
				Rule rule = next.getEvent().getRule();
				if (!rules.contains(rule)) {
					SwitchRelation sr = this.sts.ruleToSwitchRelation(rule);
	            	Location l = this.sts.hostGraphToLocation(transition.target().getGraph());
					current.addSwitchRelation(sr, l);
					rules.add(rule);
				}
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
		GraphState result = getRemoteStrategy();
		if (result == null) {
			//send(format_to_simple_sts(getGTS()));
			send(this.sts.toJSON());
			disconnect();
		}
		return null;
		//return result;
	}
	
	// Sends the possible rule matches to the remote server.
	// Wait for rule choice. Apply the chosen rule.
	private GraphState getRemoteStrategy() {
		Location current = this.sts.getCurrentLocation();
		
		if (useDFS) {
			// Use the DfsStrategy to decide on the next state.
			GraphState state = dfsStrategy.getNextState();
			this.sts.toLocation(this.sts.getLocation(state.getGraph()));
			return state;
		} else {
			// Send the possible switch relations from the current location to the remote server.
			Set<SwitchRelation> relations = current.getSwitchRelations();
			send(createMessage(relations));
			
			// Get the response with the choice from the remote server.
			InstantiatedSwitchRelation isr = receive();
			this.sts.toLocation(current.getRelationTarget(isr.getSwitchRelation()));
			
			return isr.getTransition().target();
		}
	}
	
	private String createMessage(Set<SwitchRelation> relations) {
		String message = "[";
		for (SwitchRelation r : relations ) {
			message += r.toJSON()+",";
		}
		message = message.substring(0, message.length()-1)+"]";
 		return message;
	}

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
	}

	// Connect to the remote server
	private void connect() {
		try {
			// Create a URLConnection object for a URL
			URL url = new URL(host);
			this.conn = (HttpURLConnection)url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setReadTimeout(10000);
			conn.setRequestProperty("Content-Type","application/json");
	
			conn.connect();
	
			this.out = new OutputStreamWriter(conn.getOutputStream());
			this.in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Disconnect from the remote server.
	private void disconnect() {
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
		try {
			out.write(message);
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
	
	/*
	 * A Symbolic Transition System.
	 * This contains the sts representing the GTS explored so far.
	 */
	private class STS {

		private Location current;
		private Map<HostGraph, Location> locationMapping;
		private Map<Rule, SwitchRelation> switchRelationMapping;
		
		public STS() {
			this.locationMapping = new HashMap<HostGraph, Location>();
			this.switchRelationMapping = new HashMap<Rule, SwitchRelation>();
		}
		
		public Location getCurrentLocation() {
			return current;
		}
		
		public void toLocation(Location l) {
			current = l;
		}
		
		public SwitchRelation getSwitchRelation(Rule rule) {
			return switchRelationMapping.get(rule);
		}
		
		public Location getLocation(HostGraph graph) {
			return locationMapping.get(graph);
		}
		
		public Location hostGraphToLocation(HostGraph graph) {
			HostGraph locationGraph = generalize(graph);
			Location location = locationMapping.get(locationGraph);
			if (location == null) {
				location = new RemoteStrategy.Location("s"+locationMapping.size());
				locationMapping.put(locationGraph, location);
			}
			return location;
		}
		
		public SwitchRelation ruleToSwitchRelation(Rule rule) {
			SwitchRelation switchRelation = switchRelationMapping.get(rule);
			if (switchRelation == null) {
				switchRelation = new RemoteStrategy.SwitchRelation(rule);
				switchRelationMapping.put(rule, switchRelation);
			}
			return switchRelation;
		}
		
		public String toJSON() {
			return "";
		}
		
		private HostGraph generalize(HostGraph graph) {
			HostGraph generalizedGraph = graph.clone();
			System.out.println("nodes:");
			for (HostNode node : generalizedGraph.nodeSet()) {
				System.out.println(node.toString());
			}
			System.out.println("edges:");
			for (HostEdge edge : generalizedGraph.edgeSet()) {
				System.out.println(edge.toString());
			}
			return generalizedGraph;
		}
	}
	
	/*
	 * A location in the STS.
	 */
	private class Location {
		
		private String identifier;
		private Map<SwitchRelation, Location> relations;
		
		public Location(String identifier) {
			this.identifier = identifier;
			relations = new HashMap<SwitchRelation, Location>();
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
		
		public boolean explored() {
			return !this.relations.isEmpty();
		}
		
		public String toString() {
			return identifier;
		}
	
	}
	
	/*
	 * A switch relation in the STS.
	 */
	private class SwitchRelation {
		
		private String gate;
		private String guard;
		private String update;
		
		public SwitchRelation(String gate, String guard, String update) {
			this.gate = gate;
			this.guard = guard;
			this.update = update;
		}
		
		public SwitchRelation(Rule rule) {
			this.gate = rule.getFullName();
			RuleGraph lhs = rule.lhs();
			RuleGraph rhs = rule.rhs();
			Condition nac = rule.getCondition();
			System.out.println(lhs.toString());
			System.out.println(rhs.toString());
			System.out.println(nac.toString());
		}
		
		public String toJSON() {
			return "["+guard+","+update+"]";
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
}

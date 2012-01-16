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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
	}

	public void setHost(String host) {
		this.host = host;
	}

	@Override
	protected GraphState getNextState() {
		GraphState result = dfsStrategy.getNextState();
		if (result == null) {
			send(format_to_simple_sts(getGTS()));
		}
		return result;
	}

	private String format_to_simple_sts(GTS gts) {
		String message = "[";
		
		// For each state
		Iterator<? extends GraphState> nodesIter = gts.nodeSet().iterator();
		while(nodesIter.hasNext()) {
			GraphState node = nodesIter.next();
			Set<String> noParamEdges = new HashSet<String>(); // if a label has no parameter edges, future edges with that label will also not have parameters
			
			// For each outgoing edge
			Iterator<GraphTransition> edgeIter = node.getTransitionIter();
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

	private String format_to_sts(GTS gts) {
		String message = "[";
		// For each state
		Iterator<? extends GraphState> nodesIter = gts.nodeSet().iterator();
		while(nodesIter.hasNext()) {
			GraphState node = nodesIter.next();
			Map<String, List<Object[]>> valueMap = new HashMap<String, List<Object[]>>(); // maps a label to a list of parameter values
			Set<String> noParamEdges = new HashSet<String>(); // if a label has no parameter edges, future edges with that label will also not have parameters
			// For each outgoing edge
			Iterator<GraphTransition> edgeIter = node.getTransitionIter();
			while(edgeIter.hasNext()) {
				GraphTransition edge = edgeIter.next();
				String label = edge.label().text();
				if(!noParamEdges.contains(label)) {
					// Find if the label has parameters
					Pattern pattern = Pattern.compile("\\(.+\\)$");
					Matcher matcher = pattern.matcher(edge.label().text());
					if(matcher.find()) {
						String paramString = label.substring(matcher.start());
						label = label.substring(0, matcher.start());
						// Parse the parameters
						Object[] params = parseParams(paramString);
						// Map the label to these parameter values
						List<Object[]> values = valueMap.get(label);
						if(values == null) {
							values = new ArrayList<Object[]>();
							valueMap.put(label, values);
						}
						values.add(params);
					} else {
						// Remove possible empty parameter
						//if(label.endsWith("()"))
						//label = label.substring(0,label.length()-2);
						// We do not have to explore future edges with this label
						noParamEdges.add(label);
					}
				}
			}
			// For each label
			Iterator<String> ix = valueMap.keySet().iterator();
			while (ix.hasNext()) {
				String label = ix.next();
				List<Object[]> params = valueMap.get(label);
				// Construct the constraint (guard) by doing some smart partitioning
				//String guard = partitionParams(params);
				String type = null;
				if (label.contains("?"))
					type = "stimulus";
				else
					type = "response";
				// TODO: fix this problem
				//message += "[\"s"+node.getNumber()+"\",[\""+label+"\",\""+type+"\"],\"s"+edge.target().getNumber()+"\"],";
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

	// Sends a JSON message to the remote server
	private void send(String message) {
		// Connect to the remote server
		try {
			// Create a URLConnection object for a URL
			URL url = new URL(host);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setReadTimeout(10000);
			conn.setRequestProperty("Content-Type","application/json");

			System.out.println(message);

			conn.connect();

			OutputStreamWriter out = new OutputStreamWriter(
					conn.getOutputStream());
			out.write(message);
			out.close();

			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							conn.getInputStream()));

			String decodedString;
			System.out.println("Server response:");

			while ((decodedString = in.readLine()) != null) {
				System.out.println(decodedString);
			}
			in.close();

			conn.disconnect();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private DFSStrategy dfsStrategy;
	private String host;
}

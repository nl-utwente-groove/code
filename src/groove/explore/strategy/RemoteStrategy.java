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
import groove.lts.MatchResult;
import groove.lts.RuleTransition;
import groove.sts.InstantiatedSwitchRelation;
import groove.sts.Location;
import groove.sts.STS;
import groove.sts.SwitchRelation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;


/**
 * The exploration strategy will be obtained from a remote server or, if the DFSStrategy is used, it sends the explored statespace (using the DFS Strategy) to the remote server.
 * 
 * In the latter case the JSON format is:
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
			SwitchRelation sr = this.sts.ruleMatchToSwitchRelation(getState().getGraph(), next);
			if (current.getRelationTarget(sr) == null) {
	        	RuleTransition transition = getMatchApplier().apply(getState(), next);
	            Location l = this.sts.hostGraphToLocation(transition.target().getGraph());
				current.addSwitchRelation(sr, l);
			}
        }
        return updateAtState();
    }

	/**
	 * Sets the remote host.
	 * @param host The remote host
	 */
	public void setHost(String host) {
		this.host = host;
	}
	
	/**
	 * Sets the use of the DFSStrategy. If the strategy is used, this exploration strategy
	 * will first explore the statespace and transmit it to the remote server.
	 * If the strategy is not used, the exploration strategy is obtained from the remote
	 * server.
	 * @param use Whether the DFSStrategy should be used or not
	 */
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
	
	/**
	 * Creates the message to be sent to the remote server. Used when the
	 * DFSStrategy is not used.
	 * @param relations The possible switch relations from the current state.
	 * @return The message to be sent.
	 */
	private String createMessage(Set<SwitchRelation> relations) {
		String message = "[";
		for (SwitchRelation r : relations ) {
			//message += r.toJSON()+",";
		}
		message = message.substring(0, message.length()-1)+"]";
 		return message;
	}

	/** 
	 * Connects to the remote server.
	 */
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
	
	/** 
	 * Disconnects from the remote server. 
	 */
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

	/** 
	 * Sends a JSON message to the remote server.
	 * @param message A JSON formatted message
	 */
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
	
	/** 
	 * Get the response from the remote server. 
	 *  @return The response is an InstantiatedSwitchRelation, representing the rule choice of the remote server
	 */
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
	
}

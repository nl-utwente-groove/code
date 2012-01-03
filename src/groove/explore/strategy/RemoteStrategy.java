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
import java.util.Iterator;

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
          send(format(getGTS()));
        }
        return result;
    }
    
    private String format(GTS gts) {
        String message = "[";
        Iterator<? extends GraphState> nodesIter = gts.nodeSet().iterator();
        while(nodesIter.hasNext()) {
          GraphState node = nodesIter.next();
          Iterator<GraphTransition> edgeIter = node.getTransitionIter();
          while(edgeIter.hasNext()) {
            GraphTransition edge = edgeIter.next();
            message += "["+node.getCtrlState().getNumber()+",\""+edge.label().text()+"\","+edge.target().getCtrlState().getNumber()+"],";
          }
        }
        message = message.substring(0, message.length()-1)+"]";
        return message;
    }
    
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
          
          conn.connect();
          
          System.out.println(message);
          
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

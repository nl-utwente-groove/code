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
import groove.sts.CompleteSTS;
import groove.sts.InstantiatedSwitchRelation;
import groove.sts.Location;
import groove.sts.OnTheFlySTS;
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
 * The exploration strategy will be obtained from a remote server or, if the
 * SymbolicStrategy is used, it sends the sts obtained from the SymbolicStrategy
 * to the remote server.
 * @author Vincent de Bruijn
 */
public class RemoteStrategy extends SymbolicStrategy {

    private String host;
    private HttpURLConnection conn;
    private Writer out;
    private BufferedReader in;
    private boolean onTheFlyExploration;

    /**
     * Sets the remote host.
     * @param host The remote host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Sets on the fly model exploration to true. If this is called, the 
     * exploration strategy is obtained from the remote server. Otherwise, this
     * exploration strategy will first explore the statespace and transmit an
     * sts to the remote server.
     */
    public void useOnTheFlyExploration(boolean use) {
        this.onTheFlyExploration = use;
        if (use) {
            connect();
            setSTS(new OnTheFlySTS());
        } else {
            // Initiate the Breadth-First strategy
            ClosingStrategy strategy = new BFSStrategy();
            setStrategy(strategy);
            setSTS(new CompleteSTS());
        }
    }

    @Override
    public void prepare(GTS gts, GraphState startState) {
        super.prepare(gts, startState);
    }

    @Override
    protected GraphState getNextState() {
        if (this.onTheFlyExploration) {
            Location current = this.sts.getCurrentLocation();
            GraphState state = null;
            // Send the possible switch relations from the current location to
            // the remote server.
            Set<SwitchRelation> relations = current.getSwitchRelations();
            send(createMessage(relations));

            // Get the response with the choice from the remote server.
            InstantiatedSwitchRelation isr = receive();
            if (isr == null) {
                disconnect();
            } else {
                // cannot deal with being in multiple places at once yet.
                Location l =
                    current.getRelationTargets(isr.getSwitchRelation()).iterator().next();
                this.sts.toLocation(l);
                state = isr.getTransition().target();
            }
            return state;
        } else {
            GraphState state = null;
            // Use the strategy to decide on the next state.
            state = this.strategy.getNextState();
            if (state != null) {
                this.sts.toLocation(this.sts.hostGraphToLocation(state.getGraph()));
            } else {
                send(getSTS().toJSON());
            }
            return state;
        }
    }

    /**
     * Creates the message to be sent to the remote server. Used when the
     * model exploration is done on the fly.
     * 
     * @param relations
     *            The possible switch relations from the current state.
     * @return The message to be sent.
     */
    private String createMessage(Set<SwitchRelation> relations) {
        String message = "[";
        // for (SwitchRelation r : relations) {
        // message += r.toJSON()+",";
        // }
        message = message.substring(0, message.length() - 1) + "]";
        return message;
    }

    /**
     * Connects to the remote server.
     */
    private void connect() {
        System.out.println("Connecting...");
        try {
            // Create a URLConnection object for a URL
            URL url = new URL(this.host);
            this.conn = (HttpURLConnection) url.openConnection();
            this.conn.setDoOutput(true);
            this.conn.setRequestMethod("POST");
            this.conn.setReadTimeout(10000);
            this.conn.setRequestProperty("Content-Type", "application/json");

            // conn.connect();

            /*
             * BufferedReader error = new BufferedReader(new
             * InputStreamReader(conn.getErrorStream())); StringBuffer buf = new
             * StringBuffer(); String line; while ((line = error.readLine()) !=
             * null) { buf.append(line); }
             * 
             * if (buf.length() > 0) {
             * System.out.println("Error in connection to: "+url);
             * System.out.println(buf); } else { this.out = new
             * OutputStreamWriter(conn.getOutputStream()); this.in = new
             * BufferedReader(new InputStreamReader(conn.getInputStream())); }
             */
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // this.out = new OutputStreamWriter(System.out);
        // this.in = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * Disconnects from the remote server.
     */
    private void disconnect() {
        System.out.println("Disconnecting...");
        try {
            this.in.close();
            this.out.close();
            this.conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a JSON message to the remote server.
     * 
     * @param message
     *            A JSON formatted message
     */
    private void send(String message) {
        System.out.println("Sending JSON message...");
        System.out.println(message);
        try {
            if (this.conn == null || this.out == null) {
                connect();
                this.out = new OutputStreamWriter(this.conn.getOutputStream());
            }
            this.out.write(message);
            this.out.flush();
            if (this.conn.getResponseCode() != 200) {
                this.in =
                    new BufferedReader(new InputStreamReader(
                        this.conn.getErrorStream()));
                System.out.println("Error in connection to: "
                    + this.conn.getURL());
            } else if (this.in == null) {
                this.in =
                    new BufferedReader(new InputStreamReader(
                        this.conn.getInputStream()));
            }
            StringBuffer buf = new StringBuffer();
            String line;
            while ((line = this.in.readLine()) != null) {
                buf.append(line);
            }
            System.out.println(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the response from the remote server. Only used in on-the-fly model
     * generation.
     * 
     * @return The response is an InstantiatedSwitchRelation, representing the
     *         rule choice of the remote server
     */
    private InstantiatedSwitchRelation receive() {
        try {
            String decodedString;
            System.out.println("Server response:");

            while ((decodedString = this.in.readLine()) != null) {
                System.out.println(decodedString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

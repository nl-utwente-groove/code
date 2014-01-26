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

import groove.lts.GraphState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

    /**
     * Sets the remote host.
     * @param host The remote host
     */
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    protected GraphState computeNextState() {
        GraphState state = null;
        // Use the strategy to decide on the next state.
        state = this.strategy.computeNextState();
        if (state != null) {
            this.sts.toLocation(this.sts.hostGraphToLocation(state.getGraph()));
        } else {
            send(getSTS().toJSON());
        }
        return state;
    }

    /**
     * Connects to the remote server.
     */
    private void connect() throws MalformedURLException, IOException {
        System.out.println("Connecting...");
        // Create a URLConnection object for a URL
        URL url = new URL(this.host);
        this.conn = (HttpURLConnection) url.openConnection();
        this.conn.setDoOutput(true);
        this.conn.setRequestMethod("POST");
        this.conn.setReadTimeout(50000);
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
    }

    /**
     * Sends a JSON message to the remote server.
     * 
     * @param message A JSON formatted message
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
                this.in = new BufferedReader(new InputStreamReader(this.conn.getErrorStream()));
                System.out.println("Error in connection to: " + this.conn.getURL());
            } else if (this.in == null) {
                this.in = new BufferedReader(new InputStreamReader(this.conn.getInputStream()));
            }
            StringBuffer buf = new StringBuffer();
            String line;
            while ((line = this.in.readLine()) != null) {
                buf.append(line);
            }
            System.out.println(buf);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}

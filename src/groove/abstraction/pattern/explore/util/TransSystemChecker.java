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
package groove.abstraction.pattern.explore.util;

import groove.abstraction.pattern.lts.PGTS;
import groove.abstraction.pattern.lts.PatternState;
import groove.graph.DefaultEdge;
import groove.graph.DefaultGraph;
import groove.graph.DefaultNode;
import groove.graph.iso.IsoChecker;
import groove.lts.AbstractGraphState;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.StartGraphState;
import groove.trans.HostGraph;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Class that compares a simple graph transition system and a pattern graph
 * one to see if they are equivalent.
 * 
 * @author Eduardo Zambon
 */
public final class TransSystemChecker {

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /** Pattern graph transition system. */
    private final PGTS pgts;
    /** Simple graph transition system. */
    private final GTS sgts;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor. */
    public TransSystemChecker(PGTS pgts, GTS sgts) {
        this.pgts = pgts;
        this.sgts = sgts;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Prints the analysis to stout. */
    public void report() {
        PrintStream out = System.out;
        out.println(String.format("PGTS: states = %s / transitions = %s",
            this.pgts.getStateCount(), this.pgts.getTransitionCount()));
        out.println(String.format("SGTS: states = %s / transitions = %s",
            this.sgts.nodeCount(), this.sgts.getTransitionCount()));
        compare(out);
    }

    /** Returns true if the two transition systems are isomorphic. */
    public boolean compare() {
        OutputStream nullStream = new OutputStream() {
            @Override
            public void write(int b) {
                // Empty by design.
            }
        };
        return compare(new PrintStream(nullStream));
    }

    /**
     * Compares the two transition system and prints the result of the
     * comparison to the given stream.
     * First the graph structure of both systems is checked. If the system are
     * isomorphic then each of the states are compared for isomorphism as well.
     * 
     * @return true if the systems are equivalent, false otherwise.
     */
    private boolean compare(PrintStream out) {
        DefaultGraph plainSGTS =
            this.sgts.toPlainGraph(false, false, false, false);
        DefaultGraph plainPGTS = this.pgts.toPlainGraph();
        IsoChecker<DefaultNode,DefaultEdge> gtsChecker =
            IsoChecker.getInstance(true);
        if (gtsChecker.areIsomorphic(plainSGTS, plainPGTS)) {
            out.print("Transision systems are isomorphic. Checking states... ");
            // EZ says: for some reason that I can't recall right now the test
            // fails if we try to use the isomorphism map for the
            // transition systems, so don't change the code below to try to
            // do that.
            for (PatternState pState : this.pgts.nodeSet()) {
                HostGraph pGraph = pState.getGraph().flatten();
                AbstractGraphState newSState =
                    new StartGraphState(this.sgts.getRecord(), pGraph);
                // We need to copy the control state otherwise the IsoCheck fails.
                newSState.setCtrlState(pState.getCtrlState());
                GraphState oldSState = this.sgts.addState(newSState);
                if (oldSState == null) {
                    out.println("FAILED!");
                    return false;
                }
            }
            out.println("PASSED.");
            return true;
        } else {
            out.println("Transition systems are NOT isomorphic.");
            return false;
        }
    }
}

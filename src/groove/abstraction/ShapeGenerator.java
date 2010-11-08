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
package groove.abstraction;

import groove.abstraction.lts.AGTS;
import groove.abstraction.lts.ShapeStateGenerator;
import groove.explore.result.Acceptor;
import groove.explore.result.NoStateAcceptor;
import groove.explore.result.Result;
import groove.trans.GraphGrammar;
import groove.util.Groove;
import groove.view.FormatException;

import java.io.IOException;

/**
 * Performs a full abstract exploration of a grammar given as parameter.
 * 
 * @author Eduardo Zambon
 */
public final class ShapeGenerator {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    private GraphGrammar grammar;
    private AGTS gts;
    private ShapeStateGenerator sgen;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Basic constructor. */
    public ShapeGenerator() {
        Multiplicity.initMultStore();
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Loads a grammar from a given grammar location and a start graph. */
    private void loadGrammar(String grammarFile, String startGraph) {
        try {
            this.grammar =
                Groove.loadGrammar(grammarFile, startGraph).toGrammar();
        } catch (FormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exploreGrammar(boolean fromMain) {
        this.gts = new AGTS(this.grammar);

        ShapeBFSStrategy strategy = new ShapeBFSStrategy();
        Acceptor acceptor = new NoStateAcceptor();
        acceptor.setResult(new Result(0));
        strategy.prepare(this.gts);
        this.sgen = (ShapeStateGenerator) strategy.getMatchApplier();

        // EDUARDO: How integrate this with the Exploration class?
        // initialize profiling and prepare graph listener
        strategy.addGTSListener(acceptor);
        boolean interrupted = false;
        // start working until done or nothing to do
        while (!interrupted && !acceptor.getResult().done() && strategy.next()) {
            interrupted = Thread.currentThread().isInterrupted();
        }
        // remove graph listener and stop profiling       
        strategy.removeGTSListener(acceptor);

        if (fromMain) {
            System.out.println("States: " + this.sgen.getStateCount());
            System.out.println("Transitions: " + this.sgen.getTransitionCount());
        }
        if (interrupted) {
            new Exception().printStackTrace();
        }
    }

    /** Generates the state space for the given grammar and start graph. */
    public void generate(String grammarFile, String startGraph, boolean fromMain) {
        loadGrammar(grammarFile, startGraph);
        exploreGrammar(fromMain);
    }

    /** Basic getter method. */
    public int getStateCount() {
        return this.sgen.getStateCount();
    }

    /** Basic getter method. */
    public int getTransitionCount() {
        return this.sgen.getTransitionCount();
    }

    // ------------------------------------------------------------------------
    // Main method.
    // ------------------------------------------------------------------------

    /** Command line method. */
    public static void main(String args[]) {
        String usage =
            "Usage : ShapeGenerator <grammar> <startGraph> "
                + "-n <node_mult_bound> -m <edge_mult_bound -i <abs_radius>";
        if (args.length != 8) {
            System.err.println(usage);
            System.exit(1);
        }

        String grammarFile = args[0];
        String startGraph = args[1];
        int nodeMultBound = Integer.parseInt(args[3]);
        int edgeMultBound = Integer.parseInt(args[5]);
        int absRadius = Integer.parseInt(args[7]);

        // Set the abstraction parameters.
        Parameters.setNodeMultBound(nodeMultBound);
        Parameters.setEdgeMultBound(edgeMultBound);
        Parameters.setAbsRadius(absRadius);

        ShapeGenerator generator = new ShapeGenerator();
        generator.generate(grammarFile, startGraph, true);
    }
}

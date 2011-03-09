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
import groove.explore.Exploration;
import groove.explore.encode.Serialized;
import groove.lts.GraphState;
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

        Exploration exploration =
            new Exploration(new Serialized("shapebfs"),
                new Serialized("final"), 0);
        try {
            exploration.play(this.gts, null);
            if (exploration.isInterrupted()) {
                new Exception().printStackTrace();
            }
            if (fromMain) {
                System.out.println("States: " + this.getStateCount());
                System.out.println("Transitions: " + this.getTransitionCount());
                System.out.println("Final states: "
                    + this.getFinalStatesCount());
                for (GraphState finalState : this.gts.getResultStates()) {
                    Shape finalShape = (Shape) finalState.getGraph();
                    System.out.println(finalShape.toString());
                }
            }
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    /** Generates the state space for the given grammar and start graph. */
    public void generate(String grammarFile, String startGraph, boolean fromMain) {
        loadGrammar(grammarFile, startGraph);
        if (this.grammar != null) {
            exploreGrammar(fromMain);
        }
    }

    /** Basic getter method. */
    public int getStateCount() {
        return this.gts.nodeCount();
    }

    /** Basic getter method. */
    public int getTransitionCount() {
        return this.gts.edgeCount();
    }

    /** Basic getter method. */
    public int getFinalStatesCount() {
        return this.gts.getResultStates().size();
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

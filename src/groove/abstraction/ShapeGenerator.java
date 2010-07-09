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
import groove.explore.Scenario;
import groove.explore.ScenarioFactory;
import groove.explore.result.NoStateAcceptor;
import groove.explore.strategy.BranchingStrategy;
import groove.trans.GraphGrammar;
import groove.util.Groove;
import groove.view.FormatException;

import java.io.IOException;

/**
 * Performs a full abstract exploration of a grammar given as parameter.
 * 
 * @author Eduardo Zambon
 */
public class ShapeGenerator {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    private GraphGrammar grammar;
    private AGTS gts;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    private ShapeGenerator() {
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
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void exploreGrammar() {
        this.gts = new AGTS(this.grammar);

        BranchingStrategy strategy = new BranchingStrategy();
        Scenario scenario =
            ScenarioFactory.getScenario(strategy, new NoStateAcceptor(), "", "");
        scenario.prepare(this.gts);
        ShapeStateGenerator sgen = new ShapeStateGenerator(this.gts);
        strategy.setMatchApplier(sgen);
        scenario.play();
        System.out.println("States: " + sgen.states);
        System.out.println("Transitions: " + sgen.transitions);
        if (scenario.isInterrupted()) {
            new Exception().printStackTrace();
        }
    }

    private void generate(String grammarFile, String startGraph) {
        loadGrammar(grammarFile, startGraph);
        exploreGrammar();
    }

    /** Main method */
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
        generator.generate(grammarFile, startGraph);
    }
}

/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.samples;

import groove.explore.AcceptorValue;
import groove.explore.Exploration;
import groove.explore.StrategyValue;
import groove.explore.result.Result;
import groove.graph.Label;
import groove.graph.TypeLabel;
import groove.graph.algebra.ValueNode;
import groove.lts.GTS;
import groove.lts.GTSAdapter;
import groove.lts.GTSListener;
import groove.lts.GraphTransition;
import groove.trans.GraphGrammar;
import groove.trans.HostGraph;
import groove.util.Groove;
import groove.view.FormatException;
import groove.view.GrammarView;

import java.io.IOException;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class AntWorld {

    /**
     * @param args - two arguments: <grammar-name> <rounds>
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: Antworld <grammar-name> <rounds>");
        } else {
            try {
                go(args[0], Integer.parseInt(args[1]));
            } catch (NumberFormatException e) {
                System.out.println("Usage: Antworld <grammar-name> <rounds>");
            }
        }
    }

    private static void go(String grammarName, int rounds) {
        try {
            GrammarView grammarView = Groove.loadGrammar(grammarName);
            grammarView.setStartGraph("start-" + rounds);
            GraphGrammar grammar = grammarView.toGrammar();
            GTS gts = new GTS(grammar);
            gts.addLTSListener(getStatisticsListener());
            Exploration scenario =
                new Exploration(StrategyValue.RANDOM, AcceptorValue.ANY, 0);
            long startTime = System.currentTimeMillis();
            scenario.play(gts, gts.startState());
            Result result = scenario.getLastResult();
            long endTime = System.currentTimeMillis();
            HostGraph graph = result.getValue().iterator().next().getGraph();
            //            System.out.println(graph);
            int turnCount = getTargetValue(graph, TURN_COUNT_LABEL);
            int antCount = getTargetValue(graph, ANT_COUNT_LABEL);
            int fieldCount = getTargetValue(graph, FIELD_COUNT_LABEL);
            int ringCount = getTargetValue(graph, RING_COUNT_LABEL);
            System.out.printf("%n%s:\t%s\t%s\t%s\t%s%n", "Turn", "Time",
                "Rings", "Fields", "Ants");
            System.out.printf("%d:\t%d\t%d\t%d\t%d%n", turnCount,
                (endTime - startTime), ringCount, fieldCount + 16, antCount);
        } catch (IOException exc) {
            System.err.printf("Can't read in grammar %s: %s", grammarName,
                exc.getMessage());
        } catch (FormatException exc) {
            System.err.printf("Format error in grammar %s: %s", grammarName,
                exc.getMessage());
        }
    }

    static private GTSListener getStatisticsListener() {
        return new GTSAdapter() {
            @Override
            public void addUpdate(GTS gts, GraphTransition trans) {
                this.counter++;
                if (trans.getEvent().getRule().getName().equals("end_turn")) {
                    System.out.print("\n" + trans.getEvent());
                } else if (this.counter % 10 == 0) {
                    System.out.print(".");
                }
            }

            int counter;
        };
    }

    /** Retrieves the int value that is the target of the only edge with a given label. */
    static private int getTargetValue(HostGraph graph, Label label) {
        return (Integer) ((ValueNode) graph.labelEdgeSet(label).iterator().next().target()).getValue();
    }

    static private final Label TURN_COUNT_LABEL =
        TypeLabel.createBinaryLabel("turnCount");
    static private final Label FIELD_COUNT_LABEL =
        TypeLabel.createBinaryLabel("fieldCount");
    static private final Label ANT_COUNT_LABEL =
        TypeLabel.createBinaryLabel("antCount");
    static private final Label RING_COUNT_LABEL =
        TypeLabel.createBinaryLabel("ringCount");
}
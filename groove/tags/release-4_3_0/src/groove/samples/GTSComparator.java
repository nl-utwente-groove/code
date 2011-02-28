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
import groove.lts.GTS;
import groove.lts.GTSAdapter;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.GraphGrammar;
import groove.util.GenerateProgressMonitor;
import groove.util.Groove;
import groove.view.FormatException;

import java.io.IOException;
import java.util.HashMap;

/**
 * Class to test the results of two different explorations of the same grammar.
 * @author Arend Rensink
 * @version $Revision $
 */
public class GTSComparator {

    /**
     * @param args - grammar name and start graph name.
     */
    public static void main(String[] args) {
        if (args.length == 0 || args.length > 2) {
            System.err.println("Call with grammar name and optional start file name");
        } else {
            try {
                String dirName = args[0];
                String startFileName = args.length == 2 ? args[1] : null;
                GraphGrammar grammar =
                    Groove.loadGrammar(dirName, startFileName).toGrammar();
                GTS result1 = runScenario1(grammar);
                runScenario2(grammar, result1);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (FormatException e) {
                e.printStackTrace();
            }
        }
    }

    static private GTS runScenario1(GraphGrammar grammar) {
        GTS result = new GTS(grammar);
        result.addLTSListener(new GenerateProgressMonitor());
        Exploration scenario1 =
            new Exploration(StrategyValue.BFS, AcceptorValue.ANY, 0);
        try {
            scenario1.play(result, result.startState());
        } catch (FormatException e) {
            assert false;
        }
        System.out.printf("%nStates: %d, transitions: %d%n%n",
            result.nodeCount(), result.edgeCount());
        return result;
    }

    static private void runScenario2(GraphGrammar grammar, final GTS result1) {
        final java.util.Map<GraphState,GraphState> relation =
            new HashMap<GraphState,GraphState>();
        GTS result = new GTS(grammar);
        result.addLTSListener(new GenerateProgressMonitor());
        result.addLTSListener(new GTSAdapter() {
            @Override
            public void closeUpdate(GTS lts, GraphState explored) {
                GraphState otherState = result1.addState(explored);
                if (otherState.getTransitionSet().size() != (explored).getTransitionSet().size()) {
                    throw new IllegalStateException();
                }
            }

            @Override
            public void addUpdate(GTS gts, GraphState state) {
                if (!result1.containsNode(state)) {
                    throw new IllegalStateException();
                } else {
                    relation.put(state, result1.getStateSet().put(state));
                }
            }

            @Override
            public void addUpdate(GTS gts, GraphTransition transition) {
                if (!result1.containsEdge(transition)) {
                    throw new IllegalStateException();
                }
            }
        });
        Exploration scenario2 =
            new Exploration(StrategyValue.DFS, AcceptorValue.ANY, 0);
        try {
            scenario2.play(result, result.startState());
        } catch (FormatException e) {
            assert false;
        }
        System.out.printf("%nStates: %d, transitions: %d%n%n",
            result.nodeCount(), result.edgeCount());
    }
}

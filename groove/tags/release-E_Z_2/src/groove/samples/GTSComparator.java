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

import groove.explore.Scenario;
import groove.explore.ScenarioFactory;
import groove.explore.strategy.BFSStrategy;
import groove.explore.strategy.DFSStrategy;
import groove.graph.Edge;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.LTS;
import groove.lts.LTSAdapter;
import groove.lts.State;
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
        result.addGraphListener(new GenerateProgressMonitor());
        Scenario scenario1 =
            ScenarioFactory.getScenario(new BFSStrategy(), null, null, null);
        scenario1.prepare(result);
        scenario1.play();
        System.out.printf("%nStates: %d, transitions: %d%n%n",
            result.nodeCount(), result.edgeCount());
        return result;
    }

    static private void runScenario2(GraphGrammar grammar, final GTS result1) {
        final java.util.Map<GraphState,GraphState> relation =
            new HashMap<GraphState,GraphState>();
        GTS result = new GTS(grammar);
        result.addGraphListener(new GenerateProgressMonitor());
        result.addGraphListener(new LTSAdapter() {
            @Override
            public void closeUpdate(LTS graph, State explored) {
                GraphState otherState = result1.addState((GraphState) explored);
                if (otherState.getTransitionSet().size() != ((GraphState) explored).getTransitionSet().size()) {
                    throw new IllegalStateException();
                }
            }

            @Override
            public void addUpdate(GraphShape graph, Node node) {
                GraphState state = (GraphState) node;
                if (!result1.containsElement(state)) {
                    throw new IllegalStateException();
                } else {
                    relation.put(state, result1.getStateSet().put(state));
                }
            }

            @Override
            public void addUpdate(GraphShape graph, Edge edge) {
                GraphTransition trans = (GraphTransition) edge;
                if (!result1.containsElement(trans)) {
                    throw new IllegalStateException();
                } else {
                    //                    relation.put(trans, result1.getStateSet().put(trans));
                }
            }
        });
        Scenario scenario2 =
            ScenarioFactory.getScenario(new DFSStrategy(), null, null, null);
        scenario2.prepare(result);
        scenario2.play();
        System.out.printf("%nStates: %d, transitions: %d%n%n",
            result.nodeCount(), result.edgeCount());
    }
}

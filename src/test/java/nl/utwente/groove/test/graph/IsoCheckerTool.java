/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.test.graph;

import java.io.IOException;

import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.iso.IsoChecker;
import nl.utwente.groove.graph.plain.PlainEdge;
import nl.utwente.groove.graph.plain.PlainGraph;
import nl.utwente.groove.graph.plain.PlainMorphism;
import nl.utwente.groove.graph.plain.PlainNode;
import nl.utwente.groove.io.Groove;

/**
 * Hand-run debug harness for {@link IsoChecker}.
 * @author Arend Rensink
 * @version $Revision$
 */
public class IsoCheckerTool {
    private IsoCheckerTool() {
        // empty by design
    }

    /**
     * If called with two file names, compares the graphs stored in those files
     * and reports whether they are isomorphic.
     */
    public static void main(String[] args) {
        try {
            if (args.length == 1) {
                testIso(args[0]);
            } else if (args.length == 2) {
                compareGraphs(args[0], args[1]);
            } else {
                System.out.println("Usage: DefaultIsoChecker file1 file2");
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testIso(String name) throws IOException {
        PlainGraph graph1 = Groove.loadGraph(name);
        IsoChecker checker = IsoChecker.getInstance(true);
        System.out
            .printf("Graph certificate: %s%n",
                    checker.getCertifier(graph1, true).getGraphCertificate());
        for (int i = 0; i < 1000; i++) {
            PlainGraph graph2 = new PlainGraph(name, GraphRole.NONE);
            PlainMorphism nodeMap = new PlainMorphism(graph2.getFactory());
            for (PlainNode node : graph1.nodeSet()) {
                PlainNode newNode = graph2.addNode();
                nodeMap.putNode(node, newNode);
            }
            for (PlainEdge edge : graph1.edgeSet()) {
                var image = nodeMap.mapEdge(edge);
                assert image != null;
                graph2.addEdgeContext(image);
            }
            if (!checker.areIsomorphic(graph1, graph2)) {
                System.out.println("Error! Graph not isomorphic to itself");
            }
        }
    }

    private static void compareGraphs(String name1, String name2) throws IOException {
        PlainGraph graph1 = Groove.loadGraph(name1);
        PlainGraph graph2 = Groove.loadGraph(name2);
        System.out.printf("Graphs '%s' and '%s' isomorphic?%n", name1, name2);
        System.out
            .printf("Done. Result: %b%n",
                    (IsoChecker.getInstance(true)).areIsomorphic(graph1, graph2));
        System.out.printf("Certification time: %d%n", IsoChecker.getCertifyingTime());
        System.out.printf("Simulation time: %d%n", IsoChecker.getSimCheckTime());
    }
}

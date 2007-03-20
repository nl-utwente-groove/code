// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/*
 * $Id: GenerateProgressMonitor.java,v 1.1.1.2 2007-03-20 10:42:58 kastenberg Exp $
 */
package groove.util;

import groove.graph.Edge;
import groove.graph.GraphAdapter;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.lts.ExploreStrategy;
import groove.lts.GTS;
import groove.lts.explore.BranchingStrategy;

/**
 * Class that implements a visualisation of the progress of a GTS generation process.
 * The monitor should be added as a {@link groove.graph.GraphListener} to the GTS
 * in question.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public class GenerateProgressMonitor extends GraphAdapter {
    /**
     * Creates a monitor that does not care which GTS is being monitored.
     */
    public GenerateProgressMonitor() {
        this.strategy = null;
    }
    
    /**
     * Creates a monitor for a given graph transformation system.
     * The GTS is only used to determine how many nodes and edges already exist.
     * @param gts the GTS being explored
     */
    public GenerateProgressMonitor(GTS gts, ExploreStrategy strategy) {
        this.strategy = strategy;
    }

    public void addUpdate(GraphShape graph, Node node) {
        if (graph.nodeCount() % UNIT == 0) {
            System.out.print("s");
            printed++;
        }
        endLine((GTS) graph);
    }

    public void addUpdate(GraphShape graph, Edge edge) {
        if (graph.edgeCount() % UNIT == 0) {
            System.out.print("t");
            printed++;
        }
        endLine((GTS) graph);
    }
    
    private void endLine(GTS gts) {
        if (printed == WIDTH) {
            int nodeCount = gts.nodeCount();
            int edgeCount = gts.edgeCount();
            int explorableCount = gts.openStateCount();
            if (strategy instanceof BranchingStrategy) {
                explorableCount -= ((BranchingStrategy) strategy).getIgnoredCount();
            }
            System.out.println(" " + nodeCount + "s (" + explorableCount + "x) "
                    + edgeCount + "t ");
            printed = 0;
        }
    }

    /**
     * The number of indications printed on the current line.
     */
    private int printed = 0;
    /** The exploration strategy for the GTS. */
    private final ExploreStrategy strategy;
    /**
     * The number of additions after which an indication is printed to screen.
     */
    static private final int UNIT = 100;
    /**
     * Number of indications on one line.
     */
    static private final int WIDTH = 100;
}
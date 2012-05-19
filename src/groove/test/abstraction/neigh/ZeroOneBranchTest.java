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
package groove.test.abstraction.neigh;

import groove.abstraction.neigh.NeighAbsParam;
import groove.abstraction.neigh.explore.ShapeGenerator;
import groove.abstraction.neigh.lts.AGTS;
import groove.abstraction.neigh.trans.EquationSystem;
import groove.lts.GraphState;

/**
 * Testing whether the loss of transitions caused by not
 * testing the zero/one branches is semantically sound.
 */
@SuppressWarnings("all")
public class ZeroOneBranchTest {
    private static final String STRATEGY = "shapedfs";

    /** Starts the comparison. */
    public static void main(String[] args) {
        final String GRAMMAR = "junit/abstraction/euler-counting.gps";
        ShapeGenerator generator;
        args = new String[] {"-v", "0", "-s", STRATEGY, GRAMMAR};
        NeighAbsParam.getInstance().setNodeMultBound(1);
        NeighAbsParam.getInstance().setEdgeMultBound(1);

        generator = new ShapeGenerator(args);
        generator.start();
        AGTS gts1 = generator.getReducedGTS();
        EquationSystem.ENABLE_ZERO_ONE_BRANCHES = false;
        generator = new ShapeGenerator(args);
        generator.start();
        AGTS gts2 = generator.getReducedGTS();
        for (GraphState state : gts2.nodeSet()) {
            if (!gts1.containsNode(state)) {
                System.out.println(state.getGraph());
            }
        }
    }
}

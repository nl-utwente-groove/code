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
package groove.abstraction.neigh.explore.util;

import groove.abstraction.neigh.explore.ShapeGenerator;
import groove.abstraction.neigh.gui.dialog.ShapePreviewDialog;
import groove.abstraction.neigh.io.xml.ShapeGxl;
import groove.abstraction.neigh.lts.AGTS;
import groove.abstraction.neigh.lts.ShapeState;
import groove.abstraction.neigh.shape.iso.ShapeIsoChecker;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Test class used to check the consistency of state space collapsing under
 * subsumption. 
 * 
 * @author Eduardo Zambon
 */
public final class CollapsingAnalyser {

    private static final String grammar =
        "junit/abstraction/euler-counting.gps";
    private static final String startGraph = "start";

    private static List<String> getArgs(String strategy) {
        String args[] = {"-v", 1 + "", "-s", strategy, grammar, startGraph};
        return new LinkedList<String>(Arrays.asList(args));
    }

    private static boolean areExactlyEqual(ShapeState s, ShapeState t) {
        return ShapeIsoChecker.areExactlyEqual(s.getGraph(), t.getGraph());
    }

    /** Test method. Run without arguments. */
    public static void main(String[] args) {
        ShapeGenerator genBFS = new ShapeGenerator(getArgs("shapebfs"));
        genBFS.start();
        AGTS gtsBFS = genBFS.getReducedGTS();

        ShapeGenerator genDFS = new ShapeGenerator(getArgs("shapedfs"));
        genDFS.start();
        AGTS gtsDFS = genDFS.getReducedGTS();

        for (ShapeState stateDFS : gtsDFS.nodeSet()) {
            boolean found = false;
            for (ShapeState stateBFS : gtsBFS.nodeSet()) {
                if (areExactlyEqual(stateDFS, stateBFS)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("DFS state missing in BFS: " + stateDFS);
                File file = new File(stateDFS.toString() + ".gxl");
                ShapeGxl.getInstance().saveShape(stateDFS.getGraph(), file);
                ShapePreviewDialog.showShape(stateDFS.getGraph());
            }
        }

        for (ShapeState stateBFS : gtsBFS.nodeSet()) {
            boolean found = false;
            for (ShapeState stateDFS : gtsDFS.nodeSet()) {
                if (areExactlyEqual(stateDFS, stateBFS)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("BFS state missing in DFS: " + stateBFS);
                File file = new File(stateBFS.toString() + ".gxl");
                ShapeGxl.getInstance().saveShape(stateBFS.getGraph(), file);
                ShapePreviewDialog.showShape(stateBFS.getGraph());
            }
        }
    }
}

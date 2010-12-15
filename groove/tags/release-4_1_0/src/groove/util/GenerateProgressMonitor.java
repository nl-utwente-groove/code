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
 * $Id: GenerateProgressMonitor.java,v 1.3 2008-01-30 09:32:09 iovka Exp $
 */
package groove.util;

import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.LTS;
import groove.lts.LTSAdapter;

/**
 * Class that implements a visualisation of the progress of a GTS generation
 * process. The monitor should be added as a {@link groove.graph.GraphListener}
 * to the GTS in question.
 * @author Arend Rensink
 * @version $Revision$
 */
public class GenerateProgressMonitor extends LTSAdapter {
    /**
     * Creates a monitor that reports on states and transitions generated.
     */
    public GenerateProgressMonitor() {
        // empty
    }

    @Override
    public void addUpdate(LTS lts, GraphState state) {
        if (lts.nodeCount() % UNIT == 0) {
            System.out.print("s");
            this.printed++;
        }
        endLine((GTS) lts);
    }

    @Override
    public void addUpdate(LTS lts, GraphTransition transition) {
        if (lts.edgeCount() % UNIT == 0) {
            System.out.print("t");
            this.printed++;
        }
        endLine((GTS) lts);
    }

    private void endLine(GTS gts) {
        if (this.printed == WIDTH) {
            int nodeCount = gts.nodeCount();
            int edgeCount = gts.edgeCount();
            int explorableCount = gts.openStateCount();
            System.out.println(" " + nodeCount + "s (" + explorableCount
                + "x) " + edgeCount + "t ");
            this.printed = 0;
        }
    }

    /**
     * The number of indications printed on the current line.
     */
    private int printed = 0;
    /**
     * The number of additions after which an indication is printed to screen.
     */
    static private final int UNIT = 100;
    /**
     * Number of indications on one line.
     */
    static private final int WIDTH = 100;
}
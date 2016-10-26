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
 * $Id$
 */
package groove.explore.util;

import groove.lts.GTSListener;

/**
 * Class that implements a visualisation of the progress of a GTS generation
 * process. The monitor should be added as a {@link GTSListener}
 * to the GTS in question.
 * @author Arend Rensink
 * @version $Revision$
 */
public class GenerateProgressMonitor {
    /** Resets all counters, in preparation for a new GTS exploration. */
    protected void restart() {
        this.started = false;
        this.printed = 0;
    }

    /** Prints an "s" and potentially ends the current line. */
    protected void addState(int stateCount, int transCount, int openCount) {
        if (stateCount % UNIT == 0) {
            print("s");
            endLine(stateCount, transCount, openCount);
        }
    }

    /** Prints t "t" and potentially ends the current line. */
    protected void addTransition(int stateCount, int transCount, int openCount) {
        if (transCount % UNIT == 0) {
            print("t");
            endLine(stateCount, transCount, openCount);
        }
    }

    private void print(String text) {
        if (!this.started) {
            System.out.printf(
                "Progress: (s = %1$s states, t = %1$s transitions):%n  ", UNIT);
            this.started = true;
        }
        System.out.print(text);
        this.printed++;
    }

    private void endLine(int stateCount, int transCount, int openCount) {
        if (this.printed == WIDTH) {
            System.out.printf(" %ss (%sx) %st%n  ", stateCount, openCount,
                transCount);
            this.printed = 0;
        }
    }

    /** Boolean indicating if any output has been generated. */
    private boolean started = false;
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
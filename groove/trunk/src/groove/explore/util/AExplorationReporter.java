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
package groove.explore.util;

import groove.explore.Exploration;
import groove.lts.GTS;

import java.io.IOException;

/**
 * Abstract implementation of an {@link ExplorationReporter}.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class AExplorationReporter implements ExplorationReporter {
    @Override
    public void start(Exploration exploration, GTS gts) {
        this.exploration = exploration;
        this.gts = gts;
    }

    /** This implementation delegates to {@link #stop(GTS)}. */
    @Override
    public void abort(GTS gts) {
        stop(gts);
    }

    /** This implementation does nothing. */
    @Override
    public void stop(GTS gts) {
        // empty
    }

    /**
     * Produces the result of the most recently recorded exploration.
     * This method will only be called after {@link #start}.
     * @throws IOException if an error occurred during reporting
     */
    @Override
    public void report() throws IOException {
        // the default implementation does nothing
    }

    /** Returns the exploration set at the latest call of {@link #start(Exploration, GTS)}. */
    protected Exploration getExploration() {
        return this.exploration;
    }

    /** Returns the most recently explored GTS. */
    protected GTS getGTS() {
        return this.gts;
    }

    private Exploration exploration;
    private GTS gts;
}

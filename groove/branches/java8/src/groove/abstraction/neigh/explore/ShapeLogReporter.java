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
package groove.abstraction.neigh.explore;

import groove.abstraction.neigh.NeighAbsParam;
import groove.abstraction.neigh.lts.AGTS;
import groove.explore.Verbosity;
import groove.explore.util.LogReporter;

import java.io.IOException;

/**
 * Log reporter for shape generation.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ShapeLogReporter extends LogReporter {
    /**
     * Constructs a reporter with the given parameters.
     */
    public ShapeLogReporter(String startGraphName, Verbosity verbosity, boolean reachability) {
        super(verbosity, null);
        this.reachability = reachability;
    }

    @Override
    protected void emitStartMessage() {
        super.emitStartMessage();
        NeighAbsParam params = NeighAbsParam.getInstance();
        emit("Node bound:\t%s\tEdge bound:\t%s%s%n", params.getNodeMultBound(),
            params.getEdgeMultBound(), params.isUseThreeValues()
                    ? "\tLIMITING MULTIPLICITIES TO 0, 1 and 0+" : "");
        if (this.reachability) {
            emit("Reachability mode ON.%n");
        }
    }

    @Override
    public void report() throws IOException {
        emit("%n");
        reportGTS(getGTS(), "Original GTS ");
        AGTS reducedGTS = getGTS().reduceGTS();
        reportGTS(reducedGTS, "Reduced GTS  ");
        super.report();
    }

    private void reportGTS(AGTS gts, String header) {
        emit(
            "%s: States: %d (%d final) -- %d subsumed (%d discarded) / Transitions: %d (%d subsumed)\n",
            header, gts.nodeCount(), gts.getFinalStates().size(), gts.getSubsumedStatesCount(),
            gts.getOpenStateCount(), gts.edgeCount(), gts.getSubsumedTransitionsCount());
    }

    /* Specialises the return type. */
    @Override
    protected AGTS getGTS() {
        return (AGTS) super.getGTS();
    }

    private final boolean reachability;
}

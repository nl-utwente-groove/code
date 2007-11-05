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
 * $Id: CompositeDeltaApplier.java,v 1.1 2007-11-05 15:43:38 rensink Exp $
 */
package groove.graph;

import java.util.List;

/**
 * Class wrapping a list of delta appliers.
 * Application of an instance of this class means sequential application of the
 * appliers in the list.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CompositeDeltaApplier implements DeltaApplier {
    /** 
     * Constructs a composite applier from a given list.
     * The list is shared.
     * @param deltaList the list of individual delta appliers.
     */
    public CompositeDeltaApplier(List<DeltaApplier> deltaList) {
        this.deltaList = deltaList;
    }
    
    /** Applies the deltas in the list sequentially. */
    public void applyDelta(DeltaTarget target) {
        for (DeltaApplier applier: deltaList) {
            applier.applyDelta(target);
        }
    }

    /** Applies the deltas in the list sequentially. */
    public void applyDelta(DeltaTarget target, int mode) {
        for (DeltaApplier applier: deltaList) {
            applier.applyDelta(target, mode);
        }
    }

    /** The wrapped list of delta appliers. */
    private final List<DeltaApplier> deltaList;
}

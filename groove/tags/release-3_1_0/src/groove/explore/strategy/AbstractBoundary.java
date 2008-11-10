/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
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
package groove.explore.strategy;

import groove.lts.ProductTransition;
import groove.verify.ModelChecking;

/**
 * Abstract implementation for boundaries.
 * 
 * @author Harmen Kastenberg
 * @version $Revision $
 */
public abstract class AbstractBoundary implements Boundary {

    public int currentDepth() {
        return this.currentDepth;
    }

    public void setCurrentDepth(int value) {
        this.currentDepth = value;
    }

    public void decreaseDepth() {
        this.currentDepth--;
        assert (this.currentDepth >= 0) : "The value of currentDepth should not be negative.";
    }

    public void increaseDepth() {
        this.currentDepth++;
        assert (this.currentDepth <= ModelChecking.CURRENT_ITERATION) : "the number of boundary-crossing transitions ("
            + this.currentDepth
            + ") in the current path exceeded the maximum ("
            + ModelChecking.CURRENT_ITERATION + ")";
    }

    public void backtrackTransition(ProductTransition transition) {
        // by default, do nothing
    }

    /**
     * container for the number of boundary-crossing transitions in the current
     * path
     */
    private int currentDepth = 0;
}

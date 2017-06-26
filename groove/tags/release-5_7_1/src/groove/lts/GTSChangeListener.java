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
package groove.lts;

/**
 * LTS listener to observe changes to the GTS.
 */
public class GTSChangeListener implements GTSListener {
    /** Empty constructor with the correct visibility. */
    public GTSChangeListener() {
        // empty
    }

    /** Clears the changed flag. */
    public void clear() {
        this.changed = false;
    }

    /** Hook for subclasses to signal that a change has occurred. */
    protected void setChanged() {
        this.changed = true;
    }

    /**
     * May only be called with the current lts as first parameter. Updates
     * the frame title by showing the number of nodes and edges.
     */
    @Override
    public void addUpdate(GTS gts, GraphState state) {
        this.changed = true;
    }

    @Override
    public void addUpdate(GTS gts, GraphTransition transition) {
        this.changed = true;
    }

    @Override
    public void statusUpdate(GTS graph, GraphState explored, int change) {
        this.changed = true;
    }

    /** Indicates that a change has been registered. */
    public boolean isChanged() {
        return this.changed;
    }

    private boolean changed;
}
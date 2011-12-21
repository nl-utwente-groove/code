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
package groove.util;

/**
 * Class that records the change count of a given structure,
 * and allows observers to keep track of the count.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ChangeCount {
    /** Increases the change count. */
    public void increase() {
        this.value++;
    }

    /**
     * Creates a tracker for this count, 
     * initialised at the current value.
     */
    public Tracker createTracker() {
        return new Tracker(this);
    }

    /** Returns the current value of the count. */
    int getValue() {
        return this.value;
    }

    private int value;

    /** Dummy tracker that never gets stale. */
    public static final Tracker DUMMY_TRACKER = new DummyTracker();

    /** Tracker of an update count. */
    static public class Tracker {
        /** Constructs a tracker, initialised at a given update. */
        private Tracker(ChangeCount model) {
            this.count = model;
            this.last = model.getValue();
        }

        /**
         * Indicates if the tracker is stale with respect to the
         * observed change count.
         * @return if {@code true}, the observed count has been increased since the
         * last invocation of {@link #isStale()}
         */
        public boolean isStale() {
            int current = this.count.getValue();
            boolean result = this.last < current;
            this.last = current;
            return result;
        }

        private final ChangeCount count;
        private int last;
    }

    static private class DummyTracker extends Tracker {
        private DummyTracker() {
            super(new ChangeCount());
        }
    }

    /** 
     * Class wrapping a value that is derived from a structure with a {@link ChangeCount},
     * with capability to recompute the value whenever it gets stale with respect 
     * to the parent structure.
     * @author Arend Rensink
     * @version $Revision $
     */
    static public abstract class Derived<O> {
        /** Constructs a derived value for a given change count. */
        public Derived(ChangeCount count) {
            this.tracker = count.createTracker();
        }

        /** Gets the (possibly recomputed) value. */
        public O getValue() {
            if (this.value == null || this.tracker.isStale()) {
                this.value = computeValue();
            }
            return this.value;
        }

        /** Callback method to recompute the value. */
        abstract protected O computeValue();

        private final Tracker tracker;
        private O value;
    }
}

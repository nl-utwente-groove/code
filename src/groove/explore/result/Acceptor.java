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
package groove.explore.result;

import groove.explore.ExploreResult;
import groove.lts.GTS;
import groove.lts.GTSListener;

/**
 * Listens to a GTS and adds accepted elements to a result.
 * Also indicates if the exploration is done.
 */
public abstract class Acceptor implements GTSListener {
    /** Creates an acceptor without exploration bound. */
    protected Acceptor(boolean prototype) {
        this(prototype, 0);
    }

    /** Creates an acceptor with a given exploration bound. */
    protected Acceptor(int bound) {
        this(false, bound);
    }

    /** Auxiliary constructor that sets both the prototype and the bound field. */
    private Acceptor(boolean prototype, int bound) {
        assert bound >= 0;
        this.prototype = prototype;
        this.bound = bound;
    }

    /**
     * Prototype method to create a new instance of this acceptor,
     * with a given exploration bound.
     */
    public abstract Acceptor newAcceptor(int bound);

    /** Indicates if this acceptor has a (non-zero) explroation bound. */
    public boolean hasBound() {
        return getBound() > 0;
    }

    /** Returns the exploration bound of this acceptor.
     * The bound is the number of states in the result after which the acceptor
     * signals that the exploration should be halted (using {@link #done()}.
     * A bound of 0 means that the bound is infinite, i.e., exploration is never halted.
     * @see #done()
     */
    public int getBound() {
        return this.bound;
    }

    private final int bound;

    /**
     * Indicates whether this acceptor is a prototype object.
     * If so, it should only be used to invoke {@link #newAcceptor(int)}.
     */
    public boolean isPrototype() {
        return this.prototype;
    }

    /**
     * Flag indicating that this is a prototype acceptor.
     * For a prototype acceptor, {@link #prepare(GTS)} should not be invoked.
     */
    private final boolean prototype;

    /** Prepares the acceptor for a new exploration.
     * In particular, sets a fresh {@link ExploreResult}.
     * @param gts the GTS of the new exploration
     */
    public void prepare(GTS gts) {
        assert !this.prototype : "Using a prototype acceptor";
        this.result = createResult(gts);
    }

    /** Factory method to create a result object.
     * @param gts the GTS being explored
     */
    protected ExploreResult createResult(GTS gts) {
        return new ExploreResult(gts);
    }

    /** Tests if the exploration is done,
     * according to the demands of this acceptor.
     */
    public boolean done() {
        return hasBound() && getResult().size() >= getBound();
    }

    /**
     * Returns the result.
     * @return The result
     */
    public ExploreResult getResult() {
        return this.result;
    }

    private ExploreResult result;

    /** Returns a message describing the accepted result. */
    public String getMessage() {
        String result;
        if (this.result.isEmpty()) {
            result = "No result states found";
        } else {
            result = this.result.size() + " result states found: " + this.result;
        }
        return result;
    }
}

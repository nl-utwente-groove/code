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
 * $Id: AbstrGraphStateImpl.java,v 1.3 2008-01-31 08:22:52 rensink Exp $
 */
package groove.abs.lts;

import groove.abs.AbstrGraph;
import groove.abs.MyHashSet;
import groove.control.Location;
import groove.graph.Element;
import groove.lts.AbstractGraphState;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.RuleEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Eduardo Zambon
 * @version $Revision $
 */
public class ShapeGraphState extends AbstractGraphState {

    /**
     * @ensure All resulting objects are of type {@link ShapeGraphTransition}.
     */
    @Override
    public Iterator<GraphTransition> getTransitionIter() {
        return this.transitions.iterator();
    }

    /**
     * @return true if the state has no transitions, false otherwise.
     */
    public boolean isWithoutOutTransition() {
        return this.transitions.isEmpty();
    }

    /**
     * @ensure All resulting objects are of type {@link ShapeGraphTransition}.
     */
    @Override
    public Set<GraphTransition> getTransitionSet() {
        return Collections.unmodifiableSet(new HashSet<GraphTransition>(
            this.transitions));
    }

    /**
     * @require transition is of type ShapeGraphTransition
     */
    @Override
    public boolean addTransition(GraphTransition transition) {
        return this.transitions.getAndAdd(transition) == null;
    }

    @Override
    final public AbstrGraph getGraph() {
        return this.graph;
    }

    @Override
    public boolean setClosed() {
        boolean result = !this.closed;
        this.closed = true;
        return result;
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    /**
     * This implementation compares state numbers. The current state is either
     * compared with the other, if that is a {@link ShapeGraphState}, or
     * with its source state if it is a {@link ShapeGraphTransition}.
     * Otherwise, the method throws an {@link UnsupportedOperationException}.
     */
    @Override
    public int compareTo(Element obj) {
        if (obj instanceof ShapeGraphState) {
            return getStateNumber() - ((ShapeGraphState) obj).getStateNumber();
        } else if (obj instanceof ShapeGraphTransition) {
            return getStateNumber()
                - ((ShapeGraphTransition) obj).source().getNumber();
        } else {
            throw new UnsupportedOperationException(String.format(
                "Classes %s and %s cannot be compared", getClass(),
                obj.getClass()));
        }
    }

    /** Always null for the moment. */
    public Location getControl() {
        return null;
    }

    /**
     * Sets the state number. This method should be called only once, with a
     * non-negative number.
     * @throws IllegalStateException if {@link #hasStateNumber()} returns
     *         <code>true</code>
     * @throws IllegalArgumentException if <code>nr</code> is illegal (i.e.,
     *         smaller than -1)
     */
    void setStateNumber(int n) {
        if (hasStateNumber()) {
            throw new IllegalStateException(String.format(
                "State number already set to %s", this.nr));
        }
        if (n < -1) {
            throw new IllegalArgumentException(String.format(
                "Illegal state number %s", this.nr));
        }
        this.nr = n;
    }

    /** */
    protected int getStateNumber() {
        if (!hasStateNumber()) {
            throw new IllegalStateException("State number not set");
        }
        return this.nr;
    }

    /**
     * @return the state number
     */
    public boolean hasStateNumber() {
        return this.nr >= 0;
    }

    // ------------------------------------------------------------
    // CONSTRUCTORS, STANDARD METHODS
    // ------------------------------------------------------------

    /**
     * Constructs an state with specified underlying graph and with empty set of
     * transitions.
     */
    public ShapeGraphState(AbstrGraph graph) {
        // EDUARDO: We don't have a cache reference at the moment, so just pass
        // null to the super constructor.
        super(null);
        this.graph = graph;
        this.closed = false;
        this.nr = -1;
    }

    private final AbstrGraph graph;
    private boolean closed;
    /** The outgoing transitions from this state */
    protected final MyHashSet<GraphTransition> transitions =
        new MyHashSet<GraphTransition>(new TransitionHasher());

    @Override
    public String toString() {
        return "s" + (hasStateNumber() ? this.nr : "??");
    }

    /**
     * This implementation returns true if the underlying abstract graphs have
     * isomorphic structure with compatible types and multiplicities. OPTIM to
     * be adapted if I want to group together graphs with compatible
     * multiplicities
     */
    // EDUARDO: TODO: Perform the optimization described above, otherwise the
    // state spaces get too big.
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ShapeGraphState)) {
            return false;
        }
        if (o instanceof ShapeGraphNextState) {
            return false;
        }
        ShapeGraphState other = (ShapeGraphState) o;
        if (other == AGTS.INVALID_STATE) {
            return false;
        }
        if (hasStateNumber() && other.hasStateNumber()
            && getStateNumber() == other.getStateNumber()) {
            return true;
        }
        boolean result = getGraph().equals(((ShapeGraphState) o).getGraph());
        assert (!result || other.hashCode() == hashCode()) : "The equals method does not comply with the hash code method !!!";
        return result;
    }

    @Override
    public int hashCode() {
        return getGraph().hashCode();
    }

    /**
     * @ensure All resulting objects are of type {@link ShapeGraphState}.
     */
    @Override
    public Iterator<GraphState> getNextStateIter() {
        return new Iterator<GraphState>() {
            Iterator<GraphTransition> it =
                ShapeGraphState.this.getTransitionIter();

            public boolean hasNext() {
                return this.it.hasNext();
            }

            public GraphState next() {
                return this.it.next().target();
            }

            public void remove() {
                this.it.remove();
            }

        };
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public int getNumber() {
        return this.nr;
    }

    @Override
    protected void updateClosed() {
        // Nothing to do.
    }

    // ------------------------------------------------------------
    // UNIMPLEMENTED METHODS
    // ------------------------------------------------------------

    @Override
    public GraphState getNextState(RuleEvent prime) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<GraphState> getNextStateSet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsTransition(GraphTransition transition) {
        throw new UnsupportedOperationException();
    }

    /**
     * @param event
     * @return unimplemented
     */
    public Iterator<GraphState> getNextStates(RuleEvent event) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLocation(Location l) {
        throw new UnsupportedOperationException();
    }

    // ------------------------------------------------------------------------
    // FIELDS
    // ------------------------------------------------------------------------

    /** The internally stored (optional) control location. */
    private Location location;

    // ------------------------------------------------------------------------
    // INNER CLASSES
    // ------------------------------------------------------------------------

    class TransitionHasher implements MyHashSet.Hasher<GraphTransition> {

        public int getHashCode(GraphTransition o) {
            return o.label().hashCode();
        }

        public boolean areEqual(GraphTransition o1, GraphTransition o2) {
            ShapeGraphState s1 = (ShapeGraphState) o1.source();
            ShapeGraphState s2 = (ShapeGraphState) o2.source();
            ShapeGraphState t1 = (ShapeGraphState) o1.target();
            ShapeGraphState t2 = (ShapeGraphState) o2.target();
            return s1.nr == s2.nr && t1.nr == t2.nr
                && o1.label().equals(o2.label());
        }

    }

}

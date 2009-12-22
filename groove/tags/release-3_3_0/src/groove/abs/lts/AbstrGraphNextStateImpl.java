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
package groove.abs.lts;

import groove.abs.AbstrGraph;
import groove.graph.Label;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.lts.GraphTransitionStub;
import groove.trans.RuleEvent;
import groove.trans.RuleMatch;

/**
 * Combines an {@link AbstrGraphState} and an {@link AbstrGraphTransition}. The
 * graph transition functionality is implemented by an enclosed
 * {@link AbstrGraphTransition}.
 * @author Iovka Boneva
 * @version $Revision $
 */
public class AbstrGraphNextStateImpl extends AbstrGraphStateImpl implements
        AbstrGraphNextState {

    /**
     * Creates a next state from the given graph, source graph state and event.
     * @param graph
     * @param source
     * @param event
     */
    public AbstrGraphNextStateImpl(AbstrGraph graph, AbstrGraphState source,
            RuleEvent event) {
        super(graph);
        this.theTrans = new AbstrGraphTransitionImpl(source, event, null);
    }

    /** The transition component. */
    private final AbstrGraphTransitionImpl theTrans;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AbstrGraphNextStateImpl)) {
            return false;
        }
        AbstrGraphNextStateImpl other = (AbstrGraphNextStateImpl) o;
        boolean result = getGraph().equals(((AbstrGraphState) o).getGraph());
        result = result && super.equals(other);
        // the targets of this.theTrans and other.theTrans cannot be compared
        // with equals, as they are both null
        result =
            result && this.theTrans.source().equals(other.theTrans.source());
        result = result && this.theTrans.label().equals(other.theTrans.label());
        assert (!result || other.hashCode() == hashCode()) : "The equals method does not comply with the hash code method !!!";
        return result;
    }

    /** Is the hash code considering the state as transition. */
    @Override
    public int hashCode() {
        return super.hashCode() + this.theTrans.source().hashCode();
    }

    public Node[] getAddedNodes() {
        return this.theTrans.getAddedNodes();
    }

    public RuleEvent getEvent() {
        return this.theTrans.getEvent();
    }

    public RuleMatch getMatch() {
        return this.theTrans.getMatch();
    }

    public Morphism getMorphism() {
        return this.theTrans.getMorphism();
    }

    public boolean isSymmetry() {
        throw new UnsupportedOperationException();
    }

    public GraphTransitionStub toStub() {
        throw new UnsupportedOperationException();
    }

    public AbstrGraphState source() {
        return this.theTrans.source();
    }

    public AbstrGraphState target() {
        return this;
    }

    public Node end(int i) {
        switch (i) {
        case SOURCE_INDEX:
            return source();
        case TARGET_INDEX:
            return target();
        default:
            throw new IllegalArgumentException("Illegal end index number " + i
                + " for " + this);
        }
    }

    public int endCount() {
        return 2;
    }

    public int endIndex(Node node) {
        if (source().equals(node)) {
            return 0;
        }
        if (target().equals(node)) {
            return 1;
        }
        return -1;
    }

    public Node[] ends() {
        Node[] result = new Node[2];
        result[0] = source();
        result[1] = target();
        return result;
    }

    public boolean hasEnd(Node node) {
        return source().equals(node) || target().equals(node);
    }

    public Label label() {
        return this.theTrans.label();
    }

    public Node opposite() {
        return target();
    }

    public boolean isEquivalent(AbstrGraphTransition other) {
        // ok, as the targets are note compared
        return this.theTrans.isEquivalent(other);
    }

}

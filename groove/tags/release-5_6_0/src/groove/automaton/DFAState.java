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
package groove.automaton;

import groove.grammar.type.TypeLabel;
import groove.graph.Direction;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * State of a normalised finite automaton.
 */
public class DFAState {
    DFAState(int number, Set<RegNode> nodes, boolean initial, boolean isFinal) {
        this.number = number;
        this.nodes = new HashSet<>(nodes);
        this.initial = initial;
        this.isFinal = isFinal;
        for (Direction dir : Direction.values()) {
            this.labelSuccMap.put(dir, new HashMap<TypeLabel,DFAState>());
        }
    }

    /** Indicates if this is the unique initial state. */
    public boolean isInitial() {
        return this.initial;
    }

    /** Indicates if this is a final state. */
    public boolean isFinal() {
        return this.isFinal;
    }

    /** Returns the underlying set of nodes. */
    public Set<RegNode> getNodes() {
        return this.nodes;
    }

    /** Adds an outgoing, concretely labelled transition to another state. */
    public void addSuccessor(Direction dir, TypeLabel label, DFAState succ) {
        DFAState oldSucc = this.labelSuccMap.get(dir).put(label, succ);
        assert oldSucc == null : "Overrides existing transition to " + oldSucc;
    }

    /** Returns the number of this state. */
    public int getNumber() {
        return this.number;
    }

    /** Returns the label successor map of this state. */
    public Map<Direction,Map<TypeLabel,DFAState>> getLabelMap() {
        return this.labelSuccMap;
    }

    @Override
    public String toString() {
        return "a" + getNumber();
    }

    private final int number;
    /** Flag indicating if this is the unique initial state. */
    private final boolean initial;
    /** Flag indicating if this is a final state. */
    private final boolean isFinal;
    /** The set of nodes corresponding to an automaton state. */
    private final Set<RegNode> nodes;
    /** Mapping per direction from outgoing labels to successors states. */
    private final Map<Direction,Map<TypeLabel,DFAState>> labelSuccMap =
        new EnumMap<>(Direction.class);
}
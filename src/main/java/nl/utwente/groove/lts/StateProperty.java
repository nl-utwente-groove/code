/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.lts;

import java.util.function.Predicate;

/**
 * Named predicate for a graph state.
 * @author Arend Rensink
 * @version $Revision$
 */
public abstract class StateProperty implements Predicate<GraphState>, Comparable<StateProperty> {
    /**
     * Constructs a predicate with a given name.
     */
    public StateProperty(String name) {
        this.name = name;
    }

    /** Returns the name of this predicate. */
    public String getName() {
        return this.name;
    }

    private final String name;

    @Override
    public int compareTo(StateProperty o) {
        return getName().compareTo(o.getName());
    }

    @Override
    abstract public boolean test(GraphState t);
}

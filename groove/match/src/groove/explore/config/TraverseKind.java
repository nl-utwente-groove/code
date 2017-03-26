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
package groove.explore.config;

/**
 * Selection strategy for the next state from the frontier.
 * Only used to select among states with the same heuristic value.
 * @author Arend Rensink
 * @version $Revision $
 */
public enum TraverseKind implements SettingKind<TraverseKind> {
    /** Depth-first selection. */
    NEWEST("Depth first", "Depth-first selection"),
    /** Breadth-first search. */
    OLDEST("Breadth first", "Breadth-first selection"),
    /** Random selection. */
    RANDOM("Random", "Random selection"),;

    private TraverseKind(String name, String explanation) {
        this.name = name;
        this.explanation = explanation;
    }

    /** Returns the name of this search order. */
    @Override
    public String getName() {
        return this.name;
    }

    private final String name;

    @Override
    public String getExplanation() {
        return this.explanation;
    }

    private final String explanation;

}

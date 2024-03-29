// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2023 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/**
 *
 */
package nl.utwente.groove.lts;

/**
 * Combination of a {@link GraphState} and a
 * {@link RuleTransition}.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface GraphNextState extends GraphState, RuleTransition {
    /* The default implementation from RuleTransition won't work here. */
    @Override
    public GTS getGTS();

    /**
     * Returns the (rule or recipe) transition
     * leading up to this state. The resulting transition is
     * internal if and only if this state is so.
     */
    public GraphTransition getInTransition();
}

/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.verify;

import groove.graph.AEdge;

import java.util.Set;

/**
 * @author Harmen Kastenberg
 * @version $Revision $
 */
public class BuchiTransition extends AEdge<BuchiLocation,BuchiLabel> {
    /**
     * Constructor for creating a new Buchi transition
     */
    public BuchiTransition(BuchiLocation source, BuchiLabel label, BuchiLocation target) {
        super(source, label, target);
    }

    @Override
    public boolean isSimple() {
        return true;
    }

    /**
     * Determines whether the transition is enabled based on the given set of names of applicable rules.
     *
     * @param satisfiedProps
     *          the set of propositions satisfied in a given state
     * @return <code>true</code> if the set of propositions enable the transition, <code>false</code> otherwise.
     */
    public boolean isEnabled(Set<Proposition> satisfiedProps) {
        return label().guard()
            .stream()
            .allMatch(a -> a.isNegated() == !satisfiedProps.stream()
                .anyMatch(r -> a.getAtom()
                    .matches(r)));
    }
}

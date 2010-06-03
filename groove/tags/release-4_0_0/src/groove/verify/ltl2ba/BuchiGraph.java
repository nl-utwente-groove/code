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
package groove.verify.ltl2ba;

import groove.verify.BuchiLocation;
import groove.verify.BuchiTransition;

import java.util.Set;

/**
 * @author Harmen Kastenberg
 * @version $Revision $
 */
public interface BuchiGraph {
    /**
     * Checks whether a given set of names of applicable rules enable a given
     * {@link groove.verify.DefaultBuchiTransition} .
     * 
     * @param transition the transition for which the enabledness must be
     *        checked
     * @param applicableRules the set of names of rule
     * @return <code>true</code> if the set of rule names enable the given
     *         transition, <code>false</code> otherwise
     */
    public boolean isEnabled(BuchiTransition transition,
            Set<String> applicableRules);

    /**
     * Create a {@link BuchiGraph} from the provided LTL formula.
     * 
     * @param formula the formula for which to create an equivalent
     *        {@link BuchiGraph}
     * @return the {@link BuchiGraph}
     */
    public BuchiGraph newBuchiGraph(String formula);

    /**
     * Returns the set of initial locations.
     * 
     * @return the set of initial locations
     */
    public Set<BuchiLocation> initialLocations();

    /**
     * Returns the set of accepting locations.
     * 
     * @return the set of accepting locations
     */
    public Set<BuchiLocation> acceptingLocations();

    /**
     * Add the provided Buechi location to the set of initial locations.
     * @param location
     * @return see {@link Set#add(Object)}
     */
    public boolean addInitialLocation(BuchiLocation location);

    /**
     * Add the provided Buechi location to the set of accepting locations.
     * @param location
     * @return see {@link Set#add(Object)}
     */
    public boolean addAcceptingLocation(BuchiLocation location);

    /**
     * @param transition
     * @return see {@link Set#add(Object)}
     */
    public boolean addTransition(BuchiTransition transition);
}

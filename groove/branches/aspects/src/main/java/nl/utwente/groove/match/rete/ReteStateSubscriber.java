/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package nl.utwente.groove.match.rete;

import nl.utwente.groove.match.rete.ReteNetwork.ReteState;

/**
 * Any object subscribing to a {@link ReteNetwork}'s state
 * for runtime data service should implement this interface. A {@link ReteState}
 * object communicates with its subscribers through this interface.
 *
 * @author Arash Jalali
 * @version $Revision $
 */
public interface ReteStateSubscriber {
    /**
     * This method instructs the subscriber to create new runtime data
     * structures anew.
     *
     * The subscriber is responsible for making sure the objects are actually
     * freshly created and not merely cleared.
     */
    default public void initialize() {
        // does nothing
    }

    /**
     * This method instructs the subscriber to clean up its existing runtime
     * data objects without creating new ones.
     *
     */
    public void clear();
}

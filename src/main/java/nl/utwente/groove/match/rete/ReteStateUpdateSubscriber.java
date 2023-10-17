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
 * $Id: ReteStateSubscriber.java 6072 2021-07-14 18:23:50Z rensink $
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
public interface ReteStateUpdateSubscriber extends ReteStateSubscriber {
    /**
     * This method tells the subscriber that a new round of updates
     * to the RETE network has just begun.
     */
    public void updateBegin();

    /**
     * This method tells the subscriber that all updates
     * have been delivered to the RETE network. However, it does
     * not necessarily mean that these updates
     * have fully propagated through the network.
     */
    public void updateEnd();
}

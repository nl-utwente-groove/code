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
package groove.explore.result;

import groove.lts.GraphTransition;

/**
 * A <code>TransitionCondition</code> describes a condition that can be
 * evaluated when a new transition is added to the LTS.
 * 
 * @author Maarten de Mol
 */
public interface TransitionCondition {

    /**
     * The evaluation of the condition for a newly created transition in the
     * LTS.
     */
    public boolean evalNewTransition(GraphTransition newTransition);
}

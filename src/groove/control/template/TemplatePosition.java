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
package groove.control.template;

import groove.control.CtrlVar;
import groove.control.Position;

import java.util.Map;

/**
 * Supertype for {@link Location} (which is a node of a {@link Template}) 
 * and {@link Deadlock} (which is not). This is so as to be able to keep the {@link Template}
 * graph "clean" by not having to include verdicts to deadlock locations (which
 * would otherwise be all over the place).
 * @author Arend Rensink
 * @version $Revision $
 */
public interface TemplatePosition extends Position<Location>,
        Comparable<TemplatePosition> {
    MultiSwitch getAttempt();

    /* Specialises the return type. */
    TemplatePosition onSuccess();

    /* Specialises the return type. */
    TemplatePosition onFailure();

    /**
     * Returns the first stage of this position,
     * with stage number {@code 0} and success status {@code false}.
     */
    Stage getFirstStage();

    /** Returns a mapping from variables to their indices for this position. */
    public Map<CtrlVar,Integer> getVarIxMap();

    /** 
     * Method to allow easy comparison of positions.
     * The number uniquely identifies the position within a template.
     */
    int getNumber();
}

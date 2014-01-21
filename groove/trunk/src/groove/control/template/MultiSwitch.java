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

import groove.control.MultiAttempt;

import java.util.ArrayList;
import java.util.List;

/**
 * Vector of switches.
 * @author Arend Rensink
 * @version $Revision $
 */
public class MultiSwitch extends MultiAttempt<Location,Switch> {
    /** Returns a single slot of this vector, with target set
     * to the first slot of the appropriate target location. */
    public StageSwitch getStage(int nr) {
        if (this.slots == null) {
            this.slots = new ArrayList<StageSwitch>();
            for (int i = 0; i < size(); i++) {
                this.slots.add(new StageSwitch(get(i), get(i).target().getStage(
                    0, false)));
            }
        }
        return this.slots.get(nr);
    }

    private List<StageSwitch> slots;
}

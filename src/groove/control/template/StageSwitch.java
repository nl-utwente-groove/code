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

import groove.control.Call;
import groove.control.SingleAttempt;
import groove.util.Pair;

/**
 * Switch to a location index.
 * @author Arend Rensink
 * @version $Revision $
 */
public class StageSwitch extends Pair<Switch,Stage> implements
        SingleAttempt<Stage> {
    /**
     * Constructs a switch index.
     */
    public StageSwitch(Switch edge, Stage target) {
        super(edge, target);
    }

    /** Returns the underlying switch. */
    public Switch getSwitch() {
        return one();
    }

    /** Returns the template of which the stage switch is part. */
    public Template getTemplate() {
        return getSwitch().source().getTemplate();
    }

    public Call getCall() {
        return getSwitch().getCall();
    }

    public Stage target() {
        return two();
    }
}

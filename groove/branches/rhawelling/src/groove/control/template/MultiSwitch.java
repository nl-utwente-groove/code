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
import groove.util.Duo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Vector of switches in combination with the success and failure alternates.
 * @author Arend Rensink
 * @version $Revision $
 */
public class MultiSwitch extends MultiAttempt<Location,Switch> {
    /** Constructs a multiswitch for a given source location. */
    public MultiSwitch(Location source, Location onSuccess, Location onFailure) {
        this.source = source;
        this.onSuccess = onSuccess;
        this.onFailure = onFailure;
    }

    /** Returns the source location of this multiswitch. */
    public Location source() {
        return this.source;
    }

    private final Location source;

    @Override
    public Location onFailure() {
        return this.onFailure;
    }

    private final Location onFailure;

    @Override
    public Location onSuccess() {
        return this.onSuccess;
    }

    private final Location onSuccess;

    /**
     * Derives a single stage of this multiswitch, with its target set
     * to the the appropriate target location.
     * @param caller caller of the derived switch
     */
    public Switch getStage(Switch caller, int nr, boolean success) {
        List<Duo<Switch>> stages = this.stageMap.get(caller);
        if (stages == null) {
            stages = new ArrayList<Duo<Switch>>();
            for (int i = 0; i < size(); i++) {
                stages.add(Duo.newDuo(computeSwitch(caller, i, true),
                    computeSwitch(caller, i, false)));
            }
            this.stageMap.put(caller, stages);
        }
        Duo<Switch> duo = stages.get(nr);
        return success ? duo.one() : duo.two();
    }

    private Switch computeSwitch(Switch caller, int i, boolean success) {
        Stage onFinish = get(i).target().getFirstStage(caller);
        boolean last = i == size() - 1;
        Stage onSuccess =
            last ? onSuccess().getFirstStage(caller) : source().getStage(caller, i + 1, true);
        Stage onFailure;
        if (sameVerdict()) {
            onFailure = onSuccess;
        } else {
            onFailure =
                last ? (success ? onSuccess() : onFailure()).getFirstStage(caller)
                        : source().getStage(caller, i + 1, success);
        }
        return new Switch(get(i), caller, onFinish, onSuccess, onFailure);
    }

    private final Map<Switch,List<Duo<Switch>>> stageMap = new HashMap<Switch,List<Duo<Switch>>>();
}

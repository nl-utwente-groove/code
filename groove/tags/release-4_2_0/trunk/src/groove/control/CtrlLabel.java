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
package groove.control;

import groove.graph.AbstractLabel;
import groove.util.Groove;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * A control label wraps a control call and a guard, consisting of a 
 * set of failure calls.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlLabel extends AbstractLabel {
    /** 
     * Constructs a control label from a call, with an empty guard.
     */
    public CtrlLabel(CtrlCall call) {
        this(call, Collections.<CtrlCall>emptyList());
    }

    /** 
     * Constructs a control label from a call and
     * a guard.
     */
    public CtrlLabel(CtrlCall call, Collection<CtrlCall> guard) {
        this.call = call;
        for (CtrlCall guardCall : guard) {
            this.guardMap.put(guardCall.getName(), guardCall);
        }
    }

    @Override
    public String text() {
        StringBuilder result = new StringBuilder();
        if (!this.guardMap.isEmpty()) {
            result.append(Groove.toString(this.guardMap.keySet().toArray()));
        }
        result.append(getCall().toString());
        return result.toString();
    }

    /** Returns the rule wrapped into this label. */
    public final CtrlCall getCall() {
        return this.call;
    }

    /** The rule call wrapped in this control label. */
    private final CtrlCall call;

    /** Returns the set of failure rules wrapped into this label. */
    public final Collection<CtrlCall> getGuard() {
        return this.guardMap.values();
    }

    /** Indicates if the guard contains a call with a given (rule or function) call. */
    public final boolean hasGuardCall(String name) {
        return this.guardMap.containsKey(name);
    }

    /** Returns the guarded call for a given (rule or function) name, if any. */
    public final CtrlCall getGuardCall(String name) {
        return this.guardMap.get(name);
    }

    /** Guard of this label, consisting of a list of failure rules. */
    private final Map<String,CtrlCall> guardMap =
        new TreeMap<String,CtrlCall>();
}

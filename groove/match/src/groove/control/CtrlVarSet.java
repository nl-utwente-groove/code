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
package groove.control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Convenience type for sorted sets of control variables.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlVarSet {
    /** Constructs an initially empty variable set. */
    public CtrlVarSet() {
        this.modified = new HashSet<>();
    }

    /** Creates a collection on the basis of a given sorted list. */
    public CtrlVarSet(List<CtrlVar> vars) {
        this.init = vars;
    }

    /** Returns the ordered list of all variables. */
    public List<CtrlVar> getAll() {
        List<CtrlVar> result = this.init;
        if (result == null) {
            result = new ArrayList<>(this.modified);
            Collections.sort(result);
        }
        return result;
    }

    /** Adds a set of variables to this set. */
    public boolean addAll(Collection<CtrlVar> vars) {
        if (this.modified == null && !vars.isEmpty()) {
            this.modified = new HashSet<>(this.init);
        }
        boolean changed = !vars.isEmpty() && this.modified.addAll(vars);
        if (changed) {
            this.init = null;
        }
        return changed;
    }

    /** Removes a set of variables from this set. */
    public boolean removeAll(Collection<CtrlVar> vars) {
        if (this.modified == null && !vars.isEmpty()) {
            this.modified = new HashSet<>(this.init);
        }
        boolean changed = !vars.isEmpty() && this.modified.removeAll(vars);
        if (changed) {
            this.init = null;
        }
        return changed;
    }

    /**
     * The list with which this set was initialised;
     * as long as no variables are added or removed, this list is retained.
     */
    private List<CtrlVar> init;
    /** A clone of #init, used to calculate a modified set. */
    private HashSet<CtrlVar> modified;
}

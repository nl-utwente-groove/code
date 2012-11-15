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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Set of control transitions forming a guard for another transition.
 * All transitions in the guard must have failed before the guarded transition
 * is enabled.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlGuard extends TreeSet<CtrlTransition> implements
        Comparable<CtrlGuard> {
    /**
     * Constructs a new guard by applying a mapping to all elements of this one.
     * If the map is {@code null}, merely clones this guard.
     */
    public CtrlGuard newGuard(Map<CtrlTransition,CtrlTransition> map) {
        CtrlGuard result = new CtrlGuard();
        for (CtrlTransition key : this) {
            CtrlTransition image = map == null ? key : map.get(key);
            if (image != null) {
                result.add(image);
            }
        }
        return result;
    }

    @Override
    public boolean add(CtrlTransition e) {
        assert e != null;
        return super.add(e);
    }

    @Override
    public int compareTo(CtrlGuard o) {
        int result = size() - o.size();
        if (result == 0) {
            Iterator<CtrlTransition> myIter = iterator();
            Iterator<CtrlTransition> hisIter = o.iterator();
            while (result == 0 && myIter.hasNext()) {
                result = myIter.next().compareTo(hisIter.next());
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CtrlGuard)) {
            return false;
        }
        CtrlGuard other = (CtrlGuard) obj;
        if (size() != other.size()) {
            return false;
        }
        Set<CtrlLabel> otherLabels = new HashSet<CtrlLabel>();
        for (CtrlTransition trans : other) {
            otherLabels.add(trans.label());
        }
        for (CtrlTransition trans : this) {
            if (!otherLabels.contains(trans.label())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (CtrlTransition trans : this) {
            result += trans.label().hashCode();
        }
        return result;
    }

}

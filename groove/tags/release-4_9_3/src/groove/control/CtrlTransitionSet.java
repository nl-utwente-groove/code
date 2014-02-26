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

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Convenience type for sorted sets of control transitions from a common source state.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlTransitionSet extends TreeSet<CtrlTransition> {
    /** Creates a fresh empty set. */
    public CtrlTransitionSet() {
        super();
    }

    /** Copies a given transition set. */
    public CtrlTransitionSet(SortedSet<CtrlTransition> s) {
        super(s);
    }

    /** Copies a given transition set. */
    public CtrlTransitionSet(Collection<? extends CtrlTransition> s) {
        super(s);
    }
}

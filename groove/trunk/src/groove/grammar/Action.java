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
package groove.grammar;

import groove.control.Callable;
import groove.control.CtrlEdge.Kind;

import java.util.Comparator;

/**
 * Supertype of the actions in a rule system. 
 * @author Arend Rensink
 * @version $Revision $
 */
public interface Action extends Callable, Comparable<Action> {
    /** 
     * Returns the action kind of this action.
     * @return the action kind; can only be {@link Kind#RECIPE} or {@link Kind#RULE}.
     */
    public Kind getKind();

    /**
     * A comparator for priorities, encoded as {@link Integer} objects. This
     * implementation orders priorities from high to low.
     */
    public static final Comparator<Integer> PRIORITY_COMPARATOR =
        new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                return o2.intValue() - o1.intValue();
            }

        };

    /**
     * A comparator for actions. This
     * implementation orders priorities from high to low, and within priority,
     * according to the action (full) name.
     */
    public static final Comparator<Action> ACTION_COMPARATOR =
        new Comparator<Action>() {
            public int compare(Action o1, Action o2) {
                if (o1 == o2) {
                    return 0;
                }
                int result = o2.getPriority() - o1.getPriority();
                if (result == 0) {
                    result = o1.getFullName().compareTo(o2.getFullName());
                }
                return result;
            }

        };

    /**
     * The lowest rule priority, which is also the default value if no explicit
     * priority is given.
     */
    public static final int DEFAULT_PRIORITY = 0;
}

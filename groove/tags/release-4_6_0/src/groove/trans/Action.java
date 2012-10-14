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
package groove.trans;

import groove.control.CtrlPar.Var;

import java.util.Comparator;
import java.util.List;

/**
 * Supertype of the actions in a rule system. 
 * @author Arend Rensink
 * @version $Revision $
 */
public interface Action extends Comparable<Action> {
    /** Returns the full (qualified) name of the action. */
    public String getFullName();

    /** 
     * Returns the last part of the action name.
     */
    public String getLastName();

    /**
     * Returns the priority of the action.
     */
    public int getPriority();

    /** Returns the signature of the action. */
    public List<Var> getSignature();

    /** Returns the action kind of this action. */
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

    /** Enumeration of the kind of actions. */
    public static enum Kind {
        /** Recipe kind, represented by {@link Recipe}.*/
        RECIPE,
        /** Recipe kind, represented by {@link Recipe}.*/
        RULE;

        /** Returns the lower case version of the name. */
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
}

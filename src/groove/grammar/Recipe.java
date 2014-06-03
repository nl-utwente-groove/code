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

import groove.control.CtrlFrame;
import groove.control.CtrlPar;
import groove.control.Procedure;
import groove.control.template.Switch.Kind;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class wrapping a transaction.
 * A transaction consists of a control automaton and an associated priority.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Recipe extends Procedure implements Action {
    /** Constructs a recipe from a control automaton, given a priority. */
    public Recipe(String fullName, int priority, List<CtrlPar.Var> signature, String controlName,
            int startLine) {
        super(fullName, Kind.RECIPE, priority, signature, controlName, startLine);
    }

    /**
     * Returns the set of rules called by this recipe.
     * May be {@code null} if the recipe body is not set or contains errors.
     */
    public Set<Rule> getRules() {
        Set<Rule> result = this.rules;
        if (result == null) {
            if (CtrlFrame.NEW_CONTROL) {
                result = new HashSet<Rule>();
                for (Action action : getTemplate().getActions()) {
                    if (action instanceof Rule) {
                        result.add((Rule) action);
                    }
                }
            } else {
                if (getBody() != null) {
                    result = this.rules = getBody().getRules();
                }
            }
        }
        return result;
    }

    /**
     * Compares two actions on the basis of their names.
     */
    @Override
    public int compareTo(Action other) {
        return getFullName().compareTo(other.getFullName());
    }

    private Set<Rule> rules;
}

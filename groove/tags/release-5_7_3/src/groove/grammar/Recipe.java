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

import java.util.HashSet;
import java.util.Set;

import groove.control.Procedure;

/**
 * Class wrapping a transaction.
 * A transaction consists of a control automaton and an associated priority.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Recipe extends Procedure implements Action {
    /** Constructs a recipe. */
    public Recipe(QualName fullName, int priority, Signature<UnitPar.ProcedurePar> signature,
        QualName controlName, int startLine, GrammarProperties grammarProperties) {
        super(fullName, Kind.RECIPE, signature, controlName, startLine, grammarProperties);
        this.priority = priority;
    }

    /**
     * Returns the set of rules called by this recipe.
     * May be {@code null} if the recipe body is not set or contains errors.
     */
    public Set<Rule> getRules() {
        Set<Rule> result = this.rules;
        if (result == null) {
            result = new HashSet<>();
            if (getTemplate() != null) {
                for (Action action : getTemplate().getActions()) {
                    if (action instanceof Rule) {
                        result.add((Rule) action);
                    }
                }
            }
            this.rules = result;
        }
        return result;
    }

    private Set<Rule> rules;

    @Override
    public Role getRole() {
        return Role.TRANSFORMER;
    }

    @Override
    public boolean isProperty() {
        return getRole().isProperty();
    }

    @Override
    public CheckPolicy getPolicy() {
        return null;
    }

    @Override
    public boolean isPartial() {
        return false;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    private final int priority;

    @Override
    public String getTransitionLabel() {
        return getQualName().toString();
    }

    @Override
    public String getFormatString() {
        return "";
    }

    /**
     * Compares two actions on the basis of their names.
     */
    @Override
    public int compareTo(Action other) {
        return getQualName().compareTo(other.getQualName());
    }
}

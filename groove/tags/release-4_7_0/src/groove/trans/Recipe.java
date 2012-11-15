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

import groove.control.CtrlAut;
import groove.control.CtrlPar;
import groove.control.CtrlPar.Var;

import java.util.List;
import java.util.Set;

/**
 * Class wrapping a transaction.
 * A transaction consists of a control automaton and an associated priority.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Recipe implements Action {
    /** Constructs a recipe from a control automaton, given a priority. */
    public Recipe(String name, int priority, List<CtrlPar.Var> sig,
            String controlName, int startLine) {
        this.name = name;
        this.priority = priority;
        this.sig = sig;
        this.controlName = controlName;
        this.startLine = startLine;
    }

    @Override
    public String getFullName() {
        return this.name;
    }

    @Override
    public String getLastName() {
        return QualName.getLastName(getFullName());
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    /** Sets the body of the recipe. */
    public void setBody(CtrlAut body) {
        assert this.body == null : String.format(
            "Recipe body of %s already set to %s", this.name, body);
        this.body = body;
    }

    /** Returns the body of this recipe. */
    public CtrlAut getBody() {
        return this.body;
    }

    /** 
     * Returns the set of rules called by this recipe.
     * May be {@code null} if the recipe automaton is not set or contains errors.
     */
    public Set<Rule> getRules() {
        Set<Rule> result = this.rules;
        if (result == null && getBody() != null) {
            result = this.rules = getBody().getRules();
        }
        return result;
    }

    /** Returns the full name of the control program in which this recipe is declared. */
    public String getControlName() {
        return this.controlName;
    }

    /** Returns the start line of this recipe's declaration within the control program. */
    public int getStartLine() {
        return this.startLine;
    }

    @Override
    public List<Var> getSignature() {
        return this.sig;
    }

    @Override
    public Kind getKind() {
        return Kind.RECIPE;
    }

    /**
     * Compares two actions on the basis of their names.
     */
    public int compareTo(Action other) {
        return getFullName().compareTo(other.getFullName());
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Recipe)) {
            return false;
        }
        Recipe other = (Recipe) obj;
        if (!this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    private final String name;
    private final int priority;
    private final List<CtrlPar.Var> sig;
    private CtrlAut body;
    private Set<Rule> rules;
    private final String controlName;
    private final int startLine;
}

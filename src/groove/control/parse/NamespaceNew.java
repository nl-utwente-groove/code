/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: Namespace.java,v 1.4 2008-02-05 13:27:53 rensink Exp $
 */
package groove.control.parse;

import groove.trans.Rule;
import groove.trans.RuleName;
import groove.trans.SPORule;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Namespace for building a control automaton.
 * The namespace holds the function and rule names,
 * as well as local variable declarations.
 * @author Arend Rensink
 * @version $Revision $
 */
public class NamespaceNew {
    /**
     * Adds a function name to the set of declared functions.
     * @return {@code true} if the function name is new.
     */
    public boolean addFunction(String name) {
        return this.functions.add(name);
    }

    /** Checks if a given function has been declared. */
    public boolean hasFunction(String name) {
        return this.functions.contains(name);
    }

    /**
     * Tests if there is a rule with a given name.
     */
    public boolean hasRule(String name) {
        return this.ruleNames.contains(name);
    }

    /**
     * Returns the rule associated with a given rule name
     */
    public SPORule getRule(String name) {
        debug("trying to get rule: " + name + ", currently "
            + this.ruleMap.size() + " rules known");
        return this.ruleMap.get(name);
    }

    /** Initialises the rule names of this name space from a given grammar view. */
    public void setRuleNames(Set<RuleName> ruleNames) {
        for (RuleName rule : ruleNames) {
            this.ruleNames.add(rule.text());
        }
    }

    /**
     * Sets the rules so that the control program may know about them
     * @param rules a collection of the rules used in this grammar
     */
    public void setRules(Collection<Rule> rules) {
        for (Rule r : rules) {
            debug(" == adding rule: " + r.getName().toString());
            this.ruleMap.put(r.getName().toString(), (SPORule) r);
        }
    }

    /**
     * Returns whether this program uses variables.
     * @return true if variables are being used, false if not
     */
    public boolean usesVariables() {
        return !this.variables.isEmpty();
    }

    /** 
     * Adds a variable name from the control program to the list of 
     * variable names. 
     */
    public void addVariable(String name) {
        this.variables.put(name, false);
    }

    /**
     * Marks a variable as initialized.
     */
    public boolean initializeVariable(String name) {
        if (hasVariable(name)) {
            this.variables.put(name, true);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Tests if there is a variable with a given name 
     */
    public boolean hasVariable(String name) {
        return this.variables.containsKey(name);
    }

    /**
     * Tests if the variable with the given name is initialized.
     */
    public boolean isInitialized(String name) {
        if (hasVariable(name)) {
            return this.variables.get(name);
        } else {
            return false;
        }
    }

    /**
     * Returns the set of rule names associated with this name space. Only
     * returns a value different from <code>null</code> if the rule names have
     * been initialised using {@link #setRuleNames(Set)}.
     */
    public Set<String> getRuleNames() {
        return this.ruleNames;
    }

    private void debug(String msg) {
        if (this.usesVariables()) {
            //System.err.println("Variables debug (NameSpace): "+msg);
        }
    }

    private final Set<String> ruleNames = new HashSet<String>();
    private final HashMap<String,SPORule> ruleMap =
        new HashMap<String,SPORule>();

    /** Set of declared functions. */
    private final Set<String> functions = new HashSet<String>();

    private final HashMap<String,Boolean> variables =
        new HashMap<String,Boolean>();
}

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

import groove.control.CtrlAut;
import groove.control.CtrlPar;
import groove.trans.SPORule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    public boolean addFunction(String name, List<CtrlPar.Var> sig) {
        boolean result = this.functions.add(name);
        if (result) {
            this.sigMap.put(name, sig);
        }
        return result;
    }

    /** Checks if a given function has been declared. */
    public boolean hasFunction(String name) {
        return this.functions.contains(name);
    }

    /**
     * Adds a function body to the namespace.
     * The function name should have been defined already. 
     */
    public void addFunctionBody(String name, CtrlAut body) {
        assert hasFunction(name) : String.format("Unknown function %s", name);
        this.functionMap.put(name, body);
    }

    /**
     * Adds a function body to the namespace.
     * The function body should have been inserted before. 
     */
    public CtrlAut getFunctionBody(String name) {
        CtrlAut result = this.functionMap.get(name);
        assert result != null : String.format("Unknown function %s", name);
        return result;
    }

    /**
     * Tests if there is a rule with a given name.
     */
    public boolean hasRule(String name) {
        return this.ruleMap.containsKey(name);
    }

    /**
     * Returns the rule with a given name.
     */
    public SPORule getRule(String name) {
        return this.ruleMap.get(name);
    }

    /**
     * Returns the rule associated with a given rule name
     */
    public SPORule useRule(String name) {
        SPORule result = this.ruleMap.get(name);
        this.usedRules.add(name);
        return result;
    }

    /**
     * Returns the set of all known rules.
     */
    public Set<String> getAllRules() {
        return this.ruleMap.keySet();
    }

    /** Returns the set of all used rules,
     * i.e., all rules for which {@link NamespaceNew#useRule(String)}
     * has been invoked.
     */
    public Set<String> getUsedRules() {
        return this.usedRules;
    }

    /**
     * Returns the signature associated with a given rule name
     */
    public List<CtrlPar.Var> getSig(String name) {
        return this.sigMap.get(name);
    }

    /**
     * Adds a rule to the name space.
     */
    public boolean addRule(SPORule rule) {
        String ruleName = rule.getName().text();
        this.ruleMap.put(ruleName, rule);
        List<CtrlPar.Var> oldSig =
            this.sigMap.put(ruleName, rule.getSignature());
        return oldSig == null;
    }

    /** Mapping from declared rules names to the rules. */
    private final Map<String,SPORule> ruleMap = new HashMap<String,SPORule>();
    /** Mapping from declared rule names to their signatures. */
    private final Map<String,List<CtrlPar.Var>> sigMap =
        new HashMap<String,List<CtrlPar.Var>>();

    /** Set of declared functions. */
    private final Set<String> functions = new HashSet<String>();
    /** Mapping from function names to their declared bodies. */
    private final Map<String,CtrlAut> functionMap =
        new HashMap<String,CtrlAut>();
    /** Set of used rules. */
    private final Set<String> usedRules = new HashSet<String>();
}

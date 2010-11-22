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

import groove.control.CtrlPar;
import groove.control.CtrlType;
import groove.control.CtrlVar;
import groove.trans.SPORule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
        return this.sigMap.containsKey(name);
    }

    /**
     * Returns the rule associated with a given rule name
     */
    public SPORule getRule(String name) {
        return this.ruleMap.get(name);
    }

    /**
     * Returns the signature associated with a given rule name
     */
    public List<CtrlPar> getSig(String name) {
        return this.sigMap.get(name);
    }

    /**
     * Adds a rule to the name space.
     */
    public boolean addRule(SPORule rule) {
        List<CtrlPar> sig = new ArrayList<CtrlPar>();
        for (int i = 0; i < rule.getNumberOfParameters(); i++) {
            String parName = "par" + i;
            CtrlType parType =
                CtrlType.createDataType(rule.getAttributeParameterType(i));
            CtrlVar var = new CtrlVar(parName, parType);
            CtrlPar par;
            boolean inOnly = !rule.isOutputParameter(i);
            boolean outOnly = !rule.isInputParameter(i);
            if (!inOnly && !outOnly) {
                par = new CtrlPar.Var(var);
            } else {
                par = new CtrlPar.Var(var, inOnly);
            }
            sig.add(par);
        }
        String ruleName = rule.getName().text();
        this.ruleMap.put(ruleName, rule);
        List<CtrlPar> oldSig = this.sigMap.put(ruleName, sig);
        return oldSig == null;
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
     * Marks a variable as initialised.
     */
    public boolean initialiseVariable(String name) {
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
     * Tests if the variable with the given name is initialised.
     */
    public boolean isInitialised(String name) {
        if (hasVariable(name)) {
            return this.variables.get(name);
        } else {
            return false;
        }
    }

    /** Mapping from declared rules names to the rules. */
    private final HashMap<String,SPORule> ruleMap =
        new HashMap<String,SPORule>();
    /** Mapping from declared rule names to their signatures. */
    private final HashMap<String,List<CtrlPar>> sigMap =
        new HashMap<String,List<CtrlPar>>();

    /** Set of declared functions. */
    private final Set<String> functions = new HashSet<String>();

    private final HashMap<String,Boolean> variables =
        new HashMap<String,Boolean>();
}

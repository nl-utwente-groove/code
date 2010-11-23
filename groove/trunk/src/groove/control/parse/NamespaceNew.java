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
import java.util.Collection;
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
     * Tests if there is a rule with a given name.
     */
    public boolean hasRule(String name) {
        return this.ruleMap.containsKey(name);
    }

    /**
     * Returns the rule associated with a given rule name
     */
    public SPORule useRule(String name) {
        SPORule result = this.ruleMap.get(name);
        this.usedRules.add(result);
        return result;
    }

    /**
     * Returns the set of all known rules.
     */
    public Collection<SPORule> getAllRules() {
        return this.ruleMap.values();
    }

    /** Returns the set of all used rules,
     * i.e., all rules for which {@link NamespaceNew#useRule(String)}
     * has been invoked.
     */
    public Set<SPORule> getUsedRules() {
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
        List<CtrlPar.Var> sig = new ArrayList<CtrlPar.Var>();
        for (int i = 0; i < rule.getNumberOfParameters(); i++) {
            String parName = "par" + i;
            String parTypeName = rule.getAttributeParameterType(i + 1);
            if (parTypeName == null) {
                parTypeName = CtrlType.NODE_TYPE_NAME;
            }
            CtrlType parType = CtrlType.createType(parTypeName);
            CtrlVar var = new CtrlVar(parName, parType);
            CtrlPar.Var par;
            boolean inOnly = !rule.isOutputParameter(i + 1);
            boolean outOnly = !rule.isInputParameter(i + 1);
            if (!inOnly && !outOnly) {
                par = new CtrlPar.Var(var);
            } else {
                par = new CtrlPar.Var(var, inOnly);
            }
            sig.add(par);
        }
        String ruleName = rule.getName().text();
        this.ruleMap.put(ruleName, rule);
        List<CtrlPar.Var> oldSig = this.sigMap.put(ruleName, sig);
        return oldSig == null;
    }

    /** Mapping from declared rules names to the rules. */
    private final HashMap<String,SPORule> ruleMap =
        new HashMap<String,SPORule>();
    /** Mapping from declared rule names to their signatures. */
    private final HashMap<String,List<CtrlPar.Var>> sigMap =
        new HashMap<String,List<CtrlPar.Var>>();

    /** Set of declared functions. */
    private final Set<String> functions = new HashSet<String>();
    /** Set of used rules. */
    private final Set<SPORule> usedRules = new HashSet<SPORule>();
}

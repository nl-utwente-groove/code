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
import groove.control.CtrlPar.Var;
import groove.trans.Rule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Namespace for building a control automaton.
 * The namespace holds the function, transaction and rule names.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Namespace {
    /**
     * Adds a function name to the set of declared functions.
     * @return {@code true} if the name is new.
     */
    public boolean addName(Kind kind, String name, List<CtrlPar.Var> sig) {
        boolean result = !this.kindMap.containsKey(name);
        if (result) {
            this.kindMap.put(name, kind);
            this.sigMap.put(name, sig);
            if (kind == Kind.ACTION) {
                this.allRules.add(name);
            }
        }
        return result;
    }

    /** Checks if a given name has been declared. */
    public boolean hasName(String name) {
        return getKind(name) != null;
    }

    /**
     * Checks the kind of object a given name has been declared as.
     * @param name the name to be checked
     * @return the kind of object {@code name} has been declared as,
     * or {@code null} if {@code name} is unknown
     */
    public Kind getKind(String name) {
        return this.kindMap.get(name);
    }

    /**
     * Adds a function or transaction body to the namespace.
     * The function name should have been defined already. 
     */
    public void addBody(Kind kind, String name, CtrlAut body) {
        assert hasName(name) && getKind(name).hasBody() : String.format(
            "Unknown or inappropriate name %s", name);
        this.autMap.put(name, body);
        // the rules in a transaction body are no longer available
        if (kind == Kind.ACTION) {
            this.allRules.removeAll(body.getRules());
        }
    }

    /**
     * Adds a rule to the name space.
     */
    public boolean addRule(Rule rule) {
        String ruleName = rule.getName().toString();
        this.kindMap.put(ruleName, Kind.RULE);
        this.ruleMap.put(ruleName, rule);
        this.allRules.add(ruleName);
        List<Var> oldSig = this.sigMap.put(ruleName, rule.getSignature());
        return oldSig == null;
    }

    /**
     * Returns the automaton for a named function.
     */
    public CtrlAut getBody(String name) {
        assert hasName(name) && getKind(name).hasBody() : String.format(
            "Unknown or inappropriate name %s", name);
        CtrlAut result = this.autMap.get(name);
        assert result != null : String.format("Unknown function %s", name);
        return result;
    }

    /**
     * Returns the rule with a given name.
     */
    public Rule getRule(String name) {
        return this.ruleMap.get(name);
    }

    /**
     * Returns the rule associated with a given rule name
     */
    public Rule useRule(String name) {
        Rule result = this.ruleMap.get(name);
        this.usedRules.add(name);
        return result;
    }

    /**
     * Returns the set of all known rules.
     * These consist of the graph rules and the transactions.
     */
    public Set<String> getAllRules() {
        return this.allRules;
    }

    /** Returns the set of all used rules,
     * i.e., all rules for which {@link Namespace#useRule(String)}
     * has been invoked.
     */
    public Set<String> getUsedRules() {
        return this.usedRules;
    }

    /**
     * Returns the signature associated with a given rule name
     */
    public List<Var> getSig(String name) {
        return this.sigMap.get(name);
    }

    /** Mapping from declared names to their signatures. */
    private final Map<String,List<Var>> sigMap =
        new HashMap<String,List<Var>>();
    /** Mapping from declared names to their kinds. */
    private final Map<String,Kind> kindMap = new HashMap<String,Kind>();
    /** Mapping from declared rules names to the rules. */
    private final Map<String,Rule> ruleMap = new HashMap<String,Rule>();
    /** Mapping from names to their declared bodies. */
    private final Map<String,CtrlAut> autMap = new HashMap<String,CtrlAut>();
    /** Set of all rule names. */
    private final Set<String> allRules = new HashSet<String>();
    /** Set of used rule names. */
    private final Set<String> usedRules = new HashSet<String>();

    /** Kinds of names encountered in a control program. */
    public static enum Kind {
        /** Graph transformation rules. */
        RULE("rule"),
        /** Transactions (declared by {@code rule} blocks). */
        ACTION("transaction"),
        /** Functions (declared by {@code function} blocks). */
        FUNCTION("function");

        private Kind(String name) {
            this.name = name;
        }

        /** 
         * Indicates if this kind of name has an associated body
         * (translated to a control automaton).
         */
        public boolean hasBody() {
            return this != RULE;
        }

        /** 
         * Returns the description of this name kind,
         * with the initial letter optionally capitalised.
         */
        public String getName(boolean upper) {
            StringBuilder result = new StringBuilder(this.name);
            if (upper) {
                result.replace(0, 1,
                    "" + Character.toUpperCase(this.name.charAt(0)));
            }
            return result.toString();
        }

        private final String name;
    }
}

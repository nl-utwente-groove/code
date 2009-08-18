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

import groove.trans.NameLabel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.antlr.runtime.tree.CommonTree;

/**
 * Namespace class for the AutomatonBuilder (for checking). Can be used to store
 * names with an optional referenced Object.
 * 
 * @author Tom Staijen
 * @version $Revision $
 */
public class Namespace {
    /**
     * Stores the AST of a function.
     */
    public void store(String name, CommonTree ast) {
        this.procs.put(name, ast);
    }

    /**
     * Returns the AST for a function.
     */
    public CommonTree getProc(String name) {
        CommonTree result = this.procs.get(name);
        return result;
    }

    /**
     * Tests if a function with a given name exists.
     */
    public boolean hasProc(String name) {
        CommonTree result = this.procs.get(name);
        return result != null;
    }

    /**
     * Tests if there is a rule with a given name.
     */
    public boolean hasRule(String name) {
        return this.ruleNames.contains(name);
    }

    /** Initialises the rule names of this name space from a given grammar view. */
    public void setRuleNames(Set<NameLabel> ruleNames) {
        for (NameLabel rule : ruleNames) {
            this.ruleNames.add(rule.text());
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

    private final Set<String> ruleNames = new HashSet<String>();

    private final HashMap<String,CommonTree> procs =
        new HashMap<String,CommonTree>();
}

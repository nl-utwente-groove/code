/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.control;

import groove.graph.AbstractLabel;
import groove.trans.Rule;
import groove.trans.RuleSystem;
import groove.util.Groove;
import groove.view.FormatError;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A control label wraps a control call and a guard, consisting of a 
 * set of failure rules.
 * A label is <i>virtual</i> if the call and guard are only specified by
 * name, and <i>actual</i> if the rules are instantiated.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlLabel extends AbstractLabel {
    /** 
     * Constructs a virtual control label with an omega call and a
     * given set of guard names.
     */
    public CtrlLabel(Collection<String> guardNames) {
        this.call = CtrlCall.OMEGA;
        this.guard = null;
        this.guardNames = new LinkedHashSet<String>(guardNames);
    }

    /** 
     * Constructs a virtual control label from a rule name,
     * a set of call arguments, and a set of guard names.
     */
    public CtrlLabel(String ruleName, List<CtrlPar> args,
            Collection<String> guardNames) {
        this.call = new CtrlCall(ruleName, args);
        this.guard = null;
        this.guardNames = new LinkedHashSet<String>(guardNames);
    }

    /** 
     * Constructs an actual control label from a given rule call
     * and guard.
     */
    private CtrlLabel(CtrlCall call, Collection<Rule> guard) {
        this.call = call;
        this.guard = new LinkedHashSet<Rule>(guard);
        this.guardNames = new LinkedHashSet<String>();
        for (Rule guardRule : guard) {
            this.guardNames.add(guardRule.getName().text());
        }
    }

    @Override
    public String text() {
        StringBuilder result = new StringBuilder();
        if (!this.guardNames.isEmpty()) {
            result.append(Groove.toString(this.guardNames.toArray(), "[", "]",
                ","));
        }
        result.append(getCall().toString());
        return result.toString();
    }

    /** Returns the rule wrapped into this label. */
    public final CtrlCall getCall() {
        return this.call;
    }

    /** The rule call wrapped in this control label. */
    private final CtrlCall call;

    /** Returns the set of failure rules names wrapped into this label. */
    public final Set<String> getGuardNames() {
        return this.guardNames;
    }

    /** Guard of this label, consisting of a list of failure rules. */
    private final Set<String> guardNames;

    /** Returns the set of failure rules wrapped into this label. */
    public final Set<Rule> getGuard() {
        return this.guard;
    }

    /** Guard of this label, consisting of a list of failure rules. */
    private final Set<Rule> guard;

    /** 
     * Returns an actual label based on this (virtual) label and a given rule 
     * system.
     * @throws FormatException if this call's rule name or one of the guard 
     * names does not occur in the rule system,
     * or the arguments of this call are not compatible with the rule parameters.  
     */
    public CtrlLabel instantiate(RuleSystem grammar) throws FormatException {
        List<FormatError> errors = new ArrayList<FormatError>();
        Collection<Rule> guard = new LinkedHashSet<Rule>();
        for (String guardName : getGuardNames()) {
            Rule guardRule = grammar.getRule(guardName);
            if (guardRule == null) {
                errors.add(new FormatError(
                    "Failure rule '%s' does not occur in grammar", guardName));
            } else {
                guard.add(guardRule);
            }
        }
        CtrlCall call = null;
        try {
            call = getCall().instantiate(grammar);
        } catch (FormatException exc) {
            errors.addAll(exc.getErrors());
        }
        if (errors.isEmpty()) {
            return new CtrlLabel(call, guard);
        } else {
            throw new FormatException(errors);
        }
    }
}

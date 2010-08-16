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
import groove.util.Groove;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlLabel extends AbstractLabel {
    /** Constructs a dummy label. */
    private CtrlLabel() {
        this.rule = null;
        this.parameters = null;
        this.guard = null;
        this.ruleGuard = null;
    }

    /** Constructs a control label from a (non-{@code null}) rule. */
    public CtrlLabel(Rule rule, List<CtrlArg> pars, Collection<Rule> ruleGuard) {
        this.rule = rule;
        this.parameters =
            pars == null ? Collections.<CtrlArg>emptyList()
                    : new ArrayList<CtrlArg>(pars);
        this.ruleGuard =
            ruleGuard == null ? Collections.<Rule>emptySet()
                    : new LinkedHashSet<Rule>(ruleGuard);
        this.guard = new LinkedHashSet<String>();
        for (Rule guardRule : ruleGuard) {
            this.guard.add(guardRule.getName().text());
        }
    }

    @Override
    public String text() {
        if (this.rule == null) {
            return "";
        } else {
            StringBuilder result =
                new StringBuilder(this.rule.getName().toString());
            if (!this.guard.isEmpty()) {
                result.insert(0, Groove.toString(this.guard.toArray(), "[",
                    "]", ","));
            }
            if (!this.parameters.isEmpty()) {
                result.insert(0, Groove.toString(this.parameters.toArray(),
                    "(", ")", ","));
            }
        }
        return null;
    }

    /** Returns the rule wrapped into this label. */
    public final Rule getRule() {
        return this.rule;
    }

    /** Returns the list of parameters wrapped into this label. */
    public final List<CtrlArg> getParameters() {
        return this.parameters;
    }

    /** Returns the set of failure rules names wrapped into this label. */
    public final Set<String> getGuard() {
        return this.guard;
    }

    /** Returns the set of failure rules wrapped into this label. */
    public final Set<Rule> getRuleGuard() {
        return this.ruleGuard;
    }

    /** The rule wrapped in this control label. */
    private final Rule rule;
    /** Parameters of this label. */
    private final List<CtrlArg> parameters;
    /** Guard of this label, consisting of a list of failure rules. */
    private final Set<String> guard;
    /** Guard of this label, consisting of a list of failure rules. */
    private final Set<Rule> ruleGuard;

    /** Dummy label, to be used as long as a real label cannot be constructed. */
    public final static CtrlLabel DUMMY_LABEL = new CtrlLabel();
}

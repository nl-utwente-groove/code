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
import groove.util.Fixable;
import groove.util.Groove;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlLabel extends AbstractLabel implements Fixable {
    /** Constructs a dummy label. */
    private CtrlLabel() {
        this.rule = null;
        try {
            setFixed();
        } catch (FormatException e) {
            // can't happen
        }
    }

    /** Constructs a control label from a (non-{@code null}) rule. */
    public CtrlLabel(Rule rule) {
        this.rule = rule;
    }

    /** Sets the guard of this label to a collection of failure rules. */
    public void setGuard(Collection<Rule> guard) {
        testFixed(false);
        this.guard.addAll(guard);
    }

    /** Sets the parameters of this label to a list of control parameters. */
    public void setParameters(List<CtrlPar> parameters) {
        testFixed(false);
        this.parameters = new ArrayList<CtrlPar>(parameters);
    }

    @Override
    public String text() {
        testFixed(true);
        if (this.rule == null) {
            return "";
        } else {
            StringBuilder result =
                new StringBuilder(this.rule.getName().toString());
            if (!this.parameters.isEmpty()) {
                result.insert(0, Groove.toString(this.parameters.toArray(),
                    "(", ")", ","));
            }
        }
        return null;
    }

    @Override
    public boolean isFixed() {
        return this.fixed;
    }

    @Override
    public void setFixed() throws FormatException {
        testFixed(false);
        if (this.guard == null) {
            this.guard = Collections.emptyList();
        }
        if (this.parameters == null) {
            this.parameters = Collections.emptyList();
        }
        this.fixed = true;
    }

    @Override
    public void testFixed(boolean fixed) {
        if (fixed != this.fixed) {
            throw new IllegalStateException(String.format(
                "Illegal manipulation is %s control label", this.fixed
                        ? "fixed" : "unfixed"));
        }
    }

    /** Flag storing whether the label has been fixed. */
    private boolean fixed;

    /** The rule wrapped in this control label. */
    private final Rule rule;
    /** Guard of this label, consisting of a list of failed rules. */
    private List<Rule> guard;
    /** Guard of this label, consisting of a list of failed rules. */
    private List<CtrlPar> parameters;

    /** Dummy label, to be used as long as a real label cannot be constructed. */
    public final static CtrlLabel DUMMY_LABEL = new CtrlLabel();
}

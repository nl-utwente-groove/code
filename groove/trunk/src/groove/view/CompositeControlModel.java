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
package groove.view;

import static groove.trans.ResourceKind.CONTROL;
import groove.control.CtrlAut;
import groove.control.CtrlLoader;
import groove.trans.Action;
import groove.trans.ResourceKind;
import groove.trans.Rule;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * Model combining all enabled control programs.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CompositeControlModel extends ResourceModel<CtrlAut> {
    /** Constructs an instance for a given grammar model. */
    CompositeControlModel(GrammarModel grammar) {
        super(grammar, ResourceKind.CONTROL, "control");
    }

    @Override
    public Object getSource() {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    CtrlAut compute() throws FormatException {
        FormatErrorSet errors = createErrors();
        this.actionMap.clear();
        for (Rule rule : getGrammar().getRules()) {
            this.actionMap.put(rule.getFullName(), rule);
        }
        this.loader.init(getGrammar().getProperties().getAlgebraFamily(),
            getGrammar().getRules());
        for (String controlName : getGrammar().getActiveNames(CONTROL)) {
            ControlModel controlModel =
                getGrammar().getControlModel(controlName);
            if (controlModel == null) {
                errors.add("Control program '%s' cannot be found", controlName);
            } else {
                try {
                    this.loader.parse(controlName, controlModel.getProgram());
                } catch (FormatException exc) {
                    for (FormatError error : exc.getErrors()) {
                        errors.add("Error in control program '%s': %s",
                            controlName, error, controlModel);
                    }
                }
            }
        }
        errors.throwException();
        return this.loader.getAutomaton();
    }

    /** Returns the set of all top-level actions of the enabled control programs. */
    public Collection<Action> getActions() {
        synchronise();
        return this.loader.getActions();
    }

    /** Returns the control loader used in this composite control model. */
    public CtrlLoader getLoader() {
        return this.loader;
    }

    /** Mapping from recipe names to recipes defined in the enabled control programs. */
    private final Map<String,Action> actionMap = new TreeMap<String,Action>();
    private final CtrlLoader loader = new CtrlLoader();
}

/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
 * $Id$
 */
package nl.utwente.groove.grammar.model;

import java.util.Collections;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.control.CtrlLoader;
import nl.utwente.groove.control.parse.Namespace;
import nl.utwente.groove.control.template.Program;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Factory;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Bridge between control program texts and control program.
 * @author Arend Rensink
 */
@NonNullByDefault
public class ControlModel extends TextBasedModel<Program> {
    /**
     * Constructs a control model from a given control program.
     * @param grammar the grammar model to which this control view belongs; non-{@code null}
     * @param name the name of the control program; non-{@code null}
     * @param program the control program text; non-{@code null}
     */
    public ControlModel(GrammarModel grammar, QualName name, String program) {
        super(grammar, ResourceKind.CONTROL, name, program);
        addDependencies(ResourceKind.RULE, ResourceKind.TYPE);
    }

    @Override
    public Program compute() throws FormatException {
        Program result;
        if (isActive()) {
            CompositeControlModel model = getGrammar().getControlModel();
            if (model.hasErrors()) {
                model.getPartErrors(getQualName()).throwException();
                // there were errors in the composite model but not in this particular part
                new FormatErrorSet("The control model cannot be built due to the following errors:")
                    .addAll(model.getErrors())
                    .throwException();
                throw Exceptions.unreachable();
            } else {
                result = model.getProgram();
            }
        } else {
            // this program is disabled and therefore checked in isolation;
            // register all sibling programs and unavailable rules as invisible,
            // to allow more informative error messages for calls and imports; see gh #560
            var grammar = getGrammar();
            var activeControlNames = grammar.getActiveNames(ResourceKind.CONTROL);
            for (QualName name : grammar.getNames(ResourceKind.CONTROL)) {
                if (!name.equals(getQualName())) {
                    ControlModel sibling = grammar.getControlModel(name);
                    if (sibling != null) {
                        var reason = activeControlNames.contains(name)
                            ? Namespace.InvisibleDecl.Reason.ISOLATED
                            : Namespace.InvisibleDecl.Reason.DISABLED;
                        getLoader().addInvisibleControl(name, sibling.getProgram(), reason);
                    }
                }
            }
            getLoader()
                .addInvisibleRules(grammar.getNames(ResourceKind.RULE),
                                   grammar.getActiveNames(ResourceKind.RULE));
            getLoader().addControl(getQualName(), getProgram()).check();
            result = getLoader().buildProgram(Collections.singleton(getQualName()));
        }
        return result;
    }

    /** Returns the control loader used in this control model. */
    public CtrlLoader getLoader() {
        return this.loader.get();
    }

    @Override
    void notifyWillRebuild() {
        this.loader.reset();
        super.notifyWillRebuild();
    }

    /** The control parser. */
    private Factory<CtrlLoader> loader
        = Factory.lazy(() -> new CtrlLoader(getGrammar().getProperties(), getGrammar().getRules()));
}

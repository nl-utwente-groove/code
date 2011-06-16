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
 * $Id: ControlView.java,v 1.10 2008-03-18 12:17:29 fladder Exp $
 */
package groove.view;

import groove.trans.ResourceKind;

import java.util.Collections;
import java.util.List;

/**
 * View for prolog programs (which are just strings).
 * @author Arend Rensink
 */
public class PrologModel extends TextBasedModel<String> {
    /**
     * Constructs a prolog view from a given prolog program.
     * @param name the name of the prolog program; non-{@code null}
     * @param program the prolog program; non-null
     */
    public PrologModel(GrammarModel grammar, String name, String program) {
        super(grammar, ResourceKind.PROLOG, name, program);
        this.errors = Collections.emptyList();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toResource() throws FormatException {
        if (hasErrors()) {
            throw new FormatException(getErrors());
        } else {
            return getProgram();
        }
    }

    /** Clears the errors in this view. */
    public void clearErrors() {
        this.errors = Collections.emptyList();
    }

    /** Sets the errors in this view to a given list. */
    public void setErrors(List<FormatError> errors) {
        this.errors = errors;
    }

    /** Returns the list of errors in this view. */
    @Override
    public List<FormatError> getErrors() {
        return this.errors;
    }

    /** List of Prolog formatting errors in this program. */
    private List<FormatError> errors;
}

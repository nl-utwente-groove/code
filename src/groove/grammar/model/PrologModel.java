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
 * $Id$
 */
package groove.grammar.model;

import groove.grammar.QualName;
import groove.util.parse.FormatErrorSet;
import groove.util.parse.FormatException;

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
    public PrologModel(GrammarModel grammar, QualName name, String program) {
        super(grammar, ResourceKind.PROLOG, name, program);
        this.externalErrors = new FormatErrorSet();
    }

    /** Clears the errors in this view. */
    public void clearErrors() {
        this.externalErrors.clear();
    }

    /** Sets the errors in this view to a given list. */
    public void setErrors(FormatErrorSet errors) {
        this.externalErrors = errors;
    }

    @Override
    String compute() throws FormatException {
        this.externalErrors.throwException();
        return getProgram();
    }

    /** List of Prolog formatting errors in this program. */
    private FormatErrorSet externalErrors;
}

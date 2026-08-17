/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.prolog;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.grammar.model.ResourceValidator;
import nl.utwente.groove.util.AIGenerated;

/**
 * Validator for the prolog programs of a grammar, contributed to the grammar
 * model through the {@link java.util.ServiceLoader} (declared in
 * {@code META-INF/services} and {@code module-info.java}). Validation loads
 * the active programs into a fresh {@link GrooveEnvironment}, which records
 * parse errors on the individual {@code PrologModel}s.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public class PrologValidator implements ResourceValidator {
    /** Public constructor, as required for service loading. */
    public PrologValidator() {
        // empty
    }

    @Override
    public ResourceKind getKind() {
        return ResourceKind.PROLOG;
    }

    @Override
    public void validate(GrammarModel grammar) {
        GrooveEnvironment.ofGrammar(grammar);
    }
}

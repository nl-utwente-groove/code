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
package nl.utwente.groove.grammar.model;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.util.AIGenerated;

/**
 * Validator for grammar resources whose content syntax is owned by a layer
 * above the grammar model (such as the prolog programs). Validators are
 * contributed through the {@link java.util.ServiceLoader} (see
 * {@link ResourceValidators}) and invoked whenever the grammar is rebuilt;
 * they parse the content of the active resources of their kind and record any
 * errors on the individual resource models.
 * <p>
 * Implementations must have a public no-argument constructor, and be declared
 * both in
 * {@code META-INF/services/nl.utwente.groove.grammar.model.ResourceValidator}
 * (for class-path runs, in particular the installed application) and in a
 * {@code provides} clause of {@code module-info.java} (for module-path runs).
 * Unlike {@link SettingsSchema.Provider}, no provider indirection is needed:
 * validators are stateless and carry no singleton identity.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public interface ResourceValidator {
    /** Returns the resource kind validated by this validator. */
    public ResourceKind getKind();

    /**
     * Validates the active resources of this validator's kind in a given
     * grammar model, recording any errors on the individual resource models.
     */
    public void validate(GrammarModel grammar);
}

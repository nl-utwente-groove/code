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

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.util.AIGenerated;

/**
 * Registry of the {@link ResourceValidator}s contributed through the
 * {@link ServiceLoader}, in service-declaration order.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public class ResourceValidators {
    private ResourceValidators() {
        // no instances of this utility class
    }

    /** Applies every registered validator to a given grammar model. */
    static public void validate(GrammarModel grammar) {
        validators.forEach(v -> v.validate(grammar));
    }

    /** The registered validators, in service-declaration order. */
    static private final List<ResourceValidator> validators = new ArrayList<>();

    static {
        ServiceLoader.load(ResourceValidator.class).forEach(validators::add);
    }
}

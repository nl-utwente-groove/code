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
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatError;

/**
 * Pair of a resource kind and a resource name, identifying a grammar resource.
 * Used in particular as context parameter for a {@link FormatError}, from
 * which {@link ErrorLocation} retrieves it.
 * (Formerly the {@code Resource} record nested in {@link FormatError}.)
 * @author Arend Rensink
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public record ResourceId(ResourceKind kind, QualName name) {
    /** Constructs a control resource id from a given name. */
    public static ResourceId control(QualName name) {
        return new ResourceId(ResourceKind.CONTROL, name);
    }
}

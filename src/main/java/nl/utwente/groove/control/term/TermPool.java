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
package nl.utwente.groove.control.term;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.control.Procedure;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.collect.Pool;

/**
 * Pool of terms shared by all terms compiled against one name space,
 * which also records the bodies of the procedures declared in that name space.
 * The bodies are kept here rather than in the {@link Procedure}s themselves,
 * so that the procedures do not expose the term level of the control compiler.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Opus 5.5, 2026-09")
class TermPool extends Pool<Term> {
    /** Registers the body of a procedure. */
    void putBody(Procedure proc, Term body) {
        this.bodies.put(proc, body);
    }

    /** Returns the registered body of a procedure, if any. */
    @Nullable
    Term getBody(Procedure proc) {
        return this.bodies.get(proc);
    }

    /** Mapping from procedures to their bodies; only used for lookups, never iterated. */
    private final Map<Procedure,@Nullable Term> bodies = new HashMap<>();
}

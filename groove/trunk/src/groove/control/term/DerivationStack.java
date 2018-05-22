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
package groove.control.term;

import java.util.ArrayDeque;

import groove.control.CallStack;

/**
 * Stack of derivations; the bottom (first) element is the original caller.
 * @author Arend Rensink
 * @version $Revision $
 */
public class DerivationStack extends ArrayDeque<Derivation> {
    /**
     * Constructs a stack of derivations, from a given bottom-level
     * derivation.
     * The bottom-level derivation will become the first element of
     * the stack, its nested derivation (if any) the second, etc.
     */
    public DerivationStack(Derivation bottom) {
        add(bottom);
        if (bottom.hasNested()) {
            addAll(bottom.getNested()
                .getStack());
        }
    }

    /** Returns the call stack corresponding to this derivation stack. */
    public CallStack getCallStack() {
        if (this.callStack == null) {
            this.callStack = new CallStack(this.stream()
                .map(d -> d.getCall()));
        }
        return this.callStack;
    }

    private CallStack callStack;
}

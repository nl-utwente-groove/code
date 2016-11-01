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
package groove.match;

import java.util.Collections;

import groove.algebra.Constant;
import groove.grammar.Condition;
import groove.grammar.rule.VariableNode;

/**
 * Oracle that always returns an empty range of values.
 * @author Arend Rensink
 * @version $Revision $
 */
public class NoValueOracle implements ValueOracle {
    @Override
    public Iterable<Constant> getValues(Condition condition, VariableNode var) {
        return Collections.emptyList();
    }

    @Override
    public Kind getKind() {
        return Kind.NONE;
    }

    /** Returns the singleton instance of this class. */
    public final static NoValueOracle instance() {
        if (INSTANCE == null) {
            INSTANCE = new NoValueOracle();
        }
        return INSTANCE;
    }

    private static NoValueOracle INSTANCE;
}

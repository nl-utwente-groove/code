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
package groove.transform.oracle;

import org.eclipse.jdt.annotation.NonNullByDefault;

import groove.grammar.GrammarProperties;
import groove.util.parse.FormatException;

/**
 * Factory to create a value oracle for a given GTS.
 * @author Arend Rensink
 * @version $Revision $
 */
@NonNullByDefault
public interface ValueOracleFactory {
    /** Creates an oracle for a grammar with given properties.
     * @throws FormatException if the oracle is not compatible with the grammar properties. */
    public ValueOracle instance(GrammarProperties properties) throws FormatException;

    /** Returns the kind of value oracle this factory produces. */
    public ValueOracleKind getKind();
}

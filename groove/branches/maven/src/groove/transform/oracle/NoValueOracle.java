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
import org.eclipse.jdt.annotation.Nullable;

import groove.algebra.Constant;
import groove.grammar.GrammarProperties;
import groove.grammar.UnitPar.RulePar;
import groove.grammar.host.HostGraph;
import groove.transform.RuleEvent;
import groove.util.parse.FormatException;

/**
 * Oracle that always throws an exception.
 * @author Arend Rensink
 * @version $Revision $
 */
@NonNullByDefault
public class NoValueOracle implements ValueOracleFactory, ValueOracle {
    @Override
    public NoValueOracle instance(GrammarProperties properties) {
        return instance();
    }

    @Override
    public Constant getValue(HostGraph host, RuleEvent event, RulePar par) throws FormatException {
        throw new FormatException("No value");
    }

    @Override
    public ValueOracleKind getKind() {
        return ValueOracleKind.NONE;
    }

    /** Returns the singleton instance of this class. */
    public final static NoValueOracle instance() {
        NoValueOracle result = INSTANCE;
        if (result == null) {
            result = INSTANCE = new NoValueOracle();
        }
        return result;
    }

    private static @Nullable NoValueOracle INSTANCE;
}

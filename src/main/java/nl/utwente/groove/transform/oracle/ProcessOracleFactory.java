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
package nl.utwente.groove.transform.oracle;

import nl.utwente.groove.grammar.GrammarProperties;
import nl.utwente.groove.util.parse.FormatException;
import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Oracle starting a process to fetch values.
 * @author Mark Boom
 * @version $Revision$
 */
@NonNullByDefault
public class ProcessOracleFactory implements ValueOracleFactory {

    private final String command;

    public ProcessOracleFactory(String command) {
        this.command = command;
    }

    @Override
    public ValueOracle instance(GrammarProperties properties) throws FormatException {
        return new ProcessOracle(command);
    }

    @Override
    public ValueOracleKind getKind() {
        return ValueOracleKind.PROCESS;
    }
}

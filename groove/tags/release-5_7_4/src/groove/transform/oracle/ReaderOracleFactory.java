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
 * $Id: RandomValueOracle.java 5905 2017-04-18 15:39:30Z rensink $
 */
package groove.transform.oracle;

import org.eclipse.jdt.annotation.NonNullByDefault;

import groove.grammar.GrammarProperties;
import groove.util.parse.FormatException;

/**
 * Oracle returning values from a file reader.
 * @author Arend Rensink
 * @version $Revision $
 */
@NonNullByDefault
public class ReaderOracleFactory implements ValueOracleFactory {
    /** Constructor for a file reader to be created for a given filename. */
    public ReaderOracleFactory(String filename) {
        this.filename = filename;
    }

    @Override
    public ValueOracle instance(GrammarProperties properties) throws FormatException {
        return new ReaderOracle(properties, this.filename);
    }

    /**
     * Returns the filename of this oracle.
     */
    public String getFilename() {
        return this.filename;
    }

    private final String filename;

    @Override
    public ValueOracleKind getKind() {
        return ValueOracleKind.READER;
    }
}

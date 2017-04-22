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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import groove.algebra.Constant;
import groove.algebra.Sort;
import groove.grammar.UnitPar.RulePar;
import groove.grammar.host.HostGraph;
import groove.transform.RuleEvent;
import groove.util.parse.FormatException;

/** Oracle returning a single random value for the appropriate type. */
public class ReaderValueOracle implements ValueOracle {
    /** Constructor for a file reader to be created for a given filename.
     * @throws FileNotFoundException if the filename does not correspond to an existing file. */
    @SuppressWarnings("resource")
    public ReaderValueOracle(String filename) throws FileNotFoundException {
        this(new FileReader(filename));
    }

    /** Constructor for a predefined reader. */
    private ReaderValueOracle(Reader reader) {
        this.reader = new BufferedReader(reader);
    }

    private final BufferedReader reader;

    @Override
    public Constant getValue(HostGraph host, RuleEvent event, RulePar par) throws FormatException {
        Sort sort = par.getType()
            .getSort();
        try {
            String input = this.reader.readLine();
            if (input == null) {
                this.reader.close();
                throw new FormatException("End of file reached while reading values");
            }
            return sort.createConstant(input);
        } catch (IOException exc) {
            throw new FormatException("Can't read next value: %s", exc.getMessage());
        }
    }

    @Override
    public Kind getKind() {
        return Kind.READER;
    }
}

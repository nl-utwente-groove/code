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
package groove.grammar;

import groove.io.HTMLConverter;
import groove.match.DefaultValueOracle;
import groove.match.NoValueOracle;
import groove.match.RandomValueOracle;
import groove.match.ValueOracle;
import groove.util.Exceptions;
import groove.util.parse.FormatException;
import groove.util.parse.Parser;

/**
 * Parser for the {@code valueOracle} grammar property.
 * @author Arend Rensink
 * @version $Revision $
 */
public class OracleParser implements Parser<ValueOracle> {
    /** Private constructor for the singleton instance. */
    private OracleParser() {
        // empty
    }

    @Override
    public String getDescription() {
        if (this.description == null) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("One of");
            boolean first = true;
            for (ValueOracle.Kind kind : ValueOracle.Kind.values()) {
                buffer.append(first ? ": " : ", ");
                buffer.append(HTMLConverter.ITALIC_TAG.on(kind.name()
                    .toLowerCase()));
                if (first) {
                    buffer.append(" (default)");
                    first = false;
                }
            }
            this.description = buffer.toString();
        }
        return this.description;
    }

    private String description;

    @Override
    public ValueOracle parse(String input) throws FormatException {
        ValueOracle result = null;
        if (input == null || input.length() == 0) {
            result = createOracle(ValueOracle.Kind.NONE);
        } else {
            for (ValueOracle.Kind kind : ValueOracle.Kind.values()) {
                if (input.equals(kind.name()
                    .toLowerCase())) {
                    result = createOracle(kind);
                    break;
                }
            }
            if (result == null) {
                throw new FormatException("%s is not a valid oracle kind", input);
            }
        }
        return result;
    }

    /** Returns an oracle of the desired kind. */
    private ValueOracle createOracle(ValueOracle.Kind kind) {
        switch (kind) {
        case DEFAULT:
            return DefaultValueOracle.instance();
        case DIALOG:

        case NONE:
            return NoValueOracle.instance();
        case RANDOM:
            return RandomValueOracle.instance();
        default:
            throw Exceptions.UNREACHABLE;
        }
    }

    @Override
    public String toParsableString(Object value) {
        ValueOracle.Kind result = null;
        Class<? extends ValueOracle> oracle = ((ValueOracle) value).getClass();
        if (oracle == NoValueOracle.class) {
            result = ValueOracle.Kind.NONE;
        } else if (oracle == DefaultValueOracle.class) {
            result = ValueOracle.Kind.DEFAULT;
        } else if (oracle == RandomValueOracle.class) {
            result = ValueOracle.Kind.RANDOM;
        } else {
            throw Exceptions.UNREACHABLE;
        }
        return result.name()
            .toLowerCase();
    }

    @Override
    public Class<? extends ValueOracle> getValueType() {
        return ValueOracle.class;
    }

    /** Returns the singleton instance of this parser. */
    public static OracleParser instance() {
        if (INSTANCE == null) {
            INSTANCE = new OracleParser();
        }
        return INSTANCE;
    }

    @Override
    public ValueOracle getDefaultValue() throws UnsupportedOperationException {
        return NoValueOracle.instance();
    }

    private static OracleParser INSTANCE;
}

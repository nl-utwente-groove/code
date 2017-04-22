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

import java.io.FileNotFoundException;
import java.util.Arrays;

import groove.io.HTMLConverter;
import groove.transform.oracle.DefaultValueOracle;
import groove.transform.oracle.DialogValueOracle;
import groove.transform.oracle.NoValueOracle;
import groove.transform.oracle.RandomValueOracle;
import groove.transform.oracle.ReaderValueOracle;
import groove.transform.oracle.ValueOracle;
import groove.transform.oracle.ValueOracle.Kind;
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
            for (Kind kind : Kind.values()) {
                buffer.append(first ? ": " : ", ");
                buffer.append(HTMLConverter.ITALIC_TAG.on(kind.getName()));
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
        ValueOracle result;
        if (input == null || input.length() == 0) {
            result = createOracle(Kind.NONE, null);
        } else {
            FormatException exc =
                new FormatException("%s is not a valid oracle specification", input);
            Kind kind = Arrays.stream(Kind.values())
                .filter(k -> input.startsWith(k.getName()))
                .findAny()
                .orElseThrow(() -> exc);
            String par;
            if (input.equals(kind.getName())) {
                par = null;
            } else {
                int colon = input.indexOf(':');
                if (colon != kind.getName()
                    .length()) {
                    throw exc;
                }
                par = input.substring(colon + 1);
            }
            try {
                result = createOracle(kind, par);
            } catch (FormatException inner) {
                throw new FormatException("Error in oracle specifcation '%s': %s", input, inner);
            }
        }
        return result;
    }

    /** Returns an oracle of the desired kind. */
    private ValueOracle createOracle(Kind kind, String par) throws FormatException {
        FormatException exc = new FormatException("Unexpected parameter '%s'", par);
        switch (kind) {
        case DEFAULT:
            if (par != null) {
                throw exc;
            }
            return DefaultValueOracle.instance();
        case DIALOG:
            if (par != null) {
                throw exc;
            }
            return DialogValueOracle.instance();
        case NONE:
            if (par != null) {
                throw exc;
            }
            return NoValueOracle.instance();
        case RANDOM:
            if (par == null) {
                return RandomValueOracle.instance();
            } else {
                try {
                    long seed = Long.parseLong(par);
                    return RandomValueOracle.instance(seed);
                } catch (NumberFormatException number) {
                    throw new FormatException("Seed '%s' should be long value", par);
                }
            }
        case READER:
            if (par == null) {
                throw new FormatException("Reader oracle should specify filename");
            }
            try {
                return new ReaderValueOracle(par);
            } catch (FileNotFoundException exc1) {
                throw new FormatException(exc1.getMessage());
            }
        default:
            throw Exceptions.UNREACHABLE;
        }
    }

    @Override
    public String toParsableString(Object value) {
        Kind result = null;
        Class<? extends ValueOracle> oracle = ((ValueOracle) value).getClass();
        if (oracle == NoValueOracle.class) {
            result = Kind.NONE;
        } else if (oracle == DefaultValueOracle.class) {
            result = Kind.DEFAULT;
        } else if (oracle == RandomValueOracle.class) {
            result = Kind.RANDOM;
        } else {
            throw Exceptions.UNREACHABLE;
        }
        return result.getName();
    }

    @Override
    public Class<? extends ValueOracle> getValueType() {
        return ValueOracle.class;
    }

    @Override
    public ValueOracle getDefaultValue() {
        return NoValueOracle.instance();
    }

    /** Returns the singleton instance of this parser. */
    public static OracleParser instance() {
        if (INSTANCE == null) {
            INSTANCE = new OracleParser();
        }
        return INSTANCE;
    }

    private static OracleParser INSTANCE;
}

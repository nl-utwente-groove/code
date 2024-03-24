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
package nl.utwente.groove.grammar;

import java.util.Arrays;

import nl.utwente.groove.io.HTMLConverter;
import nl.utwente.groove.transform.oracle.DefaultOracle;
import nl.utwente.groove.transform.oracle.DialogOracle;
import nl.utwente.groove.transform.oracle.NoValueOracle;
import nl.utwente.groove.transform.oracle.RandomOracleFactory;
import nl.utwente.groove.transform.oracle.ReaderOracleFactory;
import nl.utwente.groove.transform.oracle.ValueOracleFactory;
import nl.utwente.groove.transform.oracle.ValueOracleKind;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.util.parse.Parser;

/**
 * Parser for the {@code valueOracle} grammar property.
 * @author Arend Rensink
 * @version $Revision$
 */
public class OracleParser extends Parser.AParser<ValueOracleFactory> {
    /** Private constructor for the singleton instance. */
    private OracleParser() {
        super(null, NoValueOracle.instance());
    }

    /** Creates a description of the values of this parser. */
    @Override
    protected String createDescription() {
        StringBuilder result = new StringBuilder("One of");
        boolean first = true;
        for (ValueOracleKind kind : ValueOracleKind.values()) {
            result
                .append(first
                    ? ": "
                    : ", ");
            result.append(HTMLConverter.ITALIC_TAG.on(kind.getName()));
            if (first) {
                result.append(" (default)");
                first = false;
            }
        }
        return result.toString();
    }

    @Override
    public ValueOracleFactory parse(String input) throws FormatException {
        ValueOracleFactory result;
        if (input.isEmpty()) {
            result = createOracle(ValueOracleKind.NONE, null);
        } else {
            FormatException exc
                = new FormatException("%s is not a valid oracle specification", input);
            ValueOracleKind kind = Arrays
                .stream(ValueOracleKind.values())
                .filter(k -> input.startsWith(k.getName()))
                .findAny()
                .orElseThrow(() -> exc);
            String par;
            if (input.equals(kind.getName())) {
                par = null;
            } else {
                int colon = input.indexOf(':');
                if (colon != kind.getName().length()) {
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
    private ValueOracleFactory createOracle(ValueOracleKind kind,
                                            String par) throws FormatException {
        FormatException exc = new FormatException("Unexpected parameter '%s'", par);
        switch (kind) {
        case DEFAULT:
            if (par != null) {
                throw exc;
            }
            return DefaultOracle.instance();
        case DIALOG:
            if (par != null) {
                throw exc;
            }
            return DialogOracle.instance();
        case NONE:
            if (par != null) {
                throw exc;
            }
            return NoValueOracle.instance();
        case RANDOM:
            if (par == null) {
                return RandomOracleFactory.instance();
            } else {
                try {
                    long seed = Long.parseLong(par);
                    return RandomOracleFactory.instance(seed);
                } catch (NumberFormatException number) {
                    throw new FormatException("Seed '%s' should be long value", par);
                }
            }
        case READER:
            if (par == null) {
                throw new FormatException("Reader oracle should specify filename");
            }
            return new ReaderOracleFactory(par);
        default:
            throw Exceptions.UNREACHABLE;
        }
    }

    @Override
    public <V extends ValueOracleFactory> String unparse(V value) {
        String result;
        if (value instanceof NoValueOracle) {
            result = ValueOracleKind.NONE.getName();
        } else if (value instanceof DefaultOracle) {
            result = ValueOracleKind.DEFAULT.getName();
        } else if (value instanceof RandomOracleFactory random) {
            result = ValueOracleKind.RANDOM + (random.hasSeed()
                ? ":" + random.getSeed()
                : "");
        } else if (value instanceof ReaderOracleFactory factory) {
            result = ValueOracleKind.READER + ":" + factory.getFilename();
        } else {
            throw Exceptions.UNREACHABLE;
        }
        return result;
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

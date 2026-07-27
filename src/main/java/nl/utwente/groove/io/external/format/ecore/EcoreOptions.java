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
package nl.utwente.groove.io.external.format.ecore;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.GrammarProperties;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Factory;
import nl.utwente.groove.util.Strings;
import nl.utwente.groove.util.parse.Parser;

/**
 * Encoding options of the Ecore porter.
 * The options are persisted as grammar properties; see
 * {@link GrammarProperties#getEcoreOrdering()} and
 * {@link GrammarProperties#isEcoreUseIdentifiers()}.
 * @param ordering the way in which ordered or non-unique many-valued features are encoded
 * @param useIdentifiers if {@code true}, {@code xmi:id} values are turned into
 * {@code id:} aspects of the host graph nodes
 * @author Arend Rensink
 */
@NonNullByDefault
public record EcoreOptions(Ordering ordering, boolean useIdentifiers) {
    /** Returns the options encoded in a given set of grammar properties. */
    public static EcoreOptions of(GrammarProperties properties) {
        return new EcoreOptions(properties.getEcoreOrdering(), properties.isEcoreUseIdentifiers());
    }

    /** Returns the options encoded in the properties of a given grammar model. */
    public static EcoreOptions of(GrammarModel grammar) {
        return of(grammar.getProperties());
    }

    /** The default options: no ordering encoding, identifiers in use. */
    public static EcoreOptions getDefault() {
        return DEFAULT;
    }

    private static final EcoreOptions DEFAULT = new EcoreOptions(Ordering.NONE, true);

    /** Encoding of the order of ordered or non-unique many-valued features. */
    public static enum Ordering {
        /** Many-valued features are encoded as plain edges; the order is lost. */
        NONE,
        /** Ordered or non-unique features are encoded through intermediate nodes
         * carrying an {@code index} attribute. */
        INDEX,;

        /** Returns the textual representation of this value, as used in the grammar properties. */
        public String text() {
            return Strings.toCamel(name());
        }

        /** Returns the value with a given textual representation.
         * @throws IllegalArgumentException if {@code text} is not the representation of any value
         */
        public static Ordering valueOfText(String text) throws IllegalArgumentException {
            var result = textMap.get().get(text);
            if (result == null) {
                throw Exceptions.illegalArg("Unknown Ecore ordering '%s'", text);
            }
            return result;
        }

        /** Tests if a given string is the textual representation of some value. */
        public static boolean hasText(@Nullable String text) {
            return textMap.get().containsKey(text);
        }

        /** Lazily computed mapping from textual representations to values. */
        private static final Factory<Map<String,@Nullable Ordering>> textMap
            = Factory.lazy(Ordering::createTextMap);

        private static Map<String,@Nullable Ordering> createTextMap() {
            Map<String,@Nullable Ordering> result = new LinkedHashMap<>();
            Arrays.stream(values()).forEach(o -> result.put(o.text(), o));
            return result;
        }
    }

    /** Returns the parser for the {@link Ordering} values.
     * The parser delivers the values as strings, so that the corresponding
     * grammar key can be of value type {@link Parser}-compatible {@code STRING}.
     */
    public static Parser<String> orderingParser() {
        return ORDERING_PARSER;
    }

    /** Parser for {@link Ordering} values, delivered as their textual representations. */
    private static class OrderingParser extends Parser.Wrap<String> {
        OrderingParser() {
            super(new Parser.EnumParser<>(Ordering.class, Ordering.NONE), String.class,
                  Ordering::text, Ordering::valueOfText);
        }

        @Override
        public boolean isValid(String value) {
            return Ordering.hasText(value);
        }
    }

    private static final Parser<String> ORDERING_PARSER = new OrderingParser();
}

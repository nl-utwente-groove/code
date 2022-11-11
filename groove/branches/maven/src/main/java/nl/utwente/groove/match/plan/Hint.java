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
package nl.utwente.groove.match.plan;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.util.parse.Parser.AParser;
import nl.utwente.groove.util.parse.StringHandler;

/** Hint for a search plan, consisting of a set of commonly occurring and a set of rare labels. */
public record Hint(@NonNull List<String> common, @NonNull List<String> rare) {
    /** Constructs an empty hint. */
    public Hint() {
        this(Collections.emptyList(), Collections.emptyList());
    }

    /** Indicates if there are any rare labels in this hint. */
    public boolean hasRare() {
        return !rare().isEmpty();
    }

    /** Indicates if there are any common labels in this hint. */
    public boolean hasCommon() {
        return !common().isEmpty();
    }

    /** Parser for {@link Hint} objects. */
    static public class Parser extends AParser<Hint> {
        private Parser() {
            super("Either empty, or a comma-separated pair of strings <i>rare,common</i>"
                + "where <i>rare</i> and <i>common</i> are quoted, space-separated lists of labels",
                  new Hint());
        }

        @Override
        public Hint parse(@Nullable String input) throws FormatException {
            Hint result;
            if (input == null || input.isEmpty()) {
                result = getDefaultValue();
            } else {
                String[] split = exprParser.split(input, ",");
                if (split.length == 2) {
                    String rare = toUnquoted(split[0]);
                    String common = toUnquoted(split[1]);
                    List<String> rareList = Arrays.asList(exprParser.split(rare, " "));
                    List<String> commonList = Arrays.asList(exprParser.split(common, " "));
                    result = new Hint(rareList, commonList);
                } else {
                    throw new FormatException(
                        "Match hint should be comma-separated pair of label lists");
                }
            }
            return result;
        }

        private String toUnquoted(String text) throws FormatException {
            text = text.trim();
            if (text.length() < 2) {
                throw new FormatException("Label list '%s' should be double-quoted string", text);
            }
            if (text.charAt(0) != StringHandler.DOUBLE_QUOTE_CHAR) {
                throw new FormatException("Label list '%s' should be double-quoted string", text);
            }
            return StringHandler.toUnquoted(text, StringHandler.DOUBLE_QUOTE_CHAR);
        }

        @Override
        public <V extends nl.utwente.groove.match.plan.Hint> String unparse(V value) {
            if (!value.hasRare() && !value.hasCommon()) {
                return "";
            } else {
                String rareString = Groove.toString(value.rare().toArray(), "\"", "\"", " ");
                String commonString = Groove.toString(value.common().toArray(), "\"", "\"", " ");
                return rareString + "," + commonString;
            }
        }

        /** Returns the singleton instance of this parser. */
        static final public Parser instance() {
            return INSTANCE;
        }

        static private final Parser INSTANCE = new Parser();

        static final StringHandler exprParser = new StringHandler("\'");
    }
}
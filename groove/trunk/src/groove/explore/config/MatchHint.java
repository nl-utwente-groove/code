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
package groove.explore.config;

import static groove.util.parse.StringHandler.DOUBLE_QUOTE_CHAR;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import groove.util.Duo;
import groove.util.Groove;
import groove.util.parse.FormatException;
import groove.util.parse.Parser;
import groove.util.parse.StringHandler;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class MatchHint extends Duo<List<String>> {
    /**
     * Constructs an empty match hint.
     */
    public MatchHint() {
        super(Collections.<String>emptyList(), Collections.<String>emptyList());
    }

    /** Constructs a hint from lists of common and control labels. */
    public MatchHint(List<String> common, List<String> control) {
        super(common, control);
    }

    /** Parser for match hints. */
    public static final Parser<MatchHint> PARSER = new Parser<MatchHint>() {
        @Override
        public String getDescription() {
            return "Either empty, or a comma-separated pair of strings <i>rare,common</i>"
                + "where <i>rare</i> and <i>common</i> are quoted, space-separated lists of labels";
        }

        @Override
        public MatchHint parse(String input) throws FormatException {
            MatchHint result;
            if (input == null || input.isEmpty()) {
                result = getDefaultValue();
            } else {
                String[] split = exprParser.split(input, ",");
                if (split.length == 2) {
                    String rare = toUnquoted(split[0]);
                    String common = toUnquoted(split[1]);
                    List<String> rareList = Arrays.asList(exprParser.split(rare, " "));
                    List<String> commonList = Arrays.asList(exprParser.split(common, " "));
                    result = new MatchHint(rareList, commonList);
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
            if (text.charAt(0) != DOUBLE_QUOTE_CHAR) {
                throw new FormatException("Label list '%s' should be double-quoted string", text);
            }
            return StringHandler.toUnquoted(text, DOUBLE_QUOTE_CHAR);
        }

        @Override
        public String toParsableString(Object value) {
            MatchHint hint = (MatchHint) value;
            List<String> rare = hint.one();
            List<String> common = hint.two();
            if (rare.isEmpty() && common.isEmpty()) {
                return "";
            } else {
                String rareString = Groove.toString(rare.toArray(), "\"", "\"", " ");
                String commonString = Groove.toString(common.toArray(), "\"", "\"", " ");
                return rareString + "," + commonString;
            }
        }

        @Override
        public Class<MatchHint> getValueType() {
            return MatchHint.class;
        }

        @Override
        public MatchHint getDefaultValue() {
            return this.defaultValue;
        }

        private final MatchHint defaultValue = new MatchHint();
    };

    private static final StringHandler exprParser = new StringHandler("\'");
}

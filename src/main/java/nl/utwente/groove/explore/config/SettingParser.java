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
package nl.utwente.groove.explore.config;

import nl.utwente.groove.io.HTMLConverter;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Strings;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.util.parse.IdValidator;
import nl.utwente.groove.util.parse.Parser;

/**
 * Parser for settings of a given exploration key.
 * @author Arend Rensink
 * @version $Revision $
 */
public class SettingParser extends Parser.AParser<Setting> {
    /**
     * Constructs a parser for settings of a given key.
     */
    public SettingParser(ExploreKey key) {
        super(createDescription(key), key.getDefaultKind().getDefaultValue());
        this.key = key;
        this.kindMap = key.getKindMap();
    }

    /** Returns the exploration key of this setting parser. */
    private ExploreKey getKey() {
        return this.key;
    }

    private final ExploreKey key;

    /** Returns the kind corresponding to a given kind name, converted to lower case. */
    private Setting.Key getKind(String name) {
        return this.kindMap.get(name.toLowerCase());
    }

    private final SettingKeyMap kindMap;

    /** Creates a description of values expected for a given exploration key. */
    static private String createDescription(ExploreKey key) {
        StringBuilder result = new StringBuilder("<body>");
        if (key.isSingular()) {
            result.append("A value ");
        } else {
            result.append("One or more values ");
        }
        result
            .append("of the form <i>kind</i> <i>args</i> (without the space), where <i>kind</i> is one of");
        for (var k : key.getKindType().getEnumConstants()) {
            result.append("<li> - ");
            result.append(HTMLConverter.ITALIC_TAG.on(k.getName()));
            result.append(": ");
            result.append(k.getExplanation());
            result.append(", with <i>arg</i> ");
            result.append(Strings.toLower(k.parser().getDescription()));
        }
        return result.toString();
    }

    @Override
    public Setting parse(String input) throws FormatException {
        if (input.length() == 0) {
            return getDefaultValue();
        } else if (getKey().isSingular()) {
            return parseSingle(input);
        } else {
            throw Exceptions.unsupportedOp("Non-singular keys not yet implemented");
        }
    }

    /** Parses a string holding a single setting value. */
    public Setting parseSingle(String text) throws FormatException {
        SplitPair splitText = split(text);
        String name = splitText.name();
        Setting.Key kind = getKind(name);
        if (kind == null) {
            if (name.isEmpty()) {
                throw new FormatException("Value '%s' should start with setting kind", text);
            } else {
                throw new FormatException("Unknown setting kind '%s' in '%s'", name, text);
            }
        }
        Object content = kind.parser().parse(splitText.content());
        return kind.createSetting(content);
    }

    /**
     * Splits a string into (potential) kind name and (potential) content part.
     * The string is split at the first occurrence of {@link #CONTENT_SEPARATOR}.
     * If there is no {@link #CONTENT_SEPARATOR} and {@code input}
     * is an identifier, {@code input} is assumed to be a name with empty content;
     * otherwise, it is assumed to be content for an empty (default) name.
     */
    private SplitPair split(String input) {
        SplitPair result;
        int pos = input.indexOf(CONTENT_SEPARATOR);
        if (pos < 0) {
            if (IdValidator.GROOVE_ID.isValid(input)) {
                result = new SplitPair(input, "");
            } else {
                result = new SplitPair("", input);
            }
        } else {
            result = new SplitPair(input.substring(0, pos), input.substring(pos + 1));
        }
        return result;
    }

    @Override
    public <V extends Setting> String unparse(V value) {
        String result = "";
        if (!isDefault(value)) {
            StringBuilder builder = new StringBuilder();
            Setting.Key kind = value.key();
            String kindName = kind.getName().toLowerCase();
            String contentString = kind.parser().unparse(value);
            if (contentString.isEmpty()) {
                builder.append(kindName);
            } else if (getKind("") == kind) {
                builder.append(contentString);
            } else {
                builder.append(kindName);
                builder.append(CONTENT_SEPARATOR);
                builder.append(contentString);
            }
            result = builder.toString();
        }
        return result;
    }

    @Override
    public boolean isValue(Object value) {
        boolean result = value instanceof Setting;
        if (result) {
            Setting setting = (Setting) value;
            result = setting.key().isValue(setting);
        }
        return result;
    }

    /** Separator between kind name and (optional) setting content. */
    private static final char CONTENT_SEPARATOR = ':';

    private record SplitPair(String name, String content) {
        // no additional functionality
    }
}

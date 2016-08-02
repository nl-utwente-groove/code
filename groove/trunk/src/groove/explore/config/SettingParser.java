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

import groove.io.HTMLConverter;
import groove.util.Duo;
import groove.util.parse.FormatException;
import groove.util.parse.Parser;
import groove.util.parse.StringHandler;

/**
 * Parser for settings of a given exploration key.
 * @author Arend Rensink
 * @version $Revision $
 */
public class SettingParser implements Parser<Setting<?,?>> {
    /**
     * Constructs a parser for settings of a given key.
     */
    public SettingParser(ExploreKey key) {
        this.key = key;
        this.kindMap = key.getKindMap();
    }

    /** Returns the exploration key of this setting parser. */
    private ExploreKey getKey() {
        return this.key;
    }

    private final ExploreKey key;

    /** Returns the kind corresponding to a given kind name, converted to lower case. */
    private SettingKey getKind(String name) {
        return this.kindMap.get(name.toLowerCase());
    }

    private final KindMap kindMap;

    @Override
    public String getDescription() {
        StringBuilder result = new StringBuilder("<body>");
        if (getKey().isSingular()) {
            result.append("A value ");
        } else {
            result.append("One or more values ");
        }
        result.append(
            "of the form <i>kind</i> <i>args</i> (without the space), where <i>kind</i> is one of");
        for (SettingKey key : getKey().getKindType()
            .getEnumConstants()) {
            result.append("<li> - ");
            result.append(HTMLConverter.ITALIC_TAG.on(key.getName()));
            result.append(": ");
            result.append(key.getExplanation());
            result.append(", with <i>arg</i> ");
            result.append(StringHandler.toLower(key.parser()
                .getDescription()));
        }
        return result.toString();
    }

    @Override
    public Setting<?,?> parse(String input) throws FormatException {
        if (input == null || input.length() == 0) {
            return getDefaultValue();
        } else if (getKey().isSingular()) {
            return parseSingle(input);
        } else {
            throw new UnsupportedOperationException("Non-singular keys not yet implemented");
        }
    }

    /** Parses a string holding a single setting value. */
    public Setting<?,?> parseSingle(String text) throws FormatException {
        Duo<String> splitText = split(text);
        String name = splitText.one();
        SettingKey kind = getKind(name);
        if (kind == null) {
            if (name.isEmpty()) {
                throw new FormatException("Value '%s' should start with setting kind", text);
            } else {
                throw new FormatException("Unknown setting kind '%s' in '%s'", name, text);
            }
        }
        Object content = kind.parser()
            .parse(splitText.two());
        return kind.createSetting(content);
    }

    /**
     * Splits a string into (potential) kind name and (potential) content part.
     * The string is split at the first occurrence of {@link #CONTENT_SEPARATOR}.
     * If there is no {@link #CONTENT_SEPARATOR} and {@code input}
     * is an identifier, {@code input} is assumed to be a name with empty content;
     * otherwise, it is assumed to be content for an empty (default) name.
     */
    private Duo<String> split(String input) {
        Duo<String> result;
        int pos = input.indexOf(CONTENT_SEPARATOR);
        if (pos < 0) {
            if (StringHandler.isIdentifier(input)) {
                result = Duo.newDuo(input, "");
            } else {
                result = Duo.newDuo("", input);
            }
        } else {
            result = Duo.newDuo(input.substring(0, pos), input.substring(pos + 1));
        }
        return result;
    }

    @Override
    public String toParsableString(Object value) {
        String result = "";
        Setting<?,?> setting = (Setting<?,?>) value;
        if (!isDefault(value)) {
            StringBuilder builder = new StringBuilder();
            SettingKey kind = setting.getKind();
            String kindName = kind.getName()
                .toLowerCase();
            String contentString = kind.parser()
                .toParsableString(setting.getContent());
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

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Setting<?,?>> getValueType() {
        return (Class<? extends Setting<?,?>>) Setting.class;
    }

    @Override
    public boolean isValue(Object value) {
        boolean result = value instanceof Setting;
        if (result) {
            Setting<?,?> setting = (Setting<?,?>) value;
            result = setting.getKind()
                .isValue(setting.getContent());
        }
        return result;
    }

    @Override
    public Setting<?,?> getDefaultValue() {
        if (this.defaultValue == null) {
            SettingKey defaultKind = getKey().getDefaultKind();
            this.defaultValue = defaultKind.createSetting(defaultKind.getDefaultValue());
        }
        return this.defaultValue;
    }

    private Setting<?,?> defaultValue;

    @Override
    public String getDefaultString() {
        if (this.defaultString == null) {
            this.defaultString = toParsableString(getDefaultValue());
        }
        return this.defaultString;
    }

    private String defaultString;

    /** Separator between kind name and (optional) setting content. */
    private static final char CONTENT_SEPARATOR = ':';
}

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

import groove.grammar.model.FormatException;
import groove.io.HTMLConverter;
import groove.util.ExprParser;
import groove.util.Parser;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class SettingParser implements Parser<SettingList> {
    /**
     * Constructs a parser for explore values.
     */
    public SettingParser(SettingKey defaultKind, boolean multiple) {
        this.defaultKind = defaultKind;
        this.multiple = multiple;
        this.keyType = defaultKind.getClass();
        this.keyMap = new HashMap<String,SettingKey>();
        this.parserMap = new HashMap<SettingKey,Parser<SettingContent>>();
        for (SettingKey key : this.keyType.getEnumConstants()) {
            this.keyMap.put(key.getName(), key);
            this.parserMap.put(key, new BracketParser<SettingContent>(key.parser()));
        }
        assert this.keyMap.size() > 0;
        this.uniqueKey = this.keyMap.size() == 1;

        this.defaultValue =
            multiple ? SettingList.multiple()
                : SettingList.single(defaultKind.createSetting(defaultKind.getDefaultValue()));
        if (this.uniqueKey) {
            this.defaultString = defaultKind.parser().getDefaultString();
        } else {
            this.defaultString = defaultKind.getName() + getParser(defaultKind).getDefaultString();
        }
    }

    private final SettingKey defaultKind;
    private final Class<? extends SettingKey> keyType;
    /** Flag indicating that the key type has only a single value. */
    private final boolean uniqueKey;

    private SettingKey getKey(String name) {
        return this.keyMap.get(name);
    }

    private final Map<String,SettingKey> keyMap;

    /** Returns the pre-initialised parser for a given setting kind. */
    private Parser<SettingContent> getParser(SettingKey kind) {
        return this.parserMap.get(kind);
    }

    private final Map<SettingKey,Parser<SettingContent>> parserMap;

    /** Indicates if this parser accepts multiple (whitespace-separated) setting values. */
    private boolean isMultiple() {
        return this.multiple;
    }

    private final boolean multiple;

    @Override
    public String getDescription(boolean uppercase) {
        if (this.uniqueKey) {
            return this.defaultKind.parser().getDescription(uppercase);
        } else {
            StringBuilder result = new StringBuilder("<body>");
            result.append(uppercase ? "A " : "a ");
            result.append("value of the form <i>kind</i> <i>args</i> (without the space), where <i>kind</i> is one of");
            for (SettingKey key : this.keyType.getEnumConstants()) {
                result.append("<li> - ");
                result.append(HTMLConverter.ITALIC_TAG.on(key.getName()));
                result.append(": ");
                result.append(key.getExplanation());
                result.append(", with <i>arg</i> ");
                result.append(getParser(key).getDescription(false));
            }
            return result.toString();
        }
    }

    @Override
    public boolean accepts(String text) {
        boolean result = false;
        if (text == null || text.length() == 0) {
            return true;
        } else if (isMultiple()) {
            result = true;
            try {
                String[] parts = exprParser.split(text, " ");
                for (int i = 0; i < parts.length; i++) {
                    if (!acceptsSingle(parts[i])) {
                        result = false;
                        break;
                    }
                }
            } catch (FormatException exc) {
                // the string contains unbalanced quotes or brackets
                result = false;
            }
        } else {
            result = acceptsSingle(text);
        }
        return result;
    }

    /** Tests if a given string represents a single setting value. */
    public boolean acceptsSingle(String text) {
        if (text == null || text.length() == 0) {
            return true;
        } else if (this.uniqueKey) {
            return this.defaultKind.parser().accepts(text);
        } else {
            String name = getNamePrefix(text);
            SettingKey key = getKey(name);
            return key == null ? false : getParser(key).accepts(text.substring(name.length()));
        }
    }

    /**
     * Returns the name prefix of a potential exploration value string.
     * This consists of the sequence of initial alphanumerical characters.
     */
    private String getNamePrefix(String text) {
        StringBuilder name = new StringBuilder();
        int i;
        char c;
        for (i = 0; i < text.length() && Character.isLetterOrDigit(c = text.charAt(i)); i++) {
            name.append(c);
        }
        return name.toString();
    }

    @Override
    public SettingList parse(String text) throws FormatException {
        if (text == null || text.length() == 0) {
            return getDefaultValue();
        } else if (isMultiple()) {
            try {
                SettingList result = SettingList.multiple();
                for (String part : exprParser.split(text, " ")) {
                    result.add(parseSingle(part));
                }
                return result;
            } catch (FormatException exc) {
                // the string contains unbalanced quotes or brackets
                return null;
            }
        } else {
            return SettingList.single(parseSingle(text));
        }
    }

    /** Parses a string holding a single setting value. */
    public Setting<?,?> parseSingle(String text) throws FormatException {
        if (this.uniqueKey) {
            return this.defaultKind.createSetting(this.defaultKind.parser().parse(text));
        } else {
            String name = getNamePrefix(text);
            SettingKey kind = getKey(name);
            SettingContent content = this.parserMap.get(kind).parse(text.substring(name.length()));
            return kind.createSetting(content);
        }
    }

    @Override
    public String toParsableString(Object value) {
        if (isDefault(value)) {
            return "";
        } else if (this.uniqueKey) {
            Setting<?,?> val = (Setting<?,?>) value;
            return this.defaultKind.parser().toParsableString(val.getContent());
        } else {
            Setting<?,?> val = (Setting<?,?>) value;
            return val.getKind().getName()
                + getParser(val.getKind()).toParsableString(val.getContent());
        }
    }

    @Override
    public Class<? extends SettingList> getValueType() {
        return SettingList.class;
    }

    @Override
    public boolean isValue(Object value) {
        boolean result = value instanceof Setting;
        if (result) {
            Setting<?,?> val = (Setting<?,?>) value;
            result = val.getKind().getClass() == this.keyType;
            if (result) {
                result = val.getKind().isValue(val.getContent());
            }
        }
        return result;
    }

    @Override
    public SettingList getDefaultValue() {
        return this.defaultValue;
    }

    private final SettingList defaultValue;

    @Override
    public String getDefaultString() {
        return this.defaultString;
    }

    private final String defaultString;

    @Override
    public boolean isDefault(Object value) {
        return getDefaultValue().equals(value);
    }

    private static final ExprParser exprParser = new ExprParser("\"", "()");
}

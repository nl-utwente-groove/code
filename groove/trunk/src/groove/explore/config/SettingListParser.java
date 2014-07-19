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
import groove.util.parse.Parser;
import groove.util.parse.StringHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Parser for a setting list of a given exploration key.
 * @author Arend Rensink
 * @version $Revision $
 */
public class SettingListParser implements Parser<SettingList> {
    /**
     * Constructs a parser for explore values.
     */
    public SettingListParser(ExploreKey key) {
        this.key = key;
        this.kindMap = new HashMap<String,SettingKey>();
        this.parserMap = new HashMap<SettingKey,Parser<SettingContent>>();
        for (SettingKey kind : key.getKindType().getEnumConstants()) {
            this.kindMap.put(kind.getName(), kind);
            this.parserMap.put(kind, new BracketParser<SettingContent>(kind.parser(), true));
        }
        assert this.kindMap.size() > 0;
        if (this.kindMap.size() == 1) {
            this.defaultString = key.getDefaultKind().parser().getDefaultString();
        } else {
            this.defaultString =
                key.getDefaultKind().getName() + getParser(key.getDefaultKind()).getDefaultString();
        }
    }

    /** Returns the exploration key of this setting parser. */
    private ExploreKey getKey() {
        return this.key;
    }

    private final ExploreKey key;

    private SettingKey getKind(String name) {
        return this.kindMap.get(name);
    }

    private final Map<String,SettingKey> kindMap;

    /** Returns the pre-initialised parser for a given setting kind. */
    private Parser<SettingContent> getParser(SettingKey kind) {
        return this.parserMap.get(kind);
    }

    private final Map<SettingKey,Parser<SettingContent>> parserMap;

    @Override
    public String getDescription() {
        StringBuilder result = new StringBuilder("<body>");
        if (getKey().isSingular()) {
            result.append("A value ");
        } else {
            result.append("One or more values ");
        }
        result.append("of the form <i>kind</i> <i>args</i> (without the space), where <i>kind</i> is one of");
        for (SettingKey key : getKey().getKindType().getEnumConstants()) {
            result.append("<li> - ");
            result.append(HTMLConverter.ITALIC_TAG.on(key.getName()));
            result.append(": ");
            result.append(key.getExplanation());
            result.append(", with <i>arg</i> ");
            result.append(StringHandler.toLower(getParser(key).getDescription()));
        }
        return result.toString();
    }

    @Override
    public boolean accepts(String text) {
        boolean result = false;
        if (text == null || text.length() == 0) {
            return true;
        } else if (getKey().isSingular()) {
            result = acceptsSingle(text);
        } else {
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
        }
        return result;
    }

    /** Tests if a given string represents a single setting value. */
    public boolean acceptsSingle(String text) {
        if (text == null || text.length() == 0) {
            return true;
        } else {
            String name = getNamePrefix(text);
            SettingKey key = getKind(name);
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
        } else if (getKey().isSingular()) {
            return SettingList.single(parseSingle(text));
        } else {
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
        }
    }

    /** Parses a string holding a single setting value. */
    public Setting<?,?> parseSingle(String text) throws FormatException {
        String name = getNamePrefix(text);
        if (name.isEmpty()) {
            throw new FormatException("Value '%s' should start with identifier", text);
        }
        SettingKey kind = getKind(name);
        if (kind == null) {
            throw new FormatException("Unknown setting kind '%s' in '%s'", name, text);
        }
        SettingContent content = getParser(kind).parse(text.substring(name.length()));
        return kind.createSetting(content);
    }

    @Override
    public String toParsableString(Object value) {
        String result = "";
        SettingList settings = (SettingList) value;
        if (!isDefault(value)) {
            StringBuilder builder = new StringBuilder();
            boolean first = true;
            for (Setting<?,?> setting : settings) {
                if (first) {
                    first = false;
                } else {
                    builder.append(" ");
                }
                SettingKey kind = setting.getKind();
                builder.append(kind.getName());
                builder.append(getParser(kind).toParsableString(setting.getContent()));
            }
            result = builder.toString();
        }
        return result;
    }

    @Override
    public Class<? extends SettingList> getValueType() {
        return SettingList.class;
    }

    @Override
    public boolean isValue(Object value) {
        boolean result = value instanceof SettingList;
        if (result) {
            SettingList settings = (SettingList) value;
            if (settings.isSingular() == getKey().isSingular()) {
                for (Setting<?,?> setting : settings) {
                    if (!setting.getKind().isValue(setting.getContent())) {
                        result = false;
                        break;
                    }
                }
            } else {
                result = false;
            }
        }
        return result;
    }

    @Override
    public boolean hasDefault() {
        return true;
    }

    @Override
    public SettingList getDefaultValue() {
        if (this.defaultValue == null) {
            if (getKey().isSingular()) {
                SettingKey defaultKind = getKey().getDefaultKind();
                Setting<?,?> defaultSetting =
                    defaultKind.createSetting(defaultKind.getDefaultValue());
                this.defaultValue = SettingList.single(defaultSetting);
            } else {
                this.defaultValue = SettingList.multiple();
            }
        }
        return this.defaultValue;
    }

    private SettingList defaultValue;

    @Override
    public String getDefaultString() {
        return this.defaultString;
    }

    private final String defaultString;

    @Override
    public boolean isDefault(Object value) {
        return getDefaultValue().equals(value);
    }

    private static final StringHandler exprParser = new StringHandler("\"", "()");
}

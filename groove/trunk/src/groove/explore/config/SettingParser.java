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
import groove.util.Parser;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class SettingParser implements Parser<Setting<?,?>> {
    /**
     * Constructs a parser for explore values, using a given content parser.
     */
    public SettingParser(SettingKey defaultKey) {
        this.defaultKey = defaultKey;
        this.keyType = defaultKey.getClass();
        this.keyMap = new HashMap<String,SettingKey>();
        this.parserMap = new HashMap<SettingKey,Parser<SettingContent>>();
        for (SettingKey key : this.keyType.getEnumConstants()) {
            this.keyMap.put(key.getName(), key);
            this.parserMap.put(key, new BracketParser<SettingContent>(key.parser()));
        }
        assert this.keyMap.size() > 0;
        this.single = this.keyMap.size() == 1;

        this.defaultValue = defaultKey.createSetting(this.defaultKey.getDefaultValue());
        if (this.single) {
            this.defaultString = defaultKey.parser().getDefaultString();
        } else {
            this.defaultString = defaultKey.getName() + getParser(defaultKey).getDefaultString();
        }
    }

    private final SettingKey defaultKey;
    private final Class<? extends SettingKey> keyType;
    /** Flag indicating that the key type has only a single value. */
    private final boolean single;

    private SettingKey getKey(String name) {
        return this.keyMap.get(name);
    }

    private final Map<String,SettingKey> keyMap;

    private Parser<SettingContent> getParser(SettingKey key) {
        return this.parserMap.get(key);
    }

    private final Map<SettingKey,Parser<SettingContent>> parserMap;

    @Override
    public String getDescription(boolean uppercase) {
        if (this.single) {
            return this.defaultKey.parser().getDescription(uppercase);
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
        if (text == null || text.length() == 0) {
            return true;
        } else if (this.single) {
            return this.defaultKey.parser().accepts(text);
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
    public Setting<?,?> parse(String text) {
        if (text == null || text.length() == 0) {
            return getDefaultValue();
        } else if (this.single) {
            return this.defaultKey.createSetting(this.defaultKey.parser().parse(text));
        } else {
            String name = getNamePrefix(text);
            SettingKey key = getKey(name);
            SettingContent content = this.parserMap.get(key).parse(text.substring(name.length()));
            return key.createSetting(content);
        }
    }

    @Override
    public String toParsableString(Object value) {
        if (isDefault(value)) {
            return "";
        } else if (this.single) {
            return this.defaultKey.parser().toParsableString(value);
        } else {
            Setting<?,?> val = (Setting<?,?>) value;
            return val.getKey().getName()
                + getParser(val.getKey()).toParsableString(val.getContent());
        }
    }

    @Override
    public boolean isValue(Object value) {
        boolean result = value instanceof Setting;
        if (result) {
            Setting<?,?> val = (Setting<?,?>) value;
            result = val.getKey().getClass() == this.keyType;
            if (result) {
                result = val.getKey().isValue(val.getContent());
            }
        }
        return result;
    }

    @Override
    public Setting<?,?> getDefaultValue() {
        return this.defaultValue;
    }

    private final Setting<?,?> defaultValue;

    @Override
    public String getDefaultString() {
        return this.defaultString;
    }

    private final String defaultString;

    @Override
    public boolean isDefault(Object value) {
        return getDefaultValue().equals(value);
    }
}

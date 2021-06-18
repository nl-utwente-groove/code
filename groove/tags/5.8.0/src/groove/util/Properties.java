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
package groove.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import groove.grammar.GrammarChecker;
import groove.grammar.model.GrammarModel;
import groove.util.parse.FormatChecker;
import groove.util.parse.FormatErrorSet;
import groove.util.parse.FormatException;
import groove.util.parse.Parser;

/**
 * Specialised properties class.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class Properties extends java.util.Properties implements Fixable {
    /** Constructs a properties object with keys of a given type. */
    protected Properties(Class<? extends PropertyKey<?>> keyType) {
        this.keyType = keyType;
        this.keyMap = new LinkedHashMap<>();
        for (PropertyKey<?> key : keyType.getEnumConstants()) {
            this.keyMap.put(key.getName(), key);
        }
    }

    /** Returns the key type of this properties class. */
    public Class<? extends PropertyKey<?>> getKeyType() {
        return this.keyType;
    }

    private final Class<? extends PropertyKey<?>> keyType;

    /** Returns the key with a given name, if any. */
    public PropertyKey<?> getKey(String name) {
        return this.keyMap.get(name);
    }

    private final Map<String,PropertyKey<?>> keyMap;

    /** Returns a map from property keys to checkers driven by a given grammar model. */
    public CheckerMap getCheckers(final GrammarModel grammar) {
        CheckerMap result = new CheckerMap();
        for (final PropertyKey<?> key : getKeyType().getEnumConstants()) {
            FormatChecker<String> checker;
            if (key instanceof GrammarChecker) {
                final GrammarChecker checkerKey = (GrammarChecker) key;
                checker = new FormatChecker<String>() {
                    @Override
                    public FormatErrorSet check(String value) {
                        try {
                            return checkerKey.check(grammar, key.parser()
                                .parse(value));
                        } catch (FormatException exc) {
                            return exc.getErrors();
                        }
                    }
                };
            } else {
                checker = FormatChecker.EMPTY_STRING_CHECKER;
            }
            result.put(key, checker);
        }
        return result;
    }

    @Override
    public synchronized String toString() {
        StringBuffer result = new StringBuffer();
        if (isEmpty()) {
            result.append("No stored properties");
        } else {
            result.append("Properties:\n");
            for (Map.Entry<Object,Object> entry : entrySet()) {
                result.append("  " + entry + "\n");
            }
        }
        return result.toString();
    }

    /** Retrieves and parses the value for a given key.
     * Returns the default value for the key if the stored value contains an error. */
    protected Object parsePropertyOrDefault(PropertyKey<?> key) {
        try {
            return parseProperty(key);
        } catch (FormatException exc) {
            return key.parser()
                .getDefaultValue();
        }
    }

    /** Retrieves and parses the value for a given key.
     * Returns the default value for the key if the stored value contains an error. */
    public Object parseProperty(PropertyKey<?> key) throws FormatException {
        String result = getProperty(key.getName());
        Parser<?> parser = key.parser();
        return parser.parse(result);
    }

    /** Stores a property value, converted to a parsable string. */
    public void storeProperty(PropertyKey<?> key, Object value) {
        assert key.parser()
            .isValue(value) : String.format("%s is not appropriate for %s", value, key);
        Parser<?> parser = key.parser();
        if (parser.isDefault(value)) {
            remove(key.getName());
        } else {
            setProperty(key.getName(), parser.toParsableString(value));
        }
    }

    /** Convenience method to retrieve a property by key value rather than string. */
    public String getProperty(PropertyKey<?> key) {
        String result = getProperty(key.getName());
        if (result == null) {
            result = key.parser()
                .getDefaultString();
        }
        return result;
    }

    @Override
    public String setProperty(String keyword, String value) {
        testFixed(false);
        String oldValue;
        PropertyKey<?> key = getKey(keyword);
        if (value == null || value.length() == 0) {
            oldValue = (String) remove(keyword);
        } else if (key == null) {
            // this is a non-system key
            oldValue = (String) super.setProperty(keyword, value);
        } else if (!key.parser()
            .accepts(value)
            || key.parser()
                .isDefault(value)) {
            oldValue = (String) remove(keyword);
        } else {
            oldValue = (String) super.setProperty(keyword, value);
        }
        return oldValue;
    }

    @Override
    public boolean setFixed() {
        return this.fixable.setFixed();
    }

    @Override
    public boolean isFixed() {
        return this.fixable.isFixed();
    }

    /** Object to delegate the fixable functionality. */
    private final DefaultFixable fixable = new DefaultFixable();

    /*
     * Before calling the super method, tests if the properties are fixed and
     * throws an {@link IllegalStateException} if this is the case.
     * @throws IllegalStateException if the graph has been fixed.
     * @see #setFixed()
     */
    @Override
    public synchronized void load(InputStream inStream) throws IOException {
        testFixed(false);
        clear();
        super.load(inStream);
    }

    /*
     * Before calling the super method, tests if the properties are fixed and
     * throws an {@link IllegalStateException} if this is the case.
     * @throws IllegalStateException if the properties have been fixed.
     * @see #setFixed()
     */
    @Override
    public synchronized void loadFromXML(InputStream in)
        throws IOException, InvalidPropertiesFormatException {
        testFixed(false);
        clear();
        super.loadFromXML(in);
    }

    /*
     * Before calling the super method, tests if the properties are fixed and
     * throws an {@link IllegalStateException} if this is the case.
     * @throws IllegalStateException if the properties have been fixed.
     * @see #setFixed()
     */
    @Override
    public synchronized void clear() {
        testFixed(false);
        super.clear();
    }

    /*
     * Before calling the super method, tests if the properties are fixed and
     * throws an {@link IllegalStateException} if this is the case.
     * @throws IllegalStateException if the graph has been fixed.
     * @see #setFixed()
     */
    @Override
    public synchronized Object put(Object key, Object value) {
        testFixed(false);
        if (value == null || (value instanceof String && ((String) value).length() == 0)) {
            return super.remove(key);
        } else {
            return super.put(key, value);
        }
    }

    /*
     * Before calling the super method, tests if the properties are fixed and
     * throws an {@link IllegalStateException} if this is the case.
     * @throws IllegalStateException if the graph has been fixed.
     * @see #setFixed()
     */

    @Override
    public synchronized Object remove(Object key) {
        testFixed(false);
        return super.remove(key);
    }

    /* Returns an unmodifiable set. */
    @Override
    public Set<Object> keySet() {
        return Collections.unmodifiableSet(super.keySet());
    }

    /* Returns an unmodifiable set. */
    @Override
    public Set<Entry<Object,Object>> entrySet() {
        return Collections.unmodifiableSet(super.entrySet());
    }

    /** Map from property keys to format checkers for those keys. */
    public static class CheckerMap extends HashMap<PropertyKey<?>,FormatChecker<String>> {
        @Override
        public FormatChecker<String> get(Object key) {
            FormatChecker<String> result = super.get(key);
            if (result == null) {
                result = FormatChecker.EMPTY_STRING_CHECKER;
            }
            return result;
        }
    }
}

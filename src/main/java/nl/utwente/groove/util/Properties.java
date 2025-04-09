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
package nl.utwente.groove.util;

import static nl.utwente.groove.io.HTMLConverter.TABLE_TAG_NAME;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Algebra;
import nl.utwente.groove.algebra.AlgebraFamily;
import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.grammar.Action.Role;
import nl.utwente.groove.grammar.CheckPolicy;
import nl.utwente.groove.grammar.CheckPolicy.PolicyMap;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.rule.MethodName;
import nl.utwente.groove.gui.look.Values;
import nl.utwente.groove.io.HTMLConverter;
import nl.utwente.groove.io.HTMLConverter.HTMLTag;
import nl.utwente.groove.transform.oracle.ValueOracleFactory;
import nl.utwente.groove.util.collect.DeltaMap;
import nl.utwente.groove.util.parse.FormatChecker;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.util.parse.ParsableKey;
import nl.utwente.groove.util.parse.Parser;

/**
 * Specialised properties class.
 * The underlying map is from {@link String} keys to {@link String} values, but this class
 * adds functionality to
 * <ul>
 * <li> access the keys using {@link Key}-typed objects, each of which accommodates actual values of a dedicated type
 * <li> access the values using {@link Entry}-typed objects wrapping the actual values for the {@link Key}s
 * <li> parsing and unparsing between {@link String}s and {@link Entry}s
 * <li> wrapping and unwrapping between {@link Entry}s and the actual values for the corresponding keys
 * </ul>
 * @author Arend Rensink
 * @version $Revision$
 */
// I'd like to include the following tag but Maven throws up
// @NonNullByDefault
public abstract class Properties implements Fixable {
    /** Constructs a properties object with keys of a given type. */
    protected Properties(Class<? extends Key> keyType) {
        this.keyType = keyType;
    }

    /** Returns the internal properties map. */
    private java.util.Properties getProperties() {
        return this.properties;
    }

    /** This internal properties map. */
    private final java.util.Properties properties = new java.util.Properties();

    /** Returns the key type of this properties class. */
    public Class<? extends Key> getKeyType() {
        return this.keyType;
    }

    private final Class<? extends Key> keyType;

    /** Returns the key with a given name, if any;
     * or {@code null} if the name is not a recognisable key.
     * @param name the name of the key, as regurned by {@code key.getName()}.
     * <i>Note:</i> this is different from the enum value string returned by {@code key.name()}!*/
    abstract public Optional<? extends Key> getKey(String name);

    /** Indicates if there is a notable property value in this table. */
    public boolean isNotable() {
        return this.notable;
    }

    /** Changes the notability property. */
    private void setNotable(boolean notable) {
        this.notable = notable;
    }

    /**
     * Recomputes (and sets) whether there are any notable values in this properties map.
     */
    private void refreshNotable() {
        var notable = getProperties()
            .entrySet()
            .stream()
            .anyMatch(e -> isNotable((String) e.getKey(), (String) e.getValue()));
        setNotable(notable);
    }

    private boolean notable;

    /** Helper method to determine if a given key-value-pair is notable. */
    public boolean isNotable(String keyword, @Nullable String value) {
        return value != null && getKey(keyword).map(k -> k.isNotableValue(value)).orElse(false);
    }

    /** Returns HTML-formatted text (without the final HTML tag) listing
     * the notable properties in this object. If there are no notable properties,
     * the return value is the empty string.
     */
    public String getNotableProperties() {
        StringBuilder result = new StringBuilder();
        for (var entry : getProperties().entrySet()) {
            var keyword = (String) entry.getKey();
            var value = (String) entry.getValue();
            if (isNotable(keyword, value)) {
                var rule = new StringBuilder();
                rule.append("<th style=\"padding:0\" align=\"right\">");
                rule.append(getKey(keyword).get().getKeyPhrase());
                rule
                    .append("<td width=\"10\" align=\"center\"> = <td style=\"vertical-align:top\" align=\"left\">");
                rule.append(value);
                TABLE_RULE_TAG.on(rule);
                result.append(rule);
            }
        }
        if (result.isEmpty()) {
            return "";
        } else {
            TABLE_TAG.on(result);
            return result.toString();
        }
    }

    @Override
    public synchronized String toString() {
        StringBuffer result = new StringBuffer();
        if (getProperties().isEmpty()) {
            result.append("No stored properties");
        } else {
            result.append("Properties:\n");
            for (Map.Entry<Object,Object> entry : getProperties().entrySet()) {
                result.append("  " + entry + "\n");
            }
        }
        return result.toString();
    }

    /** Retrieves and parses the stored value for a given key.
     * Returns the default entry for the key if the stored value contains an error. */
    protected Entry parsePropertyOrDefault(Key key) {
        try {
            return parseProperty(key);
        } catch (FormatException exc) {
            return key.parser().getDefaultValue();
        }
    }

    /** Retrieves and parses the entry for a given key.
     * Throws a {@link FormatException} if the stored (string) value contains an error. */
    public Entry parseProperty(Key key) throws FormatException {
        String result = getProperties().getProperty(key.getName());
        return key
            .parser()
            .parse(result == null
                ? ""
                : result);
    }

    /** Convenience method to test the presence of a key value rather than key name.
     * @see #containsKey(String)
     */
    public boolean containsKey(Key key) {
        return containsKey(key.getName());
    }

    /** Tests whether a given keyword is in the properties map. *
     * @param keyword the keyword to be removed; may be a {@link Key} name or a user property
     * @return the value associated with {@code keyword}
     */
    public boolean containsKey(String keyword) {
        return getProperties().containsKey(keyword);
    }

    /** Convenience method to retrieve a property by key value rather than key name. */
    public @Nullable String getProperty(Key key) {
        return getProperty(key.getName());
    }

    /** Retrieve a given property value.
     * @param keyword the keyword to be removed; may be a {@link Key} name or a user property
     * @return the value associated with {@code keyword}
     */
    public @Nullable String getProperty(String keyword) {
        return getProperties().getProperty(keyword);
    }

    /** Convenience method to remove a property by key value rather than key name.
     * @see #remove(String)
     */
    public @Nullable String remove(Key key) {
        return remove(key.getName());
    }

    /** Removes a given keyword from the properties map.
     * @param keyword the keyword to be removed; may be a {@link Key} name or a user property
     * @return the value previously associated with {@code keyword}
     */
    public @Nullable String remove(String keyword) {
        var result = (String) getProperties().remove(keyword);
        if (isNotable(keyword, result)) {
            refreshNotable();
        }
        return result;
    }

    /** Stores a property value in the map.
     * The value should be of the type expected by the key.
     * @throws IllegalArgumentException if {@code value} is not a valid value for {@code key}
     */
    public void storeValue(Key key, Object value) throws IllegalArgumentException {
        storeEntry(key, key.wrap(value));
    }

    /** Stores a property entry in the map. */
    public void storeEntry(Key key, Entry entry) {
        if (key.isDefault(entry)) {
            remove(key);
        } else {
            setProperty(key, entry.unparse());
        }
    }

    /** Convenience method to store a property value by key value rather than key name. */
    public @Nullable String setProperty(Key key, String value) {
        return setProperty(key.getName(), value);
    }

    /** Sets a property by the string representation of its key, i.e., the keyword.
     * If the keyword is not the name of a {@link Key}, the property is stored without
     * checking. If it is the name of a {@link Key}, the property is parsed according to
     * that key and an {@link IllegalArgumentException} is thrown if it is not parsable.
     * @param keyword a non-empty string representing either a system key or a user property
     * @param value the value to be stored for {@code keyword}
     * @return the (possibly {@code null}) value previously associated with the keyword
     * @throws IllegalArgumentException if the value is not appropriate for the keyword
     */
    public @Nullable String setProperty(String keyword,
                                        String value) throws IllegalArgumentException {
        testFixed(false);
        assert keyword != null;
        String oldValue;
        Optional<? extends Key> key = getKey(keyword);
        if (key.isEmpty()) {
            // this is a non-system key
            oldValue = (String) getProperties().setProperty(keyword, value);
        } else if (key.get().parser().parsesToDefault(value)) {
            oldValue = (String) getProperties().remove(keyword);
            if (oldValue != null) {
                refreshNotable();
            }
        } else {
            oldValue = (String) getProperties().setProperty(keyword, value);
            if (key.get().isNotable()) {
                setNotable(true);
            }
        }
        return oldValue;
    }

    /**
     * Copies a given property map into this one.
     * @param properties the property map to be copied into this one
     * @throws IllegalArgumentException if the key type of {@code properties}
     * does not coincide with this one
     */
    public void putAll(Properties properties) throws IllegalArgumentException {
        if (properties.getKeyType() != getKeyType()) {
            throw Exceptions
                .illegalArg("Property map to be cloned has key type %s rather than %s",
                            properties.getKeyType(), getKeyType());
        }
        getProperties().putAll(properties.getProperties());
        setNotable(properties.isNotable());
    }

    /** Returns a stream of the entries in this property map,
     * as {@link String}-typed key/value pairs.
     */
    public Stream<Map.Entry<String,String>> entryStream() {
        return getProperties().entrySet().stream().map(e -> convert(e));
    }

    /** Loads this property map from a given input stream.
     * @throws IOException if reading from the stream throws this exception
     * @throws IllegalArgumentException if one of the values in the loaded stream
     *  is not appropriate for the keyword
     */
    public void load(InputStream stream) throws IOException {
        var newProps = new java.util.Properties();
        newProps.load(stream);
        for (var e : newProps.entrySet()) {
            var k = (String) e.getKey();
            var v = (String) e.getValue();
            //            Optional<? extends Key> key = getKey(k);
            //            if (key.isPresent()) {
            //                try {
            //                    key.get().parse(v);
            //                } catch (FormatException exc) {
            //                    throw new IOException(exc);
            //                }
            //            }
            setProperty(k, v);
        }
    }

    /** Stores this property map to a given writer.
     * @throws IOException if reading from the stream throws this exception
     */
    public void store(Writer writer) throws IOException {
        getProperties().store(writer, null);
    }

    private Map.Entry<String,String> convert(Map.Entry<Object,Object> inner) {
        return new Map.Entry<>() {
            public String getValue() {
                return (String) inner.getValue();
            }

            public String setValue(String value) {
                throw Exceptions.UNREACHABLE;
            }

            public String getKey() {
                return (String) inner.getKey();
            }
        };
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

    /** HTML-formatted colour specification for the {@link Values#INFO_NORMAL_FOREGROUND} colour. */
    static public final String HTML_INFO_COLOR
        = HTMLConverter.toHtmlColor(Values.INFO_NORMAL_FOREGROUND);
    /** HTMLfont tag for the {@link Values#INFO_NORMAL_FOREGROUND} colour. */
    static public final HTMLTag INFO_COLOR_TAG
        = HTMLConverter.createColorTag(Values.INFO_NORMAL_FOREGROUND);
    /** HTMLfont tag for the {@link Values#INFO_NORMAL_FOREGROUND} colour. */
    static public final HTMLTag INFO_FONT_TAG
        = HTMLConverter.createHtmlTag("font", "color", HTML_INFO_COLOR);
    static private final HTMLTag TABLE_RULE_TAG
        = HTMLConverter.createHtmlTag("tr", "style", "padding:0; color:" + HTML_INFO_COLOR);
    static private final HTMLTag TABLE_TAG = HTMLConverter
        .createHtmlTag(TABLE_TAG_NAME, "cellpadding", "0", "valign", "top", "style",
                       "border-spacing:2; margin-left:20");

    /**
     * Interface for property keys; that is,
     * keys that are used in a {@link Properties} object.
     * @author Arend Rensink
     * @version $Id$
     */
    public static interface Key extends ParsableKey<Entry> {
        /** Short description for user consumption. */
        String getKeyPhrase();

        /** Indicates if this is a system key.
         * System keys are not user-modifiable.
         */
        boolean isSystem();

        /** Indicates if the value for this key is derived, i.e.,
         * not stored or modifiable.
         * Derived keys are always system keys.
         */
        default boolean isDerived() {
            return false;
        }

        /** Specialises the type of the interface. */
        @Override
        KeyParser parser();

        /** Indicates if a non-default value should be actively signalled. */
        public boolean isNotable();

        /** Indicates if a given (String) value is a notable value for this key. */
        default public boolean isNotableValue(String value) {
            try {
                return isNotable() && !parse(value).equals(getDefaultValue());
            } catch (FormatException exc) {
                return false;
            }
        }

        /** Wraps a given value into an entry for this key.
         * First checks whether the value is of the right type for this key.
         * @throws IllegalArgumentException of {@code value} is not of the right type for this key.
         * */
        default Entry wrap(Object value) throws IllegalArgumentException {
            return new Entry(this, value);
        }

        /** Checks whether the value is of the right type for this key, using #cast.
         * @throws IllegalArgumentException of {@code value} is not of the right type for this key.
         * */
        default void check(Entry value) throws IllegalArgumentException {
            wrap(value);
        }

        /** Unwraps the value in a given {@link Entry}.
         * First tests whether the entry's key equals {@code this}, and throws
         * an {@link IllegalArgumentException} otherwise.
         * The return type defaults to {@link Object}, but may be specialised by implementations
         * to cast to the actual type for this key.
         * @throws IllegalArgumentException if the key of {@code entry} does not equal {@code this}
         */
        default Object unwrap(Entry entry) throws IllegalArgumentException {
            if (entry.key() != this) {
                throw Exceptions
                    .illegalArg("Entry '%s' does not correspond to key '%s'", entry, this);
            }
            return entry.value();
        }

        /** Parses a given string value into an entry for this key.
         * Convenience method for {@link KeyParser#parse(String)} called on {@code value}
         * @throws FormatException if {@code value} cannot be parsed.
         */
        @Override
        default Entry parse(String value) throws FormatException {
            return parser().parse(value);
        }

        /** Returns the value for this key stored in the given map.
         * Returns the default value if the key is not in the map, or the stored value is not a parsable string.
         * Concrete keys may specialise the return type of this method.
         */
        default Object parseFrom(Properties properties) {
            Entry entry;
            try {
                entry = properties.parseProperty(this);
            } catch (FormatException exc) {
                entry = getDefaultValue();
            }
            return entry.value();
        }

        /** Returns the type of the actual values of this key.
         * Note that this is <i>not</i> the value type of the parser (which is {@link Entry})
         * but the value type of the inner parser.
         */
        ValueType getKeyType();

        /**
         * Start character that distinguishes system properties from user-definable
         * properties. Any string starting with this character is a system key.
         */
        static public final String SYSTEM_KEY_PREFIX = "$";
    }

    /** Exhaustive enumeration of all value types occurring for {@link Key} implementations. */
    public static enum ValueType {
        /** Value for type {@link Boolean}. */
        BOOLEAN(Boolean.class),
        /** Value for type {@link Integer}. */
        INTEGER(Integer.class),
        /** Value for type {@link Integer}. */
        STRING(String.class),
        /** Value for type {@link List} of {@link QualName}s. */
        QUAL_NAME_LIST(List.class),
        /** Value for type {@link DeltaMap} of {@link QualName}s. */
        QUAL_NAME_DELTA_MAP(DeltaMap.class),
        /** Value for type {@link MethodName}. */
        CLAZ(Optional.class),
        /** Value for type {@link MethodName}. */
        METHOD_NAME(Optional.class),
        /** Value for type {@link List}. */
        STRING_LIST(List.class),
        /** Value for type {@link CheckPolicy}. */
        CHECK_POLICY(CheckPolicy.class),
        /** Value for type {@link PolicyMap}. */
        POLICY_MAP(CheckPolicy.PolicyMap.class),
        /** Value for type {@link Algebra}. */
        ALGEBRA_FAMILY(AlgebraFamily.class),
        /** Value for type {@link Role}. */
        ROLE(Optional.class),
        /** Value for type {@link ExploreType}. */
        EXPLORE_TYPE(ExploreType.class),
        /** Value for type {@link ThreeValued}. */
        THREE_VALUED(ThreeValued.class),
        /** Value for type {@link Path}. */
        PATH(Path.class),
        /** Value for type {@link ThreeValued}. */
        ORACLE_FACTORY(ValueOracleFactory.class),;

        ValueType(Class<?> type) {
            this.type = type;
        }

        Class<?> type() {
            return this.type;
        }

        private final Class<?> type;
    }

    /** Property entry, consisting of a key and a wrapped value for that key.
     * Includes functionality to cast the wrapped value to any of the types supported by {@link ValueType}.
     */
    public static record Entry(Key key, Object value) {
        /** Record constructor, checking the invariant using #checkInvariant. */
        public Entry {
            checkInvariant(key, value);
        }

        /** Helper method for subclasses to assert the required invariant for this entry.
         * @throws IllegalArgumentException if the entry's key and value type are incompatible.
         */
        void checkInvariant(Key key, Object value) throws IllegalArgumentException {
            assert key != null : "Key should not be null";
            assert value != null : String.format("Value for '%s' should not be null", key);
            if (!key.getKeyType().type().isInstance(value)) {
                throw Exceptions
                    .illegalArg("Key type '%s' does not admit value '%s' of type %s", key, value,
                                value.getClass());
            }
        }

        /** Unparses this entry to a {@link String} value that the key's parser can understand. */
        public String unparse() {
            return key().parser().unparse(this);
        }

        /** Indicates if this entry represents a notable value.
         * This is the case if the key itself is notable and the value is not the key's default value.
         */
        public boolean isNotable() {
            return key().isNotable() && !value().equals(key().getDefaultValue());
        }

        /**
         * Casts the wrapped value to an {@link AlgebraFamily}.
         * This is only valid if this entry's key type is {@link ValueType#ALGEBRA_FAMILY}
         * @throws UnsupportedOperationException if this entry's key type is inappropriate
         */
        public AlgebraFamily getAlgebraFamily() {
            check(ValueType.ALGEBRA_FAMILY);
            return (AlgebraFamily) value();
        }

        /**
         * Casts the wrapped value to a {@link Boolean}.
         * This is only valid if this entry's key type is {@link ValueType#BOOLEAN}
         * @throws UnsupportedOperationException if this entry's key type is inappropriate
         */
        public Boolean getBoolean() {
            check(ValueType.BOOLEAN);
            return (Boolean) value();
        }

        /**
         * Casts the wrapped value to a {@link CheckPolicy}.
         * This is only valid if this entry's key type is {@link ValueType#CHECK_POLICY}
         * @throws UnsupportedOperationException if this entry's key type is inappropriate
         */
        public CheckPolicy getCheckPolicy() {
            check(ValueType.CHECK_POLICY);
            return (CheckPolicy) value();
        }

        /**
         * Casts the wrapped value to an {@link ExploreType}.
         * This is only valid if this entry's key type is {@link ValueType#EXPLORE_TYPE}
         * @throws UnsupportedOperationException if this entry's key type is inappropriate
         */
        public ExploreType getExploreType() {
            check(ValueType.EXPLORE_TYPE);
            return (ExploreType) value();
        }

        /**
         * Casts the wrapped value to an {@link Integer}.
         * This is only valid if this entry's key type is {@link ValueType#INTEGER}
         * @throws UnsupportedOperationException if this entry's key type is inappropriate
         */
        public Integer getInteger() {
            check(ValueType.INTEGER);
            return (Integer) value();
        }

        /**
         * Casts the wrapped value to an optional {@link Class}.
         * This is only valid if this entry's key type is {@link ValueType#CLAZ}
         * @throws UnsupportedOperationException if this entry's key type is inappropriate
         */
        @SuppressWarnings("unchecked")
        public Optional<Class<?>> getClaz() {
            check(ValueType.CLAZ);
            return (Optional<Class<?>>) value();
        }

        /**
         * Casts the wrapped value to a {@link MethodName}.
         * This is only valid if this entry's key type is {@link ValueType#METHOD_NAME}
         * @throws UnsupportedOperationException if this entry's key type is inappropriate
         */
        @SuppressWarnings("unchecked")
        public Optional<MethodName> getMethodName() {
            check(ValueType.METHOD_NAME);
            return (Optional<MethodName>) value();
        }

        /**
         * Casts the wrapped value to a {@link ValueOracleFactory}.
         * This is only valid if this entry's key type is {@link ValueType#ORACLE_FACTORY}
         * @throws UnsupportedOperationException if this entry's key type is inappropriate
         */
        public ValueOracleFactory getOracleFactory() {
            check(ValueType.ORACLE_FACTORY);
            return (ValueOracleFactory) value();
        }

        /**
         * Casts the wrapped value to a {@link Path}.
         * This is only valid if this entry's key type is {@link ValueType#PATH}
         * @throws UnsupportedOperationException if this entry's key type is inappropriate
         */
        public Path getPath() {
            check(ValueType.PATH);
            return (Path) value();
        }

        /**
         * Casts the wrapped value to a {@link PolicyMap}.
         * This is only valid if this entry's key type is {@link ValueType#POLICY_MAP}
         * @throws UnsupportedOperationException if this entry's key type is inappropriate
         */
        public PolicyMap getPolicyMap() {
            check(ValueType.POLICY_MAP);
            return (PolicyMap) value();
        }

        /**
         * Casts the wrapped value to a {@link List} of {@link QualName}s.
         * This is only valid if this entry's key type is {@link ValueType#QUAL_NAME_LIST}
         * @throws UnsupportedOperationException if this entry's key type is inappropriate
         */
        @SuppressWarnings("unchecked")
        public List<QualName> getQualNameList() {
            check(ValueType.QUAL_NAME_LIST);
            return (List<QualName>) value();
        }

        /**
         * Casts the wrapped value to a {@link DeltaMap} of {@link QualName}s.
         * This is only valid if this entry's key type is {@link ValueType#QUAL_NAME_DELTA_MAP}
         * @throws UnsupportedOperationException if this entry's key type is inappropriate
         */
        @SuppressWarnings("unchecked")
        public DeltaMap<QualName> getQualNameDeltaMap() {
            check(ValueType.QUAL_NAME_DELTA_MAP);
            return (DeltaMap<QualName>) value();
        }

        /**
         * Casts the wrapped value to a {@link Role}.
         * This is only valid if this entry's key type is {@link ValueType#ROLE}
         * @throws UnsupportedOperationException if this entry's key type is inappropriate
         */
        @SuppressWarnings("unchecked")
        public Optional<Role> getRole() {
            check(ValueType.ROLE);
            return (Optional<Role>) value();
        }

        /**
         * Casts the wrapped value to a {@link String}.
         * This is only valid if this entry's key type is {@link ValueType#STRING}
         * @throws UnsupportedOperationException if this entry's key type is inappropriate
         */
        public String getString() {
            check(ValueType.STRING);
            return (String) value();
        }

        /**
         * Casts the wrapped value to a {@link List} of {@link String}s.
         * This is only valid if this entry's key type is {@link ValueType#STRING_LIST}
         * @throws UnsupportedOperationException if this entry's key type is inappropriate
         */
        @SuppressWarnings("unchecked")
        public List<String> getStringList() {
            check(ValueType.STRING_LIST);
            return (List<String>) value();
        }

        /**
         * Casts the wrapped value to a {@link ThreeValued}.
         * This is only valid if this entry's key type is {@link ValueType#THREE_VALUED}
         * @throws UnsupportedOperationException if this entry's key type is inappropriate
         */
        public ThreeValued getThreeValued() {
            check(ValueType.THREE_VALUED);
            return (ThreeValued) value();
        }

        /** Checks whether the key type of this entry equals a given key type.
         * Throws an {@link UnsupportedOperationException} if the two key types do not coincide.
         * @param keyType they key type to check for
         * @throws UnsupportedOperationException of {@code keyType} does not equal this entry's key type
         */
        private void check(ValueType keyType) {
            if (this.key().getKeyType() != keyType) {
                throw Exceptions
                    .unsupportedOp("Can't extract %s value from %s entry", keyType.type(),
                                   this.key());
            }
        }
    }

    /** Parser for {@link Key} of this properties class.
     * Parses to {@link Entry} values as a wrapped parser, where the inner parser is {@link Key}-specific.
     */
    public static class KeyParser extends Parser.Wrap<Entry> {
        /** Constructs a wrapped parser from a given inner parser, target type, and wrapping function. */
        @SuppressWarnings("unchecked")
        public <T> KeyParser(Key key, Parser<T> inner) {
            super(inner, Entry.class, v -> new Entry(key, v), e -> (T) e.value());
            this.key = key;
        }

        /** Returns the key of this parser. */
        public Key key() {
            return this.key;
        }

        private final Key key;
    }

    /** Map from property keys to format checkers for those keys. */
    public static class CheckerMap extends HashMap<Key,FormatChecker<String>> {
        @Override
        public FormatChecker<String> get(@Nullable Object key) {
            var result = super.get(key);
            if (result == null) {
                result = FormatChecker.EMPTY_STRING_CHECKER;
            }
            return result;
        }
    }
}

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.grammar.model.Settings;
import nl.utwente.groove.grammar.model.SettingsContent;
import nl.utwente.groove.grammar.model.SettingsModel;
import nl.utwente.groove.grammar.model.SettingsSchemas;
import nl.utwente.groove.io.external.PortException;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Factory;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.Strings;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Configuration of the Ecore porter: the global encoding options plus the
 * per-element overrides, as read from the grammar's unique settings resource
 * of schema {@link EcoreMappingSchema#NAME} (created, by default, under the
 * name {@link #RESOURCE_NAME}).
 * <p>
 * Entry keys are parsed from the right: the last {@code .}-separated segment is
 * the choice key, the segments before it (if any) form an Ecore element path
 * (package-qualified Ecore names, unqualified allowed when unambiguous).
 * The key forms are enumerated in {@link EcoreKey}.
 * The element paths are kept as strings here; they are resolved against the
 * metamodel in hand when the porter runs.
 * @author Arend Rensink
 */
@NonNullByDefault
public class EcoreMapping {
    /**
     * Constructs a mapping from a set of settings entries.
     * @param props the settings entries; the {@code $schema} entry is ignored
     * @throws FormatException if any entry does not fit the vocabulary
     */
    public EcoreMapping(Properties props) throws FormatException {
        this(props, null);
    }

    /**
     * Constructs a mapping from the parsed content of a settings resource.
     * Behaves as {@link #EcoreMapping(Properties)}, except that every error
     * carries the position of the entry it is about.
     * @param content the parsed settings content; the {@code $schema} entry is ignored
     * @throws FormatException if any entry does not fit the vocabulary
     */
    public EcoreMapping(SettingsContent content) throws FormatException {
        this(content.properties(), content);
    }

    /**
     * Constructs a mapping from a set of settings entries, optionally
     * accompanied by the content they were parsed from; if the content is
     * given, the errors carry the position of the entry they are about.
     */
    private EcoreMapping(Properties props, @Nullable SettingsContent content)
        throws FormatException {
        FormatErrorSet errors = new FormatErrorSet();
        Ordering ordering = Ordering.NONE;
        boolean useIdentifiers = true;
        // process the keys in alphabetical order, for deterministic
        // map content and error order (Properties iterates hash-ordered)
        List<String> keys = new ArrayList<>(props.stringPropertyNames());
        Collections.sort(keys);
        for (String key : keys) {
            if (key.equals(SettingsModel.SCHEMA_KEY)) {
                continue;
            }
            String rawValue = props.getProperty(key);
            assert rawValue != null; // key is one of the property names of props
            String value = rawValue.trim();
            var numbers = SettingsContent.numbers(content, key);
            List<String> segments = Arrays.asList(key.split("\\.", -1));
            String choice = segments.get(segments.size() - 1);
            List<String> path = segments.subList(0, segments.size() - 1);
            if (path.stream().anyMatch(String::isEmpty) || choice.isEmpty()) {
                errors.add("Malformed Ecore mapping key '%s'", key, numbers);
                continue;
            }
            EcoreKey keyForm = EcoreKey.lookup(choice, path.size());
            if (keyForm == null) {
                List<EcoreKey> forms = EcoreKey.withText(choice);
                if (forms.isEmpty()) {
                    errors.add("Unknown Ecore mapping key '%s'", key, numbers);
                } else if (forms.stream().allMatch(EcoreKey::isGlobal)) {
                    errors
                        .add("'%s' is a global option; key '%s' should have no prefix", choice, key,
                             numbers);
                } else {
                    errors
                        .add("Key '%s' should have the form %s", key, EcoreKey.patterns(forms),
                             numbers);
                }
                continue;
            }
            String valueError = keyForm.checkValue(value);
            if (valueError != null) {
                errors.add("Value '%s' of '%s' %s", value, key, valueError, numbers);
                continue;
            }
            switch (keyForm) {
            case ORDERING -> ordering = Ordering.valueOfText(value);
            case USE_IDENTIFIERS -> useIdentifiers = Boolean.parseBoolean(value);
            case FEATURE_ORDERING -> this.featureOrdering
                .put(joinPath(path), Ordering.valueOfText(value));
            case TYPE_NAME, LITERAL_TYPE_NAME -> this.typeNames.put(joinPath(path), value);
            case LITERAL_STYLE -> this.literalStyles
                .put(joinPath(path), LiteralStyle.valueOfText(value));
            default -> throw Exceptions.unreachable();
            }
        }
        errors.throwException();
        this.ordering = ordering;
        this.useIdentifiers = useIdentifiers;
    }

    /** Returns the global ordering encoding, to be used for features without an override. */
    public Ordering ordering() {
        return this.ordering;
    }

    private final Ordering ordering;

    /** Indicates if {@code xmi:id} values are turned into {@code id:} aspects. */
    public boolean useIdentifiers() {
        return this.useIdentifiers;
    }

    private final boolean useIdentifiers;

    /** Returns the per-feature ordering overrides, from Ecore element paths
     * ({@code <class>.<feature>}, optionally package-qualified) to orderings. */
    public Map<String,Ordering> featureOrdering() {
        return Collections.unmodifiableMap(this.featureOrdering);
    }

    private final Map<String,Ordering> featureOrdering = new LinkedHashMap<>();

    /** Returns the type name overrides, from Ecore element paths (classifiers
     * or enum literals, optionally package-qualified) to GROOVE type names. */
    public Map<String,String> typeNames() {
        return Collections.unmodifiableMap(this.typeNames);
    }

    private final Map<String,String> typeNames = new LinkedHashMap<>();

    /** Returns the literal style overrides, from Ecore element paths (enums,
     * optionally package-qualified) to literal styles. */
    public Map<String,LiteralStyle> literalStyles() {
        return Collections.unmodifiableMap(this.literalStyles);
    }

    private final Map<String,LiteralStyle> literalStyles = new LinkedHashMap<>();

    private static String joinPath(List<String> path) {
        return String.join(".", path);
    }

    /**
     * Returns the mapping configured in a given grammar model, being the
     * content of its unique settings resource of the
     * {@link EcoreMappingSchema#NAME} schema; the default mapping if the
     * grammar is {@code null} or has no such resource.
     * @throws PortException if the resource has errors, or if there is more
     * than one candidate
     */
    public static EcoreMapping of(@Nullable GrammarModel grammar) throws PortException {
        if (grammar == null) {
            return getDefault();
        }
        List<QualName> candidates = candidates(grammar);
        if (candidates.isEmpty()) {
            return getDefault();
        }
        if (candidates.size() > 1) {
            throw new PortException(String
                .format("Multiple Ecore mapping resources: %s; keep exactly one",
                        candidates
                            .stream()
                            .map(QualName::toString)
                            .collect(Collectors.joining(", "))));
        }
        QualName name = candidates.get(0);
        var model = grammar.getTextResource(ResourceKind.SETTINGS, name);
        assert model != null;
        try {
            Settings settings = (Settings) model.toResource();
            return new EcoreMapping(settings.getProperties());
        } catch (FormatException exc) {
            throw new PortException(String
                .format("Error in settings resource '%s': %s", name, exc.getMessage()));
        }
    }

    /**
     * Returns the names of the settings resources of the
     * {@link EcoreMappingSchema#NAME} schema in a given grammar, in
     * alphabetical order: the singleton {@link #RESOURCE_NAME} resource as
     * well as any residents of an {@code ecore} folder, since the resources
     * are found by their schema rather than by a fixed name.
     */
    public static List<QualName> candidates(GrammarModel grammar) {
        return SettingsSchemas.getResourceNames(grammar, EcoreMappingSchema.INSTANCE);
    }

    /** Returns the default mapping: no ordering encoding, identifiers in use,
     * no overrides. */
    public static EcoreMapping getDefault() {
        return DEFAULT.get();
    }

    private static final Factory<EcoreMapping> DEFAULT = Factory.lazy(() -> {
        try {
            return new EcoreMapping(new Properties());
        } catch (FormatException exc) {
            throw Exceptions.illegalState("Default Ecore mapping cannot fail: %s", exc);
        }
    });

    /**
     * Returns the text of a settings resource with the global entries set to
     * given values. If an original text is given, only the (first) global
     * {@code ordering} and {@code useIdentifiers} lines are replaced or, if
     * absent, appended, so that comments and per-element entries survive;
     * otherwise a fresh resource text is generated.
     */
    public static String setGlobals(@Nullable String oldText, Ordering ordering,
                                    boolean useIdentifiers) {
        String orderingLine = ORDERING_KEY + " = " + ordering.text();
        String useIdsLine = USE_IDENTIFIERS_KEY + " = " + useIdentifiers;
        if (oldText == null) {
            return "# Ecore encoding options; see the '" + EcoreMappingSchema.NAME + "' schema\n"
                + SettingsModel.SCHEMA_KEY + " = " + EcoreMappingSchema.NAME + "\n" + orderingLine
                + "\n" + useIdsLine + "\n";
        }
        List<String> lines = new ArrayList<>(Arrays.asList(oldText.split("\\R", -1)));
        boolean orderingSeen = false;
        boolean useIdsSeen = false;
        for (int i = 0; i < lines.size(); i++) {
            String trimmed = lines.get(i).trim();
            if (!orderingSeen && trimmed.matches(ORDERING_KEY + "\\s*[=:].*")) {
                lines.set(i, orderingLine);
                orderingSeen = true;
            } else if (!useIdsSeen && trimmed.matches(USE_IDENTIFIERS_KEY + "\\s*[=:].*")) {
                lines.set(i, useIdsLine);
                useIdsSeen = true;
            }
        }
        // strip a single trailing empty line before appending, restore after
        boolean endedWithNewline = !lines.isEmpty() && lines.get(lines.size() - 1).isEmpty();
        if (endedWithNewline) {
            lines.remove(lines.size() - 1);
        }
        if (!orderingSeen) {
            lines.add(orderingLine);
        }
        if (!useIdsSeen) {
            lines.add(useIdsLine);
        }
        return String.join("\n", lines) + "\n";
    }

    /** Default name of a newly created settings resource holding the Ecore
     * mapping; an existing resource is located by its schema, not by its name. */
    public static final String RESOURCE_NAME = "ecore";
    /** Qualified form of {@link #RESOURCE_NAME}. */
    public static final QualName RESOURCE_QUAL_NAME = QualName.name(RESOURCE_NAME);
    /** Choice key for the ordering encoding (global or per-feature).
     * @see EcoreKey#ORDERING */
    public static final String ORDERING_KEY = EcoreKey.ORDERING.text();
    /** Choice key for the (global) use of {@code xmi:id} values.
     * @see EcoreKey#USE_IDENTIFIERS */
    public static final String USE_IDENTIFIERS_KEY = EcoreKey.USE_IDENTIFIERS.text();
    /** Choice key for a classifier or enum literal type name override.
     * @see EcoreKey#TYPE_NAME */
    public static final String TYPE_NAME_KEY = EcoreKey.TYPE_NAME.text();
    /** Choice key for a per-enum literal naming style.
     * @see EcoreKey#LITERAL_STYLE */
    public static final String LITERAL_STYLE_KEY = EcoreKey.LITERAL_STYLE.text();

    /** Encoding of the order of ordered or non-unique many-valued features. */
    public static enum Ordering {
        /** Many-valued features are encoded as plain edges; the order is lost. */
        NONE,
        /** Ordered or non-unique features are encoded through intermediate nodes
         * carrying an {@code index} attribute. */
        INDEX,;

        /** Returns the textual representation of this value, as used in the settings. */
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

        /** Returns the textual representations of all values. */
        public static List<String> texts() {
            return List.copyOf(textMap.get().keySet());
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

    /** Naming style of the literal types of an enum. */
    public static enum LiteralStyle {
        /** Literal types are named after their enum: {@code E$L}. */
        QUALIFIED,
        /** Literal types carry the plain literal name: {@code L}. */
        PLAIN,;

        /** Returns the textual representation of this value, as used in the settings. */
        public String text() {
            return Strings.toCamel(name());
        }

        /** Returns the value with a given textual representation.
         * @throws IllegalArgumentException if {@code text} is not the representation of any value
         */
        public static LiteralStyle valueOfText(String text) throws IllegalArgumentException {
            var result = textMap.get().get(text);
            if (result == null) {
                throw Exceptions.illegalArg("Unknown literal style '%s'", text);
            }
            return result;
        }

        /** Tests if a given string is the textual representation of some value. */
        public static boolean hasText(@Nullable String text) {
            return textMap.get().containsKey(text);
        }

        /** Returns the textual representations of all values. */
        public static List<String> texts() {
            return List.copyOf(textMap.get().keySet());
        }

        /** Lazily computed mapping from textual representations to values. */
        private static final Factory<Map<String,@Nullable LiteralStyle>> textMap
            = Factory.lazy(LiteralStyle::createTextMap);

        private static Map<String,@Nullable LiteralStyle> createTextMap() {
            Map<String,@Nullable LiteralStyle> result = new LinkedHashMap<>();
            Arrays.stream(values()).forEach(s -> result.put(s.text(), s));
            return result;
        }
    }
}

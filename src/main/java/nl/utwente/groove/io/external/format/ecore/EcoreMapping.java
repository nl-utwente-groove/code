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
import nl.utwente.groove.util.AIGenerated;
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
            case TYPE_NAME -> this.typeNames.put(joinPath(path), value);
            case LITERAL_STYLE -> this.literalStyles
                .put(joinPath(path), LiteralStyle.valueOfText(value));
            case PACKAGE -> this.packages.put(joinPath(path), PackageData.parse(value).getValue());
            case KIND -> this.kinds.put(joinPath(path), Kind.valueOfText(value));
            case FEATURE -> this.features.put(joinPath(path), FeatureData.parse(value).getValue());
            case OPPOSITE -> this.opposites.put(joinPath(path), value);
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

    /** Returns the recorded package data, from Ecore package paths to
     * namespace declarations. */
    @AIGenerated("Claude Opus 5, 2026-09")
    public Map<String,PackageData> packages() {
        return Collections.unmodifiableMap(this.packages);
    }

    private final Map<String,PackageData> packages = new LinkedHashMap<>();

    /** Returns the recorded classifier kinds, from Ecore element paths
     * (classifiers, optionally package-qualified) to kinds. */
    @AIGenerated("Claude Opus 5, 2026-09")
    public Map<String,Kind> kinds() {
        return Collections.unmodifiableMap(this.kinds);
    }

    private final Map<String,Kind> kinds = new LinkedHashMap<>();

    /** Returns the recorded feature data, from Ecore element paths
     * ({@code <class>.<feature>}, optionally package-qualified) to declarations. */
    @AIGenerated("Claude Opus 5, 2026-09")
    public Map<String,FeatureData> features() {
        return Collections.unmodifiableMap(this.features);
    }

    private final Map<String,FeatureData> features = new LinkedHashMap<>();

    /** Returns the recorded opposite references, from Ecore element paths
     * ({@code <class>.<reference>}) to the paths of their opposites. */
    @AIGenerated("Claude Opus 5, 2026-09")
    public Map<String,String> opposites() {
        return Collections.unmodifiableMap(this.opposites);
    }

    private final Map<String,String> opposites = new LinkedHashMap<>();

    /**
     * Renders the metadata records of this mapping as settings entry lines.
     * @see #entryLines(Map, Map, Map, Map)
     */
    @AIGenerated("Claude Opus 5, 2026-09")
    public List<String> entryLines() {
        return entryLines(this.packages, this.kinds, this.features, this.opposites);
    }

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
            return header() + orderingLine + "\n" + useIdsLine + "\n";
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

    /**
     * Returns the text of a settings resource with a set of generated entries
     * merged in. An entry whose key already occurs is replaced in place (at its
     * first occurrence, as {@link #setGlobals} does for the globals), so that
     * the surrounding comments and hand-written entries survive; entries with a
     * new key are appended in one group, under a comment naming the source they
     * were generated from. Nothing is ever removed. If no original text is
     * given, a fresh resource text is generated to merge into.
     * @param oldText the text of the existing resource, or {@code null} if
     * there is none
     * @param entries the generated entry lines, as {@link #entryLines} renders them
     * @param source the name of the file the entries were generated from
     */
    @AIGenerated("Claude Opus 5, 2026-09")
    public static String addEntries(@Nullable String oldText, List<String> entries, String source) {
        List<String> lines = new ArrayList<>(Arrays.asList((oldText == null
            ? header()
            : oldText).split("\\R", -1)));
        List<String> added = new ArrayList<>();
        for (var entry : entries) {
            String key = keyOf(entry);
            boolean replaced = false;
            for (int i = 0; i < lines.size() && !replaced; i++) {
                if (keyOf(lines.get(i)).equals(key)) {
                    lines.set(i, entry);
                    replaced = true;
                }
            }
            if (!replaced) {
                added.add(entry);
            }
        }
        // strip a single trailing empty line before appending, restore after
        if (!lines.isEmpty() && lines.get(lines.size() - 1).isEmpty()) {
            lines.remove(lines.size() - 1);
        }
        if (!added.isEmpty()) {
            lines.add("# recorded by the import of " + source);
            lines.addAll(added);
        }
        return String.join("\n", lines) + "\n";
    }

    /** Returns the key of a settings entry line, or the empty string if the
     * line is a comment or carries no key. */
    @AIGenerated("Claude Opus 5, 2026-09")
    private static String keyOf(String line) {
        String trimmed = line.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith("!")) {
            return "";
        }
        int split = trimmed.length();
        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (c == '=' || c == ':') {
                split = i;
                break;
            }
        }
        return trimmed.substring(0, split).trim();
    }

    /** Returns the opening lines of a freshly generated settings resource:
     * an explanatory comment and the schema declaration. */
    private static String header() {
        return "# Ecore encoding options; see the '" + EcoreMappingSchema.NAME + "' schema\n"
            + SettingsModel.SCHEMA_KEY + " = " + EcoreMappingSchema.NAME + "\n";
    }

    /**
     * Renders a set of metadata records as settings entry lines, in the
     * syntax of the vocabulary: one {@code key = value} line per entry, with
     * only the non-default fields of a composite value written. The lines come
     * in a fixed group order (packages, classifier kinds, features,
     * opposites), and within a group in the iteration order of the map, so
     * that the result is deterministic for insertion-ordered maps.
     * @param packages recorded package data, keyed by Ecore package path
     * @param kinds recorded classifier kinds, keyed by Ecore element path
     * @param features recorded feature declarations, keyed by Ecore element path
     * @param opposites recorded opposite references, keyed by Ecore element path
     */
    @AIGenerated("Claude Opus 5, 2026-09")
    public static List<String> entryLines(Map<String,PackageData> packages, Map<String,Kind> kinds,
                                          Map<String,FeatureData> features,
                                          Map<String,String> opposites) {
        List<String> result = new ArrayList<>();
        packages.forEach((path, data) -> result.add(line(path, EcoreKey.PACKAGE, data.toValue())));
        kinds.forEach((path, kind) -> result.add(line(path, EcoreKey.KIND, kind.text())));
        features.forEach((path, data) -> result.add(line(path, EcoreKey.FEATURE, data.toValue())));
        opposites.forEach((path, target) -> result.add(line(path, EcoreKey.OPPOSITE, target)));
        return result;
    }

    /** Renders a single settings entry line, in the style of {@link #setGlobals}. */
    @AIGenerated("Claude Opus 5, 2026-09")
    private static String line(String path, EcoreKey key, String value) {
        return path + "." + key.text() + " = " + value;
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
    /** Choice key for the namespace data of a package.
     * @see EcoreKey#PACKAGE */
    @AIGenerated("Claude Opus 5, 2026-09")
    public static final String PACKAGE_KEY = EcoreKey.PACKAGE.text();
    /** Choice key for the kind of a classifier.
     * @see EcoreKey#KIND */
    @AIGenerated("Claude Opus 5, 2026-09")
    public static final String KIND_KEY = EcoreKey.KIND.text();
    /** Choice key for the Ecore declaration of a structural feature.
     * @see EcoreKey#FEATURE */
    @AIGenerated("Claude Opus 5, 2026-09")
    public static final String FEATURE_KEY = EcoreKey.FEATURE.text();
    /** Choice key for the opposite of a reference.
     * @see EcoreKey#OPPOSITE */
    @AIGenerated("Claude Opus 5, 2026-09")
    public static final String OPPOSITE_KEY = EcoreKey.OPPOSITE.text();

    /**
     * Recorded namespace data of an Ecore package: what the type graph does
     * not determine about a package, beyond its path.
     * @param nsURI the namespace URI of the package
     * @param nsPrefix the namespace prefix, or {@code null} if it equals the
     * package name
     */
    @AIGenerated("Claude Opus 5, 2026-09")
    public static record PackageData(String nsURI, @Nullable String nsPrefix) {
        /** Renders this data as an entry value, omitting the unset prefix. */
        public String toValue() {
            StringBuilder result = new StringBuilder();
            EcoreFields.append(result, NS_URI_FIELD, this.nsURI);
            EcoreFields.append(result, NS_PREFIX_FIELD, this.nsPrefix);
            return result.toString();
        }

        /** Parses an entry value of the {@link EcoreKey#PACKAGE} form. */
        public static EcoreFields.Parsed<PackageData> parse(String value) {
            var parsed = EcoreFields.parse(value, FIELDS);
            if (parsed.error() != null) {
                return parsed.retype();
            }
            var fields = parsed.getValue();
            String nsURI = fields.get(NS_URI_FIELD);
            if (nsURI == null) {
                return EcoreFields.Parsed.error("misses field '%s'", NS_URI_FIELD);
            }
            return EcoreFields.Parsed
                .of(new PackageData(nsURI, fields.get(NS_PREFIX_FIELD)));
        }

        /** Name of the (mandatory) namespace URI field. */
        public static final String NS_URI_FIELD = "nsURI";
        /** Name of the (optional) namespace prefix field. */
        public static final String NS_PREFIX_FIELD = "nsPrefix";
        /** The admissible field names, in rendering order. */
        public static final List<String> FIELDS = List.of(NS_URI_FIELD, NS_PREFIX_FIELD);
    }

    /**
     * Recorded Ecore declaration of a structural feature: the parts that the
     * type graph does not determine. Every component is optional; an unset
     * component stands for the default that the type graph itself implies.
     * @param type the name of the declared Ecore data type, or {@code null}
     * @param ordered the declared {@code ordered} flag, or {@code null}
     * @param unique the declared {@code unique} flag, or {@code null}
     * @param bounds the declared multiplicity bounds, or {@code null}
     * @param name the Ecore name the GROOVE label was repaired from, or {@code null}
     */
    @AIGenerated("Claude Opus 5, 2026-09")
    public static record FeatureData(@Nullable String type, @Nullable Boolean ordered,
        @Nullable Boolean unique, @Nullable Bounds bounds, @Nullable String name) {
        /** Renders this data as an entry value, omitting the unset fields. */
        public String toValue() {
            StringBuilder result = new StringBuilder();
            EcoreFields.append(result, TYPE_FIELD, this.type);
            EcoreFields.append(result, ORDERED_FIELD, this.ordered);
            EcoreFields.append(result, UNIQUE_FIELD, this.unique);
            EcoreFields.append(result, BOUNDS_FIELD, this.bounds);
            EcoreFields.append(result, NAME_FIELD, this.name);
            return result.toString();
        }

        /** Parses an entry value of the {@link EcoreKey#FEATURE} form. */
        public static EcoreFields.Parsed<FeatureData> parse(String value) {
            var parsed = EcoreFields.parse(value, FIELDS);
            if (parsed.error() != null) {
                return parsed.retype();
            }
            var fields = parsed.getValue();
            String orderedText = fields.get(ORDERED_FIELD);
            String flagError = checkFlag(ORDERED_FIELD, orderedText);
            String uniqueText = fields.get(UNIQUE_FIELD);
            if (flagError == null) {
                flagError = checkFlag(UNIQUE_FIELD, uniqueText);
            }
            if (flagError != null) {
                return new EcoreFields.Parsed<>(null, flagError);
            }
            @Nullable
            Bounds bounds = null;
            String boundsText = fields.get(BOUNDS_FIELD);
            if (boundsText != null) {
                var parsedBounds = Bounds.parse(boundsText);
                if (parsedBounds.error() != null) {
                    return parsedBounds.retype();
                }
                bounds = parsedBounds.getValue();
            }
            return EcoreFields.Parsed
                .of(new FeatureData(fields.get(TYPE_FIELD), toFlag(orderedText), toFlag(uniqueText),
                    bounds, fields.get(NAME_FIELD)));
        }

        /** Checks the value of an optional boolean-valued field.
         * @return an error message, or {@code null} if the value is admissible
         */
        private static @Nullable String checkFlag(String field, @Nullable String text) {
            return text == null || text.equals(TRUE) || text.equals(FALSE)
                ? null
                : String
                    .format("has field '%s' with value '%s'; expected '%s' or '%s'", field, text,
                            TRUE, FALSE);
        }

        /** Converts the checked value of an optional boolean-valued field. */
        private static @Nullable Boolean toFlag(@Nullable String text) {
            return text == null
                ? null
                : Boolean.valueOf(text);
        }

        /** Name of the declared-data-type field. */
        public static final String TYPE_FIELD = "type";
        /** Name of the {@code ordered} flag field. */
        public static final String ORDERED_FIELD = "ordered";
        /** Name of the {@code unique} flag field. */
        public static final String UNIQUE_FIELD = "unique";
        /** Name of the multiplicity bounds field. */
        public static final String BOUNDS_FIELD = "bounds";
        /** Name of the original-Ecore-name field. */
        public static final String NAME_FIELD = "name";
        /** The admissible field names, in rendering order. */
        public static final List<String> FIELDS
            = List.of(TYPE_FIELD, ORDERED_FIELD, UNIQUE_FIELD, BOUNDS_FIELD, NAME_FIELD);
        /** Textual representation of the {@code true} flag value. */
        private static final String TRUE = "true";
        /** Textual representation of the {@code false} flag value. */
        private static final String FALSE = "false";
    }

    /**
     * Multiplicity bounds of a structural feature, written {@code lo..hi}
     * with {@code hi} either a number or {@code *} for unbounded.
     * @param lower the (non-negative) lower bound
     * @param upper the upper bound, or {@link #UNBOUNDED} if there is none
     */
    @AIGenerated("Claude Opus 5, 2026-09")
    public static record Bounds(int lower, int upper) {
        /** Indicates if the upper bound is unbounded. */
        public boolean isUnbounded() {
            return this.upper == UNBOUNDED;
        }

        @Override
        public String toString() {
            return this.lower + SEPARATOR + (isUnbounded()
                ? STAR
                : Integer.toString(this.upper));
        }

        /** Parses the value of a {@link FeatureData#BOUNDS_FIELD} field. */
        public static EcoreFields.Parsed<Bounds> parse(String text) {
            int split = text.indexOf(SEPARATOR);
            if (split < 0) {
                return malformed(text);
            }
            int lower;
            try {
                lower = Integer.parseInt(text.substring(0, split));
            } catch (NumberFormatException exc) {
                return malformed(text);
            }
            String upperText = text.substring(split + SEPARATOR.length());
            int upper;
            if (upperText.equals(STAR)) {
                upper = UNBOUNDED;
            } else {
                try {
                    upper = Integer.parseInt(upperText);
                } catch (NumberFormatException exc) {
                    return malformed(text);
                }
            }
            if (lower < 0 || upper < UNBOUNDED || (upper >= 0 && upper < lower)) {
                return malformed(text);
            }
            return EcoreFields.Parsed.of(new Bounds(lower, upper));
        }

        private static EcoreFields.Parsed<Bounds> malformed(String text) {
            return EcoreFields.Parsed
                .error("has field '%s' with value '%s'; expected 'lower%supper' "
                    + "with a non-negative lower bound and an upper bound of '%s' or at least "
                    + "the lower bound", FeatureData.BOUNDS_FIELD, text, SEPARATOR, STAR);
        }

        /** Value of the upper bound if the multiplicity is unbounded. */
        public static final int UNBOUNDED = -1;
        /** Separator between the two bounds. */
        private static final String SEPARATOR = "..";
        /** Textual representation of an unbounded upper bound. */
        private static final String STAR = "*";
    }

    /** Kind of an Ecore classifier, as recorded in the mapping. */
    @AIGenerated("Claude Opus 5, 2026-09")
    public static enum Kind {
        /** An ordinary (non-interface) class. */
        CLASS(EcoreNames.CLASS_KIND),
        /** An interface. */
        INTERFACE(EcoreNames.INTERFACE_KIND),
        /** An enumeration. */
        ENUM(EcoreNames.ENUM_KIND),
        /** A data type. */
        DATATYPE(EcoreNames.DATATYPE_KIND),;

        private Kind(String text) {
            this.text = text;
        }

        /** Returns the textual representation of this value, as used in the settings. */
        public String text() {
            return this.text;
        }

        private final String text;

        /** Returns the value with a given textual representation.
         * @throws IllegalArgumentException if {@code text} is not the representation of any value
         */
        public static Kind valueOfText(String text) throws IllegalArgumentException {
            var result = textMap.get().get(text);
            if (result == null) {
                throw Exceptions.illegalArg("Unknown Ecore classifier kind '%s'", text);
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
        private static final Factory<Map<String,@Nullable Kind>> textMap
            = Factory.lazy(Kind::createTextMap);

        private static Map<String,@Nullable Kind> createTextMap() {
            Map<String,@Nullable Kind> result = new LinkedHashMap<>();
            Arrays.stream(values()).forEach(k -> result.put(k.text(), k));
            return result;
        }
    }

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

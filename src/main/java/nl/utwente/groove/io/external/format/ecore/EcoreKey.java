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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.annotation.HelpMap;
import nl.utwente.groove.annotation.Syntax;
import nl.utwente.groove.annotation.ToolTipBody;
import nl.utwente.groove.annotation.ToolTipHeader;
import nl.utwente.groove.annotation.ToolTipPars;
import nl.utwente.groove.io.external.format.ecore.EcoreMapping.LiteralStyle;
import nl.utwente.groove.io.external.format.ecore.EcoreMapping.Ordering;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.parse.IdValidator;

/**
 * Vocabulary of the Ecore mapping: the key forms that an {@code ecore}
 * settings resource may declare. A key form consists of a <i>choice key</i>
 * (the last {@code .}-separated segment of an entry key) plus the admissible
 * lengths of the Ecore element path preceding it, and knows how to validate
 * the entry value. Several forms may share a choice key; they are told apart
 * by the path length, in declaration order.
 * @author Arend Rensink
 */
@NonNullByDefault
public enum EcoreKey {
    /** Global ordering encoding. */
    @Syntax("ORDERING = value")
    @ToolTipHeader("Global ordering encoding")
    @ToolTipBody({"Determines how ordered or non-unique many-valued features are encoded:",
            "as plain edges (losing the order), or through intermediate nodes",
            "carrying an index attribute."})
    @ToolTipPars({"the encoding: 'none' (default) or 'index'"})
    ORDERING("ordering", 0, 0, "none", EcoreKey::checkOrdering),
    /** Global use of {@code xmi:id} values. */
    @Syntax("USE_IDENTIFIERS = flag")
    @ToolTipHeader("Use of xmi:id values")
    @ToolTipBody({"Determines whether xmi:id values in instance models are turned into",
            "id: aspects of host graph nodes, and regenerated on export."})
    @ToolTipPars({"'true' (default) or 'false'"})
    USE_IDENTIFIERS("useIdentifiers", 0, 0, "true", EcoreKey::checkBoolean),
    /** Per-feature override of the ordering encoding. */
    @Syntax("class.DOT.feature.DOT.ORDERING = value")
    @ToolTipHeader("Per-feature ordering encoding")
    @ToolTipBody({"Overrides the global ordering encoding for a single feature.",
            "An entry that does not resolve against the metamodel at hand is ignored;",
            "an ambiguously resolving entry is an error."})
    @ToolTipPars({"the Ecore class declaring the feature; package qualification is allowed,"
            + " and needed if the plain name is ambiguous",
            "the structural feature whose encoding is overridden",
            "the encoding: 'none' or 'index'"})
    FEATURE_ORDERING("ordering", 2, EcoreKey.UNBOUNDED, "index", EcoreKey::checkOrdering, "class",
        "feature"),
    /** Classifier type name override. */
    @Syntax("classifier.DOT.TYPE_NAME = name")
    @ToolTipHeader("Classifier type name")
    @ToolTipBody({"Overrides the GROOVE type name derived for an Ecore classifier.",
            "Derived names of other classifiers are disambiguated around the override."})
    @ToolTipPars({"the Ecore class, data type or enum; package qualification is allowed,"
            + " and needed if the plain name is ambiguous",
            "the GROOVE type name to be used"})
    TYPE_NAME("typeName", 1, EcoreKey.UNBOUNDED, "<name>", EcoreKey::checkTypeName, "classifier"),
    /** Per-enum literal naming style. */
    @Syntax("enum.DOT.LITERAL_STYLE = value")
    @ToolTipHeader("Enum literal naming style")
    @ToolTipBody({"Determines how the literal types of an enum are named:",
            "qualified by the enum name (E$L) or by the plain literal name (L)."})
    @ToolTipPars({"the Ecore enum; package qualification is allowed,"
            + " and needed if the plain name is ambiguous",
            "the style: 'qualified' (default) or 'plain'"})
    LITERAL_STYLE("literalStyle", 1, EcoreKey.UNBOUNDED, "plain", EcoreKey::checkLiteralStyle,
        "enum"),
    /** Enum literal type name override. */
    @Syntax("enum.DOT.literal.DOT.TYPE_NAME = name")
    @ToolTipHeader("Enum literal type name")
    @ToolTipBody({"Overrides the GROOVE type name of a single enum literal;",
            "wins over the enum's literal naming style."})
    @ToolTipPars({"the Ecore enum; package qualification is allowed,"
            + " and needed if the plain name is ambiguous", "the enum literal",
            "the GROOVE type name to be used"})
    LITERAL_TYPE_NAME("typeName", 2, EcoreKey.UNBOUNDED, "<name>", EcoreKey::checkTypeName, "enum",
        "literal"),;

    private EcoreKey(String text, int minPath, int maxPath, String templateValue,
                     Function<String,@Nullable String> valueCheck, String... pathParts) {
        this.text = text;
        this.minPath = minPath;
        this.maxPath = maxPath;
        this.templateValue = templateValue;
        this.valueCheck = valueCheck;
        this.pathParts = pathParts;
    }

    /** Returns the choice key of this key form, being the last segment of its
     * entry keys. */
    public String text() {
        return this.text;
    }

    private final String text;

    /** Indicates if this is a global option, taking no Ecore element path. */
    public boolean isGlobal() {
        return this.maxPath == 0;
    }

    private final int minPath;
    private final int maxPath;

    /**
     * Checks an entry value against this key form.
     * @return an error description completing the sentence
     * "Value 'v' of 'key' ...", or {@code null} if the value is admissible
     */
    public @Nullable String checkValue(String value) {
        return this.valueCheck.apply(value);
    }

    private final Function<String,@Nullable String> valueCheck;

    /** Returns the canonical pattern of this key form, e.g.
     * {@code <class>.<feature>.ordering}. */
    public String pattern() {
        StringBuilder result = new StringBuilder();
        for (String part : this.pathParts) {
            result.append('<').append(part).append('>').append('.');
        }
        return result.append(this.text).toString();
    }

    private final String[] pathParts;

    /**
     * Returns the example line for this key form in a generated settings
     * resource: the pattern with the default value for global keys, an
     * example value for the per-element forms.
     */
    public String templateLine() {
        return pattern() + " = " + this.templateValue;
    }

    private final String templateValue;

    /**
     * Returns the key form matching a given choice key and element path
     * length, or {@code null} if there is none. If several forms match, the
     * first in declaration order is returned.
     */
    public static @Nullable EcoreKey lookup(String choice, int pathLength) {
        for (EcoreKey key : values()) {
            if (key.text.equals(choice) && key.minPath <= pathLength
                && pathLength <= key.maxPath) {
                return key;
            }
        }
        return null;
    }

    /** Returns all key forms with a given choice key, in declaration order. */
    public static List<EcoreKey> withText(String choice) {
        return Arrays.stream(values()).filter(key -> key.text.equals(choice)).toList();
    }

    /** Returns the patterns of a list of key forms, joined by "or". */
    public static String patterns(List<EcoreKey> keys) {
        return keys.stream().map(EcoreKey::pattern).collect(Collectors.joining(" or "));
    }

    /**
     * Returns a syntax help map for the key forms, from (HTML-formatted)
     * syntax lines to tool tips, harvested from the annotations on the enum
     * constants.
     */
    public static HelpMap getDocMap() {
        var result = docMap;
        if (result == null) {
            docMap = result = computeDocMap();
        }
        return result;
    }

    private static HelpMap computeDocMap() {
        var result = new HelpMap();
        for (EcoreKey key : values()) {
            try {
                result.add(EcoreKey.class.getField(key.name()), tokenMap);
            } catch (NoSuchFieldException exc) {
                throw Exceptions.illegalState("Missing enum field %s", key.name());
            }
        }
        return result;
    }

    /** Syntax helper map, from syntax items to associated tool tips. */
    private static @Nullable HelpMap docMap;

    /** Mapping from tokens in the syntax annotations to corresponding text. */
    private static final Map<String,String> tokenMap = Map
        .of("DOT", ".", "ORDERING", ORDERING.text(), "USE_IDENTIFIERS", USE_IDENTIFIERS.text(),
            "TYPE_NAME", TYPE_NAME.text(), "LITERAL_STYLE", LITERAL_STYLE.text());

    private static @Nullable String checkOrdering(String value) {
        return Ordering.hasText(value)
            ? null
            : "should be one of " + Ordering.texts();
    }

    private static @Nullable String checkBoolean(String value) {
        return value.equals("true") || value.equals("false")
            ? null
            : "should be true or false";
    }

    private static @Nullable String checkTypeName(String value) {
        return IdValidator.JAVA_ID_NON_RESERVED.isValid(value)
            ? null
            : "is not a valid GROOVE type name";
    }

    private static @Nullable String checkLiteralStyle(String value) {
        return LiteralStyle.hasText(value)
            ? null
            : "should be one of " + LiteralStyle.texts();
    }

    /** Path length bound for key forms whose element path may be arbitrarily
     * package-qualified. */
    private static final int UNBOUNDED = Integer.MAX_VALUE;
}

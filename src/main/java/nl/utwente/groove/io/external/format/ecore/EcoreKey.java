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
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.io.external.format.ecore.EcoreMapping.LiteralStyle;
import nl.utwente.groove.io.external.format.ecore.EcoreMapping.Ordering;
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
    ORDERING("ordering", 0, 0, EcoreKey::checkOrdering),
    /** Global use of {@code xmi:id} values. */
    USE_IDENTIFIERS("useIdentifiers", 0, 0, EcoreKey::checkBoolean),
    /** Per-feature override of the ordering encoding. */
    FEATURE_ORDERING("ordering", 2, EcoreKey.UNBOUNDED, EcoreKey::checkOrdering, "class", "feature"),
    /** Classifier type name override. */
    TYPE_NAME("typeName", 1, EcoreKey.UNBOUNDED, EcoreKey::checkTypeName, "classifier"),
    /** Per-enum literal naming style. */
    LITERAL_STYLE("literalStyle", 1, EcoreKey.UNBOUNDED, EcoreKey::checkLiteralStyle, "enum"),
    /** Enum literal type name override. */
    LITERAL_TYPE_NAME("typeName", 2, EcoreKey.UNBOUNDED, EcoreKey::checkTypeName, "enum", "literal"),;

    private EcoreKey(String text, int minPath, int maxPath,
                     Function<String,@Nullable String> valueCheck, String... pathParts) {
        this.text = text;
        this.minPath = minPath;
        this.maxPath = maxPath;
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
     * Returns the key form matching a given choice key and element path
     * length, or {@code null} if there is none. If several forms match, the
     * first in declaration order is returned.
     */
    public static @Nullable EcoreKey lookup(String choice, int pathLength) {
        return Arrays
            .stream(values())
            .filter(key -> key.text.equals(choice))
            .filter(key -> key.minPath <= pathLength && pathLength <= key.maxPath)
            .findFirst()
            .orElse(null);
    }

    /** Returns all key forms with a given choice key, in declaration order. */
    public static List<EcoreKey> withText(String choice) {
        return Arrays.stream(values()).filter(key -> key.text.equals(choice)).toList();
    }

    /** Returns the patterns of a list of key forms, joined by "or". */
    public static String patterns(List<EcoreKey> keys) {
        return keys.stream().map(EcoreKey::pattern).collect(Collectors.joining(" or "));
    }

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

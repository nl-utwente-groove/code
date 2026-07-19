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
package nl.utwente.groove.explore.config;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.io.HTMLConverter;
import nl.utwente.groove.util.Strings;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.util.parse.IdValidator;
import nl.utwente.groove.util.parse.Parser;

/**
 * Parser for settings of a given exploration key. The textual form is
 * <i>kind</i>{@code :}<i>content</i>, where the content part (with the
 * separator) is omitted for kinds without content, and the kind part (with the
 * separator) may be omitted for the key's designated content-only kind (see
 * {@link ExploreKey#getKindMap()}). The empty string parses to the key's
 * default setting.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class SettingParser extends Parser.AParser<Setting> {
    /**
     * Constructs a parser for settings of a given key.
     */
    public SettingParser(ExploreKey key) {
        super(createDescription(key), key.getDefaultSetting());
        this.key = key;
        this.kindMap = key.getKindMap();
    }

    /** Returns the exploration key of this setting parser. */
    private ExploreKey getKey() {
        return this.key;
    }

    private final ExploreKey key;

    /** Returns the kind corresponding to a given kind name, if any. */
    private Setting.@Nullable Kind getKind(String name) {
        return this.kindMap.get(name);
    }

    private final SettingKindMap kindMap;

    /** Creates a description of the values expected for a given exploration key. */
    static private String createDescription(ExploreKey key) {
        StringBuilder result = new StringBuilder("<body>");
        result
            .append("A value of the form <i>kind</i>:<i>content</i> "
                + "(without the separator if there is no content), where <i>kind</i> is one of");
        for (var kind : key.getKindType().getEnumConstants()) {
            result.append("<li> - ");
            result.append(HTMLConverter.ITALIC_TAG.on(kind.getName()));
            result.append(": ");
            result.append(kind.getExplanation());
            if (kind.contentType() != Setting.ContentType.NULL) {
                result.append(", with content ");
                result.append(Strings.toLower(kind.parser().getDescription()));
            }
        }
        return result.toString();
    }

    @Override
    public Setting parse(String input) throws FormatException {
        if (input.isEmpty()) {
            return getDefaultValue();
        }
        SplitPair splitText = split(input);
        String name = splitText.name();
        Setting.Kind kind = getKind(name);
        if (kind == null) {
            if (name.isEmpty()) {
                throw new FormatException("Value '%s' should start with a kind name", input);
            } else {
                throw new FormatException("Unknown kind '%s' for key '%s'", name,
                    getKey().getName());
            }
        }
        return kind.parser().parse(splitText.content());
    }

    /**
     * Splits a string into (potential) kind name and (potential) content part.
     * The string is split at the first occurrence of {@link #CONTENT_SEPARATOR}.
     * If there is no {@link #CONTENT_SEPARATOR} and {@code input}
     * is an identifier, {@code input} is assumed to be a name with empty content;
     * otherwise, it is assumed to be content for the empty (default) name.
     */
    private SplitPair split(String input) {
        SplitPair result;
        int pos = input.indexOf(CONTENT_SEPARATOR);
        if (pos < 0) {
            if (IdValidator.GROOVE_ID.isValid(input) && getKind(input) != null) {
                result = new SplitPair(input, "");
            } else {
                result = new SplitPair("", input);
            }
        } else {
            result = new SplitPair(input.substring(0, pos), input.substring(pos + 1));
        }
        return result;
    }

    @Override
    public <V extends Setting> String unparse(V value) {
        String result = "";
        if (!isDefault(value)) {
            Setting.Kind kind = value.kind();
            String contentString = kind.parser().unparse(value);
            if (contentString.isEmpty()) {
                result = kind.getName();
            } else if (getKind("") == kind) {
                result = contentString;
            } else {
                result = kind.getName() + CONTENT_SEPARATOR + contentString;
            }
        }
        return result;
    }

    @Override
    public boolean isValue(Object value) {
        return value instanceof Setting setting
            && getKey().getKindType().isInstance(setting.kind());
    }

    /** Separator between kind name and (optional) setting content. */
    private static final char CONTENT_SEPARATOR = ':';

    private record SplitPair(String name, String content) {
        // no additional functionality
    }
}

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
package nl.utwente.groove.grammar.model;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.parse.FormatError;

/**
 * Parsed content of a settings resource: the raw text, the entries it declares
 * (as a {@link Properties} object) and the position in the text at which every
 * key is declared. The positions are what allows the errors of a settings
 * resource to be reported with line and column numbers, so that selecting the
 * error in the display jumps to the offending line.
 * <p>
 * The positions are found by a line scanner over the raw text, which
 * approximates the {@link Properties} syntax as follows:
 * <ul>
 * <li>Natural lines are delimited by {@code \n}, {@code \r} or {@code \r\n} and
 * numbered from 1; blank lines and lines whose first non-whitespace character
 * is {@code #} or {@code !} are skipped.
 * <li>A natural line ending in an odd number of backslashes continues onto the
 * next natural line; the position recorded is that of the first line of such a
 * logical line, so a key spread over continuation lines is located at its
 * start.
 * <li>The key runs from the first non-whitespace character up to the first
 * unescaped {@code =}, {@code :} or whitespace character; the escapes
 * {@code \t}, {@code \n}, {@code \r}, {@code \f} and {@code \\u}<i>hhhh</i> are
 * converted, and any other backslash-escaped character stands for itself (as in
 * {@link Properties}). Deviation: a malformed {@code \\u} escape is left as
 * written rather than being rejected, since a position map is no place to
 * report syntax errors — the {@link Properties} parser itself reports them.
 * <li>If a key is declared more than once, the position of the <i>last</i>
 * declaration is recorded, matching the entry that {@link Properties} keeps.
 * </ul>
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class SettingsContent {
    /**
     * Constructs the content of a given settings text, by parsing it as
     * properties and scanning it for key positions.
     * @param text the raw settings text
     * @throws IOException if the text is not in properties syntax
     */
    public SettingsContent(String text) throws IOException {
        this.text = text;
        this.properties = new Properties();
        this.properties.load(new StringReader(text));
        scan(text);
    }

    /** Returns the raw text this content was parsed from. */
    public String getText() {
        return this.text;
    }

    /** The raw settings text. */
    private final String text;

    /** Returns the entries declared in the text. */
    public Properties properties() {
        return this.properties;
    }

    /** The entries declared in the text. */
    private final Properties properties;

    /**
     * Returns the position at which a given key is declared in the text.
     * @return the position of {@code key}, or {@code null} if the key is not
     * declared in the text
     */
    public @Nullable Position position(String key) {
        return this.positionMap.get(key);
    }

    /**
     * Returns the position of a given key as a list of numbers (line first,
     * then column), to be passed as a context argument to a
     * {@link FormatError}; empty if the key is not declared in the text.
     */
    public List<Integer> numbers(String key) {
        var position = position(key);
        return position == null
            ? List.of()
            : List.of(position.line(), position.column());
    }

    /**
     * Returns the position of a given key in a (possibly {@code null}) content,
     * as a list of numbers; empty if there is no content or the key is not
     * declared in it. Convenience for checks that are shared between the
     * position-aware and the position-less form.
     * @see #numbers(String)
     */
    public static List<Integer> numbers(@Nullable SettingsContent content, String key) {
        return content == null
            ? List.of()
            : content.numbers(key);
    }

    /** Mapping from declared keys to their positions in the text. */
    private final Map<String,@Nullable Position> positionMap = new LinkedHashMap<>();

    /** Scans a settings text for the positions of the keys it declares. */
    private void scan(String text) {
        List<String> lines = splitLines(text);
        int i = 0;
        while (i < lines.size()) {
            String first = lines.get(i);
            int start = firstNonWhitespace(first);
            char kind = start < 0
                ? '#'
                : first.charAt(start);
            if (start < 0 || kind == '#' || kind == '!') {
                // a blank or comment line declares nothing
                i++;
                continue;
            }
            // collect the logical line, which may continue onto further lines
            StringBuilder logical = new StringBuilder(first.substring(start));
            int last = i;
            while (isContinued(logical)) {
                logical.setLength(logical.length() - 1);
                last++;
                if (last >= lines.size()) {
                    break;
                }
                String next = lines.get(last);
                int nextStart = firstNonWhitespace(next);
                if (nextStart >= 0) {
                    logical.append(next.substring(nextStart));
                }
            }
            this.positionMap.put(getKey(logical.toString()), new Position(i + 1, start + 1));
            i = last + 1;
        }
    }

    /** Splits a text into its natural lines, delimited by {@code \n},
     * {@code \r} or {@code \r\n}. */
    private static List<String> splitLines(String text) {
        List<String> result = new ArrayList<>();
        int start = 0;
        int i = 0;
        while (i < text.length()) {
            char c = text.charAt(i);
            if (c == '\n' || c == '\r') {
                result.add(text.substring(start, i));
                if (c == '\r' && i + 1 < text.length() && text.charAt(i + 1) == '\n') {
                    i++;
                }
                i++;
                start = i;
            } else {
                i++;
            }
        }
        if (start < text.length()) {
            result.add(text.substring(start));
        }
        return result;
    }

    /** Returns the index of the first non-whitespace character of a line,
     * or {@code -1} if the line is blank. */
    private static int firstNonWhitespace(String line) {
        for (int i = 0; i < line.length(); i++) {
            if (!Character.isWhitespace(line.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    /** Tests if a (partial) logical line continues onto the next natural line,
     * which is the case if it ends in an odd number of backslashes. */
    private static boolean isContinued(StringBuilder line) {
        int count = 0;
        for (int i = line.length() - 1; i >= 0 && line.charAt(i) == '\\'; i--) {
            count++;
        }
        return count % 2 == 1;
    }

    /** Extracts the key declared by a logical line, which is assumed to start
     * at the first non-whitespace character. */
    private static String getKey(String line) {
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < line.length()) {
            char c = line.charAt(i);
            if (c == '\\' && i + 1 < line.length()) {
                char escaped = line.charAt(i + 1);
                i += 2;
                switch (escaped) {
                case 't' -> result.append('\t');
                case 'n' -> result.append('\n');
                case 'r' -> result.append('\r');
                case 'f' -> result.append('\f');
                case 'u' -> {
                    if (i + 4 <= line.length()) {
                        try {
                            result.append((char) Integer.parseInt(line.substring(i, i + 4), 16));
                            i += 4;
                        } catch (NumberFormatException exc) {
                            // a malformed escape is left as written
                            result.append(escaped);
                        }
                    } else {
                        result.append(escaped);
                    }
                }
                default -> result.append(escaped);
                }
            } else if (c == '=' || c == ':' || Character.isWhitespace(c)) {
                break;
            } else {
                result.append(c);
                i++;
            }
        }
        return result.toString();
    }

    /** Position of a key declaration in a settings text: the 1-based number of
     * the line the key starts on, and the 1-based column of its first
     * character. */
    public static record Position(int line, int column) {
        // empty by design
    }
}

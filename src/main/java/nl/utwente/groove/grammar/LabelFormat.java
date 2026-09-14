/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2026 University of Twente
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
package nl.utwente.groove.grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Parsed form of a special transition label (see {@link Action#getSpecialLabel()}).
 * The label is a format string in a restricted {@link String#format} syntax:
 * <ul>
 * <li>{@code %s} inserts the next rule parameter, in order of occurrence;
 * <li>{@code %i$s} inserts the {@code i}-th rule parameter ({@code i} counting from 1);
 * <li>{@code %%} inserts a percent sign;
 * <li>every other character stands for itself.
 * </ul>
 * Flags, width, precision and other conversions are rejected, since they
 * distort the rendered label (padding, radix) and cannot be inverted (gh #877).
 * As with {@link String#format}, the implicit numbering of {@code %s} runs
 * independently of the explicit indices.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5.1, 2026-09")
public final class LabelFormat {
    /**
     * Constructs a format from its parsed constituents.
     * @param format the original format string
     * @param literals the literal text segments; one more than there are parameter references
     * @param indices the (0-based) parameter index of each reference
     */
    private LabelFormat(String format, List<String> literals, int[] indices) {
        this.format = format;
        this.literals = literals.toArray(new String[0]);
        this.indices = indices;
        int arity = 0;
        for (int index : indices) {
            arity = Math.max(arity, index + 1);
        }
        this.arity = arity;
    }

    /** Returns the format string from which this object was parsed. */
    public String getFormat() {
        return this.format;
    }

    private final String format;

    /**
     * Returns the number of arguments this format expects, being one more
     * than the highest parameter index it refers to.
     */
    public int getArity() {
        return this.arity;
    }

    private final int arity;

    /**
     * Renders the label for a given list of arguments.
     * Every argument is inserted as its {@link String#valueOf(Object)} form.
     * @param args the arguments; there must be at least {@link #getArity()} of them
     * @throws IllegalArgumentException if there are fewer arguments than {@link #getArity()}
     */
    public String apply(Object... args) {
        if (args.length < this.arity) {
            throw Exceptions
                .illegalArg("Format '%s' expects %s arguments, got %s", this.format, this.arity,
                            args.length);
        }
        var result = new StringBuilder(this.literals[0]);
        for (int i = 0; i < this.indices.length; i++) {
            result.append(String.valueOf(args[this.indices[i]]));
            result.append(this.literals[i + 1]);
        }
        return result.toString();
    }

    /** The literal text segments surrounding the parameter references. */
    private final String[] literals;
    /** The parameter index of each reference, in order of occurrence. */
    private final int[] indices;

    @Override
    public String toString() {
        return this.format;
    }

    /**
     * Parses a format string.
     * @throws FormatException if the string contains a format specifier other
     * than {@code %s}, {@code %i$s} or {@code %%}
     */
    public static LabelFormat parse(String format) throws FormatException {
        var literals = new ArrayList<String>();
        var indices = new ArrayList<Integer>();
        var literal = new StringBuilder();
        int ordinal = 0;
        // end of the previous specifier
        int end = 0;
        var matcher = SPECIFIER.matcher(format);
        while (matcher.find()) {
            literal.append(checkLiteral(format, format.substring(end, matcher.start())));
            end = matcher.end();
            var spec = format.substring(matcher.start(), end);
            var index = matcher.group(1);
            var flags = matcher.group(2);
            boolean plain = (flags == null || flags.isEmpty()) && matcher.group(3) == null
                && matcher.group(4) == null && matcher.group(5) == null;
            var conversion = matcher.group(6);
            assert conversion != null : "The conversion group is mandatory";
            switch (conversion) {
            case "s" -> {
                if (!plain) {
                    throw unsupported(spec, format);
                }
                int i;
                if (index == null) {
                    i = ordinal++;
                } else {
                    // strip the trailing '$'
                    i = Integer.parseInt(index.substring(0, index.length() - 1)) - 1;
                    if (i < 0) {
                        throw unsupported(spec, format);
                    }
                }
                indices.add(i);
                literals.add(literal.toString());
                literal.setLength(0);
            }
            case "%" -> {
                if (!plain || index != null) {
                    throw unsupported(spec, format);
                }
                literal.append('%');
            }
            default -> throw unsupported(spec, format);
            }
        }
        literal.append(checkLiteral(format, format.substring(end)));
        literals.add(literal.toString());
        return new LabelFormat(format, literals,
            indices.stream().mapToInt(Integer::intValue).toArray());
    }

    /**
     * Checks that a stretch of text between specifiers contains no stray percent sign.
     * @return the text itself, if it passes the check
     */
    private static String checkLiteral(String format, String text) throws FormatException {
        if (text.indexOf('%') >= 0) {
            throw new FormatException("Incomplete format specifier in '%s'", format);
        }
        return text;
    }

    /** Constructs the exception for an unsupported specifier. */
    private static FormatException unsupported(String spec, String format) {
        return new FormatException(
            "Unsupported format specifier '%s' in '%s': only %%s, %%i$s and %%%% are allowed", spec,
            format);
    }

    /**
     * The format specifier syntax of {@link java.util.Formatter}:
     * index, flags, width, precision, date/time prefix and conversion.
     */
    private static final Pattern SPECIFIER
        = Pattern.compile("%(\\d+\\$)?([-#+ 0,(<]*)(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])");
}

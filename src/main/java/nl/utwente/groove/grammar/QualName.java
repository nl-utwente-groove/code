// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2023 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id$
 *
 * Angela Lozano's thesis. EMOOSE student 2002 - 2003 EMOOSE (European Master in
 * Object-Oriented & Software Engineering technologies) Vrije Universiteit
 * Brussel - Ecole des Mines de Nantes - Universiteit Twente
 */
package nl.utwente.groove.grammar;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.line.Line;
import nl.utwente.groove.util.parse.Fallible;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.util.parse.IdValidator;
import nl.utwente.groove.util.parse.Parser;

/**
 * Representation of a qualified name. A qualified name is a
 * name consisting of a nonempty sequence of tokens, separated by
 * {@link #SEPARATOR} characters. Each individual token may be empty. The prefix
 * without the last token is called the parent (which is <tt>null</tt> if there
 * is only a single token); the last token is called the child.
 *
 * @author Angela Lozano and Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:32:37 $
 */
@NonNullByDefault
public class QualName extends ModuleName implements Comparable<QualName>, Fallible {
    /**
     * Creates a new qualified name, on the basis of a given non-empty list of tokens.
     * The name is not tested for validity.
     * @param tokens the list of tokens for the qualified name
     */
    public QualName(List<String> tokens) {
        this.tokens.addAll(tokens);
    }

    /**
     * Creates a new qualified name, on the basis of a given non-empty list of tokens.
     * The name is not tested for validity.
     * @param tokens the list of tokens for the qualified name
     */
    public QualName(String... tokens) {
        Arrays.stream(tokens).forEach(this.tokens::add);
    }

    /** Constructor for internal consumption, to construct qualified names more efficiently. */
    QualName() {
        // empty
    }

    /**
     * Tests whether this name is valid, i.e., contains only allowed
     * characters, and throws an appropriate exception otherwise.
     * @return this object, if it valid
     * @throws FormatException if the qualified name contains errors
     */
    public QualName testValid() throws FormatException {
        getErrors().throwException();
        return this;
    }

    @Override
    public FormatErrorSet getErrors() {
        FormatErrorSet result = this.errors;
        if (result == null) {
            this.errors = result = new FormatErrorSet();
            if (this.tokens.isEmpty()) {
                result.add("Qualified name is empty");
            }
            for (String token : this.tokens) {
                if (token.equals(WILDCARD)) {
                    continue;
                }
                try {
                    tokenValidator.testValid(token);
                } catch (FormatException exc) {
                    for (FormatError err : exc.getErrors()) {
                        result.add("Error in qualified name %s: %s", this, err);
                    }
                }
            }
        }
        return result;
    }

    /** Non-{@code null} exception in case this name has been found to be invalid. */
    private @Nullable FormatErrorSet errors;

    /** Returns the line consisting of the flattened qualified name. */
    public Line toLine() {
        return Line.atom(toString());
    }

    @Override
    public int compareTo(QualName o) {
        int result = 0;
        int minSize = Math.min(size(), o.size());
        for (int i = 0; result == 0 && i < minSize; i++) {
            result = get(i).compareTo(o.get(i));
        }
        if (result == 0) {
            result = size() - o.size();
        }
        return result;
    }

    /**
     * Indicates whether this qualified name has a (nun-<tt>null</tt>)
     * parent.
     * @return <tt>true</tt> if this name has a (non-<tt>null</tt>) parent
     */
    public boolean hasParent() {
        return this.parent != null;
    }

    /**
     * Returns the parent qualified name (all tokens except the last), or
     * <tt>null</tt> if there is no parent name. There is no parent
     * name iff the qualified name consists of a single token only.
     * @return the parent qualified name
     */
    public ModuleName parent() {
        var result = this.parent;
        if (result == null) {
            if (size() == 1) {
                result = ModuleName.TOP;
            } else {
                result = new QualName();
                for (int i = 0; i < size() - 1; i++) {
                    result.tokens.add(get(i));
                }
            }
            this.parent = result;
        }
        return result;
    }

    /** The parent qualified name (may be {@code null}). */
    private @Nullable ModuleName parent;

    /**
     * Returns the last token of this qualified name.
     * @return the last token of the qualified name
     */
    public String last() {
        return get(size() - 1);
    }

    /** Returns the longest common parent containing both this name and
     * a given other module name. This name has to be properly contained,
     * i.e., the result will always be shorter than this name.
     */
    public ModuleName getCommonParent(ModuleName other) {
        ModuleName result = new ModuleName();
        int maxSize = Math.max(size() - 1, other.size());
        boolean equal = true;
        for (int i = 0; equal && i < maxSize; i++) {
            if (tokens().get(i).equals(other.tokens().get(i))) {
                result.tokens.add(this.tokens().get(i));
            } else {
                equal = false;
            }
        }
        return result;
    }

    /** Removes part of the ancestry of this qualified name.
     * @param parent ancestry of this name, from the top down
     * @return this name with parent removed from the ancestry;
     * or this name (unchanged) if parent is not actually part of the ancestry
     */
    public QualName removeParent(ModuleName parent) {
        QualName result;
        if (parent.contains(this) && parent.size() < size()) {
            result = new QualName();
            for (int i = parent.size(); i < size(); i++) {
                result.tokens.add(this.tokens.get(i));
            }
        } else {
            result = this;
        }
        return result;
    }

    /** Returns a valid qualified constructed from this one,
     * by replacing all illegal characters (including wildcards).
     */
    public QualName toValidName() {
        QualName result = new QualName();
        for (String token : tokens()) {
            result.tokens.add(nameValidator.repair(token));
        }
        return result;
    }

    /** Turns this qualified name into a {@link File} object with a given extension. */
    public File toFile(String extension) {
        return new File(parent().toFile(), last() + extension);
    }

    /**
     * Turns a number of strings into a qualified name.
     * The name is not tested for validity.
     */
    public static QualName name(String... tokens) {
        return new QualName(tokens);
    }

    /**
     * Parses a {@link QualName#SEPARATOR_CHAR}-separated string into a qualified name.
     */
    public static QualName parse(String text) {
        return parse(text, SEPARATOR_CHAR);
    }

    /**
     * Parses a file name into a qualified name.
     */
    public static QualName parse(File filename) {
        return parse(filename.getPath(), File.separatorChar);
    }

    /**
     * Parses a string into a qualified name, using a given token separator.
     */
    public static QualName parse(String text, char separator) {
        QualName result = new QualName();
        StringBuilder currentFragment = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == separator) {
                result.tokens.add(currentFragment.toString());
                currentFragment = new StringBuilder();
            } else {
                currentFragment.append(c);
            }
        }
        result.tokens.add(currentFragment.toString());
        return result;
    }

    /**
     * Validator for qualified name tokens.
     * The rules for qualified name tokens are those of Java identifiers,
     * except that, in addition, hyphens may be used as internal characters.
     */
    public static final IdValidator tokenValidator = new IdValidator() {
        @Override
        public boolean isIdentifierPart(char c) {
            return c == '-' || super.isIdentifierPart(c);
        }
    };

    /**
     * Validator for qualified names.
     * The rules for qualified name tokens are those of Java identifiers,
     * except that, in addition, hyphens may be used as internal characters.
     */
    public static final IdValidator nameValidator = new IdValidator() {
        @Override
        public boolean isIdentifierPart(char c) {
            return c == '-' || super.isIdentifierPart(c);
        }

        @Override
        public boolean isSeparator(char c) {
            return c == SEPARATOR_CHAR;
        }
    };

    /** Returns a parser for qualified names. */
    public static final Parser<QualName> parser() {
        var result = PARSER;
        if (result == null) {
            result = Parser
                .newParser("Qualified name", QualName.class, QualName::parse, n -> n.toString());
            PARSER = result;
        }
        return result;
    }

    /** Parser for qualified names. */
    private static @Nullable Parser<QualName> PARSER;

    /** Returns a parser for space-separated lists of qualified names. */
    public static final Parser<List<QualName>> listParser() {
        var result = LIST_PARSER;
        if (result == null) {
            LIST_PARSER = result = new Parser.SplitParser<>(parser());
        }
        return result;
    }

    /** The singleton parser for space-separated lists of qualified names. */
    private static @Nullable Parser<List<QualName>> LIST_PARSER;
}
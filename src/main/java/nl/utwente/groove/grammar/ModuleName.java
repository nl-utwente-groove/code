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
package nl.utwente.groove.grammar;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.Groove;

/**
 * Name of a module within a grammar.
 * Serves as namespace for qualified names.
 * in contrast to a qualified name, a module name may be empty.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class ModuleName {
    /**
     * Constructs an initially empty module name.
     */
    ModuleName() {
        this.tokens = new ArrayList<>();
    }

    /**
     * Constructs an module name from a given list of tokens.
     */
    ModuleName(List<String> tokens) {
        this.tokens = new ArrayList<>(tokens);
    }

    /**
     * Returns the tokens in this module name as an array of strings.
     */
    public List<String> tokens() {
        return this.tokens;
    }

    /**
     * Returns the number of tokens in the module name.
     * @return number of tokens in this module name
     */
    public int size() {
        return tokens().size();
    }

    /** Indicates if this is the top module name, i.e., without any tokens. */
    public boolean isTop() {
        return size() == 0;
    }

    /**
     * Returns the token in this name at a specific instance
     * @param i the index at which the token is requested
     * @return the token at index <tt>i</tt>
     * @require <tt>0 <= i && i < size()</tt>
     * @ensure </tt>return == tokens[i]</tt>
     */
    public String get(int i) {
        return tokens().get(i);
    }

    /** The tokens of which this module name consists. */
    final List<String> tokens;

    /** Returns a new module name consisting of the tokens of this one,
     * followed by the tokens of another one.
     */
    public ModuleName concat(ModuleName other) {
        var result = new ModuleName(this.tokens);
        result.tokens.addAll(other.tokens);
        return result;
    }

    /** Extends this module name with a child, and returns the result. */
    public QualName extend(String... children) {
        QualName result = new QualName(tokens());
        result.tokens.addAll(Arrays.asList(children));
        return result;
    }

    /** Returns a qualified name, extending this module name
     * with a nested qualified name. The tokens of the resulting name
     * consist of the tokens of this module name,
     * followed by the tokens of a given qualified name.
     */
    public QualName extend(QualName other) {
        var result = new QualName(this.tokens);
        result.tokens.addAll(other.tokens);
        return result;
    }

    /** Nests this module name inside a new outer token, and returns the result. */
    public QualName nest(String parent) {
        QualName result = new QualName();
        result.tokens.add(parent);
        result.tokens.addAll(tokens());
        return result;
    }

    /** Indicates if the module name contains a wildcard text as last token. */
    public boolean hasWildCard() {
        return tokens().contains(WILDCARD);
    }

    /**
     * Indicates if this module name matches another
     * Module names match if they are equal, except that a wildcard
     * in one module name equals any corresponding token in the other.
     */
    public boolean matches(ModuleName other) {
        boolean result = size() <= other.size();
        for (int i = 0; result && i < size(); i++) {
            String myToken = get(i);
            if (myToken.equals(WILDCARD)) {
                assert i == size() - 1 : "Wildcard may only appear as last token in qualified name";
                break;
            }
            String otherToken = other.get(i);
            if (otherToken.equals(WILDCARD)) {
                continue;
            }
            result = myToken.equals(otherToken);
        }
        return result;
    }

    /**
     * Indicates if this module name contains a given qualified name.
     * This is the case if the module name shorter than the qualified name or equally long,
     * and each token matches the corresponding token of the other.
     */
    public boolean contains(QualName other) {
        boolean result = size() <= other.size();
        for (int i = 0; result && i < size(); i++) {
            String myToken = get(i);
            if (myToken.equals(WILDCARD)) {
                continue;
            }
            String otherToken = other.get(i);
            if (otherToken.equals(WILDCARD)) {
                continue;
            }
            result = myToken.equals(otherToken);
        }
        return result;
    }

    /** Turns this qualified name into a {@link File} object. */
    public File toFile() {
        File result;
        if (isTop()) {
            result = new File("");
        } else {
            result = null;
            for (String token : tokens()) {
                result = new File(result, token);
            }
            assert result != null;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return tokens().hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        QualName other = (QualName) obj;
        if (this.tokens.equals(other.tokens)) {
            return true;
        }
        return false;
    }

    /** Returns a string representation of this name, using a given separator. */
    public String toString(char sep) {
        return toString("" + sep);
    }

    /** Returns a string representation of this name, using a given separator. */
    public String toString(String sep) {
        return Groove.toString(this.tokens.toArray(), "", "", sep, sep);
    }

    @Override
    public String toString() {
        var result = this.text;
        if (result == null) {
            this.text = result = toString(SEPARATOR);
        }
        return result;
    }

    /** The text returned by {@link #toString()}. */
    private @Nullable String text;

    /**
     * Character to separate constituent tokens.
     */
    static public final char SEPARATOR_CHAR = '.';
    /**
     * Character to separate constituent tokens (as String).
     */
    static public final String SEPARATOR = "" + SEPARATOR_CHAR;
    /** Wildcard character. */
    static public final char WILDCARD_CHAR = '*';
    /** Wildcard string. */
    static public final String WILDCARD = "" + WILDCARD_CHAR;

    /** The empty module name, consisting of no tokens. */
    static public final ModuleName TOP = new ModuleName();
}

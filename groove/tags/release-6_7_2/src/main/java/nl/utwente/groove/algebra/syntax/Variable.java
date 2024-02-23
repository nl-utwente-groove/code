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
package nl.utwente.groove.algebra.syntax;

import java.util.Objects;
import java.util.function.Function;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.util.line.Line;
import nl.utwente.groove.util.line.Line.Style;
import nl.utwente.groove.util.parse.OpKind;

/**
 * Algebraic variable.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public final class Variable extends Expression {
    /** Constructs a new variable with a given name and sort.
     * @param prefixed indicates if the expression was explicitly typed
     * by a type prefix in the parsed text
     * @param name name of the new variable
     * @param sort sort of the new variable
     * @param binding optional binding information for this variable.
     */
    private Variable(boolean prefixed, String name, Sort sort, @Nullable Object binding) {
        super(prefixed);
        this.sort = sort;
        this.name = name;
        this.binding = binding;
    }

    /** Constructs a new variable with a given name and sort.
     * @param prefixed indicates if the expression was explicitly typed
     * by a type prefix in the parsed text
     * @param name name of the new variable
     * @param sort sort of the new variable
     */
    public Variable(boolean prefixed, String name, Sort sort) {
        this(prefixed, name, sort, null);
    }

    /** Constructs a new, non-prefixed variable with a given name and sort.
     * @param name name of the new variable
     * @param sort sort of the new variable
     */
    public Variable(String name, Sort sort) {
        this(false, name, sort);
    }

    /** Returns the name of this variable. */
    public String getName() {
        return this.name;
    }

    /** The name of this variable. */
    private final String name;

    @Override
    protected Line toLine(OpKind context) {
        return Line.atom(getName()).style(Style.ITALIC).style(Style.UNDERLINE);
    }

    @Override
    public Sort getSort() {
        return this.sort;
    }

    /** The signature of this variable. */
    private final Sort sort;

    /** Indicates if this variable has non-{@code null} binding information. */
    public boolean isBound() {
        return this.binding != null;
    }

    /** Returns the optional binding information of this variable.
     * Should only be invoked if {@link #isBound()} holds.
     */
    public Object getBinding() {
        var result = this.binding;
        assert result != null;
        return result;
    }

    /** Optional binding information of this variable. */
    private final @Nullable Object binding;

    @Override
    public Variable bind(Function<Variable,Object> bindMap) {
        return new Variable(isPrefixed(), getName(), getSort(), bindMap.apply(this));
    }

    @Override
    public boolean isTerm() {
        return true;
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public SortMap computeTyping() {
        return SortMap.newInstance(getName(), getSort());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSort(), getName(), this.binding);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Variable other)) {
            return false;
        }
        if (!getName().equals(other.getName())) {
            return false;
        }
        assert getSort() == other.getSort() && Objects.equals(this.binding, other.binding);
        return true;
    }

    @Override
    protected String createParseString() {
        String result = toDisplayString();
        if (isPrefixed()) {
            result = getSort() + ":" + toDisplayString();
        }
        return result;
    }

    @Override
    public String toString() {
        return getSort().getName() + ":" + getName();
    }

    /** Callback method to determine if a given character is suitable as first character for a variable name. */
    static public boolean isIdentifierStart(char c) {
        return Character.isJavaIdentifierStart(c);
    }

    /** Callback method to determine if a given character is suitable as middle character of a variable name. */
    static public boolean isIdentifierPart(char c) {
        return Character.isJavaIdentifierPart(c);
    }

    /** Callback method to determine if a given character is suitable as last character of a variable name. */
    static public boolean isIdentifierEnd(char c) {
        return Character.isJavaIdentifierPart(c);
    }
}

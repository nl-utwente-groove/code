/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.algebra.syntax;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import groove.algebra.Sort;
import groove.util.line.Line;
import groove.util.parse.OpKind;

/**
 * Algebraic variable.
 * @author Arend Rensink
 * @version $Revision $
 */
@NonNullByDefault
public class Variable extends Expression {
    /** Constructs a new variable with a given name and sort. */
    public Variable(boolean prefixed, String name, Sort sort) {
        super(prefixed);
        this.sort = sort;
        this.name = name;
    }

    /** Constructs a new, non-prefixed variable with a given name and sort. */
    public Variable(String name, Sort sort) {
        this(false, name, sort);
    }

    /** Returns the name of this variable. */
    public String getName() {
        return this.name;
    }

    @Override
    protected Line toLine(OpKind context) {
        return Line.atom(getName());
    }

    @Override
    public Sort getSort() {
        return this.sort;
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
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
    public Typing computeTyping() {
        return Typing.singletonTyping(getName(), getSort());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Variable)) {
            return false;
        }
        Variable other = (Variable) obj;
        if (!getName().equals(other.getName())) {
            return false;
        }
        assert getSort() == other.getSort();
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

    /** The name of this variable. */
    private final String name;
    /** The signature of this variable. */
    private final Sort sort;

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

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
 * Parameter expression.
 * @author Arend Rensink
 * @version $Revision $
 */
@NonNullByDefault
public class Parameter extends Expression {
    /** Constructs a new parameter. */
    public Parameter(boolean prefixed, int nr, Sort type) {
        super(prefixed);
        assert nr >= 0;
        this.nr = nr;
        this.type = type;
    }

    @Override
    public Sort getSort() {
        return this.type;
    }

    @Override
    protected Line toLine(OpKind context) {
        return Line.atom("$" + getNumber());
    }

    /** Returns the parameter number. */
    public int getNumber() {
        return this.nr;
    }

    @Override
    public boolean isTerm() {
        return false;
    }

    @Override
    public boolean isClosed() {
        return true;
    }

    @Override
    protected Typing computeTyping() {
        return Typing.emptyTyping();
    }

    @Override
    public int hashCode() {
        return this.nr;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Parameter)) {
            return false;
        }
        Parameter other = (Parameter) obj;
        return this.nr == other.nr;
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
        return getSort() + ":" + toDisplayString();
    }

    private final int nr;
    private final Sort type;
}

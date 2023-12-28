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

import static nl.utwente.groove.graph.EdgeRole.BINARY;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.util.Keywords;
import nl.utwente.groove.util.line.Line;
import nl.utwente.groove.util.line.Line.Style;
import nl.utwente.groove.util.parse.OpKind;

/**
 * Expression consisting of a target (a node ID) and a field name.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public final class FieldExpr extends Expression {
    /** Constructs a new field expression. */
    public FieldExpr(boolean prefixed, @Nullable String target, String field, Sort type) {
        super(prefixed);
        assert field != null && type != null;
        this.target = target;
        this.field = field;
        this.type = type;
    }

    @Override
    public Sort getSort() {
        return this.type;
    }

    /**
     * Returns the target of this field expression.
     * If {@code null}, the target is self.
     */
    public @Nullable String getTarget() {
        return this.target;
    }

    /** Checks if this is a self-field expression. */
    public boolean isSelf() {
        String target = getTarget();
        return target == null || target.equals(Keywords.SELF);
    }

    /** Returns the name of this field expression. */
    public String getField() {
        return this.field;
    }

    @Override
    public Expression relabel(TypeLabel oldLabel, TypeLabel newLabel) {
        if (oldLabel.getRole() == BINARY && oldLabel.text().equals(getField())) {
            return new FieldExpr(isPrefixed(), getTarget(), newLabel.text(), getSort());
        } else {
            return this;
        }
    }

    @Override
    protected Line toLine(OpKind context) {
        Line result;
        if (getTarget() != null) {
            result = Line
                .atom(getTarget())
                .style(Style.ITALIC)
                .style(Style.UNDERLINE)
                .append("." + getField());
        } else {
            result = Line.atom(getField());
        }
        return result;
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
    protected SortMap computeTyping() {
        return SortMap.newInstance();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof FieldExpr other) {
            if (this.type != other.type) {
                return false;
            }
            String target = this.target;
            if (target == null) {
                if (other.target != null) {
                    return false;
                }
            } else if (!target.equals(other.target)) {
                return false;
            }
            return this.field.equals(other.field);
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = this.field.hashCode();
        String target = this.target;
        result = prime * result + (target == null
            ? 0
            : target.hashCode());
        result = prime * result + this.type.hashCode();
        return result;
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

    private final @Nullable String target;
    private final String field;
    private final Sort type;
}

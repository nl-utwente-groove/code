/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package nl.utwente.groove.graph;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.line.Line;
import nl.utwente.groove.util.line.Line.Style;

/**
 * Abstract label implementation consisting of simple, plain text,
 * passed in at construction time.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
abstract public class TextLabel implements Label, Cloneable {
    /**
     * Constructs a standard implementation of Label on the basis of a given
     * text index. For internal purposes only.
     * @param text the index of the label text
     * @param role role of the label
     */
    protected TextLabel(String text, EdgeRole role) {
        this.text = text;
        this.role = role;
    }

    @Override
    public Line toLine() {
        var result = Line.atom(this.text);
        return switch (getRole()) {
        case BINARY -> result;
        case FLAG -> result.style(Style.ITALIC);
        case NODE_TYPE -> result.style(Style.BOLD);
        };
    }

    @Override
    public int compareTo(Label o) {
        int result = getRole().compareTo(o.getRole());
        if (result == 0) {
            result = text().compareTo(o.text());
        }
        return result;
    }

    @Override
    public String text() {
        return this.text;
    }

    /** The text of this label. */
    private final String text;

    /* Parsing is not enabled by default. */
    @Override
    public String toParsableString() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    final public EdgeRole getRole() {
        return this.role;
    }

    private final EdgeRole role;

    @Override
    final protected TextLabel clone() {
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRole(), text());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TextLabel other)) {
            return false;
        }
        return getRole().equals(other.getRole()) && text().equals(other.text());
    }

    @Override
    public String toString() {
        return text();
    }
}
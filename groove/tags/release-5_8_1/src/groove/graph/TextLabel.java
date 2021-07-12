/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
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
package groove.graph;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import groove.util.line.Line;

/**
 * Abstract label implementation consisting of simple, plain text,
 * passed in at construction time.
 * Text labels are by default binary; comparison and identity are based on label text.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
abstract public class TextLabel implements Label, Cloneable {
    /**
     * Constructs a standard implementation of Label on the basis of a given
     * text index. For internal purposes only.
     * @param text the index of the label text
     */
    protected TextLabel(String text) {
        this.line = Line.atom(text);
    }

    @Override
    public Line toLine() {
        return this.line;
    }

    /** The text line of this label. */
    private final Line line;

    @Override
    public int compareTo(Label o) {
        return text().compareTo(o.text());
    }

    @Override
    public String text() {
        return toLine().toFlatString();
    }

    /* Parsing is not enabled by default. */
    @Override
    public String toParsableString() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    final public EdgeRole getRole() {
        return EdgeRole.BINARY;
    }

    @Override
    final protected TextLabel clone() {
        return this;
    }

    @Override
    public int hashCode() {
        return text().hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TextLabel)) {
            return false;
        }
        return text().equals(((TextLabel) obj).text());
    }

    @Override
    public String toString() {
        return text();
    }
}
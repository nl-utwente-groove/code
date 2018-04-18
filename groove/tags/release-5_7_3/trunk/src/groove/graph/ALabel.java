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

import org.eclipse.jdt.annotation.NonNull;

import groove.util.line.Line;

/**
 * Provides a partial implementation of the Label interface, consisting only of
 * a label text.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:32:57 $
 */
public abstract class ALabel implements Cloneable, Label {
    @Override
    final public String text() {
        return toLine().toFlatString();
    }

    @Override
    final public Line toLine() {
        Line result = this.line;
        if (result == null) {
            this.line = result = computeLine();
        }
        return result;
    }

    private Line line;

    /** Callback method to compute the line returned by {@link #toLine()}. */
    abstract protected @NonNull Line computeLine();

    /** In general, we do not expect labels to be reconstructable from a string. */
    @Override
    public String toParsableString() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /** Labels are binary by default. */
    @Override
    public EdgeRole getRole() {
        return EdgeRole.BINARY;
    }

    /**
     * This implementation compares this label's class, and then its
     * {@link #text()} with that of <code>obj</code>.
     */
    @Override
    public int compareTo(Label obj) {
        int result = getRole().compareTo(obj.getRole());
        if (result == 0) {
            result = text().compareTo(obj.text());
        }
        return result;
    }

    /**
     * This implementation compares this label's {@link #text()} with that of
     * <code>obj</code>.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Label)) {
            return false;
        }
        if (getRole() != ((Label) obj).getRole()) {
            return false;
        }
        return text().equals(((Label) obj).text());
    }

    /** The hash code is computed by {@link #computeHashCode()}. */
    @Override
    final public int hashCode() {
        // lazy computation because the object may not have been initialised
        // otherwise
        if (this.hashCode == 0) {
            this.hashCode = computeHashCode();
            if (this.hashCode == 0) {
                this.hashCode = -1;
            }
        }
        return this.hashCode;
    }

    /** This implementation delegates to {@link #text()}. */
    @Override
    public String toString() {
        return text();
    }

    /** Callback method computing the label hash code. */
    protected int computeHashCode() {
        return text().hashCode() ^ getKindMask();
    }

    /**
     * Mask that is a function of the label kind,
     * and may be used to modify the label hash code.
     */
    final protected int getKindMask() {
        int mask;
        switch (getRole()) {
        case NODE_TYPE:
            mask = NODE_TYPE_MASK;
            break;
        case FLAG:
            mask = FLAG_MASK;
            break;
        default:
            mask = 0;
        }
        return mask;
    }

    private int hashCode;

    /** Mask to distinguish (the hash code of) node type labels. */
    static private final int NODE_TYPE_MASK = 0xAAAA;
    /** Mask to distinguish (the hash code of) flag labels. */
    static private final int FLAG_MASK = 0x5555;
}

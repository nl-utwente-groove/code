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

import groove.util.line.Line;

/**
 * Interface for edge labels.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:32:51 $
 */
@NonNullByDefault
public interface Label extends Comparable<Label>, java.io.Serializable {
    /** Returns the formatted display line for this label. */
    public Line toLine();

    /**
     * Returns the (plain, unformatted) text that this label carries.
     * Convenience method for {@code toLine().toFlatString()}.
     * @see Line#toFlatString()
     */
    public String text();

    /**
     * Returns a string that can be parsed to reconstruct this label.
     * Only valid for labels that can be (re)constructed from a string
     * representation.
     * @return A parsable string representation for this label.
     * @throws UnsupportedOperationException if this label type
     * cannot be constructed from a string representation.
     */
    public String toParsableString() throws UnsupportedOperationException;

    /**
     * Returns the edge role of this label.
     */
    public EdgeRole getRole();
}

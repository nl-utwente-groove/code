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
package nl.utwente.groove.gui.jgraph;

import java.util.Iterator;

import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.util.collect.NestedIterator;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.FormatErrorSet;

/**
 * Object holding the errors for a given {@link AspectJCell}.
 * These consist of the aspect errors and the extra errors.
 * @author Arend Rensink
 * @version $Revision$
 */
public class AspectJCellErrors implements Iterable<FormatError> {
    AspectJCellErrors(AspectJCell jCell) {
        this.jCell = jCell;
    }

    /** Adds a format error to either the aspect errors or the extra errors.
     * @param aspect if {@code true}, adds to the aspect errors, else to the extra errors.
     */
    void addError(FormatError error, boolean aspect) {
        getErrors(aspect, true).add(error);
        this.jCell.setStale(VisualKey.ERROR);
    }

    /** Adds a collection of format errors to either the aspect errors or the extra errors.
     * @param aspect if {@code true}, adds to the aspect errors, else to the extra errors.
     */
    void addErrors(FormatErrorSet errors, boolean aspect) {
        if (!errors.isEmpty()) {
            getErrors(aspect, true).addAll(errors);
            this.jCell.setStale(VisualKey.ERROR);
        }
    }

    /** Clears either the aspect errors or the extra errors. */
    void clear() {
        this.aspectErrors = FormatErrorSet.EMPTY;
        this.extraErrors = FormatErrorSet.EMPTY;
        this.jCell.setStale(VisualKey.ERROR);
    }

    @Override
    public Iterator<FormatError> iterator() {
        return new NestedIterator<>(getErrors(true, false).iterator(),
            getErrors(false, false).iterator());
    }

    /** Indicates if the object contains no errors whatsoever. */
    public boolean isEmpty() {
        return this.aspectErrors.isEmpty() && this.extraErrors.isEmpty();
    }

    /** Returns either the aspect errors or the extra errors, depending on a flag.
     * A second flag determines if the returned set must be modifiable.
     */
    private FormatErrorSet getErrors(boolean aspect, boolean modifiable) {
        FormatErrorSet result;
        result = aspect
            ? this.aspectErrors
            : this.extraErrors;
        if (modifiable && result.isFixed()) {
            assert result.isEmpty();
            if (aspect) {
                result = this.aspectErrors = new FormatErrorSet();
            } else {
                result = this.extraErrors = new FormatErrorSet();
            }
        }
        return result;
    }

    /** The JCell of which this is the error set. */
    private final AspectJCell jCell;
    /** Initially empty set of aspect errors. */
    private FormatErrorSet aspectErrors = FormatErrorSet.EMPTY;
    /** Initially empty set of extra errors. */
    private FormatErrorSet extraErrors = FormatErrorSet.EMPTY;
}

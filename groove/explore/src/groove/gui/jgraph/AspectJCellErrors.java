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
package groove.gui.jgraph;

import groove.gui.look.VisualKey;
import groove.util.collect.NestedIterator;
import groove.util.parse.FormatError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Object holding the errors for a given {@link AspectJCell}.
 * These consist of the aspect errors and the extra errors.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AspectJCellErrors implements Iterable<FormatError> {
    AspectJCellErrors(AspectJCell jCell) {
        this.jCell = jCell;
    }

    /** Adds a format error to either the aspect errors or the extra errors.
     * @param aspect if {@code true}, adds to the aspect errors, else to the extra errors.
     */
    void addError(FormatError error, boolean aspect) {
        getErrors(aspect).add(error);
        this.jCell.setStale(VisualKey.ERROR);
    }

    /** Adds a collection of format errors to either the aspect errors or the extra errors.
     * @param aspect if {@code true}, adds to the aspect errors, else to the extra errors.
     */
    void addErrors(Collection<FormatError> errors, boolean aspect) {
        getErrors(aspect).addAll(errors);
        this.jCell.setStale(VisualKey.ERROR);
    }

    /** Clears either the aspect errors or the extra errors. */
    void clear() {
        getErrors(true).clear();
        getErrors(false).clear();
        this.jCell.setStale(VisualKey.ERROR);
    }

    @Override
    public Iterator<FormatError> iterator() {
        return new NestedIterator<>(getErrors(true).iterator(),
            getErrors(false).iterator());
    }

    /** Indicates if the object contains no errors whatsoever. */
    public boolean isEmpty() {
        return this.aspectErrors.isEmpty() && this.extraErrors.isEmpty();
    }

    /** Returns either the errors or the extra errors, depending on a flag. */
    private List<FormatError> getErrors(boolean aspect) {
        return aspect ? this.aspectErrors : this.extraErrors;
    }

    private final AspectJCell jCell;
    private final List<FormatError> aspectErrors = new ArrayList<>();
    private final List<FormatError> extraErrors = new ArrayList<>();
}

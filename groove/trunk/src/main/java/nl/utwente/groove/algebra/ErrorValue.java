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
package nl.utwente.groove.algebra;

import java.util.Objects;

/**
 * Class encoding the (singular) error value of a sort.
 * @author Arend Rensink
 * @version $Revision$
 */
public class ErrorValue extends Exception {
    ErrorValue(Sort sort, Exception exc) {
        super(exc);
        this.sort = sort;
    }

    /**
     * Returns the sort of this error value.
     */
    public Sort getSort() {
        return this.sort;
    }

    private final Sort sort;

    /**
     * Returns the inner exception causing this {@link ErrorValue}.
     */
    public Exception getInner() {
        return (Exception) getCause();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.sort, getMessage());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ErrorValue other = (ErrorValue) obj;
        return this.sort == other.sort && getMessage().equals(other.getMessage());
    }

    @Override
    public String toString() {
        return getSort() + "error";
    }
}

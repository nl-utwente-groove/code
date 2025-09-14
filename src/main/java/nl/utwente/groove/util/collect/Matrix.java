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
package nl.utwente.groove.util.collect;

import java.util.ArrayList;
import java.util.List;

/** A matrix of entities.
 * @author Arend Rensink
 */
public class Matrix<E> extends ArrayList<List<E>> {
    /** Look up the entry in sizeMatrix at a given width and height, if defined. */
    public E lookup(int width, int height) {
        E result = null;
        if (width < this.size()) {
            var forWidth = this.get(width);
            if (height < forWidth.size()) {
                result = forWidth.get(height);
            }
        }
        return result;
    }

    /** Store an entry in sizeMatrix at a given width and height. */
    public void store(int width, int height, E entry) {
        for (int i = this.size(); i <= width; i++) {
            this.add(new ArrayList<>());
        }
        var forWidth = this.get(width);
        for (int i = forWidth.size(); i <= height; i++) {
            forWidth.add(null);
        }
        forWidth.set(height, entry);
    }
}
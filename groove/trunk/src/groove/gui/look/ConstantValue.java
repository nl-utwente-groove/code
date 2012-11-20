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
package groove.gui.look;

import groove.gui.jgraph.GraphJCell;

/**
 * Refresher that always returns the same value.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ConstantValue<T> implements VisualValue<T> {
    /** Constructs a constant refresher for a given value. */
    public ConstantValue(T value) {
        super();
        this.value = value;
    }

    @Override
    public T get(GraphJCell cell) {
        return this.value;
    }

    private final T value;
}

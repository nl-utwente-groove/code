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
package groove.util.parse;

import groove.util.line.Line;

import java.util.ArrayList;
import java.util.List;

/**
 * An identifier as can appear in the payload of an atomic {@link Expr} object.
 * An identifier consists of an optional prefix and a list of names.
 * @author Arend Rensink
 * @version $Id$
 */
public class Id extends ArrayList<String> {
    /** Constructs an identifier with an initially empty list of names.
     */
    public Id() {
        // empty
    }

    /** Constructs an identifier with a list of names initially consisting of a given name.
     */
    public Id(String name) {
        add(name);
    }

    /** Constructs an identifier with a given (possibly {@code null}) prefix
     * and an initially empty list of names.
     */
    public Id(List<String> names) {
        super(names);
    }

    /** Returns a formatted line representation of this identifier. */
    public Line toLine() {
        StringBuffer result = new StringBuffer();
        result.append(get(0));
        for (int i = 1; i < size(); i++) {
            result.append('.');
            result.append(get(i));
        }
        return Line.atom(result.toString());
    }
}
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

import groove.util.Pair;
import groove.util.line.Line;

import java.util.ArrayList;
import java.util.List;

/**
 * An identifier as can appear in the payload of an atomic {@link Expr} object.
 * An identifier consists of an optional prefix and a list of names.
 * @author Arend Rensink
 * @version $Id$
 */
public class Id extends Pair<String,List<String>> {
    /** Constructs an identifier with a given (possibly {@code null}) prefix
     * and an initially empty list of names.
     */
    public Id(String prefix) {
        super(prefix, new ArrayList<String>());
    }

    /** Constructs an identifier with a given (possibly {@code null}) prefix
     * and an initially empty list of names.
     */
    public Id(String prefix, List<String> names) {
        super(prefix, names);
    }

    /** Tests if this identifier has a prefix. */
    public boolean hasPrefix() {
        return getPrefix() != null;
    }

    /** Returns the (possibly {@code null}) prefix of this identifier. */
    public String getPrefix() {
        return one();
    }

    /** Adds a name to the qualified name list. */
    public void addName(String name) {
        two().add(name);
    }

    /** Returns the (nonempty) list of names that this identifier consists of. */
    public List<String> getNames() {
        return two();
    }

    /** Returns a formatted line representation of this identifier. */
    public Line toLine() {
        StringBuffer result = new StringBuffer();
        if (hasPrefix()) {
            result.append(getPrefix());
            result.append(":");
        }
        result.append(getNames().get(0));
        for (int i = 1; i < getNames().size(); i++) {
            result.append('.');
            result.append(getNames().get(i));
        }
        return Line.atom(result.toString());
    }
}
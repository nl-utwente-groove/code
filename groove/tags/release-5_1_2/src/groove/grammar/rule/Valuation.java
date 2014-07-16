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
package groove.grammar.rule;

import groove.grammar.type.TypeElement;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Valuation of label variables in terms of type edges.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Valuation extends LinkedHashMap<LabelVar,TypeElement> {
    /** Constructor for an initially empty valuation. */
    public Valuation() {
        super();
    }

    /** Constructor initialising the valuation to a given one. */
    public Valuation(Map<LabelVar,TypeElement> m) {
        super(m);
    }

    /**
     * Returns a merger of this valuation with another, if the two do not
     * conflict on any value.
     * @param other the other valuation
     * @return A new valuation map that is the result of consistent union of both,
     * <code>null</code> if there is a conflict.
     */
    public Valuation getMerger(Valuation other) {
        Valuation result = new Valuation(this);
        if (other != null) {
            for (Entry<LabelVar,TypeElement> e : other.entrySet()) {
                LabelVar key = e.getKey();
                TypeElement newValue = e.getValue();
                TypeElement oldValue = result.put(key, newValue);
                if (oldValue != null && !oldValue.equals(newValue)) {
                    result = null;
                    break;
                }
            }
        }
        return result;
    }

    /** The empty valuation. */
    public final static Valuation EMPTY = new Valuation();
}

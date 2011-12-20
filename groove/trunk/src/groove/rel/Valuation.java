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
package groove.rel;

import groove.graph.TypeEdge;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Valuation of label variables in terms of type edges.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Valuation extends LinkedHashMap<LabelVar,TypeEdge> {
    /** Constructor for an initially empty valuation. */
    public Valuation() {
        super();
    }

    /** Constructor for an initially empty valuation with a given capacity. */
    public Valuation(int c) {
        super(c);
    }

    /** Constructor initialising the valuation to a given one. */
    public Valuation(Map<LabelVar,TypeEdge> m) {
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
            for (Entry<LabelVar,TypeEdge> e : other.entrySet()) {
                LabelVar key = e.getKey();
                TypeEdge newValue = e.getValue();
                TypeEdge oldValue = result.put(key, newValue);
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

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
package groove.io.graph;

import groove.graph.AEdge;
import groove.graph.plain.PlainLabel;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An attributed edge.
 * This is an ordinary edge extended with a sting-to-string mapping
 * holding additional edge attributes.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AttrEdge extends AEdge<AttrNode,PlainLabel> {
    /** Construct a new edge. */
    AttrEdge(AttrNode source, PlainLabel label, AttrNode target, int number) {
        super(source, label, target, number);
        this.attributeMap = new LinkedHashMap<>();
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    /** Returns a deep copy of this edge. */
    @Override
    public AttrEdge clone() {
        AttrEdge result = new AttrEdge(source(), label(), target(), getNumber());
        result.attributeMap.putAll(this.attributeMap);
        return result;
    }

    /** Sets a value in this edge's attribute map. */
    public void setAttribute(String key, String value) {
        this.attributeMap.put(key, value);
    }

    /** Returns a value from this edge's attribute map. */
    public String getAttribute(String key) {
        return this.attributeMap.get(key);
    }

    /** Returns an unmodifiable view on the string-to-string map of additional attributes. */
    public Map<String,String> getAttributes() {
        return Collections.unmodifiableMap(this.attributeMap);
    }

    private final Map<String,String> attributeMap;
}

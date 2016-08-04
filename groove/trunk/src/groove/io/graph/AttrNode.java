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

import groove.graph.ANode;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A node of an XmlGraph.
 * This is an ordinary node extended with a sting-to-string mapping
 * holding additional node attributes.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AttrNode extends ANode {
    /** Constructs a node with a given number. */
    public AttrNode(int nr) {
        super(nr);
        this.attributeMap = new LinkedHashMap<>();
    }

    /**
     * Returns a string consisting of the letter <tt>'n'</tt>.
     */
    @Override
    public String getToStringPrefix() {
        return "n";
    }

    /** Returns a deep copy of this node. */
    @Override
    public AttrNode clone() {
        AttrNode result = new AttrNode(getNumber());
        result.attributeMap.putAll(this.attributeMap);
        return result;
    }

    /** Sets a value in this node's attribute map. */
    public void setAttribute(String key, String value) {
        this.attributeMap.put(key, value);
    }

    /** Returns a value from this node's attribute map. */
    public String getAttribute(String key) {
        return this.attributeMap.get(key);
    }

    /** Returns an unmodifiable view on the string-to-string map of additional attributes. */
    public Map<String,String> getAttributes() {
        return Collections.unmodifiableMap(this.attributeMap);
    }

    private final Map<String,String> attributeMap;
}

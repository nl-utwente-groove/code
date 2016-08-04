/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.explore.encode;

import java.util.Map;
import java.util.TreeMap;

/**
 * <!=========================================================================>
 * A Serialized is a textual representation consisting of a keyword (String)
 * and a mapping of named arguments (a Map<String,String>). The arguments can
 * be changed, but the keyword is fixed.  
 * <!=========================================================================>
 * @author Maarten de Mol
 */
public class Serialized implements Cloneable {

    private final String keyword;
    private final TreeMap<String,String> arguments;

    /**
     * Build a Serialized with the indicated keyword.
     * Always initializes with the empty argument map. 
     */
    public Serialized(String keyword) {
        this.keyword = keyword;
        this.arguments = new TreeMap<>();
    }

    /**
     * Get the keyword of the Serialized.
     */
    public String getKeyword() {
        return this.keyword;
    }

    /**
     * Get the argument with the indicated name. If the name does not occur
     * in the map, the argument is returned as the empty String.
     */
    public String getArgument(String name) {
        if (this.arguments.containsKey(name)) {
            return this.arguments.get(name);
        } else {
            return "";
        }
    }

    /**
     * Set the argument with the indicated name. If the name occurs in the 
     * map, the argument is overwritten. If it does not occur in the map, it
     * it stored as a new argument.
     */
    public void setArgument(String name, String value) {
        this.arguments.put(name, value);
    }

    /**
     * Appends a text to the argument with the indicated name.
     */
    public void appendArgument(String name, String value) {
        setArgument(name, getArgument(name) + value);
    }

    /**
     * Compress the serialized into a String representation of the form
     * keyword(arg=value, ..., arg=value). The order of the arguments is
     * always alphabetical.
     */
    @Override
    public String toString() {
        StringBuffer result = new StringBuffer(getKeyword());
        if (!this.arguments.keySet().isEmpty()) {
            Boolean isFirst = true;
            result.append("(");
            for (Map.Entry<String,String> entry : this.arguments.entrySet()) {
                if (!isFirst) {
                    result.append(", ");
                }
                result.append(entry.getKey());
                result.append("=");
                result.append(entry.getValue());
                isFirst = false;
            }
            result.append(")");
        }
        return result.toString();
    }

    @Override
    public Serialized clone() {
        Serialized result = new Serialized(getKeyword());
        result.arguments.putAll(this.arguments);
        return result;
    }
}

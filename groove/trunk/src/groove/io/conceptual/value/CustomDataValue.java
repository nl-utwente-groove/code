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
package groove.io.conceptual.value;

import groove.io.conceptual.Visitor;
import groove.io.conceptual.type.CustomDataType;

import java.util.regex.Matcher;

/** Class for values of custom data types. */
public class CustomDataValue extends Value {
    /** 
     * Constructs a custom data value representation.
     * @param type the custom data type of which this is a value; non-{@code null}
     * @param value the string representation of the custom value; non-{@code null}
     */
    public CustomDataValue(CustomDataType type, String value) {
        super(type);
        this.m_value = value;
    }

    @Override
    public boolean doVisit(Visitor v, String param) {
        v.visit(this, param);
        return true;
    }

    @Override
    public String getValue() {
        return this.m_value.replaceAll(Matcher.quoteReplacement("\\"),
            "\\\\\\\\").replaceAll(Matcher.quoteReplacement("\""), "\\\\\"");
    }

    @Override
    public String toString() {
        return "Data: " + this.m_value;
    }

    /** The (String-represented) value. */
    private final String m_value;
}

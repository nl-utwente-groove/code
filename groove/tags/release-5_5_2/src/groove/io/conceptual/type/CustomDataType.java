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
package groove.io.conceptual.type;

import groove.io.conceptual.Id;
import groove.io.conceptual.Visitor;
import groove.io.conceptual.value.CustomDataValue;
import groove.io.conceptual.value.Value;

/** User-provided special data type. */
public class CustomDataType extends DataType {
    /** Constructs a custom data type with a given identifier. */
    public CustomDataType(Id id) {
        super(id);
    }

    @Override
    public boolean doVisit(Visitor v, String param) {
        v.visit(this, param);
        return true;
    }

    @Override
    public Value valueFromString(String valueString) {
        return new CustomDataValue(this, valueString);
    }

    @Override
    public String typeString() {
        return getId().getName().toString();
    }
}

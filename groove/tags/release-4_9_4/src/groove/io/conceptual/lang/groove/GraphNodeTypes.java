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
package groove.io.conceptual.lang.groove;

import groove.io.conceptual.type.Type;

import java.util.HashMap;
import java.util.Map;

public class GraphNodeTypes {
    public enum ModelType {
        TypeClass,
        TypeClassNullable,
        TypeEnum,
        TypeEnumValue,
        TypeIntermediate,
        TypeDatatype,
        TypeContainerSet,
        TypeContainerBag,
        TypeContainerSeq,
        TypeContainerOrd,
        TypeTuple,
        TypeNone
    }

    private Map<String,ModelType> m_modelTypes = new HashMap<String,ModelType>();
    private Map<String,Type> m_types = new HashMap<String,Type>();

    public GraphNodeTypes() {

    }

    public void addModelType(String typeName, ModelType typeString) {
        if (m_modelTypes.containsKey(typeName)) {
            return;
        }

        m_modelTypes.put(typeName, typeString);
    }

    public void addType(String typeName, Type cmType) {
        if (m_types.containsKey(typeName)) {
            return;
        }

        if (!m_modelTypes.containsKey(typeName)) {
            throw new IllegalArgumentException("Setting type without model type");
        }

        m_types.put(typeName, cmType);
    }

    public boolean hasModelType(String typeString) {
        return m_modelTypes.containsKey(typeString);
    }

    public boolean hasType(String typeString) {
        return m_types.containsKey(typeString);
    }

    public ModelType getModelType(String typeString) {
        if (!m_modelTypes.containsKey(typeString)) {
            return null;
        }

        return m_modelTypes.get(typeString);
    }

    public Type getType(String typeString) {
        if (!m_types.containsKey(typeString)) {
            return null;
        }

        return m_types.get(typeString);
    }
}

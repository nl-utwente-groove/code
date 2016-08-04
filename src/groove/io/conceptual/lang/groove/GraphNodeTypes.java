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

import java.util.HashMap;
import java.util.Map;

import groove.io.conceptual.type.Type;

@SuppressWarnings("javadoc")
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

    private Map<String,ModelType> m_modelTypes = new HashMap<>();
    private Map<String,Type> m_types = new HashMap<>();

    public GraphNodeTypes() {

    }

    public void addModelType(String typeName, ModelType typeString) {
        if (this.m_modelTypes.containsKey(typeName)) {
            return;
        }

        this.m_modelTypes.put(typeName, typeString);
    }

    public void addType(String typeName, Type cmType) {
        if (this.m_types.containsKey(typeName)) {
            return;
        }

        if (!this.m_modelTypes.containsKey(typeName)) {
            throw new IllegalArgumentException("Setting type without model type");
        }

        this.m_types.put(typeName, cmType);
    }

    public boolean hasModelType(String typeString) {
        return this.m_modelTypes.containsKey(typeString);
    }

    public boolean hasType(String typeString) {
        return this.m_types.containsKey(typeString);
    }

    public ModelType getModelType(String typeString) {
        if (!this.m_modelTypes.containsKey(typeString)) {
            return null;
        }

        return this.m_modelTypes.get(typeString);
    }

    public Type getType(String typeString) {
        if (!this.m_types.containsKey(typeString)) {
            return null;
        }

        return this.m_types.get(typeString);
    }
}

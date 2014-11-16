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

import groove.io.conceptual.configuration.schema.StringsType;
import groove.io.conceptual.type.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/** Record of type name associations with corresponding meta-types and glossary types. */
public class GraphNodeTypes {
    /** Enumeration of (outer) types that may occur in a metamodel. */
    public enum ModelType {
        /** Class type. */
        TypeClass(s -> s.getProperPostfix()),
        /** Nullable class type. */
        TypeClassNullable(s -> s.getNullablePostfix()),
        /** Enumeration type. */
        TypeEnum(s -> s.getEnumPostfix()),
        /** Type of (singleton) enumeration subtypes, if that is the chosen representation. */
        TypeEnumValue(),
        /** Type of intermediate nodes. */
        TypeIntermediate(),
        /** Primitive data type. */
        TypeDatatype(s -> s.getDataPostfix()),
        /** Set container. */
        TypeContainerSet(),
        /** Multiset container. */
        TypeContainerBag(),
        /** Ordered (list-like) container. */
        TypeContainerSeq(),
        /** Ordered (set-like) container. */
        TypeContainerOrd(),
        /** Tuple type. */
        TypeTuple(s -> s.getTuplePostfix()),
        /** Artificial type with no values. */
        TypeNone(), ;

        private ModelType() {
            this(null);
        }

        private ModelType(Function<StringsType,String> postfix) {
            this.postfix = postfix;
        }

        /** Indicates if a given type name ends on the postfix specifying this model type. */
        public boolean admits(StringsType strings, String typeName) {
            boolean result;
            if (this.postfix == null) {
                String post = this.postfix.apply(strings);
                result = post.length() > 0 && typeName.endsWith(post);
            } else {
                result = false;
            }
            return result;
        }

        private final Function<StringsType,String> postfix;
    }

    /** Adds a mapping from a type name to a meta-type. */
    public void addModelType(String typeName, ModelType typeString) {
        if (this.m_modelTypes.containsKey(typeName)) {
            return;
        }
        this.m_modelTypes.put(typeName, typeString);
    }

    /** Tests if a given type name has a known associated meta-type. */
    public boolean hasModelType(String typeString) {
        return this.m_modelTypes.containsKey(typeString);
    }

    /** Returns the meta-type for a given type name. */
    public ModelType getModelType(String typeString) {
        return this.m_modelTypes.get(typeString);
    }

    private final Map<String,ModelType> m_modelTypes = new HashMap<String,ModelType>();

    /** Adds a mapping from a type name to a glossary type. */
    public void addType(String typeName, Type cmType) {
        if (!this.m_modelTypes.containsKey(typeName)) {
            throw new IllegalArgumentException("Setting type without model type");
        }
        Type oldType = this.m_types.put(typeName, cmType);
        assert oldType == null : "Double definition of " + typeName;
    }

    /** Tests if a given type name has a known associated glossary type. */
    public boolean hasType(String typeString) {
        return this.m_types.containsKey(typeString);
    }

    /** Returns the glossary type for a given type name. */
    public Type getType(String typeString) {
        return this.m_types.get(typeString);
    }

    private final Map<String,Type> m_types = new HashMap<>();
}

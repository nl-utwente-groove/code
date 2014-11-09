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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/** Types of the nodes in the meta-graph. */
enum MetaType {
    /** Class type. */
    Class(s -> s.getMetaClass()),
    /** Nullable class type. */
    ClassNullable(s -> s.getMetaClassNullable()),
    /** Enumeration type. */
    Enum(s -> s.getMetaEnum()),
    /** Type of intermediate nodes. */
    Intermediate(s -> s.getMetaIntermediate()),
    Type(s -> s.getMetaType()),
    /** Set container. */
    ContainerSet(s -> s.getMetaContainerSet()),
    /** Multiset container. */
    ContainerBag(s -> s.getMetaContainerBag()),
    /** Ordered (list-like) container. */
    ContainerSeq(s -> s.getMetaContainerSeq()),
    /** Ordered (set-like) container. */
    ContainerOrd(s -> s.getMetaContainerOrd()),
    /** Primitive data type. */
    DataType(s -> s.getMetaDataType()),
    /** Tuple type. */
    Tuple(s -> s.getMetaTuple()),
    /** Artificial type with no values. */
    None(null), ;

    private MetaType(Function<StringsType,String> name) {
        this.name = name;
    }

    /** Retrieves the node name for this meta-type from a {@link StringsType} object. */
    public String getName(StringsType strings) {
        return this.name.apply(strings);
    }

    private final Function<StringsType,String> name;

    /** Creates a map from strings to metatypes, based on a given {@link StringsType} object. */
    public static Map<String,MetaType> createMap(StringsType strings) {
        Map<String,MetaType> result = new HashMap<>();
        for (MetaType mt : MetaType.values()) {
            result.put(mt.getName(strings), mt);
        }
        return result;
    }
}
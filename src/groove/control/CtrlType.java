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
package groove.control;

import java.util.HashMap;
import java.util.Map;

import groove.algebra.Sort;

/**
 * Class encapsulating control variable types.
 * A control type is either a node type or a data type.
 * A data type has an attached signature.
 * @author Arend Rensink
 * @version $Revision $
 */
public enum CtrlType {
    /** Node type. */
    NODE,
    /** Boolean type. */
    BOOL(Sort.BOOL),
    /** Integer type. */
    INT(Sort.INT),
    /** Real number type. */
    REAL(Sort.REAL),
    /** String type. */
    STRING(Sort.STRING);

    /** Constructs a control data type from a given data signature. */
    private CtrlType() {
        this.sort = null;
        this.name = NODE_TYPE_NAME;
    }

    /** Constructs a control data type from a given data signature. */
    private CtrlType(Sort signature) {
        this.sort = signature;
        this.name = signature.getName();
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Returns the associated data sort, if this type is a data type.
     * @return the data sort, or {@code null} if this type
     * is a node type.
     */
    public Sort getSort() {
        return this.sort;
    }

    /** Data sort of this type, in case it is a data type. */
    private final Sort sort;

    /**
     * Returns the name of this type.
     */
    public String getName() {
        return this.name;
    }

    /** Name of this control type. */
    private final String name;

    /**
     * Returns a data type instance for a given signature.
     * @throws IllegalArgumentException if there is no signature with the given name
     */
    public static CtrlType getType(Sort signature) {
        return valueOf(signature.name());
    }

    /** Returns the control type with a given name. */
    public static CtrlType getType(String name) {
        return typeMap.get(name);
    }

    private static final Map<String,CtrlType> typeMap;

    static {
        typeMap = new HashMap<>();
        for (CtrlType type : CtrlType.values()) {
            typeMap.put(type.toString(), type);
        }
    }
    /** The name of the node type. */
    static public final String NODE_TYPE_NAME = "node";
}

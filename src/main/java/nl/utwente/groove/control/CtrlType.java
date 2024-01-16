/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.control;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Sort;

/**
 * Class encapsulating control variable types.
 * A control type is either {@link #NODE} or a data sort.
 * A data type has an attached sort.
 * @author Arend Rensink
 * @version $Revision$
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

    /** Constructs the unique a control node type. */
    private CtrlType() {
        this.sort = null;
        this.name = NODE_TYPE_NAME;
    }

    /** Constructs a control data type from a given data sort. */
    private CtrlType(Sort sort) {
        this.sort = sort;
        this.name = sort.getName();
    }

    @Override
    public String toString() {
        return this.name;
    }

    /** Indicates if this control type corresponds to a data sort. */
    public boolean isSort() {
        return this != NODE;
    }

    /**
     * Returns the associated data sort, if this type is a data type.
     * @return the data sort, or {@code null} if this type
     * is a node type.
     * @see #isSort()
     */
    public @Nullable Sort getSort() {
        return this.sort;
    }

    /** Data sort of this type, in case it is a data type. */
    private final @Nullable Sort sort;

    /**
     * Returns the name of this control type.
     */
    public String getName() {
        return this.name;
    }

    /** Name of this control type. */
    private final String name;

    /**
     * Returns a data type instance for a given data sort.
     * @throws IllegalArgumentException if there is no sort with the given name
     */
    public static CtrlType getType(Sort sort) {
        return valueOf(sort.name());
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

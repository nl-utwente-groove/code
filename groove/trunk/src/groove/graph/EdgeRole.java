/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.graph;

import groove.util.Pair;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

/**
 * Role of an edge within a graph: node type, flag or binary.
 * @author Arend Rensink
 * @version $Revision $
 */
public enum EdgeRole {
    /** An ordinary binary edge. */
    BINARY("binary", ""),
    /** A node type edge, i.e., a self-loop determining the type of a node. */
    NODE_TYPE("node type", "type:"),
    /** A flag edge, i.e., a self-loop that stands for a node property. */
    FLAG("flag", "flag:");

    private EdgeRole(String name, String prefix) {
        this.name = name;
        this.prefix = prefix;
    }

    /** Returns the label prefix associated with this edge role. */
    public String getPrefix() {
        return this.prefix;
    }

    /** Returns the name of this edge role, with the first letter optionally capitalised. */
    public String getName(boolean capitalised) {
        String result = this.name;
        if (capitalised) {
            char[] resultChars = result.toCharArray();
            resultChars[0] = Character.toUpperCase(resultChars[0]);
            result = new String(resultChars);
        }
        return result;
    }

    private final String name;
    private final String prefix;

    /** 
     * Parses a label into the pair consisting of its edge role and the
     * actual label text. The original label text can be obtained as 
     * {@code result.one().getPrefix()+result.two()}.
     */
    public static Pair<EdgeRole,String> parseLabel(String text) {
        EdgeRole resultRole = BINARY;
        for (EdgeRole role : EnumSet.of(NODE_TYPE, FLAG)) {
            if (text.startsWith(role.getPrefix())) {
                resultRole = role;
                break;
            }
        }
        return new Pair<EdgeRole,String>(resultRole,
            text.substring(resultRole.getPrefix().length()));
    }

    /** 
     * Returns a unique index for every edge role.
     * The indices are guaranteed to range from 0 to the number of roles - 1.
     * This is the inverse to {@link #getKind(int)}
     */
    public static int getIndex(EdgeRole role) {
        return indexMap.get(role);
    }

    /**
     * Returns an edge role for a given index.
     * This is the inverse to {@link #getIndex(EdgeRole)}.
     */
    public static EdgeRole getKind(int index) {
        return rolesArray[index];
    }

    /** Injective mapping from edge roles to indices. */
    private static Map<EdgeRole,Integer> indexMap =
        new EnumMap<EdgeRole,Integer>(EdgeRole.class);
    /** Array of edge roles, in the order of their indices. */
    private static final EdgeRole[] rolesArray = new EdgeRole[EnumSet.allOf(
        EdgeRole.class).size()];
    static {
        int index = 0;
        for (EdgeRole role : EnumSet.allOf(EdgeRole.class)) {
            indexMap.put(role, index);
            rolesArray[index] = role;
            index++;
        }
    }
}

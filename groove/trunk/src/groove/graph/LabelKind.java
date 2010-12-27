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
 * The kind of a graph label: node type, flag or binary.
 * @author Arend Rensink
 * @version $Revision $
 */
public enum LabelKind {
    /** Label for an ordinary binary edge. */
    BINARY("binary", ""),
    /** Label for a node type, i.e., a unary edge determining the type of a node. */
    NODE_TYPE("node type", "type:"),
    /** Label for a flag, i.e., a unary edge that stands for a node property. */
    FLAG("flag", "flag:");

    private LabelKind(String name, String prefix) {
        this.name = name;
        this.prefix = prefix;
    }

    /** Returns the prefix associated with this label kind. */
    public String getPrefix() {
        return this.prefix;
    }

    /** Returns the name of this label kind, with the first letter optionally capitalised. */
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
     * Parses a label into the pair consisting of its kind and the
     * actual label text. The original label can be obtained as 
     * {@code result.one().getPrefix()+result.two()}.
     */
    public static Pair<LabelKind,String> parse(String text) {
        LabelKind resultKind = BINARY;
        for (LabelKind kind : EnumSet.of(NODE_TYPE, FLAG)) {
            if (text.startsWith(kind.getPrefix())) {
                resultKind = kind;
                break;
            }
        }
        return new Pair<LabelKind,String>(resultKind,
            text.substring(resultKind.getPrefix().length()));
    }

    /** 
     * Returns a unique index for every label kind.
     * The indices are guaranteed to range from 0 to the number of kind values - 1.
     * This is the inverse to {@link #getKind(int)}
     */
    public static int getIndex(LabelKind kind) {
        return indexMap.get(kind);
    }

    /**
     * Returns a label kind for a given index.
     * This is the inverse to {@link #getIndex(LabelKind)}.
     */
    public static LabelKind getKind(int index) {
        return kindsArray[index];
    }

    /** Injective mapping from label kinds to indices. */
    private static Map<LabelKind,Integer> indexMap =
        new EnumMap<LabelKind,Integer>(LabelKind.class);
    /** Array of label kinds, in the order of their indices. */
    private static final LabelKind[] kindsArray = new LabelKind[EnumSet.allOf(
        LabelKind.class).size()];
    static {
        int index = 0;
        for (LabelKind kind : EnumSet.allOf(LabelKind.class)) {
            indexMap.put(kind, index);
            kindsArray[index] = kind;
            index++;
        }
    }
}

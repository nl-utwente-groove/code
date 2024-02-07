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
package nl.utwente.groove.graph;

import static nl.utwente.groove.util.Factory.lazy;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import nl.utwente.groove.annotation.Help;
import nl.utwente.groove.annotation.HelpMap;
import nl.utwente.groove.annotation.Syntax;
import nl.utwente.groove.annotation.ToolTipBody;
import nl.utwente.groove.annotation.ToolTipHeader;
import nl.utwente.groove.annotation.ToolTipPars;
import nl.utwente.groove.grammar.aspect.AspectParser;
import nl.utwente.groove.util.Factory;
import nl.utwente.groove.util.Pair;

/**
 * Role of an edge within a graph: node type, flag or binary.
 * @author Arend Rensink
 * @version $Revision$
 */
public enum EdgeRole {
    /** A node type edge, i.e., a self-loop determining the type of a node. */
    @Syntax("NODE_TYPE.COLON.label")
    @ToolTipHeader("Node type")
    @ToolTipBody("Represents a node type label")
    @ToolTipPars("the type name")
    NODE_TYPE("node type", "type"),

    /** A flag edge, i.e., a self-loop that stands for a node property. */
    @Syntax("FLAG.COLON.label")
    @ToolTipHeader("Flag")
    @ToolTipBody("Represents a node label (not a type)")
    @ToolTipPars("the text of the flag")
    FLAG("flag", "flag"),

    /** An ordinary binary edge. */
    @Syntax("label")
    @ToolTipHeader("Binary edge")
    @ToolTipBody("Represents a binary edge between nodes")
    @ToolTipPars("the edge label")
    BINARY("edge", "");

    private EdgeRole(String description, String name) {
        this.description = description;
        this.name = name;
        this.prefix = name.length() == 0
            ? name
            : name + AspectParser.SEPARATOR;
    }

    /**
     * Returns the name of this edge role.
     * The name is the prefix without the separator,
     * or the empty string in case of {@link #BINARY}.
     */
    public String getName() {
        return this.name;
    }

    /** Returns the label prefix associated with this edge role. */
    public String getPrefix() {
        return this.prefix;
    }

    /** Returns the description of this edge role, with the first letter optionally capitalised. */
    public String getDescription(boolean capitalised) {
        String result = this.description;
        if (capitalised) {
            char[] resultChars = result.toCharArray();
            resultChars[0] = Character.toUpperCase(resultChars[0]);
            result = new String(resultChars);
        }
        return result;
    }

    /** Returns the help item for this edge role. */
    public Help getDoc() {
        return getRoleToDocMap().get(this);
    }

    private final String description;
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
        return new Pair<>(resultRole, text.substring(resultRole.getPrefix().length()));
    }

    /**
     * Returns a unique index for every edge role.
     * The indices are guaranteed to range from 0 to the number of roles - 1.
     * This is the inverse to {@link #getRole(int)}
     */
    public static int getIndex(EdgeRole role) {
        return indexMap.get(role);
    }

    /**
     * Returns an edge role for a given index.
     * This is the inverse to {@link #getIndex(EdgeRole)}.
     */
    public static EdgeRole getRole(int index) {
        return rolesArray[index];
    }

    /**
     * Returns an edge role for a given (non-empty) role name.
     */
    public static EdgeRole getRole(String name) {
        assert name.length() > 0;
        return symbolToRoleMap.get(name);
    }

    /** Returns the documentation map per edge role. */
    public static Map<EdgeRole,Help> getRoleToDocMap() {
        return roleToDocMap.get();
    }

    private static final Factory<Map<EdgeRole,Help>> roleToDocMap
        = lazy(EdgeRole::computeRoleToDocMap);

    /** Computes the documentation map per edge role. */
    private static Map<EdgeRole,Help> computeRoleToDocMap() {
        Map<EdgeRole,Help> result = new EnumMap<>(EdgeRole.class);
        for (Field field : EdgeRole.class.getFields()) {
            if (field.isEnumConstant()) {
                EdgeRole role = symbolToRoleMap.get(nameToSymbolMap.get(field.getName()));
                result.put(role, Help.createHelp(field, nameToSymbolMap));
            }
        }
        return result;
    }

    /** Returns the documentation map for all edge roles. */
    public static HelpMap getDocMap() {
        return docMap.get();
    }

    private static final Factory<HelpMap> docMap = lazy(EdgeRole::computeDocMap);

    /** Computes the documentation map for all edge roles. */
    private static HelpMap computeDocMap() {
        HelpMap result = new HelpMap();
        for (Help doc : getRoleToDocMap().values()) {
            result.add(doc);
        }
        return result;
    }

    /** Injective mapping from edge roles to indices. */
    private static final Map<EdgeRole,Integer> indexMap = new EnumMap<>(EdgeRole.class);
    /** Injective mapping from role symbols to edge roles. */
    private static final Map<String,EdgeRole> symbolToRoleMap = new HashMap<>();
    /** Injective mapping from role names to symbol text. */
    public static final Map<String,String> nameToSymbolMap = new HashMap<>();
    /** Array of edge roles, in the order of their indices. */
    private static final EdgeRole[] rolesArray = new EdgeRole[EdgeRole.values().length];
    static {
        int index = 0;
        for (EdgeRole role : EdgeRole.values()) {
            indexMap.put(role, index);
            symbolToRoleMap.put(role.getName(), role);
            rolesArray[index] = role;
            nameToSymbolMap.put(role.name(), role.getName());
            index++;
        }
        nameToSymbolMap.put("COLON", "" + AspectParser.SEPARATOR);
    }
}

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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/** Role of a graph within a rule system.
 *
 * @author Arend Rensink
 * @version $Revision $
 */
public enum GraphRole {
    /** Unspecified graph role. */
    NONE("none", "No Graph"),
    /** Host graph role. */
    HOST("graph", "Graph"),
    /** Rule graph role. */
    RULE("rule", "Rule Graph"),
    /** Type graph role. */
    TYPE("type", "Type Graph"),
    /** LTS graph role. */
    LTS("lts", "LTS"),
    /** RETE graph role. */
    RETE("rete", "Rete Network"),
    /** Control automaton role. */
    CTRL("control", "Control Automaton"),
    /** Buchi automaton role. */
    BUCHI("buchi", "Buchi Automaton"),
    /** Unknown role. */
    UNKNOWN("unknown", "Unknown graph type");

    private GraphRole(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return this.name;
    }

    /** Returns a short (1- or 2-word) description of this graph role. */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the name of this role, with the first letter
     * capitalised on demand.
     * @param upper if <code>true</code>, the first letter is capitalised
     */
    public String toString(boolean upper) {
        String roleName = toString();
        if (upper) {
            char[] result = roleName.toCharArray();
            result[0] = Character.toUpperCase(result[0]);
            return String.valueOf(result);
        } else {
            return roleName;
        }
    }

    /**
     * Indicates if this is a grammar-related role.
     * @see #grammarRoles
     */
    public boolean inGrammar() {
        return grammarRoles.contains(this);
    }

    private final String description;
    private final String name;

    /** Map from graph role names to graph roles. */
    static public final Map<String,GraphRole> roles = new HashMap<>();
    static {
        for (GraphRole role : GraphRole.values()) {
            roles.put(role.toString(), role);
        }
    }

    /** Set of roles that are part of the grammar. */
    public static final EnumSet<GraphRole> grammarRoles = EnumSet.of(HOST, RULE, TYPE);
}

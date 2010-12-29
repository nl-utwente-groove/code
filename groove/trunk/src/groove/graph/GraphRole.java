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
    NONE("none"),
    /** Host graph role. */
    HOST("graph"),
    /** Rule graph role. */
    RULE("rule"),
    /** Type graph role. */
    TYPE("type"),
    /** LTS graph role. */
    LTS("lts"),
    /** RETE graph role. */
    RETE("rete"),
    /** Control automaton role. */
    CTRL("control");

    private GraphRole(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    /** 
     * Indicates if this is a grammar-related role.
     * @see #grammarRoles 
     */
    public boolean inGrammar() {
        return grammarRoles.contains(this);
    }

    private final String name;

    /** Map from graph role names to graph roles. */
    static public final Map<String,GraphRole> roles =
        new HashMap<String,GraphRole>();
    static {
        for (GraphRole role : EnumSet.allOf(GraphRole.class)) {
            roles.put(role.toString(), role);
        }
    }

    /** Set of roles that are part of the grammar. */
    public static final EnumSet<GraphRole> grammarRoles = EnumSet.of(HOST,
        RULE, TYPE);
}

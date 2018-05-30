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
package groove.grammar.model;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import groove.grammar.QualName;
import groove.graph.GraphRole;
import groove.io.FileType;
import groove.util.Groove;

/**
 * Abstract type of the resources that make up a grammar.
 * @author Arend Rensink
 * @version $Revision $
 */
public enum ResourceKind {
    /** Host graph resources; in other words, potential start graphs of the grammar. */
    HOST("Graph", "host graph", GraphRole.HOST, FileType.STATE, Groove.DEFAULT_START_GRAPH_NAME),
    /** Transformation rule resources. */
    RULE("Rule", "rule", GraphRole.RULE, FileType.RULE, null),
    /** Type graph resources. */
    TYPE("Type", "type graph", GraphRole.TYPE, FileType.TYPE, Groove.DEFAULT_TYPE_NAME),
    /**
     * Control program resources.
     */
    CONTROL("Control", "control program", FileType.CONTROL, Groove.DEFAULT_CONTROL_NAME),
    /** Prolog program resources. */
    PROLOG("Prolog", "prolog program", FileType.PROLOG, null),
    /** Grammar properties resource. */
    PROPERTIES("Properties", "grammar properties", FileType.PROPERTY, Groove.PROPERTY_NAME),
    /** Groovy script resources. */
    GROOVY("Groovy", "groovy script", FileType.GROOVY, null),
    /** Configuration resource. */
    CONFIG("Configuration", "configuration file", FileType.CONFIG, null);

    /** Constructs a value with no corresponding graph role. */
    private ResourceKind(String name, String description, FileType fileType, String defaultName) {
        this(name, description, GraphRole.NONE, fileType, defaultName);
    }

    /** Constructs a value with a given graph role. */
    private ResourceKind(String name, String description, GraphRole graphRole, FileType fileType,
        String defaultName) {
        this.graphRole = graphRole;
        this.description = description;
        this.name = name;
        this.fileType = fileType;
        this.defaultName =
            Optional.ofNullable(defaultName == null ? null : QualName.name(defaultName));
    }

    /** Returns the graph role associated with this resource kind,
     * or {@link GraphRole#NONE} if there is no corresponding graph role.
     */
    public GraphRole getGraphRole() {
        return this.graphRole;
    }

    /**
     * Indicates if this resource is graph-based.
     * This holds if and only if the resource kind has a proper graph role.
     * @see #getGraphRole()
     */
    public boolean isGraphBased() {
        return getGraphRole() != GraphRole.NONE;
    }

    /**
     * Indicates if this resource is text-based.
     * This holds if and only if it is not equal to {@link #PROPERTIES},
     * and is not graph-based.
     * @see #isGraphBased()
     */
    public boolean isTextBased() {
        return this != PROPERTIES && !isGraphBased();
    }

    /** Returns the (capitalised) name of this kind of resource. */
    public String getName() {
        return this.name;
    }

    /** Returns the default name of a resource of this kind, if any.
     * @return a default name, of {@code null} if there is no default
     */
    public Optional<QualName> getDefaultName() {
        return this.defaultName;
    }

    /**
     * Returns a short description of this resource kind,
     * starting with lowercase.
     */
    public String getDescription() {
        return this.description;
    }

    /** Returns the file filter for this kind of resource. */
    public FileType getFileType() {
        return this.fileType;
    }

    /** Flag indicating if this resource kind has additional (editable) properties. */
    public boolean hasProperties() {
        return this == RULE;
    }

    /** Indicates if this resource can be changed to enabled. */
    public boolean isEnableable() {
        return this != PROPERTIES && this != CONFIG && this != GROOVY;
    }

    /** Indicates if this resource has a display in the simulator. */
    public boolean hasDisplay() {
        return this != PROPERTIES && this != CONFIG;
    }

    /** The graph role associated with this resource kind, or {@link GraphRole#NONE}
     * if there is no corresponding graph role.
     */
    private final GraphRole graphRole;
    /** Name of this resource kind. */
    private final String name;
    /** Description starting with lowercase letter. */
    private final String description;
    /** File filter for this resource kind. */
    private final FileType fileType;
    /** Default name of this resource kind, if any. */
    private final Optional<QualName> defaultName;

    /**
     * Returns the resource kind of a given graph role or {@code null}
     *  if the graph role does not correspond to a resource kind.
     */
    public static ResourceKind toResource(GraphRole graphRole) {
        return roleKindMap.get(graphRole);
    }

    /**
     * Returns the set of all resource kinds, possibly excluding {@link #PROPERTIES}.
     * @param withProperties if {@code true}, {@link #PROPERTIES} is included
     * in the result.
     */
    public static Set<ResourceKind> all(boolean withProperties) {
        return withProperties ? allResources : allNonProperties;
    }

    /** Set of all resource kinds. */
    private static final Set<ResourceKind> allResources = EnumSet.allOf(ResourceKind.class);
    /** Set of resource kinds that do not equal {@link #PROPERTIES}. */
    private static final Set<ResourceKind> allNonProperties = EnumSet.allOf(ResourceKind.class);

    static {
        allNonProperties.remove(PROPERTIES);
    }

    private static Map<GraphRole,ResourceKind> roleKindMap = new EnumMap<>(GraphRole.class);

    static {
        for (ResourceKind kind : ResourceKind.values()) {
            if (kind.isGraphBased()) {
                roleKindMap.put(kind.getGraphRole(), kind);
            }
        }
    }
}

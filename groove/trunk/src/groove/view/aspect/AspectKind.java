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
package groove.view.aspect;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Distinguishes the aspects that can be found in a plain graph representation
 * of a rule, host graph or type graph.
 * @author Arend Rensink
 * @version $Revision $
 */
public enum AspectKind {
    /** Used for comments/documentation. */
    REMARK("rem"),

    // rule roles
    /** Indicates an unmodified element. */
    READER("use"),
    /** Indicates an element to be deleted. */
    ERASER("del"),
    /** Indicates an element to be created. */
    CREATOR("new"),
    /** Indicates a forbidden element. */
    EMBARGO("not"),

    // data types
    /** Indicates a data value of unknown type. */
    ATTR("attr"),
    /** Indicates a boolean value or operator. */
    BOOL("bool"),
    /** Indicates an integer value or operator. */
    INT("int"),
    /** Indicates a floating-point value or operator. */
    REAL("real"),
    /** Indicates a string value or operator. */
    STRING("string"),

    // auxiliary attribute-related aspects
    /** Indicates an argument edge. */
    ARGUMENT("arg"),
    /** Indicates a product node. */
    PRODUCT("prod"),

    // rule parameters
    /** Indicates a rule parameter. */
    PARAMETER("par"),

    // type-related aspects
    /** Indicates an abstract type. */
    ABSTRACT("abs"),
    /** Indicates a subtype relation. */
    SUBTYPE("sub"),

    // label-related aspects
    /** Indicates that the remainder of the label is a regular expression. */
    PATH("path"),
    /** Indicates that the remainder of the label is a literal. */
    LITERAL(""),

    // quantifier-related aspects
    /** Universal quantifier. */
    FORALL("forall"),
    /** Non-vacuous universal quantifier. */
    FORALL_POS("forallx"),
    /** Existential quantifier. */
    EXISTS("exists"),
    /** Nesting edge. */
    NESTED("nested");

    private AspectKind(String name) {
        this.name = name;
    }

    /** Returns the name of this aspect kind. */
    public String getName() {
        return this.name;
    }

    /** 
     * Indicates if this aspect is among the set of roles.
     * @see #roles 
     */
    public boolean isRole() {
        return roles.contains(this);
    }

    /** 
     * Indicates if this aspect is among the set of NAC elements.
     * @see #nac 
     */
    public boolean isNAC() {
        return nac.contains(this);
    }

    /** 
     * Indicates if this aspect is among the set of LHS element.
     * @see #lhs 
     */
    public boolean isLHS() {
        return lhs.contains(this);
    }

    /** 
     * Indicates if this aspect is among the set of RHS elements.
     * @see #rhs 
     */
    public boolean isRHS() {
        return rhs.contains(this);
    }

    /** 
     * Indicates if this aspect is among the set of type aspects.
     * @see #types 
     */
    public boolean isType() {
        return types.contains(this);
    }

    /** 
     * Indicates if this aspect is among the set of data aspects.
     * @see #data 
     */
    public boolean isData() {
        return data.contains(this);
    }

    /** 
     * Indicates if this aspect is among the set of meta-aspects.
     * @see #meta 
     */
    public boolean isMeta() {
        return meta.contains(this);
    }

    /** 
     * Indicates if this aspect is among the set of quantifiers.
     * @see #quantifier 
     */
    public boolean isQuantifier() {
        return quantifier.contains(this);
    }

    /** Indicates that this aspect kind is allowed to appear on edges. */
    public boolean isForEdge() {
        return !nodeOnly.contains(this);
    }

    /** Indicates that this aspect kind is allowed to appear on nodes. */
    public boolean isForNode() {
        return !edgeOnly.contains(this);
    }

    /** Indicates that this aspect kind is always the last on a label. */
    public boolean isLast() {
        return !series.contains(this);
    }

    private final String name;

    /** 
     * Returns the aspect kind corresponding to a certain non-{@code null}
     * name, or {@code null} if there is no such aspect kind.
     */
    public static AspectKind parse(String name) {
        return aspectMap.get(name);
    }

    /** Static mapping from all aspect names to aspects. */
    private static final Map<String,AspectKind> aspectMap =
        new HashMap<String,AspectKind>();

    static {
        // initialise the aspect map
        for (AspectKind aspect : EnumSet.allOf(AspectKind.class)) {
            AspectKind oldAspect = aspectMap.put(aspect.toString(), aspect);
            assert oldAspect == null;
        }
    }

    /** Set of role aspects. */
    public static EnumSet<AspectKind> roles = EnumSet.of(ERASER, CREATOR,
        READER, EMBARGO);
    /** Set of role aspects appearing in NACs. */
    public static EnumSet<AspectKind> nac = EnumSet.of(READER, EMBARGO);
    /** Set of role aspects appearing in LHSs. */
    public static EnumSet<AspectKind> lhs = EnumSet.of(READER, ERASER);
    /** Set of role aspects appearing in RHSs. */
    public static EnumSet<AspectKind> rhs = EnumSet.of(READER, CREATOR);
    /** Set of typed data aspects. */
    public static EnumSet<AspectKind> types = EnumSet.of(ATTR, STRING, BOOL,
        INT, REAL);
    /** Set of data aspects, typed or untyped. */
    public static EnumSet<AspectKind> data =
        EnumSet.of(STRING, BOOL, INT, REAL);
    /** Set of meta-aspects, i.e., which do not reflect real graph structure. */
    public static EnumSet<AspectKind> meta = EnumSet.of(FORALL, FORALL_POS,
        EXISTS, NESTED, REMARK);
    /** Set of quantifier aspects, i.e., which do not reflect real graph structure. */
    public static EnumSet<AspectKind> quantifier = EnumSet.of(FORALL,
        FORALL_POS, EXISTS);

    /** Set of all aspects that can be used <i>only</i> on nodes. */
    public static EnumSet<AspectKind> nodeOnly = EnumSet.of(ATTR, PRODUCT);
    /** Set of all aspects that can be used <i>only</i> on edges. */
    public static EnumSet<AspectKind> edgeOnly = EnumSet.of(ARGUMENT,
        PARAMETER, SUBTYPE, PATH, LITERAL, NESTED);
    /** Set of aspects that may be followed by others, when used in an edge label. */
    public static EnumSet<AspectKind> series = EnumSet.of(READER, ERASER,
        CREATOR, EMBARGO, FORALL, FORALL_POS, EXISTS);
}

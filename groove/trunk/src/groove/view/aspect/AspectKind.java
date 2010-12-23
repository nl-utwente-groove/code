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
    /** Indicates an element to be deleted. */
    ERASER("del"),
    /** Indicates an element to be created. */
    CREATOR("new"),
    /** Indicates an unmodified element. */
    READER("use"),
    /** Indicates a forbidden element. */
    EMBARGO("not"),

    // data types
    /** Indicates a data value of unknown type. */
    ATTR("attr"),
    /** Indicates a string value or operator. */
    STRING("string"),
    /** Indicates a boolean value or operator. */
    BOOL("bool"),
    /** Indicates an integer value or operator. */
    INT("int"),
    /** Indicates a floating-point value or operator. */
    REAL("real"),

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

    private final String name;

    /** 
     * Returns the aspect kind corresponding to a certain non-{@code null}
     * name, or {@code null} if there is no such aspect kind.
     */
    public static AspectKind parse(String name) {
        for (AspectKind aspect : EnumSet.allOf(AspectKind.class)) {
            if (aspect.toString().equals(name)) {
                return aspect;
            }
        }
        return null;
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
}

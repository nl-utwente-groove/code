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
package nl.utwente.groove.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.grammar.aspect.AspectContent.ContentKind;
import nl.utwente.groove.grammar.aspect.AspectKind;

/**
 * Reserved wordt used in GROOVE
 * @author Arend Rensink
 * @version $Revision$
 */
public class Keywords {
    /**
     * The boolean type.
     * @see Sort#BOOL
     * @see AspectKind#BOOL
     */
    @Reserved
    public static final String BOOL = "bool";
    /**
     * Boolean value for "false"
     * @see Sort#BOOL
     */
    @Reserved
    public static final String FALSE = "false";
    /** The id prefix.
     * @see AspectKind#ID
     */
    public static final String ID = "id";
    /** The integer type.
     * @see Sort#INT
     * @see AspectKind#INT
     */
    @Reserved
    public static final String INT = "int";

    /** Error value denotation of {@link Sort#BOOL}. */
    @Reserved
    public static final String NAB = "NaB";

    /** Error value denotation of {@link Sort#INT}. */
    @Reserved
    public static final String NAI = "NaI";

    /** Error value denotation of {@link Sort#REAL}. */
    @Reserved
    public static final String NAR = "NaR";

    /** Error value denotation of {@link Sort#STRING}. */
    @Reserved
    public static final String NAS = "NaS";

    /** Error value denotation of {@link Sort#USER}. */
    @Reserved
    public static final String NAU = "NaU";

    /**
     * The parameter prefix.
     * @see AspectKind#PARAM_BI
     */
    public static final String PAR = "par";
    /**
     * The input parameter prefix.
     * @see AspectKind#PARAM_IN
     */
    public static final String PAR_IN = "parin";
    /**
     * The output parameter prefix.
     * @see AspectKind#PARAM_OUT
     */
    public static final String PAR_OUT = "parout";
    /**
     * The interactive parameter prefix.
     * @see AspectKind#PARAM_ASK
     */
    public static final String PAR_ASK = "ask";
    /** The real type.
     * @see Sort#REAL
     * @see AspectKind#REAL
     */
    @Reserved
    public static final String REAL = "real";
    /**
     * The self keyword in attribute expressions.
     * @see ContentKind#NODE_NAME
     */
    @Reserved
    public static final String SELF = "self";
    /** The string type.
     * @see Sort#STRING
     * @see AspectKind#STRING
     */
    @Reserved
    public static final String STRING = "string";
    /**
     * Boolean value for "true"
     * @see Sort#BOOL
     */
    @Reserved
    public static final String TRUE = "true";
    /** The user-defined value type.
     * @see Sort#USER
     * @see AspectKind#USER
     */
    @Reserved
    public static final String USER = "user";

    /** Tests whether a given (identifier) string is a reserved keyword. */
    public static boolean isReserved(String id) {
        return keywords.get().contains(id);
    }

    /** The set of reserved keywords defined in this class. */
    static private final Supplier<Set<String>> keywords
        = LazyFactory.instance(Keywords::computeKeywords);

    /** Computes the value of {@link #keywords}. */
    static private Set<String> computeKeywords() {
        Set<String> result = new HashSet<>();
        for (var field : Keywords.class.getDeclaredFields()) {
            if (field.getAnnotationsByType(Reserved.class).length > 0) {
                try {
                    result.add((String) field.get(null));
                } catch (IllegalArgumentException | IllegalAccessException exc) {
                    // do nothing
                }
            }
        }
        return result;
    }

    /** Annotation for reserved keywords, i.e., which may not be used as identifiers. */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    static public @interface Reserved {
        // empty
    }
}

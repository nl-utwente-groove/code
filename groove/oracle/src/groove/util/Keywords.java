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
package groove.util;

import groove.algebra.Sort;
import groove.algebra.syntax.Parameter;
import groove.grammar.aspect.AspectKind;
import groove.grammar.aspect.AspectKind.ContentKind;

/**
 * Keywords used in GROOVE
 * @author Arend Rensink
 * @version $Revision $
 */
public class Keywords {
    /**
     * The boolean type.
     * @see Sort#BOOL
     * @see AspectKind#BOOL
     */
    public static final String BOOL = "bool";
    /**
     * Boolean value for "false"
     * @see Sort#BOOL
     */
    public static final String FALSE = "false";
    /** The id prefix.
     * @see AspectKind#ID
     */
    public static final String ID = "id";
    /** The integer type.
     * @see Sort#INT
     * @see AspectKind#INT
     */
    public static final String INT = "int";
    /**
     * The parameter prefix.
     * @see AspectKind#PARAM_BI
     * @see Parameter
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
    public static final String REAL = "real";
    /**
     * The self keyword in attribute expressions.
     * @see ContentKind#NAME
     */
    public static final String SELF = "self";
    /** The string type.
     * @see Sort#STRING
     * @see AspectKind#STRING
     */
    public static final String STRING = "string";
    /**
     * Boolean value for "true"
     * @see Sort#BOOL
     */
    public static final String TRUE = "true";
}

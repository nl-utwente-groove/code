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

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Catalogue of special characters used in the textual rendering of
 * graphs, labels and expressions.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class Unicode {
    /** Unicode hex string for "there exists" (existential quantifier) */
    public final static char EXISTS = '\u2203';
    /** Unicode hex string for "for all" (universal quantifier) */
    public final static char FORALL = '\u2200';
    /** Unicode hex string for "and" (conjunction) */
    public final static char WEDGE = '\u2227';
    /** Unicode hex string for "or" (disjunction) */
    public final static char VEE = '\u2228';
    /** Unicode hex string for "not" (negation) */
    public final static char NEG = '\u00AC';
    /** Unicode plusminus symbol. */
    static public final char PLUSMINUS = '\u00b1';
    /** Unicode times symbol. */
    static public final char TIMES = '\u00D7';

    /** Lower case letter pi. */
    public static final char LC_PI = '\u03C0';
    /** Lower case letter tau. */
    static public final char LC_TAU = '\u03C4';
    /** Lower case letter epsilon. */
    static public final char LC_EPSILON = '\u03B5';
    /** Lower case letter lambda. */
    static public final char LC_LAMBDA = '\u03BB';
    /** Upper case letter omega. */
    static public final char UC_OMEGA = '\u03A9';

    /** HTML opening {@code <<} quote symbol. */
    static public final char FRENCH_QUOTES_OPEN = '\u00AB';
    /** HTML closing {@code >>} quote symbol. */
    static public final char FRENCH_QUOTES_CLOSED = '\u00BB';
    /** HTML greater than symbol. */
    static public final char GT = '>';
    /** HTML left angular bracket symbol. */
    static public final char LANGLE = '<'; // &#9001;
    /** HTML right angular bracket symbol. */
    static public final char RANGLE = '>'; // &#9002;

    /** Unicode small right-triangle symbol; large/small = ..B6/B8. */
    static public final char RT = '\u25B8';
    /** Unicode large right-triangle symbol; large/small = ..B6/B8. */
    static public final char RT_LARGE = '\u25B6';
    /** Unicode left-triangle symbol; large/small = ..C0/C2. */
    static public final char LT = '\u25C2';
    /** Unicode up-triangle symbol; large/small = ..B2/B4. */
    static public final char UT = '\u25B4';
    /** Unicode down-triangle symbol; large/small = ..BC/BE. */
    static public final char DT = '\u25BE';
    /** Unicode right-arrow symbol. */
    static public final char RA = '\u2192';
    /** Unicode left-arrow symbol. */
    static public final char LA = '\u2190';
    /** Unicode up-arrow symbol. */
    static public final char UA = '\u2191';
    /** Unicode down-arrow symbol. */
    static public final char DA = '\u2193';
    /** Unicode up-right-arrow symbol. */
    static public final char URA = '\u2197';
    /** Unicode up-left-arrow symbol. */
    static public final char ULA = '\u2196';
    /** Unicode down-right-arrow symbol. */
    static public final char DRA = '\u2198';
    /** Unicode down-left-arrow symbol. */
    static public final char DLA = '\u2199';
    /** Unicode  circular arrow symbol. */
    static public final char CA = '\u21ba';

    /** Unicode info symbol. */
    static public final char INFO_SYMBOL = '\u24D8';

    /** Unicode thin space symbol. */
    static public final char HAIR_SPACE = '\u200A';
    /** Unicode thin space symbol. */
    static public final char THIN_SPACE = '\u2009';
    /** Unicode end-of-text character. */
    static public final char EOT = '\u0003';
}

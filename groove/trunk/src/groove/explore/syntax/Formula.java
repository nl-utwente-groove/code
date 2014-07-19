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
package groove.explore.syntax;

import groove.control.Call;
import groove.grammar.type.TypeLabel;
import groove.util.line.Line;
import groove.util.parse.FormatException;
import groove.util.parse.Precedence;

import org.antlr.runtime.RecognitionException;

/**
 * Expressions are constants, variables, field expressions or call expressions.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class Formula {
    /**
     * Constructor for subclasses.
     * @param kind the kind of formula
     */
    protected Formula(Kind kind) {
        this.kind = kind;
        assert this.kind != null;
    }

    /**
     * Returns a text representation of the term.
     * The difference with {@link #toString()} is that
     * the display string does not contain type prefixes.
     */
    final public String toDisplayString() {
        return toLine().toFlatString();
    }

    /**
     * Returns a text representation of the term.
     * The difference with {@link #toString()} is that
     * the display string does not contain type prefixes.
     */
    final public Line toLine() {
        return toLine(Precedence.NONE);
    }

    /**
     * Builds the display string for this expression in the
     * result parameter.
     */
    abstract protected Line toLine(Precedence context);

    /**
     * Returns a string representation from which
     * this expression can be been parsed.
     * If the expression has been constructed rather
     * than parsed, calls {@link #createParseString()}.
     * @see #toDisplayString()
     */
    public String toParseString() {
        if (this.parseString == null) {
            this.parseString = createParseString();
        }
        return this.parseString;
    }

    /**
     * Callback method to create the input string for such
     * expressions that were constructed rather than parsed.
     */
    abstract protected String createParseString();

    /** Sets the string from which this expression has been parsed. */
    public void setParseString(String parseString) {
        this.parseString = parseString;
    }

    /** The string from which this expression has been parsed, if any. */
    private String parseString;

    /** Returns the precedence of the top-level operator of this expression,
     * or {@link Precedence#ATOM} if this is not a call expression.
     */
    public Precedence getPrecedence() {
        return Precedence.ATOM;
    }

    /**
     * Returns an expression obtained from this one by changing all
     * occurrences of a certain label into another.
     * In particular, this concerns field names.
     * @param oldLabel the label to be changed
     * @param newLabel the new value for {@code oldLabel}
     * @return a clone of this object with changed labels, or this object
     *         if {@code oldLabel} did not occur
     */
    public Formula relabel(TypeLabel oldLabel, TypeLabel newLabel) {
        return this;
    }

    /** Returns the expression kind of this expression. */
    public final Kind getKind() {
        return this.kind;
    }

    /** The expression kind of this expression. */
    private final Kind kind;

    /**
     * Returns the formula tree for a given string.
     * @param term the string to be parsed as a formula
     */
    public static Formula parse(String term) throws FormatException {
        return parseToTree(term).toFormula();
    }

    /**
     * Returns the formula tree for a given string.
     * @param term the string to be parsed as a formula
     */
    private static FormulaTree parseToTree(String term) throws FormatException {
        FormulaParser parser = FormulaTree.getParser(term);
        try {
            FormulaTree result = (FormulaTree) parser.formula().getTree();
            parser.getErrors().throwException();
            return result;
        } catch (FormatException e) {
            throw new FormatException("Can't parse %s: %s", term, e.getMessage());
        } catch (RecognitionException re) {
            throw new FormatException(re);
        }
    }

    /** Call with &lt;formula> */
    public static void main(String[] args) {
        try {
            FormulaTree tree = parseToTree(args[0]);
            System.out.printf("Original expression: %s%n", args[0]);
            System.out.printf("Flattened term tree: %s%n", tree.toStringTree());
            System.out.printf("Corresponding term:  %s%n", tree.toFormula());
            System.out.printf("Display string:      %s%n", tree.toFormula().toDisplayString());
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    /** Atomic formula. */
    public static class Atom extends Formula {
        /** Creates an atomic formula for a given call. */
        public Atom(Call call) {
            super(Kind.ATOM);
            this.call = call;
        }

        @Override
        protected Line toLine(Precedence context) {
            return Line.atom(this.call.toString());
        }

        @Override
        protected String createParseString() {
            return this.call.toString();
        }

        private final Call call;
    }

    /** Expression kinds. */
    public static enum Kind {
        /** Atomic proposition. */
        ATOM,
        /** And operator. */
        AND,
        /** Or operator. */
        OR,
        /** Left-to-right implication operator. */
        IMPLIES,
        /** Right-to-left implication operator. */
        IMPLIED_BY,
        /** Equivalence. */
        EQUIV,
        /** Negation. */
        NEG,
        /** Truth. */
        TRUE,
        /** Falsehood. */
        FALSE, ;
    }
}

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
package groove.algebra.syntax;

import groove.grammar.type.TypeLabel;
import groove.graph.EdgeRole;
import groove.util.line.Line;
import groove.util.parse.FormatException;

/**
 * Assignment in a host or rule graph.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Assignment {
    /** Constructs an assignment from a left hand side and right hand side. */
    public Assignment(String lhs, Expression rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    /** Returns the target (left hand side) of the assignment. */
    public String getLhs() {
        return this.lhs;
    }

    /** Returns the right hand side expression of the assignment. */
    public Expression getRhs() {
        return this.rhs;
    }

    /** 
     * Returns a string representation from which
     * this assignment can be been parsed.
     * If the assignment has been constructed rather
     * than parsed, calls {@link #toString()}.
     */
    public String toParseString() {
        if (this.parseString == null) {
            this.parseString = toString();
        }
        return this.parseString;
    }

    /** Sets the string from which this expression has been parsed. */
    public void setParseString(String parseString) {
        this.parseString = parseString;
    }

    /** The string from which this expression has been parsed, if any. */
    private String parseString;

    /** 
     * Returns the display line used by the GUI.
     * @param assignSymbol the assignment symbol to be used
     */
    public Line toLine(String assignSymbol) {
        StringBuilder result = new StringBuilder(getLhs());
        result.append(' ');
        result.append(assignSymbol == null ? "=" : assignSymbol);
        result.append(' ');
        return Line.atom(result.toString()).append(getRhs().toLine());
    }

    @Override
    public String toString() {
        return getLhs() + " = " + getRhs().toParseString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.lhs.hashCode();
        result = prime * result + this.rhs.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Assignment)) {
            return false;
        }
        Assignment other = (Assignment) obj;
        return this.lhs.equals(other.lhs) && this.rhs.equals(other.rhs);
    }

    /**
     * Returns an assignment obtained from this one by changing all
     * occurrences of a certain label into another.
     * @param oldLabel the label to be changed
     * @param newLabel the new value for {@code oldLabel}
     * @return a clone of this object with changed labels, or this object
     *         if {@code oldLabel} did not occur
     */
    public Assignment relabel(TypeLabel oldLabel, TypeLabel newLabel) {
        Assignment result = this;
        if (oldLabel.getRole() == EdgeRole.BINARY) {
            Expression newRhs = getRhs().relabel(oldLabel, newLabel);
            String newLhs = oldLabel.text().equals(getLhs()) ? newLabel.text() : getLhs();
            if (newRhs != getRhs() || newLhs != getLhs()) {
                result = new Assignment(newLhs, newRhs);
            }
        }
        return result;
    }

    private final Expression rhs;
    private final String lhs;

    /**
     * Attempts to parse a given string as an assignment.
     * @param text the string that is to be parsed as assignment
     * @return the resulting assignment
     * @throws FormatException if the input string contains syntax errors
     */
    public static Assignment parse(String text) throws FormatException {
        return ExprTreeParser.parseAssign(text).toAssignment();
    }
}

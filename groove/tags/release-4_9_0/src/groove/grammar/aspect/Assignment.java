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
package groove.grammar.aspect;

import groove.algebra.syntax.Expression;
import groove.grammar.model.FormatException;
import groove.grammar.type.TypeLabel;
import groove.graph.EdgeRole;

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

    /** Returns the identifier, if this is an identifier expression. */
    public String getLhs() {
        return this.lhs;
    }

    /** Returns the right hand side of the assignment. */
    public Expression getRhs() {
        return this.rhs;
    }

    @Override
    public String toString() {
        return getLhs() + " = " + getRhs().toInputString();
    }

    /** 
     * Returns the string to be used by the GUI.
     * @param assignSymbol the assignment symbol to be used
     */
    public String toDisplayString(String assignSymbol) {
        StringBuilder result = new StringBuilder(getLhs());
        result.append(' ');
        result.append(assignSymbol == null ? "=" : assignSymbol);
        result.append(' ');
        result.append(getRhs().toDisplayString());
        return result.toString();
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
            groove.algebra.syntax.Expression newRhs =
                getRhs().relabel(oldLabel, newLabel);
            String newLhs =
                oldLabel.text().equals(getLhs()) ? newLabel.text() : getLhs();
            if (newRhs != getRhs() || newLhs != getLhs()) {
                result = new Assignment(newLhs, newRhs);
            }
        }
        return result;
    }

    private final groove.algebra.syntax.Expression rhs;
    private final String lhs;

    /**
     * Attempts to parse a given string as an expression.
     * @param text the string that is to be parsed as expression
     * @return the resulting expression
     * @throws FormatException if the input string contains syntax errors
     */
    public static Assignment parse(String text) throws FormatException {
        if (text.length() == 0) {
            throw new FormatException(
                "Empty string cannot be parsed as assignment");
        }
        int pos = text.indexOf('=');
        if (pos < 0) {
            throw new FormatException(
                "Assignment expression '%s' does not contain '='", text);
        }
        String lhs = text.substring(0, pos).trim();
        String rhs = text.substring(pos + 1, text.length()).trim();
        if (!isIdentifier(lhs)) {
            throw new FormatException(
                "Assignment target '%s' is not an identifier", lhs);
        }
        return new Assignment(lhs, groove.algebra.syntax.Expression.parse(rhs));
    }

    private static boolean isIdentifier(String text) {
        boolean result = text.length() > 0;
        if (result) {
            result = Character.isJavaIdentifierStart(text.charAt(0));
            for (int i = 1; result && i < text.length(); i++) {
                result = Character.isJavaIdentifierPart(text.charAt(i));
            }
        }
        return result;
    }
}

/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: TemporalFormula.java,v 1.8 2008-03-05 16:52:10 rensink Exp $
 */

package groove.verify;

import groove.graph.Label;
import groove.lts.GTS;
import groove.lts.State;
import groove.trans.Condition;
import groove.trans.RuleName;
import groove.util.ExprParser;
import groove.verify.CTLStarFormula.All;
import groove.verify.CTLStarFormula.And;
import groove.verify.CTLStarFormula.Atom;
import groove.verify.CTLStarFormula.Exists;
import groove.verify.CTLStarFormula.Finally;
import groove.verify.CTLStarFormula.Globally;
import groove.verify.CTLStarFormula.Neg;
import groove.verify.CTLStarFormula.Next;
import groove.verify.CTLStarFormula.Or;
import groove.verify.CTLStarFormula.Until;
import groove.view.FormatException;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Abstract class as a generalization of LTL and CTL formulas.
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2008-03-05 16:52:10 $
 */
public abstract class TemporalFormula {

    /**
     * Constructor.
     * @param operator the top-level operator of this formula
     */
    public TemporalFormula(String operator) {
        this.operator = operator;
    }

    /**
     * Returns the operator of this CTL-expression.
     * @return the operator of this CTL-expression.
     */
    public String getOperator() {
        return this.operator;
    }

    /**
     * Sets the {@link #FACTORY} field to the given value.
     * @param factory the new {@link #FACTORY} value
     */
    static public void setFactory(CTLStarFormula factory) {
        FACTORY = factory;
    }

    /**
     * @return the {@link #FACTORY} value
     */
    static public CTLStarFormula getFactory() {
        return FACTORY;
    }

    /**
     * Comment for <code>prototypes</code>
     */
    static private final TemporalFormula[] prototypes = new TemporalFormula[] {
        new Exists(), new All(), new Finally(), new Globally(), new And(),
        new Or(), new Next(), new Until(), new Neg(), new Atom()};

    /**
     * The list of operators into which a ctl expression will be parsed, in
     * order of increasing priority.
     */
    static private final List<String> operators;

    static {
        List<String> result = new LinkedList<String>();
        for (TemporalFormula prototype : prototypes) {
            if (!(prototype instanceof Atom)) {
                result.add(prototype.getOperator());
            }
        }
        operators = result;
    }

    /** The factory for creating new instances of the logical operators. */
    static public CTLStarFormula FACTORY;
    /** The top-level operator of this formula. */
    private final String operator;
    /** The set of states that do not fulfil this formula. */
    private Set<State> counterExamples;

    /**
     * Parse the given expression with the current operator and return the
     * correct CTL-expression. If the given expression is in the wrong format,
     * throw an exception.
     * 
     * @param expr the expression to be parsed
     * @return the CTL-expression represented by the given string
     * @throws FormatException if the string expression is in the wrong format
     */
    abstract protected TemporalFormula parseOperator(String expr)
        throws FormatException;

    /**
     * Mark all the states that satisfy this CTL-expression.
     * @param marker the marker to which marking is deferred
     * @param marking marking of states so far
     * @param gts the gts providing the states and transitions
     */
    public abstract void mark(CTLFormulaMarker marker, Marking marking, GTS gts);

    /**
     * Returns the operands of this CTL-expression as a list.
     * @return the operands of this CTL-expression as a list
     */
    abstract public List<TemporalFormula> getOperands();

    /**
     * Tests whether a given text may be regarded as an atom, according to the
     * rules of regular expressions. (If not, then it should be single-quoted.)
     * This implementation throws an exception if the text contains any of the
     * operator strings in {@link #operators} as a sub-string. which is the case
     * if the text does not contain any special characters
     * @param text the text to be tested
     * @throws FormatException if the text contains a special character
     */
    protected void assertAtom(String text) throws FormatException {
        String parsedText = ExprParser.parseExpr(text).first();
        for (int c = 0; c < operators.size(); c++) {
            if (parsedText.indexOf(operators.get(c)) >= 0) {
                throw new FormatException("Operator " + operators.get(c)
                    + " in unquoted atom " + text);
            }
        }
    }

    /**
     * Checks whether the given property is build up from valid atomic
     * propositions.
     * @param property the property to check for validity of its atomic
     *        propositions
     * @param atoms the set of atomic propositions allowed
     * @return <tt>null</tt>, if the given <code>property</code> is build up
     *         from valid atoms. If not, it returns the string representation of
     *         the first found invalid atom.
     */
    static public String validAtoms(TemporalFormula property,
            Set<? extends Label> atoms) {
        if (property.getOperands().size() > 0) {
            for (TemporalFormula operand : property.getOperands()) {
                String invalidAtom = validAtoms(operand, atoms);
                if (invalidAtom != null) {
                    return invalidAtom;
                }
            }
        } else {
            assert (property instanceof Atom && property.getOperands().size() == 0) : "An atom should have 0 operands.";
            boolean validAtom = false;
            for (Label nameLabel : atoms) {
                String ruleName = ((RuleName) nameLabel).text();
                if (property.toString().equals(ruleName)
                    || (property.toString().startsWith(ruleName + "(") && property.toString().endsWith(
                        ")"))
                    || property.toString().equals(CTLStarFormula.TRUE)
                    || property.toString().equals(CTLStarFormula.FALSE)) {
                    validAtom |= true;
                }
            }
            if (!validAtom) {
                return property.toString();
            }
        }
        return null;
    }

    /**
     * Links the propositions to the rules (only used in
     * {@link CTLMatchingMarker}).
     * @param verifier the <code>CTLModelChecker</code>-instance which keeps
     *        track of the gratra-rules.
     * @throws GraphPredicateNotFoundException if some graph-predicate used in
     *         the formula is not available
     */
    public void linkPredicates(CTLModelChecker verifier)
        throws GraphPredicateNotFoundException {
        if (this.getClass().equals(Atom.class)) {
            Condition graphCondition =
                verifier.getGraphCondition(((Atom) this).predicateName());
            if (graphCondition == null) {
                throw new GraphPredicateNotFoundException("Graph-predicate '"
                    + ((Atom) this).predicateName() + "' not present.");
            } else {
                ((Atom) this).graphCondition = graphCondition;
            }
        } else {
            List<TemporalFormula> operands = getOperands();
            for (int i = 0; i < operands.size(); i++) {
                TemporalFormula operand = operands.get(i);
                operand.linkPredicates(verifier);
            }
        }
    }

    /**
     * Returns whether this CTL-expression has any counter-examples.
     * @return <tt>true</tt> if the list of counter-examples is non-empty,
     *         <tt>false</tt> otherwise.
     */
    protected boolean hasCounterExamples() {
        return !(this.counterExamples.isEmpty());
    }

    /**
     * Returns the collection of counter-examples.
     * @return the collection of counter-examples.
     */
    public Set<State> getCounterExamples() {
        if (this.counterExamples == null) {
            this.counterExamples = new HashSet<State>();
        }
        return this.counterExamples;
    }
}

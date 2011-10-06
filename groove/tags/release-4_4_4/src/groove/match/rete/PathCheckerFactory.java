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
package groove.match.rete;

import groove.rel.RegExpr;
import groove.rel.RegExpr.Atom;
import groove.rel.RegExpr.Choice;
import groove.rel.RegExpr.Inv;
import groove.rel.RegExpr.Seq;
import groove.rel.RegExpr.Wildcard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

/**
 * Factory class that creates path-checkers for each regular expression
 * making sure checkers for similar expressions are reused. 
 * @author Arash Jalali
 * @version $Revision $
 */
public class PathCheckerFactory {
    private ReteNetwork owner;
    private HashMap<String,AbstractPathChecker> pathCheckers =
        new HashMap<String,AbstractPathChecker>();

    /**
     * Creates a path-checker factory for a given RETE network
     * @param owner The RETE network for which this factory is to be built.
     */
    public PathCheckerFactory(ReteNetwork owner) {
        this.owner = owner;
    }

    /**
     * Factory method that creates or re-uses an already created
     * RETE path checker 
     */
    public AbstractPathChecker getPathCheckerFor(RegExpr exp) {
        AbstractPathChecker result = this.pathCheckers.get(exp.toString());
        if (result == null) {
            result = create(exp);
            this.pathCheckers.put(exp.toString(), result);
        }
        return result;
    }

    private AbstractPathChecker create(RegExpr exp) {
        AbstractPathChecker result = null;
        List<RegExpr> operands = null;
        if (exp.isAtom()) {
            operands = Collections.emptyList();
            result = new AtomPathChecker(this.owner, (Atom) exp);
            this.owner.getRoot().addSuccessor(result);
        } else if (exp.isChoice()) {
            operands = ((Choice) exp).getChoiceOperands();
            result = new ChoicePathChecker(this.owner, (RegExpr.Choice) exp);
        } else if (exp.isInv()) {
            operands = new ArrayList<RegExpr>();
            operands.add(exp.getInvOperand());
            result = new InversionPathChecker(this.owner, (Inv) exp);
        } else if (exp.isNeg()) {
            throw new UnsupportedOperationException(
                "Negation is not supported by this factory.");
        } else if (exp.isPlus() || exp.isStar()) {
            operands = new ArrayList<RegExpr>();
            operands.add(exp.isPlus() ? exp.getPlusOperand()
                    : exp.getStarOperand());
            result = new ClosurePathChecker(this.owner, exp);
        } else if (exp.isSeq()) {
            operands = exp.getSeqOperands();
            if (operands.size() == 2) {
                result = new SequenceOperatorPathChecker(this.owner, exp);
            } else {
                result = buildBinaryTree(exp);
                operands = Collections.emptyList();
            }
        } else if (exp.isEmpty()) {
            operands = Collections.emptyList();
            result = EmptyPathChecker.getInstance(this.owner);
            this.owner.getRoot().addSuccessor(result);
        } else if (exp.isWildcard()) {
            operands = Collections.emptyList();
            result = new WildcardPathChecker(this.owner, (Wildcard) exp);
            this.owner.getRoot().addSuccessor(result);
        }

        if (result != null) {
            for (RegExpr operand : operands) {
                AbstractPathChecker pc = getPathCheckerFor(operand);
                result.addAntecedent(pc);
                pc.addSuccessor(result);
            }
        }
        return result;
    }

    /**
     * Makes a binary tree of path checker nodes nodes that un-flatten
     * a expression of type {@link Seq} or {@link Choice} with two or more 
     * operands because all the descendants of the {@link SequenceOperatorPathChecker}
     * take two operands (antecedents).
     * 
     * @param exp The expression that is either of type {@link Seq} or
     * {@link Choice}.
     * 
     */
    private AbstractPathChecker buildBinaryTree(RegExpr exp) {
        List<RegExpr> operands = exp.getOperands();
        assert (exp.isSeq()) || (exp.isChoice()) && (operands.size() >= 2);
        Stack<AbstractPathChecker> checkers = new Stack<AbstractPathChecker>();
        for (RegExpr op : operands) {
            checkers.push(getPathCheckerFor(op));
        }
        while (checkers.size() > 1) {
            AbstractPathChecker pc2 = checkers.pop();
            AbstractPathChecker pc1 = checkers.pop();
            List<RegExpr> ops = new ArrayList<RegExpr>();
            ops.add(pc1.getExpression());
            ops.add(pc2.getExpression());
            RegExpr e =
                (exp.isSeq()) ? new RegExpr.Seq(ops) : new RegExpr.Choice(ops);
            AbstractPathChecker combined = getPathCheckerFor(e);
            checkers.push(combined);
        }
        return checkers.pop();
    }

    /**
     * @return A map from regular expression strings to path-checkers that
     * check against that expression.
     */
    public HashMap<String,AbstractPathChecker> getPathCheckersMap() {
        return this.pathCheckers;
    }
}

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
 * $Id$
 */
package groove.automaton;

import java.util.List;

/**
 * Visitor interface for regular expressions.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface RegExprCalculator<Result> {
    /**
     * Visitor method called by the accept method of a {@link RegExpr.Neg}.
     * @param expr the expression being visited
     * @param arg the argument for the negation
     * @return the return value of the computation
     */
    public Result computeNeg(RegExpr.Neg expr, Result arg);

    /**
     * Visitor method called by the accept method of a {@link RegExpr.Star}.
     * @param expr the expression being visited
     * @param arg the argument for the negation
     * @return the return value of the computation
     */
    public Result computeStar(RegExpr.Star expr, Result arg);

    /**
     * Visitor method called by the accept method of a {@link RegExpr.Plus}.
     * @param expr the expression being visited
     * @param arg the argument for the negation
     * @return the return value of the computation
     */
    public Result computePlus(RegExpr.Plus expr, Result arg);

    /**
     * Visitor method called by the accept method of a {@link RegExpr.Inv}.
     * @param expr the expression being visited
     * @param arg the argument for the negation
     * @return the return value of the computation
     */
    public Result computeInv(RegExpr.Inv expr, Result arg);

    /**
     * Visitor method called by the accept method of a {@link RegExpr.Seq}.
     * @param expr the expression being visited
     * @param argList the arguments for the computation
     * @return the return value of the computation
     */
    public Result computeSeq(RegExpr.Seq expr, List<Result> argList);

    /**
     * Visitor method called by the accept method of a {@link RegExpr.Choice}.
     * @param expr the expression being visited
     * @param argList the arguments for the computation
     * @return the return value of the computation
     */
    public Result computeChoice(RegExpr.Choice expr, List<Result> argList);

    /**
     * Visitor method called by the accept method of a {@link RegExpr.Atom}.
     * @param expr the expression being visited
     * @return the return value of the computation
     */
    public Result computeAtom(RegExpr.Atom expr);

    /**
     * Visitor method called by the accept method of a {@link RegExpr.Sharp}.
     * @param expr the expression being visited
     * @return the return value of the computation
     */
    public Result computeSharp(RegExpr.Sharp expr);

    /**
     * Visitor method called by the accept method of a {@link RegExpr.Wildcard}.
     * @param expr the expression being visited
     * @return the return value of the computation
     */
    public Result computeWildcard(RegExpr.Wildcard expr);

    /**
     * Visitor method called by the accept method of a {@link RegExpr.Empty}.
     * @param expr the expression being visited
     * @return the return value of the computation
     */
    public Result computeEmpty(RegExpr.Empty expr);
}

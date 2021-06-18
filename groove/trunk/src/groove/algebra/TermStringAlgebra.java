/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.algebra;

import groove.algebra.syntax.Expression;

/**
 * Term algebra of strings.
 * @author Arend Rensink
 * @version $Revision $
 */
public class TermStringAlgebra extends StringAlgebra<Expression,Expression,Expression,Expression> {
    /** Private constructor for the singleton instance. */
    private TermStringAlgebra() {
        // empty
    }

    @Override
    public Expression concat(Expression arg0, Expression arg1) {
        return Op.CONCAT.getOperator()
            .newTerm(arg0, arg1);
    }

    @Override
    public Expression isBool(Expression arg0) {
        return Op.IS_BOOL.getOperator()
            .newTerm(arg0);
    }

    @Override
    public Expression isInt(Expression arg0) {
        return Op.IS_INT.getOperator()
            .newTerm(arg0);
    }

    @Override
    public Expression isReal(Expression arg0) {
        return Op.IS_REAL.getOperator()
            .newTerm(arg0);
    }

    @Override
    public Expression toBool(Expression arg0) {
        return Op.TO_BOOL.getOperator()
            .newTerm(arg0);
    }

    @Override
    public Expression toInt(Expression arg0) {
        return Op.TO_INT.getOperator()
            .newTerm(arg0);
    }

    @Override
    public Expression toReal(Expression arg0) {
        return Op.TO_REAL.getOperator()
            .newTerm(arg0);
    }

    @Override
    public Expression eq(Expression arg0, Expression arg1) {
        return Op.EQ.getOperator()
            .newTerm(arg0, arg1);
    }

    @Override
    public Expression ge(Expression arg0, Expression arg1) {
        return Op.GE.getOperator()
            .newTerm(arg0, arg1);
    }

    @Override
    public Expression gt(Expression arg0, Expression arg1) {
        return Op.GT.getOperator()
            .newTerm(arg0, arg1);
    }

    @Override
    public Expression ite(Expression arg0, Expression arg1, Expression arg2) {
        return Op.ITE.getOperator()
            .newTerm(arg0);
    }

    @Override
    public Expression le(Expression arg0, Expression arg1) {
        return Op.LE.getOperator()
            .newTerm(arg0, arg1);
    }

    @Override
    public Expression lt(Expression arg0, Expression arg1) {
        return Op.LT.getOperator()
            .newTerm(arg0, arg1);
    }

    @Override
    public Expression neq(Expression arg0, Expression arg1) {
        return Op.NEQ.getOperator()
            .newTerm(arg0, arg1);
    }

    @Override
    public Expression length(Expression arg) {
        return Op.LENGTH.getOperator()
            .newTerm(arg);
    }

    @Override
    public Expression substring(Expression arg0, Expression arg1, Expression arg2) {
        return Op.SUBSTRING.getOperator()
            .newTerm(arg0, arg1, arg2);
    }

    @Override
    public Expression suffix(Expression arg0, Expression arg1) {
        return Op.SUFFIX.getOperator()
            .newTerm(arg0, arg1);
    }

    @Override
    public Expression lookup(Expression arg0, Expression arg1) {
        return Op.LOOKUP.getOperator()
            .newTerm(arg0, arg1);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public AlgebraFamily getFamily() {
        return AlgebraFamily.TERM;
    }

    @Override
    public boolean isValue(Object value) {
        return value instanceof Expression && ((Expression) value).getSort() == getSort();
    }

    @Override
    public String getSymbol(Object value) {
        return ((Expression) value).toDisplayString();
    }

    @Override
    public Expression toTerm(Object value) {
        return (Expression) value;
    }

    @Override
    public Expression toValueFromConstant(Constant constant) {
        return constant;
    }

    @Override
    public String toJavaValue(Object value) {
        return (String) AlgebraFamily.DEFAULT.toValue((Expression) value);
    }

    @Override
    protected Constant toValueFromJavaString(String value) {
        return Constant.instance(value);
    }

    /** Name of this algebra. */
    public static final String NAME = "tstring";
    /** Singleton instance of this algebra. */
    public static final TermStringAlgebra instance = new TermStringAlgebra();
}

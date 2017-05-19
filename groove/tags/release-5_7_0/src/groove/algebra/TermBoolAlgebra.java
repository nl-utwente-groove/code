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

import java.util.List;

import groove.algebra.syntax.Expression;

/**
 * Implementation of booleans consisting of a singleton value.
 * @author Arend Rensink
 * @version $Revision $
 */
public class TermBoolAlgebra extends BoolAlgebra<Expression> {
    /** Private constructor for the singleton instance. */
    private TermBoolAlgebra() {
        // empty
    }

    @Override
    public Expression and(Expression arg0, Expression arg1) {
        return Op.AND.getOperator()
            .newTerm(arg0, arg1);
    }

    @Override
    public Expression bigand(List<Expression> arg) {
        return Op.BIGAND.getOperator()
            .newTerm(arg.toArray(new Expression[arg.size()]));
    }

    @Override
    public Expression bigor(List<Expression> arg) {
        return Op.BIGOR.getOperator()
            .newTerm(arg.toArray(new Expression[arg.size()]));
    }

    @Override
    public Expression eq(Expression arg0, Expression arg1) {
        return Op.EQ.getOperator()
            .newTerm(arg0, arg1);
    }

    @Override
    public Expression neq(Expression arg0, Expression arg1) {
        return Op.NEQ.getOperator()
            .newTerm(arg0, arg1);
    }

    @Override
    public Expression not(Expression arg) {
        return Op.NOT.getOperator()
            .newTerm(arg);
    }

    @Override
    public Expression or(Expression arg0, Expression arg1) {
        return Op.OR.getOperator()
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
    public Boolean toJavaValue(Object value) {
        return (Boolean) AlgebraFamily.DEFAULT.toValue((Expression) value);
    }

    @Override
    protected Constant toValueFromJavaBoolean(Boolean value) {
        return Constant.instance(value);
    }

    /** Name of this algebra. */
    public static final String NAME = "tbool";
    /** Singleton instance of this algebra. */
    public static final TermBoolAlgebra instance = new TermBoolAlgebra();
}

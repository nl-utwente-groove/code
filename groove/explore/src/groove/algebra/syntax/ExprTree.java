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
package groove.algebra.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.jdt.annotation.NonNull;

import groove.algebra.Constant;
import groove.algebra.IntSignature;
import groove.algebra.Operator;
import groove.algebra.RealSignature;
import groove.algebra.Signature.OpValue;
import groove.algebra.Sort;
import groove.grammar.QualName;
import groove.util.parse.AExprTree;
import groove.util.parse.DefaultOp;
import groove.util.parse.FormatException;
import groove.util.parse.OpKind;

/**
 * Expression tree, with functionality to convert to an {@link Expression} or {@link Assignment}.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ExprTree extends AExprTree<ExprTree.ExprOp,ExprTree> {
    /**
     * Constructs a new expression with a given top-level operator.
     */
    public ExprTree(ExprOp op) {
        super(op);
        assert op.getKind() != OpKind.NONE;
    }

    /** Sets an explicit (non-{@code null}) sort declaration for this expression. */
    public void setSort(Sort sort) {
        assert !isFixed();
        assert sort != null;
        this.sort = sort;
        if (hasConstant() && sort != getConstant().getSort()) {
            getErrors().add("Invalid sorted expression '%s:%s'",
                sort.getName(),
                getConstant().getSymbol());
        }
    }

    /** Indicates if this expression contains an explicit sort declaration. */
    public boolean hasSort() {
        return getSort() != null;
    }

    /** Returns the sort declaration wrapped in this expression, if any. */
    public Sort getSort() {
        return this.sort;
    }

    private Sort sort;

    /**
     * Converts this parse tree into an {@link Assignment}.
     */
    public Assignment toAssignment() throws FormatException {
        assert isFixed();
        getErrors().throwException();
        if (getOp() != ASSIGN) {
            throw new FormatException("'%s' is not an assignment", getParseString());
        }
        String lhs = getArg(0).getId()
            .toString();
        Expression rhs = getArg(1).toExpression();
        Assignment result = new Assignment(lhs, rhs);
        result.setParseString(getParseString());
        return result;
    }

    /**
     * Returns the term object corresponding to this tree.
     * All free variables in the tree must be type-derivable.
     */
    public Expression toExpression() throws FormatException {
        assert isFixed();
        return toExpression(Typing.emptyTyping());
    }

    /**
     * Returns the unique expression object corresponding to this tree.
     * @param varMap mapping from known variables to types. Only variables in this map are
     * allowed to occur in the term.
     */
    public Expression toExpression(@NonNull Typing varMap) throws FormatException {
        assert isFixed();
        getErrors().throwException();
        Map<Sort,? extends Expression> choice = toExpressions(varMap);
        if (choice.size() > 1) {
            throw new FormatException("Can't derive type of '%s': add type prefix",
                getParseString());
        }
        Expression result = choice.values()
            .iterator()
            .next();
        result.setParseString(getParseString());
        return result;
    }

    /**
     * Returns the multi-expression derived from this tree.
     * @param varMap mapping from known variables to types. Only variables in this map are
     * allowed to occur in the term.
     */
    private MultiExpression toExpressions(@NonNull Typing varMap) throws FormatException {
        MultiExpression result;
        if (hasConstant()) {
            Constant constant = toConstant();
            result = new MultiExpression(constant.getSort(), constant);
        } else if (getOp().getKind() == OpKind.ATOM) {
            result = toAtomExprs(varMap);
        } else {
            result = toCallExprs(varMap);
        }
        return result;
    }

    /**
     * Returns the constant expression this tree represents, if any.
     * @return the constant expression this tree represents
     * @throws FormatException if this tree does not represent a constant
     */
    public Constant toConstant() throws FormatException {
        assert isFixed();
        getErrors().throwException();
        if (!hasConstant()) {
            throw new FormatException("'%s' does not represent a constant", getParseString());
        }
        return getConstant();
    }

    /**
     * Converts this tree to a multi-sorted {@link Variable} or a {@link FieldExpr}.
     * Chained field expressions are currently unsupported.
     * @param varMap variable typing
     */
    private MultiExpression toAtomExprs(@NonNull Typing varMap) throws FormatException {
        assert getOp().getKind() == OpKind.ATOM;
        MultiExpression result = new MultiExpression();
        if (hasSort()) {
            Sort sort = getSort();
            assert sort != null;
            result.put(sort, toAtomExpr(varMap, sort));
        } else {
            for (Sort sort : Sort.values()) {
                result.put(sort, toAtomExpr(varMap, sort));
            }
        }
        return result;
    }

    /**
     * Converts this tree to a {@link Variable} or a {@link FieldExpr}.
     * Chained field expressions are currently unsupported.
     * @param varMap variable typing
     * @param sort expected type of the expression
     */
    private Expression toAtomExpr(@NonNull Typing varMap, @NonNull Sort sort)
        throws FormatException {
        Expression result;
        assert hasId();
        QualName id = getId();
        if (id.size() > 2) {
            throw new FormatException("Nested field expression '%s' not supported", id);
        } else if (id.size() > 1) {
            result = new FieldExpr(hasSort(), id.get(0), id.get(1), sort);
        } else {
            String name = id.get(0);
            Optional<Sort> varSort = varMap.getSort(name);
            if (!varSort.isPresent()) {
                // this is a self-field
                result = new FieldExpr(hasSort(), null, name, sort);
            } else if (varSort.get() != sort) {
                throw new FormatException("Variable %s is of type %s, not %s", name, varSort.get()
                    .getName(), sort.getName());
            } else {
                result = toVarExpr(name, sort);
            }
        }
        return result;
    }

    /**
     * Converts this tree to a {@link Variable} or a {@link Parameter}.
     * @param name variable name
     * @param sort expected type of the expression
     */
    private Expression toVarExpr(@NonNull String name, @NonNull Sort sort) throws FormatException {
        Expression result;
        if (name.charAt(0) == '$') {
            int number;
            try {
                number = Integer.parseInt(name.substring(1));
            } catch (NumberFormatException exc) {
                throw new FormatException(
                    "Parameter '%s' should equal '$number' for a non-negative number",
                    getParseString());
            }
            result = new Parameter(hasSort(), number, sort);
        } else {
            result = new Variable(hasSort(), name, sort);
        }
        return result;
    }

    /**
     * Returns the set of derivable expressions in case the top level is
     * a non-atomic operator.
     */
    private MultiExpression toCallExprs(@NonNull Typing varMap) throws FormatException {
        MultiExpression result = new MultiExpression();
        List<MultiExpression> resultArgs = new ArrayList<>();
        // all children are arguments
        for (ExprTree arg : getArgs()) {
            resultArgs.add(arg.toExpressions(varMap));
        }
        for (Operator op : getOp().getOperators()) {
            if (hasSort() && getSort() != op.getSort()) {
                // the type of op does not correspond to the known operator type
                continue;
            }
            boolean duplicate = false;
            try {
                duplicate = (result.put(op.getResultType(), newCallExpr(op, resultArgs)) != null);
            } catch (FormatException e) {
                // this candidate did not work out; proceed
            }
            if (duplicate) {
                throw new FormatException("Typing of '%s' is ambiguous: add type prefixes",
                    getParseString());
            }
        }
        if (result.isEmpty()) {
            throw new FormatException("Operator '%s' not applicable to arguments in '%s'",
                getOp().getSymbol(), getParseString());
        }
        return result;
    }

    /**
     * Factory method for a new operator expression.
     * @param op the operator of the new expression
     * @param args operator arguments. Each argument is a map from
     * possible types to corresponding expressions
     * @throws FormatException if {@code args} does not have values
     * for the required operator types
     */
    private Expression newCallExpr(Operator op, List<MultiExpression> args) throws FormatException {
        if (op.getArity() != args.size()) {
            throw new FormatException("Operator '%s' expects %s parameters but has %s",
                op.toString(), op.getArity(), args.size());
        }
        List<Sort> parTypes = op.getParamTypes();
        List<Expression> selectedArgs = new ArrayList<>();
        for (int i = 0; i < args.size(); i++) {
            Expression arg = args.get(i)
                .get(parTypes.get(i));
            if (arg == null) {
                throw new FormatException("Parameter %s of '%s' should have type %s", i,
                    getParseString(), parTypes.get(i));
            }
            selectedArgs.add(arg);
        }
        // we distinguish negated constants, to make sure that
        // int:-1 parses to the same expression as -1
        OpValue opValue = op.getOpValue();
        if ((opValue == IntSignature.Op.NEG || opValue == RealSignature.Op.NEG)
            && selectedArgs.get(0) instanceof Constant) {
            return op.getResultType()
                .createConstant(op.getSymbol() + selectedArgs.get(0)
                    .toDisplayString());
        } else {
            return new CallExpr(hasSort(), op, selectedArgs);
        }
    }

    @Override
    public ExprTree createTree(ExprOp op) {
        return new ExprTree(op);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.sort == null) ? 0 : this.sort.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        ExprTree other = (ExprTree) obj;
        assert other != null; // guaranteed by !super.equals
        if (this.sort == null) {
            if (other.sort != null) {
                return false;
            }
        } else if (!this.sort.equals(other.sort)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String result = super.toString();
        if (hasSort()) {
            result = getSort().getName() + ":" + result;
        }
        return result;
    }

    /** Auxiliary operator to represent assignment. */
    public static final ExprOp ASSIGN = new ExprOp(OpKind.ASSIGN, "=", 2);

    private static class MultiExpression extends EnumMap<Sort,Expression> {
        /** Creates an empty instance. */
        public MultiExpression() {
            super(Sort.class);
        }

        /** Creates a singleton instance. */
        public MultiExpression(Sort sort, Expression expr) {
            this();
            put(sort, expr);
        }
    }

    /**
     * Operator class collecting data operators with the same symbol.
     * @author Arend Rensink
     * @version $Revision $
     */
    static class ExprOp extends DefaultOp {
        /**
         * Constructs the unique atomic operator, with empty symbol.
         */
        private ExprOp() {
            super();
        }

        /**
         * Constructs an operator with a given kind, symbol and arity.
         * The arity should equal the kind's arity, unless the latter is unspecified.
         */
        public ExprOp(OpKind kind, String symbol, int arity) {
            super(kind, symbol, arity);
        }

        /** Adds an algebra operator to the operators wrapped in this object. */
        public void add(Operator sortOp) {
            Operator old = this.sortOps.put(sortOp.getSort(), sortOp);
            assert old == null;
        }

        /** Returns the algebra operator of a given sort wrapped into this object,
         * if any.
         * @param sort the non-{@code null} sort for which the operator is requested
         */
        public Operator getOperator(Sort sort) {
            assert sort != null;
            return this.sortOps.get(sort);
        }

        /** Returns the collection of algebra operators wrapped in this object. */
        public Collection<Operator> getOperators() {
            return this.sortOps.values();
        }

        private Map<Sort,Operator> sortOps = new EnumMap<>(Sort.class);

        @Override
        public String toString() {
            return "ExprOp[" + this.sortOps + "]";
        }

        /** Returns the unique atom operator. */
        public static ExprOp atom() {
            return ATOM;
        }

        private static ExprOp ATOM = new ExprOp();
    }
}
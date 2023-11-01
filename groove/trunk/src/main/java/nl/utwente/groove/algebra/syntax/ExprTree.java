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
package nl.utwente.groove.algebra.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Constant;
import nl.utwente.groove.algebra.IntSignature;
import nl.utwente.groove.algebra.Operator;
import nl.utwente.groove.algebra.RealSignature;
import nl.utwente.groove.algebra.Signature.OpValue;
import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.Keywords;
import nl.utwente.groove.util.parse.AExprTree;
import nl.utwente.groove.util.parse.DefaultOp;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.util.parse.OpKind;

/**
 * Expression tree, with functionality to convert to an {@link Expression} or {@link Assignment}.
 * @author Arend Rensink
 * @version $Revision $
 */
@NonNullByDefault
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
            getErrors()
                .add("Invalid sorted expression '%s:%s'", sort.getName(),
                     getConstant().getSymbol());
        }
    }

    /** Indicates if this expression contains an explicit sort declaration. */
    public boolean hasSort() {
        return getSort() != null;
    }

    /** Returns the sort declaration wrapped in this expression, if any. */
    public @Nullable Sort getSort() {
        return this.sort;
    }

    private @Nullable Sort sort;

    /**
     * Converts this parse tree into an {@link Assignment}.
     */
    public Assignment toAssignment() throws FormatException {
        return toAssignment(SortMap.newInstance());
    }

    /**
     * Converts this parse tree into an {@link Assignment}.
     * @param sortMap mapping from known variables to types. Only variables in this map are
     * allowed to occur in the term.
     */
    public Assignment toAssignment(SortMap sortMap) throws FormatException {
        assert isFixed();
        getErrors().throwException();
        if (getOp() != ASSIGN) {
            throw new FormatException("'%s' is not an assignment", getParseString());
        }
        var lhs = getArg(0).getId();
        var sort = sortMap.getSort(lhs.nest(Keywords.SELF));
        Expression rhs = sort.isEmpty()
            ? getArg(1).toExpression(sortMap)
            : getArg(1).toExpression(sort.get(), sortMap);
        Assignment result = new Assignment(lhs.toString(), rhs);
        result.setParseString(getParseString());
        return result;
    }

    /**
     * Returns the expression object corresponding to this tree.
     * All free variables in the tree must be type-derivable.
     */
    public Expression toExpression() throws FormatException {
        return toExpression(SortMap.newInstance());
    }

    /**
     * Returns the expression object corresponding to this tree, provided
     * it can be typed as a given sort.
     * All free variables in the tree must be type-derivable.
     * @param sort the required sort
     */
    public Expression toExpression(Sort sort) throws FormatException {
        return toExpression(sort, SortMap.newInstance());
    }

    /**
     * Returns the unique expression object corresponding to this tree,
     * based on a given set of typed variables
     * @param sortMap mapping from known variables to types. Variables not occurring in this
     * map are assumed to be self-fields
     */
    public Expression toExpression(SortMap sortMap) throws FormatException {
        assert isFixed();
        getErrors().throwException();
        var choice = toExpressions(sortMap);
        if (choice.size() > 1) {
            throw new FormatException(
                "Expression '%s' does not have a unique type; add type prefix", getParseString());
        }
        Expression result = choice.value();
        assert result != null;
        result.setParseString(getParseString());
        return result;
    }

    /**
     * Returns the unique expression object corresponding to this tree, provided it can be
     * typed as a given sort.
     * @param sort the required sort
     * @param sortMap mapping from known variables to types. Variables not occurring in this
     * map are assumed to be self-fields
     */
    public Expression toExpression(Sort sort, SortMap sortMap) throws FormatException {
        assert isFixed();
        getErrors().throwException();
        var choice = toExpressions(sortMap);
        Expression result = choice.get(sort);
        if (result == null) {
            throw new FormatException("Expression '%s' is not of required type %s",
                getParseString(), sort);
        }
        result.setParseString(getParseString());
        return result;
    }

    /**
     * Returns the multi-expression derived from this tree.
     * @param sortMap mapping from known variables to types. Only variables in this map are
     * allowed to occur in the term.
     */
    private MultiExpression toExpressions(SortMap sortMap) throws FormatException {
        MultiExpression result;
        if (hasConstant()) {
            Constant constant = toConstant();
            result = new MultiExpression(constant.getSort(), constant);
        } else if (getOp().getKind() == OpKind.ATOM) {
            result = toAtomExprs(sortMap);
        } else {
            result = toCallExprs(sortMap);
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
     * @param sortMap variable typing
     */
    private MultiExpression toAtomExprs(SortMap sortMap) throws FormatException {
        assert getOp().getKind() == OpKind.ATOM;
        MultiExpression result = new MultiExpression();
        // flag determining if this is a variable
        // if not, it must be a (possibly qualified) field
        boolean isVar = getId().size() == 1 && sortMap.contains(getId());
        if (hasSort()) {
            // the expression has a sort prefix
            Sort sort = getSort();
            assert sort != null;
            if (isSorted(sortMap) && getSort(sortMap) != sort) {
                throw new FormatException(
                    "Declared sort '%s' of '%s' distinct from derived sort '%s'", sort, getId(),
                    getSort(sortMap));
            }
            result.put(sort, toAtomExpr(sort, isVar));
        } else if (isSorted(sortMap)) {
            // the identifier has a derived sort
            Sort sort = getSort(sortMap);
            assert sort != null;
            result.put(sort, toAtomExpr(sort, isVar));
        } else {
            // we have to guess; try out multiple sorts
            for (Sort sort : Sort.values()) {
                result.put(sort, toAtomExpr(sort, isVar));
            }
        }
        return result;
    }

    /**
     * Converts this tree to a {@link Variable} or a {@link FieldExpr}.
     * Chained field expressions are currently unsupported.
     * @param sort expected type of the expression
     * @param isVar flag indicating that this is a variable, not a field name
     */
    private Expression toAtomExpr(Sort sort, boolean isVar) throws FormatException {
        Expression result;
        assert hasId();
        QualName id = getId();
        if (id.size() > 2) {
            throw new FormatException("Nested field expression '%s' not supported", id);
        } else if (id.size() > 1) {
            result = new FieldExpr(hasSort(), id.get(0), id.get(1), sort);
        } else if (isVar) {
            result = toVarExpr(id.get(0), sort);
        } else {
            // this is a self-field
            result = new FieldExpr(hasSort(), null, id.get(0), sort);
        }
        return result;
    }

    /** Checks if this expression is a qualified name present in the given sort map. */
    private boolean isSorted(SortMap sortMap) {
        var result = false;
        if (getOp().getKind() == OpKind.ATOM) {
            result = sortMap.contains(getId());
            if (!result && getId().size() == 1) {
                result = sortMap.contains(getId().nest(Keywords.SELF));
            }
        }
        return result;
    }

    /** Returns the declared type of this expression, if it is a qualified name in
     * the given sort map.
     */
    private @Nullable Sort getSort(SortMap sortMap) {
        var result = sortMap.getSort(getId());
        if (!result.isPresent() && getId().size() == 1) {
            result = sortMap.getSort(getId().nest(Keywords.SELF));
        }
        return Groove.orElse(result, null);
    }

    /**
     * Converts this tree to a {@link Variable}.
     * @param name variable name
     * @param sort expected type of the expression
     */
    private Expression toVarExpr(String name, Sort sort) {
        return new Variable(hasSort(), name, sort);
    }

    /**
     * Returns the set of derivable expressions in case the top level is
     * a non-atomic operator.
     */
    private MultiExpression toCallExprs(SortMap sortMap) throws FormatException {
        MultiExpression result = new MultiExpression();
        List<MultiExpression> resultArgs = new ArrayList<>();
        // all children are arguments
        for (ExprTree arg : getArgs()) {
            resultArgs.add(arg.toExpressions(sortMap));
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
            Expression arg = args.get(i).get(parTypes.get(i));
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
            return op
                .getResultType()
                .createConstant(op.getSymbol() + selectedArgs.get(0).toDisplayString());
        } else {
            return new CallExpr(hasSort(), op, selectedArgs);
        }
    }

    @Override
    public ExprTree createTree(ExprOp op) {
        return new ExprTree(op);
    }

    /**
     * Returns an expression tree obtained from this one by changing all
     * occurrences of a certain label into another.
     * In particular, this concerns field names.
     * @param oldLabel the label to be changed
     * @param newLabel the new value for {@code oldLabel}
     * @param sortMap map from variables and qualified fields to sorts
     * @return a clone of this object with changed labels, or this object
     *         if {@code oldLabel} did not occur
     */
    public ExprTree relabel(TypeLabel oldLabel, TypeLabel newLabel, SortMap sortMap) {
        ExprTree result = this;
        QualName id = getId();
        if (getOp().getKind() == OpKind.ATOM && oldLabel.hasRole(EdgeRole.BINARY) && id != null) {
            var tokens = id.tokens();
            boolean changed = false;
            int i = tokens.size() - 1;
            // change if it is a self-field or qualified field
            if (!(id.size() == 1 && sortMap.contains(id))
                && tokens.get(i).equals(oldLabel.text())) {
                tokens = new ArrayList<>(tokens);
                tokens.set(i, newLabel.text());
                changed = true;
            }
            if (changed) {
                result = new ExprTree(ExprOp.atom());
                result.setId(new QualName(tokens));
                getErrors().forEach(result::addError);
                result.setFixed();
            }
        } else {
            boolean changed = false;
            var newArgs = new ArrayList<ExprTree>();
            for (var arg : getArgs()) {
                var newArg = arg.relabel(oldLabel, newLabel, sortMap);
                changed |= arg != newArg;
                newArgs.add(newArg);
            }
            if (changed) {
                result = new ExprTree(getOp());
                newArgs.forEach(result::addArg);
                getErrors().forEach(result::addError);
                result.setFixed();
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hashCode(this.sort);
        return result;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        ExprTree other = (ExprTree) obj;
        assert other != null; // guaranteed by !super.equals
        if (!Objects.equals(this.sort, other.sort)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String result = super.toString();
        var sort = getSort();
        if (sort != null) {
            result = sort + ":" + result;
        }
        return result;
    }

    /** Auxiliary operator to represent assignment. */
    public static final ExprOp ASSIGN = new ExprOp(OpKind.ASSIGN, "=", 2);

    /** Map from sorts to expressions if they may have that sort. */
    private static class MultiExpression extends EnumMap<Sort,@Nullable Expression> {
        /** Creates an empty instance. */
        MultiExpression() {
            super(Sort.class);
        }

        /** Creates a singleton instance. */
        MultiExpression(Sort sort, Expression expr) {
            this();
            put(sort, expr);
        }

        /** Returns the first expression value in this map, if any. */
        @Nullable
        Expression value() {
            return values().stream().findFirst().orElse(null);
        }
    }

    /**
     * Operator class collecting data operators with the same symbol.
     * @author Arend Rensink
     * @version $Revision $
     */
    static public class ExprOp extends DefaultOp {
        /**
         * Constructs the unique atomic operator, with empty symbol.
         */
        private ExprOp() {
        }

        /**
         * Constructs an operator with a given kind, symbol and arity.
         * The arity should equal the kind's arity, unless the latter is unspecified.
         */
        public ExprOp(OpKind kind, String symbol, int arity) {
            super(kind, symbol, arity);
        }

        /** Adds an algebra operator to the operators wrapped in this object. */
        @SuppressWarnings("null")
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
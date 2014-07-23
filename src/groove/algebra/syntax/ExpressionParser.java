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

import groove.algebra.Operator;
import groove.algebra.Signature.OpValue;
import groove.algebra.Sort;
import groove.util.parse.DefaultOp;
import groove.util.parse.Expr;
import groove.util.parse.FormatError;
import groove.util.parse.FormatException;
import groove.util.parse.Id;
import groove.util.parse.OpKind;
import groove.util.parse.Parser;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class ExpressionParser implements Parser<Expression> {
    /**
     * Creates an expression parser.
     * @param test if {@code true}, the expression to be parsed may have
     * a top-level assignment operator that should be parsed as an equality operator.
     */
    public ExpressionParser(boolean test) {
        this.test = test;
        this.parser = new MyParser();
    }

    /** Indicates if this parser recognises the legacy test semantics. */
    public boolean isTest() {
        return this.test;
    }

    private final boolean test;

    /** Returns the internal parser to generic expression trees. */
    public MyParser getParser() {
        return this.parser;
    }

    /** Parser to expression trees. */
    private final MyParser parser;

    @Override
    public String getDescription() {
        return "Algebraic expression";
    }

    @Override
    public boolean accepts(String text) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Expression parse(String text) throws FormatException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toParsableString(Object value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<? extends Expression> getValueType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isValue(Object value) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean hasDefault() {
        return false;
    }

    @Override
    public boolean isDefault(Object value) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Expression getDefaultValue() throws UnsupportedOperationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDefaultString() throws UnsupportedOperationException {
        // TODO Auto-generated method stub
        return null;
    }

    /** Returns the collection of operators to be recognised by the parser. */
    public List<MyOp> getOpList() {
        if (this.opList == null) {
            List<MyOp> result = this.opList = new ArrayList<MyOp>();
            result.addAll(getOpMap().values());
            result.add(new MyOp(OpKind.ATOM, null));
        }
        return this.opList;
    }

    private List<MyOp> opList;

    /** Returns the collection of operators to be recognised by the parser. */
    public Map<String,MyOp> getOpMap() {
        if (this.opMap == null) {
            Map<String,MyOp> result = this.opMap = new TreeMap<String,MyOp>();
            // register all operators
            for (Sort sort : Sort.values()) {
                for (OpValue opValue : sort.getOpValues()) {
                    Operator sortOp = opValue.getOperator();
                    // register the operator by name, as a call operator
                    String opName = sortOp.getName();
                    MyOp op = result.get(opName);
                    if (op == null) {
                        result.put(opName, op = new MyOp(opName, sortOp.getArity()));
                    }
                    op.add(sortOp);
                    // register the operator by symbol, if it is not only usable as call operator
                    OpKind opKind = sortOp.getKind();
                    if (opKind == OpKind.CALL) {
                        continue;
                    }
                    String opSymbol = sortOp.getSymbol();
                    op = result.get(opSymbol);
                    if (op == null) {
                        result.put(opSymbol, op = new MyOp(sortOp.getKind(), opSymbol));
                    }
                    op.add(sortOp);
                }
            }
        }
        return this.opMap;
    }

    private Map<String,MyOp> opMap;

    /**
     * Operator class collecting data operators with the same symbol.
     * @author Arend Rensink
     * @version $Revision $
     */
    private class MyOp extends DefaultOp {
        /**
         * Constructs an operator with a given kind and symbol.
         */
        public MyOp(OpKind kind, String symbol) {
            super(kind, symbol);
        }

        /**
         * Constructs a call-type operator with a given symbol and arity.
         */
        public MyOp(String symbol, int arity) {
            super(symbol, arity);
        }

        /** Adds an algebra operator to the operators wrapped in this object. */
        public void add(Operator sortOp) {
            assert sortOp.getSymbol().equals(getSymbol());
            Operator old = this.sortOps.put(sortOp.getSort(), sortOp);
            assert old == null;
        }

        private Map<Sort,Operator> sortOps = new EnumMap<Sort,Operator>(Sort.class);
    }

    private class MyExpr extends Expr<MyOp> {
        /**
         * Constructs a new expression with a given top-level operator.
         */
        public MyExpr(MyOp op) {
            super(op);
        }

        /** Sets an explicit (non-{@code null}) sort declaration for this expression. */
        public void setSort(Sort sort) {
            assert !isFixed();
            assert sort != null;
            assert !hasConstant();
            this.sort = sort;
            if (hasConstant() && sort != getConstant().getSort()) {
                addError(new FormatError("Invalid sorted expression '%s:%s'", sort.getName(),
                    getConstant().getSymbol()));
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
            MyExpr other = (MyExpr) obj;
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
                result += getSort().getName() + ":";
            }
            return result;
        }
    }

    /**
     * Parser specialisation that also parses optional sort prefixes,
     * and returns {@link MyExpr} objects.
     * @author Arend Rensink
     * @version $Revision $
     */
    private class MyParser extends groove.util.parse.ExprParser<MyOp,MyExpr> {
        /** Constructs a parser
         */
        public MyParser() {
            super(getOpList());
        }

        @Override
        protected MyExpr parse() {
            MyExpr result = null;
            try {
                if (isTest() && has(TokenClaz.NAME)) {
                    result = parseTest();
                }
            } catch (FormatException exc) {
                result = createErrorExpr(exc);
            }
            if (result == null) {
                result = super.parse();
            }
            return result;
        }

        /**
         * Attempts to parse the input as a legacy test expression,
         * with a top-level operator '='
         */
        protected MyExpr parseTest() throws FormatException {
            MyExpr result = null;
            Token nameToken = consume(TokenClaz.NAME);
            if (nameToken != null) {
                if (has(TokenClaz.TEST)) {
                    Expr<MyOp> lhs = createAtomExpr(new Id(nameToken.substring()));
                    result = createOpExpr(getOpMap().get("=="));
                    result.addArg(lhs);
                    result.addArg(super.parse());
                } else {
                    // this was not a legacy test expression after all
                    rollBack();
                }
            }
            return result;
        }

        @Override
        protected MyExpr parse(OpKind context) throws FormatException {
            Sort sort = null;
            if (has(TokenClaz.SORT)) {
                Token sortToken = consume(TokenClaz.SORT);
                if (consume(TokenClaz.SORT_SEP) == null) {
                    // this wasn't meant to be a sort prefix after all
                    rollBack();
                } else {
                    sort = sortToken.type(TokenClaz.SORT).sort();
                }
            }
            MyExpr result = super.parse(context);
            if (sort != null) {
                result.setSort(sort);
            }
            return result;
        }
    }
}

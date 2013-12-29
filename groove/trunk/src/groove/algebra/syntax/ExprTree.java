package groove.algebra.syntax;

import groove.algebra.Constant;
import groove.algebra.IntSignature;
import groove.algebra.Operator;
import groove.algebra.RealSignature;
import groove.algebra.Signature.OpValue;
import groove.algebra.SignatureKind;
import groove.grammar.model.FormatException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

/**
 * Dedicated tree node for term parsing.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ExprTree extends CommonTree {
    /** Constructor to duplicate a given tree node. */
    private ExprTree(ExprTree node, CommonTokenStream tokenStream) {
        super(node);
        this.tokenStream = tokenStream;
    }

    /** Constructor for the factory method. */
    public ExprTree(Token t, CommonTokenStream tokenStream) {
        super(t);
        this.tokenStream = tokenStream;
    }

    /** Empty constructor for subclassing. */
    protected ExprTree(CommonTokenStream tokenStream) {
        this.tokenStream = tokenStream;
    }

    @Override
    public Tree dupNode() {
        return new ExprTree(this, this.tokenStream);
    }

    /** Overridden to specialise the type. */
    @Override
    public ExprTree getChild(int i) {
        return (ExprTree) super.getChild(i);
    }

    /**
     * Returns the term object corresponding to this tree.
     * The term cannot contain any variables.
     */
    public Expression toExpression() throws FormatException {
        return toExpression(Collections.<String,SignatureKind>emptyMap());
    }

    /**
     * Returns the unique expression object corresponding to this tree.
     * @param varMap mapping from known variables to types. Only variables in this map are
     * allowed to occur in the term.
     */
    public Expression toExpression(Map<String,SignatureKind> varMap)
        throws FormatException {
        Map<SignatureKind,? extends Expression> choice = toExpressions(varMap);
        if (choice.size() > 1) {
            throw new FormatException(
                "Can't derive type of '%s': add type prefix", toInputString());
        }
        Expression result = choice.values().iterator().next();
        result.setInputString(toInputString());
        return result;
    }

    /**
     * Returns the set of expression objects corresponding to this tree.
     * @param varMap mapping from known variables to types. Only variables in this map are
     * allowed to occur in the term.
     */
    private Map<SignatureKind,? extends Expression> toExpressions(
            Map<String,SignatureKind> varMap) throws FormatException {
        Map<SignatureKind,? extends Expression> result;
        switch (getToken().getType()) {
        case ExprParser.CONST:
            Constant constant = toConstant();
            result =
                Collections.singletonMap(constant.getSignature(), constant);
            break;
        case ExprParser.PAR:
            result = toParameters();
            break;
        case ExprParser.FIELD:
            result = toFieldOrVarExprs(varMap);
            break;
        case ExprParser.CALL:
            result = toCallExprs(varMap);
            break;
        case ExprParser.LPAR:
            result = getChild(0).toExpressions(varMap);
            break;
        default:
            result = toOpExprs(varMap);
        }
        return result;
    }

    /** 
     * Returns the constant expression this tree represents, if any.
     * @return the constant expression this tree represents
     * @throws FormatException if this tree does not represent a constant
     */
    public Constant toConstant() throws FormatException {
        if (getToken().getType() != ExprParser.CONST) {
            throw new FormatException("'%s' does not represent a constant",
                toInputString());
        }
        Constant result = getChild(0).findConstant();
        if (getChildCount() == 2) {
            String prefix = getChild(1).getText();
            SignatureKind sig = SignatureKind.getKind(prefix);
            if (sig == null) {
                throw new FormatException(
                    "Prefix '%s' in '%s' does not represent a type", prefix,
                    toInputString());
            }
            if (result.getSignature() != sig) {
                throw new FormatException("Literal %s is not of type %s",
                    result, prefix);
            }
        }
        result.setInputString(toInputString());
        return result;
    }

    /** Returns the constant represented by this subtree. */
    private Constant findConstant() {
        boolean minus = getType() == ExprParser.MINUS;
        ExprTree literal = minus ? getChild(0) : this;
        SignatureKind type = getSigKind(literal.getToken());
        String literalText = minus ? getText() : "";
        literalText += literal.getChild(0).getText();
        return Constant.instance(type, literalText);
    }

    private Map<SignatureKind,Parameter> toParameters() throws FormatException {
        assert getType() == ExprParser.PAR;
        Map<SignatureKind,Parameter> result =
            new EnumMap<SignatureKind,Parameter>(SignatureKind.class);
        int number = Integer.parseInt(getChild(0).getText());
        if (number < 0) {
            throw new FormatException(
                "Parameter '%s' must have a non-negative number",
                toInputString());
        }
        if (getChildCount() == 2) {
            String prefix = getChild(1).getText();
            SignatureKind type = SignatureKind.getKind(prefix);
            if (type == null) {
                throw new FormatException(
                    "Prefix '%s' does not represent a type", prefix);
            }
            result.put(type, new Parameter(number, type));
        } else {
            for (SignatureKind type : SignatureKind.values()) {
                result.put(type, new Parameter(number, type));
            }
        }
        return result;
    }

    private Map<SignatureKind,Expression> toFieldOrVarExprs(
            Map<String,SignatureKind> varMap) throws FormatException {
        assert getType() == ExprParser.FIELD;
        Map<SignatureKind,Expression> result =
            new EnumMap<SignatureKind,Expression>(SignatureKind.class);
        if (getChildCount() == 2) {
            String prefix = getChild(1).getText();
            SignatureKind type = SignatureKind.getKind(prefix);
            if (type == null) {
                throw new FormatException(
                    "Prefix '%s' does not represent a type", prefix);
            }
            result.put(type, getChild(0).toFieldOrVarExpr(varMap, type));
        } else {
            for (SignatureKind type : SignatureKind.values()) {
                result.put(type, getChild(0).toFieldOrVarExpr(varMap, type));
            }
        }
        return result;
    }

    /** 
     * Converts this tree to a {@link Variable} or a {@link FieldExpr}.
     * Chained field expressions are currently unsupported.
     * @param varMap variable typing
     * @param type expected type of the expression
     */
    private Expression toFieldOrVarExpr(Map<String,SignatureKind> varMap,
            SignatureKind type) throws FormatException {
        Expression result;
        if (getType() == ExprParser.DOT) {
            assert getChildCount() == 2;
            result =
                new FieldExpr(getChild(0).getText(), getChild(1).getText(),
                    type);
        } else {
            assert getChildCount() == 0;
            String name = getText();
            SignatureKind varSig = varMap.get(name);
            if (varSig == null) {
                // this is a self-field
                result = new FieldExpr(null, name, type);
            } else if (varSig != type) {
                throw new FormatException("Variable %s is of type %s, not %s",
                    name, varSig.getName(), type.getName());
            } else {
                result = new Variable(name, type);
            }
        }
        return result;
    }

    /**
     * Returns the set of derivable expressions for an operator tree,
     * i.e., in which the root represents an operator.
     */
    private Map<SignatureKind,Expression> toOpExprs(
            Map<String,SignatureKind> varMap) throws FormatException {
        List<Map<SignatureKind,? extends Expression>> args =
            new ArrayList<Map<SignatureKind,? extends Expression>>();
        // all children are arguments
        for (int i = 0; i < getChildCount(); i++) {
            args.add(getChild(i).toExpressions(varMap));
        }
        return toCallExprs(getText(), args, varMap);
    }

    /**
     * Returns the set of derivable expressions for a call tree,
     * i.e., in which the root is a {@link ExprParser#CALL} node.
     * @param varMap variable typing
     */
    private Map<SignatureKind,Expression> toCallExprs(
            Map<String,SignatureKind> varMap) throws FormatException {
        Map<SignatureKind,Expression> result =
            new EnumMap<SignatureKind,Expression>(SignatureKind.class);
        List<Map<SignatureKind,? extends Expression>> args =
            new ArrayList<Map<SignatureKind,? extends Expression>>();
        // last token is an artificial CLOSE
        for (int i = 1; i < getChildCount() - 1; i++) {
            args.add(getChild(i).toExpressions(varMap));
        }
        ExprTree opTree = getChild(0);
        String opName = opTree.getChild(0).getText();
        if (opTree.getChildCount() == 2) {
            result =
                toCallExprs(opTree.getChild(1).getText(), opName, args, varMap);
        } else {
            result = toCallExprs(opName, args, varMap);
        }
        return result;
    }

    /**
     * Returns the set of derivable expressions for an operator call
     * with an explicitly typed operator.
     * @param prefix signature name of the operator
     * @param opName operator name
     * @param args operator arguments. Each argument is a map from 
     * possible types to corresponding expressions
     * @param varMap variable typing
     */
    private Map<SignatureKind,Expression> toCallExprs(String prefix,
            String opName, List<Map<SignatureKind,? extends Expression>> args,
            Map<String,SignatureKind> varMap) throws FormatException {
        Map<SignatureKind,Expression> result =
            new EnumMap<SignatureKind,Expression>(SignatureKind.class);
        SignatureKind opSig = SignatureKind.getKind(prefix);
        Operator op = opSig.getOperator(opName);
        if (op == null) {
            throw new FormatException("Operator '%s:%s' does exist",
                opSig.getName(), opName);
        }
        result.put(op.getResultType(), newCallExp(op, args));
        return result;
    }

    /**
     * Returns the set of derivable expressions for an operator call
     * with an untyped typed operator.
     * @param opName operator name
     * @param args operator arguments. Each argument is a map from 
     * possible types to corresponding expressions
     * @param varMap variable typing
     */
    private Map<SignatureKind,Expression> toCallExprs(String opName,
            List<Map<SignatureKind,? extends Expression>> args,
            Map<String,SignatureKind> varMap) throws FormatException {
        Map<SignatureKind,Expression> result =
            new EnumMap<SignatureKind,Expression>(SignatureKind.class);
        List<Operator> ops = Operator.getOps(opName);
        // look up op based on argument types
        if (ops.isEmpty()) {
            throw new FormatException("No such operator '%s' in '%s'", opName,
                toInputString());
        }
        for (Operator op : ops) {
            boolean duplicate = false;
            try {
                duplicate =
                    (result.put(op.getResultType(), newCallExp(op, args)) != null);
            } catch (FormatException e) {
                // this candidate did not work out; proceed
            }
            if (duplicate) {
                throw new FormatException(
                    "Typing of '%s' is ambiguous: add type prefixes",
                    toInputString());
            }
        }
        if (result.isEmpty()) {
            throw new FormatException(
                "Operator '%s' not applicable to arguments in '%s'", opName,
                toInputString());
        }
        return result;
    }

    /**
     * Factory method for a new operator call expression.
     * @param op the operator of the new expression
     * @param args operator arguments. Each argument is a map from 
     * possible types to corresponding expressions
     * @throws FormatException if {@code args} does not have values
     * for the required operator types
     */
    private Expression newCallExp(Operator op,
            List<Map<SignatureKind,? extends Expression>> args)
        throws FormatException {
        if (op.getArity() != args.size()) {
            throw new FormatException(
                "Operator '%s' expects %s parameters but has %s",
                op.toString(), op.getArity(), args.size());
        }
        List<SignatureKind> parTypes = op.getParamTypes();
        List<Expression> selectedArgs = new ArrayList<Expression>();
        for (int i = 0; i < args.size(); i++) {
            Expression arg = args.get(i).get(parTypes.get(i));
            if (arg == null) {
                throw new FormatException(
                    "Parameter %s of '%s' should have type %s", i,
                    toInputString(), parTypes.get(i));
            }
            selectedArgs.add(arg);
        }
        // we distinguish negated constants, to make sure that
        // int:-1 parses to the same expression as -1
        OpValue opValue = op.getOpValue();
        if ((opValue == IntSignature.Op.NEG || opValue == RealSignature.Op.NEG)
            && selectedArgs.get(0) instanceof Constant) {
            return Constant.instance(op.getResultType(), op.getSymbol()
                + selectedArgs.get(0).toDisplayString());
        } else {
            return new CallExpr(op, selectedArgs);
        }
    }

    /** Returns the signature kind corresponding to a given type token. */
    private SignatureKind getSigKind(Token token) {
        switch (token.getType()) {
        case ExprParser.STRING:
            return SignatureKind.STRING;
        case ExprParser.INT:
            return SignatureKind.INT;
        case ExprParser.BOOL:
            return SignatureKind.BOOL;
        case ExprParser.REAL:
            return SignatureKind.REAL;
        }
        return null;
    }

    /** 
     * Returns the part of the input token stream corresponding to this tree.
     * This is determined by the token numbers of the first and last tokens. 
     */
    private String toInputString() {
        Token first = findFirstToken();
        Token last = findLastToken();
        return this.tokenStream.toString(first, last);
    }

    /** Returns the first token among the root and its children. */
    private Token findFirstToken() {
        Token result = getToken();
        for (int i = 0; i < getChildCount(); i++) {
            Token childFirst = getChild(i).findFirstToken();
            result = getMin(result, childFirst);
        }
        return result;
    }

    /** Returns the last token among the root and its children. */
    private Token findLastToken() {
        Token result = getToken();
        for (int i = 0; i < getChildCount(); i++) {
            Token childFirst = getChild(i).findLastToken();
            result = getMax(result, childFirst);
        }
        return result;
    }

    /** Returns the token that comes first in the input stream. */
    private Token getMin(Token one, Token two) {
        if (one.getTokenIndex() < 0) {
            return two;
        }
        if (two.getTokenIndex() < 0) {
            return one;
        }
        if (one.getTokenIndex() < two.getTokenIndex()) {
            return one;
        }
        return two;
    }

    /** Returns the token that comes last in the input stream. */
    private Token getMax(Token one, Token two) {
        if (one.getTokenIndex() < 0) {
            return two;
        }
        if (two.getTokenIndex() < 0) {
            return one;
        }
        if (one.getTokenIndex() > two.getTokenIndex()) {
            return one;
        }
        return two;
    }

    /** The token stream from which this token is created. */
    private final CommonTokenStream tokenStream;
}

package groove.algebra.syntax;

import groove.algebra.Constant;
import groove.algebra.Operator;
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
            throw new IllegalArgumentException(String.format(
                "Can't derive type of '%s': add type prefix", toInputString()));
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
        case ExprParser.VAR:
            Variable variable = toVariable(varMap);
            result =
                Collections.singletonMap(variable.getSignature(), variable);
            break;
        case ExprParser.PAR:
            result = toParameters();
            break;
        case ExprParser.FIELD:
            result = toFieldExprs();
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

    private Variable toVariable(Map<String,SignatureKind> varMap)
        throws FormatException {
        String name = getChild(0).getText();
        SignatureKind varSig = varMap.get(name);
        if (varSig == null) {
            throw new FormatException("Unknown variable %s", name);
        }
        if (getChildCount() == 2) {
            String prefix = getChild(1).getText();
            SignatureKind prefixSig = SignatureKind.getKind(prefix);
            if (prefixSig == null) {
                throw new FormatException(
                    "Prefix '%s' does not represent a type", prefix);
            }
            if (varSig != prefixSig) {
                throw new FormatException("Variable %s is of type %s, not %s",
                    name, varSig.getName(), prefix);
            }
        }
        return new Variable(name, varSig);
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

    private Map<SignatureKind,FieldExpr> toFieldExprs() throws FormatException {
        assert getType() == ExprParser.FIELD;
        Map<SignatureKind,FieldExpr> result =
            new EnumMap<SignatureKind,FieldExpr>(SignatureKind.class);
        String target = getChild(0).getText();
        String field = getChild(1).getText();
        if (getChildCount() == 3) {
            String prefix = getChild(2).getText();
            SignatureKind type = SignatureKind.getKind(prefix);
            if (type == null) {
                throw new FormatException(
                    "Prefix '%s' does not represent a type", prefix);
            }
            result.put(type, new FieldExpr(target, field, type));
        } else {
            for (SignatureKind type : SignatureKind.values()) {
                result.put(type, new FieldExpr(target, field, type));
            }
        }
        return result;
    }

    /**
     * Returns the set of derivable expressions for an operator tree,
     * i.e., in which the root represents an operator.
     */
    private Map<SignatureKind,CallExpr> toOpExprs(
            Map<String,SignatureKind> varMap) throws FormatException {
        List<Map<SignatureKind,? extends Expression>> args =
            new ArrayList<Map<SignatureKind,? extends Expression>>();
        // all children are arguments
        for (int i = 0; i < getChildCount(); i++) {
            args.add(getChild(i).toExpressions(varMap));
        }
        return toCallExprs(getText(), args, varMap);
    }

    private Map<SignatureKind,CallExpr> toCallExprs(
            Map<String,SignatureKind> varMap) throws FormatException {
        Map<SignatureKind,CallExpr> result =
            new EnumMap<SignatureKind,CallExpr>(SignatureKind.class);
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

    private Map<SignatureKind,CallExpr> toCallExprs(String prefix,
            String opName, List<Map<SignatureKind,? extends Expression>> args,
            Map<String,SignatureKind> varMap) throws FormatException {
        Map<SignatureKind,CallExpr> result =
            new EnumMap<SignatureKind,CallExpr>(SignatureKind.class);
        SignatureKind opSig = SignatureKind.getKind(prefix);
        Operator op = opSig.getOperator(opName);
        if (op == null) {
            throw new FormatException("Operator '%s:%s' does exist",
                opSig.getName(), opName);
        }
        result.put(op.getResultType(), newCallExp(op, args));
        return result;
    }

    private Map<SignatureKind,CallExpr> toCallExprs(String opName,
            List<Map<SignatureKind,? extends Expression>> args,
            Map<String,SignatureKind> varMap) throws FormatException {
        Map<SignatureKind,CallExpr> result =
            new EnumMap<SignatureKind,CallExpr>(SignatureKind.class);
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

    private CallExpr newCallExp(Operator op,
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
        return new CallExpr(op, selectedArgs);
    }

    /** Returns the constant represented by this subtree. */
    private Constant findConstant() {
        return Constant.instance(getSigKind(getToken()), getChild(0).getText());
    }

    /** Returns the signature kind corresponding to a given type token. */
    private SignatureKind getSigKind(Token token) {
        switch (getToken().getType()) {
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

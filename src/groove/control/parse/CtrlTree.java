package groove.control.parse;

import groove.control.Call;
import groove.control.CtrlAut;
import groove.control.CtrlCall;
import groove.control.CtrlPar;
import groove.control.CtrlType;
import groove.control.CtrlVar;
import groove.control.symbolic.Term;
import groove.grammar.Action;
import groove.grammar.model.FormatException;
import groove.util.antlr.ParseTree;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;

/**
 * Dedicated tree node for GCL parsing.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlTree extends ParseTree<CtrlTree,Namespace> {
    /**
     * Empty constructor for prototype construction.
     * Keep visibility protected to allow constructions from {@link ParseTree}. 
     */
    public CtrlTree() {
        // empty
    }

    /** Creates a tree wrapping a given token. */
    CtrlTree(Token token) {
        this.token = token;
    }

    /** Creates a tree wrapping a token of a given type. */
    CtrlTree(int tokenType) {
        this(new CommonToken(tokenType));
    }

    /** Returns the derived type stored in this tree node, if any. */
    public CtrlType getCtrlType() {
        return this.type;
    }

    /** Stores a type in this tree node. */
    public void setCtrlType(CtrlType type) {
        this.type = type;
    }

    private CtrlType type;

    /** Returns the control variable stored in this tree node, if any. */
    public CtrlVar getCtrlVar() {
        return this.var;
    }

    /** Stores a control variable in this tree node. */
    public void setCtrlVar(CtrlVar var) {
        this.var = var;
    }

    private CtrlVar var;

    /** Returns the control parameter stored in this tree node, if any. */
    public CtrlPar getCtrlPar() {
        return this.par;
    }

    /** Stores a control parameter in this tree node. */
    public void setCtrlPar(CtrlPar par) {
        this.par = par;
    }

    private CtrlPar par;

    /** Returns the derived rule call stored in this tree node, if any. */
    public CtrlCall getCtrlCall() {
        return this.call;
    }

    /** Stores a rule call in this tree node. */
    public void setCtrlCall(CtrlCall call) {
        this.call = call;
    }

    private CtrlCall call;

    /** Returns a list of all call tokens in this tree with a given name. */
    public List<Token> getCallTokens(String name) {
        List<Token> result = new ArrayList<Token>();
        collectCallTokens(result, name);
        return result;
    }

    /** Recursively collects all call tokens with a given name. */
    private void collectCallTokens(List<Token> result, String name) {
        if (getToken().getType() == CtrlLexer.CALL
            && getToken().getText().equals(name)) {
            result.add(getToken());
        }
        for (int i = 0; i < getChildCount(); i++) {
            getChild(i).collectCallTokens(result, name);
        }
    }

    @Override
    protected void setNode(CtrlTree node) {
        super.setNode(node);
        this.par = node.par;
        this.var = node.var;
        this.call = node.call;
        this.type = node.type;
    }

    /**
     * Runs the checker on this tree.
     * @return the resulting (transformed) syntax tree
     */
    public CtrlTree check() throws FormatException {
        try {
            CtrlChecker checker = createChecker();
            CtrlTree result = (CtrlTree) checker.program().getTree();
            getInfo().getErrors().throwException();
            return result;
        } catch (RecognitionException e) {
            throw new FormatException(e);
        }
    }

    /** Creates a checker for this tree. */
    public CtrlChecker createChecker() {
        return createTreeParser(CtrlChecker.class, getInfo());
    }

    /**
     * Runs the builder on this tree.
     * @return the resulting control automaton
     */
    public CtrlAut build() throws FormatException {
        try {
            CtrlBuilder builder = createBuilder();
            CtrlAut result = builder.program().aut;
            getInfo().getErrors().throwException();
            return result == null ? null
                    : result.clone(getInfo().getFullName());
        } catch (RecognitionException e) {
            throw new FormatException(e);
        }
    }

    /**
     * Constructs a control term from this tree.
     * This is only well-defined if the root of the tree is a statement.
     */
    public Term toTerm() {
        Term result = null;
        Term prototype = Term.prototype();
        switch (getType()) {
        case CtrlParser.BLOCK:
            result = prototype.epsilon();
            for (int i = getChildCount() - 1; i >= 0; i--) {
                result = getChild(i).toTerm().seq(result);
            }
            break;
        case CtrlParser.SEMI:
            result = getChild(0).toTerm();
            break;
        case CtrlParser.TRUE:
        case CtrlParser.VAR:
            result = prototype.epsilon();
            break;
        case CtrlParser.ALAP:
            result = getChild(0).toTerm().alap();
            break;
        case CtrlParser.WHILE:
            result = getChild(0).toTerm().seq(getChild(1).toTerm()).whileDo();
            break;
        case CtrlParser.UNTIL:
            result = getChild(0).toTerm().untilDo(getChild(1).toTerm());
            break;
        case CtrlParser.TRY:
            result = getChild(0).toTerm().tryElse(getChild(1).toTerm());
            break;
        case CtrlParser.IF:
            result = getChild(0).toTerm().ifElse(getChild(1).toTerm());
            break;
        case CtrlParser.CHOICE:
            result = getChild(0).toTerm();
            for (int i = 1; i < getChildCount(); i++) {
                result = result.or(getChild(i).toTerm());
            }
            break;
        case CtrlParser.STAR:
            result = getChild(0).toTerm().star();
            break;
        case CtrlParser.CALL:
            CtrlCall call = getCtrlCall();
            Call newCall = new Call(call.getUnit(), call.getArgs());
            result = prototype.call(newCall);
            break;
        case CtrlParser.ANY:
            result = prototype.delta();
            for (Action action : getInfo().getActions()) {
                Call part = new Call(action);
                result = result.or(prototype.call(part));
            }
            break;
        case CtrlParser.OTHER:
            result = prototype.delta();
            for (Action action : getInfo().getActions()) {
                if (!getInfo().getUsedNames().contains(action.getFullName())) {
                    Call part = new Call(action);
                    result = result.or(prototype.call(part));
                }
            }
            break;
        }
        return result;
    }

    /** Creates a builder for this tree */
    public CtrlBuilder createBuilder() {
        return createTreeParser(CtrlBuilder.class, getInfo());
    }

    /** Parses a given term, using an existing name space. */
    static public CtrlTree parse(Namespace namespace, String term)
        throws FormatException {
        try {
            CtrlParser parser = createParser(namespace, term);
            CtrlTree result = (CtrlTree) parser.program().getTree();
            namespace.getErrors().throwException();
            return result;
        } catch (RecognitionException e) {
            throw new FormatException(e);
        }
    }

    /** Creates a parser for a given term. */
    static public CtrlParser createParser(Namespace namespace, String term) {
        return PROTOTYPE.createParser(CtrlParser.class, namespace, term);
    }

    private static final CtrlTree PROTOTYPE = new CtrlTree();
}

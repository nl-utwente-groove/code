package groove.control.parse;

import groove.control.Call;
import groove.control.CtrlAut;
import groove.control.CtrlCall;
import groove.control.CtrlPar;
import groove.control.CtrlType;
import groove.control.CtrlVar;
import groove.control.Procedure;
import groove.control.Program;
import groove.control.symbolic.Term;
import groove.grammar.QualName;
import groove.grammar.model.FormatError;
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
        CtrlType result;
        if (getType() == CtrlChecker.NODE) {
            result = CtrlType.NODE;
        } else {
            result = CtrlType.valueOf(getText().toUpperCase());
        }
        return result;
    }

    /** Returns the control program name stored in this tree node, if any. */
    public String getControlName() {
        return this.controlName;
    }

    /** Stores a control variable in this tree node. */
    public void setControlName(String controlName) {
        assert getType() == CtrlParser.PROGRAM;
        assert controlName != null;
        this.controlName = controlName;
    }

    private String controlName;

    /** Returns the control variable stored in this tree node, if any. */
    public CtrlVar getCtrlVar() {
        return this.var;
    }

    /** Stores a control variable in this tree node. */
    public void setCtrlVar(CtrlVar var) {
        assert var != null;
        this.var = var;
    }

    private CtrlVar var;

    /** Returns the control parameter stored in this tree node, if any. */
    public CtrlPar getCtrlPar() {
        return this.par;
    }

    /** Stores a control parameter in this tree node. */
    public void setCtrlPar(CtrlPar par) {
        assert par != null;
        this.par = par;
    }

    private CtrlPar par;

    /** Returns the derived rule call stored in this tree node, if any. */
    public CtrlCall getCtrlCall() {
        return this.call;
    }

    /** Stores a rule call in this tree node. */
    public void setCtrlCall(CtrlCall call) {
        assert call != null;
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

    /** Sets this tree to checked. */
    private void setChecked() {
        this.checked = true;
    }

    /** Indicates if this tree has been checked,
     * i.e., it is the result of a {@link #check()} call.
     */
    public boolean isChecked() {
        return this.checked;
    }

    private boolean checked;

    /**
     * Constructs a control term from this tree.
     * This is only well-defined if the root of the tree is a statement.
     * @throws FormatException if the term being built violates static semantic assumptions
     */
    public Term toTerm() throws FormatException {
        return toTerm(Term.prototype());
    }

    /**
     * Constructs a control term from this tree, using a given term prototype.
     * @throws FormatException if the term being built violates static semantic assumptions
     */
    private Term toTerm(Term prot) throws FormatException {
        Term result = null;
        int arity;
        switch (getType()) {
        case CtrlParser.VAR:
        case CtrlParser.CALL:
            arity = 0;
            break;
        default:
            arity = getChildCount();
        }
        Term[] args = new Term[arity];
        for (int i = 0; i < arity; i++) {
            args[i] = getChild(i).toTerm(prot);
        }
        switch (getType()) {
        case CtrlParser.BLOCK:
            result = prot.epsilon();
            for (Term arg : args) {
                result = result.seq(arg);
            }
            break;
        case CtrlParser.ATOM:
            checkSuitableForAtom(args[0]);
            result = args[0].atom();
            break;
        case CtrlParser.SEMI:
            result = args[0];
            break;
        case CtrlParser.TRUE:
        case CtrlParser.VAR:
            result = prot.epsilon();
            break;
        case CtrlParser.ALAP:
            checkSuitableForAtom(args[0]);
            result = args[0].alap();
            break;
        case CtrlParser.WHILE:
            result = args[0].seq(args[1]).whileDo();
            break;
        case CtrlParser.UNTIL:
            result = args[0].untilDo(args[1]);
            break;
        case CtrlParser.TRY:
            args[0] = args[0];
            checkSuitableForAtom(args[0]);
            if (getChildCount() == 1) {
                // without else clause
                result = args[0].tryNoElse();
            } else {
                result = args[0].tryElse(args[1]);
            }
            break;
        case CtrlParser.IF:
            if (getChildCount() == 2) {
                // without else clause
                result = args[0].seq(args[1]).ifNoElse();
            } else {
                // with else clause
                result = args[0].seq(args[1]).ifElse(args[2]);
            }
            break;
        case CtrlParser.CHOICE:
            result = prot.delta();
            for (Term a : args) {
                result = result.or(a);
            }
            break;
        case CtrlParser.STAR:
            result = args[0].star();
            break;
        case CtrlParser.CALL:
            CtrlCall call = getCtrlCall();
            Call newCall;
            if (call.getArgs() == null) {
                newCall = new Call(call.getUnit());
            } else {
                newCall = new Call(call.getUnit(), call.getArgs());
            }
            result = prot.call(newCall);
            break;
        case CtrlParser.ANY:
            result = prot.delta();
            for (String name : getInfo().getTopNames()) {
                Call part = new Call(getInfo().getCallable(name));
                result = result.or(prot.call(part));
            }
            break;
        case CtrlParser.OTHER:
            result = prot.delta();
            for (String name : getInfo().getTopNames()) {
                if (!getInfo().getUsedNames().contains(name)) {
                    Call part = new Call(getInfo().getCallable(name));
                    result = result.or(prot.call(part));
                }
            }
            break;
        default:
            assert false;
        }
        return result;
    }

    private void checkSuitableForAtom(Term term) throws FormatException {
        if (!term.hasClearFinal()) {
            throw new FormatException(
                createError("Atomically wrapped argument does not have a clear final state"));
        }
    }

    /** Constructs a control program from a top-level control tree. */
    public Program toProgram() throws FormatException {
        assert getType() == CtrlParser.PROGRAM;
        Program result = new Program(getControlName());
        CtrlTree functions = getChild(2);
        CtrlTree recipes = getChild(3);
        CtrlTree body = getChild(4);
        if (body.getChildCount() > 0) {
            result.setTerm(body.toTerm());
        }
        for (int i = 0; i < functions.getChildCount(); i++) {
            CtrlTree funcTree = functions.getChild(i);
            result.addProc(funcTree.toProcedure());
        }
        for (int i = 0; i < recipes.getChildCount(); i++) {
            CtrlTree recipeTree = recipes.getChild(i);
            result.addProc(recipeTree.toProcedure());
        }
        result.setFixed();
        return result;
    }

    /** Returns the package name from a top-level control tree. */
    public String toPackageName() {
        assert getType() == CtrlParser.PROGRAM;
        CtrlTree pack = getChild(0);
        return pack == null ? "" : pack.getText();
    }

    /**
     * Converts this control tree to a procedure object.
     * This is only valid if the root is a function or recipe node.
     * @throws FormatException if there are static semantic errors in the declaration.
     */
    public Procedure toProcedure() throws FormatException {
        assert getType() == CtrlParser.FUNCTION
            || getType() == CtrlParser.RECIPE;
        // look up the package name
        String packName = getParent().getParent().toPackageName();
        String name = QualName.extend(packName, getChild(0).getText());
        Procedure result = (Procedure) getInfo().getCallable(name);
        CtrlTree bodyTree = getChild(getChildCount() - 1);
        result.setTerm(bodyTree.toTerm());
        return result;
    }

    /**
     * Tests if this term can evolve to a final state.
     */
    public boolean maybeFinal() {
        boolean result = false;
        CtrlTree arg0 = getChildCount() > 0 ? getChild(0) : null;
        CtrlTree arg1 = getChildCount() > 1 ? getChild(1) : null;
        switch (getType()) {
        case CtrlParser.TRUE:
        case CtrlParser.VAR:
        case CtrlParser.ALAP:
        case CtrlParser.STAR:
            result = true;
            break;
        case CtrlParser.CALL:
        case CtrlParser.ANY:
        case CtrlParser.OTHER:
            result = false;
            break;
        case CtrlParser.BLOCK:
            result = true;
            for (int i = 0; i < getChildCount(); i++) {
                result &= getChild(i).maybeFinal();
            }
            break;
        case CtrlParser.ATOM:
        case CtrlParser.SEMI:
        case CtrlParser.UNTIL:
            result = arg0.maybeFinal();
            break;
        case CtrlParser.WHILE:
            result = arg0.maybeFinal() && arg1.maybeFinal();
            break;
        case CtrlParser.TRY:
        case CtrlParser.IF:
            result = arg0.maybeFinal() || (arg1 != null && arg1.maybeFinal());
            break;
        case CtrlParser.CHOICE:
            result = false;
            for (int i = 0; i < getChildCount(); i++) {
                result |= getChild(i).maybeFinal();
            }
            break;
        default:
            assert false;
        }
        return result;
    }

    /**
     * Tests if this term always evolves to a clean final state.
     * A final state is clean if it has no outgoing transitions.
     */
    public boolean hasCleanFinal() {
        boolean result = false;
        CtrlTree arg0 = getChildCount() > 0 ? getChild(0) : null;
        CtrlTree arg1 = getChildCount() > 1 ? getChild(1) : null;
        switch (getType()) {
        case CtrlParser.TRUE:
        case CtrlParser.VAR:
        case CtrlParser.CALL:
        case CtrlParser.ANY:
        case CtrlParser.OTHER:
            result = true;
            break;
        case CtrlParser.STAR:
        case CtrlParser.ALAP:
            result = false;
            break;
        case CtrlParser.BLOCK:
            result = true;
            for (int i = 0; i < getChildCount(); i++) {
                result &= getChild(i).maybeFinal();
            }
            break;
        case CtrlParser.ATOM:
        case CtrlParser.SEMI:
        case CtrlParser.UNTIL:
            result = arg0.maybeFinal();
            break;
        case CtrlParser.WHILE:
            result = arg0.maybeFinal() && arg1.maybeFinal();
            break;
        case CtrlParser.TRY:
        case CtrlParser.IF:
            result = arg0.maybeFinal() || (arg1 != null && arg1.maybeFinal());
            break;
        case CtrlParser.CHOICE:
            result = false;
            for (int i = 0; i < getChildCount(); i++) {
                result |= getChild(i).maybeFinal();
            }
            break;
        default:
            assert false;
        }
        return result;
    }

    /**
     * Runs the checker on this tree.
     * @return the resulting (transformed) syntax tree
     */
    public CtrlTree check() throws FormatException {
        assert getType() == CtrlParser.PROGRAM;
        if (isChecked()) {
            return this;
        } else {
            try {
                CtrlChecker checker = createChecker();
                CtrlTree result = (CtrlTree) checker.program().getTree();
                getInfo().getErrors().throwException();
                result.setControlName(getControlName());
                result.setChecked();
                return result;
            } catch (RecognitionException e) {
                throw new FormatException(e);
            }
        }
    }

    /** Creates a checker for this tree. */
    public CtrlChecker createChecker() {
        return createTreeParser(CtrlChecker.class, getInfo());
    }

    /**
     * Runs the builder on this tree.
     * Checks the tree first, if this has not yet been done.
     * @return the resulting control automaton
     */
    public CtrlAut build() throws FormatException {
        if (isChecked()) {
            try {
                CtrlBuilder builder = createBuilder();
                CtrlAut result = builder.program().aut;
                getInfo().getErrors().throwException();
                return result == null ? null : result.clone(getControlName());
            } catch (RecognitionException e) {
                throw new FormatException(e);
            }
        } else {
            return check().build();
        }
    }

    /** Creates a builder for this tree */
    public CtrlBuilder createBuilder() {
        return createTreeParser(CtrlBuilder.class, getInfo());
    }

    @Override
    protected void setNode(CtrlTree node) {
        super.setNode(node);
        this.par = node.par;
        this.var = node.var;
        this.call = node.call;
        this.controlName = node.controlName;
    }

    /**
     * Constructs an error object from a given message and arguments,
     * by adding a line and column indicator in front.
     */
    public FormatError createError(String message, Object... args) {
        FormatError inner = new FormatError(message, args);
        int line = getLine();
        int column = getCharPositionInLine();
        return new FormatError("line %d:%d %s", line, column, inner);
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

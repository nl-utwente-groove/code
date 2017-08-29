package groove.control.parse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;

import groove.control.Call;
import groove.control.CtrlPar;
import groove.control.CtrlType;
import groove.control.CtrlVar;
import groove.control.Procedure;
import groove.control.template.Fragment;
import groove.control.term.Term;
import groove.grammar.Action;
import groove.grammar.Callable;
import groove.grammar.Callable.Kind;
import groove.grammar.CheckPolicy;
import groove.grammar.ModuleName;
import groove.grammar.QualName;
import groove.util.antlr.ParseTree;
import groove.util.parse.FormatError;
import groove.util.parse.FormatException;

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
        this.calls = new ArrayList<>();
    }

    /** Creates a tree wrapping a given token. */
    CtrlTree(Token token) {
        this();
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

    /** Returns the qualified name stored in this tree node, if any. */
    public QualName getQualName() {
        return this.qualName;
    }

    /** Stores a qualified name in this tree node. */
    public void setQualName(QualName qualName) {
        assert qualName != null && !qualName.hasErrors();
        this.qualName = qualName;
    }

    private QualName qualName;

    /** Returns the control program name stored in this tree node, if any. */
    public QualName getControlName() {
        return this.controlName;
    }

    /** Stores a control program name in this tree node. */
    public void setControlName(QualName controlName) {
        assert getType() == CtrlParser.PROGRAM;
        assert controlName != null && !controlName.hasErrors();
        this.controlName = controlName;
    }

    private QualName controlName;

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

    /** Returns the set of calls stored in this tree node, if any. */
    public List<Call> getCalls() {
        return this.calls;
    }

    /** Adds a call to this tree node. */
    public void addCall(Call call) {
        assert call != null;
        this.calls.add(call);
    }

    private final List<Call> calls;

    /** Returns a list of all rule ID tokens in this tree with a given name. */
    public List<CtrlTree> getRuleIdTokens(QualName name) {
        List<CtrlTree> result = new ArrayList<>();
        collectRuleIdTokens(result, name);
        return result;
    }

    /** Recursively collects all rule ID tokens with a given name. */
    private void collectRuleIdTokens(List<CtrlTree> result, QualName name) {
        int tokenType = getToken().getType();
        if (tokenType == CtrlLexer.CALL || tokenType == CtrlLexer.IMPORT) {
            CtrlTree id = getChild(0);
            if (id.getText()
                .equals(name.toString())) {
                result.add(id);
            }
        } else {
            for (int i = 0; i < getChildCount(); i++) {
                getChild(i).collectRuleIdTokens(result, name);
            }
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
        Term prot = getInfo().getPrototype();
        Term result = null;
        int arity;
        switch (getType()) {
        case CtrlParser.VAR:
        case CtrlParser.BECOMES:
        case CtrlParser.CALL:
            arity = 0;
            break;
        default:
            arity = getChildCount();
        }
        Term[] args = new Term[arity];
        for (int i = 0; i < arity; i++) {
            args[i] = getChild(i).toTerm();
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
            result = args[0].whileDo(args[1]);
            break;
        case CtrlParser.UNTIL:
            result = args[0].untilDo(args[1]);
            break;
        case CtrlParser.TRY:
            args[0] = args[0];
            checkSuitableForAtom(args[0]);
            if (getChildCount() == 1) {
                // without else clause
                result = args[0].tryOnly();
            } else {
                result = args[0].tryElse(args[1]);
            }
            break;
        case CtrlParser.IF:
            if (args[0].willSucceed()) {
                // the condition will always succeed, no else required
                result = args[0].seq(args[1]);
            } else if (getChildCount() == 2) {
                // without else clause
                result = args[0].ifOnly(args[1]);
            } else {
                // with else clause
                result = args[0].ifElse(args[1], args[2]);
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
        case CtrlParser.BECOMES:
        case CtrlParser.CALL:
            CtrlTree callTree = getType() == CtrlParser.CALL ? this : getChild(1);
            if (callTree.getChild(0)
                .getType() == CtrlParser.ID) {
                // it's a single call
                assert getCalls().size() == 1;
                result = prot.call(getCalls().get(0));
            } else {
                // it's a group call
                SortedMap<Integer,List<Call>> prioMap = new TreeMap<>();
                for (Call call : getCalls()) {
                    Action action = (Action) call.getUnit();
                    if (action.getPolicy() == CheckPolicy.OFF) {
                        continue;
                    }
                    // the action list to which this action should be added
                    List<Call> actions = prioMap.get(action.getPriority());
                    if (actions == null) {
                        prioMap.put(action.getPriority(), actions = new ArrayList<>());
                    }
                    actions.add(call);
                }
                result = prot.delta();
                for (List<Call> calls : prioMap.values()) {
                    result = or(calls).tryElse(result);
                }
            }
            break;
        default:
            assert false;
        }
        return result;
    }

    private Term or(Collection<Call> actions) {
        Term prot = getInfo().getPrototype();
        Term result = prot.delta();
        for (Call call : actions) {
            result = result.or(prot.call(call));
        }
        return result;
    }

    /**
     * Checks the suitability of a given term as body of an atomic block
     * @throws FormatException if the given term is not suitable to be used
     * within an atomic block
     */
    private void checkSuitableForAtom(Term term) throws FormatException {
        // currently no limiting factors are imposed
    }

    /**
     * Constructs a control program fragment from a top-level control tree.
     */
    public Fragment toFragment() throws FormatException {
        assert getType() == CtrlParser.PROGRAM && isChecked();
        Fragment result = new Fragment(getControlName());
        CtrlTree body = getChild(4);
        // set the main if this tree has a body
        if (body.getChildCount() > 0) {
            result.setMain(body.toTerm());
        }
        for (CtrlTree funcTree : getProcs(Callable.Kind.FUNCTION).values()) {
            result.addProc(funcTree.toProcedure());
        }
        for (CtrlTree recipeTree : getProcs(Callable.Kind.RECIPE).values()) {
            result.addProc(recipeTree.toProcedure());
        }
        return result;
    }

    /**
     * Returns the function or recipe subtrees declared in the control program.
     * Only valid if this tree is a program.
     * @param procKind either {@link Kind#FUNCTION}
     * or {@link Kind#RECIPE}.
     */
    public Map<QualName,CtrlTree> getProcs(Callable.Kind procKind) {
        assert getType() == CtrlParser.PROGRAM && isChecked();
        Map<QualName,CtrlTree> result = new TreeMap<>();
        ModuleName packName = toPackageName();
        CtrlTree parent;
        if (procKind == Callable.Kind.FUNCTION) {
            parent = getChild(2);
            assert parent.getToken()
                .getType() == CtrlParser.FUNCTIONS;
        } else {
            parent = getChild(3);
            assert parent.getToken()
                .getType() == CtrlParser.RECIPES;
        }
        for (int i = 0; i < parent.getChildCount(); i++) {
            CtrlTree tree = parent.getChild(i);
            QualName name = packName.extend(tree.getChild(0)
                .getText());
            result.put(name, tree);
        }
        return result;
    }

    /** Returns the package name from a top-level control tree. */
    public ModuleName toPackageName() {
        assert getType() == CtrlParser.PROGRAM;
        CtrlTree pack = getChild(0);
        assert pack != null : "Empty package should have been set";
        ModuleName result = pack.getChild(0)
            .getQualName();
        // the empty package has a null module name
        return result == null ? ModuleName.TOP : result;
    }

    /**
     * Converts this control tree to a procedure object.
     * This is only valid if the root is a function or recipe node.
     * @throws FormatException if there are static semantic errors in the declaration.
     */
    public Procedure toProcedure() throws FormatException {
        assert getType() == CtrlParser.FUNCTION || getType() == CtrlParser.RECIPE;
        // look up the package name
        ModuleName packName = getParent().getParent()
            .toPackageName();
        QualName name = packName.extend(getChild(0).getText());
        Procedure result = (Procedure) getInfo().getCallable(name);
        CtrlTree bodyTree = getChild(getChildCount() - 1);
        Term bodyTerm = bodyTree.toTerm();
        if (getType() == CtrlParser.RECIPE) {
            checkSuitableForAtom(bodyTerm);
        }
        result.setTerm(bodyTerm);
        return result;
    }

    /**
     * Tests if this term can evolve to a final state.
     */
    public boolean maybeFinal() {
        boolean result = false;
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
            result = getChild(0).maybeFinal();
            break;
        case CtrlParser.WHILE:
            result = getChild(0).maybeFinal() && getChild(1).maybeFinal();
            break;
        case CtrlParser.TRY:
        case CtrlParser.IF:
            result = getChild(0).maybeFinal() || (getChild(1) != null && getChild(1).maybeFinal());
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
                getInfo().setControlName(getControlName());
                CtrlChecker checker = createChecker();
                CtrlTree result = (CtrlTree) checker.program()
                    .getTree();
                getInfo().getErrors()
                    .throwException();
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

    @Override
    protected void setNode(CtrlTree node) {
        super.setNode(node);
        this.par = node.par;
        this.var = node.var;
        this.calls.addAll(node.calls);
        this.qualName = node.qualName;
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
    static public CtrlTree parse(Namespace namespace, String term) throws FormatException {
        try {
            CtrlParser parser = createParser(namespace, term);
            CtrlTree result = parser.program()
                .getTree();
            namespace.getErrors()
                .throwException();
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

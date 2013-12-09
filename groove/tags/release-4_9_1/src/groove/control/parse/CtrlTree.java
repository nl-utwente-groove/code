package groove.control.parse;

import groove.control.CtrlCall;
import groove.control.CtrlPar;
import groove.control.CtrlType;
import groove.control.CtrlVar;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

/**
 * Dedicated tree node for GCL parsing.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlTree extends CommonTree {
    /** Constructor to duplicate a given tree node. */
    private CtrlTree(CtrlTree node) {
        super(node);
        this.par = node.par;
        this.var = node.var;
        this.call = node.call;
        this.type = node.type;
    }

    /** Constructor for the factory method. */
    public CtrlTree(Token t) {
        super(t);
    }

    /** Empty constructor for subclassing. */
    protected CtrlTree() {
        // empty
    }

    @Override
    public Tree dupNode() {
        return new CtrlTree(this);
    }

    /** Overridden to specialise the type. */
    @Override
    public CtrlTree getChild(int i) {
        return (CtrlTree) super.getChild(i);
    }

    /** Returns the derived type stored in this tree node, if any. */
    public CtrlType getCtrlType() {
        return this.type;
    }

    /** Stores a type in this tree node. */
    public void setCtrlType(CtrlType type) {
        this.type = type;
    }

    /** Returns the control variable stored in this tree node, if any. */
    public CtrlVar getCtrlVar() {
        return this.var;
    }

    /** Stores a control variable in this tree node. */
    public void setCtrlVar(CtrlVar var) {
        this.var = var;
    }

    /** Returns the control parameter stored in this tree node, if any. */
    public CtrlPar getCtrlPar() {
        return this.par;
    }

    /** Stores a control parameter in this tree node. */
    public void setCtrlPar(CtrlPar par) {
        this.par = par;
    }

    /** Returns the derived rule call stored in this tree node, if any. */
    public CtrlCall getCtrlCall() {
        return this.call;
    }

    /** Stores a rule call in this tree node. */
    public void setCtrlCall(CtrlCall call) {
        this.call = call;
    }

    /** Returns the (possibly {@code null}) input stream of this call tree. */
    public ANTLRStringStream getInputStream() {
        return this.inputStream;
    }

    /** Sets the input stream of this call tree. */
    public void setInputStream(ANTLRStringStream inputStream) {
        this.inputStream = inputStream;
    }

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

    private CtrlVar var;
    private CtrlPar par;
    private CtrlCall call;
    private CtrlType type;
    private ANTLRStringStream inputStream;
}

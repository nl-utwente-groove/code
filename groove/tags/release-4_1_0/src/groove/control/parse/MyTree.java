package groove.control.parse;

import groove.control.CtrlCall;
import groove.control.CtrlPar;
import groove.control.CtrlType;
import groove.control.CtrlVar;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

/**
 * Dedicated tree node for GCL parsing.
 * @author Arend Rensink
 * @version $Revision $
 */
public class MyTree extends CommonTree {
    /** Constructor to duplicate a given tree node. */
    private MyTree(MyTree node) {
        super(node);
        this.par = node.par;
        this.var = node.var;
        this.call = node.call;
        this.type = node.type;
    }

    /** Constructor for the factory method. */
    public MyTree(Token t) {
        super(t);
    }

    /** Empty constructor for subclassing. */
    protected MyTree() {
        // empty
    }

    @Override
    public Tree dupNode() {
        return new MyTree(this);
    }

    /** Overridden to specialise the type. */
    @Override
    public MyTree getChild(int i) {
        return (MyTree) super.getChild(i);
    }

    /** Overridden to specialise the type. */
    @Override
    public Tree getFirstChildWithType(int type) {
        return super.getFirstChildWithType(type);
    }

    /** Returns the derived type stored in this tree node, if any. */
    public CtrlType getCtrlType() {
        return this.type;
    }

    /** Stores a type in this tree node. */
    public void setCtrlType(CtrlType type) {
        this.type = type;
    }

    /** Copies the chart type from a given tree node. */
    public void setCtrlType(MyTree tree) {
        this.type = tree.type;
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

    private CtrlVar var;
    private CtrlPar par;
    private CtrlCall call;
    private CtrlType type;
}

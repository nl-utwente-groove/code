package groove.control.parse;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonErrorNode;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.TreeAdaptor;
import org.antlr.runtime.tree.TreeIterator;
import org.antlr.runtime.tree.TreeNodeStream;

/**
 * Tree adaptor creating {@link MyTree} nodes and error nodes. 
 * @author Arend Rensink
 * @version $Revision $
 */
public class MyTreeAdaptor extends CommonTreeAdaptor {
    @Override
    public MyTree create(Token payload) {
        return new MyTree(payload);
    }

    @Override
    public MyTree errorNode(TokenStream input, Token start, Token stop,
            RecognitionException e) {
        return new MyErrorNode(input, start, stop, e);
    }

    /** Creates a tree node stream based on this adaptor. */
    public TreeNodeStream createTreeNodeStream(MyTree tree) {
        return new MyTreeNodeStream(this, tree);
    }

    /** 
     * Subclass necessary to set the tree adaptor of the inner tree iterator correctly.
     * 
     * @author Arend Rensink
     * @version $Revision$
     */
    private class MyTreeNodeStream extends CommonTreeNodeStream {
        public MyTreeNodeStream(TreeAdaptor adaptor, Object tree) {
            super(adaptor, tree);
            // HACK necessary to get the tree adaptor right
            this.it = new MyTreeIterator(adaptor, this.root);
            this.it.eof = this.eof;
        }

        private class MyTreeIterator extends TreeIterator {
            public MyTreeIterator(TreeAdaptor adaptor, Object tree) {
                super(adaptor, tree);
            }
        }
    }

    /**
     * Subclass of {@link MyTree} that delegates to {@link CommonErrorNode}. This is
     * necessary to ensure that all tree nodes are {@link MyTree} instances.
     */
    private class MyErrorNode extends MyTree {
        /** Creates a new error node. */
        public MyErrorNode(TokenStream input, Token start, Token stop,
                RecognitionException e) {
            this.innerNode = new CommonErrorNode(input, start, stop, e);
        }

        @Override
        public boolean isNil() {
            return this.innerNode.isNil();
        }

        @Override
        public int getType() {
            return this.innerNode.getType();
        }

        @Override
        public String getText() {
            return this.innerNode.getText();
        }

        @Override
        public String toString() {
            return this.innerNode.toString();
        }

        private final CommonErrorNode innerNode;
    }
}

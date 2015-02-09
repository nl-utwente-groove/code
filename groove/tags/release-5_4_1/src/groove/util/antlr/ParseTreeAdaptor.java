package groove.util.antlr;

import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeNodeStream;

/**
 * Tree adaptor creating {@link ParseTree} nodes and error nodes. 
 * @author Arend Rensink
 * @version $Revision $
 */
public class ParseTreeAdaptor<T extends ParseTree<T,I>,I extends ParseInfo>
        extends CommonTreeAdaptor {
    /** 
     * Constructs an adaptor on the basis of a tree node prototype.
     * The token stream and parse info of the prototype will be passed on to the tree nodes.
     */
    public ParseTreeAdaptor(ParseTree<T,I> prototype) {
        this(prototype, prototype.getInfo(), prototype.getTokenStream());
    }

    /** 
     * Constructs an adaptor on the basis of a given tree node prototype and token stream.
     * The token stream is passed on to the created tree nodes, to be able
     * to generate more meaningful error messages.
     */
    public ParseTreeAdaptor(ParseTree<T,I> prototype, I info,
            CommonTokenStream tokenStream) {
        this.prototype = prototype.newNode(tokenStream, info);
    }

    @Override
    public T create(Token payload) {
        T result = this.prototype.newNode();
        result.token = payload;
        return result;
    }

    @Override
    public T errorNode(TokenStream input, Token start, Token stop,
            RecognitionException e) {
        T result = this.prototype.newNode();
        result.setErrorNode(start, stop, e);
        return result;
    }

    /** Creates a tree node stream based on this adaptor. */
    public TreeNodeStream createTreeNodeStream(Tree tree) {
        return new CommonTreeNodeStream(this, tree);
    }

    private final ParseTree<T,I> prototype;
}

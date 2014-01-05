package groove.util.antlr;

import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTreeAdaptor;

/**
 * Tree adaptor creating {@link ParseTree} nodes and error nodes. 
 * @author Arend Rensink
 * @version $Revision $
 */
class ParseTreeAdaptor<T extends ParseTree<T>> extends CommonTreeAdaptor {
    /** 
     * Constructs an adaptor on the basis of a given token stream.
     * The token stream is passed on to the tree nodes, to be able
     * to generate more meaningful error messages.
     */
    ParseTreeAdaptor(ParseTree<T> prototype, CommonTokenStream tokenStream) {
        this.prototype = prototype.newNode(tokenStream);
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

    private final ParseTree<T> prototype;
}

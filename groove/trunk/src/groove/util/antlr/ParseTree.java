package groove.util.antlr;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.Parser;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonErrorNode;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.TreeAdaptor;

/**
 * Dedicated parse tree with the ability to reconstruct
 * the parsed input string.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class ParseTree<T extends ParseTree<T>> extends CommonTree {
    /** Empty constructor for subclassing. */
    protected ParseTree() {
        // empty
    }

    /**
     * Creates a new node of the same type and with the
     * same token stream as this tree.
     */
    public T newNode() {
        return newNode(this.tokenStream);
    }

    /**
     * Creates a new node of the same type as this tree,
     * using a given token stream.
     */
    @SuppressWarnings("unchecked")
    final T newNode(CommonTokenStream tokenStream) {
        T result = null;
        try {
            result = (T) getClass().newInstance();
            result.tokenStream = tokenStream;
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            // other exceptions should not occur and will be ignored
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    final public T dupNode() {
        T result = newNode();
        result.setNode((T) this);
        return result;
    }

    /* Overridden to specialise the type. */
    @SuppressWarnings("unchecked")
    @Override
    public T getChild(int i) {
        return (T) super.getChild(i);
    }

    /** Sets this tree to be a duplicate of another. */
    protected void setNode(T node) {
        this.token = node.token;
        this.startIndex = node.startIndex;
        this.stopIndex = node.stopIndex;
    }

    /** Returns the token stream with which this tree has been initialised. */
    protected CommonTokenStream getTokenStream() {
        return this.tokenStream;
    }

    /** The token stream from which this tree has been created. */
    private CommonTokenStream tokenStream;

    /** 
     * Returns the part of the input token stream corresponding to this tree.
     * This is determined by the token numbers of the first and last tokens. 
     */
    public String toInputString() {
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
            Token childLast = getChild(i).findLastToken();
            result = getMax(result, childLast);
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

    /** Creates a parser for a given term, generating trees of this kind. */
    public Parser createParser(String term) {
        try {
            Lexer lexer = createLexer(term);
            // instantiate the parser
            Class<? extends Parser> parserType = getParserType();
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            Constructor<? extends Parser> parserConstructor =
                parserType.getConstructor(TokenStream.class);
            Parser result = parserConstructor.newInstance(tokenStream);
            Method adaptorSetter =
                parserType.getMethod("setTreeAdaptor", TreeAdaptor.class);
            adaptorSetter.invoke(result, new ParseTreeAdaptor<T>(this,
                tokenStream));
            return result;
        } catch (Exception e) {
            throw toRuntime(e);
        }
    }

    /** Factory method for a lexer generating this kind of tree. */
    protected Lexer createLexer(String term) {
        try {
            // find the lexer type
            String parserName = getParserType().getName();
            String lexerName =
                parserName.substring(0, parserName.indexOf("Parser")).concat(
                    "Lexer");
            @SuppressWarnings("unchecked")
            Class<? extends Lexer> lexerType =
                (Class<? extends Lexer>) Class.forName(lexerName);
            // instantiate the lexer
            ANTLRStringStream input = new ANTLRStringStream(term);
            Constructor<? extends Lexer> lexerConstructor =
                lexerType.getConstructor(CharStream.class);
            return lexerConstructor.newInstance(input);
        } catch (Exception e) {
            throw toRuntime(e);
        }
    }

    /** Returns the parser type for this kind of tree. */
    abstract protected Class<? extends Parser> getParserType();

    /** Throws a given exception as a runtime exception. */
    private RuntimeException toRuntime(Exception exc) {
        if (exc instanceof RuntimeException) {
            return (RuntimeException) exc;
        } else {
            return new IllegalStateException(exc);
        }
    }

    /** Changes this tree node into an error node. */
    void setErrorNode(Token start, Token stop, RecognitionException e) {
        this.delegate = new CommonErrorNode(this.tokenStream, start, stop, e);
    }

    /* Overwritten to account for error node behaviour. */
    @Override
    public boolean isNil() {
        if (isErrorNode()) {
            return getDelegate().isNil();
        } else {
            return super.isNil();
        }
    }

    /* Overwritten to account for error node behaviour. */
    @Override
    public int getType() {
        if (isErrorNode()) {
            return getDelegate().getType();
        } else {
            return super.getType();
        }
    }

    /* Overwritten to account for error node behaviour. */
    @Override
    public String getText() {
        if (isErrorNode()) {
            return getDelegate().getText();
        } else {
            return super.getText();
        }
    }

    /* Overwritten to account for error node behaviour. */
    @Override
    public String toString() {
        if (isErrorNode()) {
            return getDelegate().toString();
        } else {
            return super.toString();
        }
    }

    private boolean isErrorNode() {
        return getDelegate() != null;
    }

    private CommonErrorNode getDelegate() {
        return this.delegate;
    }

    private CommonErrorNode delegate;
}

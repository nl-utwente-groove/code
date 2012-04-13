/**
 * 
 */
package groove.util;

import groove.algebra.StringAlgebra;
import groove.graph.DefaultGraph;
import groove.graph.DefaultLabel;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.TypeGraph;
import groove.graph.TypeNode;
import groove.graph.algebra.ValueNode;
import groove.view.FormatException;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.antlr.runtime.Parser;
import org.antlr.runtime.tree.CommonTree;

/**
 * Objects of this class can construct instance graphs and a type graph
 * for the ASTs of a particular Antlr parser.
 * @author Arend Rensink
 * @version $Revision$
 */
public class AntlrGrapher {
    /** 
     * Constructs an grapher for a particular Antlr parser.
     * @param parser the parser class that this object should construct graphs for
     * @param textTypes the token types for which the text should be stored 
     * in the graph (as string attributes)
     * @throws IllegalArgumentException if the parser class doesn't define an accessible
     * static array {@code String[] tokenNames}, or the value of one of
     * the {@code textTypes} is not a valid index in this array
     */
    public AntlrGrapher(Class<? extends Parser> parser, int... textTypes)
        throws IllegalArgumentException {
        try {
            this.tokens = (String[]) parser.getField(TOKEN_NAMES).get(null);
        } catch (SecurityException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(e);
        }
        this.textTypes = new BitSet(this.tokens.length);
        for (int type : textTypes) {
            if (type < 0 || type > this.tokens.length) {
                throw new IllegalArgumentException(String.format(
                    "Token type %d does not exist in parser class %s", type,
                    parser));
            } else {
                this.textTypes.set(type);
            }
        }
    }

    /** Returns the type graph for this parser. */
    public TypeGraph getType() {
        TypeGraph result = new TypeGraph();
        TypeNode topNode = result.addNode(TOP_TYPE);
        result.addEdge(topNode, CHILD_LABEL, topNode);
        result.addEdge(topNode, NEXT_LABEL, topNode);
        result.addEdge(topNode, FIRST_FLAG, topNode);
        result.addEdge(topNode, LAST_FLAG, topNode);
        result.addEdge(topNode, LEAF_FLAG, topNode);
        Node stringNode = result.addNode(STRING_TYPE);
        for (int i = 0; i < this.tokens.length; i++) {
            String token = this.tokens[i];
            if (ExprParser.isIdentifier(token)) {
                Label typeLabel =
                    DefaultLabel.createLabel(token, Label.NODE_TYPE);
                TypeNode tokenNode = result.addNode(typeLabel);
                try {
                    result.addSubtype(topNode, tokenNode);
                } catch (FormatException e) {
                    assert false;
                }
                if (this.textTypes.get(i)) {
                    result.addEdge(tokenNode, TEXT_LABEL, stringNode);
                }
            }
        }
        return result;
    }

    /** Returns the graph representing a given AST. */
    public Graph getGraph(CommonTree tree) {
        Graph result = new DefaultGraph();
        Map<CommonTree,Node> treeNodeMap = new HashMap<CommonTree,Node>();
        treeNodeMap.put(tree, createNode(result, tree));
        Set<CommonTree> pool = new HashSet<CommonTree>();
        pool.add(tree);
        while (!pool.isEmpty()) {
            CommonTree next = pool.iterator().next();
            assert next != null;
            pool.remove(next);
            Node nextNode = treeNodeMap.get(next);
            Node prevChild = null;
            for (int i = 0; i < next.getChildCount(); i++) {
                CommonTree child = (CommonTree) next.getChild(i);
                Node childNode = createNode(result, child);
                treeNodeMap.put(child, childNode);
                result.addEdge(nextNode, CHILD_LABEL, childNode);
                if (prevChild == null) {
                    result.addEdge(childNode, FIRST_FLAG, childNode);
                } else {
                    result.addEdge(prevChild, NEXT_LABEL, childNode);
                }
                pool.add(child);
                prevChild = childNode;
            }
            if (prevChild == null) {
                result.addEdge(nextNode, LEAF_FLAG, nextNode);
            } else {
                result.addEdge(prevChild, LAST_FLAG, prevChild);
            }
        }
        return result;
    }

    private Node createNode(Graph graph, CommonTree tree) {
        Node result = graph.addNode();
        int tokenType = tree.getType();
        graph.addEdge(result, DefaultLabel.createLabel(this.tokens[tokenType],
            Label.NODE_TYPE), result);
        if (this.textTypes.get(tokenType) && tree.getText() != null) {
            ValueNode nameNode =
                ValueNode.createValueNode(StringAlgebra.instance,
                    tree.getText());
            graph.addEdge(result, TEXT_LABEL, nameNode);
        }
        return result;
    }

    /** List of token names. */
    private final String[] tokens;
    /** Set of token types for which the text representation should be stored
     * in the graph as a string attribute.
     */
    private final BitSet textTypes;

    private static final String TOKEN_NAMES = "tokenNames";

    /** Default label to be used for child edges. */
    public final static Label CHILD_LABEL = DefaultLabel.createLabel("child");
    /** Default label to be used for next edges. */
    public final static Label NEXT_LABEL = DefaultLabel.createLabel("next");
    /** Default label to be used for text edges. */
    public final static Label TEXT_LABEL = DefaultLabel.createLabel("text");
    /** Flag to be used for the first child. */
    public final static Label FIRST_FLAG =
        DefaultLabel.createLabel("first", Label.FLAG);
    /** Flag to be used for the last child. */
    public final static Label LAST_FLAG =
        DefaultLabel.createLabel("last", Label.FLAG);
    /** Flag to be used for a childless token node. */
    public final static Label LEAF_FLAG =
        DefaultLabel.createLabel("leaf", Label.FLAG);
    /** Type of the (abstract) top node. */
    public final static Label TOP_TYPE =
        DefaultLabel.createLabel("TOP$", Label.NODE_TYPE);
    /** String type label. */
    private final static Label STRING_TYPE =
        DefaultLabel.createLabel("string", Label.NODE_TYPE);
    /** Subtype edge label. */
}
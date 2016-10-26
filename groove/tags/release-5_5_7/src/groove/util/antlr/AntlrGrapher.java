/**
 *
 */
package groove.util.antlr;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.antlr.runtime.Parser;
import org.antlr.runtime.tree.CommonTree;

import groove.algebra.JavaStringAlgebra;
import groove.grammar.host.DefaultHostGraph;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.grammar.model.ResourceKind;
import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeLabel;
import groove.grammar.type.TypeNode;
import groove.graph.EdgeRole;
import groove.util.parse.FormatException;
import groove.util.parse.StringHandler;

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
            this.tokens = (String[]) parser.getField(TOKEN_NAMES)
                .get(null);
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
                throw new IllegalArgumentException(
                    String.format("Token type %d does not exist in parser class %s", type, parser));
            } else {
                this.textTypes.set(type);
            }
        }
    }

    /** Returns the type graph for this parser. */
    public TypeGraph getType() {
        TypeGraph result = new TypeGraph(ResourceKind.TYPE.getDefaultName()
            .get());
        TypeNode topNode = result.addNode(TOP_TYPE);
        result.addEdge(topNode, CHILD_LABEL, topNode);
        result.addEdge(topNode, NEXT_LABEL, topNode);
        result.addEdge(topNode, FIRST_FLAG, topNode);
        result.addEdge(topNode, LAST_FLAG, topNode);
        result.addEdge(topNode, LEAF_FLAG, topNode);
        TypeNode stringNode = result.addNode(STRING_TYPE);
        for (int i = 0; i < this.tokens.length; i++) {
            String token = this.tokens[i];
            if (StringHandler.isIdentifier(token)) {
                TypeLabel typeLabel = TypeLabel.createLabel(EdgeRole.NODE_TYPE, token);
                TypeNode tokenNode = result.addNode(typeLabel);
                try {
                    result.addInheritance(tokenNode, topNode);
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
    public HostGraph getGraph(CommonTree tree) {
        DefaultHostGraph result = new DefaultHostGraph("ast");
        Map<CommonTree,HostNode> treeNodeMap = new HashMap<>();
        treeNodeMap.put(tree, createNode(result, tree));
        Set<CommonTree> pool = new HashSet<>();
        pool.add(tree);
        while (!pool.isEmpty()) {
            CommonTree next = pool.iterator()
                .next();
            assert next != null;
            pool.remove(next);
            HostNode nextNode = treeNodeMap.get(next);
            HostNode prevChild = null;
            for (int i = 0; i < next.getChildCount(); i++) {
                CommonTree child = (CommonTree) next.getChild(i);
                HostNode childNode = createNode(result, child);
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

    private HostNode createNode(DefaultHostGraph graph, CommonTree tree) {
        HostNode result = graph.addNode();
        int tokenType = tree.getType();
        graph.addEdge(result,
            TypeLabel.createLabel(EdgeRole.NODE_TYPE, this.tokens[tokenType]),
            result);
        if (this.textTypes.get(tokenType) && tree.getText() != null) {
            HostNode nameNode = graph.addNode(JavaStringAlgebra.instance, tree.getText());
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
    public final static TypeLabel CHILD_LABEL = TypeLabel.createBinaryLabel("child");
    /** Default label to be used for next edges. */
    public final static TypeLabel NEXT_LABEL = TypeLabel.createBinaryLabel("next");
    /** Default label to be used for text edges. */
    public final static TypeLabel TEXT_LABEL = TypeLabel.createBinaryLabel("text");
    /** Flag to be used for the first child. */
    public final static TypeLabel FIRST_FLAG = TypeLabel.createLabel(EdgeRole.FLAG, "first");
    /** Flag to be used for the last child. */
    public final static TypeLabel LAST_FLAG = TypeLabel.createLabel(EdgeRole.FLAG, "last");
    /** Flag to be used for a childless token node. */
    public final static TypeLabel LEAF_FLAG = TypeLabel.createLabel(EdgeRole.FLAG, "leaf");
    /** Type of the (abstract) top node. */
    public final static TypeLabel TOP_TYPE = TypeLabel.createLabel(EdgeRole.NODE_TYPE, "TOP$");
    /** String type label. */
    private final static TypeLabel STRING_TYPE =
        TypeLabel.createLabel(EdgeRole.NODE_TYPE, "string");
    /** Subtype edge label. */
}

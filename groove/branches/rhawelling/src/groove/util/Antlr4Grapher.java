/**
 * 
 */
package groove.util;

import groove.algebra.JavaStringAlgebra;
import groove.grammar.host.DefaultHostGraph;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.grammar.model.FormatException;
import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeLabel;
import groove.grammar.type.TypeNode;
import groove.graph.EdgeRole;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * Objects of this class can construct instance graphs and a type graph
 * for the parse trees of a particular Antlr 4 parser.
 * @author Arend Rensink
 * @version $Revision$
 */
public class Antlr4Grapher {
    /** 
     * Constructs an grapher for a particular Antlr 4 parser.
     * @param parser the parser class that this object should construct graphs for
     * @param textTypes the token types for which the text should be stored 
     * in the graph (as string attributes)
     * @throws IllegalArgumentException if the parser class doesn't define 
     * the expected static fields, or the value of one of
     * the {@code textTypes} is not a valid token index
     */
    public Antlr4Grapher(Class<? extends Parser> parser, int... textTypes)
        throws IllegalArgumentException {
        this.tokenNames = extractTokenNames(parser);
        this.ruleNames = extractRuleNames(parser);
        this.textTypes = new BitSet(this.tokenNames.size());
        for (int type : textTypes) {
            if (!this.tokenNames.containsKey(type)) {
                throw new IllegalArgumentException(String.format(
                    "Token type %d does not exist in parser class %s", type,
                    parser));
            } else {
                this.textTypes.set(type);
            }
        }
    }

    /** 
     * Extracts map from token numbers to symbolic token names 
     * by reflection from the parser class file.
     */
    private Map<Integer,String> extractTokenNames(Class<? extends Parser> parser) {
        Map<Integer,String> result = new HashMap<Integer,String>();
        for (Field field : parser.getDeclaredFields()) {
            String fieldName = field.getName();
            if (fieldName.equals("tokenNames")) {
                // after this field the generated parser contains no more tokens
                break;
            }
            int modifiers = field.getModifiers();
            if (!Modifier.isStatic(modifiers)) {
                continue;
            }
            if (!Modifier.isPublic(modifiers)) {
                continue;
            }
            if (!Modifier.isFinal(modifiers)) {
                continue;
            }
            if (field.getType() != int.class) {
                continue;
            }
            try {
                Integer index = (Integer) field.get(null);
                result.put(index, fieldName);
            } catch (IllegalAccessException exc) {
                throw new IllegalArgumentException(exc);
            }
        }
        return result;
    }

    /** Extracts the set of symbolic rule names by reflection from the parser class file. */
    private Set<String> extractRuleNames(Class<? extends Parser> parser) {
        Set<String> result = new HashSet<String>();
        for (Class<?> claz : parser.getDeclaredClasses()) {
            int modifiers = claz.getModifiers();
            if (!Modifier.isStatic(modifiers)) {
                continue;
            }
            if (!Modifier.isPublic(modifiers)) {
                continue;
            }
            String name = toRuleName(claz);
            if (name != null) {
                result.add(name);
            }
        }
        return result;
    }

    /** Extracts a rule name from a class, if the class name ends on {@link #RULE_SUFFIX}. */
    private String toRuleName(Class<?> claz) {
        String result = null;
        String name = claz.getSimpleName();
        if (name.endsWith(RULE_SUFFIX)) {
            result = name.substring(0, name.length() - RULE_SUFFIX.length());
        }
        return result;

    }

    /** Returns the type graph for this parser. */
    public TypeGraph getType() {
        TypeGraph result = new TypeGraph("type");
        TypeNode topNode = result.addNode(TOP_TYPE);
        result.addEdge(topNode, CHILD_LABEL, topNode);
        result.addEdge(topNode, NEXT_LABEL, topNode);
        result.addEdge(topNode, FIRST_FLAG, topNode);
        result.addEdge(topNode, LAST_FLAG, topNode);
        result.addEdge(topNode, LEAF_FLAG, topNode);
        TypeNode stringNode = result.addNode(STRING_TYPE);
        for (Map.Entry<Integer,String> tokenEntry : this.tokenNames.entrySet()) {
            String token = tokenEntry.getValue();
            if (ExprParser.isIdentifier(token)) {
                TypeNode tokenNode = addType(result, topNode, token);
                if (this.textTypes.get(tokenEntry.getKey())) {
                    result.addEdge(tokenNode, TEXT_LABEL, stringNode);
                }
            }
        }
        for (String rule : this.ruleNames) {
            addType(result, topNode, rule);
        }
        return result;
    }

    /**
     * Adds a type node to a given type graph, and makes it a subtype of an
     * already existing type node.
     */
    private TypeNode addType(TypeGraph typeGraph, TypeNode topNode, String name) {
        TypeLabel typeLabel = TypeLabel.createLabel(EdgeRole.NODE_TYPE, name);
        TypeNode result = typeGraph.addNode(typeLabel);
        try {
            typeGraph.addInheritance(result, topNode);
        } catch (FormatException e) {
            assert false;
        }
        return result;
    }

    /** Returns the graph representing a given AST. */
    public HostGraph getGraph(ParseTree tree) {
        DefaultHostGraph result = new DefaultHostGraph("ast");
        Map<ParseTree,HostNode> treeNodeMap = new HashMap<ParseTree,HostNode>();
        treeNodeMap.put(tree, createNode(result, tree));
        Set<ParseTree> pool = new HashSet<ParseTree>();
        pool.add(tree);
        while (!pool.isEmpty()) {
            ParseTree next = pool.iterator().next();
            assert next != null;
            pool.remove(next);
            HostNode nextNode = treeNodeMap.get(next);
            HostNode prevChild = null;
            for (int i = 0; i < next.getChildCount(); i++) {
                ParseTree child = next.getChild(i);
                HostNode childNode = createNode(result, child);
                if (childNode == null) {
                    continue;
                }
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

    private HostNode createNode(DefaultHostGraph graph, ParseTree tree) {
        HostNode result = null;
        String type;
        String text = null;
        Object payload = tree.getPayload();
        if (payload instanceof Token) {
            // this is a leaf node; only include if it has text
            int tokenType = ((Token) payload).getType();
            type = this.tokenNames.get(tokenType);
            if (this.textTypes.get(tokenType)) {
                text = tree.getText();
            }
        } else {
            type = toRuleName(payload.getClass());
        }
        if (type != null) {
            // create the node and type it
            result = graph.addNode();
            graph.addEdge(result,
                TypeLabel.createLabel(EdgeRole.NODE_TYPE, type), result);
            // give it a text attribute if appropriate
            if (text != null) {
                HostNode nameNode =
                    graph.addNode(JavaStringAlgebra.instance, tree.getText());
                graph.addEdge(result, TEXT_LABEL, nameNode);
            }
        }
        return result;
    }

    /** List of token names. */
    private final Map<Integer,String> tokenNames;
    /** List of rule names. */
    private final Set<String> ruleNames;
    /** Set of token types for which the text representation should be stored
     * in the graph as a string attribute.
     */
    private final BitSet textTypes;

    /** Default label to be used for child edges. */
    public final static TypeLabel CHILD_LABEL =
        TypeLabel.createBinaryLabel("child");
    /** Default label to be used for next edges. */
    public final static TypeLabel NEXT_LABEL =
        TypeLabel.createBinaryLabel("next");
    /** Default label to be used for text edges. */
    public final static TypeLabel TEXT_LABEL =
        TypeLabel.createBinaryLabel("text");
    /** Flag to be used for the first child. */
    public final static TypeLabel FIRST_FLAG = TypeLabel.createLabel(
        EdgeRole.FLAG, "first");
    /** Flag to be used for the last child. */
    public final static TypeLabel LAST_FLAG = TypeLabel.createLabel(
        EdgeRole.FLAG, "last");
    /** Flag to be used for a childless token node. */
    public final static TypeLabel LEAF_FLAG = TypeLabel.createLabel(
        EdgeRole.FLAG, "leaf");
    /** Type of the (abstract) top node. */
    public final static TypeLabel TOP_TYPE = TypeLabel.createLabel(
        EdgeRole.NODE_TYPE, "TOP$");
    /** String type label. */
    private final static TypeLabel STRING_TYPE = TypeLabel.createLabel(
        EdgeRole.NODE_TYPE, "string");
    private final static String RULE_SUFFIX = "Context";
}

/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: AspectGraph.java,v 1.16 2008-02-29 11:02:22 fladder Exp $
 */
package groove.view.aspect;

import groove.graph.DefaultLabel;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.GraphShape;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.graph.NodeSetEdgeSetGraph;
import groove.rel.RegExprLabel;
import groove.trans.SystemProperties;
import groove.util.Groove;
import groove.view.AspectualGraphView;
import groove.view.DefaultTypeView;
import groove.view.FormatError;
import groove.view.FormatException;
import groove.view.GraphView;
import groove.view.NewRuleView;
import groove.view.RuleView;
import groove.view.TypeView;
import groove.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Graph implementation to convert from a label prefix representation of an
 * aspect graph to a graph where the aspect values are stored in
 * {@link AspectNode}s and {@link AspectEdge}s.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AspectGraph extends NodeSetEdgeSetGraph {
    // /**
    // * Constructor that returns an empty graph.
    // */
    // public AspectGraph() {
    // this.errors = Collections.emptyList();
    // }

    /**
     * Specialises the return type.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Set<AspectEdge> edgeSet() {
        return (Set<AspectEdge>) super.edgeSet();
    }

    /**
     * Specialises the return type.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Set<AspectEdge> edgeSet(Node node) {
        return (Set<AspectEdge>) super.edgeSet(node);
    }

    /**
     * Specialises the return type.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Set<AspectEdge> edgeSet(Node node, int end) {
        return (Set<AspectEdge>) super.edgeSet(node, end);
    }

    /**
     * Specialises the return type.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Set<AspectEdge> outEdgeSet(Node node) {
        return (Set<AspectEdge>) super.outEdgeSet(node);
    }

    /**
     * Specialises the return type.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Set<AspectNode> nodeSet() {
        return (Set<AspectNode>) super.nodeSet();
    }

    /**
     * Returns the list of format errors in this graph. If the list is empty,
     * the graph has no errors.
     * @return a possibly empty, non-<code>null</code> list of format errors in
     *         this aspect graph
     */
    public List<FormatError> getErrors() {
        List<FormatError> result;
        if (getInfo() == null) {
            result = Collections.<FormatError>emptyList();
        } else {
            result = getInfo().getErrors();
            if (result == null) {
                result = Collections.<FormatError>emptyList();
            }
        }
        return result;
    }

    /**
     * Indicates if this aspect graph has format errors. Convenience method for
     * <code>! getErrors().isEmpty()</code>.
     * @return <code>true</code> if this aspect graph has format errors
     */
    public boolean hasErrors() {
        return !getErrors().isEmpty();
    }

    /** Sets the list of errors to a copy of a given list. */
    private void addErrors(List<FormatError> errors) {
        List<FormatError> newErrors = new ArrayList<FormatError>();
        if (GraphInfo.hasErrors(this)) {
            newErrors.addAll(GraphInfo.getErrors(this));
        }
        if (errors != null) {
            newErrors.addAll(errors);
        }
        Collections.sort(newErrors);
        GraphInfo.setErrors(this, newErrors);
    }

    /**
     * Method that returns an {@link AspectGraph} based on a graph whose edges
     * are interpreted as aspect value prefixed. This means that nodes with
     * self-edges that have no text (apart from their aspect prefixes) are
     * treated as indicating the node aspect. The method never throws an
     * exception, but the resulting graph may have format errors, reported in
     * {@link #getErrors()}.
     * @param graph the graph to take as input.
     * @return an aspect graph whose format errors are recorded in
     *         {@link #getErrors()}
     */
    public AspectGraph fromPlainGraph(GraphShape graph) {
        // map from original graph elements to aspect graph elements
        NodeEdgeMap elementMap = new NodeEdgeHashMap();
        return fromPlainGraph(graph, elementMap);
    }

    /**
     * Method that returns an {@link AspectGraph} based on a graph whose edges
     * are interpreted as aspect value prefixed. This means that nodes with
     * self-edges that have no text (apart from their aspect prefixes) are
     * treated as indicating the node aspect. The mapping from the old to the
     * new graph is stored in a parameter. The method never throws an exception,
     * but the resulting graph may have format errors, reported in
     * {@link #getErrors()} as well as in the graph errors of the result.
     * @param graph the graph to take as input.
     * @param elementMap output parameter for mapping from plain graph elements
     *        to resulting {@link AspectGraph} elements; should be initially
     *        empty
     */
    public AspectGraph fromPlainGraph(GraphShape graph, NodeEdgeMap elementMap) {
        AspectGraph result = new AspectGraph();
        // we set the role now, because some of the aspects may depend on it
        GraphInfo.setRole(result, GraphInfo.getRole(graph));
        List<FormatError> errors = new ArrayList<FormatError>();
        assert elementMap != null && elementMap.isEmpty();
        // first do the nodes;
        // map from original graph nodes to aspect graph nodes
        Map<Node,AspectNode> nodeMap = new HashMap<Node,AspectNode>();
        for (Node node : graph.nodeSet()) {
            AspectNode nodeImage = createAspectNode(node.getNumber());
            result.addNode(nodeImage);
            // update the maps
            nodeMap.put(node, nodeImage);
            elementMap.putNode(node, nodeImage);
        }
        // look for node aspect indicators
        // and put all correct aspect vales in a map
        Map<Edge,AspectMap> edgeDataMap = new HashMap<Edge,AspectMap>();
        for (Edge edge : graph.edgeSet()) {
            try {
                AspectNode sourceImage = nodeMap.get(edge.source());
                AspectNode targetImage = nodeMap.get(edge.opposite());
                String labelText = edge.label().text();
                AspectValue nodeValue = null;
                try {
                    nodeValue = getNodeValue(edge, getAspectParser(graph));
                } catch (FormatException e) {
                    throw e.extend(sourceImage);
                }
                if (nodeValue != null) {
                    // the edge encodes a node aspect
                    sourceImage.addDeclaredValue(nodeValue);
                } else {
                    AspectMap aspectMap =
                        getAspectParser(graph).parse(labelText);
                    edgeDataMap.put(edge, aspectMap);
                    // add inferred aspect values to the source and target
                    for (AspectValue edgeValue : aspectMap.getDeclaredValues()) {
                        AspectValue sourceValue = edgeValue.edgeToSource();
                        if (sourceValue != null) {
                            sourceImage.addInferredValue(sourceValue);
                        }
                        AspectValue targetValue = edgeValue.edgeToTarget();
                        if (targetValue != null) {
                            targetImage.addInferredValue(targetValue);
                        }
                    }
                }
            } catch (FormatException e) {
                errors.addAll(e.getErrors());
            }
        }
        // Now iterate over the remaining edges
        for (Map.Entry<Edge,AspectMap> entry : edgeDataMap.entrySet()) {
            Edge edge = entry.getKey();
            try {
                AspectEdge edgeImage =
                    createAspectEdge(nodeMap.get(edge.source()),
                        nodeMap.get(edge.opposite()), entry.getValue());
                result.addEdge(edgeImage);
                elementMap.putEdge(edge, edgeImage);
                edgeImage.initAspects();
            } catch (FormatException e) {
                errors.addAll(e.getErrors());
            }
        }
        result.checkAspects(errors);
        GraphInfo.transfer(graph, result, elementMap);
        result.addErrors(errors);
        result.setFixed();
        return result;
    }

    /**
     * Checks this aspect graph for aspect errors.
     * @see Aspect#checkNode(AspectNode, AspectGraph)
     * @see Aspect#checkEdge(AspectEdge, AspectGraph)
     */
    private List<FormatError> checkAspects(List<FormatError> errors) {
        // now test all nodes and edges for context correctness w.r.t. all their
        // aspect values
        for (AspectNode node : nodeSet()) {
            try {
                for (AspectValue value : node.getAspectMap()) {
                    value.getAspect().checkNode(node, this);
                }
            } catch (FormatException exc) {
                errors.addAll(exc.getErrors());
            }
        }
        for (AspectEdge edge : edgeSet()) {
            try {
                for (AspectValue value : edge.getAspectMap()) {
                    value.getAspect().checkEdge(edge, this);
                }
            } catch (FormatException exc) {
                errors.addAll(exc.getErrors());
            }
        }
        return errors;
    }

    /**
     * Returns the correct aspect parser for a given graph. This may take
     * version information into account.
     */
    private AspectParser getAspectParser(GraphShape graph) {
        return new AspectParser(graph);
    }

    /**
     * Creates a graph where the aspect values are represented as label prefixes
     * for the edges, and as special edges for the nodes.
     */
    public Graph toPlainGraph() {
        NodeEdgeMap elementMap = new NodeEdgeHashMap();
        return toPlainGraph(elementMap);
    }

    /**
     * Creates a graph where the aspect values are represented as label prefixes
     * for the edges, and as special edges for the nodes. The mapping from the
     * old to the new graph is stored in a parameter.
     * @param elementMap output parameter for mapping from plain graph elements
     *        to resulting {@link AspectGraph} elements; should be initially
     *        empty
     */
    private Graph toPlainGraph(NodeEdgeMap elementMap) {
        Graph result = createPlainGraph();
        for (AspectNode node : nodeSet()) {
            Node nodeImage = addFreshNode(result, node);
            elementMap.putNode(node, nodeImage);
            for (AspectValue value : node.getDeclaredValues()) {
                result.addEdge(nodeImage,
                    createLabel(AspectParser.toString(value)), nodeImage);
            }
        }
        for (AspectEdge edge : edgeSet()) {
            Node[] nodeImages = new Node[edge.endCount()];
            for (int i = 0; i < edge.endCount(); i++) {
                nodeImages[i] = elementMap.getNode(edge.end(i));
            }
            Edge edgeImage =
                result.addEdge(nodeImages, createLabel(edge.getPlainText()));
            elementMap.putEdge(edge, edgeImage);
        }
        GraphInfo.transfer(this, result, elementMap);
        return result;
    }

    /**
     * Adds a fresh node to a given graph, taking the node identity from an
     * existing aspect node.
     */
    private Node addFreshNode(Graph graph, AspectNode node) {
        Node result = DefaultNode.createNode(node.getNumber());
        boolean fresh = graph.addNode(result);
        assert fresh : String.format("Node '%s' is not fresh in graph '%s'",
            node, graph);
        return result;
    }

    /**
     * Tests if a given edge encodes a node aspect value, and returns that
     * value. An edge encodes a node aspect value if it has no text of its own.
     * Returns <code>null</code> if the edge does not encode a node aspect
     * value, and throws an exception if the edge is not a self-edge or contains
     * more than one aspect value.
     * @param edge the edge to be tested
     * @param parser
     * @return a node aspect value for the (unique) endpoint of the edge, or
     *         <code>null</code> if <code>edge</code> does not encode a node
     *         aspect value.
     * @throws FormatException if <code>edge</code> does encode a node aspect
     *         value, but is not a self-edge or contains more than one aspect
     *         value
     */
    private AspectValue getNodeValue(Edge edge, AspectParser parser)
        throws FormatException {
        AspectValue result = null;
        String labelText = edge.label().text();
        AspectMap aspectMap = parser.parse(labelText);
        if (aspectMap.getText() == null) {
            // this edge is empty or indicates a node aspect
            if (edge.opposite() != edge.source()) {
                throw new FormatException("Label '%s' only allowed on nodes",
                    labelText);
            } else if (aspectMap.size() > 1) {
                // Only one aspect value per node self-edge
                throw new FormatException(
                    "Multiple node aspect values in '%s'", labelText);
            } else {
                // add the aspect value found
                result = aspectMap.iterator().next();
                if (!result.isNodeValue()) {
                    throw new FormatException(
                        "Aspect value '%s' is for edges only", result);
                }
            }
        }
        return result;
    }

    /**
     * Factory method for a <code>Graph</code>.
     * @see #toPlainGraph()
     */
    private Graph createPlainGraph() {
        return new NodeSetEdgeSetGraph();
    }

    /**
     * Factory method for an <code>AspectNode</code>.
     */
    @Override
    public AspectNode createNode() {
        return new AspectNode(getNodeCounter().getNumber());
    }

    /** Factory method for an aspect node with a given number. */
    private AspectNode createAspectNode(int nr) {
        return new AspectNode(nr);
    }

    /**
     * Factory method for an {@link AspectEdge}.
     * @throws FormatException if the aspect label is inconsistent with the end
     *         node aspect values
     */
    private AspectEdge createAspectEdge(AspectNode source, AspectNode target,
            AspectMap aspectData) throws FormatException {
        return new AspectEdge(source, target, aspectData);
    }

    /**
     * Creates a label from a string.
     * @see DefaultLabel#createLabel(String)
     */
    private Label createLabel(String text) {
        return DefaultLabel.createLabel(text);
    }

    /** 
     * Returns a new aspect graph obtained from this one
     * by renumbering the nodes in a consecutive sequence starting from {@code 0}
     */
    public AspectGraph renumber() {
        AspectGraph result = this;
        // renumber the nodes in their original order
        SortedSet<AspectNode> nodes = new TreeSet<AspectNode>(nodeSet());
        if (!nodes.isEmpty() && nodes.last().getNumber() != nodeCount() - 1) {
            try {
                result = new AspectGraph();
                NodeEdgeMap elementMap = new NodeEdgeHashMap();
                int nodeNr = 0;
                for (AspectNode node : nodes) {
                    AspectNode image = new AspectNode(nodeNr);
                    for (AspectValue value : node.getAspectMap().getDeclaredValues()) {
                        image.addDeclaredValue(value);
                    }
                    for (AspectValue value : node.getAspectMap().getInferredValues()) {
                        image.addInferredValue(value);
                    }
                    result.addNode(image);
                    elementMap.putNode(node, image);
                    nodeNr++;
                }
                for (AspectEdge edge : edgeSet()) {
                    AspectEdge image =
                        createAspectEdge(
                            (AspectNode) elementMap.getNode(edge.source()),
                            (AspectNode) elementMap.getNode(edge.opposite()),
                            edge.getAspectMap());
                    result.addEdge(image);
                    elementMap.putEdge(edge, image);
                }
                GraphInfo.transfer(this, result, elementMap);
            } catch (FormatException exc) {
                assert false : String.format("Exception when renumbering: %s",
                    exc.getMessage());
            }
        }
        return result;
    }

    /**
     * Returns an aspect graph obtained from this one by changing all
     * occurrences of a certain label into another.
     * @param oldLabel the label to be changed
     * @param newLabel the new value for {@code oldLabel}
     * @return a clone of this aspect graph with changed labels, or this graph
     *         if {@code oldLabel} did not occur
     */
    public AspectGraph relabel(Label oldLabel, Label newLabel) {
        AspectGraph result = clone();
        Map<AspectEdge,AspectEdge> oldToNew =
            new HashMap<AspectEdge,AspectEdge>();
        for (AspectEdge edge : result.edgeSet()) {
            try {
                Label label = edge.getModelLabel();
                Label replacement = null;
                if (label instanceof DefaultLabel) {
                    if (label.equals(oldLabel)) {
                        replacement = newLabel;
                    }
                } else if (label instanceof RegExprLabel) {
                    replacement =
                        ((RegExprLabel) label).getRegExpr().relabel(oldLabel,
                            newLabel).toLabel();
                }
                if (replacement != null) {
                    AspectMap newData = edge.getAspectMap().clone();
                    newData.remove(TypeAspect.getInstance());
                    if (replacement.isNodeType()) {
                        newData.addDeclaredValue(TypeAspect.NODE_TYPE);
                    } else if (replacement.isFlag()) {
                        newData.addDeclaredValue(TypeAspect.FLAG);
                    }
                    if (GraphInfo.hasRuleRole(this)) {
                        replacement =
                            RegExprLabelParser.getInstance(false).unparse(
                                replacement);
                    }
                    newData.setText(replacement.text());
                    oldToNew.put(edge, createAspectEdge(edge.source(),
                        edge.target(), newData));
                }
            } catch (FormatException exc) {
                // do nothing with this label
            }
        }
        if (oldToNew.isEmpty()) {
            return this;
        } else {
            result.removeEdgeSet(oldToNew.keySet());
            result.addEdgeSet(oldToNew.values());
            // get errors by converting to a plain graph and back
            result.addErrors(Collections.<FormatError>emptyList());
            result.addErrors(fromPlainGraph(result.toPlainGraph()).getErrors());
            return result;
        }
    }

    /** 
     * Method to make sure that the graph, rule or type view is reconstructed.
     * This is necessary after a change to the aspect graph properties, in particular the rule priority. 
     */
    public void invalidateView() {
        this.graphView = null;
        this.ruleView = null;
        this.typeView = null;
    }

    /**
     * Creates a graph view from this aspect graph. Further information for the
     * conversion is given through a properties object. The view object is
     * reused when possible.
     * @param properties the properties object with respect to which the graph
     *        is to be constructed
     * @return the resulting state graph view (non-null)
     * @throws IllegalStateException if the aspect graph role is not
     *         {@link Groove#GRAPH_ROLE}
     */
    public GraphView toGraphView(SystemProperties properties)
        throws IllegalStateException {
        if (!GraphInfo.hasGraphRole(this)) {
            throw new IllegalStateException(
                "Aspect graph does not represent a graph");
        }
        boolean refreshView = this.graphView == null;
        if (!refreshView) {
            String myName = GraphInfo.getName(this);
            String viewName = this.graphView.getName();
            refreshView =
                myName == null ? viewName != null : !myName.equals(viewName);
        }
        if (refreshView) {
            this.graphView = new AspectualGraphView(this, properties);
        } else {
            this.graphView.setProperties(properties);
        }
        return this.graphView;

    }

    /**
     * Creates a type view from this aspect graph. Further information for the
     * conversion is given through a properties object. The view object is
     * reused when possible.
     * @param properties the properties object with respect to which the type
     *        graph is to be constructed
     * @return the resulting type graph view (non-null)
     * @throws IllegalStateException if the aspect graph role is not
     *         {@link Groove#TYPE_ROLE}
     */
    public TypeView toTypeView(SystemProperties properties)
        throws IllegalStateException {
        if (!GraphInfo.hasTypeRole(this)) {
            throw new IllegalStateException(
                String.format("Aspect graph does not represent a type graph"));
        }
        boolean refreshView = this.typeView == null;
        if (!refreshView) {
            String myName = GraphInfo.getName(this);
            String viewName = this.typeView.getName();
            refreshView =
                myName == null ? viewName != null : !myName.equals(viewName);
        }
        if (refreshView) {
            this.typeView = new DefaultTypeView(this);
        }
        return this.typeView;
    }

    /**
     * Creates a rule view from this aspect graph. Further information for the
     * conversion is given through a properties object. The view object is
     * reused when possible.
     * @param properties the properties object with respect to which the rule is
     *        to be constructed
     * @return the resulting rule view (non-null)
     * @throws IllegalStateException if the aspect graph role is not
     *         {@link Groove#RULE_ROLE}
     */
    public RuleView toRuleView(SystemProperties properties)
        throws IllegalStateException {
        if (!GraphInfo.hasRuleRole(this)) {
            throw new IllegalStateException(
                "Aspect graph does not represent a rule graph");
        }
        boolean refreshView = this.ruleView == null;
        if (!refreshView) {
            String myName = GraphInfo.getName(this);
            String viewName = this.ruleView.getName();
            refreshView =
                myName == null ? viewName != null : !myName.equals(viewName);
        }
        if (refreshView) {
            this.ruleView = new NewRuleView(this, properties);
        } else {
            this.ruleView.setProperties(properties);
        }
        return this.ruleView;
    }

    /**
     * Creates a graph or rule view from this aspect graph, depending on the
     * role of the aspect graph.
     * @see #toGraphView(SystemProperties)
     * @see #toRuleView(SystemProperties)
     */
    public View<?> toView() {
        if (GraphInfo.hasRuleRole(this)) {
            return toRuleView(null);
        } else if (GraphInfo.hasTypeRole(this)) {
            return toTypeView(null);
        } else {
            return toGraphView(null);
        }
    }

    /** Auxiliary object for converting this aspect graph to a type graph. */
    private TypeView typeView;

    /** Auxiliary object for converting this aspect graph to a state graph. */
    private AspectualGraphView graphView;

    /** Auxiliary object for converting this aspect graph to a rule. */
    private RuleView ruleView;

    /**
     * Copies this aspect graph to one with the same nodes, edges and graph
     * info.
     */
    @Override
    public AspectGraph clone() {
        AspectGraph result = new AspectGraph();
        result.addNodeSet(nodeSet());
        result.addEdgeSetWithoutCheck(edgeSet());
        GraphInfo.transfer(this, result, null);
        result.addErrors(getErrors());
        return result;
    }

    //
    // /** Format errors in this aspect graph. */
    // private List<String> errors;

    /**
     * Returns a factory for {@link AspectGraph}s, i.e., an object to invoke
     * {@link #fromPlainGraph(GraphShape)} upon.
     */
    public static AspectGraph getFactory() {
        return factory;
    }

    /**
     * Creates an aspect graph from a given (plain) graph. Convenience method
     * for <code>getFactory().fromPlainGraph(GraphShape)</code>.
     * @param plainGraph the plain graph to convert; non-null
     * @return the resulting aspect graph; non-null
     * @see #fromPlainGraph(GraphShape)
     */
    public static AspectGraph newInstance(GraphShape plainGraph) {
        return getFactory().fromPlainGraph(plainGraph);
    }

    /**
     * Main method, taking a sequence of filenames and testing conversion from
     * plain to aspect graphs of the graphs contained in those files.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Call with sequence of files or directories");
        }
        for (String arg : args) {
            File file = new File(arg);
            if (!file.exists()) {
                System.err.printf("File %s cannot be found", arg);
            } else {
                try {
                    testFile(file);
                } catch (FormatException exc) {
                    exc.printStackTrace();
                }
            }
        }
    }

    /**
     * Loads a graph from a file and tests its conversion from plain to aspect
     * graph and back, using {@link #testTranslation(Graph)}. Recursively
     * descends into directories.
     */
    private static void testFile(File file) throws FormatException {
        if (file.isDirectory()) {
            for (File nestedFile : file.listFiles()) {
                testFile(nestedFile);
            }
        } else {
            try {
                Graph plainGraph = Groove.loadGraph(file);
                if (plainGraph != null) {
                    System.out.printf("Testing %s", file);
                    testTranslation(plainGraph);
                    System.out.println(" - OK");
                }
            } catch (Exception exc) {
                // do nothing (skip)
            }
        }
    }

    /**
     * Tests the {@link AspectGraph} implementation by translating a plain graph
     * to an aspect graph and back, and checking if the result is isomorphic to
     * the original.
     * @throws FormatException if anything goes wrong in the translation
     */
    public static void testTranslation(Graph plainGraph) throws FormatException {
        NodeEdgeMap fromPlainToAspect = new NodeEdgeHashMap();
        NodeEdgeMap fromAspectToPlain = new NodeEdgeHashMap();
        AspectGraph aspectGraph =
            getFactory().fromPlainGraph(plainGraph, fromPlainToAspect);
        Graph result = aspectGraph.toPlainGraph(fromAspectToPlain);
        if (result.nodeCount() > plainGraph.nodeCount()) {
            throw new FormatException(
                "Result graph has more nodes: %s (%d) than original: %s (%d)",
                plainGraph.nodeSet(), plainGraph.nodeCount(), result.nodeSet(),
                result.nodeCount());
        }
        if (result.edgeCount() > plainGraph.edgeCount()) {
            throw new FormatException(
                "Result graph has more nodes: %s (%d) than original: %s (%d)",
                plainGraph.edgeSet(), plainGraph.edgeCount(), result.edgeSet(),
                result.edgeCount());
        }
        for (Node plainNode : plainGraph.nodeSet()) {
            Node aspectNode = fromPlainToAspect.getNode(plainNode);
            if (aspectNode == null) {
                throw new FormatException(
                    "Node %s not translated to aspect node", plainNode);
            }
            Node resultNode = fromAspectToPlain.getNode(aspectNode);
            if (resultNode == null) {
                throw new FormatException(
                    "Node %s translated to aspect node %s, but not back",
                    plainNode, aspectNode);
            }
            Set<AspectValue> plainNodeValues =
                getNodeValues(plainGraph, plainNode);
            Set<AspectValue> resultNodeValues =
                getNodeValues(result, resultNode);
            if (!plainNodeValues.equals(resultNodeValues)) {
                throw new FormatException(
                    "Node values for %s and %s differ: %s versus %s",
                    plainNode, resultNode, plainNodeValues, resultNodeValues);
            }
        }
        for (Edge plainEdge : plainGraph.edgeSet()) {
            Edge aspectEdge = fromPlainToAspect.getEdge(plainEdge);
            if (aspectGraph.getNodeValue(plainEdge,
                aspectGraph.getAspectParser(plainGraph)) == null) {
                if (aspectEdge == null) {
                    throw new FormatException(
                        "Edge %s not translated to aspect edge", plainEdge);
                }
                Edge resultEdge = fromAspectToPlain.getEdge(aspectEdge);
                if (resultEdge == null) {
                    throw new FormatException(
                        "Edge %s translated to aspect edge %s, but not back",
                        plainEdge, aspectEdge);
                }
            } else {
                if (aspectEdge != null) {
                    throw new FormatException(
                        "Node value-encoding edge %s translated to aspect edge %s",
                        plainEdge, aspectEdge);
                }
            }
        }
    }

    /**
     * Retrieves all node values of a given node in a given (plain) graph.
     */
    private static Set<AspectValue> getNodeValues(Graph graph, Node node)
        throws FormatException {
        Set<AspectValue> result = new HashSet<AspectValue>();
        for (Edge outEdge : graph.outEdgeSet(node)) {
            AspectValue nodeValue =
                getFactory().getNodeValue(outEdge,
                    getFactory().getAspectParser(graph));
            if (nodeValue != null) {
                result.add(nodeValue);
            }
        }
        return result;
    }

    /**
     * The static instance serving as a factory.
     */
    private static final AspectGraph factory = new AspectGraph();
}

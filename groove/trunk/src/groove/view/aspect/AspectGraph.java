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

import static groove.graph.GraphRole.HOST;
import static groove.graph.GraphRole.RULE;
import static groove.graph.GraphRole.TYPE;
import groove.graph.DefaultEdge;
import groove.graph.DefaultFactory;
import groove.graph.DefaultGraph;
import groove.graph.DefaultLabel;
import groove.graph.DefaultNode;
import groove.graph.ElementFactory;
import groove.graph.ElementMap;
import groove.graph.GraphInfo;
import groove.graph.GraphRole;
import groove.graph.Morphism;
import groove.graph.NodeSetEdgeSetGraph;
import groove.graph.TypeLabel;
import groove.rel.RegExpr;
import groove.trans.SystemProperties;
import groove.view.DefaultGraphView;
import groove.view.DefaultRuleView;
import groove.view.DefaultTypeView;
import groove.view.FormatError;
import groove.view.FormatException;
import groove.view.GraphView;
import groove.view.RuleView;
import groove.view.TypeView;
import groove.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Graph implementation to convert from a label prefix representation of an
 * aspect graph to a graph where the aspect values are stored in
 * {@link AspectNode}s and {@link AspectEdge}s.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AspectGraph extends
        NodeSetEdgeSetGraph<AspectNode,AspectLabel,AspectEdge> implements
        Cloneable {

    /**
     * Creates an empty graph, with a given graph role.
     */
    private AspectGraph(GraphRole graphRole) {
        super();
        GraphInfo.setRole(this, graphRole);
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
    public AspectGraph fromPlainGraph(DefaultGraph graph) {
        // map from original graph elements to aspect graph elements
        PlainToAspectMap elementMap =
            new PlainToAspectMap(graph.getInfo().getRole());
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
    public AspectGraph fromPlainGraph(DefaultGraph graph,
            PlainToAspectMap elementMap) {
        GraphRole role = GraphInfo.getRole(graph);
        AspectGraph result = new AspectGraph(role);
        AspectParser labelParser = AspectParser.getInstance(role);
        List<FormatError> errors = new ArrayList<FormatError>();
        assert elementMap != null && elementMap.isEmpty();
        // first do the nodes;
        for (DefaultNode node : graph.nodeSet()) {
            AspectNode nodeImage = result.addNode(node.getNumber());
            // update the maps
            elementMap.putNode(node, nodeImage);
        }
        // look for node aspect indicators
        // and put all correct aspect vales in a map
        Map<DefaultEdge,AspectLabel> edgeDataMap =
            new HashMap<DefaultEdge,AspectLabel>();
        for (DefaultEdge edge : graph.edgeSet()) {
            try {
                AspectLabel label = labelParser.parse(edge.label());
                if (label.isNodeOnly()) {
                    AspectNode sourceImage = elementMap.getNode(edge.source());
                    sourceImage.setAspects(label);
                } else if (edge.source().equals(edge.target())
                    || label.isBinary()) {
                    edgeDataMap.put(edge, label);
                } else {
                    throw new FormatException("%s %s must be a node label",
                        label.getKind().getName(true), label);
                }
            } catch (FormatException e) {
                // we can't trace the error to the aspect graph element,
                // as the aspect graph element has not yet been created
                e.extend(edge);
                errors.addAll(e.getErrors());
            }
        }
        // Now iterate over the remaining edges
        for (Map.Entry<DefaultEdge,AspectLabel> entry : edgeDataMap.entrySet()) {
            DefaultEdge edge = entry.getKey();
            AspectEdge edgeImage =
                result.addEdge(elementMap.getNode(edge.source()),
                    entry.getValue(), elementMap.getNode(edge.target()));
            elementMap.putEdge(edge, edgeImage);
        }
        GraphInfo.transfer(graph, result, elementMap);
        result.addErrors(errors);
        result.setFixed();
        return result;
    }

    /**
     * Creates a graph where the aspect values are represented as label prefixes
     * for the edges, and as special edges for the nodes.
     */
    public DefaultGraph toPlainGraph() {
        AspectToPlainMap elementMap = new AspectToPlainMap();
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
    private DefaultGraph toPlainGraph(AspectToPlainMap elementMap) {
        DefaultGraph result = createPlainGraph();
        for (AspectNode node : nodeSet()) {
            DefaultNode nodeImage = result.addNode(node.getNumber());
            elementMap.putNode(node, nodeImage);
            for (DefaultLabel label : node.getPlainLabels()) {
                result.addEdge(nodeImage, label, nodeImage);
            }
        }
        for (AspectEdge edge : edgeSet()) {
            result.addEdge(elementMap.mapEdge(edge));
        }
        GraphInfo.transfer(this, result, elementMap);
        result.setFixed();
        return result;
    }

    /**
     * Factory method for a <code>Graph</code>.
     * @see #toPlainGraph()
     */
    private DefaultGraph createPlainGraph() {
        return new DefaultGraph();
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
                result = new AspectGraph(getInfo().getRole());
                AspectGraphMorphism elementMap =
                    new AspectGraphMorphism(getInfo().getRole());
                int nodeNr = 0;
                for (AspectNode node : nodes) {
                    AspectNode image = result.addNode(nodeNr);
                    for (AspectLabel label : node.getNodeLabels()) {
                        image.setAspects(label);
                    }
                    elementMap.putNode(node, image);
                    nodeNr++;
                }
                for (AspectEdge edge : edgeSet()) {
                    AspectEdge edgeImage = elementMap.mapEdge(edge);
                    result.addEdge(edgeImage);
                }
                GraphInfo.transfer(this, result, elementMap);
                result.setFixed();
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
    public AspectGraph relabel(TypeLabel oldLabel, TypeLabel newLabel) {
        // create a plain graph under relabelling
        DefaultGraph result = createPlainGraph();
        AspectToPlainMap elementMap = new AspectToPlainMap();
        // flag registering if anything changed due to relabelling
        boolean graphChanged = false;
        for (AspectNode node : nodeSet()) {
            DefaultNode image = result.addNode(node.getNumber());
            elementMap.putNode(node, image);
            for (DefaultLabel nodeLabel : node.getPlainLabels()) {
                result.addEdge(image, nodeLabel, image);
            }
        }
        for (AspectEdge edge : edgeSet()) {
            String replacement = null;
            if (edge.getRuleLabel() != null) {
                RegExpr oldLabelExpr = edge.getRuleLabel().getMatchExpr();
                if (oldLabelExpr != null) {
                    RegExpr newLabelExpr =
                        oldLabelExpr.relabel(oldLabel, newLabel);
                    if (newLabelExpr != oldLabelExpr) {
                        replacement = newLabelExpr.toString();
                    }
                }
            } else if (oldLabel.equals(edge.getTypeLabel())) {
                replacement = TypeLabel.toPrefixedString(newLabel);
            }
            AspectLabel edgeLabel = edge.label();
            if (replacement != null) {
                graphChanged = true;
                try {
                    AspectLabel newEdgeLabel = edgeLabel.clone();
                    newEdgeLabel.setInnerText(replacement);
                    edgeLabel = newEdgeLabel;
                } catch (FormatException e) {
                    // do nothing with this label
                }
            }
            DefaultNode sourceImage = elementMap.getNode(edge.source());
            DefaultNode targetImage = elementMap.getNode(edge.target());
            DefaultEdge edgeImage =
                result.addEdge(sourceImage, edgeLabel.toString(), targetImage);
            elementMap.putEdge(edge, edgeImage);
        }
        if (!graphChanged) {
            return this;
        } else {
            result.setFixed();
            GraphInfo.transfer(this, result, elementMap);
            return fromPlainGraph(result);
        }
    }

    @Override
    public void setFixed() {
        List<FormatError> errors = new ArrayList<FormatError>();
        for (AspectEdge edge : edgeSet()) {
            try {
                edge.setFixed();
            } catch (FormatException exc) {
                errors.addAll(exc.getErrors());
            }
        }
        for (AspectNode node : nodeSet()) {
            try {
                node.setFixed();
            } catch (FormatException exc) {
                errors.addAll(exc.getErrors());
            }
        }
        addErrors(errors);
        super.setFixed();
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
     *         {@link GraphRole#HOST}
     */
    public GraphView toGraphView(SystemProperties properties)
        throws IllegalStateException {
        if (!GraphInfo.hasHostRole(this)) {
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
            this.graphView = new DefaultGraphView(this, properties);
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
     *         {@link GraphRole#TYPE}
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
     *         {@link GraphRole#RULE}
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
            this.ruleView = new DefaultRuleView(this, properties);
        } else {
            this.ruleView.setSystemProperties(properties);
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
    private DefaultGraphView graphView;

    /** Auxiliary object for converting this aspect graph to a rule. */
    private RuleView ruleView;

    /**
     * Copies this aspect graph to one with the same nodes, edges and graph
     * info.
     */
    @Override
    public AspectGraph clone() {
        AspectGraph result = new AspectGraph(getInfo().getRole());
        GraphInfo.transfer(this, result, null);
        result.addNodeSet(nodeSet());
        result.addEdgeSetWithoutCheck(edgeSet());
        GraphInfo.transfer(this, result, null);
        result.addErrors(getErrors());
        return result;
    }

    @Override
    public AspectFactory getFactory() {
        return AspectFactory.instance(getInfo().getRole());
    }

    /**
     * Creates an aspect graph from a given (plain) graph. Convenience method
     * for <code>getFactory().fromPlainGraph(GraphShape)</code>.
     * @param plainGraph the plain graph to convert; non-null
     * @return the resulting aspect graph; non-null
     * @see #fromPlainGraph(DefaultGraph)
     */
    public static AspectGraph newInstance(DefaultGraph plainGraph) {
        return factory.fromPlainGraph(plainGraph);
    }

    /**
     * Creates an aspect graph from a given (plain) graph. Convenience method
     * for {@link #fromPlainGraph(DefaultGraph, PlainToAspectMap)}.
     * @param plainGraph the plain graph to convert; non-null
     * @param elementMap output parameter for mapping from plain graph elements
     *        to resulting {@link AspectGraph} elements; should be initially
     *        empty
     * @return the resulting aspect graph; non-null
     * @see #fromPlainGraph(DefaultGraph)
     */
    public static AspectGraph newInstance(DefaultGraph plainGraph,
            PlainToAspectMap elementMap) {
        return factory.fromPlainGraph(plainGraph, elementMap);
    }

    /**
     * The static instance serving as a factory.
     */
    private static final AspectGraph factory = new AspectGraph(HOST);

    /** Factory for AspectGraph elements. */
    static class AspectFactory implements
            ElementFactory<AspectNode,AspectLabel,AspectEdge> {
        /** Private constructor to ensure singleton usage. */
        private AspectFactory(GraphRole graphRole) {
            this.graphRole = graphRole;
        }

        @Override
        public AspectNode createNode(int nr) {
            this.maxNodeNr = Math.max(this.maxNodeNr, nr);
            return new AspectNode(nr, this.graphRole);
        }

        @Override
        public AspectLabel createLabel(String text) {
            throw new UnsupportedOperationException();
        }

        @Override
        public AspectEdge createEdge(AspectNode source, String text,
                AspectNode target) {
            throw new UnsupportedOperationException();
        }

        @Override
        public AspectEdge createEdge(AspectNode source, AspectLabel label,
                AspectNode target) {
            return new AspectEdge(source, label, target, this.graphRole);
        }

        @Override
        public AspectGraphMorphism createMorphism() {
            return new AspectGraphMorphism(this.graphRole);
        }

        @Override
        public int getMaxNodeNr() {
            return this.maxNodeNr;
        }

        /** The highest node number returned by this factory. */
        private int maxNodeNr;

        /** The graph role of the created elements. */
        private final GraphRole graphRole;

        /** Returns the singleton instance of this class. */
        static public AspectFactory instance(GraphRole graphRole) {
            return factoryMap.get(graphRole);
        }

        /** Mapping from graph rules to element-producting factories. */
        static private Map<GraphRole,AspectFactory> factoryMap =
            new EnumMap<GraphRole,AspectFactory>(GraphRole.class);

        static {
            factoryMap.put(RULE, new AspectFactory(RULE));
            factoryMap.put(HOST, new AspectFactory(HOST));
            factoryMap.put(TYPE, new AspectFactory(TYPE));
        }
    }

    private static class AspectGraphMorphism extends
            Morphism<AspectNode,AspectLabel,AspectEdge> {
        /** Constructs a new, empty map. */
        public AspectGraphMorphism(GraphRole graphRole) {
            super(AspectFactory.instance(graphRole));
            this.graphRole = graphRole;
        }

        @Override
        public AspectGraphMorphism newMap() {
            return new AspectGraphMorphism(this.graphRole);
        }

        /** The graph role of the created elements. */
        private final GraphRole graphRole;
    }

    private static class AspectToPlainMap
            extends
            ElementMap<AspectNode,AspectLabel,AspectEdge,DefaultNode,DefaultLabel,DefaultEdge> {
        /** Constructs a new, empty map. */
        public AspectToPlainMap() {
            super(DefaultFactory.instance());
        }

        @Override
        public DefaultEdge createImage(AspectEdge key) {
            DefaultNode imageSource = getNode(key.source());
            if (imageSource == null) {
                return null;
            }
            DefaultNode imageTarget = getNode(key.target());
            if (imageTarget == null) {
                return null;
            }
            return getFactory().createEdge(imageSource, key.getPlainLabel(),
                imageTarget);
        }

        @Override
        public AspectToPlainMap newMap() {
            return new AspectToPlainMap();
        }
    }
}

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
import groove.graph.Label;
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
public class AspectGraph extends NodeSetEdgeSetGraph<AspectNode,AspectEdge>
        implements Cloneable {

    /**
     * Creates an empty graph, with a given name and graph role.
     */
    private AspectGraph(String name, GraphRole graphRole) {
        super(name);
        assert graphRole.inGrammar();
        this.role = graphRole;
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
        PlainToAspectMap elementMap = new PlainToAspectMap(graph.getRole());
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
        GraphRole role = graph.getRole();
        AspectGraph result = new AspectGraph(graph.getName(), role);
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
            AspectLabel label = labelParser.parse(edge.label().text());
            if (label.isNodeOnly()) {
                AspectNode sourceImage = elementMap.getNode(edge.source());
                if (label.hasErrors()) {
                    for (FormatError error : label.getErrors()) {
                        errors.add(error.extend(sourceImage));
                    }
                } else {
                    try {
                        sourceImage.setAspects(label);
                    } catch (FormatException e) {
                        for (FormatError error : e.getErrors()) {
                            errors.add(error.extend(sourceImage));
                        }
                    }
                }
            } else {
                edgeDataMap.put(edge, label);
            }
        }
        // Now iterate over the remaining edges
        for (Map.Entry<DefaultEdge,AspectLabel> entry : edgeDataMap.entrySet()) {
            DefaultEdge edge = entry.getKey();
            AspectLabel label = entry.getValue();
            AspectEdge edgeImage =
                result.addEdge(elementMap.getNode(edge.source()), label,
                    elementMap.getNode(edge.target()));
            elementMap.putEdge(edge, edgeImage);
            // signal an error only now, so the edge is already in the result graph
            for (FormatError error : label.getErrors()) {
                errors.add(error.extend(edgeImage));
            }
            if (!edge.source().equals(edge.target()) && !label.isBinary()) {
                errors.add(new FormatError("%s %s must be a node label",
                    label.getKind().getName(true), label, edgeImage));
            }
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
        DefaultGraph result = new DefaultGraph(getName());
        result.setRole(getRole());
        return result;
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
                result = newGraph(getName());
                AspectGraphMorphism elementMap =
                    new AspectGraphMorphism(getRole());
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
                AspectLabel newEdgeLabel = edgeLabel.clone();
                newEdgeLabel.setInnerText(replacement);
                newEdgeLabel.setFixed();
                if (!newEdgeLabel.hasErrors()) {
                    edgeLabel = newEdgeLabel;
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

    /**
     * Returns the role of this default graph.
     * The role is set at construction time.
     */
    @Override
    public final GraphRole getRole() {
        return this.role;
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

    @Override
    public AspectGraph newGraph(String name) {
        return new AspectGraph(name, getRole());
    }

    /**
     * Copies this aspect graph to one with the same nodes, edges and graph
     * info. The result is not fixed.
     */
    @Override
    public AspectGraph clone() {
        AspectGraph result = newGraph(getName());
        result.addNodeSet(nodeSet());
        result.addEdgeSetWithoutCheck(edgeSet());
        GraphInfo.transfer(this, result, null);
        result.addErrors(getErrors());
        return result;
    }

    /** 
     * Clones this aspect graph while giving it a different name.
     * This graph is required to be fixed, and the resulting graph
     * will be fixed as well.
     * @param name the new graph name; non-{@code null}
     */
    public AspectGraph rename(String name) {
        AspectGraph result = clone();
        result.setName(name);
        result.setFixed();
        return result;
    }

    @Override
    public AspectFactory getFactory() {
        return AspectFactory.instance(getRole());
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
        if (getRole() != HOST) {
            throw new IllegalStateException(
                "Aspect graph does not represent a graph");
        }
        boolean refreshView = this.graphView == null;
        if (!refreshView) {
            String viewName = this.graphView.getName();
            refreshView = !getName().equals(viewName);
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
        if (getRole() != TYPE) {
            throw new IllegalStateException(
                String.format("Aspect graph does not represent a type graph"));
        }
        boolean refreshView = this.typeView == null;
        if (!refreshView) {
            String viewName = this.typeView.getName();
            refreshView = !getName().equals(viewName);
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
        if (this.getRole() != RULE) {
            throw new IllegalStateException(
                "Aspect graph does not represent a rule graph");
        }
        boolean refreshView = this.ruleView == null;
        if (!refreshView) {
            String viewName = this.ruleView.getName();
            refreshView = !getName().equals(viewName);
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
        switch (getRole()) {
        case RULE:
            return toRuleView(null);
        case TYPE:
            return toTypeView(null);
        case HOST:
            return toGraphView(null);
        }
        assert false;
        return null;
    }

    private final GraphRole role;
    /** Auxiliary object for converting this aspect graph to a type graph. */
    private TypeView typeView;

    /** Auxiliary object for converting this aspect graph to a state graph. */
    private DefaultGraphView graphView;

    /** Auxiliary object for converting this aspect graph to a rule. */
    private RuleView ruleView;

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

    /** Creates an empty named aspect graph, with a given graph role. */
    public static AspectGraph newInstance(String name, GraphRole role) {
        return new AspectGraph(name, role);
    }

    /** Creates an empty aspect graph, with a given graph role. */
    public static AspectGraph newInstance(GraphRole role) {
        return newInstance(NO_NAME, role);
    }

    /**
     * The static instance serving as a factory.
     */
    private static final AspectGraph factory = new AspectGraph(NO_NAME, HOST);

    /** Factory for AspectGraph elements. */
    public static class AspectFactory implements
            ElementFactory<AspectNode,AspectEdge> {
        /** Private constructor to ensure singleton usage. */
        protected AspectFactory(GraphRole graphRole) {
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
        public AspectEdge createEdge(AspectNode source, Label label,
                AspectNode target) {
            return new AspectEdge(source, (AspectLabel) label, target,
                this.graphRole);
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

        /** Mapping from graph rules to element-producing factories. */
        static private Map<GraphRole,AspectFactory> factoryMap =
            new EnumMap<GraphRole,AspectFactory>(GraphRole.class);

        static {
            factoryMap.put(RULE, new AspectFactory(RULE));
            factoryMap.put(HOST, new AspectFactory(HOST));
            factoryMap.put(TYPE, new AspectFactory(TYPE));
        }
    }

    private static class AspectGraphMorphism extends
            Morphism<AspectNode,AspectEdge> {
        /** Constructs a new, empty map. */
        public AspectGraphMorphism(GraphRole graphRole) {
            super(AspectFactory.instance(graphRole));
            assert graphRole.inGrammar();
            this.graphRole = graphRole;
        }

        @Override
        public AspectGraphMorphism newMap() {
            return new AspectGraphMorphism(this.graphRole);
        }

        /** The graph role of the created elements. */
        private final GraphRole graphRole;
    }

    private static class AspectToPlainMap extends
            ElementMap<AspectNode,AspectEdge,DefaultNode,DefaultEdge> {
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

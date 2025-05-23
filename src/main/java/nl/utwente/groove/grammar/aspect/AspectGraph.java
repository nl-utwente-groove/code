/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
 	 * $Id$
 */
package nl.utwente.groove.grammar.aspect;

import static nl.utwente.groove.grammar.aspect.AspectKind.COLOR;
import static nl.utwente.groove.graph.GraphRole.HOST;
import static nl.utwente.groove.util.Factory.lazy;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.algebra.syntax.SortMap;
import nl.utwente.groove.automaton.RegExpr;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.aspect.AspectContent.NestedValue;
import nl.utwente.groove.grammar.aspect.AspectKind.Category;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.graph.AGraphMap;
import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.graph.ElementFactory;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.GraphInfo;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.graph.Morphism;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.graph.NodeComparator;
import nl.utwente.groove.graph.NodeSetEdgeSetGraph;
import nl.utwente.groove.graph.plain.PlainEdge;
import nl.utwente.groove.graph.plain.PlainFactory;
import nl.utwente.groove.graph.plain.PlainGraph;
import nl.utwente.groove.graph.plain.PlainLabel;
import nl.utwente.groove.graph.plain.PlainNode;
import nl.utwente.groove.gui.layout.JVertexLayout;
import nl.utwente.groove.gui.layout.LayoutMap;
import nl.utwente.groove.gui.list.SearchResult;
import nl.utwente.groove.util.Dispenser;
import nl.utwente.groove.util.Factory;
import nl.utwente.groove.util.Keywords;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.FormatErrorSet;

/**
 * Graph implementation to convert from a label prefix representation of an
 * aspect graph to a graph where the aspect values are stored in
 * {@link AspectNode}s and {@link AspectEdge}s.
 * @author Arend Rensink
 * @version $Revision$
 */
public class AspectGraph extends NodeSetEdgeSetGraph<@NonNull AspectNode,@NonNull AspectEdge> {
    /**
     * Creates an empty graph, with a given qualified name and graph role.
     * @param simple flag indicating whether this is a simple graph
     */
    public AspectGraph(String name, GraphRole graphRole, boolean simple) {
        super(name.toString(), simple);
        this.qualName = QualName.parse(name);
        //        assert graphRole.inGrammar() : String
        //            .format("Cannot create aspect graph for %s", graphRole.toString());
        this.role = graphRole;
        this.normal = true;
        this.edgeNumber = simple
            ? Dispenser.constant(0)
            : Dispenser.counter();
        // make sure the properties object is initialised
        addErrors(this.qualName.getErrors());
    }

    /* Also sets the qualified name. */
    @Override
    public void setName(String name) {
        super.setName(name);
        this.qualName = QualName.parse(name);
        addErrors(this.qualName.getErrors());
    }

    /** Returns the qualified name of this aspect graph. */
    public QualName getQualName() {
        return this.qualName;
    }

    /** Changes the qualified name of this aspect graph. */
    private void setQualName(QualName qualName) {
        this.qualName = qualName;
        super.setName(qualName.toString());
    }

    private QualName qualName;

    /** Sets the mapping from node type labels to their sort maps.
     * This is used for typing field expressions.
     * The map can be {@code null}, in which case field expression types must be derived
     * in some other way.
     */
    public void setTypeSortMap(@Nullable Map<TypeLabel,SortMap> typeSortMap) {
        this.typeSortMap = typeSortMap;
        this.sortMap.reset();
    }

    /** Returns the (possibly {@code null}) mapping from node type labels to
     * sort maps for those node types.
     */
    @Nullable
    public Map<TypeLabel,SortMap> getTypeSortMap() {
        return this.typeSortMap;
    }

    /** Mapping from node type labels to their sort maps. */
    private @Nullable Map<TypeLabel,SortMap> typeSortMap;

    /**
     * Collects search results matching the given label into the given list.
     */
    public void getSearchResults(TypeLabel label, List<SearchResult> results) {
        String msg = getRole().getDescription() + " '%s' - Element '%s'";
        for (AspectEdge edge : edgeSet()) {
            if ((edge.getRuleLabel() != null && label.equals(edge.getRuleLabel().getTypeLabel()))
                || label.equals(edge.getTypeLabel())) {
                results.add(new SearchResult(msg, this.getName(), edge, this));
            }
        }
    }

    /**
     * Creates a graph where the aspect values are represented as label prefixes
     * for the edges, and as special edges for the nodes.
     */
    public PlainGraph toPlainGraph() {
        AspectToPlainMap elementMap = new AspectToPlainMap();
        PlainGraph result = createPlainGraph();
        for (AspectNode node : nodeSet()) {
            PlainNode nodeImage = result.addNode(node.getNumber());
            elementMap.putNode(node, nodeImage);
            for (PlainLabel label : node.getPlainLabels()) {
                result.addEdge(nodeImage, label, nodeImage);
            }
        }
        for (AspectEdge edge : edgeSet()) {
            var image = elementMap.mapEdge(edge);
            assert image != null;
            result.addEdgeContext(image);
        }
        GraphInfo.transferAll(this, result, elementMap);
        result.setFixed();
        return result;
    }

    /**
     * Factory method for a <code>Graph</code>.
     * @see #toPlainGraph()
     */
    private PlainGraph createPlainGraph() {
        return new PlainGraph(getName(), getRole());
    }

    /**
     * Returns the normalised aspect graph.
     * An aspect graph is normalised if all {@link AspectKind#LET} and
     * {@link AspectKind#TEST} edges have been substituted by explicit
     * attribute elements.
     */
    public AspectGraph normalise() {
        return this.normalGraph.get();
    }

    /** The lazily grated normalised graph. */
    private final Factory<AspectGraph> normalGraph = Factory.lazy(this::createNormalGraph);

    /**
     * Computes the normalised aspect graph.
     * @see #normalise()
     */
    public AspectGraph createNormalGraph() {
        assert isFixed();
        AspectGraph result;
        if (isNormal() || hasErrors()) {
            result = this;
        } else {
            result = new NormalAspectGraph(this);
            result.setFixed();
        }
        return result;
    }

    /**
     * Returns a new aspect graph obtained from this one
     * by renumbering the nodes in a consecutive sequence starting from {@code 0}
     */
    public AspectGraph renumber() {
        AspectGraph result = this;
        // renumber the nodes in their original order
        SortedSet<AspectNode> nodes = new TreeSet<>(NodeComparator.instance());
        nodes.addAll(nodeSet());
        if (!nodes.isEmpty() && nodes.last().getNumber() != nodeCount() - 1) {
            result = newGraph();
            result.setTypeSortMap(getTypeSortMap());
            AspectGraphMorphism elementMap = new AspectGraphMorphism(result);
            int nodeNr = 0;
            for (AspectNode node : nodes) {
                AspectNode image = result.addNode(nodeNr);
                node.getNodeLabels().forEach(l -> image.addLabel(l));
                elementMap.putNode(node, image);
                nodeNr++;
            }
            for (AspectEdge edge : edgeSet()) {
                var image = elementMap.mapEdge(edge);
                assert image != null;
                result.addEdgeContext(image);
            }
            GraphInfo.transferProperties(this, result, elementMap);
            result.setFixed();
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
        PlainGraph result = createPlainGraph();
        AspectToPlainMap elementMap = new AspectToPlainMap();
        // flag registering if anything changed due to relabelling
        boolean graphChanged = false;
        for (AspectNode node : nodeSet()) {
            PlainNode image = result.addNode(node.getNumber());
            elementMap.putNode(node, image);
            for (AspectLabel oldNodeLabel : node.getNodeLabels()) {
                AspectLabel newNodeLabel = oldNodeLabel.relabel(oldLabel, newLabel, getSortMap());
                newNodeLabel.setFixed();
                graphChanged |= newNodeLabel != oldNodeLabel;
                String text = newNodeLabel.toString();
                assert !text.isEmpty();
                result.addEdge(image, PlainLabel.parseLabel(text), image);
            }
        }
        for (AspectEdge edge : edgeSet()) {
            String replacement = null;
            if (edge.getRuleLabel() != null) {
                RegExpr oldLabelExpr = edge.getRuleLabel().getMatchExpr();
                RegExpr newLabelExpr = oldLabelExpr.relabel(oldLabel, newLabel);
                if (newLabelExpr != oldLabelExpr) {
                    replacement = newLabelExpr.toString();
                }
            } else if (oldLabel.equals(edge.getTypeLabel())) {
                replacement = newLabel.toParsableString();
            }
            AspectLabel edgeLabel = edge.label();
            // don't relabel operators
            AspectLabel newEdgeLabel = edge.isOperator()
                ? edgeLabel
                : edgeLabel.relabel(oldLabel, newLabel, getSortMap());
            // force a new object if the inner text has to change
            if (replacement != null && newEdgeLabel == edgeLabel) {
                newEdgeLabel = newEdgeLabel.clone();
            }
            if (newEdgeLabel != edgeLabel) {
                graphChanged = true;
                if (replacement != null) {
                    newEdgeLabel.setInnerText(replacement);
                }
                newEdgeLabel.setFixed();
                edgeLabel = newEdgeLabel;
            }
            PlainNode sourceImage = elementMap.getNode(edge.source());
            assert sourceImage != null;
            PlainNode targetImage = elementMap.getNode(edge.target());
            assert targetImage != null;
            PlainEdge edgeImage = result.addEdge(sourceImage, edgeLabel.toString(), targetImage);
            elementMap.putEdge(edge, edgeImage);
        }
        if (!graphChanged) {
            return this;
        } else {
            GraphInfo.transferProperties(this, result, elementMap);
            result.setFixed();
            return newInstance(result);
        }
    }

    /**
     * Returns an aspect graph obtained from this one by changing the colour aspect
     * of one or more nodes.
     * @param changedNodes the nodes to be changed (guaranteed to be nodes of this graph)
     * @param colour the new colour for the node; may be {@code null}
     * if the colour is to be reset to default
     * @return a clone of this aspect graph with changed node colour, or this graph
     *         if {@code node} already had the required colour
     */
    public AspectGraph colour(Collection<AspectNode> changedNodes, Aspect colour) {
        boolean graphChanged = false;
        // create a plain graph
        PlainGraph result = createPlainGraph();
        AspectToPlainMap elementMap = new AspectToPlainMap();
        // construct the plain graph for the aspect nodes,
        // except for the colour aspects of the changed node
        for (AspectNode node : nodeSet()) {
            boolean isChangedNode = changedNodes.contains(node);
            graphChanged |= isChangedNode && !Objects.equals(node.get(COLOR), colour);
            PlainNode image = result.addNode(node.getNumber());
            elementMap.putNode(node, image);
            node
                .getNodeLabels()
                .stream()
                .filter(l -> !isChangedNode || !l.has(COLOR))
                .forEach(l -> result.addEdge(image, l.toString(), image));
            if (isChangedNode && colour != null) {
                result.addEdge(image, colour.toString(), image);
            }
        }
        if (graphChanged) {
            // copy the edges over
            for (AspectEdge edge : edgeSet()) {
                AspectLabel edgeLabel = edge.label();
                PlainNode sourceImage = elementMap.getNode(edge.source());
                assert sourceImage != null;
                PlainNode targetImage = elementMap.getNode(edge.target());
                assert targetImage != null;
                PlainEdge edgeImage
                    = result.addEdge(sourceImage, edgeLabel.toString(), targetImage);
                elementMap.putEdge(edge, edgeImage);
            }
            GraphInfo.transferProperties(this, result, elementMap);
            result.setFixed();
            return newInstance(result);
        } else {
            return this;
        }
    }

    @Override
    public boolean addNode(AspectNode node) {
        assert !node.hasId() || !this.sortMap.isSet() : String
            .format("Named node '%s' added after typing has been computed", node);
        assert !node.hasId() || !this.nodeIdMap.isSet() : String
            .format("Named node '%s' added after ID map has been computed", node);
        return super.addNode(node);
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
    public boolean isFixed() {
        return isStatus(Status.FIXED);
    }

    @Override
    public boolean setFixed() {
        if (DEBUG) {
            System.out.printf("setFixed called on %s %s%n", getRole(), getName());
        }
        boolean result = !isFixed();
        if (result) {
            FormatErrorSet errors = new FormatErrorSet();
            // first parse all nodes and edges
            nodeSet().forEach(AspectNode::setParsed);
            edgeSet().forEach(AspectEdge::setParsed);
            // first fix the edges, then the nodes
            for (AspectElement edge : edgeSet()) {
                edge.setFixed();
                errors.addAll(edge.getErrors());
            }
            for (AspectNode node : nodeSet()) {
                node.setFixed();
                errors.addAll(node.getErrors());
            }
            if (!hasErrors() && !errors.isEmpty()) {
                addErrors(errors);
            }
            // create the node id map to check for duplicate IDs
            getNodeIdMap();
            if (!hasErrors()) {
                addErrors(DependencyChecker.instance().check(this));
            }
            getErrors().setFixed();
            setStatus(Status.FIXED);
            super.setFixed();
        }
        return result;
    }

    /** Changes the status of this graph. */
    void setStatus(Status status) {
        this.status = status;
    }

    /** Checks if this graph has a given status. */
    private boolean isStatus(Status status) {
        return this.status == status;
    }

    private Status status = Status.NEW;

    @Override
    public AspectGraph newGraph(String name) {
        return new AspectGraph(name, getRole(), isSimple());
    }

    /** Returns a new, empty AspectGraph with the same name and graph role as this one. */
    public AspectGraph newGraph() {
        return newGraph(getName());
    }

    /**
     * Copies this aspect graph to one with the same nodes, edges and graph
     * info. The result is not fixed.
     */
    @Override
    public AspectGraph clone() {
        return cloneTo(new AspectGraphMorphism(newGraph()));
    }

    /**
     * Copies this aspect graph under a given mapping.
     * The mapping is assumed to be initially empty, and have an empty target graph.
     * The result of the method is the target of the mapping, which then will be filled with a copy of this graph
     * @param map the (initially empty) morphism under which the clone is to be constructed
     * @return the result graph; also obtainable as {@code map.target()}
     */
    AspectGraph cloneTo(AspectGraphMorphism map) {
        assert isFixed();
        assert map.isEmpty();
        assert map.getTarget().isEmpty();
        AspectGraph result = map.getTarget();
        result.setTypeSortMap(getTypeSortMap());
        for (AspectNode node : nodeSet()) {
            AspectNode clone = node.clone(result);
            map.putNode(node, clone);
            result.addNode(clone);
        }
        for (AspectEdge edge : edgeSet()) {
            var image = map.mapEdge(edge);
            assert image != null;
            result.addEdgeContext(image);
        }
        GraphInfo.transferProperties(this, result, map);
        return result;
    }

    /**
     * Clones this aspect graph while giving it a different name.
     * This graph is required to be fixed, and the resulting graph
     * will be fixed as well.
     * @param name the new graph name; non-{@code null}
     */
    public AspectGraph rename(QualName name) {
        AspectGraph result = clone();
        result.setQualName(name);
        result.setFixed();
        return result;
    }

    /** Returns a copy of this graph with all labels unwrapped.
     * @see AspectLabel#unwrap()
     */
    public AspectGraph unwrap() {
        AspectGraph result = cloneTo(new AspectGraphUnwrapper(newGraph()));
        result.setFixed();
        return result;
    }

    @Override
    public AspectFactory getFactory() {
        return this.aspectFactory.get();
    }

    /** Creates a new aspect factory. */
    private AspectFactory createFactory() {
        return new AspectFactory(this);
    }

    /** Aspect factory for this graph. */
    private Factory<AspectFactory> aspectFactory = lazy(this::createFactory);

    /** The graph role of the aspect graph. */
    private final GraphRole role;

    /** Indicates if this ApectGraph is normal.
     * An aspect graph is normal if it contains only primitive attribute syntax, i.e., no
     * {@code let}-, {@code test}- or {@code role:sort}-edges.
     */
    public boolean isNormal() {
        return this.normal;
    }

    /** Callback method to set the graph to non-normal. */
    void setNonNormal() {
        this.normal = false;
    }

    /** Flag indicating whether the graph is normal. */
    private boolean normal;

    /** Returns the node with a given ID, if any. */
    AspectNode getNodeForId(String id) {
        return getNodeIdMap().get(id);
    }

    /** Returns the mapping from declared node identities to nodes.
     * Once this method has been called, no new node IDs should be added
     */
    private Map<String,AspectNode> getNodeIdMap() {
        return this.nodeIdMap.get();
    }

    /** Resets the previously computed mapping from node IDs to nodes.
     * This has to be called after normalisation, because ID nodes may have changed.
     */
    void resetNodeIdMap() {
        this.nodeIdMap.reset();
    }

    /** Mapping from node identifiers to nodes. */
    private final Factory<Map<String,AspectNode>> nodeIdMap = lazy(this::createNodeIdMap);

    /** Computes the value for {@link #getNodeIdMap()}
     */
    private Map<String,AspectNode> createNodeIdMap() {
        this.sortMap.reset();
        Map<String,AspectNode> result = new HashMap<>();
        nodeSet().stream().filter(AspectNode::hasId).forEach(n -> {
            var old = result.put(n.getId(), n);
            if (old != null) {
                addError("Duplicate node ID '%s'", n.getId(), n, old);
            }
        });
        return result;
    }

    /** Returns a mapping from named variables and fields of named nodes to primitive node sorts.
     * Once this method has been called, no new node IDs may be added.
     */
    SortMap getSortMap() {
        return this.sortMap.get();
    }

    /** The factory for {@link #getSortMap()}. */
    private final Factory<SortMap> sortMap = lazy(this::createSortMap);

    /** Computes the value for {@link #getSortMap()}. */
    @SuppressWarnings("cast")
    private SortMap createSortMap() {
        SortMap result = new SortMap();
        // add variable sorts
        getNodeIdMap()
            .entrySet()
            .stream()
            .filter(e -> e.getValue().hasSort())
            .forEach(e -> result.add(e.getKey(), (@NonNull Sort) e.getValue().getSort()));
        // add quantifier count sorts
        getNodeIdMap()
            .entrySet()
            .stream()
            .filter(e -> e.getValue().has(Category.NESTING))
            .map(Map.Entry::getKey)
            .map(id -> new QualName(id, NestedValue.COUNT.toString()))
            .forEach(id -> result.add(id, Sort.INT));
        var typeSortMap = getTypeSortMap();
        if (hasRole(GraphRole.RULE) && typeSortMap != null) {
            // add field sorts
            for (var e : edgeSet()) {
                if (e.label().hasRole(EdgeRole.NODE_TYPE) && e.source().hasId()) {
                    String id = e.source().getId();
                    var typeLabel = e.getRuleLabel().getTypeLabel();
                    if (typeLabel != null && typeSortMap.containsKey(typeLabel)) {
                        for (var se : typeSortMap.get(typeLabel).entrySet()) {
                            var fieldName = new QualName(id, se.getKey());
                            result.add(fieldName, se.getValue());
                        }
                    }
                }
            }
        }
        return result;
    }

    /** Returns the mapping from variables and field to primitive node sorts,
     * extended with self-fields for a given node type label.
     * The self-fields have the empty string as their first token.
     * Once this method has been called, no new node IDs may be added.
     */
    SortMap getSortMap(@Nullable TypeLabel self) {
        var result = getSortMap();
        var typeSortMap = getTypeSortMap();
        if (self != null && typeSortMap != null && typeSortMap.containsKey(self)) {
            result = new SortMap().add(result);
            for (var se : typeSortMap.get(self).entrySet()) {
                var fieldName = new QualName("", se.getKey());
                result.add(fieldName, se.getValue());
                fieldName = new QualName(Keywords.SELF, se.getKey());
                result.add(fieldName, se.getValue());
            }
        }
        return result;
    }

    /** Returns a number for the next edge to be created for this graph. */
    int getEdgeNumber() {
        return this.edgeNumber.getNext();
    }

    private final Dispenser edgeNumber;

    /** Returns a hash code for this graph based on normal status, name and role. */
    int externalHashCode() {
        return Objects.hash(isNormal(), getRole(), getName());
    }

    /** Compares this graph to another one on the basis of normal status, name and role. */
    boolean externalEquals(AspectGraph other) {
        return isNormal() == other.isNormal() && getRole() == other.getRole()
            && Objects.equals(getName(), other.getName());
    }

    /**
     * Creates an aspect graph from a given (plain) grammar-related graph.
     * @param graph the plain graph to convert
     * @return the resulting aspect graph
     */
    public static @NonNull AspectGraph newInstance(@NonNull Graph graph) {
        GraphRole role = graph.getRole();
        AspectGraph result = new AspectGraph(graph.getName(), role, graph.isSimple());
        // map from original graph elements to aspect graph elements
        GraphToAspectMap elementMap = new GraphToAspectMap(result);
        assert elementMap != null && elementMap.isEmpty();
        // first do the nodes;
        for (Node node : graph.nodeSet()) {
            AspectNode nodeImage = result.addNode(node.getNumber());
            // update the maps
            elementMap.putNode(node, nodeImage);
        }
        // look for node aspect indicators
        // and put all correct aspect vales in a map
        Map<Edge,AspectLabel> edgeDataMap = new HashMap<>();
        for (Edge edge : graph.edgeSet()) {
            AspectLabel label = parser.parse(edge.label().text(), role);
            if (label.isNodeOnly()) {
                AspectNode sourceImage = elementMap.getNode(edge.source());
                assert sourceImage != null;
                sourceImage.addLabel(label);
            } else {
                edgeDataMap.put(edge, label);
            }
        }
        // Now iterate over the remaining edges
        for (Map.Entry<Edge,AspectLabel> entry : edgeDataMap.entrySet()) {
            Edge edge = entry.getKey();
            AspectLabel label = entry.getValue();
            AspectNode sourceImage = elementMap.getNode(edge.source());
            assert sourceImage != null;
            AspectNode targetImage = elementMap.getNode(edge.target());
            assert targetImage != null;
            AspectEdge edgeImage = result.addEdge(sourceImage, label, targetImage);
            elementMap.putEdge(edge, edgeImage);
        }
        GraphInfo.transferAll(graph, result, elementMap);
        result.setFixed();
        return result;
    }

    /** Creates an empty, fixed, named aspect graph, with a given graph role. */
    public static AspectGraph emptyGraph(String name, GraphRole role, boolean simple) {
        AspectGraph result = new AspectGraph(name, role, simple);
        result.setFixed();
        return result;
    }

    /**
     * Merges a given set of graphs into a single graph.
     * Nodes with the same {@link AspectKind#ID} value are merged,
     * all other nodes are kept distinct.
     * The merged graph is layed out by placing the original graphs next to one another.
     * @return a merged aspect graph or {@code null} if the set of input graphs is empty
     */
    public static AspectGraph mergeGraphs(Collection<AspectGraph> graphs) {
        if (graphs.size() == 0) {
            return null;
        }
        // Compute name and layout boundaries
        StringBuilder name = new StringBuilder();
        List<Point.Double> dimensions = new ArrayList<>();
        double globalMaxX = 0;
        double globalMaxY = 0;
        boolean simple = true;
        for (AspectGraph graph : graphs) {
            assert graph.getRole() == HOST;
            simple &= graph.isSimple();
            if (name.length() != 0) {
                name.append("_");
            }
            name.append(graph.getName());
            // compute dimensions of this graph
            double maxX = 0;
            double maxY = 0;
            LayoutMap layoutMap = GraphInfo.getLayoutMap(graph);
            if (layoutMap != null) {
                for (AspectNode node : graph.nodeSet()) {
                    JVertexLayout layout = layoutMap.nodeMap().get(node);
                    if (layout != null) {
                        Rectangle2D b = layout.getBounds();
                        maxX = Math.max(maxX, b.getX() + b.getWidth());
                        maxY = Math.max(maxY, b.getY() + b.getHeight());
                    }
                }
            }
            dimensions.add(new Point.Double(maxX, maxY));
            globalMaxX = Math.max(globalMaxX, maxX);
            globalMaxY = Math.max(globalMaxY, maxY);
        }
        // construct the result graph
        AspectGraph result = new AspectGraph(name.toString(), HOST, simple);
        LayoutMap newLayoutMap = new LayoutMap();
        FormatErrorSet newErrors = new FormatErrorSet();
        // Local bookkeeping.
        int nodeNr = 0;
        int index = 0;
        double offsetX = 0;
        double offsetY = 0;
        Map<AspectNode,AspectNode> nodeMap = new HashMap<>();
        Map<String,AspectNode> sharedNodes = new HashMap<>();
        var transfer = new AspectGraphMorphism(result);
        // Copy the graphs one by one into the combined graph
        for (AspectGraph graph : graphs) {
            nodeMap.clear();
            LayoutMap oldLayoutMap = GraphInfo.getLayoutMap(graph);
            // Copy the nodes
            for (AspectNode node : graph.nodeSet()) {
                AspectNode fresh = null;
                if (node.hasId()) {
                    String id = node.getId();
                    if (sharedNodes.containsKey(id)) {
                        nodeMap.put(node, sharedNodes.get(id));
                    } else {
                        fresh = node.clone(result, nodeNr++);
                        sharedNodes.put(id, fresh);
                    }
                } else {
                    fresh = node.clone(result, nodeNr++);
                }
                if (fresh != null) {
                    newLayoutMap.copyNodeWithOffset(fresh, node, oldLayoutMap, offsetX, offsetY);
                    nodeMap.put(node, fresh);
                    result.addNode(fresh);
                }
            }
            transfer.nodeMap().putAll(nodeMap);
            // Copy the edges
            for (AspectEdge edge : graph.edgeSet()) {
                AspectEdge fresh = new AspectEdge(nodeMap.get(edge.source()), edge.label(),
                    nodeMap.get(edge.target()), edge.getNumber());
                newLayoutMap.copyEdgeWithOffset(fresh, edge, oldLayoutMap, offsetX, offsetY);
                result.addEdgeContext(fresh);
                transfer.putEdge(edge, fresh);
            }
            // Copy the errors
            for (FormatError oldError : graph.getErrors()) {
                newErrors.add("Error in start graph '%s': %s", graph.getName(), oldError);
            }
            // Move the offsets
            if (globalMaxX > globalMaxY) {
                offsetY = offsetY + dimensions.get(index).getY() + 50;
            } else {
                offsetX = offsetX + dimensions.get(index).getX() + 50;
            }
            index++;
        }

        // Finalise combined graph.
        GraphInfo.setLayoutMap(result, newLayoutMap);
        result.setErrors(newErrors.apply(transfer));
        result.setFixed();
        return result;
    }

    /** The singleton aspect parser instance. */
    private static final AspectParser parser = AspectParser.getInstance();

    /** Debug flag. */
    static private final boolean DEBUG = false;

    /** Factory for AspectGraph elements. */
    public static class AspectFactory extends ElementFactory<AspectNode,AspectEdge> {
        /** Creates a factory for a given graph. */
        public AspectFactory(AspectGraph graph) {
            this.graph = graph;
        }

        @Override
        protected AspectNode newNode(int nr) {
            return new AspectNode(nr, this.graph);
        }

        @Override
        public AspectLabel createLabel(String text) {
            return AspectParser.getInstance().parse(text, this.graph.getRole());
        }

        @Override
        public AspectEdge createEdge(AspectNode source, Label label, AspectNode target) {
            int nr = 0;
            AspectLabel aLabel = (AspectLabel) label;
            if (aLabel.has(AspectKind.REMARK)) {
                nr = this.remarkCount.getNext();
            } else {
                // non-remark edges are all numbered 0
                // nr = this.graph.getEdgeNumber();
            }
            return new AspectEdge(source, (AspectLabel) label, target, nr);
        }

        /** Number of remark edges encountered thus far. */
        private final Dispenser remarkCount = Dispenser.counter();

        @Override
        public AspectGraphMorphism createMorphism() {
            return new AspectGraphMorphism(this.graph);
        }

        /** The graph role of the created elements. */
        private final AspectGraph graph;
    }

    /** Mapping from one aspect graph to another. */
    public static class AspectGraphMorphism extends Morphism<AspectNode,AspectEdge> {
        /** Constructs a new, empty map to a given graph. */
        public AspectGraphMorphism(AspectGraph target) {
            super(target.getFactory());
            assert target.getRole().inGrammar();
            this.target = target;
        }

        /** Returns the target graph of this morphism. */
        public AspectGraph getTarget() {
            return this.target;
        }

        /** The target graph of this morphism. */
        private final AspectGraph target;

        @Override
        public @Nullable AspectEdge mapEdge(AspectEdge key) {
            var result = super.mapEdge(key);
            if (result != null) {
                key
                    .getAspects()
                    .values()
                    .stream()
                    .filter(a -> !result.has(a.getCategory()))
                    .forEach(result::set);
            }
            return result;
        }

        @Override
        public AspectGraphMorphism newMap() {
            return new AspectGraphMorphism(this.target);
        }
    }

    /** Mapping from one aspect graph to another. */
    public static class AspectGraphUnwrapper extends AspectGraphMorphism {
        /** Constructs a new, empty map to a given target graph. */
        public AspectGraphUnwrapper(AspectGraph target) {
            super(target);
        }

        @Override
        public Label mapLabel(Label label) {
            return ((AspectLabel) label).unwrap();
        }
    }

    private static class AspectToPlainMap
        extends AGraphMap<AspectNode,AspectEdge,PlainNode,PlainEdge> {
        /** Constructs a new, empty map. */
        public AspectToPlainMap() {
            super(PlainFactory.instance());
        }

        @Override
        public PlainEdge createImage(AspectEdge key) {
            PlainNode imageSource = getNode(key.source());
            if (imageSource == null) {
                return null;
            }
            PlainNode imageTarget = getNode(key.target());
            if (imageTarget == null) {
                return null;
            }
            return getFactory().createEdge(imageSource, key.getPlainLabel(), imageTarget);
        }
    }

    /**
     * Graph element map from a plain graph to an aspect graph.
     * @author Arend Rensink
     * @version $Revision$
     */
    private static class GraphToAspectMap extends AGraphMap<Node,Edge,AspectNode,AspectEdge> {
        /** Creates a fresh, empty map. */
        public GraphToAspectMap(AspectGraph graph) {
            super(graph.getFactory());
        }
    }

    /** Construction status of an {@link AspectGraph}. */
    static enum Status {
        /** In the process of being built up; nodes and edges are being added. */
        NEW,
        /** In the process of being normalised. */
        NORMALISING,
        /** Completely fixed. */
        FIXED,;
    }
}

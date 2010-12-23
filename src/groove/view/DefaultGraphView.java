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
 * $Id: AspectualGraphView.java,v 1.18 2008-01-30 09:33:25 iovka Exp $
 */
package groove.view;

import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.TypeGraph;
import groove.graph.TypeLabel;
import groove.graph.algebra.OperatorEdge;
import groove.graph.algebra.ValueNode;
import groove.trans.DefaultHostGraph;
import groove.trans.HostEdge;
import groove.trans.HostFactory;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.SystemProperties;
import groove.util.Pair;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectNode;
import groove.view.aspect.AttributeElementFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Aspectual view upon an attributed graph. The attribute values are represented
 * by {@link ValueNode}s with self-{@link OperatorEdge}s.
 * @author Arend Rensink
 * @version $Revision $
 */
public class DefaultGraphView implements GraphView {
    /**
     * Constructs an instance from a given aspect graph view.
     * @see GraphInfo#getName(Graph)
     */
    public DefaultGraphView(AspectGraph view, SystemProperties properties) {
        view.testFixed(true);
        this.view = view;
        this.attributeFactory = createAttributeFactory(properties);
        // we fix the view; is it conceptually right to do that here?
        view.setFixed();
        String name = GraphInfo.getName(view);
        this.name = name == null ? "" : name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public AspectGraph getView() {
        return this.view;
    }

    public HostGraph toModel() throws FormatException {
        initialise();
        if (this.model == null) {
            throw new FormatException(getErrors());
        } else {
            return this.model;
        }
    }

    public List<FormatError> getErrors() {
        initialise();
        return this.errors;
    }

    @Override
    public ViewToHostMap getMap() {
        initialise();
        return this.viewToModelMap;
    }

    /**
     * Changes the system properties under which the model is to be created.
     */
    public void setProperties(SystemProperties properties) {
        AttributeElementFactory newFactory = createAttributeFactory(properties);
        if (!newFactory.equals(this.attributeFactory)) {
            this.attributeFactory = newFactory;
            invalidate();
        }
    }

    /** Changes the type graph under against which the model should be tested. */
    @Override
    public void setType(TypeGraph type) {
        if (this.type != type) {
            this.type = type;
            invalidate();
        }
    }

    /** Returns the set of labels used in this graph. */
    public Set<TypeLabel> getLabels() {
        initialise();
        return this.labelSet;
    }

    /** Constructs the model and associated data structures from the view. */
    private void initialise() {
        // first test if there is something to be done
        if (this.errors == null) {
            this.labelSet = new HashSet<TypeLabel>();
            Pair<HostGraph,ViewToHostMap> modelPlusMap =
                computeModel(this.view);
            this.model = modelPlusMap.one();
            this.viewToModelMap = modelPlusMap.two();
            this.errors = GraphInfo.getErrors(this.model);
        }
    }

    /**
     * Resets the constructed fields of this view to {@code null}, so that they
     * will be reconstructed again.
     */
    private void invalidate() {
        this.errors = null;
        this.model = null;
        this.viewToModelMap = null;
        this.labelSet = null;
    }

    /** Factory method. */
    private AttributeElementFactory createAttributeFactory(
            SystemProperties properties) {
        return new AttributeElementFactory(this.view, properties);
    }

    /**
     * Computes a fresh model from a given aspect graph, together with a mapping
     * from the aspect graph's node to the (fresh) graph nodes.
     */
    private Pair<HostGraph,ViewToHostMap> computeModel(AspectGraph view) {
        Set<FormatError> errors = new TreeSet<FormatError>(view.getErrors());
        HostGraph model = createGraph();
        // we need to record the view-to-model element map for layout transfer
        ViewToHostMap elementMap = new ViewToHostMap();
        // copy the nodes from view to model
        for (AspectNode viewNode : view.nodeSet()) {
            processViewNode(model, elementMap, viewNode);
        }
        // copy the edges from view to model
        for (AspectEdge viewEdge : view.edgeSet()) {
            try {
                processViewEdge(model, elementMap, viewEdge);
            } catch (FormatException exc) {
                errors.addAll(exc.getErrors());
            }
        }
        // remove isolated value nodes from the result graph
        for (HostNode modelNode : elementMap.nodeMap().values()) {
            if (modelNode instanceof ValueNode
                && model.edgeSet(modelNode).isEmpty()) {
                // the node is an isolated value node; remove it
                model.removeNode(modelNode);
            }
        }
        // test against the type graph, if any
        if (this.type != null) {
            Collection<FormatError> typeErrors;
            try {
                TypeGraph.Typing<HostNode,HostEdge> typing =
                    this.type.checkTyping(model);
                typeErrors = new TreeSet<FormatError>();
                for (Element elem : typing.getAbstractElements()) {
                    if (elem instanceof HostNode) {
                        typeErrors.add(new FormatError(
                            "Graph may not contain abstract %s-node",
                            typing.getType((HostNode) elem), elem));
                    } else {
                        typeErrors.add(new FormatError(
                            "Graph may not contain abstract %s-edge",
                            ((HostEdge) elem).label(), elem));
                    }
                }
            } catch (FormatException e) {
                typeErrors = e.getErrors();
            }
            if (!typeErrors.isEmpty()) {
                // compute inverse element map
                Map<Element,Element> inverseMap =
                    new HashMap<Element,Element>();
                for (Map.Entry<AspectNode,? extends HostNode> nodeEntry : elementMap.nodeMap().entrySet()) {
                    inverseMap.put(nodeEntry.getValue(), nodeEntry.getKey());
                }
                for (Map.Entry<AspectEdge,? extends HostEdge> edgeEntry : elementMap.edgeMap().entrySet()) {
                    inverseMap.put(edgeEntry.getValue(), edgeEntry.getKey());
                }
                for (FormatError error : typeErrors) {
                    errors.add(error.transfer(inverseMap));
                }
            }
        }
        // transfer graph info such as layout from view to model
        GraphInfo.transfer(view, model, elementMap);
        GraphInfo.setErrors(model, errors);
        model.setFixed();
        return new Pair<HostGraph,ViewToHostMap>(model, elementMap);
    }

    /**
     * Processes the information in a view node by updating the model and
     * element map.
     */
    private void processViewNode(HostGraph model, ViewToHostMap elementMap,
            AspectNode viewNode) {
        boolean nodeInModel = !viewNode.isRemark();
        // include the node in the model if it is not virtual
        if (nodeInModel) {
            HostNode nodeImage = null;
            if (viewNode.isDataValue()) {
                nodeImage =
                    (ValueNode) this.attributeFactory.createValueNode(viewNode);
                model.addNode(nodeImage);
            } else {
                nodeImage = model.addNode(viewNode.getNumber());
            }
            elementMap.putNode(viewNode, nodeImage);
        }
    }

    /**
     * Processes the information in a view edge by updating the model, element
     * map and subtypes.
     * @throws FormatException if the presence of the edge signifies an error
     */
    private void processViewEdge(HostGraph model, ViewToHostMap elementMap,
            AspectEdge viewEdge) throws FormatException {
        if (viewEdge.isRemark()) {
            return;
        }
        HostNode modelSource = elementMap.getNode(viewEdge.source());
        assert modelSource != null : String.format(
            "Source of '%s' is not in element map %s", viewEdge.source(),
            elementMap);
        HostNode modelTarget = elementMap.getNode(viewEdge.target());
        assert modelTarget != null : String.format(
            "Target of '%s' is not in element map %s", viewEdge.target(),
            elementMap);
        TypeLabel modelLabel = viewEdge.getTypeLabel();
        assert !modelLabel.isDataType();
        HostEdge modelEdge =
            model.addEdge(modelSource, modelLabel, modelTarget);
        this.labelSet.add(modelLabel);
        elementMap.putEdge(viewEdge, modelEdge);
    }

    /**
     * Returns the graph factory used to construct the model.
     */
    private HostGraph createGraph() {
        return new DefaultHostGraph();
    }

    /** The name of the view. */
    private final String name;
    /** The view represented by this object. */
    private final AspectGraph view;
    /** The graph model that is being viewed. */
    private HostGraph model;
    /**
     * List of errors in the view that prevent the model from being constructed.
     */
    private List<FormatError> errors;
    /** Map from view to model nodes. */
    private ViewToHostMap viewToModelMap;
    /** Set of labels occurring in this graph. */
    private Set<TypeLabel> labelSet;
    /** The attribute element factory for this view. */
    private AttributeElementFactory attributeFactory;
    /** Optional type graph for this aspect graph. */
    private TypeGraph type;

    /** Mapping from aspect graph to type graph. */
    public static class ViewToHostMap extends
            ViewToModelMap<HostNode,TypeLabel,HostEdge> {
        /**
         * Creates a new, empty map.
         */
        public ViewToHostMap() {
            super(HostFactory.instance());
        }

        @Override
        public ViewToHostMap newMap() {
            return new ViewToHostMap();
        }
    }
}

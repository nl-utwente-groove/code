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

import static groove.view.aspect.AspectKind.UNTYPED;
import groove.algebra.Algebra;
import groove.algebra.AlgebraFamily;
import groove.algebra.Constant;
import groove.graph.Element;
import groove.graph.GraphInfo;
import groove.graph.LabelStore;
import groove.graph.TypeGraph;
import groove.graph.TypeLabel;
import groove.graph.algebra.ValueNode;
import groove.trans.DefaultHostGraph;
import groove.trans.HostEdge;
import groove.trans.HostFactory;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.SystemProperties;
import groove.util.Pair;
import groove.view.aspect.Aspect;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectKind;
import groove.view.aspect.AspectNode;
import groove.view.aspect.Predicate;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Graph-based model of a host graph graph. Attribute values are represented
 * by {@link ValueNode}s.
 * @author Arend Rensink
 * @version $Revision $
 */
public class HostModel extends GraphBasedModel<HostGraph> {
    /**
     * Constructs an instance from a given aspect graph.
     */
    public HostModel(AspectGraph source, SystemProperties properties) {
        super(source);
        source.testFixed(true);
        this.algebraFamily = getFamily(properties);
    }

    /** 
     * Constructs the host graph from this resource.
     * @throws FormatException if the resource contains errors. 
     */
    public DefaultHostGraph toHost() throws FormatException {
        initialise();
        if (this.model == null) {
            throw new FormatException(getErrors());
        } else {
            return this.model;
        }
    }

    @Override
    public DefaultHostGraph toResource() throws FormatException {
        return toHost();
    }

    @Override
    public List<FormatError> getErrors() {
        initialise();
        return this.errors;
    }

    @Override
    public HostModelMap getMap() {
        initialise();
        return this.hostModelMap;
    }

    /**
     * Changes the system properties under which the resource is to be created.
     */
    public void setProperties(SystemProperties properties) {
        AlgebraFamily newFamily = getFamily(properties);
        if (!newFamily.equals(this.algebraFamily)) {
            this.algebraFamily = newFamily;
            invalidate();
        }
    }

    /** Changes the type graph against which the model should be tested. */
    @Override
    public void setType(TypeGraph type) {
        if (this.type != type) {
            this.type = type;
            invalidate();
        }
    }

    @Override
    public void setLabelStore(LabelStore labelStore) {
        // Does nothing
    }

    /** Returns the set of labels used in this graph. */
    @Override
    public Set<TypeLabel> getLabels() {
        initialise();
        return this.labelSet == null ? Collections.<TypeLabel>emptySet()
                : this.labelSet;
    }

    /** 
     * Extracts the algebra family from a (possibly {@code null}) properties
     * object.
     */
    private AlgebraFamily getFamily(SystemProperties properties) {
        AlgebraFamily result;
        if (properties == null) {
            result = AlgebraFamily.getInstance();
        } else {
            result = AlgebraFamily.getInstance(properties.getAlgebraFamily());
        }
        return result;
    }

    /** Constructs the resource and associated data structures from the model. */
    private void initialise() {
        // first test if there is something to be done
        if (this.errors == null) {
            if (getSource().hasErrors()) {
                this.errors = getSource().getErrors();
            } else {
                this.labelSet = new HashSet<TypeLabel>();
                Pair<DefaultHostGraph,HostModelMap> modelPlusMap =
                    computeModel(getSource());
                this.model = modelPlusMap.one();
                this.hostModelMap = modelPlusMap.two();
                this.errors = GraphInfo.getErrors(this.model);
            }
        }
    }

    /**
     * Resets the constructed fields of this model to {@code null}, so that they
     * will be reconstructed again.
     */
    private void invalidate() {
        this.errors = null;
        this.model = null;
        this.hostModelMap = null;
        this.labelSet = null;
    }

    /**
     * Computes a fresh model from a given aspect graph, together with a mapping
     * from the aspect graph's node to the (fresh) graph nodes.
     */
    private Pair<DefaultHostGraph,HostModelMap> computeModel(AspectGraph source) {
        Set<FormatError> errors = new TreeSet<FormatError>(source.getErrors());
        DefaultHostGraph result = createGraph(source.getName());
        // we need to record the model-to-resource element map for layout transfer
        HostModelMap elementMap = new HostModelMap(result.getFactory());
        // copy the nodes from model to resource
        // first the non-value nodes because their numbers are fixed
        for (AspectNode modelNode : source.nodeSet()) {
            if (!modelNode.getAttrKind().isData()) {
                processModelNode(result, elementMap, modelNode);
            }
        }
        // then the value nodes because their numbers are generated
        for (AspectNode modelNode : source.nodeSet()) {
            if (modelNode.getAttrKind().isData()) {
                processModelNode(result, elementMap, modelNode);
            }
        }
        // copy the edges from model to resource
        for (AspectEdge modelEdge : source.edgeSet()) {
            try {
                processModelEdge(result, elementMap, modelEdge);
            } catch (FormatException exc) {
                errors.addAll(exc.getErrors());
            }
        }
        // remove isolated value nodes from the result graph
        for (HostNode modelNode : elementMap.nodeMap().values()) {
            if (modelNode instanceof ValueNode
                && result.edgeSet(modelNode).isEmpty()) {
                // the node is an isolated value node; remove it
                result.removeNode(modelNode);
            }
        }
        // test against the type graph, if any
        if (this.type != null) {
            Collection<FormatError> typeErrors;
            try {
                TypeGraph.Typing<HostNode,HostEdge> typing =
                    this.type.checkTyping(result);
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
        // transfer graph info such as layout from model to resource
        GraphInfo.transfer(source, result, elementMap);
        GraphInfo.setErrors(result, errors);
        result.setFixed();
        return new Pair<DefaultHostGraph,HostModelMap>(result, elementMap);
    }

    /**
     * Processes the information in a model node by updating the model and
     * element map.
     */
    private void processModelNode(DefaultHostGraph result,
            HostModelMap elementMap, AspectNode modelNode) {
        // include the node in the model if it is not virtual
        if (!modelNode.getKind().isMeta()) {
            HostNode nodeImage = null;
            AspectKind attrType = modelNode.getAttrKind();
            if (attrType.isData()) {
                assert attrType != UNTYPED;
                Algebra<?> nodeAlgebra =
                    this.algebraFamily.getAlgebra(attrType.getName());
                Aspect dataType = modelNode.getAttrAspect();
                String symbol = ((Constant) dataType.getContent()).getSymbol();
                nodeImage =
                    result.getFactory().createNodeFromString(nodeAlgebra,
                        symbol);
                result.addNode(nodeImage);
            } else {
                nodeImage = result.addNode(modelNode.getNumber());
            }
            elementMap.putNode(modelNode, nodeImage);
        }
    }

    /**
     * Processes the information in a model edge by updating the resource, element
     * map and subtypes.
     * @throws FormatException if the presence of the edge signifies an error
     */
    private void processModelEdge(HostGraph result, HostModelMap elementMap,
            AspectEdge modelEdge) throws FormatException {
        if (modelEdge.getKind().isMeta()) {
            return;
        }
        HostNode hostSource = elementMap.getNode(modelEdge.source());
        assert hostSource != null : String.format(
            "Source of '%s' is not in element map %s", modelEdge.source(),
            elementMap);
        HostNode hostNode = elementMap.getNode(modelEdge.target());
        assert hostNode != null : String.format(
            "Target of '%s' is not in element map %s", modelEdge.target(),
            elementMap);
        TypeLabel hostLabel = modelEdge.getTypeLabel();
        assert hostLabel == null || !hostLabel.isDataType();

        if (modelEdge.isPredicate()) {
            Predicate pred = modelEdge.getPredicate();
            // Create the value node.
            Algebra<?> nodeAlgebra =
                this.algebraFamily.getAlgebra(pred.getSignature());
            hostNode =
                result.getFactory().createNodeFromString(nodeAlgebra,
                    pred.getValue().getSymbol());
            result.addNode(hostNode);

            // Update the label for the edge.
            hostLabel = result.getFactory().createLabel(pred.getName());
        }

        HostEdge hostEdge = result.addEdge(hostSource, hostLabel, hostNode);
        this.labelSet.add(hostLabel);
        elementMap.putEdge(modelEdge, hostEdge);
    }

    /**
     * Callback method to create the host graph.
     */
    private DefaultHostGraph createGraph(String name) {
        return new DefaultHostGraph(name);
    }

    /** The host graph that is being constructed. */
    private DefaultHostGraph model;
    /**
     * List of errors in the model that prevent the resource from being constructed.
     */
    private List<FormatError> errors;
    /** Map from model to resource nodes. */
    private HostModelMap hostModelMap;
    /** Set of labels occurring in this graph. */
    private Set<TypeLabel> labelSet;
    /** The attribute element factory for this model. */
    private AlgebraFamily algebraFamily;
    /** Optional type graph for this aspect graph. */
    private TypeGraph type;

    /** Mapping from aspect graph to type graph. */
    public static class HostModelMap extends ModelMap<HostNode,HostEdge> {
        /**
         * Creates a new, empty map.
         */
        public HostModelMap(HostFactory factory) {
            super(factory);
        }

        @Override
        public HostModelMap newMap() {
            return new HostModelMap(getFactory());
        }

        @Override
        public HostFactory getFactory() {
            return (HostFactory) super.getFactory();
        }
    }
}

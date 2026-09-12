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
package nl.utwente.groove.gui.view.cell;

import static nl.utwente.groove.grammar.aspect.AspectKind.PRODUCT;
import static nl.utwente.groove.grammar.aspect.AspectKind.REMARK;
import static nl.utwente.groove.gui.look.VisualKey.COLOR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.aspect.Aspect;
import nl.utwente.groove.grammar.aspect.AspectEdge;
import nl.utwente.groove.grammar.aspect.AspectElement;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectKind.Category;
import nl.utwente.groove.grammar.aspect.AspectLabel;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.grammar.aspect.AspectParser;
import nl.utwente.groove.grammar.model.GraphBasedModel;
import nl.utwente.groove.grammar.model.GraphBasedModel.TypeModelMap;
import nl.utwente.groove.grammar.rule.OperatorNode;
import nl.utwente.groove.grammar.rule.VariableNode;
import nl.utwente.groove.grammar.type.LabelPattern;
import nl.utwente.groove.grammar.type.TypeEdge;
import nl.utwente.groove.grammar.type.TypeElement;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.grammar.type.TypeNode;
import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.gui.look.Look;
import nl.utwente.groove.gui.look.Values;
import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.gui.view.AspectGraphViewModel;
import nl.utwente.groove.gui.view.AspectViewCell;
import nl.utwente.groove.gui.view.AspectViewCellErrors;
import nl.utwente.groove.gui.view.AspectViewVertex;
import nl.utwente.groove.gui.view.EditableLabels;
import nl.utwente.groove.gui.view.GraphViewMode;
import nl.utwente.groove.util.HTMLConverter;
import nl.utwente.groove.util.parse.FormatError;

/**
 * Vertex cell of an aspect graph.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class AspectVertexCell extends AViewVertex<AspectGraph> implements AspectViewVertex {
    /** Constructs a vertex cell for a given view model. */
    public AspectVertexCell(AspectGraphViewModel viewModel) {
        super(viewModel);
        this.aspects = new Aspect.Map(true, viewModel.getController().getGraphRole());
    }

    @Override
    public AspectGraphViewModel getViewModel() {
        return (AspectGraphViewModel) super.getViewModel();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<? extends AspectEdgeCell> getContext() {
        return (Iterator<? extends AspectEdgeCell>) super.getContext();
    }

    @Override
    public AspectNode getNode() {
        return (AspectNode) super.getNode();
    }

    @Override
    public Aspect.Map getAspects() {
        return this.aspects;
    }

    /** The aspects of the node wrapped by this cell. */
    private final Aspect.Map aspects;

    @SuppressWarnings("unchecked")
    @Override
    public Set<AspectEdge> getEdges() {
        return (Set<AspectEdge>) super.getEdges();
    }

    @Override
    public void initialise() {
        super.initialise();
        this.errors.clear();
        AspectNode node = getNode();
        getAspects().putAll(node.getAspects());
        var data = node.getKind(Category.SORT);
        if (data != null) {
            setLook(Look.getLookFor(data), true);
        } else if (node.has(PRODUCT)) {
            setLook(Look.getLookFor(PRODUCT), true);
        }
        getErrors().addErrors(node.getErrors(), true);
        setStale(COLOR);
    }

    @Override
    public void addEdge(Edge edge) {
        super.addEdge(edge);
        getErrors().addErrors(((AspectElement) edge).getErrors(), true);
    }

    @Override
    public boolean isCompatible(Edge edge) {
        if (super.isCompatible(edge)) {
            return true;
        } else if (((AspectEdge) edge).has(REMARK)) {
            return edge.source() == getNode() && edge.target() == getNode();
        }
        return false;
    }

    @Override
    public Set<AspectEdge> getExtraSelfEdges() {
        Set<AspectEdge> result = createEdgeSet();
        // add all outgoing edge cells that are source labels
        Iterator<? extends AspectEdgeCell> iter = getContext();
        while (iter.hasNext()) {
            AspectEdgeCell edge = iter.next();
            if (edge.getSourceVertex() == this && edge.isSourceLabel()) {
                result.addAll(edge.getEdges());
            }
        }
        return result;
    }

    @Override
    public void setNodeFixed() {
        getNode().setFixed();
        if (getNode().hasErrors()) {
            getErrors().addErrors(getNode().getErrors(), true);
            setStale(VisualKey.ERROR);
        }
    }

    @Override
    public @Nullable String getNodeIdString() {
        if (getAspects().containsKey(Category.NESTING)) {
            return null;
        } else if (getNode().has(Category.SORT)) {
            // this is an expression or variable node
            if (getNode().hasValue()) {
                return null;
            } else {
                return VariableNode.TO_STRING_PREFIX + getNode().getNumber();
            }
        } else if (getNode().has(PRODUCT)) {
            // delegate the identity string to a corresponding product node
            return OperatorNode.TO_STRING_PREFIX + getNode().getNumber();
        } else {
            return super.getNodeIdString();
        }
    }

    @Override
    protected StringBuilder getNodeDescription() {
        StringBuilder result = new StringBuilder();
        if (hasErrors()) {
            for (FormatError error : getErrors()) {
                if (result.length() > 0) {
                    result.append("<br>");
                }
                result.append(Values.getSeverityTag(error.getSeverity()).on(error.toString()));
            }
        } else {
            if (getNode().has(Category.SORT)) {
                if (getNode().hasValue()) {
                    result.append("Expression node");
                } else {
                    result.append("Variable node");
                }
            } else if (getNode().has(PRODUCT)) {
                result.append("Product node");
            } else {
                result.append(super.getNodeDescription());
            }
            var roleAspect = getAspects().get(Category.ROLE);
            if (roleAspect != null) {
                HTMLConverter.toUppercase(result, false);
                result.insert(0, " ");
                result.insert(0, AspectGraphViewModel.ROLE_NAMES.get(roleAspect.getKind()));
                result
                    .append("<br>" + AspectGraphViewModel.ROLE_DESCRIPTIONS.get(roleAspect.getKind()));
            }
        }
        return result;
    }

    @Override
    public Collection<? extends Label> getLabels() {
        Collection<TypeElement> result = new ArrayList<>();
        if (!getAspects().containsKey(Category.NESTING)) {
            for (Edge edge : getEdges()) {
                TypeEdge key = getKey(edge);
                if (key != null) {
                    result.add(key);
                }
            }
            // add additional self-edges
            for (var edge : getExtraSelfEdges()) {
                TypeEdge key = getKey(edge);
                if (key != null) {
                    result.add(key);
                }
            }
            result.addAll(getNodeKeys());
        }
        return result;
    }

    @Override
    public Collection<? extends Label> getKeys() {
        Collection<TypeElement> result = new ArrayList<>();
        if (!getAspects().containsKey(Category.NESTING)) {
            for (Edge edge : getEdges()) {
                TypeEdge key = getKey(edge);
                if (key != null) {
                    result.add(key);
                }
            }
            // add incident edges
            var edges = getContext();
            while (edges.hasNext()) {
                for (var edge : edges.next().getEdges()) {
                    TypeEdge key = getKey(edge);
                    if (key != null) {
                        result.add(key);
                    }
                }
            }
            result.addAll(getNodeKeys());
        }
        return result;
    }

    @Override
    protected Collection<TypeNode> getNodeKeys() {
        List<TypeNode> result = new ArrayList<>();
        TypeModelMap typeMap = getTypeMap();
        if (typeMap != null) {
            TypeNode type = typeMap.getNode(getNode());
            if (type != null) {
                result.add(type);
            }
        }
        return result;
    }

    @Override
    public @Nullable TypeEdge getKey(Edge edge) {
        TypeModelMap typeMap = getTypeMap();
        return typeMap == null
            ? null
            : typeMap.getEdge(edge);
    }

    private @Nullable TypeModelMap getTypeMap() {
        return getResourceModel().getTypeMap();
    }

    @Override
    public GraphBasedModel<?> getResourceModel() {
        return getViewModel().getResourceModel();
    }

    @Override
    public TypeGraph getTypeGraph() {
        return getViewModel().getTypeGraph();
    }

    @Override
    protected Set<Look> getStructuralLooks() {
        if (isNodeEdge()) {
            return EnumSet.of(Look.NODIFIED);
        } else if (getNode().hasGraphRole(GraphRole.TYPE)
            && !getAspects().containsKey(Category.SORT)) {
            return getNode().has(REMARK)
                ? EnumSet.of(Look.TYPE_REMARK)
                : EnumSet.of(Look.TYPE);
        } else {
            return Look.getLooksFor(getAspects());
        }
    }

    @Override
    public boolean isNodeEdge() {
        var canvas = getCanvas();
        return canvas != null && canvas.getMode() != GraphViewMode.EDIT_MODE
            && getEdgeLabelPattern() != null;
    }

    @Override
    public @Nullable LabelPattern getEdgeLabelPattern() {
        LabelPattern result = null;
        if (getNode().getGraphRole() == GraphRole.HOST) {
            TypeNode typeNode = getNodeType();
            if (typeNode != null) {
                result = typeNode.getLabelPattern();
            }
        }
        return result;
    }

    @Override
    public @Nullable TypeNode getNodeType() {
        TypeModelMap typeMap = getTypeMap();
        return typeMap == null
            ? null
            : typeMap.getNode(getNode());
    }

    @Override
    public void refreshEditableLabels() {
        // collect the node and edge information
        var labels = getEditableLabels();
        labels.clear();
        labels.addLabels(getNode().getNodeLabels());
        labels.addEdges(getEdges());
    }

    @Override
    public void applyEditableLabels(AspectGraph graph) {
        AspectNode node = new AspectNode(getNode().getNumber(), graph);
        AspectParser parser = AspectParser.getInstance();
        List<AspectLabel> edgeLabels = new ArrayList<>();
        for (String text : getEditableLabels()) {
            AspectLabel label = parser.parse(text, graph.getRole());
            if (label.isNodeOnly()) {
                node.addLabel(label);
            } else {
                // don't process the edge labels yet, as the node is not
                // yet completely determined
                edgeLabels.add(label);
            }
        }
        node.setParsed();
        // collect remark edges
        boolean hasRemark = false;
        StringBuilder remarkText = new StringBuilder();
        // collect edges to be added explicitly
        List<AspectEdge> newEdges = new ArrayList<>();
        // now process the edge labels
        for (AspectLabel label : edgeLabels) {
            if (label.has(REMARK)) {
                if (hasRemark) {
                    remarkText.append(EditableLabels.NEWLINE);
                }
                remarkText.append(label.getInnerText());
                hasRemark = true;
            } else {
                AspectEdge edge = new AspectEdge(node, label, node);
                newEdges.add(edge);
            }
        }
        // turn the collected remark text into a single edge
        if (hasRemark) {
            remarkText.insert(0, REMARK.getPrefix());
            AspectEdge edge
                = new AspectEdge(node, parser.parse(remarkText.toString(), graph.getRole()), node);
            newEdges.add(edge);
        }
        setNode(node);
        initialise();
        for (AspectEdge edge : newEdges) {
            edge.setParsed();
            addEdge(edge);
        }
        setStale(VisualKey.refreshables());
        // attributes will be refreshed upon the call to setNodeFixed()
    }

    @Override
    public EditableLabels getEditableLabels() {
        return this.labels;
    }

    @Override
    public void setEditableLabels(EditableLabels labels) {
        // a fresh object is needed, otherwise undo does not work
        this.labels = new EditableLabels(labels);
    }

    /** The editable labels of this cell. */
    private EditableLabels labels = new EditableLabels();

    @Override
    public AspectVertexCell clone() {
        AspectVertexCell result = (AspectVertexCell) super.clone();
        result.labels = new EditableLabels(this.labels);
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <E extends Edge> Comparator<E> edgeComparator() {
        return (Comparator<E>) AspectViewCell.EDGE_COMPARATOR;
    }

    @Override
    public AspectViewCellErrors getErrors() {
        return this.errors;
    }

    /** The errors of this cell. */
    private final AspectViewCellErrors errors = new AspectViewCellErrors(this);
}

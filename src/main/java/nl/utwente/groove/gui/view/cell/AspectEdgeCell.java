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

import static nl.utwente.groove.grammar.aspect.AspectKind.ARGUMENT;
import static nl.utwente.groove.grammar.aspect.AspectKind.PATH;
import static nl.utwente.groove.gui.look.VisualKey.COLOR;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.aspect.Aspect;
import nl.utwente.groove.grammar.aspect.AspectEdge;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectKind;
import nl.utwente.groove.grammar.aspect.AspectKind.Category;
import nl.utwente.groove.grammar.aspect.AspectLabel;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.grammar.aspect.AspectParser;
import nl.utwente.groove.grammar.model.GraphBasedModel;
import nl.utwente.groove.grammar.model.GraphBasedModel.TypeModelMap;
import nl.utwente.groove.grammar.rule.RuleLabel;
import nl.utwente.groove.grammar.type.TypeEdge;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.EdgeComparator;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.gui.look.Look;
import nl.utwente.groove.gui.look.Values;
import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.gui.view.AspectGraphViewModel;
import nl.utwente.groove.gui.view.AspectViewCell;
import nl.utwente.groove.gui.view.AspectViewCellErrors;
import nl.utwente.groove.gui.view.AspectViewEdge;
import nl.utwente.groove.gui.view.EditableLabels;
import nl.utwente.groove.util.HTMLConverter;
import nl.utwente.groove.util.parse.FormatError;

/**
 * Edge cell of an aspect graph.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class AspectEdgeCell extends AViewEdge<AspectGraph> implements AspectViewEdge {
    /** Constructs an edge cell for a given view model. */
    public AspectEdgeCell(AspectGraphViewModel viewModel) {
        super(viewModel);
        this.aspects = new Aspect.Map(false, viewModel.getController().getGraphRole());
    }

    @Override
    public AspectGraphViewModel getViewModel() {
        return (AspectGraphViewModel) super.getViewModel();
    }

    @Override
    public Aspect.Map getAspects() {
        return this.aspects;
    }

    /** The aspects of the edges wrapped by this cell. */
    private final Aspect.Map aspects;

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<? extends AspectVertexCell> getContext() {
        return (Iterator<? extends AspectVertexCell>) super.getContext();
    }

    @Override
    public @Nullable AspectVertexCell getSourceVertex() {
        return (AspectVertexCell) super.getSourceVertex();
    }

    @Override
    public @Nullable AspectVertexCell getTargetVertex() {
        return (AspectVertexCell) super.getTargetVertex();
    }

    @Override
    public AspectNode getSourceNode() {
        return (AspectNode) super.getSourceNode();
    }

    @Override
    public AspectNode getTargetNode() {
        return (AspectNode) super.getTargetNode();
    }

    @Override
    public boolean isNodeEdgeIn() {
        var target = getTargetVertex();
        return target != null && target.isNodeEdge();
    }

    @Override
    public boolean isNodeEdgeOut() {
        var source = getSourceVertex();
        return source != null && source.isNodeEdge();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<AspectEdge> getEdges() {
        return (Set<AspectEdge>) super.getEdges();
    }

    @Override
    public @Nullable AspectEdge getEdge() {
        return (AspectEdge) super.getEdge();
    }

    @Override
    public void initialise() {
        super.initialise();
        this.errors.clear();
    }

    @Override
    public boolean isCompatible(Edge edge) {
        if (!super.isCompatible(edge)) {
            return false;
        }
        var myEdge = getEdge();
        if (myEdge != null && !((AspectEdge) edge).isCompatible(myEdge)) {
            return false;
        }
        return true;
    }

    @Override
    public void addEdge(Edge e) {
        AspectEdge edge = (AspectEdge) e;
        AspectEdge oldEdge = getEdge();
        if (oldEdge == null || getAspects().has(AspectKind.REMARK)) {
            this.aspects.putAll(edge.getAspects());
        }
        FormatError error = null;
        if (edge.getRole() != EdgeRole.BINARY) {
            error = new FormatError("Node label '%s' not allowed on edges", edge.label(), this);
        } else if (oldEdge != null && !edge.isCompatible(oldEdge)) {
            error = new FormatError("Conflicting aspects in edge labels %s and %s", oldEdge.label(),
                edge.label(), this);
        }
        if (error != null) {
            edge = new AspectEdge(edge.source(), edge.label(), edge.target());
            edge.addError(error);
            edge.setFixed();
        }
        super.addEdge(edge);
        updateLook(edge);
        setStale(COLOR);
    }

    /** Updates the looks on the basis of a newly added edge. */
    private void updateLook(AspectEdge edge) {
        RuleLabel ruleLabel = edge.getRuleLabel();
        if (ruleLabel != null) {
            if (ruleLabel.isEmpty() && !getAspects().has(AspectKind.CREATOR)
                || ruleLabel.isNeg(n -> n.getOperand().isEmpty())) {
                // remove edge arrow
                setLook(Look.NO_ARROW, true);
            } else if (!ruleLabel.isAtom() && !ruleLabel.isNeg(n -> n.getOperand().isAtom())) {
                // a negated atom counts as plain, not as a regular expression
                setLook(Look.REGULAR, true);
            }
        }
        if (edge.has(AspectKind.COMPOSITE)) {
            setLook(Look.COMPOSITE, true);
        }
        getErrors().addErrors(edge.getErrors(), true);
        setStale(VisualKey.ERROR);
    }

    @Override
    protected StringBuilder getEdgeDescription() {
        StringBuilder result = new StringBuilder();
        if (hasErrors()) {
            for (FormatError error : getErrors()) {
                if (result.length() > 0) {
                    result.append("<br>");
                }
                result.append(Values.getSeverityTag(error.getSeverity()).on(error.toString()));
            }
        } else {
            var edge = getEdge();
            if (edge != null && edge.has(ARGUMENT)) {
                result.append("Argument edge");
            } else if (edge != null && edge.has(Category.SORT)) {
                result.append("Operation edge");
            } else {
                result.append(super.getEdgeDescription());
            }
            var roleAspect = this.aspects.get(Category.ROLE);
            if (roleAspect != null) {
                result
                    .append("<br>" + AspectGraphViewModel.ROLE_DESCRIPTIONS.get(roleAspect.getKind()));
            }
        }
        return result;
    }

    @Override
    protected StringBuilder getEdgeKindDescription() {
        StringBuilder result = super.getEdgeKindDescription();
        var roleAspect = this.aspects.get(Category.ROLE);
        if (roleAspect != null) {
            HTMLConverter.toUppercase(result, false);
            result.insert(0, " ");
            result.insert(0, AspectGraphViewModel.ROLE_NAMES.get(roleAspect.getKind()));
        }
        return result;
    }

    @Override
    public Collection<? extends Label> getKeys() {
        if (this.aspects.containsKey(Category.NESTING)) {
            return Collections.emptySet();
        } else {
            return super.getKeys();
        }
    }

    @Override
    public @Nullable TypeEdge getKey(Edge edge) {
        TypeEdge result = null;
        TypeModelMap typeMap = getTypeMap();
        if (typeMap != null) {
            result = typeMap.getEdge(edge);
        }
        return result;
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
    public boolean isSourceLabel() {
        if (getViewModel().getController().isShowValueNodes()) {
            return false;
        }
        if (getSourceNode().hasSort()) {
            return false;
        }
        if (!getTargetNode().hasSort()) {
            return false;
        }
        if (getTargetNode().hasId()) {
            return false;
        }
        if (getTargetNode().has(Category.PARAM)) {
            return false;
        }
        // regular expression edges cannot be source labels
        if (getEdges().stream().anyMatch(e -> e.has(PATH))) {
            return false;
        }
        var edge = getEdge();
        if (edge != null && edge.isNestedCount()) {
            return false;
        }
        if (getTargetNode().hasValue() || getTargetNode().hasExpression()) {
            return true;
        }
        // if the target has no further constraints (in the form of incoming edges), it's OK
        var target = getTargetVertex();
        if (target != null && target.getContextSize() == 1) {
            return true;
        }
        return false;
    }

    @Override
    protected Set<Look> getStructuralLooks() {
        if (isNodeEdgeIn()) {
            return EnumSet.of(Look.NODIFIED);
        } else {
            return Look.getLooksFor(getAspects());
        }
    }

    @Override
    public void refreshEditableLabels() {
        // collect the edge information
        var labels = getEditableLabels();
        labels.clear();
        labels.addEdges(getEdges());
    }

    @Override
    public void applyEditableLabels(AspectGraph graph) {
        boolean bidirectional = getLooks().contains(Look.BIDIRECTIONAL);
        initialise();
        AspectParser parser = AspectParser.getInstance();
        // collect remark edges
        boolean hasRemark = false;
        StringBuilder remarkText = new StringBuilder();
        for (String text : getEditableLabels()) {
            AspectLabel label = parser.parse(text, graph.getRole());
            if (label.has(AspectKind.REMARK)) {
                if (hasRemark) {
                    remarkText.append(EditableLabels.NEWLINE);
                }
                remarkText.append(label.getInnerText());
                hasRemark = true;
            } else {
                addEdges(label, bidirectional);
            }
        }
        // turn the collected remark text into a single edge
        if (hasRemark) {
            remarkText.insert(0, AspectKind.REMARK.getPrefix());
            addEdges(parser.parse(remarkText.toString(), graph.getRole()), bidirectional);
        }
        setStale(VisualKey.refreshables());
    }

    /** Adds an edge with a given label, and its reverse if the cell is bidirectional. */
    private void addEdges(AspectLabel label, boolean bidirectional) {
        AspectEdge edge = new AspectEdge(getSourceNode(), label, getTargetNode());
        edge.setParsed();
        addEdge(edge);
        if (bidirectional) {
            edge = new AspectEdge(getTargetNode(), label, getSourceNode());
            edge.setParsed();
            addEdge(edge);
        }
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
    public AspectEdgeCell clone() {
        AspectEdgeCell result = (AspectEdgeCell) super.clone();
        result.labels = new EditableLabels(this.labels);
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <E extends Edge> Comparator<E> edgeComparator() {
        return (Comparator<E>) COMPARATOR;
    }

    /** Orders the edges of a cell: remarks first, then by aspects, then by the edge order. */
    private final static Comparator<Edge> COMPARATOR = new Comparator<>() {
        @Override
        public int compare(Edge o1, Edge o2) {
            int result = AspectViewCell.REMARK_FIRST_COMPARATOR.compare(o1, o2);
            if (result != 0) {
                return result;
            }
            result = getAspects(o1).compareTo(getAspects(o2));
            if (result != 0) {
                return result;
            }
            return EdgeComparator.instance().compare(o1, o2);
        }

        private Aspect.Map getAspects(Edge e) {
            return ((AspectEdge) e).getAspects();
        }
    };

    @Override
    public AspectViewCellErrors getErrors() {
        return this.errors;
    }

    /** The errors of this cell. */
    private final AspectViewCellErrors errors = new AspectViewCellErrors(this);
}

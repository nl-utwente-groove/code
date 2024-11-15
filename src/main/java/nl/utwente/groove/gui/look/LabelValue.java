/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package nl.utwente.groove.gui.look;

import static nl.utwente.groove.grammar.aspect.AspectKind.COLOR;
import static nl.utwente.groove.grammar.aspect.AspectKind.EDGE;
import static nl.utwente.groove.grammar.aspect.AspectKind.IMPORT;
import static nl.utwente.groove.grammar.aspect.AspectKind.REMARK;
import static nl.utwente.groove.graph.EdgeRole.NODE_TYPE;
import static nl.utwente.groove.util.line.Line.Style.ITALIC;
import static nl.utwente.groove.util.line.Line.Style.UNDERLINE;

import java.awt.Color;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;

import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.control.CallStack;
import nl.utwente.groove.control.CtrlVar;
import nl.utwente.groove.control.Position;
import nl.utwente.groove.control.instance.Frame;
import nl.utwente.groove.control.template.Location;
import nl.utwente.groove.grammar.Action.Role;
import nl.utwente.groove.grammar.aspect.Aspect;
import nl.utwente.groove.grammar.aspect.AspectEdge;
import nl.utwente.groove.grammar.aspect.AspectKind;
import nl.utwente.groove.grammar.aspect.AspectKind.Category;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.grammar.host.ValueNode;
import nl.utwente.groove.grammar.model.GraphBasedModel;
import nl.utwente.groove.grammar.type.LabelPattern;
import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.gui.jgraph.AspectJEdge;
import nl.utwente.groove.gui.jgraph.AspectJGraph;
import nl.utwente.groove.gui.jgraph.AspectJVertex;
import nl.utwente.groove.gui.jgraph.CtrlJGraph;
import nl.utwente.groove.gui.jgraph.CtrlJVertex;
import nl.utwente.groove.gui.jgraph.JCell;
import nl.utwente.groove.gui.jgraph.JEdge;
import nl.utwente.groove.gui.jgraph.JGraph;
import nl.utwente.groove.gui.jgraph.JVertex;
import nl.utwente.groove.gui.jgraph.LTSJEdge;
import nl.utwente.groove.gui.jgraph.LTSJGraph;
import nl.utwente.groove.gui.jgraph.LTSJVertex;
import nl.utwente.groove.gui.look.MultiLabel.Direct;
import nl.utwente.groove.gui.tree.LabelTree;
import nl.utwente.groove.io.Util;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.lts.StartGraphState;
import nl.utwente.groove.lts.Status.Flag;
import nl.utwente.groove.util.Colors;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.line.Line;
import nl.utwente.groove.util.line.Line.Style;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Visual value refresher for the {@link VisualKey#LABEL}.
 * @author Arend Rensink
 * @version $Revision$
 */
public class LabelValue implements VisualValue<MultiLabel> {
    @Override
    public <G extends @NonNull Graph> MultiLabel get(JGraph<G> jGraph, JCell<G> cell) {
        MultiLabel result = null;
        if (cell instanceof JVertex) {
            result = getJVertexLabel(jGraph, (JVertex<G>) cell);
        } else if (cell instanceof JEdge) {
            result = getJEdgeLabel(jGraph, (JEdge<G>) cell);
        }
        return result;
    }

    /** Returns a list of lines together making up the label text of a vertex.
     * @param jGraph the (non-{@code null}) {@link JGraph} of the {@link JVertex}
     */
    protected <G extends @NonNull Graph> MultiLabel getJVertexLabel(JGraph<G> jGraph,
                                                                    JVertex<G> jVertex) {
        MultiLabel result;
        switch (jGraph.getGraphRole()) {
        case HOST:
            result = getHostNodeLabel((AspectJGraph) jGraph, (AspectJVertex) jVertex);
            break;
        case RULE:
            result = getRuleNodeLabel((AspectJGraph) jGraph, (AspectJVertex) jVertex);
            break;
        case TYPE:
            result = getTypeNodeLabel((AspectJGraph) jGraph, (AspectJVertex) jVertex);
            break;
        case LTS:
            result = getLTSJVertexLabel((LTSJGraph) jGraph, (LTSJVertex) jVertex);
            break;
        case CTRL:
            result = getCtrlJVertexLabel((CtrlJGraph) jGraph, (CtrlJVertex) jVertex);
            break;
        default:
            result = getBasicVertexLabel(jGraph, jVertex);
        }
        return result;
    }

    /** This implementation adds the data edges to the super result.
     * @param jGraph the (non-{@code null}) {@link JGraph} of the {@link JVertex}
     */
    private <G extends @NonNull Graph> MultiLabel getBasicVertexLabel(JGraph<G> jGraph,
                                                                      JVertex<G> jVertex) {
        MultiLabel result = new MultiLabel();
        // only add edges that have an unfiltered label
        addEdgeLabels(jGraph, jVertex, result);
        return result;
    }

    /** Recomputes the set of node lines for this aspect node.
     * The node lines consist of:
     * <ul>
     * <li> The internal node ID, if set to be shown
     * <li> The (optional) external node ID combined with (optional) node type
     * <li> Additional node type self-edges (which may exist in an untyped setting)
     * <li> Flag self-edges
     * <li> Regular self-edges
     * <li> Attributes stored as self-edges
     * <li> Pure attribute edges not stored as self-edges, if data nodes are not shown
     * </ul>
     * Node: self-edges displayed as node labels may be filtered
     * @param jGraph the (non-{@code null}) {@link JGraph} of the {@link JVertex}
     */
    private MultiLabel getHostNodeLabel(AspectJGraph jGraph, AspectJVertex jVertex) {
        AspectNode node = jVertex.getNode();
        node.testFixed(true);
        MultiLabel result = new MultiLabel(false);
        if (!jVertex.getLooks().contains(Look.NODIFIED)) {
            // the following used to include hasError() as a disjunct
            if (jGraph.isShowAspects()) {
                result.add(jVertex.getUserObject().toLines());
            } else {
                Line idLine = jGraph.isShowUserIds()
                    ? getUserIdLine(node)
                    : null;
                // show data constants correctly
                Line dataLine = getDataLine(node, idLine);
                if (dataLine != null) {
                    result.add(dataLine);
                    idLine = null;
                }
                // show the visible self-edges
                for (AspectEdge edge : jVertex.getEdges()) {
                    if (isVisible(jGraph, jVertex, edge)) {
                        Line line = edge.toLine(true, jVertex.getAspects());
                        if (edge.getRole() == NODE_TYPE) {
                            line = insertUserId(idLine, line);
                            idLine = null;
                        }
                        if (idLine != null) {
                            // we're not going to have any node types:
                            // add the node id on a separate line
                            result.add(idLine);
                            idLine = null;
                        }
                        if (showLoopSuffix(jVertex, edge)) {
                            line = line.append(LOOP_SUFFIX);
                        }
                        result.add(line);
                    }
                }
                if (idLine != null) {
                    // there weren't any node types (or other node label lines):
                    // add the node id on a separate line
                    result.add(idLine);
                }
            }
            for (AspectEdge edge : jVertex.getExtraSelfEdges()) {
                if (isVisible(jGraph, jVertex, edge)) {
                    result.add(edge.toLine(true, jVertex.getAspects()));
                }
            }
        }
        return result;
    }

    /**
     * Constructs an external node ID line for an aspect node, if the node has an ID aspect.
     */
    private Line getUserIdLine(AspectNode node) {
        return node.hasId()
            ? formatUserId(node.getId())
            : null;
    }

    /**
     * Constructs an external node ID line from a given string.
     */
    private Line formatUserId(String id) {
        return Line.atom(id).style(ITALIC).style(UNDERLINE);
    }

    /** Inserts an external node identifier in front of a given line, if the node identifier is not {@code null}. */
    private Line insertUserId(Line idLine, Line line) {
        return idLine == null
            ? line
            : idLine.append(TYPED_AS).append(line);
    }

    /** Recomputes the set of node lines for this aspect node.
     * @param jGraph the (non-{@code null}) {@link JGraph} of the {@link JVertex}
     */
    private MultiLabel getTypeNodeLabel(AspectJGraph jGraph, AspectJVertex jVertex) {
        AspectNode node = jVertex.getNode();
        node.testFixed(true);
        MultiLabel result = new MultiLabel();
        if (jGraph.isShowAspects()) {
            result.add(jVertex.getUserObject().toLines());
            for (AspectEdge edge : jVertex.getExtraSelfEdges()) {
                if (isVisible(jGraph, jVertex, edge)) {
                    Line line = edge.label().toLine();
                    // check for primitive type edges
                    var sortKind = edge.target().getKind(Category.SORT);
                    if (sortKind != null) {
                        line = line.append(Line.atom(sortKind.getSort().getName()));
                    }
                    result.add(line);
                }
            }
        } else {
            if (node.has(IMPORT)) {
                result.add(IMPORT_LINE);
            }
            // show data constants and variables correctly
            Line dataLine = getDataLine(node, null);
            if (dataLine != null) {
                result.add(dataLine);
            }
            // show the visible self-edges
            for (AspectEdge edge : jVertex.getEdges()) {
                if (isVisible(jGraph, jVertex, edge)) {
                    Line line = edge.toLine(true, jVertex.getAspects());
                    if (showLoopSuffix(jVertex, edge)) {
                        line = line.append(LOOP_SUFFIX);
                    }
                    result.add(line);
                }
            }
            for (AspectEdge edge : jVertex.getExtraSelfEdges()) {
                if (isVisible(jGraph, jVertex, edge)) {
                    result.add(edge.toLine(true, jVertex.getAspects()));
                }
            }
            if (node.has(EDGE)) {
                StringBuilder line = new StringBuilder();
                LabelPattern pattern = node.getEdgePattern();
                line.append(">> ");
                line.append(pattern.getLabel(pattern.getArgNames().toArray()));
                result.add(Line.atom(line.toString()));
            }
        }
        return result;
    }

    /** Recomputes the set of node lines for this aspect node.
     * @param jGraph the (non-{@code null}) {@link JGraph} of the {@link JVertex}     */
    private MultiLabel getRuleNodeLabel(AspectJGraph jGraph, AspectJVertex jVertex) {
        AspectNode node = jVertex.getNode();
        node.testFixed(true);
        MultiLabel result = new MultiLabel();
        // the following used to include hasError() as a disjunct
        if (jGraph.isShowAspects() || jVertex.getNode().hasErrors()) {
            result.add(jVertex.getUserObject().toLines());
            for (AspectEdge edge : jVertex.getExtraSelfEdges()) {
                if (isVisible(jGraph, jVertex, edge)) {
                    Line line = edge.label().toLine();
                    // check for assignment edges
                    var sortContent = edge.target().get(Category.SORT, Aspect::getContentString);
                    if (sortContent != null) {
                        line = line.append(POINTS_TO + sortContent);
                    }
                    result.add(line);
                }
            }
            Aspect color = node.get(COLOR);
            if (color != null) {
                result.add(Line.atom(color.toString()));
            }
        } else {
            Line idLine = getUserIdLine(node);
            // show the quantifier aspect correctly
            if (node.has(Category.NESTING)) {
                result.add(getQuantifierLines(node, idLine));
                idLine = null;
            }
            // show data constants and variables correctly
            Line data = getDataLine(node, idLine);
            if (data != null) {
                result.add(data);
                idLine = null;
            }
            // show the visible self-edges
            for (AspectEdge edge : jVertex.getEdges()) {
                if (isVisible(jGraph, jVertex, edge)) {
                    Line line = edge.toLine(true, jVertex.getAspects());
                    if (edge.getRole() == NODE_TYPE) {
                        line = insertUserId(idLine, line);
                        idLine = null;
                    }
                    if (idLine != null) {
                        result.add(idLine);
                        idLine = null;
                    }
                    if (showLoopSuffix(jVertex, edge)) {
                        line = line.append(LOOP_SUFFIX);
                    }
                    result.add(line);
                }
            }
            if (idLine != null) {
                // we're not going to have any node types:
                // add the node id on a separate line
                result.add(idLine);
            }
            for (AspectEdge edge : jVertex.getExtraSelfEdges()) {
                if (isVisible(jGraph, jVertex, edge)) {
                    result.add(edge.toLine(true, jVertex.getAspects()));
                }
            }
            Aspect color = node.get(COLOR);
            if (color != null) {
                StringBuilder text = new StringBuilder("& ");
                text.append(AspectKind.COLOR.getName());
                Line colorLine
                    = Line.atom(text.toString()).color(Colors.findColor(color.getContentString()));
                result.add(colorLine);
            }
        }
        return result;
    }

    /** Indicates if the label corresponding to a given node edge should be
     * suffixed by {@link #LOOP_SUFFIX}.
     */
    private boolean showLoopSuffix(AspectJVertex jVertex, AspectEdge edge) {
        if (edge.hasErrors()) {
            return false;
        }
        var jGraph = jVertex.getJGraph();
        if (jVertex.getJModel().getTypeGraph().isImplicit() && jGraph != null
            && jGraph.getGraphRole() != GraphRole.TYPE) {
            return false;
        }
        if (edge.getRole() != EdgeRole.BINARY) {
            return false;
        }
        if (edge.has(REMARK)) {
            return false;
        }
        return true;
    }

    /**
     * Returns the lines describing this node's main aspect.
     * Currently this just concerns a possible quantifier.
     */
    private MultiLabel getQuantifierLines(AspectNode node, Line id) {
        Line line = Line.empty();
        if (id != null) {
            line = line.append(id).append(TYPED_AS);
        }
        AspectKind nestingKind = node.getKind(Category.NESTING);
        if (nestingKind != null) {
            line = line.append(switch (nestingKind) {
            case FORALL -> FORALL_LINE;
            case FORALL_POS -> FORALL_POS_LINE;
            case EXISTS -> EXISTS_LINE;
            case EXISTS_OPT -> EXISTS_OPT_LINE;
            default -> throw Exceptions.UNREACHABLE;
            });
        }
        return MultiLabel.singleton(line, Direct.NONE);
    }

    /** This implementation adds the data edges to the super result.
     * @param jGraph the (non-{@code null}) {@link JGraph} of the {@link JVertex}
     */
    private MultiLabel getLTSJVertexLabel(LTSJGraph jGraph, LTSJVertex jVertex) {
        MultiLabel result = new MultiLabel();
        // show the node identity if required
        Line idLine = null;
        if (jGraph.isShowStateIdentities()) {
            GraphState state = jVertex.getNode();
            StringBuilder id = new StringBuilder(state.toString());
            idLine = formatUserId(id.toString());
        }
        if (jGraph.isShowStateStatus()) {
            Line statusLine = getStatus(jGraph, jVertex.getNode());
            if (idLine == null) {
                idLine = statusLine;
            } else {
                idLine = idLine.append(TYPED_AS).append(statusLine);
            }
        }
        if (idLine != null) {
            result.add(idLine);
        }
        boolean hasControl = false;
        if (jGraph.isShowControlStates()) {
            GraphState state = jVertex.getNode();
            Frame frame = state.getPrimeFrame();
            //if (frame.isDead()) {
            //    frame = frame.getPred();
            //    assert frame != null;
            //}
            Object[] stack = state.getFrameStack(frame);
            // NOTE: the following was surrounded by a condition that causes
            // no control info to be shown if we're in the start state, which
            // surely was never intended. See gh issue #775
            // if (!frame.isStart() || stack.length > 0) {
            int popCount = state.getActualFrame().getPopCount();
            result.add(getStackLine(frame.getLocation(), stack, popCount <= 0));
            hasControl = true;
            // }
            for (var sw : frame.getContext().outIterable()) {
                stack = CallStack.pop(stack);
                popCount--;
                result.add(getStackLine(sw.onFinish(), stack, popCount <= 0));
                hasControl = true;
            }
        }
        MultiLabel transLabels = new MultiLabel();
        // only add edges that have an unfiltered label
        boolean isShowAnchors = jGraph.isShowAnchors();
        boolean isShowInvariants = jGraph.isShowInvariants();
        for (Edge edge : jVertex.getEdges()) {
            GraphTransition trans = (GraphTransition) edge;
            if (trans.getAction().getRole() == Role.INVARIANT && !isShowInvariants) {
                continue;
            }
            if (isVisible(jGraph, jVertex, edge)) {
                Line line;
                if (isShowAnchors) {
                    line = Line.atom(((GraphTransition) edge).text(isShowAnchors));
                } else {
                    line = edge.label().toLine();
                }
                if (edge.getRole() == EdgeRole.BINARY) {
                    line = line.append(LOOP_SUFFIX);
                }
                if (trans.isInnerStep()) {
                    line = line.color(Values.RECIPE_COLOR);
                }
                transLabels.add(line);
            }
        }
        if (!jVertex.isAllOutVisible()) {
            transLabels.add(RESIDUAL_LINE);
        }
        // insert horizontal line if the state has both control and transition labels
        if (hasControl && !transLabels.isEmpty()) {
            // this solution is very ugly (in html), work on it!
            // result.add(Line.hrule());
        }
        result.addAll(transLabels);
        return result;
    }

    /** Returns the status line for a given state.
     * @param jGraph the (non-{@code null}) {@link JGraph} of the {@link JVertex}
     */
    private Line getStatus(LTSJGraph jGraph, GraphState state) {
        Line result;
        if (isResult(jGraph, state)) {
            result = this.resultLine;
        } else if (state instanceof StartGraphState && !state.isError() && !state.isFinal()) {
            result = this.startLine;
        } else {
            // determine main flag
            Flag main = null;
            if (state.isInner()) {
                main = Flag.INNER;
            } else if (state.isError()) {
                main = Flag.ERROR;
            } else if (state.isTransient()) {
                main = Flag.TRANSIENT;
            } else if (state.isFinal()) {
                main = Flag.FINAL;
            } else if (state.isFull()) {
                main = Flag.FULL;
            } else if (state.isClosed()) {
                main = Flag.CLOSED;
            }
            result = main == null
                ? this.openLine
                : getStatus(main);
        }
        return result;
    }

    private boolean isResult(LTSJGraph jGraph, GraphState state) {
        return jGraph.isResult(state);
    }

    /** Returns the status line for a given status flag. */
    private Line getStatus(Flag flag) {
        if (this.statusMap == null) {
            this.statusMap = new EnumMap<>(Flag.class);
            for (Flag f : Flag.values()) {
                String text = null;
                switch (f) {
                case CLOSED:
                case FULL:
                    text = "closed";
                    break;
                case ERROR:
                    text = "error";
                    break;
                case FINAL:
                    text = "final";
                    break;
                case INNER:
                    text = "internal";
                    break;
                case TRANSIENT:
                    text = "transient";
                    break;
                default:
                    // no annotation value
                }
                if (text != null) {
                    this.statusMap.put(f, Line.atom(text).style(Style.BOLD));
                }
            }
        }
        return this.statusMap.get(flag);
    }

    /** Map from flags to corresponding lines on state. */
    private Map<Flag,Line> statusMap;
    /** State line for the start state. */
    private final Line startLine = Line.atom("start").style(Style.BOLD);
    /** State line result states. */
    private final Line resultLine = Line.atom("result").style(Style.BOLD);
    /** State line for an open state. */
    private final Line openLine = Line.atom("open").style(Style.BOLD);

    private Line getStackLine(Location loc, Object[] values, boolean actual) {
        Line result = Line.empty();
        if (loc != null) {
            result = formatUserId(loc.toString());
            if (loc.hasVars()) {
                List<CtrlVar> vars = loc.getVars();
                StringBuilder content = new StringBuilder();
                content.append(" [");
                for (int i = 0; i < vars.size(); i++) {
                    if (i > 0) {
                        content.append(',');
                    }
                    content.append(vars.get(i).name());
                    content.append('=');
                    HostNode val = (HostNode) values[i];
                    if (val instanceof ValueNode) {
                        content.append(((ValueNode) val).getSymbol());
                    } else {
                        content.append(val);
                    }
                }
                content.append(']');
                result = result.append(content.toString());
            }
        }
        return actual
            ? result
            : result.color(Color.gray);
    }

    /**
     * Appends the bound variables to the lines, if this list is not empty
     * @param jGraph the (non-{@code null}) {@link JGraph} of the {@link JVertex}
     */
    private MultiLabel getCtrlJVertexLabel(CtrlJGraph jGraph, CtrlJVertex jVertex) {
        MultiLabel result = new MultiLabel();
        result.add(formatUserId(jVertex.getNode().toString()));
        Position<?,?> state = jVertex.getNode().getPosition();
        // add start/final/depth qualifiers
        Line qualifiers = Line.empty();
        if (state.isStart()) {
            qualifiers = qualifiers.append(START_LINE);
        }
        if (!state.isTrial()) {
            if (!qualifiers.isEmpty()) {
                qualifiers = qualifiers.append("/");
            }
            qualifiers = qualifiers
                .append(state.isDead()
                    ? DEAD_LINE
                    : FINAL_LINE);
        }
        if (!qualifiers.isEmpty()) {
            result.add(qualifiers.style(Style.BOLD));
        }
        if (state.getTransience() > 0) {
            result.add(Line.atom("transience = " + state.getTransience()));
        }
        // add location variables
        for (CtrlVar var : state.getVars()) {
            Line line = Line
                .atom(var.name())
                .append(TYPED_AS)
                .append(Line.atom(var.type().toString()).style(Style.BOLD));
            result.add(line);
        }
        // add self-edges
        addEdgeLabels(jGraph, jVertex, result);
        return result;
    }

    /** Returns a list of lines together making up the label text of a jEdge.
     * @param jGraph the (non-{@code null}) {@link JGraph} of the {@link JVertex}
     */
    protected <G extends @NonNull Graph> MultiLabel getJEdgeLabel(JGraph<G> jGraph,
                                                                  JEdge<G> jEdge) {
        MultiLabel result;
        switch (jGraph.getGraphRole()) {
        case HOST:
        case RULE:
        case TYPE:
            result = getAspectJEdgeLabel((AspectJGraph) jGraph, (AspectJEdge) jEdge);
            break;
        case LTS:
            result = getLTSJEdgeLabel((LTSJGraph) jGraph, (LTSJEdge) jEdge);
            break;
        default:
            result = getBasicJEdgeLabel(jGraph, jEdge);
        }
        return result;
    }

    private MultiLabel getBasicJEdgeLabel(JGraph<?> jGraph, JEdge<?> jEdge) {
        MultiLabel result = new MultiLabel();
        addEdgeLabels(jGraph, jEdge, result);
        return result;
    }

    /**
     * Adds the labels of all edges of a given cell to a multi-label.
     * @param jGraph the (non-{@code null}) {@link JGraph} of the {@link JCell}
     * @param jCell the cell from which the edges are added
     * @param result the resulting multi-label; modified by this call
     */
    private void addEdgeLabels(JGraph<?> jGraph, JCell<?> jCell, MultiLabel result) {
        boolean onVertex = jCell instanceof JVertex;
        for (Edge edge : jCell.getEdges()) {
            // only add edges that have an unfiltered label
            if (isVisible(jGraph, jCell, edge)) {
                Direct dir = onVertex
                    ? Direct.NONE
                    : ((JEdge<?>) jCell).getDirect(edge);
                Line line = edge.label().toLine();
                if (onVertex && edge.getRole() == EdgeRole.BINARY) {
                    line = line.append(LOOP_SUFFIX);
                }
                result.add(line, dir);
            }
        }
    }

    private MultiLabel getAspectJEdgeLabel(AspectJGraph jGraph, AspectJEdge jEdge) {
        MultiLabel result = null;
        // if both source and target nodes are nodified,
        // test for source node first
        if (jEdge.isNodeEdgeOut()) {
            result = new MultiLabel();
        } else if (jEdge.isNodeEdgeIn()) {
            result = new MultiLabel();
            AspectJVertex targetVertex = jEdge.getTargetVertex();
            assert targetVertex != null; // model has been initialised by now
            LabelPattern pattern = targetVertex.getEdgeLabelPattern();
            @SuppressWarnings({"unchecked", "rawtypes"})
            GraphBasedModel<HostGraph> resourceModel
                = (GraphBasedModel) jEdge.getJModel().getResourceModel();
            try {
                HostNode target = (HostNode) resourceModel.getMap().getNode(jEdge.getTargetNode());
                String label = pattern.getLabel(resourceModel.toResource(), target);
                result.add(Line.atom(label), jEdge.getDirect(null));
            } catch (FormatException e) {
                // assert false;
            }
        } else if (jEdge.isSourceLabel()) {
            result = new MultiLabel();
        } else {
            result = new MultiLabel();
            for (AspectEdge edge : jEdge.getEdges()) {
                // only add edges that have an unfiltered label
                if (isVisible(jGraph, jEdge, edge)) {
                    Line line;
                    if (jGraph.isShowAspects()) {
                        line = edge.label().toLine();
                    } else {
                        line = edge.toLine(false, jEdge.getAspects());
                    }
                    result.add(line, jEdge.getDirect(edge));
                }
            }
        }
        return result;
    }

    /** Computes the multi-line label for a given LSTJEdge.
     * @param jGraph the (non-{@code null}) {@link JGraph} of the {@link JVertex}
     */
    private MultiLabel getLTSJEdgeLabel(LTSJGraph jGraph, LTSJEdge jEdge) {
        MultiLabel result = new MultiLabel();
        boolean isShowAnchors = jGraph.isShowAnchors();
        for (Edge edge : jEdge.getEdges()) {
            // only add edges that have an unfiltered label
            if (isVisible(jGraph, jEdge, edge)) {
                GraphTransition trans = (GraphTransition) edge;
                Line line = Line.atom(trans.text(isShowAnchors));
                result.add(line, jEdge.getDirect(edge));
            }
        }
        return result;
    }

    /** Returns lines describing any data content of an {@link AspectNode}
     * @param node the node for which the data line is to be constructed
     * @param idLine potentially {@code null} identifier to be inserted in front of the node content
     */
    private Line getDataLine(AspectNode node, Line idLine) {
        Line result = null;
        AspectKind sortKind = node.getKind(Category.SORT);
        if (sortKind != null) {
            Line sortLine = getSortLine(sortKind.getSort());
            if (node.hasValue() || node.hasExpression()) {
                Line contentLine = node.hasValue()
                    ? node.getValueLine()
                    : node.getExpressionLine();
                if (idLine == null) {
                    result = contentLine;
                } else {
                    result = idLine
                        .append(TYPED_AS)
                        .append(sortLine)
                        .append(EQUALS_TO)
                        .append(contentLine);
                }
            } else {
                if (idLine == null) {
                    result = sortLine;
                } else {
                    result = idLine.append(TYPED_AS).append(sortLine);
                }
            }
        }
        return result;
    }

    /**
     * Tests if a given edge is currently being filtered.
     * @param jGraph the (non-{@code null}) {@link JGraph} of the {@link JVertex}
     */
    private boolean isVisible(JGraph<?> jGraph, JCell<?> jCell, Edge edge) {
        boolean result = true;
        LabelTree<?> labelTree = jGraph.getLabelTree();
        if (edge != null && labelTree != null) {
            Label key = jCell.getKey(edge);
            result = key == null || labelTree.isIncluded(key);
        }
        return result;
    }

    /** Returns the label prefix associated with a given sort. */
    private static Line getSortLine(Sort kind) {
        return sortLineMap.get(kind);
    }

    static private final Map<Sort,Line> sortLineMap;

    static {
        Map<Sort,Line> map = new EnumMap<>(Sort.class);
        for (Sort kind : Sort.values()) {
            map.put(kind, Line.atom(kind.getName()).style(Style.BOLD));
        }
        sortLineMap = map;
    }

    static private final String IMPORT_TEXT
        = String.format("%simport%s", Util.FRENCH_QUOTES_OPEN, Util.FRENCH_QUOTES_CLOSED);
    static private final Line IMPORT_LINE = Line.atom(IMPORT_TEXT).style(Style.ITALIC);
    static private final Line EXISTS_LINE = Line.atom("" + Util.EXISTS);
    static private final Line EXISTS_OPT_LINE
        = EXISTS_LINE.append(Line.atom("?").style(Style.SUPER));
    static private final Line FORALL_LINE = Line.atom("" + Util.FORALL);
    static private final Line FORALL_POS_LINE
        = FORALL_LINE.append(Line.atom(">0").style(Style.SUPER));
    /** Final line in a state vertex indicating residual invisible outgoing transitions. */
    static private final Line RESIDUAL_LINE = Line.atom("" + Util.DLA + Util.DA + Util.DRA);
    /** Line in a control vertex indicating a start state. */
    static private final Line START_LINE = Line.atom("start");
    /** Line in a control vertex indicating a deadlocked state. */
    static private final Line DEAD_LINE = Line.atom("dead");
    /** Line in a control vertex indicating a final state. */
    static private final Line FINAL_LINE = Line.atom("final");
    /** Suffix indicating a self-loop on a node label. */
    static private final String LOOP_SUFFIX = " " + Util.CA;
    /** Points-to operator between field name and value. */
    static private final String POINTS_TO = " " + Util.RA + " ";
    /** Points-to operator between field name and value. */
    static private final String TYPED_AS = Util.HAIR_SPACE + ":" + Util.HAIR_SPACE;
    /** Points-to operator between field name and value. */
    static private final String EQUALS_TO = Util.HAIR_SPACE + "=" + Util.THIN_SPACE;
}

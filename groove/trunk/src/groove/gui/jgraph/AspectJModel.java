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
 * $Id: AspectJModel.java,v 1.35 2008-03-13 14:40:32 rensink Exp $
 */
package groove.gui.jgraph;

import groove.graph.Element;
import groove.gui.Options;
import groove.view.FormatError;
import groove.view.View;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectKind;
import groove.view.aspect.AspectNode;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Implements jgraph's GraphModel interface on top of a {@link View}. This is
 * used to visualise rules and attributed graphs.
 * @author Arend Rensink
 * @version $Revision$
 */
public class AspectJModel extends GraphJModel<AspectNode,AspectEdge> {

    // --------------------- INSTANCE DEFINITIONS ------------------------

    /**
     * Creates a new aspect model instance on top of a given aspectual view.
     */
    AspectJModel(AspectGraph graph, Options options) {
        super(graph, options);
        this.view = graph.toView();
    }

    /** Constructor for a dummy model. */
    private AspectJModel() {
        this.view = null;
    }

    /** Specialises the return type. */
    @Override
    public AspectGraph getGraph() {
        return (AspectGraph) super.getGraph();
    }

    @Override
    public void reload() {
        super.reload();
        List<FormatError> graphErrors = this.view.getErrors();
        if (graphErrors != null) {
            for (FormatError error : graphErrors) {
                for (Element errorObject : error.getElements()) {
                    JCell errorCell = null;
                    if (errorObject instanceof AspectNode) {
                        errorCell = getJCellForNode((AspectNode) errorObject);
                    } else if (errorObject instanceof AspectEdge) {
                        errorCell = getJCell(errorObject);
                        if (errorCell instanceof AspectJEdge
                            && ((AspectJEdge) errorCell).isDataEdgeSourceLabel()) {
                            errorCell =
                                ((AspectJEdge) errorCell).getSourceVertex();
                        }
                    }
                    if (errorCell instanceof AspectJEdge) {
                        ((AspectJEdge) errorCell).setError(true);
                    } else if (errorCell instanceof AspectJVertex) {
                        ((AspectJVertex) errorCell).setError(true);
                    }
                }
            }
        }
    }

    /**
     * Indicates whether aspect prefixes should be shown for nodes and edges.
     */
    public final boolean isShowRemarks() {
        return getOptionValue(Options.SHOW_REMARKS_OPTION);
    }

    /**
     * Indicates whether aspect prefixes should be shown for nodes and edges.
     */
    final boolean isShowAspects() {
        return getOptionValue(Options.SHOW_ASPECTS_OPTION);
    }

    /**
     * Indicates whether data nodes should be shown in the rule and lts views.
     */
    boolean isShowValueNodes() {
        return getOptionValue(Options.SHOW_VALUE_NODES_OPTION);
    }

    /**
     * Overwrites the method so as to return a rule vertex.
     * @require <tt>edge instanceof RuleGraph.RuleNode</tt>
     */
    @Override
    protected GraphJVertex<AspectNode,AspectEdge> createJVertex(AspectNode node) {
        return new AspectJVertex(this, node);
    }

    /**
     * Overwrites the method so as to return a rule edge.
     * @require <tt>edge instanceof RuleGraph.RuleEdge</tt>
     */
    @Override
    protected GraphJEdge<AspectNode,AspectEdge> createJEdge(AspectEdge edge) {
        return new AspectJEdge(this, edge);
    }

    /**
     * The underlying view of this graph model.
     */
    private final View<?> view;

    /**
     * Creates a new aspect model instance on top of a given aspectual view.
     * Returns {@link #EMPTY_ASPECT_JMODEL} if the view is <code>null</code>.
     */
    static public AspectJModel newInstance(AspectGraph graph, Options options) {
        if (graph == null) {
            return EMPTY_ASPECT_JMODEL;
        } else {
            AspectJModel result = new AspectJModel(graph, options);
            result.reload();
            return result;
        }
    }

    /** Empty instance of the {@link AspectJModel}. */
    static public final AspectJModel EMPTY_ASPECT_JMODEL = new AspectJModel();

    /** Role names (for the tool tips). */
    static final Map<AspectKind,String> ROLE_NAMES =
        new EnumMap<AspectKind,String>(AspectKind.class);
    /** Role descriptions (for the tool tips). */
    static final Map<AspectKind,String> ROLE_DESCRIPTIONS =
        new EnumMap<AspectKind,String>(AspectKind.class);

    static {
        ROLE_NAMES.put(AspectKind.EMBARGO, "Embargo");
        ROLE_NAMES.put(AspectKind.READER, "Reader");
        ROLE_NAMES.put(AspectKind.CREATOR, "Creator");
        ROLE_NAMES.put(AspectKind.ADDER, "Adder");
        ROLE_NAMES.put(AspectKind.ERASER, "Eraser");
        ROLE_NAMES.put(AspectKind.REMARK, "Remark");

        ROLE_DESCRIPTIONS.put(AspectKind.EMBARGO,
            "Must be absent from a graph for this rule to apply");
        ROLE_DESCRIPTIONS.put(AspectKind.READER,
            "Must be matched for this rule to apply");
        ROLE_DESCRIPTIONS.put(AspectKind.CREATOR,
            "Will be created by applying this rule");
        ROLE_DESCRIPTIONS.put(
            AspectKind.ADDER,
            "Must be absent from a graph for this rule to apply, and will be created when applying this rule");
        ROLE_DESCRIPTIONS.put(AspectKind.ERASER,
            "Will be deleted by applying this rule");
        ROLE_DESCRIPTIONS.put(AspectKind.REMARK,
            "Has no effect on the execution of the rule");
    }
}
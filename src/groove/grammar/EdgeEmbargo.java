// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id$
 */
package groove.grammar;

import groove.grammar.rule.LabelVar;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleGraph;
import groove.graph.Label;
import groove.graph.Node;
import groove.util.Groove;

/**
 * A specialised NAC that forbids the presence of a certain edge.
 * @author Arend Rensink
 * @version $Revision$
 */
public class EdgeEmbargo extends Condition {
    /** Constructs a named edge embargo.
     *
     * @param name the (non-{@code null}) name for the embargo
     * @param context the graph on which this is an embargo
     * @param embargoEdge the forbidden edge
     * @param properties properties of the graph grammar
     */
    private EdgeEmbargo(String name, RuleGraph context, RuleEdge embargoEdge,
        GrammarProperties properties) {
        super(name, Condition.Op.NOT, context.newGraph(name), null, properties);
        this.embargoEdge = embargoEdge;
        getPattern().addEdgeContext(embargoEdge);
        getRoot().addNode(embargoEdge.source());
        getRoot().addNode(embargoEdge.target());
        for (LabelVar var : getPattern().varSet()) {
            if (context.containsVar(var)) {
                getRoot().addVar(var);
            }
        }
        if (CONSTRUCTOR_DEBUG) {
            Groove.message("Edge embargo: " + this);
            Groove.message("Embargo edge: " + embargoEdge);
        }
    }

    /**
     * Constructs an edge embargo on a given graph from a given edge with end
     * nodes in a given graph (presumably a rule lhs).
     * @param graph the graph on which this embargo works
     * @param embargoEdge the edge that is forbidden
     */
    public EdgeEmbargo(RuleGraph graph, RuleEdge embargoEdge, GrammarProperties properties) {
        this(String.format("%s:!(%s)", graph.getName(), embargoEdge), graph, embargoEdge,
            properties);
    }

    /**
     * Returns the embargo edge, which is an edge in this NAC's domain that is
     * tested for.
     */
    public RuleEdge getEmbargoEdge() {
        return this.embargoEdge;
    }

    /**
     * Returns the source node of the forbidden edge.
     * @ensure <tt>result != null</tt>
     */
    public Node edgeSource() {
        return this.embargoEdge.source();
    }

    /**
     * Returns the label of the forbidden edge.
     * @ensure <tt>result != null</tt>
     */
    public Label edgeLabel() {
        return this.embargoEdge.label();
    }

    /**
     * The forbidden edge.
     */
    protected final RuleEdge embargoEdge;

    private final static boolean CONSTRUCTOR_DEBUG = false;
}
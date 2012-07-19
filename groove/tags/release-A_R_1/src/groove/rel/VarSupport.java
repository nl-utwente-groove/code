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
 * $Id $
 */

package groove.rel;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphShape;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class containing some static utilities for handling variables in graphs.
 * Variables are <code>String</code>s, evaluated to <code>Label</code>s,
 * used in some types of regular expression.
 * @author Arend Rensink
 * @version $Revision$
 */
public class VarSupport {
    /**
     * Returns the set of all variables involved in a given edge. If the edge is
     * has a {@link RegExprLabel}, this is the result of
     * {@link RegExpr#allVarSet()}; otherwise, it is the empty set.
     */
    static public Set<LabelVar> getAllVars(Edge edge) {
        if (edge.label() instanceof RegExprLabel) {
            return ((RegExprLabel) edge.label()).getRegExpr().allVarSet();
        } else {
            return Collections.emptySet();
        }
    }

    /**
     * Returns the set of all variables involved in a given graph. This is the
     * union of the variables involved in the edges.
     * @see #getAllVars(Edge)
     */
    static public Set<LabelVar> getAllVars(GraphShape graph) {
        Set<LabelVar> result = new HashSet<LabelVar>();
        for (Edge edge : graph.edgeSet()) {
            result.addAll(getAllVars(edge));
        }
        return result;
    }

    /**
     * Returns the set of variables bound by a given edge. If the edge is has a
     * {@link RegExprLabel}, this is the result of
     * {@link RegExpr#boundVarSet()}; otherwise, it is the empty set.
     */
    static public Set<LabelVar> getBoundVars(Edge edge) {
        if (edge.label() instanceof RegExprLabel) {
            return ((RegExprLabel) edge.label()).getRegExpr().boundVarSet();
        } else {
            return Collections.emptySet();
        }
    }

    /**
     * Returns the set of variables bound by a given graph. This is the union of
     * the variables bound by the edges.
     * @see #getBoundVars(Edge)
     */
    static public Set<LabelVar> getBoundVars(Graph graph) {
        Set<LabelVar> result = new HashSet<LabelVar>();
        for (Edge edge : graph.edgeSet()) {
            result.addAll(getBoundVars(edge));
        }
        return result;
    }

    /**
     * Returns the set of variable-containing edges occurring in a given graph. An
     * edge is variable-containing if {@link #getAllVars(Edge)} is non-empty.
     */
    static public Set<Edge> getVarEdges(Graph graph) {
        Set<Edge> result = new HashSet<Edge>();
        for (Edge edge : graph.edgeSet()) {
            if (!getAllVars(edge).isEmpty()) {
                result.add(edge);
            }
        }
        return result;
    }

    /**
     * Returns a map from bound variables in a graph to edges that bind them.
     */
    static public Map<LabelVar,Edge> getVarBinders(Graph graph) {
        Map<LabelVar,Edge> result = new HashMap<LabelVar,Edge>();
        for (Edge binder : graph.edgeSet()) {
            for (LabelVar boundVar : getBoundVars(binder)) {
                result.put(boundVar, binder);
            }
        }
        return result;
    }

    /**
     * Returns the set of named wildcard edges occurring in a given graph.
     */
    static public Set<Edge> getSimpleVarEdges(Graph graph) {
        Set<Edge> result = new HashSet<Edge>();
        for (Edge edge : graph.edgeSet()) {
            if (RegExprLabel.getWildcardId(edge.label()) != null) {
                result.add(edge);
            }
        }
        return result;
    }

    /**
     * Returns a map from variables in a graph to edges that have them as a
     * named wildcard.
     */
    static public Map<LabelVar,Edge> getSimpleVarBinders(Graph graph) {
        Map<LabelVar,Edge> result = new HashMap<LabelVar,Edge>();
        for (Edge binder : graph.edgeSet()) {
            LabelVar id = RegExprLabel.getWildcardId(binder.label());
            if (id != null) {
                result.put(id, binder);
            }
        }
        return result;
    }
}
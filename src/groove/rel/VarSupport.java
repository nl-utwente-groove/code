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

import groove.trans.RuleEdge;
import groove.trans.RuleElement;
import groove.trans.RuleGraph;
import groove.trans.RuleLabel;
import groove.trans.RuleNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
     * Returns the set of all variables involved in a given element. If the edge is
     * has a {@link RuleLabel}, this is the result of
     * {@link RegExpr#allVarSet()}; otherwise, it is the empty set.
     */
    static public Collection<LabelVar> getAllVars(RuleElement element) {
        Collection<LabelVar> result = Collections.emptySet();
        if (element instanceof RuleEdge) {
            RuleLabel label = ((RuleEdge) element).label();
            if (label.isMatchable()) {
                result = label.getMatchExpr().allVarSet();
            }
        } else {
            result = ((RuleNode) element).getTypeVars();
        }
        return result;
    }

    /**
     * Returns the set of all variables involved in a given graph. This is the
     * union of the variables involved in the edges.
     * @see #getAllVars(RuleElement)
     */
    static public Set<LabelVar> getAllVars(RuleGraph graph) {
        Set<LabelVar> result = new HashSet<LabelVar>();
        for (RuleEdge edge : graph.edgeSet()) {
            result.addAll(getAllVars(edge));
        }
        for (RuleNode node : graph.nodeSet()) {
            result.addAll(getAllVars(node));
        }
        return result;
    }

    /**
     * Returns the set of variables bound by a given edge. If the edge is has a
     * {@link RuleLabel}, this is the result of
     * {@link RegExpr#boundVarSet()}; otherwise, it is the empty set.
     */
    static public Collection<LabelVar> getBoundVars(RuleElement element) {
        Collection<LabelVar> result = Collections.emptySet();
        if (element instanceof RuleEdge) {
            RuleLabel label = ((RuleEdge) element).label();
            if (label.isMatchable()) {
                result = label.getMatchExpr().boundVarSet();
            }
        } else {
            result = ((RuleNode) element).getTypeVars();
        }
        return result;
    }

    /**
     * Returns the set of variables bound by a given graph. This is the union of
     * the variables bound by the edges.
     * @see #getBoundVars(RuleElement)
     */
    static public Set<LabelVar> getBoundVars(RuleGraph graph) {
        Set<LabelVar> result = new HashSet<LabelVar>();
        for (RuleNode node : graph.nodeSet()) {
            result.addAll(getBoundVars(node));
        }
        for (RuleEdge edge : graph.edgeSet()) {
            result.addAll(getBoundVars(edge));
        }
        return result;
    }

    /**
     * Returns the set of variable-containing edges occurring in a given graph. An
     * edge is variable-containing if {@link #getAllVars(RuleElement)} is non-empty.
     */
    static public Set<RuleElement> getVarElements(RuleGraph graph) {
        Set<RuleElement> result = new HashSet<RuleElement>();
        for (RuleEdge edge : graph.edgeSet()) {
            if (!getAllVars(edge).isEmpty()) {
                result.add(edge);
            }
        }
        for (RuleNode node : graph.nodeSet()) {
            if (!getAllVars(node).isEmpty()) {
                result.add(node);
            }
        }
        return result;
    }

    /**
     * Returns a map from bound variables in a graph to elements that bind them.
     */
    static public Map<LabelVar,Set<RuleElement>> getVarBinders(RuleGraph graph) {
        Map<LabelVar,Set<RuleElement>> result =
            new HashMap<LabelVar,Set<RuleElement>>();
        List<RuleElement> elements = new ArrayList<RuleElement>();
        elements.addAll(graph.nodeSet());
        elements.addAll(graph.edgeSet());
        for (RuleElement binder : elements) {
            for (LabelVar boundVar : getBoundVars(binder)) {
                Set<RuleElement> binders = result.get(boundVar);
                if (binders == null) {
                    result.put(boundVar, binders = new HashSet<RuleElement>());
                }
                binders.add(binder);
            }
        }
        return result;
    }

    /**
     * Returns a map from variables in a graph to edges that have them as a
     * named wildcard.
     */
    static public Map<LabelVar,RuleEdge> getSimpleVarBinders(RuleGraph graph) {
        Map<LabelVar,RuleEdge> result = new HashMap<LabelVar,RuleEdge>();
        for (RuleEdge binder : graph.edgeSet()) {
            LabelVar id = binder.label().getWildcardId();
            if (id != null) {
                result.put(id, binder);
            }
        }
        return result;
    }
}

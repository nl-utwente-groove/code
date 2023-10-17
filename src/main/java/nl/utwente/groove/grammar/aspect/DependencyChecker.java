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
package nl.utwente.groove.grammar.aspect;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import nl.utwente.groove.io.Util;
import nl.utwente.groove.util.CycleChecker;
import nl.utwente.groove.util.Groove;

/**
 * Checker for dependency cycles in variable nodes.
 * @author Arend Rensink
 * @version $Revision $
 */
class DependencyChecker extends CycleChecker<AspectGraph,AspectNode> {
    /**
     * Creates the singleton a checker.
     */
    private DependencyChecker() {
        // empty by design
    }

    @Override
    protected Connect buildConnect(AspectGraph graph) {
        Connect connect = new Connect();
        graph.nodeSet().forEach(n -> connect.put(n, getOutLinksFor(n)));
        return connect;
    }

    /** Collects the outgoing links for a given node. */
    @SuppressWarnings("null")
    private List<Link<AspectNode>> getOutLinksFor(AspectNode node) {
        var targets = new ArrayList<Link<AspectNode>>();
        if (node.hasId() && node.hasExpression()) {
            for (var depId : node.getExpression().getTyping().keySet()) {
                var depNode = node.getGraph().getNodeForId(depId);
                targets.add(newLink(node, depNode, depNode));
            }
        }
        return targets;
    }

    @Override
    protected String errorMessage(List<AspectNode> cycle) {
        var ids = cycle.stream().map(AspectNode::getId).collect(Collectors.toList());
        ids.add(ids.get(0));
        return "Variable dependency cycle: "
            + Groove.toString(ids.toArray(), "", "", " " + Util.RA + " ");
    }

    /** Returns the singleton instance of this class. */
    static public DependencyChecker instance() {
        return INSTANCE;
    }

    /** The singleton instance of this class. */
    static private final DependencyChecker INSTANCE = new DependencyChecker();
}

/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.grammar.type;

import groove.grammar.host.HostEdge;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.graph.Direction;
import groove.util.Pair;
import groove.util.parse.FormatErrorSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@link MultiplicityChecker} class provides functionality for
 * verifying edge multiplicities in a {@link HostGraph}.
 *
 * @author Arend Rensink
 */
public class MultiplicityChecker implements TypeChecker {
    /**
     * Default constructor. Collects the type edges that have a multiplicity,
     * and stores them for quick lookup.
     */
    public MultiplicityChecker(TypeGraph type) {
        assert type != null;
        this.typeGraph = type;
        this.checks = new HashMap<>();
        for (TypeEdge edge : type.edgeSet()) {
            if (edge.getInMult() != null) {
                addCheck(edge, Direction.INCOMING);
            }
            if (edge.getOutMult() != null) {
                addCheck(edge, Direction.OUTGOING);
            }
        }
    }

    private void addCheck(TypeEdge edge, Direction dir) {
        Check check = new Check(edge, dir);
        for (TypeNode node : dir.origin(edge).getSubtypes()) {
            List<Check> nodeChecks = this.checks.get(node);
            if (nodeChecks == null) {
                this.checks.put(node, nodeChecks = new ArrayList<>());
            }
            nodeChecks.add(check);
        }
    }

    @Override
    public TypeGraph getTypeGraph() {
        return this.typeGraph;
    }

    private final TypeGraph typeGraph;

    @Override
    public boolean isTrivial() {
        return this.checks.isEmpty();
    }

    @Override
    public FormatErrorSet check(HostGraph source) {
        FormatErrorSet result = new FormatErrorSet();
        for (HostNode node : source.nodeSet()) {
            List<Check> nodeChecks = this.checks.get(node.getType());
            if (nodeChecks == null) {
                continue;
            }
            for (Check c : nodeChecks) {
                TypeEdge type = c.one();
                Direction dir = c.two();
                Multiplicity mult =
                    dir == Direction.INCOMING ? type.getInMult() : type.getOutMult();
                int count = 0;
                for (HostEdge edge : dir.edges(source, node)) {
                    if (edge.getType() == type) {
                        count++;
                    }
                }
                if (!mult.inRange(count)) {
                    result.add("Node %s violates %s edge multiplicity %s for edge type %s: actual count = %s",
                        node,
                        dir,
                        mult,
                        type,
                        count);
                }
            }
        }
        return result;
    }

    /** The set of node types for which we want to check a multiplicity. */
    private final Map<TypeNode,List<Check>> checks;

    private static class Check extends Pair<TypeEdge,Direction> {
        public Check(TypeEdge one, Direction two) {
            super(one, two);
        }
    }
}

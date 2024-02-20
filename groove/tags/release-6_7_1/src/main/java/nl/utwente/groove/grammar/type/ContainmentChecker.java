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
package nl.utwente.groove.grammar.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import nl.utwente.groove.grammar.host.HostEdge;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.util.CycleChecker;

/**
 * The {@link ContainmentChecker} class provides functionality for
 * verifying containment in a {@link HostGraph}.
 *
 * @author Arend Rensink
 */
public class ContainmentChecker extends CycleChecker<HostGraph,HostEdge> implements TypeChecker {
    /**
     * Default constructor. Collects the type edges that have a multiplicity,
     * and stores them for quick lookup.
     */
    public ContainmentChecker(TypeGraph type) {
        assert type != null;
        this.typeGraph = type;
        this.checks = new ArrayList<>();
        for (TypeEdge edge : type.edgeSet()) {
            for (TypeEdge subEdge : edge.getSubtypes()) {
                if (edge.isComposite() || subEdge.isComposite()) {
                    this.checks.add(edge);
                }
            }
        }
    }

    @Override
    public TypeGraph getTypeGraph() {
        return this.typeGraph;
    }

    private final TypeGraph typeGraph;

    /** The set of node types for which we want to check containment. */
    private final List<TypeEdge> checks;

    @Override
    public boolean isTrivial() {
        return this.checks.isEmpty();
    }

    /**
     * Builds the connection map for a given host graph.
     */
    @Override
    protected Connect buildConnect(HostGraph host) {
        Connect connect = new Connect();
        for (TypeEdge check : this.checks) {
            Set<? extends HostEdge> edges = host.edgeSet(check.label());
            for (HostEdge edge : edges) {
                var source = edge.source();
                var targets = connect.get(source);
                if (targets == null) {
                    connect.put(source, targets = new ArrayList<>());
                }
                targets.add(newLink(source, edge.target(), edge));
            }
        }
        return connect;
    }

    @Override
    protected String errorMessage(List<HostEdge> cycle) {
        return "Containment cycle in graph";
    }
}

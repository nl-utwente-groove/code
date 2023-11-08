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
package nl.utwente.groove.grammar.host;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.algebra.AlgebraFamily;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.graph.GGraph;
import nl.utwente.groove.graph.GraphInfo;
import nl.utwente.groove.transform.DeltaTarget;
import nl.utwente.groove.util.parse.FormatErrorSet;

/**
 * Graph type used for graphs under transformation.
 * Host graphs consist of {@link HostNode}s and {@link HostEdge}s.
 * @author Arend Rensink
 * @version $Revision $
 */
@NonNullByDefault
public interface HostGraph extends GGraph<HostNode,HostEdge>, DeltaTarget {
    @Override
    HostGraph newGraph(String name);

    @Override
    HostGraph clone();

    /** Clones this host graph, while optionally changing the algebras. */
    default HostGraph clone(AlgebraFamily family) {
        return new DefaultHostGraph(this, family);
    }

    @Override
    HostFactory getFactory();

    /** Returns the type graph for this host graph, if any. */
    default public TypeGraph getTypeGraph() {
        return getFactory().getTypeFactory().getGraph();
    }

    /** Indicates if this is a simple or multi-graph. */
    default public boolean isSimple() {
        return getFactory().isSimple();
    }

    /**
     * Checks the graph for type constraints that cannot be
     * prevented statically: in particular, multiplicity and containment
     * violations. Any errors found are collected and returned.
     * @see GraphInfo#getErrors
     */
    public FormatErrorSet checkTypeConstraints();
}

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

import java.util.Set;

import groove.grammar.QualName;
import groove.graph.EdgeRole;

/**
 * Type graph with only a single (top) node type and all data types.
 * For all type labels there are edges from the top node type to all node types.
 * Implicit type graphs have no (nontrivial) inheritance.
 * @author rensink
 * @version $Revision $
 */
public class ImplicitTypeGraph extends TypeGraph {
    /** Constructs a fresh implicit type graph. */
    public ImplicitTypeGraph() {
        super(QualName.name("implicit-type-graph"), true);
        // instantiate the top node
        getFactory().getTopNode();
    }

    /** Returns the top node type. */
    public TypeNode getTopNode() {
        return getFactory().getTopNode();
    }

    /** Adds type edges for a given type label. */
    public void addLabel(TypeLabel label) {
        TypeNode top = getTopNode();
        if (label.getRole() == EdgeRole.BINARY) {
            for (TypeNode target : nodeSet()) {
                addEdge(top, label, target);
            }
        } else {
            addEdge(top, label, top);
        }
    }

    /** Adds type edges for a given label, given as a string. */
    public void addLabel(String label) {
        addLabel(getFactory().createLabel(label));
    }

    /** Creates a (fixed) implicit type graph for a given set of labels. */
    public static TypeGraph newInstance(Set<TypeLabel> labels) {
        ImplicitTypeGraph result = new ImplicitTypeGraph();
        for (TypeLabel label : labels) {
            result.addLabel(label);
        }
        result.setFixed();
        return result;
    }
}

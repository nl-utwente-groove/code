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
package nl.utwente.groove.prolog.builtin;

import nl.utwente.groove.annotation.Signature;
import nl.utwente.groove.annotation.ToolTipBody;
import nl.utwente.groove.annotation.ToolTipPars;
import nl.utwente.groove.prolog.builtin.type.Predicate_composite_type_graph;
import nl.utwente.groove.prolog.builtin.type.Predicate_direct_subtype;
import nl.utwente.groove.prolog.builtin.type.Predicate_label;
import nl.utwente.groove.prolog.builtin.type.Predicate_subtype;
import nl.utwente.groove.prolog.builtin.type.Predicate_type_graph_name;

/** Type-related Groove predicates.
 * Documentation reading guide:
 * <li> +     The argument shall be instantiated.
 * <li> ?     The argument shall be instantiated or a variable.
 * <li> @     The argument shall remain unaltered.
 * <li> -     The argument shall be a variable that will be instantiated
 */
@SuppressWarnings("all")
public class TypePredicates extends GroovePredicates {
    @Signature({"TypeGraph", "?"})
    @ToolTipBody("Retrieves the composite type graph, which is the union of the individual type graphs.")
    @ToolTipPars("Composite type graph object")
    public void composite_type_graph_1() {
        s(Predicate_composite_type_graph.class, 1);
    }

    @Signature({"TypeGraph", "Label", "Label", "++?", "+?+"})
    @ToolTipBody("Retrieves the direct subtype relation, as defined in a given type graph.")
    @ToolTipPars({"Type graph object", "Subtype label", "Supertype label"})
    public void direct_subtype_3() {
        s(Predicate_direct_subtype.class, 3);
    }

    @Signature({"String", "Label", "+?", "?+"})
    @ToolTipBody("Estabishes the relation between label text (of type String) and label object")
    public void label_2() {
        s(Predicate_label.class, 2);
    }

    @Signature({"TypeGraph", "Label", "Label", "++?", "+?+"})
    @ToolTipBody("Retrieves the recursively and transitively closed subtype relation, as defined in a given type graph.")
    @ToolTipPars({"Type graph object", "Subtype label", "Supertype label"})
    public void subtype_3() {
        s(Predicate_subtype.class, 3);
    }

    @Signature({"String", "?"})
    @ToolTipBody("Cycles over the set of available (active) type graph names.")
    @ToolTipPars("Name of an active type graph")
    public void type_graph_name_1() {
        s(Predicate_type_graph_name.class, 1);
    }

    // DERIVED PREDICATES

    @Signature({"TypeGraph", "String", "String", "++?", "+?+"})
    @ToolTipBody({"Retrieves the direct subtype relation, as defined in a given type graph.",
        "The type labels are given as Strings"})
    @ToolTipPars({"Type graph object", "Subtype label text", "Supertype label text"})
    public void direct_subtype_label_3() {
        s("direct_subtype_label(TG,A,B) :- label(A,AL), label(B,BL), direct_subtype(TG,AL,BL).");
    }

    @Signature({"TypeGraph", "String", "String", "++?", "+?+"})
    @ToolTipBody({
        "Retrieves the recursively and transitively closed subtype relation, as defined in a given type graph.",
        "The type labels are given as Strings"})
    @ToolTipPars({"Type graph object", "Subtype label text", "Supertype label text"})
    public void subtype_label_3() {
        s("subtype_label(TG,A,B) :- label(A,AL), label(B,BL), subtype(TG,AL,BL).");
    }
}

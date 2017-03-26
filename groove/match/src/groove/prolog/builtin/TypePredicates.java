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
package groove.prolog.builtin;

import groove.annotation.Signature;
import groove.annotation.ToolTipBody;
import groove.annotation.ToolTipPars;

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
        s(":-build_in(composite_type_graph/1,'groove.prolog.builtin.type.Predicate_composite_type_graph').");
    }

    @Signature({"TypeGraph", "Label", "Label", "++?", "+?+"})
    @ToolTipBody("Retrieves the direct subtype relation, as defined in a given type graph.")
    @ToolTipPars({"Type graph object", "Subtype label", "Supertype label"})
    public void direct_subtype_3() {
        s(":-build_in(direct_subtype/3,'groove.prolog.builtin.type.Predicate_direct_subtype').");
    }

    @Signature({"String", "Label", "+?", "?+"})
    @ToolTipBody("Estabishes the relation between label text (of type String) and label object")
    public void label_2() {
        s(":-build_in(label/2,'groove.prolog.builtin.type.Predicate_label').");
    }

    @Signature({"TypeGraph", "Label", "Label", "++?", "+?+"})
    @ToolTipBody("Retrieves the recursively and transitively closed subtype relation, as defined in a given type graph.")
    @ToolTipPars({"Type graph object", "Subtype label", "Supertype label"})
    public void subtype_3() {
        s(":-build_in(subtype/3,'groove.prolog.builtin.type.Predicate_subtype').");
    }

    @Signature({"String", "?"})
    @ToolTipBody("Cycles over the set of available (active) type graph names.")
    @ToolTipPars("Name of an active type graph")
    public void type_graph_name_1() {
        s(":-build_in(type_graph_name/1,'groove.prolog.builtin.type.Predicate_type_graph_name').");
    }

    // DERIVED PREDICATES

    @Signature({"TypeGraph", "String", "String", "++?", "+?+"})
    @ToolTipBody({
        "Retrieves the direct subtype relation, as defined in a given type graph.",
        "The type labels are given as Strings"})
    @ToolTipPars({"Type graph object", "Subtype label text",
        "Supertype label text"})
    public void direct_subtype_label_3() {
        s("direct_subtype_label(TG,A,B) :- label(A,AL), label(B,BL), direct_subtype(TG,AL,BL).");
    }

    @Signature({"TypeGraph", "String", "String", "++?", "+?+"})
    @ToolTipBody({
        "Retrieves the recursively and transitively closed subtype relation, as defined in a given type graph.",
        "The type labels are given as Strings"})
    @ToolTipPars({"Type graph object", "Subtype label text",
        "Supertype label text"})
    public void subtype_label_3() {
        s("subtype_label(TG,A,B) :- label(A,AL), label(B,BL), subtype(TG,AL,BL).");
    }
}

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

import groove.annotation.ToolTipPars;
import groove.annotation.Signature;
import groove.annotation.ToolTipBody;

/** Algebra-related Groove predicates. 
 * Documentation reading guide:
 * <li> +     The argument shall be instantiated.
 * <li> ?     The argument shall be instantiated or a variable.
 * <li> @     The argument shall remain unaltered.
 * <li> -     The argument shall be a variable that will be instantiated
 */
@SuppressWarnings("all")
public class AlgebraPredicates extends GroovePredicates {
    @Signature({"Node", "@"})
    @ToolTipBody("Succeeds if the given term is a value node")
    public void is_valuenode_1() {
        s(":-build_in(is_valuenode/1,'groove.prolog.builtin.algebra.Predicate_is_valuenode').");
    }

    @Signature({"Node", "Term", "+?"})
    @ToolTipBody({
        "Converts the value node's value to a prolog term. A string value is converted to an",
        "AtomicTerm, and integer and double value are converted to a IntegerTerm and FloatTerm",
        "respectively. All other values are converted to a JavaObjectTerm"})
    @ToolTipPars({"The value node", "The corresponding term"})
    public void convert_valuenode_2() {
        s(":-build_in(convert_valuenode/2,'groove.prolog.builtin.algebra.Predicate_convert_valuenode').");
    }

    @Signature({"Graph", "Node", "AttrName", "AttrValue", "+?+?"})
    @ToolTipBody("Get all nodes with a given attribute")
    @ToolTipPars({"The graph", "The node with the given attribute",
        "The attribute name", "The value of the attribute"})
    public void node_with_attribute_4() {
        s("node_with_attribute(Graph,Node,AttrName,AttrValue):-");
        s("  label_edge(Graph,AttrName,Edge), % get all edges with a given label");
        s("  edge_target(Edge,ValNode), % get the destination of the edge");
        s("  is_valuenode(ValNode), % make sure it's a value node");
        s("  convert_valuenode(ValNode,AttrValue), % convert it to a term");
        s("  edge_source(Edge,Node). % get the node that has this attribute");
    }

    @Signature({"Graph", "Node", "AttrName", "+?+"})
    public void node_with_attribute_3() {
        s("node_with_attribute(X,Y,Z) :- node_with_attribute(X,Y,Z,_).");
    }
}

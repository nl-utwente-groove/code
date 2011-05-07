/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.prolog.builtin;

import groove.prolog.annotation.Param;
import groove.prolog.annotation.Signature;
import groove.prolog.annotation.ToolTip;

/** Graph-related GROOVE predicates.
 * Documentation reading guide:
 * <li> +     The argument shall be instantiated.
 * <li> ?     The argument shall be instantiated or a variable.
 * <li> @     The argument shall remain unaltered.
 * <li> -     The argument shall be a variable that will be instantiated
 */
@SuppressWarnings("all")
public class GraphPredicates extends GroovePredicates {
    @Signature({"Graph", "+"})
    @ToolTip("Fails if the first argument is not a Groove Graph")
    public void is_graph_1() {
        s(":-build_in(is_graph/1,'groove.prolog.builtin.graph.Predicate_is_graph').");
    }

    @Signature({"Node", "+"})
    @ToolTip("Fails if the first argument is not a Groove Node")
    public void is_node_1() {
        s(":-build_in(is_node/1,'groove.prolog.builtin.graph.Predicate_is_node').");
    }

    @Signature({"Edge", "+"})
    @ToolTip("Fails if the first argument is not a Groove Edge")
    public void is_edge_1() {
        s(":-build_in(is_edge/1,'groove.prolog.builtin.graph.Predicate_is_edge').");
    }

    @Signature({"Graph", "?"})
    @ToolTip("Retrieves the start graph")
    @Param({"the graph"})
    public void start_graph_1() {
        s(":-build_in(start_graph/1,'groove.prolog.builtin.graph.Predicate_start_graph').");
    }

    @Signature({"String", "?"})
    @ToolTip("Retrieves the start graph name")
    @Param({"the graph name"})
    public void start_graph_name_1() {
        s(":-build_in(start_graph_name/1,'groove.prolog.builtin.graph.Predicate_start_graph_name').");
    }

    @Signature({"Graph", "Node", "+?"})
    @ToolTip("Gets a node from a graph")
    @Param({"the graph", "the node"})
    //    % @see groove.graph.GraphShape#nodeSet()"})
    public void graph_node_2() {
        s(":-build_in(graph_node/2,'groove.prolog.builtin.graph.Predicate_graph_node').");
    }

    @Signature({"Graph", "NodeSet", "+?"})
    @ToolTip("Gets the complete node set of a graph")
    @Param({"the graph", "the list of nodes"})
    //    % @see groove.graph.GraphShape#nodeSet()"})
    public void graph_node_set_2() {
        s(":-build_in(graph_node_set/2,'groove.prolog.builtin.graph.Predicate_graph_node_set').");
    }

    @Signature({"Graph", "Count", "+?"})
    @ToolTip("Gets the number of nodes in a graph")
    @Param({"the graph", "the number of nodes"})
    //    % @see groove.graph.GraphShape#nodeCount()
    public void graph_node_count_2() {
        s(":-build_in(graph_node_count/2,'groove.prolog.builtin.graph.Predicate_graph_node_count').");
    }

    @Signature({"Graph", "Edge", "+?"})
    @ToolTip("Gets an edge from a graph")
    @Param({"the graph", "the edge"})
    //    % @see groove.graph.GraphShape#edgeSet()
    public void graph_edge_2() {
        s(":-build_in(graph_edge/2,'groove.prolog.builtin.graph.Predicate_graph_edge').");
    }

    @Signature({"Graph", "EdgeSet", "+?"})
    @ToolTip("Gets a set of edges from a graph")
    @Param({"the graph ", "the list of edges"})
    //    % @see groove.graph.GraphShape#edgeSet()
    public void graph_edge_set_2() {
        s(":-build_in(graph_edge_set/2,'groove.prolog.builtin.graph.Predicate_graph_edge_set').");
    }

    @Signature({"Graph", "Count", "+?"})
    @ToolTip("Gets the number of edges in a graph")
    @Param({"the graph", "the number of edges"})
    //    % @see groove.graph.GraphShape#edgeCount
    public void graph_edge_count_2() {
        s(":-build_in(graph_edge_count/2,'groove.prolog.builtin.graph.Predicate_graph_edge_count').");
    }

    @Signature({"Graph", "Node", "Edge", "++?"})
    @ToolTip("Gets an edge from a node, can be incoming or outgoing")
    @Param({"the graph", "the node", "the edge"})
    //    % @see groove.graph.GraphShape#edgeSet(Node,int)
    public void node_edge_3() {
        s(":-build_in(node_edge/3,'groove.prolog.builtin.graph.Predicate_node_edge').");
    }

    @Signature({"Graph", "Node", "EdgeSet", "++?"})
    @ToolTip("Gets the set of edges for a single node. Both incoming and outgoing edges.")
    @Param({"the graph", "the node", "the list of edges"})
    //    % @see groove.graph.GraphShape#edgeSet(Node,int)
    public void node_edge_set_3() {
        s(":-build_in(node_edge_set/3,'groove.prolog.builtin.graph.Predicate_node_edge_set').");
    }

    @Signature({"Graph", "Node", "Edge", "++?"})
    @ToolTip("Gets an outgoing edge from a node")
    @Param({"the graph", "the node", "list of outgoing edges"})
    //    % @see groove.graph.GraphShape#outEdgeSet(Node)
    public void node_out_edge_3() {
        s(":-build_in(node_out_edge/3,'groove.prolog.builtin.graph.Predicate_node_out_edge').");
    }

    @Signature({"Graph", "Node", "EdgeSet", "++?"})
    @ToolTip("Gets the outgoing edges for a given node")
    @Param({"the graph", "the node", "list of outgoing edges"})
    //    % @see groove.graph.GraphShape#outEdgeSet(Node)
    public void node_out_edge_set_3() {
        s(":-build_in(node_out_edge_set/3,'groove.prolog.builtin.graph.Predicate_node_out_edge_set').");
    }

    @Signature({"Graph", "Label", "Edge", "++?"})
    @ToolTip("Gets an edge with a given label")
    @Param({"the graph", "the label", "the edges"})
    //    % @see groove.graph.GraphShape#labelEdgeSet(int,Label)
    public void label_edge_3() {
        s(":-build_in(label_edge/3,'groove.prolog.builtin.graph.Predicate_label_edge').");
    }

    @Signature({"Graph", "Label", "EdgeSet", "++?"})
    @ToolTip("Gets the edge set of a graph with a given label")
    @Param({"the graph", "the label", "the list of edges"})
    //    % @see groove.graph.GraphShape#labelEdgeSet(int,Label)
    public void label_edge_set_3() {
        s(":-build_in(label_edge_set/3,'groove.prolog.builtin.graph.Predicate_label_edge_set').");
    }

    @Signature({"Edge", "Node", "+?"})
    @ToolTip("Gets the source node of an edge")
    @Param({"the edge", "the node"})
    //    % @see groove.graph.Edge#source()
    public void edge_source_2() {
        s(":-build_in(edge_source/2,'groove.prolog.builtin.graph.Predicate_edge_source').");
    }

    @Signature({"Edge", "Node", "+?"})
    @ToolTip("Gets the destination node of an edge (opposite of the source)")
    @Param({"the edge", "the node"})
    //    % @see groove.graph.Edge#target()
    public void edge_target_2() {
        s(":-build_in(edge_target/2,'groove.prolog.builtin.graph.Predicate_edge_target').");
    }

    @Signature({"Edge", "Label", "+?"})
    @ToolTip("Gets the label of the edge")
    @Param({"the edge", "the label/Atom"})
    //    % @see groove.graph.Edge#label()
    public void edge_label_2() {
        s(":-build_in(edge_label/2,'groove.prolog.builtin.graph.Predicate_edge_label').");
    }

    //
    @Signature({"Edge", "+"})
    @ToolTip("Checks if the edge has a binary role")
    public void edge_role_binary_1() {
        s(":-build_in(edge_role_binary/1,'groove.prolog.builtin.graph.Predicate_edge_role_binary').");
    }

    //
    @Signature({"Edge", "+"})
    @ToolTip("Checks if the edge has a flag role")
    public void edge_role_flag_1() {
        s(":-build_in(edge_role_flag/1,'groove.prolog.builtin.graph.Predicate_edge_role_flag').");
    }

    @Signature({"Edge", "+"})
    @ToolTip("Checks if the edge has a node type role")
    public void edge_role_node_type_1() {
        s(":-build_in(edge_role_node_type/1,'groove.prolog.builtin.graph.Predicate_edge_role_node_type').");
    }

    @Signature({"Graph", "Edge", "+?"})
    @ToolTip("Gets all binary edges in the graph")
    public void graph_binary_2() {
        s("graph_binary(G,E) :- graph_edge(G,E), edge_role_binary(E).");
    }

    @Signature({"Graph", "Edge", "+?"})
    @ToolTip("Gets all flag edges in the graph")
    public void graph_flag_2() {
        s("graph_flag(G,E) :- graph_edge(G,E), edge_role_flag(E).");
    }

    @Signature({"Graph", "Edge", "+?"})
    @ToolTip("Gets all node type edges in the graph")
    public void graph_node_type_2() {
        s("graph_node_type(G,E) :- graph_edge(G,E), edge_role_node_type(E).");
    }

    @ToolTip({"Gets the path from one node to an other"})
    @Signature({"Graph", "Node", "Node", "Path", "+++?"})
    @Param({"the graph that contains the nodes", "the starting node",
        "the destination node", "list of edges that define the path"})
    public void node_path_4() {
        s("node_path(Graph,From,To,Path):-          ");
        s("        node_path(Graph,From,To,Path,[]).");
    }

    @Signature({"Graph", "Node", "Node", "Path", "Visited", "+++??"})
    @ToolTip({
        "Internal predicate which does all the processing for node_path/4",
        "Helper predicate, stop processing when the start node is reached"})
    public void node_path_5() {
        s("node_path(Graph,From,From,[],_).            ");
        //
        s("node_path(Graph,From,To,[E|Path],Visited):- ");
        s("    node_out_edge(Graph,From,E),            ");
        s("    \\+ member(E,Visited),                  ");
        s("    edge_target(E,N),                       ");
        s("    From \\= N, % to abolish self edges     ");
        s("    node_path(Graph,N,To,Path,[E|Visited]). ");
    }

    @Signature({"Graph", "Node", "Labels", "+??"})
    @ToolTip({
        "Nodes from the graph that contain self edges with labels from the list.",
        "All the labels must be present, but more are allowed.",
        "<p>Example: start_graph(G),node_self_edges(G,Node,['Feature','includedFeature'])"})
    @Param({"the graph to query", "the node",
        "the list of labels of the self edges"})
    public void node_self_edges_3() {
        s(":-build_in(node_self_edges/3,'groove.prolog.builtin.graph.Predicate_node_self_edges').");
    }

    @Signature({"Graph", "Node", "Labels", "+??"})
    @ToolTip({
        "Same as node_self_edges/3 except that that the list is exclusive, thus the node",
        "may not contain more edges"})
    @Param({"the graph to query", "the node",
        "the list of labels of the self edges"})
    public void node_self_edges_excl_3() {
        s(":-build_in(node_self_edges_excl/3,'groove.prolog.builtin.graph.Predicate_node_self_edges_excl').");
    }

    @ToolTip({
        "Get the \"internal\" number of a node. Node numbers are volatile information,",
        "\"similar\" nodes in different graph states do not share the same number. You should",
        "not build algorithms around the usage of this predicate. Note, that all node",
        "forms contain numbers, this completely depends on the Groove implementation "})
    @Signature({"Node", "Integer", "+?"})
    @Param({"the node", "the node number"})
    public void node_number_2() {
        s(":-build_in(node_number/2,'groove.prolog.builtin.graph.Predicate_node_number').");
    }

    @ToolTip("Finds the node in the graph with a given number")
    @Signature({"Graph", "Node", "Number", "+??"})
    public void node_number_3() {
        s("node_number(Graph,Node,Number):-graph_node(Graph,Node),node_number(Node,Number).");
    }
}

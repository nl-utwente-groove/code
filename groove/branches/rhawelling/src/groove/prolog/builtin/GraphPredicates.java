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

import groove.annotation.Signature;
import groove.annotation.ToolTipBody;
import groove.annotation.ToolTipPars;

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
    @ToolTipBody("Fails if the first argument is not a Groove Graph")
    public void is_graph_1() {
        s(":-build_in(is_graph/1,'groove.prolog.builtin.graph.Predicate_is_graph').");
    }

    @Signature({"Node", "+"})
    @ToolTipBody("Fails if the first argument is not a Groove Node")
    public void is_node_1() {
        s(":-build_in(is_node/1,'groove.prolog.builtin.graph.Predicate_is_node').");
    }

    @Signature({"Edge", "+"})
    @ToolTipBody("Fails if the first argument is not a Groove Edge")
    public void is_edge_1() {
        s(":-build_in(is_edge/1,'groove.prolog.builtin.graph.Predicate_is_edge').");
    }

    @Signature({"Graph", "?"})
    @ToolTipBody("Retrieves the start graph")
    @ToolTipPars({"the graph"})
    public void start_graph_1() {
        s(":-build_in(start_graph/1,'groove.prolog.builtin.graph.Predicate_start_graph').");
    }

    @Signature({"String", "?"})
    @ToolTipBody("Retrieves the start graph name")
    @ToolTipPars({"the graph name"})
    public void start_graph_name_1() {
        s(":-build_in(start_graph_name/1,'groove.prolog.builtin.graph.Predicate_start_graph_name').");
    }

    @Signature({"Graph", "Node", "+?"})
    @ToolTipBody("Gets a node from a graph")
    @ToolTipPars({"the graph", "the node"})
    //    % @see groove.graph.GraphShape#nodeSet()"})
    public void graph_node_2() {
        s(":-build_in(graph_node/2,'groove.prolog.builtin.graph.Predicate_graph_node').");
    }

    @Signature({"Graph", "NodeSet", "+?"})
    @ToolTipBody("Gets the complete node set of a graph")
    @ToolTipPars({"the graph", "the list of nodes"})
    //    % @see groove.graph.GraphShape#nodeSet()"})
    public void graph_node_set_2() {
        s(":-build_in(graph_node_set/2,'groove.prolog.builtin.graph.Predicate_graph_node_set').");
    }

    @Signature({"Graph", "Count", "+?"})
    @ToolTipBody("Gets the number of nodes in a graph")
    @ToolTipPars({"the graph", "the number of nodes"})
    //    % @see groove.graph.GraphShape#nodeCount()
    public void graph_node_count_2() {
        s(":-build_in(graph_node_count/2,'groove.prolog.builtin.graph.Predicate_graph_node_count').");
    }

    @Signature({"Graph", "Edge", "+?"})
    @ToolTipBody("Gets an edge from a graph")
    @ToolTipPars({"the graph", "the edge"})
    //    % @see groove.graph.GraphShape#edgeSet()
    public void graph_edge_2() {
        s(":-build_in(graph_edge/2,'groove.prolog.builtin.graph.Predicate_graph_edge').");
    }

    @Signature({"Graph", "EdgeSet", "+?"})
    @ToolTipBody("Gets a set of edges from a graph")
    @ToolTipPars({"the graph ", "the list of edges"})
    //    % @see groove.graph.GraphShape#edgeSet()
    public void graph_edge_set_2() {
        s(":-build_in(graph_edge_set/2,'groove.prolog.builtin.graph.Predicate_graph_edge_set').");
    }

    @Signature({"Graph", "Count", "+?"})
    @ToolTipBody("Gets the number of edges in a graph")
    @ToolTipPars({"the graph", "the number of edges"})
    //    % @see groove.graph.GraphShape#edgeCount
    public void graph_edge_count_2() {
        s(":-build_in(graph_edge_count/2,'groove.prolog.builtin.graph.Predicate_graph_edge_count').");
    }

    @Signature({"Graph", "Node", "Edge", "++?"})
    @ToolTipBody("Gets an edge from a node, can be incoming or outgoing")
    @ToolTipPars({"the graph", "the node", "the edge"})
    //    % @see groove.graph.GraphShape#edgeSet(Node,int)
    public void node_edge_3() {
        s(":-build_in(node_edge/3,'groove.prolog.builtin.graph.Predicate_node_edge').");
    }

    @Signature({"Graph", "Node", "EdgeSet", "++?"})
    @ToolTipBody("Gets the set of edges for a single node. Both incoming and outgoing edges.")
    @ToolTipPars({"the graph", "the node", "the list of edges"})
    //    % @see groove.graph.GraphShape#edgeSet(Node,int)
    public void node_edge_set_3() {
        s(":-build_in(node_edge_set/3,'groove.prolog.builtin.graph.Predicate_node_edge_set').");
    }

    @Signature({"Graph", "Node", "Edge", "++?"})
    @ToolTipBody("Gets an outgoing edge from a node")
    @ToolTipPars({"the graph", "the node", "list of outgoing edges"})
    //    % @see groove.graph.GraphShape#outEdgeSet(Node)
    public void node_out_edge_3() {
        s(":-build_in(node_out_edge/3,'groove.prolog.builtin.graph.Predicate_node_out_edge').");
    }

    @Signature({"Graph", "Node", "EdgeSet", "++?"})
    @ToolTipBody("Gets the outgoing edges for a given node")
    @ToolTipPars({"the graph", "the node", "list of outgoing edges"})
    //    % @see groove.graph.GraphShape#outEdgeSet(Node)
    public void node_out_edge_set_3() {
        s(":-build_in(node_out_edge_set/3,'groove.prolog.builtin.graph.Predicate_node_out_edge_set').");
    }

    @Signature({"Graph", "Label", "Edge", "++?"})
    @ToolTipBody("Gets an edge with a given label")
    @ToolTipPars({"the graph", "the label", "the edges"})
    //    % @see groove.graph.GraphShape#labelEdgeSet(int,Label)
    public void label_edge_3() {
        s(":-build_in(label_edge/3,'groove.prolog.builtin.graph.Predicate_label_edge').");
    }

    @Signature({"Graph", "Label", "EdgeSet", "++?"})
    @ToolTipBody("Gets the edge set of a graph with a given label")
    @ToolTipPars({"the graph", "the label", "the list of edges"})
    //    % @see groove.graph.GraphShape#labelEdgeSet(int,Label)
    public void label_edge_set_3() {
        s(":-build_in(label_edge_set/3,'groove.prolog.builtin.graph.Predicate_label_edge_set').");
    }

    @Signature({"Edge", "Node", "+?"})
    @ToolTipBody("Gets the source node of an edge")
    @ToolTipPars({"the edge", "the node"})
    //    % @see groove.graph.Edge#source()
    public void edge_source_2() {
        s(":-build_in(edge_source/2,'groove.prolog.builtin.graph.Predicate_edge_source').");
    }

    @Signature({"Edge", "Node", "+?"})
    @ToolTipBody("Gets the destination node of an edge (opposite of the source)")
    @ToolTipPars({"the edge", "the node"})
    //    % @see groove.graph.Edge#target()
    public void edge_target_2() {
        s(":-build_in(edge_target/2,'groove.prolog.builtin.graph.Predicate_edge_target').");
    }

    @Signature({"Edge", "Label", "+?"})
    @ToolTipBody("Gets the label text of the edge")
    @ToolTipPars({"the edge", "the label text"})
    //    % @see groove.graph.Edge#label()
    public void edge_label_2() {
        s(":-build_in(edge_label/2,'groove.prolog.builtin.graph.Predicate_edge_label').");
    }

    //
    @Signature({"Edge", "+"})
    @ToolTipBody("Checks if the edge has a binary role")
    public void edge_role_binary_1() {
        s(":-build_in(edge_role_binary/1,'groove.prolog.builtin.graph.Predicate_edge_role_binary').");
    }

    //
    @Signature({"Edge", "+"})
    @ToolTipBody("Checks if the edge has a flag role")
    public void edge_role_flag_1() {
        s(":-build_in(edge_role_flag/1,'groove.prolog.builtin.graph.Predicate_edge_role_flag').");
    }

    @Signature({"Edge", "+"})
    @ToolTipBody("Checks if the edge has a node type role")
    public void edge_role_node_type_1() {
        s(":-build_in(edge_role_node_type/1,'groove.prolog.builtin.graph.Predicate_edge_role_node_type').");
    }

    @Signature({"Graph", "Edge", "+?"})
    @ToolTipBody("Gets all binary edges in the graph")
    public void graph_binary_2() {
        s("graph_binary(G,E) :- graph_edge(G,E), edge_role_binary(E).");
    }

    @Signature({"Graph", "Edge", "+?"})
    @ToolTipBody("Gets all flag edges in the graph")
    public void graph_flag_2() {
        s("graph_flag(G,E) :- graph_edge(G,E), edge_role_flag(E).");
    }

    @Signature({"Graph", "Edge", "+?"})
    @ToolTipBody("Gets all node type edges in the graph")
    public void graph_node_type_2() {
        s("graph_node_type(G,E) :- graph_edge(G,E), edge_role_node_type(E).");
    }

    @Signature({"Graph", "String", "++"})
    @ToolTipBody("Succeeds if the graph has at least a node with the given node type")
    public void has_node_type_2() {
        s("has_node_type(G,T) :- graph_node_type(G,E), edge_label(E,L), L == T.");
    }

    @ToolTipBody({"Gets the path from one node to an other"})
    @Signature({"Graph", "Node", "Node", "Path", "+++?"})
    @ToolTipPars({"the graph that contains the nodes", "the starting node", "the destination node",
        "list of edges that define the path"})
    public void node_path_4() {
        s("node_path(Graph,From,To,Path):-          ");
        s("        node_path(Graph,From,To,Path,[]).");
    }

    @Signature({"Graph", "Node", "Node", "Path", "Visited", "+++??"})
    @ToolTipBody({"Internal predicate which does all the processing for node_path/4",
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
    @ToolTipBody({"Nodes from the graph that contain self edges with labels from the list.",
        "All the labels must be present, but more are allowed.",
        "<p>Example: start_graph(G),node_self_edges(G,Node,['Feature','includedFeature'])"})
    @ToolTipPars({"the graph to query", "the node", "the list of labels of the self edges"})
    public void node_self_edges_3() {
        s(":-build_in(node_self_edges/3,'groove.prolog.builtin.graph.Predicate_node_self_edges').");
    }

    @Signature({"Graph", "Node", "Labels", "+??"})
    @ToolTipBody({
        "Same as node_self_edges/3 except that that the list is exclusive, thus the node",
        "may not contain more edges"})
    @ToolTipPars({"the graph to query", "the node", "the list of labels of the self edges"})
    public void node_self_edges_excl_3() {
        s(":-build_in(node_self_edges_excl/3,'groove.prolog.builtin.graph.Predicate_node_self_edges_excl').");
    }

    @ToolTipBody({"Get the \"internal\" number of a node. Node numbers are volatile information,",
        "\"similar\" nodes in different graph states do not share the same number. You should",
        "not build algorithms around the usage of this predicate. Note, that all node",
        "forms contain numbers, this completely depends on the Groove implementation "})
    @Signature({"Node", "Integer", "+?"})
    @ToolTipPars({"the node", "the node number"})
    public void node_number_2() {
        s(":-build_in(node_number/2,'groove.prolog.builtin.graph.Predicate_node_number').");
    }

    @ToolTipBody("Finds the node in the graph with a given number")
    @Signature({"Graph", "Node", "Number", "+??"})
    public void node_number_3() {
        s("node_number(Graph,Node,Number):-graph_node(Graph,Node),node_number(Node,Number).");
    }

    @Signature({"Graph", "+"})
    @ToolTipBody("Displays the given graph in a new preview dialog.")
    public void show_graph_1() {
        s(":-build_in(show_graph/1,'groove.prolog.builtin.graph.Predicate_show_graph').");
    }

    @Signature({"Graph", "String", "+?"})
    @ToolTipBody("Saves the given graph into the given file.")
    @ToolTipPars({"the graph to save",
        "file name to save to (the extension .gst is appended), if left empty, the graph name is used."})
    public void save_graph_2() {
        s(":-build_in(save_graph/2,'groove.prolog.builtin.graph.Predicate_save_graph').");
    }
}

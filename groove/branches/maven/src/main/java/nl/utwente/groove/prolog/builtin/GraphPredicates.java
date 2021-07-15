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
package nl.utwente.groove.prolog.builtin;

import nl.utwente.groove.annotation.Signature;
import nl.utwente.groove.annotation.ToolTipBody;
import nl.utwente.groove.annotation.ToolTipPars;
import nl.utwente.groove.prolog.builtin.graph.Predicate_edge_label;
import nl.utwente.groove.prolog.builtin.graph.Predicate_edge_role_binary;
import nl.utwente.groove.prolog.builtin.graph.Predicate_edge_role_flag;
import nl.utwente.groove.prolog.builtin.graph.Predicate_edge_role_node_type;
import nl.utwente.groove.prolog.builtin.graph.Predicate_edge_source;
import nl.utwente.groove.prolog.builtin.graph.Predicate_edge_target;
import nl.utwente.groove.prolog.builtin.graph.Predicate_graph_edge;
import nl.utwente.groove.prolog.builtin.graph.Predicate_graph_edge_count;
import nl.utwente.groove.prolog.builtin.graph.Predicate_graph_edge_set;
import nl.utwente.groove.prolog.builtin.graph.Predicate_graph_node;
import nl.utwente.groove.prolog.builtin.graph.Predicate_graph_node_count;
import nl.utwente.groove.prolog.builtin.graph.Predicate_graph_node_set;
import nl.utwente.groove.prolog.builtin.graph.Predicate_is_edge;
import nl.utwente.groove.prolog.builtin.graph.Predicate_is_graph;
import nl.utwente.groove.prolog.builtin.graph.Predicate_is_node;
import nl.utwente.groove.prolog.builtin.graph.Predicate_label_edge;
import nl.utwente.groove.prolog.builtin.graph.Predicate_label_edge_set;
import nl.utwente.groove.prolog.builtin.graph.Predicate_node_edge;
import nl.utwente.groove.prolog.builtin.graph.Predicate_node_edge_set;
import nl.utwente.groove.prolog.builtin.graph.Predicate_node_number;
import nl.utwente.groove.prolog.builtin.graph.Predicate_node_out_edge;
import nl.utwente.groove.prolog.builtin.graph.Predicate_node_out_edge_set;
import nl.utwente.groove.prolog.builtin.graph.Predicate_node_self_edges;
import nl.utwente.groove.prolog.builtin.graph.Predicate_node_self_edges_excl;
import nl.utwente.groove.prolog.builtin.graph.Predicate_save_graph;
import nl.utwente.groove.prolog.builtin.graph.Predicate_show_graph;
import nl.utwente.groove.prolog.builtin.graph.Predicate_start_graph;
import nl.utwente.groove.prolog.builtin.graph.Predicate_start_graph_name;

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
        s(Predicate_is_graph.class, 1);
    }

    @Signature({"Node", "+"})
    @ToolTipBody("Fails if the first argument is not a Groove Node")
    public void is_node_1() {
        s(Predicate_is_node.class, 1);
    }

    @Signature({"Edge", "+"})
    @ToolTipBody("Fails if the first argument is not a Groove Edge")
    public void is_edge_1() {
        s(Predicate_is_edge.class, 1);
    }

    @Signature({"Graph", "?"})
    @ToolTipBody("Retrieves the start graph")
    @ToolTipPars({"the graph"})
    public void start_graph_1() {
        s(Predicate_start_graph.class, 1);
    }

    @Signature({"String", "?"})
    @ToolTipBody("Retrieves the start graph name")
    @ToolTipPars({"the graph name"})
    public void start_graph_name_1() {
        s(Predicate_start_graph_name.class, 1);
    }

    @Signature({"Graph", "Node", "+?"})
    @ToolTipBody("Gets a node from a graph")
    @ToolTipPars({"the graph", "the node"})
    //    % @see groove.graph.GraphShape#nodeSet()"})
    public void graph_node_2() {
        s(Predicate_graph_node.class, 2);
    }

    @Signature({"Graph", "NodeSet", "+?"})
    @ToolTipBody("Gets the complete node set of a graph")
    @ToolTipPars({"the graph", "the list of nodes"})
    //    % @see groove.graph.GraphShape#nodeSet()"})
    public void graph_node_set_2() {
        s(Predicate_graph_node_set.class, 2);
    }

    @Signature({"Graph", "Count", "+?"})
    @ToolTipBody("Gets the number of nodes in a graph")
    @ToolTipPars({"the graph", "the number of nodes"})
    //    % @see groove.graph.GraphShape#nodeCount()
    public void graph_node_count_2() {
        s(Predicate_graph_node_count.class, 2);
    }

    @Signature({"Graph", "Edge", "+?"})
    @ToolTipBody("Gets an edge from a graph")
    @ToolTipPars({"the graph", "the edge"})
    //    % @see groove.graph.GraphShape#edgeSet()
    public void graph_edge_2() {
        s(Predicate_graph_edge.class, 2);
    }

    @Signature({"Graph", "EdgeSet", "+?"})
    @ToolTipBody("Gets a set of edges from a graph")
    @ToolTipPars({"the graph ", "the list of edges"})
    //    % @see groove.graph.GraphShape#edgeSet()
    public void graph_edge_set_2() {
        s(Predicate_graph_edge_set.class, 2);
    }

    @Signature({"Graph", "Count", "+?"})
    @ToolTipBody("Gets the number of edges in a graph")
    @ToolTipPars({"the graph", "the number of edges"})
    //    % @see groove.graph.GraphShape#edgeCount
    public void graph_edge_count_2() {
        s(Predicate_graph_edge_count.class, 2);
    }

    @Signature({"Graph", "Node", "Edge", "++?"})
    @ToolTipBody("Gets an edge from a node, can be incoming or outgoing")
    @ToolTipPars({"the graph", "the node", "the edge"})
    //    % @see groove.graph.GraphShape#edgeSet(Node,int)
    public void node_edge_3() {
        s(Predicate_node_edge.class, 3);
    }

    @Signature({"Graph", "Node", "EdgeSet", "++?"})
    @ToolTipBody("Gets the set of edges for a single node. Both incoming and outgoing edges.")
    @ToolTipPars({"the graph", "the node", "the list of edges"})
    //    % @see groove.graph.GraphShape#edgeSet(Node,int)
    public void node_edge_set_3() {
        s(Predicate_node_edge_set.class, 3);
    }

    @Signature({"Graph", "Node", "Edge", "++?"})
    @ToolTipBody("Gets an outgoing edge from a node")
    @ToolTipPars({"the graph", "the node", "list of outgoing edges"})
    //    % @see groove.graph.GraphShape#outEdgeSet(Node)
    public void node_out_edge_3() {
        s(Predicate_node_out_edge.class, 3);
    }

    @Signature({"Graph", "Node", "EdgeSet", "++?"})
    @ToolTipBody("Gets the outgoing edges for a given node")
    @ToolTipPars({"the graph", "the node", "list of outgoing edges"})
    //    % @see groove.graph.GraphShape#outEdgeSet(Node)
    public void node_out_edge_set_3() {
        s(Predicate_node_out_edge_set.class, 3);
    }

    @Signature({"Graph", "Label", "Edge", "++?"})
    @ToolTipBody("Gets an edge with a given label")
    @ToolTipPars({"the graph", "the label", "the edges"})
    //    % @see groove.graph.GraphShape#labelEdgeSet(int,Label)
    public void label_edge_3() {
        s(Predicate_label_edge.class, 3);
    }

    @Signature({"Graph", "Label", "EdgeSet", "++?"})
    @ToolTipBody("Gets the edge set of a graph with a given label")
    @ToolTipPars({"the graph", "the label", "the list of edges"})
    //    % @see groove.graph.GraphShape#labelEdgeSet(int,Label)
    public void label_edge_set_3() {
        s(Predicate_label_edge_set.class, 3);
    }

    @Signature({"Edge", "Node", "+?"})
    @ToolTipBody("Gets the source node of an edge")
    @ToolTipPars({"the edge", "the node"})
    //    % @see groove.graph.Edge#source()
    public void edge_source_2() {
        s(Predicate_edge_source.class, 2);
    }

    @Signature({"Edge", "Node", "+?"})
    @ToolTipBody("Gets the destination node of an edge (opposite of the source)")
    @ToolTipPars({"the edge", "the node"})
    //    % @see groove.graph.Edge#target()
    public void edge_target_2() {
        s(Predicate_edge_target.class, 2);
    }

    @Signature({"Edge", "Label", "+?"})
    @ToolTipBody("Gets the label text of the edge")
    @ToolTipPars({"the edge", "the label text"})
    //    % @see groove.graph.Edge#label()
    public void edge_label_2() {
        s(Predicate_edge_label.class, 2);
    }

    //
    @Signature({"Edge", "+"})
    @ToolTipBody("Checks if the edge has a binary role")
    public void edge_role_binary_1() {
        s(Predicate_edge_role_binary.class, 1);
    }

    //
    @Signature({"Edge", "+"})
    @ToolTipBody("Checks if the edge has a flag role")
    public void edge_role_flag_1() {
        s(Predicate_edge_role_flag.class, 1);
    }

    @Signature({"Edge", "+"})
    @ToolTipBody("Checks if the edge has a node type role")
    public void edge_role_node_type_1() {
        s(Predicate_edge_role_node_type.class, 1);
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
        s(Predicate_node_self_edges.class, 3);
    }

    @Signature({"Graph", "Node", "Labels", "+??"})
    @ToolTipBody({"Same as node_self_edges/3 except that that the list is exclusive, thus the node",
        "may not contain more edges"})
    @ToolTipPars({"the graph to query", "the node", "the list of labels of the self edges"})
    public void node_self_edges_excl_3() {
        s(Predicate_node_self_edges_excl.class, 3);
    }

    @ToolTipBody({"Get the \"internal\" number of a node. Node numbers are volatile information,",
        "\"similar\" nodes in different graph states do not share the same number. You should",
        "not build algorithms around the usage of this predicate. Note, that all node",
        "forms contain numbers, this completely depends on the Groove implementation "})
    @Signature({"Node", "Integer", "+?"})
    @ToolTipPars({"the node", "the node number"})
    public void node_number_2() {
        s(Predicate_node_number.class, 2);
    }

    @ToolTipBody("Finds the node in the graph with a given number")
    @Signature({"Graph", "Node", "Number", "+??"})
    public void node_number_3() {
        s("node_number(Graph,Node,Number):-graph_node(Graph,Node),node_number(Node,Number).");
    }

    @Signature({"Graph", "+"})
    @ToolTipBody("Displays the given graph in a new preview dialog.")
    public void show_graph_1() {
        s(Predicate_show_graph.class, 1);
    }

    @Signature({"Graph", "String", "+?"})
    @ToolTipBody("Saves the given graph into the given file.")
    @ToolTipPars({"the graph to save",
        "file name to save to (the extension .gst is appended), if left empty, the graph name is used."})
    public void save_graph_2() {
        s(Predicate_save_graph.class, 2);
    }
}

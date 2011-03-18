///*
// * Groove Prolog Interface
// * Copyright (C) 2009 Michiel Hendriks, University of Twente
// * 
// * This library is free software; you can redistribute it and/or
// * modify it under the terms of the GNU Lesser General Public
// * License as published by the Free Software Foundation; either
// * version 2.1 of the License, or (at your option) any later version.
// * 
// * This library is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// * Lesser General Public License for more details.
// * 
// * You should have received a copy of the GNU Lesser General Public
// * License along with this library; if not, write to the Free Software
// * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
// */
//package groove.prolog.builtin.graph.mod;
//
//import gnu.prolog.term.AtomTerm;
//import gnu.prolog.term.CompoundTerm;
//import gnu.prolog.term.CompoundTermTag;
//import gnu.prolog.term.Term;
//import gnu.prolog.vm.Interpreter;
//import gnu.prolog.vm.PrologException;
//import groove.graph.Edge;
//import groove.graph.Graph;
//import groove.graph.Node;
//
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.Set;
//
///**
// * 
// * 
// * @author Michiel Hendriks
// */
//public class Predicate_graph_remove_edge extends GraphModPrologCode {
//    public static final CompoundTermTag NODES_TAG = CompoundTermTag.get(
//        "nodes", 1);
//    public static final AtomTerm REMOVE_ATOM = AtomTerm.get("remove");
//
//    public Predicate_graph_remove_edge() {
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see gnu.prolog.vm.PrologCode#execute(gnu.prolog.vm.Interpreter, boolean,
//     * gnu.prolog.term.Term[])
//     */
//    public int execute(Interpreter interpreter, boolean backtrackMode,
//            Term[] args) throws PrologException {
//        Graph<?,?> graph = getGraph(args[0]);
//        if (graph.isFixed()) {
//            PrologException.domainError(
//                GraphModPrologCode.READ_ONLY_GRAPH_ATOM, args[0]);
//        }
//        boolean removeNodes =
//            hasOption(interpreter, args[2], NODES_TAG, new Term[] {REMOVE_ATOM});
//
//        Set<Edge<Node>> edges;
//        if (CompoundTerm.isListPair(args[1])) {
//            edges = new HashSet<Edge<Node>>();
//            Set<Term> values = new HashSet<Term>();
//            CompoundTerm.toCollection(args[1], values);
//            for (Term val : values) {
//                edges.add(getEdge(val));
//            }
//        } else {
//            edges = Collections.singleton(getEdge(args[1]));
//        }
//
//        for (Edge<Node> edge : edges) {
//            Node[] ends = edge.ends();
//            if (graph.removeEdge(edge)) {
//                if (removeNodes) {
//                    for (Node node : ends) {
//                        if (graph.edgeSet(node).isEmpty()) {
//                            graph.removeNode(node);
//                        }
//                    }
//                }
//                return SUCCESS_LAST;
//            }
//        }
//        return FAIL;
//    }
//
//}

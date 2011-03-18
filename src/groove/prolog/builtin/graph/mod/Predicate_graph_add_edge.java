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
//import gnu.prolog.term.JavaObjectTerm;
//import gnu.prolog.term.Term;
//import gnu.prolog.vm.Interpreter;
//import gnu.prolog.vm.PrologException;
//import gnu.prolog.vm.TermConstants;
//import groove.graph.DefaultLabel;
//import groove.graph.Edge;
//import groove.graph.Label;
//import groove.trans.HostGraph;
//import groove.trans.HostNode;
//
///**
// * 
// * 
// * @author Michiel Hendriks
// */
//public class Predicate_graph_add_edge extends GraphModPrologCode {
//    /*
//     * (non-Javadoc)
//     * @see gnu.prolog.vm.PrologCode#execute(gnu.prolog.vm.Interpreter, boolean,
//     * gnu.prolog.term.Term[])
//     */
//    // TODO: Should be tested
//    public int execute(Interpreter interpreter, boolean backtrackMode,
//            Term[] args) throws PrologException {
//        HostGraph graph = (HostGraph) getGraph(args[0]);
//        if (graph.isFixed()) {
//            PrologException.domainError(
//                GraphModPrologCode.READ_ONLY_GRAPH_ATOM, args[0]);
//        }
//        HostNode source = (HostNode) getNode(args[1]);
//        HostNode target = (HostNode) getNode(args[2]);
//        Label label = null;
//        if (args[3] instanceof AtomTerm) {
//            label = DefaultLabel.createLabel(((AtomTerm) args[3]).value);
//        } else {
//            PrologException.typeError(TermConstants.atomAtom, args[3]);
//        }
//        Edge<?> edge = graph.addEdge(source, label, target);
//        return interpreter.unify(args[4], new JavaObjectTerm(edge));
//    }
//
//}

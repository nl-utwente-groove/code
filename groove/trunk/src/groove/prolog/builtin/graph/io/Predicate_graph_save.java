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
//package groove.prolog.builtin.graph.io;
//
//import gnu.prolog.term.AtomTerm;
//import gnu.prolog.term.Term;
//import gnu.prolog.vm.Interpreter;
//import gnu.prolog.vm.PrologException;
//import gnu.prolog.vm.TermConstants;
//import groove.graph.Graph;
//import groove.io.DefaultGxl;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//
///**
// * 
// * 
// * @author Michiel Hendriks
// */
//public class Predicate_graph_save extends GraphIOPrologCode {
//    /*
//     * (non-Javadoc)
//     * @see gnu.prolog.vm.PrologCode#execute(gnu.prolog.vm.Interpreter, boolean,
//     * gnu.prolog.term.Term[])
//     */
//    public int execute(Interpreter interpreter, boolean backtrackMode,
//            Term[] args) throws PrologException {
//        Graph<?,?> graph = getGraph(args[0]);
//        if (!(args[1] instanceof AtomTerm)) {
//            PrologException.typeError(TermConstants.atomAtom, args[1]);
//        }
//        File file = new File(((AtomTerm) args[1]).value);
//        try {
//            DefaultGxl out = new DefaultGxl();
//            out.marshalGraph(graph, file);
//        } catch (IOException e) {
//            AtomTerm errorCode;
//            if (e instanceof FileNotFoundException) {
//                errorCode = AtomTerm.get("file_not_found");
//            } else {
//                errorCode = AtomTerm.get(e.getClass().getSimpleName());
//            }
//            PrologException.domainError(errorCode, args[1]);
//        }
//        return SUCCESS_LAST;
//    }
//
//}

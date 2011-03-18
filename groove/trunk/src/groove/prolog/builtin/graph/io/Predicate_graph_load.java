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
//import gnu.prolog.term.JavaObjectTerm;
//import gnu.prolog.term.Term;
//import gnu.prolog.vm.Interpreter;
//import gnu.prolog.vm.PrologException;
//import gnu.prolog.vm.TermConstants;
//import groove.io.AspectGxl;
//import groove.trans.SystemProperties;
//import groove.view.DefaultGraphView;
//import groove.view.aspect.AspectGraph;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.net.MalformedURLException;
//import java.net.URL;
//
///**
// * 
// * 
// * @author Michiel Hendriks
// */
//public class Predicate_graph_load extends GraphIOPrologCode {
//    /*
//     * (non-Javadoc)
//     * @see gnu.prolog.vm.PrologCode#execute(gnu.prolog.vm.Interpreter, boolean,
//     * gnu.prolog.term.Term[])
//     */
//    public int execute(Interpreter interpreter, boolean backtrackMode,
//            Term[] args) throws PrologException {
//        if (!(args[0] instanceof AtomTerm)) {
//            PrologException.typeError(TermConstants.atomAtom, args[0]);
//        }
//        URL url = null;
//        try {
//            url = new URL(((AtomTerm) args[0]).value);
//        } catch (MalformedURLException e1) {
//            File fl = new File(((AtomTerm) args[0]).value);
//            if (fl.exists()) {
//                try {
//                    url = fl.toURI().toURL();
//                } catch (MalformedURLException e) {
//                    url = null;
//                }
//            } else {
//                url = null;
//            }
//            if (url == null) {
//                PrologException.domainError(AtomTerm.get("malformed_url"),
//                    args[0]);
//                return FAIL;
//            }
//        }
//        AspectGxl out = new AspectGxl();
//        try {
//            AspectGraph agraph = out.unmarshalGraph(url);
//            DefaultGraphView agv =
//                new DefaultGraphView(agraph, new SystemProperties());
//            return interpreter.unify(args[1], new JavaObjectTerm(agv.toModel()));
//        } catch (Exception e) {
//            AtomTerm errorCode;
//            if (e instanceof FileNotFoundException) {
//                errorCode = AtomTerm.get("file_not_found");
//            } else {
//                errorCode = AtomTerm.get(e.getClass().getSimpleName());
//            }
//            PrologException.domainError(errorCode, args[0]);
//        }
//        return FAIL;
//    }
//
//}

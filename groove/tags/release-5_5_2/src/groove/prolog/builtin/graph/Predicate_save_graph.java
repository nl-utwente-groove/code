/*
 * Groove Prolog Interface
 * Copyright (C) 2009 Michiel Hendriks, University of Twente
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package groove.prolog.builtin.graph;

import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.JavaObjectTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologException;
import groove.graph.Graph;
import groove.io.FileType;
import groove.io.graph.GxlIO;

import java.io.File;
import java.io.IOException;

/**
 * Predicate save_graph(+Graph, ?FileName)
 * @author Eduardo Zambon
 */
public class Predicate_save_graph extends GraphPrologCode {
    @Override
    public int execute(Interpreter interpreter, boolean backtrackMode, Term[] args)
        throws PrologException {
        try {
            Graph graph = (Graph) ((JavaObjectTerm) args[0]).value;
            String fileName;
            if (args[1] instanceof AtomTerm) {
                fileName = ((AtomTerm) args[1]).value;
            } else {
                fileName = graph.getName();
            }
            fileName += FileType.STATE.getExtension();
            File file = new File(fileName);
            GxlIO.instance().saveGraph(graph, file);
            return SUCCESS_LAST;
        } catch (ClassCastException e) {
            PrologException.typeError(GRAPH_ATOM, args[0]);
            return FAIL;
        } catch (IOException e) {
            PrologException.systemError(e);
            return FAIL;
        }
    }
}

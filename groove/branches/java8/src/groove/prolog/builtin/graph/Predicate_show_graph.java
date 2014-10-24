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

import gnu.prolog.term.JavaObjectTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologException;
import groove.graph.Graph;
import groove.gui.dialog.GraphPreviewDialog;

/**
 * Predicate show_graph(+Graph)
 * @author Eduardo Zambon
 */
public class Predicate_show_graph extends GraphPrologCode {
    @Override
    public int execute(Interpreter interpreter, boolean backtrackMode, Term[] args)
        throws PrologException {
        try {
            Graph graph = (Graph) ((JavaObjectTerm) args[0]).value;
            GraphPreviewDialog.showGraph(graph);
            return SUCCESS_LAST;
        } catch (ClassCastException e) {
            PrologException.typeError(GRAPH_ATOM, args[0]);
            return FAIL;
        }
    }
}

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
import gnu.prolog.term.Term;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologException;
import groove.prolog.GrooveEnvironment;

/**
 * Predicate start_graph_name(?Name).
 * @author Lesley Wevers
 */
public class Predicate_start_graph_name extends GraphPrologCode {
    @Override
    public int execute(Interpreter interpreter, boolean backtrackMode,
            Term[] args) throws PrologException {
        if (!(interpreter.getEnvironment() instanceof GrooveEnvironment)) {
            GrooveEnvironment.invalidEnvironment();
        }

        try {
            String startGraphName =
                ((GrooveEnvironment) interpreter.getEnvironment()).getGrooveState().getGraphGrammar().getStartGraph().getName();

            if (startGraphName == null) {
                return FAIL;
            }

            Term value = AtomTerm.get(startGraphName);
            return interpreter.unify(args[0], value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return FAIL;
    }
}

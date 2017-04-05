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
package groove.prolog.builtin.algebra;

import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.JavaObjectTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.PrologException;
import groove.grammar.host.ValueNode;
import groove.prolog.builtin.graph.GraphPrologCode;

/**
 * This class contains some utility methods to extract Java objects from prolog terms
 * 
 * @author Michiel Hendriks
 */
public abstract class AlgebraPrologCode extends GraphPrologCode {
    /**
     * Prolog atom "valuenode"
     */
    public static final AtomTerm VALUENODE_ATOM = AtomTerm.get("valuenode");

    /**
     * Gets a valuenode from a term
     * @param term      A term representing a valuenode
     * @return          A valuenode
     */
    public static final ValueNode getValueNode(Term term)
        throws PrologException {
        if (term instanceof JavaObjectTerm) {
            JavaObjectTerm jot = (JavaObjectTerm) term;
            if (!(jot.value instanceof ValueNode)) {
                PrologException.domainError(VALUENODE_ATOM, term);
            }
            return (ValueNode) jot.value;
        } else {
            PrologException.typeError(VALUENODE_ATOM, term);
        }
        return null;
    }
}

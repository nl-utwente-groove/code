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
import gnu.prolog.term.FloatTerm;
import gnu.prolog.term.IntegerTerm;
import gnu.prolog.term.JavaObjectTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.Environment;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologException;
import groove.algebra.Algebra;
import groove.grammar.host.ValueNode;

/**
 * Predicate convert_valuenode(+ValueNode,?Atom)
 * @author Michiel Hendriks
 */
public class Predicate_convert_valuenode extends AlgebraPrologCode {
    @Override
    public int execute(Interpreter interpreter, boolean backtrackMode,
            Term[] args) throws PrologException {
        try {
            ValueNode node = getValueNode(args[0]);

            Term result;
            Algebra<?> alg = node.getAlgebra();
            Object value = alg.toJavaValue(node.getValue());
            switch (alg.getSort()) {
            case BOOL:
                result = new JavaObjectTerm(value);
                break;
            case INT:
                result = IntegerTerm.get((Integer) value);
                break;
            case REAL:
                result = new FloatTerm((Double) value);
                break;
            case STRING:
                result = AtomTerm.get((String) value);
                break;
            default:
                result = null;
                assert false;
            }
            return interpreter.unify(args[1], result);
        } catch (Exception e) {
            return FAIL;
        }
    }

    @Override
    public void install(Environment env) {
        /**
         * Left blank by design
         */
    }

    @Override
    public void uninstall(Environment env) {
        /**
         * Left blank by design
         */
    }
}

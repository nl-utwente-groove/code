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
import groove.algebra.BigDoubleAlgebra;
import groove.algebra.BigIntAlgebra;
import groove.algebra.JavaDoubleAlgebra;
import groove.algebra.JavaIntAlgebra;
import groove.algebra.StringAlgebra;
import groove.graph.algebra.ValueNode;

/**
 * <code>convert_valuenode(ValueNode,Term)</code>
 * 
 * @author Michiel Hendriks
 */
public class Predicate_convert_valuenode extends AlgebraPrologCode {
    /*
     * (non-Javadoc)
     * @see gnu.prolog.vm.PrologCode#execute(gnu.prolog.vm.Interpreter, boolean,
     * gnu.prolog.term.Term[])
     */
    public int execute(Interpreter interpreter, boolean backtrackMode,
            Term[] args) throws PrologException {
        try {
            ValueNode node = getValueNode(args[0]);

            Term result = null;
            Algebra<?> alg = node.getAlgebra();
            if (alg instanceof StringAlgebra) {
                result = AtomTerm.get((String) node.getValue());
            } else if (alg instanceof BigIntAlgebra
                || alg instanceof JavaIntAlgebra) {
                Integer val = (Integer) node.getValue();
                result = IntegerTerm.get(val);
            } else if (alg instanceof BigDoubleAlgebra
                || alg instanceof JavaDoubleAlgebra) {
                Double val = (Double) node.getValue();
                result = new FloatTerm(val);
            } else {
                result = new JavaObjectTerm(node.getValue());
            }
            return interpreter.unify(args[1], result);
        } catch (Exception e) {
            return FAIL;
        }
    }

    /*
     * (non-Javadoc)
     * @see gnu.prolog.vm.PrologCode#install(gnu.prolog.vm.Environment)
     */
    @Override
    public void install(Environment env) {
        /**
         * TODO
         */
    }

    /*
     * (non-Javadoc)
     * @see gnu.prolog.vm.PrologCode#uninstall(gnu.prolog.vm.Environment)
     */
    @Override
    public void uninstall(Environment env) {
        /**
         * TODO
         */
    }
}

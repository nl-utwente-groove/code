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
import groove.algebra.Constant;
import groove.graph.algebra.ValueNode;

import java.math.BigDecimal;
import java.math.BigInteger;

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
            Object value = node.getValue();
            switch (alg.getKind()) {
            case BOOL:
                result = new JavaObjectTerm(value);
                break;
            case INT:
                Integer intValue;
                switch (alg.getFamily()) {
                case DEFAULT:
                    intValue = (Integer) value;
                    break;
                case BIG:
                    intValue = ((BigInteger) value).intValue();
                    break;
                case POINT:
                    intValue = Integer.parseInt((String) value);
                    break;
                case TERM:
                    intValue = Integer.parseInt(((Constant) value).getSymbol());
                    break;
                default:
                    intValue = null;
                    assert false;
                }
                result = IntegerTerm.get(intValue);
                break;
            case REAL:
                Double realValue;
                switch (alg.getFamily()) {
                case DEFAULT:
                    realValue = (Double) value;
                    break;
                case BIG:
                    realValue = ((BigDecimal) value).doubleValue();
                    break;
                case POINT:
                    realValue = Double.parseDouble((String) value);
                    break;
                case TERM:
                    realValue =
                        Double.parseDouble(((Constant) value).getSymbol());
                    break;
                default:
                    realValue = null;
                    assert false;
                }
                result = new FloatTerm(realValue);
                break;
            case STRING:
                String stringValue;
                switch (alg.getFamily()) {
                case DEFAULT:
                case BIG:
                case POINT:
                    stringValue = (String) value;
                    break;
                case TERM:
                    stringValue = ((Constant) value).getSymbol();
                    break;
                default:
                    stringValue = null;
                    assert false;
                }
                result = AtomTerm.get(stringValue);
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

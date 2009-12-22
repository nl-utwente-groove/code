/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: DefaultIntegerAlgebra.java,v 1.10 2008-01-26 09:47:32 kastenberg Exp $
 */

package groove.algebra;

import groove.util.Groove;

import java.util.List;

/**
 * Default integer algebra, in which natural numbers serve as constants.
 * 
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2008-01-26 09:47:32 $
 * @deprecated Superseded by the new algebra implementation
 */
@Deprecated
public class DefaultIntegerAlgebra extends OldAlgebra {
    /**
     * Constructs the (singleton) instance of this class.
     */
    private DefaultIntegerAlgebra() {
        super(NAME, DESCRIPTION);
    }

    @Override
    public Constant getConstant(String symbol) {
        Constant result;
        try {
            int value = Integer.parseInt(symbol);
            result = new IntegerConstant(value);
        } catch (NumberFormatException nfe) {
            result = null;
        }
        return result;
    }

    @Override
    public String getSymbol(Object value) {
        if (!(value instanceof Integer)) {
            throw new IllegalArgumentException(String.format(
                "Value is of class %s rather than Integer", value.getClass()));
        }
        return value.toString();
    }

    /** Returns the {@link Constant} corresponding to a given integer value. */
    static public IntegerConstant getInteger(int value) {
        return new IntegerConstant(value);
    }

    /**
     * Method facilitating the singleton-pattern.
     * @return the single <tt>IntegerAlgebra</tt>-instance.
     */
    public static DefaultIntegerAlgebra getInstance() {
        return instance;
    }

    /**
     * Short name of this signature.
     */
    public static final String NAME =
        Groove.getXMLProperty("label.integer.prefix");
    /** Long description of this algebra. */
    public static final String DESCRIPTION = "Default integer algebra";
    // initialize after NAME and DESCRIPTION but before the operations
    /**
     * Singleton instance.
     */
    private static final DefaultIntegerAlgebra instance =
        new DefaultIntegerAlgebra();

    /**
     * Integer addition operation symbol.
     */
    public static final String ADD_SYMBOL = "add";
    /**
     * Integer substraction operation symbol.
     */
    public static final String SUB_SYMBOL = "sub";
    /**
     * Integer multiplication operation symbol.
     */
    public static final String MUL_SYMBOL = "mul";
    /**
     * Integer division operation symbol.
     */
    public static final String DIV_SYMBOL = "div";
    /**
     * Integer modulus operation symbol.
     */
    public static final String MOD_SYMBOL = "mod";
    /**
     * Integer minimum symbol.
     */
    public static final String MIN_SYMBOL = "min";
    /**
     * Integer maximum symbol.
     */
    public static final String MAX_SYMBOL = "max";
    /**
     * Integer less than operation symbol.
     */
    public static final String LT_SYMBOL = "lt";
    /**
     * Integer less-or-equal operation symbol.
     */
    public static final String LE_SYMBOL = "le";
    /**
     * Integer greater than operation symbol.
     */
    public static final String GT_SYMBOL = "gt";
    /**
     * Integer greater-or-equal operation symbol.
     */
    public static final String GE_SYMBOL = "ge";
    /**
     * Integer equals operation symbol.
     */
    public static final String EQ_SYMBOL = "eq";
    /**
     * Integer-to-string coercion operation symbol.
     */
    public static final String TO_STRING_SYMBOL = "toString";
    /**
     * The negation/opposite operator, like e.g. -x, or -5
     */
    public static final String NEG_SYMBOL = "neg";
    /**
     * Integer addition operation.
     */
    private static final OldOperation ADD_OPERATION =
        new IntInt2IntOperation(ADD_SYMBOL) {
            @Override
            int apply(int arg1, int arg2) {
                return arg1 + arg2;
            }
        };
    /**
     * Integer subtraction operation.
     */
    private static final OldOperation SUB_OPERATION =
        new IntInt2IntOperation(SUB_SYMBOL) {
            @Override
            int apply(int arg1, int arg2) {
                return arg1 - arg2;
            }
        };
    /**
     * Integer multiplication operation.
     */
    private static final OldOperation MUL_OPERATION =
        new IntInt2IntOperation(MUL_SYMBOL) {
            @Override
            int apply(int arg1, int arg2) {
                return arg1 * arg2;
            }
        };
    /**
     * Integer division operation.
     */
    private static final OldOperation DIV_OPERATION =
        new IntInt2IntOperation(DIV_SYMBOL) {
            @Override
            int apply(int arg1, int arg2) {
                return arg1 / arg2;
            }
        };
    /**
     * Integer modulus operation.
     */
    private static final OldOperation MOD_OPERATION =
        new IntInt2IntOperation(MOD_SYMBOL) {
            @Override
            int apply(int arg1, int arg2) {
                return arg1 % arg2;
            }
        };
    /**
     * Integer less than operation.
     */
    private static final OldOperation LT_OPERATION =
        new IntInt2BoolOperation(LT_SYMBOL) {
            @Override
            boolean apply(int arg1, int arg2) {
                return arg1 < arg2;
            }
        };
    /**
     * Integer less-or-equal operation.
     */
    private static final OldOperation LE_OPERATION =
        new IntInt2BoolOperation(LE_SYMBOL) {
            @Override
            boolean apply(int arg1, int arg2) {
                return arg1 <= arg2;
            }
        };
    /**
     * Integer greater than operation.
     */
    private static final OldOperation GT_OPERATION =
        new IntInt2BoolOperation(GT_SYMBOL) {
            @Override
            boolean apply(int arg1, int arg2) {
                return arg1 > arg2;
            }
        };
    /**
     * Integer greater-or-equal operation.
     */
    private static final OldOperation GE_OPERATION =
        new IntInt2BoolOperation(GE_SYMBOL) {
            @Override
            boolean apply(int arg1, int arg2) {
                return arg1 >= arg2;
            }
        };
    /**
     * Integer equals operation.
     */
    private static final OldOperation EQ_OPERATION =
        new IntInt2BoolOperation(EQ_SYMBOL) {
            @Override
            boolean apply(int arg1, int arg2) {
                return arg1 == arg2;
            }
        };

    /**
     * Integer minimum operation.
     */
    private static final OldOperation MIN_OPERATION =
        new IntInt2IntOperation(MIN_SYMBOL) {
            @Override
            int apply(int arg1, int arg2) {
                return Math.min(arg1, arg2);
            }
        };
    /**
     * Integer subtraction operation.
     */
    private static final OldOperation MAX_OPERATION =
        new IntInt2IntOperation(MAX_SYMBOL) {
            @Override
            int apply(int arg1, int arg2) {
                return Math.max(arg1, arg2);
            }
        };

    /**
     * Integer-to-string coercion operation.
     */
    private static final OldOperation TO_STRING_OPERATION =
        new Int2StringOperation(TO_STRING_SYMBOL) {
            @Override
            String apply(int arg1) {
                return "" + arg1;
            }
        };

    /**
     * Integer opposite/negation operation.
     */
    private static final OldOperation NEG_OPERATION =
        new Int2IntOperation(NEG_SYMBOL) {
            @Override
            int apply(int arg1) {
                return -arg1;
            }
        };

    static {
        instance.addOperation(ADD_OPERATION);
        instance.addOperation(SUB_OPERATION);
        instance.addOperation(MUL_OPERATION);
        instance.addOperation(DIV_OPERATION);
        instance.addOperation(MOD_OPERATION);
        instance.addOperation(MIN_OPERATION);
        instance.addOperation(MAX_OPERATION);
        instance.addOperation(LT_OPERATION);
        instance.addOperation(LE_OPERATION);
        instance.addOperation(GT_OPERATION);
        instance.addOperation(GE_OPERATION);
        instance.addOperation(EQ_OPERATION);
        instance.addOperation(TO_STRING_OPERATION);
        instance.addOperation(NEG_OPERATION);
    }

    /**
     * Integer constant.
     */
    public static class IntegerConstant extends DefaultConstant {
        /**
         * Constructs an integer constant with a given value.
         */
        public IntegerConstant(int value) {
            super(DefaultIntegerAlgebra.getInstance(), "" + value);
            this.value = value;
        }

        /** Returns the value of this constant. */
        public Integer getValue() {
            return this.value;
        }

        private final int value;
    }

    /** Binary integer operation of signature <code>int, int -> int</code>. */
    private static abstract class IntInt2IntOperation extends DefaultOperation {
        /**
         * Constructs an operation in the current algebra, with arity 2 and a
         * given symbol.
         */
        protected IntInt2IntOperation(String symbol) {
            super(getInstance(), symbol, 2);
        }

        /**
         * Performs a binary operation of type <code>int, int -> int</code>.
         * @throws IllegalArgumentException if the number or types of operands
         *         are incorrect.
         */
        public Object apply(List<Object> args) {
            try {
                Integer arg0 = (Integer) args.get(0);
                Integer arg1 = (Integer) args.get(1);
                return apply(arg0, arg1);
            } catch (ClassCastException exc) {
                throw new IllegalArgumentException(exc);
            }
        }

        /** Applies the function encapsulated in this interface. */
        abstract int apply(int arg1, int arg2);
    }

    /** Binary integer operation of signature <code>int, int -> bool</code>. */
    private static abstract class IntInt2BoolOperation extends DefaultOperation {
        /**
         * Constructs an operation in the current algebra, with arity 2 and a
         * given symbol.
         */
        protected IntInt2BoolOperation(String symbol) {
            super(getInstance(), symbol, 2, DefaultBooleanAlgebra.getInstance());
        }

        /**
         * Performs a binary operation of type <code>int, int -> bool</code>.
         * @throws IllegalArgumentException if the number or types of operands
         *         are incorrect.
         */
        public Object apply(List<Object> args) {
            try {
                Integer arg0 = (Integer) args.get(0);
                Integer arg1 = (Integer) args.get(1);
                return apply(arg0, arg1);
            } catch (ClassCastException exc) {
                throw new IllegalArgumentException(exc);
            }
        }

        /** Applies the function encapsulated in this interface. */
        abstract boolean apply(int arg1, int arg2);
    }

    /** Unary integer operation of signature <code>int -> string</code>. */
    private static abstract class Int2StringOperation extends DefaultOperation {
        /**
         * Constructs an operation in the current algebra, with arity 2 and a
         * given symbol.
         */
        protected Int2StringOperation(String symbol) {
            super(getInstance(), symbol, 1, DefaultStringAlgebra.getInstance());
        }

        /**
         * Performs a binary operation of type <code>int, int -> bool</code>.
         * @throws IllegalArgumentException if the number or types of operands
         *         are incorrect.
         */
        public Object apply(List<Object> args) {
            try {
                Integer arg0 = (Integer) args.get(0);
                return apply(arg0);
            } catch (ClassCastException exc) {
                throw new IllegalArgumentException(exc);
            }
        }

        /** Applies the function encapsulated in this interface. */
        abstract String apply(int arg1);
    }

    /** Unary integer operation of signature <code>int -> int</code>. */
    private static abstract class Int2IntOperation extends DefaultOperation {
        /**
         * Constructs an operation in the current algebra, with arity 1 and a
         * given symbol.
         */
        protected Int2IntOperation(String symbol) {
            super(getInstance(), symbol, 1);
        }

        /**
         * Performs a unary operation of type <code>int -> int</code>.
         * @throws IllegalArgumentException if the number or types of operands
         *         are incorrect.
         */
        public Object apply(List<Object> args) {
            try {
                Integer arg0 = (Integer) args.get(0);
                return apply(arg0);
            } catch (ClassCastException exc) {
                throw new IllegalArgumentException(exc);
            }
        }

        /** Applies the function encapsulated in this interface. */
        abstract int apply(int arg1);
    }

}

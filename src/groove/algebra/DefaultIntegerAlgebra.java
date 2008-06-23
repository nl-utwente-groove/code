/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
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
 * @version $Revision: 1.10 $ $Date: 2008-01-26 09:47:32 $
 */
public class DefaultIntegerAlgebra extends Algebra {
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
            throw new IllegalArgumentException(String.format("Value is of class %s rather than Integer", value.getClass()));
        }
        return value.toString();
    }

	/** Returns the {@link Constant} corresponding to a given integer value. */
	static public IntegerConstant getInteger(int value) {
		return new IntegerConstant(value);
	}

//
//	/**
//	 * integer addition operation 
//	 */
//	private Operation operAdd;
//	/**
//	 * integer substraction operation 
//	 */
//	private Operation operSub;
//	/**
//	 * integer multiplication operation 
//	 */
//	private Operation operMul;
//	/**
//	 * integer division operation 
//	 */
//	private Operation operDiv;
//	/**
//	 * integer modulo operation 
//	 */
//	private Operation operMod;
//	/**
//	 * integer less than operation 
//	 */
//	private Operation operLessThan;
//	/**
//	 * integer less equal operation 
//	 */
//	private Operation operLessEqual;
//	/**
//	 * integer greater than operation 
//	 */
//	private Operation operGreaterThan;
//	/**
//	 * integer greater equal operation 
//	 */
//	private Operation operGreaterEqual;
//	/**
//	 * integer equals operation 
//	 */
//	private Operation operEquals;
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
	public static final String NAME = Groove.getXMLProperty("label.integer.prefix");
	/** Long description of this algebra. */
	public static final String DESCRIPTION = "Default integer algebra";
	// initialize after NAME and DESCRIPTION but before the operations
    /**
     * Singleton instance.
     */
    private static final DefaultIntegerAlgebra instance = new DefaultIntegerAlgebra();

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
     * Integer addition operation.
     */
    private static final Operation ADD_OPERATION = new IntInt2IntOperation(ADD_SYMBOL) {
        @Override
        int apply(int arg1, int arg2) {
            return arg1+arg2;
        }
    };
    /**
     * Integer subtraction operation.
     */
    private static final Operation SUB_OPERATION = new IntInt2IntOperation(SUB_SYMBOL) {
        @Override
        int apply(int arg1, int arg2) {
            return arg1-arg2;
        }
    };
    /**
     * Integer multiplication operation.
     */
    private static final Operation MUL_OPERATION = new IntInt2IntOperation(MUL_SYMBOL) {
        @Override
        int apply(int arg1, int arg2) {
            return arg1*arg2;
        }
    };
    /**
     * Integer division operation.
     */
    private static final Operation DIV_OPERATION = new IntInt2IntOperation(DIV_SYMBOL) {
        @Override
        int apply(int arg1, int arg2) {
            return arg1/arg2;
        }
    };
    /**
     * Integer modulus operation.
     */
    private static final Operation MOD_OPERATION = new IntInt2IntOperation(MOD_SYMBOL) {
        @Override
        int apply(int arg1, int arg2) {
            return arg1%arg2;
        }
    };
    /**
     * Integer less than operation.
     */
    private static final Operation LT_OPERATION = new IntInt2BoolOperation(LT_SYMBOL) {
        @Override
        boolean apply(int arg1, int arg2) {
            return arg1<arg2;
        }
    };
    /**
     * Integer less-or-equal operation.
     */
    private static final Operation LE_OPERATION = new IntInt2BoolOperation(LE_SYMBOL) {
        @Override
        boolean apply(int arg1, int arg2) {
            return arg1<=arg2;
        }
    };
    /**
     * Integer greater than operation.
     */
    private static final Operation GT_OPERATION = new IntInt2BoolOperation(GT_SYMBOL) {
        @Override
        boolean apply(int arg1, int arg2) {
            return arg1>arg2;
        }
    };
    /**
     * Integer greater-or-equal operation.
     */
    private static final Operation GE_OPERATION = new IntInt2BoolOperation(GE_SYMBOL) {
        @Override
        boolean apply(int arg1, int arg2) {
            return arg1>=arg2;
        }
    };
    /**
     * Integer equals operation.
     */
    private static final Operation EQ_OPERATION = new IntInt2BoolOperation(EQ_SYMBOL) {
        @Override
        boolean apply(int arg1, int arg2) {
            return arg1==arg2;
        }
    };
    
    /**
     * Integer minimum operation.
     */
    private static final Operation MIN_OPERATION = new IntInt2IntOperation(MIN_SYMBOL) {
        @Override
        int apply(int arg1, int arg2) {
            return Math.min(arg1,arg2);
        }
    };
    /**
     * Integer subtraction operation.
     */
    private static final Operation MAX_OPERATION = new IntInt2IntOperation(MAX_SYMBOL) {
        @Override
        int apply(int arg1, int arg2) {
            return Math.max(arg1,arg2);
        }
    };

    /**
     * Integer-to-string coercion operation.
     */
    private static final Operation TO_STRING_OPERATION = new Int2StringOperation(TO_STRING_SYMBOL) {
        @Override
        String apply(int arg1) {
            return ""+arg1;
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
	}
    
	/**
	 * Integer constant.
	 */
	public static class IntegerConstant extends DefaultConstant {
		/**
		 * Constructs an integer constant with a given value.
		 */
		public IntegerConstant(int value) {
			super(DefaultIntegerAlgebra.getInstance(), ""+value);
			this.value = value;
		}
		
		/** Returns the value of this constant. */
		public Integer getValue() {
			return value;
		}
		
		private final int value;
	}

    /** Binary integer operation of signature <code>int, int -> int</code>. */
    private static abstract class IntInt2IntOperation extends DefaultOperation {
        /** Constructs an operation in the current algebra, with arity 2 and a given symbol. */
        protected IntInt2IntOperation(String symbol) {
            super(getInstance(), symbol, 2);
        }

        /** 
         * Performs a binary operation of type <code>int, int -> int</code>. 
         * @throws IllegalArgumentException if the number or types of operands are incorrect.
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
        /** Constructs an operation in the current algebra, with arity 2 and a given symbol. */
        protected IntInt2BoolOperation(String symbol) {
            super(getInstance(), symbol, 2, DefaultBooleanAlgebra.getInstance());
        }

        /** 
         * Performs a binary operation of type <code>int, int -> bool</code>. 
         * @throws IllegalArgumentException if the number or types of operands are incorrect.
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
        /** Constructs an operation in the current algebra, with arity 2 and a given symbol. */
        protected Int2StringOperation(String symbol) {
            super(getInstance(), symbol, 1, DefaultStringAlgebra.getInstance());
        }

        /** 
         * Performs a binary operation of type <code>int, int -> bool</code>. 
         * @throws IllegalArgumentException if the number or types of operands are incorrect.
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
//    
//	/**
//	 * Integer addition operation.
//	 * @author Harmen Kastenberg
//	 */
//	private static class AddOperation extends IntInt2IntOperation {
//		/**
//		 * Constructor.
//		 */
//		private AddOperation() {
//			super(ADD);
//		}
//
//		@Override
//		public int apply(int arg1, int arg2) {
//			return arg1+arg2;
//		}
//
//		/**
//		 * Returns the singleton instance of this operation class.
//		 */
//		public static Operation getInstance() {
//			return INSTANCE;
//		}
//		
//		/** The singleton instance of this operation class. */
//		static private final AddOperation INSTANCE = new AddOperation();
//	}
//
//	/**
//	 * Integer subtraction operation.
//	 * @author Harmen Kastenberg
//	 */
//	private static class SubOperation extends DefaultOperation {
//		/**
//		 * Constructor.
//		 */
//		private SubOperation() {
//			super(DefaultIntegerAlgebra.getInstance(), SUB, 2);
//		}
//
//		@Override
//		public Constant apply(List<Constant> operands) throws IllegalArgumentException {
//			try {
//				Constant oper1 = operands.get(0);
//				Constant oper2 = operands.get(1);
//				// return the value obtained by subtracting the
//				// second operand from the first operand
//				int value1 = Integer.parseInt(oper1.symbol());
//				int value2 = Integer.parseInt(oper2.symbol());
//				int sub = value1 - value2;
//				return getInteger(sub);
//			} catch (NumberFormatException exc) {
//				throw new IllegalArgumentException(exc);
//			}
//		}
//
//		/**
//		 * @return the singleton instance of this operation
//		 */
//		public static Operation getInstance() {
//			return instance;
//		}
//
//		/** The singleton instance of this operation. */
//		private static final SubOperation instance = new SubOperation();
//	}
//
//	/**
//	 * Integer multiply operation.
//	 * @author Harmen Kastenberg
//	 */
//	private static class MulOperation extends DefaultOperation {
//		/**
//		 * Constructor.
//		 */
//		private MulOperation() {
//			super(DefaultBooleanAlgebra.getInstance(), MUL, 2);
//		}
//
//		@Override
//		public Constant apply(List<Constant> operands)
//				throws IllegalArgumentException {
//			try {
//				Constant oper1 = operands.get(0);
//				Constant oper2 = operands.get(1);
//
//				// return the value obtained by multiplying
//				// both operands
//				int value1 = Integer.parseInt(oper1.symbol());
//				int value2 = Integer.parseInt(oper2.symbol());
//				int mul = value1 * value2;
//				return getInteger(mul);
//			} catch (NumberFormatException exc) {
//				throw new IllegalArgumentException(exc);
//			}
//		}
//
//		/**
//		 * Returns the singleton instance of this operation.
//		 */
//		public static Operation getInstance() {
//			return instance;
//		}
//
//		/** The singleton instance of this operation. */
//		private static final MulOperation instance = new MulOperation();
//	}
//
//	/**
//	 * Integer division operation.
//	 * @author Harmen Kastenberg
//	 */
//	private static class DivOperation extends DefaultOperation {
//		/**
//		 * Constructor.
//		 */
//		private DivOperation() {
//			super(DefaultBooleanAlgebra.getInstance(), DIV, 2);
//		}
//
//		@Override
//		public Constant apply(List<Constant> operands)
//				throws IllegalArgumentException {
//			try {
//				Constant oper1 = operands.get(0);
//				Constant oper2 = operands.get(1);
//
//				// return the value obtained by dividing the first
//				// operand by the second operand
//				int value1 = Integer.parseInt(oper1.symbol());
//				int value2 = Integer.parseInt(oper2.symbol());
//				int div = value1 / value2;
//				return getInteger(div);
//			} catch (NumberFormatException exc) {
//				throw new IllegalArgumentException(exc);
//			} catch (ArithmeticException exc) {
//				throw new IllegalArgumentException(exc);
//			}
//		}
//
//		/**
//		 * Returns the singleton instance of this operation.
//		 */
//		public static Operation getInstance() {
//			return instance;
//		}
//
//		/** The singleton instance of this operation. */
//		private static final DivOperation instance = new DivOperation();
//	}
//
//	/**
//	 * Integer modulo operation.
//	 * @author Harmen Kastenberg
//	 */
//	private static class ModOperation extends DefaultOperation {
//		/**
//		 * Constructor.
//		 */
//		private ModOperation() {
//			super(DefaultBooleanAlgebra.getInstance(), MOD, 2);
//		}
//
//		@Override
//		public Constant apply(List<Constant> operands) throws IllegalArgumentException {
//			try {
//				Constant oper1 = operands.get(0);
//				Constant oper2 = operands.get(1);
//
//				// return the value obtained by taking the modulo
//				// of the first operand with the second operand
//				int value1 = Integer.parseInt(oper1.symbol());
//				int value2 = Integer.parseInt(oper2.symbol());
//				int mod = value1 % value2;
//				return getInteger(mod);
//			} catch (NumberFormatException exc) {
//				throw new IllegalArgumentException(exc);
//			} catch (ArithmeticException exc) {
//				throw new IllegalArgumentException(exc);
//			}
//		}
//
//		/**
//		 * Returns the singleton instance of this operation.
//		 */
//		public static Operation getInstance() {
//			return instance;
//		}
//
//		/** The singleton instance of this operation. */
//		private static final ModOperation instance = new ModOperation();
//	}
//
//	/**
//	 * Integer less than operation.
//	 * @author Harmen Kastenberg
//	 */
//	private static class LessThanOperation extends DefaultOperation {
//		/**
//		 * Constructor.
//		 */
//	    private LessThanOperation() {
//	        super(DefaultBooleanAlgebra.getInstance(), LT, 2);
//	    }
//
//		@Override
//		public Constant apply(List<Constant> operands) throws IllegalArgumentException {
//			try {
//				Constant oper1 = operands.get(0);
//				Constant oper2 = operands.get(1);
//
//				// return true if the first operand is less than
//				// the second operand and false otherwise
//				int value1 = Integer.parseInt(oper1.symbol());
//				int value2 = Integer.parseInt(oper2.symbol());
//				boolean lessThan = value1 < value2;
//				return DefaultBooleanAlgebra.getBoolean(lessThan);
//			} catch (NumberFormatException exc) {
//				throw new IllegalArgumentException(exc);
//			}
//		}
//		
//		/**
//		 * @return the singleton instance of this operation
//		 */
//	    public static Operation getInstance() {
//			return instance;
//		}
//
//		/** The singleton instance of this operation. */
//		private static final LessThanOperation instance = new LessThanOperation();
//
//	}
//
//	/**
//	 * Integer less than operation.
//	 * @author Harmen Kastenberg
//	 */
//	private static class LessEqualOperation extends DefaultOperation {
//		/**
//		 * Constructor.
//		 */
//	    private LessEqualOperation() {
//	        super(DefaultBooleanAlgebra.getInstance(), LE, 2);
//	    }
//
//		@Override
//		public Constant apply(List<Constant> operands) throws IllegalArgumentException {
//			try {
//				Constant oper1 = operands.get(0);
//				Constant oper2 = operands.get(1);
//
//				// return true if the first operand is less than
//				// the second operand and false otherwise
//				int value1 = Integer.parseInt(oper1.symbol());
//				int value2 = Integer.parseInt(oper2.symbol());
//				boolean lessEqual = value1 <= value2;
//				return DefaultBooleanAlgebra.getBoolean(lessEqual);
//			} catch (NumberFormatException exc) {
//				throw new IllegalArgumentException(exc);
//			}
//		}
//
//		/**
//		 * Returns the singleton instance of this operation.
//		 */
//	    public static Operation getInstance() {
//			return instance;
//		}
//
//		/** The singleton instance of this operation. */
//		private static final LessEqualOperation instance = new LessEqualOperation();
//	}
//
//	/**
//	 * Integer greater than operation.
//	 * @author Harmen Kastenberg
//	 */
//	private static class GreaterThanOperation extends DefaultOperation {
//		/**
//		 * Constructor.
//		 */
//	    private GreaterThanOperation() {
//	        super(DefaultBooleanAlgebra.getInstance(), GT, 2);
//	    }
//
//		@Override
//		public Constant apply(List<Constant> operands)
//				throws IllegalArgumentException {
//			try {
//				Constant oper1 = operands.get(0);
//				Constant oper2 = operands.get(1);
//
//				// return true if the first operand is greater than
//				// the second operand and false otherwise
//				int value1 = Integer.parseInt(oper1.symbol());
//				int value2 = Integer.parseInt(oper2.symbol());
//				boolean greaterThan = value1 > value2;
//				return DefaultBooleanAlgebra.getBoolean(greaterThan);
//			} catch (NumberFormatException exc) {
//				throw new IllegalArgumentException(exc);
//			}
//		}
//
//		/**
//		 * Returns the singleton instance of this operation.
//		 */
//	    public static Operation getInstance() {
//			return instance;
//		}
//
//		/** The singleton instance of this operation. */
//		private static final GreaterThanOperation instance = new GreaterThanOperation();
//	}
//
//	/**
//	 * Integer greater than operation.
//	 * @author Harmen Kastenberg
//	 */
//	private static class GreaterEqualOperation extends DefaultOperation {
//		/**
//		 * Constructor.
//		 */
//	    private GreaterEqualOperation() {
//	        super(DefaultBooleanAlgebra.getInstance(), GE, 2);
//	    }
//
//		@Override
//		public Constant apply(List<Constant> operands) throws IllegalArgumentException {
//			try {
//				Constant oper1 = operands.get(0);
//				Constant oper2 = operands.get(1);
//
//				// return true if the first operand is greater than
//				// the second operand and false otherwise
//				int value1 = Integer.parseInt(oper1.symbol());
//				int value2 = Integer.parseInt(oper2.symbol());
//				boolean greaterEqual = value1 >= value2;
//				return DefaultBooleanAlgebra.getBoolean(greaterEqual);
//			} catch (NumberFormatException exc) {
//				throw new IllegalArgumentException(exc);
//			}
//		}
//
//		/**
//		 * @return the singleton instance of this operation
//		 */
//	    public static Operation getInstance() {
//			return instance;
//		}
//
//		/** The singleton instance of this operation. */
//		private static final GreaterEqualOperation instance = new GreaterEqualOperation();
//	}
//
//	/**
//	 * Integer equals operation.
//	 * @author Harmen Kastenberg
//	 */
//	private static class EqualsOperation extends DefaultOperation {
//		/**
//		 * Constructor.
//		 */
//	    private EqualsOperation() {
//	        super(DefaultBooleanAlgebra.getInstance(), EQ, 2);
//	    }
//
//		@Override
//		public Constant apply(List<Constant> operands) throws IllegalArgumentException {
//			try {
//				Constant oper1 = operands.get(0);
//				Constant oper2 = operands.get(1);
//
//				// return true if both operands are equal
//				// and false otherwise
//				int value1 = Integer.parseInt(oper1.symbol());
//				int value2 = Integer.parseInt(oper2.symbol());
//				boolean equals = (value1 == value2);
//				return DefaultBooleanAlgebra.getBoolean(equals);
//			} catch (NumberFormatException exc) {
//				throw new IllegalArgumentException(exc);
//			}
//		}
//
//		/**
//		 * Returns the singleton instance of this operation.
//		 */
//	    public static Operation getInstance() {
//			return instance;
//		}
//
//		/** The singleton instance of this operation. */
//		private static final EqualsOperation instance = new EqualsOperation();
//	}
}

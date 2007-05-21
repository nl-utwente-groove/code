/*
 * GROOVE: GRaphs for Object Oriented VErification
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
 * $Id: DefaultIntegerAlgebra.java,v 1.4 2007-05-21 22:19:28 rensink Exp $
 */

package groove.algebra;

import groove.util.Groove;

import java.util.List;

/**
 * Default integer algebra, in which natural numbers serve as constants.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.4 $ $Date: 2007-05-21 22:19:28 $
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
	/**
	 * integer addition 
	 */
	public static final String ADD = "add";
	/**
	 * integer substraction 
	 */
	public static final String SUB = "sub";
	/**
	 * integer multiplication 
	 */
	public static final String MUL = "mul";
	/**
	 * integer division 
	 */
	public static final String DIV = "div";
	/**
	 * integer modulus 
	 */
	public static final String MOD = "mod";
	/**
	 * integer less than 
	 */
	public static final String LT = "lt";
	/**
	 * integer less equal 
	 */
	public static final String LE = "le";
	/**
	 * integer greater than 
	 */
	public static final String GT = "gt";
	/**
	 * integer greater equal 
	 */
	public static final String GE = "ge";
	/**
	 * integer equals 
	 */
	public static final String EQ = "eq";

	/**
	 * Singleton instance 
	 */
	private static final DefaultIntegerAlgebra instance;
	
	static {
		instance = new DefaultIntegerAlgebra();
//		operAdd = AddOperation.getInstance();
//		operSub = SubOperation.getInstance();
//		operMul = MulOperation.getInstance();
//		operDiv = DivOperation.getInstance();
//		operMod = ModOperation.getInstance();
//		operLessThan = LessThanOperation.getInstance();
//		operLessEqual = LessEqualOperation.getInstance();
//		operGreaterThan = GreaterThanOperation.getInstance();
//		operGreaterEqual = GreaterEqualOperation.getInstance();
//		operEquals = EqualsOperation.getInstance();
		instance.addOperation(AddOperation.getInstance());
		instance.addOperation(SubOperation.getInstance());
		instance.addOperation(MulOperation.getInstance());
		instance.addOperation(DivOperation.getInstance());
		instance.addOperation(ModOperation.getInstance());
		instance.addOperation(LessThanOperation.getInstance());
		instance.addOperation(LessEqualOperation.getInstance());
		instance.addOperation(GreaterThanOperation.getInstance());
		instance.addOperation(GreaterEqualOperation.getInstance());
		instance.addOperation(EqualsOperation.getInstance());
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
		public int getValue() {
			return value;
		}
		
		private final int value;
	}

	/**
	 * Integer addition operation.
	 * @author Harmen Kastenberg
	 */
	private static class AddOperation extends DefaultOperation {
		/**
		 * Constructor.
		 */
		private AddOperation() {
			super(DefaultIntegerAlgebra.getInstance(), ADD, 2);
		}

		@Override
		public Constant apply(List<Constant> operands)
				throws IllegalArgumentException {
			try {
				Constant oper1 = operands.get(0);
				Constant oper2 = operands.get(1);

				// return the sum of both operands
				int value1 = Integer.parseInt(oper1.symbol());
				int value2 = Integer.parseInt(oper2.symbol());
				int sum = value1 + value2;
				return getInteger(sum);
			} catch (NumberFormatException exc) {
				throw new IllegalArgumentException(exc);
			}
		}

		/**
		 * Returns the singleton instance of this operation class.
		 */
		public static Operation getInstance() {
			return INSTANCE;
		}
		
		/** The singleton instance of this operation class. */
		static private final AddOperation INSTANCE = new AddOperation();
	}

	/**
	 * Integer subtraction operation.
	 * @author Harmen Kastenberg
	 */
	private static class SubOperation extends DefaultOperation {
		/**
		 * Constructor.
		 */
		private SubOperation() {
			super(DefaultBooleanAlgebra.getInstance(), SUB, 2);
		}

		@Override
		public Constant apply(List<Constant> operands) throws IllegalArgumentException {
			try {
				Constant oper1 = operands.get(0);
				Constant oper2 = operands.get(1);
				// return the value obtained by subtracting the
				// second operand from the first operand
				int value1 = Integer.parseInt(oper1.symbol());
				int value2 = Integer.parseInt(oper2.symbol());
				int sub = value1 - value2;
				return getInteger(sub);
			} catch (NumberFormatException exc) {
				throw new IllegalArgumentException(exc);
			}
		}

		/**
		 * @return the singleton instance of this operation
		 */
		public static Operation getInstance() {
			return instance;
		}

		/** The singleton instance of this operation. */
		private static final SubOperation instance = new SubOperation();
	}

	/**
	 * Integer multiply operation.
	 * @author Harmen Kastenberg
	 */
	private static class MulOperation extends DefaultOperation {
		/**
		 * Constructor.
		 */
		private MulOperation() {
			super(DefaultBooleanAlgebra.getInstance(), MUL, 2);
		}

		@Override
		public Constant apply(List<Constant> operands)
				throws IllegalArgumentException {
			try {
				Constant oper1 = operands.get(0);
				Constant oper2 = operands.get(1);

				// return the value obtained by multiplying
				// both operands
				int value1 = Integer.parseInt(oper1.symbol());
				int value2 = Integer.parseInt(oper2.symbol());
				int mul = value1 * value2;
				return getInteger(mul);
			} catch (NumberFormatException exc) {
				throw new IllegalArgumentException(exc);
			}
		}

		/**
		 * Returns the singleton instance of this operation.
		 */
		public static Operation getInstance() {
			return instance;
		}

		/** The singleton instance of this operation. */
		private static final MulOperation instance = new MulOperation();
	}

	/**
	 * Integer division operation.
	 * @author Harmen Kastenberg
	 */
	private static class DivOperation extends DefaultOperation {
		/**
		 * Constructor.
		 */
		private DivOperation() {
			super(DefaultBooleanAlgebra.getInstance(), DIV, 2);
		}

		@Override
		public Constant apply(List<Constant> operands)
				throws IllegalArgumentException {
			try {
				Constant oper1 = operands.get(0);
				Constant oper2 = operands.get(1);

				// return the value obtained by dividing the first
				// operand by the second operand
				int value1 = Integer.parseInt(oper1.symbol());
				int value2 = Integer.parseInt(oper2.symbol());
				int div = value1 / value2;
				return getInteger(div);
			} catch (NumberFormatException exc) {
				throw new IllegalArgumentException(exc);
			} catch (ArithmeticException exc) {
				throw new IllegalArgumentException(exc);
			}
		}

		/**
		 * Returns the singleton instance of this operation.
		 */
		public static Operation getInstance() {
			return instance;
		}

		/** The singleton instance of this operation. */
		private static final DivOperation instance = new DivOperation();
	}

	/**
	 * Integer modulo operation.
	 * @author Harmen Kastenberg
	 */
	private static class ModOperation extends DefaultOperation {
		/**
		 * Constructor.
		 */
		private ModOperation() {
			super(DefaultBooleanAlgebra.getInstance(), MOD, 2);
		}

		@Override
		public Constant apply(List<Constant> operands) throws IllegalArgumentException {
			try {
				Constant oper1 = operands.get(0);
				Constant oper2 = operands.get(1);

				// return the value obtained by taking the modulo
				// of the first operand with the second operand
				int value1 = Integer.parseInt(oper1.symbol());
				int value2 = Integer.parseInt(oper2.symbol());
				int mod = value1 % value2;
				return getInteger(mod);
			} catch (NumberFormatException exc) {
				throw new IllegalArgumentException(exc);
			} catch (ArithmeticException exc) {
				throw new IllegalArgumentException(exc);
			}
		}

		/**
		 * Returns the singleton instance of this operation.
		 */
		public static Operation getInstance() {
			return instance;
		}

		/** The singleton instance of this operation. */
		private static final ModOperation instance = new ModOperation();
	}

	/**
	 * Integer less than operation.
	 * @author Harmen Kastenberg
	 */
	private static class LessThanOperation extends DefaultOperation {
		/**
		 * Constructor.
		 */
	    private LessThanOperation() {
	        super(DefaultBooleanAlgebra.getInstance(), LT, 2);
	    }

		@Override
		public Constant apply(List<Constant> operands) throws IllegalArgumentException {
			try {
				Constant oper1 = operands.get(0);
				Constant oper2 = operands.get(1);

				// return true if the first operand is less than
				// the second operand and false otherwise
				int value1 = Integer.parseInt(oper1.symbol());
				int value2 = Integer.parseInt(oper2.symbol());
				boolean lessThan = value1 < value2;
				return DefaultBooleanAlgebra.getBoolean(lessThan);
			} catch (NumberFormatException exc) {
				throw new IllegalArgumentException(exc);
			}
		}
		
		/**
		 * @return the singleton instance of this operation
		 */
	    public static Operation getInstance() {
			return instance;
		}

		/** The singleton instance of this operation. */
		private static final LessThanOperation instance = new LessThanOperation();

	}

	/**
	 * Integer less than operation.
	 * @author Harmen Kastenberg
	 */
	private static class LessEqualOperation extends DefaultOperation {
		/**
		 * Constructor.
		 */
	    private LessEqualOperation() {
	        super(DefaultBooleanAlgebra.getInstance(), LE, 2);
	    }

		@Override
		public Constant apply(List<Constant> operands) throws IllegalArgumentException {
			try {
				Constant oper1 = operands.get(0);
				Constant oper2 = operands.get(1);

				// return true if the first operand is less than
				// the second operand and false otherwise
				int value1 = Integer.parseInt(oper1.symbol());
				int value2 = Integer.parseInt(oper2.symbol());
				boolean lessEqual = value1 <= value2;
				return DefaultBooleanAlgebra.getBoolean(lessEqual);
			} catch (NumberFormatException exc) {
				throw new IllegalArgumentException(exc);
			}
		}

		/**
		 * Returns the singleton instance of this operation.
		 */
	    public static Operation getInstance() {
			return instance;
		}

		/** The singleton instance of this operation. */
		private static final LessEqualOperation instance = new LessEqualOperation();
	}

	/**
	 * Integer greater than operation.
	 * @author Harmen Kastenberg
	 */
	private static class GreaterThanOperation extends DefaultOperation {
		/**
		 * Constructor.
		 */
	    private GreaterThanOperation() {
	        super(DefaultBooleanAlgebra.getInstance(), GT, 2);
	    }

		@Override
		public Constant apply(List<Constant> operands)
				throws IllegalArgumentException {
			try {
				Constant oper1 = operands.get(0);
				Constant oper2 = operands.get(1);

				// return true if the first operand is greater than
				// the second operand and false otherwise
				int value1 = Integer.parseInt(oper1.symbol());
				int value2 = Integer.parseInt(oper2.symbol());
				boolean greaterThan = value1 > value2;
				return DefaultBooleanAlgebra.getBoolean(greaterThan);
			} catch (NumberFormatException exc) {
				throw new IllegalArgumentException(exc);
			}
		}

		/**
		 * Returns the singleton instance of this operation.
		 */
	    public static Operation getInstance() {
			return instance;
		}

		/** The singleton instance of this operation. */
		private static final GreaterThanOperation instance = new GreaterThanOperation();
	}

	/**
	 * Integer greater than operation.
	 * @author Harmen Kastenberg
	 */
	private static class GreaterEqualOperation extends DefaultOperation {
		/**
		 * Constructor.
		 */
	    private GreaterEqualOperation() {
	        super(DefaultBooleanAlgebra.getInstance(), GE, 2);
	    }

		@Override
		public Constant apply(List<Constant> operands) throws IllegalArgumentException {
			try {
				Constant oper1 = operands.get(0);
				Constant oper2 = operands.get(1);

				// return true if the first operand is greater than
				// the second operand and false otherwise
				int value1 = Integer.parseInt(oper1.symbol());
				int value2 = Integer.parseInt(oper2.symbol());
				boolean greaterEqual = value1 >= value2;
				return DefaultBooleanAlgebra.getBoolean(greaterEqual);
			} catch (NumberFormatException exc) {
				throw new IllegalArgumentException(exc);
			}
		}

		/**
		 * @return the singleton instance of this operation
		 */
	    public static Operation getInstance() {
			return instance;
		}

		/** The singleton instance of this operation. */
		private static final GreaterEqualOperation instance = new GreaterEqualOperation();
	}

	/**
	 * Integer equals operation.
	 * @author Harmen Kastenberg
	 */
	private static class EqualsOperation extends DefaultOperation {
		/**
		 * Constructor.
		 */
	    private EqualsOperation() {
	        super(DefaultBooleanAlgebra.getInstance(), EQ, 2);
	    }

		@Override
		public Constant apply(List<Constant> operands) throws IllegalArgumentException {
			try {
				Constant oper1 = operands.get(0);
				Constant oper2 = operands.get(1);

				// return true if both operands are equal
				// and false otherwise
				int value1 = Integer.parseInt(oper1.symbol());
				int value2 = Integer.parseInt(oper2.symbol());
				boolean equals = (value1 == value2);
				return DefaultBooleanAlgebra.getBoolean(equals);
			} catch (NumberFormatException exc) {
				throw new IllegalArgumentException(exc);
			}
		}

		/**
		 * Returns the singleton instance of this operation.
		 */
	    public static Operation getInstance() {
			return instance;
		}

		/** The singleton instance of this operation. */
		private static final EqualsOperation instance = new EqualsOperation();
	}
}

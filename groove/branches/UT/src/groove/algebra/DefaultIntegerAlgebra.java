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
 * $Id: DefaultIntegerAlgebra.java,v 1.1.1.2 2007-03-20 10:42:39 kastenberg Exp $
 */

package groove.algebra;

import groove.graph.algebra.AlgebraConstants;
import groove.graph.algebra.AlgebraGraph;
import groove.util.Groove;

import java.util.List;

/**
 * Default integer algebra, in which natural numbers serve as constants.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.1.1.2 $ $Date: 2007-03-20 10:42:39 $
 */
public class DefaultIntegerAlgebra extends Algebra {

    /** Singleton instance */
    private static DefaultIntegerAlgebra instance = null;
    /** separator between prefix and rest */
    public final String SEPARATOR = Groove.getXMLProperty("label.aspect.separator");
    /** algebra prefix */
    public final String PREFIX = Groove.getXMLProperty("label.integer.prefix");
    /** integer addition */
	public static final String ADD = "add";
	/** integer substraction */
	public static final String SUB = "sub";
	/** integer multiplication */
	public static final String MUL = "mul";
	/** integer division */
	public static final String DIV = "div";
	/** integer modulus */
	public static final String MOD = "mod";
	/** integer less than */
	public static final String LT = "lt";
	/** integer less equal */
	public static final String LE = "le";
	/** integer greater than */
	public static final String GT = "gt";
	/** integer greater equal */
	public static final String GE = "ge";
	/** integer equals */
	public static final String EQ = "eq";

	/** integer addition operation */
	private Operation operAdd;
	/** integer substraction operation */
	private Operation operSub;
	/** integer multiplication operation */
	private Operation operMul;
	/** integer division operation */
	private Operation operDiv;
	/** integer modulo operation */
	private Operation operMod;
	/** integer less than operation */
	private Operation operLessThan;
	/** integer less equal operation */
	private Operation operLessEqual;
	/** integer greater than operation */
	private Operation operGreaterThan;
	/** integer greater equal operation */
	private Operation operGreaterEqual;
	/** integer equals operation */
	private Operation operEquals;

	/**
     * Method facilitating the singleton-pattern.
	 * @return the single <tt>IntegerAlgebra</tt>-instance.
	 */
	public static DefaultIntegerAlgebra getInstance() {
	    if (instance == null)
	        instance = new DefaultIntegerAlgebra();
	    return instance;
	}

	/* (non-Javadoc)
	 * @see groove.algebra.Algebra#prefix()
	 */
	public String prefix() {
		return PREFIX + SEPARATOR;
	}

	/**
	 * Constructor creating the singleton-instance.
	 * @param name the name of this boolean algebra
	 */
	private DefaultIntegerAlgebra(String name) {
		super(name);
	}

	/**
	 * Constructor.
	 */
	private DefaultIntegerAlgebra() {
		this("Integer Algebra");

		operAdd = AddOperation.getInstance();
		operSub = SubOperation.getInstance();
		operMul = MulOperation.getInstance();
		operDiv = DivOperation.getInstance();
		operMod = ModOperation.getInstance();
		operLessThan = LessThanOperation.getInstance();
		operLessEqual = LessEqualOperation.getInstance();
		operGreaterThan = GreaterThanOperation.getInstance();
		operGreaterEqual = GreaterEqualOperation.getInstance();
		operEquals = EqualsOperation.getInstance();
		operAdd.set(this, null, -1);
		operSub.set(this, null, -1);
		operMul.set(this, null, -1);
		operDiv.set(this, null, -1);
		operMod.set(this, null, -1);
		operLessThan.set(this, null, -1);
		operLessEqual.set(this, null, -1);
		operGreaterThan.set(this, null, -1);
		operGreaterEqual.set(this, null, -1);
		operEquals.set(this, null, -1);
		this.addOperation(operAdd);
		this.addOperation(operSub);
		this.addOperation(operMul);
		this.addOperation(operDiv);
		this.addOperation(operMod);
		this.addOperation(operLessThan);
		this.addOperation(operLessEqual);
		this.addOperation(operGreaterThan);
		this.addOperation(operGreaterEqual);
		this.addOperation(operEquals);
	}

	/* (non-Javadoc)
	 * @see groove.util.Algebra#getOperation(java.lang.String)
	 */
	public Operation getOperation(String symbol) throws UnknownSymbolException {
		Operation operation;
		if (getOperationSymbols().contains(symbol))
		    operation = super.getOperation(symbol);
		else {
			try {
				int value = Integer.parseInt(symbol);
				operation = new IntegerConstant(this, "" + value);
				addOperation(operation);
			}
			catch (NumberFormatException nfe) {
		        throw new UnknownSymbolException(getName() + " does not contain the operation represented by " + symbol);
			}
		}
		return operation;
	}

	/**
	 * Integer constant.
	 * @author Harmen Kastenberg
	 */
	private static class IntegerConstant extends DefaultConstant {

		/**
		 * Constructor.
		 * @param algebra the algebra this operation is in
		 * @param symbol the symbol representing this operation
		 */
		public IntegerConstant(Algebra algebra, String symbol) {
			set(algebra, symbol, -1);
		}
	}

	/**
	 * Integer addition operation.
	 * @author Harmen Kastenberg
	 */
	private static class AddOperation extends DefaultOperation {

		/** The singleton instance of this operation. */
		private static AddOperation instance = null;

		/**
		 * Constructor.
		 */
		private AddOperation() {
			super(ADD, 2);
		}

		/**
		 * @return the singleton instance of this operation
		 */
		public static Operation getInstance() {
			if (instance == null)
				instance = new AddOperation();
			return instance;
		}

		public Constant apply(List<Constant> operands) {
			Constant result = null;
			Constant oper1 = operands.get(0);
			Constant oper2 = operands.get(1);

			// return the sum of both operands
			int value1 = Integer.parseInt(oper1.symbol());
			int value2 = Integer.parseInt(oper2.symbol());
			int sum = value1 + value2;

			try {
				result = (Constant) algebra().getOperation("" + sum); 
			}
			catch (UnknownSymbolException use) {
				System.err.println(use.toString());
			}
			return result;
		}
	}

	/**
	 * Integer subtraction operation.
	 * @author Harmen Kastenberg
	 */
	private static class SubOperation extends DefaultOperation {

		/** The singleton instance of this operation. */
		private static SubOperation instance = null;

		/**
		 * Constructor.
		 */
		private SubOperation() {
			super(SUB, 2);
		}

		/**
		 * @return the singleton instance of this operation
		 */
		public static Operation getInstance() {
			if (instance == null)
				instance = new SubOperation();
			return instance;
		}

		public Constant apply(List<Constant> operands) {
			Constant result = null;
			Constant oper1 = operands.get(0);
			Constant oper2 = operands.get(1);

			// return the value obtained by subtracting the
			// second operand from the first operand
			int value1 = Integer.parseInt(oper1.symbol());
			int value2 = Integer.parseInt(oper2.symbol());
			int sub = value1 - value2;

			try {
				result = (Constant) algebra().getOperation("" + sub); 
			}
			catch (UnknownSymbolException use) {
				System.err.println(use.toString());
			}
			return result;
		}
	}

	/**
	 * Integer multiply operation.
	 * @author Harmen Kastenberg
	 */
	private static class MulOperation extends DefaultOperation {

		/** The singleton instance of this operation. */
		private static MulOperation instance = null;

		/**
		 * Constructor.
		 */
		private MulOperation() {
			super(MUL, 2);
		}

		/**
		 * @return the singleton instance of this operation
		 */
		public static Operation getInstance() {
			if (instance == null)
				instance = new MulOperation();
			return instance;
		}

		public Constant apply(List<Constant> operands) {
			Constant result = null;
			Constant oper1 = operands.get(0);
			Constant oper2 = operands.get(1);

			// return the value obtained by multiplying
			// both operands
			int value1 = Integer.parseInt(oper1.symbol());
			int value2 = Integer.parseInt(oper2.symbol());
			int mul = value1 * value2;

			try {
				result = (Constant) algebra().getOperation("" + mul); 
			}
			catch (UnknownSymbolException use) {
				System.err.println(use.toString());
			}
			return result;
		}
	}

	/**
	 * Integer division operation.
	 * @author Harmen Kastenberg
	 */
	private static class DivOperation extends DefaultOperation {

		/** The singleton instance of this operation. */
		private static DivOperation instance = null;

		/**
		 * Constructor.
		 */
		private DivOperation() {
			super(DIV, 2);
		}

		/**
		 * @return the singleton instance of this operation
		 */
		public static Operation getInstance() {
			if (instance == null)
				instance = new DivOperation();
			return instance;
		}

		public Constant apply(List<Constant> operands) {
			Constant result = null;
			Constant oper1 = operands.get(0);
			Constant oper2 = operands.get(1);

			// return the value obtained by dividing the first
			// operand by the second operand
			int value1 = Integer.parseInt(oper1.symbol());
			int value2 = Integer.parseInt(oper2.symbol());
			int div = value1 / value2;

			try {
				result = (Constant) algebra().getOperation("" + div); 
			}
			catch (UnknownSymbolException use) {
				System.err.println(use.toString());
			}
			return result;
		}
	}

	/**
	 * Integer modulo operation.
	 * @author Harmen Kastenberg
	 */
	private static class ModOperation extends DefaultOperation {

		/** The singleton instance of this operation. */
		private static ModOperation instance = null;

		/**
		 * Constructor.
		 */
		private ModOperation() {
			super(MOD, 2);
		}

		/**
		 * @return the singleton instance of this operation
		 */
		public static Operation getInstance() {
			if (instance == null)
				instance = new ModOperation();
			return instance;
		}

		public Constant apply(List<Constant> operands) {
			Constant result = null;
			Constant oper1 = operands.get(0);
			Constant oper2 = operands.get(1);

			// return the value obtained by taking the modulo
			// of the first operand with the second operand
			int value1 = Integer.parseInt(oper1.symbol());
			int value2 = Integer.parseInt(oper2.symbol());
			int mod = value1 % value2;

			try {
				result = (Constant) algebra().getOperation("" + mod); 
			}
			catch (UnknownSymbolException use) {
				System.err.println(use.toString());
			}
			return result;
		}
	}

	/**
	 * Integer less than operation.
	 * @author Harmen Kastenberg
	 */
	private static class LessThanOperation extends DefaultOperation {

		/** The singleton instance of this operation. */
		private static LessThanOperation instance = null;

		/**
		 * Constructor.
		 */
	    private LessThanOperation() {
	        super(LT, 2);
	    }

		/**
		 * @return the singleton instance of this operation
		 */
	    public static Operation getInstance() {
			if (instance == null)
				instance = new LessThanOperation();
			return instance;
		}

		public Constant apply(List<Constant> operands) {
			Constant result = null;
			Constant oper1 = operands.get(0);
			Constant oper2 = operands.get(1);

			// return true if the first operand is less than
			// the second operand and false otherwise
			int value1 = Integer.parseInt(oper1.symbol());
			int value2 = Integer.parseInt(oper2.symbol());
			boolean lessThan = value1 < value2;

			try {
			    AlgebraGraph algebraGraph = AlgebraGraph.getInstance();
			    Algebra booleanAlgebra = algebraGraph.getAlgebra(AlgebraConstants.BOOLEAN);
			    if (lessThan)
			        result = (Constant) booleanAlgebra.getOperation(DefaultBooleanAlgebra.TRUE);
			    else
			        result = (Constant) booleanAlgebra.getOperation(DefaultBooleanAlgebra.FALSE);
			}
			catch (UnknownSymbolException use) {
				System.err.println(use.toString());
			}
			return result;
		}
	}

	/**
	 * Integer less than operation.
	 * @author Harmen Kastenberg
	 */
	private static class LessEqualOperation extends DefaultOperation {

		/** The singleton instance of this operation. */
		private static LessEqualOperation instance = null;

		/**
		 * Constructor.
		 */
	    private LessEqualOperation() {
	        super(LE, 2);
	    }

		/**
		 * @return the singleton instance of this operation
		 */
	    public static Operation getInstance() {
			if (instance == null)
				instance = new LessEqualOperation();
			return instance;
		}

		public Constant apply(List<Constant> operands) {
			Constant result = null;
			Constant oper1 = operands.get(0);
			Constant oper2 = operands.get(1);

			// return true if the first operand is less than
			// the second operand and false otherwise
			int value1 = Integer.parseInt(oper1.symbol());
			int value2 = Integer.parseInt(oper2.symbol());
			boolean lessEqual = value1 <= value2;

			try {
			    AlgebraGraph algebraGraph = AlgebraGraph.getInstance();
			    Algebra booleanAlgebra = algebraGraph.getAlgebra(AlgebraConstants.BOOLEAN);
			    if (lessEqual)
			        result = (Constant) booleanAlgebra.getOperation(DefaultBooleanAlgebra.TRUE);
			    else
			        result = (Constant) booleanAlgebra.getOperation(DefaultBooleanAlgebra.FALSE);
			}
			catch (UnknownSymbolException use) {
				System.err.println(use.toString());
			}
			return result;
		}
	}

	/**
	 * Integer greater than operation.
	 * @author Harmen Kastenberg
	 */
	private static class GreaterThanOperation extends DefaultOperation {

		/** The singleton instance of this operation. */
		private static GreaterThanOperation instance = null;

		/**
		 * Constructor.
		 */
	    private GreaterThanOperation() {
	        super(null, GT, 2);
	    }

		/**
		 * @return the singleton instance of this operation
		 */
	    public static Operation getInstance() {
			if (instance == null)
				instance = new GreaterThanOperation();
			return instance;
		}

		public Constant apply(List<Constant> operands) {
			Constant result = null;
			Constant oper1 = operands.get(0);
			Constant oper2 = operands.get(1);

			// return true if the first operand is greater than
			// the second operand and false otherwise
			int value1 = Integer.parseInt(oper1.symbol());
			int value2 = Integer.parseInt(oper2.symbol());
			boolean greaterThan = value1 > value2;

			try {
			    AlgebraGraph algebraGraph = AlgebraGraph.getInstance();
			    Algebra booleanAlgebra = algebraGraph.getAlgebra(AlgebraConstants.BOOLEAN);
			    if (greaterThan)
			        result = (Constant) booleanAlgebra.getOperation(DefaultBooleanAlgebra.TRUE);
			    else
			        result = (Constant) booleanAlgebra.getOperation(DefaultBooleanAlgebra.FALSE);
			}
			catch (UnknownSymbolException use) {
				System.err.println(use.toString());
			}
			return result;
		}
	}

	/**
	 * Integer greater than operation.
	 * @author Harmen Kastenberg
	 */
	private static class GreaterEqualOperation extends DefaultOperation {

		/** The singleton instance of this operation. */
		private static GreaterEqualOperation instance = null;

		/**
		 * Constructor.
		 */
	    private GreaterEqualOperation() {
	        super(null, GE, 2);
	    }

		/**
		 * @return the singleton instance of this operation
		 */
	    public static Operation getInstance() {
			if (instance == null)
				instance = new GreaterEqualOperation();
			return instance;
		}

		public Constant apply(List<Constant> operands) {
			Constant result = null;
			Constant oper1 = operands.get(0);
			Constant oper2 = operands.get(1);

			// return true if the first operand is greater than
			// the second operand and false otherwise
			int value1 = Integer.parseInt(oper1.symbol());
			int value2 = Integer.parseInt(oper2.symbol());
			boolean greaterEqual = value1 >= value2;

			try {
			    AlgebraGraph algebraGraph = AlgebraGraph.getInstance();
			    Algebra booleanAlgebra = algebraGraph.getAlgebra(AlgebraConstants.BOOLEAN);
			    if (greaterEqual)
			        result = (Constant) booleanAlgebra.getOperation(DefaultBooleanAlgebra.TRUE);
			    else
			        result = (Constant) booleanAlgebra.getOperation(DefaultBooleanAlgebra.FALSE);
			}
			catch (UnknownSymbolException use) {
				System.err.println(use.toString());
			}
			return result;
		}
	}

	/**
	 * Integer equals operation.
	 * @author Harmen Kastenberg
	 */
	private static class EqualsOperation extends DefaultOperation {

		/** The singleton instance of this operation. */
		private static EqualsOperation instance = null;

		/**
		 * Constructor.
		 */
	    private EqualsOperation() {
	        super(EQ, 2);
	    }

		/**
		 * @return the singleton instance of this operation
		 */
	    public static Operation getInstance() {
			if (instance == null)
				instance = new EqualsOperation();
			return instance;
		}

		public Constant apply(List<Constant> operands) {
			Constant result = null;
			Constant oper1 = operands.get(0);
			Constant oper2 = operands.get(1);

			// return true if both operands are equal
			// and false otherwise
			int value1 = Integer.parseInt(oper1.symbol());
			int value2 = Integer.parseInt(oper2.symbol());
			boolean equals = (value1 == value2);

			try {
			    AlgebraGraph algebraGraph = AlgebraGraph.getInstance();
			    Algebra booleanAlgebra = algebraGraph.getAlgebra(AlgebraConstants.BOOLEAN);
			    if (equals)
			        result = (Constant) booleanAlgebra.getOperation(DefaultBooleanAlgebra.TRUE);
			    else
			        result = (Constant) booleanAlgebra.getOperation(DefaultBooleanAlgebra.FALSE);
			}
			catch (UnknownSymbolException use) {
				System.err.println(use.toString());
			}
			return result;
		}
	}
}

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
 * $Id$
 */

package groove.algebra;

import groove.util.Groove;

import java.math.BigDecimal;
import java.util.List;

/**
 * Default real algebra, in which natural numbers serve as constants. Derived
 * from {@link DefaultIntegerAlgebra}.
 *
 * NOTE: This class is based on {@link Double}. The comparisons are implemented
 * using a tolerance (see {@link #TOLERANCE}). A more sophisticated
 * implementation of an algebra on real numbers could use {@link StrictMath},
 * {@link BigDecimal}, or other classes/frameworks dedicated to precise floating
 * point arithmetics.
 *
 * @author Christian Soltenborn
 * @version $Revision$ $Date$
 */
public class DefaultRealAlgebra extends Algebra {

	/**
	 * Used to compare real numbers: Two doubles are equal if the absolute value
	 * of their difference is smaller than this number. See
	 * {@link #approximatelyEquals(double, double)}.
	 */
	public static final double TOLERANCE = 0.0000001;

	/**
	 * Constructs the (singleton) instance of this class.
	 */
	private DefaultRealAlgebra() {
		super(NAME, DESCRIPTION);
	}

	@Override
	public Constant getConstant(String symbol) {
		Constant result;
		try {
			double value = Double.parseDouble(symbol);
			result = new RealConstant(value);
		} catch (NumberFormatException nfe) {
			result = null;
		}
		return result;
	}

	@Override
	public String getSymbol(Object value) {
		if (!(value instanceof Double)) {
			throw new IllegalArgumentException(String
					.format("Value is of class %s rather than Double", value
							.getClass()));
		}
		return value.toString();
	}

	/** Returns the {@link Constant} corresponding to a given real value. */
	static public RealConstant getReal(double value) {
		return new RealConstant(value);
	}

	/**
	 * Method facilitating the singleton-pattern.
	 *
	 * @return the single <tt>RealAlgebra</tt>-instance.
	 */
	public static DefaultRealAlgebra getInstance() {
		return instance;
	}

	/**
	 * Short name of this signature.
	 */
	public static final String NAME = Groove
			.getXMLProperty("label.real.prefix");

	/** Long description of this algebra. */
	public static final String DESCRIPTION = "Default real algebra";

	// initialize after NAME and DESCRIPTION but before the operations
	/**
	 * Singleton instance.
	 */
	private static final DefaultRealAlgebra instance = new DefaultRealAlgebra();

	/**
	 * Real addition operation symbol.
	 */
	public static final String ADD_SYMBOL = "add";

	/**
	 * Real substraction operation symbol.
	 */
	public static final String SUB_SYMBOL = "sub";

	/**
	 * Real multiplication operation symbol.
	 */
	public static final String MUL_SYMBOL = "mul";

	/**
	 * Real division operation symbol.
	 */
	public static final String DIV_SYMBOL = "div";

	/**
	 * Real minimum symbol.
	 */
	public static final String MIN_SYMBOL = "min";

	/**
	 * Real maximum symbol.
	 */
	public static final String MAX_SYMBOL = "max";

	/**
	 * Real less than operation symbol.
	 */
	public static final String LT_SYMBOL = "lt";

	/**
	 * Real less-or-equal operation symbol.
	 */
	public static final String LE_SYMBOL = "le";

	/**
	 * Real greater than operation symbol.
	 */
	public static final String GT_SYMBOL = "gt";

	/**
	 * Real greater-or-equal operation symbol.
	 */
	public static final String GE_SYMBOL = "ge";

	/**
	 * Real equals operation symbol.
	 */
	public static final String EQ_SYMBOL = "eq";

	/**
	 * Real-to-string coercion operation symbol.
	 */
	public static final String TO_STRING_SYMBOL = "toString";

	/**
	 * Real addition operation.
	 */
	private static final Operation ADD_OPERATION = new RealReal2RealOperation(
			ADD_SYMBOL) {
		@Override
		double apply(double arg1, double arg2) {
			return arg1 + arg2;
		}
	};

	/**
	 * Real subtraction operation.
	 */
	private static final Operation SUB_OPERATION = new RealReal2RealOperation(
			SUB_SYMBOL) {
		@Override
		double apply(double arg1, double arg2) {
			return arg1 - arg2;
		}
	};

	/**
	 * Real multiplication operation.
	 */
	private static final Operation MUL_OPERATION = new RealReal2RealOperation(
			MUL_SYMBOL) {
		@Override
		double apply(double arg1, double arg2) {
			return arg1 * arg2;
		}
	};

	/**
	 * Real division operation.
	 */
	private static final Operation DIV_OPERATION = new RealReal2RealOperation(
			DIV_SYMBOL) {
		@Override
		double apply(double arg1, double arg2) {
			return arg1 / arg2;
		}
	};

	/**
	 * Real less than operation.
	 */
	private static final Operation LT_OPERATION = new RealReal2BoolOperation(
			LT_SYMBOL) {
		@Override
		boolean apply(double arg1, double arg2) {
			return arg1 < arg2 && !approximatelyEquals(arg1, arg2);
		}
	};

	/**
	 * Real less-or-equal operation.
	 */
	private static final Operation LE_OPERATION = new RealReal2BoolOperation(
			LE_SYMBOL) {
		@Override
		boolean apply(double arg1, double arg2) {
			return arg1 <= arg2 || approximatelyEquals(arg1, arg2);
		}
	};

	/**
	 * Real greater than operation.
	 */
	private static final Operation GT_OPERATION = new RealReal2BoolOperation(
			GT_SYMBOL) {
		@Override
		boolean apply(double arg1, double arg2) {
			return arg1 > arg2 && !approximatelyEquals(arg1, arg2);
		}
	};

	/**
	 * Real greater-or-equal operation.
	 */
	private static final Operation GE_OPERATION = new RealReal2BoolOperation(
			GE_SYMBOL) {
		@Override
		boolean apply(double arg1, double arg2) {
			return arg1 >= arg2 || approximatelyEquals(arg1, arg2);
		}
	};

	/**
	 * Real equals operation.
	 */
	private static final Operation EQ_OPERATION = new RealReal2BoolOperation(
			EQ_SYMBOL) {
		@Override
		boolean apply(double arg1, double arg2) {
			return approximatelyEquals(arg1, arg2);
		}
	};

	private static boolean approximatelyEquals(double d1, double d2) {
		return Math.abs(d1 - d2) < TOLERANCE;
	}

	/**
	 * Real minimum operation.
	 */
	private static final Operation MIN_OPERATION = new RealReal2RealOperation(
			MIN_SYMBOL) {
		@Override
		double apply(double arg1, double arg2) {
			return Math.min(arg1, arg2);
		}
	};

	/**
	 * Real subtraction operation.
	 */
	private static final Operation MAX_OPERATION = new RealReal2RealOperation(
			MAX_SYMBOL) {
		@Override
		double apply(double arg1, double arg2) {
			return Math.max(arg1, arg2);
		}
	};

	/**
	 * Real-to-string coercion operation.
	 */
	private static final Operation TO_STRING_OPERATION = new Double2StringOperation(
			TO_STRING_SYMBOL) {
		@Override
		String apply(double arg1) {
			// TODO formatting?
			return "" + arg1;
		}
	};

	static {
		instance.addOperation(ADD_OPERATION);
		instance.addOperation(SUB_OPERATION);
		instance.addOperation(MUL_OPERATION);
		instance.addOperation(DIV_OPERATION);
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
	 * Real constant.
	 */
	public static class RealConstant extends DefaultConstant {

		/**
		 * Constructs a real constant with a given value.
		 */
		public RealConstant(double value) {
			super(DefaultRealAlgebra.getInstance(), "" + value);
			this.value = value;
		}

		/** Returns the value of this constant. */
		public Double getValue() {
			return value;
		}

		private final double value;
	}

	/**
	 * Binary real operation of signature <code>double, double -> double</code>.
	 */
	private static abstract class RealReal2RealOperation extends
			DefaultOperation {

		/**
		 * Constructs an operation in the current algebra, with arity 2 and a
		 * given symbol.
		 */
		protected RealReal2RealOperation(String symbol) {
			super(getInstance(), symbol, 2);
		}

		/**
		 * Performs a binary operation of type
		 * <code>double, double -> double</code>.
		 *
		 * @throws IllegalArgumentException
		 *             if the number or types of operands are incorrect.
		 */
		public Object apply(List<Object> args) {
			try {
				Double arg0 = (Double) args.get(0);
				Double arg1 = (Double) args.get(1);
				return apply(arg0, arg1);
			} catch (ClassCastException exc) {
				throw new IllegalArgumentException(exc);
			}
		}

		/** Applies the function encapsulated in this interface. */
		abstract double apply(double arg1, double arg2);
	}

	/** Binary real operation of signature <code>double, double -> bool</code>. */
	private static abstract class RealReal2BoolOperation extends
			DefaultOperation {

		/**
		 * Constructs an operation in the current algebra, with arity 2 and a
		 * given symbol.
		 */
		protected RealReal2BoolOperation(String symbol) {
			super(getInstance(), symbol, 2, DefaultBooleanAlgebra.getInstance());
		}

		/**
		 * Performs a binary operation of type
		 * <code>double, double -> bool</code>.
		 *
		 * @throws IllegalArgumentException
		 *             if the number or types of operands are incorrect.
		 */
		public Object apply(List<Object> args) {
			try {
				Double arg0 = (Double) args.get(0);
				Double arg1 = (Double) args.get(1);
				return apply(arg0, arg1);
			} catch (ClassCastException exc) {
				throw new IllegalArgumentException(exc);
			}
		}

		/** Applies the function encapsulated in this interface. */
		abstract boolean apply(double arg1, double arg2);
	}

	/** Unary real operation of signature <code>double -> string</code>. */
	private static abstract class Double2StringOperation extends
			DefaultOperation {

		/**
		 * Constructs an operation in the current algebra, with arity 2 and a
		 * given symbol.
		 */
		protected Double2StringOperation(String symbol) {
			super(getInstance(), symbol, 1, DefaultStringAlgebra.getInstance());
		}

		/**
		 * Performs a binary operation of type
		 * <code>double, double -> bool</code>.
		 *
		 * @throws IllegalArgumentException
		 *             if the number or types of operands are incorrect.
		 */
		public Object apply(List<Object> args) {
			try {
				Double arg0 = (Double) args.get(0);
				return apply(arg0);
			} catch (ClassCastException exc) {
				throw new IllegalArgumentException(exc);
			}
		}

		/** Applies the function encapsulated in this interface. */
		abstract String apply(double arg1);
	}
}

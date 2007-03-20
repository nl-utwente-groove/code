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
 * $Id: DefaultBooleanAlgebra.java,v 1.1.1.2 2007-03-20 10:42:39 kastenberg Exp $
 */
package groove.algebra;

import groove.util.Groove;

import java.util.List;

/**
 * Class description.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.1.1.2 $ $Date: 2007-03-20 10:42:39 $
 */
public class DefaultBooleanAlgebra extends Algebra {

    private static DefaultBooleanAlgebra booleanAlgebra = null;

    /** separator between prefix and rest */
    public final String SEPARATOR = Groove.getXMLProperty("label.aspect.separator");
    /** algebra prefix */
    public final String PREFIX = Groove.getXMLProperty("label.boolean.prefix");

    /** boolean AND-operator */
	public static final String AND = "and";
	/** boolean OR-operator */
	public static final String OR = "or";
	/** boolean NOT-operator */
	public static final String NOT = "not";

	/** representing the boolean value <tt>true</tt> */
	public static final String TRUE = "true";
	/** representing the boolean value <tt>false</tt> */
	public static final String FALSE = "false";

	private Operation operTrue, operFalse, operAnd, operOr, operNot;

    /**
     * Method facilitating the singleton-pattern.
     * @return the single <tt>BooleanAlgebra</tt>-instance.
     */
	public static DefaultBooleanAlgebra getInstance() {
	    if (booleanAlgebra == null)
	        booleanAlgebra = new DefaultBooleanAlgebra();
	    return booleanAlgebra;
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
	private DefaultBooleanAlgebra(String name) {
		super(name);
	}

	/**
     * Constructor.
     */
    private DefaultBooleanAlgebra() {
    	this("Default Boolean Algebra");
		operTrue = True.getInstance();
		operFalse = False.getInstance();
		operAnd = BooleanAndOperation.getInstance();
		operOr = BooleanOrOperation.getInstance();
		operNot = BooleanNotOperation.getInstance();
		operTrue.set(this, null, -1);
		operFalse.set(this, null, -1);
		operAnd.set(this, null, -1);
		operOr.set(this, null, -1);
		operNot.set(this, null, -1);
		this.addOperation(operTrue);
		this.addOperation(operFalse);
		this.addOperation(operAnd);
		this.addOperation(operOr);
		this.addOperation(operNot);
	}

	/**
	 * Boolean AND-operation.
	 * @author Harmen Kastenberg
	 */
	protected static class BooleanAndOperation extends DefaultOperation {

		/**
		 * The singleton instance.
		 */
		private static BooleanAndOperation instance = null;

		private BooleanAndOperation() {
			super(AND, 2);
		}

		public static Operation getInstance() {
			if (instance == null)
				instance = new BooleanAndOperation();
			return instance;
		}

		/* (non-Javadoc)
		 * @see groove.algebra.DefaultOperation#apply(java.util.List)
		 */
		public Constant apply(List<Constant> operands) {
			Constant result = null;
			Constant oper1 = operands.get(0);
			Constant oper2 = operands.get(1);

			// return the true-constant only if both operands are true
			// and the false-constant otherwise
			if (oper1.symbol().equals(True.getInstance().symbol()) &&
				oper2.symbol().equals(True.getInstance().symbol())) {
				result = True.getInstance();
			}
			else
				result = False.getInstance();

			return result;
		}
	}

	/**
	 * Boolean OR-operation.
	 * @author Harmen Kastenberg
	 */
	protected static class BooleanOrOperation extends DefaultOperation {

		private static BooleanOrOperation instance = null;

		private BooleanOrOperation() {
			super(OR, 2);
		}

		public static Operation getInstance() {
			if (instance == null)
				instance = new BooleanOrOperation();
			return instance;
		}

		public Constant apply(List<Constant> operands) {
			Constant result = null;
			Constant oper1 = operands.get(0);
			Constant oper2 = operands.get(1);

			// return the true-constant if one of the operands is true
			// and the false-constant otherwise
			if (oper1.symbol().equals(True.getInstance().symbol()) ||
				oper2.symbol().equals(True.getInstance().symbol())) {
				result = True.getInstance();
			}
			else
				result = False.getInstance();

			return result;
		}
	}

	/**
	 * Boolean NOT-operation.
	 * @author Harmen Kastenberg
	 */
	protected static class BooleanNotOperation extends DefaultOperation {

		private static BooleanNotOperation instance = null;

		private BooleanNotOperation() {
			super(NOT, 1);
		}

		public static Operation getInstance() {
			if (instance == null)
				instance = new BooleanNotOperation();
			return instance;
		}

		public Constant apply(List<Constant> operands) {
			Constant result = null;
			Constant oper1 = operands.get(0);

			// return the true-constant if the operand is false
			// and the false-constant if the operand is true
			if (oper1.symbol().equals(True.getInstance().symbol())) {
				result = False.getInstance();
			}
			else
				result = True.getInstance();

			return result;
		}
	}

	/**
	 * Boolean FALSE-constant.
	 * @author Harmen Kastenberg
	 */
	protected static class False extends DefaultConstant {

		private static Constant instance = null;

		private False() {
			set(null, FALSE, -1);
		}

		public static Constant getInstance() {
			if (instance == null)
				instance = new False();
			return instance;
		}

		public Constant apply(List<Constant> operands) {
			return getInstance();
		}
	}

	/**
	 * Boolean TRUE-constant.
	 * @author Harmen Kastenberg
	 */
	protected static class True extends DefaultConstant {

		private static Constant instance = null;

		private True() {
			set(null, TRUE, -1);
		}

		public static Constant getInstance() {
			if (instance == null)
				instance = new True();
			return instance;
		}

		public Constant apply(List<Constant> operands) {
			return getInstance();
		}
	}
}

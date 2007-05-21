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
 * $Id: DefaultBooleanAlgebra.java,v 1.5 2007-05-21 22:19:28 rensink Exp $
 */
package groove.algebra;

import groove.util.Groove;

import java.util.List;

/**
 * Class description.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.5 $ $Date: 2007-05-21 22:19:28 $
 */
public class DefaultBooleanAlgebra extends Algebra {
    /**
	 * Constructor.
	 */
	private DefaultBooleanAlgebra() {
		super(NAME, DESCRIPTION);
	}

	/** 
	 * Since all boolean constants can be recognised as operators,
	 * this method always returns <code>null</code>.
	 */
	@Override
	public Constant getConstant(String text) {
		return null;
	}
	
	/** Returns the {@link Constant} corresponding to a given boolean value. */
	static public Constant getBoolean(boolean value) {
		if (value) {
			return True.getInstance();
		} else {
			return False.getInstance();
		}
	}
//
//	private Operation operTrue;
//
//	private Operation operFalse;
//
//	private Operation operAnd;
//
//	private Operation operOr;
//
//	private Operation operNot;
	/**
	 * Method facilitating the singleton-pattern.
	 * @return the single <tt>BooleanAlgebra</tt>-instance.
	 */
	public static DefaultBooleanAlgebra getInstance() {
	    return instance;
	}

	//
//    /** separator between prefix and rest */
//    public final String SEPARATOR = Groove.getXMLProperty("label.aspect.separator");
    /** Name of the boolean signature. */
    public static final String NAME = Groove.getXMLProperty("label.boolean.prefix");
    /** Description of the default boolean algebra. */
    public static final String DESCRIPTION = "Default Boolean Algebra";
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
	/** Singleton instance of this algebra. */
    private static final DefaultBooleanAlgebra instance;
    
    static {
    	instance = new DefaultBooleanAlgebra();
    	instance.addOperation(True.getInstance());
    	instance.addOperation(False.getInstance());
    	instance.addOperation(BooleanAndOperation.getInstance());
    	instance.addOperation(BooleanOrOperation.getInstance());
    	instance.addOperation(BooleanNotOperation.getInstance());
    }
	/**
	 * Boolean AND-operator.
	 * @author Harmen Kastenberg
	 */
	protected static class BooleanAndOperation extends DefaultOperation {
		private BooleanAndOperation() {
			super(DefaultBooleanAlgebra.getInstance(), AND, 2);
		}

		@Override
		public Constant apply(List<Constant> operands) throws IllegalArgumentException {
			Constant oper1 = operands.get(0);
			Constant oper2 = operands.get(1);

			// return the true-constant only if both operands are true
			// and the false-constant otherwise
			boolean answer = (oper1.equals(True.getInstance()) &&
				oper2.equals(True.getInstance()));
			return getBoolean(answer);
		}

		/**
		 * Returns the singleton instance of this operator.
		 */
		public static Operation getInstance() {
			return instance;
		}

		/** The singleton instance. */
		private final static BooleanAndOperation instance = new BooleanAndOperation();
	}

	/**
	 * Boolean OR-operation.
	 * @author Harmen Kastenberg
	 */
	protected static class BooleanOrOperation extends DefaultOperation {
		/** Constructor for the singleton instance of this class. */
		private BooleanOrOperation() {
			super(DefaultBooleanAlgebra.getInstance(), OR, 2);
		}

		@Override
		public Constant apply(List<Constant> operands) throws IllegalArgumentException {
			Constant oper1 = operands.get(0);
			Constant oper2 = operands.get(1);

			// return the true-constant if one of the operands is true
			// and the false-constant otherwise
			boolean answer = (oper1.equals(True.getInstance()) ||
				oper2.equals(True.getInstance()));
			return getBoolean(answer);
		}

		/**
		 * Returns the singleton instance of this operation.
		 */
		public static Operation getInstance() {
			return instance;
		}

		/** The singleton instance. */
		static final private BooleanOrOperation instance = new BooleanOrOperation();
	}

	/**
	 * Boolean NOT-operation.
	 * @author Harmen Kastenberg
	 */
	protected static class BooleanNotOperation extends DefaultOperation {
		/** Constructor for the singleton instance of this class. */
		private BooleanNotOperation() {
			super(DefaultBooleanAlgebra.getInstance(), NOT, 1);
		}

		@Override
		public Constant apply(List<Constant> operands) throws IllegalArgumentException {
			Constant oper1 = operands.get(0);
			// return the true-constant if the operand is false
			// and the false-constant if the operand is true
			boolean answer = ! oper1.equals(True.getInstance());
			return getBoolean(answer);
		}

		/**
		 * Returns the singleton instance of this operation.
		 */
		public static Operation getInstance() {
			return instance;
		}

		/** The singleton instance. */
		static private final BooleanNotOperation instance = new BooleanNotOperation();
	}

	/**
	 * Boolean FALSE-constant.
	 * @author Harmen Kastenberg
	 */
	public static class False extends DefaultConstant {
		/** Constructor for the singleton instance of this class. */
		private False() {
			super(DefaultBooleanAlgebra.getInstance(), FALSE);
		}
		
		/** Returns the <code>false</code> value. */
		public boolean getValue() {
			return false;
		}

		/**
		 * @return the singleton instance
		 */
		public static Constant getInstance() {
			return instance;
		}

		/** The singleton instance. */
		static private final Constant instance = new False();
	}

	/**
	 * Boolean TRUE-constant.
	 * @author Harmen Kastenberg
	 */
	public static class True extends DefaultConstant {
		/** The singleton instance. */
		private static Constant instance = null;

		/** Constructor for the singleton instance of this class. */
		private True() {
			super(DefaultBooleanAlgebra.getInstance(), TRUE);
		}
		
		/** Returns the <code>true</code> value. */
		public boolean getValue() {
			return true;
		}

		/**
		 * @return the singleton instance
		 */
		public static Constant getInstance() {
			if (instance == null)
				instance = new True();
			return instance;
		}
	}
}

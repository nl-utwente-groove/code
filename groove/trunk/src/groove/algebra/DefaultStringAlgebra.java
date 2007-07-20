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
 * $Id: DefaultStringAlgebra.java,v 1.7 2007-07-20 09:42:39 rensink Exp $
 */
package groove.algebra;

import groove.util.ExprParser;
import groove.util.Groove;

import java.util.List;
/**
 * Class specifying the algebra of strings and the available operations
 * on strings.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.7 $ $Date: 2007-07-20 09:42:39 $
 */
public class DefaultStringAlgebra extends Algebra {	/**
	 * Constructor.
	 */
	private DefaultStringAlgebra() {
		super(NAME, DESCRIPTION);
	}

	@Override
	public Constant getConstant(String text) {
		String unquotedText = ExprParser.toUnquoted(text, ExprParser.DOUBLE_QUOTE_CHAR);
		if (unquotedText == null) {
			return null;
		} else {
			return new StringConstant(unquotedText);
		}
	}

	/** Returns the {@link Constant} corresponding to a given string value. */
	static public Constant getString(String value) {
		return new StringConstant(value);
	}

//
//    /** The concatenation operation. */
//    private Operation operConcat;
//    /** The equals operation. */
//    private Operation operEquals;
    
    /**
     * Method facilitating the singleton-pattern.
     * @return the single <tt>StringAlgebra</tt>-instance.
     */
	public static DefaultStringAlgebra getInstance() {
	    return instance;
	}

    /** Name of the string signature. */
    static public final String NAME = Groove.getXMLProperty("label.string.prefix");
    /** Description of the default string algebra. */
    static public final String DESCRIPTION = "Default String Algebra";
    /** The quote character used for strings. */
    static public final char QUOTE = ExprParser.DOUBLE_QUOTE_CHAR;
    /** Name of the string concatenation operation */
    public static final String CONCAT = "concat";
    /** Name of the string equals operation */
    public static final String EQUALS = "eq";

	/** Singleton instance of this class. */
    private final static DefaultStringAlgebra instance;

    static {
    	instance = new DefaultStringAlgebra();
//		operConcat = ConcatOperation.getInstance();
//		operEquals = EqualsOperation.getInstance();
//		operConcat.set(this, null, -1);
//		operEquals.set(this, null, -1);
		instance.addOperation(ConcatOperation.getInstance());
		instance.addOperation(EqualsOperation.getInstance());
    }
    
    /** Class implementing string constants. */
	static public class StringConstant extends DefaultConstant {
		/**
		 * Constructs a string constant from a given non-<code>null</code>) text.
		 * The text is the <i>content</i> of the string constant; i.e., it is 
		 * already unquoted.
		 * @param text the text of the constant
		 */
		public StringConstant(String text) {
			super(getInstance(), ExprParser.toQuoted(text, QUOTE));
			this.value = text;
		}

		/** Returns the (unquoted) string value of this constant. */
		public String getValue() {
			return value;
		}
		
		/** The (unquoted) string value of this constant. */
		private final String value;
	}
//
//	protected static class EmptyString extends DefaultConstant {
//	    /** Singleton instance. */
//	    private static Constant instance = null;
//
//	    private EmptyString() {
//	        set(algebra, EMPTY_STRING, 0);
//	    }
//
//	    /**
//	     * @return the singleton instance
//	     */
//	    public static Constant getInstance() {
//	        if (instance == null)
//	            instance = new EmptyString();
//	        return instance;
//	    }
//
//		@Override
//	    public Constant apply(List<Constant> operands) throws IllegalArgumentException {
//	        return getInstance();
//	    }
//	}

	/** Class implementing the string concatantion operation. */
	protected static class ConcatOperation extends DefaultOperation {
	    /** Singleton instance. */
		private static ConcatOperation operation = null;

		private ConcatOperation() {
			super(DefaultBooleanAlgebra.getInstance(), CONCAT, 2);
		}

		/**
		 * @return the singleton instance
		 */
		public static Operation getInstance() {
			if (operation == null)
				operation = new ConcatOperation();
			return operation;
		}

		@Override
		public Constant apply(List<Constant> operands) throws IllegalArgumentException {
            try {
                StringConstant oper1 = (StringConstant) operands.get(0);
                StringConstant oper2 = (StringConstant) operands.get(1);

                String concat = oper1.getValue() + oper2.getValue();
                return getString(concat);
            } catch (ClassCastException exc) {
                throw new IllegalArgumentException(exc);
            }
		}
	}

	/** Class implementing the string equality operation. */
	protected static class EqualsOperation extends DefaultOperation {
	    /** Singleton instance. */
		private static EqualsOperation operation = null;

		private EqualsOperation() {
			super(DefaultBooleanAlgebra.getInstance(), EQUALS, 2);
		}

		/**
		 * @return the singleton instance
		 */
		public static Operation getInstance() {
			if (operation == null)
				operation = new EqualsOperation();
			return operation;
		}

		@Override
		public Constant apply(List<Constant> operands) throws IllegalArgumentException {
			Constant oper1 = operands.get(0);
			Constant oper2 = operands.get(1);
			boolean equals = oper1.equals(oper2);
			return DefaultBooleanAlgebra.getBoolean(equals);
		}
	}
}

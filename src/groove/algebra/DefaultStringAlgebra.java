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
 * $Id: DefaultStringAlgebra.java,v 1.10 2007-12-22 10:11:21 kastenberg Exp $
 */
package groove.algebra;

import groove.util.ExprParser;
import groove.util.Groove;
import groove.view.FormatException;

import java.util.List;
/**
 * Class specifying the algebra of strings and the available operations
 * on strings.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.10 $ $Date: 2007-12-22 10:11:21 $
 */
public class DefaultStringAlgebra extends Algebra {	/**
	 * Constructor.
	 */
	private DefaultStringAlgebra() {
		super(NAME, DESCRIPTION);
	}

	@Override
	public Constant getConstant(String text) {
        try {
            return new StringConstant(ExprParser.toUnquoted(text, ExprParser.DOUBLE_QUOTE_CHAR));
        } catch (FormatException e) {
            return null;
        }
	}
    
    @Override
    public String getSymbol(Object value) {
        if (!(value instanceof String)) {
            throw new IllegalArgumentException(String.format("Value is of class %s rather than Boolean", value.getClass()));
        }
        return ExprParser.toQuoted((String) value, ExprParser.DOUBLE_QUOTE_CHAR);
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
    public static final String CONCAT_SYMBOL = "concat";
    /** Name of the string equals operation */
    public static final String EQ_SYMBOL = "eq";

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
			super(DefaultStringAlgebra.getInstance(), CONCAT_SYMBOL, 2);
		}

		/**
		 * @return the singleton instance
		 */
		public static Operation getInstance() {
			if (operation == null)
				operation = new ConcatOperation();
			return operation;
		}

		public Object apply(List<Object> args) throws IllegalArgumentException {
            try {
                String arg0 = (String) args.get(0);
                String arg1 = (String) args.get(1);
                return arg0+arg1;
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
			super(DefaultStringAlgebra.getInstance(), EQ_SYMBOL, 2, DefaultBooleanAlgebra.getInstance());
		}

		/**
		 * @return the singleton instance
		 */
		public static Operation getInstance() {
			if (operation == null)
				operation = new EqualsOperation();
			return operation;
		}

		public Object apply(List<Object> args) throws IllegalArgumentException {
            try {
                String arg0 = (String) args.get(0);
                String arg1 = (String) args.get(1);
                return arg0.equals(arg1);
            } catch (ClassCastException exc) {
                throw new IllegalArgumentException(exc);
            }
		}
	}
}

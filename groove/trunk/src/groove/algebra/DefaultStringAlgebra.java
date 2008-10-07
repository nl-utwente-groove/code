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
 * $Id: DefaultStringAlgebra.java,v 1.11 2008-01-16 08:40:00 rensink Exp $
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
 * @version $Revision$ $Date: 2008-01-16 08:40:00 $
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
    /** 
     * Singleton instance of this class.
     * This declaration needs to be here to solve circular dependencies. 
     */
    private final static DefaultStringAlgebra instance = new DefaultStringAlgebra();

    /** The quote character used for strings. */
    static public final char QUOTE = ExprParser.DOUBLE_QUOTE_CHAR;
    /** Name of the string concatenation operation */
    public static final String CONCAT_SYMBOL = "concat";
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
    /** Name of the string equals operation */
    public static final String EQ_SYMBOL = "eq";

    /**
     * String less than operation.
     */
    private static final Operation LT_OPERATION = new StringString2BoolOperation(LT_SYMBOL) {
        @Override
        boolean apply(String arg1, String arg2) {
            return arg1.compareTo(arg2) < 0;
        }
    };
    /**
     * String less-or-equal operation.
     */
    private static final Operation LE_OPERATION = new StringString2BoolOperation(LE_SYMBOL) {
        @Override
        boolean apply(String arg1, String arg2) {
            return arg1.compareTo(arg2) <= 0;
        }
    };
    /**
     * String greater than operation.
     */
    private static final Operation GT_OPERATION = new StringString2BoolOperation(GT_SYMBOL) {
        @Override
        boolean apply(String arg1, String arg2) {
            return arg1.compareTo(arg2) > 0;
        }
    };
    
    /**
     * String greater-or-equal operation.
     */
    private static final Operation GE_OPERATION = new StringString2BoolOperation(GE_SYMBOL) {
        @Override
        boolean apply(String arg1, String arg2) {
            return arg1.compareTo(arg2) >= 0;
        }
    };
    
    /**
     * String equals operation.
     */
    private static final Operation EQ_OPERATION = new StringString2BoolOperation(EQ_SYMBOL) {
        @Override
        boolean apply(String arg1, String arg2) {
            return arg1.equals(arg2);
        }
    };
    
    static {
		instance.addOperation(ConcatOperation.getInstance());
        instance.addOperation(LT_OPERATION);
        instance.addOperation(LE_OPERATION);
        instance.addOperation(GT_OPERATION);
        instance.addOperation(GE_OPERATION);
        instance.addOperation(EQ_OPERATION);
    }

    /** Binary integer operation of signature <code>string, string -> bool</code>. */
    private static abstract class StringString2BoolOperation extends DefaultOperation {
        /** Constructs an operation in the current algebra, with arity 2 and a given symbol. */
        protected StringString2BoolOperation(String symbol) {
            super(getInstance(), symbol, 2, DefaultBooleanAlgebra.getInstance());
        }

        /** 
         * Performs a binary operation of type <code>string, string -> bool</code>. 
         * @throws IllegalArgumentException if the number or types of operands are incorrect.
         */
        public Object apply(List<Object> args) {
            try {
                String arg0 = (String) args.get(0);
                String arg1 = (String) args.get(1);
                return apply(arg0, arg1);
            } catch (ClassCastException exc) {
                throw new IllegalArgumentException(exc);
            }
        }
        
        /** Applies the function encapsulated in this interface. */
        abstract boolean apply(String arg1, String arg2);
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
}

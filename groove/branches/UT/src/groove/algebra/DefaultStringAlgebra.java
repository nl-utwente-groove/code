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
 * $Id: DefaultStringAlgebra.java,v 1.1.1.2 2007-03-20 10:42:39 kastenberg Exp $
 */
package groove.algebra;

import groove.graph.algebra.AlgebraConstants;
import groove.graph.algebra.AlgebraGraph;
import groove.util.Groove;

import java.util.List;
/**
 * Class specifying the algebra of strings and the available operations
 * on strings.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.1.1.2 $ $Date: 2007-03-20 10:42:39 $
 */
public class DefaultStringAlgebra extends Algebra {

    private static DefaultStringAlgebra stringAlgebra = null;

    /** separator between prefix and rest */
    public final String SEPARATOR = Groove.getXMLProperty("label.aspect.separator");
    /** algebra prefix */
    public final String PREFIX = Groove.getXMLProperty("label.string.prefix");
    /** string concat-operation */
    public static final String CONCAT = "concat";
    /** string equals-operation */
    public static final String EQUALS = "eq";
    /** empty string */
    public static final String EMPTY_STRING = "";

    private Operation operConcat, operEquals;

    /**
     * Method facilitating the singleton-pattern.
     * @return the single <tt>StringAlgebra</tt>-instance.
     */
	public static DefaultStringAlgebra getInstance() {
	    if (stringAlgebra ==null)
	        stringAlgebra = new DefaultStringAlgebra();
	    return stringAlgebra;
	}

	/* (non-Javadoc)
	 * @see groove.algebra.Algebra#prefix()
	 */
	public String prefix() {
		return PREFIX + SEPARATOR;
	}

	/**
	 * Constructor.
	 * @param name the name of this algebra
	 */
	private DefaultStringAlgebra(String name) {
		super(name);
	}

	/**
	 * Constructor.
	 */
	private DefaultStringAlgebra() {
		this("Default String Algebra");
		operConcat = ConcatOperation.getInstance();
		operEquals = EqualsOperation.getInstance();
		operConcat.set(this, null, -1);
		operEquals.set(this, null, -1);
		this.addOperation(operConcat);
		this.addOperation(operEquals);
	}

	/* (non-Javadoc)
	 * @see groove.util.Algebra#getOperation(java.lang.String)
	 */
	public Operation getOperation(String symbol) throws UnknownSymbolException {
		Operation operation;
		if (getOperationSymbols().contains(symbol)) {
			operation = super.getOperation(symbol);
		} else {
			operation = new StringConstant(this, symbol);
			addOperation(operation);
		}
		return operation;
	}

	protected static class StringConstant extends DefaultConstant {
		public StringConstant(Algebra algebra, String symbol) {
			set(algebra, symbol, 0);
		}
	}

	protected static class EmptyString extends DefaultConstant {
	    public static Constant instance = null;

	    private EmptyString() {
	        set(algebra, EMPTY_STRING, 0);
	    }

	    public static Constant getInstance() {
	        if (instance == null)
	            instance = new EmptyString();
	        return instance;
	    }

	    public Constant apply(List<Constant> operands) {
	        return getInstance();
	    }
	}

	protected static class ConcatOperation extends DefaultOperation {

		private static ConcatOperation operation = null;

		private ConcatOperation() {
			super(CONCAT, 2);
		}

		public static Operation getInstance() {
			if (operation == null)
				operation = new ConcatOperation();
			return operation;
		}

		public Constant apply(List<Constant> operands) {
			Constant result = null;
			Constant oper1 = operands.get(0);
			Constant oper2 = operands.get(1);

			String concat = oper1.symbol() + oper2.symbol();

			try {
				result = (Constant) algebra().getOperation("" + concat); 
			}
			catch (UnknownSymbolException use) {
				System.err.println(use.toString());
			}
			return result;
		}
	}

	protected static class EqualsOperation extends DefaultOperation {
		
		private static EqualsOperation operation = null;

		private EqualsOperation() {
			super(EQUALS, 2);
		}

		public static Operation getInstance() {
			if (operation == null)
				operation = new EqualsOperation();
			return operation;
		}

		public Constant apply(List<Constant> operands) {
			Constant result = null;
			Constant oper1 = operands.get(0);
			Constant oper2 = operands.get(1);

			boolean equals = oper1.symbol().equals(oper2.symbol());

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
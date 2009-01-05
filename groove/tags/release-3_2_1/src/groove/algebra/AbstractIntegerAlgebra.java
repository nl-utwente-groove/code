/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: DefaultIntegerAlgebra.java,v 1.10 2008/01/26 09:47:32 kastenberg Exp $
 */
package groove.algebra;

import java.util.List;

import groove.util.Groove;

/**
 * Default integer algebra, in which natural numbers serve as constants.
 * 
 * @author Harmen Kastenberg
 * @version $Revision$
 * @deprecated Superseded by the new algebra implementation
 */
@Deprecated
public class AbstractIntegerAlgebra extends OldAlgebra {
    /** representing the abstract integer value <tt>zero</tt> */
    public static final String ZERO = "zero";
    /** representing the abstract integer value <tt>pos</tt> */
    public static final String POS = "pos";
    /** representing the abstract integer value <tt>neg</tt> */
    public static final String NEG = "neg";

    /**
     * Short name of this signature.
     */
    public static final String NAME =
        Groove.getXMLProperty("label.abstract.integer.prefix");
    /** Long description of this algebra. */
    public static final String DESCRIPTION = "Default integer algebra";
    // initialize after NAME and DESCRIPTION but before the operations
    /**
     * Singleton instance.
     */
    private static final AbstractIntegerAlgebra instance =
        new AbstractIntegerAlgebra();

    /**
     * Constructs the (singleton) instance of this class.
     */
    private AbstractIntegerAlgebra() {
        super(NAME, DESCRIPTION);
    }

    @Override
    public Constant getConstant(String symbol) {
        Constant result;
        result = new AbstractIntegerConstant(symbol);
        return result;
    }

    @Override
    public String getSymbol(Object value) {
        return value.toString();
    }

    /**
     * Method facilitating the singleton-pattern.
     * @return the single <tt>IntegerAlgebra</tt>-instance.
     */
    public static AbstractIntegerAlgebra getInstance() {
        return instance;
    }

    /**
     * Integer addition operation symbol.
     */
    public static final String ADD_SYMBOL = "add";

    /**
     * Integer addition operation.
     */
    private static final OldOperation ADD_OPERATION =
        new IntInt2IntOperation(ADD_SYMBOL) {
            @Override
            int apply(int arg1, int arg2) {
                return arg1 + arg2;
            }
        };

    static {
        instance.addOperation(ADD_OPERATION);
        instance.addOperation(Zero.getInstance());
        instance.addOperation(Pos.getInstance());
        instance.addOperation(Neg.getInstance());
    }

    /**
     * Integer constant.
     */
    public static class AbstractIntegerConstant extends DefaultConstant {
        /**
         * Constructs an integer constant with a given value.
         */
        public AbstractIntegerConstant(String value) {
            super(AbstractIntegerAlgebra.getInstance(), "" + value);
            this.value = value;
        }

        /** Returns the value of this constant. */
        public String getValue() {
            return this.value;
        }

        private final String value;
    }

    /**
     * Abstract ZERO integer.
     * @author Harmen Kastenberg
     */
    public static class Zero extends DefaultConstant {
        /** Constructor for the singleton instance of this class. */
        private Zero() {
            super(AbstractIntegerAlgebra.getInstance(), ZERO);
        }

        /** Returns the <code>false</code> value. */
        public String getValue() {
            return ZERO;
        }

        /**
         * @return the singleton instance
         */
        public static Constant getInstance() {
            return instance;
        }

        /** The singleton instance. */
        static private final Constant instance = new Zero();
    }

    /**
     * Abstract ZERO integer.
     * @author Harmen Kastenberg
     */
    public static class Pos extends DefaultConstant {
        /** Constructor for the singleton instance of this class. */
        private Pos() {
            super(AbstractIntegerAlgebra.getInstance(), POS);
        }

        /** Returns the <code>false</code> value. */
        public String getValue() {
            return POS;
        }

        /**
         * @return the singleton instance
         */
        public static Constant getInstance() {
            return instance;
        }

        /** The singleton instance. */
        static private final Constant instance = new Pos();
    }

    /**
     * Abstract ZERO integer.
     * @author Harmen Kastenberg
     */
    public static class Neg extends DefaultConstant {
        /** Constructor for the singleton instance of this class. */
        private Neg() {
            super(AbstractIntegerAlgebra.getInstance(), NEG);
        }

        /** Returns the <code>false</code> value. */
        public String getValue() {
            return NEG;
        }

        /**
         * @return the singleton instance
         */
        public static Constant getInstance() {
            return instance;
        }

        /** The singleton instance. */
        static private final Constant instance = new Neg();
    }

    /** Binary integer operation of signature <code>int, int -> int</code>. */
    private static abstract class IntInt2IntOperation extends DefaultOperation {
        /**
         * Constructs an operation in the current algebra, with arity 2 and a
         * given symbol.
         */
        protected IntInt2IntOperation(String symbol) {
            super(getInstance(), symbol, 2);
        }

        /**
         * Performs a binary operation of type <code>int, int -> int</code>.
         * @throws IllegalArgumentException if the number or types of operands
         *         are incorrect.
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
}

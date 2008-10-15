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
 * $Id: DefaultBooleanAlgebra.java,v 1.7 2007-12-22 10:11:21 kastenberg Exp $
 */
package groove.algebra;

import groove.util.Groove;

import java.util.List;

/**
 * Class description.
 * 
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2007-12-22 10:11:21 $
 */
public class DefaultBooleanAlgebra extends Algebra {
    /**
     * Constructor.
     */
    private DefaultBooleanAlgebra() {
        super(NAME, DESCRIPTION);
    }

    /**
     * Since all boolean constants can be recognised as operators, this method
     * always returns <code>null</code>.
     */
    @Override
    public Constant getConstant(String text) {
        return null;
    }

    @Override
    public String getSymbol(Object value) {
        if (!(value instanceof Boolean)) {
            throw new IllegalArgumentException(String.format(
                "Value is of class %s rather than Boolean", value.getClass()));
        }
        return value.toString();
    }

    /** Returns the {@link Constant} corresponding to a given boolean value. */
    static public Constant getBoolean(boolean value) {
        if (value) {
            return True.getInstance();
        } else {
            return False.getInstance();
        }
    }

    /**
     * Method facilitating the singleton-pattern.
     * @return the single <tt>BooleanAlgebra</tt>-instance.
     */
    public static DefaultBooleanAlgebra getInstance() {
        return instance;
    }

    /** Name of the boolean signature. */
    public static final String NAME =
        Groove.getXMLProperty("label.boolean.prefix");
    /** Description of the default boolean algebra. */
    public static final String DESCRIPTION = "Default Boolean Algebra";
    /** boolean AND-operator */
    public static final String AND_SYMBOL = "and";
    /** boolean OR-operator */
    public static final String OR_SYMBOL = "or";
    /** boolean NOT-operator */
    public static final String NOT_SYMBOL = "not";
    /** boolean EQUAL-operator */
    public static final String EQUAL_SYMBOL = "eq";

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
        instance.addOperation(BooleanEqualOperation.getInstance());
    }

    /**
     * Abstract operator of type <code>bool, bool -> bool</code>
     * @author Harmen Kastenberg
     */
    abstract private static class BoolBool2BoolOperation extends
            DefaultOperation {
        /**
         * Constructs a binary operation in the {@link DefaultBooleanAlgebra},
         * of type <code>Boolean, Boolean -> Object</code>
         */
        protected BoolBool2BoolOperation(String symbol) {
            super(DefaultBooleanAlgebra.getInstance(), symbol, 2);
        }

        public Object apply(List<Object> args) throws IllegalArgumentException {
            try {
                return apply((Boolean) args.get(0), (Boolean) args.get(1));
            } catch (ClassCastException exc) {
                throw new IllegalArgumentException(exc);
            } catch (ArrayIndexOutOfBoundsException exc) {
                throw new IllegalArgumentException(exc);
            }
        }

        /** Callback method to apply the operation. */
        abstract protected Boolean apply(Boolean arg0, Boolean arg1);
    }

    /**
     * Boolean AND-operator.
     * @author Harmen Kastenberg
     */
    protected static class BooleanAndOperation extends BoolBool2BoolOperation {
        private BooleanAndOperation() {
            super(AND_SYMBOL);
        }

        @Override
        public Boolean apply(Boolean arg0, Boolean arg1)
            throws IllegalArgumentException {
            return arg0 && arg1;
        }

        /**
         * Returns the singleton instance of this operator.
         */
        public static Operation getInstance() {
            return instance;
        }

        /** The singleton instance. */
        private final static BooleanAndOperation instance =
            new BooleanAndOperation();
    }

    /**
     * Boolean OR-operation.
     * @author Harmen Kastenberg
     */
    protected static class BooleanOrOperation extends BoolBool2BoolOperation {
        /** Constructor for the singleton instance of this class. */
        private BooleanOrOperation() {
            super(OR_SYMBOL);
        }

        @Override
        public Boolean apply(Boolean arg0, Boolean arg1)
            throws IllegalArgumentException {
            return arg0 || arg1;
        }

        /**
         * Returns the singleton instance of this operation.
         */
        public static Operation getInstance() {
            return instance;
        }

        /** The singleton instance. */
        static final private BooleanOrOperation instance =
            new BooleanOrOperation();
    }

    /**
     * Boolean NOT-operation.
     * @author Harmen Kastenberg
     */
    protected static class BooleanNotOperation extends DefaultOperation {
        /** Constructor for the singleton instance of this class. */
        private BooleanNotOperation() {
            super(DefaultBooleanAlgebra.getInstance(), NOT_SYMBOL, 1);
        }

        public Object apply(List<Object> args) throws IllegalArgumentException {
            Boolean arg = (Boolean) args.get(0);
            return !arg;
        }

        /**
         * Returns the singleton instance of this operation.
         */
        public static Operation getInstance() {
            return instance;
        }

        /** The singleton instance. */
        static private final BooleanNotOperation instance =
            new BooleanNotOperation();
    }

    /**
     * Boolean EQUAL-operation.
     * @author Eduard Bauer
     */
    protected static class BooleanEqualOperation extends BoolBool2BoolOperation {
        /** Constructor for the singleton instance of this class. */
        private BooleanEqualOperation() {
            super(EQUAL_SYMBOL);
        }

        @Override
        public Boolean apply(Boolean arg0, Boolean arg1)
            throws IllegalArgumentException {
            return arg0.equals(arg1);
        }

        /**
         * Returns the singleton instance of this operation.
         */
        public static Operation getInstance() {
            return instance;
        }

        /** The singleton instance. */
        static final private BooleanEqualOperation instance =
            new BooleanEqualOperation();
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
        public Boolean getValue() {
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
        public Boolean getValue() {
            return true;
        }

        /**
         * @return the singleton instance
         */
        public static Constant getInstance() {
            if (instance == null) {
                instance = new True();
            }
            return instance;
        }
    }
}

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
 * $Id: ReflectedAlgebra.java,v 1.4 2007-10-10 08:59:59 rensink Exp $
 */
package groove.algebra;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 
 * @author Arend Rensink
 * @version $Revision$
 */
public class ReflectedAlgebra<T> extends Algebra {
    /**
     * @param sig Signature of the algebra to be reflected
     * @param alg The algebra to be reflected.
     */
    public <S extends IntSignature<?>, A extends RealAlgebra<T>> ReflectedAlgebra(Class<S> sig, A alg) {
        super(getName(sig), "Algebra generated from "+alg.getClass());
        for (Method method: sig.getMethods()) {
            addOperation(createOperation(method));
        }
        this.alg = alg;
    }

    @Override
    public Constant getConstant(String text) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public String getSymbol(Object value) {
        return alg.getSymbol((T) value);
    }
    
    final RealAlgebra<T> alg;

    static private Operation createOperation(Method method) {
        final Class<?>[] parameterTypes = method.getParameterTypes();
        return new Operation() {

            /* (non-Javadoc)
             * @see groove.algebra.Operation#algebra()
             */
            public Algebra algebra() {
                // TODO Auto-generated method stub
                return null;
            }

            /* (non-Javadoc)
             * @see groove.algebra.Operation#apply(java.util.List)
             */
            public Object apply(List<Object> args) throws IllegalArgumentException {
                // TODO Auto-generated method stub
                return null;
            }

            /* (non-Javadoc)
             * @see groove.algebra.Operation#arity()
             */
            public int arity() {
                return parameterTypes.length;
            }

            /* (non-Javadoc)
             * @see groove.algebra.Operation#symbol()
             */
            public String symbol() {
                // TODO Auto-generated method stub
                return null;
            }

            /* (non-Javadoc)
             * @see groove.algebra.Operation#getResultType()
             */
            public Algebra getResultType() {
                // TODO Auto-generated method stub
                return null;
            }

            
        };
    }
    
    /** Retrieves the value of the static {@link #NAME_FIELD} field of a given class object. */
    static private String getName(Class<?> sig) {
        try {
            return (String) sig.getField(NAME_FIELD).get(null);
        } catch (SecurityException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(e);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    /** Name of the (static) name field of a signature class object. */
    static public final String NAME_FIELD = "NAME";
    
    /**
     * Class that wraps a reflected Java method into an algebra operation.
     */
    private class MethodOperation implements Operation {
        MethodOperation(Method method) {
            this.method = method;
            this.parameterTypes = method.getParameterTypes();
        }
        
        public Algebra algebra() {
            return ReflectedAlgebra.this;
        }

        /**
         * 
         */
        public Object apply(List<Object> args) throws IllegalArgumentException {
            int arity = arity();
            if (args.size() != arity) {
                throw new IllegalArgumentException("Wrong number of arguments");
            }
            try {
                return method.invoke(alg, args.toArray());
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException(e);
            } catch (InvocationTargetException e) {
                throw new IllegalArgumentException(e);
            }
        }

        public int arity() {
            return parameterTypes.length;
        }

        public String symbol() {
            return method.getName();
        }

        /* (non-Javadoc)
         * @see groove.algebra.Operation#getResultType()
         */
        public Algebra getResultType() {
            // TODO Auto-generated method stub
            return null;
        }

        private final Method method;
        private final Class<?>[] parameterTypes;
    }
}

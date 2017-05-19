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
 * $Id$
 */
package groove.algebra;

/**
 * General interface for attribute data signatures. All data signatures should
 * be abstract classes implementing this, and in addition adhere to the
 * following conventions:
 * <ul>
 * <li>For a signature named "zzz", the Java interface name should be
 * <code>ZzzSignature</code>
 * <li>The signature should define a single sort <code>Zzz</code>; the sort name
 * should be a type parameter
 * <li>If other data sorts are needed, they should also be declared as type
 * parameters
 * <li>For each such additional type parameter <code>Yyy</code>, there should be
 * a corresponding class <code>YyySignature</code>
 * <li>There is no overloading of the methods in a signature
 * </ul>
 * @author Arend Rensink
 * @version $Revision $
 */
public interface Signature {
    /** Returns the primary sort of this signature. */
    public Sort getSort();

    /** Enumeration of operators for this signature. */
    public interface OpValue {
        /**
         * Returns the literal name of the enum operator value.
         * (This is typically an all-caps name, according to the naming conventions
         * of Java.)
         */
        String name();

        /** Returns the corresponding operator object. */
        public Operator getOperator();

        /** Indicates if this operator supports zero arguments.
         * This is especially relevant for set-based operators.
         */
        default public boolean isSupportsZero() {
            return false;
        }
    }
}

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
    /** Returns the signature kind of this signature. */
    public SignatureKind getKind();

    /**
     * Tests if a given string is a representation of a value of the signature.
     * This should be implemented in the concrete signature, not in the algebra.
     * @param value a prospective string representation of an algebra constant 
     */
    public boolean isValue(String value);

    /**
     * Conversion of native Java representation of algebra constants to
     * the corresponding algebra values.
     * @param constant the native Java representation of an algebra constants for
     * this signature
     * @throws IllegalArgumentException if the parameter is not of the
     * native Java type
     */
    public Object getValueFromJava(Object constant)
        throws IllegalArgumentException;
}

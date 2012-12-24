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
 * Interface of an algebra (a class implementing a {@link Signature}).
 * @author Arend Rensink
 * @version $Revision $
 */
public interface Algebra<T> extends Signature {
    /**
     * Converts the string representations of an algebra value to
     * the corresponding algebra representation. May throw an exception
     * or return <code>null</code> if the string constant does not satisfy
     * {@link Signature#isValue(String)}.
     */
    T getValueFromSymbol(String constant);

    /**
     * Converts the native Java representation of a data value to
     * its corresponding algebra representation.
     */
    T getValueFromJava(Object constant);

    /** Converts an algebra value to its symbolic string representation. */
    String getSymbol(Object value);

    /** 
     * Returns the name of the algebra.
     * Note that this is <i>not</i> the same as the name of the signature;
     * for the signature name, use {@code getKind().getName()}
     * @see #getKind()
     */
    String getName();

    /**
     * Returns the algebra family to which this algebra primarily belongs.
     * Note that an algebra may belong to more than one family; in that case,
     * {@link AlgebraFamily#DEFAULT} is returned in preference to other values.
     */
    AlgebraFamily getFamily();
}

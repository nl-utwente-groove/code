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
     * Conversion of string representations of algebra constants to
     * the corresponding algebra values. May throw an exception
     * or return <code>null</code> if the string constant does not satisfy
     * {@link Signature#isValue(String)}.
     */
    T getValue(String constant);

    /** Conversion of algebra values to their string representations. */
    String getSymbol(Object value);

    /** Returns the name of the algebra. */
    String getName();
}

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
 * $Id: IntSignature.java,v 1.3 2007-08-26 07:24:19 rensink Exp $
 */
package groove.algebra;

/**
 * Interface for integer algebras.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface IntSignature<T> extends Signature {
    /** Addition of two integers. */
    T add(T arg1, T arg2);

    /** Name of this signature. */
    static final String NAME = "int";
    
    /** The default implementation of this signature. */
    static final Class<? extends IntSignature<?>> DEFAULT = JavaIntAlgebra.class;
}

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
 * Exception that is thrown when trying to instantiate an algebra value from an
 * algebra not knowing this symbol as neither one of its operations nor as one
 * of its constants.
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2007-05-21 22:19:28 $
 */
public class UnknownSymbolException extends Exception {
    /** Constructs an exception with a given error message. */
    public UnknownSymbolException(String message) {
        super(message);
    }

    /**
     * Constructs an exception with an error message cosntructed from a given
     * body and arguments, using {@link String#format(String, Object[])}.
     */
    public UnknownSymbolException(String message, Object... args) {
        this(String.format(message, args));
    }
}

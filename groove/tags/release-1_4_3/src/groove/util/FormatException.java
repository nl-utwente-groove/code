// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/*
 * $Id: FormatException.java,v 1.1 2007-04-01 12:50:01 rensink Exp $
 */
package groove.util;

/**
 * General exception class signalling a format error found during a
 * conversion between one model to another.
 * @author Arend Rensink
 * @version $Revision: 1.1 $ $Date: 2007-04-01 12:50:01 $
 */
public class FormatException extends Exception {
    /**
     * Constructs a format exception with an empty message.
     */
    public FormatException() { 
    	// explicit empty constructor
    }

    /**
     * Constructs a format exception with a given message.
     * @see java.lang.Exception#Exception(String)
     */
    public FormatException(String message) {
        super(message);
    }

    /**
     * Constructs a format exception with a given formatted message.
     * Calls {@link String#format(String, Object[])} with the message and
     * parameters, and invokes {@link #FormatException(String)} with
     * the resulting message.
     * @see #FormatException(String)
     */
    public FormatException(String message, Object... parameters) {
        super(String.format(message, parameters));
    }

    /**
     * Constructs a format exception from another exception.
     * @see java.lang.Exception#Exception(java.lang.Throwable)
     */
    public FormatException(Exception exc) {
        super(exc);
    }
}

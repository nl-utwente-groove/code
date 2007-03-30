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
 * $Id: RuleFormatException.java,v 1.1 2007-03-30 15:50:37 rensink Exp $
 */
package groove.trans.view;

/**
 * Thrown when a rule or rule view cannot be created due to
 * some format error. 
 * @author Arend Rensink
 * @version $Revision: 1.1 $
 */
public class RuleFormatException extends Exception {
	/** Constructs an exception with a given message. */
    public RuleFormatException(String message) {
        super(message);
    }
    
    /**
     * Constructs a grammar format exception with a given formatted message.
     * Calls {@link String#format(String, Object[])} with the message and
     * parameters, and invokes {@link #RuleFormatException(String)} with
     * the resulting message.
     * @see #RuleFormatException(String)
     */
    public RuleFormatException(String message, Object... parameters) {
        super(String.format(message, parameters));
    }

	/** Constructs an exception from a pre-existing exception. */
    public RuleFormatException(Exception cause) {
        super(cause);
    }
}

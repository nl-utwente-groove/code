/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
 * $Id$
 */
package groove.io.conceptual;

/**
 * Exception class for use in the abstraction model. General exception for pretty much anything that goes wrong in the abstraction.
 * @author s0141844
 * @version $Revision $
 */
public class AbstractionException extends Exception {
    /**
     * Create AbstractionException without extra information
     */
    public AbstractionException() {

    }

    /**
     * Create AbstractionException with a message
     * @param message The message of the exception
     */
    public AbstractionException(String message) {
        super(message);
    }

    /**
     * Create AbstractionException with a Throwable cause
     * @param cause The cause of the exception
     */
    public AbstractionException(Throwable cause) {
        super(cause);
    }

    /**
     * Create AbstractionException with both a message and a cause
     * @param message The message of the exception
     * @param cause The cause of the exception
     */
    public AbstractionException(String message, Throwable cause) {
        super(message, cause);
    }
}

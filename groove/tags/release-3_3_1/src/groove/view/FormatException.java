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
 * $Id: FormatException.java,v 1.5 2008-01-30 09:33:26 iovka Exp $
 */
package groove.view;

import groove.util.Groove;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * General exception class signalling a format error found during a conversion
 * between one model to another. The class can build on prior exceptions,
 * creating a list of error messages.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:33:26 $
 */
public class FormatException extends Exception {
    /** Text used for an empty format exception message. */
    static public final String FORMAT_EXCEPTION = "Format exception";

    /**
     * Constructs a format exception with prior exception and a given formatted
     * message. Calls {@link String#format(String, Object[])} with the message
     * and parameters, and inserts both the prior exceptions messages and the
     * resulting test in the message list.
     * @see #getErrors()
     */
    public FormatException(Exception exc, String message, Object... parameters) {
        super(String.format(message, parameters));
        this.errors = createMessageList();
        if (exc instanceof FormatException) {
            this.errors.addAll(((FormatException) exc).getErrors());
        } else if (exc != null) {
            this.errors.add(exc.getMessage());
        }
        this.errors.add(super.getMessage());
    }

    /**
     * Constructs a format exception with a given formatted message and no prior
     * exception.
     * @see #FormatException(Exception, String, Object[])
     */
    public FormatException(String message, Object... parameters) {
        this(null, message, parameters);
    }

    /**
     * Constructs a format exception from another exception.
     * @see java.lang.Exception#Exception(java.lang.Throwable)
     */
    public FormatException(Exception exc) {
        super(exc);
        this.errors = createMessageList();
        this.errors.add(exc.getMessage());
    }

    /** Constructs a format exception based on a given list of errors. */
    public FormatException(List<String> errors) {
        this.errors = new ArrayList<String>(errors);
    }

    /**
     * Constructs a format exception based on a given set of errors. The order
     * of the errors is determined by the set iterator.
     */
    public FormatException(Set<String> errors) {
        this.errors = new ArrayList<String>(errors);
    }

    /**
     * Constructs a format exception with an empty message.
     */
    public FormatException() {
        this(FORMAT_EXCEPTION);
    }

    /**
     * Inserts the error messages of a prior exception before the already stored
     * messages.
     */
    public void insert(FormatException prior) {
        if (prior != null) {
            this.errors.addAll(0, prior.getErrors());
        }
    }

    /** Returns a list of error messages collected in this exception. */
    public List<String> getErrors() {
        return Collections.unmodifiableList(this.errors);
    }

    /** Combines the list of error messages collected in this exception. */
    @Override
    public String getMessage() {
        return Groove.toString(getErrors().toArray(), "", "", "\n");
    }

    /**
     * Callback factory method for an empty list of error messages.
     */
    protected List<String> createMessageList() {
        return new LinkedList<String>();
    }

    /** List of error messages carried around by this exception. */
    private final List<String> errors;
}

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
 * $Id: FormatException.java,v 1.2 2007-04-19 06:39:25 rensink Exp $
 */
package groove.util;

import java.util.LinkedList;
import java.util.List;

/**
 * General exception class signalling a format error found during a
 * conversion between one model to another.
 * The class can build on prior exceptions, creating a list of error messages.
 * @author Arend Rensink
 * @version $Revision: 1.2 $ $Date: 2007-04-19 06:39:25 $
 */
public class FormatException extends Exception {
	/** Text used for an empty format exception message. */
	static public final String FORMAT_EXCEPTION = "Format exception"; 
	
    /**
     * Constructs a format exception with prior exception and a given formatted message.
     * Calls {@link String#format(String, Object[])} with the message and
     * parameters, and inserts both the prior exceptions messages and the resulting
     * test in the message list.
     * @see #getMessageList()
     */
    public FormatException(Exception exc, String message, Object... parameters) {
        super(String.format(message, parameters));
        if (exc == null) {
        	messages = createMessageList();
        } else if (exc instanceof FormatException) {
        	messages = ((FormatException) exc).getMessageList();
        } else {
        	messages = createMessageList();
        	messages.add(exc.getMessage());
        }
    	messages.add(super.getMessage());
    }

    /**
     * Constructs a format exception with a given formatted message and no prior exception.
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
        messages = createMessageList();
        messages.add(exc.getMessage());
    }
    
    /**
	 * Constructs a format exception with an empty message.
	 */
	public FormatException() { 
		this(FORMAT_EXCEPTION);
	}

	/** Inserts the error messages of a prior exception before the already stored messages. */
	public void insert(FormatException prior) {
		if (prior != null) {
			messages.addAll(0, prior.getMessageList());
		}
	}

	/** Returns a list of error messages collected in this exception. */
    public List<String> getMessageList() {
    	return messages;
    }

	/** Combines the list of error messages collected in this exception. */
    @Override
    public String getMessage() {
    	return Groove.toString(messages.toArray(), "", "", "\n");
    }
    
    /**
     * Callback factory method for an empty list of error messages.
     */
    protected List<String> createMessageList() {
    	return new LinkedList<String>();
    }
    
    /** List of error messages carried around by this exception. */
    private final List<String> messages;
}

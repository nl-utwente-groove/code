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
 * $Id: PriorityFileName.java,v 1.2 2007-03-28 15:12:32 rensink Exp $
 */
package groove.io;

import groove.trans.Rule;

/**
 * Encoding of a rule name plus priority as a string
 * @author Arend Rensink
 * @version $Revision: 1.2 $
 */
class PriorityFileName {
    /** Default priority value, copied from {@link Rule#DEFAULT_PRIORITY}. */
    static public final int DEFAULT_PRIORITY = Rule.DEFAULT_PRIORITY;
    static private String SEPARATOR = ".";
    
    /** 
     * Parses a string as <tt>priority.ruleName</tt>.
     * If there is no period in the string, the priority is assumed to be 0.
     * @throws NumberFormatException if the part before the period cannot be parsed as an integer,
     * or yields a negative number.
     */
    PriorityFileName(String fullName) {
        int separatorPos = fullName.indexOf(SEPARATOR);
        if (separatorPos <= 0) {
            priority = DEFAULT_PRIORITY;
            explicitPriority = false;
        } else {
        	int startNumber;
            boolean validStartNumber;
        	try {
        		startNumber = new Integer(fullName.substring(0,separatorPos));
                validStartNumber = true;
        	} catch (NumberFormatException nfe) {
        		startNumber = DEFAULT_PRIORITY;
        		validStartNumber = false;
        	}
        	priority = startNumber;
            explicitPriority = validStartNumber;
            if (priority < 0) {
                throw new NumberFormatException("Invalid rule priority "+priority);
//            } else if (priority == DEFAULT_PRIORITY) {
//                throw new NumberFormatException("Default priority  "+priority+" should not be included in file name");
            }
        }
        ruleName = fullName.substring(separatorPos+1);
    }

    /**
     * Creates a file name from a given rule name and priority.
     * The priority is taken to be explicit if and only if it does not equal
     * {@link #DEFAULT_PRIORITY}.
     */
    PriorityFileName(String ruleName, int priority) {
        this.ruleName = ruleName;
        this.priority = priority;
        this.explicitPriority = priority != DEFAULT_PRIORITY;
    }
    
    /**
     * Returns the priority.
     */
    public Integer getPriority() {
        return new Integer(priority);
    }

    /**
     * Returns the rule name.
     */
    public String getRuleName() {
        return ruleName;
    }

    /**
     * Returns the string from which this object was created.
     */
    @Override
    public String toString() {
        if (explicitPriority) {
            return ""+priority+SEPARATOR+ruleName;
        } else {
            return ruleName;
        }
    }
    
    /** 
     * Flag that indicates if the string from which this object was
     * created contained an explicit priority.
     */
    private final boolean explicitPriority;
    private final int priority;
    private final String ruleName;
}

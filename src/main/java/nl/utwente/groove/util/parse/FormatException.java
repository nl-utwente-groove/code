/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.util.parse;

import org.antlr.runtime.RecognitionException;

import nl.utwente.groove.util.Groove;

/**
 * General exception class signalling a format error found during a conversion
 * between one model to another. The class can build on prior exceptions,
 * creating a list of error messages.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:33:26 $
 */
public class FormatException extends Exception {
    /**
     * Constructs a format exception with a given formatted
     * message. Calls {@link String#format(String, Object[])} with the message
     * and parameters, and inserts both the prior exceptions messages and the
     * resulting test in the message list.
     * @see #getErrors()
     */
    public FormatException(String message, Object... parameters) {
        super(String.format(message, parameters));
        this.errors = new FormatErrorSet(message, parameters);
    }

    /**
     * Constructs a format exception based on a given set of errors. The order
     * of the errors is determined by the set iterator.
     */
    public FormatException(FormatErrorSet errors) {
        this.errors = errors;
    }

    /** Constructs a format exception from a format error. */
    public FormatException(FormatError err) {
        this.errors = new FormatErrorSet();
        this.errors.add(err);
    }

    /** Constructs a format exception from an (ANTLR) recognition exception. */
    public FormatException(RecognitionException exc) {
        this(exc.getMessage(), exc.line, exc.charPositionInLine);
    }

    /**
     * Inserts the error messages of a prior exception before the already stored
     * messages.
     */
    public void insert(FormatException prior) {
        if (prior != null) {
            this.errors.addAll(prior.getErrors());
        }
    }

    /** Returns a list of error messages collected in this exception. */
    public FormatErrorSet getErrors() {
        return this.errors;
    }

    /** Combines the list of error messages collected in this exception. */
    @Override
    public String getMessage() {
        return Groove.toString(getErrors().toArray(), "", "", "\n");
    }

    /** List of error messages carried around by this exception. */
    private final FormatErrorSet errors;
}

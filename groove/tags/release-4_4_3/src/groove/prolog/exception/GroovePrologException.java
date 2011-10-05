/*
 * Groove Prolog Interface
 * Copyright (C) 2009 Michiel Hendriks, University of Twente
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package groove.prolog.exception;

import groove.prolog.PrologEngine;

/**
 * A wrapper exception thrown by the {@link PrologEngine}
 *
 * @author Michiel Hendriks
 */
public class GroovePrologException extends Exception {
    private static final long serialVersionUID = -2518965928379660623L;

    /**
     * No args constructor
     */
    public GroovePrologException() {
        /**
         * Left blank by design
         */
    }

    /**
     * @param message   A message
     */
    public GroovePrologException(String message) {
        super(message);
    }

    /**
     * @param wrapped   A throwable
     */
    public GroovePrologException(Throwable wrapped) {
        super(wrapped);
    }

    /**
     * @param message           A message
     * @param throwable   A throwable
     */
    public GroovePrologException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

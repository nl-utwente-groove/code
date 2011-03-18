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

import groove.prolog.PrologQuery;

/**
 * A wrapper exception thrown by the {@link PrologQuery}
 * 
 * @author Michiel Hendriks
 */
public class GroovePrologException extends Exception {
    private static final long serialVersionUID = -2518965928379660623L;

    /**
     * TODO
     */
    public GroovePrologException() {
        /**
         * TODO
         */
    }

    /**
     * @param arg0 TODO
     */
    public GroovePrologException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0 TODO
     */
    public GroovePrologException(Throwable arg0) {
        super(arg0);
    }

    /**
     * @param arg0 TODO
     * @param arg1 TODO
     */
    public GroovePrologException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }
}

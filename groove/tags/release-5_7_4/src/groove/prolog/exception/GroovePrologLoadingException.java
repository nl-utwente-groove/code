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

import gnu.prolog.database.PrologTextLoaderError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An exception thrown when there was an error loading the prolog files
 * 
 * @author Michiel Hendriks
 */
public class GroovePrologLoadingException extends GroovePrologException {
    private static final long serialVersionUID = -657457775489441336L;

    /**
     * A list of PrologTestLoaderError
     */
    protected List<PrologTextLoaderError> errors;

    /**
     * Construct a GroovePrologLoadingException
     * @param loadingErrors     A list of PrologTextLoaderError
     */
    public GroovePrologLoadingException(
            List<PrologTextLoaderError> loadingErrors) {
        this.errors = new ArrayList<>(loadingErrors);
    }

    /**
     * @return A list of PrologTestLoaderError
     */
    public List<PrologTextLoaderError> getLoadingErrors() {
        return Collections.unmodifiableList(this.errors);
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        for (PrologTextLoaderError error : this.errors) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(error.toString());
        }
        return sb.toString();
    }
}

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
package nl.utwente.groove.prolog;

import gnu.prolog.vm.PrologCode;

/**
 * The status of the current query result
 *
 * @author Michiel Hendriks
 */
public enum QueryReturnValue {
    /**
     * The query has not run.
     */
    NOT_RUN,
    /**
     * The query returned successfully, but there might be more solutions.
     */
    SUCCESS,
    /**
     * The query returned successfully, and this is the last solution.
     */
    SUCCESS_LAST,
    /**
     * The query failed.
     */
    FAIL,
    /**
     * The interpreter was halted.
     */
    HALT;

    /**
     * Return the correct enum value based on the PrologCode constants
     */
    public static QueryReturnValue fromInt(int value) {
        return switch (value) {
        case PrologCode.FAIL -> FAIL;
        case PrologCode.SUCCESS -> SUCCESS;
        case PrologCode.SUCCESS_LAST -> SUCCESS_LAST;
        case PrologCode.HALT -> HALT;
        default -> throw new IllegalArgumentException(
            String.format("Unknown return value %d", value));

        };
    }
}

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
package groove.explore.strategy;

/**
 * A graph exploration strategy which uses prolog query to determine the steps
 * to take.
 * 
 * @author Michiel Hendriks
 */
public interface PrologStrategy extends Strategy {
    /**
     * Set the prolog query to execute at each step.
     * 
     * @param resultTerm
     *            The term name that will contain the results to use in the
     *            strategy.
     * @param query
     *            The query to execute.
     * 
     * @return TODO
     */
    boolean setPrologQuery(String resultTerm, String query);

    /**
     * Set the prolog query to execute at each step, and the usercode which
     * needs to be included in the prolog environment.
     * 
     * @param resultTerm   TODO
     * @param query        TODO
     * @param usercode
     *            Additional prolog predicates which are used in the query.
     * @return TODO
     */
    boolean setPrologQuery(String resultTerm, String query, String usercode);
}

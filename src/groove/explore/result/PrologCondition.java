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
package groove.explore.result;

import groove.lts.GraphState;
import groove.prolog.GrooveState;
import groove.prolog.PrologEngine;
import groove.prolog.QueryReturnValue;
import groove.prolog.exception.GroovePrologException;
import groove.prolog.exception.GroovePrologLoadingException;

/**
 * The condition is satisfied when the prolog clause unifies (either a single
 * result, or multiple results).
 * 
 * @author Michiel Hendriks
 */
public class PrologCondition extends ExploreCondition<String> {
    private PrologEngine engine;

    /**
     * User defined predicates
     */
    protected String usercode;

    /**
     * Set user defined predicates
     * 
     * @param usercode
     *            the usercode to set
     */
    public void setUsercode(String usercode) {
        if (usercode != null) {
            usercode = usercode.trim();
            if (usercode.length() == 0) {
                usercode = null;
            }
        }
        this.usercode = usercode;
        this.engine = null;
    }

    /*
     * (non-Javadoc)
     * @see
     * groove.explore.result.ExploreCondition#setCondition(java.lang.Object)
     */
    @Override
    public void setCondition(String condition) {
        if (condition != null) {
            condition = condition.trim();
            if (condition.length() == 0) {
                condition = null;
            }
        }
        super.setCondition(condition);
    }

    /*
     * (non-Javadoc)
     * @see groove.util.Property#isSatisfied(java.lang.Object)
     */
    @Override
    public boolean isSatisfied(GraphState value) {
        if (this.condition == null) {
            return true;
        }
        try {
            if (this.engine == null) {
                initProlog();
            }
            this.engine.setGrooveState(new GrooveState(null, null, value, null));
            this.engine.newQuery(this.condition);
            // the graphstate is accepted when the prolog query succeeds
            return this.engine.lastReturnValue() == QueryReturnValue.SUCCESS
                || this.engine.lastReturnValue() == QueryReturnValue.SUCCESS_LAST;
        } catch (GroovePrologException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Initialize the prolog environment
     * 
     * @throws GroovePrologLoadingException TODO
     */
    protected void initProlog() throws GroovePrologLoadingException {
        this.engine = PrologEngine.instance();
        if (this.usercode != null && this.usercode.length() > 0) {
            this.engine.init(this.usercode);
        }
    }
}

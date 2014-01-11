/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.control.symbolic;

import java.util.Collections;
import java.util.List;

/**
 * Successful termination.
 * @author Arend Rensink
 * @version $Revision $
 */
public class EpsilonTerm extends Term {
    /**
     * Constructs an epsilon term.
     */
    public EpsilonTerm(TermPool pool) {
        super(pool, Term.Op.EPSILON);
    }

    @Override
    protected List<OutEdge> computeOutEdges() {
        return Collections.emptyList();
    }

    @Override
    protected Term computeSuccess() {
        return null;
    }

    @Override
    protected Term computeFailure() {
        return null;
    }

    @Override
    protected int computeTransitDepth() {
        return 0;
    }

    @Override
    protected boolean computeFinal() {
        return true;
    }

    @Override
    public boolean hasClearFinal() {
        return true;
    }

    @Override
    public Term seq(Term arg1) {
        return arg1;
    }

    @Override
    public Term transit() {
        return this;
    }

    @Override
    public Term atom() {
        return this;
    }

    @Override
    public Term whileDo() {
        return delta();
    }

    @Override
    public Term ifElse(Term arg1) {
        return this;
    }
}

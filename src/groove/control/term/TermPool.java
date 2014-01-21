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
package groove.control.term;

import java.util.HashMap;
import java.util.Map;

/**
 * Pool of normalised terms, used to ensure that equal terms are reused.
 * @author Arend Rensink
 * @version $Revision $
 */
public class TermPool {
    /**
     * Normalises a given (non-{@code null}) term.
     * @return a normalised term equal to the argument
     */
    public Term normalise(Term term) {
        Term result = this.pool.get(term);
        if (result == null) {
            result = term;
            this.pool.put(result, result);
        }
        return result;
    }

    private Map<Term,Term> pool = new HashMap<Term,Term>();
}

/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
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
 * $Id: AbstrGraphState.java,v 1.1 2007-11-28 15:35:50 iovka Exp $
 */
package groove.abs.lts;

import java.util.Iterator;
import java.util.Set;

import groove.abs.AbstrGraph;
import groove.lts.GraphState;
import groove.lts.GraphTransition;

/**
 * The abstract version of a {@link GraphState}
 * 
 * @author Iovka Boneva
 * @version $Revision $
 */
public interface AbstrGraphState extends GraphState {

    /**
     * @ensure Elements of the result iterator are of type
     *         {@link AbstrGraphTransition}
     */
    public Iterator<GraphTransition> getTransitionIter();

    /**
     * @ensure Elements of the result set are of type
     *         {@link AbstrGraphTransition}
     */
    public Set<GraphTransition> getTransitionSet();

    /** Specialises return type. */
    public AbstrGraph getGraph();

    /** Returns true if the transitions set is empty */
    public boolean isWithoutOutTransition();

    /**
     * @param o
     * @return <code>true</code> if o is of type AbstrGraphState and
     *         this.getGraph().equals(o.getGraph())
     */
    public boolean equals(Object o);
}

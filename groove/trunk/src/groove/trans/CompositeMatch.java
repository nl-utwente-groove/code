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
 * $Id: CompositeMatch.java,v 1.6 2008-01-30 09:32:37 iovka Exp $
 */
package groove.trans;

import java.util.Collection;

/**
 * The match of a {@link ForallCondition} or {@link NotCondition}.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CompositeMatch extends AbstractMatch {
    @SuppressWarnings("unchecked")
    @Override
    public Collection<CompositeMatch> addSubMatchChoice(
            Iterable<? extends Match> choices) {
        return (Collection<CompositeMatch>) super.addSubMatchChoice(choices);
    }

    /** Callback factory method for a cloned match. */
    @Override
    protected CompositeMatch createMatch() {
        return new CompositeMatch();
    }
}

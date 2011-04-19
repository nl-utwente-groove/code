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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * The match of a {@link ForallCondition} or {@link NotCondition}.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CompositeMatch extends AbstractMatch {
    /** Constructs a match object for a given condition. */
    public CompositeMatch(Condition condition) {
        this.condition = condition;
    }

    /**
     * Returns a set of copies of this composite match, each augmented with an
     * additional rule match taken from a given set of choices. For efficiency,
     * the last match in the result is actually a (modified) alias of this
     * object, meaning that no references to this object should be kept after
     * invoking this method.
     */
    public Collection<CompositeMatch> addMatchChoice(Iterable<RuleMatch> choices) {
        Collection<CompositeMatch> result = new ArrayList<CompositeMatch>();
        Iterator<RuleMatch> choiceIter = choices.iterator();
        while (choiceIter.hasNext()) {
            RuleMatch choice = choiceIter.next();
            CompositeMatch copy = choiceIter.hasNext() ? clone() : this;
            copy.getSubMatches().add(choice);
            result.add(copy);
        }
        return result;
    }

    @Override
    protected CompositeMatch clone() {
        return (CompositeMatch) super.clone();
    }

    /** Callback factory method for a cloned match. */
    @Override
    protected CompositeMatch createMatch() {
        return new CompositeMatch(this.condition);
    }

    /**
     * Returns the {@link ForallCondition} or {@link NotCondition} for which
     * this is a match.
     */
    public Condition getCondition() {
        return this.condition;
    }

    private final Condition condition;
}

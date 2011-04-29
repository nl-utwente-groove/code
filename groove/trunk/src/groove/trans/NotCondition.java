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
 * $Id: NotCondition.java,v 1.6 2007-11-29 12:52:08 rensink Exp $
 */
package groove.trans;

/**
 * A negative graph condition, which tests against the existence of a graph
 * structure. A negative condition has no sub-conditions and returns an (empty)
 * match if and only if the matched graph does not exist.
 * @author Arend Rensink
 * @version $Revision $
 */
public class NotCondition extends Condition {
    /**
     * Creates a NAC over a default root graph and an initially empty target
     * pattern.
     */
    public NotCondition(RuleGraph pattern, SystemProperties properties) {
        super(new RuleName(pattern.getName()), pattern, null, properties);
    }

    /**
     * Adding sub-conditions is not allowed and will give rise to an exception.
     * @throws UnsupportedOperationException always.
     */
    @Override
    final public void addSubCondition(Condition condition) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Mode getMode() {
        return Mode.NOT;
    }
}

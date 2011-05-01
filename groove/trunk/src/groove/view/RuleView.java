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
 * $Id: RuleView.java,v 1.5 2008-01-30 09:33:26 iovka Exp $
 */
package groove.view;

import groove.graph.TypeGraph;
import groove.trans.Rule;
import groove.trans.SystemProperties;

/**
 * Interface for a graphical view upon a transformation rule.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface RuleView extends View<Rule>, Comparable<RuleView> {
    /**
     * Sets new properties for the rule view. This will typically require the
     * rule to be rebuilt.
     */
    public void setSystemProperties(SystemProperties properties);

    /** Changes the type graph under against which the model should be tested. */
    public void setType(TypeGraph type);

    /**
     * Returns the rule of which this is a view. The rule is possibly
     * constructed in the course of this method. This is just a convenience
     * method for {@link View#toModel()}.
     * @throws FormatException if there are syntax errors in the view
     */
    public Rule toRule() throws FormatException;

    /**
     * Returns the name of the rule of which this is a view. Yields the same
     * result as <code>toRule().getName().name()</code>.
     */
    public String getName();

    /**
     * Returns the priority of the rule of which this is a view. Yields the same
     * result as <code>toRule().getPriority()</code>.
     */
    public int getPriority();

    /**
     * Indicates whether the rule is enabled, i.e., will be part of an actual
     * graph grammar constructed from the rule.
     */
    public boolean isEnabled();

    /**
     * Indicates whether the rule is marked as locally confluent. If the rule is
     * marked as such, only match will be chosen among this and all other
     * locally confluent rules.
     */
    public boolean isConfluent();
}

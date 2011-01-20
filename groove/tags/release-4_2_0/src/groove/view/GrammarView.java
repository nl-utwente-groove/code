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
 * $Id$
 */
package groove.view;

import groove.control.ControlView;
import groove.view.aspect.AspectGraph;

/**
 * Instantiation of the generic grammar view with aspectual views.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface GrammarView extends
        GenericGrammarView<GraphView,RuleView,TypeView,ControlView> {
    /**
     * Sets the start graph to a given graph, or to <code>null</code>. This
     * implies the start graph is not one of the graphs stored in the rule
     * system; correspondingly, the start graph name is set to <code>null</code>
     * .
     * @param startGraph the new start graph; if <code>null</code>, the start
     *        graph is unset
     * @throws IllegalArgumentException if <code>startGraph</code> does not have
     *         a graph role
     * @see #setStartGraph(String)
     */
    public void setStartGraph(AspectGraph startGraph);
}

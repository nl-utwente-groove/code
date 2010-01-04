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
 * $Id: GrammarView.java,v 1.5 2008-01-30 09:33:25 iovka Exp $
 */
package groove.view;

import groove.control.ControlView;
import groove.graph.Graph;
import groove.trans.GraphGrammar;
import groove.trans.RuleName;
import groove.trans.SystemProperties;

import java.util.Set;

/**
 * Interface encapsulating a representation of a rule system that is essentially
 * a set of rule views, available as a map from names to views. The view as a
 * whole has a name and a set of properties.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface GenericGrammarView<GV extends View<Graph>,RV extends RuleView,TV extends View<Graph>,CV> {
    /** Returns the name of the rule system. */
    public String getName();

    /** Returns the (fixed) properties of the rule system. */
    public SystemProperties getProperties();

    /** Returns a list of all available control program names. */
    public Set<String> getControlNames();

    /** Returns an unmodifiable view on the set of graph names in this grammar. */
    public Set<String> getGraphNames();

    /** Returns an unmodifiable view on the set of rule names in this grammar. */
    public Set<RuleName> getRuleNames();

    /**
     * Returns an unmodifiable view on the set of type graph names in this
     * grammar.
     */
    public Set<String> getTypeNames();

    /**
     * Returns the rule view for a given rule name.
     * @return the rule view for rule <code>name</code>, or <code>null</code> if
     *         there is no such rule.
     */
    public RV getRuleView(RuleName name);

    /**
     * Returns the graph view for a given graph name.
     * @return the graph view for graph <code>name</code>, or <code>null</code>
     *         if there is no such graph.
     */
    public GV getGraphView(String name);

    /**
     * Returns the type graph view for a given graph name.
     * @return the type graph view for type <code>name</code>, or
     *         <code>null</code> if there is no such graph.
     */
    public TV getTypeView(String name);

    /**
     * Returns the type graph view set for the grammar.
     * @return the type graph view for the grammar, or <code>null</code> if
     *         there is no type graph set.
     */
    public TV getTypeView();

    /**
     * Returns the control view associated with a given (named) control program.
     * @param name the name of the control program to return the view of;
     * @return the corresponding control program view, or <code>null</code> if
     *         no program by that name exists
     */
    public ControlView getControlView(String name);

    /**
     * Returns the control view set for the grammar.
     * @return the control view for the grammar, or <code>null</code> if there
     *         is no control program loaded.
     */
    public CV getControlView();

    /**
     * Returns the start graph of this grammar view.
     * @return the start graph view, or <code>null</code> if no start graph is
     *         set.
     */
    public GV getStartGraphView();

    /**
     * Returns the name of the start graph, if it is one of the graphs stored
     * with the rule system.
     * @return the name of the start graph, or <code>null</code> if the start
     *         graph is not one of the graphs stored with the rule system
     */
    public String getStartGraphName();

    /**
     * Sets the name of the start graph. The name should correspond with one of
     * the graphs stored with the rule system.
     * @param name either a graph name within the current grammar; or
     *        <code>null</code> if the start graph should be unset
     */
    public void setStartGraph(String name);

    /** Unsets the start graph. */
    public void removeStartGraph();

    /**
     * Lazily converts the view to a fixed rule system. This may throw an
     * exception if the view has errors.
     * @return a rule system based on the name, properties and rules stored as
     *         views
     * @throws FormatException
     */
    public GraphGrammar toGrammar() throws FormatException;
}

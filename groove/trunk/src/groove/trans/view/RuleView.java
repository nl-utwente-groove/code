/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
 * $Id: RuleView.java,v 1.1.1.2 2007-03-20 10:42:57 kastenberg Exp $
 */
package groove.trans.view;

import groove.trans.NameLabel;
import groove.trans.Rule;

/**
 * Interface for a graphical view upon a transformation rule.
 * Currently the only implemented view is the {@link RuleGraph}, which provides
 * a monotithic vew in which all the elements of the rule are part of one graph.
 * Note that the class offers functionality to construct a rule from an existing view,
 * as well as a view of an existing rule,
 * so in fact this class acts as a bridge between an input tool for rules and the
 * {@link Rule} representation.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public interface RuleView {
    /**
     * Returns the rule of which this is a view.
     * The rule is possibly constructed in the course of this method.
     */
    public Rule toRule();

    /** 
     * Returns the name of the rule of which this is a view. 
     * Yields the same result as <code>toRule().getName()</code>.
     */
    public NameLabel getName();

    /** 
     * Returns the priority of the rule of which this is a view. 
     * Yields the same result as <code>toRule().getPriority()</code>.
     */
    public int getPriority();
    
    /**
     * Factory method for a rule view of a given rule.
     * @throws ViewFormatException if <code>rule</code> cannot be visualised
     * in the current rule view format
     */
    public RuleView newInstance(Rule rule) throws ViewFormatException;
//
//    /**
//     * Factory method for a rule view of a given graph.
//     * @throws GraphFormatException if <code>graph</code> does not give rise to a valid rule
//     */
//    public RuleView newInstance(GraphShape graph, NameLabel name, int priority) throws GraphFormatException;
}

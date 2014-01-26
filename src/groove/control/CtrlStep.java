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
package groove.control;

import groove.control.instance.Frame;
import groove.control.instance.Step;
import groove.grammar.Recipe;
import groove.grammar.Rule;
import groove.graph.Label;

/**
 * Temporary interface to unify {@link CtrlTransition} and {@link Step}.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface CtrlStep extends Comparable<CtrlStep> {
    /** Returns the rule invoked in this control step. */
    Rule getRule();

    /** 
     * Indicates if this is a partial step.
     * A step is partial if it has an enclosing recipe.
     */
    boolean isPartial();

    /** Returns the outermost recipe of which this step is a part, if any. */
    Recipe getRecipe();

    /** Returns the label of this control step. */
    Label label();

    /** 
     * Indicates if the step may cause modifications in the control state.
     * This is the case if the (prime) source and target of this step differ,
     * or the call has out-parameters.
     */
    boolean isModifying();

    /** Convenience method to return the target variable binding of the switch of this step. */
    public Binding[] getTargetBinding();

    /** Convenience method to return the call parameter binding of the switch of this step. */
    public Binding[] getCallBinding();

    /** Method returning supertype of {@link CtrlState} and {@link Frame}. */
    public CtrlFrame target();
}

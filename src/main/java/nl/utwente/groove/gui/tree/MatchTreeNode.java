/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.gui.tree;

import java.util.function.Supplier;

import javax.swing.Icon;

import nl.utwente.groove.control.Assignment;
import nl.utwente.groove.control.instance.Step;
import nl.utwente.groove.control.template.Switch;
import nl.utwente.groove.grammar.Callable.Kind;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.SimulatorModel;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.util.Factory;

/**
 * @author Arend Rensink
 * @version $Revision$
 */
abstract class MatchTreeNode extends DisplayTreeNode {
    /** Retrieves the (input) arguments of a recipe match
     * from the source state and initial step of the recipe.
     */
    protected static HostNode[] getRecipeArgs(SimulatorModel model, GraphState source,
                                              Step initStep) {
        // the transition has at least one argument
        var sourceFrame = initStep.getSource();
        Object[] stack = source.getFrameStack(sourceFrame);
        // construct the parameter assignment from the source frame of the initial step
        var assign = Assignment.identity(sourceFrame.getVars());
        for (Switch swt : initStep.getSwitch()) {
            if (swt.getKind() == Kind.RECIPE) {
                assign = swt.assignSource2Par().after(assign);
                break;
            } else {
                assign = swt.assignSource2Init().after(assign);
            }
        }
        var valuator = model.getGTS().getRecord().getValuator();
        return valuator.eval(assign, stack);
    }

    /**
     * Converts an array of argument host nodes to a string description.
     */
    protected static String toArgsString(HostNode[] args) {
        StringBuilder result = new StringBuilder();
        result.append('(');
        for (int i = 0; i < args.length; i++) {
            var arg = args[i];
            result
                .append(arg == null
                    ? "_"
                    : arg.toString());
            if (i < args.length - 1) {
                result.append(',');
            }
        }
        result.append(')');
        return result.toString();
    }

    /** Creates a node containing an (optionally explored) match of a rule or recipe. */
    MatchTreeNode(SimulatorModel simulator, GraphState source, int nr) {
        super(simulator, false);
        this.source = source;
        this.nr = nr;
    }

    /** Returns the simulator model. */
    SimulatorModel getSimulator() {
        return (SimulatorModel) getUserObject();
    }

    /** Returns the source state of the match wrapped by this node. */
    GraphState getSource() {
        return this.source;
    }

    private final GraphState source;

    /** Returns the sequence number of this match node. */
    int getNumber() {
        return this.nr;
    }

    private final int nr;

    @Override
    public String getText() {
        return this.text.get();
    }

    private final Supplier<String> text = Factory.lazy(this::computeText);

    /** Computes the text for the node. */
    abstract String computeText();

    @Override
    public Icon getIcon() {
        return Icons.GRAPH_MATCH_ICON;
    }

    /** HTML representation of an arrow tail. */
    static final String ARROW_TAIL = "--";
    /** HTML representation of the right arrow. */
    static final String RIGHTARROW = "-->";
}

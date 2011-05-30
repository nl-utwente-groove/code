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
 * $Id: RulePanel.java,v 1.21 2008-01-30 09:33:35 iovka Exp $
 */
package groove.gui;

import static groove.gui.Options.SHOW_ANCHORS_OPTION;
import static groove.gui.Options.SHOW_ASPECTS_OPTION;
import static groove.gui.Options.SHOW_NODE_IDS_OPTION;
import static groove.gui.Options.SHOW_REMARKS_OPTION;
import static groove.gui.Options.SHOW_VALUE_NODES_OPTION;
import groove.graph.GraphProperties;
import groove.graph.GraphRole;
import groove.graph.LabelStore;
import groove.graph.TypeGraph;
import groove.gui.SimulatorModel.Change;
import groove.gui.dialog.ErrorDialog;
import groove.gui.jgraph.AspectJGraph;
import groove.io.HTMLConverter;
import groove.trans.Rule;
import groove.trans.RuleElement;
import groove.trans.SystemProperties;
import groove.util.Groove;
import groove.view.FormatException;
import groove.view.RuleView;
import groove.view.StoredGrammarView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import java.util.Set;

import javax.swing.JToolBar;

/**
 * Window that displays and controls the current rule graph. Auxiliary class for
 * Simulator.
 * @author Arend Rensink
 * @version $Revision$
 */
final public class RulePanel extends JGraphPanel<AspectJGraph> implements
        SimulatorListener {
    /** Frame name when no rule is selected. */
    private static final String INITIAL_FRAME_NAME = "No rule selected";

    /**
     * Constructs a new rule frame on the basis of a given graph.
     */
    public RulePanel(final Simulator simulator) {
        super(new AspectJGraph(simulator, GraphRole.RULE), false);
        initialise();
    }

    @Override
    protected JToolBar createToolBar() {
        return null;
    }

    @Override
    protected TabLabel createTabLabel() {
        return new TabLabel(this, Icons.RULE_MODE_ICON, "");
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        getSimulatorModel().addListener(this, Change.GRAMMAR, Change.RULE);
        addRefreshListener(SHOW_ANCHORS_OPTION);
        addRefreshListener(SHOW_ASPECTS_OPTION);
        addRefreshListener(SHOW_NODE_IDS_OPTION);
        addRefreshListener(SHOW_REMARKS_OPTION);
        addRefreshListener(SHOW_VALUE_NODES_OPTION);
        getJGraph().setToolTipEnabled(true);
        getJGraph().getLabelTree().addLabelStoreObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                assert arg instanceof LabelStore;
                SystemProperties newProperties =
                    getGrammar().getProperties().clone();
                newProperties.setSubtypes(((LabelStore) arg).toDirectSubtypeString());
                try {
                    getSimulator().getModel().doSetProperties(newProperties);
                } catch (IOException exc) {
                    new ErrorDialog(RulePanel.this,
                        "Error while modifying type hierarchy", exc).setVisible(true);
                }
            }
        });
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        if (changes.contains(Change.GRAMMAR)) {
            setGrammarUpdate(source.getGrammar());
        }
    }

    /**
     * Sets the frame to a given rule system. Resets the display, and creates
     * and stores a model for each rule in the system.
     */
    private synchronized void setGrammarUpdate(StoredGrammarView grammar) {
        // create a mapping from rule names to (fresh) rule models
        if (grammar != null) {
            // reset the graph model so it doesn't get mixed up with the new type
            getJGraph().setModel(null);
            // set either the type or the label store of the associated JGraph
            TypeGraph type;
            try {
                type = grammar.toModel().getType();
            } catch (FormatException e) {
                type = null;
            }
            if (type == null) {
                getJGraph().setLabelStore(grammar.getLabelStore());
            } else {
                getJGraph().setType(type, null);
            }
        }
    }

    /**
     * Returns the rule description of the currently selected rule, if any.
     */
    @Override
    protected String getStatusText() {
        StringBuilder text = new StringBuilder();
        RuleView view = getSimulatorModel().getRule();
        if (view != null) {
            text.append("Rule ");
            text.append(HTMLConverter.STRONG_TAG.on(view.getName()));
            try {
                Rule rule = view.toRule();
                if (getOptionsItem(SHOW_ANCHORS_OPTION).isSelected()) {
                    text.append(", anchor ");
                    text.append(getAnchorString(rule));
                }
            } catch (FormatException exc) {
                // don't add the anchor
            }
            String remark = GraphProperties.getRemark(view.getAspectGraph());
            if (remark != null) {
                text.append(": ");
                text.append(HTMLConverter.toHtml(remark));
            }
        } else {
            text.append(INITIAL_FRAME_NAME);
        }
        return HTMLConverter.HTML_TAG.on(text).toString();
    }

    /** Returns a string description of the anchors of a given rule. */
    private String getAnchorString(Rule rule) {
        if (!rule.hasSubRules()) {
            return toAnchorString(rule);
        } else {
            List<String> result = new ArrayList<String>();
            // collect all subrules
            Queue<Rule> ruleQueue = new LinkedList<Rule>();
            ruleQueue.add(rule);
            while (!ruleQueue.isEmpty()) {
                Rule next = ruleQueue.poll();
                result.add(next.getName() + toAnchorString(next));
                ruleQueue.addAll(next.getSubRules());
            }
            return Groove.toString(result.toArray());
        }
    }

    /** Constructs a string listing all anchors of a given rule. */
    private String toAnchorString(Rule rule) {
        List<RuleElement> anchor = new ArrayList<RuleElement>();
        anchor.addAll(Arrays.asList(rule.getAnchorNodes()));
        anchor.addAll(Arrays.asList(rule.getAnchorEdges()));
        return Groove.toString(anchor.toArray(), "(", ")", ",");
    }

    /** Convenience method to retrieve the current grammar view. */
    private StoredGrammarView getGrammar() {
        return getSimulatorModel().getGrammar();
    }

}

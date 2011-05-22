package groove.gui.action;

import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.view.RuleView;
import groove.view.aspect.AspectGraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Action that raises the priority of a selected set of rules.
 */
public class RaisePriorityAction extends SimulatorAction {
    /** Constructs an instance of the action for a given simulator. */
    public RaisePriorityAction(Simulator simulator) {
        super(simulator, Options.RAISE_PRIORITY_ACTION_NAME, null);
    }

    @Override
    public void refresh() {
        boolean ruleSelected = getModel().getRule() != null;
        setEnabled(ruleSelected && getModel().getStore().isModifiable());
    }

    @Override
    public boolean execute() {
        boolean result = false;
        // collect all rules according to current priority
        SortedMap<Integer,Set<AspectGraph>> rulesMap =
            new TreeMap<Integer,Set<AspectGraph>>();
        for (AspectGraph ruleGraph : getModel().getStore().getRules().values()) {
            int priority = GraphProperties.getPriority(ruleGraph);
            Set<AspectGraph> cell = rulesMap.get(priority);
            if (cell == null) {
                rulesMap.put(priority, cell = new HashSet<AspectGraph>());
            }
            cell.add(ruleGraph);
        }
        // collect the selected rules
        Set<AspectGraph> selectedRules = new HashSet<AspectGraph>();
        for (RuleView ruleView : getModel().getRuleSet()) {
            selectedRules.add(ruleView.getAspectGraph());
        }
        // now shift rules to higher priority classes
        List<Integer> priorities = new ArrayList<Integer>();
        List<Set<AspectGraph>> remainingRules =
            new ArrayList<Set<AspectGraph>>();
        List<Set<AspectGraph>> shiftedRules = new ArrayList<Set<AspectGraph>>();
        Set<AspectGraph> oldShifted = Collections.<AspectGraph>emptySet();
        for (Map.Entry<Integer,Set<AspectGraph>> cell : rulesMap.entrySet()) {
            priorities.add(cell.getKey());
            Set<AspectGraph> remaining =
                new HashSet<AspectGraph>(cell.getValue());
            Set<AspectGraph> shifted = new HashSet<AspectGraph>(selectedRules);
            shifted.retainAll(remaining);
            remaining.removeAll(shifted);
            boolean allShifted = remaining.isEmpty();
            remaining.addAll(oldShifted);
            remainingRules.add(remaining);
            if (allShifted && priorities.size() < rulesMap.size()) {
                shiftedRules.add(Collections.<AspectGraph>emptySet());
                oldShifted = shifted;
            } else {
                shiftedRules.add(shifted);
                oldShifted = Collections.<AspectGraph>emptySet();
            }
        }
        // reassign priorities based on remaining and shifted rules
        Set<AspectGraph> changedRules = new HashSet<AspectGraph>();
        int min = 0;
        for (int i = 0; i < priorities.size(); i++) {
            int priority = priorities.get(i);
            if (priority < min) {
                priority = min;
            }
            boolean remaining =
                setPriority(remainingRules.get(i), priority, changedRules);
            if (remaining) {
                priority++;
            }
            boolean shifted =
                setPriority(shiftedRules.get(i), priority, changedRules);
            min = shifted ? priority + 1 : priority;
        }
        if (confirmAbandon()) {
            try {
                result |= getSimulator().getModel().doAddRules(changedRules);
            } catch (IOException exc) {
                showErrorDialog(exc, "Error during rule priority change");
            }
        }
        return result;
    }

    private boolean setPriority(Set<AspectGraph> rules, int priority,
            Set<AspectGraph> changed) {
        for (AspectGraph rule : rules) {
            if (GraphProperties.getPriority(rule) != priority) {
                AspectGraph newRule = rule.clone();
                GraphInfo.getProperties(newRule, true).setPriority(priority);
                newRule.setFixed();
                changed.add(newRule);
            }
        }
        return !rules.isEmpty();
    }
}
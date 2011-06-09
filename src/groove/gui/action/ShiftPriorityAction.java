package groove.gui.action;

import groove.graph.GraphProperties;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.trans.ResourceKind;
import groove.view.RuleModel;
import groove.view.aspect.AspectGraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

/**
 * Action that raises the priority of a selected set of rules.
 */
public class ShiftPriorityAction extends SimulatorAction {
    /** Constructs an instance of the action for a given simulator.
     * @param up if {@code true}, priorities are shifte uyp, otherwise they are
     * shifted down 
     */
    public ShiftPriorityAction(Simulator simulator, boolean up) {
        super(simulator, up ? Options.RAISE_PRIORITY_ACTION_NAME
                : Options.LOWER_PRIORITY_ACTION_NAME, up
                ? Icons.ARROW_SIMPLE_UP_ICON : Icons.ARROW_SIMPLE_DOWN_ICON);
        this.up = up;
    }

    @Override
    public void refresh() {
        boolean ruleSelected = getSimulatorModel().getRule() != null;
        setEnabled(ruleSelected
            && getSimulatorModel().getStore().isModifiable());
    }

    @Override
    public void execute() {
        // collect all rules according to current priority
        NavigableMap<Integer,Set<AspectGraph>> rulesMap =
            new TreeMap<Integer,Set<AspectGraph>>();
        for (AspectGraph ruleGraph : getGrammarStore().getGraphs(
            ResourceKind.RULE).values()) {
            int priority = GraphProperties.getPriority(ruleGraph);
            Set<AspectGraph> cell = rulesMap.get(priority);
            if (cell == null) {
                rulesMap.put(priority, cell = new HashSet<AspectGraph>());
            }
            cell.add(ruleGraph);
        }
        if (!this.up) {
            rulesMap = rulesMap.descendingMap();
        }
        // collect the selected rules
        Set<AspectGraph> selectedRules = new HashSet<AspectGraph>();
        for (RuleModel ruleView : getSimulatorModel().getRuleSet()) {
            selectedRules.add(ruleView.getSource());
        }
        // now shift rules to higher or lower priority classes
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
        List<Integer> newPriorities = new ArrayList<Integer>();
        List<Set<AspectGraph>> newCells = new ArrayList<Set<AspectGraph>>();
        int last = start();
        for (int i = 0; i < priorities.size(); i++) {
            int priority = priorities.get(i);
            if (!exceeds(priority, last)) {
                priority = inc(last);
            }
            Set<AspectGraph> cell = remainingRules.get(i);
            if (!cell.isEmpty()) {
                newPriorities.add(priority);
                newCells.add(cell);
                last = priority;
            }
            cell = shiftedRules.get(i);
            if (!cell.isEmpty()) {
                priority = inc(priority);
                newCells.add(cell);
                newPriorities.add(priority);
                last = priority;
            }
        }
        // check if the new priorities did not get negative
        if (!this.up && last < 0) {
            // shift up priorities starting from 0
            int corrected = 0;
            for (int i = newPriorities.size() - 1; i >= 0; i--) {
                int current = newPriorities.get(i);
                if (current < corrected) {
                    newPriorities.set(i, corrected);
                    corrected++;
                } else {
                    break;
                }
            }
        }
        // Create the new priorities map
        Map<String,Integer> priorityMap = new HashMap<String,Integer>();
        for (int i = 0; i < newPriorities.size(); i++) {
            int priority = newPriorities.get(i);
            for (AspectGraph rule : newCells.get(i)) {
                if (GraphProperties.getPriority(rule) != priority) {
                    priorityMap.put(rule.getName(), priority);
                }
            }
        }
        if (!priorityMap.isEmpty() && confirmStopSimulation()) {
            try {
                getSimulatorModel().doSetPriority(priorityMap);
            } catch (IOException exc) {
                showErrorDialog(exc, "Error during rule priority change");
            }
        }
    }

    private int start() {
        return this.up ? -1 : Integer.MAX_VALUE;
    }

    private int inc(int index) {
        return this.up ? index + 1 : index - 1;
    }

    private boolean exceeds(int ix, int last) {
        return this.up ? ix > last : ix < last;
    }

    private final boolean up;
}
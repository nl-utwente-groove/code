package groove.gui.action;

import static groove.grammar.model.ResourceKind.RULE;

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

import groove.grammar.QualName;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.ResourceModel;
import groove.grammar.model.RuleModel;
import groove.graph.GraphInfo;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;

/**
 * Action that raises the priority of a selected set of rules.
 */
public class ShiftPriorityAction extends SimulatorAction {
    /** Constructs an instance of the action for a given simulator.
     * @param up if {@code true}, priorities are shifte uyp, otherwise they are
     * shifted down
     */
    public ShiftPriorityAction(Simulator simulator, boolean up) {
        super(simulator,
            up ? Options.RAISE_PRIORITY_ACTION_NAME : Options.LOWER_PRIORITY_ACTION_NAME,
            up ? Icons.ARROW_SIMPLE_UP_ICON : Icons.ARROW_SIMPLE_DOWN_ICON);
        this.up = up;
    }

    @Override
    public void refresh() {
        boolean ruleSelected = getSimulatorModel().isSelected(RULE);
        setEnabled(ruleSelected);
    }

    @Override
    public void execute() {
        // Collect all properties and subrules (they should remain at priority 0)
        Set<QualName> frozen = new HashSet<>();
        // collect all rules according to current priority
        NavigableMap<Integer,Set<QualName>> rulesMap = new TreeMap<>();
        for (ResourceModel<?> model : getGrammarModel().getResourceSet(RULE)) {
            RuleModel rule = (RuleModel) model;
            if (rule.isProperty() || rule.hasRecipes()) {
                frozen.add(rule.getQualName());
            } else {
                int priority = rule.getPriority();

                Set<QualName> cell = rulesMap.get(priority);
                if (cell == null) {
                    rulesMap.put(priority, cell = new HashSet<>());
                }
                cell.add(rule.getQualName());
            }
        }
        if (!this.up) {
            rulesMap = rulesMap.descendingMap();
        }
        // collect the selected rules
        Set<QualName> selectedRules = new HashSet<>(getSimulatorModel().getSelectSet(RULE));
        selectedRules.removeAll(frozen);
        // now shift rules to higher or lower priority classes
        List<Integer> priorities = new ArrayList<>();
        List<Set<QualName>> remainingRules = new ArrayList<>();
        List<Set<QualName>> shiftedRules = new ArrayList<>();
        Set<QualName> oldShifted = Collections.<QualName>emptySet();
        for (Map.Entry<Integer,Set<QualName>> cell : rulesMap.entrySet()) {
            priorities.add(cell.getKey());
            Set<QualName> remaining = new HashSet<>(cell.getValue());
            Set<QualName> shifted = new HashSet<>(selectedRules);
            shifted.retainAll(remaining);
            remaining.removeAll(shifted);
            boolean allShifted = remaining.isEmpty();
            remaining.addAll(oldShifted);
            remainingRules.add(remaining);
            if (allShifted && priorities.size() < rulesMap.size()) {
                shiftedRules.add(Collections.<QualName>emptySet());
                oldShifted = shifted;
            } else {
                shiftedRules.add(shifted);
                oldShifted = Collections.<QualName>emptySet();
            }
        }
        // reassign priorities based on remaining and shifted rules
        List<Integer> newPriorities = new ArrayList<>();
        List<Set<QualName>> newCells = new ArrayList<>();
        int last = start();
        for (int i = 0; i < priorities.size(); i++) {
            int priority = priorities.get(i);
            if (!exceeds(priority, last)) {
                priority = inc(last);
            }
            Set<QualName> cell = remainingRules.get(i);
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
        Map<QualName,Integer> priorityMap = new HashMap<>();
        for (int i = 0; i < newPriorities.size(); i++) {
            int priority = newPriorities.get(i);
            for (QualName ruleName : newCells.get(i)) {
                AspectGraph ruleGraph = getGrammarStore().getGraphs(RULE)
                    .get(ruleName);
                if (GraphInfo.getPriority(ruleGraph) != priority) {
                    priorityMap.put(ruleName, priority);
                }
            }
        }
        if (!priorityMap.isEmpty()) {
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
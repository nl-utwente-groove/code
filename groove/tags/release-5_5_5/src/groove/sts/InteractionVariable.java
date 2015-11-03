package groove.sts;

import groove.algebra.Sort;
import groove.grammar.Rule;
import groove.grammar.rule.VariableNode;

/**
 * An interaction variable in an sts.
 * @author Vincent de Bruijn
 *
 */
public class InteractionVariable extends Variable {

    /**
     * Creates a new instance.
     * @param label The label of this variable.
     * @param type They type of this variable.
     */
    public InteractionVariable(String label, Sort type) {
        super(label, type);
    }

    /**
     * Creates a label for an InteractionVariable based on a VariableNode.
     * @param rule The rule where the node is in.
     * @param node The node on which the label is based.
     * @return The variable label.
     */
    public static String createInteractionVariableLabel(Rule rule,
            VariableNode node) {
        return rule.getFullName() + "_" + node.getNumber();
    }

    /**
     * Creates a JSON formatted string based on this variable.
     * @return The JSON string.
     */
    public String toJSON() {
        return "\"" + getLabel() + "\":\"" + this.type + "\"";
    }

}

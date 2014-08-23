package groove.sts;

import groove.algebra.Sort;
import groove.grammar.host.HostEdge;

/**
 * A location variable in an sts.
 * @author Vincent de Bruijn
 *
 */
public class LocationVariable extends Variable {

    private Object initialValue;

    /**
     * Creates a new instance.
     * @param label The label of the new variable. 
     * @param type The type of the new variable.
     * @param initialValue The initial value of the new variable.
     */
    public LocationVariable(String label, Sort type,
            Object initialValue) {
        super(label, type);
        this.initialValue = initialValue;
    }

    /**
     * Creates a label for a LocationVariable based on a HostEdge. Assumed is
     * that the target of the edge is a data node.
     * 
     * @param edge
     *            The edge on which the label is based.
     * @return The variable label.
     */
    public static String createLocationVariableLabel(HostEdge edge) {
        return edge.label().text() + "_" + edge.source().getNumber();
    }

    /**
     * Gets the initial value of this variable.
     * @return The initial value.
     */
    public Object getInitialValue() {
        return this.initialValue;
    }

    /**
     * Creates a JSON formatted string based on this variable.
     * @return The JSON string.
     */
    public String toJSON() {
        return "\"" + getLabel() + "\":{\"type\":\"" + getType()
            + "\",\"init\":" + getInitialValue().toString() + "}";
    }

}

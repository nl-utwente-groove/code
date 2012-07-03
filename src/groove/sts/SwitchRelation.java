package groove.sts;

/**
 * A switch relation in an sts.
 * 
 * @author Vincent de Bruijn
 */
public class SwitchRelation {

    private Gate gate;
    private String guard;
    private String update;

    /**
     * Creates a new instance.
     * @param gate The gate of the new switch relation.
     * @param guard The guard of the new switch relation.
     * @param update The update of the new switch relation.
     */
    public SwitchRelation(Gate gate, String guard, String update) {
        this.gate = gate;
        this.guard = guard;
        this.update = update;
    }

    /**
     * Gets the gate of this switch relation.
     * @return The gate.
     */
    public Gate getGate() {
        return this.gate;
    }

    /**
     * Gets the guard of this switch relation.
     * @return The guard.
     */
    public String getGuard() {
        return this.guard;
    }

    /**
     * Gets the update of this switch relation.
     * @return The update.
     */
    public String getUpdate() {
        return this.update;
    }

    /**
     * Gets a unique identifier object for Switch Relations.
     * @param gate The gate of the Switch Relation.
     * @param guard The guard of the Switch Relation.
     * @param update The update of the Switch Relation.
     * @return A unique identifier object.
     */
    public static Object getSwitchIdentifier(Gate gate, String guard,
            String update) {
        // TODO: replace with triple
        return gate.getLabel() + guard + update;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SwitchRelation)) {
            return false;
        }
        SwitchRelation other = (SwitchRelation) o;
        return other.getGate() == getGate() && other.getGuard() == getGuard()
            && other.getUpdate() == getUpdate();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getGate().hashCode();
        result = prime * result + getGuard().hashCode();
        result = prime * result + getUpdate().hashCode();
        return result;
    }

    /**
     * Creates a JSON formatted string based on this switch relation, with given source and target locations.
     * @param source The source location.
     * @param target The target location.
     * @return The JSON string.
     */
    public String toJSON(Location source, Location target) {
        return "{\"s\":" + source.toJSON() + ",\"l\":\""
            + this.gate.getStrippedLabel() + "\",\"t\":" + target.toJSON()
            + ",\"g\":\"" + this.guard + "\",\"u\":\"" + this.update + "\"}";
    }

}

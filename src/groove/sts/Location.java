package groove.sts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A location in the STS. It represents a Graph State stripped of data values.
 */
public class Location {

    private String label;
    private Map<SwitchRelation,Set<Location>> relations;

    /**
     * Creates a new instance.
     * @param label The label on this Location.
     */
    public Location(String label) {
        this.label = label;
        this.relations = new HashMap<SwitchRelation,Set<Location>>();
    }

    /**
     * Returns the possible Switch Relations from this Location.
     * @return The possible Switch Relations.
     */
    public Set<SwitchRelation> getSwitchRelations() {
        return this.relations.keySet();
    }

    /**
     * Gets the target Locations of the Switch Relation.
     * @param sr The Switch Relation.
     * @return The target Location of sr.
     */
    public Set<Location> getRelationTargets(SwitchRelation sr) {
        return this.relations.get(sr);
    }

    /**
     * Adds a new outgoing Switch Relation from this Location.
     * @param sr The outgoing Switch Relation.
     * @param l The target Location of sr.
     */
    public void addSwitchRelation(SwitchRelation sr, Location l) {
        Set<Location> set = this.relations.get(sr);
        if (set == null) {
            set = new HashSet<Location>();
            this.relations.put(sr, set);
        }
        set.add(l);
    }

    /**
     * Gets the label of this Location.
     * @return The label.
     */
    public String getLabel() {
        return this.label;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Location)) {
            return false;
        }
        return this.label == ((Location) o).getLabel();
    }

    @Override
    public int hashCode() {
        return getLabel().hashCode();
    }

    /**
     * Creates a JSON formatted string based on this Location.
     * @return The JSON string.
     */
    public String toJSON() {
        return "\"" + this.label + "\"";
    }

}

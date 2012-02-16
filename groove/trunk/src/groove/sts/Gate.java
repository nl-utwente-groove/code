package groove.sts;

import java.util.List;

/**
 * A gate in an STS.
 * @author Vincent de Bruijn
 *
 */
public class Gate {
	
	private String label;
	private List<InteractionVariable> iVars;
	
	/**
	 * Creates a new instance.
	 * @param label The label of the new gate.
	 * @param iVars The interaction variables of the new gate.
	 */
	public Gate(String label, List<InteractionVariable> iVars) {
		this.label = label;
		this.iVars = iVars;
	}
	
	/**
	 * Gets the label of this gate.
	 * @return The label.
	 */
	public String getLabel() {
		return this.label;
	}
	

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Gate))
			return false;
		Gate other = (Gate)o;
		return other.getLabel() == getLabel();
	}
	
	@Override
	public int hashCode() {
		return getLabel().hashCode();
	}
	
	/**
	 * Creates a JSON formatted string based on this gate.
	 * @return The JSON string.
	 */
	public String toJSON() {
		String type = "!";
		if (label.contains("?"))
			type = "?";
		String json = "\""+getStrippedLabel()+"\":{\"type\":\""+type+"\",\"iVars\":[";
		for (Variable v : iVars) {
			json+="\""+v.getLabel()+"\",";
		}
		return json.substring(0, json.length()-1)+"]}";
	}
	
	public String getStrippedLabel() {
		if (label.endsWith("?") || label.endsWith("!"))
			return label.substring(0, label.length()-1);
		else
			return label;
	}
	
}

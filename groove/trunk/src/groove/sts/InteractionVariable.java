package groove.sts;

import groove.algebra.SignatureKind;

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
	public InteractionVariable(String label, SignatureKind type) {
		super(label, type);
	}
	
	/**
	 * Creates a JSON formatted string based on this variable.
	 * @return The JSON string.
	 */
	public String toJSON() {
		return "\""+getLabel()+"\":\""+type+"\"";
	}
	
}

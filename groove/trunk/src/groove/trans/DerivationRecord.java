/* $Id: DerivationRecord.java,v 1.1 2007-03-30 15:50:26 rensink Exp $ */
package groove.trans;

import groove.util.Dispenser;

/**
 * Type that stores information gathered during rule derivation.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface DerivationRecord {
	/** Returns a counter for node numbers, used to make fresh nodes unique. */
	public Dispenser getNodeCounter();
}

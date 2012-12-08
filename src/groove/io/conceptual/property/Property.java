package groove.io.conceptual.property;

import groove.io.conceptual.Acceptor;

/**
 * Representation for properties. The conceptual model itself can check if the property is satisfied by means of the satisfied method.
 * @author s0141844
 *
 */
public interface Property extends Acceptor {
    /**
     * Retrieve actual metamodel instances
     */
    public void resolveFields();
}

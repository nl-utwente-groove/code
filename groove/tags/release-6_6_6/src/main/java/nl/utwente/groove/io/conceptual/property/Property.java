package nl.utwente.groove.io.conceptual.property;

import nl.utwente.groove.io.conceptual.Acceptor;
import nl.utwente.groove.io.conceptual.Field;

/**
 * Representation for properties.
 * The conceptual model itself can check if the property is satisfied by means of the satisfied method.
 * @author Harold Bruintjes
 *
 */
public interface Property extends Acceptor {
    /**
     * Sets any named field in the property to their actual {@link Field}s
     * according to the metamodel.
     */
    public void resolveFields();
}

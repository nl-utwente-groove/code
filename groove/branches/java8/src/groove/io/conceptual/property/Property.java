package groove.io.conceptual.property;

import groove.io.conceptual.Concept;
import groove.io.conceptual.Field;

/**
 * Representation for properties.
 * A design itself can check if the property is satisfied by means of the satisfied method.
 * @author Harold Bruintjes
 *
 */
public abstract class Property extends Concept {
    /**
     * Sets any named field in the property to their actual {@link Field}s
     * according to the glossary.
     */
    abstract public void resolveFields();
}

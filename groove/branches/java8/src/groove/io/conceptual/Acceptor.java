package groove.io.conceptual;

import java.io.Serializable;

/**
 * Accept interface for visitor pattern (See Visitor)
 * @author Harold Bruijntjes
 *
 */
public interface Acceptor extends Serializable {
    /**
     * Adds this element to a given export builder.
     * @param v export builder to add to
     * @param param string that may be passed along as a parameter
     * @return true if visited successfully
     */
    public boolean doBuild(ExportBuilder<?> v, String param);
}

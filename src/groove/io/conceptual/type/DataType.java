package groove.io.conceptual.type;

import groove.io.conceptual.Id;
import groove.io.conceptual.Identifiable;
import groove.io.conceptual.value.Value;

public abstract class DataType extends Type implements Identifiable {

    protected Id m_id;
    
    @Override
    public Id getId() {
        return m_id;
    }
    
    public abstract Value valueFromString(String valueString);
}

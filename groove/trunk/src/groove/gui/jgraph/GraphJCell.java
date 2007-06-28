/**
 * 
 */
package groove.gui.jgraph;

import groove.graph.Edge;
import groove.graph.Label;
import groove.view.LabelParser;

/**
 * Extension of {@link JCell} that recognizes that cells have underlying edges.
 * @author Arend Rensink
 * @version $Revision: 1.1 $
 */
interface GraphJCell extends JCell {
    /** 
     * This implementation returns the label text of the object
     * (which is known to be an edge).
     * Callback method from {@link #getLines()}.
     */
    StringBuilder getLine(Edge edge);
    
    /** 
     * Returns the label of the edge as to be displayed in the label list.
     * Callback method from {@link #getListLabels()}.
     */
    String getListLabel(Edge edge);

    /** 
     * Retrieves an edge label. 
     * Callback method from {@link #getLine(Edge)}, {@link #getPlainLabel(Edge)} and {@link #getListLabel(Edge)}.
     */
    Label getLabel(Edge edge);
    
    /** 
     * Returns the label of the edge as to be displayed in an edit view.
     * Callback method from {@link #getPlainLabels()}.
     */
    String getPlainLabel(Edge edge);
    
    /** 
     * Returns a label parser for this jnode.
     * Callback method from {@link #getPlainLabel(Edge)}.
     */
    LabelParser getLabelParser();
    
    /**
     * Specialises the return type.
     */
    public EdgeContent getUserObject();
}

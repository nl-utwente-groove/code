package groove.gui.jgraph;

import groove.gui.Options;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import org.jgraph.graph.BasicMarqueeHandler;

/**
 * Marquee handler that activates and shows the popup menu and adds and 
 * removes edge points.
 * @see JGraph#isPopupMenuEvent(MouseEvent)
 * @see JGraph#activatePopupMenu(Point)
 * @see JGraph#addPoint(JEdge, Point2D)
 * @see JGraph#removePoint(JEdge, Point2D)
 */
class JGraphMarqueeHandler<J extends JGraph> extends BasicMarqueeHandler {
	/**
	 * Constructs a marquee handler for a given j-graph.
	 * @param jGraph the JGraph for which to create a marquee handler
	 */
    JGraphMarqueeHandler(J jGraph) {
        this.jGraph = jGraph;
    }
    
    
//    
//    @Override
//    public boolean isForceMarqueeEvent(MouseEvent evt) {
//        return jGraph.isPopupMenuEvent(evt) || super.isForceMarqueeEvent(evt);
//    }
//
//    /**
//     * If the mouse event is a popup menu event, create the popup. 
//     * If it is an add or remove event and the graph selection is appropriate,
//     * add or remove j-edge points.
//     * Pass on the event to <tt>super</tt> if it is not for us.
//     * @param evt the event that happened
//     */
//    @Override
//    public void mousePressed(MouseEvent evt) {
//        if (jGraph.isAddPointEvent(evt)) {
//            JCell jCell = (JCell) jGraph.getSelectionCell();
//            if (jCell instanceof JEdge) {
//                jGraph.addPoint((JEdge) jCell, evt.getPoint());
//            }
//        } else if (jGraph.isRemovePointEvent(evt)) {
//            JCell jCell = (JCell) jGraph.getSelectionCell();
//            if (jCell instanceof JEdge) {
//                jGraph.removePoint((JEdge) jCell, evt.getPoint());
//            }
//        } else {
//            super.mousePressed(evt);
//        }
//    }

	@Override
	public void mouseReleased(MouseEvent e) {
		if (!Options.isPointEditEvent(e)) {
			super.mouseReleased(e);
		}
	}

	/**
	 * Hook for subclassers. Current implementation checks if graph selection is
	 * enabled. This is called from mousePressed before initiating the marquee
	 * selection.
	 */
    @Override
	public boolean isMarqueeTriggerEvent(MouseEvent e, org.jgraph.JGraph graph) {
		return !Options.isPointEditEvent(e) && super.isMarqueeTriggerEvent(e, graph); 
	}

	/**
	 * Returns the j-graph upon which this marquee handler works.
	 */
	final J getJGraph() {
		return this.jGraph;
	}

	/** The j-graph upon which this marquee handler works. */
    private final J jGraph;
}
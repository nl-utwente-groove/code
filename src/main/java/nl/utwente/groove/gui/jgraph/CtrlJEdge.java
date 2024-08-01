package nl.utwente.groove.gui.jgraph;

import java.util.EnumSet;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;

import nl.utwente.groove.control.graph.ControlEdge;
import nl.utwente.groove.control.graph.ControlGraph;
import nl.utwente.groove.control.instance.Step;
import nl.utwente.groove.gui.look.Look;
import nl.utwente.groove.io.HTMLConverter;
import nl.utwente.groove.util.Groove;

/**
 * JEdge class that describes the underlying edge as a graph transition.
 * @author Tom Staijen
 * @version $Revision$
 */
public class CtrlJEdge
    extends AJEdge<@NonNull ControlGraph,CtrlJGraph,JModel<@NonNull ControlGraph>,CtrlJVertex> {
    /** Constructor for a prototype object. */
    private CtrlJEdge() {
        // empty
    }

    @Override
    public ControlEdge getEdge() {
        return (ControlEdge) super.getEdge();
    }

    @Override
    StringBuilder getEdgeKindDescription() {
        return new StringBuilder("transition");
    }

    @Override
    String getLabelDescription() {
        StringBuffer result = new StringBuffer(", generated by ");
        String[] displayedLabels = new String[getEdges().size()];
        int labelIndex = 0;
        for (Object part : getEdges()) {
            Step trans = (Step) part;
            String description;
            description = trans.toString();
            displayedLabels[labelIndex] = HTMLConverter.STRONG_TAG.on(description, true);
            labelIndex++;
        }
        if (displayedLabels.length == 1) {
            result.append(displayedLabels[0]);
        } else {
            result.append(Groove.toString(displayedLabels, "<br>- ", "", "<br>- "));
        }
        return result.toString();
    }

    @Override
    protected Set<Look> getStructuralLooks() {
        var edge = getEdge();
        if (edge != null && edge.isVerdict()) {
            return EnumSet.of(Look.CTRL_VERDICT);
        } else {
            return EnumSet.of(Look.TRANS);
        }
    }

    /**
     * Returns a fresh, uninitialised instance.
     * Call {@link #setJModel} to initialise.
     */
    public static CtrlJEdge newInstance() {
        return new CtrlJEdge();
    }
}
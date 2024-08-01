package nl.utwente.groove.gui.jgraph;

import java.util.EnumSet;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;

import nl.utwente.groove.control.graph.ControlGraph;
import nl.utwente.groove.control.graph.ControlNode;
import nl.utwente.groove.gui.look.Look;

/**
 * JVertex class that describes the underlying node as a graph state.
 * @author Tom Staijen
 * @version $Revision$
 */
public class CtrlJVertex
    extends AJVertex<@NonNull ControlGraph,CtrlJGraph,JModel<@NonNull ControlGraph>,CtrlJEdge> {
    /**
     * Creates a new instance.
     * Call {@link #setJModel} and {@link #setNode(nl.utwente.groove.graph.Node)}
     * to initialise.
     */
    private CtrlJVertex() {
        // empty
    }

    @Override
    public void initialise() {
        super.initialise();
        if (isFinal()) {
            setLook(Look.FINAL, true);
        }
    }

    @Override
    public ControlNode getNode() {
        return (ControlNode) super.getNode();
    }

    /** Indicates if this jVertex represents the start state of the control automaton. */
    public boolean isStart() {
        return getNode().getNumber() == 0;
    }

    /** Indicates if this jVertex represents the start state of the control automaton. */
    public boolean isFinal() {
        return getNode().getPosition().isFinal();
    }

    /** Indicates if this jVertex corresponds to a transient control state. */
    public boolean isTransient() {
        return getNode().getPosition().getTransience() > 0;
    }

    @Override
    protected Set<Look> getStructuralLooks() {
        if (isStart()) {
            return EnumSet.of(Look.START);
        } else if (isTransient()) {
            return EnumSet.of(Look.CTRL_TRANSIENT_STATE);
        } else {
            return EnumSet.of(Look.STATE);
        }
    }

    /** Returns a fresh, uninitialised instance.
     * Call {@link #setJModel} and {@link #setNode(nl.utwente.groove.graph.Node)} to initialise.
     */
    public static CtrlJVertex newInstance() {
        return new CtrlJVertex();
    }
}
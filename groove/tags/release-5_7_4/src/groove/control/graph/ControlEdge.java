/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.control.graph;

import java.awt.Color;

import groove.control.CallStack;
import groove.graph.ALabelEdge;
import groove.graph.Edge;
import groove.graph.EdgeRole;
import groove.util.line.Line;
import groove.util.line.Line.Style;

/**
 * @author rensink
 * @version $Revision $
 */
public class ControlEdge extends ALabelEdge<ControlNode> {
    /**
     * Constructs a verdict edge
     * @param source source node of the control edge
     * @param target target node of the control edge
     * @param success flag indicating if this is a success or failure verdict
     */
    public ControlEdge(ControlNode source, ControlNode target, boolean success) {
        super(source, target);
        this.success = success;
        this.callStack = null;
    }

    /**
     * Constructs a verdict edge
     * @param source source node of the control edge
     * @param target target node of the control edge
     */
    public ControlEdge(ControlNode source, ControlNode target, CallStack callStack) {
        super(source, target);
        this.success = false;
        this.callStack = callStack;
    }

    /** Indicates if this is a verdict edge. */
    public boolean isVerdict() {
        return getCallStack() == null;
    }

    /**
     * If this is a verdict edge, indicates if it is a success edge.
     * Should only be invoked if {@code isVerdict} holds
     * @return {@code true} if this a success verdict edge; {@code false} if
     * it is a failure verdict edge
     */
    public boolean isSuccess() {
        return this.success;
    }

    private final boolean success;

    /** Returns the call wrapped in this edge, if it is a call edge. */
    public CallStack getCallStack() {
        return this.callStack;
    }

    /** Call wrapped in this edge, if this is a call edge. */
    private final CallStack callStack;

    @Override
    protected Line computeLine() {
        String text;
        if (isVerdict()) {
            text = isSuccess() ? "succ" : "fail";
        } else {
            text = getCallStack().toString();
        }
        Line result = Line.atom(text);
        if (isVerdict() || getRole() == EdgeRole.FLAG) {
            result = result.style(Style.ITALIC);
        }
        if (!isVerdict()) {
            Color color = getCallStack().getRule()
                .getRole()
                .getColor();
            if (color != null) {
                if (source().isStart()) {
                    color = color.brighter()
                        .brighter();
                }
                result = result.color(color);
            }
        }
        return result;
    }

    @Override
    public EdgeRole getRole() {
        return isLoop() && (isVerdict() || getCallStack().getRule()
            .isProperty()) ? EdgeRole.FLAG : EdgeRole.BINARY;
    }

    @Override
    protected int computeLabelHash() {
        return isVerdict() ? Boolean.valueOf(isSuccess())
            .hashCode() : getCallStack().hashCode();
    }

    @Override
    protected boolean isLabelEqual(Edge edge) {
        ControlEdge other = (ControlEdge) edge;
        if (isVerdict()) {
            if (other.isVerdict()) {
                return isSuccess() == other.isSuccess();
            } else {
                return false;
            }
        }
        if (other.isVerdict()) {
            return false;
        }
        return getCallStack().equals(other.getCallStack());
    }
}

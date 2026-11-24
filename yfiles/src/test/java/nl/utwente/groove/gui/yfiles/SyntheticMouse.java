/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2026
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package nl.utwente.groove.gui.yfiles;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.util.AIGenerated;

/**
 * Synthetic mouse input for the tests of the yFiles canvases: mouse events dispatched
 * to yFiles' input surface, the descendant of the graph component that carries yFiles'
 * mouse listeners. Events dispatched to the graph component itself reach only the Swing
 * listeners on that component (a lightweight container routes nothing to its children),
 * so they exercise the listeners of a display, never yFiles' input modes. Points are in
 * the coordinates of the graph component, which the surface fills.
 * <p>
 * The events are dispatched on the event thread, as real input is, and the call returns
 * once they have been handled. Dispatching runs the whole gesture on the calling thread,
 * and the canvases queue the continuation of a gesture on the event thread (the edge that
 * a click starts, the editor that a double click opens): from any other thread that
 * continuation would run concurrently with the tail of the gesture, and lose against it.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
final class SyntheticMouse {
    private SyntheticMouse() {
        // no instances
    }

    /** Returns the descendant of a component that carries mouse listeners, or the component itself. */
    static Component surfaceOf(Container component) {
        for (Component child : component.getComponents()) {
            if (child.getMouseListeners().length > 0) {
                return child;
            }
            if (child instanceof Container container) {
                Component result = surfaceOf(container);
                if (result != container) {
                    return result;
                }
            }
        }
        return component;
    }

    /**
     * Clicks a mouse button at a point of a graph component: a move, a press, a release
     * and a click on the input surface. A right click is a popup trigger on release, as
     * on Windows.
     * @param button the button, {@link MouseEvent#BUTTON1} or {@link MouseEvent#BUTTON3}
     * @param modifiers extended modifier mask of the click, such as
     * {@link InputEvent#CTRL_DOWN_MASK}
     * @param clickCount the click count, 2 for the second click of a double click
     */
    static void click(JComponent component, int x, int y, int button, int modifiers,
                      int clickCount) {
        onEventThread(() -> {
            Component target = surfaceOf(component);
            boolean popup = button == MouseEvent.BUTTON3;
            int down = popup
                ? InputEvent.BUTTON3_DOWN_MASK
                : InputEvent.BUTTON1_DOWN_MASK;
            long when = System.currentTimeMillis();
            target
                .dispatchEvent(new MouseEvent(target, MouseEvent.MOUSE_MOVED, when, modifiers, x,
                    y, 0, false));
            target
                .dispatchEvent(new MouseEvent(target, MouseEvent.MOUSE_PRESSED, when + 1,
                    modifiers | down, x, y, clickCount, false, button));
            target
                .dispatchEvent(new MouseEvent(target, MouseEvent.MOUSE_RELEASED, when + 20,
                    modifiers, x, y, clickCount, popup, button));
            target
                .dispatchEvent(new MouseEvent(target, MouseEvent.MOUSE_CLICKED, when + 20,
                    modifiers, x, y, clickCount, false, button));
        });
    }

    /**
     * Drags with the left mouse button from a point of a graph component over a given
     * distance, in a few steps, on the input surface.
     */
    static void drag(JComponent component, int x, int y, int dx, int dy) {
        onEventThread(() -> {
            Component target = surfaceOf(component);
            long when = System.currentTimeMillis();
            target
                .dispatchEvent(new MouseEvent(target, MouseEvent.MOUSE_MOVED, when, 0, x, y, 0,
                    false));
            target
                .dispatchEvent(new MouseEvent(target, MouseEvent.MOUSE_PRESSED, when + 1,
                    InputEvent.BUTTON1_DOWN_MASK, x, y, 1, false, MouseEvent.BUTTON1));
            int steps = 5;
            for (int i = 1; i <= steps; i++) {
                target
                    .dispatchEvent(new MouseEvent(target, MouseEvent.MOUSE_DRAGGED, when + 1 + i,
                        InputEvent.BUTTON1_DOWN_MASK, x + dx * i / steps, y + dy * i / steps, 0,
                        false));
            }
            target
                .dispatchEvent(new MouseEvent(target, MouseEvent.MOUSE_RELEASED, when + 2 + steps,
                    0, x + dx, y + dy, 1, false, MouseEvent.BUTTON1));
        });
    }

    /**
     * Runs a task on the event thread and waits for it: directly when already on that
     * thread (the callers of the Simulator test are), through
     * {@link SwingUtilities#invokeAndWait} otherwise.
     */
    private static void onEventThread(Runnable task) {
        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
            return;
        }
        try {
            SwingUtilities.invokeAndWait(task);
        } catch (InterruptedException | InvocationTargetException exc) {
            throw new AssertionError(exc);
        }
    }
}

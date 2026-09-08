/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2026 University of Twente
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
package nl.utwente.groove.gui.view;

import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.AIGenerated;

/**
 * Cut, copy and paste of the cells of an edited graph, through the system clipboard
 * (or a clipboard of its own when there is no desktop). What travels is a
 * {@link GraphFragment}, see {@link GraphTransferable}; both backends share these
 * operations, which go through the view model so that they are recorded as edits.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public final class GraphClipboard {
    private GraphClipboard() {
        // no instances
    }

    /**
     * Copies the selected cells of a canvas to the clipboard.
     * @return {@code true} if there was a selection with vertices to copy
     */
    public static boolean copy(AspectGraphCanvas canvas) {
        AspectGraphViewModel model = canvas.getNonNullViewModel();
        model.settlePendingInsertion();
        GraphFragment fragment = GraphFragment.of(canvas.getSelection());
        if (fragment.isEmpty()) {
            return false;
        }
        local = fragment;
        try {
            clipboard().setContents(new GraphTransferable(fragment), null);
        } catch (IllegalStateException exc) {
            // the system clipboard is in use by another application: the local copy serves
        }
        return true;
    }

    /**
     * Copies the selected cells of a canvas to the clipboard and removes them, as
     * one edit.
     * @return {@code true} if there was a selection with vertices to cut
     */
    public static boolean cut(AspectGraphCanvas canvas) {
        if (!copy(canvas)) {
            return false;
        }
        canvas.getNonNullViewModel().remove(List.copyOf(canvas.getSelection()));
        return true;
    }

    /**
     * Pastes the fragment on the clipboard into a canvas, as one edit, and selects
     * the pasted cells. The fragment is centred at the mouse pointer if that is over
     * the canvas, and otherwise put at a small offset ({@link #PASTE_OFFSET}) from
     * where it was copied.
     * @return {@code true} if the clipboard held a fragment
     */
    public static boolean paste(AspectGraphCanvas canvas) {
        GraphFragment fragment = getFragment();
        if (fragment == null) {
            return false;
        }
        Rectangle2D bounds = fragment.getBounds();
        Point2D pointer = canvas.getPointerLocation();
        double dx;
        double dy;
        if (pointer == null) {
            dx = PASTE_OFFSET;
            dy = PASTE_OFFSET;
        } else {
            dx = pointer.getX() - bounds.getCenterX();
            dy = pointer.getY() - bounds.getCenterY();
        }
        var cells = canvas.getNonNullViewModel().insertFragment(fragment, dx, dy);
        canvas.select(cells);
        return true;
    }

    /** Indicates if the clipboard holds a graph fragment. */
    public static boolean hasFragment() {
        return getFragment() != null;
    }

    /**
     * Returns the graph fragment on the clipboard, if any: the last fragment copied
     * in this JVM if the system clipboard cannot be read, {@code null} if it holds
     * something else.
     */
    public static @Nullable GraphFragment getFragment() {
        try {
            Transferable contents = clipboard().getContents(GraphClipboard.class);
            if (contents != null
                && contents.isDataFlavorSupported(GraphTransferable.FRAGMENT_FLAVOR)) {
                return (GraphFragment) contents.getTransferData(GraphTransferable.FRAGMENT_FLAVOR);
            }
            return null;
        } catch (UnsupportedFlavorException | IOException exc) {
            return null;
        } catch (IllegalStateException exc) {
            // the system clipboard is in use by another application
            return local;
        }
    }

    /** The last fragment copied in this JVM, for when the system clipboard cannot be used. */
    private static @Nullable GraphFragment local;

    /** Returns the system clipboard, or the local one when there is no desktop. */
    private static Clipboard clipboard() {
        return GraphicsEnvironment.isHeadless()
            ? LOCAL
            : Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    /** Clipboard of this JVM, used when there is no system clipboard. */
    private static final Clipboard LOCAL = new Clipboard("GROOVE");

    /** Distance by which a pasted fragment is offset from the copied cells, in either direction. */
    public static final int PASTE_OFFSET = 20;
}

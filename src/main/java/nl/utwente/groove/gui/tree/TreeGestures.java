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
package nl.utwente.groove.gui.tree;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.AIGenerated;

/**
 * Mouse-gesture helpers shared by the trees of the lists panels: the resource
 * trees and the state tree, which switch the display on a click.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
final class TreeGestures {
    private TreeGestures() {
        // not to be instantiated
    }

    /**
     * Returns the path of a tree under a mouse event, counting the whole width
     * of a row as belonging to its entry.
     * {@link JTree#getPathForLocation} accepts only a click on the rendered
     * label, which is a fraction of the row: the look and feel paints the
     * selection across the full width, and the tree's own selection handling
     * uses the closest path, so a click beside the label does move the
     * selection but was invisible to the listeners of the tree. On a tree
     * whose selection cannot change -- a single resource, or a click on the
     * entry already selected -- that made the click do nothing at all.
     * @return the path of the row that was clicked, or {@code null} if the
     * event was above the first or below the last row, or left of the label
     * (where the expand control lives)
     */
    @AIGenerated("Claude Opus 5, 2026-09")
    static @Nullable TreePath getMousedPath(JTree tree, MouseEvent evt) {
        TreePath result = tree.getClosestPathForLocation(evt.getX(), evt.getY());
        if (result != null) {
            Rectangle bounds = tree.getPathBounds(result);
            if (bounds == null || evt.getY() < bounds.y
                || evt.getY() >= bounds.y + bounds.height || evt.getX() < bounds.x) {
                result = null;
            }
        }
        return result;
    }

    /**
     * Keeps the focus in a tree across a display switch that a mouse press on
     * it has just triggered.
     * The press gives the tree the focus, but through posted events; the
     * switch hides the previous display before those are dispatched, and as
     * that display still contains the focus owner of record, AWT transfers the
     * focus to the next component of the cycle, in the display coming up. The
     * request queued here comes after all of that, so the tree ends up with the
     * focus and the entry pressed is rendered as actively selected.
     */
    static void keepFocus(JTree tree) {
        SwingUtilities.invokeLater(tree::requestFocusInWindow);
    }
}

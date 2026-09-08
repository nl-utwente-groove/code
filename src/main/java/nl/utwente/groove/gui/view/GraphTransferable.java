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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.AIGenerated;

/**
 * Clipboard content holding a {@link GraphFragment}: offered under its own flavour,
 * within this JVM, and as text (the label texts of the fragment) to other applications.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class GraphTransferable implements Transferable {
    /** Constructs a transferable for a given fragment. */
    public GraphTransferable(GraphFragment fragment) {
        this.fragment = fragment;
    }

    private final GraphFragment fragment;

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {FRAGMENT_FLAVOR, DataFlavor.stringFlavor};
    }

    @Override
    public boolean isDataFlavorSupported(@Nullable DataFlavor flavor) {
        return FRAGMENT_FLAVOR.equals(flavor) || DataFlavor.stringFlavor.equals(flavor);
    }

    @Override
    public Object getTransferData(@Nullable DataFlavor flavor) throws UnsupportedFlavorException {
        if (FRAGMENT_FLAVOR.equals(flavor)) {
            return this.fragment;
        } else if (DataFlavor.stringFlavor.equals(flavor)) {
            return this.fragment.toText();
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    /** The flavour under which a graph fragment is offered, within this JVM. */
    public static final DataFlavor FRAGMENT_FLAVOR
        = new DataFlavor(GraphFragment.class, "GROOVE graph fragment");
}

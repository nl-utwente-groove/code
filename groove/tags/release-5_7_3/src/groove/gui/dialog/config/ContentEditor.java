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
package groove.gui.dialog.config;

import java.awt.CardLayout;

import javax.swing.JPanel;

import groove.explore.config.ExploreKey;
import groove.explore.config.SettingKey;
import groove.gui.dialog.ConfigDialog;

/**
 * Abstract setting content editor.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class ContentEditor extends SettingEditor {
    /**
     * Constructs an editor.
     */
    protected ContentEditor(ConfigDialog<?> dialog, JPanel holder, ExploreKey key,
        SettingKey kind) {
        this.dialog = dialog;
        this.holder = holder;
        this.key = key;
        this.kind = kind;
        if (holder != null) {
            holder.add(this, kind.getName());
        }
    }

    /** Returns the dialog on which this editor is placed. */
    protected ConfigDialog<?> getDialog() {
        return this.dialog;
    }

    private final ConfigDialog<?> dialog;

    /** Tests if this editor is placed on a parent container. */
    protected boolean hasHolder() {
        return getHolder() != null;
    }

    /** Returns the parent container on which this editor is placed, if any. */
    protected JPanel getHolder() {
        return this.holder;
    }

    @Override
    public ExploreKey getKey() {
        return this.key;
    }

    private final ExploreKey key;

    @Override
    public SettingKey getKind() {
        return this.kind;
    }

    private final SettingKey kind;

    /**
     * The parent panel, on which this is placed.
     * May be {@code null} if the explore key has no content at all.
     */
    private final JPanel holder;

    @Override
    public void activate() {
        if (hasHolder()) {
            CardLayout layout = (CardLayout) getHolder().getLayout();
            layout.show(getHolder(), getKind().getName());
        }
        testError();
    }

    /** Reports the error value (if any) to the dialog. */
    protected void testError() {
        getDialog().setError(getKey(), getError());
    }
}

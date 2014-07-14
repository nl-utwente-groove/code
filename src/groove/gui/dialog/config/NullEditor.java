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

import groove.explore.config.ExploreKey;
import groove.explore.config.NullContent;
import groove.explore.config.SettingKey;
import groove.explore.config.SettingList;
import groove.grammar.model.FormatException;

import java.awt.CardLayout;

import javax.swing.JPanel;

/**
 * Editor for the null content.
 * @author Arend Rensink
 * @version $Revision $
 */
public class NullEditor extends SettingEditor {
    /**
     * Constructs a null editor for a given exploration key and setting kind.
     */
    public NullEditor(JPanel holder, ExploreKey key, SettingKey kind) {
        assert kind.getContentType() == NullContent.class;
        this.holder = holder;
        this.key = key;
        this.kind = kind;
        if (holder != null) {
            holder.add(this, kind.getName());
        }
    }

    /** Tests if this editor is placed on a parent container. */
    private boolean hasHolder() {
        return getHolder() != null;
    }

    /** Returns the parent container on which this editor is placed, if any. */
    private JPanel getHolder() {
        return this.holder;
    }

    /**
     * The parent panel, on which this is placed.
     * May be {@code null} if the explore key has no content at all.
     */
    private final JPanel holder;

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

    @Override
    public void refresh() {
        // does nothing
    }

    @Override
    public void activate() {
        if (hasHolder()) {
            CardLayout layout = (CardLayout) getHolder().getLayout();
            layout.show(getHolder(), getKind().getName());
        }
    }

    @Override
    public SettingList getSetting() throws FormatException {
        return getKind().getDefaultSetting();
    }

    @Override
    public void setSetting(SettingList content) {
        assert content.isSingular() && content.single().getKind() == getKind();
    }

    @Override
    public boolean hasError() {
        return getError() != null;
    }

    @Override
    public String getError() {
        return null;
    }
}

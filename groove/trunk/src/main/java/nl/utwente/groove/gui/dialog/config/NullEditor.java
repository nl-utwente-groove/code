/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.gui.dialog.config;

import javax.swing.JPanel;

import nl.utwente.groove.explore.config.ExploreKey;
import nl.utwente.groove.explore.config.Null;
import nl.utwente.groove.explore.config.Setting;
import nl.utwente.groove.explore.config.Setting.ContentType;
import nl.utwente.groove.gui.dialog.ConfigDialog;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Editor for the null content.
 * @author Arend Rensink
 * @version $Revision$
 */
public class NullEditor extends ContentEditor {
    /**
     * Constructs a null editor for a given exploration key and setting kind.
     * @param dialog the configuration dialog for which this is an editor
     */
    public NullEditor(ConfigDialog<?> dialog, JPanel holder, ExploreKey key, Setting.Key kind) {
        super(dialog, holder, key, kind);
        assert kind.contentType() == ContentType.NULL;
    }

    @Override
    public void refresh() {
        // does nothing
    }

    @Override
    public Setting getSetting() throws FormatException {
        return getKind().createSetting(Null.instance());
    }

    @Override
    public void setSetting(Setting content) {
        assert content.key() == getKind();
    }

    @Override
    public String getError() {
        return null;
    }
}

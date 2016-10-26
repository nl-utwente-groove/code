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

import javax.swing.JPanel;

import groove.explore.config.ExploreKey;
import groove.explore.config.Setting;
import groove.explore.config.SettingKey;
import groove.gui.dialog.ConfigDialog;
import groove.util.parse.FormatException;
import groove.util.parse.NullParser;

/**
 * Editor for the null content.
 * @author Arend Rensink
 * @version $Revision $
 */
public class NullEditor extends ContentEditor {
    /**
     * Constructs a null editor for a given exploration key and setting kind.
     * @param dialog the configuration dialog for which this is an editor
     */
    public NullEditor(ConfigDialog<?> dialog, JPanel holder, ExploreKey key, SettingKey kind) {
        super(dialog, holder, key, kind);
        assert kind.parser() instanceof NullParser;
    }

    @Override
    public void refresh() {
        // does nothing
    }

    @Override
    public Setting<?,?> getSetting() throws FormatException {
        return getKind().getDefaultSetting();
    }

    @Override
    public void setSetting(Setting<?,?> content) {
        assert content.getKind() == getKind();
    }

    @Override
    public String getError() {
        return null;
    }
}

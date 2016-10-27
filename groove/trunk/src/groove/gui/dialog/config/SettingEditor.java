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
import groove.gui.action.Refreshable;
import groove.util.parse.FormatException;

/**
 * Interface for the editor of a setting kind.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class SettingEditor extends JPanel implements Refreshable {
    /** Returns the explore key for which this is an editor. */
    abstract public ExploreKey getKey();

    /** Returns the setting kind for which this is an editor. */
    abstract public SettingKey getKind();

    /** Activates the editor. */
    abstract public void activate();

    /**
     * Returns the content according to the current state of the editor.
     * @throws FormatException if the current state cannot be parsed.
     */
    abstract public Setting<?,?> getSetting() throws FormatException;

    /** Fills the editor with a certain content. */
    abstract public void setSetting(Setting<?,?> content);

    /** Indicates that the editor is in an erroneous state. */
    public boolean hasError() {
        return getError() != null;
    }

    /** Returns the current error in the editor, if any. */
    abstract public String getError();
}

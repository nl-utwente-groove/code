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
 */
package nl.utwente.groove.gui.action;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.dialog.UserClassPathDialog;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.UserClassPath;

/**
 * Action for editing the installation-level user class path
 * (see {@link UserClassPath}). If the stored class path changes and a grammar
 * is loaded, the grammar is refreshed so that its user operations are
 * resolved against the new class path.
 * @author Arend Rensink
 */
@AIGenerated("Claude Opus 5, 2026-10")
@NonNullByDefault
public class UserClassPathAction extends SimulatorAction {
    /** Constructs an instance of the action, for a given simulator. */
    public UserClassPathAction(Simulator simulator) {
        super(simulator, Options.USER_CLASS_PATH_ACTION_NAME, null);
        // the action may be created after the last refresh, so enable it right away
        setEnabled(true);
    }

    @Override
    public void execute() {
        var stored = UserClassPath.getStored();
        var edited = new UserClassPathDialog(stored).showDialog(getFrame());
        if (edited != null && !edited.equals(stored)) {
            UserClassPath.setStored(edited);
            if (getGrammarModel() != null) {
                getActions().getRefreshGrammarAction().execute();
            }
        }
    }

    @Override
    public void refresh() {
        setEnabled(true);
    }
}

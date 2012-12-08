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
package groove.io.conceptual.configuration;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.action.SimulatorAction;
import groove.trans.ResourceKind;

import javax.swing.Icon;

public class ConfigAction extends SimulatorAction {
    public enum ConfigActionType {
        New("New", Icons.NEW_ICON),
        Save("Save", Icons.SAVE_ICON),
        Copy("Copy", Icons.COPY_ICON),
        Delete("Delete", Icons.DELETE_ICON),
        Rename("Rename", Icons.RENAME_ICON);

        private String m_text;
        private Icon m_icon;

        ConfigActionType(String text, Icon icon) {
            m_text = text;
            m_icon = icon;
        }

        public String getText() {
            return m_text;
        }

        public Icon getIcon() {
            return m_icon;
        }
    }

    private ConfigActionType m_type;
    private ConfigDialog m_dlg;

    protected ConfigAction(Simulator simulator, ConfigActionType type, ConfigDialog dlg) {
        super(simulator, type.getText(), type.getIcon(), null, ResourceKind.CONFIG);

        m_type = type;
        m_dlg = dlg;
    }

    @Override
    public void execute() {

        String modelName = null;
        switch (m_type) {
            case New:
                final String newName = askNewName(Options.getNewResourceName(getResourceKind()), true);
                if (newName == null) {
                    return;
                }
                modelName = newName;
                break;
            case Save:
                if (!m_dlg.hasModels()) {
                    // Go into save as mode and ask for name
                    final String saveName = askNewName(Options.getNewResourceName(getResourceKind()), true);
                    if (saveName == null) {
                        return;
                    }
                    modelName = saveName;
                }
                break;
            case Delete:
                break;
            case Rename:
                //TODO: find old name
                String oldName = "configuration";
                final String renameName = askNewName(oldName, false);
                if (renameName == null) {
                    return;
                }
                modelName = renameName;
                break;
            case Copy:
                final String copyName = askNewName(Options.getNewResourceName(getResourceKind()), true);
                if (copyName == null) {
                    return;
                }
                modelName = copyName;
                break;
        }

        m_dlg.executeAction(m_type, modelName);
    }

}

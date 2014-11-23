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

import groove.grammar.model.ResourceKind;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.action.SimulatorAction;

/** Actions to manipulate (save, copy, ect.) configurations. */
public class ConfigAction extends SimulatorAction {
    /** Constructs a new configuration manipulation action of a given type. */
    protected ConfigAction(Simulator simulator, Type type, ConfigDialog dlg) {
        super(simulator, type.getText(), type.getIcon(), null, ResourceKind.FORMAT);
        this.m_type = type;
        this.m_dlg = dlg;
    }

    private final Type m_type;
    private final ConfigDialog m_dlg;

    @Override
    public void execute() {
        String configName = null;
        switch (this.m_type) {
        case NEW:
            final String newName = askNewName(Options.getNewResourceName(getResourceKind()), true);
            if (newName == null) {
                return;
            }
            configName = newName;
            break;
        case SAVE:
            if (!this.m_dlg.hasConfigs()) {
                // Go into save as mode and ask for name
                final String saveName =
                    askNewName(Options.getNewResourceName(getResourceKind()), true);
                if (saveName == null) {
                    return;
                }
                configName = saveName;
            }
            break;
        case DELETE:
            break;
        case RENAME:
            //TODO: find old name
            String oldName = "configuration";
            final String renameName = askNewName(oldName, false);
            if (renameName == null) {
                return;
            }
            configName = renameName;
            break;
        case COPY:
            final String copyName = askNewName(Options.getNewResourceName(getResourceKind()), true);
            if (copyName == null) {
                return;
            }
            configName = copyName;
            break;
        }

        this.m_dlg.executeAction(this.m_type, configName);
    }

    /** Action type. */
    static public enum Type {
        /** Create a new configuration. */
        NEW("New", Icons.NEW_ICON),
        /** Save the current configuration. */
        SAVE("Save", Icons.SAVE_ICON),
        /** Copy the current configuration. */
        COPY("Copy", Icons.COPY_ICON),
        /** Delete the current configuration. */
        DELETE("Delete", Icons.DELETE_ICON),
        /** Rename the current configuration. */
        RENAME("Rename", Icons.RENAME_ICON);

        private String m_text;
        private groove.gui.Icons.Icon m_icon;

        Type(String text, groove.gui.Icons.Icon icon) {
            this.m_text = text;
            this.m_icon = icon;
        }

        /** Returns the name of this action. */
        public String getText() {
            return this.m_text;
        }

        /** Returns the icon used for this action. */
        public groove.gui.Icons.Icon getIcon() {
            return this.m_icon;
        }
    }

}

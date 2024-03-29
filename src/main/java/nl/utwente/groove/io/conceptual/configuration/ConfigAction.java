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
package nl.utwente.groove.io.conceptual.configuration;

import javax.swing.Icon;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.action.SimulatorAction;

/** Actions to manipulate configurations. */
public class ConfigAction extends SimulatorAction {
    private Type m_type;
    private ConfigDialog m_dlg;

    /** Constructs a new configuration manipulation action. */
    protected ConfigAction(Simulator simulator, Type type, ConfigDialog dlg) {
        super(simulator, type.getText(), type.getIcon(), null, ResourceKind.CONFIG);

        this.m_type = type;
        this.m_dlg = dlg;
    }

    @Override
    public void execute() {
        QualName modelName = switch (this.m_type) {
        case NEW -> askNewName(Options.getNewResourceName(getResourceKind()), true);
        case SAVE -> this.m_dlg.hasModels() ? null
            : askNewName(Options.getNewResourceName(getResourceKind()), true);
        case DELETE -> null;
        case RENAME ->
            //TODO: find old name
            askNewName("configuration", false);
        case COPY -> askNewName(Options.getNewResourceName(getResourceKind()), true);
        };
        if (modelName != null) {
            this.m_dlg.executeAction(this.m_type, modelName);
        }
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
        private Icon m_icon;

        Type(String text, Icon icon) {
            this.m_text = text;
            this.m_icon = icon;
        }

        /** Returns the name of this action. */
        public String getText() {
            return this.m_text;
        }

        /** Returns the icon used for this action. */
        public Icon getIcon() {
            return this.m_icon;
        }
    }

}

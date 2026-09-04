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
 */
package nl.utwente.groove.gui.view;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.accessibility.AccessibleState;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Listener that refreshes a canvas when a display option changes value or enabledness.
 * Subclasses may override {@link #doRefresh()} for options that need more than a refresh.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class OptionRefreshListener implements ItemListener, PropertyChangeListener {
    /** Constructs a listener for a given canvas. */
    public OptionRefreshListener(GraphCanvas<?> canvas) {
        this.canvas = canvas;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (this.canvas.isEnabled()) {
            doRefresh();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(AccessibleState.ENABLED.toDisplayString())
            && this.canvas.isEnabled()) {
            doRefresh();
        }
    }

    /** Callback method invoked when the option changed; does nothing if the canvas has no model. */
    protected void doRefresh() {
        var viewModel = this.canvas.getViewModel();
        if (viewModel != null) {
            viewModel.refreshVisuals();
            this.canvas.refreshAll(true);
        }
    }

    /** Returns the canvas refreshed by this listener. */
    protected GraphCanvas<?> getCanvas() {
        return this.canvas;
    }

    private final GraphCanvas<?> canvas;
}

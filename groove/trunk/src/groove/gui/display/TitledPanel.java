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
package groove.gui.display;

import static groove.io.HTMLConverter.HTML_TAG;
import static groove.io.HTMLConverter.NBSP;
import static groove.io.HTMLConverter.STRONG_TAG;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

/** Optionally titled decoration of another component. */
public class TitledPanel extends JPanel {
    /** Creates a label panel for a given inner component.
     * @param name initial title of the component
     * @param inner the inner component; non-{@code null}
     * @param toolBar the optional tool bar; may be {@code null}
     * @param scroll flag indicating if the inner component should be put inside a {@link JScrollPane}
     */
    public TitledPanel(String name, JComponent inner, JToolBar toolBar,
            boolean scroll) {
        super(new BorderLayout(), false);
        setBorder(null);
        this.titleLabel = new JLabel();
        this.titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        setName(name);
        this.inner = inner;
        this.labelPanelTop = Box.createVerticalBox();
        this.labelPanelTop.add(this.titleLabel);
        if (toolBar != null) {
            toolBar.setAlignmentX(LEFT_ALIGNMENT);
            this.labelPanelTop.add(toolBar);
        }
        add(this.labelPanelTop, BorderLayout.NORTH);
        add(scroll ? new JScrollPane(inner) : inner, BorderLayout.CENTER);
    }

    /** Adds or removes the panel title. */
    public void setTitled(boolean titled) {
        if (titled) {
            this.labelPanelTop.add(this.titleLabel, 0);
        } else {
            this.labelPanelTop.remove(this.titleLabel);
        }
    }

    /**
     * Sets the title of this panel to a given text.
     * The text is set bold.
     */
    @Override
    public void setName(String name) {
        super.setName(name);
        this.titleLabel.setText(HTML_TAG.on(NBSP + STRONG_TAG.on(name)));
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.inner.setEnabled(enabled);
        if (this.enabledBackground != null) {
            this.inner.setBackground(enabled ? this.enabledBackground : null);
        }
    }

    /** Sets the background colour for the enabled state. */
    public void setEnabledBackground(Color background) {
        this.enabledBackground = background;
        if (background != null && this.inner.isEnabled()) {
            this.inner.setBackground(background);
        }
    }

    private final Box labelPanelTop;
    private final JLabel titleLabel;
    private final JComponent inner;
    private Color enabledBackground;
}
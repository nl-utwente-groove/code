/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.gui;

import groove.gui.jgraph.JAttr;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Tab component for displays that can be detached.
 * Contains a JLabel to show the text and 
 * a JButton to close the tab it belongs to.
 * This is modified from a Java Swing demo.
 * 
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class DetachTabLabel extends JPanel {
    /** 
     * Creates a new component, for a given pane. 
     */
    public DetachTabLabel(DisplaysPanel panel, Display display, Icon icon,
            String title) {
        //unset default FlowLayout' gaps
        super(new FlowLayout(FlowLayout.LEFT, 1, 0));
        this.display = display;
        this.panel = panel;
        setOpaque(false);
        setBorder(null);
        this.iconLabel = new JLabel(title, icon, 0);
        this.iconLabel.setBackground(JAttr.ERROR_COLOR);
        this.iconLabel.setBorder(null);
        this.iconLabel.setFont(this.iconLabel.getFont().deriveFont(Font.BOLD));
        this.unpinButton = new UnpinButton();
        add(this.iconLabel);
        //tab button
        add(this.unpinButton);
    }

    /** Changes the title of the tab. */
    public void setTitle(String title) {
        this.iconLabel.setText(title);
        if (title == null) {
            remove(this.unpinButton);
        } else {
            add(this.unpinButton);
        }
    }

    /** Visually displays the error property. */
    public void setError(boolean error) {
        this.iconLabel.setOpaque(error);
        this.repaint();
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.iconLabel.setEnabled(enabled);
    }

    /** Returns the label showing the icon and text. */
    public JLabel getLabel() {
        return this.iconLabel;
    }

    /** The panel on which the display is shown. */
    private final DisplaysPanel panel;
    /** The editor panel in this tab. */
    private final Display display;
    /** The label that the icon is displayed on. */
    private final JLabel iconLabel;
    /** The unpin button of this tab component. */
    private final UnpinButton unpinButton;

    /** Cancel button. */
    private class UnpinButton extends JButton implements ActionListener {
        public UnpinButton() {
            int size = 17;
            setPreferredSize(new Dimension(size, size));
            setToolTipText("Unpin display");
            //Make the button looks the same for all Laf's
            setUI(new BasicButtonUI());
            //Make it transparent
            setContentAreaFilled(false);
            //No need to be focusable
            setFocusable(false);
            setIcon(Icons.PIN_ICON);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            //Making nice rollover effect
            //we use the same listener for all buttons
            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);
            //Close the proper tab by clicking the button
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            DetachTabLabel.this.panel.detach(DetachTabLabel.this.display);
        }

        //we don't want to update UI for this button
        @Override
        public void updateUI() {
            //
        }
    }

    /** Listener that arms any {@link UnpinButton} that the mouse comes over. */
    private final static MouseListener buttonMouseListener =
        new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                Component component = e.getComponent();
                if (component instanceof AbstractButton) {
                    AbstractButton button = (AbstractButton) component;
                    button.setBorderPainted(true);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                Component component = e.getComponent();
                if (component instanceof AbstractButton) {
                    AbstractButton button = (AbstractButton) component;
                    button.setBorderPainted(false);
                }
            }
        };
}

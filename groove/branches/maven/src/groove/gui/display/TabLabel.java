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
package groove.gui.display;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicButtonUI;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.list.ListTabbedPane;
import groove.gui.look.Values;
import groove.io.HTMLConverter;

/**
 * Component to be used as tabComponent;
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
public class TabLabel extends JPanel {
    /**
     * Creates a new tab label.
     * @param tabKind the kind of tab label
     * @param icon icon for the tab label
     * @param title text for the tab label
     */
    private TabLabel(Kind tabKind, Icon icon, String title, boolean button) {
        super(new FlowLayout(FlowLayout.LEFT, 1, 0));
        setOpaque(false);
        setBorder(null);
        this.kind = tabKind;
        this.hasButton = button;
        this.iconLabel = new JLabel(title, icon, SwingConstants.LEFT);
        this.iconLabel.setBackground(Values.ERROR_COLOR);
        this.iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, tabKind.getHGap()));
        if (tabKind != Kind.RESOURCE) {
            this.iconLabel.setFont(this.iconLabel.getFont()
                .deriveFont(Font.BOLD));
        }
        add(this.iconLabel);
        if (button && title != null) {
            add(getButton());
        }
    }

    /**
     * Creates a new component, for a given resource tab.
     */
    public TabLabel(ResourceTab tab, Icon icon, String title) {
        this(Kind.RESOURCE, icon, title, tab.isEditor());
        this.tab = tab;
    }

    /**
     * Creates a new component, for a given display.
     */
    public TabLabel(DisplaysPanel parent, Display display, Icon icon, String title) {
        this(Kind.DISPLAY, icon, title, true);
        this.display = display;
        this.parent = parent;
    }

    /**
     * Creates new component for the state tab.
     */
    public TabLabel(Display display, ResourceTab tab, Icon icon, String title) {
        this(Kind.STATE, icon, title, false);
        this.display = display;
    }

    /**
     * Creates new component for the state tab.
     */
    public TabLabel(ListTabbedPane parent, Icon icon, String title) {
        this(Kind.LIST, icon, title, true);
        this.parent = parent;
    }

    /** Changes the title of the tab. */
    public void setTitle(String title) {
        this.iconLabel.setText(title == null ? null : HTMLConverter.HTML_TAG.on(title));
        if (title == null) {
            remove(getButton());
        } else if (this.hasButton) {
            add(getButton());
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

    /** Callback factory method for the button on the tab label. */
    protected JButton getButton() {
        if (this.button == null) {
            this.button = new TabButton();
        }
        return this.button;
    }

    /** Performs the action for the button on the tab label. */
    protected void doButtonAction() {
        switch (this.kind) {
        case RESOURCE:
            this.tab.saveEditor(true, true);
            break;
        case DISPLAY:
            ((DisplaysPanel) this.parent).detach(this.display);
            break;
        case LIST:
            ((ListTabbedPane) this.parent).closeSearchTab();
            break;
        default:
            // do nothing
        }
    }

    /** The label that the icon is displayed on. */
    private final JLabel iconLabel;
    /** The kind of tab label. */
    private final Kind kind;
    /** Flag indicating that this tab label should have a button. */
    private final boolean hasButton;
    private TabButton button;
    /** The editor panel in this tab. */
    private ResourceTab tab;
    /** The panel on which the display is shown. */
    private JTabbedPane parent;
    /** The editor panel in this tab. */
    private Display display;

    /** Cancel button. */
    private class TabButton extends JButton implements ActionListener {
        public TabButton() {
            int size = 17;
            setPreferredSize(new Dimension(size, size));
            setToolTipText(TabLabel.this.kind.getName());
            //Make the button looks the same for all Laf's
            setUI(new BasicButtonUI());
            //Make it transparent
            setContentAreaFilled(false);
            //No need to be focusable
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            //Making nice rollover effect
            //we use the same listener for all buttons
            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);
            //Close the proper tab by clicking the button
            addActionListener(this);
            if (TabLabel.this.kind != Kind.RESOURCE && TabLabel.this.kind != Kind.LIST) {
                setIcon(Icons.PIN_ICON);
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            doButtonAction();
        }

        //we don't want to update UI for this button
        @Override
        public void updateUI() {
            // empty
        }

        //paint the cross
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (TabLabel.this.kind == Kind.RESOURCE || TabLabel.this.kind == Kind.LIST) {
                Graphics2D g2 = (Graphics2D) g.create();
                //shift the image for pressed buttons
                if (getModel().isPressed()) {
                    g2.translate(1, 1);
                }
                g2.setStroke(new BasicStroke(2));
                g2.setColor(Color.BLACK);
                if (getModel().isRollover()) {
                    g2.setColor(Color.MAGENTA);
                }
                int delta = 6;
                g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
                g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
                g2.dispose();
            }
        }
    }

    private static enum Kind {
        RESOURCE(1, Options.CANCEL_EDIT_ACTION_NAME),
        DISPLAY(3, Options.DETACH_ACTION_NAME),
        STATE(5, Options.DETACH_ACTION_NAME),
        LIST(5, "Close");

        private Kind(int hGap, String name) {
            this.hGap = hGap;
            this.name = name;
        }

        /** Returns the horizontal gap between label and tab button. */
        public int getHGap() {
            return this.hGap;
        }

        /** Returns the horizontal gap between label and tab button. */
        public String getName() {
            return this.name;
        }

        private final int hGap;
        private final String name;
    }

    /** Listener that arms any {@link TabButton} that the mouse comes over. */
    private final static MouseListener buttonMouseListener = new MouseAdapter() {
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

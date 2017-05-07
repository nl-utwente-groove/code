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
package groove.io.external.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

/** Dialog to select options for saving as PDF. */
public class PdfDialog extends JDialog {
    private JCheckBox m_outlineFont;
    private boolean m_dialogResult;

    /** Creates a new dialog. */
    public PdfDialog(JFrame owner) {
        super(owner, "Select graphs to export", true);

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                close();
            }
        });
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setVisible(false);
            }
        };
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        this.getRootPane()
            .registerKeyboardAction(actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        buildGUI();
    }

    /** Invokes the dialog. */
    public boolean doDialog() {
        this.m_dialogResult = false;
        setVisible(true);
        return this.m_dialogResult;
    }

    /** Builds the dialog GUI. */
    private void buildGUI() {
        this.m_outlineFont = new JCheckBox("Use font outlines (larger PDF, more accurate)");

        JPanel form = new JPanel(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(4, 4, 4, 4);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 0;
        form.add(this.m_outlineFont, c);

        //SpringUtilities.makeCompactGrid(form, 3, 2, 6, 6, 6, 6);

        JPanel contents = new JPanel(new BorderLayout());
        contents.add(form, BorderLayout.NORTH);

        JButton okBtn = new JButton("OK");
        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PdfDialog.this.m_dialogResult = true;
                PdfDialog.this.dispose();
            }
        });
        this.getRootPane()
            .setDefaultButton(okBtn);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PdfDialog.this.dispose();
            }
        });

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(okBtn);
        buttonPane.add(Box.createRigidArea(new Dimension(5, 0)));
        buttonPane.add(cancelBtn);
        contents.add(buttonPane, BorderLayout.SOUTH);

        this.setContentPane(contents);

        this.setSize(350, 150);
    }

    /** Indicates if the option to use font outline was selected. */
    public boolean useFontOutline() {
        return this.m_outlineFont.isSelected();
    }

    private void close() {
        super.dispose();
    }
}

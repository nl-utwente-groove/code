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
package groove.io.conceptual.lang.groove;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class GrammarDialog extends JDialog {
    private Frame m_parent;
    private JComboBox m_typeList;
    private JComboBox m_metaList;
    private JComboBox m_instanceList;

    private boolean m_dialogResult;

    public GrammarDialog(Frame parent) {
        super(parent, "Select graphs to export", true);
        m_parent = parent;

        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                close();
            }
        });
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                setVisible(false);
            }
        };
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        this.getRootPane().registerKeyboardAction(actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        buildGUI();
    }

    public boolean doDialog() {
        m_dialogResult = false;
        setLocationRelativeTo(m_parent);
        setVisible(true);
        return m_dialogResult;
    }

    private void buildGUI() {
        m_typeList = new JComboBox();
        m_metaList = new JComboBox();
        m_instanceList = new JComboBox();

        JLabel typeLabel = new JLabel("Select type graph:", JLabel.TRAILING);
        typeLabel.setLabelFor(m_typeList);
        JLabel metaLabel = new JLabel("Select meta graph:", JLabel.TRAILING);
        metaLabel.setLabelFor(m_metaList);
        JLabel instanceLabel = new JLabel("Select instance graph:", JLabel.TRAILING);
        instanceLabel.setLabelFor(m_instanceList);

        JPanel form = new JPanel(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(4, 4, 4, 4);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 0;
        form.add(typeLabel, c);
        c.weightx = 1;
        c.gridx = 1;
        c.gridy = 0;
        form.add(m_typeList, c);
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 1;
        form.add(metaLabel, c);
        c.gridx = 1;
        c.gridy = 1;
        form.add(m_metaList, c);
        c.gridx = 0;
        c.gridy = 2;
        form.add(instanceLabel, c);
        c.gridx = 1;
        c.gridy = 2;
        form.add(m_instanceList, c);

        //SpringUtilities.makeCompactGrid(form, 3, 2, 6, 6, 6, 6);

        JPanel contents = new JPanel(new BorderLayout());
        contents.add(form, BorderLayout.NORTH);

        JButton okBtn = new JButton("OK");
        okBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                m_dialogResult = true;
                GrammarDialog.this.dispose();
            }
        });
        this.getRootPane().setDefaultButton(okBtn);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                m_typeList.removeAllItems();
                m_metaList.removeAllItems();
                m_instanceList.removeAllItems();
                GrammarDialog.this.dispose();
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

    public void setTypeModels(Set<String> typeModelNames) {
        m_typeList.removeAllItems();

        for (String type : typeModelNames) {
            m_typeList.addItem(type);
        }
    }

    public void setMetaModels(Set<String> metaModelNames) {
        m_metaList.removeAllItems();
        m_metaList.addItem("");

        for (String type : metaModelNames) {
            m_metaList.addItem(type);
        }
        if (metaModelNames.size() > 0) {
            m_metaList.setSelectedIndex(1);
        }
    }

    public void setInstanceModels(Set<String> instanceModelNames, boolean force) {
        m_instanceList.removeAllItems();
        if (!force) {
            m_instanceList.addItem("");
        }

        for (String type : instanceModelNames) {
            m_instanceList.addItem(type);
        }
        if (!force && instanceModelNames.size() > 0) {
            m_instanceList.setSelectedIndex(1);
        }
    }

    public String getTypeModel() {
        return (String) m_typeList.getSelectedItem();
    }

    public String getMetaModel() {
        return (String) m_metaList.getSelectedItem();
    }

    public String getInstanceModel() {
        return (String) m_instanceList.getSelectedItem();
    }

    private void close() {
        super.dispose();
    }
}

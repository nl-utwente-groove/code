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

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.IOException;

import javax.swing.JPanel;

import org.apache.xerces.xni.parser.XMLParseException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.jaxfront.core.dom.DOMBuilder;
import com.jaxfront.core.dom.DOMHelper;
import com.jaxfront.core.dom.DocumentCreationException;
import com.jaxfront.core.schema.SchemaCreationException;
import com.jaxfront.core.schema.ValidationException;
import com.jaxfront.swing.ui.editor.EditorPanel;
import com.jaxfront.swing.ui.editor.TypeWorkspace;
import com.jaxfront.swing.ui.wrapper.JAXJSplitPane;

import groove.grammar.QualName;
import groove.gui.Simulator;

/** Dialog for constructing and saving configurations. */
public class JaxFrontDialog extends ConfigDialog {
    private JPanel m_panel;
    private EditorPanel m_editor;
    private Component m_panelComponent;
    // Store the default Swing tooltip class, so the broken JaxFront version wont interfere after dialog is closed
    private static Object s_tooltipObj;

    static {
        s_tooltipObj = javax.swing.UIManager.get("ToolTipUI");
    }

    /** Constructs a fresh dialog, for a given simulator. */
    public JaxFrontDialog(Simulator simulator) {
        super(simulator);
    }

    @Override
    public QualName getConfig() {
        QualName result = super.getConfig();
        // After closing the dialog, restore the tooltip class
        javax.swing.UIManager.put("ToolTipUI", s_tooltipObj);
        return result;
    }

    @Override
    protected JPanel getXMLPanel() {
        this.m_panel = new JPanel();
        this.m_panel.setLayout(new BorderLayout());

        newModel();

        return this.m_panel;
    }

    private void setDocument(com.jaxfront.core.dom.Document doc) {
        if (this.m_panelComponent != null) {
            this.m_panel.remove(this.m_panelComponent);
        }
        this.m_editor = new EditorPanel(doc.getRootType(), null);
        this.m_panelComponent = this.m_editor;
        try {
            JAXJSplitPane pane = (JAXJSplitPane) this.m_editor.getComponent(0);
            TypeWorkspace space = (TypeWorkspace) pane.getRightComponent();
            space.getButtonBar()
                .setVisible(false);
            space.getHeaderPanel()
                .setVisible(false);
            space.getMessageTablePanel()
                .setVisible(false);

            this.m_panelComponent = pane.getRightComponent();
            this.m_panel.add(this.m_panelComponent);
        } catch (ClassCastException ex) {
            // In case of an exception (the UI is changed) just add the editor itself
            // nothing to do here
        } catch (ArrayIndexOutOfBoundsException ex) {
            // nothing to do here
        }
        this.m_panel.add(this.m_panelComponent);
        this.m_panel.validate();
        this.m_panel.repaint();
    }

    @Override
    protected void newModel() {
        try {
            com.jaxfront.core.dom.Document dom = DOMBuilder.getInstance()
                .build(null, this.m_schemaURL, "configuration");
            setDocument(dom);
        } catch (SchemaCreationException e) {
            // Silently catch error
        } catch (DocumentCreationException e) {
            // Silently catch error
        }
    }

    @Override
    protected void loadModel(String xmlString) throws ConfigurationException {
        try {
            Document xmlDoc = DOMHelper.createDocument(xmlString);
            com.jaxfront.core.dom.Document doc = DOMBuilder.getInstance()
                .build(null, this.m_schemaURL, xmlDoc, null, "configuration");
            setDocument(doc);
        } catch (SAXException e) {
            throw new ConfigurationException(e);
        } catch (IOException e) {
            throw new ConfigurationException(e);
        } catch (SchemaCreationException e) {
            throw new ConfigurationException(e);
        } catch (DocumentCreationException e) {
            throw new ConfigurationException(e);
        }
    }

    @Override
    protected Document getDocument() throws ConfigurationException {
        try {
            return this.m_editor.getDOM()
                .serializeToW3CDocument();
        } catch (XMLParseException e) {
            throw new ConfigurationException(e);
        } catch (SAXException e) {
            throw new ConfigurationException(e);
        } catch (IOException e) {
            throw new ConfigurationException(e);
        } catch (ValidationException e) {
            throw new ConfigurationException(e);
        }
    }

}

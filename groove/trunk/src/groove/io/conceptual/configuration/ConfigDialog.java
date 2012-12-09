package groove.io.conceptual.configuration;

import groove.gui.Simulator;
import groove.gui.dialog.ErrorDialog;
import groove.io.conceptual.configuration.ConfigAction.ConfigActionType;
import groove.trans.ResourceKind;
import groove.view.FormatException;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

//ActionListener: change selection
//TODO: add area for exception messages, many errors are silently dropped
public abstract class ConfigDialog extends JDialog implements ActionListener {
    protected Simulator m_simulator;

    protected URL m_schemaURL;
    protected String m_activeModel;
    protected String m_selectedModel;

    private JComboBox m_configsList;
    // True if combobox events should be ignored
    private boolean m_ignoreCombobox = false;

    // Various actions for toolbar buttons
    private Action m_newAction;
    private Action m_saveAction;
    private Action m_copyAction;
    private Action m_deleteAction;
    private Action m_renameAction;

    public ConfigDialog(Simulator simulator) {
        super(simulator.getFrame(), "Config Dialog", true);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        // Make sure that closeDialog is called whenever the dialog is closed.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                ConfigDialog.this.m_selectedModel = null;
                close();
            }
        });

        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                setVisible(false);
            }
        };
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        this.getRootPane().registerKeyboardAction(actionListener, stroke,
            JComponent.WHEN_IN_FOCUSED_WINDOW);

        this.m_simulator = simulator;

        buildGUI();

        setSize(800, 600);
    }

    public String getConfig() {
        this.setLocationRelativeTo(this.m_simulator.getFrame());
        this.m_selectedModel = null;
        setVisible(true);

        if (hasModels()) {
            return this.m_selectedModel;
        }
        return null;
    }

    private void close() {
        super.dispose();
    }

    private void loadActions() {
        this.m_newAction = getAction(ConfigActionType.New);
        this.m_saveAction = getAction(ConfigActionType.Save);
        this.m_copyAction = getAction(ConfigActionType.Copy);
        this.m_deleteAction = getAction(ConfigActionType.Delete);
        this.m_renameAction = getAction(ConfigActionType.Rename);
    }

    private void buildGUI() {
        this.m_schemaURL =
            this.getClass().getClassLoader().getResource(Config.g_xmlSchema);
        if (this.m_schemaURL == null) {
            throw new RuntimeException(
                "Unable to load the XML schema resource " + Config.g_xmlSchema);
        }

        loadActions();

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        toolBar.add(this.m_newAction);
        toolBar.addSeparator();

        this.m_configsList = new JComboBox();
        this.m_configsList.setEditable(false);
        this.m_configsList.addActionListener(this);
        toolBar.add(this.m_configsList);
        toolBar.add(this.m_saveAction);
        toolBar.addSeparator();

        toolBar.add(this.m_copyAction);
        toolBar.add(this.m_deleteAction);
        toolBar.add(this.m_renameAction);

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(toolBar, BorderLayout.NORTH);

        this.getContentPane().add(getXMLPanel(), BorderLayout.CENTER);

        JButton okBtn = new JButton("OK");
        okBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ConfigDialog.this.m_selectedModel =
                    ConfigDialog.this.m_activeModel;
                ((ConfigAction) ConfigDialog.this.m_saveAction).execute();
                ConfigDialog.this.dispose();
            }
        });
        this.getRootPane().setDefaultButton(okBtn);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ConfigDialog.this.m_selectedModel = null;
                ConfigDialog.this.dispose();
            }
        });
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(okBtn);
        buttonPane.add(Box.createRigidArea(new Dimension(5, 0)));
        buttonPane.add(cancelBtn);
        this.getContentPane().add(buttonPane, BorderLayout.SOUTH);

        if (hasModels()) {
            this.m_activeModel =
                this.m_simulator.getModel().getGrammar().getNames(
                    ResourceKind.CONFIG).iterator().next();
        }

        refreshGUI();
        loadModel();
    }

    protected abstract JPanel getXMLPanel();

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (this.m_ignoreCombobox) {
            return;
        }
        if (ae.getSource() == this.m_configsList) {
            Object current = this.m_configsList.getSelectedItem();
            if (!current.equals(this.m_activeModel)) {
                this.m_activeModel = (String) current;
                loadModel();
            }
        }
    }

    private Action getAction(ConfigActionType type) {
        Action newAct = new ConfigAction(this.m_simulator, type, this);
        newAct.setEnabled(true);
        return newAct;
    }

    private void refreshGUI() {
        this.m_renameAction.setEnabled(hasModels());
        this.m_copyAction.setEnabled(hasModels());
        this.m_deleteAction.setEnabled(hasModels());

        refreshList();
    }

    private void refreshList() {
        this.m_ignoreCombobox = true;
        this.m_configsList.removeAllItems();

        Set<String> names =
            this.m_simulator.getModel().getGrammar().getNames(
                ResourceKind.CONFIG);

        if (!hasModels()) {
            final String newStr = new String("<New>");
            this.m_configsList.addItem(newStr);
            this.m_configsList.setSelectedItem(newStr);

            this.m_ignoreCombobox = false;
            return;
        }

        Object[] nameArray = names.toArray();
        Arrays.sort(nameArray);
        for (Object name : nameArray) {
            this.m_configsList.addItem(name);
            if (name.equals(this.m_activeModel)) {
                this.m_configsList.setSelectedItem(name);
            }
        }

        this.m_ignoreCombobox = false;
    }

    public void executeAction(ConfigActionType type, String modelName) {
        try {
            switch (type) {
            case New:
                this.m_activeModel = modelName;
                newModel();
                // Immediately save model with current model name
                saveModel();
                refreshGUI();
                break;
            case Save:
                if (!hasModels()) {
                    this.m_activeModel = modelName;
                    saveModel();
                    refreshGUI();
                } else {
                    saveModel();
                }
                break;
            case Delete:
                if (!hasModels()) {
                    return;
                }

                try {
                    this.m_simulator.getModel().getStore().deleteTexts(
                        ResourceKind.CONFIG,
                        Collections.singletonList(this.m_activeModel));

                    if (!hasModels()) {
                        this.m_activeModel = null;
                    } else {
                        this.m_activeModel =
                            this.m_simulator.getModel().getGrammar().getNames(
                                ResourceKind.CONFIG).iterator().next();
                    }
                } catch (IOException e) {
                    new ErrorDialog(this.m_simulator.getFrame(),
                        "Error deleting configuration", e).setVisible(true);
                }
                refreshGUI();
                loadModel();
                break;
            case Rename:
                if (!hasModels()) {
                    return;
                }
                try {
                    this.m_simulator.getModel().getStore().rename(
                        ResourceKind.CONFIG, this.m_activeModel, modelName);
                    this.m_activeModel = modelName;
                } catch (IOException e) {
                    new ErrorDialog(this.m_simulator.getFrame(),
                        "Error renaming configuration", e).setVisible(true);
                }
                refreshGUI();
                loadModel();
                break;
            case Copy:
                if (!hasModels()) {
                    return;
                }
                String xmlString;
                try {
                    xmlString =
                        (String) this.m_simulator.getModel().getGrammar().getResource(
                            ResourceKind.CONFIG, this.m_activeModel).toResource();
                    this.m_simulator.getModel().getStore().putTexts(
                        ResourceKind.CONFIG,
                        Collections.singletonMap(modelName, xmlString));
                    this.m_activeModel = modelName;
                } catch (FormatException e) {
                    // FormatException not applicable to CONFIG resources
                    return;
                } catch (IOException e) {
                    new ErrorDialog(this.m_simulator.getFrame(),
                        "Error copying configuration", e).setVisible(true);
                }
                refreshGUI();
                loadModel();
                break;
            }
        } catch (ConfigurationException e) {
            //TODO:
            // Silently catch error. The dialog should have message area or something for this
        }
    }

    protected abstract void newModel();

    private void loadModel() {
        if (!hasModels()) {
            newModel();
            return;
        }

        String xmlString = null;
        try {
            xmlString =
                (String) this.m_simulator.getModel().getGrammar().getResource(
                    ResourceKind.CONFIG, this.m_activeModel).toResource();
        } catch (FormatException e) {
            // FormatException not applicable to CONFIG resources
            return;
        }

        // Do something with xmlString
        try {
            loadModel(xmlString);
        } catch (ConfigurationException e) {
            // Not much that can be done here, silently catch the error
        }
    }

    protected abstract void loadModel(String xmlString)
        throws ConfigurationException;

    private void saveModel() throws ConfigurationException {
        Document doc = getDocument();

        Transformer transformer = null;
        Exception exc = null;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // Allow indenting
            // The following line is specific to apache xalan. Since indenting is not really required anyway, commented out
            // See also http://stackoverflow.com/questions/1264849
            //transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            // Transform to string
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new StringWriter());
            transformer.transform(source, result);
            String xmlString = result.getWriter().toString();

            this.m_simulator.getModel().getStore().putTexts(
                ResourceKind.CONFIG,
                Collections.singletonMap(this.m_activeModel, xmlString));
        } catch (TransformerConfigurationException e) {
            exc = e;
        } catch (IOException e) {
            exc = e;
        } catch (TransformerException e) {
            exc = e;
        }
        if (exc != null) {
            new ErrorDialog(this.m_simulator.getFrame(),
                "Error saving configuration resource " + this.m_activeModel,
                exc).setVisible(true);
        }
    }

    protected abstract Document getDocument() throws ConfigurationException;

    public boolean hasModels() {
        Set<String> names =
            this.m_simulator.getModel().getGrammar().getNames(
                ResourceKind.CONFIG);
        return names.size() > 0;
    }
}

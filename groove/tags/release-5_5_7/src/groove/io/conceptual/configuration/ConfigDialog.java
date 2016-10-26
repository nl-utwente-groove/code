package groove.io.conceptual.configuration;

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
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

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
import javax.swing.WindowConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import groove.grammar.QualName;
import groove.grammar.model.ResourceKind;
import groove.gui.Simulator;
import groove.gui.dialog.ErrorDialog;
import groove.util.parse.FormatException;

//ActionListener: change selection
//TODO: add area for exception messages, many errors are silently dropped
@SuppressWarnings("javadoc")
public abstract class ConfigDialog extends JDialog implements ActionListener {
    protected final Simulator m_simulator;

    protected URL m_schemaURL;
    protected QualName m_activeModel;
    protected QualName m_selectedModel;

    private JComboBox<String> m_configsList;
    // True if combobox events should be ignored
    private boolean m_ignoreCombobox = false;

    public ConfigDialog(Simulator simulator) {
        super(simulator.getFrame(), "Config Dialog", true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        // Make sure that closeDialog is called whenever the dialog is closed.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                ConfigDialog.this.m_selectedModel = null;
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

        this.m_simulator = simulator;

        buildGUI();

        setSize(800, 600);
    }

    /** Invokes the dialog, and returns the name of the selected configuration model. */
    public QualName getConfig() {
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

    private void buildGUI() {
        this.m_schemaURL = this.getClass()
            .getClassLoader()
            .getResource(Config.CONFIG_SCHEMA);
        if (this.m_schemaURL == null) {
            throw new RuntimeException(
                "Unable to load the XML schema resource " + Config.CONFIG_SCHEMA);
        }

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        toolBar.add(getAction(ConfigAction.Type.NEW));
        toolBar.addSeparator();

        this.m_configsList = new JComboBox<>();
        this.m_configsList.setEditable(false);
        this.m_configsList.addActionListener(this);
        toolBar.add(this.m_configsList);
        toolBar.add(getAction(ConfigAction.Type.SAVE));
        toolBar.addSeparator();

        toolBar.add(getAction(ConfigAction.Type.COPY));
        toolBar.add(getAction(ConfigAction.Type.DELETE));
        toolBar.add(getAction(ConfigAction.Type.RENAME));

        this.getContentPane()
            .setLayout(new BorderLayout());
        this.getContentPane()
            .add(toolBar, BorderLayout.NORTH);

        this.getContentPane()
            .add(getXMLPanel(), BorderLayout.CENTER);

        JButton okBtn = new JButton("OK");
        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getAction(ConfigAction.Type.SAVE).execute();
                ConfigDialog.this.m_selectedModel = ConfigDialog.this.m_activeModel;
                ConfigDialog.this.dispose();
            }
        });
        this.getRootPane()
            .setDefaultButton(okBtn);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(new ActionListener() {
            @Override
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
        this.getContentPane()
            .add(buttonPane, BorderLayout.SOUTH);

        if (hasModels()) {
            this.m_activeModel = this.m_simulator.getModel()
                .getGrammar()
                .getNames(ResourceKind.CONFIG)
                .iterator()
                .next();
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
                this.m_activeModel = QualName.parse((String) current);
                loadModel();
            }
        }
    }

    private ConfigAction getAction(ConfigAction.Type type) {
        ConfigAction result = this.actionMap.get(type);
        if (result == null) {
            this.actionMap.put(type, result = new ConfigAction(this.m_simulator, type, this));
            result.setEnabled(true);
        }
        return result;
    }

    private final Map<ConfigAction.Type,ConfigAction> actionMap =
        new EnumMap<>(ConfigAction.Type.class);

    private void refreshGUI() {
        getAction(ConfigAction.Type.RENAME).setEnabled(hasModels());
        getAction(ConfigAction.Type.COPY).setEnabled(hasModels());
        getAction(ConfigAction.Type.DELETE).setEnabled(hasModels());

        refreshList();
    }

    private void refreshList() {
        this.m_ignoreCombobox = true;
        this.m_configsList.removeAllItems();

        Set<QualName> names = this.m_simulator.getModel()
            .getGrammar()
            .getNames(ResourceKind.CONFIG);

        if (!hasModels()) {
            final String newStr = new String("<New>");
            this.m_configsList.addItem(newStr);
            this.m_configsList.setSelectedItem(newStr);

            this.m_ignoreCombobox = false;
            return;
        }

        QualName[] nameArray = names.toArray(new QualName[0]);
        Arrays.sort(nameArray);
        for (QualName name : nameArray) {
            this.m_configsList.addItem(name.toString());
            if (name.equals(this.m_activeModel)) {
                this.m_configsList.setSelectedItem(name);
            }
        }

        this.m_ignoreCombobox = false;
    }

    /** Carries out the consequences of a given action. */
    public void executeAction(ConfigAction.Type type, QualName modelName) {
        try {
            switch (type) {
            case NEW:
                this.m_activeModel = modelName;
                newModel();
                // Immediately save model with current model name
                saveModel();
                refreshGUI();
                break;
            case SAVE:
                if (!hasModels()) {
                    this.m_activeModel = modelName;
                    saveModel();
                    refreshGUI();
                } else {
                    saveModel();
                }
                break;
            case DELETE:
                if (!hasModels()) {
                    return;
                }

                try {
                    this.m_simulator.getModel()
                        .getStore()
                        .deleteTexts(ResourceKind.CONFIG,
                            Collections.singletonList(this.m_activeModel));

                    if (!hasModels()) {
                        this.m_activeModel = null;
                    } else {
                        this.m_activeModel = this.m_simulator.getModel()
                            .getGrammar()
                            .getNames(ResourceKind.CONFIG)
                            .iterator()
                            .next();
                    }
                } catch (IOException e) {
                    new ErrorDialog(this.m_simulator.getFrame(), "Error deleting configuration", e)
                        .setVisible(true);
                }
                refreshGUI();
                loadModel();
                break;
            case RENAME:
                if (!hasModels()) {
                    return;
                }
                try {
                    this.m_simulator.getModel()
                        .getStore()
                        .rename(ResourceKind.CONFIG, this.m_activeModel, modelName);
                    this.m_activeModel = modelName;
                } catch (IOException e) {
                    new ErrorDialog(this.m_simulator.getFrame(), "Error renaming configuration", e)
                        .setVisible(true);
                }
                refreshGUI();
                loadModel();
                break;
            case COPY:
                if (!hasModels()) {
                    return;
                }
                String xmlString;
                try {
                    xmlString = (String) this.m_simulator.getModel()
                        .getGrammar()
                        .getResource(ResourceKind.CONFIG, this.m_activeModel)
                        .toResource();
                    this.m_simulator.getModel()
                        .getStore()
                        .putTexts(ResourceKind.CONFIG,
                            Collections.singletonMap(modelName, xmlString));
                    this.m_activeModel = modelName;
                } catch (FormatException e) {
                    // FormatException not applicable to CONFIG resources
                    return;
                } catch (IOException e) {
                    new ErrorDialog(this.m_simulator.getFrame(), "Error copying configuration", e)
                        .setVisible(true);
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
            xmlString = (String) this.m_simulator.getModel()
                .getGrammar()
                .getResource(ResourceKind.CONFIG, this.m_activeModel)
                .toResource();
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

    protected abstract void loadModel(String xmlString) throws ConfigurationException;

    private void saveModel() throws ConfigurationException {
        Document doc = getDocument();

        Transformer transformer = null;
        Exception exc = null;
        try {
            transformer = TransformerFactory.newInstance()
                .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // Allow indenting
            // The following line is specific to apache xalan. Since indenting is not really required anyway, commented out
            // See also http://stackoverflow.com/questions/1264849
            //transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            // Transform to string
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new StringWriter());
            transformer.transform(source, result);
            String xmlString = result.getWriter()
                .toString();

            this.m_simulator.getModel()
                .getStore()
                .putTexts(ResourceKind.CONFIG,
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
                "Error saving configuration resource " + this.m_activeModel, exc).setVisible(true);
        }
    }

    protected abstract Document getDocument() throws ConfigurationException;

    public boolean hasModels() {
        return !this.m_simulator.getModel()
            .getGrammar()
            .getNames(ResourceKind.CONFIG)
            .isEmpty();
    }
}

package groove.io.conceptual.configuration;

import static groove.grammar.model.ResourceKind.CONFIG;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.Text;
import groove.gui.Simulator;
import groove.gui.SimulatorModel;
import groove.gui.dialog.ErrorDialog;
import groove.io.store.SystemStore;
import groove.util.Exceptions;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xerces.xni.parser.XMLParseException;
import org.eclipse.jdt.annotation.NonNull;
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
import com.sun.istack.internal.Nullable;

/** Dialog for creating and manipulating im/-export configurations. */
public class ConfigDialog extends JDialog {
    /** Constructs a new dialog, for a given simulator. */
    public ConfigDialog(Simulator simulator) {
        super(simulator.getFrame(), "Config Dialog", true);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        // Make sure that closeDialog is called whenever the dialog is closed.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                resetSelectedConfig();
                dispose();
            }
        });

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setVisible(false);
            }
        };
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        this.getRootPane().registerKeyboardAction(actionListener,
            stroke,
            JComponent.WHEN_IN_FOCUSED_WINDOW);

        this.m_simulator = simulator;

        buildGUI();

        setSize(800, 600);
    }

    /** Returns the store backing up the current simulator model. */
    private SystemStore getStore() {
        return getSimulator().getStore();
    }

    /** Returns the currently loaded grammar model. */
    private GrammarModel getGrammar() {
        return getSimulator().getGrammar();
    }

    /** Returns the current simulator model. */
    private SimulatorModel getSimulator() {
        return this.m_simulator.getModel();
    }

    /** Returns the frame with which the dialog is associated. */
    private JFrame getFrame() {
        return this.m_simulator.getFrame();
    }

    /** Simulator with which this dialog is associated. */
    private final Simulator m_simulator;

    /** Invokes the dialog and returns the selected configuration name */
    public String getConfig() {
        setLocationRelativeTo(getFrame());
        setVisible(true);
        // After closing the dialog, restore the tooltip class
        javax.swing.UIManager.put("ToolTipUI", s_tooltipObj);
        return getSelectedConfig();
    }

    private void buildGUI() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        toolBar.add(getAction(ConfigAction.Type.NEW));
        toolBar.addSeparator();

        toolBar.add(getConfigList());
        toolBar.add(getAction(ConfigAction.Type.SAVE));
        toolBar.addSeparator();

        toolBar.add(getAction(ConfigAction.Type.COPY));
        toolBar.add(getAction(ConfigAction.Type.DELETE));
        toolBar.add(getAction(ConfigAction.Type.RENAME));

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(toolBar, BorderLayout.NORTH);

        this.getContentPane().add(getXMLPanel(), BorderLayout.CENTER);

        JButton okBtn = new JButton("OK");
        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getAction(ConfigAction.Type.SAVE).execute();
                setSelectedConfig(getActiveName());
                ConfigDialog.this.dispose();
            }
        });
        this.getRootPane().setDefaultButton(okBtn);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetSelectedConfig();
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

        String name = hasConfigs() ? getConfigNames().iterator().next() : null;
        loadConfig(name);
    }

    /**
     * Returns the combobox holding the list of configurations.
     */
    private JComboBox<String> getConfigList() {
        if (this.m_configList == null) {
            final JComboBox<String> result = this.m_configList = new JComboBox<String>();
            result.setEditable(false);
            result.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    if (ConfigDialog.this.m_ignoreConfigList) {
                        return;
                    }
                    if (ae.getSource() == result) {
                        loadConfig((String) result.getSelectedItem());
                    }
                }
            });
        }
        return this.m_configList;
    }

    private JComboBox<String> m_configList;

    private void refreshConfigList() {
        this.m_ignoreConfigList = true;
        getConfigList().removeAllItems();

        /** Add all current names in the grammar. */
        Set<String> names = getGrammar().getNames(CONFIG);
        if (names.isEmpty()) {
            final String newStr = new String("<New>");
            getConfigList().addItem(newStr);
            getConfigList().setSelectedItem(newStr);
        } else {
            for (String name : new TreeSet<String>(names)) {
                getConfigList().addItem(name);
            }
            getConfigList().setSelectedItem(getActiveName());
        }
        this.m_ignoreConfigList = false;
    }

    /** Callback method creating the panel with the XML dialog. */
    private JPanel getXMLPanel() {
        if (this.m_panel == null) {
            this.m_panel = new JPanel();
            this.m_panel.setLayout(new BorderLayout());
        }
        return this.m_panel;
    }

    private JPanel m_panel;

    /**
     * Flag indicating whether the combo box should be currently listened true.
     * Temporarily set to {@code false} when the box is bing refreshed.
     */
    private boolean m_ignoreConfigList = false;

    private ConfigAction getAction(ConfigAction.Type type) {
        ConfigAction result = this.actionMap.get(type);
        if (result == null) {
            this.actionMap.put(type, result = new ConfigAction(this.m_simulator, type, this));
            result.setEnabled(true);
        }
        return result;
    }

    private final Map<ConfigAction.Type,ConfigAction> actionMap =
        new EnumMap<ConfigAction.Type,ConfigAction>(ConfigAction.Type.class);

    /** Refreshes the enabled actions and the content of the configurations list. */
    private void refreshActions() {
        getAction(ConfigAction.Type.RENAME).setEnabled(hasConfigs());
        getAction(ConfigAction.Type.COPY).setEnabled(hasConfigs());
        getAction(ConfigAction.Type.DELETE).setEnabled(hasConfigs());

        refreshConfigList();
    }

    /** Carries out the consequences of a given action.
     * @param name configuration name with which the action is to be carried out;
     * if {@code null}, the active name is to be used.
     */
    public void executeAction(ConfigAction.Type type, @Nullable String name) {
        if (name == null) {
            name = getActiveName();
            if (name == null) {
                // this should not happen; defensively abort
                return;
            }
        }
        try {
            switch (type) {
            case NEW:
                loadEmptyConfig();
                // Immediately save model with current model name
                saveConfig(name);
                break;
            case SAVE:
                saveConfig(name);
                break;
            case DELETE:
                try {
                    getStore().delete(CONFIG, Collections.singletonList(name));
                } catch (IOException e) {
                    new ErrorDialog(getFrame(), "Error deleting configuration", e).setVisible(true);
                }
                String newName = hasConfigs() ? getConfigNames().iterator().next() : null;
                loadConfig(newName);
                break;
            case RENAME:
                try {
                    assert hasActiveName();
                    getStore().rename(CONFIG, getActiveName(), name);
                } catch (IOException e) {
                    new ErrorDialog(getFrame(), "Error renaming configuration", e).setVisible(true);
                }
                loadConfig(name);
                break;
            case COPY:
                try {
                    Text source = getStore().getText(CONFIG, getActiveName());
                    Text target = source.rename(name);
                    getStore().put(CONFIG, Collections.singleton(target));
                } catch (IOException e) {
                    new ErrorDialog(getFrame(), "Error copying configuration", e).setVisible(true);
                }
                loadConfig(name);
                break;
            default:
                throw Exceptions.UNREACHABLE;
            }
        } catch (ConfigurationException e) {
            //TODO:
            // Silently catch error. The dialog should have message area or something for this
        }
    }

    /** Load a fresh, empty configuration into this dialog. */
    private void loadEmptyConfig() {
        try {
            com.jaxfront.core.dom.Document dom =
                DOMBuilder.getInstance().build(null, getSchemaURL(), "configuration");
            setDocument(dom);
        } catch (SchemaCreationException | DocumentCreationException e) {
            // Silently catch error
        }
    }

    /** Loads a named configuration from the grammar into this dialog.
     * If successful, sets the active name to that name.
     * @param name name of the configuration to be loaded; will be
     * assigned to the active name
     */
    private void loadConfig(String name) {
        if (name == null) {
            loadEmptyConfig();
        } else {
            String xmlString = getGrammar().getConfigModel(name).toConfig();
            // Do something with xmlString
            try (PrintStream tmpOut = new PrintStream(File.createTempFile("tmp", null))) {
                PrintStream out = System.out;
                System.setOut(tmpOut);
                Document xmlDoc = DOMHelper.createDocument(xmlString);
                com.jaxfront.core.dom.Document doc =
                    DOMBuilder.getInstance().build(null,
                        getSchemaURL(),
                        xmlDoc,
                        null,
                        "configuration");
                setDocument(doc);
                System.setOut(out);
            } catch (SAXException | IOException | SchemaCreationException
                | DocumentCreationException e) {
                // Not much that can be done here, silently catch the error
            }
        }
        this.m_activeName = name;
        refreshActions();
    }

    /**
     * Saves the current configuration under a given name.
     * The name is simultaneously set as active name.
     * @param name name of the saved model; will be set as active name
     * @throws ConfigurationException if an error occurred during saving
     */
    private void saveConfig(String name) throws ConfigurationException {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // Allow indenting
            // The following line is specific to apache xalan.
            // Since indenting is not really required anyway, commented out
            // See also http://stackoverflow.com/questions/1264849
            //transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            // Transform document to string
            DOMSource source = new DOMSource(getDocument());
            StreamResult result = new StreamResult(new StringWriter());
            transformer.transform(source, result);
            String xmlString = result.getWriter().toString();

            getStore().put(CONFIG, Collections.singleton(new Text(CONFIG, name, xmlString)));
            if (!name.equals(getActiveName())) {
                this.m_activeName = name;
                refreshActions();
            }
        } catch (IOException | TransformerException e) {
            new ErrorDialog(getFrame(), "Error saving configuration resource " + name, e).setVisible(true);
        }
    }

    /** Returns the active name of the dialog,
     * being the name for the currently loaded configuration.
     */
    private String getActiveName() {
        return this.m_activeName;
    }

    /** Tests if the active configuration name is set. */
    private boolean hasActiveName() {
        return getActiveName() != null;
    }

    /** Name of the currently active configuration. */
    private String m_activeName;

    /** Convenience method to return the set of configuration names in the grammar. */
    public Set<String> getConfigNames() {
        return getGrammar().getNames(CONFIG);
    }

    /** Convenience method to test if the grammar contains any configurations. */
    public boolean hasConfigs() {
        return !getConfigNames().isEmpty();
    }

    /** Returns the document of the most recently loaded model. */
    private Document getDocument() throws ConfigurationException {
        try {
            return this.m_editor.getDOM().serializeToW3CDocument();
        } catch (XMLParseException | SAXException | IOException | ValidationException e) {
            throw new ConfigurationException(e);
        }
    }

    private void setDocument(com.jaxfront.core.dom.Document doc) {
        if (this.m_panelComponent != null) {
            getXMLPanel().remove(this.m_panelComponent);
        }
        this.m_editor = new EditorPanel(doc.getRootType(), null);
        this.m_panelComponent = this.m_editor;
        try {
            // get the editor pane from the original editor panel and put it on our dialog
            JAXJSplitPane pane = (JAXJSplitPane) this.m_editor.getComponent(0);
            TypeWorkspace space = (TypeWorkspace) pane.getRightComponent();
            space.getButtonBar().setVisible(false);
            space.getHeaderPanel().setVisible(false);
            space.getMessageTablePanel().setVisible(false);
            this.m_panelComponent = space;
            getXMLPanel().add(this.m_panelComponent);
        } catch (ClassCastException ex) {
            // In case of an exception (the UI is changed) just add the editor itself
            // nothing to do here
        } catch (ArrayIndexOutOfBoundsException ex) {
            // nothing to do here
        }
        getXMLPanel().add(this.m_panelComponent);
        getXMLPanel().validate();
    }

    private EditorPanel m_editor;
    private Component m_panelComponent;

    /**
     * Sets the name of the currently selected configuration to a given value.
     * @param name name of the configuration; should be an existing name
     */
    private void setSelectedConfig(@NonNull String name) {
        assert getConfigNames().contains(name);
        this.m_selectedModel = name;
    }

    /** Resets the currently selected configuration to {@code null}. */
    private void resetSelectedConfig() {
        this.m_selectedModel = null;
    }

    /** Returns the name of the currently selected configuration, if any. */
    private @Nullable String getSelectedConfig() {
        return this.m_selectedModel;
    }

    private @Nullable String m_selectedModel;

    /** Returns the URL of the configuration schema. */
    private URL getSchemaURL() {
        if (this.m_schemaURL == null) {
            this.m_schemaURL = this.getClass().getClassLoader().getResource(Config.CONFIG_SCHEMA);
            if (this.m_schemaURL == null) {
                throw new IllegalStateException("Unable to load the XML schema resource "
                    + Config.CONFIG_SCHEMA);
            }

        }
        return this.m_schemaURL;
    }

    private URL m_schemaURL;

    // Store the default Swing tooltip class, so the broken JaxFront version wont interfere after dialog is closed
    private final static Object s_tooltipObj;
    static {
        s_tooltipObj = javax.swing.UIManager.get("ToolTipUI");
    }

}

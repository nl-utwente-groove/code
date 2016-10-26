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
package groove.gui.dialog.config;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import groove.explore.config.BooleanKey;
import groove.explore.config.CheckingKind;
import groove.explore.config.ExploreKey;
import groove.explore.config.SettingKey;
import groove.gui.display.DismissDelayer;
import groove.io.HTMLConverter;
import groove.io.HTMLConverter.HTMLTag;
import groove.util.parse.StringHandler;
import groove.verify.FormulaParser;
import groove.verify.Logic;

/**
 * Factory for syntax help.
 * @author Arend Rensink
 * @version $Revision $
 */
public class HelpFactory {
    /**
     * Creates an instance of the factory.
     */
    protected HelpFactory() {
        // empty
    }

    /** Creates an returns a panel showing syntax help for a given combination of exploration key
     * and setting kind.
     */
    public JComponent createHelp(ExploreKey key, SettingKey kind) {
        JComponent result;
        switch (key) {
        case ISO:
            result = createIsoHelp((BooleanKey) kind);
            break;
        case RANDOM:
            result = createRandomHelp((BooleanKey) kind);
            break;
        case CHECKING:
            result = createCheckingHelp((CheckingKind) kind);
            break;
        default:
            result = createDefaultHelp(key);
        }
        return result;
    }

    /** Creates the help panel for the isomorphism checking setting. */
    protected JTextPane createRandomHelp(BooleanKey kind) {
        JTextPane result = createTextPane();
        StringBuilder text = getExplanation(ExploreKey.RANDOM);
        text.append("Determines if successor states are explored in random order.");
        text.append(HTMLConverter.HTML_LINEBREAK);
        text.append(HTMLConverter.HTML_LINEBREAK);
        switch (kind) {
        case FALSE:
            text.append(
                "Currently set to <b>false</b>, meaning that when the successors of a given state "
                    + "are explored, the next state to be picked is determined by the search strategy "
                    + "and deterministally fixed between one exploration and the next.");
            break;
        case TRUE:
            text.append("Currently set to <b>true</b>, meaning that whenever the next successor of "
                + "a given state is explored, a random choice is made between the as yet unexplored "
                + "states.");
            break;
        default:
            assert false;
        }
        result.setText(text.toString());
        return result;
    }

    /** Creates the help panel for the isomorphism checking setting. */
    protected JTextPane createIsoHelp(BooleanKey kind) {
        JTextPane result = createTextPane();
        StringBuilder text = getExplanation(ExploreKey.ISO);
        text.append("Determines if isomorphic states are detected and collapsed");
        text.append(HTMLConverter.HTML_LINEBREAK);
        text.append(HTMLConverter.HTML_LINEBREAK);
        switch (kind) {
        case FALSE:
            text.append(
                "Currently set to <b>false</b>, meaning that no isomorphism check is performed. "
                    + "This will speed up exploration, but may result in a far greater number of states "
                    + "if there is any symmetry.");
            break;
        case TRUE:
            text.append("Currently set to <b>true</b>, meaning that a state is only added if no "
                + "isomorphic state has been discovered previously. If an isomorphic state has been "
                + "found, that is used instead. If states have self-symmetry, this will reduce the state "
                + "space; however, the isomorphism check itself is costly.");
            break;
        default:
            assert false;
        }
        result.setText(text.toString());
        return result;
    }

    /** Creates a help panel for a given model checking kind. */
    protected JComponent createCheckingHelp(CheckingKind kind) {
        JComponent result;
        switch (kind) {
        case CTL_CHECK:
        case LTL_CHECK:
            result = createSyntaxPanel(kind.getLogic());
            break;
        default:
            result = createDefaultHelp(ExploreKey.CHECKING);
        }
        return result;
    }

    private JComponent createSyntaxPanel(Logic logic) {
        final JList<String> list = new JList<>();
        DefaultListModel<String> model = new DefaultListModel<>();
        Map<String,String> docMap = FormulaParser.getDocMap(logic);
        for (Map.Entry<String,String> entry : docMap.entrySet()) {
            model.addElement(entry.getKey());
        }
        list.setModel(model);
        list.setCellRenderer(new MyCellRenderer(docMap));
        list.addMouseListener(new DismissDelayer(list));
        list.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                super.setSelectionInterval(-1, -1);
            }
        });
        JPanel result = new JPanel(new BorderLayout());
        result.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
        result.add(new JLabel("<html><b>Syntax:"), BorderLayout.NORTH);
        result.add(new JScrollPane(list), BorderLayout.CENTER);
        return result;
    }

    /**
     * Creates a default help panel, listing all the setting kinds for a given exploration key
     * with corresponding explanations.
     */
    protected JTextPane createDefaultHelp(ExploreKey key) {
        JTextPane result = createTextPane();
        StringBuilder text = getExplanation(key);
        StringBuilder list = new StringBuilder();
        HTMLTag dt = new HTMLTag("dt");
        HTMLTag dd = new HTMLTag("dd");
        HTMLTag strong = HTMLConverter.STRONG_TAG;
        for (SettingKey kind : key.getKindType()
            .getEnumConstants()) {
            list.append(dt.on(strong.on(StringHandler.toUpper(kind.getName()))));
            list.append(dd.on(kind.getExplanation()));
        }
        new HTMLTag("dl").on(list);
        text.append(list);
        result.setText(text.toString());
        return result;
    }

    /** Callback factory method for a fresh HTML-enabled text pane. */
    protected JTextPane createTextPane() {
        JTextPane result = new JTextPane();
        result.setContentType("text/html");
        result.setEditable(false);
        return result;
    }

    /** Returns a fresh string builder initialised with the explanatory text of a given key. */
    protected StringBuilder getExplanation(ExploreKey key) {
        StringBuilder result = new StringBuilder();
        result.append(new HTMLTag("h2").on(key.getExplanation()));
        return result;
    }

    /** Private cell renderer class that inserts the correct tool tips. */
    private static class MyCellRenderer extends DefaultListCellRenderer {
        MyCellRenderer(Map<String,String> tipMap) {
            this.tipMap = tipMap;
        }

        @SuppressWarnings("rawtypes")
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
            Component result =
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (result == this) {
                setToolTipText(this.tipMap.get(value));
            }
            return result;
        }

        private final Map<String,String> tipMap;
    }
}

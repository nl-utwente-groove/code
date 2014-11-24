package groove.gui.action;

import groove.control.Procedure;
import groove.control.graph.ControlGraph;
import groove.control.template.Program;
import groove.control.template.Template;
import groove.grammar.model.ControlModel;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.ResourceKind;
import groove.grammar.model.TextBasedModel;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.dialog.GraphPreviewDialog;
import groove.util.parse.FormatException;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JPopupMenu;

/**
 * Creates a dialog showing the control automaton.
 */
public class PreviewControlAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public PreviewControlAction(Simulator simulator) {
        super(simulator, Options.PREVIEW_CONTROL_ACTION_NAME, Icons.CONTROL_MODE_ICON, null,
            ResourceKind.CONTROL);
    }

    @Override
    public void execute() {
        try {
            Collection<Template> templates = getTemplates();
            if (templates != null) {
                if (templates.size() == 1) {
                    getDialog(templates.iterator().next()).setVisible(true);
                } else {
                    Point pos = MouseInfo.getPointerInfo().getLocation();
                    createMenu(templates).show(getSimulator().getFrame(), pos.x, pos.y);
                }
            }
        } catch (FormatException exc) {
            showError(exc);
        }
    }

    private JPopupMenu createMenu(Collection<Template> templates) {
        JPopupMenu result = new JPopupMenu();
        for (Template t : templates) {
            String text;
            if (t.hasOwner()) {
                Procedure proc = t.getOwner();
                text = proc.getKind().getName(true) + " " + proc.getFullName();
            } else {
                text = "Main program";
            }
            final Template template = t;
            result.add(new AbstractAction(text) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    getDialog(template).setVisible(true);
                }
            });
        }
        return result;
    }

    private void showError(FormatException exc) {
        showErrorDialog(exc,
            String.format("Error in control program '%s'",
                getSimulatorModel().getSelected(ResourceKind.CONTROL)));
    }

    @Override
    public void refresh() {
        try {
            setEnabled(getTemplates() != null);
        } catch (FormatException e) {
            setEnabled(false);
        }
    }

    private JDialog getDialog(Template template) {
        return new GraphPreviewDialog<ControlGraph>(getSimulator(), template.toGraph(true));
    }

    /** Convenience method to obtain the currently selected control automaton. */
    private Collection<Template> getTemplates() throws FormatException {
        Collection<Template> result = null;
        GrammarModel grammarModel = getGrammarModel();
        if (grammarModel != null) {
            Optional<TextBasedModel<?>> controlModel =
                getSimulatorModel().getTextResource(getResourceKind());
            if (controlModel.isPresent()) {
                result = ((ControlModel) controlModel.get()).toResource();
            } else {
                Program program = grammarModel.getControlModel().getProgram();
                if (program != null && program.getTemplate() != null) {
                    result = Collections.singleton(program.getTemplate());
                }
            }
        }
        return result;
    }
}
package nl.utwente.groove.gui.action;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JPopupMenu;

import nl.utwente.groove.control.template.Program;
import nl.utwente.groove.control.template.Template;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.model.ControlModel;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.dialog.GraphPreviewDialog;
import nl.utwente.groove.util.parse.FormatException;

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
            Program program = getProgram();
            if (program != null) {
                List<Template> templates = new ArrayList<>();
                if (program.hasMain()) {
                    templates.add(program.getTemplate());
                }
                program
                    .getProcs()
                    .values()
                    .stream()
                    .filter(p -> p.getControlName().equals(getSelectedName()))
                    .forEach(p -> templates.add(p.getTemplate()));
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
            var owner = t.getOwner();
            if (owner != null) {
                text = owner.getKind().getName(true) + " " + owner.getQualName();
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

    /** Returns the currently selected control name. */
    private QualName getSelectedName() {
        return getSimulatorModel().getSelected(ResourceKind.CONTROL);
    }

    private void showError(FormatException exc) {
        showErrorDialog(exc, String.format("Error in control program '%s'", getSelectedName()));
    }

    @Override
    public void refresh() {
        try {
            setEnabled(getProgram() != null);
        } catch (FormatException e) {
            setEnabled(false);
        }
    }

    private JDialog getDialog(Template template) {
        return new GraphPreviewDialog<>(getSimulator(), template.toGraph(true));
    }

    /** Convenience method to obtain the currently selected (fixed) control program. */
    private Program getProgram() throws FormatException {
        Program result = null;
        GrammarModel grammarModel = getGrammarModel();
        if (grammarModel != null) {
            ControlModel controlModel
                = (ControlModel) getSimulatorModel().getTextResource(getResourceKind());
            if (controlModel == null) {
                result = grammarModel.getControlModel().getProgram();
            } else {
                result = controlModel.toResource();
            }
        }
        assert result == null || result.isFixed();
        return result;
    }
}
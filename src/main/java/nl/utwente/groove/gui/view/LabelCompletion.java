/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2026
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package nl.utwente.groove.gui.view;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.aspect.AspectKind;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Exceptions;

/**
 * Autocompletion of labels in the text area of an in-place label editor:
 * Ctrl+Space completes the word before the caret to the next label of the
 * completion set that starts with it, cycling through the candidates on
 * repetition. The completion set is the aspect prefixes plus the labels of the
 * type graph, see {@link #labelsFor(TypeGraph)}. Shared by the in-place
 * editors of the backends.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class LabelCompletion implements CaretListener {
    /** Installs the completion on a text area. */
    public LabelCompletion(JTextArea area) {
        this.area = area;
        area.getInputMap(JComponent.WHEN_FOCUSED).put(KEY, ACTION_NAME);
        area.getActionMap().put(ACTION_NAME, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                complete();
            }
        });
        area.addCaretListener(this);
    }

    private final JTextArea area;

    /** Sets the labels to complete to. */
    public void setLabels(Collection<String> labels) {
        this.labels = new TreeSet<>(labels);
        reset();
    }

    private SortedSet<String> labels = new TreeSet<>();

    /**
     * Returns the completion set for a graph typed by a given type graph:
     * the aspect prefixes and the labels of the type graph.
     */
    public static SortedSet<String> labelsFor(TypeGraph type) {
        SortedSet<String> result = new TreeSet<>(PREFIXES);
        for (TypeLabel label : type.getLabels()) {
            result.add(label.text());
        }
        return result;
    }

    /* A caret move (other than the completion's own) starts the cycle afresh. */
    @Override
    public void caretUpdate(CaretEvent e) {
        reset();
    }

    private void reset() {
        this.completions = null;
    }

    /** Completes the word before the caret to the next candidate. */
    private void complete() {
        var completions = this.completions;
        if (completions == null) {
            this.completions = completions = computeCompletions();
        }
        String completion = completions.poll();
        if (completion != null) {
            completions.add(completion);
            SwingUtilities.invokeLater(() -> apply(completion));
        }
    }

    /** Replaces the selection by a completion, leaving the completion selected. */
    private void apply(String completion) {
        this.area.removeCaretListener(this);
        Caret caret = this.area.getCaret();
        int pos = Math.min(caret.getDot(), caret.getMark());
        this.area.replaceSelection(completion);
        this.area.setCaretPosition(pos);
        this.area.moveCaretPosition(pos + completion.length());
        this.area.addCaretListener(this);
    }

    /**
     * Computes the completions of the word before the selection, as the tails
     * that the candidates add to it; empty if the selection does not run up to
     * the end of a word.
     */
    private LinkedList<String> computeCompletions() {
        LinkedList<String> result = new LinkedList<>();
        Caret caret = this.area.getCaret();
        int dot = caret.getDot();
        int mark = caret.getMark();
        int min = Math.min(dot, mark);
        int max = Math.max(dot, mark);
        String content;
        try {
            Document document = this.area.getDocument();
            if (max < document.getLength()
                && Character.isLetterOrDigit(document.getText(max, 1).charAt(0))) {
                return result;
            }
            content = document.getText(0, min);
        } catch (BadLocationException exc) {
            throw Exceptions.illegalState("Impossible error: %s", exc);
        }
        // find where the word starts
        int start = min;
        while (start > 0 && Character.isLetterOrDigit(content.charAt(start - 1))) {
            start--;
        }
        if (start < min) {
            String root = content.substring(start);
            Iterator<String> iter = this.labels.tailSet(root).iterator();
            while (iter.hasNext()) {
                String candidate = iter.next();
                if (!candidate.startsWith(root)) {
                    break;
                }
                result.add(candidate.substring(min - start));
            }
        }
        return result;
    }

    /** The completions of the current word, cycled by repeated completion; {@code null} if stale. */
    private @Nullable LinkedList<String> completions;

    /** The key stroke that completes. */
    public static final KeyStroke KEY = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.CTRL_DOWN_MASK);
    private static final String ACTION_NAME = "autocomplete";
    /** The aspect and edge-role prefixes, part of every completion set. */
    private static final List<String> PREFIXES;

    static {
        List<String> prefixes = new ArrayList<>();
        for (AspectKind aspectKind : AspectKind.values()) {
            String prefix = aspectKind.getPrefix();
            if (prefix.length() > 1) {
                prefixes.add(prefix);
            }
        }
        for (EdgeRole edgeRole : EdgeRole.values()) {
            String prefix = edgeRole.getPrefix();
            if (prefix.length() > 1) {
                prefixes.add(prefix);
            }
        }
        PREFIXES = List.copyOf(prefixes);
    }
}

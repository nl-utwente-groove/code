/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
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
package groove.gui.display;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.codehaus.groovy.control.CompilationFailedException;

import groove.grammar.QualName;
import groove.grammar.groovy.GraphManager;
import groove.grammar.model.ResourceKind;
import groove.gui.Simulator;
import groove.io.FileType;
import groovy.lang.Binding;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;

/**
 * The Simulator panel that shows the groovy program, with a button that shows
 * the corresponding control automaton.
 *
 * @author Harold
 * @version $x$
 */
final public class GroovyDisplay extends ResourceDisplay {
    /**
     * @param simulator
     *            The Simulator the panel is added to.
     */
    public GroovyDisplay(Simulator simulator) {
        super(simulator, ResourceKind.GROOVY);
    }

    @Override
    protected void buildDisplay() {
        this.setLayout(new BorderLayout());
        this.setFocusable(false);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getTabPane(),
            new JScrollPane(getEditorPane()));

        getEditorPane().setEditable(false);

        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(0.8);
        splitPane.setResizeWeight(0.8);

        add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Executes the Groovy script
     *
     * @param name Name of script resource to execute
     */
    public void executeGroovy(QualName name) {
        String program = getSimulatorModel().getStore()
            .getTexts(getResourceKind())
            .get(name);
        GraphManager manager = new GraphManager(getSimulatorModel());
        Binding binding = new Binding();

        PaneWriter writer;
        try (PipedOutputStream output = new PipedOutputStream();
            PrintStream newstream = new PrintStream(output);) {
            writer = new PaneWriter(getEditorPane(), output);
            getEditorPane().setText("");

            writer.start();

            binding.setVariable("simulator", getSimulator());
            binding.setVariable("simulatorModel", getSimulatorModel());
            binding.setVariable("manager", manager);
            binding.setVariable("out", newstream);
            GroovyShell shell = new GroovyShell(binding);
            try {
                shell.evaluate(program);
            } catch (CompilationFailedException e) {
                newstream.println("Failed to compile Groovy script");
                newstream.println(e.getMessage());
            } catch (GroovyRuntimeException e) {
                newstream.println("Error during execution of Groovy script");
                String loc = "";
                for (StackTraceElement elem : e.getStackTrace()) {
                    if (elem.getFileName()
                        .endsWith(FileType.GROOVY.getExtension())) {
                        loc = elem.getFileName() + ":" + elem.getLineNumber() + " : ";
                        break;
                    }
                }
                newstream.println(loc + e.getMessage());
            } catch (Exception e) {
                newstream.println(e.getClass()
                    .getSimpleName() + " during execution of Groovy script");
                newstream.println(e.getMessage());
                for (StackTraceElement elem : e.getStackTrace()) {
                    newstream.println(elem.toString());
                }
            } catch (Error e) {
                newstream.println("!" + e.getClass()
                    .getSimpleName() + " during execution of Groovy script!");
                newstream.println(e.getMessage());
            }

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        // Stop thread, ignore any errors
        try {
            writer.join();
        } catch (InterruptedException e) {
            // Ignore
        }
    }

    /** Lazily creates and returns the editor pane. */
    private JEditorPane getEditorPane() {
        if (this.editorPane == null) {
            this.editorPane = new JEditorPane();
        }
        return this.editorPane;
    }

    private JEditorPane editorPane;

    private class PaneWriter extends Thread {
        private JEditorPane pane;
        private PipedInputStream stream;

        public PaneWriter(JEditorPane pane, PipedOutputStream stream) throws IOException {
            this.pane = pane;
            this.stream = new PipedInputStream(stream);
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            Document doc = this.pane.getDocument();
            int lenRead = 0;
            try {
                while ((lenRead = this.stream.read(buffer, 0, 1023)) > 0) {
                    buffer[lenRead] = 0;
                    // pane.setText(new String(buffer));
                    try {
                        doc.insertString(doc.getLength(), new String(buffer, 0, lenRead), null);
                    } catch (BadLocationException e) {
                        // Ignore
                    }
                }
            } catch (IOException e) {
                // Ignore
            }
        }
    }
}

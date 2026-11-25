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
package nl.utwente.groove.gui.yfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import java.awt.geom.PathIterator;
import java.awt.geom.Path2D;
import com.yworks.yfiles.graph.IEdge;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.view.GraphComponent;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.display.AspectEditorTab;
import nl.utwente.groove.gui.display.DisplayKind;
import nl.utwente.groove.gui.display.GraphDisplay;
import nl.utwente.groove.gui.display.LTSDisplay;
import nl.utwente.groove.gui.display.ResourceDisplay;
import nl.utwente.groove.gui.display.StateDisplay;
import nl.utwente.groove.gui.export.CanvasExportable;
import nl.utwente.groove.gui.export.RasterExporter;
import nl.utwente.groove.gui.export.VectorExporter;
import nl.utwente.groove.gui.layout.Layouter;
import nl.utwente.groove.gui.look.VisualMap;
import nl.utwente.groove.gui.view.AspectViewCell;
import nl.utwente.groove.gui.view.LTSViewVertex;
import nl.utwente.groove.gui.view.ViewCell;
import nl.utwente.groove.gui.view.ViewEdge;
import nl.utwente.groove.gui.view.ViewVertex;
import nl.utwente.groove.io.external.PortException;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.FileType;
import nl.utwente.groove.util.QualName;

/**
 * Checks the yFiles canvas inside a running Simulator, the way a user sees it: the state
 * display, the host graph tab and a rule tab show their graphs on yFiles with all
 * their edges, the live repaint (which updates the visuals rather than creating them)
 * paints the edges, a right click opens GROOVE's popup menu, and a click in the LTS
 * display selects a state. Clicks are dispatched to the components synthetically, so
 * those tests need a display but not a visible desktop. The popup menu and the editing
 * gestures need a Robot, which takes over the mouse: those tests run only when asked
 * for with the system property {@value #ROBOT_PROPERTY} set to {@code true}, and are
 * skipped otherwise. Preferences are kept in memory, by {@link TestIsolation}.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class YFilesSimulatorTest {
    /** The GROOVE checkout the fixture grammars are read from; set by the build. */
    private static final String GROOVE_DIR = System.getProperty("groove.dir", "..");
    /** Grammar loaded into the Simulator. */
    private static final String GRAMMAR = GROOVE_DIR + "/junit/samples/ferryman.gps";
    /** System property that switches the Robot-driven tests on. */
    private static final String ROBOT_PROPERTY = "groove.test.robot";
    /** A typed grammar, to switch to. */
    private static final String OTHER_GRAMMAR = GROOVE_DIR + "/junit/samples/inheritance.gps";
    /** Time to wait for the GUI to settle, in milliseconds. */
    private static final long TIMEOUT = 30_000;

    @BeforeAll
    static void launch() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless(), "no display");
        assertTrue(InMemoryPreferencesFactory.isInstalled(),
                   "Preferences not isolated: " + TestIsolation.class.getSimpleName()
                       + " did not run (on the module path it cannot: pass the VM arguments"
                       + " of the Surefire argLine in the unit's pom), or java.util.prefs was"
                       + " used before it did");
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> EDT_ERRORS.add(e));
        SwingUtilities.invokeAndWait(() -> {
            var sim = new Simulator();
            simulator = sim;
            sim.start();
            sim.getFrame().toFront();
        });
    }

    @AfterAll
    static void close() throws Exception {
        var sim = simulator;
        if (sim != null) {
            SwingUtilities.invokeAndWait(() -> sim.getFrame().dispose());
        }
    }

    /** Every test starts with the fixture grammar loaded and no pending errors. */
    @BeforeEach
    void loadFixtureGrammar() throws Exception {
        loadGrammar(GRAMMAR);
        EDT_ERRORS.clear();
    }

    /** No test may leave an exception on the event thread. */
    @AfterEach
    void checkEventThread() {
        assertTrue(EDT_ERRORS.isEmpty(), () -> "Errors on the event thread: " + EDT_ERRORS);
    }

    /** Loads a grammar through the same action the File menu uses. */
    private static void loadGrammar(String path) throws Exception {
        var file = new File(path).getCanonicalFile();
        var store = simulator().getModel().getStore();
        if (store != null && file.equals(store.getLocation())) {
            return;
        }
        SwingUtilities.invokeAndWait(() -> {
            try {
                simulator().getActions().getLoadGrammarAction().load(file);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    /** Exceptions caught on the event thread. */
    private static final List<Throwable> EDT_ERRORS = new ArrayList<>();

    @Test
    void stateDisplayShowsAllCells() throws Exception {
        checkAllCellsShown(stateCanvas());
    }

    @Test
    void stateDisplayPaintsEdges() throws Exception {
        checkEdgesPainted(stateCanvas());
    }

    @Test
    void hostTabShowsAllCells() throws Exception {
        checkAllCellsShown(resourceCanvas(ResourceKind.HOST, DisplayKind.HOST, "start"));
    }

    @Test
    void hostTabPaintsEdges() throws Exception {
        checkEdgesPainted(resourceCanvas(ResourceKind.HOST, DisplayKind.HOST, "start"));
    }

    @Test
    void ltsDisplayShowsAndPaintsTheExploredStates() throws Exception {
        var canvas = ltsCanvas();
        // the state bound keeps part of the LTS out of the shown graph
        checkCellsShown(canvas, false);
        checkEdgesPainted(canvas);
    }

    /**
     * The LTS canvas exports through the canvas contract as the aspect canvases do:
     * a raster image and a vector drawing of the whole shown graph. The files stay in
     * {@code target/imager/lts} for inspection.
     */
    @Test
    void ltsDisplayExportsRasterAndVector() throws Exception {
        var canvas = ltsCanvas();
        var bounds = canvas.getGraphBounds();
        assertNotNull(bounds);
        File dir = new File("target/imager/lts");
        dir.mkdirs();
        var exportable = CanvasExportable.instance(canvas);
        File png = new File(dir, "lts.png");
        File svg = new File(dir, "lts.svg");
        SwingUtilities.invokeAndWait(() -> {
            try {
                RasterExporter.getInstance().doExport(exportable, png, FileType.PNG);
                VectorExporter.getInstance().doExport(exportable, svg, FileType.SVG);
            } catch (PortException exc) {
                throw new IllegalStateException(exc);
            }
        });
        BufferedImage image = ImageIO.read(png);
        assertNotNull(image, png + " is not an image");
        assertTrue(image.getWidth() >= bounds.getWidth()
            && image.getHeight() >= bounds.getHeight(),
                   "image " + image.getWidth() + "x" + image.getHeight() + " smaller than "
                       + bounds);
        String text = Files.readString(svg.toPath(), StandardCharsets.UTF_8);
        Matcher size = Pattern
            .compile("<svg [^>]*\\swidth=\"(\\d+)\"[^>]*\\sheight=\"(\\d+)\"")
            .matcher(text);
        assertTrue(size.find(), svg + " has no sized svg element");
        assertTrue(Integer.parseInt(size.group(1)) >= bounds.getWidth()
            && Integer.parseInt(size.group(2)) >= bounds.getHeight(),
                   "drawing " + size.group(1) + "x" + size.group(2) + " smaller than " + bounds);
        assertTrue(text.contains("<text"), svg + " draws no state labels");
    }

    /**
     * A yFiles layouter selected for the LTS display lays out the explored LTS
     * completely, and incrementally as the LTS grows: the states it places are
     * distinct and the edges are painted.
     */
    @Test
    void ltsIsLaidOutByAYFilesLayouter() throws Exception {
        var canvas = ltsCanvas();
        var controller = canvas.getController();
        Layouter hierarchic = null;
        for (Layouter prototype : canvas.getBackendLayouters()) {
            if (prototype.getName().startsWith("Hierarchic")) {
                hierarchic = prototype;
            }
        }
        assertNotNull(hierarchic, "no hierarchic layouter in the palette");
        var layouter = hierarchic;
        SwingUtilities.invokeAndWait(() -> {
            controller.setLayouter(layouter);
            controller.doLayout(true);
        });
        // every item and its visual must have finite, sane bounds: a layered layout puts
        // bends on node borders and edges in directions for which the perimeter point
        // of the node shape is undefined, which once gave yFiles' path cropping an
        // absurd extent and the canvas an absurd content rectangle
        {
            StringBuilder report = new StringBuilder();
            var shown = ((GraphComponent) canvas.getComponent()).getGraph();
            for (var node : shown.getNodes()) {
                var r = node.getLayout().toRectD();
                if (!Double.isFinite(r.getY()) || !Double.isFinite(r.getHeight()) || Math.abs(r.getY()) > 1e6 || r.getHeight() > 1e6) {
                    report.append("node " + node.getTag() + " layout " + r + " pos " + ((ViewVertex<?>) node.getTag()).getVisuals().getNodePos() + "\n");
                }
                for (var label : node.getLabels()) {
                    var b = label.getLayout().getBounds();
                    if (!Double.isFinite(b.getY()) || Math.abs(b.getY()) > 1e6) {
                        report.append("node label of " + node.getTag() + " bounds " + b + "\n");
                    }
                }
            }
            for (var edge : shown.getEdges()) {
                for (var bend : edge.getBends()) {
                    var l = bend.getLocation();
                    if (!Double.isFinite(l.getY()) || Math.abs(l.getY()) > 1e6) {
                        report.append("bend of " + edge.getTag() + " at " + l + "\n");
                    }
                }
                for (var label : edge.getLabels()) {
                    var b = label.getLayout().getBounds();
                    if (!Double.isFinite(b.getY()) || Math.abs(b.getY()) > 1e6 || !Double.isFinite(b.getHeight())) {
                        report.append("label " + label.getText() + " of " + edge.getTag() + " bounds " + b + " parameter " + label.getLayoutParameter() + " points " + ((ViewEdge<?>) edge.getTag()).getVisuals().getPoints() + " ports " + edge.getSourcePort().getLocation() + "/" + edge.getTargetPort().getLocation() + "\n");
                    }
                }
            }
            var context = ((GraphComponent) canvas.getComponent()).getCanvasContext();
            for (var node : shown.getNodes()) {
                var b = node.getStyle().getRenderer().getBoundsProvider(node, node.getStyle()).getBounds(context);
                if (!Double.isFinite(b.getY()) || !Double.isFinite(b.getHeight()) || Math.abs(b.getY()) > 1e6 || b.getHeight() > 1e6) {
                    report.append("node visual of " + node.getTag() + " bounds " + b + " layout " + node.getLayout().toRectD() + "\n");
                }
            }
            for (var edge : shown.getEdges()) {
                var b = edge.getStyle().getRenderer().getBoundsProvider(edge, edge.getStyle()).getBounds(context);
                if (!Double.isFinite(b.getY()) || !Double.isFinite(b.getHeight()) || Math.abs(b.getY()) > 1e6 || b.getHeight() > 1e6) {
                    var cell = (ViewEdge<?>) edge.getTag();
                    report.append("edge visual of " + cell + " bounds " + b + " style " + cell.getVisuals().getLineStyle() + " points " + cell.getVisuals().getPoints() + " path " + edge.getPathPoints() + " loop " + cell.isLoop() + "\n");
                }
                for (var label : edge.getLabels()) {
                    var lb = label.getStyle().getRenderer().getBoundsProvider(label, label.getStyle()).getBounds(context);
                    if (!Double.isFinite(lb.getY()) || Math.abs(lb.getY()) > 1e6 || lb.getHeight() > 1e6) {
                        report.append("edge label visual " + label.getText() + " bounds " + lb + "\n");
                    }
                }
            }
            assertTrue(report.length() == 0, "absurd items:\n" + report);
        }
        checkStatesApart(canvas);
        checkEdgesPainted(canvas);
        // grow the LTS: the new states are placed among the old ones
        var sim = simulator();
        var model = sim.getModel();
        var gts = model.getGTS();
        assertNotNull(gts);
        int shownBefore = canvas.getShownGraph().getNodes().size();
        int bound = gts.nodeCount() + 10;
        SwingUtilities.invokeAndWait(() -> {
            var display = (LTSDisplay) sim.getDisplaysPanel().getDisplay(DisplayKind.LTS);
            display.setStateBound(bound);
        });
        waitFor("more states to be shown",
                () -> canvas.getShownGraph().getNodes().size() > shownBefore);
        checkStatesApart(canvas);
    }

    /** Checks that no two shown states of an LTS canvas have the same position. */
    private static void checkStatesApart(YFilesLTSCanvas canvas) {
        Set<Point2D> positions = new HashSet<>();
        for (var cell : canvas.getCells()) {
            if (cell instanceof ViewVertex<?> vertex && cell.getVisuals().isVisible()) {
                Point2D pos = vertex.getVisuals().getNodePos();
                assertTrue(positions.add(new Point2D.Double(pos.getX(), pos.getY())),
                           "two states at " + pos);
            }
        }
        assertTrue(positions.size() > 1, "fewer than two states shown");
    }

    /**
     * Clicking a state in the LTS display makes it the current state, and a control-click
     * also switches to the state display: the display's Swing mouse listener must receive
     * the clicks that yFiles handles.
     */
    @Test
    void clickingAStateInTheLtsSelectsIt() throws Exception {
        var sim = simulator();
        var canvas = ltsCanvas();
        JComponent component = canvas.getComponent();
        // a shown state other than the current one is picked and clicked in one
        // event-thread task, so that the view cannot change between the two
        List<LTSViewVertex> targets = new ArrayList<>();
        SwingUtilities.invokeAndWait(() -> {
            var current = sim.getModel().getState();
            for (var cell : canvas.getCells()) {
                if (cell instanceof LTSViewVertex vertex && vertex.getNode() != current
                    && cell.getVisuals().isVisible()) {
                    Point spot = clickableSpot(canvas, vertex);
                    if (spot != null) {
                        targets.add(vertex);
                        SyntheticMouse.click(component, spot.x, spot.y, MouseEvent.BUTTON1, 0, 1);
                        break;
                    }
                }
            }
        });
        assertFalse(targets.isEmpty(), "no clickable state in view");
        var target = targets.get(0);
        GraphState state = target.getNode();
        waitFor("state " + state + " to become current",
                () -> sim.getModel().getState() == state);
        // well apart from the first click, so as not to make a double click
        Thread.sleep(1000);
        List<Point> spots = new ArrayList<>();
        SwingUtilities.invokeAndWait(() -> {
            Point spot = clickableSpot(canvas, target);
            if (spot != null) {
                spots.add(spot);
                SyntheticMouse
                    .click(component, spot.x, spot.y, MouseEvent.BUTTON1,
                           InputEvent.CTRL_DOWN_MASK, 1);
            }
        });
        assertFalse(spots.isEmpty(), "state " + state + " is no longer clickable");
        waitFor("state display to be selected",
                () -> sim.getModel().getDisplay() == DisplayKind.STATE);
    }

    /** Returns the LTS display's canvas, shown on screen with the explored LTS loaded. */
    private static YFilesLTSCanvas ltsCanvas() throws Exception {
        var sim = simulator();
        if (sim.getModel().getLastExploreType() == null) {
            SwingUtilities.invokeLater(() -> sim.getActions().getExploreAction().execute());
            waitFor("exploration to finish", () -> sim.getModel().getLastExploreType() != null);
        }
        var display = (LTSDisplay) sim.getDisplaysPanel().getDisplay(DisplayKind.LTS);
        SwingUtilities.invokeAndWait(() -> showOnScreen(display));
        waitFor("LTS to be shown",
                () -> display.getCanvas().getViewModel() != null
                    && display.getCanvas().getComponent().isShowing());
        waitForLayout("LTS canvas", display.getCanvas().getComponent());
        return assertInstanceOf(YFilesLTSCanvas.class, display.getCanvas());
    }

    /**
     * A double click on a vertex of the editor tab opens the in-place editor on that
     * vertex, with its labels selected, although its first click started an edge: the
     * second click ends that gesture as the editor rather than as a loop. The clicks go
     * to yFiles' input surface synthetically, in separate event-thread tasks, since the
     * gesture starts once the first click has been handled; the second click follows
     * within the double-click interval.
     */
    @Test
    void doubleClickOnAVertexOfTheEditorTabEditsIt() throws Exception {
        var sim = simulator();
        SwingUtilities.invokeAndWait(() -> {
            sim.getModel().doSelect(ResourceKind.HOST, QualName.name("start"));
            sim.getModel().setDisplay(DisplayKind.HOST);
        });
        var resourceDisplay = (ResourceDisplay) sim.getDisplaysPanel().getDisplay(DisplayKind.HOST);
        SwingUtilities.invokeAndWait(() -> resourceDisplay.startEditResource(QualName.name("start")));
        waitFor("editor tab to be selected",
                () -> resourceDisplay.getSelectedTab() instanceof AspectEditorTab);
        var tab = (AspectEditorTab) resourceDisplay.getSelectedTab();
        SwingUtilities.invokeAndWait(() -> showOnScreen(tab.getCanvas().getComponent()));
        waitFor("editor canvas to show", () -> tab.getCanvas().getComponent().isShowing());
        waitForLayout("editor canvas", tab.getCanvas().getComponent());
        var canvas = assertInstanceOf(YFilesAspectEditorCanvas.class, tab.getCanvas());
        JComponent component = canvas.getComponent();
        var model = canvas.getViewModel();
        assertNotNull(model);
        var createEdge = canvas.editorMode().getCreateEdgeInputMode();
        try {
            int cells = model.getCells().size();
            // the first click, on a vertex with labels, selects it and starts an edge
            List<ViewVertex<?>> targets = new ArrayList<>();
            SwingUtilities.invokeAndWait(() -> {
                for (var cell : canvas.getCells()) {
                    if (cell instanceof ViewVertex<?> vertex && cell instanceof AspectViewCell aspectCell
                        && !aspectCell.getEditableLabels().toEditString().isEmpty()) {
                        Point spot = clickableSpot(canvas, vertex);
                        if (spot != null) {
                            targets.add(vertex);
                            SyntheticMouse.click(component, spot.x, spot.y, MouseEvent.BUTTON1, 0, 1);
                            break;
                        }
                    }
                }
            });
            assertFalse(targets.isEmpty(), "no clickable labelled vertex in view");
            var vertex = targets.get(0);
            String labels = ((AspectViewCell) vertex).getEditableLabels().toEditString();
            waitFor("edge creation to start", createEdge::isCreationInProgress);
            assertSame(vertex, canvas.getSelectedCell(), "the click did not select the vertex");
            // the second click, on the same vertex, ends the gesture as the editor
            List<Point> spots = new ArrayList<>();
            SwingUtilities.invokeAndWait(() -> {
                Point spot = clickableSpot(canvas, vertex);
                if (spot != null) {
                    spots.add(spot);
                    SyntheticMouse.click(component, spot.x, spot.y, MouseEvent.BUTTON1, 0, 2);
                }
            });
            assertFalse(spots.isEmpty(), "the vertex is no longer clickable");
            waitFor("in-place editor to open", canvas::isEditingLabel);
            SwingUtilities.invokeAndWait(() -> {
                assertFalse(createEdge.isCreationInProgress(), "edge creation still in progress");
                assertEquals(cells, model.getCells().size(), "the double click added a cell");
                var area = canvas.editorMode().getTextEditorInputMode().getTextArea();
                assertEquals(labels, area.getText(), "the editor shows the vertex's labels");
                assertEquals(0, area.getSelectionStart(), "start of the initial selection");
                assertEquals(labels.length(), area.getSelectionEnd(), "end of the initial selection");
                canvas.finishEditing();
            });
        } finally {
            SwingUtilities.invokeAndWait(() -> {
                canvas.finishEditing();
                var h = model.getEditHistory();
                if (h != null) {
                    while (h.canUndo()) {
                        h.undo();
                    }
                }
                resourceDisplay.saveEditor(QualName.name("start"), false, true);
            });
        }
    }

    /**
     * The in-place editor of the editor tab opens with the labels of its vertex selected,
     * and takes its keys as the JGraph editor does: Ctrl+Enter and Shift+Enter break the
     * line (and do not reach the Simulator's Ctrl+Enter accelerator), Enter commits. The
     * keys go through the event queue to the focused text area, the path of real key
     * input, which yFiles' key gestures need; so the Simulator window takes the keyboard
     * focus, which makes this a Robot-flagged test like the others that take over the
     * desktop, and it skips when the text area cannot get the focus.
     */
    @Test
    void inPlaceEditorOfTheEditorTabTakesItsKeys() throws Exception {
        assumeTrue(Boolean.getBoolean(ROBOT_PROPERTY),
                   "Robot tests are off: pass -D" + ROBOT_PROPERTY + "=true to run them");
        var sim = simulator();
        SwingUtilities.invokeAndWait(() -> {
            sim.getModel().doSelect(ResourceKind.HOST, QualName.name("start"));
            sim.getModel().setDisplay(DisplayKind.HOST);
        });
        var resourceDisplay = (ResourceDisplay) sim.getDisplaysPanel().getDisplay(DisplayKind.HOST);
        SwingUtilities.invokeAndWait(() -> resourceDisplay.startEditResource(QualName.name("start")));
        waitFor("editor tab to be selected",
                () -> resourceDisplay.getSelectedTab() instanceof AspectEditorTab);
        var tab = (AspectEditorTab) resourceDisplay.getSelectedTab();
        SwingUtilities.invokeAndWait(() -> showOnScreen(tab.getCanvas().getComponent()));
        waitFor("editor canvas to show", () -> tab.getCanvas().getComponent().isShowing());
        waitForLayout("editor canvas", tab.getCanvas().getComponent());
        var canvas = assertInstanceOf(YFilesAspectEditorCanvas.class, tab.getCanvas());
        var model = canvas.getViewModel();
        assertNotNull(model);
        try {
            // the editor opens on a labelled vertex
            List<AspectViewCell> targets = new ArrayList<>();
            SwingUtilities.invokeAndWait(() -> {
                for (var cell : canvas.getCells()) {
                    if (cell instanceof ViewVertex<?> && cell instanceof AspectViewCell aspectCell
                        && !aspectCell.getEditableLabels().toEditString().isEmpty()) {
                        targets.add(aspectCell);
                        canvas.startEditing(cell);
                        break;
                    }
                }
            });
            assertFalse(targets.isEmpty(), "no labelled vertex");
            String labels = targets.get(0).getEditableLabels().toEditString();
            waitFor("in-place editor to open", canvas::isEditingLabel);
            var area = canvas.editorMode().getTextEditorInputMode().getTextArea();
            // the Simulator window is activated by a Robot click on its title bar, as the
            // desktop refuses a window that brings itself to the front while another
            // application is active
            var frame = sim.getFrame();
            var robot = new Robot();
            robot.setAutoDelay(50);
            Point title = frame.getLocationOnScreen();
            robot.mouseMove(title.x + frame.getWidth() / 2, title.y + 10);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            SwingUtilities.invokeAndWait(area::requestFocusInWindow);
            long deadline = System.currentTimeMillis() + 5_000;
            while (!area.isFocusOwner() && System.currentTimeMillis() < deadline) {
                Thread.sleep(100);
            }
            assumeTrue(area.isFocusOwner(),
                       "the in-place editor cannot get the focus: is the Simulator window active?");
            SwingUtilities.invokeAndWait(() -> {
                assertEquals(labels, area.getText(), "the editor shows the vertex's labels");
                assertEquals(0, area.getSelectionStart(), "start of the initial selection");
                assertEquals(labels.length(), area.getSelectionEnd(), "end of the initial selection");
                area.setCaretPosition(labels.length());
            });
            typeKey(area, KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK);
            assertTrue(canvas.isEditingLabel(),
                       () -> "Ctrl+Enter closed the editor, leaving '" + area.getText() + "'");
            assertEquals(labels + "\n", area.getText(), "text after Ctrl+Enter");
            typeKey(area, KeyEvent.VK_ENTER, InputEvent.SHIFT_DOWN_MASK);
            assertTrue(canvas.isEditingLabel(),
                       () -> "Shift+Enter closed the editor, leaving '" + area.getText() + "'");
            assertEquals(labels + "\n\n", area.getText(), "text after Shift+Enter");
            typeKey(area, KeyEvent.VK_ENTER, 0);
            waitFor("editor to close on Enter", () -> !canvas.isEditingLabel());
        } finally {
            SwingUtilities.invokeAndWait(() -> {
                canvas.finishEditing();
                var h = model.getEditHistory();
                if (h != null) {
                    while (h.canUndo()) {
                        h.undo();
                    }
                }
                resourceDisplay.saveEditor(QualName.name("start"), false, true);
            });
        }
    }

    /**
     * The editor tab runs on the yFiles editor canvas: a double click on empty canvas
     * adds a vertex and opens its in-place editor, a click on a vertex followed by a
     * click on another creates an edge, and the tab's history undoes both. The editor
     * input mode of yFiles takes only real mouse input, so this test uses a Robot and
     * needs the canvas visible on screen.
     */
    @Test
    void editorTabEditsThroughGestures() throws Exception {
        var sim = simulator();
        SwingUtilities.invokeAndWait(() -> {
            sim.getModel().doSelect(ResourceKind.HOST, QualName.name("start"));
            sim.getModel().setDisplay(DisplayKind.HOST);
        });
        var resourceDisplay = (ResourceDisplay) sim.getDisplaysPanel().getDisplay(DisplayKind.HOST);
        SwingUtilities.invokeAndWait(() -> resourceDisplay.startEditResource(QualName.name("start")));
        waitFor("editor tab to be selected",
                () -> resourceDisplay.getSelectedTab() instanceof AspectEditorTab);
        var tab = (AspectEditorTab) resourceDisplay.getSelectedTab();
        SwingUtilities.invokeAndWait(() -> showOnScreen(tab.getCanvas().getComponent()));
        waitFor("editor canvas to show", () -> tab.getCanvas().getComponent().isShowing());
        waitForLayout("editor canvas", tab.getCanvas().getComponent());
        var canvas = assertInstanceOf(YFilesAspectEditorCanvas.class, tab.getCanvas());
        JComponent component = canvas.getComponent();
        var model = canvas.getViewModel();
        assertNotNull(model);
        var history = model.getEditHistory();
        assertNotNull(history, "the editor's model has no history");
        var robot = new Robot();
        robot.setAutoDelay(50);
        try {
            assumeCanvasOnScreen(robot, component);
            Point onScreen = component.getLocationOnScreen();
            int cells = model.getCells().size();
            // a double click on empty canvas adds a vertex
            Point empty = emptySpot(canvas);
            robot.mouseMove(onScreen.x + empty.x, onScreen.y + empty.y);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            // a Robot's clicks on a shared desktop do not always arrive as a double click;
            // the gestures themselves are covered headlessly by YFilesEditorTest
            long deadline = System.currentTimeMillis() + 5_000;
            while (model.getCells().size() != cells + 1 && System.currentTimeMillis() < deadline) {
                Thread.sleep(100);
            }
            assumeTrue(model.getCells().size() == cells + 1,
                       "the Robot's double click did not reach the editor canvas");
            waitFor("in-place editor to open", canvas::isEditingLabel);
            SwingUtilities.invokeAndWait(canvas::finishEditing);
            assertTrue(history.canUndo());
            // a click on a vertex, then on another, adds an edge between them
            List<Point> spots = new ArrayList<>();
            List<ViewVertex<?>> vertices = new ArrayList<>();
            for (var cell : canvas.getCells()) {
                if (cell instanceof ViewVertex<?> vertex && spots.size() < 2) {
                    Point2D pos = vertex.getVisuals().getNodePos();
                    var view = ((GraphComponent) component)
                        .toViewCoordinates(new PointD(pos.getX(), pos.getY()));
                    Point spot = new Point((int) Math.round(view.getX()),
                        (int) Math.round(view.getY()));
                    if (spot.x > 0 && spot.y > 0 && spot.x < component.getWidth()
                        && spot.y < component.getHeight()
                        && canvas.getCellAt(spot.x, spot.y) == cell) {
                        spots.add(spot);
                        vertices.add(vertex);
                    }
                }
            }
            assertEquals(2, spots.size(), "no two clickable vertices");
            int edges = model.getCells().size();
            robot.mouseMove(onScreen.x + spots.get(0).x, onScreen.y + spots.get(0).y);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            waitFor("edge creation to start",
                    () -> canvas.editorMode().getCreateEdgeInputMode().isCreationInProgress());
            robot.mouseMove(onScreen.x + spots.get(1).x, onScreen.y + spots.get(1).y);
            robot.delay(200);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            waitFor("edge to be added", () -> model.getCells().size() == edges + 1);
            waitFor("in-place editor to open on the edge", canvas::isEditingLabel);
            // an edge that gets no label is withdrawn: give it one
            SwingUtilities.invokeAndWait(() -> {
                canvas.editorMode().getTextEditorInputMode().getTextArea().setText("fresh");
                canvas.finishEditing();
            });
            ViewEdge<?> edge = assertInstanceOf(ViewEdge.class, canvas.getSelectedCell(),
                "the new edge is selected");
            assertSame(vertices.get(0), edge.getSourceVertex());
            assertSame(vertices.get(1), edge.getTargetVertex());
            // undo both through the history
            SwingUtilities.invokeAndWait(() -> {
                history.undo();
                history.undo();
            });
            assertEquals(cells, model.getCells().size(), "cells after undoing both edits");
        } finally {
            // leave the editor, which is clean again; forget its edits if not
            SwingUtilities.invokeAndWait(() -> {
                canvas.finishEditing();
                var h = model.getEditHistory();
                if (h != null) {
                    while (h.canUndo()) {
                        h.undo();
                    }
                }
                resourceDisplay.saveEditor(QualName.name("start"), false, true);
            });
        }
    }

    /**
     * A double click on a vertex of the editor tab, by a Robot, opens the in-place editor
     * on that vertex and adds nothing, although its first click starts an edge: as in the
     * JGraph editor, a gesture that ends on its own vertex creates no edge, and a
     * self-edge takes a click on empty canvas instead, which the second half of the test
     * makes. The synthetic double click of {@link #doubleClickOnAVertexOfTheEditorTabEditsIt}
     * passed while a real one created a loop, so this test uses real mouse input.
     */
    @Test
    @AIGenerated("Claude Fable 5.1, 2026-10")
    void editorTabDoubleClickEditsAVertexAndLoopsOnEmptyCanvas() throws Exception {
        var sim = simulator();
        SwingUtilities.invokeAndWait(() -> {
            sim.getModel().doSelect(ResourceKind.HOST, QualName.name("start"));
            sim.getModel().setDisplay(DisplayKind.HOST);
        });
        var resourceDisplay = (ResourceDisplay) sim.getDisplaysPanel().getDisplay(DisplayKind.HOST);
        SwingUtilities.invokeAndWait(() -> resourceDisplay.startEditResource(QualName.name("start")));
        waitFor("editor tab to be selected",
                () -> resourceDisplay.getSelectedTab() instanceof AspectEditorTab);
        var tab = (AspectEditorTab) resourceDisplay.getSelectedTab();
        SwingUtilities.invokeAndWait(() -> showOnScreen(tab.getCanvas().getComponent()));
        waitFor("editor canvas to show", () -> tab.getCanvas().getComponent().isShowing());
        waitForLayout("editor canvas", tab.getCanvas().getComponent());
        var canvas = assertInstanceOf(YFilesAspectEditorCanvas.class, tab.getCanvas());
        JComponent component = canvas.getComponent();
        var model = canvas.getViewModel();
        assertNotNull(model);
        var createEdge = canvas.editorMode().getCreateEdgeInputMode();
        var robot = new Robot();
        robot.setAutoDelay(50);
        try {
            assumeCanvasOnScreen(robot, component);
            Point onScreen = component.getLocationOnScreen();
            ViewVertex<?> vertex = null;
            Point spot = null;
            for (var cell : canvas.getCells()) {
                if (cell instanceof ViewVertex<?> candidate) {
                    Point2D pos = candidate.getVisuals().getNodePos();
                    var view = ((GraphComponent) component)
                        .toViewCoordinates(new PointD(pos.getX(), pos.getY()));
                    Point at = new Point((int) Math.round(view.getX()),
                        (int) Math.round(view.getY()));
                    if (at.x > 20 && at.y > 20 && at.x < component.getWidth() - 60
                        && at.y < component.getHeight() - 60
                        && canvas.getCellAt(at.x, at.y) == cell) {
                        vertex = candidate;
                        spot = at;
                        break;
                    }
                }
            }
            assertNotNull(vertex, "no clickable vertex in view");
            assertNotNull(spot);
            int cells = model.getCells().size();
            // a click on the vertex, then on empty canvas, adds a self-edge
            Point empty = emptySpot(canvas);
            robot.mouseMove(onScreen.x + spot.x, onScreen.y + spot.y);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            waitFor("edge creation to start", createEdge::isCreationInProgress);
            robot.mouseMove(onScreen.x + empty.x, onScreen.y + empty.y);
            robot.delay(200);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            waitFor("the click on empty canvas to end the edge",
                    () -> !createEdge.isCreationInProgress());
            waitFor("loop to be added", () -> model.getCells().size() == cells + 1);
            waitFor("in-place editor to open on the loop", canvas::isEditingLabel);
            ViewEdge<?> loop = assertInstanceOf(ViewEdge.class, canvas.getSelectedCell(),
                "the new loop is selected");
            assertSame(vertex, loop.getSourceVertex());
            assertSame(vertex, loop.getTargetVertex());
            // an unlabelled loop is withdrawn when its editor closes
            SwingUtilities.invokeAndWait(canvas::finishEditing);
            assertEquals(cells, model.getCells().size(), "cells after withdrawing the loop");
            // a double click on the vertex opens its editor and adds nothing; it comes
            // last, as a Robot's clicks on a shared desktop do not always arrive as a
            // double click, which ends the test by assumption
            robot.delay(1000);
            robot.mouseMove(onScreen.x + spot.x, onScreen.y + spot.y);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            long deadline = System.currentTimeMillis() + 5_000;
            while (!canvas.isEditingLabel() && model.getCells().size() == cells
                && System.currentTimeMillis() < deadline) {
                Thread.sleep(100);
            }
            var last = ((GraphComponent) component).getLastMouse2DEvent();
            assumeTrue(last != null && last.getClickCount() == 2,
                       "the Robot's clicks did not arrive as a double click");
            assertEquals(cells, model.getCells().size(), "the double click added a cell");
            assertFalse(createEdge.isCreationInProgress(), "edge creation still in progress");
            assertTrue(canvas.isEditingLabel(), "the double click did not open the editor");
            assertSame(vertex, canvas.getSelectedCell(), "the vertex is not selected");
            SwingUtilities.invokeAndWait(canvas::finishEditing);
        } finally {
            // leave the editor, which is clean again; forget its edits if not
            SwingUtilities.invokeAndWait(() -> {
                canvas.finishEditing();
                var h = model.getEditHistory();
                if (h != null) {
                    while (h.canUndo()) {
                        h.undo();
                    }
                }
                resourceDisplay.saveEditor(QualName.name("start"), false, true);
            });
        }
    }

    /**
     * Dragging a vertex in the editor tab moves it: a Robot presses on the vertex, moves
     * and releases, and the vertex position changes as one edit. Needs the canvas on
     * screen, like the other Robot tests.
     */
    @Test
    void editorTabDragsAVertex() throws Exception {
        var sim = simulator();
        SwingUtilities.invokeAndWait(() -> {
            sim.getModel().doSelect(ResourceKind.HOST, QualName.name("start"));
            sim.getModel().setDisplay(DisplayKind.HOST);
        });
        var resourceDisplay = (ResourceDisplay) sim.getDisplaysPanel().getDisplay(DisplayKind.HOST);
        SwingUtilities.invokeAndWait(() -> resourceDisplay.startEditResource(QualName.name("start")));
        waitFor("editor tab to be selected",
                () -> resourceDisplay.getSelectedTab() instanceof AspectEditorTab);
        var tab = (AspectEditorTab) resourceDisplay.getSelectedTab();
        SwingUtilities.invokeAndWait(() -> showOnScreen(tab.getCanvas().getComponent()));
        waitFor("editor canvas to show", () -> tab.getCanvas().getComponent().isShowing());
        waitForLayout("editor canvas", tab.getCanvas().getComponent());
        var canvas = assertInstanceOf(YFilesAspectEditorCanvas.class, tab.getCanvas());
        JComponent component = canvas.getComponent();
        var model = canvas.getViewModel();
        assertNotNull(model);
        var robot = new Robot();
        robot.setAutoDelay(30);
        try {
            assumeCanvasOnScreen(robot, component);
            Point onScreen = component.getLocationOnScreen();
            ViewVertex<?> vertex = null;
            Point spot = null;
            for (var cell : canvas.getCells()) {
                if (cell instanceof ViewVertex<?> candidate) {
                    Point2D pos = candidate.getVisuals().getNodePos();
                    var view = ((GraphComponent) component)
                        .toViewCoordinates(new PointD(pos.getX(), pos.getY()));
                    Point at = new Point((int) Math.round(view.getX()),
                        (int) Math.round(view.getY()));
                    if (at.x > 20 && at.y > 20 && at.x < component.getWidth() - 60
                        && at.y < component.getHeight() - 60
                        && canvas.getCellAt(at.x, at.y) == cell) {
                        vertex = candidate;
                        spot = at;
                        break;
                    }
                }
            }
            assertNotNull(vertex, "no draggable vertex");
            assertNotNull(spot);
            Point2D before = vertex.getVisuals().getNodePos();
            robot.mouseMove(onScreen.x + spot.x, onScreen.y + spot.y);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            for (int i = 1; i <= 8; i++) {
                robot.mouseMove(onScreen.x + spot.x + 5 * i, onScreen.y + spot.y + 4 * i);
            }
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            var moved = vertex;
            waitFor("vertex to move", () -> !moved.getVisuals().getNodePos().equals(before));
            var history = model.getEditHistory();
            assertNotNull(history);
            assertTrue(history.canUndo(), "the drag is an edit");
            SwingUtilities.invokeAndWait(history::undo);
            assertEquals(before, vertex.getVisuals().getNodePos(), "position after undo");
        } finally {
            SwingUtilities.invokeAndWait(() -> {
                canvas.finishEditing();
                var h = model.getEditHistory();
                if (h != null) {
                    while (h.canUndo()) {
                        h.undo();
                    }
                }
                resourceDisplay.saveEditor(QualName.name("start"), false, true);
            });
        }
    }

    /**
     * Dragging a vertex on the resource view tab moves it: a Robot presses on the
     * vertex, moves and releases, and the vertex position in the view model changes.
     * Needs the canvas on screen, like the other Robot tests. The vertex is put back
     * afterwards, since the view tab stores the layout in the grammar.
     */
    @Test
    void viewTabDragsAVertex() throws Exception {
        var canvas = resourceCanvas(ResourceKind.HOST, DisplayKind.HOST, "start");
        JComponent component = canvas.getComponent();
        var robot = new Robot();
        robot.setAutoDelay(30);
        ViewCell<AspectGraph> vertex = null;
        Point spot = null;
        for (var cell : canvas.getCells()) {
            if (cell instanceof ViewVertex<?>) {
                Point2D pos = cell.getVisuals().getNodePos();
                var view = ((GraphComponent) component)
                    .toViewCoordinates(new PointD(pos.getX(), pos.getY()));
                Point at = new Point((int) Math.round(view.getX()), (int) Math.round(view.getY()));
                if (at.x > 20 && at.y > 20 && at.x < component.getWidth() - 60
                    && at.y < component.getHeight() - 60 && canvas.getCellAt(at.x, at.y) == cell) {
                    vertex = cell;
                    spot = at;
                    break;
                }
            }
        }
        assertNotNull(vertex, "no draggable vertex");
        assertNotNull(spot);
        Point2D old = vertex.getVisuals().getNodePos();
        Point2D before = new Point2D.Double(old.getX(), old.getY());
        var moved = vertex;
        try {
            assumeCanvasOnScreen(robot, component);
            Point onScreen = component.getLocationOnScreen();
            robot.mouseMove(onScreen.x + spot.x, onScreen.y + spot.y);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            for (int i = 1; i <= 8; i++) {
                robot.mouseMove(onScreen.x + spot.x + 5 * i, onScreen.y + spot.y + 4 * i);
            }
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            waitFor("vertex to move", () -> !moved.getVisuals().getNodePos().equals(before));
        } finally {
            if (!before.equals(moved.getVisuals().getNodePos())) {
                VisualMap back = new VisualMap();
                back.setNodePos(before);
                SwingUtilities.invokeAndWait(() -> canvas.edit(Map.of(moved, back)));
            }
        }
    }

    @Test
    void ruleTabShowsAllCells() throws Exception {
        checkAllCellsShown(resourceCanvas(ResourceKind.RULE, DisplayKind.RULE, "load"));
    }

    @Test
    void ruleTabPaintsEdges() throws Exception {
        checkEdgesPainted(resourceCanvas(ResourceKind.RULE, DisplayKind.RULE, "load"));
    }

    /** Checks that the shown graph of a canvas holds an item for every visible cell. */
    private static void checkAllCellsShown(YFilesCanvas<?> canvas) {
        checkCellsShown(canvas, true);
    }

    /**
     * Checks that the shown graph of a canvas holds an item for every visible cell.
     * @param allNodes if {@code true}, every node of the graph must have a visible cell
     */
    private static <G extends Graph> void checkCellsShown(YFilesCanvas<G> canvas,
        boolean allNodes) {
        GraphComponent component = (GraphComponent) canvas.getComponent();
        int visibleNodes = 0;
        int visibleEdges = 0;
        for (var cell : canvas.getCells()) {
            if (cell.getVisuals().isVisible()) {
                if (cell instanceof ViewVertex) {
                    visibleNodes++;
                } else {
                    visibleEdges++;
                }
            }
        }
        var graph = canvas.getGraph();
        assertNotNull(graph);
        if (allNodes) {
            assertEquals(graph.nodeCount(), visibleNodes, "visible vertex cells");
        } else {
            assertTrue(visibleNodes > 0, "no visible vertex cells");
        }
        assertTrue(visibleEdges > 0, "no visible edge cells");
        assertEquals(visibleNodes, component.getGraph().getNodes().size(), "shown nodes");
        assertEquals(visibleEdges, component.getGraph().getEdges().size(), "shown edges");
    }

    /** Checks that the live paint of a canvas paints its longest straight edge. */
    private static <G extends Graph> void checkEdgesPainted(YFilesCanvas<G> canvas)
        throws Exception {
        GraphComponent component = (GraphComponent) canvas.getComponent();
        var bounds = canvas.getGraphBounds();
        assertNotNull(bounds);
        List<String> zoomedSize = new ArrayList<>();
        SwingUtilities.invokeAndWait(() -> {
            component.zoomTo(RectD.fromRectangle2D(bounds));
            zoomedSize.add(component.getWidth() + "x" + component.getHeight());
        });
        Point2D probe = edgeProbe(canvas);
        // the first paint creates the visuals, the second updates them
        for (int pass = 1; pass <= 2; pass++) {
            // the image is sized, painted and probed in one event-thread task, so that
            // the three agree on the component's size and zoom
            List<PaintShot> shots = new ArrayList<>();
            SwingUtilities.invokeAndWait(() -> {
                var image = paint(component);
                PointD view = component.toViewCoordinates(new PointD(probe.getX(), probe.getY()));
                shots.add(new PaintShot(image, view, component.getZoom()));
            });
            var shot = shots.get(0);
            if (!isPainted(shot.image(), shot.view())) {
                fail("pass " + pass + ": nothing painted at " + shot.view() + " (probe " + probe
                    + ", bounds " + bounds + ", zoom " + shot.zoom() + ", component "
                    + zoomedSize.get(0) + " when zoomed, image " + shot.image().getWidth() + "x"
                    + shot.image().getHeight() + "; " + saveShot(canvas, pass, shot.image())
                    + ")");
            }
        }
    }

    /** An image painted by a canvas component, with a probe point in view coordinates. */
    private record PaintShot(BufferedImage image, PointD view, double zoom) {
        // no further members
    }

    /**
     * Saves the image of a failed paint check among the test reports, which a failed CI
     * run keeps, and returns a description of where it went.
     */
    private static <G extends Graph> String saveShot(YFilesCanvas<G> canvas, int pass,
                                                     BufferedImage image) {
        @Nullable G graph = canvas.getGraph();
        String name = graph == null
            ? "canvas"
            : graph.getName().replaceAll("\\W", "_");
        File dir = new File("target/surefire-reports").getAbsoluteFile();
        File file = new File(dir, "YFilesSimulatorTest-" + name + "-pass" + pass + ".png");
        try {
            dir.mkdirs();
            ImageIO.write(image, "png", file);
            return "image saved as " + file;
        } catch (IOException exc) {
            return "image not saved: " + exc.getMessage();
        }
    }

    /** Switching to a grammar with another type graph must keep the canvases working. */
    @Test
    void switchingGrammarsKeepsShowingCells() throws Exception {
        checkAllCellsShown(stateCanvas());
        loadGrammar(OTHER_GRAMMAR);
        checkAllCellsShown(stateCanvas());
        assertTrue(EDT_ERRORS.isEmpty(), () -> "Errors on the event thread: " + EDT_ERRORS);
    }

    /**
     * A right click opens GROOVE's popup menu. yFiles' popup mode does not react to
     * synthetic mouse events, so this test uses a Robot, which needs the canvas visible on
     * screen; it is skipped otherwise.
     */
    @Test
    void rightClickOpensPopupMenu() throws Exception {
        var canvas = stateCanvas();
        JComponent component = canvas.getComponent();
        var robot = new Robot();
        robot.setAutoDelay(50);
        assumeCanvasOnScreen(robot, component);
        Point onScreen = component.getLocationOnScreen();
        // an empty spot, away from the borders where the scroll bars may be
        Point spot = emptySpot(canvas);
        robot.mouseMove(onScreen.x + spot.x, onScreen.y + spot.y);
        robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
        try {
            waitFor("popup menu to show (event thread errors: " + EDT_ERRORS + ")",
                    () -> visiblePopup() != null);
            JPopupMenu popup = visiblePopup();
            assertNotNull(popup);
            assertTrue(popup.getComponentCount() > 0, "empty popup menu");
        } finally {
            SwingUtilities
                .invokeAndWait(() -> MenuSelectionManager.defaultManager().clearSelectedPath());
        }
        waitFor("popup menu to close", () -> visiblePopup() == null);
    }

    /** Returns the state display's canvas, shown on screen with the start state loaded. */
    private static YFilesAspectCanvas stateCanvas() throws Exception {
        var display = (StateDisplay) simulator().getDisplaysPanel().getDisplay(DisplayKind.STATE);
        SwingUtilities.invokeAndWait(() -> showOnScreen(display));
        waitFor("start state to be shown",
                () -> display.getCanvas().getViewModel() != null
                    && display.getCanvas().getComponent().isShowing());
        waitForLayout("state canvas", display.getCanvas().getComponent());
        return assertInstanceOf(YFilesAspectCanvas.class, display.getCanvas());
    }

    /**
     * Returns the canvas of the tab of a named resource, shown on screen.
     * @param kind the resource kind
     * @param display the display kind of that resource kind
     * @param name the name of the resource
     */
    private static YFilesAspectCanvas resourceCanvas(ResourceKind kind, DisplayKind display,
                                                     String name) throws Exception {
        var sim = simulator();
        SwingUtilities.invokeAndWait(() -> {
            sim.getModel().doSelect(kind, QualName.name(name));
            sim.getModel().setDisplay(display);
        });
        var resourceDisplay = (ResourceDisplay) sim.getDisplaysPanel().getDisplay(display);
        waitFor(name + " tab to be selected",
                () -> resourceDisplay.getSelectedTab() instanceof GraphDisplay<?> tab
                    && tab.getViewModel() != null);
        var tab = (GraphDisplay<?>) resourceDisplay.getSelectedTab();
        SwingUtilities.invokeAndWait(() -> showOnScreen(tab.getCanvas().getComponent()));
        waitFor(name + " canvas to show", () -> tab.getCanvas().getComponent().isShowing());
        waitForLayout(name + " canvas", tab.getCanvas().getComponent());
        return assertInstanceOf(YFilesAspectCanvas.class, tab.getCanvas());
    }

    /**
     * Waits until a shown component has been laid out. A tab that was just selected shows
     * before the pending validation has sized it, so geometry read in between is stale.
     */
    private static void waitForLayout(String name, Component component)
        throws InterruptedException {
        waitFor(name + " to be laid out", () -> component.isValid() && component.getWidth() > 0
            && component.getHeight() > 0);
    }

    /**
     * Returns the view point of a vertex if it lies inside the canvas component, clear of
     * its right and bottom edges, and hits that vertex. To be called on the event thread,
     * in the same task that uses the point.
     */
    private static @Nullable Point clickableSpot(YFilesCanvas<?> canvas, ViewVertex<?> vertex) {
        JComponent component = canvas.getComponent();
        Point2D pos = vertex.getVisuals().getNodePos();
        var view = ((GraphComponent) component)
            .toViewCoordinates(new PointD(pos.getX(), pos.getY()));
        int x = (int) Math.round(view.getX());
        int y = (int) Math.round(view.getY());
        boolean inside = x > 0 && y > 0 && x < component.getWidth() - 20
            && y < component.getHeight() - 20;
        return inside && canvas.getCellAt(x, y) == vertex
            ? new Point(x, y)
            : null;
    }

    /**
     * Presses and releases a key on the focused component, through the event queue, so
     * that the events take the path of real key input (a key event dispatched to a
     * component directly is rerouted to the focus owner by the focus manager). Returns
     * once the events have been processed.
     */
    private static void typeKey(Component target, int keyCode, int modifiers) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            long when = System.currentTimeMillis();
            char ch = keyCode == KeyEvent.VK_ENTER
                ? '\n'
                : KeyEvent.CHAR_UNDEFINED;
            var pressed = new KeyEvent(target, KeyEvent.KEY_PRESSED, when, modifiers, keyCode, ch);
            var released = new KeyEvent(target, KeyEvent.KEY_RELEASED, when + 10, modifiers,
                keyCode, ch);
            var queue = Toolkit.getDefaultToolkit().getSystemEventQueue();
            queue.postEvent(pressed);
            queue.postEvent(released);
        });
        // the posted events are processed before this task
        SwingUtilities.invokeAndWait(() -> {
            // nothing: the wait is the point
        });
    }

    /** Selects the tabs on the path to a component, so that it shows. */
    private static void showOnScreen(Component component) {
        Component child = component;
        for (Container parent = component.getParent(); parent != null; child = parent,
            parent = parent.getParent()) {
            if (parent instanceof JTabbedPane tabs && tabs.indexOfComponent(child) >= 0) {
                tabs.setSelectedComponent(child);
            }
        }
    }

    /** Returns a point of the canvas component, in its own coordinates, showing no cell. */
    private static Point emptySpot(YFilesCanvas<?> canvas) {
        JComponent component = canvas.getComponent();
        int w = component.getWidth();
        int h = component.getHeight();
        int margin = 40;
        for (int y = h - margin; y >= margin; y -= 20) {
            for (int x = w - margin; x >= margin; x -= 20) {
                if (canvas.getCellAt(x, y) == null) {
                    return new Point(x, y);
                }
            }
        }
        throw new AssertionError("no empty spot on the canvas");
    }

    /** Returns a world point on the drawn path of the longest straight edge, near its source. */
    private static <G extends Graph> Point2D edgeProbe(YFilesCanvas<G> canvas) {
        ViewEdge<G> longest = null;
        double longestLength = 0;
        for (var cell : canvas.getCells()) {
            if (cell instanceof ViewEdge<G> edge && !edge.isLoop()
                && edge.getVisuals().getPoints().size() == 2) {
                var source = edge.getSourceVertex();
                var target = edge.getTargetVertex();
                assertNotNull(source);
                assertNotNull(target);
                double length = source
                    .getVisuals()
                    .getNodePos()
                    .distance(target.getVisuals().getNodePos());
                if (length > longestLength) {
                    longest = edge;
                    longestLength = length;
                }
            }
        }
        assertNotNull(longest, "no straight edge");
        // a point a quarter along the drawn path, which leaves the source node where
        // the JGraph backend would, not necessarily on the line between the centres
        IEdge item = assertInstanceOf(IEdge.class, YFilesCellStore.itemOf(longest));
        IEdgeStyle style = item.getStyle();
        Path2D shape = new Path2D.Double();
        style.getRenderer().getPathGeometry(item, style).getPath().updatePath(shape, null);
        List<Point2D> path = new ArrayList<>();
        var iter = shape.getPathIterator(null);
        double[] coords = new double[6];
        while (!iter.isDone()) {
            if (iter.currentSegment(coords) != PathIterator.SEG_CLOSE) {
                path.add(new Point2D.Double(coords[0], coords[1]));
            }
            iter.next();
        }
        assertTrue(path.size() >= 2, "drawn path of the longest edge: " + path);
        Point2D p0 = path.get(0);
        Point2D p1 = path.get(1);
        return new Point2D.Double(p0.getX() + (p1.getX() - p0.getX()) / 4,
            p0.getY() + (p1.getY() - p0.getY()) / 4);
    }

    /**
     * Paints a component into an image of its own size. To be called on the event thread,
     * in the same task as every use of the component's geometry that must agree with it.
     */
    private static BufferedImage paint(JComponent component) {
        var image = new BufferedImage(component.getWidth(), component.getHeight(),
            BufferedImage.TYPE_INT_RGB);
        var g = image.createGraphics();
        component.paint(g);
        g.dispose();
        return image;
    }

    /** Tests if any pixel within two pixels of a view point is not white. */
    private static boolean isPainted(BufferedImage image, PointD view) {
        int px = (int) Math.round(view.getX());
        int py = (int) Math.round(view.getY());
        for (int x = px - 2; x <= px + 2; x++) {
            for (int y = py - 2; y <= py + 2; y++) {
                if (x >= 0 && y >= 0 && x < image.getWidth() && y < image.getHeight()
                    && (image.getRGB(x, y) & 0xFFFFFF) != 0xFFFFFF) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Brings the Simulator frame to the front and checks that a component is actually
     * visible on screen where it says it is, by comparing a screen capture with the
     * component's own painting; a locked desktop or a covering window skips the test,
     * since a Robot's clicks would then go elsewhere.
     */
    private static void assumeCanvasOnScreen(Robot robot, JComponent component)
        throws Exception {
        assumeTrue(Boolean.getBoolean(ROBOT_PROPERTY),
                   "Robot tests are off: pass -D" + ROBOT_PROPERTY + "=true to run them");
        var frame = simulator().getFrame();
        SwingUtilities.invokeAndWait(() -> {
            frame.setAlwaysOnTop(true);
            frame.toFront();
            frame.setAlwaysOnTop(false);
        });
        Thread.sleep(500);
        // the screen area and the painting are taken in one event-thread task, so that
        // they agree on the component's size
        List<java.awt.Rectangle> areas = new ArrayList<>();
        List<BufferedImage> paintings = new ArrayList<>();
        SwingUtilities.invokeAndWait(() -> {
            areas.add(new java.awt.Rectangle(component.getLocationOnScreen(),
                component.getSize()));
            paintings.add(paint(component));
        });
        var capture = robot.createScreenCapture(areas.get(0));
        BufferedImage painted = paintings.get(0);
        int samples = 0;
        int matches = 0;
        for (int x = 5; x < capture.getWidth() - 5; x += 7) {
            for (int y = 5; y < capture.getHeight() - 5; y += 7) {
                samples++;
                if (near(capture.getRGB(x, y), painted.getRGB(x, y))) {
                    matches++;
                }
            }
        }
        assumeTrue(matches > samples * 0.8,
                   "the canvas is not visible on screen (locked desktop or covering window): "
                       + matches + " of " + samples + " samples match");
    }

    /** Tests if two colours are close. */
    private static boolean near(int rgb1, int rgb2) {
        int dr = Math.abs(((rgb1 >> 16) & 0xFF) - ((rgb2 >> 16) & 0xFF));
        int dg = Math.abs(((rgb1 >> 8) & 0xFF) - ((rgb2 >> 8) & 0xFF));
        int db = Math.abs((rgb1 & 0xFF) - (rgb2 & 0xFF));
        return dr + dg + db < 60;
    }

    /** Returns the showing popup menu, if any. */
    private static @Nullable JPopupMenu visiblePopup() {
        for (MenuElement element : MenuSelectionManager.defaultManager().getSelectedPath()) {
            if (element instanceof JPopupMenu popup && popup.isShowing()) {
                return popup;
            }
        }
        return null;
    }

    /** Polls a condition until it holds, failing after {@link #TIMEOUT} ms. */
    private static void waitFor(String description, BooleanSupplier condition)
        throws InterruptedException {
        long deadline = System.currentTimeMillis() + TIMEOUT;
        while (!condition.getAsBoolean()) {
            if (System.currentTimeMillis() > deadline) {
                fail("Timed out waiting for " + description);
            }
            Thread.sleep(50);
        }
    }

    /** Returns the Simulator launched for this test class, failing if it was not launched. */
    private static Simulator simulator() {
        var result = simulator;
        assertNotNull(result, "Simulator not launched");
        return result;
    }

    /** The Simulator launched for this test class. */
    private static @Nullable Simulator simulator;
}

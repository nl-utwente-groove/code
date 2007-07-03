package groove.gui;

import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.GraphInfo;
import groove.graph.Node;
import groove.gui.jgraph.JGraph;
import groove.gui.layout.JVertexLayout;
import groove.gui.layout.LayoutMap;
import groove.io.ExtensionFilter;
import groove.io.GrooveFileChooser;
import groove.util.Converter;
import groove.util.ExprParser;
import groove.util.Groove;
import groove.view.aspect.Aspect;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectNode;
import groove.view.aspect.AspectValue;
import groove.view.aspect.RuleAspect;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

import net.sf.epsgraphics.ColorMode;
import net.sf.epsgraphics.EpsGraphics;

/**
 * Class providing functionality to export a {@link JGraph} to a file in different formats.
 * @author Arend Rensink
 * @version $Revision: 1.6 $
 */
public class Exporter {
    /**
     * Returns a file chooser for exporting, lazily creating it first.
     */
    public JFileChooser getFileChooser() {
        if (fileChooser== null) {
            fileChooser = new GrooveFileChooser();
            fileChooser.setAcceptAllFileFilterUsed(false);
            for (Format format: getFormatList()) {
                fileChooser.addChoosableFileFilter(format.getFilter());
            }
            fileChooser.setFileFilter(PngFormat.getInstance().getFilter());
        }
        return fileChooser;
    }
    
    /**
     * Exports the current state to a given format. The format is deduced from the file name, using
     * known file filters.
     */
    public void export(JGraph jGraph, File file) throws IOException {
        for (Format format: getFormatList()) {
            if (format.getFilter().accept(file)) {
                format.export(jGraph, file);
                return;
            }
        }
    }

    /** Returns an unmodifiable view on the list of currently supported formats. */
    public List<Format> getFormats() {
        return Collections.unmodifiableList(getFormatList());
    }

    /** Returns the list of file extensions of the supported formats. */
    public List<String> getExtensions() {
        List<String> result = new ArrayList<String>();
        for (Format format: getFormatList()) {
            result.add(format.getFilter().getExtension());
        }
        return result;
    }
    
    /** Returns the default format. */
    public Format getDefaultFormat() {
        return PngFormat.getInstance();
    }
    
    /** Returns the (modifiable) list of currently supported formats. */
    private List<Format> getFormatList() {
        if (formats == null) {
            formats = new ArrayList<Format>();
            formats.add(LispFormat.getInstance());
            formats.add(FsmFormat.getInstance());
            formats.add(JpgFormat.getInstance());
            formats.add(PngFormat.getInstance());
            formats.add(EpsFormat.getInstance());
        }
        return formats;
    }
    
    /** The file chooser of this exporter. */
    private GrooveFileChooser fileChooser;
    /** List of the supported export formats. */
    private List<Format> formats;
    
    /** Singleton class implementing the FSM export format. */
    private static class FsmFormat implements Format {
        /** Empty constructor to ensure singleton usage of the class. */
        private FsmFormat() {
            // empty
        }
        
        public ExtensionFilter getFilter() {
            return fsmFilter;
        }

        public void export(JGraph jGraph, File file) throws IOException {
            PrintWriter writer = new PrintWriter(new FileWriter(file));
            Converter.graphToFsm(jGraph.getModel().toPlainGraph(), writer);
            writer.close();
        }

        /**
         * Extension filter used for exporting graphs in fsm format.
         */
        private final ExtensionFilter fsmFilter = Groove.createFsmFilter();
        
        /** Returns the singleton instance of this class. */
        public static Format getInstance() {
            return instance;
        }
        
        /** The singleton instance of this class. */
        private static final Format instance = new FsmFormat();
    }

    
    /** Singleton class implementing the Lisp export format. */
    private static class LispFormat implements Format {
        /** Empty constructor to ensure singleton usage of the class. */
        private LispFormat() {
            // empty
        }

        public ExtensionFilter getFilter() {
            return lispFilter;
        }

        public void export(JGraph jGraph, File file) throws IOException {
            PrintWriter writer = new PrintWriter(new FileWriter(file));
            convert(AspectGraph.getFactory().fromPlainGraph(jGraph.getModel().toPlainGraph()), writer);
            writer.close();
        }
        
        /** Writes a graph to a writer in the requried format. */
        private void convert(AspectGraph graph, PrintWriter writer) {
            this.writer = writer;
            this.indent = 0;
            nodeMap = new HashMap<Node,Integer>();
            edgeMap = new HashMap<Edge,Integer>();
            println("(%s", GRAPH_KEYWORD);
            println("(%s", SUBGRAPH_KEYWORD);
            println("(%s (", LET_KEYWORD);
            int max = 0;
            for (Node node: graph.nodeSet()) {
            	if (node instanceof DefaultNode) {
            		int nr = ((DefaultNode) node).getNumber();
            		max = Math.max(max, nr+1);
            		nodeMap.put(node, nr);
            		println("(%s (%s %d))", nodeId(node), CREATE_NODE_KEYWORD, nr);
            	}
            }
            for (Node node: graph.nodeSet()) {
            	if (!(node instanceof DefaultNode)) {
            		int nr = max++;
            		nodeMap.put(node, nr);
            		println("(%s (%s %d))", nodeId(node), CREATE_NODE_KEYWORD, nr);
            	}
            }
            
            int edgeCount = 0;
            for (Edge edge: graph.edgeSet()) {
            	if (!isNodeLabel(edge)) {
					int nr = edgeCount;
					edgeCount++;
					edgeMap.put(edge, nr);
					println("(%s (%s %s %s))",
							edgeId(edge),
							CREATE_EDGE_KEYWORD,
							nodeId(edge.source()),
							nodeId(edge.opposite()));
				}
            }
            println(")");
            
            // definitions are done; now add labels
            //for (AspectEdge edge: graph.edgeSet()) {
            //	String id = isNodeLabel(edge) ? nodeId(edge.source()) : edgeId(edge);
            //	println("(%s (%s %s) %s)", ADD_LABEL_KEYWORD, LIST_KEYWORD, id, label(edge));
            //}
            
            // add edge roles 
            // chr: the loops are redundant, which is inefficient but better for readability
            Aspect currentAspect;
            StringBuffer currentLabel;
            for (AspectEdge edge: graph.edgeSet()) {
            	String id = isNodeLabel(edge) ? nodeId(edge.source()) : edgeId(edge);
            	currentLabel = new StringBuffer();
            	for (AspectValue value: edge.getAspectMap().values()) {
            		currentAspect = value.getAspect();
            		// only rule aspects can be outputted for now
            		// this will very soon change to something like (defun set-aspect (list-of-ids aspect aspect-name) ...)
            		if( currentAspect instanceof RuleAspect )
            			println("(%s-%s (%s %s) (%s-%s))", SET_PREFIX, value.getAspect(), LIST_KEYWORD, id, value.getAspect(), value.getName());
            		else
            			currentLabel.append( value.getName() );
            	}
            	
            	if( currentLabel.length() > 0)
            		currentLabel.append(":");
            	
            	currentLabel.append( label(edge) );
            	
            	println("(%s (%s %s) %s)", ADD_LABEL_KEYWORD, LIST_KEYWORD, id, quote( currentLabel.toString() ));
            }
            
            // add node roles 
            for (AspectNode node: graph.nodeSet()) {
            	for (AspectValue value: node.getAspectMap().values()) {
            		println("(%s-%s (%s %s) (%s-%s))", SET_PREFIX, value.getAspect(), LIST_KEYWORD, nodeId(node), value.getAspect(), value.getName());
            	}
            }
            
            // if we have layout information, export it
            if (GraphInfo.hasLayoutMap(graph)) {
                LayoutMap<Node, Edge> layoutMap = GraphInfo.getLayoutMap(graph);

                // lists of coordinates seen so far
                ArrayList<Double> xlist = new ArrayList<Double>();
                ArrayList<Double> ylist = new ArrayList<Double>();

                double epsilon = 10; // grid size

                Double x, y;

                for (Node node : graph.nodeSet()) {
                    JVertexLayout layout = layoutMap.nodeMap().get(node);
                    Rectangle2D r = layout.getBounds();

                    x = new Double(r.getCenterX());
                    y = new Double(r.getCenterY());

                    // check whether current node is
                    // aligned on the grid with the ones seen so far
                    for (Double val : xlist) {
                        if (Math.abs((val.doubleValue() - x.doubleValue())) < epsilon) {
                            x = val;
                            break;
                        }
                    }

                    for (Double val : ylist) {
                        if (Math.abs((val.doubleValue() - y.doubleValue())) < epsilon) {
                            y = val;
                            break;
                        }
                    }

                    // if the coordinate differs more than grid-size
                    // from the ones seen so far add it as a new coordinate to
                    // the list
                    if (!xlist.contains(x)) {
                        xlist.add(x);
                    }
                    if (!ylist.contains(y)) {
                        ylist.add(y);
                    }

                    // output coordinates of the current node
                    println("(%s %s (%s %s %s))", ADD_POSITION, nodeId(node), LIST_KEYWORD, x.toString(), y.toString());
                }
            }
            println(")))");
            assert this.indent == 0 : String.format("Conversion ended at indentation level %d", indent);
        }

        /** Returns an identifier for a node, using the underlying {@link #nodeMap}. */
        private String nodeId(Node node) {
        	return "n"+nodeMap.get(node);
        }

        /** Returns an identifier for an edge, using the underlying {@link #edgeMap}. */
        private String edgeId(Edge edge) {
        	return "e"+edgeMap.get(edge);
        }

        /** Retrieves the edge label. */
        private String label(Edge edge) {
        	return edge.label().text();
        }
        
        private String quote(String aString)
        {
        	return ExprParser.toQuoted( aString, ExprParser.DOUBLE_QUOTE_CHAR );
        }
        
        /** Indicates if an edge should be regarded as a node label. */
        private boolean isNodeLabel(Edge edge) {
        	return edge.source() == edge.opposite();
        }
        
        /** Prints a line to a writer, taking care of indentation. */
        private void println(String line, Object... args) {
        	// chr: The following code will break if we get 
        	//      wrong indentation counts (closes > opens).
            //char[] spaces = new char[INDENT_COUNT * indent];
            //Arrays.fill(spaces, ' ');
            
        	// wrong indentation is acceptable, therefore use
        	// a loop that simply terminates if i < 0
        	for( int i=0; i < indent * INDENT_COUNT; i++)
            	writer.print(' ');
            
        	// chr: take care of escape characters
        	//      and quotes to handle labels like:
        	//      String s = "\"(\""; 
            String text = String.format(line, args);
            writer.println(text);
            int opens = 0;
            int closes = 0;
            int escape = 0;
            boolean ignore = false;
            for (char c : text.toCharArray()) {
				if (escape > 0) {
					escape++;
				}
				if (escape == 3) {
					escape = 0;
				}
				if (c == '\\') {
					escape++;
				}
				if (c == '\"' && (escape == 0)) {
					ignore = !ignore;
				}
				if (!ignore && (escape == 0)) {
					if (c == '(') {
						opens++;
					} else if (c == ')') {
						closes++;
					}
				}
			}
            indent += opens - closes;
        }

        /**
		 * The writer used in the current
		 * {@link #convert(AspectGraph, PrintWriter)} invocation.
		 */
        private PrintWriter writer;
        /** The indentation level of the current {@link #convert(AspectGraph, PrintWriter)} invocation. */
        private int indent;
        /**
         * Extension filter used for exporting graphs in lisp format.
         */
        private final ExtensionFilter lispFilter = Groove.getFilter("Lisp layout files", Groove.LISP_EXTENSION, true);

        /** Map from nodes to numbers built up during {@link #convert(AspectGraph, PrintWriter)}. */
        private Map<Node,Integer> nodeMap;
        /** Map from edges to numbers built up during {@link #convert(AspectGraph, PrintWriter)}. */
        private Map<Edge,Integer> edgeMap;
        /** Returns the singleton instance of this class. */
        public static Format getInstance() {
            return instance;
        }
        
        /** The singleton instance of this class. */
        private static final Format instance = new LispFormat();
        
        /** Number of indentation positions per indent level. */
        private static int INDENT_COUNT = 4;
        /** Lisp function for graphs. */
        private static final String GRAPH_KEYWORD = "graph";
        /** Lisp function for subgraphs. */
        private static final String SUBGRAPH_KEYWORD = "sub-graph";
        /** Lisp let function. */
        private static final String LET_KEYWORD = "let*";
        /** Lisp function for node creation. */
        private static final String CREATE_NODE_KEYWORD = "create-node";
        /** Lisp function for edge creation. */
        private static final String CREATE_EDGE_KEYWORD = "create-edge";
        /** Lisp function for creating lists. */
        private static final String LIST_KEYWORD = "list";
        /** Lisp function for adding labels. */
        private static final String ADD_LABEL_KEYWORD = "add-label";
        /** Lisp function for setting rule roles. */
        private static final String SET_PREFIX = "set";
        /** Lisp function for adding positions for nodes. */
        private static final String ADD_POSITION = "add-position";
    }

    
    /** Class implementing the JPG export format. */
    private static class JpgFormat implements Format {
        /** Empty constructor to ensure singleton usage of the class. */
        private JpgFormat() {
            // empty
        }
        
        public ExtensionFilter getFilter() {
            return jpgFilter;
        }

        public void export(JGraph jGraph, File file) throws IOException {
            ImageIO.write(jGraph.toImage(), jpgFilter.getExtension().substring(1), file);
        }

        /**
         * Extension filter used for exporting graphs in jpeg format.
         */
        private final ExtensionFilter jpgFilter = new ExtensionFilter("JPEG image files", Groove.JPG_EXTENSION);

        /** Returns the singleton instance of this class. */
        public static Format getInstance() {
            return instance;
        }
        
        /** The singleton instance of this class. */
        private static final Format instance = new JpgFormat();
    }

    
    /** Class implementing the PNG export format. */
    private static class PngFormat implements Format {
        /** Empty constructor to ensure singleton usage of the class. */
        private PngFormat() {
            // empty
        }
        
        public ExtensionFilter getFilter() {
            return pngFilter;
        }

        public void export(JGraph jGraph, File file) throws IOException {
            ImageIO.write(jGraph.toImage(), pngFilter.getExtension().substring(1), file);
        }

        /**
         * Extension filter used for exporting graphs in png format.
         */
        private final ExtensionFilter pngFilter = new ExtensionFilter("PNG image files",
                Groove.PNG_EXTENSION);

        /** Returns the singleton instance of this class. */
        public static Format getInstance() {
            return instance;
        }
        
        /** The singleton instance of this class. */
        private static final Format instance = new PngFormat();
    }

    
    /** Class implementing the EPS export format. */
    private static class EpsFormat implements Format {
        /** Empty constructor to ensure singleton usage of the class. */
        private EpsFormat() {
            // empty
        }

        public ExtensionFilter getFilter() {
            return epsFilter;
        }

        public void export(JGraph jGraph, File file) throws IOException {
            // Create a graphics contents on the buffered image
            BufferedImage image = jGraph.toImage();
            // Create an output stream
            OutputStream out = new FileOutputStream(file);
            // minX,minY,maxX,maxY
            EpsGraphics g2d = new EpsGraphics("Title", out, 0, 0, image.getWidth(), image
                    .getHeight(), ColorMode.COLOR_RGB);
            g2d.drawImage(jGraph.toImage(), new AffineTransform(), null);
            g2d.close();
        }

        /**
         * Extension filter used for exporting graphs in png format.
         */
        private final ExtensionFilter epsFilter = new ExtensionFilter("EPS image files",
                Groove.EPS_EXTENSION);

        /** Returns the singleton instance of this class. */
        public static Format getInstance() {
            return instance;
        }
        
        /** The singleton instance of this class. */
        private static final Format instance = new EpsFormat();
    }

    /**
     * Interface for export formats.
     */
    public static interface Format {
        /** Returns the extension filter for this format. */
        ExtensionFilter getFilter();
        /** Exports a JGraph into this format. */
        void export(JGraph jGraph, File file) throws IOException;
    }
}

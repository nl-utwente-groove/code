package groove.calc;

import groove.graph.Graph;
import groove.trans.GraphGrammar;
import groove.view.FormatException;

import java.io.File;
import java.io.IOException;

@Deprecated
@SuppressWarnings("all")
public class GrooveIO {

	private String path;
	
	public GrooveIO(String path) {
		this.path = path;
	}
	
	public GraphGrammar loadGrammar(String relativePath) throws FormatException {
		// TODO get default graph name from static variable somewhere
		return loadGrammar(path, "start");
	}
	
	public GraphGrammar loadGrammar (String relativePath, String startGraphName) throws FormatException {
		return null;
	}
	
	public Graph loadGraph(String relativePath, String graphName) {
		return null;
	}
	
	public void saveGraph(Graph graph, File file) throws IOException { 
		
	}
	
	/**
	 * Save the graph in the directory relative to the loader path with a given graphname (without extension) 
	 * @param graph the graph to be saved
	 * @param path the directory relative to the loader path
	 * @param graphName the name of the graph without extension
	 */
	public void saveGraph(Graph graph, String path, String graphName) {
		
	}
	
}

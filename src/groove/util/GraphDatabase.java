package groove.util;

import groove.graph.*;
import groove.trans.NameLabel;

import java.io.*;
import java.util.Vector;

/**
 * This is a factory class for graphs of the http://amalfi.dis.unina.it/graph/ collection. This
 * class exposes all needed methods to read and transform these graphs into GROOVE's internal
 * graph formats.
 */
public class GraphDatabase {
    /** String holdiong the base path of the graph database **/
    private String location;

    /** filter for filenames, so we only list the index files **/
    FilenameFilter filter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return name.endsWith(".gtr");
        }
    };

    /** collection containing the index files **/
    Vector<String> indices = new Vector<String>();

    public GraphDatabase(String loc) {
        location = loc;
        if( (!loc.endsWith("/")) || (!loc.endsWith("\\")) )
            location += "/";
        listIndices();
    }

    /**
     * List the graph database ground truth files
     */
    private void listIndices() {
        File dir = new File(location);

        String[] children = dir.list(filter);
        if (children == null) {
            // Either dir does not exist or is not a directory
        } else {
            for (int i=0; i<children.length; i++) {
                // Get filename of file or directory
                String filename = children[i];
                indices.add(filename);
            }
        }
    }

    /**
     * This method load's a graph file and returns it as a NodeEdgeSetGraph
     *
     * @param location
     * @return NodeEdgeSetGraph the graph represented by the file from the collection
     */
    public static NodeEdgeSetGraph load(String location) {
        NodeEdgeSetGraph nesg = new NodeEdgeSetGraph();
        int target, edges;

        try {
            Label label = DefaultLabel.parseLabel("a");
            FileInputStream fis = new FileInputStream(new File(location));
            int nodecount = readword(fis);
            //System.out.println("nodes: " + nodecount);
            Node[] nodes = new Node[nodecount];
            // for all nodes, construct an "index"
            for (int i = 0; i < nodecount; i++)
                nodes[i] = nesg.addNode();

            // for all nodes
            for (int source = 0; source < nodecount; source++) {
                edges = readword(fis);
                //System.out.println("edges: " + edges);
                // for all outgoing edges
                for (int j = 0; j < edges; j++) {
                    // read target node
                    target = readword(fis);
                    //System.out.println("adding: " + source + " " + target);
                    nesg.addEdge(nodes[source], label, nodes[target]);
                }
            }
        } catch (GraphFormatException e) {
            System.err.println("label parse error"+ e.getMessage());            
        } catch (FileNotFoundException e) {
            System.err.println("Graph file " + location + " does not exist.");
        }
        return nesg;
    }

    public static int actual(String location, String gtr, String afile) {
        try {
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            BufferedReader input = new BufferedReader(new FileReader(new File(location+gtr)));
            String line = null; //not declared within while loop

            //readLine is a bit quirky :
            // it returns the content of a line MINUS the newline.
            // it returns null only for the END of the stream.
            // it returns an empty String if two newlines appear in a row.

            while ((line = input.readLine()) != null) {
                if( line.startsWith(afile) ) {
                    return Integer.parseInt(line.substring( afile.length()+1, line.length()));
                }
            }
            System.err.println("Did not find graph " + afile + " in ground truth file!!" );
            input.close();
        } catch (IOException e) {
            System.err.println("Graph file " + location + " does not exist.");
            System.exit(-1);
        }
        return -1;
    }

    /**
     * This method reads a word from the file and constructs a suitable Java representation.
     *
     * @param fis FileInputStream for the file being read
     * @return int the word that was read or -1 if something went wrong.
     */
    private static int readword(FileInputStream fis) {
        int lsb, msb;
        int word = -1;
        try {
            lsb  = fis.read();
            msb  = fis.read();
            word = lsb | (msb << 8);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return word;
    }

    /**
     * This function returns a collection of strings representing all the Ground Truth Records
     * from the Graph Database. These can be used to query for all the graphs via graphs(string).
     * @return
     */
    public String[] records() {
        String[] result = new String[0];
        return indices.toArray(result);
    }

    public GraphDatabaseIterator read(String gtr) {
        gtr           = gtr.replace(".gtr", "");
        String[] dirs = gtr.split("_");
        String subdir = location;
        for( String dir : dirs ) {
            subdir = subdir.concat(dir + "\\");
        }
        
        return new GraphDatabaseIterator(location, subdir, dirs);
    }
}

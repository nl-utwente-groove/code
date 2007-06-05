package groove.util;

import java.util.Iterator;
import java.io.File;

/**
 * @author iGniSz
 */
public class GraphDatabaseIterator implements Iterator {
    /** holds the filename used to reconstruct the actual graph filename in next() **/
    private String file;
    /** the nr of files in the directory **/
    private int size = 0;
    /** current file we are at **/
    private int graphindex = 0;

    /** current size of graph we are at **/
    private int sizeindex = 0;
    /** holds the indicators for the sizes of the graphs, used to
     *  reconstruct the actual graph filename in next()           **/
    private String[]    sizes = new String[] {
            "s20", "s40", "s60", "s80", "s100", "m200", "m400", "m600", "m800", "m1000"
    };

    private String gtrlocation   = "";
    private String graphlocation = "";

    public GraphDatabaseIterator(String loc, String subdir, String[] parts) {
        gtrlocation   = loc;
        graphlocation = subdir; 
        size       = countFiles();
        graphindex = 0;
        sizeindex  = -1;
        file       = parts[0] + "_" + parts[1];
    }

    /**
     * Read the directory we are iterating over and return the file count so
     * we know when to stop.
     * @return int number of graph files
     */
    private int countFiles() {
        File dir = new File(graphlocation);

        String[] children = dir.list();
        if (children == null) {
            // Either dir does not exist or is not a directory
            System.err.println("Could not find the graph files.");
            System.exit(-1);
        } else {            
            return Math.round((children.length-1)/2);            
        }
        return 0;
    }

    public boolean hasNext() {
        return (graphindex < size);
    }

    public MatchingPair next() {
        int index = (graphindex % 10);
        // keep track of the size
        if( index == 0 )
            sizeindex++;

        // quick and dirty formatting
        String sindex = "";
        if( index < 10 )
            sindex = "0" + index;
        else
            sindex = index + "";

        graphindex++;        

        // construct filename
        String afile = file + "_" + sizes[sizeindex] + ".A" + sindex;
        String bfile = file + "_" + sizes[sizeindex] + ".B" + sindex;           
        // hack for outofmemory
        File f = new File(graphlocation+bfile);
        if( f.length() > 1500 ) {
            System.err.println("Skipped a graph pair because it is to large.. " + afile );
            return null;
        }

        
        try {
            MatchingPair p = new MatchingPair(GraphDatabase.load(graphlocation+afile),
                                              GraphDatabase.load(graphlocation+bfile),
                                              GraphDatabase.actual(gtrlocation, file+".gtr", afile));
            return p;
        } catch( OutOfMemoryError e ) {
            System.gc();
        }
        return null;        
    }

    public void remove() {
        throw new RuntimeException("Not supported");
    }
}

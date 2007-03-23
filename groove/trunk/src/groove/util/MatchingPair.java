package groove.util;

import groove.graph.Graph;

/**
 * Helper class to represent a pair of graphs and the number of injective matchings between them
 * @author J. ter Hove
 */
public class MatchingPair {
    public Graph pattern;
    public Graph model;
    public int   matchings = 0;

    public MatchingPair(Graph lhs, Graph rhs, int m) {
        pattern   = lhs;
        model     = rhs;
        matchings = m;
    }
}

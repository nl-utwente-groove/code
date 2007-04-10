package groove.graph.match.vf2;

/**
 * @author iGniSz
 */
public class Pair {
    public int left;
    public int right;


    public Pair(int left, int right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "( " + left + ", " + right + ")";
    }
}

package groove.graph.match.vf1;

/**
 * @author J. ter Hove
 */
public class VF1Pair<A,B> {
    public A left;
    public B right;

    public VF1Pair(A left, B right) {
        this.left = left;
        this.right = right;
    }

    public String toString() {
        return "("+left+", "+right+")";
    }

    public int hashCode() {
        return left.hashCode() + right.hashCode();
    }
}

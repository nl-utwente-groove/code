package groove.abstraction.neigh.trans;

import static groove.abstraction.neigh.Multiplicity.OMEGA;

/** Range of possible values for the variables. Used in the solutions. */
final class Value {
    /** Total range of values. */
    final int bound;
    /** Current minimum index in the range. */
    int i;
    /** Current maximum index in the range. */
    int j;

    /** Basic constructor. Start the indices to cover the given range. */
    Value(int bound) {
        this.bound = bound;
        this.i = 0;
        this.j = OMEGA;
    }

    /** Copying constructor. */
    Value(Value original) {
        this.bound = original.bound;
        this.i = original.i;
        this.j = original.j;
    }

    @Override
    public String toString() {
        return "(" + this.i + "," + (this.j == OMEGA ? "w" : this.j) + ")";
    }

    @Override
    public Value clone() {
        return new Value(this);
    }

    /** Returns true if this range indices are equal. */
    boolean isSingleton() {
        return this.i == this.j;
    }

    /**
     * Increases the minimum index until we hit the given limit or
     * the maximum index. Doesn't operate on the dual range.
     */
    void cutLow(int limit) {
        if (this.i < limit) {
            this.i = Math.min(this.j, Math.min(this.bound, limit));
        }
    }

    /**
     * Decreases the maximum index until we hit the given limit or
     * the minimum index. Doesn't operate on the dual range.
     */
    void cutHigh(int limit) {
        if (this.j > limit) {
            this.j = Math.max(this.i, limit);
        }
    }

    /** Returns true if this current range is 0 .. 1 .*/
    boolean isZeroOne() {
        return this.i == 0 && this.j == 1;
    }

    /** Sets this value to zero. */
    void setToZero() {
        this.i = 0;
        this.j = 0;
    }
}
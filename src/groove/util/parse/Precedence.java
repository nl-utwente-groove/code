package groove.util.parse;

import static groove.util.parse.Precedence.Direction.LEFT;
import static groove.util.parse.Precedence.Direction.NEITHER;
import static groove.util.parse.Precedence.Direction.RIGHT;
import static groove.util.parse.Precedence.Placement.INFIX;
import static groove.util.parse.Precedence.Placement.PREFIX;

/** 
 * Operator precedence values, from low to high.
 * This is copied directly from the Java operator precedence.
 */
public enum Precedence {
    /** Dummy value used for context of an expression. */
    NONE(NEITHER),
    /** Disjunction. */
    OR(LEFT),
    /** Conjunction. */
    AND(LEFT),
    /** Negation. */
    NOT(PREFIX, RIGHT),
    /** Equality and inequality tests. */
    EQUAL(LEFT),
    /** Comparison operators: lesser than, greater than (or equal). */
    COMPARE(LEFT),
    /** Assignment operators. */
    ASSIGN(NEITHER),
    /** Additive operators: addition, subtraction, string concatenation. */
    ADD(LEFT),
    /** Multiplicative operators: multiplication, division, modulo. */
    MULT(LEFT),
    /** Unary operator: unary minus. */
    UNARY(PREFIX, RIGHT),
    /** Field expressions. */
    FIELD(LEFT),
    /** Atomic expressions: variables, constants. */
    ATOM(NEITHER);

    private Precedence(Placement place, Direction direction) {
        this.direction = direction;
        this.place = place;
    }

    private Precedence(Direction direction) {
        this(INFIX, direction);
    }

    /** Returns the direction of associativity. */
    public Direction getDirection() {
        return this.direction;
    }

    /** Returns the direction of associativity. */
    public Placement getPlace() {
        return this.place;
    }

    /** Returns the next higher precedence, or {@code null} if this is the highest value. */
    public Precedence increase() {
        int nextIx = ordinal() + 1;
        return nextIx >= values().length ? null : values()[nextIx];
    }

    private final Direction direction;

    private final Placement place;

    /** Direction of associativity. */
    public static enum Direction {
        /** Left associative. */
        LEFT,
        /** Left associative. */
        RIGHT,
        /** Not associative. */
        NEITHER, ;
    }

    /** Operator placement. */
    public static enum Placement {
        /** Prefix operator. */
        PREFIX,
        /** Postfix operator. */
        POSTFIX,
        /** Infix operator. */
        INFIX, ;
    }
}
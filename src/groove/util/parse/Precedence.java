package groove.util.parse;

import static groove.util.parse.Precedence.Direction.LEFT;
import static groove.util.parse.Precedence.Direction.NEITHER;
import static groove.util.parse.Precedence.Direction.RIGHT;
import static groove.util.parse.Precedence.Placement.INFIX;
import static groove.util.parse.Precedence.Placement.PREFIX;

/**
 * Operator kind, consisting of an implicit precedence ordering,
 * a {@link Placement} type, and (for infix operators) an associativity {@link Direction}.
 * The precedence mimics the Java operator precedence.
 */
public enum Precedence {
    /** Dummy value used for lowest-level context of an expression. */
    NONE(NEITHER),
    /** Disjunction. */
    OR(LEFT),
    /** Conjunction. */
    AND(LEFT),
    /** Negation. */
    NOT(PREFIX),
    /** Equality and inequality tests. */
    EQUAL(RIGHT),
    /** Comparison operators: lesser than, greater than (or equal). */
    COMPARE(RIGHT),
    /** Existential and universal quantification. */
    QUANT(PREFIX),
    /** Assignment operators. */
    ASSIGN(RIGHT),
    /** Additive operators: addition, subtraction, string concatenation. */
    ADD(LEFT),
    /** Multiplicative operators: multiplication, division, modulo. */
    MULT(LEFT),
    /** Unary operator: unary minus. */
    UNARY(PREFIX),
    /** Field expressions. */
    FIELD(LEFT),
    /** Call expressions. */
    CALL(PREFIX),
    /** Identifier prefix separator. */
    PREFIX_ID(NEITHER),
    /** Qualified identifier separator. */
    QUAL_ID(LEFT),
    /** Atomic expressions: variable names and constants. */
    ATOM(NEITHER), ;

    private Precedence(Placement place, Direction direction) {
        this.direction = direction;
        this.place = place;
    }

    private Precedence(Direction direction) {
        this(INFIX, direction);
    }

    private Precedence(Placement placement) {
        this(placement, placement == PREFIX ? RIGHT : LEFT);
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
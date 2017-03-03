package groove.util.parse;

import static groove.util.parse.OpKind.Direction.LEFT;
import static groove.util.parse.OpKind.Direction.NEITHER;
import static groove.util.parse.OpKind.Direction.RIGHT;
import static groove.util.parse.OpKind.Placement.INFIX;
import static groove.util.parse.OpKind.Placement.PREFIX;

import org.eclipse.jdt.annotation.NonNull;

/**
 * Operator kind, consisting of an implicit precedence ordering,
 * a {@link Placement} type, and (for infix operators) an associativity {@link Direction}.
 * The precedence mimics the Java operator precedence.
 */
public enum OpKind {
    /** Dummy value used for lowest-level context of an expression.
     * Operators of this kind will be ignored by the parser, and so
     * are guaranteed to never end up in a term tree.
     */
    NONE(PREFIX, NEITHER, 0),
    /** (In)equivalence. */
    EQUIV(NEITHER),
    /** Implication. */
    IMPLIES(RIGHT),
    /** Disjunction. */
    OR(RIGHT),
    /** Conjunction. */
    AND(RIGHT),
    /** Temporal prefix operators. */
    TEMP_PREFIX(PREFIX),
    /** Temporal infix operators. */
    TEMP_INFIX(RIGHT),
    /** Negation. */
    NOT(PREFIX),
    /** Existential and universal quantification. */
    QUANT(PREFIX),
    /** Equality and inequality tests. */
    EQUAL(NEITHER),
    /** Comparison operators: lesser than, greater than [or equal]. */
    COMPARE(RIGHT),
    /** Assignment operators. */
    ASSIGN(RIGHT),
    /** Additive operators: addition, subtraction, string concatenation. */
    ADD(LEFT),
    /** Multiplicative operators: multiplication, division, modulo. */
    MULT(LEFT),
    /** Unary operator: unary minus. */
    UNARY(PREFIX),
    /** Field operator. */
    FIELD(LEFT),
    /** Call-type operator. */
    CALL(PREFIX, RIGHT, -1),
    /** Atom, e.g., variable name or constant.
     * This is the only non-dummy operator kind (e.g., not {@link #NONE} or {@link #HIGH}) with arity 0.
     */
    ATOM(PREFIX, NEITHER, 0),
    /** Dummy value used for highest-level context of an expression.
     * Operators of this kind will be ignored by the parser, and so
     * are guaranteed to never end up in a term tree.
     */
    HIGH(PREFIX, NEITHER, 0),;

    private OpKind(Placement place, Direction direction, int arity) {
        this.direction = direction;
        this.place = place;
        this.arity = arity;
    }

    /** Constructs a binary infix operator kind with given associativity direction. */
    private OpKind(Direction direction) {
        this(INFIX, direction, 2);
    }

    /** Constructs a unary pre- or postfix operator kind. */
    private OpKind(Placement placement) {
        this(placement, placement == PREFIX ? RIGHT : LEFT, 1);
        assert placement == PREFIX || placement == Placement.POSTFIX;
    }

    /** Returns the direction of associativity. */
    public Direction getDirection() {
        return this.direction;
    }

    private final Direction direction;

    /** Returns the direction of associativity. */
    public Placement getPlace() {
        return this.place;
    }

    private final Placement place;

    /** Returns the number of arguments an operator of this kind expects,
     * or -1 if the number of arguments is not fixed by the operator kind.
     */
    public int getArity() {
        return this.arity;
    }

    private final int arity;

    /** Returns the next higher precedence, or {@code null} if this is the highest value. */
    public @NonNull OpKind increase() {
        int nextIx = ordinal() + 1;
        return nextIx >= values().length ? HIGH : values()[nextIx];
    }

    /** Direction of associativity. */
    public static enum Direction {
        /** Left associative. */
        LEFT,
        /** Left associative. */
        RIGHT,
        /** Not associative. */
        NEITHER,;
    }

    /** Operator placement. */
    public static enum Placement {
        /** Prefix operator. */
        PREFIX,
        /** Postfix operator. */
        POSTFIX,
        /** Infix operator. */
        INFIX,;
    }
}
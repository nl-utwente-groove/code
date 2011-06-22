package groove.algebra;

/** 
 * Operator precedence values, from low to high.
 * This is copied directly from the Java operator precedence.
 */
public enum Precedence {
    /** Conjunction. */
    AND,
    /** Disjunction. */
    OR,
    /** Equality and inequality tests. */
    EQUAL,
    /** Comparison operators: lesser than, greater than (or equal). */
    COMPARE,
    /** Additive operators: addition, subtraction, string concatenation. */
    ADD,
    /** Multiplicative operators: multiplication, division, modulo. */
    MULT,
    /** Unary operators. */
    UNARY
}
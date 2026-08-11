package nl.utwente.groove.util;

import java.util.Random;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Allows to choose a random element from a sequence without storing all the
 * elements of the sequence. On can "show" objects from the sequence to the
 * chooser and, at each moment, the chooser can give back one random element
 * among those it has seen up to now. It also guarantees that all seen elements
 * can be picked with equal probability.
 *
 * A typical usage is to show to the chooser some number of elements, and then
 * ask for a random representative. Note that two successive calls to
 * {@link #pickRandom()} will return the same element. This cannot be avoided if
 * one does not want to store the whole sequence.
 * @author Iovka Boneva
 *
 */
public class RandomChooserInSequence<E> {

    /**
     * Creates a chooser drawing its choices from a given random generator.
     * The generator is typically owned by the caller and shared over
     * successive choosers, so that every chooser draws fresh values.
     * @param rgen source of the random choices
     */
    public RandomChooserInSequence(Random rgen) {
        this.rgen = rgen;
    }

    /**
     * Shows an element to the random chooser.
     * @throws NullPointerException if <code>e</code> is null
     */
    public void show(E e) {
        if (e == null) {
            throw new NullPointerException();
        }
        this.nbSeen++;
        if (this.rgen.nextInt(this.nbSeen) == 0) {
            this.current = e;
        }
    }

    /**
     * Gives back a random element among those that have been seen so far. Two
     * successive calls to this method will give the same element.
     * @see #show
     * @return a random element among those that have been seen so far, or null
     *         if no element is seen.
     */
    public @Nullable E pickRandom() {
        return this.current;
    }

    /**
     * Returns the number of elements shown to the chooser since the last reset.
     */
    public int size() {
        return this.nbSeen;
    }

    /** Forgets all elements seen so far. */
    public void reset() {
        this.nbSeen = 0;
        this.current = null;
    }

    /** The number of currently seen elements. */
    private int nbSeen = 0;
    /** The current randomly chosen element, among those already seen. */
    private @Nullable E current;
    /**
     * The random generator supplied at construction. Deliberately not
     * created here: a chooser is typically constructed per choice, so a
     * generator seeded at construction would replay the same drawings
     * in every chooser, making the choice a fixed function of the number
     * of elements shown.
     */
    private final Random rgen;

}

package nl.utwente.groove.explore.verify;

import java.util.Random;

import nl.utwente.groove.util.RandomChooserInSequence;
import nl.utwente.groove.verify.ProductListener;
import nl.utwente.groove.verify.ProductState;
import nl.utwente.groove.verify.ProductStateSet;

/**
 * Listens to a GTS and allows to pick a random state among those newly added to
 * the GTS. Should listen to a single GTS.
 */
public class RandomNewStateChooser implements ProductListener {
    /**
     * Creates a chooser drawing its choices from a given random generator.
     * @param rgen source of the random choices
     */
    public RandomNewStateChooser(Random rgen) {
        this.rc = new RandomChooserInSequence<>(rgen);
    }

    /**
     * Returns a randomly chosen state among those newly added to the GTS it
     * listens to since last {@link #reset()} operation. Two successive calls
     * will return the same element.
     * @return a randomly chosen state among those newly added to the GTS it
     *         listens to since last {@link #reset()}, or <code>null</code>
     *         if no new state was added.
     */
    public ProductState pickRandomNewState() {
        return this.rc.pickRandom();
    }

    /** Forgets all new states it has seen so far. */
    public void reset() {
        this.rc.reset();
    }

    /**
     * Indicates if no new state was seen since the last {@link #reset()}.
     */
    public boolean isEmpty() {
        return this.rc.size() == 0;
    }

    @Override
    public void addUpdate(ProductStateSet shape, ProductState state) {
        this.rc.show(state);
    }

    private final RandomChooserInSequence<ProductState> rc;
}

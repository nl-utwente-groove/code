package groove.explore.util;

import groove.verify.ProductListener;
import groove.verify.ProductState;
import groove.verify.ProductStateSet;

/**
 * Listens to a GTS and allows to pick a random state among those newly added to
 * the GTS. Should listen to a single GTS.
 */
public class RandomNewStateChooser implements ProductListener {

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

    @Override
    public void closeUpdate(ProductStateSet gts, ProductState state) {
        // does nothing
    }

    private final RandomChooserInSequence<ProductState> rc =
        new RandomChooserInSequence<ProductState>();
}

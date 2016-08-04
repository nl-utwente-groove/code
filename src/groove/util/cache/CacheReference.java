/**
 *
 */
package groove.util.cache;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Reference subclass with the following features:
 * <ul>
 * <li> It knows <i>strong</i> and <i>soft</i> states; in the strong state, it
 * maintains a strong reference to its referent so that the referent cannot be
 * collected;
 * <li> When it detects that the referent has been collected, it replaces the
 * reference in the holder by a constant <code>null</code> reference for
 * memory efficiency;
 * <li> It maintains a reincarnation count.
 * </ul>
 * @author Arend Rensink
 * @version $Revision$
 */
public class CacheReference<C> extends SoftReference<@Nullable C> {
    /**
     * Constructs a new reference, for a given cache and cache holder, on the
     * basis of an existing template. All data except the cache are shared from
     * the template reference. Presumably the template reference was cleared.
     * @param holder the cache holder for the new cache
     * @param referent the cache to be held; non-<code>null</code>
     * @param template the template reference; not <code>null</code>
     */
    protected CacheReference(CacheHolder<C> holder, C referent, CacheReference<C> template) {
        super(referent, queue);
        this.holder = holder;
        this.incarnation = template.incarnation + 1;
        incFrequency(this.incarnation);
        this.strong = template.strong;
        if (this.strong) {
            this.referent = referent;
        }
        this.strongNull = template.strongNull;
        this.softNull = template.softNull;
        // see if there is any post-clearing up to be done for caches
        // that have been collected by the gc
        CacheReference<@Nullable ?> reference = (CacheReference<@Nullable ?>) queue.poll();
        while (reference != null) {
            reference.updateCleared();
            cacheCollectCount++;
            reference = (CacheReference<@Nullable ?>) queue.poll();
        }
        if (holder != null) {
            createCount++;
        }
    }

    /**
     * Constructor for a cache holder with (strong or weak) null references.
     * @param strong if <code>true</code>, the reference is initially strong
     * @param incarnation the incarnation count of the new reference
     * @param template reference to copy the lists of strong and soft null
     *        references from; if <code>null</code>, the lists are freshly
     *        created
     */
    protected CacheReference(boolean strong, int incarnation, CacheReference<C> template) {
        super(null, queue);
        this.holder = null;
        this.incarnation = incarnation;
        this.strong = strong;
        if (template == null) {
            this.strongNull = new ArrayList<>();
            this.softNull = new ArrayList<>();
        } else {
            this.strongNull = template.strongNull;
            this.softNull = template.softNull;
            assert incarnation >= (strong ? this.strongNull : this.softNull).size();
        }
    }

    /**
     * Indicates if the reference is currently strong. If the reference is
     * strong, the referent will never be collected.
     * @return <code>true</code> if the reference is currently strong.
     * @see #setSoft()
     */
    final public boolean isStrong() {
        return this.strong;
    }

    /**
     * Sets the reference to soft. Afterwards, the reference can be collected.
     * @see #isStrong()
     */
    final public void setSoft() {
        if (this.holder != null) {
            assert!this.strong
                || this.referent != null : "Referent cannot be null for strong reference";
            this.strong = false;
            this.referent = null;
        }
    }

    /**
     * Returns the incarnation count of this reference. The incarnation count is
     * the number of times the holder has had to create a new cache, or in other
     * words, the numer of times the cache has been cleared. The first instance
     * of the reference thus has incarnation count <code>0</code>.
     */
    final public int getIncarnation() {
        return this.incarnation;
    }

    /** Sets the appropriate <code>null</code> reference in the holder. */
    @Override
    final public void clear() {
        super.clear();
        updateCleared();
    }

    /**
     * Factory method for a fresh, initialised cache. Apart from the holder and
     * referent, all characteristic of the new reference will be copied from the
     * current reference. The new incarnation of the new reference will be
     * increased w.r.t. the current.
     * @param cache the reference; non-<code>null</code>
     * @return a reference with referent <code>cache </code>, which is strong
     *         if the current reference is strong
     */
    public CacheReference<C> newReference(CacheHolder<C> holder, C cache) {
        return new CacheReference<>(holder, cache, this);
    }

    /**
     * Factory method to create a null reference with the strength (strong or
     * soft) of this reference, and incarnation count set to 0.
     */
    final public CacheReference<C> newNullReference() {
        return getNullInstance(this.strong, 0);
    }

    /**
     * Returns a (shared) null reference with the strength (strong or soft) and
     * the incarnation count of this reference.
     */
    final public CacheReference<C> getNullReference(boolean strong) {
        return getNullInstance(strong, this.incarnation);
    }

    /**
     * Returns a constant null reference of given strength and incarnation
     * count.
     */
    private CacheReference<C> getNullInstance(boolean strong, int incarnation) {
        // fill the null reference lists up to the required incarnation
        for (int i = this.strongNull.size(); i <= incarnation; i++) {
            this.strongNull.add(createNullInstance(true, i));
            this.softNull.add(createNullInstance(false, i));
        }
        return (strong ? this.strongNull : this.softNull).get(incarnation);
    }

    /**
     * Callback factory method to create a <code>null</code> reference with a
     * given strength and incarnation count.
     */
    protected CacheReference<C> createNullInstance(boolean strong, int incarnation) {
        return new CacheReference<>(strong, incarnation, this);
    }

    /**
     * Callback method that sets the reference in the holder to the appropriate
     * null reference.
     * @see CacheHolder#setCacheReference(CacheReference)
     */
    private void updateCleared() {
        if (this.holder != null) {
            synchronized (this.holder) {
                if (this.holder.getCacheReference() == this) {
                    this.holder.setCacheReference(getNullInstance(this.strong, this.incarnation));
                    cacheClearCount++;
                }
            }
        }
    }

    /** Holder of this reference. */
    private final CacheHolder<C> holder;
    /** Flag set as long as the reference is tied. */
    private boolean strong;
    /** Strong reference to the referent, set only if the reference is strong. */
    private @Nullable C referent;
    /** The incarnation count of this reference. */
    private final int incarnation;

    /**
     * Constant null reference, with {@link #isStrong()} set to
     * <code>true</code>.
     */
    private final List<CacheReference<C>> strongNull; // = new
    // CacheReference<Object>(true);
    /**
     * Constant null reference, with {@link #isStrong()} set to
     * <code>false</code>.
     */
    private final List<CacheReference<C>> softNull; // = new
    // CacheReference<Object>(false);

    /**
     * Factory method for an uninitialised strong reference, i.e., with referent
     * <code>null</code>. This is a convenience method for
     * {@link #newInstance(boolean)} with parameter <code>true</code>.
     */
    static public <C> CacheReference<C> newInstance() {
        return newInstance(true);
    }

    /**
     * Factory method for an uninitialised reference, i.e., with referent
     * <code>null</code>.
     * @param strong if <code>true</code> the reference instance is to be
     *        strong
     * @return a reference that is either strong or soft, depending on
     *         <code>strong</code>
     */
    @SuppressWarnings("unchecked")
    static public <C> CacheReference<C> newInstance(boolean strong) {
        return strong ? strongInstance : softInstance;
    }

    /**
     * Returns the total number of cache reincarnations. This equals the sum of
     * the incarnation sizes, except for incarnation 0.
     */
    static public int getIncarnationCount() {
        return incarnationCount;
    }

    /**
     * Registers an incarnation in the frequency list.
     */
    static private void incFrequency(int incarnation) {
        for (int i = frequencies.size(); i <= incarnation; i++) {
            frequencies.add(0);
        }
        frequencies.set(incarnation, frequencies.get(incarnation) + 1);
        if (incarnation > 1) {
            incarnationCount++;
        }
    }

    /**
     * Returns the frequency of a given incarnation. The frequency is the total
     * number of caches that have reached this incarnation.
     */
    static public int getFrequency(int incarnation) {
        return incarnation >= frequencies.size() ? 0 : frequencies.get(incarnation);
    }

    /**
     * Returns the total number of caches created.
     * @return the total number of caches created
     */
    static public int getCreateCount() {
        return createCount;
    }

    /**
     * Returns the number of times a cache was cleared explicitly.
     * @return the number of times a cache was cleared explicitly
     */
    static public int getClearCount() {
        return cacheClearCount;
    }

    /**
     * Returns the number of times a cache was collected by the garbage
     * collector.
     * @return the number of times a cache was collected by the garbage
     *         collector
     */
    static public int getCollectCount() {
        return cacheCollectCount;
    }

    /**
     * Array of frequency counters for each incarnation count.
     */
    static private List<Integer> frequencies = new ArrayList<>();

    /** The singleton null instance for strong references. */
    @SuppressWarnings("rawtypes") static private final CacheReference strongInstance =
        new CacheReference<>(true, 0, null);
    /** The singleton null instance for weak references. */
    @SuppressWarnings("rawtypes") static private final CacheReference softInstance =
        new CacheReference<>(false, 0, null);

    /**
     * Global counter of the total number of cache reincarnations.
     */
    static private int incarnationCount;
    /**
     * Number of cache clearances counted (in {@link #updateCleared()} .
     */
    static private int cacheCollectCount;
    /**
     * Number of effective invocations of {@link #clear()} .
     */
    private static int cacheClearCount;
    /**
     * The total number of non-<code>null</code> graph cache references
     * created.
     */
    static private int createCount;
    /**
     * Queue for garbage collected {@link CacheReference} objects.
     */
    static final private ReferenceQueue<@Nullable Object> queue = new ReferenceQueue<>();
}

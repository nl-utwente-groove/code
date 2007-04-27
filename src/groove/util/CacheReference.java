/**
 * 
 */
package groove.util;

import groove.graph.GraphCache;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

/**
 * Reference subclass with the following features:
 * <ul>
 * <li> It knows <i>tied</i> and <i>untied</i> states; in the tied state, it
 * maintains a srtong reference to its referent so that the referent cannot be collected;
 * <li> When it detects that the referent has been collected, it replaces the
 * reference in the holder by a constant <code>null</code> reference for memory efficiency;
 * <li> It maintains a reincarnation count.
 * </ul>
 * @author Arend Rensink
 * @version $Revision$
 */
public class CacheReference<C> extends SoftReference<C> {
	/**
	 * Constructs a reference for a given holder, with a given referent.
	 * The reference is strong if the holder has no reference set at the time of
	 * invocation; otherwise the strength is copied from the currently set reference.
	 * @param holder the reference holder; non-<code>null</code>
	 * @param referent the cache to be held; non-<code>null</code>
	 * @param strong if <code>true</code>, the reference is to be strong
	 * @param incarnation the incarnation of the new reference
	 */
	private CacheReference(CacheHolder<C> holder, C referent, boolean strong, int incarnation) {
		super(referent, queue);
		this.holder = holder;
		this.strong = strong;
		if (strong) {
			this.referent = referent;
		}
		this.incarnation = incarnation;
        // see if there is any post-clearing up to be done for caches
        // that have been collected by the gc
        CacheReference<?> cache = (CacheReference<?>) queue.poll();
        while (cache != null) {
            cache.updateCleared();
			cacheCollectCount++;
            cache = (CacheReference<?>) queue.poll();
        }
        if (holder != null) {
        	createCount++;
        }
	}
	
	/** Private constructor for (strong or weak) null references. */
	private CacheReference(boolean strong, int incarnation) {
		this(null, null, strong, incarnation);
	}
	
	/**
	 * Indicates if the reference is currently strong.
	 * If the reference is strong, the referent will never be collected.
	 * @return <code>true</code> if the reference is currently strong.
	 * @see #setSoft()
	 */
	public boolean isStrong() {
		return strong;
	}
	
	/**
	 * Sets the reference to soft.
	 * Afterwards, the reference can be collected.
	 * @see #isStrong()
	 */
	public void setSoft() {
		if (holder != null) {
			assert !strong || referent != null : "Referent cannot be null for strong reference";
			strong = false;
			referent = null;
		}
	}
	
	/** 
	 * Returns the incarnation count of this reference.
	 * The incarnation count is the number of times the holder has had to create a new cache,
	 * or in other words, the numer of times the cache has been cleared.
	 * The first instance of the reference thus has incarnation count <code>0</code>.
	 */
	public int getIncarnation() {
		return incarnation;
	}
	
	/** Sets the appropriate <code>null</code> reference in the holder. */
	@Override
	public void clear() {
		super.clear();
		updateCleared();
	}

	/**
	 * Callback method that sets the reference in the holder to the appropriate null reference.
	 * @see CacheHolder#setCacheReference(CacheReference)
	 */
	private void updateCleared() {
		if (holder != null) {
			synchronized (holder) {
				if (holder.getCacheReference() == this) {
					holder.setCacheReference(CacheReference.<C>getNullInstance(strong, incarnation));
				}
			}
		}
	}
	
	/** Holder of this reference. */
	private final CacheHolder<C> holder;
	/** Flag set as long as the reference is tied. */
	private boolean strong;
	/** Strong reference to the referent, set only if the reference is strong. */
	private C referent;
	/** The incarnation count of this reference. */
	private final int incarnation;
	/**
	 * Number of effective invocations of {@link #clear()}.
	 */
	private static int cacheClearCount;
	/**
     * The total number of non-<code>null</code> graph cache references created.
     */
    static private int createCount;

	
	/** Queue for garbage collected {@link CacheReference} objects. */
	static final private ReferenceQueue<Object> queue = new ReferenceQueue<Object>();
	
	/**
	 * Factory method for an uninitialised strong reference, i.e., with referent <code>null</code>.
	 * This is a convenience method for {@link #getNullInstance(boolean)} with parameter <code>true</code>.
	 */
	static public <C> CacheReference<C> getNullInstance() {
		return getNullInstance(true, 0);
	}

	/** 
	 * Factory method for a fresh, initialised reference.
	 * The new reference will be at incarnation 0.
	 * @param <C> the type of the cache
	 * @param holder the cache holder for the new reference; non-<code>null</code>
	 * @param cache the reference; non-<code>null</code>
	 * @return a reference with referent <code>cache </code>, which is strong if the 
	 * current reference of the holder is uninitialised or strong
	 * @see #getNullInstance(boolean) to obtain an uninitialised reference
	 */
	static public <C> CacheReference<C> getInstance(CacheHolder<C> holder, C cache) {
		CacheReference holderReference = holder.getCacheReference();
		boolean strong = holderReference == null || holderReference.isStrong();
		int incarnation = holderReference == null ? 0 : holderReference.getIncarnation() + 1;
		incIncarnationSize(incarnation);
		return new CacheReference<C>(holder, cache, strong, incarnation);
	}

	/**
	 * Factory method for an uninitialised reference, i.e., with referent <code>null</code>.
	 * The returned reference will be shared between all holders requesting it,
	 * at incarnation 0.
	 * @param <C> the type of the cache
	 * @param strong if <code>true</code> the reference instance is to be strong
	 * @return a reference that is either strong or soft, depending on <code>strong</code>
	 */
	static public <C> CacheReference<C> getNullInstance(boolean strong) {
		return getNullInstance(strong, 0);
	}

	/** Returns a constant null reference of given strength and incarnation count. */
	static private <C> CacheReference<C> getNullInstance(boolean strong, int incarnation) {
		if (incarnation >= maxIncarnation) {
			initNullInstances();
		}
		return (CacheReference<C>) (strong ? STRONG_NULL : SOFT_NULL)[incarnation];
	}

	/** Increases the length of the null reference arrays. */
	static private void initNullInstances() {
		int newMaxIncarnation = maxIncarnation == 0 ? 3 : maxIncarnation * 2;
		CacheReference[] NEW_STRONG_NULL = new CacheReference[newMaxIncarnation];
		CacheReference[] NEW_SOFT_NULL = new CacheReference[newMaxIncarnation];
		int[] newIncarnationSize = new int[newMaxIncarnation];
		if (maxIncarnation > 0) {
			System.arraycopy(STRONG_NULL, 0, NEW_STRONG_NULL, 0, maxIncarnation);
			System.arraycopy(SOFT_NULL, 0, NEW_SOFT_NULL, 0, maxIncarnation);
			System.arraycopy(incarnationSize, 0, newIncarnationSize, 0, maxIncarnation);
		}
		for (int i = maxIncarnation; i < newMaxIncarnation; i++) {
			NEW_STRONG_NULL[i] = new CacheReference<Object>(true, i);
			NEW_SOFT_NULL[i] = new CacheReference<Object>(false, i);
		}
		STRONG_NULL = NEW_STRONG_NULL;
		SOFT_NULL = NEW_SOFT_NULL;
		incarnationSize = newIncarnationSize;
		maxIncarnation = newMaxIncarnation;
	}

	/**
	 * Registers an incarnation in the frequency list.
	 */
	static private void incIncarnationSize(int incarnation) {
	    if (incarnation >= maxIncarnation) {
	        initNullInstances();
	    }
	    incarnationSize[incarnation]++;
	    if (incarnation > 1) {
	        incarnationCount++;
	    }
	}

	/** Constant null reference, with {@link #isStrong()} set to <code>true</code>. */
	static private CacheReference[] STRONG_NULL; // = new CacheReference<Object>(true);
	/** Constant null reference, with {@link #isStrong()} set to <code>false</code>. */
	static private CacheReference[] SOFT_NULL; // = new CacheReference<Object>(false);
	/** The length of the null reference arrays. */
	static private int maxIncarnation = 0;
    static {
		initNullInstances();
	}
	
	/**
	 * Returns the total number of cache reincarnations.
	 * This equals the sum of the incarnation sizes, except for incarnation 0.
	 */
	static public int getIncarnationCount() {
	    return incarnationCount;
	}

	/**
	 * Returns the frequency of a given incarnation.
	 * The frequency is the total number of caches that have reached this incarnation.
	 */
	static public int getIncarnationSize(int incarnation) {
	    return incarnation >= maxIncarnation ? 0 : incarnationSize[incarnation];
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
	 * Returns the number of times a cache was collected by the garbage collector.
	 * @return the number of times a cache was collected by the garbage collector
	 */
	static public int getCollectCount() {
	    return cacheCollectCount;
	}

	/**
	 * Array of frequency counters for each incarnation count.
	 */
	static private int[] incarnationSize;

	/**
	 * Global counter of the total number of cache reincarnations.
	 */
	static private int incarnationCount;
	/**
	 * Number of cache clearances counted (in  {@link #updateCleared()} .
	 */
	static private int cacheCollectCount;

}

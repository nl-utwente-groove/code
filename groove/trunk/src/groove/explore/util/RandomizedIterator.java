package groove.explore.util;

import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

/** An iterator over a collection which next method retrieves
 * the elements of the collection in a randomized order.
 * 
 * Uses few space, as it does not need a private copy of the 
 * underlying collection, thus the underlying collection
 * may be shared (for reading only) by several instances
 * of a randomized iterator. The {@link #next()} operation
 * is not constant time, but <code>O(n) * K</code>, where
 * <code>n</code> is the number of elements of the underlying
 * collection, and <code>K</code> is the cost of the next
 * operation for the usual iterator of the underlying collection.
 * Thus, <code>K</code> is not necessarily a constant !
 * 
 * The collection on which this iterator works should guarantee
 * that its iterator() method always retrieves the elements in
 * the same order. Otherwise, the randomized iterator may
 * return some elements multiple times, and others never.
 * 
 * @author Iovka Boneva
 *
 * @param <T>
 */
public class RandomizedIterator<T> implements Iterator<T> {
	public boolean hasNext() {
		return this.available.cardinality() > 0;
	}

	public synchronized T next() {
		if (this.available.cardinality() <= 0) {
			throw new NoSuchElementException();
		}
		int turn = random.nextInt(this.available.cardinality());
		int iterIdx = this.available.nextSetBit(0);
		for (int i = 0; i < turn; i++) {
			iterIdx = this.available.nextSetBit(++iterIdx);
		}
		// now iterIdx is the index of the turn-th bit set to true in this.available
		Iterator<T> iter = this.elements.iterator();
		for (int i = 0; i < iterIdx; i++) {
			iter.next();
		}
		this.available.clear(iterIdx);
		return iter.next();  // should never throw a NoSuchElementException
	}

	/** Informs the iterator that an object is not to be considered as 
	 * a part of the collection any more.
	 * Though, the object is not removed from the collection.
	 * No effect if the object is not an element of the underlying collection.
	 *  
	 */
	public synchronized void removeFromIterator(T object) {
		Iterator<T> iter = this.elements.iterator();
		int idx = 0;
		while (iter.hasNext() && iter.next() != object) {
			idx++;
		}
		this.available.clear(idx);
	}
	
	/** Not supported. */
	public void remove() { throw new UnsupportedOperationException(); }

	/** Constructs a randomized iterator over the elements of a collection.
	 * @param elements 
	 * @require Several copies of elements.iterator() should iterate
	 * over <code>elements</code> in the same order. 
	 */
	public RandomizedIterator (Collection<T> elements) {
		this.elements = elements;
		this.available = new BitSet(elements.size());
		this.available.set(0, elements.size());  // sets all elements to be available
	}
	
	/** The elements on which the iterator iterates. */
	private Collection<T> elements;
	/** Stores information on already returned values. 
	 * The n-th bit of used is set to false if the n-th element
	 * of <code>elements</code> has already been returned by the iterator. 
	 */
	private BitSet available;
	
	/** A shared random generator for all instances of the class. 
	 * Give it a seed (e.g. 0) if for debugging purposes or other reasons
	 * you want all explorations of a strategy to be the same.
	 */
	private static final Random random = new Random();
	
// TESTING METHOD	
//	public static void main (String[] args) {
//		
//		ArrayList<Integer> list = new ArrayList<Integer>(5);
//		for (int i = 0; i < 5; i++) {
//			list.add(i);
//		}
//		
//		for (int i = 0; i < 10; i++) {
//			RandomizedIterator<Integer> it = new RandomizedIterator<Integer>(list);
//			String s = new String();
//			while (it.hasNext()) {
//				s += it.next().toString() + " ";
//			}
//			System.out.println(s + "\n");
//		}
//		
//		System.out.println("\n-------------------------------\n");
//		for (int i = 0; i < 5; i++) {
//			String s = new String();
//			RandomizedIterator<Integer> iter = new RandomizedIterator<Integer>(list);
//			s += iter.next().toString() + " ";
//			s += iter.next().toString() + " ";
//			iter.removeFromIterator(list.get(2));
//			s += iter.next().toString() + " ";
//			s += iter.next().toString() + " ";
//			if (iter.hasNext()) {
//				s += iter.next().toString() + " ";
//			}
//			System.out.println(s + "\n");	
//		}
//		
//	}
}

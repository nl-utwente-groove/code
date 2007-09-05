/**
 * 
 */
package groove.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Set;

/**
 * Provides a view upon a given set that sends notifications of
 * additions and removals.
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 */
public class ObservableSet<T> extends Observable implements Set<T> {
    /** 
     * Creates a new observable set on top of a given set.
     * The set will be aliased. 
     */
    public ObservableSet(final Set<T> set) {
        super();
        this.set = set;
    }
    
    /** Constructs an observable set on top of a fresh empty set. */
    public ObservableSet() {
        this(new HashSet<T>());
    }
    
    /**
     * Delegates the method to the underlying set,
     * then notifies the observers with an AddUpdate.
     */
    public boolean add(T o) {
        if (set.add(o)) {
            setChanged();
            notifyObservers(new AddUpdate<T>(o));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Adds the elements to the underlying set, then
     * notifies the observers for those elements actually added.
     */
    public boolean addAll(Collection< ? extends T> c) {
        Set<T> addedElements = new HashSet<T>();
        boolean result = false;
        for (T element: c) {
            if (set.add(element)) {
                addedElements.add(element);
                result = true;
            }
        }
        if (result) {
            setChanged();
            notifyObservers(new AddUpdate<T>(addedElements));
        }
        return result;
    }

    /**
     * Delegates the method to the underlying set, then
     * notifies the observers with a {@link RemoveUpdate}.
     */
    public void clear() {
        if (!set.isEmpty()) {
            Set<T> elements = new HashSet<T>(set);
            set.clear();
            setChanged();
            notifyObservers(new RemoveUpdate<T>(elements));
        }
    }

    /**
     * Delegates the method to the underlying set.
     */
    public boolean contains(Object o) {
        return set.contains(o);
    }

    /**
     * Delegates the method to the underlying set.
     */
    public boolean containsAll(Collection< ? > c) {
        return set.containsAll(c);
    }

    /**
     * Delegates the method to the underlying set.
     */
    @Override
    public boolean equals(Object o) {
        return set.equals(o);
    }

    /**
     * Delegates the method to the underlying set.
     */
    @Override
    public int hashCode() {
        return set.hashCode();
    }

    /**
     * Delegates the method to the underlying set.
     */
    public boolean isEmpty() {
        return set.isEmpty();
    }

    /**
     * Returns an iterator that delegates to an iterator over the underlying set,
     * in addition notifying the observers if <code>remove</code> is called in the iterator.
     */
    public Iterator<T> iterator() {
        final Iterator<T> iter = set.iterator();
        return new Iterator<T>() {
            public boolean hasNext() {
                return iter.hasNext();
            }

            public T next() {
                last = iter.next();
                return last;
            }

            public void remove() {
                iter.remove();
                setChanged();
                notifyObservers(new RemoveUpdate<T>(last));
            }
            
            /** The last element returned by #next(). */
            private T last;
        };
    }

    /**
     * Delegates the method to the underlying set.
     */
    public boolean remove(Object o) {
        if (set.remove(o)) {
            setChanged();
            notifyObservers(new RemoveUpdate<T>((T) o));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Delegates the method to the underlying set.
     */
    public boolean removeAll(Collection< ? > c) {
        Set<T> removedElements = new HashSet<T>();
        boolean result = false;
        for (Object element: c) {
            if (set.remove(element)) {
                removedElements.add((T) element);
                result = true;
            }
        }
        if (result) {
            setChanged();
            notifyObservers(new RemoveUpdate<T>(removedElements));
        }
        return result;

    }

    /**
     * Delegates the method to the underlying set.
     */
    public boolean retainAll(Collection< ? > c) {
        boolean result = false;
        Set<T> removedSet = new HashSet<T>();
        Iterator<T> iter = set.iterator();
        while (iter.hasNext()) {
            T element = iter.next();
            if (!c.contains(element)) {
                iter.remove();
                removedSet.add(element);
                result = true;
            }
        }
        if (result) {
            setChanged();
            notifyObservers(new RemoveUpdate<T>(removedSet));
        }
        return result;
    }

    /**
     * Delegates the method to the underlying set.
     */
    public int size() {
        return set.size();
    }

    /**
     * Delegates the method to the underlying set.
     */
    public Object[] toArray() {
        return set.toArray();
    }

    /**
     * Delegates the method to the underlying set.
     */
    public <U> U[] toArray(U[] a) {
        return set.toArray(a);
    }
    
    /** The underlying set. */
    private final Set<T> set;

    /** Class wrapping an update that has added one or more elements. */
    static public class AddUpdate<T> {
        /** Constructs an instance for a given set of added elements. */
        private AddUpdate(Set<T> addedSet) {
            this.addedSet = Collections.unmodifiableSet(addedSet);
        }
        
        /** Constructs an instance for a given singleton element. */
        private AddUpdate(T element) {
            this.addedSet = Collections.singleton(element);
        }
        
        /** Returns the set of added elements. */
        public Set<T> getAddedSet() {
            return addedSet;
        }
        
        /** The set of added elements. */
        private final Set<T> addedSet;
    }
    
    /** Class wrapping an update that has removed one or more elements. */
    static public class RemoveUpdate<T> {
        /** Constructs an instance for a given set of removed elements. */
        private RemoveUpdate(Set<T> removedSet) {
            this.removedSet = Collections.unmodifiableSet(removedSet);
        }
        
        /** Constructs an instance for a given singleton element. */
        private RemoveUpdate(T element) {
            this.removedSet = Collections.singleton(element);
        }
        
        /** Returns the set of removed elements. */
        public Set<T> getRemovedSet() {
            return removedSet;
        }
        
        /** The set of added elements. */
        private final Set<T> removedSet;
    }
}

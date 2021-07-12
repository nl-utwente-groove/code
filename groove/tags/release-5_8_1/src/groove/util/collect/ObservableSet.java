/**
 *
 */
package groove.util.collect;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Provides a view upon a given set that sends notifications of additions and
 * removals.
 * @author Arend Rensink
 * @version $Revision$
 */
public class ObservableSet<T> extends Observable implements Set<T> {
    /**
     * Creates a new observable set on top of a given set. The set will be
     * aliased.
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
     * Delegates the method to the underlying set, then notifies the observers
     * with an AddUpdate.
     */
    @Override
    public boolean add(T o) {
        if (this.set.add(o)) {
            setChanged();
            notifyObservers(new AddUpdate<>(o));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Adds the elements to the underlying set, then notifies the observers for
     * those elements actually added.
     */
    @Override
    public boolean addAll(Collection<? extends T> c) {
        Set<T> addedElements = new HashSet<>();
        boolean result = false;
        for (T element : c) {
            if (this.set.add(element)) {
                addedElements.add(element);
                result = true;
            }
        }
        if (result) {
            setChanged();
            notifyObservers(new AddUpdate<>(addedElements));
        }
        return result;
    }

    /**
     * Delegates the method to the underlying set, then notifies the observers
     * with a {@link RemoveUpdate}.
     */
    @Override
    public void clear() {
        if (!this.set.isEmpty()) {
            Set<T> elements = new HashSet<>(this.set);
            this.set.clear();
            setChanged();
            notifyObservers(new RemoveUpdate<>(elements));
        }
    }

    /**
     * Delegates the method to the underlying set.
     */
    @Override
    public boolean contains(Object o) {
        return this.set.contains(o);
    }

    /**
     * Delegates the method to the underlying set.
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        return this.set.containsAll(c);
    }

    /**
     * Delegates the method to the underlying set.
     */
    @Override
    public boolean equals(Object o) {
        return this.set.equals(o);
    }

    /**
     * Delegates the method to the underlying set.
     */
    @Override
    public int hashCode() {
        return this.set.hashCode();
    }

    /**
     * Delegates the method to the underlying set.
     */
    @Override
    public boolean isEmpty() {
        return this.set.isEmpty();
    }

    /**
     * Returns an iterator that delegates to an iterator over the underlying
     * set, in addition notifying the observers if <code>remove</code> is
     * called in the iterator.
     */
    @Override
    public Iterator<T> iterator() {
        final Iterator<T> iter = this.set.iterator();
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public T next() {
                this.last = iter.next();
                return this.last;
            }

            @Override
            public void remove() {
                iter.remove();
                setChanged();
                notifyObservers(new RemoveUpdate<>(this.last));
            }

            /** The last element returned by #next(). */
            private @Nullable T last;
        };
    }

    /**
     * Delegates the method to the underlying set.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(Object o) {
        if (this.set.remove(o)) {
            setChanged();
            notifyObservers(new RemoveUpdate<>((T) o));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Delegates the method to the underlying set.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean removeAll(Collection<?> c) {
        Set<T> removedElements = new HashSet<>();
        boolean result = false;
        for (Object element : c) {
            if (this.set.remove(element)) {
                removedElements.add((T) element);
                result = true;
            }
        }
        if (result) {
            setChanged();
            notifyObservers(new RemoveUpdate<>(removedElements));
        }
        return result;

    }

    /**
     * Delegates the method to the underlying set.
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        boolean result = false;
        Set<T> removedSet = new HashSet<>();
        Iterator<T> iter = this.set.iterator();
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
            notifyObservers(new RemoveUpdate<>(removedSet));
        }
        return result;
    }

    /**
     * Delegates the method to the underlying set.
     */
    @Override
    public int size() {
        return this.set.size();
    }

    /**
     * Delegates the method to the underlying set.
     */
    @Override
    public Object[] toArray() {
        return this.set.toArray();
    }

    /**
     * Delegates the method to the underlying set.
     */
    @Override
    public <U> U[] toArray(U[] a) {
        return this.set.toArray(a);
    }

    /**
     * Just calls the super method. This is there for the sake of visibility
     * from the inner Iterator class.
     */
    @Override
    final protected synchronized void setChanged() {
        super.setChanged();
    }

    /** The underlying set. */
    private final Set<T> set;

    /** Class wrapping an update that has added one or more elements. */
    static public class AddUpdate<T> {
        /** Constructs an instance for a given set of added elements. */
        AddUpdate(Set<T> addedSet) {
            this.addedSet = Collections.unmodifiableSet(addedSet);
        }

        /** Constructs an instance for a given singleton element. */
        AddUpdate(T element) {
            this.addedSet = Collections.singleton(element);
        }

        /** Returns the set of added elements. */
        public Set<T> getAddedSet() {
            return this.addedSet;
        }

        /** The set of added elements. */
        private final Set<T> addedSet;
    }

    /** Class wrapping an update that has removed one or more elements. */
    static public class RemoveUpdate<T> {
        /** Constructs an instance for a given set of removed elements. */
        RemoveUpdate(Set<T> removedSet) {
            this.removedSet = Collections.unmodifiableSet(removedSet);
        }

        /** Constructs an instance for a given singleton element. */
        RemoveUpdate(T element) {
            this.removedSet = Collections.singleton(element);
        }

        /** Returns the set of removed elements. */
        public Set<T> getRemovedSet() {
            return this.removedSet;
        }

        /** The set of added elements. */
        private final Set<T> removedSet;
    }
}

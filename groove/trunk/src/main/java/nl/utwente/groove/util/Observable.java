/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package nl.utwente.groove.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Replacement for the deprecated Java </<code>Observable</code> class.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Observable {
    /**
     * Add a PropertyChangeListener to the observers.
     * The listener is registered for all properties.
     * The same listener object may be added more than once, and will be called
     * as many times as it is added.
     * If {@code listener} is null, no exception is thrown and no action
     * is taken.
     * @param  listener an observer to be added.
     * @throws NullPointerException   if the parameter listener is null.
     */
    public synchronized void addObserver(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    /**
     * Remove a PropertyChangeListener from the observers.
     * This removes a PropertyChangeListener that was registered
     * for all properties.
     * If {@code listener} was added more than once to the same event
     * source, it will be notified one less time after being removed.
     * If {@code listener} is null, or was never added, no exception is
     * thrown and no action is taken.
     *
     * @param listener  The PropertyChangeListener to be removed
     */
    public synchronized void deleteObserver(PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }

    /** Fires a property change of {@link #PROPERTY} to all listeners,
     * with old and new property value {@code null}.
     */
    public void notifyObservers() {
        notifyObservers(null);
    }

    /** Fires a property change of {@link #PROPERTY} to all listeners,
     * with old property value {@code null} and new value {@code arg}.
     */
    public void notifyObservers(Object arg) {
        this.support.firePropertyChange(PROPERTY, null, arg);
    }

    /** The property change support object actually implementing the functionality. */
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    /** The single property that this observable reports on. */
    public static final String PROPERTY = "property";
}

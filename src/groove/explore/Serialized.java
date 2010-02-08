/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id$
 */
package groove.explore;

import groove.gui.Simulator;
import groove.gui.StatusPanel;

/**
 * Class for serialized objects (which may depend on serialized arguments).
 * This class will be used to serialize strategies and acceptors.
 *
 * @author Maarten de Mol
 * @version $Revision $
 * 
 */
public class Serialized<A> {

    // Serialized representation of the object. May not change.
    private final String object;
    // Fixed value of the object (only used when nrArguments = 0).
    private final A value;
    // Materialize function (only used when nrArguments > 0).
    private final Materialize<A> materialize;
    // Array that holds the serialized arguments.
    private SerializedArgument[] arguments;

    /**
     * Create a serialized object that has no additional arguments.
     * Such an object is constant, and has a fixed value as well.
     * @param object - serialized representation of the object
     * @param value - value of the object
     */
    public Serialized(String object, A value) {
        this.object = object;
        this.value = value;
        this.materialize = null;
        this.arguments = new SerializedArgument[0];
    }

    /**
     * Create a serialized object with one additional argument.
     * @param object - serialized representation of the object
     * @param arg1 - serialized argument 1
     */
    public Serialized(String object, SerializedArgument arg1,
            Materialize<A> materialize) {
        this.object = object;
        this.value = null;
        this.materialize = materialize;
        this.arguments = new SerializedArgument[1];
        this.arguments[0] = arg1;
    }

    /**
     * Create a serialized object with two additional argument.
     * @param object - serialized representation of the object
     * @param arg1 - serialized argument 1
     * @param arg2 - serialized argument 2
     */
    public Serialized(String object, SerializedArgument arg1,
            SerializedArgument arg2, Materialize<A> materialize) {
        this.object = object;
        this.value = null;
        this.materialize = materialize;
        this.arguments = new SerializedArgument[2];
        this.arguments[0] = arg1;
        this.arguments[1] = arg2;
    }

    /**
     * Get a serialized representation of the object and its arguments.
     * Default syntax: Object(Arg1, Arg2, ..., Argn).
     * @return - serialized representation of the object and its arguments
     */
    public String getIdentifier() {
        if (this.arguments.length == 0) {
            return this.object;
        } else {
            StringBuffer buffer = new StringBuffer();
            buffer.append(this.object);
            buffer.append("(");
            for (int i = 0; i < this.arguments.length; i++) {
                buffer.append(this.arguments[i].getSerializedValue());
                if (i < this.arguments.length - 1) {
                    buffer.append(",");
                }
            }
            buffer.append(")");
            return buffer.toString();
        }
    }

    /**
     * Get a serialized representation of the object only.
     * @return - serialized representation of the object only
     */
    public String getObjectOnly() {
        return this.object;
    }

    /**
     * Get the number of parameters that the serialized object has.
     * @return - length of the parameters array
     */
    public int getNrArguments() {
        return this.arguments.length;
    }

    /**
     * Materialize function. Either produces the fixed value (if there are no
     * arguments), or relays to the stored materialize function. 
     */
    public A materialize(Simulator simulator) {
        if (this.arguments.length == 0) {
            return this.value;
        } else {
            Object[] argValues = new Object[this.arguments.length];
            for (int i = 0; i < this.arguments.length; i++) {
                argValues[i] = this.arguments[i].getValue(simulator);
                if (argValues[i] == null) {
                    return null;
                }
            }
            return this.materialize.materialize(argValues);
        }
    }

    /**
     * Get the selector panel of one of the arguments.
     */
    public StatusPanel createSelectorPanel(int index, Simulator simulator) {
        return this.arguments[index].createSelectorPanel(simulator);
    }

    /**
     * Interface that all serialized arguments must implement.
     */
    public interface SerializedArgument {
        /**
         * Get the serialized value.
         */
        public String getSerializedValue();

        /**
         * Set the serialized value.
         */
        public void setSerializedValue(String value);

        /**
         * Get the real value, which is calculated dependent of the simulator.
         * Must return null if the serialized value cannot be bound.
         */
        public Object getValue(Simulator simulator);

        /**
         * Create a StatusPanel in which the user can select values.
         * The available values can be calculated using the simulator.
         * The status variable in the panel indicates whether the range of
         * allowed values is empty.
         */
        public StatusPanel createSelectorPanel(Simulator simulator);
    }

    /**
     * Interface for the materialize function.
     */
    public interface Materialize<B> {
        /**
         * The materialize function. Takes the values of all the arguments of
         * the serialized object, and produces the real value.
         * The arguments are passed as (already converted) Objects.
         * The arguments are never null. 
         */
        public B materialize(Object[] arguments);
    }
}

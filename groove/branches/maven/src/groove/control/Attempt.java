/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.control;

import java.util.ArrayList;

/**
 * List of calls to be attempted,
 * to be tried successively in the given order.
 * <P> the position type for which this is an attempt
 * <A> the type of call to be attempted
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class Attempt<P extends Position<P,A>,A extends Attempt.Stage<P,A>>
    extends ArrayList<A> {
    /** Constructs an initially empty attempt of default size. */
    protected Attempt() {
        super();
    }

    /** Constructs an initially empty attempt with a given size. */
    protected Attempt(int size) {
        super(size);
    }

    /** Sets the success alternate. */
    final public void setSuccess(P onSuccess) {
        this.onSuccess = onSuccess;
    }

    /** Next alternative position in case this attempt succeeds. */
    final public P onSuccess() {
        return this.onSuccess;
    }

    private P onSuccess;

    /** Sets the failure alternate. */
    final public void setFailure(P onFailure) {
        this.onFailure = onFailure;
    }

    /** Next alternative position in case this attempt fails. */
    final public P onFailure() {
        return this.onFailure;
    }

    private P onFailure;

    /** Indicates that the success and failure alternates are identical. */
    public boolean sameVerdict() {
        return onFailure() == onSuccess();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + this.onFailure.hashCode();
        result = prime * result + this.onSuccess.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return super.toString() + "/" + onSuccess() + "/" + onFailure();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof Attempt)) {
            return false;
        }
        Attempt<?,?> other = (Attempt<?,?>) obj;
        if (!this.onFailure.equals(other.onFailure)) {
            return false;
        }
        if (!this.onSuccess.equals(other.onSuccess)) {
            return false;
        }
        return true;
    }

    /**
     * Element of a {@link Attempt}.
     * @author Arend Rensink
     * @version $Revision $
     */
    public interface Stage<P extends Position<P,A>,A extends Stage<P,A>> {
        /**
         * The rule called in this stage.
         * This is the top element of the call stack.
         * @see #getCallStack()
         */
        Call getRuleCall();

        /** Returns the stack of calls of this stage. */
        CallStack getCallStack();

        /** The target position. */
        P onFinish();

        /** Returns the transient depth entered by this stage. */
        int getTransience();
    }
}

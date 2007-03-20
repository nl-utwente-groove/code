// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/*
 * $Id: InjectiveSimulation.java,v 1.1.1.2 2007-03-20 10:42:42 kastenberg Exp $
 */
package groove.graph;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements a simulation geared towards producing an isomorphism.
 * This means that injectivity and surjectivity constraints are brought
 * into play in the construction and stabilization of the simulation..
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public class InjectiveSimulation extends DefaultSimulation {
    static private final IllegalStateException nonInjective = new IllegalStateException("Injectivity violation");

    public InjectiveSimulation(InjectiveMorphism morph) {
        super(morph);
    }

    /**
     * Clones the used images, in addition to calling the <tt>super</tt> method.
     */
    public InjectiveSimulation clone() {
        InjectiveSimulation result = (InjectiveSimulation) super.clone();
        if (usedImages != null) {
            result.usedImages = new HashSet<Element>(usedImages);
        }
        return result;
    }

    /**
     * Initializes the used images and then invokes the <tt>super</tt> method.
     */
    protected void initSimulation() {
        usedImages = new HashSet<Element>();
        super.initSimulation();
    }

    /**
     * Clears the backup images, in addition to invoking the <tt>super</tt> method. 
     */
    protected void backup() {
        backupUsedImages = new HashSet<Element>();
        super.backup();
    }

    /**
     * Also restores the used images, in addition to calling the <tt>super</tt> method.
     */
    protected void restore() {
        usedImages.removeAll(backupUsedImages);
        super.restore();
    }

    /**
     * This implementation adds a given image to the used images, while testing if the image was
     * already there. If it was already there, this means injectivity is violated
     * and hence an {@link IllegalStateException} is thrown.
     */
    protected void notifySingular(ImageSet<?> changed) {
        if (!usedImages.add(changed.getSingular())) {
            throw nonInjective;
        } else if (backupUsedImages != null) {
            backupUsedImages.add(changed.getSingular());
        }
        super.notifySingular(changed);
    }

    /** The set os images used as singular image. */
    private Set<Element> usedImages;
    /** The backup set for {@link #usedImages}, used in {@link #backup()} and {@link #restore()}. */
    private Set<Element> backupUsedImages;
}
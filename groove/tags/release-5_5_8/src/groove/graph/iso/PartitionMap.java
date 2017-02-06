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
package groove.graph.iso;

import groove.graph.Element;
import groove.graph.iso.CertificateStrategy.ElementCertificate;
import groove.util.collect.SmallCollection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapping from certificate values to sets of graph elements having those
 * certificates. For efficiency, images are stored as {@link SmallCollection}s
 * @author Arend Rensink
 * @version $Revision$
 */
public class PartitionMap<E extends Element> {
    /** Adds a pair of certificate and graph element to the partition map. */
    public void add(ElementCertificate<? extends E> certificate) {
        E elem = certificate.getElement();
        // retrieve the image of the certificate, if any
        SmallCollection<E> oldPartition = this.partitionMap.get(certificate);
        if (oldPartition == null) {
            // no, the certificate did not yet exist; create an entry for it
            this.partitionMap.put(certificate, new SmallCollection<>(elem));
        } else {
            oldPartition.add(elem);
            this.oneToOne = false;
        }
    }

    /** Indicates if the partition map has only singleton partitions as values. */
    public boolean isOneToOne() {
        return this.oneToOne;
    }

    /**
     * Retrieves the partition for a given certificate value. The partition can
     * be a single {@link Element} or a {@link Collection} of elements.
     * @param certificate the value for which we want the partition.
     * @return an object of type {@link Element} or type {@link Collection}, or
     *         <code>null</code>
     */
    public SmallCollection<E> get(ElementCertificate<E> certificate) {
        return this.partitionMap.get(certificate);
    }

    /** Number of certificates in the map. */
    public int size() {
        return this.partitionMap.size();
    }

    /**
     * Returns the string description of the internal partition map.
     */
    @Override
    public String toString() {
        return this.partitionMap.toString();
    }

    /** The actual mapping. */
    private final Map<ElementCertificate<? extends E>,SmallCollection<E>> partitionMap =
        new HashMap<>();
    /** Flag indicating if the partition map contains non-singleton images. */
    private boolean oneToOne = true;
}

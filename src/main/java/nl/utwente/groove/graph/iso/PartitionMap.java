/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.graph.iso;

import java.util.HashMap;
import java.util.Map;

import nl.utwente.groove.graph.iso.CertificateStrategy.ElementCertificate;
import nl.utwente.groove.util.collect.SmallCollection;

/**
 * Mapping from certificate values to the certificates having those values.
 * Certificates are the units at which the graph is partitioned: for nodes
 * there is one per node, but for edges there is one per bundle of parallel
 * copies, so a certificate may stand for more than one graph element.
 * For efficiency, images are stored as {@link SmallCollection}s.
 * @author Arend Rensink
 * @version $Revision$
 */
public class PartitionMap<C extends ElementCertificate<?>> {
    /** Adds a certificate to the partition map. */
    public void add(C certificate) {
        // retrieve the image of the certificate value, if any
        SmallCollection<C> oldPartition = this.partitionMap.get(certificate);
        if (oldPartition == null) {
            // no, the certificate value did not yet exist; create an entry for it
            this.partitionMap.put(certificate, new SmallCollection<>(certificate));
        } else {
            oldPartition.add(certificate);
            this.oneToOne = false;
        }
    }

    /** Indicates if the partition map has only singleton partitions as values. */
    public boolean isOneToOne() {
        return this.oneToOne;
    }

    /**
     * Retrieves the partition for a given certificate value.
     * @param certificate the value for which we want the partition; need not
     *        be a certificate of the underlying graph, only equal to one
     * @return the certificates equal to <code>certificate</code>, or
     *         <code>null</code> if there are none
     */
    public SmallCollection<C> get(C certificate) {
        return this.partitionMap.get(certificate);
    }

    /** Number of distinct certificate values in the map. */
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
    private final Map<C,SmallCollection<C>> partitionMap = new HashMap<>();
    /** Flag indicating if the partition map contains non-singleton images. */
    private boolean oneToOne = true;
}

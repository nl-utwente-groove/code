/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
 * $Id: StateReference.java,v 1.2 2008-01-30 09:32:18 iovka Exp $
 */
package groove.lts;

import groove.trans.SystemRecord;
import groove.util.CacheHolder;
import groove.util.CacheReference;

/**
 * Cache reference for state caches, which carry the system record as static information.
 * @author Arend Rensink
 * @version $Revision $
 */
public class StateReference extends CacheReference<StateCache> {
	/**
	 * Copies the system record from the template.
	 */
	protected StateReference(CacheHolder<StateCache> holder, StateCache referent, StateReference template) {
		super(holder, referent, template);
		record = template.record;
	}

	/**
	 * Creates a reference with an explicitly given (non-<code>null</code>) system record.
	 */
	protected StateReference(boolean strong, int incarnation, StateReference template, SystemRecord record) {
		super(strong, incarnation, template);
		this.record = record;
	}

	/** Returns the system record associated with this reference. */
	public SystemRecord getRecord() {
		return record;
	}
	
	@Override
	protected CacheReference<StateCache> createNullInstance(boolean strong, int incarnation) {
		return new StateReference(strong, incarnation, this, record);
	}

	@Override
	public CacheReference<StateCache> newReference(CacheHolder<StateCache> holder, StateCache cache) {
		return new StateReference(holder, cache, this);
	}

	/** The system record associated with this reference. */
	private final SystemRecord record;
	
	/**
	 * Factory method for an uninitialised strong reference, i.e., with referent <code>null</code>.
	 * This is a convenience method for {@link #newInstance(boolean)} with parameter <code>true</code>.
	 */
	static public StateReference newInstance(SystemRecord record) {
		return newInstance(record, true);
	}

	/**
	 * Factory method for an uninitialised reference, i.e., with referent <code>null</code>.
	 * @param strong if <code>true</code> the reference instance is to be strong
	 * @return a reference that is either strong or soft, depending on <code>strong</code>
	 */
	static public StateReference newInstance(SystemRecord record, boolean strong) {
		return new StateReference(strong, 0, null, record);
	}
}

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
package nl.utwente.groove.test;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.ClassRule;
import org.junit.rules.ExternalResource;

import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Randomness;

/**
 * Class rule that snapshots the {@link Randomness} master-seed state before a
 * test class runs and restores it afterwards, so that a class setting its own
 * seed does not leak it into test classes running later. Usage:
 * <pre>
 * &#64;ClassRule
 * public static final MasterSeedGuard SEED_GUARD = new MasterSeedGuard();
 * </pre>
 * @see ClassRule
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5, 2026-08")
public class MasterSeedGuard extends ExternalResource {
    @Override
    protected void before() {
        this.saved = Randomness.peekMasterSeed();
    }

    @Override
    protected void after() {
        Randomness.restoreMasterSeed(this.saved);
    }

    /** The master-seed state at the start of the test class. */
    private @Nullable Long saved;
}

/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2026
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
package nl.utwente.groove.test;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Log;

/**
 * Extension that switches GROOVE's diagnostic logging off while a test class runs,
 * and restores it afterwards. Usage:
 * <pre>
 * &#64;RegisterExtension
 * static final QuietLogging QUIET_LOGGING = new QuietLogging();
 * </pre>
 * This is for test classes that provoke diagnostics on purpose, such as the ones
 * installing a stale extension jar or a locked one: their messages are logged at
 * {@code INFO} or {@code WARNING}, and the default {@code java.util.logging}
 * configuration prints those to the console, where they look like errors of the run.
 * <p>
 * The registration is by field rather than by {@code @ExtendWith}, which instantiates
 * the extension reflectively: this package is not exported, and Surefire opens only the
 * packages of the test classes it actually runs, so a filtered run of a test class in
 * another package (such as {@code -Dtest=*GuiTest}) would fail to construct it.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Opus 5, 2026-09")
@NonNullByDefault
public class QuietLogging implements BeforeAllCallback, AfterAllCallback {
    /** Switches the GROOVE root logger off, remembering its level. */
    // the callback overrides carry no default annotation:
    // the inherited methods' parameters are unconstrained
    @Override
    @NonNullByDefault({})
    public void beforeAll(ExtensionContext context) {
        Logger logger = Logger.getLogger(Log.ROOT);
        this.logger = logger;
        this.saved = logger.getLevel();
        logger.setLevel(Level.OFF);
    }

    /** Restores the level of the GROOVE root logger. */
    @Override
    @NonNullByDefault({})
    public void afterAll(ExtensionContext context) {
        Logger logger = this.logger;
        if (logger != null) {
            logger.setLevel(this.saved);
            this.logger = null;
        }
    }

    /**
     * The GROOVE root logger while it is switched off. The {@code LogManager} holds
     * loggers weakly, so a logger whose level is set must be pinned: were it collected,
     * it would be recreated without the level and start printing again.
     */
    private @Nullable Logger logger;
    /** The level of the GROOVE root logger before it was switched off. */
    private @Nullable Level saved;
}

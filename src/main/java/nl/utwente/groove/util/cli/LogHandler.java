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
package nl.utwente.groove.util.cli;

import java.lang.System.Logger.Level;

import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.TypeConversionException;

import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.Log;

/**
 * Option handler that parses a diagnostic log setting of the form
 * {@code level[:subsystem]} (see gh #891).
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
public class LogHandler implements ITypeConverter<Log.Setting> {
    @Override
    public Log.Setting convert(String value) {
        String levelPart = value;
        String subsystem = "";
        int ix = value.indexOf(':');
        if (ix >= 0) {
            levelPart = value.substring(0, ix);
            subsystem = value.substring(ix + 1);
        }
        Level level;
        try {
            level = Level.valueOf(levelPart.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new TypeConversionException(String
                .format("\"%s\" is not a valid log level; use one of trace, debug, info, warning, error",
                        levelPart));
        }
        return new Log.Setting(level, subsystem);
    }

    /** Name of the log option. */
    public final static String NAME = "-log";
    /** Meta-variable of the log option. */
    public final static String VAR = "level[:subsystem]";
    /** Usage message for the log option. */
    public final static String USAGE = "Print diagnostic messages from the given level upwards "
        + "(trace, debug, info, warning, error) to stderr, "
        + "optionally restricted to a single subsystem (e.g., explore.timing). "
        + "May be repeated for different subsystems.";
}

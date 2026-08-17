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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.System.Logger;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Bootstrap for runtime-switchable diagnostic logging (see gh #891).
 * <p>
 * Diagnostic messages are emitted through {@link Logger System.Logger} (JEP 264),
 * which is zero-dependency and can be redirected by an embedding application
 * via a {@code LoggerFinder}. In the default JDK backend the loggers are backed
 * by {@code java.util.logging}; {@link #set} configures that backend to print
 * messages of a given level to the standard error output, in a terse
 * single-line format. Without such a call (or external configuration),
 * diagnostic messages are dropped at the cost of a single level check.
 * <p>
 * Loggers are organised in dot-separated subsystem names below the common
 * namespace {@link #ROOT}, e.g., {@code explore.timing}, so that diagnostics
 * can be enabled per subsystem.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public final class Log {
    private Log() {
        // no instances
    }

    /**
     * Returns the diagnostic logger for a given GROOVE subsystem.
     * @param subsystem dot-separated subsystem name, relative to {@link #ROOT};
     * if empty, the root GROOVE logger itself is returned
     */
    static public Logger getLogger(String subsystem) {
        return System.getLogger(toLoggerName(subsystem));
    }

    /**
     * Enables diagnostic output for a given GROOVE subsystem, at a given level.
     * Messages are printed to the standard error output. Passing the empty
     * subsystem name enables output for all GROOVE loggers at once (except
     * those with a more specific level of their own).
     * <p>
     * This configures the {@code java.util.logging} backend, so it only has an
     * effect under the JDK's default {@code LoggerFinder}.
     * @param subsystem dot-separated subsystem name, relative to {@link #ROOT}
     * @param level level from which messages should be printed
     */
    static public synchronized void set(String subsystem, Logger.Level level) {
        installHandler();
        var logger = java.util.logging.Logger.getLogger(toLoggerName(subsystem));
        logger.setLevel(toJul(level));
        // the LogManager holds loggers weakly; pin the configured ones
        configured.add(logger);
    }

    /** Converts a subsystem name to a fully qualified logger name. */
    static private String toLoggerName(String subsystem) {
        return subsystem.isEmpty()
            ? ROOT
            : ROOT + "." + subsystem;
    }

    /** Installs the terse console handler on the root GROOVE logger, once. */
    static private void installHandler() {
        if (handlerInstalled) {
            return;
        }
        var root = java.util.logging.Logger.getLogger(ROOT);
        var handler = new ConsoleHandler();
        handler.setLevel(java.util.logging.Level.ALL);
        handler.setFormatter(new TerseFormatter());
        root.addHandler(handler);
        // do not also pass records to the JVM's default console handler
        root.setUseParentHandlers(false);
        configured.add(root);
        handlerInstalled = true;
    }

    /** Flag set once {@link #installHandler()} has done its work. */
    static private boolean handlerInstalled;

    /** Strong references to configured backend loggers, to protect their
     * settings from garbage collection. */
    static private final List<java.util.logging.Logger> configured = new ArrayList<>();

    /** Converts a {@link Logger System.Logger} level to its backend equivalent. */
    static private java.util.logging.Level toJul(Logger.Level level) {
        return switch (level) {
        case ALL -> java.util.logging.Level.ALL;
        case TRACE -> java.util.logging.Level.FINER;
        case DEBUG -> java.util.logging.Level.FINE;
        case INFO -> java.util.logging.Level.INFO;
        case WARNING -> java.util.logging.Level.WARNING;
        case ERROR -> java.util.logging.Level.SEVERE;
        case OFF -> java.util.logging.Level.OFF;
        };
    }

    /** Display name of a backend level, in {@link Logger System.Logger} terms
     * where an equivalent exists. */
    static private String toLevelName(java.util.logging.Level level) {
        if (level == java.util.logging.Level.FINER) {
            return "TRACE";
        } else if (level == java.util.logging.Level.FINE) {
            return "DEBUG";
        } else if (level == java.util.logging.Level.SEVERE) {
            return "ERROR";
        } else {
            return level.getName();
        }
    }

    /** Common namespace prefix of all GROOVE loggers. */
    static public final String ROOT = "nl.utwente.groove";

    /**
     * Level setting for a single subsystem, as parsed from the command line.
     * @param level level from which messages should be printed
     * @param subsystem dot-separated subsystem name, relative to {@link #ROOT};
     * empty for all GROOVE loggers
     */
    public record Setting(Logger.Level level, String subsystem) {
        /** Puts this setting into effect (see {@link Log#set}). */
        public void apply() {
            set(subsystem(), level());
        }
    }

    /** Single-line formatter: {@code HH:mm:ss.SSS LEVEL [subsystem] message}. */
    static private class TerseFormatter extends Formatter {
        // no default annotation: the inherited method's parameter is unconstrained
        @Override
        @NonNullByDefault({})
        public String format(LogRecord record) {
            var time = LocalTime.ofInstant(record.getInstant(), ZoneId.systemDefault());
            var name = record.getLoggerName();
            if (name == null) {
                name = "";
            } else if (name.startsWith(ROOT + ".")) {
                name = name.substring(ROOT.length() + 1);
            }
            var result = new StringBuilder();
            result
                .append("%s %s [%s] %s%n"
                    .formatted(TIME_FORMAT.format(time), toLevelName(record.getLevel()), name,
                               formatMessage(record)));
            var thrown = record.getThrown();
            if (thrown != null) {
                var trace = new StringWriter();
                thrown.printStackTrace(new PrintWriter(trace));
                result.append(trace);
            }
            return result.toString();
        }

        /** Formatter for the time stamp of diagnostic messages. */
        static private final DateTimeFormatter TIME_FORMAT
            = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    }
}

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
package nl.utwente.groove.transform.oracle;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Constant;
import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.grammar.UnitPar.RulePar;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.transform.RuleEvent;
import nl.utwente.groove.util.parse.FormatException;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Value oracle that runs a process and parses the output value.
 * When the getValue() method is invoked for the first time, a
 * process specified by the property string is started. No shell
 * is used, but the command string is broken up into an argument
 * list by whitespaces. Double quotes remain preserved. For every
 * getValue() invocation, the process will receive a line on stdin
 * and should produce a (newline-terminated) value on stdout which
 * the oracle will use. Before the process is started, the user is
 * asked for permission through a dialog for security purposes.
 * @author Mark Boom
 * @version $Revision$
 */
@NonNullByDefault
public class ProcessOracle implements ValueOracle {

    private final String command;
    @Nullable
    private Process process;
    @Nullable
    private BufferedWriter stdin;
    @Nullable
    private BufferedReader stdout;

    public ProcessOracle(String command) {
        this.command = command;
    }

    private void startProcess() throws FormatException {
        ensurePermission(command);

        ProcessBuilder builder = new ProcessBuilder(parseCommand(command));
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);

        try {
            this.process = builder.start();
        } catch (IOException e) {
            throw new FormatException(String.format("Unable to start oracle process: %s", e));
        }

        this.stdin = new BufferedWriter(new OutputStreamWriter(
                process.getOutputStream(), StandardCharsets.UTF_8)
        );
        this.stdout = new BufferedReader(new InputStreamReader(
                process.getInputStream(), StandardCharsets.UTF_8)
        );
    }

    private List<String> parseCommand(String command) {
        List<String> args = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        boolean started = false;

        for (int i = 0; i < command.length(); i++) {
            char c = command.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
                started = true;
            } else if (Character.isWhitespace(c) && !inQuotes) {
                if (started) {
                    args.add(current.toString());
                    current.setLength(0);
                    started = false;
                }
            } else {
                current.append(c);
                started = true;
            }
        }

        if (inQuotes) {
            throw new IllegalArgumentException("Unclosed double quote");
        }

        if (started) {
            args.add(current.toString());
        }

        return args;
    }

    public static void ensurePermission(String command) throws FormatException {
        int answer = JOptionPane
                .showConfirmDialog(null,
                    String.format(
                        "This system wants to run a process to use as a value oracle.\nIf you proceed, the following command will be executed on your computer:\n\n%s\n\nDo you want to continue?",
                        command
                    ),
                    "Warning", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) {
            throw new FormatException("User refused to start oracle");
        }
    }

    /** Send one newline-terminated message to the process. */
    public synchronized void sendLine(String message) throws IOException {
        assert stdin != null;
        stdin.write(message);
        stdin.newLine();
        stdin.flush();
    }

    /** Read one line from stdout; returns null at EOF. */
    public String readLine() throws IOException {
        assert stdout != null;
        return stdout.readLine();
    }

    @Override
    public Constant getValue(HostGraph host, RuleEvent event, RulePar par) throws FormatException {
        if (this.process == null) {
            this.startProcess();
        }

        Sort sort = par.getType().getSort();
        assert sort != null;

        String response;
        try {
            sendLine("getValue");
            response = readLine();
        } catch (IOException e) {
            throw new FormatException("Unable to retrieve value from process oracle");
        }

        return sort.createConstant(response);
    }

    @Override
    public void close() {
        try {
            if (stdin != null) {
                stdin.close();
            }
        } catch (IOException ignored) {};
        try {
            if (stdout != null) {
                stdout.close();
            }
        } catch (IOException ignored) {};
        if (process != null) {
            process.destroyForcibly();
        }
    }

    @Override
    public ValueOracleKind getKind() {
        return ValueOracleKind.PROCESS;
    }

}

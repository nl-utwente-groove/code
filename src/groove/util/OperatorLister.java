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
package groove.util;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import au.com.bytecode.opencsv.CSVWriter;
import groove.algebra.Operator;
import groove.algebra.Signature.OpValue;
import groove.algebra.Sort;
import groove.io.FileType;
import groove.util.cli.GrooveCmdLineTool;

/**
 * Command-line tool to list the available operators in a format that
 * can then be used as input to the user manual
 * @author Arend Rensink
 * @version $Revision $
 */
public class OperatorLister extends GrooveCmdLineTool<List<String[]>> {
    /**
     * Creates a new instance.
     */
    public OperatorLister(String[] args) {
        super("OperatorLister", args);
    }

    @Override
    protected List<String[]> run() throws Exception {
        List<String[]> result = collectOperators();
        String outFileName = FileType.CSV.addExtension(this.outFileName);
        try (CSVWriter writer = new CSVWriter(new FileWriter(outFileName))) {
            writer.writeAll(result);
            writer.flush();
        }
        return result;
    }

    private List<String[]> collectOperators() {
        List<String[]> result = new ArrayList<>();
        for (Sort sig : Sort.values()) {
            for (OpValue opValue : sig.getOpValues()) {
                Operator op = opValue.getOperator();
                StringBuilder argTypes = new StringBuilder();
                for (Sort argType : op.getParamTypes()) {
                    if (argTypes.length() > 0) {
                        argTypes.append(this.argTypeSeparator);
                    }
                    argTypes.append(argType.getName());
                }
                String[] line = {sig.getName(), op.getName(), op.getSymbol(), op.getDescription(),
                    op.getResultType()
                        .getName(),
                    argTypes.toString()};
                result.add(line);
            }
        }
        return result;
    }

    @Argument(metaVar = "file", usage = "Output file name", required = true)
    private String outFileName;

    @Option(name = "-s", metaVar = "sep", usage = "Argument type separator")
    private String argTypeSeparator = ", ";

    /**
     * Main method to produce the list of operators and exit gracefully on errors.
     */
    static public void main(String[] args) {
        GrooveCmdLineTool.tryExecute(OperatorLister.class, args);
    }

    /** Produces the current list of operators. */
    static public void execute(String[] args) throws Exception {
        new OperatorLister(args).start();
    }
}

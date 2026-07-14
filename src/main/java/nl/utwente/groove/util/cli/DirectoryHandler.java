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

import java.io.File;

import org.eclipse.jdt.annotation.Nullable;

import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.TypeConversionException;

import nl.utwente.groove.io.FileType;

/**
 * Option handler that checks whether a value is an existing directory.
 */
public class DirectoryHandler implements ITypeConverter<File> {
    /** Constructor that allows an optional extension filter to be specified. */
    protected DirectoryHandler(@Nullable FileType filter) {
        this.filter = filter;
    }

    /** Required constructor. */
    public DirectoryHandler() {
        this(null);
    }

    @Override
    public File convert(String value) {
        File result = new File(value);
        if (!result.isDirectory()) {
            // see if we can set an extension
            var filter = this.filter;
            if (filter != null && !filter.hasExtension(result)) {
                result = new File(filter.addExtension(result.getPath()));
            }
            if (!result.isDirectory()) {
                throw new TypeConversionException(String
                    .format("File name \"%s\" must be an existing directory", result));
            }
        }
        return result;
    }

    private final @Nullable FileType filter;
}

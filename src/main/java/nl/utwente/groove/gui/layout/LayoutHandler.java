/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2026 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 *
 * $Id$
 */
package nl.utwente.groove.gui.layout;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.AIGenerated;
import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.TypeConversionException;

/**
 * Option handler for the layouter with which a command-line tool lays out
 * the graphs it shows or renders (gh #941).
 * The names are those of GROOVE's own layouters only, which are there
 * regardless of the graph library behind the display.
 */
@AIGenerated("Claude Fable 5.2, 2026-11")
@NonNullByDefault
public class LayoutHandler implements ITypeConverter<Layouter> {
    @Override
    public Layouter convert(@Nullable String value) {
        var result = LAYOUTERS.get(value);
        if (result == null) {
            throw new TypeConversionException(String
                .format("Unknown layout \"%s\": should be one of %s", value,
                        String.join(", ", LAYOUTERS.keySet())));
        }
        return result;
    }

    /** The prototype layouters, by the name under which they can be selected. */
    private final static Map<String,@Nullable Layouter> LAYOUTERS = new LinkedHashMap<>();
    static {
        LAYOUTERS.put("spring", SpringLayouter.PROTOTYPE);
        LAYOUTERS.put("forest", ForestLayouter.PROTOTYPE);
    }

    /** Name of the layout option. */
    public final static String NAME = "-l";
    /** Meta-variable of the layout option. */
    public final static String VAR = "layout";
    /** Usage message for the layout option. */
    public final static String USAGE
        = "Lays out every graph afresh, ignoring the layout stored with it. "
            + "Supported layouts are: spring, forest";
}

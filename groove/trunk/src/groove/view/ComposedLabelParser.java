/**
 * 
 */
package groove.view;

import java.util.Collection;
import java.util.LinkedHashSet;

import groove.graph.Label;

/**
 * Label parser consisting of a collection of parser, which are consecutively applied.
 * Parsing only succeeds if all parsers agree on the result.
 * @author Arend Rensink
 * @version $Revision: 1.1 $
 */
public class ComposedLabelParser implements LabelParser {
    /** Constructs a new parser from a given collection of parsers. */
    public ComposedLabelParser(final Collection<LabelParser> parsers) {
        this.parsers = new LinkedHashSet<LabelParser>(parsers);
    }

    /**
     * Iterates over the stored parsers, and compares thei results for the label text.
     * Throws an exception if the parsers do not agree on the result.
     */
    public Label parse(String text) throws FormatException {
        Label result = null;
        for (LabelParser parser : parsers) {
            Label newLabel = parser.parse(text);
            if (result == null) {
                result = newLabel;
            } else if (!result.equals(newLabel)) {
                throw new FormatException("label '%s' cannot be parsed unambiguously", text);
            }
        }
        return result;
    }

    /**
     * Tries each of the stored unparsers, and returns the result if all
     * stored parsers agree that it is parsed back to the original label.
     */
    public String unparse(Label label) {
        for (LabelParser parser : parsers) {
            String result = parser.unparse(label);
            try {
                if (parse(result).equals(label)) {
                    return result;
                }
            } catch (FormatException e) {
                // go on to try the next unparser
            }
        }
        // no sucess
        return null;
    }

    /** The sub-parsers of which this parser is composed. */
    private final Collection<LabelParser> parsers;
}

package groove.view.aspect;

import groove.graph.TypeLabel;
import groove.view.FormatException;

/**
 * Parser constructing default labels, while taking label kinds into account.
 */
class PrefixedLabelParser implements LabelParser {
    /**
     * Private constructor for this singleton class.
     */
    private PrefixedLabelParser() {
        // empty
    }

    @Override
    public TypeLabel parse(String text) throws FormatException {
        String prefix = TypeLabel.getPrefix(text);
        if (prefix == null) {
            prefix = "";
        }
        int kind = TypeLabel.getPrefixKind(prefix);
        text = text.substring(prefix.length());
        return TypeLabel.createLabel(text, kind, true);
    }

    /**
     * Returns the singleton instance of this class.
     */
    static public PrefixedLabelParser getInstance() {
        return instance;
    }

    private static final PrefixedLabelParser instance =
        new PrefixedLabelParser();
}
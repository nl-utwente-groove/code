package groove.view.aspect;

import groove.graph.DefaultLabel;
import groove.graph.Label;
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
    public Label parse(String text) throws FormatException {
        String prefix = DefaultLabel.getPrefix(text);
        if (prefix == null) {
            prefix = "";
        }
        int kind = DefaultLabel.getPrefixKind(prefix);
        text = text.substring(prefix.length());
        return DefaultLabel.createLabel(text, kind, true);
    }

    @Override
    public DefaultLabel unparse(Label label) {
        return DefaultLabel.createLabel(label.text(), label.getKind());
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
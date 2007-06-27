package groove.view;

import groove.graph.DefaultLabel;
import groove.graph.Label;
import groove.rel.RegExpr;
import groove.rel.RegExprLabel;

/** Parser that attempts to turn the string into a regular expression label. */
public class RegExprLabelParser implements LabelParser {
	/**
	 * This implementation attempts to turn <code>text</code> into a 
	 * regular expression, and if successful, turns the expression into
	 * a {@link RegExprLabel}.
	 */
	public Label parse(String text) throws FormatException {
		RegExpr expr = RegExpr.parse(text);
		if (expr.isAtom()) {
			// TODO this is the place to get rid of single quotes,
			// by using expr.getAtomText()
			return DefaultLabel.createLabel(expr.getAtomText());
		} else {
			return expr.toLabel();
		}
	}

	/** 
     * This implementation puts quotes around the label text, if it can otherwise be
     * interpreted as a non-atom. 
     */
    public String unparse(Label label) {
        String result = label.text();
        if (label instanceof DefaultLabel && !RegExpr.isAtom(result)) {
            result = RegExpr.atom(result).toString();
        }
        return result;
    }
}

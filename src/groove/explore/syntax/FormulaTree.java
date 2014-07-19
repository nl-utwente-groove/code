package groove.explore.syntax;

import groove.grammar.model.FormatException;
import groove.util.antlr.ParseInfo;
import groove.util.antlr.ParseTree;

/**
 * Dedicated tree node for term parsing.
 * @author Arend Rensink
 * @version $Revision $
 */
public class FormulaTree extends ParseTree<FormulaTree,ParseInfo> {
    /**
     * Returns the term object corresponding to this tree.
     * All free variables in the tree must be type-derivable.
     */
    public Formula toFormula() throws FormatException {
        Formula result = null;
        switch (getType()) {
        case FormulaParser.AMP:
        case FormulaParser.AND:
        case FormulaParser.BAR:
        case FormulaParser.OR:
        case FormulaParser.IMPL:
        case FormulaParser.IMPL_BY:
        case FormulaParser.EQUIV:
        case FormulaParser.NOT:
        case FormulaParser.CALL:
        }
        return result;
    }

    /** Returns an expression parser for a given string. */
    public static FormulaParser getParser(String term) {
        return PROTOTYPE.createParser(FormulaParser.class, null, term);
    }

    private static final FormulaTree PROTOTYPE = new FormulaTree();
}

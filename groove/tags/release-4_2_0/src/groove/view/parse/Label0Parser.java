// $ANTLR 3.2 Sep 23, 2009 12:02:23 Label0.g 2010-05-04 09:28:18

package groove.view.parse;

import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.BitSet;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.DFA;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.FailedPredicateException;
import org.antlr.runtime.IntStream;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.RewriteRuleSubtreeStream;
import org.antlr.runtime.tree.RewriteRuleTokenStream;
import org.antlr.runtime.tree.TreeAdaptor;

@SuppressWarnings("all")
public class Label0Parser extends Parser {
    public static final String[] tokenNames = new String[] {"<invalid>",
        "<EOR>", "<DOWN>", "<UP>", "NEW", "DEL", "NOT", "USE", "CNEW", "REM",
        "FORALL", "FORALLX", "EXISTS", "NESTED", "INT", "REAL", "STRING",
        "BOOL", "ATTR", "PROD", "ARG", "PAR", "TYPE", "FLAG", "PATH", "EMPTY",
        "ATOM", "TRUE", "FALSE", "CONSTRAINT", "MINUS", "STAR", "PLUS", "DOT",
        "BAR", "HAT", "EQUALS", "LBRACE", "RBRACE", "LPAR", "RPAR", "LSQUARE",
        "RSQUARE", "PLING", "QUERY", "COLON", "COMMA", "SQUOTE", "DQUOTE",
        "DOLLAR", "UNDER", "BSLASH", "IDENT", "LABEL", "NUMBER", "LETTER",
        "IDENTCHAR", "DIGIT", "'\\n'"};
    public static final int DOLLAR = 49;
    public static final int STAR = 31;
    public static final int FORALLX = 11;
    public static final int LSQUARE = 41;
    public static final int LETTER = 55;
    public static final int DEL = 5;
    public static final int LBRACE = 37;
    public static final int NEW = 4;
    public static final int IDENTCHAR = 56;
    public static final int DQUOTE = 48;
    public static final int EQUALS = 36;
    public static final int NOT = 6;
    public static final int ATOM = 26;
    public static final int EOF = -1;
    public static final int TYPE = 22;
    public static final int HAT = 35;
    public static final int UNDER = 50;
    public static final int T__58 = 58;
    public static final int PLING = 43;
    public static final int ARG = 20;
    public static final int LPAR = 39;
    public static final int COMMA = 46;
    public static final int PATH = 24;
    public static final int PROD = 19;
    public static final int IDENT = 52;
    public static final int PAR = 21;
    public static final int PLUS = 32;
    public static final int DIGIT = 57;
    public static final int EXISTS = 12;
    public static final int DOT = 33;
    public static final int ATTR = 18;
    public static final int RBRACE = 38;
    public static final int NUMBER = 54;
    public static final int BOOL = 17;
    public static final int FORALL = 10;
    public static final int INT = 14;
    public static final int SQUOTE = 47;
    public static final int REM = 9;
    public static final int MINUS = 30;
    public static final int RSQUARE = 42;
    public static final int TRUE = 27;
    public static final int CNEW = 8;
    public static final int FLAG = 23;
    public static final int EMPTY = 25;
    public static final int COLON = 45;
    public static final int NESTED = 13;
    public static final int REAL = 15;
    public static final int LABEL = 53;
    public static final int QUERY = 44;
    public static final int RPAR = 40;
    public static final int USE = 7;
    public static final int FALSE = 28;
    public static final int CONSTRAINT = 29;
    public static final int BSLASH = 51;
    public static final int BAR = 34;
    public static final int STRING = 16;

    // delegates
    // delegators

    public Label0Parser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }

    public Label0Parser(TokenStream input, RecognizerSharedState state) {
        super(input, state);

    }

    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }

    public TreeAdaptor getTreeAdaptor() {
        return this.adaptor;
    }

    public String[] getTokenNames() {
        return Label0Parser.tokenNames;
    }

    public String getGrammarFileName() {
        return "Label0.g";
    }

    private boolean isGraph;

    public void setIsGraph(boolean isGraph) {
        this.isGraph = isGraph;
    }

    private List<String> errors = new LinkedList<String>();

    public void displayRecognitionError(String[] tokenNames,
            RecognitionException e) {
        String hdr = getErrorHeader(e);
        String msg = getErrorMessage(e, tokenNames);
        this.errors.add(hdr + " " + msg);
    }

    public List<String> getErrors() {
        return this.errors;
    }

    CommonTree concat(CommonTree seq) {
        String result;
        List children = seq.getChildren();
        if (children == null) {
            result = seq.getText();
        } else {
            StringBuilder builder = new StringBuilder();
            for (Object token : seq.getChildren()) {
                builder.append(((CommonTree) token).getText());
            }
            result = builder.toString();
        }
        return new CommonTree(new CommonToken(IDENT, result));
    }

    public static class label_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "label"
    // Label0.g:109:1: label : ( quantLabel EOF | specialLabel EOF );
    public final Label0Parser.label_return label() throws RecognitionException {
        Label0Parser.label_return retval = new Label0Parser.label_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token EOF2 = null;
        Token EOF4 = null;
        Label0Parser.quantLabel_return quantLabel1 = null;

        Label0Parser.specialLabel_return specialLabel3 = null;

        CommonTree EOF2_tree = null;
        CommonTree EOF4_tree = null;

        try {
            // Label0.g:110:4: ( quantLabel EOF | specialLabel EOF )
            int alt1 = 2;
            alt1 = this.dfa1.predict(this.input);
            switch (alt1) {
            case 1:
                // Label0.g:110:6: quantLabel EOF
            {
                root_0 = (CommonTree) this.adaptor.nil();

                pushFollow(FOLLOW_quantLabel_in_label494);
                quantLabel1 = quantLabel();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, quantLabel1.getTree());
                }
                EOF2 = (Token) match(this.input, EOF, FOLLOW_EOF_in_label496);
                if (this.state.failed) {
                    return retval;
                }

            }
                break;
            case 2:
                // Label0.g:111:6: specialLabel EOF
            {
                root_0 = (CommonTree) this.adaptor.nil();

                pushFollow(FOLLOW_specialLabel_in_label504);
                specialLabel3 = specialLabel();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, specialLabel3.getTree());
                }
                EOF4 = (Token) match(this.input, EOF, FOLLOW_EOF_in_label506);
                if (this.state.failed) {
                    return retval;
                }

            }
                break;

            }
            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "label"

    public static class quantLabel_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "quantLabel"
    // Label0.g:114:1: quantLabel : ( quantPrefix ( EQUALS IDENT COLON ( rolePrefix COLON actualLabel -> ^( rolePrefix IDENT actualLabel ) | actualLabel -> ^( USE IDENT actualLabel ) | -> ^( quantPrefix IDENT ) ) | COLON -> quantPrefix ) | roleLabel );
    public final Label0Parser.quantLabel_return quantLabel()
        throws RecognitionException {
        Label0Parser.quantLabel_return retval =
            new Label0Parser.quantLabel_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token EQUALS6 = null;
        Token IDENT7 = null;
        Token COLON8 = null;
        Token COLON10 = null;
        Token COLON13 = null;
        Label0Parser.quantPrefix_return quantPrefix5 = null;

        Label0Parser.rolePrefix_return rolePrefix9 = null;

        Label0Parser.actualLabel_return actualLabel11 = null;

        Label0Parser.actualLabel_return actualLabel12 = null;

        Label0Parser.roleLabel_return roleLabel14 = null;

        CommonTree EQUALS6_tree = null;
        CommonTree IDENT7_tree = null;
        CommonTree COLON8_tree = null;
        CommonTree COLON10_tree = null;
        CommonTree COLON13_tree = null;
        RewriteRuleTokenStream stream_COLON =
            new RewriteRuleTokenStream(this.adaptor, "token COLON");
        RewriteRuleTokenStream stream_IDENT =
            new RewriteRuleTokenStream(this.adaptor, "token IDENT");
        RewriteRuleTokenStream stream_EQUALS =
            new RewriteRuleTokenStream(this.adaptor, "token EQUALS");
        RewriteRuleSubtreeStream stream_actualLabel =
            new RewriteRuleSubtreeStream(this.adaptor, "rule actualLabel");
        RewriteRuleSubtreeStream stream_rolePrefix =
            new RewriteRuleSubtreeStream(this.adaptor, "rule rolePrefix");
        RewriteRuleSubtreeStream stream_quantPrefix =
            new RewriteRuleSubtreeStream(this.adaptor, "rule quantPrefix");
        try {
            // Label0.g:115:4: ( quantPrefix ( EQUALS IDENT COLON ( rolePrefix COLON actualLabel -> ^( rolePrefix IDENT actualLabel ) | actualLabel -> ^( USE IDENT actualLabel ) | -> ^( quantPrefix IDENT ) ) | COLON -> quantPrefix ) | roleLabel )
            int alt4 = 2;
            alt4 = this.dfa4.predict(this.input);
            switch (alt4) {
            case 1:
                // Label0.g:115:6: quantPrefix ( EQUALS IDENT COLON ( rolePrefix COLON actualLabel -> ^( rolePrefix IDENT actualLabel ) | actualLabel -> ^( USE IDENT actualLabel ) | -> ^( quantPrefix IDENT ) ) | COLON -> quantPrefix )
            {
                pushFollow(FOLLOW_quantPrefix_in_quantLabel522);
                quantPrefix5 = quantPrefix();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_quantPrefix.add(quantPrefix5.getTree());
                }
                // Label0.g:116:6: ( EQUALS IDENT COLON ( rolePrefix COLON actualLabel -> ^( rolePrefix IDENT actualLabel ) | actualLabel -> ^( USE IDENT actualLabel ) | -> ^( quantPrefix IDENT ) ) | COLON -> quantPrefix )
                int alt3 = 2;
                int LA3_0 = this.input.LA(1);

                if ((LA3_0 == EQUALS)) {
                    alt3 = 1;
                } else if ((LA3_0 == COLON)) {
                    alt3 = 2;
                } else {
                    if (this.state.backtracking > 0) {
                        this.state.failed = true;
                        return retval;
                    }
                    NoViableAltException nvae =
                        new NoViableAltException("", 3, 0, this.input);

                    throw nvae;
                }
                switch (alt3) {
                case 1:
                    // Label0.g:116:8: EQUALS IDENT COLON ( rolePrefix COLON actualLabel -> ^( rolePrefix IDENT actualLabel ) | actualLabel -> ^( USE IDENT actualLabel ) | -> ^( quantPrefix IDENT ) )
                {
                    EQUALS6 =
                        (Token) match(this.input, EQUALS,
                            FOLLOW_EQUALS_in_quantLabel531);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        stream_EQUALS.add(EQUALS6);
                    }

                    IDENT7 =
                        (Token) match(this.input, IDENT,
                            FOLLOW_IDENT_in_quantLabel533);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        stream_IDENT.add(IDENT7);
                    }

                    COLON8 =
                        (Token) match(this.input, COLON,
                            FOLLOW_COLON_in_quantLabel535);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        stream_COLON.add(COLON8);
                    }

                    // Label0.g:117:8: ( rolePrefix COLON actualLabel -> ^( rolePrefix IDENT actualLabel ) | actualLabel -> ^( USE IDENT actualLabel ) | -> ^( quantPrefix IDENT ) )
                    int alt2 = 3;
                    alt2 = this.dfa2.predict(this.input);
                    switch (alt2) {
                    case 1:
                        // Label0.g:117:10: rolePrefix COLON actualLabel
                    {
                        pushFollow(FOLLOW_rolePrefix_in_quantLabel546);
                        rolePrefix9 = rolePrefix();

                        this.state._fsp--;
                        if (this.state.failed) {
                            return retval;
                        }
                        if (this.state.backtracking == 0) {
                            stream_rolePrefix.add(rolePrefix9.getTree());
                        }
                        COLON10 =
                            (Token) match(this.input, COLON,
                                FOLLOW_COLON_in_quantLabel548);
                        if (this.state.failed) {
                            return retval;
                        }
                        if (this.state.backtracking == 0) {
                            stream_COLON.add(COLON10);
                        }

                        pushFollow(FOLLOW_actualLabel_in_quantLabel550);
                        actualLabel11 = actualLabel();

                        this.state._fsp--;
                        if (this.state.failed) {
                            return retval;
                        }
                        if (this.state.backtracking == 0) {
                            stream_actualLabel.add(actualLabel11.getTree());
                        }

                        // AST REWRITE
                        // elements: actualLabel, rolePrefix, IDENT
                        // token labels: 
                        // rule labels: retval
                        // token list labels: 
                        // rule list labels: 
                        // wildcard labels: 
                        if (this.state.backtracking == 0) {
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval =
                                new RewriteRuleSubtreeStream(this.adaptor,
                                    "rule retval", retval != null ? retval.tree
                                            : null);

                            root_0 = (CommonTree) this.adaptor.nil();
                            // 118:10: -> ^( rolePrefix IDENT actualLabel )
                            {
                                // Label0.g:118:13: ^( rolePrefix IDENT actualLabel )
                                {
                                    CommonTree root_1 =
                                        (CommonTree) this.adaptor.nil();
                                    root_1 =
                                        (CommonTree) this.adaptor.becomeRoot(
                                            stream_rolePrefix.nextNode(),
                                            root_1);

                                    this.adaptor.addChild(root_1,
                                        stream_IDENT.nextNode());
                                    this.adaptor.addChild(root_1,
                                        stream_actualLabel.nextTree());

                                    this.adaptor.addChild(root_0, root_1);
                                }

                            }

                            retval.tree = root_0;
                        }
                    }
                        break;
                    case 2:
                        // Label0.g:119:10: actualLabel
                    {
                        pushFollow(FOLLOW_actualLabel_in_quantLabel580);
                        actualLabel12 = actualLabel();

                        this.state._fsp--;
                        if (this.state.failed) {
                            return retval;
                        }
                        if (this.state.backtracking == 0) {
                            stream_actualLabel.add(actualLabel12.getTree());
                        }

                        // AST REWRITE
                        // elements: IDENT, actualLabel
                        // token labels: 
                        // rule labels: retval
                        // token list labels: 
                        // rule list labels: 
                        // wildcard labels: 
                        if (this.state.backtracking == 0) {
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval =
                                new RewriteRuleSubtreeStream(this.adaptor,
                                    "rule retval", retval != null ? retval.tree
                                            : null);

                            root_0 = (CommonTree) this.adaptor.nil();
                            // 120:10: -> ^( USE IDENT actualLabel )
                            {
                                // Label0.g:120:13: ^( USE IDENT actualLabel )
                                {
                                    CommonTree root_1 =
                                        (CommonTree) this.adaptor.nil();
                                    root_1 =
                                        (CommonTree) this.adaptor.becomeRoot(
                                            (CommonTree) this.adaptor.create(
                                                USE, "USE"), root_1);

                                    this.adaptor.addChild(root_1,
                                        stream_IDENT.nextNode());
                                    this.adaptor.addChild(root_1,
                                        stream_actualLabel.nextTree());

                                    this.adaptor.addChild(root_0, root_1);
                                }

                            }

                            retval.tree = root_0;
                        }
                    }
                        break;
                    case 3:
                        // Label0.g:121:10: 
                    {

                        // AST REWRITE
                        // elements: IDENT, quantPrefix
                        // token labels: 
                        // rule labels: retval
                        // token list labels: 
                        // rule list labels: 
                        // wildcard labels: 
                        if (this.state.backtracking == 0) {
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval =
                                new RewriteRuleSubtreeStream(this.adaptor,
                                    "rule retval", retval != null ? retval.tree
                                            : null);

                            root_0 = (CommonTree) this.adaptor.nil();
                            // 121:10: -> ^( quantPrefix IDENT )
                            {
                                // Label0.g:121:13: ^( quantPrefix IDENT )
                                {
                                    CommonTree root_1 =
                                        (CommonTree) this.adaptor.nil();
                                    root_1 =
                                        (CommonTree) this.adaptor.becomeRoot(
                                            stream_quantPrefix.nextNode(),
                                            root_1);

                                    this.adaptor.addChild(root_1,
                                        stream_IDENT.nextNode());

                                    this.adaptor.addChild(root_0, root_1);
                                }

                            }

                            retval.tree = root_0;
                        }
                    }
                        break;

                    }

                }
                    break;
                case 2:
                    // Label0.g:123:8: COLON
                {
                    COLON13 =
                        (Token) match(this.input, COLON,
                            FOLLOW_COLON_in_quantLabel634);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        stream_COLON.add(COLON13);
                    }

                    // AST REWRITE
                    // elements: quantPrefix
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if (this.state.backtracking == 0) {
                        retval.tree = root_0;
                        RewriteRuleSubtreeStream stream_retval =
                            new RewriteRuleSubtreeStream(this.adaptor,
                                "rule retval", retval != null ? retval.tree
                                        : null);

                        root_0 = (CommonTree) this.adaptor.nil();
                        // 124:10: -> quantPrefix
                        {
                            this.adaptor.addChild(root_0,
                                stream_quantPrefix.nextTree());

                        }

                        retval.tree = root_0;
                    }
                }
                    break;

                }

            }
                break;
            case 2:
                // Label0.g:126:6: roleLabel
            {
                root_0 = (CommonTree) this.adaptor.nil();

                pushFollow(FOLLOW_roleLabel_in_quantLabel661);
                roleLabel14 = roleLabel();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, roleLabel14.getTree());
                }

            }
                break;

            }
            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "quantLabel"

    public static class quantPrefix_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "quantPrefix"
    // Label0.g:129:1: quantPrefix : ( FORALL | FORALLX | EXISTS );
    public final Label0Parser.quantPrefix_return quantPrefix()
        throws RecognitionException {
        Label0Parser.quantPrefix_return retval =
            new Label0Parser.quantPrefix_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token set15 = null;

        CommonTree set15_tree = null;

        try {
            // Label0.g:130:4: ( FORALL | FORALLX | EXISTS )
            // Label0.g:
            {
                root_0 = (CommonTree) this.adaptor.nil();

                set15 = (Token) this.input.LT(1);
                if ((this.input.LA(1) >= FORALL && this.input.LA(1) <= EXISTS)) {
                    this.input.consume();
                    if (this.state.backtracking == 0) {
                        this.adaptor.addChild(root_0,
                            (CommonTree) this.adaptor.create(set15));
                    }
                    this.state.errorRecovery = false;
                    this.state.failed = false;
                } else {
                    if (this.state.backtracking > 0) {
                        this.state.failed = true;
                        return retval;
                    }
                    MismatchedSetException mse =
                        new MismatchedSetException(null, this.input);
                    throw mse;
                }

            }

            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "quantPrefix"

    public static class roleLabel_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "roleLabel"
    // Label0.g:133:1: roleLabel : ( rolePrefix ( EQUALS IDENT COLON actualLabel -> ^( rolePrefix IDENT actualLabel ) | COLON ( actualLabel -> ^( rolePrefix actualLabel ) | -> rolePrefix ) ) | actualLabel );
    public final Label0Parser.roleLabel_return roleLabel()
        throws RecognitionException {
        Label0Parser.roleLabel_return retval =
            new Label0Parser.roleLabel_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token EQUALS17 = null;
        Token IDENT18 = null;
        Token COLON19 = null;
        Token COLON21 = null;
        Label0Parser.rolePrefix_return rolePrefix16 = null;

        Label0Parser.actualLabel_return actualLabel20 = null;

        Label0Parser.actualLabel_return actualLabel22 = null;

        Label0Parser.actualLabel_return actualLabel23 = null;

        CommonTree EQUALS17_tree = null;
        CommonTree IDENT18_tree = null;
        CommonTree COLON19_tree = null;
        CommonTree COLON21_tree = null;
        RewriteRuleTokenStream stream_COLON =
            new RewriteRuleTokenStream(this.adaptor, "token COLON");
        RewriteRuleTokenStream stream_IDENT =
            new RewriteRuleTokenStream(this.adaptor, "token IDENT");
        RewriteRuleTokenStream stream_EQUALS =
            new RewriteRuleTokenStream(this.adaptor, "token EQUALS");
        RewriteRuleSubtreeStream stream_actualLabel =
            new RewriteRuleSubtreeStream(this.adaptor, "rule actualLabel");
        RewriteRuleSubtreeStream stream_rolePrefix =
            new RewriteRuleSubtreeStream(this.adaptor, "rule rolePrefix");
        try {
            // Label0.g:134:4: ( rolePrefix ( EQUALS IDENT COLON actualLabel -> ^( rolePrefix IDENT actualLabel ) | COLON ( actualLabel -> ^( rolePrefix actualLabel ) | -> rolePrefix ) ) | actualLabel )
            int alt7 = 2;
            alt7 = this.dfa7.predict(this.input);
            switch (alt7) {
            case 1:
                // Label0.g:134:6: rolePrefix ( EQUALS IDENT COLON actualLabel -> ^( rolePrefix IDENT actualLabel ) | COLON ( actualLabel -> ^( rolePrefix actualLabel ) | -> rolePrefix ) )
            {
                pushFollow(FOLLOW_rolePrefix_in_roleLabel699);
                rolePrefix16 = rolePrefix();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_rolePrefix.add(rolePrefix16.getTree());
                }
                // Label0.g:135:6: ( EQUALS IDENT COLON actualLabel -> ^( rolePrefix IDENT actualLabel ) | COLON ( actualLabel -> ^( rolePrefix actualLabel ) | -> rolePrefix ) )
                int alt6 = 2;
                int LA6_0 = this.input.LA(1);

                if ((LA6_0 == EQUALS)) {
                    alt6 = 1;
                } else if ((LA6_0 == COLON)) {
                    alt6 = 2;
                } else {
                    if (this.state.backtracking > 0) {
                        this.state.failed = true;
                        return retval;
                    }
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 0, this.input);

                    throw nvae;
                }
                switch (alt6) {
                case 1:
                    // Label0.g:135:8: EQUALS IDENT COLON actualLabel
                {
                    EQUALS17 =
                        (Token) match(this.input, EQUALS,
                            FOLLOW_EQUALS_in_roleLabel708);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        stream_EQUALS.add(EQUALS17);
                    }

                    IDENT18 =
                        (Token) match(this.input, IDENT,
                            FOLLOW_IDENT_in_roleLabel710);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        stream_IDENT.add(IDENT18);
                    }

                    COLON19 =
                        (Token) match(this.input, COLON,
                            FOLLOW_COLON_in_roleLabel712);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        stream_COLON.add(COLON19);
                    }

                    pushFollow(FOLLOW_actualLabel_in_roleLabel714);
                    actualLabel20 = actualLabel();

                    this.state._fsp--;
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        stream_actualLabel.add(actualLabel20.getTree());
                    }

                    // AST REWRITE
                    // elements: actualLabel, IDENT, rolePrefix
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if (this.state.backtracking == 0) {
                        retval.tree = root_0;
                        RewriteRuleSubtreeStream stream_retval =
                            new RewriteRuleSubtreeStream(this.adaptor,
                                "rule retval", retval != null ? retval.tree
                                        : null);

                        root_0 = (CommonTree) this.adaptor.nil();
                        // 136:10: -> ^( rolePrefix IDENT actualLabel )
                        {
                            // Label0.g:136:13: ^( rolePrefix IDENT actualLabel )
                            {
                                CommonTree root_1 =
                                    (CommonTree) this.adaptor.nil();
                                root_1 =
                                    (CommonTree) this.adaptor.becomeRoot(
                                        stream_rolePrefix.nextNode(), root_1);

                                this.adaptor.addChild(root_1,
                                    stream_IDENT.nextNode());
                                this.adaptor.addChild(root_1,
                                    stream_actualLabel.nextTree());

                                this.adaptor.addChild(root_0, root_1);
                            }

                        }

                        retval.tree = root_0;
                    }
                }
                    break;
                case 2:
                    // Label0.g:137:8: COLON ( actualLabel -> ^( rolePrefix actualLabel ) | -> rolePrefix )
                {
                    COLON21 =
                        (Token) match(this.input, COLON,
                            FOLLOW_COLON_in_roleLabel742);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        stream_COLON.add(COLON21);
                    }

                    // Label0.g:138:8: ( actualLabel -> ^( rolePrefix actualLabel ) | -> rolePrefix )
                    int alt5 = 2;
                    alt5 = this.dfa5.predict(this.input);
                    switch (alt5) {
                    case 1:
                        // Label0.g:138:10: actualLabel
                    {
                        pushFollow(FOLLOW_actualLabel_in_roleLabel754);
                        actualLabel22 = actualLabel();

                        this.state._fsp--;
                        if (this.state.failed) {
                            return retval;
                        }
                        if (this.state.backtracking == 0) {
                            stream_actualLabel.add(actualLabel22.getTree());
                        }

                        // AST REWRITE
                        // elements: actualLabel, rolePrefix
                        // token labels: 
                        // rule labels: retval
                        // token list labels: 
                        // rule list labels: 
                        // wildcard labels: 
                        if (this.state.backtracking == 0) {
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval =
                                new RewriteRuleSubtreeStream(this.adaptor,
                                    "rule retval", retval != null ? retval.tree
                                            : null);

                            root_0 = (CommonTree) this.adaptor.nil();
                            // 139:10: -> ^( rolePrefix actualLabel )
                            {
                                // Label0.g:139:13: ^( rolePrefix actualLabel )
                                {
                                    CommonTree root_1 =
                                        (CommonTree) this.adaptor.nil();
                                    root_1 =
                                        (CommonTree) this.adaptor.becomeRoot(
                                            stream_rolePrefix.nextNode(),
                                            root_1);

                                    this.adaptor.addChild(root_1,
                                        stream_actualLabel.nextTree());

                                    this.adaptor.addChild(root_0, root_1);
                                }

                            }

                            retval.tree = root_0;
                        }
                    }
                        break;
                    case 2:
                        // Label0.g:140:10: 
                    {

                        // AST REWRITE
                        // elements: rolePrefix
                        // token labels: 
                        // rule labels: retval
                        // token list labels: 
                        // rule list labels: 
                        // wildcard labels: 
                        if (this.state.backtracking == 0) {
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval =
                                new RewriteRuleSubtreeStream(this.adaptor,
                                    "rule retval", retval != null ? retval.tree
                                            : null);

                            root_0 = (CommonTree) this.adaptor.nil();
                            // 140:10: -> rolePrefix
                            {
                                this.adaptor.addChild(root_0,
                                    stream_rolePrefix.nextTree());

                            }

                            retval.tree = root_0;
                        }
                    }
                        break;

                    }

                }
                    break;

                }

            }
                break;
            case 2:
                // Label0.g:143:6: actualLabel
            {
                root_0 = (CommonTree) this.adaptor.nil();

                pushFollow(FOLLOW_actualLabel_in_roleLabel807);
                actualLabel23 = actualLabel();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, actualLabel23.getTree());
                }

            }
                break;

            }
            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "roleLabel"

    public static class rolePrefix_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "rolePrefix"
    // Label0.g:146:1: rolePrefix : ( NEW | DEL | NOT | USE | CNEW );
    public final Label0Parser.rolePrefix_return rolePrefix()
        throws RecognitionException {
        Label0Parser.rolePrefix_return retval =
            new Label0Parser.rolePrefix_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token set24 = null;

        CommonTree set24_tree = null;

        try {
            // Label0.g:147:4: ( NEW | DEL | NOT | USE | CNEW )
            // Label0.g:
            {
                root_0 = (CommonTree) this.adaptor.nil();

                set24 = (Token) this.input.LT(1);
                if ((this.input.LA(1) >= NEW && this.input.LA(1) <= CNEW)) {
                    this.input.consume();
                    if (this.state.backtracking == 0) {
                        this.adaptor.addChild(root_0,
                            (CommonTree) this.adaptor.create(set24));
                    }
                    this.state.errorRecovery = false;
                    this.state.failed = false;
                } else {
                    if (this.state.backtracking > 0) {
                        this.state.failed = true;
                        return retval;
                    }
                    MismatchedSetException mse =
                        new MismatchedSetException(null, this.input);
                    throw mse;
                }

            }

            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "rolePrefix"

    public static class specialLabel_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "specialLabel"
    // Label0.g:150:1: specialLabel : ( REM COLON text | PAR ( EQUALS LABEL )? COLON | NESTED COLON IDENT | INT COLON ( NUMBER | IDENT )? | REAL COLON ( rnumber | IDENT )? | STRING COLON ( dqText | IDENT )? | BOOL COLON ( TRUE | FALSE | IDENT )? | ATTR COLON | PROD COLON | ARG COLON NUMBER );
    public final Label0Parser.specialLabel_return specialLabel()
        throws RecognitionException {
        Label0Parser.specialLabel_return retval =
            new Label0Parser.specialLabel_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token REM25 = null;
        Token COLON26 = null;
        Token PAR28 = null;
        Token EQUALS29 = null;
        Token LABEL30 = null;
        Token COLON31 = null;
        Token NESTED32 = null;
        Token COLON33 = null;
        Token IDENT34 = null;
        Token INT35 = null;
        Token COLON36 = null;
        Token set37 = null;
        Token REAL38 = null;
        Token COLON39 = null;
        Token IDENT41 = null;
        Token STRING42 = null;
        Token COLON43 = null;
        Token IDENT45 = null;
        Token BOOL46 = null;
        Token COLON47 = null;
        Token set48 = null;
        Token ATTR49 = null;
        Token COLON50 = null;
        Token PROD51 = null;
        Token COLON52 = null;
        Token ARG53 = null;
        Token COLON54 = null;
        Token NUMBER55 = null;
        Label0Parser.text_return text27 = null;

        Label0Parser.rnumber_return rnumber40 = null;

        Label0Parser.dqText_return dqText44 = null;

        CommonTree REM25_tree = null;
        CommonTree COLON26_tree = null;
        CommonTree PAR28_tree = null;
        CommonTree EQUALS29_tree = null;
        CommonTree LABEL30_tree = null;
        CommonTree COLON31_tree = null;
        CommonTree NESTED32_tree = null;
        CommonTree COLON33_tree = null;
        CommonTree IDENT34_tree = null;
        CommonTree INT35_tree = null;
        CommonTree COLON36_tree = null;
        CommonTree set37_tree = null;
        CommonTree REAL38_tree = null;
        CommonTree COLON39_tree = null;
        CommonTree IDENT41_tree = null;
        CommonTree STRING42_tree = null;
        CommonTree COLON43_tree = null;
        CommonTree IDENT45_tree = null;
        CommonTree BOOL46_tree = null;
        CommonTree COLON47_tree = null;
        CommonTree set48_tree = null;
        CommonTree ATTR49_tree = null;
        CommonTree COLON50_tree = null;
        CommonTree PROD51_tree = null;
        CommonTree COLON52_tree = null;
        CommonTree ARG53_tree = null;
        CommonTree COLON54_tree = null;
        CommonTree NUMBER55_tree = null;

        try {
            // Label0.g:151:4: ( REM COLON text | PAR ( EQUALS LABEL )? COLON | NESTED COLON IDENT | INT COLON ( NUMBER | IDENT )? | REAL COLON ( rnumber | IDENT )? | STRING COLON ( dqText | IDENT )? | BOOL COLON ( TRUE | FALSE | IDENT )? | ATTR COLON | PROD COLON | ARG COLON NUMBER )
            int alt13 = 10;
            switch (this.input.LA(1)) {
            case REM: {
                alt13 = 1;
            }
                break;
            case PAR: {
                alt13 = 2;
            }
                break;
            case NESTED: {
                alt13 = 3;
            }
                break;
            case INT: {
                alt13 = 4;
            }
                break;
            case REAL: {
                alt13 = 5;
            }
                break;
            case STRING: {
                alt13 = 6;
            }
                break;
            case BOOL: {
                alt13 = 7;
            }
                break;
            case ATTR: {
                alt13 = 8;
            }
                break;
            case PROD: {
                alt13 = 9;
            }
                break;
            case ARG: {
                alt13 = 10;
            }
                break;
            default:
                if (this.state.backtracking > 0) {
                    this.state.failed = true;
                    return retval;
                }
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, this.input);

                throw nvae;
            }

            switch (alt13) {
            case 1:
                // Label0.g:151:6: REM COLON text
            {
                root_0 = (CommonTree) this.adaptor.nil();

                REM25 =
                    (Token) match(this.input, REM,
                        FOLLOW_REM_in_specialLabel853);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    REM25_tree = (CommonTree) this.adaptor.create(REM25);
                    root_0 =
                        (CommonTree) this.adaptor.becomeRoot(REM25_tree, root_0);
                }
                COLON26 =
                    (Token) match(this.input, COLON,
                        FOLLOW_COLON_in_specialLabel856);
                if (this.state.failed) {
                    return retval;
                }
                pushFollow(FOLLOW_text_in_specialLabel859);
                text27 = text();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, text27.getTree());
                }

            }
                break;
            case 2:
                // Label0.g:152:6: PAR ( EQUALS LABEL )? COLON
            {
                root_0 = (CommonTree) this.adaptor.nil();

                PAR28 =
                    (Token) match(this.input, PAR,
                        FOLLOW_PAR_in_specialLabel866);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    PAR28_tree = (CommonTree) this.adaptor.create(PAR28);
                    root_0 =
                        (CommonTree) this.adaptor.becomeRoot(PAR28_tree, root_0);
                }
                // Label0.g:152:11: ( EQUALS LABEL )?
                int alt8 = 2;
                int LA8_0 = this.input.LA(1);

                if ((LA8_0 == EQUALS)) {
                    alt8 = 1;
                }
                switch (alt8) {
                case 1:
                    // Label0.g:152:12: EQUALS LABEL
                {
                    EQUALS29 =
                        (Token) match(this.input, EQUALS,
                            FOLLOW_EQUALS_in_specialLabel870);
                    if (this.state.failed) {
                        return retval;
                    }
                    LABEL30 =
                        (Token) match(this.input, LABEL,
                            FOLLOW_LABEL_in_specialLabel873);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        LABEL30_tree =
                            (CommonTree) this.adaptor.create(LABEL30);
                        this.adaptor.addChild(root_0, LABEL30_tree);
                    }

                }
                    break;

                }

                COLON31 =
                    (Token) match(this.input, COLON,
                        FOLLOW_COLON_in_specialLabel877);
                if (this.state.failed) {
                    return retval;
                }

            }
                break;
            case 3:
                // Label0.g:153:6: NESTED COLON IDENT
            {
                root_0 = (CommonTree) this.adaptor.nil();

                NESTED32 =
                    (Token) match(this.input, NESTED,
                        FOLLOW_NESTED_in_specialLabel885);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    NESTED32_tree = (CommonTree) this.adaptor.create(NESTED32);
                    root_0 =
                        (CommonTree) this.adaptor.becomeRoot(NESTED32_tree,
                            root_0);
                }
                COLON33 =
                    (Token) match(this.input, COLON,
                        FOLLOW_COLON_in_specialLabel888);
                if (this.state.failed) {
                    return retval;
                }
                IDENT34 =
                    (Token) match(this.input, IDENT,
                        FOLLOW_IDENT_in_specialLabel891);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    IDENT34_tree = (CommonTree) this.adaptor.create(IDENT34);
                    this.adaptor.addChild(root_0, IDENT34_tree);
                }

            }
                break;
            case 4:
                // Label0.g:155:6: INT COLON ( NUMBER | IDENT )?
            {
                root_0 = (CommonTree) this.adaptor.nil();

                INT35 =
                    (Token) match(this.input, INT,
                        FOLLOW_INT_in_specialLabel902);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    INT35_tree = (CommonTree) this.adaptor.create(INT35);
                    root_0 =
                        (CommonTree) this.adaptor.becomeRoot(INT35_tree, root_0);
                }
                COLON36 =
                    (Token) match(this.input, COLON,
                        FOLLOW_COLON_in_specialLabel905);
                if (this.state.failed) {
                    return retval;
                }
                // Label0.g:155:18: ( NUMBER | IDENT )?
                int alt9 = 2;
                int LA9_0 = this.input.LA(1);

                if ((LA9_0 == IDENT || LA9_0 == NUMBER)) {
                    alt9 = 1;
                }
                switch (alt9) {
                case 1:
                    // Label0.g:
                {
                    set37 = (Token) this.input.LT(1);
                    if (this.input.LA(1) == IDENT || this.input.LA(1) == NUMBER) {
                        this.input.consume();
                        if (this.state.backtracking == 0) {
                            this.adaptor.addChild(root_0,
                                (CommonTree) this.adaptor.create(set37));
                        }
                        this.state.errorRecovery = false;
                        this.state.failed = false;
                    } else {
                        if (this.state.backtracking > 0) {
                            this.state.failed = true;
                            return retval;
                        }
                        MismatchedSetException mse =
                            new MismatchedSetException(null, this.input);
                        throw mse;
                    }

                }
                    break;

                }

            }
                break;
            case 5:
                // Label0.g:156:6: REAL COLON ( rnumber | IDENT )?
            {
                root_0 = (CommonTree) this.adaptor.nil();

                REAL38 =
                    (Token) match(this.input, REAL,
                        FOLLOW_REAL_in_specialLabel922);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    REAL38_tree = (CommonTree) this.adaptor.create(REAL38);
                    root_0 =
                        (CommonTree) this.adaptor.becomeRoot(REAL38_tree,
                            root_0);
                }
                COLON39 =
                    (Token) match(this.input, COLON,
                        FOLLOW_COLON_in_specialLabel925);
                if (this.state.failed) {
                    return retval;
                }
                // Label0.g:156:19: ( rnumber | IDENT )?
                int alt10 = 3;
                int LA10_0 = this.input.LA(1);

                if ((LA10_0 == DOT || LA10_0 == NUMBER)) {
                    alt10 = 1;
                } else if ((LA10_0 == IDENT)) {
                    alt10 = 2;
                }
                switch (alt10) {
                case 1:
                    // Label0.g:156:20: rnumber
                {
                    pushFollow(FOLLOW_rnumber_in_specialLabel929);
                    rnumber40 = rnumber();

                    this.state._fsp--;
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        this.adaptor.addChild(root_0, rnumber40.getTree());
                    }

                }
                    break;
                case 2:
                    // Label0.g:156:30: IDENT
                {
                    IDENT41 =
                        (Token) match(this.input, IDENT,
                            FOLLOW_IDENT_in_specialLabel933);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        IDENT41_tree =
                            (CommonTree) this.adaptor.create(IDENT41);
                        this.adaptor.addChild(root_0, IDENT41_tree);
                    }

                }
                    break;

                }

            }
                break;
            case 6:
                // Label0.g:157:6: STRING COLON ( dqText | IDENT )?
            {
                root_0 = (CommonTree) this.adaptor.nil();

                STRING42 =
                    (Token) match(this.input, STRING,
                        FOLLOW_STRING_in_specialLabel942);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    STRING42_tree = (CommonTree) this.adaptor.create(STRING42);
                    root_0 =
                        (CommonTree) this.adaptor.becomeRoot(STRING42_tree,
                            root_0);
                }
                COLON43 =
                    (Token) match(this.input, COLON,
                        FOLLOW_COLON_in_specialLabel945);
                if (this.state.failed) {
                    return retval;
                }
                // Label0.g:157:21: ( dqText | IDENT )?
                int alt11 = 3;
                int LA11_0 = this.input.LA(1);

                if ((LA11_0 == DQUOTE)) {
                    alt11 = 1;
                } else if ((LA11_0 == IDENT)) {
                    alt11 = 2;
                }
                switch (alt11) {
                case 1:
                    // Label0.g:157:22: dqText
                {
                    pushFollow(FOLLOW_dqText_in_specialLabel949);
                    dqText44 = dqText();

                    this.state._fsp--;
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        this.adaptor.addChild(root_0, dqText44.getTree());
                    }

                }
                    break;
                case 2:
                    // Label0.g:157:31: IDENT
                {
                    IDENT45 =
                        (Token) match(this.input, IDENT,
                            FOLLOW_IDENT_in_specialLabel953);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        IDENT45_tree =
                            (CommonTree) this.adaptor.create(IDENT45);
                        this.adaptor.addChild(root_0, IDENT45_tree);
                    }

                }
                    break;

                }

            }
                break;
            case 7:
                // Label0.g:158:6: BOOL COLON ( TRUE | FALSE | IDENT )?
            {
                root_0 = (CommonTree) this.adaptor.nil();

                BOOL46 =
                    (Token) match(this.input, BOOL,
                        FOLLOW_BOOL_in_specialLabel962);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    BOOL46_tree = (CommonTree) this.adaptor.create(BOOL46);
                    root_0 =
                        (CommonTree) this.adaptor.becomeRoot(BOOL46_tree,
                            root_0);
                }
                COLON47 =
                    (Token) match(this.input, COLON,
                        FOLLOW_COLON_in_specialLabel965);
                if (this.state.failed) {
                    return retval;
                }
                // Label0.g:158:19: ( TRUE | FALSE | IDENT )?
                int alt12 = 2;
                int LA12_0 = this.input.LA(1);

                if (((LA12_0 >= TRUE && LA12_0 <= FALSE) || LA12_0 == IDENT)) {
                    alt12 = 1;
                }
                switch (alt12) {
                case 1:
                    // Label0.g:
                {
                    set48 = (Token) this.input.LT(1);
                    if ((this.input.LA(1) >= TRUE && this.input.LA(1) <= FALSE)
                        || this.input.LA(1) == IDENT) {
                        this.input.consume();
                        if (this.state.backtracking == 0) {
                            this.adaptor.addChild(root_0,
                                (CommonTree) this.adaptor.create(set48));
                        }
                        this.state.errorRecovery = false;
                        this.state.failed = false;
                    } else {
                        if (this.state.backtracking > 0) {
                            this.state.failed = true;
                            return retval;
                        }
                        MismatchedSetException mse =
                            new MismatchedSetException(null, this.input);
                        throw mse;
                    }

                }
                    break;

                }

            }
                break;
            case 8:
                // Label0.g:159:6: ATTR COLON
            {
                root_0 = (CommonTree) this.adaptor.nil();

                ATTR49 =
                    (Token) match(this.input, ATTR,
                        FOLLOW_ATTR_in_specialLabel986);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    ATTR49_tree = (CommonTree) this.adaptor.create(ATTR49);
                    root_0 =
                        (CommonTree) this.adaptor.becomeRoot(ATTR49_tree,
                            root_0);
                }
                COLON50 =
                    (Token) match(this.input, COLON,
                        FOLLOW_COLON_in_specialLabel989);
                if (this.state.failed) {
                    return retval;
                }

            }
                break;
            case 9:
                // Label0.g:160:6: PROD COLON
            {
                root_0 = (CommonTree) this.adaptor.nil();

                PROD51 =
                    (Token) match(this.input, PROD,
                        FOLLOW_PROD_in_specialLabel997);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    PROD51_tree = (CommonTree) this.adaptor.create(PROD51);
                    root_0 =
                        (CommonTree) this.adaptor.becomeRoot(PROD51_tree,
                            root_0);
                }
                COLON52 =
                    (Token) match(this.input, COLON,
                        FOLLOW_COLON_in_specialLabel1000);
                if (this.state.failed) {
                    return retval;
                }

            }
                break;
            case 10:
                // Label0.g:161:6: ARG COLON NUMBER
            {
                root_0 = (CommonTree) this.adaptor.nil();

                ARG53 =
                    (Token) match(this.input, ARG,
                        FOLLOW_ARG_in_specialLabel1008);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    ARG53_tree = (CommonTree) this.adaptor.create(ARG53);
                    root_0 =
                        (CommonTree) this.adaptor.becomeRoot(ARG53_tree, root_0);
                }
                COLON54 =
                    (Token) match(this.input, COLON,
                        FOLLOW_COLON_in_specialLabel1011);
                if (this.state.failed) {
                    return retval;
                }
                NUMBER55 =
                    (Token) match(this.input, NUMBER,
                        FOLLOW_NUMBER_in_specialLabel1014);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    NUMBER55_tree = (CommonTree) this.adaptor.create(NUMBER55);
                    this.adaptor.addChild(root_0, NUMBER55_tree);
                }

            }
                break;

            }
            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "specialLabel"

    public static class actualLabel_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "actualLabel"
    // Label0.g:164:1: actualLabel : ( TYPE COLON IDENT -> ^( ATOM ^( TYPE IDENT ) ) | FLAG COLON IDENT -> ^( ATOM ^( FLAG IDENT ) ) | COLON text -> ^( ATOM ) | PATH COLON regExpr | ( graphDefault EOF )=>{...}? => graphLabel | ( ruleLabel EOF )=>{...}? => ruleLabel );
    public final Label0Parser.actualLabel_return actualLabel()
        throws RecognitionException {
        Label0Parser.actualLabel_return retval =
            new Label0Parser.actualLabel_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token TYPE56 = null;
        Token COLON57 = null;
        Token IDENT58 = null;
        Token FLAG59 = null;
        Token COLON60 = null;
        Token IDENT61 = null;
        Token COLON62 = null;
        Token PATH64 = null;
        Token COLON65 = null;
        Label0Parser.text_return text63 = null;

        Label0Parser.regExpr_return regExpr66 = null;

        Label0Parser.graphLabel_return graphLabel67 = null;

        Label0Parser.ruleLabel_return ruleLabel68 = null;

        CommonTree TYPE56_tree = null;
        CommonTree COLON57_tree = null;
        CommonTree IDENT58_tree = null;
        CommonTree FLAG59_tree = null;
        CommonTree COLON60_tree = null;
        CommonTree IDENT61_tree = null;
        CommonTree COLON62_tree = null;
        CommonTree PATH64_tree = null;
        CommonTree COLON65_tree = null;
        RewriteRuleTokenStream stream_COLON =
            new RewriteRuleTokenStream(this.adaptor, "token COLON");
        RewriteRuleTokenStream stream_IDENT =
            new RewriteRuleTokenStream(this.adaptor, "token IDENT");
        RewriteRuleTokenStream stream_FLAG =
            new RewriteRuleTokenStream(this.adaptor, "token FLAG");
        RewriteRuleTokenStream stream_TYPE =
            new RewriteRuleTokenStream(this.adaptor, "token TYPE");
        RewriteRuleSubtreeStream stream_text =
            new RewriteRuleSubtreeStream(this.adaptor, "rule text");
        try {
            // Label0.g:165:4: ( TYPE COLON IDENT -> ^( ATOM ^( TYPE IDENT ) ) | FLAG COLON IDENT -> ^( ATOM ^( FLAG IDENT ) ) | COLON text -> ^( ATOM ) | PATH COLON regExpr | ( graphDefault EOF )=>{...}? => graphLabel | ( ruleLabel EOF )=>{...}? => ruleLabel )
            int alt14 = 6;
            alt14 = this.dfa14.predict(this.input);
            switch (alt14) {
            case 1:
                // Label0.g:165:6: TYPE COLON IDENT
            {
                TYPE56 =
                    (Token) match(this.input, TYPE,
                        FOLLOW_TYPE_in_actualLabel1029);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_TYPE.add(TYPE56);
                }

                COLON57 =
                    (Token) match(this.input, COLON,
                        FOLLOW_COLON_in_actualLabel1031);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_COLON.add(COLON57);
                }

                IDENT58 =
                    (Token) match(this.input, IDENT,
                        FOLLOW_IDENT_in_actualLabel1033);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_IDENT.add(IDENT58);
                }

                // AST REWRITE
                // elements: TYPE, IDENT
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                if (this.state.backtracking == 0) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval =
                        new RewriteRuleSubtreeStream(this.adaptor,
                            "rule retval", retval != null ? retval.tree : null);

                    root_0 = (CommonTree) this.adaptor.nil();
                    // 165:23: -> ^( ATOM ^( TYPE IDENT ) )
                    {
                        // Label0.g:165:26: ^( ATOM ^( TYPE IDENT ) )
                        {
                            CommonTree root_1 = (CommonTree) this.adaptor.nil();
                            root_1 =
                                (CommonTree) this.adaptor.becomeRoot(
                                    (CommonTree) this.adaptor.create(ATOM,
                                        "ATOM"), root_1);

                            // Label0.g:165:33: ^( TYPE IDENT )
                            {
                                CommonTree root_2 =
                                    (CommonTree) this.adaptor.nil();
                                root_2 =
                                    (CommonTree) this.adaptor.becomeRoot(
                                        stream_TYPE.nextNode(), root_2);

                                this.adaptor.addChild(root_2,
                                    stream_IDENT.nextNode());

                                this.adaptor.addChild(root_1, root_2);
                            }

                            this.adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                }
            }
                break;
            case 2:
                // Label0.g:166:6: FLAG COLON IDENT
            {
                FLAG59 =
                    (Token) match(this.input, FLAG,
                        FOLLOW_FLAG_in_actualLabel1052);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_FLAG.add(FLAG59);
                }

                COLON60 =
                    (Token) match(this.input, COLON,
                        FOLLOW_COLON_in_actualLabel1054);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_COLON.add(COLON60);
                }

                IDENT61 =
                    (Token) match(this.input, IDENT,
                        FOLLOW_IDENT_in_actualLabel1056);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_IDENT.add(IDENT61);
                }

                // AST REWRITE
                // elements: IDENT, FLAG
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                if (this.state.backtracking == 0) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval =
                        new RewriteRuleSubtreeStream(this.adaptor,
                            "rule retval", retval != null ? retval.tree : null);

                    root_0 = (CommonTree) this.adaptor.nil();
                    // 166:23: -> ^( ATOM ^( FLAG IDENT ) )
                    {
                        // Label0.g:166:26: ^( ATOM ^( FLAG IDENT ) )
                        {
                            CommonTree root_1 = (CommonTree) this.adaptor.nil();
                            root_1 =
                                (CommonTree) this.adaptor.becomeRoot(
                                    (CommonTree) this.adaptor.create(ATOM,
                                        "ATOM"), root_1);

                            // Label0.g:166:33: ^( FLAG IDENT )
                            {
                                CommonTree root_2 =
                                    (CommonTree) this.adaptor.nil();
                                root_2 =
                                    (CommonTree) this.adaptor.becomeRoot(
                                        stream_FLAG.nextNode(), root_2);

                                this.adaptor.addChild(root_2,
                                    stream_IDENT.nextNode());

                                this.adaptor.addChild(root_1, root_2);
                            }

                            this.adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                }
            }
                break;
            case 3:
                // Label0.g:167:6: COLON text
            {
                COLON62 =
                    (Token) match(this.input, COLON,
                        FOLLOW_COLON_in_actualLabel1075);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_COLON.add(COLON62);
                }

                pushFollow(FOLLOW_text_in_actualLabel1077);
                text63 = text();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_text.add(text63.getTree());
                }

                // AST REWRITE
                // elements: 
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                if (this.state.backtracking == 0) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval =
                        new RewriteRuleSubtreeStream(this.adaptor,
                            "rule retval", retval != null ? retval.tree : null);

                    root_0 = (CommonTree) this.adaptor.nil();
                    // 167:17: -> ^( ATOM )
                    {
                        // Label0.g:167:20: ^( ATOM )
                        {
                            CommonTree root_1 = (CommonTree) this.adaptor.nil();
                            root_1 =
                                (CommonTree) this.adaptor.becomeRoot(
                                    (CommonTree) this.adaptor.create(ATOM,
                                        "ATOM"), root_1);

                            this.adaptor.addChild(root_1,
                                concat((text63 != null
                                        ? ((CommonTree) text63.tree) : null)));

                            this.adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                }
            }
                break;
            case 4:
                // Label0.g:168:6: PATH COLON regExpr
            {
                root_0 = (CommonTree) this.adaptor.nil();

                PATH64 =
                    (Token) match(this.input, PATH,
                        FOLLOW_PATH_in_actualLabel1092);
                if (this.state.failed) {
                    return retval;
                }
                COLON65 =
                    (Token) match(this.input, COLON,
                        FOLLOW_COLON_in_actualLabel1095);
                if (this.state.failed) {
                    return retval;
                }
                pushFollow(FOLLOW_regExpr_in_actualLabel1098);
                regExpr66 = regExpr();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, regExpr66.getTree());
                }

            }
                break;
            case 5:
                // Label0.g:169:6: ( graphDefault EOF )=>{...}? => graphLabel
            {
                root_0 = (CommonTree) this.adaptor.nil();

                if (!((this.isGraph))) {
                    if (this.state.backtracking > 0) {
                        this.state.failed = true;
                        return retval;
                    }
                    throw new FailedPredicateException(this.input,
                        "actualLabel", " isGraph ");
                }
                pushFollow(FOLLOW_graphLabel_in_actualLabel1117);
                graphLabel67 = graphLabel();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, graphLabel67.getTree());
                }

            }
                break;
            case 6:
                // Label0.g:170:6: ( ruleLabel EOF )=>{...}? => ruleLabel
            {
                root_0 = (CommonTree) this.adaptor.nil();

                if (!((!this.isGraph))) {
                    if (this.state.backtracking > 0) {
                        this.state.failed = true;
                        return retval;
                    }
                    throw new FailedPredicateException(this.input,
                        "actualLabel", " !isGraph ");
                }
                pushFollow(FOLLOW_ruleLabel_in_actualLabel1136);
                ruleLabel68 = ruleLabel();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, ruleLabel68.getTree());
                }

            }
                break;

            }
            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "actualLabel"

    public static class text_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "text"
    // Label0.g:173:1: text : (~ '\\n' )* ;
    public final Label0Parser.text_return text() throws RecognitionException {
        Label0Parser.text_return retval = new Label0Parser.text_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token set69 = null;

        CommonTree set69_tree = null;

        try {
            // Label0.g:174:4: ( (~ '\\n' )* )
            // Label0.g:174:6: (~ '\\n' )*
            {
                root_0 = (CommonTree) this.adaptor.nil();

                // Label0.g:174:6: (~ '\\n' )*
                loop15: do {
                    int alt15 = 2;
                    int LA15_0 = this.input.LA(1);

                    if (((LA15_0 >= NEW && LA15_0 <= DIGIT))) {
                        alt15 = 1;
                    }

                    switch (alt15) {
                    case 1:
                        // Label0.g:174:7: ~ '\\n'
                    {
                        set69 = (Token) this.input.LT(1);
                        if ((this.input.LA(1) >= NEW && this.input.LA(1) <= DIGIT)) {
                            this.input.consume();
                            if (this.state.backtracking == 0) {
                                this.adaptor.addChild(root_0,
                                    (CommonTree) this.adaptor.create(set69));
                            }
                            this.state.errorRecovery = false;
                            this.state.failed = false;
                        } else {
                            if (this.state.backtracking > 0) {
                                this.state.failed = true;
                                return retval;
                            }
                            MismatchedSetException mse =
                                new MismatchedSetException(null, this.input);
                            throw mse;
                        }

                    }
                        break;

                    default:
                        break loop15;
                    }
                } while (true);

            }

            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "text"

    public static class graphLabel_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "graphLabel"
    // Label0.g:177:1: graphLabel : graphDefault -> ^( ATOM ) ;
    public final Label0Parser.graphLabel_return graphLabel()
        throws RecognitionException {
        Label0Parser.graphLabel_return retval =
            new Label0Parser.graphLabel_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Label0Parser.graphDefault_return graphDefault70 = null;

        RewriteRuleSubtreeStream stream_graphDefault =
            new RewriteRuleSubtreeStream(this.adaptor, "rule graphDefault");
        try {
            // Label0.g:178:4: ( graphDefault -> ^( ATOM ) )
            // Label0.g:178:6: graphDefault
            {
                pushFollow(FOLLOW_graphDefault_in_graphLabel1170);
                graphDefault70 = graphDefault();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_graphDefault.add(graphDefault70.getTree());
                }

                // AST REWRITE
                // elements: 
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                if (this.state.backtracking == 0) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval =
                        new RewriteRuleSubtreeStream(this.adaptor,
                            "rule retval", retval != null ? retval.tree : null);

                    root_0 = (CommonTree) this.adaptor.nil();
                    // 178:19: -> ^( ATOM )
                    {
                        // Label0.g:178:22: ^( ATOM )
                        {
                            CommonTree root_1 = (CommonTree) this.adaptor.nil();
                            root_1 =
                                (CommonTree) this.adaptor.becomeRoot(
                                    (CommonTree) this.adaptor.create(ATOM,
                                        "ATOM"), root_1);

                            this.adaptor.addChild(root_1,
                                concat((graphDefault70 != null
                                        ? ((CommonTree) graphDefault70.tree)
                                        : null)));

                            this.adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                }
            }

            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "graphLabel"

    public static class graphDefault_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "graphDefault"
    // Label0.g:181:1: graphDefault : (~ COLON )+ ;
    public final Label0Parser.graphDefault_return graphDefault()
        throws RecognitionException {
        Label0Parser.graphDefault_return retval =
            new Label0Parser.graphDefault_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token set71 = null;

        CommonTree set71_tree = null;

        try {
            // Label0.g:182:4: ( (~ COLON )+ )
            // Label0.g:182:6: (~ COLON )+
            {
                root_0 = (CommonTree) this.adaptor.nil();

                // Label0.g:182:6: (~ COLON )+
                int cnt16 = 0;
                loop16: do {
                    int alt16 = 2;
                    int LA16_0 = this.input.LA(1);

                    if (((LA16_0 >= NEW && LA16_0 <= QUERY) || (LA16_0 >= COMMA && LA16_0 <= 58))) {
                        alt16 = 1;
                    }

                    switch (alt16) {
                    case 1:
                        // Label0.g:182:7: ~ COLON
                    {
                        set71 = (Token) this.input.LT(1);
                        if ((this.input.LA(1) >= NEW && this.input.LA(1) <= QUERY)
                            || (this.input.LA(1) >= COMMA && this.input.LA(1) <= 58)) {
                            this.input.consume();
                            if (this.state.backtracking == 0) {
                                this.adaptor.addChild(root_0,
                                    (CommonTree) this.adaptor.create(set71));
                            }
                            this.state.errorRecovery = false;
                            this.state.failed = false;
                        } else {
                            if (this.state.backtracking > 0) {
                                this.state.failed = true;
                                return retval;
                            }
                            MismatchedSetException mse =
                                new MismatchedSetException(null, this.input);
                            throw mse;
                        }

                    }
                        break;

                    default:
                        if (cnt16 >= 1) {
                            break loop16;
                        }
                        if (this.state.backtracking > 0) {
                            this.state.failed = true;
                            return retval;
                        }
                        EarlyExitException eee =
                            new EarlyExitException(16, this.input);
                        throw eee;
                    }
                    cnt16++;
                } while (true);

            }

            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "graphDefault"

    public static class ruleLabel_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "ruleLabel"
    // Label0.g:185:1: ruleLabel : ( PLING ( simpleRuleLabel | LBRACE regExpr RBRACE ) | simpleRuleLabel | LBRACE ( PLING regExpr | regExpr ) RBRACE );
    public final Label0Parser.ruleLabel_return ruleLabel()
        throws RecognitionException {
        Label0Parser.ruleLabel_return retval =
            new Label0Parser.ruleLabel_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token PLING72 = null;
        Token LBRACE74 = null;
        Token RBRACE76 = null;
        Token LBRACE78 = null;
        Token PLING79 = null;
        Token RBRACE82 = null;
        Label0Parser.simpleRuleLabel_return simpleRuleLabel73 = null;

        Label0Parser.regExpr_return regExpr75 = null;

        Label0Parser.simpleRuleLabel_return simpleRuleLabel77 = null;

        Label0Parser.regExpr_return regExpr80 = null;

        Label0Parser.regExpr_return regExpr81 = null;

        CommonTree PLING72_tree = null;
        CommonTree LBRACE74_tree = null;
        CommonTree RBRACE76_tree = null;
        CommonTree LBRACE78_tree = null;
        CommonTree PLING79_tree = null;
        CommonTree RBRACE82_tree = null;

        try {
            // Label0.g:186:4: ( PLING ( simpleRuleLabel | LBRACE regExpr RBRACE ) | simpleRuleLabel | LBRACE ( PLING regExpr | regExpr ) RBRACE )
            int alt19 = 3;
            switch (this.input.LA(1)) {
            case PLING: {
                alt19 = 1;
            }
                break;
            case NEW:
            case DEL:
            case NOT:
            case USE:
            case CNEW:
            case REM:
            case FORALL:
            case FORALLX:
            case EXISTS:
            case NESTED:
            case INT:
            case REAL:
            case STRING:
            case BOOL:
            case ATTR:
            case PROD:
            case ARG:
            case PAR:
            case TYPE:
            case FLAG:
            case PATH:
            case EMPTY:
            case ATOM:
            case TRUE:
            case FALSE:
            case CONSTRAINT:
            case MINUS:
            case STAR:
            case PLUS:
            case DOT:
            case BAR:
            case HAT:
            case EQUALS:
            case LPAR:
            case RPAR:
            case LSQUARE:
            case RSQUARE:
            case QUERY:
            case COMMA:
            case SQUOTE:
            case DQUOTE:
            case DOLLAR:
            case UNDER:
            case IDENT:
            case LABEL:
            case NUMBER:
            case LETTER:
            case IDENTCHAR:
            case DIGIT:
            case 58: {
                alt19 = 2;
            }
                break;
            case LBRACE: {
                alt19 = 3;
            }
                break;
            default:
                if (this.state.backtracking > 0) {
                    this.state.failed = true;
                    return retval;
                }
                NoViableAltException nvae =
                    new NoViableAltException("", 19, 0, this.input);

                throw nvae;
            }

            switch (alt19) {
            case 1:
                // Label0.g:186:6: PLING ( simpleRuleLabel | LBRACE regExpr RBRACE )
            {
                root_0 = (CommonTree) this.adaptor.nil();

                PLING72 =
                    (Token) match(this.input, PLING,
                        FOLLOW_PLING_in_ruleLabel1212);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    PLING72_tree = (CommonTree) this.adaptor.create(PLING72);
                    root_0 =
                        (CommonTree) this.adaptor.becomeRoot(PLING72_tree,
                            root_0);
                }
                // Label0.g:187:6: ( simpleRuleLabel | LBRACE regExpr RBRACE )
                int alt17 = 2;
                int LA17_0 = this.input.LA(1);

                if (((LA17_0 >= NEW && LA17_0 <= EQUALS)
                    || (LA17_0 >= LPAR && LA17_0 <= RSQUARE) || LA17_0 == QUERY
                    || (LA17_0 >= COMMA && LA17_0 <= UNDER) || (LA17_0 >= IDENT && LA17_0 <= 58))) {
                    alt17 = 1;
                } else if ((LA17_0 == LBRACE)) {
                    alt17 = 2;
                } else {
                    if (this.state.backtracking > 0) {
                        this.state.failed = true;
                        return retval;
                    }
                    NoViableAltException nvae =
                        new NoViableAltException("", 17, 0, this.input);

                    throw nvae;
                }
                switch (alt17) {
                case 1:
                    // Label0.g:187:8: simpleRuleLabel
                {
                    pushFollow(FOLLOW_simpleRuleLabel_in_ruleLabel1222);
                    simpleRuleLabel73 = simpleRuleLabel();

                    this.state._fsp--;
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        this.adaptor.addChild(root_0,
                            simpleRuleLabel73.getTree());
                    }

                }
                    break;
                case 2:
                    // Label0.g:188:8: LBRACE regExpr RBRACE
                {
                    LBRACE74 =
                        (Token) match(this.input, LBRACE,
                            FOLLOW_LBRACE_in_ruleLabel1231);
                    if (this.state.failed) {
                        return retval;
                    }
                    pushFollow(FOLLOW_regExpr_in_ruleLabel1234);
                    regExpr75 = regExpr();

                    this.state._fsp--;
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        this.adaptor.addChild(root_0, regExpr75.getTree());
                    }
                    RBRACE76 =
                        (Token) match(this.input, RBRACE,
                            FOLLOW_RBRACE_in_ruleLabel1236);
                    if (this.state.failed) {
                        return retval;
                    }

                }
                    break;

                }

            }
                break;
            case 2:
                // Label0.g:190:6: simpleRuleLabel
            {
                root_0 = (CommonTree) this.adaptor.nil();

                pushFollow(FOLLOW_simpleRuleLabel_in_ruleLabel1251);
                simpleRuleLabel77 = simpleRuleLabel();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, simpleRuleLabel77.getTree());
                }

            }
                break;
            case 3:
                // Label0.g:191:6: LBRACE ( PLING regExpr | regExpr ) RBRACE
            {
                root_0 = (CommonTree) this.adaptor.nil();

                LBRACE78 =
                    (Token) match(this.input, LBRACE,
                        FOLLOW_LBRACE_in_ruleLabel1258);
                if (this.state.failed) {
                    return retval;
                }
                // Label0.g:192:6: ( PLING regExpr | regExpr )
                int alt18 = 2;
                int LA18_0 = this.input.LA(1);

                if ((LA18_0 == PLING)) {
                    alt18 = 1;
                } else if ((LA18_0 == MINUS || LA18_0 == EQUALS
                    || LA18_0 == LPAR || LA18_0 == QUERY || LA18_0 == SQUOTE || (LA18_0 >= IDENT && LA18_0 <= NUMBER))) {
                    alt18 = 2;
                } else {
                    if (this.state.backtracking > 0) {
                        this.state.failed = true;
                        return retval;
                    }
                    NoViableAltException nvae =
                        new NoViableAltException("", 18, 0, this.input);

                    throw nvae;
                }
                switch (alt18) {
                case 1:
                    // Label0.g:192:8: PLING regExpr
                {
                    PLING79 =
                        (Token) match(this.input, PLING,
                            FOLLOW_PLING_in_ruleLabel1268);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        PLING79_tree =
                            (CommonTree) this.adaptor.create(PLING79);
                        root_0 =
                            (CommonTree) this.adaptor.becomeRoot(PLING79_tree,
                                root_0);
                    }
                    pushFollow(FOLLOW_regExpr_in_ruleLabel1271);
                    regExpr80 = regExpr();

                    this.state._fsp--;
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        this.adaptor.addChild(root_0, regExpr80.getTree());
                    }

                }
                    break;
                case 2:
                    // Label0.g:193:8: regExpr
                {
                    pushFollow(FOLLOW_regExpr_in_ruleLabel1280);
                    regExpr81 = regExpr();

                    this.state._fsp--;
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        this.adaptor.addChild(root_0, regExpr81.getTree());
                    }

                }
                    break;

                }

                RBRACE82 =
                    (Token) match(this.input, RBRACE,
                        FOLLOW_RBRACE_in_ruleLabel1294);
                if (this.state.failed) {
                    return retval;
                }

            }
                break;

            }
            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "ruleLabel"

    public static class simpleRuleLabel_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "simpleRuleLabel"
    // Label0.g:198:1: simpleRuleLabel : ( wildcard | EQUALS | sqText -> ^( ATOM sqText ) | ruleDefault -> ^( ATOM ) );
    public final Label0Parser.simpleRuleLabel_return simpleRuleLabel()
        throws RecognitionException {
        Label0Parser.simpleRuleLabel_return retval =
            new Label0Parser.simpleRuleLabel_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token EQUALS84 = null;
        Label0Parser.wildcard_return wildcard83 = null;

        Label0Parser.sqText_return sqText85 = null;

        Label0Parser.ruleDefault_return ruleDefault86 = null;

        CommonTree EQUALS84_tree = null;
        RewriteRuleSubtreeStream stream_ruleDefault =
            new RewriteRuleSubtreeStream(this.adaptor, "rule ruleDefault");
        RewriteRuleSubtreeStream stream_sqText =
            new RewriteRuleSubtreeStream(this.adaptor, "rule sqText");
        try {
            // Label0.g:199:4: ( wildcard | EQUALS | sqText -> ^( ATOM sqText ) | ruleDefault -> ^( ATOM ) )
            int alt20 = 4;
            switch (this.input.LA(1)) {
            case QUERY: {
                alt20 = 1;
            }
                break;
            case EQUALS: {
                alt20 = 2;
            }
                break;
            case SQUOTE: {
                alt20 = 3;
            }
                break;
            case NEW:
            case DEL:
            case NOT:
            case USE:
            case CNEW:
            case REM:
            case FORALL:
            case FORALLX:
            case EXISTS:
            case NESTED:
            case INT:
            case REAL:
            case STRING:
            case BOOL:
            case ATTR:
            case PROD:
            case ARG:
            case PAR:
            case TYPE:
            case FLAG:
            case PATH:
            case EMPTY:
            case ATOM:
            case TRUE:
            case FALSE:
            case CONSTRAINT:
            case MINUS:
            case STAR:
            case PLUS:
            case DOT:
            case BAR:
            case HAT:
            case LPAR:
            case RPAR:
            case LSQUARE:
            case RSQUARE:
            case COMMA:
            case DQUOTE:
            case DOLLAR:
            case UNDER:
            case IDENT:
            case LABEL:
            case NUMBER:
            case LETTER:
            case IDENTCHAR:
            case DIGIT:
            case 58: {
                alt20 = 4;
            }
                break;
            default:
                if (this.state.backtracking > 0) {
                    this.state.failed = true;
                    return retval;
                }
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, this.input);

                throw nvae;
            }

            switch (alt20) {
            case 1:
                // Label0.g:199:6: wildcard
            {
                root_0 = (CommonTree) this.adaptor.nil();

                pushFollow(FOLLOW_wildcard_in_simpleRuleLabel1310);
                wildcard83 = wildcard();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, wildcard83.getTree());
                }

            }
                break;
            case 2:
                // Label0.g:200:6: EQUALS
            {
                root_0 = (CommonTree) this.adaptor.nil();

                EQUALS84 =
                    (Token) match(this.input, EQUALS,
                        FOLLOW_EQUALS_in_simpleRuleLabel1317);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    EQUALS84_tree = (CommonTree) this.adaptor.create(EQUALS84);
                    this.adaptor.addChild(root_0, EQUALS84_tree);
                }

            }
                break;
            case 3:
                // Label0.g:201:6: sqText
            {
                pushFollow(FOLLOW_sqText_in_simpleRuleLabel1324);
                sqText85 = sqText();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_sqText.add(sqText85.getTree());
                }

                // AST REWRITE
                // elements: sqText
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                if (this.state.backtracking == 0) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval =
                        new RewriteRuleSubtreeStream(this.adaptor,
                            "rule retval", retval != null ? retval.tree : null);

                    root_0 = (CommonTree) this.adaptor.nil();
                    // 201:13: -> ^( ATOM sqText )
                    {
                        // Label0.g:201:16: ^( ATOM sqText )
                        {
                            CommonTree root_1 = (CommonTree) this.adaptor.nil();
                            root_1 =
                                (CommonTree) this.adaptor.becomeRoot(
                                    (CommonTree) this.adaptor.create(ATOM,
                                        "ATOM"), root_1);

                            this.adaptor.addChild(root_1,
                                stream_sqText.nextTree());

                            this.adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                }
            }
                break;
            case 4:
                // Label0.g:202:6: ruleDefault
            {
                pushFollow(FOLLOW_ruleDefault_in_simpleRuleLabel1339);
                ruleDefault86 = ruleDefault();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_ruleDefault.add(ruleDefault86.getTree());
                }

                // AST REWRITE
                // elements: 
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                if (this.state.backtracking == 0) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval =
                        new RewriteRuleSubtreeStream(this.adaptor,
                            "rule retval", retval != null ? retval.tree : null);

                    root_0 = (CommonTree) this.adaptor.nil();
                    // 202:18: -> ^( ATOM )
                    {
                        // Label0.g:202:21: ^( ATOM )
                        {
                            CommonTree root_1 = (CommonTree) this.adaptor.nil();
                            root_1 =
                                (CommonTree) this.adaptor.becomeRoot(
                                    (CommonTree) this.adaptor.create(ATOM,
                                        "ATOM"), root_1);

                            this.adaptor.addChild(root_1,
                                concat((ruleDefault86 != null
                                        ? ((CommonTree) ruleDefault86.tree)
                                        : null)));

                            this.adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                }
            }
                break;

            }
            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "simpleRuleLabel"

    public static class ruleDefault_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "ruleDefault"
    // Label0.g:205:1: ruleDefault : ~ ( EQUALS | QUERY | PLING | SQUOTE | LBRACE | RBRACE | BSLASH | COLON ) (~ ( SQUOTE | LBRACE | RBRACE | BSLASH | COLON ) )* ;
    public final Label0Parser.ruleDefault_return ruleDefault()
        throws RecognitionException {
        Label0Parser.ruleDefault_return retval =
            new Label0Parser.ruleDefault_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token set87 = null;
        Token set88 = null;

        CommonTree set87_tree = null;
        CommonTree set88_tree = null;

        try {
            // Label0.g:206:4: (~ ( EQUALS | QUERY | PLING | SQUOTE | LBRACE | RBRACE | BSLASH | COLON ) (~ ( SQUOTE | LBRACE | RBRACE | BSLASH | COLON ) )* )
            // Label0.g:206:6: ~ ( EQUALS | QUERY | PLING | SQUOTE | LBRACE | RBRACE | BSLASH | COLON ) (~ ( SQUOTE | LBRACE | RBRACE | BSLASH | COLON ) )*
            {
                root_0 = (CommonTree) this.adaptor.nil();

                set87 = (Token) this.input.LT(1);
                if ((this.input.LA(1) >= NEW && this.input.LA(1) <= HAT)
                    || (this.input.LA(1) >= LPAR && this.input.LA(1) <= RSQUARE)
                    || this.input.LA(1) == COMMA
                    || (this.input.LA(1) >= DQUOTE && this.input.LA(1) <= UNDER)
                    || (this.input.LA(1) >= IDENT && this.input.LA(1) <= 58)) {
                    this.input.consume();
                    if (this.state.backtracking == 0) {
                        this.adaptor.addChild(root_0,
                            (CommonTree) this.adaptor.create(set87));
                    }
                    this.state.errorRecovery = false;
                    this.state.failed = false;
                } else {
                    if (this.state.backtracking > 0) {
                        this.state.failed = true;
                        return retval;
                    }
                    MismatchedSetException mse =
                        new MismatchedSetException(null, this.input);
                    throw mse;
                }

                // Label0.g:207:6: (~ ( SQUOTE | LBRACE | RBRACE | BSLASH | COLON ) )*
                loop21: do {
                    int alt21 = 2;
                    int LA21_0 = this.input.LA(1);

                    if (((LA21_0 >= NEW && LA21_0 <= EQUALS)
                        || (LA21_0 >= LPAR && LA21_0 <= QUERY)
                        || LA21_0 == COMMA
                        || (LA21_0 >= DQUOTE && LA21_0 <= UNDER) || (LA21_0 >= IDENT && LA21_0 <= 58))) {
                        alt21 = 1;
                    }

                    switch (alt21) {
                    case 1:
                        // Label0.g:207:6: ~ ( SQUOTE | LBRACE | RBRACE | BSLASH | COLON )
                    {
                        set88 = (Token) this.input.LT(1);
                        if ((this.input.LA(1) >= NEW && this.input.LA(1) <= EQUALS)
                            || (this.input.LA(1) >= LPAR && this.input.LA(1) <= QUERY)
                            || this.input.LA(1) == COMMA
                            || (this.input.LA(1) >= DQUOTE && this.input.LA(1) <= UNDER)
                            || (this.input.LA(1) >= IDENT && this.input.LA(1) <= 58)) {
                            this.input.consume();
                            if (this.state.backtracking == 0) {
                                this.adaptor.addChild(root_0,
                                    (CommonTree) this.adaptor.create(set88));
                            }
                            this.state.errorRecovery = false;
                            this.state.failed = false;
                        } else {
                            if (this.state.backtracking > 0) {
                                this.state.failed = true;
                                return retval;
                            }
                            MismatchedSetException mse =
                                new MismatchedSetException(null, this.input);
                            throw mse;
                        }

                    }
                        break;

                    default:
                        break loop21;
                    }
                } while (true);

            }

            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "ruleDefault"

    public static class rnumber_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "rnumber"
    // Label0.g:210:1: rnumber : ( NUMBER ( DOT ( NUMBER )? )? | DOT NUMBER );
    public final Label0Parser.rnumber_return rnumber()
        throws RecognitionException {
        Label0Parser.rnumber_return retval = new Label0Parser.rnumber_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token NUMBER89 = null;
        Token DOT90 = null;
        Token NUMBER91 = null;
        Token DOT92 = null;
        Token NUMBER93 = null;

        CommonTree NUMBER89_tree = null;
        CommonTree DOT90_tree = null;
        CommonTree NUMBER91_tree = null;
        CommonTree DOT92_tree = null;
        CommonTree NUMBER93_tree = null;

        try {
            // Label0.g:211:4: ( NUMBER ( DOT ( NUMBER )? )? | DOT NUMBER )
            int alt24 = 2;
            int LA24_0 = this.input.LA(1);

            if ((LA24_0 == NUMBER)) {
                alt24 = 1;
            } else if ((LA24_0 == DOT)) {
                alt24 = 2;
            } else {
                if (this.state.backtracking > 0) {
                    this.state.failed = true;
                    return retval;
                }
                NoViableAltException nvae =
                    new NoViableAltException("", 24, 0, this.input);

                throw nvae;
            }
            switch (alt24) {
            case 1:
                // Label0.g:211:6: NUMBER ( DOT ( NUMBER )? )?
            {
                root_0 = (CommonTree) this.adaptor.nil();

                NUMBER89 =
                    (Token) match(this.input, NUMBER,
                        FOLLOW_NUMBER_in_rnumber1437);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    NUMBER89_tree = (CommonTree) this.adaptor.create(NUMBER89);
                    this.adaptor.addChild(root_0, NUMBER89_tree);
                }
                // Label0.g:211:13: ( DOT ( NUMBER )? )?
                int alt23 = 2;
                int LA23_0 = this.input.LA(1);

                if ((LA23_0 == DOT)) {
                    alt23 = 1;
                }
                switch (alt23) {
                case 1:
                    // Label0.g:211:14: DOT ( NUMBER )?
                {
                    DOT90 =
                        (Token) match(this.input, DOT,
                            FOLLOW_DOT_in_rnumber1440);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        DOT90_tree = (CommonTree) this.adaptor.create(DOT90);
                        this.adaptor.addChild(root_0, DOT90_tree);
                    }
                    // Label0.g:211:18: ( NUMBER )?
                    int alt22 = 2;
                    int LA22_0 = this.input.LA(1);

                    if ((LA22_0 == NUMBER)) {
                        alt22 = 1;
                    }
                    switch (alt22) {
                    case 1:
                        // Label0.g:211:18: NUMBER
                    {
                        NUMBER91 =
                            (Token) match(this.input, NUMBER,
                                FOLLOW_NUMBER_in_rnumber1442);
                        if (this.state.failed) {
                            return retval;
                        }
                        if (this.state.backtracking == 0) {
                            NUMBER91_tree =
                                (CommonTree) this.adaptor.create(NUMBER91);
                            this.adaptor.addChild(root_0, NUMBER91_tree);
                        }

                    }
                        break;

                    }

                }
                    break;

                }

            }
                break;
            case 2:
                // Label0.g:212:6: DOT NUMBER
            {
                root_0 = (CommonTree) this.adaptor.nil();

                DOT92 =
                    (Token) match(this.input, DOT, FOLLOW_DOT_in_rnumber1452);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    DOT92_tree = (CommonTree) this.adaptor.create(DOT92);
                    this.adaptor.addChild(root_0, DOT92_tree);
                }
                NUMBER93 =
                    (Token) match(this.input, NUMBER,
                        FOLLOW_NUMBER_in_rnumber1454);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    NUMBER93_tree = (CommonTree) this.adaptor.create(NUMBER93);
                    this.adaptor.addChild(root_0, NUMBER93_tree);
                }

            }
                break;

            }
            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "rnumber"

    public static class regExpr_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "regExpr"
    // Label0.g:215:1: regExpr : choice ;
    public final Label0Parser.regExpr_return regExpr()
        throws RecognitionException {
        Label0Parser.regExpr_return retval = new Label0Parser.regExpr_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Label0Parser.choice_return choice94 = null;

        try {
            // Label0.g:216:4: ( choice )
            // Label0.g:216:6: choice
            {
                root_0 = (CommonTree) this.adaptor.nil();

                pushFollow(FOLLOW_choice_in_regExpr1469);
                choice94 = choice();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, choice94.getTree());
                }

            }

            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "regExpr"

    public static class choice_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "choice"
    // Label0.g:219:1: choice : sequence ( BAR choice )? ;
    public final Label0Parser.choice_return choice()
        throws RecognitionException {
        Label0Parser.choice_return retval = new Label0Parser.choice_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token BAR96 = null;
        Label0Parser.sequence_return sequence95 = null;

        Label0Parser.choice_return choice97 = null;

        CommonTree BAR96_tree = null;

        try {
            // Label0.g:220:4: ( sequence ( BAR choice )? )
            // Label0.g:220:6: sequence ( BAR choice )?
            {
                root_0 = (CommonTree) this.adaptor.nil();

                pushFollow(FOLLOW_sequence_in_choice1484);
                sequence95 = sequence();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, sequence95.getTree());
                }
                // Label0.g:220:15: ( BAR choice )?
                int alt25 = 2;
                int LA25_0 = this.input.LA(1);

                if ((LA25_0 == BAR)) {
                    alt25 = 1;
                }
                switch (alt25) {
                case 1:
                    // Label0.g:220:16: BAR choice
                {
                    BAR96 =
                        (Token) match(this.input, BAR, FOLLOW_BAR_in_choice1487);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        BAR96_tree = (CommonTree) this.adaptor.create(BAR96);
                        root_0 =
                            (CommonTree) this.adaptor.becomeRoot(BAR96_tree,
                                root_0);
                    }
                    pushFollow(FOLLOW_choice_in_choice1490);
                    choice97 = choice();

                    this.state._fsp--;
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        this.adaptor.addChild(root_0, choice97.getTree());
                    }

                }
                    break;

                }

            }

            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "choice"

    public static class sequence_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "sequence"
    // Label0.g:222:1: sequence : unary ( DOT sequence )? ;
    public final Label0Parser.sequence_return sequence()
        throws RecognitionException {
        Label0Parser.sequence_return retval =
            new Label0Parser.sequence_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token DOT99 = null;
        Label0Parser.unary_return unary98 = null;

        Label0Parser.sequence_return sequence100 = null;

        CommonTree DOT99_tree = null;

        try {
            // Label0.g:223:4: ( unary ( DOT sequence )? )
            // Label0.g:223:6: unary ( DOT sequence )?
            {
                root_0 = (CommonTree) this.adaptor.nil();

                pushFollow(FOLLOW_unary_in_sequence1504);
                unary98 = unary();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, unary98.getTree());
                }
                // Label0.g:223:12: ( DOT sequence )?
                int alt26 = 2;
                int LA26_0 = this.input.LA(1);

                if ((LA26_0 == DOT)) {
                    alt26 = 1;
                }
                switch (alt26) {
                case 1:
                    // Label0.g:223:13: DOT sequence
                {
                    DOT99 =
                        (Token) match(this.input, DOT,
                            FOLLOW_DOT_in_sequence1507);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        DOT99_tree = (CommonTree) this.adaptor.create(DOT99);
                        root_0 =
                            (CommonTree) this.adaptor.becomeRoot(DOT99_tree,
                                root_0);
                    }
                    pushFollow(FOLLOW_sequence_in_sequence1510);
                    sequence100 = sequence();

                    this.state._fsp--;
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        this.adaptor.addChild(root_0, sequence100.getTree());
                    }

                }
                    break;

                }

            }

            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "sequence"

    public static class unary_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "unary"
    // Label0.g:225:1: unary : ( MINUS unary | atom ( STAR | PLUS )? | EQUALS | LPAR regExpr RPAR ( STAR | PLUS )? | wildcard ( STAR | PLUS )? );
    public final Label0Parser.unary_return unary() throws RecognitionException {
        Label0Parser.unary_return retval = new Label0Parser.unary_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token MINUS101 = null;
        Token STAR104 = null;
        Token PLUS105 = null;
        Token EQUALS106 = null;
        Token LPAR107 = null;
        Token RPAR109 = null;
        Token STAR110 = null;
        Token PLUS111 = null;
        Token STAR113 = null;
        Token PLUS114 = null;
        Label0Parser.unary_return unary102 = null;

        Label0Parser.atom_return atom103 = null;

        Label0Parser.regExpr_return regExpr108 = null;

        Label0Parser.wildcard_return wildcard112 = null;

        CommonTree MINUS101_tree = null;
        CommonTree STAR104_tree = null;
        CommonTree PLUS105_tree = null;
        CommonTree EQUALS106_tree = null;
        CommonTree LPAR107_tree = null;
        CommonTree RPAR109_tree = null;
        CommonTree STAR110_tree = null;
        CommonTree PLUS111_tree = null;
        CommonTree STAR113_tree = null;
        CommonTree PLUS114_tree = null;

        try {
            // Label0.g:226:4: ( MINUS unary | atom ( STAR | PLUS )? | EQUALS | LPAR regExpr RPAR ( STAR | PLUS )? | wildcard ( STAR | PLUS )? )
            int alt30 = 5;
            switch (this.input.LA(1)) {
            case MINUS: {
                alt30 = 1;
            }
                break;
            case SQUOTE:
            case IDENT:
            case LABEL:
            case NUMBER: {
                alt30 = 2;
            }
                break;
            case EQUALS: {
                alt30 = 3;
            }
                break;
            case LPAR: {
                alt30 = 4;
            }
                break;
            case QUERY: {
                alt30 = 5;
            }
                break;
            default:
                if (this.state.backtracking > 0) {
                    this.state.failed = true;
                    return retval;
                }
                NoViableAltException nvae =
                    new NoViableAltException("", 30, 0, this.input);

                throw nvae;
            }

            switch (alt30) {
            case 1:
                // Label0.g:226:6: MINUS unary
            {
                root_0 = (CommonTree) this.adaptor.nil();

                MINUS101 =
                    (Token) match(this.input, MINUS, FOLLOW_MINUS_in_unary1524);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    MINUS101_tree = (CommonTree) this.adaptor.create(MINUS101);
                    root_0 =
                        (CommonTree) this.adaptor.becomeRoot(MINUS101_tree,
                            root_0);
                }
                pushFollow(FOLLOW_unary_in_unary1527);
                unary102 = unary();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, unary102.getTree());
                }

            }
                break;
            case 2:
                // Label0.g:227:6: atom ( STAR | PLUS )?
            {
                root_0 = (CommonTree) this.adaptor.nil();

                pushFollow(FOLLOW_atom_in_unary1534);
                atom103 = atom();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, atom103.getTree());
                }
                // Label0.g:227:11: ( STAR | PLUS )?
                int alt27 = 3;
                int LA27_0 = this.input.LA(1);

                if ((LA27_0 == STAR)) {
                    alt27 = 1;
                } else if ((LA27_0 == PLUS)) {
                    alt27 = 2;
                }
                switch (alt27) {
                case 1:
                    // Label0.g:227:12: STAR
                {
                    STAR104 =
                        (Token) match(this.input, STAR,
                            FOLLOW_STAR_in_unary1537);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        STAR104_tree =
                            (CommonTree) this.adaptor.create(STAR104);
                        root_0 =
                            (CommonTree) this.adaptor.becomeRoot(STAR104_tree,
                                root_0);
                    }

                }
                    break;
                case 2:
                    // Label0.g:227:20: PLUS
                {
                    PLUS105 =
                        (Token) match(this.input, PLUS,
                            FOLLOW_PLUS_in_unary1542);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        PLUS105_tree =
                            (CommonTree) this.adaptor.create(PLUS105);
                        root_0 =
                            (CommonTree) this.adaptor.becomeRoot(PLUS105_tree,
                                root_0);
                    }

                }
                    break;

                }

            }
                break;
            case 3:
                // Label0.g:228:6: EQUALS
            {
                root_0 = (CommonTree) this.adaptor.nil();

                EQUALS106 =
                    (Token) match(this.input, EQUALS,
                        FOLLOW_EQUALS_in_unary1552);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    EQUALS106_tree =
                        (CommonTree) this.adaptor.create(EQUALS106);
                    this.adaptor.addChild(root_0, EQUALS106_tree);
                }

            }
                break;
            case 4:
                // Label0.g:229:6: LPAR regExpr RPAR ( STAR | PLUS )?
            {
                root_0 = (CommonTree) this.adaptor.nil();

                LPAR107 =
                    (Token) match(this.input, LPAR, FOLLOW_LPAR_in_unary1559);
                if (this.state.failed) {
                    return retval;
                }
                pushFollow(FOLLOW_regExpr_in_unary1562);
                regExpr108 = regExpr();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, regExpr108.getTree());
                }
                RPAR109 =
                    (Token) match(this.input, RPAR, FOLLOW_RPAR_in_unary1564);
                if (this.state.failed) {
                    return retval;
                }
                // Label0.g:229:26: ( STAR | PLUS )?
                int alt28 = 3;
                int LA28_0 = this.input.LA(1);

                if ((LA28_0 == STAR)) {
                    alt28 = 1;
                } else if ((LA28_0 == PLUS)) {
                    alt28 = 2;
                }
                switch (alt28) {
                case 1:
                    // Label0.g:229:27: STAR
                {
                    STAR110 =
                        (Token) match(this.input, STAR,
                            FOLLOW_STAR_in_unary1568);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        STAR110_tree =
                            (CommonTree) this.adaptor.create(STAR110);
                        root_0 =
                            (CommonTree) this.adaptor.becomeRoot(STAR110_tree,
                                root_0);
                    }

                }
                    break;
                case 2:
                    // Label0.g:229:35: PLUS
                {
                    PLUS111 =
                        (Token) match(this.input, PLUS,
                            FOLLOW_PLUS_in_unary1573);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        PLUS111_tree =
                            (CommonTree) this.adaptor.create(PLUS111);
                        root_0 =
                            (CommonTree) this.adaptor.becomeRoot(PLUS111_tree,
                                root_0);
                    }

                }
                    break;

                }

            }
                break;
            case 5:
                // Label0.g:230:6: wildcard ( STAR | PLUS )?
            {
                root_0 = (CommonTree) this.adaptor.nil();

                pushFollow(FOLLOW_wildcard_in_unary1583);
                wildcard112 = wildcard();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, wildcard112.getTree());
                }
                // Label0.g:230:15: ( STAR | PLUS )?
                int alt29 = 3;
                int LA29_0 = this.input.LA(1);

                if ((LA29_0 == STAR)) {
                    alt29 = 1;
                } else if ((LA29_0 == PLUS)) {
                    alt29 = 2;
                }
                switch (alt29) {
                case 1:
                    // Label0.g:230:16: STAR
                {
                    STAR113 =
                        (Token) match(this.input, STAR,
                            FOLLOW_STAR_in_unary1586);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        STAR113_tree =
                            (CommonTree) this.adaptor.create(STAR113);
                        root_0 =
                            (CommonTree) this.adaptor.becomeRoot(STAR113_tree,
                                root_0);
                    }

                }
                    break;
                case 2:
                    // Label0.g:230:24: PLUS
                {
                    PLUS114 =
                        (Token) match(this.input, PLUS,
                            FOLLOW_PLUS_in_unary1591);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        PLUS114_tree =
                            (CommonTree) this.adaptor.create(PLUS114);
                        root_0 =
                            (CommonTree) this.adaptor.becomeRoot(PLUS114_tree,
                                root_0);
                    }

                }
                    break;

                }

            }
                break;

            }
            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "unary"

    public static class atom_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "atom"
    // Label0.g:233:1: atom : ( sqText -> ^( ATOM sqText ) | atomLabel -> ^( ATOM atomLabel ) );
    public final Label0Parser.atom_return atom() throws RecognitionException {
        Label0Parser.atom_return retval = new Label0Parser.atom_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Label0Parser.sqText_return sqText115 = null;

        Label0Parser.atomLabel_return atomLabel116 = null;

        RewriteRuleSubtreeStream stream_sqText =
            new RewriteRuleSubtreeStream(this.adaptor, "rule sqText");
        RewriteRuleSubtreeStream stream_atomLabel =
            new RewriteRuleSubtreeStream(this.adaptor, "rule atomLabel");
        try {
            // Label0.g:234:4: ( sqText -> ^( ATOM sqText ) | atomLabel -> ^( ATOM atomLabel ) )
            int alt31 = 2;
            int LA31_0 = this.input.LA(1);

            if ((LA31_0 == SQUOTE)) {
                alt31 = 1;
            } else if (((LA31_0 >= IDENT && LA31_0 <= NUMBER))) {
                alt31 = 2;
            } else {
                if (this.state.backtracking > 0) {
                    this.state.failed = true;
                    return retval;
                }
                NoViableAltException nvae =
                    new NoViableAltException("", 31, 0, this.input);

                throw nvae;
            }
            switch (alt31) {
            case 1:
                // Label0.g:234:6: sqText
            {
                pushFollow(FOLLOW_sqText_in_atom1609);
                sqText115 = sqText();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_sqText.add(sqText115.getTree());
                }

                // AST REWRITE
                // elements: sqText
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                if (this.state.backtracking == 0) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval =
                        new RewriteRuleSubtreeStream(this.adaptor,
                            "rule retval", retval != null ? retval.tree : null);

                    root_0 = (CommonTree) this.adaptor.nil();
                    // 234:13: -> ^( ATOM sqText )
                    {
                        // Label0.g:234:16: ^( ATOM sqText )
                        {
                            CommonTree root_1 = (CommonTree) this.adaptor.nil();
                            root_1 =
                                (CommonTree) this.adaptor.becomeRoot(
                                    (CommonTree) this.adaptor.create(ATOM,
                                        "ATOM"), root_1);

                            this.adaptor.addChild(root_1,
                                stream_sqText.nextTree());

                            this.adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                }
            }
                break;
            case 2:
                // Label0.g:235:6: atomLabel
            {
                pushFollow(FOLLOW_atomLabel_in_atom1624);
                atomLabel116 = atomLabel();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_atomLabel.add(atomLabel116.getTree());
                }

                // AST REWRITE
                // elements: atomLabel
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                if (this.state.backtracking == 0) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval =
                        new RewriteRuleSubtreeStream(this.adaptor,
                            "rule retval", retval != null ? retval.tree : null);

                    root_0 = (CommonTree) this.adaptor.nil();
                    // 235:16: -> ^( ATOM atomLabel )
                    {
                        // Label0.g:235:19: ^( ATOM atomLabel )
                        {
                            CommonTree root_1 = (CommonTree) this.adaptor.nil();
                            root_1 =
                                (CommonTree) this.adaptor.becomeRoot(
                                    (CommonTree) this.adaptor.create(ATOM,
                                        "ATOM"), root_1);

                            this.adaptor.addChild(root_1,
                                stream_atomLabel.nextTree());

                            this.adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                }
            }
                break;

            }
            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "atom"

    public static class atomLabel_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "atomLabel"
    // Label0.g:238:1: atomLabel : ( NUMBER | IDENT | LABEL );
    public final Label0Parser.atomLabel_return atomLabel()
        throws RecognitionException {
        Label0Parser.atomLabel_return retval =
            new Label0Parser.atomLabel_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token set117 = null;

        CommonTree set117_tree = null;

        try {
            // Label0.g:239:4: ( NUMBER | IDENT | LABEL )
            // Label0.g:
            {
                root_0 = (CommonTree) this.adaptor.nil();

                set117 = (Token) this.input.LT(1);
                if ((this.input.LA(1) >= IDENT && this.input.LA(1) <= NUMBER)) {
                    this.input.consume();
                    if (this.state.backtracking == 0) {
                        this.adaptor.addChild(root_0,
                            (CommonTree) this.adaptor.create(set117));
                    }
                    this.state.errorRecovery = false;
                    this.state.failed = false;
                } else {
                    if (this.state.backtracking > 0) {
                        this.state.failed = true;
                        return retval;
                    }
                    MismatchedSetException mse =
                        new MismatchedSetException(null, this.input);
                    throw mse;
                }

            }

            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "atomLabel"

    public static class wildcard_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "wildcard"
    // Label0.g:242:1: wildcard : QUERY ( IDENT )? ( LSQUARE ( HAT )? atom ( COMMA atom )* RSQUARE )? ;
    public final Label0Parser.wildcard_return wildcard()
        throws RecognitionException {
        Label0Parser.wildcard_return retval =
            new Label0Parser.wildcard_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token QUERY118 = null;
        Token IDENT119 = null;
        Token LSQUARE120 = null;
        Token HAT121 = null;
        Token COMMA123 = null;
        Token RSQUARE125 = null;
        Label0Parser.atom_return atom122 = null;

        Label0Parser.atom_return atom124 = null;

        CommonTree QUERY118_tree = null;
        CommonTree IDENT119_tree = null;
        CommonTree LSQUARE120_tree = null;
        CommonTree HAT121_tree = null;
        CommonTree COMMA123_tree = null;
        CommonTree RSQUARE125_tree = null;

        try {
            // Label0.g:243:4: ( QUERY ( IDENT )? ( LSQUARE ( HAT )? atom ( COMMA atom )* RSQUARE )? )
            // Label0.g:243:6: QUERY ( IDENT )? ( LSQUARE ( HAT )? atom ( COMMA atom )* RSQUARE )?
            {
                root_0 = (CommonTree) this.adaptor.nil();

                QUERY118 =
                    (Token) match(this.input, QUERY,
                        FOLLOW_QUERY_in_wildcard1673);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    QUERY118_tree = (CommonTree) this.adaptor.create(QUERY118);
                    root_0 =
                        (CommonTree) this.adaptor.becomeRoot(QUERY118_tree,
                            root_0);
                }
                // Label0.g:243:13: ( IDENT )?
                int alt32 = 2;
                int LA32_0 = this.input.LA(1);

                if ((LA32_0 == IDENT)) {
                    alt32 = 1;
                }
                switch (alt32) {
                case 1:
                    // Label0.g:243:13: IDENT
                {
                    IDENT119 =
                        (Token) match(this.input, IDENT,
                            FOLLOW_IDENT_in_wildcard1676);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        IDENT119_tree =
                            (CommonTree) this.adaptor.create(IDENT119);
                        this.adaptor.addChild(root_0, IDENT119_tree);
                    }

                }
                    break;

                }

                // Label0.g:243:20: ( LSQUARE ( HAT )? atom ( COMMA atom )* RSQUARE )?
                int alt35 = 2;
                int LA35_0 = this.input.LA(1);

                if ((LA35_0 == LSQUARE)) {
                    alt35 = 1;
                }
                switch (alt35) {
                case 1:
                    // Label0.g:243:21: LSQUARE ( HAT )? atom ( COMMA atom )* RSQUARE
                {
                    LSQUARE120 =
                        (Token) match(this.input, LSQUARE,
                            FOLLOW_LSQUARE_in_wildcard1680);
                    if (this.state.failed) {
                        return retval;
                    }
                    // Label0.g:243:30: ( HAT )?
                    int alt33 = 2;
                    int LA33_0 = this.input.LA(1);

                    if ((LA33_0 == HAT)) {
                        alt33 = 1;
                    }
                    switch (alt33) {
                    case 1:
                        // Label0.g:243:30: HAT
                    {
                        HAT121 =
                            (Token) match(this.input, HAT,
                                FOLLOW_HAT_in_wildcard1683);
                        if (this.state.failed) {
                            return retval;
                        }
                        if (this.state.backtracking == 0) {
                            HAT121_tree =
                                (CommonTree) this.adaptor.create(HAT121);
                            this.adaptor.addChild(root_0, HAT121_tree);
                        }

                    }
                        break;

                    }

                    pushFollow(FOLLOW_atom_in_wildcard1686);
                    atom122 = atom();

                    this.state._fsp--;
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        this.adaptor.addChild(root_0, atom122.getTree());
                    }
                    // Label0.g:243:40: ( COMMA atom )*
                    loop34: do {
                        int alt34 = 2;
                        int LA34_0 = this.input.LA(1);

                        if ((LA34_0 == COMMA)) {
                            alt34 = 1;
                        }

                        switch (alt34) {
                        case 1:
                            // Label0.g:243:41: COMMA atom
                        {
                            COMMA123 =
                                (Token) match(this.input, COMMA,
                                    FOLLOW_COMMA_in_wildcard1689);
                            if (this.state.failed) {
                                return retval;
                            }
                            pushFollow(FOLLOW_atom_in_wildcard1692);
                            atom124 = atom();

                            this.state._fsp--;
                            if (this.state.failed) {
                                return retval;
                            }
                            if (this.state.backtracking == 0) {
                                this.adaptor.addChild(root_0, atom124.getTree());
                            }

                        }
                            break;

                        default:
                            break loop34;
                        }
                    } while (true);

                    RSQUARE125 =
                        (Token) match(this.input, RSQUARE,
                            FOLLOW_RSQUARE_in_wildcard1696);
                    if (this.state.failed) {
                        return retval;
                    }

                }
                    break;

                }

            }

            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "wildcard"

    public static class sqText_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "sqText"
    // Label0.g:246:1: sqText : SQUOTE sqContent SQUOTE ->;
    public final Label0Parser.sqText_return sqText()
        throws RecognitionException {
        Label0Parser.sqText_return retval = new Label0Parser.sqText_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token SQUOTE126 = null;
        Token SQUOTE128 = null;
        Label0Parser.sqContent_return sqContent127 = null;

        CommonTree SQUOTE126_tree = null;
        CommonTree SQUOTE128_tree = null;
        RewriteRuleTokenStream stream_SQUOTE =
            new RewriteRuleTokenStream(this.adaptor, "token SQUOTE");
        RewriteRuleSubtreeStream stream_sqContent =
            new RewriteRuleSubtreeStream(this.adaptor, "rule sqContent");
        try {
            // Label0.g:247:4: ( SQUOTE sqContent SQUOTE ->)
            // Label0.g:247:6: SQUOTE sqContent SQUOTE
            {
                SQUOTE126 =
                    (Token) match(this.input, SQUOTE,
                        FOLLOW_SQUOTE_in_sqText1714);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_SQUOTE.add(SQUOTE126);
                }

                pushFollow(FOLLOW_sqContent_in_sqText1716);
                sqContent127 = sqContent();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_sqContent.add(sqContent127.getTree());
                }
                SQUOTE128 =
                    (Token) match(this.input, SQUOTE,
                        FOLLOW_SQUOTE_in_sqText1718);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_SQUOTE.add(SQUOTE128);
                }

                // AST REWRITE
                // elements: 
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                if (this.state.backtracking == 0) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval =
                        new RewriteRuleSubtreeStream(this.adaptor,
                            "rule retval", retval != null ? retval.tree : null);

                    root_0 = (CommonTree) this.adaptor.nil();
                    // 247:30: ->
                    {
                        this.adaptor.addChild(root_0,
                            concat((sqContent127 != null
                                    ? ((CommonTree) sqContent127.tree) : null)));

                    }

                    retval.tree = root_0;
                }
            }

            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "sqText"

    public static class sqContent_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "sqContent"
    // Label0.g:250:1: sqContent : ( sqChar )* ;
    public final Label0Parser.sqContent_return sqContent()
        throws RecognitionException {
        Label0Parser.sqContent_return retval =
            new Label0Parser.sqContent_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Label0Parser.sqChar_return sqChar129 = null;

        try {
            // Label0.g:251:4: ( ( sqChar )* )
            // Label0.g:251:6: ( sqChar )*
            {
                root_0 = (CommonTree) this.adaptor.nil();

                // Label0.g:251:6: ( sqChar )*
                loop36: do {
                    int alt36 = 2;
                    int LA36_0 = this.input.LA(1);

                    if (((LA36_0 >= NEW && LA36_0 <= COMMA) || (LA36_0 >= DQUOTE && LA36_0 <= 58))) {
                        alt36 = 1;
                    }

                    switch (alt36) {
                    case 1:
                        // Label0.g:251:6: sqChar
                    {
                        pushFollow(FOLLOW_sqChar_in_sqContent1737);
                        sqChar129 = sqChar();

                        this.state._fsp--;
                        if (this.state.failed) {
                            return retval;
                        }
                        if (this.state.backtracking == 0) {
                            this.adaptor.addChild(root_0, sqChar129.getTree());
                        }

                    }
                        break;

                    default:
                        break loop36;
                    }
                } while (true);

            }

            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "sqContent"

    public static class sqChar_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "sqChar"
    // Label0.g:254:1: sqChar : (~ ( SQUOTE | BSLASH ) | BSLASH ( BSLASH | SQUOTE ) );
    public final Label0Parser.sqChar_return sqChar()
        throws RecognitionException {
        Label0Parser.sqChar_return retval = new Label0Parser.sqChar_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token set130 = null;
        Token BSLASH131 = null;
        Token set132 = null;

        CommonTree set130_tree = null;
        CommonTree BSLASH131_tree = null;
        CommonTree set132_tree = null;

        try {
            // Label0.g:255:4: (~ ( SQUOTE | BSLASH ) | BSLASH ( BSLASH | SQUOTE ) )
            int alt37 = 2;
            int LA37_0 = this.input.LA(1);

            if (((LA37_0 >= NEW && LA37_0 <= COMMA)
                || (LA37_0 >= DQUOTE && LA37_0 <= UNDER) || (LA37_0 >= IDENT && LA37_0 <= 58))) {
                alt37 = 1;
            } else if ((LA37_0 == BSLASH)) {
                alt37 = 2;
            } else {
                if (this.state.backtracking > 0) {
                    this.state.failed = true;
                    return retval;
                }
                NoViableAltException nvae =
                    new NoViableAltException("", 37, 0, this.input);

                throw nvae;
            }
            switch (alt37) {
            case 1:
                // Label0.g:255:6: ~ ( SQUOTE | BSLASH )
            {
                root_0 = (CommonTree) this.adaptor.nil();

                set130 = (Token) this.input.LT(1);
                if ((this.input.LA(1) >= NEW && this.input.LA(1) <= COMMA)
                    || (this.input.LA(1) >= DQUOTE && this.input.LA(1) <= UNDER)
                    || (this.input.LA(1) >= IDENT && this.input.LA(1) <= 58)) {
                    this.input.consume();
                    if (this.state.backtracking == 0) {
                        this.adaptor.addChild(root_0,
                            (CommonTree) this.adaptor.create(set130));
                    }
                    this.state.errorRecovery = false;
                    this.state.failed = false;
                } else {
                    if (this.state.backtracking > 0) {
                        this.state.failed = true;
                        return retval;
                    }
                    MismatchedSetException mse =
                        new MismatchedSetException(null, this.input);
                    throw mse;
                }

            }
                break;
            case 2:
                // Label0.g:256:6: BSLASH ( BSLASH | SQUOTE )
            {
                root_0 = (CommonTree) this.adaptor.nil();

                BSLASH131 =
                    (Token) match(this.input, BSLASH,
                        FOLLOW_BSLASH_in_sqChar1766);
                if (this.state.failed) {
                    return retval;
                }
                set132 = (Token) this.input.LT(1);
                if (this.input.LA(1) == SQUOTE || this.input.LA(1) == BSLASH) {
                    this.input.consume();
                    if (this.state.backtracking == 0) {
                        this.adaptor.addChild(root_0,
                            (CommonTree) this.adaptor.create(set132));
                    }
                    this.state.errorRecovery = false;
                    this.state.failed = false;
                } else {
                    if (this.state.backtracking > 0) {
                        this.state.failed = true;
                        return retval;
                    }
                    MismatchedSetException mse =
                        new MismatchedSetException(null, this.input);
                    throw mse;
                }

            }
                break;

            }
            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "sqChar"

    public static class dqText_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "dqText"
    // Label0.g:259:1: dqText : DQUOTE dqContent DQUOTE -> ^( DQUOTE ) ;
    public final Label0Parser.dqText_return dqText()
        throws RecognitionException {
        Label0Parser.dqText_return retval = new Label0Parser.dqText_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token DQUOTE133 = null;
        Token DQUOTE135 = null;
        Label0Parser.dqContent_return dqContent134 = null;

        CommonTree DQUOTE133_tree = null;
        CommonTree DQUOTE135_tree = null;
        RewriteRuleTokenStream stream_DQUOTE =
            new RewriteRuleTokenStream(this.adaptor, "token DQUOTE");
        RewriteRuleSubtreeStream stream_dqContent =
            new RewriteRuleSubtreeStream(this.adaptor, "rule dqContent");
        try {
            // Label0.g:260:4: ( DQUOTE dqContent DQUOTE -> ^( DQUOTE ) )
            // Label0.g:260:6: DQUOTE dqContent DQUOTE
            {
                DQUOTE133 =
                    (Token) match(this.input, DQUOTE,
                        FOLLOW_DQUOTE_in_dqText1788);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_DQUOTE.add(DQUOTE133);
                }

                pushFollow(FOLLOW_dqContent_in_dqText1790);
                dqContent134 = dqContent();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_dqContent.add(dqContent134.getTree());
                }
                DQUOTE135 =
                    (Token) match(this.input, DQUOTE,
                        FOLLOW_DQUOTE_in_dqText1792);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_DQUOTE.add(DQUOTE135);
                }

                // AST REWRITE
                // elements: DQUOTE
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                if (this.state.backtracking == 0) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval =
                        new RewriteRuleSubtreeStream(this.adaptor,
                            "rule retval", retval != null ? retval.tree : null);

                    root_0 = (CommonTree) this.adaptor.nil();
                    // 260:30: -> ^( DQUOTE )
                    {
                        // Label0.g:260:33: ^( DQUOTE )
                        {
                            CommonTree root_1 = (CommonTree) this.adaptor.nil();
                            root_1 =
                                (CommonTree) this.adaptor.becomeRoot(
                                    stream_DQUOTE.nextNode(), root_1);

                            this.adaptor.addChild(root_1,
                                concat((dqContent134 != null
                                        ? ((CommonTree) dqContent134.tree)
                                        : null)));

                            this.adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                }
            }

            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "dqText"

    public static class dqContent_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "dqContent"
    // Label0.g:263:1: dqContent : ( dqTextChar )* ;
    public final Label0Parser.dqContent_return dqContent()
        throws RecognitionException {
        Label0Parser.dqContent_return retval =
            new Label0Parser.dqContent_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Label0Parser.dqTextChar_return dqTextChar136 = null;

        try {
            // Label0.g:264:4: ( ( dqTextChar )* )
            // Label0.g:264:6: ( dqTextChar )*
            {
                root_0 = (CommonTree) this.adaptor.nil();

                // Label0.g:264:6: ( dqTextChar )*
                loop38: do {
                    int alt38 = 2;
                    int LA38_0 = this.input.LA(1);

                    if (((LA38_0 >= NEW && LA38_0 <= SQUOTE) || (LA38_0 >= DOLLAR && LA38_0 <= 58))) {
                        alt38 = 1;
                    }

                    switch (alt38) {
                    case 1:
                        // Label0.g:264:6: dqTextChar
                    {
                        pushFollow(FOLLOW_dqTextChar_in_dqContent1815);
                        dqTextChar136 = dqTextChar();

                        this.state._fsp--;
                        if (this.state.failed) {
                            return retval;
                        }
                        if (this.state.backtracking == 0) {
                            this.adaptor.addChild(root_0,
                                dqTextChar136.getTree());
                        }

                    }
                        break;

                    default:
                        break loop38;
                    }
                } while (true);

            }

            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "dqContent"

    public static class dqTextChar_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "dqTextChar"
    // Label0.g:267:1: dqTextChar : (~ ( DQUOTE | BSLASH ) | BSLASH ( BSLASH | DQUOTE ) );
    public final Label0Parser.dqTextChar_return dqTextChar()
        throws RecognitionException {
        Label0Parser.dqTextChar_return retval =
            new Label0Parser.dqTextChar_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token set137 = null;
        Token BSLASH138 = null;
        Token set139 = null;

        CommonTree set137_tree = null;
        CommonTree BSLASH138_tree = null;
        CommonTree set139_tree = null;

        try {
            // Label0.g:268:4: (~ ( DQUOTE | BSLASH ) | BSLASH ( BSLASH | DQUOTE ) )
            int alt39 = 2;
            int LA39_0 = this.input.LA(1);

            if (((LA39_0 >= NEW && LA39_0 <= SQUOTE)
                || (LA39_0 >= DOLLAR && LA39_0 <= UNDER) || (LA39_0 >= IDENT && LA39_0 <= 58))) {
                alt39 = 1;
            } else if ((LA39_0 == BSLASH)) {
                alt39 = 2;
            } else {
                if (this.state.backtracking > 0) {
                    this.state.failed = true;
                    return retval;
                }
                NoViableAltException nvae =
                    new NoViableAltException("", 39, 0, this.input);

                throw nvae;
            }
            switch (alt39) {
            case 1:
                // Label0.g:268:6: ~ ( DQUOTE | BSLASH )
            {
                root_0 = (CommonTree) this.adaptor.nil();

                set137 = (Token) this.input.LT(1);
                if ((this.input.LA(1) >= NEW && this.input.LA(1) <= SQUOTE)
                    || (this.input.LA(1) >= DOLLAR && this.input.LA(1) <= UNDER)
                    || (this.input.LA(1) >= IDENT && this.input.LA(1) <= 58)) {
                    this.input.consume();
                    if (this.state.backtracking == 0) {
                        this.adaptor.addChild(root_0,
                            (CommonTree) this.adaptor.create(set137));
                    }
                    this.state.errorRecovery = false;
                    this.state.failed = false;
                } else {
                    if (this.state.backtracking > 0) {
                        this.state.failed = true;
                        return retval;
                    }
                    MismatchedSetException mse =
                        new MismatchedSetException(null, this.input);
                    throw mse;
                }

            }
                break;
            case 2:
                // Label0.g:269:6: BSLASH ( BSLASH | DQUOTE )
            {
                root_0 = (CommonTree) this.adaptor.nil();

                BSLASH138 =
                    (Token) match(this.input, BSLASH,
                        FOLLOW_BSLASH_in_dqTextChar1843);
                if (this.state.failed) {
                    return retval;
                }
                set139 = (Token) this.input.LT(1);
                if (this.input.LA(1) == DQUOTE || this.input.LA(1) == BSLASH) {
                    this.input.consume();
                    if (this.state.backtracking == 0) {
                        this.adaptor.addChild(root_0,
                            (CommonTree) this.adaptor.create(set139));
                    }
                    this.state.errorRecovery = false;
                    this.state.failed = false;
                } else {
                    if (this.state.backtracking > 0) {
                        this.state.failed = true;
                        return retval;
                    }
                    MismatchedSetException mse =
                        new MismatchedSetException(null, this.input);
                    throw mse;
                }

            }
                break;

            }
            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "dqTextChar"

    // $ANTLR start synpred1_Label0
    public final void synpred1_Label0_fragment() throws RecognitionException {
        // Label0.g:169:6: ( graphDefault EOF )
        // Label0.g:169:7: graphDefault EOF
        {
            pushFollow(FOLLOW_graphDefault_in_synpred1_Label01106);
            graphDefault();

            this.state._fsp--;
            if (this.state.failed) {
                return;
            }
            match(this.input, EOF, FOLLOW_EOF_in_synpred1_Label01108);
            if (this.state.failed) {
                return;
            }

        }
    }

    // $ANTLR end synpred1_Label0

    // $ANTLR start synpred2_Label0
    public final void synpred2_Label0_fragment() throws RecognitionException {
        // Label0.g:170:6: ( ruleLabel EOF )
        // Label0.g:170:7: ruleLabel EOF
        {
            pushFollow(FOLLOW_ruleLabel_in_synpred2_Label01125);
            ruleLabel();

            this.state._fsp--;
            if (this.state.failed) {
                return;
            }
            match(this.input, EOF, FOLLOW_EOF_in_synpred2_Label01127);
            if (this.state.failed) {
                return;
            }

        }
    }

    // $ANTLR end synpred2_Label0

    // Delegated rules

    public final boolean synpred2_Label0() {
        this.state.backtracking++;
        int start = this.input.mark();
        try {
            synpred2_Label0_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: " + re);
        }
        boolean success = !this.state.failed;
        this.input.rewind(start);
        this.state.backtracking--;
        this.state.failed = false;
        return success;
    }

    public final boolean synpred1_Label0() {
        this.state.backtracking++;
        int start = this.input.mark();
        try {
            synpred1_Label0_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: " + re);
        }
        boolean success = !this.state.failed;
        this.input.rewind(start);
        this.state.backtracking--;
        this.state.failed = false;
        return success;
    }

    protected DFA1 dfa1 = new DFA1(this);
    protected DFA4 dfa4 = new DFA4(this);
    protected DFA2 dfa2 = new DFA2(this);
    protected DFA7 dfa7 = new DFA7(this);
    protected DFA5 dfa5 = new DFA5(this);
    protected DFA14 dfa14 = new DFA14(this);
    static final String DFA1_eotS = "\30\uffff";
    static final String DFA1_eofS = "\6\uffff\1\24\2\uffff\11\24\4\uffff\2\24";
    static final String DFA1_minS = "\1\4\5\uffff\1\4\2\uffff\11\4\4\uffff\2\4";
    static final String DFA1_maxS =
        "\1\72\5\uffff\1\72\2\uffff\11\72\4\uffff\2\72";
    static final String DFA1_acceptS =
        "\1\uffff\5\1\1\uffff\2\1\11\uffff\1\1\1\2\2\1\2\uffff";
    static final String DFA1_specialS =
        "\1\0\5\uffff\1\11\2\uffff\1\6\1\4\1\5\1\3\1\1\1\14\1\12\1\10\1"
            + "\7\4\uffff\1\13\1\2}>";
    static final String[] DFA1_transitionS = {
        "\5\1\1\6\3\1\1\12\1\13\1\14\1\15\1\16\1\17\1\20\1\21\1\11\3"
            + "\1\13\22\1\4\1\7\1\10\4\22\1\2\1\3\1\1\1\22\1\5\3\22\1\10\7"
            + "\22", "", "", "", "", "",
        "\41\25\2\10\6\25\1\23\1\25\1\10\3\25\1\10\7\25", "", "",
        "\40\25\1\26\2\10\6\25\1\23\1\25\1\10\3\25\1\10\7\25",
        "\41\25\2\10\6\25\1\23\1\25\1\10\3\25\1\10\7\25",
        "\41\25\2\10\6\25\1\23\1\25\1\10\3\25\1\10\7\25",
        "\41\25\2\10\6\25\1\23\1\25\1\10\3\25\1\10\7\25",
        "\41\25\2\10\6\25\1\23\1\25\1\10\3\25\1\10\7\25",
        "\41\25\2\10\6\25\1\23\1\25\1\10\3\25\1\10\7\25",
        "\41\25\2\10\6\25\1\23\1\25\1\10\3\25\1\10\7\25",
        "\41\25\2\10\6\25\1\23\1\25\1\10\3\25\1\10\7\25",
        "\41\25\2\10\6\25\1\23\1\25\1\10\3\25\1\10\7\25", "", "", "", "",
        "\41\25\2\10\6\25\1\uffff\1\25\1\10\3\25\1\10\1\25\1\27\5\25",
        "\41\25\2\10\6\25\1\23\1\25\1\10\3\25\1\10\7\25"};

    static final short[] DFA1_eot = DFA.unpackEncodedString(DFA1_eotS);
    static final short[] DFA1_eof = DFA.unpackEncodedString(DFA1_eofS);
    static final char[] DFA1_min =
        DFA.unpackEncodedStringToUnsignedChars(DFA1_minS);
    static final char[] DFA1_max =
        DFA.unpackEncodedStringToUnsignedChars(DFA1_maxS);
    static final short[] DFA1_accept = DFA.unpackEncodedString(DFA1_acceptS);
    static final short[] DFA1_special = DFA.unpackEncodedString(DFA1_specialS);
    static final short[][] DFA1_transition;

    static {
        int numStates = DFA1_transitionS.length;
        DFA1_transition = new short[numStates][];
        for (int i = 0; i < numStates; i++) {
            DFA1_transition[i] = DFA.unpackEncodedString(DFA1_transitionS[i]);
        }
    }

    class DFA1 extends DFA {

        public DFA1(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 1;
            this.eot = DFA1_eot;
            this.eof = DFA1_eof;
            this.min = DFA1_min;
            this.max = DFA1_max;
            this.accept = DFA1_accept;
            this.special = DFA1_special;
            this.transition = DFA1_transition;
        }

        public String getDescription() {
            return "109:1: label : ( quantLabel EOF | specialLabel EOF );";
        }

        public int specialStateTransition(int s, IntStream _input)
            throws NoViableAltException {
            TokenStream input = (TokenStream) _input;
            int _s = s;
            switch (s) {
            case 0:
                int LA1_0 = input.LA(1);

                int index1_0 = input.index();
                input.rewind();
                s = -1;
                if (((LA1_0 >= NEW && LA1_0 <= CNEW)
                    || (LA1_0 >= FORALL && LA1_0 <= EXISTS)
                    || (LA1_0 >= TYPE && LA1_0 <= PATH) || LA1_0 == COLON)) {
                    s = 1;
                }

                else if ((LA1_0 == PLING)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 2;
                }

                else if ((LA1_0 == QUERY)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 3;
                }

                else if ((LA1_0 == EQUALS)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 4;
                }

                else if ((LA1_0 == SQUOTE)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 5;
                }

                else if ((LA1_0 == REM)) {
                    s = 6;
                }

                else if ((LA1_0 == LBRACE)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 7;
                }

                else if ((LA1_0 == RBRACE || LA1_0 == BSLASH)
                    && ((Label0Parser.this.isGraph))) {
                    s = 8;
                }

                else if ((LA1_0 == PAR)) {
                    s = 9;
                }

                else if ((LA1_0 == NESTED)) {
                    s = 10;
                }

                else if ((LA1_0 == INT)) {
                    s = 11;
                }

                else if ((LA1_0 == REAL)) {
                    s = 12;
                }

                else if ((LA1_0 == STRING)) {
                    s = 13;
                }

                else if ((LA1_0 == BOOL)) {
                    s = 14;
                }

                else if ((LA1_0 == ATTR)) {
                    s = 15;
                }

                else if ((LA1_0 == PROD)) {
                    s = 16;
                }

                else if ((LA1_0 == ARG)) {
                    s = 17;
                }

                else if (((LA1_0 >= EMPTY && LA1_0 <= HAT)
                    || (LA1_0 >= LPAR && LA1_0 <= RSQUARE) || LA1_0 == COMMA
                    || (LA1_0 >= DQUOTE && LA1_0 <= UNDER) || (LA1_0 >= IDENT && LA1_0 <= 58))
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 18;
                }

                input.seek(index1_0);
                if (s >= 0) {
                    return s;
                }
                break;
            case 1:
                int LA1_13 = input.LA(1);

                int index1_13 = input.index();
                input.rewind();
                s = -1;
                if ((LA1_13 == COLON)) {
                    s = 19;
                }

                else if ((LA1_13 == EOF)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 20;
                }

                else if (((LA1_13 >= NEW && LA1_13 <= EQUALS)
                    || (LA1_13 >= LPAR && LA1_13 <= QUERY) || LA1_13 == COMMA
                    || (LA1_13 >= DQUOTE && LA1_13 <= UNDER) || (LA1_13 >= IDENT && LA1_13 <= 58))
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 21;
                }

                else if (((LA1_13 >= LBRACE && LA1_13 <= RBRACE)
                    || LA1_13 == SQUOTE || LA1_13 == BSLASH)
                    && ((Label0Parser.this.isGraph))) {
                    s = 8;
                }

                input.seek(index1_13);
                if (s >= 0) {
                    return s;
                }
                break;
            case 2:
                int LA1_23 = input.LA(1);

                int index1_23 = input.index();
                input.rewind();
                s = -1;
                if ((LA1_23 == EOF)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 20;
                }

                else if (((LA1_23 >= NEW && LA1_23 <= EQUALS)
                    || (LA1_23 >= LPAR && LA1_23 <= QUERY) || LA1_23 == COMMA
                    || (LA1_23 >= DQUOTE && LA1_23 <= UNDER) || (LA1_23 >= IDENT && LA1_23 <= 58))
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 21;
                }

                else if (((LA1_23 >= LBRACE && LA1_23 <= RBRACE)
                    || LA1_23 == SQUOTE || LA1_23 == BSLASH)
                    && ((Label0Parser.this.isGraph))) {
                    s = 8;
                }

                else if ((LA1_23 == COLON)) {
                    s = 19;
                }

                input.seek(index1_23);
                if (s >= 0) {
                    return s;
                }
                break;
            case 3:
                int LA1_12 = input.LA(1);

                int index1_12 = input.index();
                input.rewind();
                s = -1;
                if ((LA1_12 == COLON)) {
                    s = 19;
                }

                else if ((LA1_12 == EOF)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 20;
                }

                else if (((LA1_12 >= NEW && LA1_12 <= EQUALS)
                    || (LA1_12 >= LPAR && LA1_12 <= QUERY) || LA1_12 == COMMA
                    || (LA1_12 >= DQUOTE && LA1_12 <= UNDER) || (LA1_12 >= IDENT && LA1_12 <= 58))
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 21;
                }

                else if (((LA1_12 >= LBRACE && LA1_12 <= RBRACE)
                    || LA1_12 == SQUOTE || LA1_12 == BSLASH)
                    && ((Label0Parser.this.isGraph))) {
                    s = 8;
                }

                input.seek(index1_12);
                if (s >= 0) {
                    return s;
                }
                break;
            case 4:
                int LA1_10 = input.LA(1);

                int index1_10 = input.index();
                input.rewind();
                s = -1;
                if ((LA1_10 == COLON)) {
                    s = 19;
                }

                else if ((LA1_10 == EOF)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 20;
                }

                else if (((LA1_10 >= NEW && LA1_10 <= EQUALS)
                    || (LA1_10 >= LPAR && LA1_10 <= QUERY) || LA1_10 == COMMA
                    || (LA1_10 >= DQUOTE && LA1_10 <= UNDER) || (LA1_10 >= IDENT && LA1_10 <= 58))
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 21;
                }

                else if (((LA1_10 >= LBRACE && LA1_10 <= RBRACE)
                    || LA1_10 == SQUOTE || LA1_10 == BSLASH)
                    && ((Label0Parser.this.isGraph))) {
                    s = 8;
                }

                input.seek(index1_10);
                if (s >= 0) {
                    return s;
                }
                break;
            case 5:
                int LA1_11 = input.LA(1);

                int index1_11 = input.index();
                input.rewind();
                s = -1;
                if ((LA1_11 == COLON)) {
                    s = 19;
                }

                else if ((LA1_11 == EOF)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 20;
                }

                else if (((LA1_11 >= NEW && LA1_11 <= EQUALS)
                    || (LA1_11 >= LPAR && LA1_11 <= QUERY) || LA1_11 == COMMA
                    || (LA1_11 >= DQUOTE && LA1_11 <= UNDER) || (LA1_11 >= IDENT && LA1_11 <= 58))
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 21;
                }

                else if (((LA1_11 >= LBRACE && LA1_11 <= RBRACE)
                    || LA1_11 == SQUOTE || LA1_11 == BSLASH)
                    && ((Label0Parser.this.isGraph))) {
                    s = 8;
                }

                input.seek(index1_11);
                if (s >= 0) {
                    return s;
                }
                break;
            case 6:
                int LA1_9 = input.LA(1);

                int index1_9 = input.index();
                input.rewind();
                s = -1;
                if ((LA1_9 == EOF)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 20;
                }

                else if ((LA1_9 == EQUALS)) {
                    s = 22;
                }

                else if (((LA1_9 >= LBRACE && LA1_9 <= RBRACE)
                    || LA1_9 == SQUOTE || LA1_9 == BSLASH)
                    && ((Label0Parser.this.isGraph))) {
                    s = 8;
                }

                else if (((LA1_9 >= NEW && LA1_9 <= HAT)
                    || (LA1_9 >= LPAR && LA1_9 <= QUERY) || LA1_9 == COMMA
                    || (LA1_9 >= DQUOTE && LA1_9 <= UNDER) || (LA1_9 >= IDENT && LA1_9 <= 58))
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 21;
                }

                else if ((LA1_9 == COLON)) {
                    s = 19;
                }

                input.seek(index1_9);
                if (s >= 0) {
                    return s;
                }
                break;
            case 7:
                int LA1_17 = input.LA(1);

                int index1_17 = input.index();
                input.rewind();
                s = -1;
                if ((LA1_17 == COLON)) {
                    s = 19;
                }

                else if ((LA1_17 == EOF)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 20;
                }

                else if (((LA1_17 >= NEW && LA1_17 <= EQUALS)
                    || (LA1_17 >= LPAR && LA1_17 <= QUERY) || LA1_17 == COMMA
                    || (LA1_17 >= DQUOTE && LA1_17 <= UNDER) || (LA1_17 >= IDENT && LA1_17 <= 58))
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 21;
                }

                else if (((LA1_17 >= LBRACE && LA1_17 <= RBRACE)
                    || LA1_17 == SQUOTE || LA1_17 == BSLASH)
                    && ((Label0Parser.this.isGraph))) {
                    s = 8;
                }

                input.seek(index1_17);
                if (s >= 0) {
                    return s;
                }
                break;
            case 8:
                int LA1_16 = input.LA(1);

                int index1_16 = input.index();
                input.rewind();
                s = -1;
                if ((LA1_16 == COLON)) {
                    s = 19;
                }

                else if ((LA1_16 == EOF)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 20;
                }

                else if (((LA1_16 >= NEW && LA1_16 <= EQUALS)
                    || (LA1_16 >= LPAR && LA1_16 <= QUERY) || LA1_16 == COMMA
                    || (LA1_16 >= DQUOTE && LA1_16 <= UNDER) || (LA1_16 >= IDENT && LA1_16 <= 58))
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 21;
                }

                else if (((LA1_16 >= LBRACE && LA1_16 <= RBRACE)
                    || LA1_16 == SQUOTE || LA1_16 == BSLASH)
                    && ((Label0Parser.this.isGraph))) {
                    s = 8;
                }

                input.seek(index1_16);
                if (s >= 0) {
                    return s;
                }
                break;
            case 9:
                int LA1_6 = input.LA(1);

                int index1_6 = input.index();
                input.rewind();
                s = -1;
                if ((LA1_6 == COLON)) {
                    s = 19;
                }

                else if ((LA1_6 == EOF)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 20;
                }

                else if (((LA1_6 >= NEW && LA1_6 <= EQUALS)
                    || (LA1_6 >= LPAR && LA1_6 <= QUERY) || LA1_6 == COMMA
                    || (LA1_6 >= DQUOTE && LA1_6 <= UNDER) || (LA1_6 >= IDENT && LA1_6 <= 58))
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 21;
                }

                else if (((LA1_6 >= LBRACE && LA1_6 <= RBRACE)
                    || LA1_6 == SQUOTE || LA1_6 == BSLASH)
                    && ((Label0Parser.this.isGraph))) {
                    s = 8;
                }

                input.seek(index1_6);
                if (s >= 0) {
                    return s;
                }
                break;
            case 10:
                int LA1_15 = input.LA(1);

                int index1_15 = input.index();
                input.rewind();
                s = -1;
                if ((LA1_15 == COLON)) {
                    s = 19;
                }

                else if ((LA1_15 == EOF)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 20;
                }

                else if (((LA1_15 >= NEW && LA1_15 <= EQUALS)
                    || (LA1_15 >= LPAR && LA1_15 <= QUERY) || LA1_15 == COMMA
                    || (LA1_15 >= DQUOTE && LA1_15 <= UNDER) || (LA1_15 >= IDENT && LA1_15 <= 58))
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 21;
                }

                else if (((LA1_15 >= LBRACE && LA1_15 <= RBRACE)
                    || LA1_15 == SQUOTE || LA1_15 == BSLASH)
                    && ((Label0Parser.this.isGraph))) {
                    s = 8;
                }

                input.seek(index1_15);
                if (s >= 0) {
                    return s;
                }
                break;
            case 11:
                int LA1_22 = input.LA(1);

                int index1_22 = input.index();
                input.rewind();
                s = -1;
                if ((LA1_22 == LABEL)) {
                    s = 23;
                }

                else if ((LA1_22 == EOF)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 20;
                }

                else if (((LA1_22 >= NEW && LA1_22 <= EQUALS)
                    || (LA1_22 >= LPAR && LA1_22 <= QUERY) || LA1_22 == COMMA
                    || (LA1_22 >= DQUOTE && LA1_22 <= UNDER) || LA1_22 == IDENT || (LA1_22 >= NUMBER && LA1_22 <= 58))
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 21;
                }

                else if (((LA1_22 >= LBRACE && LA1_22 <= RBRACE)
                    || LA1_22 == SQUOTE || LA1_22 == BSLASH)
                    && ((Label0Parser.this.isGraph))) {
                    s = 8;
                }

                input.seek(index1_22);
                if (s >= 0) {
                    return s;
                }
                break;
            case 12:
                int LA1_14 = input.LA(1);

                int index1_14 = input.index();
                input.rewind();
                s = -1;
                if ((LA1_14 == COLON)) {
                    s = 19;
                }

                else if ((LA1_14 == EOF)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 20;
                }

                else if (((LA1_14 >= NEW && LA1_14 <= EQUALS)
                    || (LA1_14 >= LPAR && LA1_14 <= QUERY) || LA1_14 == COMMA
                    || (LA1_14 >= DQUOTE && LA1_14 <= UNDER) || (LA1_14 >= IDENT && LA1_14 <= 58))
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 21;
                }

                else if (((LA1_14 >= LBRACE && LA1_14 <= RBRACE)
                    || LA1_14 == SQUOTE || LA1_14 == BSLASH)
                    && ((Label0Parser.this.isGraph))) {
                    s = 8;
                }

                input.seek(index1_14);
                if (s >= 0) {
                    return s;
                }
                break;
            }
            if (Label0Parser.this.state.backtracking > 0) {
                Label0Parser.this.state.failed = true;
                return -1;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 1, _s, input);
            error(nvae);
            throw nvae;
        }
    }

    static final String DFA4_eotS = "\17\uffff";
    static final String DFA4_eofS = "\1\uffff\1\13\10\uffff\1\13\3\uffff\1\13";
    static final String DFA4_minS = "\2\4\10\uffff\1\4\3\uffff\1\4";
    static final String DFA4_maxS = "\2\72\10\uffff\1\72\3\uffff\1\72";
    static final String DFA4_acceptS = "\2\uffff\10\2\1\uffff\2\2\1\1\1\uffff";
    static final String DFA4_specialS = "\1\0\1\3\10\uffff\1\1\3\uffff\1\2}>";
    static final String[] DFA4_transitionS = {
        "\5\2\1\7\3\1\11\7\3\2\13\7\1\5\1\10\1\11\4\7\1\3\1\4\1\2\1"
            + "\7\1\6\3\7\1\11\7\7",
        "\40\14\1\12\2\11\6\14\1\15\1\14\1\11\3\14\1\11\7\14", "", "", "", "",
        "", "", "", "",
        "\41\14\2\11\6\14\1\uffff\1\14\1\11\3\14\1\11\1\16\6\14", "", "", "",
        "\41\14\2\11\6\14\1\15\1\14\1\11\3\14\1\11\7\14"};

    static final short[] DFA4_eot = DFA.unpackEncodedString(DFA4_eotS);
    static final short[] DFA4_eof = DFA.unpackEncodedString(DFA4_eofS);
    static final char[] DFA4_min =
        DFA.unpackEncodedStringToUnsignedChars(DFA4_minS);
    static final char[] DFA4_max =
        DFA.unpackEncodedStringToUnsignedChars(DFA4_maxS);
    static final short[] DFA4_accept = DFA.unpackEncodedString(DFA4_acceptS);
    static final short[] DFA4_special = DFA.unpackEncodedString(DFA4_specialS);
    static final short[][] DFA4_transition;

    static {
        int numStates = DFA4_transitionS.length;
        DFA4_transition = new short[numStates][];
        for (int i = 0; i < numStates; i++) {
            DFA4_transition[i] = DFA.unpackEncodedString(DFA4_transitionS[i]);
        }
    }

    class DFA4 extends DFA {

        public DFA4(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 4;
            this.eot = DFA4_eot;
            this.eof = DFA4_eof;
            this.min = DFA4_min;
            this.max = DFA4_max;
            this.accept = DFA4_accept;
            this.special = DFA4_special;
            this.transition = DFA4_transition;
        }

        public String getDescription() {
            return "114:1: quantLabel : ( quantPrefix ( EQUALS IDENT COLON ( rolePrefix COLON actualLabel -> ^( rolePrefix IDENT actualLabel ) | actualLabel -> ^( USE IDENT actualLabel ) | -> ^( quantPrefix IDENT ) ) | COLON -> quantPrefix ) | roleLabel );";
        }

        public int specialStateTransition(int s, IntStream _input)
            throws NoViableAltException {
            TokenStream input = (TokenStream) _input;
            int _s = s;
            switch (s) {
            case 0:
                int LA4_0 = input.LA(1);

                int index4_0 = input.index();
                input.rewind();
                s = -1;
                if (((LA4_0 >= FORALL && LA4_0 <= EXISTS))) {
                    s = 1;
                }

                else if (((LA4_0 >= NEW && LA4_0 <= CNEW)
                    || (LA4_0 >= TYPE && LA4_0 <= PATH) || LA4_0 == COLON)) {
                    s = 2;
                }

                else if ((LA4_0 == PLING)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 3;
                }

                else if ((LA4_0 == QUERY)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 4;
                }

                else if ((LA4_0 == EQUALS)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 5;
                }

                else if ((LA4_0 == SQUOTE)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 6;
                }

                else if ((LA4_0 == REM || (LA4_0 >= NESTED && LA4_0 <= PAR)
                    || (LA4_0 >= EMPTY && LA4_0 <= HAT)
                    || (LA4_0 >= LPAR && LA4_0 <= RSQUARE) || LA4_0 == COMMA
                    || (LA4_0 >= DQUOTE && LA4_0 <= UNDER) || (LA4_0 >= IDENT && LA4_0 <= 58))
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 7;
                }

                else if ((LA4_0 == LBRACE)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 8;
                }

                else if ((LA4_0 == RBRACE || LA4_0 == BSLASH)
                    && ((Label0Parser.this.isGraph))) {
                    s = 9;
                }

                input.seek(index4_0);
                if (s >= 0) {
                    return s;
                }
                break;
            case 1:
                int LA4_10 = input.LA(1);

                int index4_10 = input.index();
                input.rewind();
                s = -1;
                if ((LA4_10 == IDENT)) {
                    s = 14;
                }

                else if ((LA4_10 == EOF)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 11;
                }

                else if (((LA4_10 >= NEW && LA4_10 <= EQUALS)
                    || (LA4_10 >= LPAR && LA4_10 <= QUERY) || LA4_10 == COMMA
                    || (LA4_10 >= DQUOTE && LA4_10 <= UNDER) || (LA4_10 >= LABEL && LA4_10 <= 58))
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 12;
                }

                else if (((LA4_10 >= LBRACE && LA4_10 <= RBRACE)
                    || LA4_10 == SQUOTE || LA4_10 == BSLASH)
                    && ((Label0Parser.this.isGraph))) {
                    s = 9;
                }

                input.seek(index4_10);
                if (s >= 0) {
                    return s;
                }
                break;
            case 2:
                int LA4_14 = input.LA(1);

                int index4_14 = input.index();
                input.rewind();
                s = -1;
                if ((LA4_14 == COLON)) {
                    s = 13;
                }

                else if ((LA4_14 == EOF)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 11;
                }

                else if (((LA4_14 >= NEW && LA4_14 <= EQUALS)
                    || (LA4_14 >= LPAR && LA4_14 <= QUERY) || LA4_14 == COMMA
                    || (LA4_14 >= DQUOTE && LA4_14 <= UNDER) || (LA4_14 >= IDENT && LA4_14 <= 58))
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 12;
                }

                else if (((LA4_14 >= LBRACE && LA4_14 <= RBRACE)
                    || LA4_14 == SQUOTE || LA4_14 == BSLASH)
                    && ((Label0Parser.this.isGraph))) {
                    s = 9;
                }

                input.seek(index4_14);
                if (s >= 0) {
                    return s;
                }
                break;
            case 3:
                int LA4_1 = input.LA(1);

                int index4_1 = input.index();
                input.rewind();
                s = -1;
                if ((LA4_1 == EQUALS)) {
                    s = 10;
                }

                else if ((LA4_1 == EOF)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 11;
                }

                else if (((LA4_1 >= NEW && LA4_1 <= HAT)
                    || (LA4_1 >= LPAR && LA4_1 <= QUERY) || LA4_1 == COMMA
                    || (LA4_1 >= DQUOTE && LA4_1 <= UNDER) || (LA4_1 >= IDENT && LA4_1 <= 58))
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 12;
                }

                else if ((LA4_1 == COLON)) {
                    s = 13;
                }

                else if (((LA4_1 >= LBRACE && LA4_1 <= RBRACE)
                    || LA4_1 == SQUOTE || LA4_1 == BSLASH)
                    && ((Label0Parser.this.isGraph))) {
                    s = 9;
                }

                input.seek(index4_1);
                if (s >= 0) {
                    return s;
                }
                break;
            }
            if (Label0Parser.this.state.backtracking > 0) {
                Label0Parser.this.state.failed = true;
                return -1;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 4, _s, input);
            error(nvae);
            throw nvae;
        }
    }

    static final String DFA2_eotS = "\16\uffff";
    static final String DFA2_eofS = "\1\12\1\14\14\uffff";
    static final String DFA2_minS = "\2\4\14\uffff";
    static final String DFA2_maxS = "\2\72\14\uffff";
    static final String DFA2_acceptS = "\2\uffff\10\2\1\3\2\2\1\1";
    static final String DFA2_specialS = "\1\0\1\1\14\uffff}>";
    static final String[] DFA2_transitionS = {
        "\5\1\15\7\3\2\13\7\1\5\1\10\1\11\4\7\1\3\1\4\1\2\1\7\1\6\3"
            + "\7\1\11\7\7", "\41\13\2\11\6\13\1\15\1\13\1\11\3\13\1\11\7\13",
        "", "", "", "", "", "", "", "", "", "", "", ""};

    static final short[] DFA2_eot = DFA.unpackEncodedString(DFA2_eotS);
    static final short[] DFA2_eof = DFA.unpackEncodedString(DFA2_eofS);
    static final char[] DFA2_min =
        DFA.unpackEncodedStringToUnsignedChars(DFA2_minS);
    static final char[] DFA2_max =
        DFA.unpackEncodedStringToUnsignedChars(DFA2_maxS);
    static final short[] DFA2_accept = DFA.unpackEncodedString(DFA2_acceptS);
    static final short[] DFA2_special = DFA.unpackEncodedString(DFA2_specialS);
    static final short[][] DFA2_transition;

    static {
        int numStates = DFA2_transitionS.length;
        DFA2_transition = new short[numStates][];
        for (int i = 0; i < numStates; i++) {
            DFA2_transition[i] = DFA.unpackEncodedString(DFA2_transitionS[i]);
        }
    }

    class DFA2 extends DFA {

        public DFA2(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 2;
            this.eot = DFA2_eot;
            this.eof = DFA2_eof;
            this.min = DFA2_min;
            this.max = DFA2_max;
            this.accept = DFA2_accept;
            this.special = DFA2_special;
            this.transition = DFA2_transition;
        }

        public String getDescription() {
            return "117:8: ( rolePrefix COLON actualLabel -> ^( rolePrefix IDENT actualLabel ) | actualLabel -> ^( USE IDENT actualLabel ) | -> ^( quantPrefix IDENT ) )";
        }

        public int specialStateTransition(int s, IntStream _input)
            throws NoViableAltException {
            TokenStream input = (TokenStream) _input;
            int _s = s;
            switch (s) {
            case 0:
                int LA2_0 = input.LA(1);

                int index2_0 = input.index();
                input.rewind();
                s = -1;
                if (((LA2_0 >= NEW && LA2_0 <= CNEW))) {
                    s = 1;
                }

                else if (((LA2_0 >= TYPE && LA2_0 <= PATH) || LA2_0 == COLON)) {
                    s = 2;
                }

                else if ((LA2_0 == PLING)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 3;
                }

                else if ((LA2_0 == QUERY)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 4;
                }

                else if ((LA2_0 == EQUALS)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 5;
                }

                else if ((LA2_0 == SQUOTE)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 6;
                }

                else if (((LA2_0 >= REM && LA2_0 <= PAR)
                    || (LA2_0 >= EMPTY && LA2_0 <= HAT)
                    || (LA2_0 >= LPAR && LA2_0 <= RSQUARE) || LA2_0 == COMMA
                    || (LA2_0 >= DQUOTE && LA2_0 <= UNDER) || (LA2_0 >= IDENT && LA2_0 <= 58))
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 7;
                }

                else if ((LA2_0 == LBRACE)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 8;
                }

                else if ((LA2_0 == RBRACE || LA2_0 == BSLASH)
                    && ((Label0Parser.this.isGraph))) {
                    s = 9;
                }

                else if ((LA2_0 == EOF)) {
                    s = 10;
                }

                input.seek(index2_0);
                if (s >= 0) {
                    return s;
                }
                break;
            case 1:
                int LA2_1 = input.LA(1);

                int index2_1 = input.index();
                input.rewind();
                s = -1;
                if (((LA2_1 >= NEW && LA2_1 <= EQUALS)
                    || (LA2_1 >= LPAR && LA2_1 <= QUERY) || LA2_1 == COMMA
                    || (LA2_1 >= DQUOTE && LA2_1 <= UNDER) || (LA2_1 >= IDENT && LA2_1 <= 58))
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 11;
                }

                else if ((LA2_1 == EOF)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 12;
                }

                else if (((LA2_1 >= LBRACE && LA2_1 <= RBRACE)
                    || LA2_1 == SQUOTE || LA2_1 == BSLASH)
                    && ((Label0Parser.this.isGraph))) {
                    s = 9;
                }

                else if ((LA2_1 == COLON)) {
                    s = 13;
                }

                input.seek(index2_1);
                if (s >= 0) {
                    return s;
                }
                break;
            }
            if (Label0Parser.this.state.backtracking > 0) {
                Label0Parser.this.state.failed = true;
                return -1;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 2, _s, input);
            error(nvae);
            throw nvae;
        }
    }

    static final String DFA7_eotS = "\17\uffff";
    static final String DFA7_eofS = "\1\uffff\1\13\10\uffff\1\13\3\uffff\1\13";
    static final String DFA7_minS = "\2\4\10\uffff\1\4\3\uffff\1\4";
    static final String DFA7_maxS = "\2\72\10\uffff\1\72\3\uffff\1\72";
    static final String DFA7_acceptS = "\2\uffff\10\2\1\uffff\2\2\1\1\1\uffff";
    static final String DFA7_specialS = "\1\0\1\1\10\uffff\1\3\3\uffff\1\2}>";
    static final String[] DFA7_transitionS = {
        "\5\1\15\7\3\2\13\7\1\5\1\10\1\11\4\7\1\3\1\4\1\2\1\7\1\6\3"
            + "\7\1\11\7\7",
        "\40\14\1\12\2\11\6\14\1\15\1\14\1\11\3\14\1\11\7\14", "", "", "", "",
        "", "", "", "",
        "\41\14\2\11\6\14\1\uffff\1\14\1\11\3\14\1\11\1\16\6\14", "", "", "",
        "\41\14\2\11\6\14\1\15\1\14\1\11\3\14\1\11\7\14"};

    static final short[] DFA7_eot = DFA.unpackEncodedString(DFA7_eotS);
    static final short[] DFA7_eof = DFA.unpackEncodedString(DFA7_eofS);
    static final char[] DFA7_min =
        DFA.unpackEncodedStringToUnsignedChars(DFA7_minS);
    static final char[] DFA7_max =
        DFA.unpackEncodedStringToUnsignedChars(DFA7_maxS);
    static final short[] DFA7_accept = DFA.unpackEncodedString(DFA7_acceptS);
    static final short[] DFA7_special = DFA.unpackEncodedString(DFA7_specialS);
    static final short[][] DFA7_transition;

    static {
        int numStates = DFA7_transitionS.length;
        DFA7_transition = new short[numStates][];
        for (int i = 0; i < numStates; i++) {
            DFA7_transition[i] = DFA.unpackEncodedString(DFA7_transitionS[i]);
        }
    }

    class DFA7 extends DFA {

        public DFA7(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 7;
            this.eot = DFA7_eot;
            this.eof = DFA7_eof;
            this.min = DFA7_min;
            this.max = DFA7_max;
            this.accept = DFA7_accept;
            this.special = DFA7_special;
            this.transition = DFA7_transition;
        }

        public String getDescription() {
            return "133:1: roleLabel : ( rolePrefix ( EQUALS IDENT COLON actualLabel -> ^( rolePrefix IDENT actualLabel ) | COLON ( actualLabel -> ^( rolePrefix actualLabel ) | -> rolePrefix ) ) | actualLabel );";
        }

        public int specialStateTransition(int s, IntStream _input)
            throws NoViableAltException {
            TokenStream input = (TokenStream) _input;
            int _s = s;
            switch (s) {
            case 0:
                int LA7_0 = input.LA(1);

                int index7_0 = input.index();
                input.rewind();
                s = -1;
                if (((LA7_0 >= NEW && LA7_0 <= CNEW))) {
                    s = 1;
                }

                else if (((LA7_0 >= TYPE && LA7_0 <= PATH) || LA7_0 == COLON)) {
                    s = 2;
                }

                else if ((LA7_0 == PLING)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 3;
                }

                else if ((LA7_0 == QUERY)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 4;
                }

                else if ((LA7_0 == EQUALS)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 5;
                }

                else if ((LA7_0 == SQUOTE)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 6;
                }

                else if (((LA7_0 >= REM && LA7_0 <= PAR)
                    || (LA7_0 >= EMPTY && LA7_0 <= HAT)
                    || (LA7_0 >= LPAR && LA7_0 <= RSQUARE) || LA7_0 == COMMA
                    || (LA7_0 >= DQUOTE && LA7_0 <= UNDER) || (LA7_0 >= IDENT && LA7_0 <= 58))
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 7;
                }

                else if ((LA7_0 == LBRACE)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 8;
                }

                else if ((LA7_0 == RBRACE || LA7_0 == BSLASH)
                    && ((Label0Parser.this.isGraph))) {
                    s = 9;
                }

                input.seek(index7_0);
                if (s >= 0) {
                    return s;
                }
                break;
            case 1:
                int LA7_1 = input.LA(1);

                int index7_1 = input.index();
                input.rewind();
                s = -1;
                if ((LA7_1 == EQUALS)) {
                    s = 10;
                }

                else if ((LA7_1 == EOF)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 11;
                }

                else if (((LA7_1 >= NEW && LA7_1 <= HAT)
                    || (LA7_1 >= LPAR && LA7_1 <= QUERY) || LA7_1 == COMMA
                    || (LA7_1 >= DQUOTE && LA7_1 <= UNDER) || (LA7_1 >= IDENT && LA7_1 <= 58))
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 12;
                }

                else if ((LA7_1 == COLON)) {
                    s = 13;
                }

                else if (((LA7_1 >= LBRACE && LA7_1 <= RBRACE)
                    || LA7_1 == SQUOTE || LA7_1 == BSLASH)
                    && ((Label0Parser.this.isGraph))) {
                    s = 9;
                }

                input.seek(index7_1);
                if (s >= 0) {
                    return s;
                }
                break;
            case 2:
                int LA7_14 = input.LA(1);

                int index7_14 = input.index();
                input.rewind();
                s = -1;
                if ((LA7_14 == COLON)) {
                    s = 13;
                }

                else if ((LA7_14 == EOF)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 11;
                }

                else if (((LA7_14 >= NEW && LA7_14 <= EQUALS)
                    || (LA7_14 >= LPAR && LA7_14 <= QUERY) || LA7_14 == COMMA
                    || (LA7_14 >= DQUOTE && LA7_14 <= UNDER) || (LA7_14 >= IDENT && LA7_14 <= 58))
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 12;
                }

                else if (((LA7_14 >= LBRACE && LA7_14 <= RBRACE)
                    || LA7_14 == SQUOTE || LA7_14 == BSLASH)
                    && ((Label0Parser.this.isGraph))) {
                    s = 9;
                }

                input.seek(index7_14);
                if (s >= 0) {
                    return s;
                }
                break;
            case 3:
                int LA7_10 = input.LA(1);

                int index7_10 = input.index();
                input.rewind();
                s = -1;
                if ((LA7_10 == IDENT)) {
                    s = 14;
                }

                else if ((LA7_10 == EOF)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 11;
                }

                else if (((LA7_10 >= NEW && LA7_10 <= EQUALS)
                    || (LA7_10 >= LPAR && LA7_10 <= QUERY) || LA7_10 == COMMA
                    || (LA7_10 >= DQUOTE && LA7_10 <= UNDER) || (LA7_10 >= LABEL && LA7_10 <= 58))
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 12;
                }

                else if (((LA7_10 >= LBRACE && LA7_10 <= RBRACE)
                    || LA7_10 == SQUOTE || LA7_10 == BSLASH)
                    && ((Label0Parser.this.isGraph))) {
                    s = 9;
                }

                input.seek(index7_10);
                if (s >= 0) {
                    return s;
                }
                break;
            }
            if (Label0Parser.this.state.backtracking > 0) {
                Label0Parser.this.state.failed = true;
                return -1;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 7, _s, input);
            error(nvae);
            throw nvae;
        }
    }

    static final String DFA5_eotS = "\12\uffff";
    static final String DFA5_eofS = "\1\11\11\uffff";
    static final String DFA5_minS = "\1\4\11\uffff";
    static final String DFA5_maxS = "\1\72\11\uffff";
    static final String DFA5_acceptS = "\1\uffff\10\1\1\2";
    static final String DFA5_specialS = "\1\0\11\uffff}>";
    static final String[] DFA5_transitionS =
        {
            "\22\6\3\1\13\6\1\4\1\7\1\10\4\6\1\2\1\3\1\1\1\6\1\5\3\6\1\10"
                + "\7\6", "", "", "", "", "", "", "", "", ""};

    static final short[] DFA5_eot = DFA.unpackEncodedString(DFA5_eotS);
    static final short[] DFA5_eof = DFA.unpackEncodedString(DFA5_eofS);
    static final char[] DFA5_min =
        DFA.unpackEncodedStringToUnsignedChars(DFA5_minS);
    static final char[] DFA5_max =
        DFA.unpackEncodedStringToUnsignedChars(DFA5_maxS);
    static final short[] DFA5_accept = DFA.unpackEncodedString(DFA5_acceptS);
    static final short[] DFA5_special = DFA.unpackEncodedString(DFA5_specialS);
    static final short[][] DFA5_transition;

    static {
        int numStates = DFA5_transitionS.length;
        DFA5_transition = new short[numStates][];
        for (int i = 0; i < numStates; i++) {
            DFA5_transition[i] = DFA.unpackEncodedString(DFA5_transitionS[i]);
        }
    }

    class DFA5 extends DFA {

        public DFA5(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 5;
            this.eot = DFA5_eot;
            this.eof = DFA5_eof;
            this.min = DFA5_min;
            this.max = DFA5_max;
            this.accept = DFA5_accept;
            this.special = DFA5_special;
            this.transition = DFA5_transition;
        }

        public String getDescription() {
            return "138:8: ( actualLabel -> ^( rolePrefix actualLabel ) | -> rolePrefix )";
        }

        public int specialStateTransition(int s, IntStream _input)
            throws NoViableAltException {
            TokenStream input = (TokenStream) _input;
            int _s = s;
            switch (s) {
            case 0:
                int LA5_0 = input.LA(1);

                int index5_0 = input.index();
                input.rewind();
                s = -1;
                if (((LA5_0 >= TYPE && LA5_0 <= PATH) || LA5_0 == COLON)) {
                    s = 1;
                }

                else if ((LA5_0 == PLING)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 2;
                }

                else if ((LA5_0 == QUERY)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 3;
                }

                else if ((LA5_0 == EQUALS)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 4;
                }

                else if ((LA5_0 == SQUOTE)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 5;
                }

                else if (((LA5_0 >= NEW && LA5_0 <= PAR)
                    || (LA5_0 >= EMPTY && LA5_0 <= HAT)
                    || (LA5_0 >= LPAR && LA5_0 <= RSQUARE) || LA5_0 == COMMA
                    || (LA5_0 >= DQUOTE && LA5_0 <= UNDER) || (LA5_0 >= IDENT && LA5_0 <= 58))
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 6;
                }

                else if ((LA5_0 == LBRACE)
                    && (((!Label0Parser.this.isGraph) || (Label0Parser.this.isGraph)))) {
                    s = 7;
                }

                else if ((LA5_0 == RBRACE || LA5_0 == BSLASH)
                    && ((Label0Parser.this.isGraph))) {
                    s = 8;
                }

                else if ((LA5_0 == EOF)) {
                    s = 9;
                }

                input.seek(index5_0);
                if (s >= 0) {
                    return s;
                }
                break;
            }
            if (Label0Parser.this.state.backtracking > 0) {
                Label0Parser.this.state.failed = true;
                return -1;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 5, _s, input);
            error(nvae);
            throw nvae;
        }
    }

    static final String DFA14_eotS = "\20\uffff";
    static final String DFA14_eofS = "\20\uffff";
    static final String DFA14_minS = "\1\4\2\0\1\uffff\7\0\5\uffff";
    static final String DFA14_maxS = "\1\72\2\0\1\uffff\7\0\5\uffff";
    static final String DFA14_acceptS =
        "\3\uffff\1\3\7\uffff\1\5\1\1\1\6\1\2\1\4";
    static final String DFA14_specialS =
        "\1\0\1\1\1\2\1\uffff\1\3\1\4\1\5\1\6\1\7\1\10\1\11\5\uffff}>";
    static final String[] DFA14_transitionS = {
        "\22\11\1\1\1\2\1\4\13\11\1\7\1\12\1\13\4\11\1\5\1\6\1\3\1\11"
            + "\1\10\3\11\1\13\7\11", "\1\uffff", "\1\uffff", "", "\1\uffff",
        "\1\uffff", "\1\uffff", "\1\uffff", "\1\uffff", "\1\uffff", "\1\uffff",
        "", "", "", "", ""};

    static final short[] DFA14_eot = DFA.unpackEncodedString(DFA14_eotS);
    static final short[] DFA14_eof = DFA.unpackEncodedString(DFA14_eofS);
    static final char[] DFA14_min =
        DFA.unpackEncodedStringToUnsignedChars(DFA14_minS);
    static final char[] DFA14_max =
        DFA.unpackEncodedStringToUnsignedChars(DFA14_maxS);
    static final short[] DFA14_accept = DFA.unpackEncodedString(DFA14_acceptS);
    static final short[] DFA14_special =
        DFA.unpackEncodedString(DFA14_specialS);
    static final short[][] DFA14_transition;

    static {
        int numStates = DFA14_transitionS.length;
        DFA14_transition = new short[numStates][];
        for (int i = 0; i < numStates; i++) {
            DFA14_transition[i] = DFA.unpackEncodedString(DFA14_transitionS[i]);
        }
    }

    class DFA14 extends DFA {

        public DFA14(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 14;
            this.eot = DFA14_eot;
            this.eof = DFA14_eof;
            this.min = DFA14_min;
            this.max = DFA14_max;
            this.accept = DFA14_accept;
            this.special = DFA14_special;
            this.transition = DFA14_transition;
        }

        public String getDescription() {
            return "164:1: actualLabel : ( TYPE COLON IDENT -> ^( ATOM ^( TYPE IDENT ) ) | FLAG COLON IDENT -> ^( ATOM ^( FLAG IDENT ) ) | COLON text -> ^( ATOM ) | PATH COLON regExpr | ( graphDefault EOF )=>{...}? => graphLabel | ( ruleLabel EOF )=>{...}? => ruleLabel );";
        }

        public int specialStateTransition(int s, IntStream _input)
            throws NoViableAltException {
            TokenStream input = (TokenStream) _input;
            int _s = s;
            switch (s) {
            case 0:
                int LA14_0 = input.LA(1);

                int index14_0 = input.index();
                input.rewind();
                s = -1;
                if ((LA14_0 == TYPE)) {
                    s = 1;
                }

                else if ((LA14_0 == FLAG)) {
                    s = 2;
                }

                else if ((LA14_0 == COLON)) {
                    s = 3;
                }

                else if ((LA14_0 == PATH)) {
                    s = 4;
                }

                else if ((LA14_0 == PLING)) {
                    s = 5;
                }

                else if ((LA14_0 == QUERY)) {
                    s = 6;
                }

                else if ((LA14_0 == EQUALS)) {
                    s = 7;
                }

                else if ((LA14_0 == SQUOTE)) {
                    s = 8;
                }

                else if (((LA14_0 >= NEW && LA14_0 <= PAR)
                    || (LA14_0 >= EMPTY && LA14_0 <= HAT)
                    || (LA14_0 >= LPAR && LA14_0 <= RSQUARE) || LA14_0 == COMMA
                    || (LA14_0 >= DQUOTE && LA14_0 <= UNDER) || (LA14_0 >= IDENT && LA14_0 <= 58))) {
                    s = 9;
                }

                else if ((LA14_0 == LBRACE)) {
                    s = 10;
                }

                else if ((LA14_0 == RBRACE || LA14_0 == BSLASH)
                    && ((synpred1_Label0() && (Label0Parser.this.isGraph)))) {
                    s = 11;
                }

                input.seek(index14_0);
                if (s >= 0) {
                    return s;
                }
                break;
            case 1:
                int LA14_1 = input.LA(1);

                int index14_1 = input.index();
                input.rewind();
                s = -1;
                if ((true)) {
                    s = 12;
                }

                else if (((synpred1_Label0() && (Label0Parser.this.isGraph)))) {
                    s = 11;
                }

                else if (((synpred2_Label0() && (!Label0Parser.this.isGraph)))) {
                    s = 13;
                }

                input.seek(index14_1);
                if (s >= 0) {
                    return s;
                }
                break;
            case 2:
                int LA14_2 = input.LA(1);

                int index14_2 = input.index();
                input.rewind();
                s = -1;
                if ((true)) {
                    s = 14;
                }

                else if (((synpred1_Label0() && (Label0Parser.this.isGraph)))) {
                    s = 11;
                }

                else if (((synpred2_Label0() && (!Label0Parser.this.isGraph)))) {
                    s = 13;
                }

                input.seek(index14_2);
                if (s >= 0) {
                    return s;
                }
                break;
            case 3:
                int LA14_4 = input.LA(1);

                int index14_4 = input.index();
                input.rewind();
                s = -1;
                if ((true)) {
                    s = 15;
                }

                else if (((synpred1_Label0() && (Label0Parser.this.isGraph)))) {
                    s = 11;
                }

                else if (((synpred2_Label0() && (!Label0Parser.this.isGraph)))) {
                    s = 13;
                }

                input.seek(index14_4);
                if (s >= 0) {
                    return s;
                }
                break;
            case 4:
                int LA14_5 = input.LA(1);

                int index14_5 = input.index();
                input.rewind();
                s = -1;
                if (((synpred1_Label0() && (Label0Parser.this.isGraph)))) {
                    s = 11;
                }

                else if (((synpred2_Label0() && (!Label0Parser.this.isGraph)))) {
                    s = 13;
                }

                input.seek(index14_5);
                if (s >= 0) {
                    return s;
                }
                break;
            case 5:
                int LA14_6 = input.LA(1);

                int index14_6 = input.index();
                input.rewind();
                s = -1;
                if (((synpred1_Label0() && (Label0Parser.this.isGraph)))) {
                    s = 11;
                }

                else if (((synpred2_Label0() && (!Label0Parser.this.isGraph)))) {
                    s = 13;
                }

                input.seek(index14_6);
                if (s >= 0) {
                    return s;
                }
                break;
            case 6:
                int LA14_7 = input.LA(1);

                int index14_7 = input.index();
                input.rewind();
                s = -1;
                if (((synpred1_Label0() && (Label0Parser.this.isGraph)))) {
                    s = 11;
                }

                else if (((synpred2_Label0() && (!Label0Parser.this.isGraph)))) {
                    s = 13;
                }

                input.seek(index14_7);
                if (s >= 0) {
                    return s;
                }
                break;
            case 7:
                int LA14_8 = input.LA(1);

                int index14_8 = input.index();
                input.rewind();
                s = -1;
                if (((synpred1_Label0() && (Label0Parser.this.isGraph)))) {
                    s = 11;
                }

                else if (((synpred2_Label0() && (!Label0Parser.this.isGraph)))) {
                    s = 13;
                }

                input.seek(index14_8);
                if (s >= 0) {
                    return s;
                }
                break;
            case 8:
                int LA14_9 = input.LA(1);

                int index14_9 = input.index();
                input.rewind();
                s = -1;
                if (((synpred1_Label0() && (Label0Parser.this.isGraph)))) {
                    s = 11;
                }

                else if (((synpred2_Label0() && (!Label0Parser.this.isGraph)))) {
                    s = 13;
                }

                input.seek(index14_9);
                if (s >= 0) {
                    return s;
                }
                break;
            case 9:
                int LA14_10 = input.LA(1);

                int index14_10 = input.index();
                input.rewind();
                s = -1;
                if (((synpred1_Label0() && (Label0Parser.this.isGraph)))) {
                    s = 11;
                }

                else if (((synpred2_Label0() && (!Label0Parser.this.isGraph)))) {
                    s = 13;
                }

                input.seek(index14_10);
                if (s >= 0) {
                    return s;
                }
                break;
            }
            if (Label0Parser.this.state.backtracking > 0) {
                Label0Parser.this.state.failed = true;
                return -1;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 14, _s, input);
            error(nvae);
            throw nvae;
        }
    }

    public static final BitSet FOLLOW_quantLabel_in_label494 = new BitSet(
        new long[] {0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_label496 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_specialLabel_in_label504 = new BitSet(
        new long[] {0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_label506 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_quantPrefix_in_quantLabel522 =
        new BitSet(new long[] {0x0000201000000000L});
    public static final BitSet FOLLOW_EQUALS_in_quantLabel531 = new BitSet(
        new long[] {0x0010000000000000L});
    public static final BitSet FOLLOW_IDENT_in_quantLabel533 = new BitSet(
        new long[] {0x0000200000000000L});
    public static final BitSet FOLLOW_COLON_in_quantLabel535 = new BitSet(
        new long[] {0x07FFFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_rolePrefix_in_quantLabel546 = new BitSet(
        new long[] {0x0000200000000000L});
    public static final BitSet FOLLOW_COLON_in_quantLabel548 = new BitSet(
        new long[] {0x07FFFFFFFFFFFFF0L});
    public static final BitSet FOLLOW_actualLabel_in_quantLabel550 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_actualLabel_in_quantLabel580 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_quantLabel634 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_roleLabel_in_quantLabel661 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_quantPrefix0 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_rolePrefix_in_roleLabel699 = new BitSet(
        new long[] {0x0000201000000000L});
    public static final BitSet FOLLOW_EQUALS_in_roleLabel708 = new BitSet(
        new long[] {0x0010000000000000L});
    public static final BitSet FOLLOW_IDENT_in_roleLabel710 = new BitSet(
        new long[] {0x0000200000000000L});
    public static final BitSet FOLLOW_COLON_in_roleLabel712 = new BitSet(
        new long[] {0x07FFFFFFFFFFFFF0L});
    public static final BitSet FOLLOW_actualLabel_in_roleLabel714 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_roleLabel742 = new BitSet(
        new long[] {0x07FFFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_actualLabel_in_roleLabel754 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_actualLabel_in_roleLabel807 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_rolePrefix0 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_REM_in_specialLabel853 = new BitSet(
        new long[] {0x0000200000000000L});
    public static final BitSet FOLLOW_COLON_in_specialLabel856 = new BitSet(
        new long[] {0x03FFFFFFFFFFFFF0L});
    public static final BitSet FOLLOW_text_in_specialLabel859 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_PAR_in_specialLabel866 = new BitSet(
        new long[] {0x0000201000000000L});
    public static final BitSet FOLLOW_EQUALS_in_specialLabel870 = new BitSet(
        new long[] {0x0020000000000000L});
    public static final BitSet FOLLOW_LABEL_in_specialLabel873 = new BitSet(
        new long[] {0x0000200000000000L});
    public static final BitSet FOLLOW_COLON_in_specialLabel877 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_NESTED_in_specialLabel885 = new BitSet(
        new long[] {0x0000200000000000L});
    public static final BitSet FOLLOW_COLON_in_specialLabel888 = new BitSet(
        new long[] {0x0010000000000000L});
    public static final BitSet FOLLOW_IDENT_in_specialLabel891 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_specialLabel902 = new BitSet(
        new long[] {0x0000200000000000L});
    public static final BitSet FOLLOW_COLON_in_specialLabel905 = new BitSet(
        new long[] {0x0050000000000002L});
    public static final BitSet FOLLOW_set_in_specialLabel908 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_REAL_in_specialLabel922 = new BitSet(
        new long[] {0x0000200000000000L});
    public static final BitSet FOLLOW_COLON_in_specialLabel925 = new BitSet(
        new long[] {0x0050000200000002L});
    public static final BitSet FOLLOW_rnumber_in_specialLabel929 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_specialLabel933 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_specialLabel942 = new BitSet(
        new long[] {0x0000200000000000L});
    public static final BitSet FOLLOW_COLON_in_specialLabel945 = new BitSet(
        new long[] {0x0011000000000002L});
    public static final BitSet FOLLOW_dqText_in_specialLabel949 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_specialLabel953 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_specialLabel962 = new BitSet(
        new long[] {0x0000200000000000L});
    public static final BitSet FOLLOW_COLON_in_specialLabel965 = new BitSet(
        new long[] {0x0010000018000002L});
    public static final BitSet FOLLOW_set_in_specialLabel968 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_ATTR_in_specialLabel986 = new BitSet(
        new long[] {0x0000200000000000L});
    public static final BitSet FOLLOW_COLON_in_specialLabel989 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_PROD_in_specialLabel997 = new BitSet(
        new long[] {0x0000200000000000L});
    public static final BitSet FOLLOW_COLON_in_specialLabel1000 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_ARG_in_specialLabel1008 = new BitSet(
        new long[] {0x0000200000000000L});
    public static final BitSet FOLLOW_COLON_in_specialLabel1011 = new BitSet(
        new long[] {0x0040000000000000L});
    public static final BitSet FOLLOW_NUMBER_in_specialLabel1014 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_TYPE_in_actualLabel1029 = new BitSet(
        new long[] {0x0000200000000000L});
    public static final BitSet FOLLOW_COLON_in_actualLabel1031 = new BitSet(
        new long[] {0x0010000000000000L});
    public static final BitSet FOLLOW_IDENT_in_actualLabel1033 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_FLAG_in_actualLabel1052 = new BitSet(
        new long[] {0x0000200000000000L});
    public static final BitSet FOLLOW_COLON_in_actualLabel1054 = new BitSet(
        new long[] {0x0010000000000000L});
    public static final BitSet FOLLOW_IDENT_in_actualLabel1056 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_actualLabel1075 = new BitSet(
        new long[] {0x03FFFFFFFFFFFFF0L});
    public static final BitSet FOLLOW_text_in_actualLabel1077 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_PATH_in_actualLabel1092 = new BitSet(
        new long[] {0x0000200000000000L});
    public static final BitSet FOLLOW_COLON_in_actualLabel1095 = new BitSet(
        new long[] {0x0070909040000000L});
    public static final BitSet FOLLOW_regExpr_in_actualLabel1098 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_graphLabel_in_actualLabel1117 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_ruleLabel_in_actualLabel1136 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_text1152 = new BitSet(
        new long[] {0x03FFFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_graphDefault_in_graphLabel1170 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_graphDefault1194 = new BitSet(
        new long[] {0x07FFDFFFFFFFFFF2L});
    public static final BitSet FOLLOW_PLING_in_ruleLabel1212 = new BitSet(
        new long[] {0x07F7D7BFFFFFFFF0L});
    public static final BitSet FOLLOW_simpleRuleLabel_in_ruleLabel1222 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_ruleLabel1231 = new BitSet(
        new long[] {0x0070909040000000L});
    public static final BitSet FOLLOW_regExpr_in_ruleLabel1234 = new BitSet(
        new long[] {0x0000004000000000L});
    public static final BitSet FOLLOW_RBRACE_in_ruleLabel1236 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_simpleRuleLabel_in_ruleLabel1251 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_ruleLabel1258 = new BitSet(
        new long[] {0x0070989040000000L});
    public static final BitSet FOLLOW_PLING_in_ruleLabel1268 = new BitSet(
        new long[] {0x0070909040000000L});
    public static final BitSet FOLLOW_regExpr_in_ruleLabel1271 = new BitSet(
        new long[] {0x0000004000000000L});
    public static final BitSet FOLLOW_regExpr_in_ruleLabel1280 = new BitSet(
        new long[] {0x0000004000000000L});
    public static final BitSet FOLLOW_RBRACE_in_ruleLabel1294 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_wildcard_in_simpleRuleLabel1310 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_simpleRuleLabel1317 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_sqText_in_simpleRuleLabel1324 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDefault_in_simpleRuleLabel1339 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_ruleDefault1363 = new BitSet(
        new long[] {0x07F75F9FFFFFFFF2L});
    public static final BitSet FOLLOW_set_in_ruleDefault1402 = new BitSet(
        new long[] {0x07F75F9FFFFFFFF2L});
    public static final BitSet FOLLOW_NUMBER_in_rnumber1437 = new BitSet(
        new long[] {0x0000000200000002L});
    public static final BitSet FOLLOW_DOT_in_rnumber1440 = new BitSet(
        new long[] {0x0040000000000002L});
    public static final BitSet FOLLOW_NUMBER_in_rnumber1442 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_rnumber1452 = new BitSet(
        new long[] {0x0040000000000000L});
    public static final BitSet FOLLOW_NUMBER_in_rnumber1454 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_choice_in_regExpr1469 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_sequence_in_choice1484 = new BitSet(
        new long[] {0x0000000400000002L});
    public static final BitSet FOLLOW_BAR_in_choice1487 = new BitSet(
        new long[] {0x0070909040000000L});
    public static final BitSet FOLLOW_choice_in_choice1490 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_unary_in_sequence1504 = new BitSet(
        new long[] {0x0000000200000002L});
    public static final BitSet FOLLOW_DOT_in_sequence1507 = new BitSet(
        new long[] {0x0070909040000000L});
    public static final BitSet FOLLOW_sequence_in_sequence1510 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_unary1524 = new BitSet(
        new long[] {0x0070909040000000L});
    public static final BitSet FOLLOW_unary_in_unary1527 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_unary1534 = new BitSet(
        new long[] {0x0000000180000002L});
    public static final BitSet FOLLOW_STAR_in_unary1537 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_unary1542 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_unary1552 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_unary1559 = new BitSet(
        new long[] {0x0070909040000000L});
    public static final BitSet FOLLOW_regExpr_in_unary1562 = new BitSet(
        new long[] {0x0000010000000000L});
    public static final BitSet FOLLOW_RPAR_in_unary1564 = new BitSet(
        new long[] {0x0000000180000002L});
    public static final BitSet FOLLOW_STAR_in_unary1568 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_unary1573 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_wildcard_in_unary1583 = new BitSet(
        new long[] {0x0000000180000002L});
    public static final BitSet FOLLOW_STAR_in_unary1586 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_unary1591 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_sqText_in_atom1609 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_atomLabel_in_atom1624 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_atomLabel0 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_QUERY_in_wildcard1673 = new BitSet(
        new long[] {0x0010020000000002L});
    public static final BitSet FOLLOW_IDENT_in_wildcard1676 = new BitSet(
        new long[] {0x0000020000000002L});
    public static final BitSet FOLLOW_LSQUARE_in_wildcard1680 = new BitSet(
        new long[] {0x0070800800000000L});
    public static final BitSet FOLLOW_HAT_in_wildcard1683 = new BitSet(
        new long[] {0x0070800000000000L});
    public static final BitSet FOLLOW_atom_in_wildcard1686 = new BitSet(
        new long[] {0x0000440000000000L});
    public static final BitSet FOLLOW_COMMA_in_wildcard1689 = new BitSet(
        new long[] {0x0070800000000000L});
    public static final BitSet FOLLOW_atom_in_wildcard1692 = new BitSet(
        new long[] {0x0000440000000000L});
    public static final BitSet FOLLOW_RSQUARE_in_wildcard1696 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_SQUOTE_in_sqText1714 = new BitSet(
        new long[] {0x07FFFFFFFFFFFFF0L});
    public static final BitSet FOLLOW_sqContent_in_sqText1716 = new BitSet(
        new long[] {0x0000800000000000L});
    public static final BitSet FOLLOW_SQUOTE_in_sqText1718 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_sqChar_in_sqContent1737 = new BitSet(
        new long[] {0x07FF7FFFFFFFFFF2L});
    public static final BitSet FOLLOW_set_in_sqChar1753 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_BSLASH_in_sqChar1766 = new BitSet(
        new long[] {0x0008800000000000L});
    public static final BitSet FOLLOW_set_in_sqChar1769 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_DQUOTE_in_dqText1788 = new BitSet(
        new long[] {0x07FFFFFFFFFFFFF0L});
    public static final BitSet FOLLOW_dqContent_in_dqText1790 = new BitSet(
        new long[] {0x0001000000000000L});
    public static final BitSet FOLLOW_DQUOTE_in_dqText1792 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_dqTextChar_in_dqContent1815 = new BitSet(
        new long[] {0x07FEFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_set_in_dqTextChar1831 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_BSLASH_in_dqTextChar1843 = new BitSet(
        new long[] {0x0009000000000000L});
    public static final BitSet FOLLOW_set_in_dqTextChar1846 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_graphDefault_in_synpred1_Label01106 =
        new BitSet(new long[] {0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_synpred1_Label01108 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_ruleLabel_in_synpred2_Label01125 =
        new BitSet(new long[] {0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_synpred2_Label01127 = new BitSet(
        new long[] {0x0000000000000002L});

}
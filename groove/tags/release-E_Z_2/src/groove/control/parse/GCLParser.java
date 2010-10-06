// $ANTLR 3.2 Sep 23, 2009 12:02:23 GCL.g 2010-09-09 10:23:43

package groove.control.parse;

import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.BitSet;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.DFA;
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
import org.antlr.runtime.tree.RewriteEarlyExitException;
import org.antlr.runtime.tree.RewriteRuleSubtreeStream;
import org.antlr.runtime.tree.RewriteRuleTokenStream;
import org.antlr.runtime.tree.TreeAdaptor;

@SuppressWarnings("all")
public class GCLParser extends Parser {
    public static final String[] tokenNames =
        new String[] {"<invalid>", "<EOR>", "<DOWN>", "<UP>", "PROGRAM",
            "BLOCK", "FUNCTIONS", "FUNCTION", "CALL", "DO", "VAR", "PARAM",
            "IDENTIFIER", "OR", "ALAP", "WHILE", "UNTIL", "CHOICE", "CH_OR",
            "IF", "ELSE", "TRY", "TRUE", "PLUS", "STAR", "SHARP", "ANY",
            "OTHER", "DOT", "NODE_TYPE", "BOOL_TYPE", "STRING_TYPE",
            "INT_TYPE", "REAL_TYPE", "COMMA", "OUT", "DONT_CARE", "FALSE",
            "STRING", "QUOTE", "BSLASH", "MINUS", "NUMBER", "AND", "NOT",
            "ML_COMMENT", "SL_COMMENT", "WS", "ANY_CHAR", "'{'", "'}'", "'('",
            "')'", "';'"};
    public static final int FUNCTION = 7;
    public static final int STAR = 24;
    public static final int FUNCTIONS = 6;
    public static final int WHILE = 15;
    public static final int BOOL_TYPE = 30;
    public static final int NODE_TYPE = 29;
    public static final int DO = 9;
    public static final int PARAM = 11;
    public static final int NOT = 44;
    public static final int ALAP = 14;
    public static final int AND = 43;
    public static final int EOF = -1;
    public static final int IF = 19;
    public static final int ANY_CHAR = 48;
    public static final int ML_COMMENT = 45;
    public static final int QUOTE = 39;
    public static final int T__51 = 51;
    public static final int T__52 = 52;
    public static final int T__53 = 53;
    public static final int COMMA = 34;
    public static final int IDENTIFIER = 12;
    public static final int CH_OR = 18;
    public static final int PLUS = 23;
    public static final int VAR = 10;
    public static final int DOT = 28;
    public static final int T__50 = 50;
    public static final int CHOICE = 17;
    public static final int SHARP = 25;
    public static final int OTHER = 27;
    public static final int T__49 = 49;
    public static final int ELSE = 20;
    public static final int NUMBER = 42;
    public static final int MINUS = 41;
    public static final int INT_TYPE = 32;
    public static final int TRUE = 22;
    public static final int TRY = 21;
    public static final int REAL_TYPE = 33;
    public static final int DONT_CARE = 36;
    public static final int WS = 47;
    public static final int ANY = 26;
    public static final int OUT = 35;
    public static final int UNTIL = 16;
    public static final int BLOCK = 5;
    public static final int STRING_TYPE = 31;
    public static final int SL_COMMENT = 46;
    public static final int OR = 13;
    public static final int PROGRAM = 4;
    public static final int CALL = 8;
    public static final int FALSE = 37;
    public static final int BSLASH = 40;
    public static final int STRING = 38;

    // delegates
    // delegators

    public GCLParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }

    public GCLParser(TokenStream input, RecognizerSharedState state) {
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
        return GCLParser.tokenNames;
    }

    public String getGrammarFileName() {
        return "GCL.g";
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
        String result = "";
        if (seq == null) {
            result = "";
        } else if (seq.getChildren() == null) {
            result = seq.getText();
        } else {
            StringBuilder builder = new StringBuilder();
            for (Object token : seq.getChildren()) {
                builder.append(((CommonTree) token).getText());
            }
            result = builder.toString();
        }
        return new CommonTree(new CommonToken(IDENTIFIER, result));
    }

    CommonTree toUnquoted(String text) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\\') {
                i++;
                c = text.charAt(i);
                result.append(c);
            } else if (c != '"') {
                result.append(c);
            }
        }
        // System.out.printf("From %s to %s%n", text, result);
        return new CommonTree(new CommonToken(IDENTIFIER, result.toString()));
    }

    public static class program_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "program"
    // GCL.g:100:1: program : ( function | statement )* -> ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( statement )* ) ) ;
    public final GCLParser.program_return program() throws RecognitionException {
        GCLParser.program_return retval = new GCLParser.program_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        GCLParser.function_return function1 = null;

        GCLParser.statement_return statement2 = null;

        RewriteRuleSubtreeStream stream_statement =
            new RewriteRuleSubtreeStream(this.adaptor, "rule statement");
        RewriteRuleSubtreeStream stream_function =
            new RewriteRuleSubtreeStream(this.adaptor, "rule function");
        try {
            // GCL.g:100:9: ( ( function | statement )* -> ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( statement )* ) ) )
            // GCL.g:100:11: ( function | statement )*
            {
                // GCL.g:100:11: ( function | statement )*
                loop1: do {
                    int alt1 = 3;
                    alt1 = this.dfa1.predict(this.input);
                    switch (alt1) {
                    case 1:
                        // GCL.g:100:12: function
                    {
                        pushFollow(FOLLOW_function_in_program106);
                        function1 = function();

                        this.state._fsp--;

                        stream_function.add(function1.getTree());

                    }
                        break;
                    case 2:
                        // GCL.g:100:21: statement
                    {
                        pushFollow(FOLLOW_statement_in_program108);
                        statement2 = statement();

                        this.state._fsp--;

                        stream_statement.add(statement2.getTree());

                    }
                        break;

                    default:
                        break loop1;
                    }
                } while (true);

                // AST REWRITE
                // elements: statement, function
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                retval.tree = root_0;
                RewriteRuleSubtreeStream stream_retval =
                    new RewriteRuleSubtreeStream(this.adaptor, "rule retval",
                        retval != null ? retval.tree : null);

                root_0 = (CommonTree) this.adaptor.nil();
                // 100:33: -> ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( statement )* ) )
                {
                    // GCL.g:100:36: ^( PROGRAM ^( FUNCTIONS ( function )* ) ^( BLOCK ( statement )* ) )
                    {
                        CommonTree root_1 = (CommonTree) this.adaptor.nil();
                        root_1 =
                            (CommonTree) this.adaptor.becomeRoot(
                                (CommonTree) this.adaptor.create(PROGRAM,
                                    "PROGRAM"), root_1);

                        // GCL.g:100:46: ^( FUNCTIONS ( function )* )
                        {
                            CommonTree root_2 = (CommonTree) this.adaptor.nil();
                            root_2 =
                                (CommonTree) this.adaptor.becomeRoot(
                                    (CommonTree) this.adaptor.create(FUNCTIONS,
                                        "FUNCTIONS"), root_2);

                            // GCL.g:100:58: ( function )*
                            while (stream_function.hasNext()) {
                                this.adaptor.addChild(root_2,
                                    stream_function.nextTree());

                            }
                            stream_function.reset();

                            this.adaptor.addChild(root_1, root_2);
                        }
                        // GCL.g:100:69: ^( BLOCK ( statement )* )
                        {
                            CommonTree root_2 = (CommonTree) this.adaptor.nil();
                            root_2 =
                                (CommonTree) this.adaptor.becomeRoot(
                                    (CommonTree) this.adaptor.create(BLOCK,
                                        "BLOCK"), root_2);

                            // GCL.g:100:77: ( statement )*
                            while (stream_statement.hasNext()) {
                                this.adaptor.addChild(root_2,
                                    stream_statement.nextTree());

                            }
                            stream_statement.reset();

                            this.adaptor.addChild(root_1, root_2);
                        }

                        this.adaptor.addChild(root_0, root_1);
                    }

                }

                retval.tree = root_0;
            }

            retval.stop = this.input.LT(-1);

            retval.tree = (CommonTree) this.adaptor.rulePostProcessing(root_0);
            this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                retval.stop);

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

    // $ANTLR end "program"

    public static class block_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "block"
    // GCL.g:102:1: block : '{' ( statement )* '}' -> ^( BLOCK ( statement )* ) ;
    public final GCLParser.block_return block() throws RecognitionException {
        GCLParser.block_return retval = new GCLParser.block_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token char_literal3 = null;
        Token char_literal5 = null;
        GCLParser.statement_return statement4 = null;

        CommonTree char_literal3_tree = null;
        CommonTree char_literal5_tree = null;
        RewriteRuleTokenStream stream_49 =
            new RewriteRuleTokenStream(this.adaptor, "token 49");
        RewriteRuleTokenStream stream_50 =
            new RewriteRuleTokenStream(this.adaptor, "token 50");
        RewriteRuleSubtreeStream stream_statement =
            new RewriteRuleSubtreeStream(this.adaptor, "rule statement");
        try {
            // GCL.g:102:7: ( '{' ( statement )* '}' -> ^( BLOCK ( statement )* ) )
            // GCL.g:102:9: '{' ( statement )* '}'
            {
                char_literal3 =
                    (Token) match(this.input, 49, FOLLOW_49_in_block138);
                stream_49.add(char_literal3);

                // GCL.g:102:13: ( statement )*
                loop2: do {
                    int alt2 = 2;
                    alt2 = this.dfa2.predict(this.input);
                    switch (alt2) {
                    case 1:
                        // GCL.g:102:13: statement
                    {
                        pushFollow(FOLLOW_statement_in_block140);
                        statement4 = statement();

                        this.state._fsp--;

                        stream_statement.add(statement4.getTree());

                    }
                        break;

                    default:
                        break loop2;
                    }
                } while (true);

                char_literal5 =
                    (Token) match(this.input, 50, FOLLOW_50_in_block144);
                stream_50.add(char_literal5);

                // AST REWRITE
                // elements: statement
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                retval.tree = root_0;
                RewriteRuleSubtreeStream stream_retval =
                    new RewriteRuleSubtreeStream(this.adaptor, "rule retval",
                        retval != null ? retval.tree : null);

                root_0 = (CommonTree) this.adaptor.nil();
                // 102:29: -> ^( BLOCK ( statement )* )
                {
                    // GCL.g:102:32: ^( BLOCK ( statement )* )
                    {
                        CommonTree root_1 = (CommonTree) this.adaptor.nil();
                        root_1 =
                            (CommonTree) this.adaptor.becomeRoot(
                                (CommonTree) this.adaptor.create(BLOCK, "BLOCK"),
                                root_1);

                        // GCL.g:102:40: ( statement )*
                        while (stream_statement.hasNext()) {
                            this.adaptor.addChild(root_1,
                                stream_statement.nextTree());

                        }
                        stream_statement.reset();

                        this.adaptor.addChild(root_0, root_1);
                    }

                }

                retval.tree = root_0;
            }

            retval.stop = this.input.LT(-1);

            retval.tree = (CommonTree) this.adaptor.rulePostProcessing(root_0);
            this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                retval.stop);

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

    // $ANTLR end "block"

    public static class function_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "function"
    // GCL.g:104:1: function : FUNCTION IDENTIFIER '(' ')' block -> ^( FUNCTION IDENTIFIER block ) ;
    public final GCLParser.function_return function()
        throws RecognitionException {
        GCLParser.function_return retval = new GCLParser.function_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token FUNCTION6 = null;
        Token IDENTIFIER7 = null;
        Token char_literal8 = null;
        Token char_literal9 = null;
        GCLParser.block_return block10 = null;

        CommonTree FUNCTION6_tree = null;
        CommonTree IDENTIFIER7_tree = null;
        CommonTree char_literal8_tree = null;
        CommonTree char_literal9_tree = null;
        RewriteRuleTokenStream stream_FUNCTION =
            new RewriteRuleTokenStream(this.adaptor, "token FUNCTION");
        RewriteRuleTokenStream stream_51 =
            new RewriteRuleTokenStream(this.adaptor, "token 51");
        RewriteRuleTokenStream stream_52 =
            new RewriteRuleTokenStream(this.adaptor, "token 52");
        RewriteRuleTokenStream stream_IDENTIFIER =
            new RewriteRuleTokenStream(this.adaptor, "token IDENTIFIER");
        RewriteRuleSubtreeStream stream_block =
            new RewriteRuleSubtreeStream(this.adaptor, "rule block");
        try {
            // GCL.g:104:10: ( FUNCTION IDENTIFIER '(' ')' block -> ^( FUNCTION IDENTIFIER block ) )
            // GCL.g:104:12: FUNCTION IDENTIFIER '(' ')' block
            {
                FUNCTION6 =
                    (Token) match(this.input, FUNCTION,
                        FOLLOW_FUNCTION_in_function161);
                stream_FUNCTION.add(FUNCTION6);

                IDENTIFIER7 =
                    (Token) match(this.input, IDENTIFIER,
                        FOLLOW_IDENTIFIER_in_function163);
                stream_IDENTIFIER.add(IDENTIFIER7);

                char_literal8 =
                    (Token) match(this.input, 51, FOLLOW_51_in_function165);
                stream_51.add(char_literal8);

                char_literal9 =
                    (Token) match(this.input, 52, FOLLOW_52_in_function167);
                stream_52.add(char_literal9);

                pushFollow(FOLLOW_block_in_function169);
                block10 = block();

                this.state._fsp--;

                stream_block.add(block10.getTree());

                // AST REWRITE
                // elements: FUNCTION, block, IDENTIFIER
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                retval.tree = root_0;
                RewriteRuleSubtreeStream stream_retval =
                    new RewriteRuleSubtreeStream(this.adaptor, "rule retval",
                        retval != null ? retval.tree : null);

                root_0 = (CommonTree) this.adaptor.nil();
                // 104:46: -> ^( FUNCTION IDENTIFIER block )
                {
                    // GCL.g:104:49: ^( FUNCTION IDENTIFIER block )
                    {
                        CommonTree root_1 = (CommonTree) this.adaptor.nil();
                        root_1 =
                            (CommonTree) this.adaptor.becomeRoot(
                                stream_FUNCTION.nextNode(), root_1);

                        this.adaptor.addChild(root_1,
                            stream_IDENTIFIER.nextNode());
                        this.adaptor.addChild(root_1, stream_block.nextTree());

                        this.adaptor.addChild(root_0, root_1);
                    }

                }

                retval.tree = root_0;
            }

            retval.stop = this.input.LT(-1);

            retval.tree = (CommonTree) this.adaptor.rulePostProcessing(root_0);
            this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                retval.stop);

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

    // $ANTLR end "function"

    public static class condition_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "condition"
    // GCL.g:106:1: condition : conditionliteral ( OR condition )? ;
    public final GCLParser.condition_return condition()
        throws RecognitionException {
        GCLParser.condition_return retval = new GCLParser.condition_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token OR12 = null;
        GCLParser.conditionliteral_return conditionliteral11 = null;

        GCLParser.condition_return condition13 = null;

        CommonTree OR12_tree = null;

        try {
            // GCL.g:107:2: ( conditionliteral ( OR condition )? )
            // GCL.g:107:4: conditionliteral ( OR condition )?
            {
                root_0 = (CommonTree) this.adaptor.nil();

                pushFollow(FOLLOW_conditionliteral_in_condition188);
                conditionliteral11 = conditionliteral();

                this.state._fsp--;

                this.adaptor.addChild(root_0, conditionliteral11.getTree());
                // GCL.g:107:21: ( OR condition )?
                int alt3 = 2;
                int LA3_0 = this.input.LA(1);

                if ((LA3_0 == OR)) {
                    alt3 = 1;
                }
                switch (alt3) {
                case 1:
                    // GCL.g:107:22: OR condition
                {
                    OR12 =
                        (Token) match(this.input, OR, FOLLOW_OR_in_condition191);
                    OR12_tree = (CommonTree) this.adaptor.create(OR12);
                    root_0 =
                        (CommonTree) this.adaptor.becomeRoot(OR12_tree, root_0);

                    pushFollow(FOLLOW_condition_in_condition194);
                    condition13 = condition();

                    this.state._fsp--;

                    this.adaptor.addChild(root_0, condition13.getTree());

                }
                    break;

                }

            }

            retval.stop = this.input.LT(-1);

            retval.tree = (CommonTree) this.adaptor.rulePostProcessing(root_0);
            this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                retval.stop);

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

    // $ANTLR end "condition"

    public static class statement_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "statement"
    // GCL.g:110:1: statement : ( ALAP block -> ^( ALAP block ) | WHILE '(' condition ')' ( DO )? block -> ^( WHILE condition block ) | UNTIL '(' condition ')' ( DO )? block -> ^( UNTIL condition block ) | DO block WHILE '(' condition ')' -> ^( DO block condition ) | ifstatement | CHOICE block ( CH_OR block )* -> ^( CHOICE ( block )+ ) | expression ';' -> expression | var_declaration ';' -> var_declaration );
    public final GCLParser.statement_return statement()
        throws RecognitionException {
        GCLParser.statement_return retval = new GCLParser.statement_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token ALAP14 = null;
        Token WHILE16 = null;
        Token char_literal17 = null;
        Token char_literal19 = null;
        Token DO20 = null;
        Token UNTIL22 = null;
        Token char_literal23 = null;
        Token char_literal25 = null;
        Token DO26 = null;
        Token DO28 = null;
        Token WHILE30 = null;
        Token char_literal31 = null;
        Token char_literal33 = null;
        Token CHOICE35 = null;
        Token CH_OR37 = null;
        Token char_literal40 = null;
        Token char_literal42 = null;
        GCLParser.block_return block15 = null;

        GCLParser.condition_return condition18 = null;

        GCLParser.block_return block21 = null;

        GCLParser.condition_return condition24 = null;

        GCLParser.block_return block27 = null;

        GCLParser.block_return block29 = null;

        GCLParser.condition_return condition32 = null;

        GCLParser.ifstatement_return ifstatement34 = null;

        GCLParser.block_return block36 = null;

        GCLParser.block_return block38 = null;

        GCLParser.expression_return expression39 = null;

        GCLParser.var_declaration_return var_declaration41 = null;

        CommonTree ALAP14_tree = null;
        CommonTree WHILE16_tree = null;
        CommonTree char_literal17_tree = null;
        CommonTree char_literal19_tree = null;
        CommonTree DO20_tree = null;
        CommonTree UNTIL22_tree = null;
        CommonTree char_literal23_tree = null;
        CommonTree char_literal25_tree = null;
        CommonTree DO26_tree = null;
        CommonTree DO28_tree = null;
        CommonTree WHILE30_tree = null;
        CommonTree char_literal31_tree = null;
        CommonTree char_literal33_tree = null;
        CommonTree CHOICE35_tree = null;
        CommonTree CH_OR37_tree = null;
        CommonTree char_literal40_tree = null;
        CommonTree char_literal42_tree = null;
        RewriteRuleTokenStream stream_DO =
            new RewriteRuleTokenStream(this.adaptor, "token DO");
        RewriteRuleTokenStream stream_WHILE =
            new RewriteRuleTokenStream(this.adaptor, "token WHILE");
        RewriteRuleTokenStream stream_ALAP =
            new RewriteRuleTokenStream(this.adaptor, "token ALAP");
        RewriteRuleTokenStream stream_51 =
            new RewriteRuleTokenStream(this.adaptor, "token 51");
        RewriteRuleTokenStream stream_52 =
            new RewriteRuleTokenStream(this.adaptor, "token 52");
        RewriteRuleTokenStream stream_53 =
            new RewriteRuleTokenStream(this.adaptor, "token 53");
        RewriteRuleTokenStream stream_UNTIL =
            new RewriteRuleTokenStream(this.adaptor, "token UNTIL");
        RewriteRuleTokenStream stream_CHOICE =
            new RewriteRuleTokenStream(this.adaptor, "token CHOICE");
        RewriteRuleTokenStream stream_CH_OR =
            new RewriteRuleTokenStream(this.adaptor, "token CH_OR");
        RewriteRuleSubtreeStream stream_expression =
            new RewriteRuleSubtreeStream(this.adaptor, "rule expression");
        RewriteRuleSubtreeStream stream_condition =
            new RewriteRuleSubtreeStream(this.adaptor, "rule condition");
        RewriteRuleSubtreeStream stream_block =
            new RewriteRuleSubtreeStream(this.adaptor, "rule block");
        RewriteRuleSubtreeStream stream_var_declaration =
            new RewriteRuleSubtreeStream(this.adaptor, "rule var_declaration");
        try {
            // GCL.g:111:2: ( ALAP block -> ^( ALAP block ) | WHILE '(' condition ')' ( DO )? block -> ^( WHILE condition block ) | UNTIL '(' condition ')' ( DO )? block -> ^( UNTIL condition block ) | DO block WHILE '(' condition ')' -> ^( DO block condition ) | ifstatement | CHOICE block ( CH_OR block )* -> ^( CHOICE ( block )+ ) | expression ';' -> expression | var_declaration ';' -> var_declaration )
            int alt7 = 8;
            alt7 = this.dfa7.predict(this.input);
            switch (alt7) {
            case 1:
                // GCL.g:111:4: ALAP block
            {
                ALAP14 =
                    (Token) match(this.input, ALAP, FOLLOW_ALAP_in_statement209);
                stream_ALAP.add(ALAP14);

                pushFollow(FOLLOW_block_in_statement211);
                block15 = block();

                this.state._fsp--;

                stream_block.add(block15.getTree());

                // AST REWRITE
                // elements: ALAP, block
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                retval.tree = root_0;
                RewriteRuleSubtreeStream stream_retval =
                    new RewriteRuleSubtreeStream(this.adaptor, "rule retval",
                        retval != null ? retval.tree : null);

                root_0 = (CommonTree) this.adaptor.nil();
                // 111:15: -> ^( ALAP block )
                {
                    // GCL.g:111:18: ^( ALAP block )
                    {
                        CommonTree root_1 = (CommonTree) this.adaptor.nil();
                        root_1 =
                            (CommonTree) this.adaptor.becomeRoot(
                                stream_ALAP.nextNode(), root_1);

                        this.adaptor.addChild(root_1, stream_block.nextTree());

                        this.adaptor.addChild(root_0, root_1);
                    }

                }

                retval.tree = root_0;
            }
                break;
            case 2:
                // GCL.g:112:4: WHILE '(' condition ')' ( DO )? block
            {
                WHILE16 =
                    (Token) match(this.input, WHILE,
                        FOLLOW_WHILE_in_statement224);
                stream_WHILE.add(WHILE16);

                char_literal17 =
                    (Token) match(this.input, 51, FOLLOW_51_in_statement226);
                stream_51.add(char_literal17);

                pushFollow(FOLLOW_condition_in_statement228);
                condition18 = condition();

                this.state._fsp--;

                stream_condition.add(condition18.getTree());
                char_literal19 =
                    (Token) match(this.input, 52, FOLLOW_52_in_statement230);
                stream_52.add(char_literal19);

                // GCL.g:112:28: ( DO )?
                int alt4 = 2;
                int LA4_0 = this.input.LA(1);

                if ((LA4_0 == DO)) {
                    alt4 = 1;
                }
                switch (alt4) {
                case 1:
                    // GCL.g:112:28: DO
                {
                    DO20 =
                        (Token) match(this.input, DO, FOLLOW_DO_in_statement232);
                    stream_DO.add(DO20);

                }
                    break;

                }

                pushFollow(FOLLOW_block_in_statement235);
                block21 = block();

                this.state._fsp--;

                stream_block.add(block21.getTree());

                // AST REWRITE
                // elements: block, WHILE, condition
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                retval.tree = root_0;
                RewriteRuleSubtreeStream stream_retval =
                    new RewriteRuleSubtreeStream(this.adaptor, "rule retval",
                        retval != null ? retval.tree : null);

                root_0 = (CommonTree) this.adaptor.nil();
                // 112:38: -> ^( WHILE condition block )
                {
                    // GCL.g:112:41: ^( WHILE condition block )
                    {
                        CommonTree root_1 = (CommonTree) this.adaptor.nil();
                        root_1 =
                            (CommonTree) this.adaptor.becomeRoot(
                                stream_WHILE.nextNode(), root_1);

                        this.adaptor.addChild(root_1,
                            stream_condition.nextTree());
                        this.adaptor.addChild(root_1, stream_block.nextTree());

                        this.adaptor.addChild(root_0, root_1);
                    }

                }

                retval.tree = root_0;
            }
                break;
            case 3:
                // GCL.g:113:4: UNTIL '(' condition ')' ( DO )? block
            {
                UNTIL22 =
                    (Token) match(this.input, UNTIL,
                        FOLLOW_UNTIL_in_statement250);
                stream_UNTIL.add(UNTIL22);

                char_literal23 =
                    (Token) match(this.input, 51, FOLLOW_51_in_statement252);
                stream_51.add(char_literal23);

                pushFollow(FOLLOW_condition_in_statement254);
                condition24 = condition();

                this.state._fsp--;

                stream_condition.add(condition24.getTree());
                char_literal25 =
                    (Token) match(this.input, 52, FOLLOW_52_in_statement256);
                stream_52.add(char_literal25);

                // GCL.g:113:28: ( DO )?
                int alt5 = 2;
                int LA5_0 = this.input.LA(1);

                if ((LA5_0 == DO)) {
                    alt5 = 1;
                }
                switch (alt5) {
                case 1:
                    // GCL.g:113:28: DO
                {
                    DO26 =
                        (Token) match(this.input, DO, FOLLOW_DO_in_statement258);
                    stream_DO.add(DO26);

                }
                    break;

                }

                pushFollow(FOLLOW_block_in_statement261);
                block27 = block();

                this.state._fsp--;

                stream_block.add(block27.getTree());

                // AST REWRITE
                // elements: UNTIL, block, condition
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                retval.tree = root_0;
                RewriteRuleSubtreeStream stream_retval =
                    new RewriteRuleSubtreeStream(this.adaptor, "rule retval",
                        retval != null ? retval.tree : null);

                root_0 = (CommonTree) this.adaptor.nil();
                // 113:38: -> ^( UNTIL condition block )
                {
                    // GCL.g:113:41: ^( UNTIL condition block )
                    {
                        CommonTree root_1 = (CommonTree) this.adaptor.nil();
                        root_1 =
                            (CommonTree) this.adaptor.becomeRoot(
                                stream_UNTIL.nextNode(), root_1);

                        this.adaptor.addChild(root_1,
                            stream_condition.nextTree());
                        this.adaptor.addChild(root_1, stream_block.nextTree());

                        this.adaptor.addChild(root_0, root_1);
                    }

                }

                retval.tree = root_0;
            }
                break;
            case 4:
                // GCL.g:114:4: DO block WHILE '(' condition ')'
            {
                DO28 = (Token) match(this.input, DO, FOLLOW_DO_in_statement276);
                stream_DO.add(DO28);

                pushFollow(FOLLOW_block_in_statement278);
                block29 = block();

                this.state._fsp--;

                stream_block.add(block29.getTree());
                WHILE30 =
                    (Token) match(this.input, WHILE,
                        FOLLOW_WHILE_in_statement280);
                stream_WHILE.add(WHILE30);

                char_literal31 =
                    (Token) match(this.input, 51, FOLLOW_51_in_statement282);
                stream_51.add(char_literal31);

                pushFollow(FOLLOW_condition_in_statement284);
                condition32 = condition();

                this.state._fsp--;

                stream_condition.add(condition32.getTree());
                char_literal33 =
                    (Token) match(this.input, 52, FOLLOW_52_in_statement286);
                stream_52.add(char_literal33);

                // AST REWRITE
                // elements: DO, condition, block
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                retval.tree = root_0;
                RewriteRuleSubtreeStream stream_retval =
                    new RewriteRuleSubtreeStream(this.adaptor, "rule retval",
                        retval != null ? retval.tree : null);

                root_0 = (CommonTree) this.adaptor.nil();
                // 114:37: -> ^( DO block condition )
                {
                    // GCL.g:114:40: ^( DO block condition )
                    {
                        CommonTree root_1 = (CommonTree) this.adaptor.nil();
                        root_1 =
                            (CommonTree) this.adaptor.becomeRoot(
                                stream_DO.nextNode(), root_1);

                        this.adaptor.addChild(root_1, stream_block.nextTree());
                        this.adaptor.addChild(root_1,
                            stream_condition.nextTree());

                        this.adaptor.addChild(root_0, root_1);
                    }

                }

                retval.tree = root_0;
            }
                break;
            case 5:
                // GCL.g:115:4: ifstatement
            {
                root_0 = (CommonTree) this.adaptor.nil();

                pushFollow(FOLLOW_ifstatement_in_statement301);
                ifstatement34 = ifstatement();

                this.state._fsp--;

                this.adaptor.addChild(root_0, ifstatement34.getTree());

            }
                break;
            case 6:
                // GCL.g:116:7: CHOICE block ( CH_OR block )*
            {
                CHOICE35 =
                    (Token) match(this.input, CHOICE,
                        FOLLOW_CHOICE_in_statement309);
                stream_CHOICE.add(CHOICE35);

                pushFollow(FOLLOW_block_in_statement311);
                block36 = block();

                this.state._fsp--;

                stream_block.add(block36.getTree());
                // GCL.g:116:20: ( CH_OR block )*
                loop6: do {
                    int alt6 = 2;
                    alt6 = this.dfa6.predict(this.input);
                    switch (alt6) {
                    case 1:
                        // GCL.g:116:21: CH_OR block
                    {
                        CH_OR37 =
                            (Token) match(this.input, CH_OR,
                                FOLLOW_CH_OR_in_statement314);
                        stream_CH_OR.add(CH_OR37);

                        pushFollow(FOLLOW_block_in_statement316);
                        block38 = block();

                        this.state._fsp--;

                        stream_block.add(block38.getTree());

                    }
                        break;

                    default:
                        break loop6;
                    }
                } while (true);

                // AST REWRITE
                // elements: block, CHOICE
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                retval.tree = root_0;
                RewriteRuleSubtreeStream stream_retval =
                    new RewriteRuleSubtreeStream(this.adaptor, "rule retval",
                        retval != null ? retval.tree : null);

                root_0 = (CommonTree) this.adaptor.nil();
                // 116:35: -> ^( CHOICE ( block )+ )
                {
                    // GCL.g:116:38: ^( CHOICE ( block )+ )
                    {
                        CommonTree root_1 = (CommonTree) this.adaptor.nil();
                        root_1 =
                            (CommonTree) this.adaptor.becomeRoot(
                                stream_CHOICE.nextNode(), root_1);

                        if (!(stream_block.hasNext())) {
                            throw new RewriteEarlyExitException();
                        }
                        while (stream_block.hasNext()) {
                            this.adaptor.addChild(root_1,
                                stream_block.nextTree());

                        }
                        stream_block.reset();

                        this.adaptor.addChild(root_0, root_1);
                    }

                }

                retval.tree = root_0;
            }
                break;
            case 7:
                // GCL.g:117:4: expression ';'
            {
                pushFollow(FOLLOW_expression_in_statement332);
                expression39 = expression();

                this.state._fsp--;

                stream_expression.add(expression39.getTree());
                char_literal40 =
                    (Token) match(this.input, 53, FOLLOW_53_in_statement334);
                stream_53.add(char_literal40);

                // AST REWRITE
                // elements: expression
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                retval.tree = root_0;
                RewriteRuleSubtreeStream stream_retval =
                    new RewriteRuleSubtreeStream(this.adaptor, "rule retval",
                        retval != null ? retval.tree : null);

                root_0 = (CommonTree) this.adaptor.nil();
                // 117:19: -> expression
                {
                    this.adaptor.addChild(root_0, stream_expression.nextTree());

                }

                retval.tree = root_0;
            }
                break;
            case 8:
                // GCL.g:118:4: var_declaration ';'
            {
                pushFollow(FOLLOW_var_declaration_in_statement343);
                var_declaration41 = var_declaration();

                this.state._fsp--;

                stream_var_declaration.add(var_declaration41.getTree());
                char_literal42 =
                    (Token) match(this.input, 53, FOLLOW_53_in_statement345);
                stream_53.add(char_literal42);

                // AST REWRITE
                // elements: var_declaration
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                retval.tree = root_0;
                RewriteRuleSubtreeStream stream_retval =
                    new RewriteRuleSubtreeStream(this.adaptor, "rule retval",
                        retval != null ? retval.tree : null);

                root_0 = (CommonTree) this.adaptor.nil();
                // 118:24: -> var_declaration
                {
                    this.adaptor.addChild(root_0,
                        stream_var_declaration.nextTree());

                }

                retval.tree = root_0;
            }
                break;

            }
            retval.stop = this.input.LT(-1);

            retval.tree = (CommonTree) this.adaptor.rulePostProcessing(root_0);
            this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                retval.stop);

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

    // $ANTLR end "statement"

    public static class ifstatement_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "ifstatement"
    // GCL.g:121:1: ifstatement : ( IF '(' condition ')' block ( ELSE elseblock )? -> ^( IF condition block ( elseblock )? ) | TRY block ( ELSE elseblock )? -> ^( TRY block ( elseblock )? ) );
    public final GCLParser.ifstatement_return ifstatement()
        throws RecognitionException {
        GCLParser.ifstatement_return retval =
            new GCLParser.ifstatement_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token IF43 = null;
        Token char_literal44 = null;
        Token char_literal46 = null;
        Token ELSE48 = null;
        Token TRY50 = null;
        Token ELSE52 = null;
        GCLParser.condition_return condition45 = null;

        GCLParser.block_return block47 = null;

        GCLParser.elseblock_return elseblock49 = null;

        GCLParser.block_return block51 = null;

        GCLParser.elseblock_return elseblock53 = null;

        CommonTree IF43_tree = null;
        CommonTree char_literal44_tree = null;
        CommonTree char_literal46_tree = null;
        CommonTree ELSE48_tree = null;
        CommonTree TRY50_tree = null;
        CommonTree ELSE52_tree = null;
        RewriteRuleTokenStream stream_51 =
            new RewriteRuleTokenStream(this.adaptor, "token 51");
        RewriteRuleTokenStream stream_52 =
            new RewriteRuleTokenStream(this.adaptor, "token 52");
        RewriteRuleTokenStream stream_TRY =
            new RewriteRuleTokenStream(this.adaptor, "token TRY");
        RewriteRuleTokenStream stream_IF =
            new RewriteRuleTokenStream(this.adaptor, "token IF");
        RewriteRuleTokenStream stream_ELSE =
            new RewriteRuleTokenStream(this.adaptor, "token ELSE");
        RewriteRuleSubtreeStream stream_condition =
            new RewriteRuleSubtreeStream(this.adaptor, "rule condition");
        RewriteRuleSubtreeStream stream_block =
            new RewriteRuleSubtreeStream(this.adaptor, "rule block");
        RewriteRuleSubtreeStream stream_elseblock =
            new RewriteRuleSubtreeStream(this.adaptor, "rule elseblock");
        try {
            // GCL.g:122:5: ( IF '(' condition ')' block ( ELSE elseblock )? -> ^( IF condition block ( elseblock )? ) | TRY block ( ELSE elseblock )? -> ^( TRY block ( elseblock )? ) )
            int alt10 = 2;
            int LA10_0 = this.input.LA(1);

            if ((LA10_0 == IF)) {
                alt10 = 1;
            } else if ((LA10_0 == TRY)) {
                alt10 = 2;
            } else {
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, this.input);

                throw nvae;
            }
            switch (alt10) {
            case 1:
                // GCL.g:122:7: IF '(' condition ')' block ( ELSE elseblock )?
            {
                IF43 =
                    (Token) match(this.input, IF, FOLLOW_IF_in_ifstatement367);
                stream_IF.add(IF43);

                char_literal44 =
                    (Token) match(this.input, 51, FOLLOW_51_in_ifstatement369);
                stream_51.add(char_literal44);

                pushFollow(FOLLOW_condition_in_ifstatement371);
                condition45 = condition();

                this.state._fsp--;

                stream_condition.add(condition45.getTree());
                char_literal46 =
                    (Token) match(this.input, 52, FOLLOW_52_in_ifstatement373);
                stream_52.add(char_literal46);

                pushFollow(FOLLOW_block_in_ifstatement375);
                block47 = block();

                this.state._fsp--;

                stream_block.add(block47.getTree());
                // GCL.g:122:34: ( ELSE elseblock )?
                int alt8 = 2;
                alt8 = this.dfa8.predict(this.input);
                switch (alt8) {
                case 1:
                    // GCL.g:122:35: ELSE elseblock
                {
                    ELSE48 =
                        (Token) match(this.input, ELSE,
                            FOLLOW_ELSE_in_ifstatement378);
                    stream_ELSE.add(ELSE48);

                    pushFollow(FOLLOW_elseblock_in_ifstatement380);
                    elseblock49 = elseblock();

                    this.state._fsp--;

                    stream_elseblock.add(elseblock49.getTree());

                }
                    break;

                }

                // AST REWRITE
                // elements: elseblock, IF, block, condition
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                retval.tree = root_0;
                RewriteRuleSubtreeStream stream_retval =
                    new RewriteRuleSubtreeStream(this.adaptor, "rule retval",
                        retval != null ? retval.tree : null);

                root_0 = (CommonTree) this.adaptor.nil();
                // 122:52: -> ^( IF condition block ( elseblock )? )
                {
                    // GCL.g:122:55: ^( IF condition block ( elseblock )? )
                    {
                        CommonTree root_1 = (CommonTree) this.adaptor.nil();
                        root_1 =
                            (CommonTree) this.adaptor.becomeRoot(
                                stream_IF.nextNode(), root_1);

                        this.adaptor.addChild(root_1,
                            stream_condition.nextTree());
                        this.adaptor.addChild(root_1, stream_block.nextTree());
                        // GCL.g:122:76: ( elseblock )?
                        if (stream_elseblock.hasNext()) {
                            this.adaptor.addChild(root_1,
                                stream_elseblock.nextTree());

                        }
                        stream_elseblock.reset();

                        this.adaptor.addChild(root_0, root_1);
                    }

                }

                retval.tree = root_0;
            }
                break;
            case 2:
                // GCL.g:123:7: TRY block ( ELSE elseblock )?
            {
                TRY50 =
                    (Token) match(this.input, TRY, FOLLOW_TRY_in_ifstatement403);
                stream_TRY.add(TRY50);

                pushFollow(FOLLOW_block_in_ifstatement405);
                block51 = block();

                this.state._fsp--;

                stream_block.add(block51.getTree());
                // GCL.g:123:17: ( ELSE elseblock )?
                int alt9 = 2;
                alt9 = this.dfa9.predict(this.input);
                switch (alt9) {
                case 1:
                    // GCL.g:123:18: ELSE elseblock
                {
                    ELSE52 =
                        (Token) match(this.input, ELSE,
                            FOLLOW_ELSE_in_ifstatement408);
                    stream_ELSE.add(ELSE52);

                    pushFollow(FOLLOW_elseblock_in_ifstatement410);
                    elseblock53 = elseblock();

                    this.state._fsp--;

                    stream_elseblock.add(elseblock53.getTree());

                }
                    break;

                }

                // AST REWRITE
                // elements: TRY, block, elseblock
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                retval.tree = root_0;
                RewriteRuleSubtreeStream stream_retval =
                    new RewriteRuleSubtreeStream(this.adaptor, "rule retval",
                        retval != null ? retval.tree : null);

                root_0 = (CommonTree) this.adaptor.nil();
                // 123:35: -> ^( TRY block ( elseblock )? )
                {
                    // GCL.g:123:38: ^( TRY block ( elseblock )? )
                    {
                        CommonTree root_1 = (CommonTree) this.adaptor.nil();
                        root_1 =
                            (CommonTree) this.adaptor.becomeRoot(
                                stream_TRY.nextNode(), root_1);

                        this.adaptor.addChild(root_1, stream_block.nextTree());
                        // GCL.g:123:50: ( elseblock )?
                        if (stream_elseblock.hasNext()) {
                            this.adaptor.addChild(root_1,
                                stream_elseblock.nextTree());

                        }
                        stream_elseblock.reset();

                        this.adaptor.addChild(root_0, root_1);
                    }

                }

                retval.tree = root_0;
            }
                break;

            }
            retval.stop = this.input.LT(-1);

            retval.tree = (CommonTree) this.adaptor.rulePostProcessing(root_0);
            this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                retval.stop);

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

    // $ANTLR end "ifstatement"

    public static class elseblock_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "elseblock"
    // GCL.g:126:1: elseblock : ( block | ifstatement -> ^( BLOCK ifstatement ) );
    public final GCLParser.elseblock_return elseblock()
        throws RecognitionException {
        GCLParser.elseblock_return retval = new GCLParser.elseblock_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        GCLParser.block_return block54 = null;

        GCLParser.ifstatement_return ifstatement55 = null;

        RewriteRuleSubtreeStream stream_ifstatement =
            new RewriteRuleSubtreeStream(this.adaptor, "rule ifstatement");
        try {
            // GCL.g:127:5: ( block | ifstatement -> ^( BLOCK ifstatement ) )
            int alt11 = 2;
            int LA11_0 = this.input.LA(1);

            if ((LA11_0 == 49)) {
                alt11 = 1;
            } else if ((LA11_0 == IF || LA11_0 == TRY)) {
                alt11 = 2;
            } else {
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, this.input);

                throw nvae;
            }
            switch (alt11) {
            case 1:
                // GCL.g:127:7: block
            {
                root_0 = (CommonTree) this.adaptor.nil();

                pushFollow(FOLLOW_block_in_elseblock444);
                block54 = block();

                this.state._fsp--;

                this.adaptor.addChild(root_0, block54.getTree());

            }
                break;
            case 2:
                // GCL.g:128:7: ifstatement
            {
                pushFollow(FOLLOW_ifstatement_in_elseblock452);
                ifstatement55 = ifstatement();

                this.state._fsp--;

                stream_ifstatement.add(ifstatement55.getTree());

                // AST REWRITE
                // elements: ifstatement
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                retval.tree = root_0;
                RewriteRuleSubtreeStream stream_retval =
                    new RewriteRuleSubtreeStream(this.adaptor, "rule retval",
                        retval != null ? retval.tree : null);

                root_0 = (CommonTree) this.adaptor.nil();
                // 128:19: -> ^( BLOCK ifstatement )
                {
                    // GCL.g:128:22: ^( BLOCK ifstatement )
                    {
                        CommonTree root_1 = (CommonTree) this.adaptor.nil();
                        root_1 =
                            (CommonTree) this.adaptor.becomeRoot(
                                (CommonTree) this.adaptor.create(BLOCK, "BLOCK"),
                                root_1);

                        this.adaptor.addChild(root_1,
                            stream_ifstatement.nextTree());

                        this.adaptor.addChild(root_0, root_1);
                    }

                }

                retval.tree = root_0;
            }
                break;

            }
            retval.stop = this.input.LT(-1);

            retval.tree = (CommonTree) this.adaptor.rulePostProcessing(root_0);
            this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                retval.stop);

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

    // $ANTLR end "elseblock"

    public static class conditionliteral_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "conditionliteral"
    // GCL.g:131:1: conditionliteral : ( TRUE | call );
    public final GCLParser.conditionliteral_return conditionliteral()
        throws RecognitionException {
        GCLParser.conditionliteral_return retval =
            new GCLParser.conditionliteral_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token TRUE56 = null;
        GCLParser.call_return call57 = null;

        CommonTree TRUE56_tree = null;

        try {
            // GCL.g:132:2: ( TRUE | call )
            int alt12 = 2;
            int LA12_0 = this.input.LA(1);

            if ((LA12_0 == TRUE)) {
                alt12 = 1;
            } else if ((LA12_0 == IDENTIFIER)) {
                alt12 = 2;
            } else {
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, this.input);

                throw nvae;
            }
            switch (alt12) {
            case 1:
                // GCL.g:132:4: TRUE
            {
                root_0 = (CommonTree) this.adaptor.nil();

                TRUE56 =
                    (Token) match(this.input, TRUE,
                        FOLLOW_TRUE_in_conditionliteral479);
                TRUE56_tree = (CommonTree) this.adaptor.create(TRUE56);
                this.adaptor.addChild(root_0, TRUE56_tree);

            }
                break;
            case 2:
                // GCL.g:132:11: call
            {
                root_0 = (CommonTree) this.adaptor.nil();

                pushFollow(FOLLOW_call_in_conditionliteral483);
                call57 = call();

                this.state._fsp--;

                this.adaptor.addChild(root_0, call57.getTree());

            }
                break;

            }
            retval.stop = this.input.LT(-1);

            retval.tree = (CommonTree) this.adaptor.rulePostProcessing(root_0);
            this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                retval.stop);

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

    // $ANTLR end "conditionliteral"

    public static class expression_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "expression"
    // GCL.g:134:1: expression : expression2 ( OR expression )? ;
    public final GCLParser.expression_return expression()
        throws RecognitionException {
        GCLParser.expression_return retval = new GCLParser.expression_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token OR59 = null;
        GCLParser.expression2_return expression258 = null;

        GCLParser.expression_return expression60 = null;

        CommonTree OR59_tree = null;

        try {
            // GCL.g:135:2: ( expression2 ( OR expression )? )
            // GCL.g:135:4: expression2 ( OR expression )?
            {
                root_0 = (CommonTree) this.adaptor.nil();

                pushFollow(FOLLOW_expression2_in_expression494);
                expression258 = expression2();

                this.state._fsp--;

                this.adaptor.addChild(root_0, expression258.getTree());
                // GCL.g:135:16: ( OR expression )?
                int alt13 = 2;
                int LA13_0 = this.input.LA(1);

                if ((LA13_0 == OR)) {
                    alt13 = 1;
                }
                switch (alt13) {
                case 1:
                    // GCL.g:135:17: OR expression
                {
                    OR59 =
                        (Token) match(this.input, OR,
                            FOLLOW_OR_in_expression497);
                    OR59_tree = (CommonTree) this.adaptor.create(OR59);
                    root_0 =
                        (CommonTree) this.adaptor.becomeRoot(OR59_tree, root_0);

                    pushFollow(FOLLOW_expression_in_expression500);
                    expression60 = expression();

                    this.state._fsp--;

                    this.adaptor.addChild(root_0, expression60.getTree());

                }
                    break;

                }

            }

            retval.stop = this.input.LT(-1);

            retval.tree = (CommonTree) this.adaptor.rulePostProcessing(root_0);
            this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                retval.stop);

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

    // $ANTLR end "expression"

    public static class expression2_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "expression2"
    // GCL.g:138:1: expression2 : ( expression_atom ( PLUS | STAR )? | SHARP expression_atom );
    public final GCLParser.expression2_return expression2()
        throws RecognitionException {
        GCLParser.expression2_return retval =
            new GCLParser.expression2_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token PLUS62 = null;
        Token STAR63 = null;
        Token SHARP64 = null;
        GCLParser.expression_atom_return expression_atom61 = null;

        GCLParser.expression_atom_return expression_atom65 = null;

        CommonTree PLUS62_tree = null;
        CommonTree STAR63_tree = null;
        CommonTree SHARP64_tree = null;

        try {
            // GCL.g:139:5: ( expression_atom ( PLUS | STAR )? | SHARP expression_atom )
            int alt15 = 2;
            int LA15_0 = this.input.LA(1);

            if ((LA15_0 == IDENTIFIER || (LA15_0 >= ANY && LA15_0 <= OTHER) || LA15_0 == 51)) {
                alt15 = 1;
            } else if ((LA15_0 == SHARP)) {
                alt15 = 2;
            } else {
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, this.input);

                throw nvae;
            }
            switch (alt15) {
            case 1:
                // GCL.g:139:7: expression_atom ( PLUS | STAR )?
            {
                root_0 = (CommonTree) this.adaptor.nil();

                pushFollow(FOLLOW_expression_atom_in_expression2516);
                expression_atom61 = expression_atom();

                this.state._fsp--;

                this.adaptor.addChild(root_0, expression_atom61.getTree());
                // GCL.g:139:23: ( PLUS | STAR )?
                int alt14 = 3;
                int LA14_0 = this.input.LA(1);

                if ((LA14_0 == PLUS)) {
                    alt14 = 1;
                } else if ((LA14_0 == STAR)) {
                    alt14 = 2;
                }
                switch (alt14) {
                case 1:
                    // GCL.g:139:24: PLUS
                {
                    PLUS62 =
                        (Token) match(this.input, PLUS,
                            FOLLOW_PLUS_in_expression2519);
                    PLUS62_tree = (CommonTree) this.adaptor.create(PLUS62);
                    root_0 =
                        (CommonTree) this.adaptor.becomeRoot(PLUS62_tree,
                            root_0);

                }
                    break;
                case 2:
                    // GCL.g:139:32: STAR
                {
                    STAR63 =
                        (Token) match(this.input, STAR,
                            FOLLOW_STAR_in_expression2524);
                    STAR63_tree = (CommonTree) this.adaptor.create(STAR63);
                    root_0 =
                        (CommonTree) this.adaptor.becomeRoot(STAR63_tree,
                            root_0);

                }
                    break;

                }

            }
                break;
            case 2:
                // GCL.g:140:7: SHARP expression_atom
            {
                root_0 = (CommonTree) this.adaptor.nil();

                SHARP64 =
                    (Token) match(this.input, SHARP,
                        FOLLOW_SHARP_in_expression2535);
                SHARP64_tree = (CommonTree) this.adaptor.create(SHARP64);
                root_0 =
                    (CommonTree) this.adaptor.becomeRoot(SHARP64_tree, root_0);

                pushFollow(FOLLOW_expression_atom_in_expression2538);
                expression_atom65 = expression_atom();

                this.state._fsp--;

                this.adaptor.addChild(root_0, expression_atom65.getTree());

            }
                break;

            }
            retval.stop = this.input.LT(-1);

            retval.tree = (CommonTree) this.adaptor.rulePostProcessing(root_0);
            this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                retval.stop);

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

    // $ANTLR end "expression2"

    public static class expression_atom_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "expression_atom"
    // GCL.g:143:1: expression_atom : ( ANY | OTHER | '(' expression ')' | call );
    public final GCLParser.expression_atom_return expression_atom()
        throws RecognitionException {
        GCLParser.expression_atom_return retval =
            new GCLParser.expression_atom_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token ANY66 = null;
        Token OTHER67 = null;
        Token char_literal68 = null;
        Token char_literal70 = null;
        GCLParser.expression_return expression69 = null;

        GCLParser.call_return call71 = null;

        CommonTree ANY66_tree = null;
        CommonTree OTHER67_tree = null;
        CommonTree char_literal68_tree = null;
        CommonTree char_literal70_tree = null;

        try {
            // GCL.g:144:2: ( ANY | OTHER | '(' expression ')' | call )
            int alt16 = 4;
            switch (this.input.LA(1)) {
            case ANY: {
                alt16 = 1;
            }
                break;
            case OTHER: {
                alt16 = 2;
            }
                break;
            case 51: {
                alt16 = 3;
            }
                break;
            case IDENTIFIER: {
                alt16 = 4;
            }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, this.input);

                throw nvae;
            }

            switch (alt16) {
            case 1:
                // GCL.g:144:4: ANY
            {
                root_0 = (CommonTree) this.adaptor.nil();

                ANY66 =
                    (Token) match(this.input, ANY,
                        FOLLOW_ANY_in_expression_atom552);
                ANY66_tree = (CommonTree) this.adaptor.create(ANY66);
                this.adaptor.addChild(root_0, ANY66_tree);

            }
                break;
            case 2:
                // GCL.g:145:4: OTHER
            {
                root_0 = (CommonTree) this.adaptor.nil();

                OTHER67 =
                    (Token) match(this.input, OTHER,
                        FOLLOW_OTHER_in_expression_atom557);
                OTHER67_tree = (CommonTree) this.adaptor.create(OTHER67);
                this.adaptor.addChild(root_0, OTHER67_tree);

            }
                break;
            case 3:
                // GCL.g:146:4: '(' expression ')'
            {
                root_0 = (CommonTree) this.adaptor.nil();

                char_literal68 =
                    (Token) match(this.input, 51,
                        FOLLOW_51_in_expression_atom562);
                pushFollow(FOLLOW_expression_in_expression_atom565);
                expression69 = expression();

                this.state._fsp--;

                this.adaptor.addChild(root_0, expression69.getTree());
                char_literal70 =
                    (Token) match(this.input, 52,
                        FOLLOW_52_in_expression_atom567);

            }
                break;
            case 4:
                // GCL.g:147:4: call
            {
                root_0 = (CommonTree) this.adaptor.nil();

                pushFollow(FOLLOW_call_in_expression_atom573);
                call71 = call();

                this.state._fsp--;

                this.adaptor.addChild(root_0, call71.getTree());

            }
                break;

            }
            retval.stop = this.input.LT(-1);

            retval.tree = (CommonTree) this.adaptor.rulePostProcessing(root_0);
            this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                retval.stop);

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

    // $ANTLR end "expression_atom"

    public static class call_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "call"
    // GCL.g:150:1: call : ruleName ( '(' ( var_list )? ')' )? -> ^( CALL ( var_list )? ) ;
    public final GCLParser.call_return call() throws RecognitionException {
        GCLParser.call_return retval = new GCLParser.call_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token char_literal73 = null;
        Token char_literal75 = null;
        GCLParser.ruleName_return ruleName72 = null;

        GCLParser.var_list_return var_list74 = null;

        CommonTree char_literal73_tree = null;
        CommonTree char_literal75_tree = null;
        RewriteRuleTokenStream stream_51 =
            new RewriteRuleTokenStream(this.adaptor, "token 51");
        RewriteRuleTokenStream stream_52 =
            new RewriteRuleTokenStream(this.adaptor, "token 52");
        RewriteRuleSubtreeStream stream_ruleName =
            new RewriteRuleSubtreeStream(this.adaptor, "rule ruleName");
        RewriteRuleSubtreeStream stream_var_list =
            new RewriteRuleSubtreeStream(this.adaptor, "rule var_list");
        try {
            // GCL.g:151:2: ( ruleName ( '(' ( var_list )? ')' )? -> ^( CALL ( var_list )? ) )
            // GCL.g:151:4: ruleName ( '(' ( var_list )? ')' )?
            {
                pushFollow(FOLLOW_ruleName_in_call585);
                ruleName72 = ruleName();

                this.state._fsp--;

                stream_ruleName.add(ruleName72.getTree());
                // GCL.g:151:13: ( '(' ( var_list )? ')' )?
                int alt18 = 2;
                int LA18_0 = this.input.LA(1);

                if ((LA18_0 == 51)) {
                    alt18 = 1;
                }
                switch (alt18) {
                case 1:
                    // GCL.g:151:14: '(' ( var_list )? ')'
                {
                    char_literal73 =
                        (Token) match(this.input, 51, FOLLOW_51_in_call588);
                    stream_51.add(char_literal73);

                    // GCL.g:151:18: ( var_list )?
                    int alt17 = 2;
                    alt17 = this.dfa17.predict(this.input);
                    switch (alt17) {
                    case 1:
                        // GCL.g:151:18: var_list
                    {
                        pushFollow(FOLLOW_var_list_in_call590);
                        var_list74 = var_list();

                        this.state._fsp--;

                        stream_var_list.add(var_list74.getTree());

                    }
                        break;

                    }

                    char_literal75 =
                        (Token) match(this.input, 52, FOLLOW_52_in_call593);
                    stream_52.add(char_literal75);

                }
                    break;

                }

                // AST REWRITE
                // elements: var_list
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                retval.tree = root_0;
                RewriteRuleSubtreeStream stream_retval =
                    new RewriteRuleSubtreeStream(this.adaptor, "rule retval",
                        retval != null ? retval.tree : null);

                root_0 = (CommonTree) this.adaptor.nil();
                // 151:34: -> ^( CALL ( var_list )? )
                {
                    // GCL.g:151:37: ^( CALL ( var_list )? )
                    {
                        CommonTree root_1 = (CommonTree) this.adaptor.nil();
                        root_1 =
                            (CommonTree) this.adaptor.becomeRoot(
                                (CommonTree) this.adaptor.create(CALL, "CALL"),
                                root_1);

                        this.adaptor.addChild(root_1,
                            concat((ruleName72 != null
                                    ? ((CommonTree) ruleName72.tree) : null)));
                        // GCL.g:151:71: ( var_list )?
                        if (stream_var_list.hasNext()) {
                            this.adaptor.addChild(root_1,
                                stream_var_list.nextTree());

                        }
                        stream_var_list.reset();

                        this.adaptor.addChild(root_0, root_1);
                    }

                }

                retval.tree = root_0;
            }

            retval.stop = this.input.LT(-1);

            retval.tree = (CommonTree) this.adaptor.rulePostProcessing(root_0);
            this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                retval.stop);

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

    // $ANTLR end "call"

    public static class ruleName_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "ruleName"
    // GCL.g:153:1: ruleName : IDENTIFIER ( DOT IDENTIFIER )* ;
    public final GCLParser.ruleName_return ruleName()
        throws RecognitionException {
        GCLParser.ruleName_return retval = new GCLParser.ruleName_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token IDENTIFIER76 = null;
        Token DOT77 = null;
        Token IDENTIFIER78 = null;

        CommonTree IDENTIFIER76_tree = null;
        CommonTree DOT77_tree = null;
        CommonTree IDENTIFIER78_tree = null;

        try {
            // GCL.g:153:10: ( IDENTIFIER ( DOT IDENTIFIER )* )
            // GCL.g:153:12: IDENTIFIER ( DOT IDENTIFIER )*
            {
                root_0 = (CommonTree) this.adaptor.nil();

                IDENTIFIER76 =
                    (Token) match(this.input, IDENTIFIER,
                        FOLLOW_IDENTIFIER_in_ruleName614);
                IDENTIFIER76_tree =
                    (CommonTree) this.adaptor.create(IDENTIFIER76);
                this.adaptor.addChild(root_0, IDENTIFIER76_tree);

                // GCL.g:153:23: ( DOT IDENTIFIER )*
                loop19: do {
                    int alt19 = 2;
                    int LA19_0 = this.input.LA(1);

                    if ((LA19_0 == DOT)) {
                        alt19 = 1;
                    }

                    switch (alt19) {
                    case 1:
                        // GCL.g:153:24: DOT IDENTIFIER
                    {
                        DOT77 =
                            (Token) match(this.input, DOT,
                                FOLLOW_DOT_in_ruleName617);
                        DOT77_tree = (CommonTree) this.adaptor.create(DOT77);
                        this.adaptor.addChild(root_0, DOT77_tree);

                        IDENTIFIER78 =
                            (Token) match(this.input, IDENTIFIER,
                                FOLLOW_IDENTIFIER_in_ruleName619);
                        IDENTIFIER78_tree =
                            (CommonTree) this.adaptor.create(IDENTIFIER78);
                        this.adaptor.addChild(root_0, IDENTIFIER78_tree);

                    }
                        break;

                    default:
                        break loop19;
                    }
                } while (true);

            }

            retval.stop = this.input.LT(-1);

            retval.tree = (CommonTree) this.adaptor.rulePostProcessing(root_0);
            this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                retval.stop);

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

    // $ANTLR end "ruleName"

    public static class var_declaration_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "var_declaration"
    // GCL.g:155:1: var_declaration : var_type IDENTIFIER ( ',' IDENTIFIER )* -> ( ^( VAR var_type IDENTIFIER ) )+ ;
    public final GCLParser.var_declaration_return var_declaration()
        throws RecognitionException {
        GCLParser.var_declaration_return retval =
            new GCLParser.var_declaration_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token IDENTIFIER80 = null;
        Token char_literal81 = null;
        Token IDENTIFIER82 = null;
        GCLParser.var_type_return var_type79 = null;

        CommonTree IDENTIFIER80_tree = null;
        CommonTree char_literal81_tree = null;
        CommonTree IDENTIFIER82_tree = null;
        RewriteRuleTokenStream stream_COMMA =
            new RewriteRuleTokenStream(this.adaptor, "token COMMA");
        RewriteRuleTokenStream stream_IDENTIFIER =
            new RewriteRuleTokenStream(this.adaptor, "token IDENTIFIER");
        RewriteRuleSubtreeStream stream_var_type =
            new RewriteRuleSubtreeStream(this.adaptor, "rule var_type");
        try {
            // GCL.g:156:2: ( var_type IDENTIFIER ( ',' IDENTIFIER )* -> ( ^( VAR var_type IDENTIFIER ) )+ )
            // GCL.g:156:4: var_type IDENTIFIER ( ',' IDENTIFIER )*
            {
                pushFollow(FOLLOW_var_type_in_var_declaration630);
                var_type79 = var_type();

                this.state._fsp--;

                stream_var_type.add(var_type79.getTree());
                IDENTIFIER80 =
                    (Token) match(this.input, IDENTIFIER,
                        FOLLOW_IDENTIFIER_in_var_declaration632);
                stream_IDENTIFIER.add(IDENTIFIER80);

                // GCL.g:156:24: ( ',' IDENTIFIER )*
                loop20: do {
                    int alt20 = 2;
                    int LA20_0 = this.input.LA(1);

                    if ((LA20_0 == COMMA)) {
                        alt20 = 1;
                    }

                    switch (alt20) {
                    case 1:
                        // GCL.g:156:25: ',' IDENTIFIER
                    {
                        char_literal81 =
                            (Token) match(this.input, COMMA,
                                FOLLOW_COMMA_in_var_declaration635);
                        stream_COMMA.add(char_literal81);

                        IDENTIFIER82 =
                            (Token) match(this.input, IDENTIFIER,
                                FOLLOW_IDENTIFIER_in_var_declaration637);
                        stream_IDENTIFIER.add(IDENTIFIER82);

                    }
                        break;

                    default:
                        break loop20;
                    }
                } while (true);

                // AST REWRITE
                // elements: IDENTIFIER, var_type
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                retval.tree = root_0;
                RewriteRuleSubtreeStream stream_retval =
                    new RewriteRuleSubtreeStream(this.adaptor, "rule retval",
                        retval != null ? retval.tree : null);

                root_0 = (CommonTree) this.adaptor.nil();
                // 156:42: -> ( ^( VAR var_type IDENTIFIER ) )+
                {
                    if (!(stream_IDENTIFIER.hasNext() || stream_var_type.hasNext())) {
                        throw new RewriteEarlyExitException();
                    }
                    while (stream_IDENTIFIER.hasNext()
                        || stream_var_type.hasNext()) {
                        // GCL.g:156:45: ^( VAR var_type IDENTIFIER )
                        {
                            CommonTree root_1 = (CommonTree) this.adaptor.nil();
                            root_1 =
                                (CommonTree) this.adaptor.becomeRoot(
                                    (CommonTree) this.adaptor.create(VAR, "VAR"),
                                    root_1);

                            this.adaptor.addChild(root_1,
                                stream_var_type.nextTree());
                            this.adaptor.addChild(root_1,
                                stream_IDENTIFIER.nextNode());

                            this.adaptor.addChild(root_0, root_1);
                        }

                    }
                    stream_IDENTIFIER.reset();
                    stream_var_type.reset();

                }

                retval.tree = root_0;
            }

            retval.stop = this.input.LT(-1);

            retval.tree = (CommonTree) this.adaptor.rulePostProcessing(root_0);
            this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                retval.stop);

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

    // $ANTLR end "var_declaration"

    public static class var_type_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "var_type"
    // GCL.g:159:1: var_type : ( NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE );
    public final GCLParser.var_type_return var_type()
        throws RecognitionException {
        GCLParser.var_type_return retval = new GCLParser.var_type_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token set83 = null;

        CommonTree set83_tree = null;

        try {
            // GCL.g:160:2: ( NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE )
            // GCL.g:
            {
                root_0 = (CommonTree) this.adaptor.nil();

                set83 = (Token) this.input.LT(1);
                if ((this.input.LA(1) >= NODE_TYPE && this.input.LA(1) <= REAL_TYPE)) {
                    this.input.consume();
                    this.adaptor.addChild(root_0,
                        (CommonTree) this.adaptor.create(set83));
                    this.state.errorRecovery = false;
                } else {
                    MismatchedSetException mse =
                        new MismatchedSetException(null, this.input);
                    throw mse;
                }

            }

            retval.stop = this.input.LT(-1);

            retval.tree = (CommonTree) this.adaptor.rulePostProcessing(root_0);
            this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                retval.stop);

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

    // $ANTLR end "var_type"

    public static class var_list_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "var_list"
    // GCL.g:167:1: var_list : variable ( COMMA var_list )? ;
    public final GCLParser.var_list_return var_list()
        throws RecognitionException {
        GCLParser.var_list_return retval = new GCLParser.var_list_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token COMMA85 = null;
        GCLParser.variable_return variable84 = null;

        GCLParser.var_list_return var_list86 = null;

        CommonTree COMMA85_tree = null;

        try {
            // GCL.g:168:2: ( variable ( COMMA var_list )? )
            // GCL.g:168:4: variable ( COMMA var_list )?
            {
                root_0 = (CommonTree) this.adaptor.nil();

                pushFollow(FOLLOW_variable_in_var_list693);
                variable84 = variable();

                this.state._fsp--;

                this.adaptor.addChild(root_0, variable84.getTree());
                // GCL.g:168:13: ( COMMA var_list )?
                int alt21 = 2;
                int LA21_0 = this.input.LA(1);

                if ((LA21_0 == COMMA)) {
                    alt21 = 1;
                }
                switch (alt21) {
                case 1:
                    // GCL.g:168:14: COMMA var_list
                {
                    COMMA85 =
                        (Token) match(this.input, COMMA,
                            FOLLOW_COMMA_in_var_list696);
                    pushFollow(FOLLOW_var_list_in_var_list699);
                    var_list86 = var_list();

                    this.state._fsp--;

                    this.adaptor.addChild(root_0, var_list86.getTree());

                }
                    break;

                }

            }

            retval.stop = this.input.LT(-1);

            retval.tree = (CommonTree) this.adaptor.rulePostProcessing(root_0);
            this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                retval.stop);

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

    // $ANTLR end "var_list"

    public static class variable_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "variable"
    // GCL.g:171:1: variable : ( OUT IDENTIFIER -> ^( PARAM OUT IDENTIFIER ) | IDENTIFIER -> ^( PARAM IDENTIFIER ) | DONT_CARE -> ^( PARAM DONT_CARE ) | literal -> ^( PARAM literal ) );
    public final GCLParser.variable_return variable()
        throws RecognitionException {
        GCLParser.variable_return retval = new GCLParser.variable_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token OUT87 = null;
        Token IDENTIFIER88 = null;
        Token IDENTIFIER89 = null;
        Token DONT_CARE90 = null;
        GCLParser.literal_return literal91 = null;

        CommonTree OUT87_tree = null;
        CommonTree IDENTIFIER88_tree = null;
        CommonTree IDENTIFIER89_tree = null;
        CommonTree DONT_CARE90_tree = null;
        RewriteRuleTokenStream stream_DONT_CARE =
            new RewriteRuleTokenStream(this.adaptor, "token DONT_CARE");
        RewriteRuleTokenStream stream_OUT =
            new RewriteRuleTokenStream(this.adaptor, "token OUT");
        RewriteRuleTokenStream stream_IDENTIFIER =
            new RewriteRuleTokenStream(this.adaptor, "token IDENTIFIER");
        RewriteRuleSubtreeStream stream_literal =
            new RewriteRuleSubtreeStream(this.adaptor, "rule literal");
        try {
            // GCL.g:172:2: ( OUT IDENTIFIER -> ^( PARAM OUT IDENTIFIER ) | IDENTIFIER -> ^( PARAM IDENTIFIER ) | DONT_CARE -> ^( PARAM DONT_CARE ) | literal -> ^( PARAM literal ) )
            int alt22 = 4;
            alt22 = this.dfa22.predict(this.input);
            switch (alt22) {
            case 1:
                // GCL.g:172:4: OUT IDENTIFIER
            {
                OUT87 =
                    (Token) match(this.input, OUT, FOLLOW_OUT_in_variable713);
                stream_OUT.add(OUT87);

                IDENTIFIER88 =
                    (Token) match(this.input, IDENTIFIER,
                        FOLLOW_IDENTIFIER_in_variable715);
                stream_IDENTIFIER.add(IDENTIFIER88);

                // AST REWRITE
                // elements: IDENTIFIER, OUT
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                retval.tree = root_0;
                RewriteRuleSubtreeStream stream_retval =
                    new RewriteRuleSubtreeStream(this.adaptor, "rule retval",
                        retval != null ? retval.tree : null);

                root_0 = (CommonTree) this.adaptor.nil();
                // 172:19: -> ^( PARAM OUT IDENTIFIER )
                {
                    // GCL.g:172:22: ^( PARAM OUT IDENTIFIER )
                    {
                        CommonTree root_1 = (CommonTree) this.adaptor.nil();
                        root_1 =
                            (CommonTree) this.adaptor.becomeRoot(
                                (CommonTree) this.adaptor.create(PARAM, "PARAM"),
                                root_1);

                        this.adaptor.addChild(root_1, stream_OUT.nextNode());
                        this.adaptor.addChild(root_1,
                            stream_IDENTIFIER.nextNode());

                        this.adaptor.addChild(root_0, root_1);
                    }

                }

                retval.tree = root_0;
            }
                break;
            case 2:
                // GCL.g:173:4: IDENTIFIER
            {
                IDENTIFIER89 =
                    (Token) match(this.input, IDENTIFIER,
                        FOLLOW_IDENTIFIER_in_variable730);
                stream_IDENTIFIER.add(IDENTIFIER89);

                // AST REWRITE
                // elements: IDENTIFIER
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                retval.tree = root_0;
                RewriteRuleSubtreeStream stream_retval =
                    new RewriteRuleSubtreeStream(this.adaptor, "rule retval",
                        retval != null ? retval.tree : null);

                root_0 = (CommonTree) this.adaptor.nil();
                // 173:15: -> ^( PARAM IDENTIFIER )
                {
                    // GCL.g:173:18: ^( PARAM IDENTIFIER )
                    {
                        CommonTree root_1 = (CommonTree) this.adaptor.nil();
                        root_1 =
                            (CommonTree) this.adaptor.becomeRoot(
                                (CommonTree) this.adaptor.create(PARAM, "PARAM"),
                                root_1);

                        this.adaptor.addChild(root_1,
                            stream_IDENTIFIER.nextNode());

                        this.adaptor.addChild(root_0, root_1);
                    }

                }

                retval.tree = root_0;
            }
                break;
            case 3:
                // GCL.g:174:4: DONT_CARE
            {
                DONT_CARE90 =
                    (Token) match(this.input, DONT_CARE,
                        FOLLOW_DONT_CARE_in_variable743);
                stream_DONT_CARE.add(DONT_CARE90);

                // AST REWRITE
                // elements: DONT_CARE
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                retval.tree = root_0;
                RewriteRuleSubtreeStream stream_retval =
                    new RewriteRuleSubtreeStream(this.adaptor, "rule retval",
                        retval != null ? retval.tree : null);

                root_0 = (CommonTree) this.adaptor.nil();
                // 174:14: -> ^( PARAM DONT_CARE )
                {
                    // GCL.g:174:17: ^( PARAM DONT_CARE )
                    {
                        CommonTree root_1 = (CommonTree) this.adaptor.nil();
                        root_1 =
                            (CommonTree) this.adaptor.becomeRoot(
                                (CommonTree) this.adaptor.create(PARAM, "PARAM"),
                                root_1);

                        this.adaptor.addChild(root_1,
                            stream_DONT_CARE.nextNode());

                        this.adaptor.addChild(root_0, root_1);
                    }

                }

                retval.tree = root_0;
            }
                break;
            case 4:
                // GCL.g:175:4: literal
            {
                pushFollow(FOLLOW_literal_in_variable756);
                literal91 = literal();

                this.state._fsp--;

                stream_literal.add(literal91.getTree());

                // AST REWRITE
                // elements: literal
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                retval.tree = root_0;
                RewriteRuleSubtreeStream stream_retval =
                    new RewriteRuleSubtreeStream(this.adaptor, "rule retval",
                        retval != null ? retval.tree : null);

                root_0 = (CommonTree) this.adaptor.nil();
                // 175:12: -> ^( PARAM literal )
                {
                    // GCL.g:175:15: ^( PARAM literal )
                    {
                        CommonTree root_1 = (CommonTree) this.adaptor.nil();
                        root_1 =
                            (CommonTree) this.adaptor.becomeRoot(
                                (CommonTree) this.adaptor.create(PARAM, "PARAM"),
                                root_1);

                        this.adaptor.addChild(root_1, stream_literal.nextTree());

                        this.adaptor.addChild(root_0, root_1);
                    }

                }

                retval.tree = root_0;
            }
                break;

            }
            retval.stop = this.input.LT(-1);

            retval.tree = (CommonTree) this.adaptor.rulePostProcessing(root_0);
            this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                retval.stop);

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

    // $ANTLR end "variable"

    public static class literal_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "literal"
    // GCL.g:178:1: literal : ( TRUE -> BOOL_TYPE TRUE | FALSE -> BOOL_TYPE FALSE | STRING -> STRING_TYPE | integer -> INT_TYPE | real -> REAL_TYPE );
    public final GCLParser.literal_return literal() throws RecognitionException {
        GCLParser.literal_return retval = new GCLParser.literal_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token TRUE92 = null;
        Token FALSE93 = null;
        Token STRING94 = null;
        GCLParser.integer_return integer95 = null;

        GCLParser.real_return real96 = null;

        CommonTree TRUE92_tree = null;
        CommonTree FALSE93_tree = null;
        CommonTree STRING94_tree = null;
        RewriteRuleTokenStream stream_FALSE =
            new RewriteRuleTokenStream(this.adaptor, "token FALSE");
        RewriteRuleTokenStream stream_TRUE =
            new RewriteRuleTokenStream(this.adaptor, "token TRUE");
        RewriteRuleTokenStream stream_STRING =
            new RewriteRuleTokenStream(this.adaptor, "token STRING");
        RewriteRuleSubtreeStream stream_real =
            new RewriteRuleSubtreeStream(this.adaptor, "rule real");
        RewriteRuleSubtreeStream stream_integer =
            new RewriteRuleSubtreeStream(this.adaptor, "rule integer");
        try {
            // GCL.g:179:2: ( TRUE -> BOOL_TYPE TRUE | FALSE -> BOOL_TYPE FALSE | STRING -> STRING_TYPE | integer -> INT_TYPE | real -> REAL_TYPE )
            int alt23 = 5;
            alt23 = this.dfa23.predict(this.input);
            switch (alt23) {
            case 1:
                // GCL.g:179:4: TRUE
            {
                TRUE92 =
                    (Token) match(this.input, TRUE, FOLLOW_TRUE_in_literal776);
                stream_TRUE.add(TRUE92);

                // AST REWRITE
                // elements: TRUE
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                retval.tree = root_0;
                RewriteRuleSubtreeStream stream_retval =
                    new RewriteRuleSubtreeStream(this.adaptor, "rule retval",
                        retval != null ? retval.tree : null);

                root_0 = (CommonTree) this.adaptor.nil();
                // 179:9: -> BOOL_TYPE TRUE
                {
                    this.adaptor.addChild(
                        root_0,
                        (CommonTree) this.adaptor.create(BOOL_TYPE, "BOOL_TYPE"));
                    this.adaptor.addChild(root_0, stream_TRUE.nextNode());

                }

                retval.tree = root_0;
            }
                break;
            case 2:
                // GCL.g:180:4: FALSE
            {
                FALSE93 =
                    (Token) match(this.input, FALSE, FOLLOW_FALSE_in_literal787);
                stream_FALSE.add(FALSE93);

                // AST REWRITE
                // elements: FALSE
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                retval.tree = root_0;
                RewriteRuleSubtreeStream stream_retval =
                    new RewriteRuleSubtreeStream(this.adaptor, "rule retval",
                        retval != null ? retval.tree : null);

                root_0 = (CommonTree) this.adaptor.nil();
                // 180:10: -> BOOL_TYPE FALSE
                {
                    this.adaptor.addChild(
                        root_0,
                        (CommonTree) this.adaptor.create(BOOL_TYPE, "BOOL_TYPE"));
                    this.adaptor.addChild(root_0, stream_FALSE.nextNode());

                }

                retval.tree = root_0;
            }
                break;
            case 3:
                // GCL.g:181:4: STRING
            {
                STRING94 =
                    (Token) match(this.input, STRING,
                        FOLLOW_STRING_in_literal798);
                stream_STRING.add(STRING94);

                // AST REWRITE
                // elements: 
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                retval.tree = root_0;
                RewriteRuleSubtreeStream stream_retval =
                    new RewriteRuleSubtreeStream(this.adaptor, "rule retval",
                        retval != null ? retval.tree : null);

                root_0 = (CommonTree) this.adaptor.nil();
                // 181:11: -> STRING_TYPE
                {
                    this.adaptor.addChild(root_0,
                        (CommonTree) this.adaptor.create(STRING_TYPE,
                            "STRING_TYPE"));
                    this.adaptor.addChild(root_0, toUnquoted((STRING94 != null
                            ? STRING94.getText() : null)));

                }

                retval.tree = root_0;
            }
                break;
            case 4:
                // GCL.g:182:4: integer
            {
                pushFollow(FOLLOW_integer_in_literal809);
                integer95 = integer();

                this.state._fsp--;

                stream_integer.add(integer95.getTree());

                // AST REWRITE
                // elements: 
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                retval.tree = root_0;
                RewriteRuleSubtreeStream stream_retval =
                    new RewriteRuleSubtreeStream(this.adaptor, "rule retval",
                        retval != null ? retval.tree : null);

                root_0 = (CommonTree) this.adaptor.nil();
                // 182:12: -> INT_TYPE
                {
                    this.adaptor.addChild(root_0,
                        (CommonTree) this.adaptor.create(INT_TYPE, "INT_TYPE"));
                    this.adaptor.addChild(root_0, concat((integer95 != null
                            ? ((CommonTree) integer95.tree) : null)));

                }

                retval.tree = root_0;
            }
                break;
            case 5:
                // GCL.g:183:4: real
            {
                pushFollow(FOLLOW_real_in_literal820);
                real96 = real();

                this.state._fsp--;

                stream_real.add(real96.getTree());

                // AST REWRITE
                // elements: 
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                retval.tree = root_0;
                RewriteRuleSubtreeStream stream_retval =
                    new RewriteRuleSubtreeStream(this.adaptor, "rule retval",
                        retval != null ? retval.tree : null);

                root_0 = (CommonTree) this.adaptor.nil();
                // 183:9: -> REAL_TYPE
                {
                    this.adaptor.addChild(
                        root_0,
                        (CommonTree) this.adaptor.create(REAL_TYPE, "REAL_TYPE"));
                    this.adaptor.addChild(root_0, concat((real96 != null
                            ? ((CommonTree) real96.tree) : null)));

                }

                retval.tree = root_0;
            }
                break;

            }
            retval.stop = this.input.LT(-1);

            retval.tree = (CommonTree) this.adaptor.rulePostProcessing(root_0);
            this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                retval.stop);

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

    // $ANTLR end "literal"

    public static class dqText_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "dqText"
    // GCL.g:186:1: dqText : QUOTE dqContent QUOTE ->;
    public final GCLParser.dqText_return dqText() throws RecognitionException {
        GCLParser.dqText_return retval = new GCLParser.dqText_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token QUOTE97 = null;
        Token QUOTE99 = null;
        GCLParser.dqContent_return dqContent98 = null;

        CommonTree QUOTE97_tree = null;
        CommonTree QUOTE99_tree = null;
        RewriteRuleTokenStream stream_QUOTE =
            new RewriteRuleTokenStream(this.adaptor, "token QUOTE");
        RewriteRuleSubtreeStream stream_dqContent =
            new RewriteRuleSubtreeStream(this.adaptor, "rule dqContent");
        try {
            // GCL.g:187:4: ( QUOTE dqContent QUOTE ->)
            // GCL.g:187:6: QUOTE dqContent QUOTE
            {
                QUOTE97 =
                    (Token) match(this.input, QUOTE, FOLLOW_QUOTE_in_dqText839);
                stream_QUOTE.add(QUOTE97);

                pushFollow(FOLLOW_dqContent_in_dqText841);
                dqContent98 = dqContent();

                this.state._fsp--;

                stream_dqContent.add(dqContent98.getTree());
                QUOTE99 =
                    (Token) match(this.input, QUOTE, FOLLOW_QUOTE_in_dqText843);
                stream_QUOTE.add(QUOTE99);

                // AST REWRITE
                // elements: 
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                retval.tree = root_0;
                RewriteRuleSubtreeStream stream_retval =
                    new RewriteRuleSubtreeStream(this.adaptor, "rule retval",
                        retval != null ? retval.tree : null);

                root_0 = (CommonTree) this.adaptor.nil();
                // 187:28: ->
                {
                    this.adaptor.addChild(root_0, concat((dqContent98 != null
                            ? ((CommonTree) dqContent98.tree) : null)));

                }

                retval.tree = root_0;
            }

            retval.stop = this.input.LT(-1);

            retval.tree = (CommonTree) this.adaptor.rulePostProcessing(root_0);
            this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                retval.stop);

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
    // GCL.g:190:1: dqContent : ( dqTextChar )* ;
    public final GCLParser.dqContent_return dqContent()
        throws RecognitionException {
        GCLParser.dqContent_return retval = new GCLParser.dqContent_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        GCLParser.dqTextChar_return dqTextChar100 = null;

        try {
            // GCL.g:191:4: ( ( dqTextChar )* )
            // GCL.g:191:6: ( dqTextChar )*
            {
                root_0 = (CommonTree) this.adaptor.nil();

                // GCL.g:191:6: ( dqTextChar )*
                loop24: do {
                    int alt24 = 2;
                    int LA24_0 = this.input.LA(1);

                    if (((LA24_0 >= PROGRAM && LA24_0 <= STRING) || (LA24_0 >= BSLASH && LA24_0 <= 53))) {
                        alt24 = 1;
                    }

                    switch (alt24) {
                    case 1:
                        // GCL.g:191:6: dqTextChar
                    {
                        pushFollow(FOLLOW_dqTextChar_in_dqContent862);
                        dqTextChar100 = dqTextChar();

                        this.state._fsp--;

                        this.adaptor.addChild(root_0, dqTextChar100.getTree());

                    }
                        break;

                    default:
                        break loop24;
                    }
                } while (true);

            }

            retval.stop = this.input.LT(-1);

            retval.tree = (CommonTree) this.adaptor.rulePostProcessing(root_0);
            this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                retval.stop);

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
    // GCL.g:194:1: dqTextChar : (~ ( QUOTE | BSLASH ) | BSLASH ( BSLASH | QUOTE ) );
    public final GCLParser.dqTextChar_return dqTextChar()
        throws RecognitionException {
        GCLParser.dqTextChar_return retval = new GCLParser.dqTextChar_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token set101 = null;
        Token BSLASH102 = null;
        Token set103 = null;

        CommonTree set101_tree = null;
        CommonTree BSLASH102_tree = null;
        CommonTree set103_tree = null;

        try {
            // GCL.g:195:4: (~ ( QUOTE | BSLASH ) | BSLASH ( BSLASH | QUOTE ) )
            int alt25 = 2;
            int LA25_0 = this.input.LA(1);

            if (((LA25_0 >= PROGRAM && LA25_0 <= STRING) || (LA25_0 >= MINUS && LA25_0 <= 53))) {
                alt25 = 1;
            } else if ((LA25_0 == BSLASH)) {
                alt25 = 2;
            } else {
                NoViableAltException nvae =
                    new NoViableAltException("", 25, 0, this.input);

                throw nvae;
            }
            switch (alt25) {
            case 1:
                // GCL.g:195:6: ~ ( QUOTE | BSLASH )
            {
                root_0 = (CommonTree) this.adaptor.nil();

                set101 = (Token) this.input.LT(1);
                if ((this.input.LA(1) >= PROGRAM && this.input.LA(1) <= STRING)
                    || (this.input.LA(1) >= MINUS && this.input.LA(1) <= 53)) {
                    this.input.consume();
                    this.adaptor.addChild(root_0,
                        (CommonTree) this.adaptor.create(set101));
                    this.state.errorRecovery = false;
                } else {
                    MismatchedSetException mse =
                        new MismatchedSetException(null, this.input);
                    throw mse;
                }

            }
                break;
            case 2:
                // GCL.g:196:6: BSLASH ( BSLASH | QUOTE )
            {
                root_0 = (CommonTree) this.adaptor.nil();

                BSLASH102 =
                    (Token) match(this.input, BSLASH,
                        FOLLOW_BSLASH_in_dqTextChar890);
                BSLASH102_tree = (CommonTree) this.adaptor.create(BSLASH102);
                this.adaptor.addChild(root_0, BSLASH102_tree);

                set103 = (Token) this.input.LT(1);
                if ((this.input.LA(1) >= QUOTE && this.input.LA(1) <= BSLASH)) {
                    this.input.consume();
                    this.adaptor.addChild(root_0,
                        (CommonTree) this.adaptor.create(set103));
                    this.state.errorRecovery = false;
                } else {
                    MismatchedSetException mse =
                        new MismatchedSetException(null, this.input);
                    throw mse;
                }

            }
                break;

            }
            retval.stop = this.input.LT(-1);

            retval.tree = (CommonTree) this.adaptor.rulePostProcessing(root_0);
            this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                retval.stop);

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

    public static class real_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "real"
    // GCL.g:199:1: real : ( MINUS )? (n1= NUMBER )? DOT (n2= NUMBER )? ;
    public final GCLParser.real_return real() throws RecognitionException {
        GCLParser.real_return retval = new GCLParser.real_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token n1 = null;
        Token n2 = null;
        Token MINUS104 = null;
        Token DOT105 = null;

        CommonTree n1_tree = null;
        CommonTree n2_tree = null;
        CommonTree MINUS104_tree = null;
        CommonTree DOT105_tree = null;

        try {
            // GCL.g:200:2: ( ( MINUS )? (n1= NUMBER )? DOT (n2= NUMBER )? )
            // GCL.g:200:4: ( MINUS )? (n1= NUMBER )? DOT (n2= NUMBER )?
            {
                root_0 = (CommonTree) this.adaptor.nil();

                // GCL.g:200:4: ( MINUS )?
                int alt26 = 2;
                int LA26_0 = this.input.LA(1);

                if ((LA26_0 == MINUS)) {
                    alt26 = 1;
                }
                switch (alt26) {
                case 1:
                    // GCL.g:200:4: MINUS
                {
                    MINUS104 =
                        (Token) match(this.input, MINUS,
                            FOLLOW_MINUS_in_real909);
                    MINUS104_tree = (CommonTree) this.adaptor.create(MINUS104);
                    this.adaptor.addChild(root_0, MINUS104_tree);

                }
                    break;

                }

                // GCL.g:200:13: (n1= NUMBER )?
                int alt27 = 2;
                int LA27_0 = this.input.LA(1);

                if ((LA27_0 == NUMBER)) {
                    alt27 = 1;
                }
                switch (alt27) {
                case 1:
                    // GCL.g:200:13: n1= NUMBER
                {
                    n1 =
                        (Token) match(this.input, NUMBER,
                            FOLLOW_NUMBER_in_real914);
                    n1_tree = (CommonTree) this.adaptor.create(n1);
                    this.adaptor.addChild(root_0, n1_tree);

                }
                    break;

                }

                DOT105 = (Token) match(this.input, DOT, FOLLOW_DOT_in_real917);
                DOT105_tree = (CommonTree) this.adaptor.create(DOT105);
                this.adaptor.addChild(root_0, DOT105_tree);

                // GCL.g:200:28: (n2= NUMBER )?
                int alt28 = 2;
                int LA28_0 = this.input.LA(1);

                if ((LA28_0 == NUMBER)) {
                    alt28 = 1;
                }
                switch (alt28) {
                case 1:
                    // GCL.g:200:28: n2= NUMBER
                {
                    n2 =
                        (Token) match(this.input, NUMBER,
                            FOLLOW_NUMBER_in_real921);
                    n2_tree = (CommonTree) this.adaptor.create(n2);
                    this.adaptor.addChild(root_0, n2_tree);

                }
                    break;

                }

            }

            retval.stop = this.input.LT(-1);

            retval.tree = (CommonTree) this.adaptor.rulePostProcessing(root_0);
            this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                retval.stop);

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

    // $ANTLR end "real"

    public static class integer_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "integer"
    // GCL.g:202:1: integer : ( MINUS )? NUMBER ;
    public final GCLParser.integer_return integer() throws RecognitionException {
        GCLParser.integer_return retval = new GCLParser.integer_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token MINUS106 = null;
        Token NUMBER107 = null;

        CommonTree MINUS106_tree = null;
        CommonTree NUMBER107_tree = null;

        try {
            // GCL.g:203:2: ( ( MINUS )? NUMBER )
            // GCL.g:203:4: ( MINUS )? NUMBER
            {
                root_0 = (CommonTree) this.adaptor.nil();

                // GCL.g:203:4: ( MINUS )?
                int alt29 = 2;
                int LA29_0 = this.input.LA(1);

                if ((LA29_0 == MINUS)) {
                    alt29 = 1;
                }
                switch (alt29) {
                case 1:
                    // GCL.g:203:4: MINUS
                {
                    MINUS106 =
                        (Token) match(this.input, MINUS,
                            FOLLOW_MINUS_in_integer932);
                    MINUS106_tree = (CommonTree) this.adaptor.create(MINUS106);
                    this.adaptor.addChild(root_0, MINUS106_tree);

                }
                    break;

                }

                NUMBER107 =
                    (Token) match(this.input, NUMBER,
                        FOLLOW_NUMBER_in_integer935);
                NUMBER107_tree = (CommonTree) this.adaptor.create(NUMBER107);
                this.adaptor.addChild(root_0, NUMBER107_tree);

            }

            retval.stop = this.input.LT(-1);

            retval.tree = (CommonTree) this.adaptor.rulePostProcessing(root_0);
            this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                retval.stop);

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

    // $ANTLR end "integer"

    // Delegated rules

    protected DFA1 dfa1 = new DFA1(this);
    protected DFA2 dfa2 = new DFA2(this);
    protected DFA7 dfa7 = new DFA7(this);
    protected DFA6 dfa6 = new DFA6(this);
    protected DFA8 dfa8 = new DFA8(this);
    protected DFA9 dfa9 = new DFA9(this);
    protected DFA17 dfa17 = new DFA17(this);
    protected DFA22 dfa22 = new DFA22(this);
    protected DFA23 dfa23 = new DFA23(this);
    static final String DFA1_eotS = "\20\uffff";
    static final String DFA1_eofS = "\1\1\17\uffff";
    static final String DFA1_minS = "\1\7\17\uffff";
    static final String DFA1_maxS = "\1\63\17\uffff";
    static final String DFA1_acceptS = "\1\uffff\1\3\1\1\1\2\14\uffff";
    static final String DFA1_specialS = "\20\uffff}>";
    static final String[] DFA1_transitionS =
        {
            "\1\2\1\uffff\1\3\2\uffff\1\3\1\uffff\4\3\1\uffff\1\3\1\uffff"
                + "\1\3\3\uffff\3\3\1\uffff\5\3\21\uffff\1\3", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "", ""};

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
            return "()* loopback of 100:11: ( function | statement )*";
        }
    }

    static final String DFA2_eotS = "\17\uffff";
    static final String DFA2_eofS = "\17\uffff";
    static final String DFA2_minS = "\1\11\16\uffff";
    static final String DFA2_maxS = "\1\63\16\uffff";
    static final String DFA2_acceptS = "\1\uffff\1\2\1\1\14\uffff";
    static final String DFA2_specialS = "\17\uffff}>";
    static final String[] DFA2_transitionS =
        {
            "\1\2\2\uffff\1\2\1\uffff\4\2\1\uffff\1\2\1\uffff\1\2\3\uffff"
                + "\3\2\1\uffff\5\2\20\uffff\1\1\1\2", "", "", "", "", "", "",
            "", "", "", "", "", "", "", ""};

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
            return "()* loopback of 102:13: ( statement )*";
        }
    }

    static final String DFA7_eotS = "\16\uffff";
    static final String DFA7_eofS = "\16\uffff";
    static final String DFA7_minS = "\1\11\15\uffff";
    static final String DFA7_maxS = "\1\63\15\uffff";
    static final String DFA7_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\uffff\1\6\1\7\4\uffff\1\10";
    static final String DFA7_specialS = "\16\uffff}>";
    static final String[] DFA7_transitionS =
        {
            "\1\4\2\uffff\1\10\1\uffff\1\1\1\2\1\3\1\7\1\uffff\1\5\1\uffff"
                + "\1\5\3\uffff\3\10\1\uffff\5\15\21\uffff\1\10", "", "", "",
            "", "", "", "", "", "", "", "", "", ""};

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
            return "110:1: statement : ( ALAP block -> ^( ALAP block ) | WHILE '(' condition ')' ( DO )? block -> ^( WHILE condition block ) | UNTIL '(' condition ')' ( DO )? block -> ^( UNTIL condition block ) | DO block WHILE '(' condition ')' -> ^( DO block condition ) | ifstatement | CHOICE block ( CH_OR block )* -> ^( CHOICE ( block )+ ) | expression ';' -> expression | var_declaration ';' -> var_declaration );";
        }
    }

    static final String DFA6_eotS = "\22\uffff";
    static final String DFA6_eofS = "\1\1\21\uffff";
    static final String DFA6_minS = "\1\7\21\uffff";
    static final String DFA6_maxS = "\1\63\21\uffff";
    static final String DFA6_acceptS = "\1\uffff\1\2\17\uffff\1\1";
    static final String DFA6_specialS = "\22\uffff}>";
    static final String[] DFA6_transitionS =
        {
            "\1\1\1\uffff\1\1\2\uffff\1\1\1\uffff\4\1\1\21\1\1\1\uffff\1"
                + "\1\3\uffff\3\1\1\uffff\5\1\20\uffff\2\1", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "", "", "", ""};

    static final short[] DFA6_eot = DFA.unpackEncodedString(DFA6_eotS);
    static final short[] DFA6_eof = DFA.unpackEncodedString(DFA6_eofS);
    static final char[] DFA6_min =
        DFA.unpackEncodedStringToUnsignedChars(DFA6_minS);
    static final char[] DFA6_max =
        DFA.unpackEncodedStringToUnsignedChars(DFA6_maxS);
    static final short[] DFA6_accept = DFA.unpackEncodedString(DFA6_acceptS);
    static final short[] DFA6_special = DFA.unpackEncodedString(DFA6_specialS);
    static final short[][] DFA6_transition;

    static {
        int numStates = DFA6_transitionS.length;
        DFA6_transition = new short[numStates][];
        for (int i = 0; i < numStates; i++) {
            DFA6_transition[i] = DFA.unpackEncodedString(DFA6_transitionS[i]);
        }
    }

    class DFA6 extends DFA {

        public DFA6(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 6;
            this.eot = DFA6_eot;
            this.eof = DFA6_eof;
            this.min = DFA6_min;
            this.max = DFA6_max;
            this.accept = DFA6_accept;
            this.special = DFA6_special;
            this.transition = DFA6_transition;
        }

        public String getDescription() {
            return "()* loopback of 116:20: ( CH_OR block )*";
        }
    }

    static final String DFA8_eotS = "\22\uffff";
    static final String DFA8_eofS = "\1\2\21\uffff";
    static final String DFA8_minS = "\1\7\21\uffff";
    static final String DFA8_maxS = "\1\63\21\uffff";
    static final String DFA8_acceptS = "\1\uffff\1\1\1\2\17\uffff";
    static final String DFA8_specialS = "\22\uffff}>";
    static final String[] DFA8_transitionS =
        {
            "\1\2\1\uffff\1\2\2\uffff\1\2\1\uffff\4\2\1\uffff\1\2\1\1\1"
                + "\2\3\uffff\3\2\1\uffff\5\2\20\uffff\2\2", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "", "", "", ""};

    static final short[] DFA8_eot = DFA.unpackEncodedString(DFA8_eotS);
    static final short[] DFA8_eof = DFA.unpackEncodedString(DFA8_eofS);
    static final char[] DFA8_min =
        DFA.unpackEncodedStringToUnsignedChars(DFA8_minS);
    static final char[] DFA8_max =
        DFA.unpackEncodedStringToUnsignedChars(DFA8_maxS);
    static final short[] DFA8_accept = DFA.unpackEncodedString(DFA8_acceptS);
    static final short[] DFA8_special = DFA.unpackEncodedString(DFA8_specialS);
    static final short[][] DFA8_transition;

    static {
        int numStates = DFA8_transitionS.length;
        DFA8_transition = new short[numStates][];
        for (int i = 0; i < numStates; i++) {
            DFA8_transition[i] = DFA.unpackEncodedString(DFA8_transitionS[i]);
        }
    }

    class DFA8 extends DFA {

        public DFA8(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 8;
            this.eot = DFA8_eot;
            this.eof = DFA8_eof;
            this.min = DFA8_min;
            this.max = DFA8_max;
            this.accept = DFA8_accept;
            this.special = DFA8_special;
            this.transition = DFA8_transition;
        }

        public String getDescription() {
            return "122:34: ( ELSE elseblock )?";
        }
    }

    static final String DFA9_eotS = "\22\uffff";
    static final String DFA9_eofS = "\1\2\21\uffff";
    static final String DFA9_minS = "\1\7\21\uffff";
    static final String DFA9_maxS = "\1\63\21\uffff";
    static final String DFA9_acceptS = "\1\uffff\1\1\1\2\17\uffff";
    static final String DFA9_specialS = "\22\uffff}>";
    static final String[] DFA9_transitionS =
        {
            "\1\2\1\uffff\1\2\2\uffff\1\2\1\uffff\4\2\1\uffff\1\2\1\1\1"
                + "\2\3\uffff\3\2\1\uffff\5\2\20\uffff\2\2", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "", "", "", ""};

    static final short[] DFA9_eot = DFA.unpackEncodedString(DFA9_eotS);
    static final short[] DFA9_eof = DFA.unpackEncodedString(DFA9_eofS);
    static final char[] DFA9_min =
        DFA.unpackEncodedStringToUnsignedChars(DFA9_minS);
    static final char[] DFA9_max =
        DFA.unpackEncodedStringToUnsignedChars(DFA9_maxS);
    static final short[] DFA9_accept = DFA.unpackEncodedString(DFA9_acceptS);
    static final short[] DFA9_special = DFA.unpackEncodedString(DFA9_specialS);
    static final short[][] DFA9_transition;

    static {
        int numStates = DFA9_transitionS.length;
        DFA9_transition = new short[numStates][];
        for (int i = 0; i < numStates; i++) {
            DFA9_transition[i] = DFA.unpackEncodedString(DFA9_transitionS[i]);
        }
    }

    class DFA9 extends DFA {

        public DFA9(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 9;
            this.eot = DFA9_eot;
            this.eof = DFA9_eof;
            this.min = DFA9_min;
            this.max = DFA9_max;
            this.accept = DFA9_accept;
            this.special = DFA9_special;
            this.transition = DFA9_transition;
        }

        public String getDescription() {
            return "123:17: ( ELSE elseblock )?";
        }
    }

    static final String DFA17_eotS = "\13\uffff";
    static final String DFA17_eofS = "\13\uffff";
    static final String DFA17_minS = "\1\14\12\uffff";
    static final String DFA17_maxS = "\1\64\12\uffff";
    static final String DFA17_acceptS = "\1\uffff\1\1\10\uffff\1\2";
    static final String DFA17_specialS = "\13\uffff}>";
    static final String[] DFA17_transitionS =
        {
            "\1\1\11\uffff\1\1\5\uffff\1\1\6\uffff\4\1\2\uffff\2\1\11\uffff"
                + "\1\12", "", "", "", "", "", "", "", "", "", ""};

    static final short[] DFA17_eot = DFA.unpackEncodedString(DFA17_eotS);
    static final short[] DFA17_eof = DFA.unpackEncodedString(DFA17_eofS);
    static final char[] DFA17_min =
        DFA.unpackEncodedStringToUnsignedChars(DFA17_minS);
    static final char[] DFA17_max =
        DFA.unpackEncodedStringToUnsignedChars(DFA17_maxS);
    static final short[] DFA17_accept = DFA.unpackEncodedString(DFA17_acceptS);
    static final short[] DFA17_special =
        DFA.unpackEncodedString(DFA17_specialS);
    static final short[][] DFA17_transition;

    static {
        int numStates = DFA17_transitionS.length;
        DFA17_transition = new short[numStates][];
        for (int i = 0; i < numStates; i++) {
            DFA17_transition[i] = DFA.unpackEncodedString(DFA17_transitionS[i]);
        }
    }

    class DFA17 extends DFA {

        public DFA17(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 17;
            this.eot = DFA17_eot;
            this.eof = DFA17_eof;
            this.min = DFA17_min;
            this.max = DFA17_max;
            this.accept = DFA17_accept;
            this.special = DFA17_special;
            this.transition = DFA17_transition;
        }

        public String getDescription() {
            return "151:18: ( var_list )?";
        }
    }

    static final String DFA22_eotS = "\12\uffff";
    static final String DFA22_eofS = "\12\uffff";
    static final String DFA22_minS = "\1\14\11\uffff";
    static final String DFA22_maxS = "\1\52\11\uffff";
    static final String DFA22_acceptS = "\1\uffff\1\1\1\2\1\3\1\4\5\uffff";
    static final String DFA22_specialS = "\12\uffff}>";
    static final String[] DFA22_transitionS =
        {"\1\2\11\uffff\1\4\5\uffff\1\4\6\uffff\1\1\1\3\2\4\2\uffff\2" + "\4",
            "", "", "", "", "", "", "", "", ""};

    static final short[] DFA22_eot = DFA.unpackEncodedString(DFA22_eotS);
    static final short[] DFA22_eof = DFA.unpackEncodedString(DFA22_eofS);
    static final char[] DFA22_min =
        DFA.unpackEncodedStringToUnsignedChars(DFA22_minS);
    static final char[] DFA22_max =
        DFA.unpackEncodedStringToUnsignedChars(DFA22_maxS);
    static final short[] DFA22_accept = DFA.unpackEncodedString(DFA22_acceptS);
    static final short[] DFA22_special =
        DFA.unpackEncodedString(DFA22_specialS);
    static final short[][] DFA22_transition;

    static {
        int numStates = DFA22_transitionS.length;
        DFA22_transition = new short[numStates][];
        for (int i = 0; i < numStates; i++) {
            DFA22_transition[i] = DFA.unpackEncodedString(DFA22_transitionS[i]);
        }
    }

    class DFA22 extends DFA {

        public DFA22(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 22;
            this.eot = DFA22_eot;
            this.eof = DFA22_eof;
            this.min = DFA22_min;
            this.max = DFA22_max;
            this.accept = DFA22_accept;
            this.special = DFA22_special;
            this.transition = DFA22_transition;
        }

        public String getDescription() {
            return "171:1: variable : ( OUT IDENTIFIER -> ^( PARAM OUT IDENTIFIER ) | IDENTIFIER -> ^( PARAM IDENTIFIER ) | DONT_CARE -> ^( PARAM DONT_CARE ) | literal -> ^( PARAM literal ) );";
        }
    }

    static final String DFA23_eotS = "\17\uffff";
    static final String DFA23_eofS = "\17\uffff";
    static final String DFA23_minS = "\1\26\3\uffff\2\34\1\uffff\1\34\7\uffff";
    static final String DFA23_maxS =
        "\1\52\3\uffff\1\52\1\64\1\uffff\1\64\7\uffff";
    static final String DFA23_acceptS =
        "\1\uffff\1\1\1\2\1\3\2\uffff\1\5\3\uffff\1\4\4\uffff";
    static final String DFA23_specialS = "\17\uffff}>";
    static final String[] DFA23_transitionS =
        {"\1\1\5\uffff\1\6\10\uffff\1\2\1\3\2\uffff\1\4\1\5", "", "", "",
            "\1\6\15\uffff\1\7", "\1\6\5\uffff\1\12\21\uffff\1\12", "",
            "\1\6\5\uffff\1\12\21\uffff\1\12", "", "", "", "", "", "", ""};

    static final short[] DFA23_eot = DFA.unpackEncodedString(DFA23_eotS);
    static final short[] DFA23_eof = DFA.unpackEncodedString(DFA23_eofS);
    static final char[] DFA23_min =
        DFA.unpackEncodedStringToUnsignedChars(DFA23_minS);
    static final char[] DFA23_max =
        DFA.unpackEncodedStringToUnsignedChars(DFA23_maxS);
    static final short[] DFA23_accept = DFA.unpackEncodedString(DFA23_acceptS);
    static final short[] DFA23_special =
        DFA.unpackEncodedString(DFA23_specialS);
    static final short[][] DFA23_transition;

    static {
        int numStates = DFA23_transitionS.length;
        DFA23_transition = new short[numStates][];
        for (int i = 0; i < numStates; i++) {
            DFA23_transition[i] = DFA.unpackEncodedString(DFA23_transitionS[i]);
        }
    }

    class DFA23 extends DFA {

        public DFA23(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 23;
            this.eot = DFA23_eot;
            this.eof = DFA23_eof;
            this.min = DFA23_min;
            this.max = DFA23_max;
            this.accept = DFA23_accept;
            this.special = DFA23_special;
            this.transition = DFA23_transition;
        }

        public String getDescription() {
            return "178:1: literal : ( TRUE -> BOOL_TYPE TRUE | FALSE -> BOOL_TYPE FALSE | STRING -> STRING_TYPE | integer -> INT_TYPE | real -> REAL_TYPE );";
        }
    }

    public static final BitSet FOLLOW_function_in_program106 =
        new BitSet(new long[] {0x00080003EE2BD282L});
    public static final BitSet FOLLOW_statement_in_program108 =
        new BitSet(new long[] {0x00080003EE2BD282L});
    public static final BitSet FOLLOW_49_in_block138 =
        new BitSet(new long[] {0x000C0003EE2BD280L});
    public static final BitSet FOLLOW_statement_in_block140 =
        new BitSet(new long[] {0x000C0003EE2BD280L});
    public static final BitSet FOLLOW_50_in_block144 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function161 =
        new BitSet(new long[] {0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_function163 =
        new BitSet(new long[] {0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_function165 =
        new BitSet(new long[] {0x0010000000000000L});
    public static final BitSet FOLLOW_52_in_function167 =
        new BitSet(new long[] {0x0002000000000000L});
    public static final BitSet FOLLOW_block_in_function169 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_conditionliteral_in_condition188 =
        new BitSet(new long[] {0x0000000000002002L});
    public static final BitSet FOLLOW_OR_in_condition191 =
        new BitSet(new long[] {0x000800000C401000L});
    public static final BitSet FOLLOW_condition_in_condition194 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_statement209 =
        new BitSet(new long[] {0x0002000000000000L});
    public static final BitSet FOLLOW_block_in_statement211 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_statement224 =
        new BitSet(new long[] {0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_statement226 =
        new BitSet(new long[] {0x000800000C401000L});
    public static final BitSet FOLLOW_condition_in_statement228 =
        new BitSet(new long[] {0x0010000000000000L});
    public static final BitSet FOLLOW_52_in_statement230 =
        new BitSet(new long[] {0x0002000000000200L});
    public static final BitSet FOLLOW_DO_in_statement232 =
        new BitSet(new long[] {0x0002000000000000L});
    public static final BitSet FOLLOW_block_in_statement235 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_statement250 =
        new BitSet(new long[] {0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_statement252 =
        new BitSet(new long[] {0x000800000C401000L});
    public static final BitSet FOLLOW_condition_in_statement254 =
        new BitSet(new long[] {0x0010000000000000L});
    public static final BitSet FOLLOW_52_in_statement256 =
        new BitSet(new long[] {0x0002000000000200L});
    public static final BitSet FOLLOW_DO_in_statement258 =
        new BitSet(new long[] {0x0002000000000000L});
    public static final BitSet FOLLOW_block_in_statement261 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_DO_in_statement276 =
        new BitSet(new long[] {0x0002000000000000L});
    public static final BitSet FOLLOW_block_in_statement278 =
        new BitSet(new long[] {0x0000000000008000L});
    public static final BitSet FOLLOW_WHILE_in_statement280 =
        new BitSet(new long[] {0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_statement282 =
        new BitSet(new long[] {0x000800000C401000L});
    public static final BitSet FOLLOW_condition_in_statement284 =
        new BitSet(new long[] {0x0010000000000000L});
    public static final BitSet FOLLOW_52_in_statement286 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_ifstatement_in_statement301 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_CHOICE_in_statement309 =
        new BitSet(new long[] {0x0002000000000000L});
    public static final BitSet FOLLOW_block_in_statement311 =
        new BitSet(new long[] {0x0000000000040002L});
    public static final BitSet FOLLOW_CH_OR_in_statement314 =
        new BitSet(new long[] {0x0002000000000000L});
    public static final BitSet FOLLOW_block_in_statement316 =
        new BitSet(new long[] {0x0000000000040002L});
    public static final BitSet FOLLOW_expression_in_statement332 =
        new BitSet(new long[] {0x0020000000000000L});
    public static final BitSet FOLLOW_53_in_statement334 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_var_declaration_in_statement343 =
        new BitSet(new long[] {0x0020000000000000L});
    public static final BitSet FOLLOW_53_in_statement345 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_ifstatement367 =
        new BitSet(new long[] {0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_ifstatement369 =
        new BitSet(new long[] {0x000800000C401000L});
    public static final BitSet FOLLOW_condition_in_ifstatement371 =
        new BitSet(new long[] {0x0010000000000000L});
    public static final BitSet FOLLOW_52_in_ifstatement373 =
        new BitSet(new long[] {0x0002000000000000L});
    public static final BitSet FOLLOW_block_in_ifstatement375 =
        new BitSet(new long[] {0x0000000000100002L});
    public static final BitSet FOLLOW_ELSE_in_ifstatement378 =
        new BitSet(new long[] {0x0002000000280000L});
    public static final BitSet FOLLOW_elseblock_in_ifstatement380 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_TRY_in_ifstatement403 =
        new BitSet(new long[] {0x0002000000000000L});
    public static final BitSet FOLLOW_block_in_ifstatement405 =
        new BitSet(new long[] {0x0000000000100002L});
    public static final BitSet FOLLOW_ELSE_in_ifstatement408 =
        new BitSet(new long[] {0x0002000000280000L});
    public static final BitSet FOLLOW_elseblock_in_ifstatement410 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_elseblock444 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_ifstatement_in_elseblock452 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_conditionliteral479 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_conditionliteral483 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_expression2_in_expression494 =
        new BitSet(new long[] {0x0000000000002002L});
    public static final BitSet FOLLOW_OR_in_expression497 =
        new BitSet(new long[] {0x000800000E001000L});
    public static final BitSet FOLLOW_expression_in_expression500 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_expression_atom_in_expression2516 =
        new BitSet(new long[] {0x0000000001800002L});
    public static final BitSet FOLLOW_PLUS_in_expression2519 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_STAR_in_expression2524 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_SHARP_in_expression2535 =
        new BitSet(new long[] {0x000800000C001000L});
    public static final BitSet FOLLOW_expression_atom_in_expression2538 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_expression_atom552 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_expression_atom557 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_expression_atom562 =
        new BitSet(new long[] {0x000800000E001000L});
    public static final BitSet FOLLOW_expression_in_expression_atom565 =
        new BitSet(new long[] {0x0010000000000000L});
    public static final BitSet FOLLOW_52_in_expression_atom567 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_expression_atom573 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_ruleName_in_call585 =
        new BitSet(new long[] {0x0008000000000002L});
    public static final BitSet FOLLOW_51_in_call588 =
        new BitSet(new long[] {0x0010067810401000L});
    public static final BitSet FOLLOW_var_list_in_call590 =
        new BitSet(new long[] {0x0010000000000000L});
    public static final BitSet FOLLOW_52_in_call593 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_ruleName614 =
        new BitSet(new long[] {0x0000000010000002L});
    public static final BitSet FOLLOW_DOT_in_ruleName617 =
        new BitSet(new long[] {0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_ruleName619 =
        new BitSet(new long[] {0x0000000010000002L});
    public static final BitSet FOLLOW_var_type_in_var_declaration630 =
        new BitSet(new long[] {0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_var_declaration632 =
        new BitSet(new long[] {0x0000000400000002L});
    public static final BitSet FOLLOW_COMMA_in_var_declaration635 =
        new BitSet(new long[] {0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_var_declaration637 =
        new BitSet(new long[] {0x0000000400000002L});
    public static final BitSet FOLLOW_set_in_var_type0 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_variable_in_var_list693 =
        new BitSet(new long[] {0x0000000400000002L});
    public static final BitSet FOLLOW_COMMA_in_var_list696 =
        new BitSet(new long[] {0x0000067810401000L});
    public static final BitSet FOLLOW_var_list_in_var_list699 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_OUT_in_variable713 =
        new BitSet(new long[] {0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_variable715 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_variable730 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_DONT_CARE_in_variable743 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_variable756 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_literal776 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_literal787 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal798 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_integer_in_literal809 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_real_in_literal820 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_QUOTE_in_dqText839 =
        new BitSet(new long[] {0x003FFFFFFFFFFFF0L});
    public static final BitSet FOLLOW_dqContent_in_dqText841 =
        new BitSet(new long[] {0x0000008000000000L});
    public static final BitSet FOLLOW_QUOTE_in_dqText843 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_dqTextChar_in_dqContent862 =
        new BitSet(new long[] {0x003FFF7FFFFFFFF2L});
    public static final BitSet FOLLOW_set_in_dqTextChar878 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_BSLASH_in_dqTextChar890 =
        new BitSet(new long[] {0x0000018000000000L});
    public static final BitSet FOLLOW_set_in_dqTextChar892 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_real909 =
        new BitSet(new long[] {0x0000040010000000L});
    public static final BitSet FOLLOW_NUMBER_in_real914 =
        new BitSet(new long[] {0x0000000010000000L});
    public static final BitSet FOLLOW_DOT_in_real917 =
        new BitSet(new long[] {0x0000040000000002L});
    public static final BitSet FOLLOW_NUMBER_in_real921 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_integer932 =
        new BitSet(new long[] {0x0000040000000000L});
    public static final BitSet FOLLOW_NUMBER_in_integer935 =
        new BitSet(new long[] {0x0000000000000002L});

}
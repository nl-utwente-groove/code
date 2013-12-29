// $ANTLR 3.4 E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g 2013-12-28 15:17:43

package groove.algebra.syntax;
import groove.grammar.model.FormatErrorSet;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

import org.antlr.runtime.tree.*;


@SuppressWarnings({"all", "warnings", "unchecked"})
public class ExprParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "AMP", "ASSIGN", "ASTERISK", "BAR", "BECOMES", "BOOL", "BSLASH", "CALL", "COLON", "COMMA", "CONST", "DOLLAR", "DONT_CARE", "DOT", "EQ", "EscapeSequence", "FALSE", "FIELD", "GE", "GT", "ID", "INT", "LCURLY", "LE", "LPAR", "LT", "MINUS", "NAT_LIT", "NEQ", "NOT", "Naturalumber", "NonIntegerNumber", "OPER", "PAR", "PERCENT", "PLUS", "QUOTE", "RCURLY", "REAL", "REAL_LIT", "RPAR", "SEMI", "SHARP", "SLASH", "STRING", "STRING_LIT", "TRUE", "WS"
    };

    public static final int EOF=-1;
    public static final int AMP=4;
    public static final int ASSIGN=5;
    public static final int ASTERISK=6;
    public static final int BAR=7;
    public static final int BECOMES=8;
    public static final int BOOL=9;
    public static final int BSLASH=10;
    public static final int CALL=11;
    public static final int COLON=12;
    public static final int COMMA=13;
    public static final int CONST=14;
    public static final int DOLLAR=15;
    public static final int DONT_CARE=16;
    public static final int DOT=17;
    public static final int EQ=18;
    public static final int EscapeSequence=19;
    public static final int FALSE=20;
    public static final int FIELD=21;
    public static final int GE=22;
    public static final int GT=23;
    public static final int ID=24;
    public static final int INT=25;
    public static final int LCURLY=26;
    public static final int LE=27;
    public static final int LPAR=28;
    public static final int LT=29;
    public static final int MINUS=30;
    public static final int NAT_LIT=31;
    public static final int NEQ=32;
    public static final int NOT=33;
    public static final int Naturalumber=34;
    public static final int NonIntegerNumber=35;
    public static final int OPER=36;
    public static final int PAR=37;
    public static final int PERCENT=38;
    public static final int PLUS=39;
    public static final int QUOTE=40;
    public static final int RCURLY=41;
    public static final int REAL=42;
    public static final int REAL_LIT=43;
    public static final int RPAR=44;
    public static final int SEMI=45;
    public static final int SHARP=46;
    public static final int SLASH=47;
    public static final int STRING=48;
    public static final int STRING_LIT=49;
    public static final int TRUE=50;
    public static final int WS=51;

    // delegates
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public ExprParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public ExprParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

protected TreeAdaptor adaptor = new CommonTreeAdaptor();

public void setTreeAdaptor(TreeAdaptor adaptor) {
    this.adaptor = adaptor;
}
public TreeAdaptor getTreeAdaptor() {
    return adaptor;
}
    public String[] getTokenNames() { return ExprParser.tokenNames; }
    public String getGrammarFileName() { return "E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g"; }


        private FormatErrorSet errors = new FormatErrorSet();
        
        public void displayRecognitionError(String[] tokenNames,
                RecognitionException e) {
            String hdr = getErrorHeader(e);
            String msg = getErrorMessage(e, tokenNames);
            this.errors.add(hdr + " " + msg, e.line, e.charPositionInLine);
        }

        public FormatErrorSet getErrors() {
            return this.errors;
        }
        
        /** Instantiates the parser for a given string. */
        public static ExprParser instance(String term) {
            ANTLRStringStream input = new ANTLRStringStream(term);
            ExprLexer lexer = new ExprLexer(input);
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            ExprParser parser = new ExprParser(tokenStream);
            parser.setTreeAdaptor(new ExprTreeAdaptor(tokenStream));
            return parser;
        }


    public static class assignment_return extends ParserRuleReturnScope {
        ExprTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "assignment"
    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:55:1: assignment : ID ASSIGN ^ expression ;
    public final ExprParser.assignment_return assignment() throws RecognitionException {
        ExprParser.assignment_return retval = new ExprParser.assignment_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token ID1=null;
        Token ASSIGN2=null;
        ExprParser.expression_return expression3 =null;


        ExprTree ID1_tree=null;
        ExprTree ASSIGN2_tree=null;

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:56:3: ( ID ASSIGN ^ expression )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:56:5: ID ASSIGN ^ expression
            {
            root_0 = (ExprTree)adaptor.nil();


            ID1=(Token)match(input,ID,FOLLOW_ID_in_assignment120); 
            ID1_tree = 
            (ExprTree)adaptor.create(ID1)
            ;
            adaptor.addChild(root_0, ID1_tree);


            ASSIGN2=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_assignment122); 
            ASSIGN2_tree = 
            (ExprTree)adaptor.create(ASSIGN2)
            ;
            root_0 = (ExprTree)adaptor.becomeRoot(ASSIGN2_tree, root_0);


            pushFollow(FOLLOW_expression_in_assignment125);
            expression3=expression();

            state._fsp--;

            adaptor.addChild(root_0, expression3.getTree());

            }

            retval.stop = input.LT(-1);


            retval.tree = (ExprTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (ExprTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "assignment"


    public static class test_expression_return extends ParserRuleReturnScope {
        ExprTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "test_expression"
    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:60:1: test_expression : ( ID op= ASSIGN expression -> ^( EQ[$op,\"==\"] ^( FIELD ID ) expression ) | expression );
    public final ExprParser.test_expression_return test_expression() throws RecognitionException {
        ExprParser.test_expression_return retval = new ExprParser.test_expression_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token op=null;
        Token ID4=null;
        ExprParser.expression_return expression5 =null;

        ExprParser.expression_return expression6 =null;


        ExprTree op_tree=null;
        ExprTree ID4_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_ASSIGN=new RewriteRuleTokenStream(adaptor,"token ASSIGN");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:61:3: ( ID op= ASSIGN expression -> ^( EQ[$op,\"==\"] ^( FIELD ID ) expression ) | expression )
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==ID) ) {
                int LA1_1 = input.LA(2);

                if ( (LA1_1==ASSIGN) ) {
                    alt1=1;
                }
                else if ( (LA1_1==EOF||LA1_1==AMP||(LA1_1 >= ASTERISK && LA1_1 <= BAR)||LA1_1==COLON||(LA1_1 >= DOT && LA1_1 <= EQ)||(LA1_1 >= GE && LA1_1 <= GT)||(LA1_1 >= LE && LA1_1 <= MINUS)||LA1_1==NEQ||(LA1_1 >= PERCENT && LA1_1 <= PLUS)||LA1_1==SLASH) ) {
                    alt1=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 1, 1, input);

                    throw nvae;

                }
            }
            else if ( (LA1_0==FALSE||LA1_0==LPAR||(LA1_0 >= MINUS && LA1_0 <= NAT_LIT)||LA1_0==NOT||LA1_0==REAL_LIT||(LA1_0 >= STRING_LIT && LA1_0 <= TRUE)) ) {
                alt1=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;

            }
            switch (alt1) {
                case 1 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:63:5: ID op= ASSIGN expression
                    {
                    ID4=(Token)match(input,ID,FOLLOW_ID_in_test_expression150);  
                    stream_ID.add(ID4);


                    op=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_test_expression154);  
                    stream_ASSIGN.add(op);


                    pushFollow(FOLLOW_expression_in_test_expression156);
                    expression5=expression();

                    state._fsp--;

                    stream_expression.add(expression5.getTree());

                    // AST REWRITE
                    // elements: ID, expression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (ExprTree)adaptor.nil();
                    // 63:29: -> ^( EQ[$op,\"==\"] ^( FIELD ID ) expression )
                    {
                        // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:63:32: ^( EQ[$op,\"==\"] ^( FIELD ID ) expression )
                        {
                        ExprTree root_1 = (ExprTree)adaptor.nil();
                        root_1 = (ExprTree)adaptor.becomeRoot(
                        (ExprTree)adaptor.create(EQ, op, "==")
                        , root_1);

                        // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:63:47: ^( FIELD ID )
                        {
                        ExprTree root_2 = (ExprTree)adaptor.nil();
                        root_2 = (ExprTree)adaptor.becomeRoot(
                        (ExprTree)adaptor.create(FIELD, "FIELD")
                        , root_2);

                        adaptor.addChild(root_2, 
                        stream_ID.nextNode()
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_1, stream_expression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:64:5: expression
                    {
                    root_0 = (ExprTree)adaptor.nil();


                    pushFollow(FOLLOW_expression_in_test_expression177);
                    expression6=expression();

                    state._fsp--;

                    adaptor.addChild(root_0, expression6.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (ExprTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (ExprTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "test_expression"


    public static class expression_return extends ParserRuleReturnScope {
        ExprTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "expression"
    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:68:1: expression : or_expr EOF !;
    public final ExprParser.expression_return expression() throws RecognitionException {
        ExprParser.expression_return retval = new ExprParser.expression_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token EOF8=null;
        ExprParser.or_expr_return or_expr7 =null;


        ExprTree EOF8_tree=null;

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:69:3: ( or_expr EOF !)
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:69:5: or_expr EOF !
            {
            root_0 = (ExprTree)adaptor.nil();


            pushFollow(FOLLOW_or_expr_in_expression192);
            or_expr7=or_expr();

            state._fsp--;

            adaptor.addChild(root_0, or_expr7.getTree());

            EOF8=(Token)match(input,EOF,FOLLOW_EOF_in_expression194); 

            }

            retval.stop = input.LT(-1);


            retval.tree = (ExprTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (ExprTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "expression"


    public static class or_expr_return extends ParserRuleReturnScope {
        ExprTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "or_expr"
    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:72:1: or_expr : and_expr ( BAR ^ and_expr )* ;
    public final ExprParser.or_expr_return or_expr() throws RecognitionException {
        ExprParser.or_expr_return retval = new ExprParser.or_expr_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token BAR10=null;
        ExprParser.and_expr_return and_expr9 =null;

        ExprParser.and_expr_return and_expr11 =null;


        ExprTree BAR10_tree=null;

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:73:3: ( and_expr ( BAR ^ and_expr )* )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:73:5: and_expr ( BAR ^ and_expr )*
            {
            root_0 = (ExprTree)adaptor.nil();


            pushFollow(FOLLOW_and_expr_in_or_expr208);
            and_expr9=and_expr();

            state._fsp--;

            adaptor.addChild(root_0, and_expr9.getTree());

            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:73:14: ( BAR ^ and_expr )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==BAR) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:73:15: BAR ^ and_expr
            	    {
            	    BAR10=(Token)match(input,BAR,FOLLOW_BAR_in_or_expr211); 
            	    BAR10_tree = 
            	    (ExprTree)adaptor.create(BAR10)
            	    ;
            	    root_0 = (ExprTree)adaptor.becomeRoot(BAR10_tree, root_0);


            	    pushFollow(FOLLOW_and_expr_in_or_expr214);
            	    and_expr11=and_expr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, and_expr11.getTree());

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            retval.tree = (ExprTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (ExprTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "or_expr"


    public static class and_expr_return extends ParserRuleReturnScope {
        ExprTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "and_expr"
    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:76:1: and_expr : not_expr ( AMP ^ not_expr )* ;
    public final ExprParser.and_expr_return and_expr() throws RecognitionException {
        ExprParser.and_expr_return retval = new ExprParser.and_expr_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token AMP13=null;
        ExprParser.not_expr_return not_expr12 =null;

        ExprParser.not_expr_return not_expr14 =null;


        ExprTree AMP13_tree=null;

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:77:3: ( not_expr ( AMP ^ not_expr )* )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:77:5: not_expr ( AMP ^ not_expr )*
            {
            root_0 = (ExprTree)adaptor.nil();


            pushFollow(FOLLOW_not_expr_in_and_expr229);
            not_expr12=not_expr();

            state._fsp--;

            adaptor.addChild(root_0, not_expr12.getTree());

            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:77:14: ( AMP ^ not_expr )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==AMP) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:77:15: AMP ^ not_expr
            	    {
            	    AMP13=(Token)match(input,AMP,FOLLOW_AMP_in_and_expr232); 
            	    AMP13_tree = 
            	    (ExprTree)adaptor.create(AMP13)
            	    ;
            	    root_0 = (ExprTree)adaptor.becomeRoot(AMP13_tree, root_0);


            	    pushFollow(FOLLOW_not_expr_in_and_expr235);
            	    not_expr14=not_expr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, not_expr14.getTree());

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            retval.tree = (ExprTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (ExprTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "and_expr"


    public static class not_expr_return extends ParserRuleReturnScope {
        ExprTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "not_expr"
    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:80:1: not_expr : ( NOT ^ not_expr | equal_expr );
    public final ExprParser.not_expr_return not_expr() throws RecognitionException {
        ExprParser.not_expr_return retval = new ExprParser.not_expr_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token NOT15=null;
        ExprParser.not_expr_return not_expr16 =null;

        ExprParser.equal_expr_return equal_expr17 =null;


        ExprTree NOT15_tree=null;

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:81:3: ( NOT ^ not_expr | equal_expr )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==NOT) ) {
                alt4=1;
            }
            else if ( (LA4_0==FALSE||LA4_0==ID||LA4_0==LPAR||(LA4_0 >= MINUS && LA4_0 <= NAT_LIT)||LA4_0==REAL_LIT||(LA4_0 >= STRING_LIT && LA4_0 <= TRUE)) ) {
                alt4=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;

            }
            switch (alt4) {
                case 1 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:81:5: NOT ^ not_expr
                    {
                    root_0 = (ExprTree)adaptor.nil();


                    NOT15=(Token)match(input,NOT,FOLLOW_NOT_in_not_expr250); 
                    NOT15_tree = 
                    (ExprTree)adaptor.create(NOT15)
                    ;
                    root_0 = (ExprTree)adaptor.becomeRoot(NOT15_tree, root_0);


                    pushFollow(FOLLOW_not_expr_in_not_expr253);
                    not_expr16=not_expr();

                    state._fsp--;

                    adaptor.addChild(root_0, not_expr16.getTree());

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:82:5: equal_expr
                    {
                    root_0 = (ExprTree)adaptor.nil();


                    pushFollow(FOLLOW_equal_expr_in_not_expr259);
                    equal_expr17=equal_expr();

                    state._fsp--;

                    adaptor.addChild(root_0, equal_expr17.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (ExprTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (ExprTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "not_expr"


    public static class equal_expr_return extends ParserRuleReturnScope {
        ExprTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "equal_expr"
    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:84:1: equal_expr : compare_expr ( ( EQ | NEQ ) ^ compare_expr )* ;
    public final ExprParser.equal_expr_return equal_expr() throws RecognitionException {
        ExprParser.equal_expr_return retval = new ExprParser.equal_expr_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token set19=null;
        ExprParser.compare_expr_return compare_expr18 =null;

        ExprParser.compare_expr_return compare_expr20 =null;


        ExprTree set19_tree=null;

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:85:3: ( compare_expr ( ( EQ | NEQ ) ^ compare_expr )* )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:85:5: compare_expr ( ( EQ | NEQ ) ^ compare_expr )*
            {
            root_0 = (ExprTree)adaptor.nil();


            pushFollow(FOLLOW_compare_expr_in_equal_expr269);
            compare_expr18=compare_expr();

            state._fsp--;

            adaptor.addChild(root_0, compare_expr18.getTree());

            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:85:18: ( ( EQ | NEQ ) ^ compare_expr )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==EQ||LA5_0==NEQ) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:85:19: ( EQ | NEQ ) ^ compare_expr
            	    {
            	    set19=(Token)input.LT(1);

            	    set19=(Token)input.LT(1);

            	    if ( input.LA(1)==EQ||input.LA(1)==NEQ ) {
            	        input.consume();
            	        root_0 = (ExprTree)adaptor.becomeRoot(
            	        (ExprTree)adaptor.create(set19)
            	        , root_0);
            	        state.errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    pushFollow(FOLLOW_compare_expr_in_equal_expr281);
            	    compare_expr20=compare_expr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, compare_expr20.getTree());

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            retval.tree = (ExprTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (ExprTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "equal_expr"


    public static class compare_expr_return extends ParserRuleReturnScope {
        ExprTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "compare_expr"
    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:87:1: compare_expr : assign_expr ( ( LT | LE | GT | GE ) ^ assign_expr )* ;
    public final ExprParser.compare_expr_return compare_expr() throws RecognitionException {
        ExprParser.compare_expr_return retval = new ExprParser.compare_expr_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token set22=null;
        ExprParser.assign_expr_return assign_expr21 =null;

        ExprParser.assign_expr_return assign_expr23 =null;


        ExprTree set22_tree=null;

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:88:3: ( assign_expr ( ( LT | LE | GT | GE ) ^ assign_expr )* )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:88:5: assign_expr ( ( LT | LE | GT | GE ) ^ assign_expr )*
            {
            root_0 = (ExprTree)adaptor.nil();


            pushFollow(FOLLOW_assign_expr_in_compare_expr293);
            assign_expr21=assign_expr();

            state._fsp--;

            adaptor.addChild(root_0, assign_expr21.getTree());

            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:88:17: ( ( LT | LE | GT | GE ) ^ assign_expr )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( ((LA6_0 >= GE && LA6_0 <= GT)||LA6_0==LE||LA6_0==LT) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:88:18: ( LT | LE | GT | GE ) ^ assign_expr
            	    {
            	    set22=(Token)input.LT(1);

            	    set22=(Token)input.LT(1);

            	    if ( (input.LA(1) >= GE && input.LA(1) <= GT)||input.LA(1)==LE||input.LA(1)==LT ) {
            	        input.consume();
            	        root_0 = (ExprTree)adaptor.becomeRoot(
            	        (ExprTree)adaptor.create(set22)
            	        , root_0);
            	        state.errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    pushFollow(FOLLOW_assign_expr_in_compare_expr313);
            	    assign_expr23=assign_expr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, assign_expr23.getTree());

            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            retval.tree = (ExprTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (ExprTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "compare_expr"


    public static class assign_expr_return extends ParserRuleReturnScope {
        ExprTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "assign_expr"
    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:91:1: assign_expr : add_expr ;
    public final ExprParser.assign_expr_return assign_expr() throws RecognitionException {
        ExprParser.assign_expr_return retval = new ExprParser.assign_expr_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        ExprParser.add_expr_return add_expr24 =null;



        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:92:3: ( add_expr )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:92:5: add_expr
            {
            root_0 = (ExprTree)adaptor.nil();


            pushFollow(FOLLOW_add_expr_in_assign_expr330);
            add_expr24=add_expr();

            state._fsp--;

            adaptor.addChild(root_0, add_expr24.getTree());

            }

            retval.stop = input.LT(-1);


            retval.tree = (ExprTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (ExprTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "assign_expr"


    public static class add_expr_return extends ParserRuleReturnScope {
        ExprTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "add_expr"
    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:95:1: add_expr : mult_expr ( ( PLUS | MINUS ) ^ mult_expr )* ;
    public final ExprParser.add_expr_return add_expr() throws RecognitionException {
        ExprParser.add_expr_return retval = new ExprParser.add_expr_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token set26=null;
        ExprParser.mult_expr_return mult_expr25 =null;

        ExprParser.mult_expr_return mult_expr27 =null;


        ExprTree set26_tree=null;

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:96:3: ( mult_expr ( ( PLUS | MINUS ) ^ mult_expr )* )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:96:5: mult_expr ( ( PLUS | MINUS ) ^ mult_expr )*
            {
            root_0 = (ExprTree)adaptor.nil();


            pushFollow(FOLLOW_mult_expr_in_add_expr345);
            mult_expr25=mult_expr();

            state._fsp--;

            adaptor.addChild(root_0, mult_expr25.getTree());

            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:96:15: ( ( PLUS | MINUS ) ^ mult_expr )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==MINUS||LA7_0==PLUS) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:96:16: ( PLUS | MINUS ) ^ mult_expr
            	    {
            	    set26=(Token)input.LT(1);

            	    set26=(Token)input.LT(1);

            	    if ( input.LA(1)==MINUS||input.LA(1)==PLUS ) {
            	        input.consume();
            	        root_0 = (ExprTree)adaptor.becomeRoot(
            	        (ExprTree)adaptor.create(set26)
            	        , root_0);
            	        state.errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    pushFollow(FOLLOW_mult_expr_in_add_expr357);
            	    mult_expr27=mult_expr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, mult_expr27.getTree());

            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            retval.tree = (ExprTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (ExprTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "add_expr"


    public static class mult_expr_return extends ParserRuleReturnScope {
        ExprTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "mult_expr"
    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:99:1: mult_expr : unary_expr ( ( ASTERISK | SLASH | PERCENT ) ^ unary_expr )* ;
    public final ExprParser.mult_expr_return mult_expr() throws RecognitionException {
        ExprParser.mult_expr_return retval = new ExprParser.mult_expr_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token set29=null;
        ExprParser.unary_expr_return unary_expr28 =null;

        ExprParser.unary_expr_return unary_expr30 =null;


        ExprTree set29_tree=null;

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:100:3: ( unary_expr ( ( ASTERISK | SLASH | PERCENT ) ^ unary_expr )* )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:100:5: unary_expr ( ( ASTERISK | SLASH | PERCENT ) ^ unary_expr )*
            {
            root_0 = (ExprTree)adaptor.nil();


            pushFollow(FOLLOW_unary_expr_in_mult_expr372);
            unary_expr28=unary_expr();

            state._fsp--;

            adaptor.addChild(root_0, unary_expr28.getTree());

            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:100:16: ( ( ASTERISK | SLASH | PERCENT ) ^ unary_expr )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==ASTERISK||LA8_0==PERCENT||LA8_0==SLASH) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:100:17: ( ASTERISK | SLASH | PERCENT ) ^ unary_expr
            	    {
            	    set29=(Token)input.LT(1);

            	    set29=(Token)input.LT(1);

            	    if ( input.LA(1)==ASTERISK||input.LA(1)==PERCENT||input.LA(1)==SLASH ) {
            	        input.consume();
            	        root_0 = (ExprTree)adaptor.becomeRoot(
            	        (ExprTree)adaptor.create(set29)
            	        , root_0);
            	        state.errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    pushFollow(FOLLOW_unary_expr_in_mult_expr388);
            	    unary_expr30=unary_expr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, unary_expr30.getTree());

            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            retval.tree = (ExprTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (ExprTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "mult_expr"


    public static class unary_expr_return extends ParserRuleReturnScope {
        ExprTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "unary_expr"
    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:103:1: unary_expr : ( MINUS ^ unary_expr | atom_expr );
    public final ExprParser.unary_expr_return unary_expr() throws RecognitionException {
        ExprParser.unary_expr_return retval = new ExprParser.unary_expr_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token MINUS31=null;
        ExprParser.unary_expr_return unary_expr32 =null;

        ExprParser.atom_expr_return atom_expr33 =null;


        ExprTree MINUS31_tree=null;

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:104:3: ( MINUS ^ unary_expr | atom_expr )
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==MINUS) ) {
                alt9=1;
            }
            else if ( (LA9_0==FALSE||LA9_0==ID||LA9_0==LPAR||LA9_0==NAT_LIT||LA9_0==REAL_LIT||(LA9_0 >= STRING_LIT && LA9_0 <= TRUE)) ) {
                alt9=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;

            }
            switch (alt9) {
                case 1 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:104:5: MINUS ^ unary_expr
                    {
                    root_0 = (ExprTree)adaptor.nil();


                    MINUS31=(Token)match(input,MINUS,FOLLOW_MINUS_in_unary_expr403); 
                    MINUS31_tree = 
                    (ExprTree)adaptor.create(MINUS31)
                    ;
                    root_0 = (ExprTree)adaptor.becomeRoot(MINUS31_tree, root_0);


                    pushFollow(FOLLOW_unary_expr_in_unary_expr406);
                    unary_expr32=unary_expr();

                    state._fsp--;

                    adaptor.addChild(root_0, unary_expr32.getTree());

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:105:5: atom_expr
                    {
                    root_0 = (ExprTree)adaptor.nil();


                    pushFollow(FOLLOW_atom_expr_in_unary_expr412);
                    atom_expr33=atom_expr();

                    state._fsp--;

                    adaptor.addChild(root_0, atom_expr33.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (ExprTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (ExprTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "unary_expr"


    public static class atom_expr_return extends ParserRuleReturnScope {
        ExprTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "atom_expr"
    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:109:1: atom_expr : ( constant | typedFieldOrVar | call | par_expr );
    public final ExprParser.atom_expr_return atom_expr() throws RecognitionException {
        ExprParser.atom_expr_return retval = new ExprParser.atom_expr_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        ExprParser.constant_return constant34 =null;

        ExprParser.typedFieldOrVar_return typedFieldOrVar35 =null;

        ExprParser.call_return call36 =null;

        ExprParser.par_expr_return par_expr37 =null;



        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:110:3: ( constant | typedFieldOrVar | call | par_expr )
            int alt10=4;
            switch ( input.LA(1) ) {
            case ID:
                {
                switch ( input.LA(2) ) {
                case COLON:
                    {
                    int LA10_4 = input.LA(3);

                    if ( (LA10_4==ID) ) {
                        int LA10_7 = input.LA(4);

                        if ( (LA10_7==LPAR) ) {
                            alt10=3;
                        }
                        else if ( (LA10_7==EOF||LA10_7==AMP||(LA10_7 >= ASTERISK && LA10_7 <= BAR)||LA10_7==COMMA||(LA10_7 >= DOT && LA10_7 <= EQ)||(LA10_7 >= GE && LA10_7 <= GT)||LA10_7==LE||(LA10_7 >= LT && LA10_7 <= MINUS)||LA10_7==NEQ||(LA10_7 >= PERCENT && LA10_7 <= PLUS)||LA10_7==RPAR||LA10_7==SLASH) ) {
                            alt10=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 10, 7, input);

                            throw nvae;

                        }
                    }
                    else if ( (LA10_4==FALSE||(LA10_4 >= MINUS && LA10_4 <= NAT_LIT)||LA10_4==REAL_LIT||(LA10_4 >= STRING_LIT && LA10_4 <= TRUE)) ) {
                        alt10=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 10, 4, input);

                        throw nvae;

                    }
                    }
                    break;
                case EOF:
                case AMP:
                case ASTERISK:
                case BAR:
                case COMMA:
                case DOT:
                case EQ:
                case GE:
                case GT:
                case LE:
                case LT:
                case MINUS:
                case NEQ:
                case PERCENT:
                case PLUS:
                case RPAR:
                case SLASH:
                    {
                    alt10=2;
                    }
                    break;
                case LPAR:
                    {
                    alt10=3;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 10, 1, input);

                    throw nvae;

                }

                }
                break;
            case FALSE:
            case NAT_LIT:
            case REAL_LIT:
            case STRING_LIT:
            case TRUE:
                {
                alt10=1;
                }
                break;
            case LPAR:
                {
                alt10=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;

            }

            switch (alt10) {
                case 1 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:110:5: constant
                    {
                    root_0 = (ExprTree)adaptor.nil();


                    pushFollow(FOLLOW_constant_in_atom_expr427);
                    constant34=constant();

                    state._fsp--;

                    adaptor.addChild(root_0, constant34.getTree());

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:111:5: typedFieldOrVar
                    {
                    root_0 = (ExprTree)adaptor.nil();


                    pushFollow(FOLLOW_typedFieldOrVar_in_atom_expr433);
                    typedFieldOrVar35=typedFieldOrVar();

                    state._fsp--;

                    adaptor.addChild(root_0, typedFieldOrVar35.getTree());

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:112:5: call
                    {
                    root_0 = (ExprTree)adaptor.nil();


                    pushFollow(FOLLOW_call_in_atom_expr439);
                    call36=call();

                    state._fsp--;

                    adaptor.addChild(root_0, call36.getTree());

                    }
                    break;
                case 4 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:113:5: par_expr
                    {
                    root_0 = (ExprTree)adaptor.nil();


                    pushFollow(FOLLOW_par_expr_in_atom_expr445);
                    par_expr37=par_expr();

                    state._fsp--;

                    adaptor.addChild(root_0, par_expr37.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (ExprTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (ExprTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "atom_expr"


    public static class par_expr_return extends ParserRuleReturnScope {
        ExprTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "par_expr"
    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:117:1: par_expr : open= LPAR or_expr close= RPAR -> ^( LPAR[$open,\"\"] or_expr RPAR[$close,\"\"] ) ;
    public final ExprParser.par_expr_return par_expr() throws RecognitionException {
        ExprParser.par_expr_return retval = new ExprParser.par_expr_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token open=null;
        Token close=null;
        ExprParser.or_expr_return or_expr38 =null;


        ExprTree open_tree=null;
        ExprTree close_tree=null;
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleSubtreeStream stream_or_expr=new RewriteRuleSubtreeStream(adaptor,"rule or_expr");
        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:118:3: (open= LPAR or_expr close= RPAR -> ^( LPAR[$open,\"\"] or_expr RPAR[$close,\"\"] ) )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:118:5: open= LPAR or_expr close= RPAR
            {
            open=(Token)match(input,LPAR,FOLLOW_LPAR_in_par_expr462);  
            stream_LPAR.add(open);


            pushFollow(FOLLOW_or_expr_in_par_expr464);
            or_expr38=or_expr();

            state._fsp--;

            stream_or_expr.add(or_expr38.getTree());

            close=(Token)match(input,RPAR,FOLLOW_RPAR_in_par_expr468);  
            stream_RPAR.add(close);


            // AST REWRITE
            // elements: RPAR, or_expr, LPAR
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (ExprTree)adaptor.nil();
            // 119:5: -> ^( LPAR[$open,\"\"] or_expr RPAR[$close,\"\"] )
            {
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:119:8: ^( LPAR[$open,\"\"] or_expr RPAR[$close,\"\"] )
                {
                ExprTree root_1 = (ExprTree)adaptor.nil();
                root_1 = (ExprTree)adaptor.becomeRoot(
                (ExprTree)adaptor.create(LPAR, open, "")
                , root_1);

                adaptor.addChild(root_1, stream_or_expr.nextTree());

                adaptor.addChild(root_1, 
                (ExprTree)adaptor.create(RPAR, close, "")
                );

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (ExprTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (ExprTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "par_expr"


    public static class constant_return extends ParserRuleReturnScope {
        ExprTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "constant"
    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:122:1: constant : (prefix= ID COLON ( literal -> ^( CONST literal ID ) | MINUS literal -> ^( CONST ^( MINUS literal ) ID ) ) | literal -> ^( CONST literal ) );
    public final ExprParser.constant_return constant() throws RecognitionException {
        ExprParser.constant_return retval = new ExprParser.constant_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token prefix=null;
        Token COLON39=null;
        Token MINUS41=null;
        ExprParser.literal_return literal40 =null;

        ExprParser.literal_return literal42 =null;

        ExprParser.literal_return literal43 =null;


        ExprTree prefix_tree=null;
        ExprTree COLON39_tree=null;
        ExprTree MINUS41_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_MINUS=new RewriteRuleTokenStream(adaptor,"token MINUS");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_literal=new RewriteRuleSubtreeStream(adaptor,"rule literal");
        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:123:3: (prefix= ID COLON ( literal -> ^( CONST literal ID ) | MINUS literal -> ^( CONST ^( MINUS literal ) ID ) ) | literal -> ^( CONST literal ) )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==ID) ) {
                alt12=1;
            }
            else if ( (LA12_0==FALSE||LA12_0==NAT_LIT||LA12_0==REAL_LIT||(LA12_0 >= STRING_LIT && LA12_0 <= TRUE)) ) {
                alt12=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;

            }
            switch (alt12) {
                case 1 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:123:5: prefix= ID COLON ( literal -> ^( CONST literal ID ) | MINUS literal -> ^( CONST ^( MINUS literal ) ID ) )
                    {
                    prefix=(Token)match(input,ID,FOLLOW_ID_in_constant499);  
                    stream_ID.add(prefix);


                    COLON39=(Token)match(input,COLON,FOLLOW_COLON_in_constant501);  
                    stream_COLON.add(COLON39);


                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:124:5: ( literal -> ^( CONST literal ID ) | MINUS literal -> ^( CONST ^( MINUS literal ) ID ) )
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==FALSE||LA11_0==NAT_LIT||LA11_0==REAL_LIT||(LA11_0 >= STRING_LIT && LA11_0 <= TRUE)) ) {
                        alt11=1;
                    }
                    else if ( (LA11_0==MINUS) ) {
                        alt11=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 11, 0, input);

                        throw nvae;

                    }
                    switch (alt11) {
                        case 1 :
                            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:124:7: literal
                            {
                            pushFollow(FOLLOW_literal_in_constant509);
                            literal40=literal();

                            state._fsp--;

                            stream_literal.add(literal40.getTree());

                            // AST REWRITE
                            // elements: ID, literal
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (ExprTree)adaptor.nil();
                            // 124:15: -> ^( CONST literal ID )
                            {
                                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:124:18: ^( CONST literal ID )
                                {
                                ExprTree root_1 = (ExprTree)adaptor.nil();
                                root_1 = (ExprTree)adaptor.becomeRoot(
                                (ExprTree)adaptor.create(CONST, "CONST")
                                , root_1);

                                adaptor.addChild(root_1, stream_literal.nextTree());

                                adaptor.addChild(root_1, 
                                stream_ID.nextNode()
                                );

                                adaptor.addChild(root_0, root_1);
                                }

                            }


                            retval.tree = root_0;

                            }
                            break;
                        case 2 :
                            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:125:7: MINUS literal
                            {
                            MINUS41=(Token)match(input,MINUS,FOLLOW_MINUS_in_constant527);  
                            stream_MINUS.add(MINUS41);


                            pushFollow(FOLLOW_literal_in_constant529);
                            literal42=literal();

                            state._fsp--;

                            stream_literal.add(literal42.getTree());

                            // AST REWRITE
                            // elements: MINUS, literal, ID
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (ExprTree)adaptor.nil();
                            // 125:21: -> ^( CONST ^( MINUS literal ) ID )
                            {
                                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:125:24: ^( CONST ^( MINUS literal ) ID )
                                {
                                ExprTree root_1 = (ExprTree)adaptor.nil();
                                root_1 = (ExprTree)adaptor.becomeRoot(
                                (ExprTree)adaptor.create(CONST, "CONST")
                                , root_1);

                                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:125:32: ^( MINUS literal )
                                {
                                ExprTree root_2 = (ExprTree)adaptor.nil();
                                root_2 = (ExprTree)adaptor.becomeRoot(
                                stream_MINUS.nextNode()
                                , root_2);

                                adaptor.addChild(root_2, stream_literal.nextTree());

                                adaptor.addChild(root_1, root_2);
                                }

                                adaptor.addChild(root_1, 
                                stream_ID.nextNode()
                                );

                                adaptor.addChild(root_0, root_1);
                                }

                            }


                            retval.tree = root_0;

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:127:5: literal
                    {
                    pushFollow(FOLLOW_literal_in_constant555);
                    literal43=literal();

                    state._fsp--;

                    stream_literal.add(literal43.getTree());

                    // AST REWRITE
                    // elements: literal
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (ExprTree)adaptor.nil();
                    // 128:5: -> ^( CONST literal )
                    {
                        // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:128:8: ^( CONST literal )
                        {
                        ExprTree root_1 = (ExprTree)adaptor.nil();
                        root_1 = (ExprTree)adaptor.becomeRoot(
                        (ExprTree)adaptor.create(CONST, "CONST")
                        , root_1);

                        adaptor.addChild(root_1, stream_literal.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (ExprTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (ExprTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "constant"


    public static class parameter_return extends ParserRuleReturnScope {
        ExprTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "parameter"
    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:131:1: parameter : (prefix= ID DOLLAR NAT_LIT -> ^( PAR NAT_LIT $prefix) | DOLLAR NAT_LIT -> ^( PAR NAT_LIT ) );
    public final ExprParser.parameter_return parameter() throws RecognitionException {
        ExprParser.parameter_return retval = new ExprParser.parameter_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token prefix=null;
        Token DOLLAR44=null;
        Token NAT_LIT45=null;
        Token DOLLAR46=null;
        Token NAT_LIT47=null;

        ExprTree prefix_tree=null;
        ExprTree DOLLAR44_tree=null;
        ExprTree NAT_LIT45_tree=null;
        ExprTree DOLLAR46_tree=null;
        ExprTree NAT_LIT47_tree=null;
        RewriteRuleTokenStream stream_DOLLAR=new RewriteRuleTokenStream(adaptor,"token DOLLAR");
        RewriteRuleTokenStream stream_NAT_LIT=new RewriteRuleTokenStream(adaptor,"token NAT_LIT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:132:3: (prefix= ID DOLLAR NAT_LIT -> ^( PAR NAT_LIT $prefix) | DOLLAR NAT_LIT -> ^( PAR NAT_LIT ) )
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==ID) ) {
                alt13=1;
            }
            else if ( (LA13_0==DOLLAR) ) {
                alt13=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;

            }
            switch (alt13) {
                case 1 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:132:5: prefix= ID DOLLAR NAT_LIT
                    {
                    prefix=(Token)match(input,ID,FOLLOW_ID_in_parameter582);  
                    stream_ID.add(prefix);


                    DOLLAR44=(Token)match(input,DOLLAR,FOLLOW_DOLLAR_in_parameter584);  
                    stream_DOLLAR.add(DOLLAR44);


                    NAT_LIT45=(Token)match(input,NAT_LIT,FOLLOW_NAT_LIT_in_parameter586);  
                    stream_NAT_LIT.add(NAT_LIT45);


                    // AST REWRITE
                    // elements: NAT_LIT, prefix
                    // token labels: prefix
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_prefix=new RewriteRuleTokenStream(adaptor,"token prefix",prefix);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (ExprTree)adaptor.nil();
                    // 133:5: -> ^( PAR NAT_LIT $prefix)
                    {
                        // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:133:8: ^( PAR NAT_LIT $prefix)
                        {
                        ExprTree root_1 = (ExprTree)adaptor.nil();
                        root_1 = (ExprTree)adaptor.becomeRoot(
                        (ExprTree)adaptor.create(PAR, "PAR")
                        , root_1);

                        adaptor.addChild(root_1, 
                        stream_NAT_LIT.nextNode()
                        );

                        adaptor.addChild(root_1, stream_prefix.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:134:5: DOLLAR NAT_LIT
                    {
                    DOLLAR46=(Token)match(input,DOLLAR,FOLLOW_DOLLAR_in_parameter607);  
                    stream_DOLLAR.add(DOLLAR46);


                    NAT_LIT47=(Token)match(input,NAT_LIT,FOLLOW_NAT_LIT_in_parameter609);  
                    stream_NAT_LIT.add(NAT_LIT47);


                    // AST REWRITE
                    // elements: NAT_LIT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (ExprTree)adaptor.nil();
                    // 135:5: -> ^( PAR NAT_LIT )
                    {
                        // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:135:8: ^( PAR NAT_LIT )
                        {
                        ExprTree root_1 = (ExprTree)adaptor.nil();
                        root_1 = (ExprTree)adaptor.becomeRoot(
                        (ExprTree)adaptor.create(PAR, "PAR")
                        , root_1);

                        adaptor.addChild(root_1, 
                        stream_NAT_LIT.nextNode()
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (ExprTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (ExprTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "parameter"


    public static class typedFieldOrVar_return extends ParserRuleReturnScope {
        ExprTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "typedFieldOrVar"
    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:138:1: typedFieldOrVar : ( ID COLON fieldOrVar -> ^( FIELD fieldOrVar ID ) | fieldOrVar -> ^( FIELD fieldOrVar ) );
    public final ExprParser.typedFieldOrVar_return typedFieldOrVar() throws RecognitionException {
        ExprParser.typedFieldOrVar_return retval = new ExprParser.typedFieldOrVar_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token ID48=null;
        Token COLON49=null;
        ExprParser.fieldOrVar_return fieldOrVar50 =null;

        ExprParser.fieldOrVar_return fieldOrVar51 =null;


        ExprTree ID48_tree=null;
        ExprTree COLON49_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_fieldOrVar=new RewriteRuleSubtreeStream(adaptor,"rule fieldOrVar");
        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:139:3: ( ID COLON fieldOrVar -> ^( FIELD fieldOrVar ID ) | fieldOrVar -> ^( FIELD fieldOrVar ) )
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==ID) ) {
                int LA14_1 = input.LA(2);

                if ( (LA14_1==COLON) ) {
                    alt14=1;
                }
                else if ( (LA14_1==EOF||LA14_1==AMP||(LA14_1 >= ASTERISK && LA14_1 <= BAR)||LA14_1==COMMA||(LA14_1 >= DOT && LA14_1 <= EQ)||(LA14_1 >= GE && LA14_1 <= GT)||LA14_1==LE||(LA14_1 >= LT && LA14_1 <= MINUS)||LA14_1==NEQ||(LA14_1 >= PERCENT && LA14_1 <= PLUS)||LA14_1==RPAR||LA14_1==SLASH) ) {
                    alt14=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 14, 1, input);

                    throw nvae;

                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;

            }
            switch (alt14) {
                case 1 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:139:5: ID COLON fieldOrVar
                    {
                    ID48=(Token)match(input,ID,FOLLOW_ID_in_typedFieldOrVar634);  
                    stream_ID.add(ID48);


                    COLON49=(Token)match(input,COLON,FOLLOW_COLON_in_typedFieldOrVar636);  
                    stream_COLON.add(COLON49);


                    pushFollow(FOLLOW_fieldOrVar_in_typedFieldOrVar638);
                    fieldOrVar50=fieldOrVar();

                    state._fsp--;

                    stream_fieldOrVar.add(fieldOrVar50.getTree());

                    // AST REWRITE
                    // elements: fieldOrVar, ID
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (ExprTree)adaptor.nil();
                    // 140:5: -> ^( FIELD fieldOrVar ID )
                    {
                        // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:140:8: ^( FIELD fieldOrVar ID )
                        {
                        ExprTree root_1 = (ExprTree)adaptor.nil();
                        root_1 = (ExprTree)adaptor.becomeRoot(
                        (ExprTree)adaptor.create(FIELD, "FIELD")
                        , root_1);

                        adaptor.addChild(root_1, stream_fieldOrVar.nextTree());

                        adaptor.addChild(root_1, 
                        stream_ID.nextNode()
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:141:5: fieldOrVar
                    {
                    pushFollow(FOLLOW_fieldOrVar_in_typedFieldOrVar658);
                    fieldOrVar51=fieldOrVar();

                    state._fsp--;

                    stream_fieldOrVar.add(fieldOrVar51.getTree());

                    // AST REWRITE
                    // elements: fieldOrVar
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (ExprTree)adaptor.nil();
                    // 142:5: -> ^( FIELD fieldOrVar )
                    {
                        // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:142:8: ^( FIELD fieldOrVar )
                        {
                        ExprTree root_1 = (ExprTree)adaptor.nil();
                        root_1 = (ExprTree)adaptor.becomeRoot(
                        (ExprTree)adaptor.create(FIELD, "FIELD")
                        , root_1);

                        adaptor.addChild(root_1, stream_fieldOrVar.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (ExprTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (ExprTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "typedFieldOrVar"


    public static class fieldOrVar_return extends ParserRuleReturnScope {
        ExprTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "fieldOrVar"
    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:145:1: fieldOrVar : ID ( DOT ^ ID )? ;
    public final ExprParser.fieldOrVar_return fieldOrVar() throws RecognitionException {
        ExprParser.fieldOrVar_return retval = new ExprParser.fieldOrVar_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token ID52=null;
        Token DOT53=null;
        Token ID54=null;

        ExprTree ID52_tree=null;
        ExprTree DOT53_tree=null;
        ExprTree ID54_tree=null;

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:146:3: ( ID ( DOT ^ ID )? )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:146:5: ID ( DOT ^ ID )?
            {
            root_0 = (ExprTree)adaptor.nil();


            ID52=(Token)match(input,ID,FOLLOW_ID_in_fieldOrVar683); 
            ID52_tree = 
            (ExprTree)adaptor.create(ID52)
            ;
            adaptor.addChild(root_0, ID52_tree);


            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:146:8: ( DOT ^ ID )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==DOT) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:146:9: DOT ^ ID
                    {
                    DOT53=(Token)match(input,DOT,FOLLOW_DOT_in_fieldOrVar686); 
                    DOT53_tree = 
                    (ExprTree)adaptor.create(DOT53)
                    ;
                    root_0 = (ExprTree)adaptor.becomeRoot(DOT53_tree, root_0);


                    ID54=(Token)match(input,ID,FOLLOW_ID_in_fieldOrVar689); 
                    ID54_tree = 
                    (ExprTree)adaptor.create(ID54)
                    ;
                    adaptor.addChild(root_0, ID54_tree);


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            retval.tree = (ExprTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (ExprTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "fieldOrVar"


    public static class call_return extends ParserRuleReturnScope {
        ExprTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "call"
    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:149:1: call : oper LPAR ( or_expr ( COMMA or_expr )* )? close= RPAR -> ^( CALL oper ( or_expr )* RPAR[$close,\"\"] ) ;
    public final ExprParser.call_return call() throws RecognitionException {
        ExprParser.call_return retval = new ExprParser.call_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token close=null;
        Token LPAR56=null;
        Token COMMA58=null;
        ExprParser.oper_return oper55 =null;

        ExprParser.or_expr_return or_expr57 =null;

        ExprParser.or_expr_return or_expr59 =null;


        ExprTree close_tree=null;
        ExprTree LPAR56_tree=null;
        ExprTree COMMA58_tree=null;
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_or_expr=new RewriteRuleSubtreeStream(adaptor,"rule or_expr");
        RewriteRuleSubtreeStream stream_oper=new RewriteRuleSubtreeStream(adaptor,"rule oper");
        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:150:3: ( oper LPAR ( or_expr ( COMMA or_expr )* )? close= RPAR -> ^( CALL oper ( or_expr )* RPAR[$close,\"\"] ) )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:150:5: oper LPAR ( or_expr ( COMMA or_expr )* )? close= RPAR
            {
            pushFollow(FOLLOW_oper_in_call704);
            oper55=oper();

            state._fsp--;

            stream_oper.add(oper55.getTree());

            LPAR56=(Token)match(input,LPAR,FOLLOW_LPAR_in_call706);  
            stream_LPAR.add(LPAR56);


            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:150:15: ( or_expr ( COMMA or_expr )* )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==FALSE||LA17_0==ID||LA17_0==LPAR||(LA17_0 >= MINUS && LA17_0 <= NAT_LIT)||LA17_0==NOT||LA17_0==REAL_LIT||(LA17_0 >= STRING_LIT && LA17_0 <= TRUE)) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:150:16: or_expr ( COMMA or_expr )*
                    {
                    pushFollow(FOLLOW_or_expr_in_call709);
                    or_expr57=or_expr();

                    state._fsp--;

                    stream_or_expr.add(or_expr57.getTree());

                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:150:24: ( COMMA or_expr )*
                    loop16:
                    do {
                        int alt16=2;
                        int LA16_0 = input.LA(1);

                        if ( (LA16_0==COMMA) ) {
                            alt16=1;
                        }


                        switch (alt16) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:150:25: COMMA or_expr
                    	    {
                    	    COMMA58=(Token)match(input,COMMA,FOLLOW_COMMA_in_call712);  
                    	    stream_COMMA.add(COMMA58);


                    	    pushFollow(FOLLOW_or_expr_in_call714);
                    	    or_expr59=or_expr();

                    	    state._fsp--;

                    	    stream_or_expr.add(or_expr59.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop16;
                        }
                    } while (true);


                    }
                    break;

            }


            close=(Token)match(input,RPAR,FOLLOW_RPAR_in_call722);  
            stream_RPAR.add(close);


            // AST REWRITE
            // elements: or_expr, oper, RPAR
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (ExprTree)adaptor.nil();
            // 151:4: -> ^( CALL oper ( or_expr )* RPAR[$close,\"\"] )
            {
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:151:7: ^( CALL oper ( or_expr )* RPAR[$close,\"\"] )
                {
                ExprTree root_1 = (ExprTree)adaptor.nil();
                root_1 = (ExprTree)adaptor.becomeRoot(
                (ExprTree)adaptor.create(CALL, "CALL")
                , root_1);

                adaptor.addChild(root_1, stream_oper.nextTree());

                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:151:19: ( or_expr )*
                while ( stream_or_expr.hasNext() ) {
                    adaptor.addChild(root_1, stream_or_expr.nextTree());

                }
                stream_or_expr.reset();

                adaptor.addChild(root_1, 
                (ExprTree)adaptor.create(RPAR, close, "")
                );

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (ExprTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (ExprTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "call"


    public static class oper_return extends ParserRuleReturnScope {
        ExprTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "oper"
    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:154:1: oper : (prefix= ID COLON name= ID -> ^( OPER $name $prefix) | ID -> ^( OPER ID ) );
    public final ExprParser.oper_return oper() throws RecognitionException {
        ExprParser.oper_return retval = new ExprParser.oper_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token prefix=null;
        Token name=null;
        Token COLON60=null;
        Token ID61=null;

        ExprTree prefix_tree=null;
        ExprTree name_tree=null;
        ExprTree COLON60_tree=null;
        ExprTree ID61_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:155:3: (prefix= ID COLON name= ID -> ^( OPER $name $prefix) | ID -> ^( OPER ID ) )
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==ID) ) {
                int LA18_1 = input.LA(2);

                if ( (LA18_1==COLON) ) {
                    alt18=1;
                }
                else if ( (LA18_1==LPAR) ) {
                    alt18=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 18, 1, input);

                    throw nvae;

                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;

            }
            switch (alt18) {
                case 1 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:155:5: prefix= ID COLON name= ID
                    {
                    prefix=(Token)match(input,ID,FOLLOW_ID_in_oper754);  
                    stream_ID.add(prefix);


                    COLON60=(Token)match(input,COLON,FOLLOW_COLON_in_oper756);  
                    stream_COLON.add(COLON60);


                    name=(Token)match(input,ID,FOLLOW_ID_in_oper760);  
                    stream_ID.add(name);


                    // AST REWRITE
                    // elements: prefix, name
                    // token labels: name, prefix
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_name=new RewriteRuleTokenStream(adaptor,"token name",name);
                    RewriteRuleTokenStream stream_prefix=new RewriteRuleTokenStream(adaptor,"token prefix",prefix);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (ExprTree)adaptor.nil();
                    // 155:29: -> ^( OPER $name $prefix)
                    {
                        // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:155:32: ^( OPER $name $prefix)
                        {
                        ExprTree root_1 = (ExprTree)adaptor.nil();
                        root_1 = (ExprTree)adaptor.becomeRoot(
                        (ExprTree)adaptor.create(OPER, "OPER")
                        , root_1);

                        adaptor.addChild(root_1, stream_name.nextNode());

                        adaptor.addChild(root_1, stream_prefix.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:156:5: ID
                    {
                    ID61=(Token)match(input,ID,FOLLOW_ID_in_oper778);  
                    stream_ID.add(ID61);


                    // AST REWRITE
                    // elements: ID
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (ExprTree)adaptor.nil();
                    // 156:8: -> ^( OPER ID )
                    {
                        // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:156:11: ^( OPER ID )
                        {
                        ExprTree root_1 = (ExprTree)adaptor.nil();
                        root_1 = (ExprTree)adaptor.becomeRoot(
                        (ExprTree)adaptor.create(OPER, "OPER")
                        , root_1);

                        adaptor.addChild(root_1, 
                        stream_ID.nextNode()
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (ExprTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (ExprTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "oper"


    public static class literal_return extends ParserRuleReturnScope {
        ExprTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "literal"
    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:159:1: literal : ( REAL_LIT -> ^( REAL REAL_LIT ) | NAT_LIT -> ^( INT NAT_LIT ) | STRING_LIT -> ^( STRING STRING_LIT ) | TRUE -> ^( BOOL TRUE ) | FALSE -> ^( BOOL FALSE ) );
    public final ExprParser.literal_return literal() throws RecognitionException {
        ExprParser.literal_return retval = new ExprParser.literal_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token REAL_LIT62=null;
        Token NAT_LIT63=null;
        Token STRING_LIT64=null;
        Token TRUE65=null;
        Token FALSE66=null;

        ExprTree REAL_LIT62_tree=null;
        ExprTree NAT_LIT63_tree=null;
        ExprTree STRING_LIT64_tree=null;
        ExprTree TRUE65_tree=null;
        ExprTree FALSE66_tree=null;
        RewriteRuleTokenStream stream_REAL_LIT=new RewriteRuleTokenStream(adaptor,"token REAL_LIT");
        RewriteRuleTokenStream stream_NAT_LIT=new RewriteRuleTokenStream(adaptor,"token NAT_LIT");
        RewriteRuleTokenStream stream_FALSE=new RewriteRuleTokenStream(adaptor,"token FALSE");
        RewriteRuleTokenStream stream_TRUE=new RewriteRuleTokenStream(adaptor,"token TRUE");
        RewriteRuleTokenStream stream_STRING_LIT=new RewriteRuleTokenStream(adaptor,"token STRING_LIT");

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:160:3: ( REAL_LIT -> ^( REAL REAL_LIT ) | NAT_LIT -> ^( INT NAT_LIT ) | STRING_LIT -> ^( STRING STRING_LIT ) | TRUE -> ^( BOOL TRUE ) | FALSE -> ^( BOOL FALSE ) )
            int alt19=5;
            switch ( input.LA(1) ) {
            case REAL_LIT:
                {
                alt19=1;
                }
                break;
            case NAT_LIT:
                {
                alt19=2;
                }
                break;
            case STRING_LIT:
                {
                alt19=3;
                }
                break;
            case TRUE:
                {
                alt19=4;
                }
                break;
            case FALSE:
                {
                alt19=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 19, 0, input);

                throw nvae;

            }

            switch (alt19) {
                case 1 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:160:5: REAL_LIT
                    {
                    REAL_LIT62=(Token)match(input,REAL_LIT,FOLLOW_REAL_LIT_in_literal799);  
                    stream_REAL_LIT.add(REAL_LIT62);


                    // AST REWRITE
                    // elements: REAL_LIT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (ExprTree)adaptor.nil();
                    // 160:14: -> ^( REAL REAL_LIT )
                    {
                        // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:160:17: ^( REAL REAL_LIT )
                        {
                        ExprTree root_1 = (ExprTree)adaptor.nil();
                        root_1 = (ExprTree)adaptor.becomeRoot(
                        (ExprTree)adaptor.create(REAL, "REAL")
                        , root_1);

                        adaptor.addChild(root_1, 
                        stream_REAL_LIT.nextNode()
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:161:5: NAT_LIT
                    {
                    NAT_LIT63=(Token)match(input,NAT_LIT,FOLLOW_NAT_LIT_in_literal813);  
                    stream_NAT_LIT.add(NAT_LIT63);


                    // AST REWRITE
                    // elements: NAT_LIT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (ExprTree)adaptor.nil();
                    // 161:13: -> ^( INT NAT_LIT )
                    {
                        // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:161:16: ^( INT NAT_LIT )
                        {
                        ExprTree root_1 = (ExprTree)adaptor.nil();
                        root_1 = (ExprTree)adaptor.becomeRoot(
                        (ExprTree)adaptor.create(INT, "INT")
                        , root_1);

                        adaptor.addChild(root_1, 
                        stream_NAT_LIT.nextNode()
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:162:5: STRING_LIT
                    {
                    STRING_LIT64=(Token)match(input,STRING_LIT,FOLLOW_STRING_LIT_in_literal827);  
                    stream_STRING_LIT.add(STRING_LIT64);


                    // AST REWRITE
                    // elements: STRING_LIT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (ExprTree)adaptor.nil();
                    // 162:16: -> ^( STRING STRING_LIT )
                    {
                        // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:162:19: ^( STRING STRING_LIT )
                        {
                        ExprTree root_1 = (ExprTree)adaptor.nil();
                        root_1 = (ExprTree)adaptor.becomeRoot(
                        (ExprTree)adaptor.create(STRING, "STRING")
                        , root_1);

                        adaptor.addChild(root_1, 
                        stream_STRING_LIT.nextNode()
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 4 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:163:5: TRUE
                    {
                    TRUE65=(Token)match(input,TRUE,FOLLOW_TRUE_in_literal841);  
                    stream_TRUE.add(TRUE65);


                    // AST REWRITE
                    // elements: TRUE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (ExprTree)adaptor.nil();
                    // 163:10: -> ^( BOOL TRUE )
                    {
                        // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:163:13: ^( BOOL TRUE )
                        {
                        ExprTree root_1 = (ExprTree)adaptor.nil();
                        root_1 = (ExprTree)adaptor.becomeRoot(
                        (ExprTree)adaptor.create(BOOL, "BOOL")
                        , root_1);

                        adaptor.addChild(root_1, 
                        stream_TRUE.nextNode()
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 5 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:164:5: FALSE
                    {
                    FALSE66=(Token)match(input,FALSE,FOLLOW_FALSE_in_literal855);  
                    stream_FALSE.add(FALSE66);


                    // AST REWRITE
                    // elements: FALSE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (ExprTree)adaptor.nil();
                    // 164:11: -> ^( BOOL FALSE )
                    {
                        // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:164:14: ^( BOOL FALSE )
                        {
                        ExprTree root_1 = (ExprTree)adaptor.nil();
                        root_1 = (ExprTree)adaptor.becomeRoot(
                        (ExprTree)adaptor.create(BOOL, "BOOL")
                        , root_1);

                        adaptor.addChild(root_1, 
                        stream_FALSE.nextNode()
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (ExprTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (ExprTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "literal"

    // Delegated rules


 

    public static final BitSet FOLLOW_ID_in_assignment120 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ASSIGN_in_assignment122 = new BitSet(new long[]{0x00060802D1100000L});
    public static final BitSet FOLLOW_expression_in_assignment125 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_test_expression150 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ASSIGN_in_test_expression154 = new BitSet(new long[]{0x00060802D1100000L});
    public static final BitSet FOLLOW_expression_in_test_expression156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_test_expression177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_expr_in_expression192 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_expression194 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_expr_in_or_expr208 = new BitSet(new long[]{0x0000000000000082L});
    public static final BitSet FOLLOW_BAR_in_or_expr211 = new BitSet(new long[]{0x00060802D1100000L});
    public static final BitSet FOLLOW_and_expr_in_or_expr214 = new BitSet(new long[]{0x0000000000000082L});
    public static final BitSet FOLLOW_not_expr_in_and_expr229 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_AMP_in_and_expr232 = new BitSet(new long[]{0x00060802D1100000L});
    public static final BitSet FOLLOW_not_expr_in_and_expr235 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_NOT_in_not_expr250 = new BitSet(new long[]{0x00060802D1100000L});
    public static final BitSet FOLLOW_not_expr_in_not_expr253 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_equal_expr_in_not_expr259 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_compare_expr_in_equal_expr269 = new BitSet(new long[]{0x0000000100040002L});
    public static final BitSet FOLLOW_set_in_equal_expr272 = new BitSet(new long[]{0x00060800D1100000L});
    public static final BitSet FOLLOW_compare_expr_in_equal_expr281 = new BitSet(new long[]{0x0000000100040002L});
    public static final BitSet FOLLOW_assign_expr_in_compare_expr293 = new BitSet(new long[]{0x0000000028C00002L});
    public static final BitSet FOLLOW_set_in_compare_expr296 = new BitSet(new long[]{0x00060800D1100000L});
    public static final BitSet FOLLOW_assign_expr_in_compare_expr313 = new BitSet(new long[]{0x0000000028C00002L});
    public static final BitSet FOLLOW_add_expr_in_assign_expr330 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mult_expr_in_add_expr345 = new BitSet(new long[]{0x0000008040000002L});
    public static final BitSet FOLLOW_set_in_add_expr348 = new BitSet(new long[]{0x00060800D1100000L});
    public static final BitSet FOLLOW_mult_expr_in_add_expr357 = new BitSet(new long[]{0x0000008040000002L});
    public static final BitSet FOLLOW_unary_expr_in_mult_expr372 = new BitSet(new long[]{0x0000804000000042L});
    public static final BitSet FOLLOW_set_in_mult_expr375 = new BitSet(new long[]{0x00060800D1100000L});
    public static final BitSet FOLLOW_unary_expr_in_mult_expr388 = new BitSet(new long[]{0x0000804000000042L});
    public static final BitSet FOLLOW_MINUS_in_unary_expr403 = new BitSet(new long[]{0x00060800D1100000L});
    public static final BitSet FOLLOW_unary_expr_in_unary_expr406 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_expr_in_unary_expr412 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constant_in_atom_expr427 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typedFieldOrVar_in_atom_expr433 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_atom_expr439 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_par_expr_in_atom_expr445 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_par_expr462 = new BitSet(new long[]{0x00060802D1100000L});
    public static final BitSet FOLLOW_or_expr_in_par_expr464 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RPAR_in_par_expr468 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_constant499 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_COLON_in_constant501 = new BitSet(new long[]{0x00060800C0100000L});
    public static final BitSet FOLLOW_literal_in_constant509 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_constant527 = new BitSet(new long[]{0x0006080080100000L});
    public static final BitSet FOLLOW_literal_in_constant529 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_constant555 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_parameter582 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_DOLLAR_in_parameter584 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_NAT_LIT_in_parameter586 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOLLAR_in_parameter607 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_NAT_LIT_in_parameter609 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_typedFieldOrVar634 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_COLON_in_typedFieldOrVar636 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_fieldOrVar_in_typedFieldOrVar638 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fieldOrVar_in_typedFieldOrVar658 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_fieldOrVar683 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_DOT_in_fieldOrVar686 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_fieldOrVar689 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_oper_in_call704 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_LPAR_in_call706 = new BitSet(new long[]{0x00061802D1100000L});
    public static final BitSet FOLLOW_or_expr_in_call709 = new BitSet(new long[]{0x0000100000002000L});
    public static final BitSet FOLLOW_COMMA_in_call712 = new BitSet(new long[]{0x00060802D1100000L});
    public static final BitSet FOLLOW_or_expr_in_call714 = new BitSet(new long[]{0x0000100000002000L});
    public static final BitSet FOLLOW_RPAR_in_call722 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_oper754 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_COLON_in_oper756 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_ID_in_oper760 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_oper778 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REAL_LIT_in_literal799 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAT_LIT_in_literal813 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LIT_in_literal827 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_literal841 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_literal855 = new BitSet(new long[]{0x0000000000000002L});

}
// $ANTLR 3.4 D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g 2013-11-24 21:01:00

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "AMP", "ASTERISK", "BAR", "BOOL", "BSLASH", "CALL", "COLON", "COMMA", "CONST", "DOLLAR", "DONT_CARE", "DOT", "EQ", "EscapeSequence", "FALSE", "FIELD", "GE", "GT", "ID", "INT", "LCURLY", "LE", "LPAR", "LT", "MINUS", "NAT_LIT", "NEQ", "NOT", "Naturalumber", "NonIntegerNumber", "OPER", "PAR", "PERCENT", "PLUS", "QUOTE", "RCURLY", "REAL", "REAL_LIT", "RPAR", "SEMI", "SHARP", "SLASH", "STRING", "STRING_LIT", "TRUE", "VAR", "WS"
    };

    public static final int EOF=-1;
    public static final int AMP=4;
    public static final int ASTERISK=5;
    public static final int BAR=6;
    public static final int BOOL=7;
    public static final int BSLASH=8;
    public static final int CALL=9;
    public static final int COLON=10;
    public static final int COMMA=11;
    public static final int CONST=12;
    public static final int DOLLAR=13;
    public static final int DONT_CARE=14;
    public static final int DOT=15;
    public static final int EQ=16;
    public static final int EscapeSequence=17;
    public static final int FALSE=18;
    public static final int FIELD=19;
    public static final int GE=20;
    public static final int GT=21;
    public static final int ID=22;
    public static final int INT=23;
    public static final int LCURLY=24;
    public static final int LE=25;
    public static final int LPAR=26;
    public static final int LT=27;
    public static final int MINUS=28;
    public static final int NAT_LIT=29;
    public static final int NEQ=30;
    public static final int NOT=31;
    public static final int Naturalumber=32;
    public static final int NonIntegerNumber=33;
    public static final int OPER=34;
    public static final int PAR=35;
    public static final int PERCENT=36;
    public static final int PLUS=37;
    public static final int QUOTE=38;
    public static final int RCURLY=39;
    public static final int REAL=40;
    public static final int REAL_LIT=41;
    public static final int RPAR=42;
    public static final int SEMI=43;
    public static final int SHARP=44;
    public static final int SLASH=45;
    public static final int STRING=46;
    public static final int STRING_LIT=47;
    public static final int TRUE=48;
    public static final int VAR=49;
    public static final int WS=50;

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
    public String getGrammarFileName() { return "D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g"; }


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


    public static class expression_return extends ParserRuleReturnScope {
        ExprTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "expression"
    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:47:1: expression : or_expr EOF !;
    public final ExprParser.expression_return expression() throws RecognitionException {
        ExprParser.expression_return retval = new ExprParser.expression_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token EOF2=null;
        ExprParser.or_expr_return or_expr1 =null;


        ExprTree EOF2_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:48:3: ( or_expr EOF !)
            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:48:5: or_expr EOF !
            {
            root_0 = (ExprTree)adaptor.nil();


            pushFollow(FOLLOW_or_expr_in_expression110);
            or_expr1=or_expr();

            state._fsp--;

            adaptor.addChild(root_0, or_expr1.getTree());

            EOF2=(Token)match(input,EOF,FOLLOW_EOF_in_expression112); 

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
    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:51:1: or_expr : and_expr ( BAR ^ and_expr )* ;
    public final ExprParser.or_expr_return or_expr() throws RecognitionException {
        ExprParser.or_expr_return retval = new ExprParser.or_expr_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token BAR4=null;
        ExprParser.and_expr_return and_expr3 =null;

        ExprParser.and_expr_return and_expr5 =null;


        ExprTree BAR4_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:52:3: ( and_expr ( BAR ^ and_expr )* )
            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:52:5: and_expr ( BAR ^ and_expr )*
            {
            root_0 = (ExprTree)adaptor.nil();


            pushFollow(FOLLOW_and_expr_in_or_expr126);
            and_expr3=and_expr();

            state._fsp--;

            adaptor.addChild(root_0, and_expr3.getTree());

            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:52:14: ( BAR ^ and_expr )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==BAR) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:52:15: BAR ^ and_expr
            	    {
            	    BAR4=(Token)match(input,BAR,FOLLOW_BAR_in_or_expr129); 
            	    BAR4_tree = 
            	    (ExprTree)adaptor.create(BAR4)
            	    ;
            	    root_0 = (ExprTree)adaptor.becomeRoot(BAR4_tree, root_0);


            	    pushFollow(FOLLOW_and_expr_in_or_expr132);
            	    and_expr5=and_expr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, and_expr5.getTree());

            	    }
            	    break;

            	default :
            	    break loop1;
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
    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:55:1: and_expr : not_expr ( AMP ^ not_expr )* ;
    public final ExprParser.and_expr_return and_expr() throws RecognitionException {
        ExprParser.and_expr_return retval = new ExprParser.and_expr_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token AMP7=null;
        ExprParser.not_expr_return not_expr6 =null;

        ExprParser.not_expr_return not_expr8 =null;


        ExprTree AMP7_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:56:3: ( not_expr ( AMP ^ not_expr )* )
            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:56:5: not_expr ( AMP ^ not_expr )*
            {
            root_0 = (ExprTree)adaptor.nil();


            pushFollow(FOLLOW_not_expr_in_and_expr147);
            not_expr6=not_expr();

            state._fsp--;

            adaptor.addChild(root_0, not_expr6.getTree());

            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:56:14: ( AMP ^ not_expr )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==AMP) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:56:15: AMP ^ not_expr
            	    {
            	    AMP7=(Token)match(input,AMP,FOLLOW_AMP_in_and_expr150); 
            	    AMP7_tree = 
            	    (ExprTree)adaptor.create(AMP7)
            	    ;
            	    root_0 = (ExprTree)adaptor.becomeRoot(AMP7_tree, root_0);


            	    pushFollow(FOLLOW_not_expr_in_and_expr153);
            	    not_expr8=not_expr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, not_expr8.getTree());

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
    // $ANTLR end "and_expr"


    public static class not_expr_return extends ParserRuleReturnScope {
        ExprTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "not_expr"
    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:59:1: not_expr : ( NOT ^ not_expr | equal_expr );
    public final ExprParser.not_expr_return not_expr() throws RecognitionException {
        ExprParser.not_expr_return retval = new ExprParser.not_expr_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token NOT9=null;
        ExprParser.not_expr_return not_expr10 =null;

        ExprParser.equal_expr_return equal_expr11 =null;


        ExprTree NOT9_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:60:3: ( NOT ^ not_expr | equal_expr )
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==NOT) ) {
                alt3=1;
            }
            else if ( (LA3_0==FALSE||LA3_0==ID||LA3_0==LPAR||(LA3_0 >= MINUS && LA3_0 <= NAT_LIT)||LA3_0==REAL_LIT||(LA3_0 >= STRING_LIT && LA3_0 <= TRUE)) ) {
                alt3=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;

            }
            switch (alt3) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:60:5: NOT ^ not_expr
                    {
                    root_0 = (ExprTree)adaptor.nil();


                    NOT9=(Token)match(input,NOT,FOLLOW_NOT_in_not_expr168); 
                    NOT9_tree = 
                    (ExprTree)adaptor.create(NOT9)
                    ;
                    root_0 = (ExprTree)adaptor.becomeRoot(NOT9_tree, root_0);


                    pushFollow(FOLLOW_not_expr_in_not_expr171);
                    not_expr10=not_expr();

                    state._fsp--;

                    adaptor.addChild(root_0, not_expr10.getTree());

                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:61:5: equal_expr
                    {
                    root_0 = (ExprTree)adaptor.nil();


                    pushFollow(FOLLOW_equal_expr_in_not_expr177);
                    equal_expr11=equal_expr();

                    state._fsp--;

                    adaptor.addChild(root_0, equal_expr11.getTree());

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
    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:63:1: equal_expr : compare_expr ( ( EQ | NEQ ) ^ compare_expr )* ;
    public final ExprParser.equal_expr_return equal_expr() throws RecognitionException {
        ExprParser.equal_expr_return retval = new ExprParser.equal_expr_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token set13=null;
        ExprParser.compare_expr_return compare_expr12 =null;

        ExprParser.compare_expr_return compare_expr14 =null;


        ExprTree set13_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:64:3: ( compare_expr ( ( EQ | NEQ ) ^ compare_expr )* )
            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:64:5: compare_expr ( ( EQ | NEQ ) ^ compare_expr )*
            {
            root_0 = (ExprTree)adaptor.nil();


            pushFollow(FOLLOW_compare_expr_in_equal_expr187);
            compare_expr12=compare_expr();

            state._fsp--;

            adaptor.addChild(root_0, compare_expr12.getTree());

            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:64:18: ( ( EQ | NEQ ) ^ compare_expr )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==EQ||LA4_0==NEQ) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:64:19: ( EQ | NEQ ) ^ compare_expr
            	    {
            	    set13=(Token)input.LT(1);

            	    set13=(Token)input.LT(1);

            	    if ( input.LA(1)==EQ||input.LA(1)==NEQ ) {
            	        input.consume();
            	        root_0 = (ExprTree)adaptor.becomeRoot(
            	        (ExprTree)adaptor.create(set13)
            	        , root_0);
            	        state.errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    pushFollow(FOLLOW_compare_expr_in_equal_expr199);
            	    compare_expr14=compare_expr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, compare_expr14.getTree());

            	    }
            	    break;

            	default :
            	    break loop4;
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
    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:66:1: compare_expr : assign_expr ( ( LT | LE | GT | GE ) ^ assign_expr )* ;
    public final ExprParser.compare_expr_return compare_expr() throws RecognitionException {
        ExprParser.compare_expr_return retval = new ExprParser.compare_expr_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token set16=null;
        ExprParser.assign_expr_return assign_expr15 =null;

        ExprParser.assign_expr_return assign_expr17 =null;


        ExprTree set16_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:67:3: ( assign_expr ( ( LT | LE | GT | GE ) ^ assign_expr )* )
            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:67:5: assign_expr ( ( LT | LE | GT | GE ) ^ assign_expr )*
            {
            root_0 = (ExprTree)adaptor.nil();


            pushFollow(FOLLOW_assign_expr_in_compare_expr211);
            assign_expr15=assign_expr();

            state._fsp--;

            adaptor.addChild(root_0, assign_expr15.getTree());

            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:67:17: ( ( LT | LE | GT | GE ) ^ assign_expr )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( ((LA5_0 >= GE && LA5_0 <= GT)||LA5_0==LE||LA5_0==LT) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:67:18: ( LT | LE | GT | GE ) ^ assign_expr
            	    {
            	    set16=(Token)input.LT(1);

            	    set16=(Token)input.LT(1);

            	    if ( (input.LA(1) >= GE && input.LA(1) <= GT)||input.LA(1)==LE||input.LA(1)==LT ) {
            	        input.consume();
            	        root_0 = (ExprTree)adaptor.becomeRoot(
            	        (ExprTree)adaptor.create(set16)
            	        , root_0);
            	        state.errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    pushFollow(FOLLOW_assign_expr_in_compare_expr231);
            	    assign_expr17=assign_expr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, assign_expr17.getTree());

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
    // $ANTLR end "compare_expr"


    public static class assign_expr_return extends ParserRuleReturnScope {
        ExprTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "assign_expr"
    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:70:1: assign_expr : add_expr ;
    public final ExprParser.assign_expr_return assign_expr() throws RecognitionException {
        ExprParser.assign_expr_return retval = new ExprParser.assign_expr_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        ExprParser.add_expr_return add_expr18 =null;



        try {
            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:71:3: ( add_expr )
            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:71:5: add_expr
            {
            root_0 = (ExprTree)adaptor.nil();


            pushFollow(FOLLOW_add_expr_in_assign_expr248);
            add_expr18=add_expr();

            state._fsp--;

            adaptor.addChild(root_0, add_expr18.getTree());

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
    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:74:1: add_expr : mult_expr ( ( PLUS | MINUS ) ^ mult_expr )* ;
    public final ExprParser.add_expr_return add_expr() throws RecognitionException {
        ExprParser.add_expr_return retval = new ExprParser.add_expr_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token set20=null;
        ExprParser.mult_expr_return mult_expr19 =null;

        ExprParser.mult_expr_return mult_expr21 =null;


        ExprTree set20_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:75:3: ( mult_expr ( ( PLUS | MINUS ) ^ mult_expr )* )
            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:75:5: mult_expr ( ( PLUS | MINUS ) ^ mult_expr )*
            {
            root_0 = (ExprTree)adaptor.nil();


            pushFollow(FOLLOW_mult_expr_in_add_expr263);
            mult_expr19=mult_expr();

            state._fsp--;

            adaptor.addChild(root_0, mult_expr19.getTree());

            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:75:15: ( ( PLUS | MINUS ) ^ mult_expr )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==MINUS||LA6_0==PLUS) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:75:16: ( PLUS | MINUS ) ^ mult_expr
            	    {
            	    set20=(Token)input.LT(1);

            	    set20=(Token)input.LT(1);

            	    if ( input.LA(1)==MINUS||input.LA(1)==PLUS ) {
            	        input.consume();
            	        root_0 = (ExprTree)adaptor.becomeRoot(
            	        (ExprTree)adaptor.create(set20)
            	        , root_0);
            	        state.errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    pushFollow(FOLLOW_mult_expr_in_add_expr275);
            	    mult_expr21=mult_expr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, mult_expr21.getTree());

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
    // $ANTLR end "add_expr"


    public static class mult_expr_return extends ParserRuleReturnScope {
        ExprTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "mult_expr"
    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:78:1: mult_expr : unary_expr ( ( ASTERISK | SLASH | PERCENT ) ^ unary_expr )* ;
    public final ExprParser.mult_expr_return mult_expr() throws RecognitionException {
        ExprParser.mult_expr_return retval = new ExprParser.mult_expr_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token set23=null;
        ExprParser.unary_expr_return unary_expr22 =null;

        ExprParser.unary_expr_return unary_expr24 =null;


        ExprTree set23_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:79:3: ( unary_expr ( ( ASTERISK | SLASH | PERCENT ) ^ unary_expr )* )
            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:79:5: unary_expr ( ( ASTERISK | SLASH | PERCENT ) ^ unary_expr )*
            {
            root_0 = (ExprTree)adaptor.nil();


            pushFollow(FOLLOW_unary_expr_in_mult_expr290);
            unary_expr22=unary_expr();

            state._fsp--;

            adaptor.addChild(root_0, unary_expr22.getTree());

            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:79:16: ( ( ASTERISK | SLASH | PERCENT ) ^ unary_expr )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==ASTERISK||LA7_0==PERCENT||LA7_0==SLASH) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:79:17: ( ASTERISK | SLASH | PERCENT ) ^ unary_expr
            	    {
            	    set23=(Token)input.LT(1);

            	    set23=(Token)input.LT(1);

            	    if ( input.LA(1)==ASTERISK||input.LA(1)==PERCENT||input.LA(1)==SLASH ) {
            	        input.consume();
            	        root_0 = (ExprTree)adaptor.becomeRoot(
            	        (ExprTree)adaptor.create(set23)
            	        , root_0);
            	        state.errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    pushFollow(FOLLOW_unary_expr_in_mult_expr306);
            	    unary_expr24=unary_expr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, unary_expr24.getTree());

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
    // $ANTLR end "mult_expr"


    public static class unary_expr_return extends ParserRuleReturnScope {
        ExprTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "unary_expr"
    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:82:1: unary_expr : ( MINUS ^ unary_expr | atom_expr );
    public final ExprParser.unary_expr_return unary_expr() throws RecognitionException {
        ExprParser.unary_expr_return retval = new ExprParser.unary_expr_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token MINUS25=null;
        ExprParser.unary_expr_return unary_expr26 =null;

        ExprParser.atom_expr_return atom_expr27 =null;


        ExprTree MINUS25_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:83:3: ( MINUS ^ unary_expr | atom_expr )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==MINUS) ) {
                alt8=1;
            }
            else if ( (LA8_0==FALSE||LA8_0==ID||LA8_0==LPAR||LA8_0==NAT_LIT||LA8_0==REAL_LIT||(LA8_0 >= STRING_LIT && LA8_0 <= TRUE)) ) {
                alt8=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;

            }
            switch (alt8) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:83:5: MINUS ^ unary_expr
                    {
                    root_0 = (ExprTree)adaptor.nil();


                    MINUS25=(Token)match(input,MINUS,FOLLOW_MINUS_in_unary_expr321); 
                    MINUS25_tree = 
                    (ExprTree)adaptor.create(MINUS25)
                    ;
                    root_0 = (ExprTree)adaptor.becomeRoot(MINUS25_tree, root_0);


                    pushFollow(FOLLOW_unary_expr_in_unary_expr324);
                    unary_expr26=unary_expr();

                    state._fsp--;

                    adaptor.addChild(root_0, unary_expr26.getTree());

                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:84:5: atom_expr
                    {
                    root_0 = (ExprTree)adaptor.nil();


                    pushFollow(FOLLOW_atom_expr_in_unary_expr330);
                    atom_expr27=atom_expr();

                    state._fsp--;

                    adaptor.addChild(root_0, atom_expr27.getTree());

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
    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:87:1: atom_expr : ( constant | variableOrField | call |open= LPAR or_expr close= RPAR -> ^( LPAR[$open,\"\"] or_expr RPAR[$close,\"\"] ) );
    public final ExprParser.atom_expr_return atom_expr() throws RecognitionException {
        ExprParser.atom_expr_return retval = new ExprParser.atom_expr_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token open=null;
        Token close=null;
        ExprParser.constant_return constant28 =null;

        ExprParser.variableOrField_return variableOrField29 =null;

        ExprParser.call_return call30 =null;

        ExprParser.or_expr_return or_expr31 =null;


        ExprTree open_tree=null;
        ExprTree close_tree=null;
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleSubtreeStream stream_or_expr=new RewriteRuleSubtreeStream(adaptor,"rule or_expr");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:88:3: ( constant | variableOrField | call |open= LPAR or_expr close= RPAR -> ^( LPAR[$open,\"\"] or_expr RPAR[$close,\"\"] ) )
            int alt9=4;
            switch ( input.LA(1) ) {
            case ID:
                {
                switch ( input.LA(2) ) {
                case COLON:
                    {
                    int LA9_4 = input.LA(3);

                    if ( (LA9_4==ID) ) {
                        int LA9_7 = input.LA(4);

                        if ( (LA9_7==EOF||(LA9_7 >= AMP && LA9_7 <= BAR)||LA9_7==COMMA||(LA9_7 >= DOT && LA9_7 <= EQ)||(LA9_7 >= GE && LA9_7 <= GT)||LA9_7==LE||(LA9_7 >= LT && LA9_7 <= MINUS)||LA9_7==NEQ||(LA9_7 >= PERCENT && LA9_7 <= PLUS)||LA9_7==RPAR||LA9_7==SLASH) ) {
                            alt9=2;
                        }
                        else if ( (LA9_7==LPAR) ) {
                            alt9=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 9, 7, input);

                            throw nvae;

                        }
                    }
                    else if ( (LA9_4==FALSE||LA9_4==NAT_LIT||LA9_4==REAL_LIT||(LA9_4 >= STRING_LIT && LA9_4 <= TRUE)) ) {
                        alt9=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 9, 4, input);

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
                    alt9=2;
                    }
                    break;
                case LPAR:
                    {
                    alt9=3;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 9, 1, input);

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
                alt9=1;
                }
                break;
            case LPAR:
                {
                alt9=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;

            }

            switch (alt9) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:88:5: constant
                    {
                    root_0 = (ExprTree)adaptor.nil();


                    pushFollow(FOLLOW_constant_in_atom_expr343);
                    constant28=constant();

                    state._fsp--;

                    adaptor.addChild(root_0, constant28.getTree());

                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:89:5: variableOrField
                    {
                    root_0 = (ExprTree)adaptor.nil();


                    pushFollow(FOLLOW_variableOrField_in_atom_expr349);
                    variableOrField29=variableOrField();

                    state._fsp--;

                    adaptor.addChild(root_0, variableOrField29.getTree());

                    }
                    break;
                case 3 :
                    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:90:5: call
                    {
                    root_0 = (ExprTree)adaptor.nil();


                    pushFollow(FOLLOW_call_in_atom_expr355);
                    call30=call();

                    state._fsp--;

                    adaptor.addChild(root_0, call30.getTree());

                    }
                    break;
                case 4 :
                    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:91:5: open= LPAR or_expr close= RPAR
                    {
                    open=(Token)match(input,LPAR,FOLLOW_LPAR_in_atom_expr363);  
                    stream_LPAR.add(open);


                    pushFollow(FOLLOW_or_expr_in_atom_expr365);
                    or_expr31=or_expr();

                    state._fsp--;

                    stream_or_expr.add(or_expr31.getTree());

                    close=(Token)match(input,RPAR,FOLLOW_RPAR_in_atom_expr369);  
                    stream_RPAR.add(close);


                    // AST REWRITE
                    // elements: LPAR, RPAR, or_expr
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (ExprTree)adaptor.nil();
                    // 92:5: -> ^( LPAR[$open,\"\"] or_expr RPAR[$close,\"\"] )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:92:8: ^( LPAR[$open,\"\"] or_expr RPAR[$close,\"\"] )
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


    public static class constant_return extends ParserRuleReturnScope {
        ExprTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "constant"
    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:95:1: constant : (prefix= ID COLON literal -> ^( CONST literal ID ) | literal -> ^( CONST literal ) );
    public final ExprParser.constant_return constant() throws RecognitionException {
        ExprParser.constant_return retval = new ExprParser.constant_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token prefix=null;
        Token COLON32=null;
        ExprParser.literal_return literal33 =null;

        ExprParser.literal_return literal34 =null;


        ExprTree prefix_tree=null;
        ExprTree COLON32_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_literal=new RewriteRuleSubtreeStream(adaptor,"rule literal");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:96:3: (prefix= ID COLON literal -> ^( CONST literal ID ) | literal -> ^( CONST literal ) )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==ID) ) {
                alt10=1;
            }
            else if ( (LA10_0==FALSE||LA10_0==NAT_LIT||LA10_0==REAL_LIT||(LA10_0 >= STRING_LIT && LA10_0 <= TRUE)) ) {
                alt10=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;

            }
            switch (alt10) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:96:5: prefix= ID COLON literal
                    {
                    prefix=(Token)match(input,ID,FOLLOW_ID_in_constant400);  
                    stream_ID.add(prefix);


                    COLON32=(Token)match(input,COLON,FOLLOW_COLON_in_constant402);  
                    stream_COLON.add(COLON32);


                    pushFollow(FOLLOW_literal_in_constant404);
                    literal33=literal();

                    state._fsp--;

                    stream_literal.add(literal33.getTree());

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
                    // 97:5: -> ^( CONST literal ID )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:97:8: ^( CONST literal ID )
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
                    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:98:5: literal
                    {
                    pushFollow(FOLLOW_literal_in_constant424);
                    literal34=literal();

                    state._fsp--;

                    stream_literal.add(literal34.getTree());

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
                    // 99:5: -> ^( CONST literal )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:99:8: ^( CONST literal )
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
    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:102:1: parameter : (prefix= ID DOLLAR NAT_LIT -> ^( PAR NAT_LIT $prefix) | DOLLAR NAT_LIT -> ^( PAR NAT_LIT ) );
    public final ExprParser.parameter_return parameter() throws RecognitionException {
        ExprParser.parameter_return retval = new ExprParser.parameter_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token prefix=null;
        Token DOLLAR35=null;
        Token NAT_LIT36=null;
        Token DOLLAR37=null;
        Token NAT_LIT38=null;

        ExprTree prefix_tree=null;
        ExprTree DOLLAR35_tree=null;
        ExprTree NAT_LIT36_tree=null;
        ExprTree DOLLAR37_tree=null;
        ExprTree NAT_LIT38_tree=null;
        RewriteRuleTokenStream stream_DOLLAR=new RewriteRuleTokenStream(adaptor,"token DOLLAR");
        RewriteRuleTokenStream stream_NAT_LIT=new RewriteRuleTokenStream(adaptor,"token NAT_LIT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:103:3: (prefix= ID DOLLAR NAT_LIT -> ^( PAR NAT_LIT $prefix) | DOLLAR NAT_LIT -> ^( PAR NAT_LIT ) )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==ID) ) {
                alt11=1;
            }
            else if ( (LA11_0==DOLLAR) ) {
                alt11=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;

            }
            switch (alt11) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:103:5: prefix= ID DOLLAR NAT_LIT
                    {
                    prefix=(Token)match(input,ID,FOLLOW_ID_in_parameter451);  
                    stream_ID.add(prefix);


                    DOLLAR35=(Token)match(input,DOLLAR,FOLLOW_DOLLAR_in_parameter453);  
                    stream_DOLLAR.add(DOLLAR35);


                    NAT_LIT36=(Token)match(input,NAT_LIT,FOLLOW_NAT_LIT_in_parameter455);  
                    stream_NAT_LIT.add(NAT_LIT36);


                    // AST REWRITE
                    // elements: prefix, NAT_LIT
                    // token labels: prefix
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_prefix=new RewriteRuleTokenStream(adaptor,"token prefix",prefix);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (ExprTree)adaptor.nil();
                    // 104:5: -> ^( PAR NAT_LIT $prefix)
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:104:8: ^( PAR NAT_LIT $prefix)
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
                    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:105:5: DOLLAR NAT_LIT
                    {
                    DOLLAR37=(Token)match(input,DOLLAR,FOLLOW_DOLLAR_in_parameter476);  
                    stream_DOLLAR.add(DOLLAR37);


                    NAT_LIT38=(Token)match(input,NAT_LIT,FOLLOW_NAT_LIT_in_parameter478);  
                    stream_NAT_LIT.add(NAT_LIT38);


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
                    // 106:5: -> ^( PAR NAT_LIT )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:106:8: ^( PAR NAT_LIT )
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


    public static class variableOrField_return extends ParserRuleReturnScope {
        ExprTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "variableOrField"
    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:109:1: variableOrField : (prefix= ID COLON name= ID ( DOT field1= ID -> ^( FIELD $name $field1 $prefix) | -> ^( VAR $name $prefix) ) |name= ID ( DOT field2= ID -> ^( FIELD $name $field2) | -> ^( VAR $name) ) );
    public final ExprParser.variableOrField_return variableOrField() throws RecognitionException {
        ExprParser.variableOrField_return retval = new ExprParser.variableOrField_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token prefix=null;
        Token name=null;
        Token field1=null;
        Token field2=null;
        Token COLON39=null;
        Token DOT40=null;
        Token DOT41=null;

        ExprTree prefix_tree=null;
        ExprTree name_tree=null;
        ExprTree field1_tree=null;
        ExprTree field2_tree=null;
        ExprTree COLON39_tree=null;
        ExprTree DOT40_tree=null;
        ExprTree DOT41_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");

        try {
            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:110:3: (prefix= ID COLON name= ID ( DOT field1= ID -> ^( FIELD $name $field1 $prefix) | -> ^( VAR $name $prefix) ) |name= ID ( DOT field2= ID -> ^( FIELD $name $field2) | -> ^( VAR $name) ) )
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==ID) ) {
                int LA14_1 = input.LA(2);

                if ( (LA14_1==COLON) ) {
                    alt14=1;
                }
                else if ( (LA14_1==EOF||(LA14_1 >= AMP && LA14_1 <= BAR)||LA14_1==COMMA||(LA14_1 >= DOT && LA14_1 <= EQ)||(LA14_1 >= GE && LA14_1 <= GT)||LA14_1==LE||(LA14_1 >= LT && LA14_1 <= MINUS)||LA14_1==NEQ||(LA14_1 >= PERCENT && LA14_1 <= PLUS)||LA14_1==RPAR||LA14_1==SLASH) ) {
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
                    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:110:5: prefix= ID COLON name= ID ( DOT field1= ID -> ^( FIELD $name $field1 $prefix) | -> ^( VAR $name $prefix) )
                    {
                    prefix=(Token)match(input,ID,FOLLOW_ID_in_variableOrField505);  
                    stream_ID.add(prefix);


                    COLON39=(Token)match(input,COLON,FOLLOW_COLON_in_variableOrField507);  
                    stream_COLON.add(COLON39);


                    name=(Token)match(input,ID,FOLLOW_ID_in_variableOrField511);  
                    stream_ID.add(name);


                    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:111:5: ( DOT field1= ID -> ^( FIELD $name $field1 $prefix) | -> ^( VAR $name $prefix) )
                    int alt12=2;
                    int LA12_0 = input.LA(1);

                    if ( (LA12_0==DOT) ) {
                        alt12=1;
                    }
                    else if ( (LA12_0==EOF||(LA12_0 >= AMP && LA12_0 <= BAR)||LA12_0==COMMA||LA12_0==EQ||(LA12_0 >= GE && LA12_0 <= GT)||LA12_0==LE||(LA12_0 >= LT && LA12_0 <= MINUS)||LA12_0==NEQ||(LA12_0 >= PERCENT && LA12_0 <= PLUS)||LA12_0==RPAR||LA12_0==SLASH) ) {
                        alt12=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 12, 0, input);

                        throw nvae;

                    }
                    switch (alt12) {
                        case 1 :
                            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:111:7: DOT field1= ID
                            {
                            DOT40=(Token)match(input,DOT,FOLLOW_DOT_in_variableOrField520);  
                            stream_DOT.add(DOT40);


                            field1=(Token)match(input,ID,FOLLOW_ID_in_variableOrField524);  
                            stream_ID.add(field1);


                            // AST REWRITE
                            // elements: prefix, name, field1
                            // token labels: name, prefix, field1
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            retval.tree = root_0;
                            RewriteRuleTokenStream stream_name=new RewriteRuleTokenStream(adaptor,"token name",name);
                            RewriteRuleTokenStream stream_prefix=new RewriteRuleTokenStream(adaptor,"token prefix",prefix);
                            RewriteRuleTokenStream stream_field1=new RewriteRuleTokenStream(adaptor,"token field1",field1);
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (ExprTree)adaptor.nil();
                            // 112:7: -> ^( FIELD $name $field1 $prefix)
                            {
                                // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:112:10: ^( FIELD $name $field1 $prefix)
                                {
                                ExprTree root_1 = (ExprTree)adaptor.nil();
                                root_1 = (ExprTree)adaptor.becomeRoot(
                                (ExprTree)adaptor.create(FIELD, "FIELD")
                                , root_1);

                                adaptor.addChild(root_1, stream_name.nextNode());

                                adaptor.addChild(root_1, stream_field1.nextNode());

                                adaptor.addChild(root_1, stream_prefix.nextNode());

                                adaptor.addChild(root_0, root_1);
                                }

                            }


                            retval.tree = root_0;

                            }
                            break;
                        case 2 :
                            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:113:7: 
                            {
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
                            // 113:7: -> ^( VAR $name $prefix)
                            {
                                // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:113:10: ^( VAR $name $prefix)
                                {
                                ExprTree root_1 = (ExprTree)adaptor.nil();
                                root_1 = (ExprTree)adaptor.becomeRoot(
                                (ExprTree)adaptor.create(VAR, "VAR")
                                , root_1);

                                adaptor.addChild(root_1, stream_name.nextNode());

                                adaptor.addChild(root_1, stream_prefix.nextNode());

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
                    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:115:5: name= ID ( DOT field2= ID -> ^( FIELD $name $field2) | -> ^( VAR $name) )
                    {
                    name=(Token)match(input,ID,FOLLOW_ID_in_variableOrField577);  
                    stream_ID.add(name);


                    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:116:5: ( DOT field2= ID -> ^( FIELD $name $field2) | -> ^( VAR $name) )
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0==DOT) ) {
                        alt13=1;
                    }
                    else if ( (LA13_0==EOF||(LA13_0 >= AMP && LA13_0 <= BAR)||LA13_0==COMMA||LA13_0==EQ||(LA13_0 >= GE && LA13_0 <= GT)||LA13_0==LE||(LA13_0 >= LT && LA13_0 <= MINUS)||LA13_0==NEQ||(LA13_0 >= PERCENT && LA13_0 <= PLUS)||LA13_0==RPAR||LA13_0==SLASH) ) {
                        alt13=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 13, 0, input);

                        throw nvae;

                    }
                    switch (alt13) {
                        case 1 :
                            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:116:7: DOT field2= ID
                            {
                            DOT41=(Token)match(input,DOT,FOLLOW_DOT_in_variableOrField585);  
                            stream_DOT.add(DOT41);


                            field2=(Token)match(input,ID,FOLLOW_ID_in_variableOrField589);  
                            stream_ID.add(field2);


                            // AST REWRITE
                            // elements: name, field2
                            // token labels: field2, name
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            retval.tree = root_0;
                            RewriteRuleTokenStream stream_field2=new RewriteRuleTokenStream(adaptor,"token field2",field2);
                            RewriteRuleTokenStream stream_name=new RewriteRuleTokenStream(adaptor,"token name",name);
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (ExprTree)adaptor.nil();
                            // 117:7: -> ^( FIELD $name $field2)
                            {
                                // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:117:10: ^( FIELD $name $field2)
                                {
                                ExprTree root_1 = (ExprTree)adaptor.nil();
                                root_1 = (ExprTree)adaptor.becomeRoot(
                                (ExprTree)adaptor.create(FIELD, "FIELD")
                                , root_1);

                                adaptor.addChild(root_1, stream_name.nextNode());

                                adaptor.addChild(root_1, stream_field2.nextNode());

                                adaptor.addChild(root_0, root_1);
                                }

                            }


                            retval.tree = root_0;

                            }
                            break;
                        case 2 :
                            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:118:7: 
                            {
                            // AST REWRITE
                            // elements: name
                            // token labels: name
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            retval.tree = root_0;
                            RewriteRuleTokenStream stream_name=new RewriteRuleTokenStream(adaptor,"token name",name);
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (ExprTree)adaptor.nil();
                            // 118:7: -> ^( VAR $name)
                            {
                                // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:118:10: ^( VAR $name)
                                {
                                ExprTree root_1 = (ExprTree)adaptor.nil();
                                root_1 = (ExprTree)adaptor.becomeRoot(
                                (ExprTree)adaptor.create(VAR, "VAR")
                                , root_1);

                                adaptor.addChild(root_1, stream_name.nextNode());

                                adaptor.addChild(root_0, root_1);
                                }

                            }


                            retval.tree = root_0;

                            }
                            break;

                    }


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
    // $ANTLR end "variableOrField"


    public static class call_return extends ParserRuleReturnScope {
        ExprTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "call"
    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:122:1: call : oper LPAR ( or_expr ( COMMA or_expr )* )? close= RPAR -> ^( CALL oper ( or_expr )* RPAR[$close,\"\"] ) ;
    public final ExprParser.call_return call() throws RecognitionException {
        ExprParser.call_return retval = new ExprParser.call_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token close=null;
        Token LPAR43=null;
        Token COMMA45=null;
        ExprParser.oper_return oper42 =null;

        ExprParser.or_expr_return or_expr44 =null;

        ExprParser.or_expr_return or_expr46 =null;


        ExprTree close_tree=null;
        ExprTree LPAR43_tree=null;
        ExprTree COMMA45_tree=null;
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_or_expr=new RewriteRuleSubtreeStream(adaptor,"rule or_expr");
        RewriteRuleSubtreeStream stream_oper=new RewriteRuleSubtreeStream(adaptor,"rule oper");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:123:3: ( oper LPAR ( or_expr ( COMMA or_expr )* )? close= RPAR -> ^( CALL oper ( or_expr )* RPAR[$close,\"\"] ) )
            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:123:5: oper LPAR ( or_expr ( COMMA or_expr )* )? close= RPAR
            {
            pushFollow(FOLLOW_oper_in_call641);
            oper42=oper();

            state._fsp--;

            stream_oper.add(oper42.getTree());

            LPAR43=(Token)match(input,LPAR,FOLLOW_LPAR_in_call643);  
            stream_LPAR.add(LPAR43);


            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:123:15: ( or_expr ( COMMA or_expr )* )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==FALSE||LA16_0==ID||LA16_0==LPAR||(LA16_0 >= MINUS && LA16_0 <= NAT_LIT)||LA16_0==NOT||LA16_0==REAL_LIT||(LA16_0 >= STRING_LIT && LA16_0 <= TRUE)) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:123:16: or_expr ( COMMA or_expr )*
                    {
                    pushFollow(FOLLOW_or_expr_in_call646);
                    or_expr44=or_expr();

                    state._fsp--;

                    stream_or_expr.add(or_expr44.getTree());

                    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:123:24: ( COMMA or_expr )*
                    loop15:
                    do {
                        int alt15=2;
                        int LA15_0 = input.LA(1);

                        if ( (LA15_0==COMMA) ) {
                            alt15=1;
                        }


                        switch (alt15) {
                    	case 1 :
                    	    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:123:25: COMMA or_expr
                    	    {
                    	    COMMA45=(Token)match(input,COMMA,FOLLOW_COMMA_in_call649);  
                    	    stream_COMMA.add(COMMA45);


                    	    pushFollow(FOLLOW_or_expr_in_call651);
                    	    or_expr46=or_expr();

                    	    state._fsp--;

                    	    stream_or_expr.add(or_expr46.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop15;
                        }
                    } while (true);


                    }
                    break;

            }


            close=(Token)match(input,RPAR,FOLLOW_RPAR_in_call659);  
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
            // 124:4: -> ^( CALL oper ( or_expr )* RPAR[$close,\"\"] )
            {
                // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:124:7: ^( CALL oper ( or_expr )* RPAR[$close,\"\"] )
                {
                ExprTree root_1 = (ExprTree)adaptor.nil();
                root_1 = (ExprTree)adaptor.becomeRoot(
                (ExprTree)adaptor.create(CALL, "CALL")
                , root_1);

                adaptor.addChild(root_1, stream_oper.nextTree());

                // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:124:19: ( or_expr )*
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
    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:127:1: oper : (prefix= ID COLON name= ID -> ^( OPER $name $prefix) | ID -> ^( OPER ID ) );
    public final ExprParser.oper_return oper() throws RecognitionException {
        ExprParser.oper_return retval = new ExprParser.oper_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token prefix=null;
        Token name=null;
        Token COLON47=null;
        Token ID48=null;

        ExprTree prefix_tree=null;
        ExprTree name_tree=null;
        ExprTree COLON47_tree=null;
        ExprTree ID48_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:128:3: (prefix= ID COLON name= ID -> ^( OPER $name $prefix) | ID -> ^( OPER ID ) )
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==ID) ) {
                int LA17_1 = input.LA(2);

                if ( (LA17_1==COLON) ) {
                    alt17=1;
                }
                else if ( (LA17_1==LPAR) ) {
                    alt17=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 17, 1, input);

                    throw nvae;

                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 17, 0, input);

                throw nvae;

            }
            switch (alt17) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:128:5: prefix= ID COLON name= ID
                    {
                    prefix=(Token)match(input,ID,FOLLOW_ID_in_oper691);  
                    stream_ID.add(prefix);


                    COLON47=(Token)match(input,COLON,FOLLOW_COLON_in_oper693);  
                    stream_COLON.add(COLON47);


                    name=(Token)match(input,ID,FOLLOW_ID_in_oper697);  
                    stream_ID.add(name);


                    // AST REWRITE
                    // elements: name, prefix
                    // token labels: prefix, name
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_prefix=new RewriteRuleTokenStream(adaptor,"token prefix",prefix);
                    RewriteRuleTokenStream stream_name=new RewriteRuleTokenStream(adaptor,"token name",name);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (ExprTree)adaptor.nil();
                    // 128:29: -> ^( OPER $name $prefix)
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:128:32: ^( OPER $name $prefix)
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
                    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:129:5: ID
                    {
                    ID48=(Token)match(input,ID,FOLLOW_ID_in_oper715);  
                    stream_ID.add(ID48);


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
                    // 129:8: -> ^( OPER ID )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:129:11: ^( OPER ID )
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
    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:132:1: literal : ( REAL_LIT -> ^( REAL REAL_LIT ) | NAT_LIT -> ^( INT NAT_LIT ) | STRING_LIT -> ^( STRING STRING_LIT ) | TRUE -> ^( BOOL TRUE ) | FALSE -> ^( BOOL FALSE ) );
    public final ExprParser.literal_return literal() throws RecognitionException {
        ExprParser.literal_return retval = new ExprParser.literal_return();
        retval.start = input.LT(1);


        ExprTree root_0 = null;

        Token REAL_LIT49=null;
        Token NAT_LIT50=null;
        Token STRING_LIT51=null;
        Token TRUE52=null;
        Token FALSE53=null;

        ExprTree REAL_LIT49_tree=null;
        ExprTree NAT_LIT50_tree=null;
        ExprTree STRING_LIT51_tree=null;
        ExprTree TRUE52_tree=null;
        ExprTree FALSE53_tree=null;
        RewriteRuleTokenStream stream_REAL_LIT=new RewriteRuleTokenStream(adaptor,"token REAL_LIT");
        RewriteRuleTokenStream stream_NAT_LIT=new RewriteRuleTokenStream(adaptor,"token NAT_LIT");
        RewriteRuleTokenStream stream_FALSE=new RewriteRuleTokenStream(adaptor,"token FALSE");
        RewriteRuleTokenStream stream_TRUE=new RewriteRuleTokenStream(adaptor,"token TRUE");
        RewriteRuleTokenStream stream_STRING_LIT=new RewriteRuleTokenStream(adaptor,"token STRING_LIT");

        try {
            // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:133:3: ( REAL_LIT -> ^( REAL REAL_LIT ) | NAT_LIT -> ^( INT NAT_LIT ) | STRING_LIT -> ^( STRING STRING_LIT ) | TRUE -> ^( BOOL TRUE ) | FALSE -> ^( BOOL FALSE ) )
            int alt18=5;
            switch ( input.LA(1) ) {
            case REAL_LIT:
                {
                alt18=1;
                }
                break;
            case NAT_LIT:
                {
                alt18=2;
                }
                break;
            case STRING_LIT:
                {
                alt18=3;
                }
                break;
            case TRUE:
                {
                alt18=4;
                }
                break;
            case FALSE:
                {
                alt18=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;

            }

            switch (alt18) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:133:5: REAL_LIT
                    {
                    REAL_LIT49=(Token)match(input,REAL_LIT,FOLLOW_REAL_LIT_in_literal736);  
                    stream_REAL_LIT.add(REAL_LIT49);


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
                    // 133:14: -> ^( REAL REAL_LIT )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:133:17: ^( REAL REAL_LIT )
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
                    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:134:5: NAT_LIT
                    {
                    NAT_LIT50=(Token)match(input,NAT_LIT,FOLLOW_NAT_LIT_in_literal750);  
                    stream_NAT_LIT.add(NAT_LIT50);


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
                    // 134:13: -> ^( INT NAT_LIT )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:134:16: ^( INT NAT_LIT )
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
                    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:135:5: STRING_LIT
                    {
                    STRING_LIT51=(Token)match(input,STRING_LIT,FOLLOW_STRING_LIT_in_literal764);  
                    stream_STRING_LIT.add(STRING_LIT51);


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
                    // 135:16: -> ^( STRING STRING_LIT )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:135:19: ^( STRING STRING_LIT )
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
                    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:136:5: TRUE
                    {
                    TRUE52=(Token)match(input,TRUE,FOLLOW_TRUE_in_literal778);  
                    stream_TRUE.add(TRUE52);


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
                    // 136:10: -> ^( BOOL TRUE )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:136:13: ^( BOOL TRUE )
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
                    // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:137:5: FALSE
                    {
                    FALSE53=(Token)match(input,FALSE,FOLLOW_FALSE_in_literal792);  
                    stream_FALSE.add(FALSE53);


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
                    // 137:11: -> ^( BOOL FALSE )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\algebra\\syntax\\Expr.g:137:14: ^( BOOL FALSE )
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


 

    public static final BitSet FOLLOW_or_expr_in_expression110 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_expression112 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_expr_in_or_expr126 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_BAR_in_or_expr129 = new BitSet(new long[]{0x00018200B4440000L});
    public static final BitSet FOLLOW_and_expr_in_or_expr132 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_not_expr_in_and_expr147 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_AMP_in_and_expr150 = new BitSet(new long[]{0x00018200B4440000L});
    public static final BitSet FOLLOW_not_expr_in_and_expr153 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_NOT_in_not_expr168 = new BitSet(new long[]{0x00018200B4440000L});
    public static final BitSet FOLLOW_not_expr_in_not_expr171 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_equal_expr_in_not_expr177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_compare_expr_in_equal_expr187 = new BitSet(new long[]{0x0000000040010002L});
    public static final BitSet FOLLOW_set_in_equal_expr190 = new BitSet(new long[]{0x0001820034440000L});
    public static final BitSet FOLLOW_compare_expr_in_equal_expr199 = new BitSet(new long[]{0x0000000040010002L});
    public static final BitSet FOLLOW_assign_expr_in_compare_expr211 = new BitSet(new long[]{0x000000000A300002L});
    public static final BitSet FOLLOW_set_in_compare_expr214 = new BitSet(new long[]{0x0001820034440000L});
    public static final BitSet FOLLOW_assign_expr_in_compare_expr231 = new BitSet(new long[]{0x000000000A300002L});
    public static final BitSet FOLLOW_add_expr_in_assign_expr248 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mult_expr_in_add_expr263 = new BitSet(new long[]{0x0000002010000002L});
    public static final BitSet FOLLOW_set_in_add_expr266 = new BitSet(new long[]{0x0001820034440000L});
    public static final BitSet FOLLOW_mult_expr_in_add_expr275 = new BitSet(new long[]{0x0000002010000002L});
    public static final BitSet FOLLOW_unary_expr_in_mult_expr290 = new BitSet(new long[]{0x0000201000000022L});
    public static final BitSet FOLLOW_set_in_mult_expr293 = new BitSet(new long[]{0x0001820034440000L});
    public static final BitSet FOLLOW_unary_expr_in_mult_expr306 = new BitSet(new long[]{0x0000201000000022L});
    public static final BitSet FOLLOW_MINUS_in_unary_expr321 = new BitSet(new long[]{0x0001820034440000L});
    public static final BitSet FOLLOW_unary_expr_in_unary_expr324 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_expr_in_unary_expr330 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constant_in_atom_expr343 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableOrField_in_atom_expr349 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_atom_expr355 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_atom_expr363 = new BitSet(new long[]{0x00018200B4440000L});
    public static final BitSet FOLLOW_or_expr_in_atom_expr365 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_RPAR_in_atom_expr369 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_constant400 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_COLON_in_constant402 = new BitSet(new long[]{0x0001820020040000L});
    public static final BitSet FOLLOW_literal_in_constant404 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_constant424 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_parameter451 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_DOLLAR_in_parameter453 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_NAT_LIT_in_parameter455 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOLLAR_in_parameter476 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_NAT_LIT_in_parameter478 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_variableOrField505 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_COLON_in_variableOrField507 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_ID_in_variableOrField511 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_DOT_in_variableOrField520 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_ID_in_variableOrField524 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_variableOrField577 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_DOT_in_variableOrField585 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_ID_in_variableOrField589 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_oper_in_call641 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_LPAR_in_call643 = new BitSet(new long[]{0x00018600B4440000L});
    public static final BitSet FOLLOW_or_expr_in_call646 = new BitSet(new long[]{0x0000040000000800L});
    public static final BitSet FOLLOW_COMMA_in_call649 = new BitSet(new long[]{0x00018200B4440000L});
    public static final BitSet FOLLOW_or_expr_in_call651 = new BitSet(new long[]{0x0000040000000800L});
    public static final BitSet FOLLOW_RPAR_in_call659 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_oper691 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_COLON_in_oper693 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_ID_in_oper697 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_oper715 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REAL_LIT_in_literal736 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAT_LIT_in_literal750 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LIT_in_literal764 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_literal778 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_literal792 = new BitSet(new long[]{0x0000000000000002L});

}
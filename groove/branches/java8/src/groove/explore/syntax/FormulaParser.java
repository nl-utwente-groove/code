// $ANTLR 3.4 D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g 2014-12-21 10:48:52

package groove.explore.syntax;
import groove.util.parse.FormatErrorSet;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

import org.antlr.runtime.tree.*;


@SuppressWarnings({"all", "warnings", "unchecked"})
public class FormulaParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "AMP", "AND", "BAR", "BOOL", "BSLASH", "CALL", "COMMA", "CONST", "DONT_CARE", "DOT", "EQUIV", "EscapeSequence", "FALSE", "FIELD", "ID", "IMPL", "IMPL_BY", "INT", "LPAR", "MINUS", "NAT_LIT", "NOT", "NaturalNumber", "NonIntegerNumber", "OPER", "OR", "PAR", "QUOTE", "REAL", "REAL_LIT", "RPAR", "STRING", "STRING_LIT", "TRUE", "WS"
    };

    public static final int EOF=-1;
    public static final int AMP=4;
    public static final int AND=5;
    public static final int BAR=6;
    public static final int BOOL=7;
    public static final int BSLASH=8;
    public static final int CALL=9;
    public static final int COMMA=10;
    public static final int CONST=11;
    public static final int DONT_CARE=12;
    public static final int DOT=13;
    public static final int EQUIV=14;
    public static final int EscapeSequence=15;
    public static final int FALSE=16;
    public static final int FIELD=17;
    public static final int ID=18;
    public static final int IMPL=19;
    public static final int IMPL_BY=20;
    public static final int INT=21;
    public static final int LPAR=22;
    public static final int MINUS=23;
    public static final int NAT_LIT=24;
    public static final int NOT=25;
    public static final int NaturalNumber=26;
    public static final int NonIntegerNumber=27;
    public static final int OPER=28;
    public static final int OR=29;
    public static final int PAR=30;
    public static final int QUOTE=31;
    public static final int REAL=32;
    public static final int REAL_LIT=33;
    public static final int RPAR=34;
    public static final int STRING=35;
    public static final int STRING_LIT=36;
    public static final int TRUE=37;
    public static final int WS=38;

    // delegates
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public FormulaParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public FormulaParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

protected TreeAdaptor adaptor = new CommonTreeAdaptor();

public void setTreeAdaptor(TreeAdaptor adaptor) {
    this.adaptor = adaptor;
}
public TreeAdaptor getTreeAdaptor() {
    return adaptor;
}
    public String[] getTokenNames() { return FormulaParser.tokenNames; }
    public String getGrammarFileName() { return "D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g"; }


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


    public static class formula_return extends ParserRuleReturnScope {
        FormulaTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "formula"
    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:45:1: formula : or_expr EOF !;
    public final FormulaParser.formula_return formula() throws RecognitionException {
        FormulaParser.formula_return retval = new FormulaParser.formula_return();
        retval.start = input.LT(1);


        FormulaTree root_0 = null;

        Token EOF2=null;
        FormulaParser.or_expr_return or_expr1 =null;


        FormulaTree EOF2_tree=null;

        try {
            // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:46:3: ( or_expr EOF !)
            // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:46:5: or_expr EOF !
            {
            root_0 = (FormulaTree)adaptor.nil();


            pushFollow(FOLLOW_or_expr_in_formula120);
            or_expr1=or_expr();

            state._fsp--;

            adaptor.addChild(root_0, or_expr1.getTree());

            EOF2=(Token)match(input,EOF,FOLLOW_EOF_in_formula122); 

            }

            retval.stop = input.LT(-1);


            retval.tree = (FormulaTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (FormulaTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "formula"


    public static class or_expr_return extends ParserRuleReturnScope {
        FormulaTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "or_expr"
    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:50:1: or_expr : and_expr ( ( BAR | OR ) ^ and_expr )* ;
    public final FormulaParser.or_expr_return or_expr() throws RecognitionException {
        FormulaParser.or_expr_return retval = new FormulaParser.or_expr_return();
        retval.start = input.LT(1);


        FormulaTree root_0 = null;

        Token set4=null;
        FormulaParser.and_expr_return and_expr3 =null;

        FormulaParser.and_expr_return and_expr5 =null;


        FormulaTree set4_tree=null;

        try {
            // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:51:3: ( and_expr ( ( BAR | OR ) ^ and_expr )* )
            // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:51:5: and_expr ( ( BAR | OR ) ^ and_expr )*
            {
            root_0 = (FormulaTree)adaptor.nil();


            pushFollow(FOLLOW_and_expr_in_or_expr138);
            and_expr3=and_expr();

            state._fsp--;

            adaptor.addChild(root_0, and_expr3.getTree());

            // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:51:14: ( ( BAR | OR ) ^ and_expr )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==BAR||LA1_0==OR) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:51:15: ( BAR | OR ) ^ and_expr
            	    {
            	    set4=(Token)input.LT(1);

            	    set4=(Token)input.LT(1);

            	    if ( input.LA(1)==BAR||input.LA(1)==OR ) {
            	        input.consume();
            	        root_0 = (FormulaTree)adaptor.becomeRoot(
            	        (FormulaTree)adaptor.create(set4)
            	        , root_0);
            	        state.errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    pushFollow(FOLLOW_and_expr_in_or_expr148);
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


            retval.tree = (FormulaTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (FormulaTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "or_expr"


    public static class and_expr_return extends ParserRuleReturnScope {
        FormulaTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "and_expr"
    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:55:1: and_expr : impl_expr ( ( AMP | AND ) ^ impl_expr )* ;
    public final FormulaParser.and_expr_return and_expr() throws RecognitionException {
        FormulaParser.and_expr_return retval = new FormulaParser.and_expr_return();
        retval.start = input.LT(1);


        FormulaTree root_0 = null;

        Token set7=null;
        FormulaParser.impl_expr_return impl_expr6 =null;

        FormulaParser.impl_expr_return impl_expr8 =null;


        FormulaTree set7_tree=null;

        try {
            // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:56:3: ( impl_expr ( ( AMP | AND ) ^ impl_expr )* )
            // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:56:5: impl_expr ( ( AMP | AND ) ^ impl_expr )*
            {
            root_0 = (FormulaTree)adaptor.nil();


            pushFollow(FOLLOW_impl_expr_in_and_expr165);
            impl_expr6=impl_expr();

            state._fsp--;

            adaptor.addChild(root_0, impl_expr6.getTree());

            // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:56:15: ( ( AMP | AND ) ^ impl_expr )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0 >= AMP && LA2_0 <= AND)) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:56:16: ( AMP | AND ) ^ impl_expr
            	    {
            	    set7=(Token)input.LT(1);

            	    set7=(Token)input.LT(1);

            	    if ( (input.LA(1) >= AMP && input.LA(1) <= AND) ) {
            	        input.consume();
            	        root_0 = (FormulaTree)adaptor.becomeRoot(
            	        (FormulaTree)adaptor.create(set7)
            	        , root_0);
            	        state.errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    pushFollow(FOLLOW_impl_expr_in_and_expr175);
            	    impl_expr8=impl_expr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, impl_expr8.getTree());

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            retval.tree = (FormulaTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (FormulaTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "and_expr"


    public static class impl_expr_return extends ParserRuleReturnScope {
        FormulaTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "impl_expr"
    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:60:1: impl_expr : not_expr ( ( IMPL | IMPL_BY | EQUIV ) ^ not_expr )* ;
    public final FormulaParser.impl_expr_return impl_expr() throws RecognitionException {
        FormulaParser.impl_expr_return retval = new FormulaParser.impl_expr_return();
        retval.start = input.LT(1);


        FormulaTree root_0 = null;

        Token set10=null;
        FormulaParser.not_expr_return not_expr9 =null;

        FormulaParser.not_expr_return not_expr11 =null;


        FormulaTree set10_tree=null;

        try {
            // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:61:3: ( not_expr ( ( IMPL | IMPL_BY | EQUIV ) ^ not_expr )* )
            // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:61:5: not_expr ( ( IMPL | IMPL_BY | EQUIV ) ^ not_expr )*
            {
            root_0 = (FormulaTree)adaptor.nil();


            pushFollow(FOLLOW_not_expr_in_impl_expr192);
            not_expr9=not_expr();

            state._fsp--;

            adaptor.addChild(root_0, not_expr9.getTree());

            // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:61:14: ( ( IMPL | IMPL_BY | EQUIV ) ^ not_expr )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==EQUIV||(LA3_0 >= IMPL && LA3_0 <= IMPL_BY)) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:61:15: ( IMPL | IMPL_BY | EQUIV ) ^ not_expr
            	    {
            	    set10=(Token)input.LT(1);

            	    set10=(Token)input.LT(1);

            	    if ( input.LA(1)==EQUIV||(input.LA(1) >= IMPL && input.LA(1) <= IMPL_BY) ) {
            	        input.consume();
            	        root_0 = (FormulaTree)adaptor.becomeRoot(
            	        (FormulaTree)adaptor.create(set10)
            	        , root_0);
            	        state.errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    pushFollow(FOLLOW_not_expr_in_impl_expr208);
            	    not_expr11=not_expr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, not_expr11.getTree());

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            retval.tree = (FormulaTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (FormulaTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "impl_expr"


    public static class not_expr_return extends ParserRuleReturnScope {
        FormulaTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "not_expr"
    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:65:1: not_expr : ( NOT ^ not_expr | atom_expr );
    public final FormulaParser.not_expr_return not_expr() throws RecognitionException {
        FormulaParser.not_expr_return retval = new FormulaParser.not_expr_return();
        retval.start = input.LT(1);


        FormulaTree root_0 = null;

        Token NOT12=null;
        FormulaParser.not_expr_return not_expr13 =null;

        FormulaParser.atom_expr_return atom_expr14 =null;


        FormulaTree NOT12_tree=null;

        try {
            // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:66:3: ( NOT ^ not_expr | atom_expr )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==NOT) ) {
                alt4=1;
            }
            else if ( (LA4_0==FALSE||LA4_0==ID||LA4_0==LPAR||LA4_0==TRUE) ) {
                alt4=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;

            }
            switch (alt4) {
                case 1 :
                    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:66:5: NOT ^ not_expr
                    {
                    root_0 = (FormulaTree)adaptor.nil();


                    NOT12=(Token)match(input,NOT,FOLLOW_NOT_in_not_expr225); 
                    NOT12_tree = 
                    (FormulaTree)adaptor.create(NOT12)
                    ;
                    root_0 = (FormulaTree)adaptor.becomeRoot(NOT12_tree, root_0);


                    pushFollow(FOLLOW_not_expr_in_not_expr228);
                    not_expr13=not_expr();

                    state._fsp--;

                    adaptor.addChild(root_0, not_expr13.getTree());

                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:67:5: atom_expr
                    {
                    root_0 = (FormulaTree)adaptor.nil();


                    pushFollow(FOLLOW_atom_expr_in_not_expr234);
                    atom_expr14=atom_expr();

                    state._fsp--;

                    adaptor.addChild(root_0, atom_expr14.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (FormulaTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (FormulaTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "not_expr"


    public static class atom_expr_return extends ParserRuleReturnScope {
        FormulaTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "atom_expr"
    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:70:1: atom_expr : ( TRUE | FALSE | call | par_expr );
    public final FormulaParser.atom_expr_return atom_expr() throws RecognitionException {
        FormulaParser.atom_expr_return retval = new FormulaParser.atom_expr_return();
        retval.start = input.LT(1);


        FormulaTree root_0 = null;

        Token TRUE15=null;
        Token FALSE16=null;
        FormulaParser.call_return call17 =null;

        FormulaParser.par_expr_return par_expr18 =null;


        FormulaTree TRUE15_tree=null;
        FormulaTree FALSE16_tree=null;

        try {
            // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:71:3: ( TRUE | FALSE | call | par_expr )
            int alt5=4;
            switch ( input.LA(1) ) {
            case TRUE:
                {
                alt5=1;
                }
                break;
            case FALSE:
                {
                alt5=2;
                }
                break;
            case ID:
                {
                alt5=3;
                }
                break;
            case LPAR:
                {
                alt5=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;

            }

            switch (alt5) {
                case 1 :
                    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:71:5: TRUE
                    {
                    root_0 = (FormulaTree)adaptor.nil();


                    TRUE15=(Token)match(input,TRUE,FOLLOW_TRUE_in_atom_expr246); 
                    TRUE15_tree = 
                    (FormulaTree)adaptor.create(TRUE15)
                    ;
                    adaptor.addChild(root_0, TRUE15_tree);


                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:72:5: FALSE
                    {
                    root_0 = (FormulaTree)adaptor.nil();


                    FALSE16=(Token)match(input,FALSE,FOLLOW_FALSE_in_atom_expr252); 
                    FALSE16_tree = 
                    (FormulaTree)adaptor.create(FALSE16)
                    ;
                    adaptor.addChild(root_0, FALSE16_tree);


                    }
                    break;
                case 3 :
                    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:73:5: call
                    {
                    root_0 = (FormulaTree)adaptor.nil();


                    pushFollow(FOLLOW_call_in_atom_expr258);
                    call17=call();

                    state._fsp--;

                    adaptor.addChild(root_0, call17.getTree());

                    }
                    break;
                case 4 :
                    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:74:5: par_expr
                    {
                    root_0 = (FormulaTree)adaptor.nil();


                    pushFollow(FOLLOW_par_expr_in_atom_expr264);
                    par_expr18=par_expr();

                    state._fsp--;

                    adaptor.addChild(root_0, par_expr18.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (FormulaTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (FormulaTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "atom_expr"


    public static class par_expr_return extends ParserRuleReturnScope {
        FormulaTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "par_expr"
    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:78:1: par_expr : LPAR ^ or_expr RPAR ;
    public final FormulaParser.par_expr_return par_expr() throws RecognitionException {
        FormulaParser.par_expr_return retval = new FormulaParser.par_expr_return();
        retval.start = input.LT(1);


        FormulaTree root_0 = null;

        Token LPAR19=null;
        Token RPAR21=null;
        FormulaParser.or_expr_return or_expr20 =null;


        FormulaTree LPAR19_tree=null;
        FormulaTree RPAR21_tree=null;

        try {
            // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:79:3: ( LPAR ^ or_expr RPAR )
            // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:79:5: LPAR ^ or_expr RPAR
            {
            root_0 = (FormulaTree)adaptor.nil();


            LPAR19=(Token)match(input,LPAR,FOLLOW_LPAR_in_par_expr279); 
            LPAR19_tree = 
            (FormulaTree)adaptor.create(LPAR19)
            ;
            root_0 = (FormulaTree)adaptor.becomeRoot(LPAR19_tree, root_0);


            pushFollow(FOLLOW_or_expr_in_par_expr282);
            or_expr20=or_expr();

            state._fsp--;

            adaptor.addChild(root_0, or_expr20.getTree());

            RPAR21=(Token)match(input,RPAR,FOLLOW_RPAR_in_par_expr284); 
            RPAR21_tree = 
            (FormulaTree)adaptor.create(RPAR21)
            ;
            adaptor.addChild(root_0, RPAR21_tree);


            }

            retval.stop = input.LT(-1);


            retval.tree = (FormulaTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (FormulaTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "par_expr"


    public static class call_return extends ParserRuleReturnScope {
        FormulaTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "call"
    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:82:1: call : qual_name LPAR ( arg ( COMMA arg )* )? close= RPAR -> ^( CALL qual_name ( arg )* RPAR[$close,\"\"] ) ;
    public final FormulaParser.call_return call() throws RecognitionException {
        FormulaParser.call_return retval = new FormulaParser.call_return();
        retval.start = input.LT(1);


        FormulaTree root_0 = null;

        Token close=null;
        Token LPAR23=null;
        Token COMMA25=null;
        FormulaParser.qual_name_return qual_name22 =null;

        FormulaParser.arg_return arg24 =null;

        FormulaParser.arg_return arg26 =null;


        FormulaTree close_tree=null;
        FormulaTree LPAR23_tree=null;
        FormulaTree COMMA25_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleSubtreeStream stream_arg=new RewriteRuleSubtreeStream(adaptor,"rule arg");
        RewriteRuleSubtreeStream stream_qual_name=new RewriteRuleSubtreeStream(adaptor,"rule qual_name");
        try {
            // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:83:3: ( qual_name LPAR ( arg ( COMMA arg )* )? close= RPAR -> ^( CALL qual_name ( arg )* RPAR[$close,\"\"] ) )
            // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:83:5: qual_name LPAR ( arg ( COMMA arg )* )? close= RPAR
            {
            pushFollow(FOLLOW_qual_name_in_call297);
            qual_name22=qual_name();

            state._fsp--;

            stream_qual_name.add(qual_name22.getTree());

            LPAR23=(Token)match(input,LPAR,FOLLOW_LPAR_in_call299);  
            stream_LPAR.add(LPAR23);


            // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:83:20: ( arg ( COMMA arg )* )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==DONT_CARE||LA7_0==FALSE||LA7_0==ID||(LA7_0 >= MINUS && LA7_0 <= NAT_LIT)||LA7_0==REAL_LIT||(LA7_0 >= STRING_LIT && LA7_0 <= TRUE)) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:83:21: arg ( COMMA arg )*
                    {
                    pushFollow(FOLLOW_arg_in_call302);
                    arg24=arg();

                    state._fsp--;

                    stream_arg.add(arg24.getTree());

                    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:83:25: ( COMMA arg )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==COMMA) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:83:26: COMMA arg
                    	    {
                    	    COMMA25=(Token)match(input,COMMA,FOLLOW_COMMA_in_call305);  
                    	    stream_COMMA.add(COMMA25);


                    	    pushFollow(FOLLOW_arg_in_call307);
                    	    arg26=arg();

                    	    state._fsp--;

                    	    stream_arg.add(arg26.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);


                    }
                    break;

            }


            close=(Token)match(input,RPAR,FOLLOW_RPAR_in_call315);  
            stream_RPAR.add(close);


            // AST REWRITE
            // elements: qual_name, arg, RPAR
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (FormulaTree)adaptor.nil();
            // 84:4: -> ^( CALL qual_name ( arg )* RPAR[$close,\"\"] )
            {
                // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:84:7: ^( CALL qual_name ( arg )* RPAR[$close,\"\"] )
                {
                FormulaTree root_1 = (FormulaTree)adaptor.nil();
                root_1 = (FormulaTree)adaptor.becomeRoot(
                (FormulaTree)adaptor.create(CALL, "CALL")
                , root_1);

                adaptor.addChild(root_1, stream_qual_name.nextTree());

                // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:84:24: ( arg )*
                while ( stream_arg.hasNext() ) {
                    adaptor.addChild(root_1, stream_arg.nextTree());

                }
                stream_arg.reset();

                adaptor.addChild(root_1, 
                (FormulaTree)adaptor.create(RPAR, close, "")
                );

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (FormulaTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (FormulaTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "call"


    public static class qual_name_return extends ParserRuleReturnScope {
        FormulaTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "qual_name"
    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:87:1: qual_name : ID ( DOT ^ ID )* ;
    public final FormulaParser.qual_name_return qual_name() throws RecognitionException {
        FormulaParser.qual_name_return retval = new FormulaParser.qual_name_return();
        retval.start = input.LT(1);


        FormulaTree root_0 = null;

        Token ID27=null;
        Token DOT28=null;
        Token ID29=null;

        FormulaTree ID27_tree=null;
        FormulaTree DOT28_tree=null;
        FormulaTree ID29_tree=null;

        try {
            // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:88:3: ( ID ( DOT ^ ID )* )
            // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:88:5: ID ( DOT ^ ID )*
            {
            root_0 = (FormulaTree)adaptor.nil();


            ID27=(Token)match(input,ID,FOLLOW_ID_in_qual_name345); 
            ID27_tree = 
            (FormulaTree)adaptor.create(ID27)
            ;
            adaptor.addChild(root_0, ID27_tree);


            // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:88:8: ( DOT ^ ID )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==DOT) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:88:9: DOT ^ ID
            	    {
            	    DOT28=(Token)match(input,DOT,FOLLOW_DOT_in_qual_name348); 
            	    DOT28_tree = 
            	    (FormulaTree)adaptor.create(DOT28)
            	    ;
            	    root_0 = (FormulaTree)adaptor.becomeRoot(DOT28_tree, root_0);


            	    ID29=(Token)match(input,ID,FOLLOW_ID_in_qual_name351); 
            	    ID29_tree = 
            	    (FormulaTree)adaptor.create(ID29)
            	    ;
            	    adaptor.addChild(root_0, ID29_tree);


            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            retval.tree = (FormulaTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (FormulaTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "qual_name"


    public static class arg_return extends ParserRuleReturnScope {
        FormulaTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arg"
    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:92:1: arg : ( DONT_CARE | STRING_LIT | ( MINUS )? NAT_LIT ^| ( MINUS )? REAL_LIT ^| TRUE | FALSE | ID );
    public final FormulaParser.arg_return arg() throws RecognitionException {
        FormulaParser.arg_return retval = new FormulaParser.arg_return();
        retval.start = input.LT(1);


        FormulaTree root_0 = null;

        Token DONT_CARE30=null;
        Token STRING_LIT31=null;
        Token MINUS32=null;
        Token NAT_LIT33=null;
        Token MINUS34=null;
        Token REAL_LIT35=null;
        Token TRUE36=null;
        Token FALSE37=null;
        Token ID38=null;

        FormulaTree DONT_CARE30_tree=null;
        FormulaTree STRING_LIT31_tree=null;
        FormulaTree MINUS32_tree=null;
        FormulaTree NAT_LIT33_tree=null;
        FormulaTree MINUS34_tree=null;
        FormulaTree REAL_LIT35_tree=null;
        FormulaTree TRUE36_tree=null;
        FormulaTree FALSE37_tree=null;
        FormulaTree ID38_tree=null;

        try {
            // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:93:3: ( DONT_CARE | STRING_LIT | ( MINUS )? NAT_LIT ^| ( MINUS )? REAL_LIT ^| TRUE | FALSE | ID )
            int alt11=7;
            switch ( input.LA(1) ) {
            case DONT_CARE:
                {
                alt11=1;
                }
                break;
            case STRING_LIT:
                {
                alt11=2;
                }
                break;
            case MINUS:
                {
                int LA11_3 = input.LA(2);

                if ( (LA11_3==NAT_LIT) ) {
                    alt11=3;
                }
                else if ( (LA11_3==REAL_LIT) ) {
                    alt11=4;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 11, 3, input);

                    throw nvae;

                }
                }
                break;
            case NAT_LIT:
                {
                alt11=3;
                }
                break;
            case REAL_LIT:
                {
                alt11=4;
                }
                break;
            case TRUE:
                {
                alt11=5;
                }
                break;
            case FALSE:
                {
                alt11=6;
                }
                break;
            case ID:
                {
                alt11=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;

            }

            switch (alt11) {
                case 1 :
                    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:93:5: DONT_CARE
                    {
                    root_0 = (FormulaTree)adaptor.nil();


                    DONT_CARE30=(Token)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg368); 
                    DONT_CARE30_tree = 
                    (FormulaTree)adaptor.create(DONT_CARE30)
                    ;
                    adaptor.addChild(root_0, DONT_CARE30_tree);


                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:94:5: STRING_LIT
                    {
                    root_0 = (FormulaTree)adaptor.nil();


                    STRING_LIT31=(Token)match(input,STRING_LIT,FOLLOW_STRING_LIT_in_arg374); 
                    STRING_LIT31_tree = 
                    (FormulaTree)adaptor.create(STRING_LIT31)
                    ;
                    adaptor.addChild(root_0, STRING_LIT31_tree);


                    }
                    break;
                case 3 :
                    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:95:5: ( MINUS )? NAT_LIT ^
                    {
                    root_0 = (FormulaTree)adaptor.nil();


                    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:95:5: ( MINUS )?
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==MINUS) ) {
                        alt9=1;
                    }
                    switch (alt9) {
                        case 1 :
                            // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:95:5: MINUS
                            {
                            MINUS32=(Token)match(input,MINUS,FOLLOW_MINUS_in_arg380); 
                            MINUS32_tree = 
                            (FormulaTree)adaptor.create(MINUS32)
                            ;
                            adaptor.addChild(root_0, MINUS32_tree);


                            }
                            break;

                    }


                    NAT_LIT33=(Token)match(input,NAT_LIT,FOLLOW_NAT_LIT_in_arg383); 
                    NAT_LIT33_tree = 
                    (FormulaTree)adaptor.create(NAT_LIT33)
                    ;
                    root_0 = (FormulaTree)adaptor.becomeRoot(NAT_LIT33_tree, root_0);


                    }
                    break;
                case 4 :
                    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:96:5: ( MINUS )? REAL_LIT ^
                    {
                    root_0 = (FormulaTree)adaptor.nil();


                    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:96:5: ( MINUS )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0==MINUS) ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:96:5: MINUS
                            {
                            MINUS34=(Token)match(input,MINUS,FOLLOW_MINUS_in_arg390); 
                            MINUS34_tree = 
                            (FormulaTree)adaptor.create(MINUS34)
                            ;
                            adaptor.addChild(root_0, MINUS34_tree);


                            }
                            break;

                    }


                    REAL_LIT35=(Token)match(input,REAL_LIT,FOLLOW_REAL_LIT_in_arg393); 
                    REAL_LIT35_tree = 
                    (FormulaTree)adaptor.create(REAL_LIT35)
                    ;
                    root_0 = (FormulaTree)adaptor.becomeRoot(REAL_LIT35_tree, root_0);


                    }
                    break;
                case 5 :
                    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:97:5: TRUE
                    {
                    root_0 = (FormulaTree)adaptor.nil();


                    TRUE36=(Token)match(input,TRUE,FOLLOW_TRUE_in_arg400); 
                    TRUE36_tree = 
                    (FormulaTree)adaptor.create(TRUE36)
                    ;
                    adaptor.addChild(root_0, TRUE36_tree);


                    }
                    break;
                case 6 :
                    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:98:5: FALSE
                    {
                    root_0 = (FormulaTree)adaptor.nil();


                    FALSE37=(Token)match(input,FALSE,FOLLOW_FALSE_in_arg406); 
                    FALSE37_tree = 
                    (FormulaTree)adaptor.create(FALSE37)
                    ;
                    adaptor.addChild(root_0, FALSE37_tree);


                    }
                    break;
                case 7 :
                    // D:\\Eclipse\\groove-java8\\src\\groove\\explore\\syntax\\Formula.g:99:5: ID
                    {
                    root_0 = (FormulaTree)adaptor.nil();


                    ID38=(Token)match(input,ID,FOLLOW_ID_in_arg412); 
                    ID38_tree = 
                    (FormulaTree)adaptor.create(ID38)
                    ;
                    adaptor.addChild(root_0, ID38_tree);


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (FormulaTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (FormulaTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "arg"

    // Delegated rules


 

    public static final BitSet FOLLOW_or_expr_in_formula120 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_formula122 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_expr_in_or_expr138 = new BitSet(new long[]{0x0000000020000042L});
    public static final BitSet FOLLOW_set_in_or_expr141 = new BitSet(new long[]{0x0000002002450000L});
    public static final BitSet FOLLOW_and_expr_in_or_expr148 = new BitSet(new long[]{0x0000000020000042L});
    public static final BitSet FOLLOW_impl_expr_in_and_expr165 = new BitSet(new long[]{0x0000000000000032L});
    public static final BitSet FOLLOW_set_in_and_expr168 = new BitSet(new long[]{0x0000002002450000L});
    public static final BitSet FOLLOW_impl_expr_in_and_expr175 = new BitSet(new long[]{0x0000000000000032L});
    public static final BitSet FOLLOW_not_expr_in_impl_expr192 = new BitSet(new long[]{0x0000000000184002L});
    public static final BitSet FOLLOW_set_in_impl_expr195 = new BitSet(new long[]{0x0000002002450000L});
    public static final BitSet FOLLOW_not_expr_in_impl_expr208 = new BitSet(new long[]{0x0000000000184002L});
    public static final BitSet FOLLOW_NOT_in_not_expr225 = new BitSet(new long[]{0x0000002002450000L});
    public static final BitSet FOLLOW_not_expr_in_not_expr228 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_expr_in_not_expr234 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_atom_expr246 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_atom_expr252 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_atom_expr258 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_par_expr_in_atom_expr264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_par_expr279 = new BitSet(new long[]{0x0000002002450000L});
    public static final BitSet FOLLOW_or_expr_in_par_expr282 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_RPAR_in_par_expr284 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qual_name_in_call297 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_LPAR_in_call299 = new BitSet(new long[]{0x0000003601851000L});
    public static final BitSet FOLLOW_arg_in_call302 = new BitSet(new long[]{0x0000000400000400L});
    public static final BitSet FOLLOW_COMMA_in_call305 = new BitSet(new long[]{0x0000003201851000L});
    public static final BitSet FOLLOW_arg_in_call307 = new BitSet(new long[]{0x0000000400000400L});
    public static final BitSet FOLLOW_RPAR_in_call315 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_qual_name345 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_DOT_in_qual_name348 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_ID_in_qual_name351 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg368 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LIT_in_arg374 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_arg380 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_NAT_LIT_in_arg383 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_arg390 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_REAL_LIT_in_arg393 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_arg400 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_arg406 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_arg412 = new BitSet(new long[]{0x0000000000000002L});

}
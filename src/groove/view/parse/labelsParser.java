// $ANTLR 3.1b1 labels.g 2010-04-23 09:21:51

package groove.view.parse;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;

@SuppressWarnings("all")              
public class labelsParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "NEW", "DEL", "NOT", "USE", "CNEW", "REM", "FORALL", "FORALLX", "EXISTS", "NESTED", "INT", "REAL", "STRING", "BOOL", "ATTR", "PROD", "ARG", "PAR", "TYPE", "FLAG", "PATH", "EMPTY", "EQUALS", "IDENT", "COLON", "NUMBER", "RNUMBER", "DQTEXT", "PLING", "DIGIT", "LBRACE", "RBRACE", "SQTEXT", "QUERY", "BAR", "DOT", "MINUS", "STAR", "PLUS", "IDENTCHAR", "LPAR", "RPAR", "LSQUARE", "HAT", "COMMA", "RSQUARE", "SQUOTE", "BSLASH", "DQUOTE", "DOLLAR", "UNDER", "LETTER", "LABEL", "'new'", "'del'", "'cnew'", "'not'", "'use'", "'rem'", "'forall'", "'forallx'", "'exists'", "'nested'", "'par'", "'attr'", "'prod'", "'arg'", "'int'", "'real'", "'string'", "'bool'", "'type'", "'flag'", "'path'", "'true'", "'false'"
    };
    public static final int DOLLAR=53;
    public static final int T__68=68;
    public static final int T__69=69;
    public static final int T__66=66;
    public static final int T__67=67;
    public static final int STAR=41;
    public static final int T__64=64;
    public static final int LSQUARE=46;
    public static final int T__65=65;
    public static final int FORALLX=11;
    public static final int T__62=62;
    public static final int T__63=63;
    public static final int LETTER=55;
    public static final int DQTEXT=31;
    public static final int DEL=5;
    public static final int LBRACE=34;
    public static final int NEW=4;
    public static final int DQUOTE=52;
    public static final int IDENTCHAR=43;
    public static final int RNUMBER=30;
    public static final int EQUALS=26;
    public static final int NOT=6;
    public static final int T__61=61;
    public static final int EOF=-1;
    public static final int T__60=60;
    public static final int TYPE=22;
    public static final int HAT=47;
    public static final int T__57=57;
    public static final int UNDER=54;
    public static final int T__58=58;
    public static final int PLING=32;
    public static final int LPAR=44;
    public static final int ARG=20;
    public static final int COMMA=48;
    public static final int PATH=24;
    public static final int T__59=59;
    public static final int PROD=19;
    public static final int IDENT=27;
    public static final int PAR=21;
    public static final int PLUS=42;
    public static final int DIGIT=33;
    public static final int EXISTS=12;
    public static final int DOT=39;
    public static final int ATTR=18;
    public static final int RBRACE=35;
    public static final int NUMBER=29;
    public static final int BOOL=17;
    public static final int FORALL=10;
    public static final int INT=14;
    public static final int SQUOTE=50;
    public static final int RSQUARE=49;
    public static final int MINUS=40;
    public static final int REM=9;
    public static final int CNEW=8;
    public static final int FLAG=23;
    public static final int EMPTY=25;
    public static final int COLON=28;
    public static final int NESTED=13;
    public static final int REAL=15;
    public static final int T__71=71;
    public static final int T__72=72;
    public static final int SQTEXT=36;
    public static final int T__70=70;
    public static final int LABEL=56;
    public static final int QUERY=37;
    public static final int RPAR=45;
    public static final int USE=7;
    public static final int T__76=76;
    public static final int T__75=75;
    public static final int T__74=74;
    public static final int BSLASH=51;
    public static final int T__73=73;
    public static final int BAR=38;
    public static final int T__79=79;
    public static final int T__78=78;
    public static final int STRING=16;
    public static final int T__77=77;

    // delegates
    // delegators


        public labelsParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public labelsParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return labelsParser.tokenNames; }
    public String getGrammarFileName() { return "labels.g"; }


    public static class graphLabel_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start graphLabel
    // labels.g:45:1: graphLabel : ( prefix )* actualGraphLabel ;
    public final labelsParser.graphLabel_return graphLabel() throws RecognitionException {
        labelsParser.graphLabel_return retval = new labelsParser.graphLabel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        labelsParser.prefix_return prefix1 = null;

        labelsParser.actualGraphLabel_return actualGraphLabel2 = null;



        try {
            // labels.g:45:12: ( ( prefix )* actualGraphLabel )
            // labels.g:46:3: ( prefix )* actualGraphLabel
            {
            root_0 = (Object)adaptor.nil();

            // labels.g:46:3: ( prefix )*
            loop1:
            do {
                int alt1=2;
                alt1 = dfa1.predict(input);
                switch (alt1) {
            	case 1 :
            	    // labels.g:46:3: prefix
            	    {
            	    pushFollow(FOLLOW_prefix_in_graphLabel177);
            	    prefix1=prefix();

            	    state._fsp--;

            	    adaptor.addChild(root_0, prefix1.getTree());

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            pushFollow(FOLLOW_actualGraphLabel_in_graphLabel180);
            actualGraphLabel2=actualGraphLabel();

            state._fsp--;

            adaptor.addChild(root_0, actualGraphLabel2.getTree());

            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end graphLabel

    public static class prefix_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start prefix
    // labels.g:48:1: prefix : ( ( forallP | forallxP | existsP ) ( EQUALS IDENT )? COLON | ( newP | delP | notP | useP | cnewP ) ( EQUALS IDENT )? COLON | nestedP COLON );
    public final labelsParser.prefix_return prefix() throws RecognitionException {
        labelsParser.prefix_return retval = new labelsParser.prefix_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQUALS6=null;
        Token IDENT7=null;
        Token COLON8=null;
        Token EQUALS14=null;
        Token IDENT15=null;
        Token COLON16=null;
        Token COLON18=null;
        labelsParser.forallP_return forallP3 = null;

        labelsParser.forallxP_return forallxP4 = null;

        labelsParser.existsP_return existsP5 = null;

        labelsParser.newP_return newP9 = null;

        labelsParser.delP_return delP10 = null;

        labelsParser.notP_return notP11 = null;

        labelsParser.useP_return useP12 = null;

        labelsParser.cnewP_return cnewP13 = null;

        labelsParser.nestedP_return nestedP17 = null;


        Object EQUALS6_tree=null;
        Object IDENT7_tree=null;
        Object COLON8_tree=null;
        Object EQUALS14_tree=null;
        Object IDENT15_tree=null;
        Object COLON16_tree=null;
        Object COLON18_tree=null;

        try {
            // labels.g:49:4: ( ( forallP | forallxP | existsP ) ( EQUALS IDENT )? COLON | ( newP | delP | notP | useP | cnewP ) ( EQUALS IDENT )? COLON | nestedP COLON )
            int alt6=3;
            switch ( input.LA(1) ) {
            case 63:
            case 64:
            case 65:
                {
                alt6=1;
                }
                break;
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
                {
                alt6=2;
                }
                break;
            case 66:
                {
                alt6=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // labels.g:49:6: ( forallP | forallxP | existsP ) ( EQUALS IDENT )? COLON
                    {
                    root_0 = (Object)adaptor.nil();

                    // labels.g:49:6: ( forallP | forallxP | existsP )
                    int alt2=3;
                    switch ( input.LA(1) ) {
                    case 63:
                        {
                        alt2=1;
                        }
                        break;
                    case 64:
                        {
                        alt2=2;
                        }
                        break;
                    case 65:
                        {
                        alt2=3;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 2, 0, input);

                        throw nvae;
                    }

                    switch (alt2) {
                        case 1 :
                            // labels.g:49:8: forallP
                            {
                            pushFollow(FOLLOW_forallP_in_prefix194);
                            forallP3=forallP();

                            state._fsp--;

                            adaptor.addChild(root_0, forallP3.getTree());

                            }
                            break;
                        case 2 :
                            // labels.g:49:18: forallxP
                            {
                            pushFollow(FOLLOW_forallxP_in_prefix198);
                            forallxP4=forallxP();

                            state._fsp--;

                            adaptor.addChild(root_0, forallxP4.getTree());

                            }
                            break;
                        case 3 :
                            // labels.g:49:29: existsP
                            {
                            pushFollow(FOLLOW_existsP_in_prefix202);
                            existsP5=existsP();

                            state._fsp--;

                            adaptor.addChild(root_0, existsP5.getTree());

                            }
                            break;

                    }

                    // labels.g:49:39: ( EQUALS IDENT )?
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==EQUALS) ) {
                        alt3=1;
                    }
                    switch (alt3) {
                        case 1 :
                            // labels.g:49:40: EQUALS IDENT
                            {
                            EQUALS6=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_prefix207); 
                            EQUALS6_tree = (Object)adaptor.create(EQUALS6);
                            adaptor.addChild(root_0, EQUALS6_tree);

                            IDENT7=(Token)match(input,IDENT,FOLLOW_IDENT_in_prefix209); 
                            IDENT7_tree = (Object)adaptor.create(IDENT7);
                            adaptor.addChild(root_0, IDENT7_tree);


                            }
                            break;

                    }

                    COLON8=(Token)match(input,COLON,FOLLOW_COLON_in_prefix213); 
                    COLON8_tree = (Object)adaptor.create(COLON8);
                    adaptor.addChild(root_0, COLON8_tree);


                    }
                    break;
                case 2 :
                    // labels.g:50:6: ( newP | delP | notP | useP | cnewP ) ( EQUALS IDENT )? COLON
                    {
                    root_0 = (Object)adaptor.nil();

                    // labels.g:50:6: ( newP | delP | notP | useP | cnewP )
                    int alt4=5;
                    switch ( input.LA(1) ) {
                    case 57:
                        {
                        alt4=1;
                        }
                        break;
                    case 58:
                        {
                        alt4=2;
                        }
                        break;
                    case 60:
                        {
                        alt4=3;
                        }
                        break;
                    case 61:
                        {
                        alt4=4;
                        }
                        break;
                    case 59:
                        {
                        alt4=5;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 4, 0, input);

                        throw nvae;
                    }

                    switch (alt4) {
                        case 1 :
                            // labels.g:50:8: newP
                            {
                            pushFollow(FOLLOW_newP_in_prefix223);
                            newP9=newP();

                            state._fsp--;

                            adaptor.addChild(root_0, newP9.getTree());

                            }
                            break;
                        case 2 :
                            // labels.g:50:15: delP
                            {
                            pushFollow(FOLLOW_delP_in_prefix227);
                            delP10=delP();

                            state._fsp--;

                            adaptor.addChild(root_0, delP10.getTree());

                            }
                            break;
                        case 3 :
                            // labels.g:50:22: notP
                            {
                            pushFollow(FOLLOW_notP_in_prefix231);
                            notP11=notP();

                            state._fsp--;

                            adaptor.addChild(root_0, notP11.getTree());

                            }
                            break;
                        case 4 :
                            // labels.g:50:29: useP
                            {
                            pushFollow(FOLLOW_useP_in_prefix235);
                            useP12=useP();

                            state._fsp--;

                            adaptor.addChild(root_0, useP12.getTree());

                            }
                            break;
                        case 5 :
                            // labels.g:50:36: cnewP
                            {
                            pushFollow(FOLLOW_cnewP_in_prefix239);
                            cnewP13=cnewP();

                            state._fsp--;

                            adaptor.addChild(root_0, cnewP13.getTree());

                            }
                            break;

                    }

                    // labels.g:50:44: ( EQUALS IDENT )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==EQUALS) ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // labels.g:50:45: EQUALS IDENT
                            {
                            EQUALS14=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_prefix244); 
                            EQUALS14_tree = (Object)adaptor.create(EQUALS14);
                            adaptor.addChild(root_0, EQUALS14_tree);

                            IDENT15=(Token)match(input,IDENT,FOLLOW_IDENT_in_prefix246); 
                            IDENT15_tree = (Object)adaptor.create(IDENT15);
                            adaptor.addChild(root_0, IDENT15_tree);


                            }
                            break;

                    }

                    COLON16=(Token)match(input,COLON,FOLLOW_COLON_in_prefix250); 
                    COLON16_tree = (Object)adaptor.create(COLON16);
                    adaptor.addChild(root_0, COLON16_tree);


                    }
                    break;
                case 3 :
                    // labels.g:51:6: nestedP COLON
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_nestedP_in_prefix258);
                    nestedP17=nestedP();

                    state._fsp--;

                    adaptor.addChild(root_0, nestedP17.getTree());
                    COLON18=(Token)match(input,COLON,FOLLOW_COLON_in_prefix260); 
                    COLON18_tree = (Object)adaptor.create(COLON18);
                    adaptor.addChild(root_0, COLON18_tree);


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end prefix

    public static class actualGraphLabel_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start actualGraphLabel
    // labels.g:53:1: actualGraphLabel : ( COLON ( . )* | remP COLON ( . )* | valueLabel | nodeLabel | (~ COLON )+ );
    public final labelsParser.actualGraphLabel_return actualGraphLabel() throws RecognitionException {
        labelsParser.actualGraphLabel_return retval = new labelsParser.actualGraphLabel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLON19=null;
        Token wildcard20=null;
        Token COLON22=null;
        Token wildcard23=null;
        Token set26=null;
        labelsParser.remP_return remP21 = null;

        labelsParser.valueLabel_return valueLabel24 = null;

        labelsParser.nodeLabel_return nodeLabel25 = null;


        Object COLON19_tree=null;
        Object wildcard20_tree=null;
        Object COLON22_tree=null;
        Object wildcard23_tree=null;
        Object set26_tree=null;

        try {
            // labels.g:54:4: ( COLON ( . )* | remP COLON ( . )* | valueLabel | nodeLabel | (~ COLON )+ )
            int alt10=5;
            alt10 = dfa10.predict(input);
            switch (alt10) {
                case 1 :
                    // labels.g:54:6: COLON ( . )*
                    {
                    root_0 = (Object)adaptor.nil();

                    COLON19=(Token)match(input,COLON,FOLLOW_COLON_in_actualGraphLabel271); 
                    COLON19_tree = (Object)adaptor.create(COLON19);
                    adaptor.addChild(root_0, COLON19_tree);

                    // labels.g:54:12: ( . )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( ((LA7_0>=NEW && LA7_0<=79)) ) {
                            alt7=1;
                        }
                        else if ( (LA7_0==EOF) ) {
                            alt7=2;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // labels.g:54:12: .
                    	    {
                    	    wildcard20=(Token)input.LT(1);
                    	    matchAny(input); 
                    	    wildcard20_tree = (Object)adaptor.create(wildcard20);
                    	    adaptor.addChild(root_0, wildcard20_tree);


                    	    }
                    	    break;

                    	default :
                    	    break loop7;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // labels.g:55:6: remP COLON ( . )*
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_remP_in_actualGraphLabel281);
                    remP21=remP();

                    state._fsp--;

                    adaptor.addChild(root_0, remP21.getTree());
                    COLON22=(Token)match(input,COLON,FOLLOW_COLON_in_actualGraphLabel283); 
                    COLON22_tree = (Object)adaptor.create(COLON22);
                    adaptor.addChild(root_0, COLON22_tree);

                    // labels.g:55:17: ( . )*
                    loop8:
                    do {
                        int alt8=2;
                        int LA8_0 = input.LA(1);

                        if ( ((LA8_0>=NEW && LA8_0<=79)) ) {
                            alt8=1;
                        }
                        else if ( (LA8_0==EOF) ) {
                            alt8=2;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // labels.g:55:17: .
                    	    {
                    	    wildcard23=(Token)input.LT(1);
                    	    matchAny(input); 
                    	    wildcard23_tree = (Object)adaptor.create(wildcard23);
                    	    adaptor.addChild(root_0, wildcard23_tree);


                    	    }
                    	    break;

                    	default :
                    	    break loop8;
                        }
                    } while (true);


                    }
                    break;
                case 3 :
                    // labels.g:56:6: valueLabel
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_valueLabel_in_actualGraphLabel293);
                    valueLabel24=valueLabel();

                    state._fsp--;

                    adaptor.addChild(root_0, valueLabel24.getTree());

                    }
                    break;
                case 4 :
                    // labels.g:57:6: nodeLabel
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_nodeLabel_in_actualGraphLabel300);
                    nodeLabel25=nodeLabel();

                    state._fsp--;

                    adaptor.addChild(root_0, nodeLabel25.getTree());

                    }
                    break;
                case 5 :
                    // labels.g:58:6: (~ COLON )+
                    {
                    root_0 = (Object)adaptor.nil();

                    // labels.g:58:6: (~ COLON )+
                    int cnt9=0;
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( ((LA9_0>=NEW && LA9_0<=IDENT)||(LA9_0>=NUMBER && LA9_0<=79)) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // labels.g:58:7: ~ COLON
                    	    {
                    	    set26=(Token)input.LT(1);
                    	    if ( (input.LA(1)>=NEW && input.LA(1)<=IDENT)||(input.LA(1)>=NUMBER && input.LA(1)<=79) ) {
                    	        input.consume();
                    	        adaptor.addChild(root_0, (Object)adaptor.create(set26));
                    	        state.errorRecovery=false;
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt9 >= 1 ) break loop9;
                                EarlyExitException eee =
                                    new EarlyExitException(9, input);
                                throw eee;
                        }
                        cnt9++;
                    } while (true);


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end actualGraphLabel

    public static class valueLabel_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start valueLabel
    // labels.g:60:1: valueLabel : ( intP COLON NUMBER -> ^( INT NUMBER ) | realP COLON RNUMBER -> ^( REAL RNUMBER ) | stringP COLON DQTEXT -> ^( STRING DQTEXT ) | boolP COLON ( trueP | falseP ) );
    public final labelsParser.valueLabel_return valueLabel() throws RecognitionException {
        labelsParser.valueLabel_return retval = new labelsParser.valueLabel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLON28=null;
        Token NUMBER29=null;
        Token COLON31=null;
        Token RNUMBER32=null;
        Token COLON34=null;
        Token DQTEXT35=null;
        Token COLON37=null;
        labelsParser.intP_return intP27 = null;

        labelsParser.realP_return realP30 = null;

        labelsParser.stringP_return stringP33 = null;

        labelsParser.boolP_return boolP36 = null;

        labelsParser.trueP_return trueP38 = null;

        labelsParser.falseP_return falseP39 = null;


        Object COLON28_tree=null;
        Object NUMBER29_tree=null;
        Object COLON31_tree=null;
        Object RNUMBER32_tree=null;
        Object COLON34_tree=null;
        Object DQTEXT35_tree=null;
        Object COLON37_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_RNUMBER=new RewriteRuleTokenStream(adaptor,"token RNUMBER");
        RewriteRuleTokenStream stream_DQTEXT=new RewriteRuleTokenStream(adaptor,"token DQTEXT");
        RewriteRuleTokenStream stream_NUMBER=new RewriteRuleTokenStream(adaptor,"token NUMBER");
        RewriteRuleSubtreeStream stream_realP=new RewriteRuleSubtreeStream(adaptor,"rule realP");
        RewriteRuleSubtreeStream stream_stringP=new RewriteRuleSubtreeStream(adaptor,"rule stringP");
        RewriteRuleSubtreeStream stream_intP=new RewriteRuleSubtreeStream(adaptor,"rule intP");
        try {
            // labels.g:61:4: ( intP COLON NUMBER -> ^( INT NUMBER ) | realP COLON RNUMBER -> ^( REAL RNUMBER ) | stringP COLON DQTEXT -> ^( STRING DQTEXT ) | boolP COLON ( trueP | falseP ) )
            int alt12=4;
            switch ( input.LA(1) ) {
            case 71:
                {
                alt12=1;
                }
                break;
            case 72:
                {
                alt12=2;
                }
                break;
            case 73:
                {
                alt12=3;
                }
                break;
            case 74:
                {
                alt12=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }

            switch (alt12) {
                case 1 :
                    // labels.g:61:6: intP COLON NUMBER
                    {
                    pushFollow(FOLLOW_intP_in_valueLabel326);
                    intP27=intP();

                    state._fsp--;

                    stream_intP.add(intP27.getTree());
                    COLON28=(Token)match(input,COLON,FOLLOW_COLON_in_valueLabel328);  
                    stream_COLON.add(COLON28);

                    NUMBER29=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_valueLabel330);  
                    stream_NUMBER.add(NUMBER29);



                    // AST REWRITE
                    // elements: NUMBER
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 61:24: -> ^( INT NUMBER )
                    {
                        // labels.g:61:27: ^( INT NUMBER )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(INT, "INT"), root_1);

                        adaptor.addChild(root_1, stream_NUMBER.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 2 :
                    // labels.g:62:6: realP COLON RNUMBER
                    {
                    pushFollow(FOLLOW_realP_in_valueLabel345);
                    realP30=realP();

                    state._fsp--;

                    stream_realP.add(realP30.getTree());
                    COLON31=(Token)match(input,COLON,FOLLOW_COLON_in_valueLabel347);  
                    stream_COLON.add(COLON31);

                    RNUMBER32=(Token)match(input,RNUMBER,FOLLOW_RNUMBER_in_valueLabel349);  
                    stream_RNUMBER.add(RNUMBER32);



                    // AST REWRITE
                    // elements: RNUMBER
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 62:26: -> ^( REAL RNUMBER )
                    {
                        // labels.g:62:29: ^( REAL RNUMBER )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(REAL, "REAL"), root_1);

                        adaptor.addChild(root_1, stream_RNUMBER.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 3 :
                    // labels.g:63:6: stringP COLON DQTEXT
                    {
                    pushFollow(FOLLOW_stringP_in_valueLabel364);
                    stringP33=stringP();

                    state._fsp--;

                    stream_stringP.add(stringP33.getTree());
                    COLON34=(Token)match(input,COLON,FOLLOW_COLON_in_valueLabel366);  
                    stream_COLON.add(COLON34);

                    DQTEXT35=(Token)match(input,DQTEXT,FOLLOW_DQTEXT_in_valueLabel368);  
                    stream_DQTEXT.add(DQTEXT35);



                    // AST REWRITE
                    // elements: DQTEXT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 63:27: -> ^( STRING DQTEXT )
                    {
                        // labels.g:63:30: ^( STRING DQTEXT )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(STRING, "STRING"), root_1);

                        adaptor.addChild(root_1, stream_DQTEXT.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 4 :
                    // labels.g:64:6: boolP COLON ( trueP | falseP )
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_boolP_in_valueLabel383);
                    boolP36=boolP();

                    state._fsp--;

                    adaptor.addChild(root_0, boolP36.getTree());
                    COLON37=(Token)match(input,COLON,FOLLOW_COLON_in_valueLabel385); 
                    COLON37_tree = (Object)adaptor.create(COLON37);
                    adaptor.addChild(root_0, COLON37_tree);

                    // labels.g:64:18: ( trueP | falseP )
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==78) ) {
                        alt11=1;
                    }
                    else if ( (LA11_0==79) ) {
                        alt11=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 11, 0, input);

                        throw nvae;
                    }
                    switch (alt11) {
                        case 1 :
                            // labels.g:64:19: trueP
                            {
                            pushFollow(FOLLOW_trueP_in_valueLabel388);
                            trueP38=trueP();

                            state._fsp--;

                            adaptor.addChild(root_0, trueP38.getTree());

                            }
                            break;
                        case 2 :
                            // labels.g:64:27: falseP
                            {
                            pushFollow(FOLLOW_falseP_in_valueLabel392);
                            falseP39=falseP();

                            state._fsp--;

                            adaptor.addChild(root_0, falseP39.getTree());

                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end valueLabel

    public static class nodeLabel_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start nodeLabel
    // labels.g:66:1: nodeLabel : ( typeP COLON IDENT -> ^( TYPE IDENT ) | flagP COLON IDENT -> ^( FLAG IDENT ) );
    public final labelsParser.nodeLabel_return nodeLabel() throws RecognitionException {
        labelsParser.nodeLabel_return retval = new labelsParser.nodeLabel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLON41=null;
        Token IDENT42=null;
        Token COLON44=null;
        Token IDENT45=null;
        labelsParser.typeP_return typeP40 = null;

        labelsParser.flagP_return flagP43 = null;


        Object COLON41_tree=null;
        Object IDENT42_tree=null;
        Object COLON44_tree=null;
        Object IDENT45_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_IDENT=new RewriteRuleTokenStream(adaptor,"token IDENT");
        RewriteRuleSubtreeStream stream_typeP=new RewriteRuleSubtreeStream(adaptor,"rule typeP");
        RewriteRuleSubtreeStream stream_flagP=new RewriteRuleSubtreeStream(adaptor,"rule flagP");
        try {
            // labels.g:67:4: ( typeP COLON IDENT -> ^( TYPE IDENT ) | flagP COLON IDENT -> ^( FLAG IDENT ) )
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==75) ) {
                alt13=1;
            }
            else if ( (LA13_0==76) ) {
                alt13=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // labels.g:67:6: typeP COLON IDENT
                    {
                    pushFollow(FOLLOW_typeP_in_nodeLabel407);
                    typeP40=typeP();

                    state._fsp--;

                    stream_typeP.add(typeP40.getTree());
                    COLON41=(Token)match(input,COLON,FOLLOW_COLON_in_nodeLabel409);  
                    stream_COLON.add(COLON41);

                    IDENT42=(Token)match(input,IDENT,FOLLOW_IDENT_in_nodeLabel411);  
                    stream_IDENT.add(IDENT42);



                    // AST REWRITE
                    // elements: IDENT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 67:24: -> ^( TYPE IDENT )
                    {
                        // labels.g:67:27: ^( TYPE IDENT )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(TYPE, "TYPE"), root_1);

                        adaptor.addChild(root_1, stream_IDENT.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 2 :
                    // labels.g:68:6: flagP COLON IDENT
                    {
                    pushFollow(FOLLOW_flagP_in_nodeLabel426);
                    flagP43=flagP();

                    state._fsp--;

                    stream_flagP.add(flagP43.getTree());
                    COLON44=(Token)match(input,COLON,FOLLOW_COLON_in_nodeLabel428);  
                    stream_COLON.add(COLON44);

                    IDENT45=(Token)match(input,IDENT,FOLLOW_IDENT_in_nodeLabel430);  
                    stream_IDENT.add(IDENT45);



                    // AST REWRITE
                    // elements: IDENT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 68:24: -> ^( FLAG IDENT )
                    {
                        // labels.g:68:27: ^( FLAG IDENT )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FLAG, "FLAG"), root_1);

                        adaptor.addChild(root_1, stream_IDENT.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end nodeLabel

    public static class ruleLabel_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start ruleLabel
    // labels.g:70:1: ruleLabel : ( prefix )* actualRuleLabel ;
    public final labelsParser.ruleLabel_return ruleLabel() throws RecognitionException {
        labelsParser.ruleLabel_return retval = new labelsParser.ruleLabel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        labelsParser.prefix_return prefix46 = null;

        labelsParser.actualRuleLabel_return actualRuleLabel47 = null;



        try {
            // labels.g:71:4: ( ( prefix )* actualRuleLabel )
            // labels.g:71:6: ( prefix )* actualRuleLabel
            {
            root_0 = (Object)adaptor.nil();

            // labels.g:71:6: ( prefix )*
            loop14:
            do {
                int alt14=2;
                alt14 = dfa14.predict(input);
                switch (alt14) {
            	case 1 :
            	    // labels.g:71:6: prefix
            	    {
            	    pushFollow(FOLLOW_prefix_in_ruleLabel449);
            	    prefix46=prefix();

            	    state._fsp--;

            	    adaptor.addChild(root_0, prefix46.getTree());

            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);

            pushFollow(FOLLOW_actualRuleLabel_in_ruleLabel452);
            actualRuleLabel47=actualRuleLabel();

            state._fsp--;

            adaptor.addChild(root_0, actualRuleLabel47.getTree());

            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end ruleLabel

    public static class actualRuleLabel_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start actualRuleLabel
    // labels.g:73:1: actualRuleLabel : ( COLON ( . )* | remP COLON ( . )* | parP COLON | pathP COLON ( PLING )? regExpr | valueLabel | nodeLabel | attrLabel | negLabel | posLabel );
    public final labelsParser.actualRuleLabel_return actualRuleLabel() throws RecognitionException {
        labelsParser.actualRuleLabel_return retval = new labelsParser.actualRuleLabel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLON48=null;
        Token wildcard49=null;
        Token COLON51=null;
        Token wildcard52=null;
        Token COLON54=null;
        Token COLON56=null;
        Token PLING57=null;
        labelsParser.remP_return remP50 = null;

        labelsParser.parP_return parP53 = null;

        labelsParser.pathP_return pathP55 = null;

        labelsParser.regExpr_return regExpr58 = null;

        labelsParser.valueLabel_return valueLabel59 = null;

        labelsParser.nodeLabel_return nodeLabel60 = null;

        labelsParser.attrLabel_return attrLabel61 = null;

        labelsParser.negLabel_return negLabel62 = null;

        labelsParser.posLabel_return posLabel63 = null;


        Object COLON48_tree=null;
        Object wildcard49_tree=null;
        Object COLON51_tree=null;
        Object wildcard52_tree=null;
        Object COLON54_tree=null;
        Object COLON56_tree=null;
        Object PLING57_tree=null;

        try {
            // labels.g:74:4: ( COLON ( . )* | remP COLON ( . )* | parP COLON | pathP COLON ( PLING )? regExpr | valueLabel | nodeLabel | attrLabel | negLabel | posLabel )
            int alt18=9;
            alt18 = dfa18.predict(input);
            switch (alt18) {
                case 1 :
                    // labels.g:74:6: COLON ( . )*
                    {
                    root_0 = (Object)adaptor.nil();

                    COLON48=(Token)match(input,COLON,FOLLOW_COLON_in_actualRuleLabel467); 
                    COLON48_tree = (Object)adaptor.create(COLON48);
                    adaptor.addChild(root_0, COLON48_tree);

                    // labels.g:74:12: ( . )*
                    loop15:
                    do {
                        int alt15=2;
                        int LA15_0 = input.LA(1);

                        if ( ((LA15_0>=NEW && LA15_0<=79)) ) {
                            alt15=1;
                        }
                        else if ( (LA15_0==EOF) ) {
                            alt15=2;
                        }


                        switch (alt15) {
                    	case 1 :
                    	    // labels.g:74:12: .
                    	    {
                    	    wildcard49=(Token)input.LT(1);
                    	    matchAny(input); 
                    	    wildcard49_tree = (Object)adaptor.create(wildcard49);
                    	    adaptor.addChild(root_0, wildcard49_tree);


                    	    }
                    	    break;

                    	default :
                    	    break loop15;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // labels.g:75:6: remP COLON ( . )*
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_remP_in_actualRuleLabel477);
                    remP50=remP();

                    state._fsp--;

                    adaptor.addChild(root_0, remP50.getTree());
                    COLON51=(Token)match(input,COLON,FOLLOW_COLON_in_actualRuleLabel479); 
                    COLON51_tree = (Object)adaptor.create(COLON51);
                    adaptor.addChild(root_0, COLON51_tree);

                    // labels.g:75:17: ( . )*
                    loop16:
                    do {
                        int alt16=2;
                        int LA16_0 = input.LA(1);

                        if ( ((LA16_0>=NEW && LA16_0<=79)) ) {
                            alt16=1;
                        }
                        else if ( (LA16_0==EOF) ) {
                            alt16=2;
                        }


                        switch (alt16) {
                    	case 1 :
                    	    // labels.g:75:17: .
                    	    {
                    	    wildcard52=(Token)input.LT(1);
                    	    matchAny(input); 
                    	    wildcard52_tree = (Object)adaptor.create(wildcard52);
                    	    adaptor.addChild(root_0, wildcard52_tree);


                    	    }
                    	    break;

                    	default :
                    	    break loop16;
                        }
                    } while (true);


                    }
                    break;
                case 3 :
                    // labels.g:76:6: parP COLON
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_parP_in_actualRuleLabel489);
                    parP53=parP();

                    state._fsp--;

                    adaptor.addChild(root_0, parP53.getTree());
                    COLON54=(Token)match(input,COLON,FOLLOW_COLON_in_actualRuleLabel491); 
                    COLON54_tree = (Object)adaptor.create(COLON54);
                    adaptor.addChild(root_0, COLON54_tree);


                    }
                    break;
                case 4 :
                    // labels.g:77:6: pathP COLON ( PLING )? regExpr
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_pathP_in_actualRuleLabel498);
                    pathP55=pathP();

                    state._fsp--;

                    adaptor.addChild(root_0, pathP55.getTree());
                    COLON56=(Token)match(input,COLON,FOLLOW_COLON_in_actualRuleLabel500); 
                    COLON56_tree = (Object)adaptor.create(COLON56);
                    adaptor.addChild(root_0, COLON56_tree);

                    // labels.g:77:18: ( PLING )?
                    int alt17=2;
                    int LA17_0 = input.LA(1);

                    if ( (LA17_0==PLING) ) {
                        alt17=1;
                    }
                    switch (alt17) {
                        case 1 :
                            // labels.g:77:18: PLING
                            {
                            PLING57=(Token)match(input,PLING,FOLLOW_PLING_in_actualRuleLabel502); 
                            PLING57_tree = (Object)adaptor.create(PLING57);
                            adaptor.addChild(root_0, PLING57_tree);


                            }
                            break;

                    }

                    pushFollow(FOLLOW_regExpr_in_actualRuleLabel505);
                    regExpr58=regExpr();

                    state._fsp--;

                    adaptor.addChild(root_0, regExpr58.getTree());

                    }
                    break;
                case 5 :
                    // labels.g:78:6: valueLabel
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_valueLabel_in_actualRuleLabel512);
                    valueLabel59=valueLabel();

                    state._fsp--;

                    adaptor.addChild(root_0, valueLabel59.getTree());

                    }
                    break;
                case 6 :
                    // labels.g:79:6: nodeLabel
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_nodeLabel_in_actualRuleLabel519);
                    nodeLabel60=nodeLabel();

                    state._fsp--;

                    adaptor.addChild(root_0, nodeLabel60.getTree());

                    }
                    break;
                case 7 :
                    // labels.g:80:6: attrLabel
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_attrLabel_in_actualRuleLabel526);
                    attrLabel61=attrLabel();

                    state._fsp--;

                    adaptor.addChild(root_0, attrLabel61.getTree());

                    }
                    break;
                case 8 :
                    // labels.g:81:6: negLabel
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_negLabel_in_actualRuleLabel533);
                    negLabel62=negLabel();

                    state._fsp--;

                    adaptor.addChild(root_0, negLabel62.getTree());

                    }
                    break;
                case 9 :
                    // labels.g:82:6: posLabel
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_posLabel_in_actualRuleLabel540);
                    posLabel63=posLabel();

                    state._fsp--;

                    adaptor.addChild(root_0, posLabel63.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end actualRuleLabel

    public static class attrLabel_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start attrLabel
    // labels.g:84:1: attrLabel : ( intP COLON ( IDENT )? -> ^( INT IDENT ) | realP COLON ( IDENT )? -> ^( REAL IDENT ) | stringP COLON ( IDENT )? -> ^( STRING IDENT ) | boolP COLON ( IDENT )? -> ^( BOOL IDENT ) | attrP COLON -> ^( ATTR IDENT ) | prodP COLON -> ^( PROD IDENT ) | argP COLON ( DIGIT )+ -> ^( ARG IDENT ) );
    public final labelsParser.attrLabel_return attrLabel() throws RecognitionException {
        labelsParser.attrLabel_return retval = new labelsParser.attrLabel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLON65=null;
        Token IDENT66=null;
        Token COLON68=null;
        Token IDENT69=null;
        Token COLON71=null;
        Token IDENT72=null;
        Token COLON74=null;
        Token IDENT75=null;
        Token COLON77=null;
        Token COLON79=null;
        Token COLON81=null;
        Token DIGIT82=null;
        labelsParser.intP_return intP64 = null;

        labelsParser.realP_return realP67 = null;

        labelsParser.stringP_return stringP70 = null;

        labelsParser.boolP_return boolP73 = null;

        labelsParser.attrP_return attrP76 = null;

        labelsParser.prodP_return prodP78 = null;

        labelsParser.argP_return argP80 = null;


        Object COLON65_tree=null;
        Object IDENT66_tree=null;
        Object COLON68_tree=null;
        Object IDENT69_tree=null;
        Object COLON71_tree=null;
        Object IDENT72_tree=null;
        Object COLON74_tree=null;
        Object IDENT75_tree=null;
        Object COLON77_tree=null;
        Object COLON79_tree=null;
        Object COLON81_tree=null;
        Object DIGIT82_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_IDENT=new RewriteRuleTokenStream(adaptor,"token IDENT");
        RewriteRuleTokenStream stream_DIGIT=new RewriteRuleTokenStream(adaptor,"token DIGIT");
        RewriteRuleSubtreeStream stream_prodP=new RewriteRuleSubtreeStream(adaptor,"rule prodP");
        RewriteRuleSubtreeStream stream_realP=new RewriteRuleSubtreeStream(adaptor,"rule realP");
        RewriteRuleSubtreeStream stream_argP=new RewriteRuleSubtreeStream(adaptor,"rule argP");
        RewriteRuleSubtreeStream stream_attrP=new RewriteRuleSubtreeStream(adaptor,"rule attrP");
        RewriteRuleSubtreeStream stream_stringP=new RewriteRuleSubtreeStream(adaptor,"rule stringP");
        RewriteRuleSubtreeStream stream_intP=new RewriteRuleSubtreeStream(adaptor,"rule intP");
        RewriteRuleSubtreeStream stream_boolP=new RewriteRuleSubtreeStream(adaptor,"rule boolP");
        try {
            // labels.g:85:4: ( intP COLON ( IDENT )? -> ^( INT IDENT ) | realP COLON ( IDENT )? -> ^( REAL IDENT ) | stringP COLON ( IDENT )? -> ^( STRING IDENT ) | boolP COLON ( IDENT )? -> ^( BOOL IDENT ) | attrP COLON -> ^( ATTR IDENT ) | prodP COLON -> ^( PROD IDENT ) | argP COLON ( DIGIT )+ -> ^( ARG IDENT ) )
            int alt24=7;
            switch ( input.LA(1) ) {
            case 71:
                {
                alt24=1;
                }
                break;
            case 72:
                {
                alt24=2;
                }
                break;
            case 73:
                {
                alt24=3;
                }
                break;
            case 74:
                {
                alt24=4;
                }
                break;
            case 68:
                {
                alt24=5;
                }
                break;
            case 69:
                {
                alt24=6;
                }
                break;
            case 70:
                {
                alt24=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 24, 0, input);

                throw nvae;
            }

            switch (alt24) {
                case 1 :
                    // labels.g:85:6: intP COLON ( IDENT )?
                    {
                    pushFollow(FOLLOW_intP_in_attrLabel555);
                    intP64=intP();

                    state._fsp--;

                    stream_intP.add(intP64.getTree());
                    COLON65=(Token)match(input,COLON,FOLLOW_COLON_in_attrLabel557);  
                    stream_COLON.add(COLON65);

                    // labels.g:85:17: ( IDENT )?
                    int alt19=2;
                    int LA19_0 = input.LA(1);

                    if ( (LA19_0==IDENT) ) {
                        alt19=1;
                    }
                    switch (alt19) {
                        case 1 :
                            // labels.g:85:17: IDENT
                            {
                            IDENT66=(Token)match(input,IDENT,FOLLOW_IDENT_in_attrLabel559);  
                            stream_IDENT.add(IDENT66);


                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: IDENT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 85:24: -> ^( INT IDENT )
                    {
                        // labels.g:85:27: ^( INT IDENT )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(INT, "INT"), root_1);

                        adaptor.addChild(root_1, stream_IDENT.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 2 :
                    // labels.g:86:6: realP COLON ( IDENT )?
                    {
                    pushFollow(FOLLOW_realP_in_attrLabel575);
                    realP67=realP();

                    state._fsp--;

                    stream_realP.add(realP67.getTree());
                    COLON68=(Token)match(input,COLON,FOLLOW_COLON_in_attrLabel577);  
                    stream_COLON.add(COLON68);

                    // labels.g:86:18: ( IDENT )?
                    int alt20=2;
                    int LA20_0 = input.LA(1);

                    if ( (LA20_0==IDENT) ) {
                        alt20=1;
                    }
                    switch (alt20) {
                        case 1 :
                            // labels.g:86:18: IDENT
                            {
                            IDENT69=(Token)match(input,IDENT,FOLLOW_IDENT_in_attrLabel579);  
                            stream_IDENT.add(IDENT69);


                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: IDENT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 86:25: -> ^( REAL IDENT )
                    {
                        // labels.g:86:28: ^( REAL IDENT )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(REAL, "REAL"), root_1);

                        adaptor.addChild(root_1, stream_IDENT.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 3 :
                    // labels.g:87:6: stringP COLON ( IDENT )?
                    {
                    pushFollow(FOLLOW_stringP_in_attrLabel595);
                    stringP70=stringP();

                    state._fsp--;

                    stream_stringP.add(stringP70.getTree());
                    COLON71=(Token)match(input,COLON,FOLLOW_COLON_in_attrLabel597);  
                    stream_COLON.add(COLON71);

                    // labels.g:87:20: ( IDENT )?
                    int alt21=2;
                    int LA21_0 = input.LA(1);

                    if ( (LA21_0==IDENT) ) {
                        alt21=1;
                    }
                    switch (alt21) {
                        case 1 :
                            // labels.g:87:20: IDENT
                            {
                            IDENT72=(Token)match(input,IDENT,FOLLOW_IDENT_in_attrLabel599);  
                            stream_IDENT.add(IDENT72);


                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: IDENT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 87:27: -> ^( STRING IDENT )
                    {
                        // labels.g:87:30: ^( STRING IDENT )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(STRING, "STRING"), root_1);

                        adaptor.addChild(root_1, stream_IDENT.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 4 :
                    // labels.g:88:6: boolP COLON ( IDENT )?
                    {
                    pushFollow(FOLLOW_boolP_in_attrLabel615);
                    boolP73=boolP();

                    state._fsp--;

                    stream_boolP.add(boolP73.getTree());
                    COLON74=(Token)match(input,COLON,FOLLOW_COLON_in_attrLabel617);  
                    stream_COLON.add(COLON74);

                    // labels.g:88:18: ( IDENT )?
                    int alt22=2;
                    int LA22_0 = input.LA(1);

                    if ( (LA22_0==IDENT) ) {
                        alt22=1;
                    }
                    switch (alt22) {
                        case 1 :
                            // labels.g:88:18: IDENT
                            {
                            IDENT75=(Token)match(input,IDENT,FOLLOW_IDENT_in_attrLabel619);  
                            stream_IDENT.add(IDENT75);


                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: IDENT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 88:25: -> ^( BOOL IDENT )
                    {
                        // labels.g:88:28: ^( BOOL IDENT )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(BOOL, "BOOL"), root_1);

                        adaptor.addChild(root_1, stream_IDENT.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 5 :
                    // labels.g:89:6: attrP COLON
                    {
                    pushFollow(FOLLOW_attrP_in_attrLabel635);
                    attrP76=attrP();

                    state._fsp--;

                    stream_attrP.add(attrP76.getTree());
                    COLON77=(Token)match(input,COLON,FOLLOW_COLON_in_attrLabel637);  
                    stream_COLON.add(COLON77);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 89:18: -> ^( ATTR IDENT )
                    {
                        // labels.g:89:21: ^( ATTR IDENT )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ATTR, "ATTR"), root_1);

                        adaptor.addChild(root_1, (Object)adaptor.create(IDENT, "IDENT"));

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 6 :
                    // labels.g:90:6: prodP COLON
                    {
                    pushFollow(FOLLOW_prodP_in_attrLabel652);
                    prodP78=prodP();

                    state._fsp--;

                    stream_prodP.add(prodP78.getTree());
                    COLON79=(Token)match(input,COLON,FOLLOW_COLON_in_attrLabel654);  
                    stream_COLON.add(COLON79);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 90:18: -> ^( PROD IDENT )
                    {
                        // labels.g:90:21: ^( PROD IDENT )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PROD, "PROD"), root_1);

                        adaptor.addChild(root_1, (Object)adaptor.create(IDENT, "IDENT"));

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 7 :
                    // labels.g:91:6: argP COLON ( DIGIT )+
                    {
                    pushFollow(FOLLOW_argP_in_attrLabel669);
                    argP80=argP();

                    state._fsp--;

                    stream_argP.add(argP80.getTree());
                    COLON81=(Token)match(input,COLON,FOLLOW_COLON_in_attrLabel671);  
                    stream_COLON.add(COLON81);

                    // labels.g:91:17: ( DIGIT )+
                    int cnt23=0;
                    loop23:
                    do {
                        int alt23=2;
                        int LA23_0 = input.LA(1);

                        if ( (LA23_0==DIGIT) ) {
                            alt23=1;
                        }


                        switch (alt23) {
                    	case 1 :
                    	    // labels.g:91:17: DIGIT
                    	    {
                    	    DIGIT82=(Token)match(input,DIGIT,FOLLOW_DIGIT_in_attrLabel673);  
                    	    stream_DIGIT.add(DIGIT82);


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt23 >= 1 ) break loop23;
                                EarlyExitException eee =
                                    new EarlyExitException(23, input);
                                throw eee;
                        }
                        cnt23++;
                    } while (true);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 91:24: -> ^( ARG IDENT )
                    {
                        // labels.g:91:27: ^( ARG IDENT )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ARG, "ARG"), root_1);

                        adaptor.addChild(root_1, (Object)adaptor.create(IDENT, "IDENT"));

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end attrLabel

    public static class negLabel_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start negLabel
    // labels.g:93:1: negLabel : PLING posLabel ;
    public final labelsParser.negLabel_return negLabel() throws RecognitionException {
        labelsParser.negLabel_return retval = new labelsParser.negLabel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token PLING83=null;
        labelsParser.posLabel_return posLabel84 = null;


        Object PLING83_tree=null;

        try {
            // labels.g:94:4: ( PLING posLabel )
            // labels.g:94:6: PLING posLabel
            {
            root_0 = (Object)adaptor.nil();

            PLING83=(Token)match(input,PLING,FOLLOW_PLING_in_negLabel693); 
            PLING83_tree = (Object)adaptor.create(PLING83);
            adaptor.addChild(root_0, PLING83_tree);

            pushFollow(FOLLOW_posLabel_in_negLabel695);
            posLabel84=posLabel();

            state._fsp--;

            adaptor.addChild(root_0, posLabel84.getTree());

            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end negLabel

    public static class posLabel_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start posLabel
    // labels.g:96:1: posLabel : ( wildcard | EQUALS | LBRACE regExpr RBRACE | SQTEXT | (~ ( SQTEXT | LBRACE | RBRACE | QUERY | EQUALS | COLON | PLING ) )* );
    public final labelsParser.posLabel_return posLabel() throws RecognitionException {
        labelsParser.posLabel_return retval = new labelsParser.posLabel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQUALS86=null;
        Token LBRACE87=null;
        Token RBRACE89=null;
        Token SQTEXT90=null;
        Token set91=null;
        labelsParser.wildcard_return wildcard85 = null;

        labelsParser.regExpr_return regExpr88 = null;


        Object EQUALS86_tree=null;
        Object LBRACE87_tree=null;
        Object RBRACE89_tree=null;
        Object SQTEXT90_tree=null;
        Object set91_tree=null;

        try {
            // labels.g:97:4: ( wildcard | EQUALS | LBRACE regExpr RBRACE | SQTEXT | (~ ( SQTEXT | LBRACE | RBRACE | QUERY | EQUALS | COLON | PLING ) )* )
            int alt26=5;
            switch ( input.LA(1) ) {
            case QUERY:
                {
                alt26=1;
                }
                break;
            case EQUALS:
                {
                alt26=2;
                }
                break;
            case LBRACE:
                {
                alt26=3;
                }
                break;
            case SQTEXT:
                {
                alt26=4;
                }
                break;
            case EOF:
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
            case IDENT:
            case NUMBER:
            case RNUMBER:
            case DQTEXT:
            case DIGIT:
            case BAR:
            case DOT:
            case MINUS:
            case STAR:
            case PLUS:
            case IDENTCHAR:
            case LPAR:
            case RPAR:
            case LSQUARE:
            case HAT:
            case COMMA:
            case RSQUARE:
            case SQUOTE:
            case BSLASH:
            case DQUOTE:
            case DOLLAR:
            case UNDER:
            case LETTER:
            case LABEL:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
                {
                alt26=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 26, 0, input);

                throw nvae;
            }

            switch (alt26) {
                case 1 :
                    // labels.g:97:6: wildcard
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_wildcard_in_posLabel710);
                    wildcard85=wildcard();

                    state._fsp--;

                    adaptor.addChild(root_0, wildcard85.getTree());

                    }
                    break;
                case 2 :
                    // labels.g:98:6: EQUALS
                    {
                    root_0 = (Object)adaptor.nil();

                    EQUALS86=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_posLabel717); 
                    EQUALS86_tree = (Object)adaptor.create(EQUALS86);
                    adaptor.addChild(root_0, EQUALS86_tree);


                    }
                    break;
                case 3 :
                    // labels.g:99:6: LBRACE regExpr RBRACE
                    {
                    root_0 = (Object)adaptor.nil();

                    LBRACE87=(Token)match(input,LBRACE,FOLLOW_LBRACE_in_posLabel724); 
                    LBRACE87_tree = (Object)adaptor.create(LBRACE87);
                    adaptor.addChild(root_0, LBRACE87_tree);

                    pushFollow(FOLLOW_regExpr_in_posLabel726);
                    regExpr88=regExpr();

                    state._fsp--;

                    adaptor.addChild(root_0, regExpr88.getTree());
                    RBRACE89=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_posLabel728); 
                    RBRACE89_tree = (Object)adaptor.create(RBRACE89);
                    adaptor.addChild(root_0, RBRACE89_tree);


                    }
                    break;
                case 4 :
                    // labels.g:100:6: SQTEXT
                    {
                    root_0 = (Object)adaptor.nil();

                    SQTEXT90=(Token)match(input,SQTEXT,FOLLOW_SQTEXT_in_posLabel735); 
                    SQTEXT90_tree = (Object)adaptor.create(SQTEXT90);
                    adaptor.addChild(root_0, SQTEXT90_tree);


                    }
                    break;
                case 5 :
                    // labels.g:101:6: (~ ( SQTEXT | LBRACE | RBRACE | QUERY | EQUALS | COLON | PLING ) )*
                    {
                    root_0 = (Object)adaptor.nil();

                    // labels.g:101:6: (~ ( SQTEXT | LBRACE | RBRACE | QUERY | EQUALS | COLON | PLING ) )*
                    loop25:
                    do {
                        int alt25=2;
                        int LA25_0 = input.LA(1);

                        if ( ((LA25_0>=NEW && LA25_0<=EMPTY)||LA25_0==IDENT||(LA25_0>=NUMBER && LA25_0<=DQTEXT)||LA25_0==DIGIT||(LA25_0>=BAR && LA25_0<=79)) ) {
                            alt25=1;
                        }


                        switch (alt25) {
                    	case 1 :
                    	    // labels.g:101:7: ~ ( SQTEXT | LBRACE | RBRACE | QUERY | EQUALS | COLON | PLING )
                    	    {
                    	    set91=(Token)input.LT(1);
                    	    if ( (input.LA(1)>=NEW && input.LA(1)<=EMPTY)||input.LA(1)==IDENT||(input.LA(1)>=NUMBER && input.LA(1)<=DQTEXT)||input.LA(1)==DIGIT||(input.LA(1)>=BAR && input.LA(1)<=79) ) {
                    	        input.consume();
                    	        adaptor.addChild(root_0, (Object)adaptor.create(set91));
                    	        state.errorRecovery=false;
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop25;
                        }
                    } while (true);


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end posLabel

    public static class regExpr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start regExpr
    // labels.g:103:1: regExpr : choice ;
    public final labelsParser.regExpr_return regExpr() throws RecognitionException {
        labelsParser.regExpr_return retval = new labelsParser.regExpr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        labelsParser.choice_return choice92 = null;



        try {
            // labels.g:104:4: ( choice )
            // labels.g:104:6: choice
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_choice_in_regExpr775);
            choice92=choice();

            state._fsp--;

            adaptor.addChild(root_0, choice92.getTree());

            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end regExpr

    public static class choice_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start choice
    // labels.g:106:1: choice : sequence ( BAR choice )? ;
    public final labelsParser.choice_return choice() throws RecognitionException {
        labelsParser.choice_return retval = new labelsParser.choice_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BAR94=null;
        labelsParser.sequence_return sequence93 = null;

        labelsParser.choice_return choice95 = null;


        Object BAR94_tree=null;

        try {
            // labels.g:107:4: ( sequence ( BAR choice )? )
            // labels.g:107:6: sequence ( BAR choice )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_sequence_in_choice787);
            sequence93=sequence();

            state._fsp--;

            adaptor.addChild(root_0, sequence93.getTree());
            // labels.g:107:15: ( BAR choice )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==BAR) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // labels.g:107:16: BAR choice
                    {
                    BAR94=(Token)match(input,BAR,FOLLOW_BAR_in_choice790); 
                    pushFollow(FOLLOW_choice_in_choice793);
                    choice95=choice();

                    state._fsp--;

                    adaptor.addChild(root_0, choice95.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end choice

    public static class sequence_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start sequence
    // labels.g:109:1: sequence : unary ( DOT sequence )? ;
    public final labelsParser.sequence_return sequence() throws RecognitionException {
        labelsParser.sequence_return retval = new labelsParser.sequence_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOT97=null;
        labelsParser.unary_return unary96 = null;

        labelsParser.sequence_return sequence98 = null;


        Object DOT97_tree=null;

        try {
            // labels.g:110:4: ( unary ( DOT sequence )? )
            // labels.g:110:6: unary ( DOT sequence )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_unary_in_sequence807);
            unary96=unary();

            state._fsp--;

            adaptor.addChild(root_0, unary96.getTree());
            // labels.g:110:12: ( DOT sequence )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==DOT) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // labels.g:110:13: DOT sequence
                    {
                    DOT97=(Token)match(input,DOT,FOLLOW_DOT_in_sequence810); 
                    pushFollow(FOLLOW_sequence_in_sequence813);
                    sequence98=sequence();

                    state._fsp--;

                    adaptor.addChild(root_0, sequence98.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end sequence

    public static class unary_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start unary
    // labels.g:112:1: unary : ( MINUS unary | atom ( STAR | PLUS )? );
    public final labelsParser.unary_return unary() throws RecognitionException {
        labelsParser.unary_return retval = new labelsParser.unary_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token MINUS99=null;
        Token STAR102=null;
        Token PLUS103=null;
        labelsParser.unary_return unary100 = null;

        labelsParser.atom_return atom101 = null;


        Object MINUS99_tree=null;
        Object STAR102_tree=null;
        Object PLUS103_tree=null;

        try {
            // labels.g:113:4: ( MINUS unary | atom ( STAR | PLUS )? )
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==MINUS) ) {
                alt30=1;
            }
            else if ( (LA30_0==EQUALS||(LA30_0>=SQTEXT && LA30_0<=QUERY)||(LA30_0>=IDENTCHAR && LA30_0<=LPAR)) ) {
                alt30=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 30, 0, input);

                throw nvae;
            }
            switch (alt30) {
                case 1 :
                    // labels.g:113:6: MINUS unary
                    {
                    root_0 = (Object)adaptor.nil();

                    MINUS99=(Token)match(input,MINUS,FOLLOW_MINUS_in_unary827); 
                    MINUS99_tree = (Object)adaptor.create(MINUS99);
                    adaptor.addChild(root_0, MINUS99_tree);

                    pushFollow(FOLLOW_unary_in_unary829);
                    unary100=unary();

                    state._fsp--;

                    adaptor.addChild(root_0, unary100.getTree());

                    }
                    break;
                case 2 :
                    // labels.g:114:6: atom ( STAR | PLUS )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_atom_in_unary836);
                    atom101=atom();

                    state._fsp--;

                    adaptor.addChild(root_0, atom101.getTree());
                    // labels.g:114:11: ( STAR | PLUS )?
                    int alt29=3;
                    int LA29_0 = input.LA(1);

                    if ( (LA29_0==STAR) ) {
                        alt29=1;
                    }
                    else if ( (LA29_0==PLUS) ) {
                        alt29=2;
                    }
                    switch (alt29) {
                        case 1 :
                            // labels.g:114:12: STAR
                            {
                            STAR102=(Token)match(input,STAR,FOLLOW_STAR_in_unary839); 

                            }
                            break;
                        case 2 :
                            // labels.g:114:20: PLUS
                            {
                            PLUS103=(Token)match(input,PLUS,FOLLOW_PLUS_in_unary844); 

                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end unary

    public static class atom_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start atom
    // labels.g:116:1: atom : ( SQTEXT | ( IDENTCHAR )+ | EQUALS | LPAR regExpr RPAR | wildcard );
    public final labelsParser.atom_return atom() throws RecognitionException {
        labelsParser.atom_return retval = new labelsParser.atom_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SQTEXT104=null;
        Token IDENTCHAR105=null;
        Token EQUALS106=null;
        Token LPAR107=null;
        Token RPAR109=null;
        labelsParser.regExpr_return regExpr108 = null;

        labelsParser.wildcard_return wildcard110 = null;


        Object SQTEXT104_tree=null;
        Object IDENTCHAR105_tree=null;
        Object EQUALS106_tree=null;
        Object LPAR107_tree=null;
        Object RPAR109_tree=null;

        try {
            // labels.g:117:4: ( SQTEXT | ( IDENTCHAR )+ | EQUALS | LPAR regExpr RPAR | wildcard )
            int alt32=5;
            switch ( input.LA(1) ) {
            case SQTEXT:
                {
                alt32=1;
                }
                break;
            case IDENTCHAR:
                {
                alt32=2;
                }
                break;
            case EQUALS:
                {
                alt32=3;
                }
                break;
            case LPAR:
                {
                alt32=4;
                }
                break;
            case QUERY:
                {
                alt32=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                throw nvae;
            }

            switch (alt32) {
                case 1 :
                    // labels.g:117:6: SQTEXT
                    {
                    root_0 = (Object)adaptor.nil();

                    SQTEXT104=(Token)match(input,SQTEXT,FOLLOW_SQTEXT_in_atom859); 
                    SQTEXT104_tree = (Object)adaptor.create(SQTEXT104);
                    adaptor.addChild(root_0, SQTEXT104_tree);


                    }
                    break;
                case 2 :
                    // labels.g:118:6: ( IDENTCHAR )+
                    {
                    root_0 = (Object)adaptor.nil();

                    // labels.g:118:6: ( IDENTCHAR )+
                    int cnt31=0;
                    loop31:
                    do {
                        int alt31=2;
                        int LA31_0 = input.LA(1);

                        if ( (LA31_0==IDENTCHAR) ) {
                            alt31=1;
                        }


                        switch (alt31) {
                    	case 1 :
                    	    // labels.g:118:6: IDENTCHAR
                    	    {
                    	    IDENTCHAR105=(Token)match(input,IDENTCHAR,FOLLOW_IDENTCHAR_in_atom866); 
                    	    IDENTCHAR105_tree = (Object)adaptor.create(IDENTCHAR105);
                    	    adaptor.addChild(root_0, IDENTCHAR105_tree);


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt31 >= 1 ) break loop31;
                                EarlyExitException eee =
                                    new EarlyExitException(31, input);
                                throw eee;
                        }
                        cnt31++;
                    } while (true);


                    }
                    break;
                case 3 :
                    // labels.g:119:6: EQUALS
                    {
                    root_0 = (Object)adaptor.nil();

                    EQUALS106=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_atom874); 
                    EQUALS106_tree = (Object)adaptor.create(EQUALS106);
                    adaptor.addChild(root_0, EQUALS106_tree);


                    }
                    break;
                case 4 :
                    // labels.g:120:6: LPAR regExpr RPAR
                    {
                    root_0 = (Object)adaptor.nil();

                    LPAR107=(Token)match(input,LPAR,FOLLOW_LPAR_in_atom881); 
                    LPAR107_tree = (Object)adaptor.create(LPAR107);
                    adaptor.addChild(root_0, LPAR107_tree);

                    pushFollow(FOLLOW_regExpr_in_atom883);
                    regExpr108=regExpr();

                    state._fsp--;

                    adaptor.addChild(root_0, regExpr108.getTree());
                    RPAR109=(Token)match(input,RPAR,FOLLOW_RPAR_in_atom885); 
                    RPAR109_tree = (Object)adaptor.create(RPAR109);
                    adaptor.addChild(root_0, RPAR109_tree);


                    }
                    break;
                case 5 :
                    // labels.g:121:6: wildcard
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_wildcard_in_atom892);
                    wildcard110=wildcard();

                    state._fsp--;

                    adaptor.addChild(root_0, wildcard110.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end atom

    public static class wildcard_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start wildcard
    // labels.g:123:1: wildcard : QUERY ( IDENT )? LSQUARE ( HAT )? atom ( COMMA atom )* RSQUARE -> ^( QUERY IDENT HAT ( atom )* ) ;
    public final labelsParser.wildcard_return wildcard() throws RecognitionException {
        labelsParser.wildcard_return retval = new labelsParser.wildcard_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token QUERY111=null;
        Token IDENT112=null;
        Token LSQUARE113=null;
        Token HAT114=null;
        Token COMMA116=null;
        Token RSQUARE118=null;
        labelsParser.atom_return atom115 = null;

        labelsParser.atom_return atom117 = null;


        Object QUERY111_tree=null;
        Object IDENT112_tree=null;
        Object LSQUARE113_tree=null;
        Object HAT114_tree=null;
        Object COMMA116_tree=null;
        Object RSQUARE118_tree=null;
        RewriteRuleTokenStream stream_HAT=new RewriteRuleTokenStream(adaptor,"token HAT");
        RewriteRuleTokenStream stream_QUERY=new RewriteRuleTokenStream(adaptor,"token QUERY");
        RewriteRuleTokenStream stream_IDENT=new RewriteRuleTokenStream(adaptor,"token IDENT");
        RewriteRuleTokenStream stream_LSQUARE=new RewriteRuleTokenStream(adaptor,"token LSQUARE");
        RewriteRuleTokenStream stream_RSQUARE=new RewriteRuleTokenStream(adaptor,"token RSQUARE");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_atom=new RewriteRuleSubtreeStream(adaptor,"rule atom");
        try {
            // labels.g:124:4: ( QUERY ( IDENT )? LSQUARE ( HAT )? atom ( COMMA atom )* RSQUARE -> ^( QUERY IDENT HAT ( atom )* ) )
            // labels.g:124:6: QUERY ( IDENT )? LSQUARE ( HAT )? atom ( COMMA atom )* RSQUARE
            {
            QUERY111=(Token)match(input,QUERY,FOLLOW_QUERY_in_wildcard904);  
            stream_QUERY.add(QUERY111);

            // labels.g:124:12: ( IDENT )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==IDENT) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // labels.g:124:12: IDENT
                    {
                    IDENT112=(Token)match(input,IDENT,FOLLOW_IDENT_in_wildcard906);  
                    stream_IDENT.add(IDENT112);


                    }
                    break;

            }

            LSQUARE113=(Token)match(input,LSQUARE,FOLLOW_LSQUARE_in_wildcard909);  
            stream_LSQUARE.add(LSQUARE113);

            // labels.g:124:27: ( HAT )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==HAT) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // labels.g:124:27: HAT
                    {
                    HAT114=(Token)match(input,HAT,FOLLOW_HAT_in_wildcard911);  
                    stream_HAT.add(HAT114);


                    }
                    break;

            }

            pushFollow(FOLLOW_atom_in_wildcard914);
            atom115=atom();

            state._fsp--;

            stream_atom.add(atom115.getTree());
            // labels.g:124:37: ( COMMA atom )*
            loop35:
            do {
                int alt35=2;
                int LA35_0 = input.LA(1);

                if ( (LA35_0==COMMA) ) {
                    alt35=1;
                }


                switch (alt35) {
            	case 1 :
            	    // labels.g:124:38: COMMA atom
            	    {
            	    COMMA116=(Token)match(input,COMMA,FOLLOW_COMMA_in_wildcard917);  
            	    stream_COMMA.add(COMMA116);

            	    pushFollow(FOLLOW_atom_in_wildcard919);
            	    atom117=atom();

            	    state._fsp--;

            	    stream_atom.add(atom117.getTree());

            	    }
            	    break;

            	default :
            	    break loop35;
                }
            } while (true);

            RSQUARE118=(Token)match(input,RSQUARE,FOLLOW_RSQUARE_in_wildcard923);  
            stream_RSQUARE.add(RSQUARE118);



            // AST REWRITE
            // elements: IDENT, atom, QUERY, HAT
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 125:6: -> ^( QUERY IDENT HAT ( atom )* )
            {
                // labels.g:125:9: ^( QUERY IDENT HAT ( atom )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_QUERY.nextNode(), root_1);

                adaptor.addChild(root_1, stream_IDENT.nextNode());
                adaptor.addChild(root_1, stream_HAT.nextNode());
                // labels.g:125:27: ( atom )*
                while ( stream_atom.hasNext() ) {
                    adaptor.addChild(root_1, stream_atom.nextTree());

                }
                stream_atom.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end wildcard

    public static class newP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start newP
    // labels.g:133:1: newP : 'new' ;
    public final labelsParser.newP_return newP() throws RecognitionException {
        labelsParser.newP_return retval = new labelsParser.newP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal119=null;

        Object string_literal119_tree=null;

        try {
            // labels.g:133:9: ( 'new' )
            // labels.g:133:11: 'new'
            {
            root_0 = (Object)adaptor.nil();

            string_literal119=(Token)match(input,57,FOLLOW_57_in_newP1018); 
            string_literal119_tree = (Object)adaptor.create(string_literal119);
            adaptor.addChild(root_0, string_literal119_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end newP

    public static class delP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start delP
    // labels.g:134:1: delP : 'del' ;
    public final labelsParser.delP_return delP() throws RecognitionException {
        labelsParser.delP_return retval = new labelsParser.delP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal120=null;

        Object string_literal120_tree=null;

        try {
            // labels.g:134:9: ( 'del' )
            // labels.g:134:11: 'del'
            {
            root_0 = (Object)adaptor.nil();

            string_literal120=(Token)match(input,58,FOLLOW_58_in_delP1028); 
            string_literal120_tree = (Object)adaptor.create(string_literal120);
            adaptor.addChild(root_0, string_literal120_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end delP

    public static class cnewP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start cnewP
    // labels.g:135:1: cnewP : 'cnew' ;
    public final labelsParser.cnewP_return cnewP() throws RecognitionException {
        labelsParser.cnewP_return retval = new labelsParser.cnewP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal121=null;

        Object string_literal121_tree=null;

        try {
            // labels.g:135:9: ( 'cnew' )
            // labels.g:135:11: 'cnew'
            {
            root_0 = (Object)adaptor.nil();

            string_literal121=(Token)match(input,59,FOLLOW_59_in_cnewP1037); 
            string_literal121_tree = (Object)adaptor.create(string_literal121);
            adaptor.addChild(root_0, string_literal121_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end cnewP

    public static class notP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start notP
    // labels.g:136:1: notP : 'not' ;
    public final labelsParser.notP_return notP() throws RecognitionException {
        labelsParser.notP_return retval = new labelsParser.notP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal122=null;

        Object string_literal122_tree=null;

        try {
            // labels.g:136:9: ( 'not' )
            // labels.g:136:11: 'not'
            {
            root_0 = (Object)adaptor.nil();

            string_literal122=(Token)match(input,60,FOLLOW_60_in_notP1047); 
            string_literal122_tree = (Object)adaptor.create(string_literal122);
            adaptor.addChild(root_0, string_literal122_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end notP

    public static class useP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start useP
    // labels.g:137:1: useP : 'use' ;
    public final labelsParser.useP_return useP() throws RecognitionException {
        labelsParser.useP_return retval = new labelsParser.useP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal123=null;

        Object string_literal123_tree=null;

        try {
            // labels.g:137:9: ( 'use' )
            // labels.g:137:11: 'use'
            {
            root_0 = (Object)adaptor.nil();

            string_literal123=(Token)match(input,61,FOLLOW_61_in_useP1057); 
            string_literal123_tree = (Object)adaptor.create(string_literal123);
            adaptor.addChild(root_0, string_literal123_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end useP

    public static class remP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start remP
    // labels.g:138:1: remP : 'rem' ;
    public final labelsParser.remP_return remP() throws RecognitionException {
        labelsParser.remP_return retval = new labelsParser.remP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal124=null;

        Object string_literal124_tree=null;

        try {
            // labels.g:138:9: ( 'rem' )
            // labels.g:138:11: 'rem'
            {
            root_0 = (Object)adaptor.nil();

            string_literal124=(Token)match(input,62,FOLLOW_62_in_remP1067); 
            string_literal124_tree = (Object)adaptor.create(string_literal124);
            adaptor.addChild(root_0, string_literal124_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end remP

    public static class forallP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start forallP
    // labels.g:140:1: forallP : 'forall' ;
    public final labelsParser.forallP_return forallP() throws RecognitionException {
        labelsParser.forallP_return retval = new labelsParser.forallP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal125=null;

        Object string_literal125_tree=null;

        try {
            // labels.g:140:9: ( 'forall' )
            // labels.g:140:11: 'forall'
            {
            root_0 = (Object)adaptor.nil();

            string_literal125=(Token)match(input,63,FOLLOW_63_in_forallP1075); 
            string_literal125_tree = (Object)adaptor.create(string_literal125);
            adaptor.addChild(root_0, string_literal125_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end forallP

    public static class forallxP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start forallxP
    // labels.g:141:1: forallxP : 'forallx' ;
    public final labelsParser.forallxP_return forallxP() throws RecognitionException {
        labelsParser.forallxP_return retval = new labelsParser.forallxP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal126=null;

        Object string_literal126_tree=null;

        try {
            // labels.g:141:10: ( 'forallx' )
            // labels.g:141:12: 'forallx'
            {
            root_0 = (Object)adaptor.nil();

            string_literal126=(Token)match(input,64,FOLLOW_64_in_forallxP1082); 
            string_literal126_tree = (Object)adaptor.create(string_literal126);
            adaptor.addChild(root_0, string_literal126_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end forallxP

    public static class existsP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start existsP
    // labels.g:142:1: existsP : 'exists' ;
    public final labelsParser.existsP_return existsP() throws RecognitionException {
        labelsParser.existsP_return retval = new labelsParser.existsP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal127=null;

        Object string_literal127_tree=null;

        try {
            // labels.g:142:9: ( 'exists' )
            // labels.g:142:11: 'exists'
            {
            root_0 = (Object)adaptor.nil();

            string_literal127=(Token)match(input,65,FOLLOW_65_in_existsP1089); 
            string_literal127_tree = (Object)adaptor.create(string_literal127);
            adaptor.addChild(root_0, string_literal127_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end existsP

    public static class nestedP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start nestedP
    // labels.g:143:1: nestedP : 'nested' ;
    public final labelsParser.nestedP_return nestedP() throws RecognitionException {
        labelsParser.nestedP_return retval = new labelsParser.nestedP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal128=null;

        Object string_literal128_tree=null;

        try {
            // labels.g:143:9: ( 'nested' )
            // labels.g:143:11: 'nested'
            {
            root_0 = (Object)adaptor.nil();

            string_literal128=(Token)match(input,66,FOLLOW_66_in_nestedP1096); 
            string_literal128_tree = (Object)adaptor.create(string_literal128);
            adaptor.addChild(root_0, string_literal128_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end nestedP

    public static class parP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start parP
    // labels.g:145:1: parP : 'par' ;
    public final labelsParser.parP_return parP() throws RecognitionException {
        labelsParser.parP_return retval = new labelsParser.parP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal129=null;

        Object string_literal129_tree=null;

        try {
            // labels.g:145:9: ( 'par' )
            // labels.g:145:11: 'par'
            {
            root_0 = (Object)adaptor.nil();

            string_literal129=(Token)match(input,67,FOLLOW_67_in_parP1107); 
            string_literal129_tree = (Object)adaptor.create(string_literal129);
            adaptor.addChild(root_0, string_literal129_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end parP

    public static class attrP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start attrP
    // labels.g:147:1: attrP : 'attr' ;
    public final labelsParser.attrP_return attrP() throws RecognitionException {
        labelsParser.attrP_return retval = new labelsParser.attrP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal130=null;

        Object string_literal130_tree=null;

        try {
            // labels.g:147:9: ( 'attr' )
            // labels.g:147:11: 'attr'
            {
            root_0 = (Object)adaptor.nil();

            string_literal130=(Token)match(input,68,FOLLOW_68_in_attrP1117); 
            string_literal130_tree = (Object)adaptor.create(string_literal130);
            adaptor.addChild(root_0, string_literal130_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end attrP

    public static class prodP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start prodP
    // labels.g:148:1: prodP : 'prod' ;
    public final labelsParser.prodP_return prodP() throws RecognitionException {
        labelsParser.prodP_return retval = new labelsParser.prodP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal131=null;

        Object string_literal131_tree=null;

        try {
            // labels.g:148:9: ( 'prod' )
            // labels.g:148:11: 'prod'
            {
            root_0 = (Object)adaptor.nil();

            string_literal131=(Token)match(input,69,FOLLOW_69_in_prodP1126); 
            string_literal131_tree = (Object)adaptor.create(string_literal131);
            adaptor.addChild(root_0, string_literal131_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end prodP

    public static class argP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start argP
    // labels.g:149:1: argP : 'arg' ;
    public final labelsParser.argP_return argP() throws RecognitionException {
        labelsParser.argP_return retval = new labelsParser.argP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal132=null;

        Object string_literal132_tree=null;

        try {
            // labels.g:149:9: ( 'arg' )
            // labels.g:149:11: 'arg'
            {
            root_0 = (Object)adaptor.nil();

            string_literal132=(Token)match(input,70,FOLLOW_70_in_argP1136); 
            string_literal132_tree = (Object)adaptor.create(string_literal132);
            adaptor.addChild(root_0, string_literal132_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end argP

    public static class intP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start intP
    // labels.g:150:1: intP : 'int' ;
    public final labelsParser.intP_return intP() throws RecognitionException {
        labelsParser.intP_return retval = new labelsParser.intP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal133=null;

        Object string_literal133_tree=null;

        try {
            // labels.g:150:9: ( 'int' )
            // labels.g:150:11: 'int'
            {
            root_0 = (Object)adaptor.nil();

            string_literal133=(Token)match(input,71,FOLLOW_71_in_intP1146); 
            string_literal133_tree = (Object)adaptor.create(string_literal133);
            adaptor.addChild(root_0, string_literal133_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end intP

    public static class realP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start realP
    // labels.g:151:1: realP : 'real' ;
    public final labelsParser.realP_return realP() throws RecognitionException {
        labelsParser.realP_return retval = new labelsParser.realP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal134=null;

        Object string_literal134_tree=null;

        try {
            // labels.g:151:9: ( 'real' )
            // labels.g:151:11: 'real'
            {
            root_0 = (Object)adaptor.nil();

            string_literal134=(Token)match(input,72,FOLLOW_72_in_realP1155); 
            string_literal134_tree = (Object)adaptor.create(string_literal134);
            adaptor.addChild(root_0, string_literal134_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end realP

    public static class stringP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start stringP
    // labels.g:152:1: stringP : 'string' ;
    public final labelsParser.stringP_return stringP() throws RecognitionException {
        labelsParser.stringP_return retval = new labelsParser.stringP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal135=null;

        Object string_literal135_tree=null;

        try {
            // labels.g:152:9: ( 'string' )
            // labels.g:152:11: 'string'
            {
            root_0 = (Object)adaptor.nil();

            string_literal135=(Token)match(input,73,FOLLOW_73_in_stringP1162); 
            string_literal135_tree = (Object)adaptor.create(string_literal135);
            adaptor.addChild(root_0, string_literal135_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end stringP

    public static class boolP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start boolP
    // labels.g:153:1: boolP : 'bool' ;
    public final labelsParser.boolP_return boolP() throws RecognitionException {
        labelsParser.boolP_return retval = new labelsParser.boolP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal136=null;

        Object string_literal136_tree=null;

        try {
            // labels.g:153:9: ( 'bool' )
            // labels.g:153:11: 'bool'
            {
            root_0 = (Object)adaptor.nil();

            string_literal136=(Token)match(input,74,FOLLOW_74_in_boolP1171); 
            string_literal136_tree = (Object)adaptor.create(string_literal136);
            adaptor.addChild(root_0, string_literal136_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end boolP

    public static class typeP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start typeP
    // labels.g:155:1: typeP : 'type' ;
    public final labelsParser.typeP_return typeP() throws RecognitionException {
        labelsParser.typeP_return retval = new labelsParser.typeP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal137=null;

        Object string_literal137_tree=null;

        try {
            // labels.g:155:9: ( 'type' )
            // labels.g:155:11: 'type'
            {
            root_0 = (Object)adaptor.nil();

            string_literal137=(Token)match(input,75,FOLLOW_75_in_typeP1181); 
            string_literal137_tree = (Object)adaptor.create(string_literal137);
            adaptor.addChild(root_0, string_literal137_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end typeP

    public static class flagP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start flagP
    // labels.g:156:1: flagP : 'flag' ;
    public final labelsParser.flagP_return flagP() throws RecognitionException {
        labelsParser.flagP_return retval = new labelsParser.flagP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal138=null;

        Object string_literal138_tree=null;

        try {
            // labels.g:156:9: ( 'flag' )
            // labels.g:156:11: 'flag'
            {
            root_0 = (Object)adaptor.nil();

            string_literal138=(Token)match(input,76,FOLLOW_76_in_flagP1190); 
            string_literal138_tree = (Object)adaptor.create(string_literal138);
            adaptor.addChild(root_0, string_literal138_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end flagP

    public static class pathP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start pathP
    // labels.g:157:1: pathP : 'path' ;
    public final labelsParser.pathP_return pathP() throws RecognitionException {
        labelsParser.pathP_return retval = new labelsParser.pathP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal139=null;

        Object string_literal139_tree=null;

        try {
            // labels.g:157:9: ( 'path' )
            // labels.g:157:11: 'path'
            {
            root_0 = (Object)adaptor.nil();

            string_literal139=(Token)match(input,77,FOLLOW_77_in_pathP1199); 
            string_literal139_tree = (Object)adaptor.create(string_literal139);
            adaptor.addChild(root_0, string_literal139_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end pathP

    public static class trueP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start trueP
    // labels.g:159:1: trueP : 'true' ;
    public final labelsParser.trueP_return trueP() throws RecognitionException {
        labelsParser.trueP_return retval = new labelsParser.trueP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal140=null;

        Object string_literal140_tree=null;

        try {
            // labels.g:159:9: ( 'true' )
            // labels.g:159:11: 'true'
            {
            root_0 = (Object)adaptor.nil();

            string_literal140=(Token)match(input,78,FOLLOW_78_in_trueP1209); 
            string_literal140_tree = (Object)adaptor.create(string_literal140);
            adaptor.addChild(root_0, string_literal140_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end trueP

    public static class falseP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start falseP
    // labels.g:160:1: falseP : 'false' ;
    public final labelsParser.falseP_return falseP() throws RecognitionException {
        labelsParser.falseP_return retval = new labelsParser.falseP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal141=null;

        Object string_literal141_tree=null;

        try {
            // labels.g:160:9: ( 'false' )
            // labels.g:160:11: 'false'
            {
            root_0 = (Object)adaptor.nil();

            string_literal141=(Token)match(input,79,FOLLOW_79_in_falseP1217); 
            string_literal141_tree = (Object)adaptor.create(string_literal141);
            adaptor.addChild(root_0, string_literal141_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end falseP

    // Delegated rules


    protected DFA1 dfa1 = new DFA1(this);
    protected DFA10 dfa10 = new DFA10(this);
    protected DFA14 dfa14 = new DFA14(this);
    protected DFA18 dfa18 = new DFA18(this);
    static final String DFA1_eotS =
        "\20\uffff";
    static final String DFA1_eofS =
        "\2\uffff\12\1\1\uffff\3\1";
    static final String DFA1_minS =
        "\1\4\1\uffff\12\4\1\uffff\3\4";
    static final String DFA1_maxS =
        "\1\117\1\uffff\12\117\1\uffff\3\117";
    static final String DFA1_acceptS =
        "\1\uffff\1\2\12\uffff\1\1\3\uffff";
    static final String DFA1_specialS =
        "\20\uffff}>";
    static final String[] DFA1_transitionS = {
            "\65\1\1\5\1\6\1\11\1\7\1\10\1\1\1\2\1\3\1\4\1\12\15\1",
            "",
            "\26\1\1\13\1\1\1\14\63\1",
            "\26\1\1\13\1\1\1\14\63\1",
            "\26\1\1\13\1\1\1\14\63\1",
            "\26\1\1\15\1\1\1\14\63\1",
            "\26\1\1\15\1\1\1\14\63\1",
            "\26\1\1\15\1\1\1\14\63\1",
            "\26\1\1\15\1\1\1\14\63\1",
            "\26\1\1\15\1\1\1\14\63\1",
            "\30\1\1\14\63\1",
            "\27\1\1\16\1\uffff\63\1",
            "",
            "\27\1\1\17\1\uffff\63\1",
            "\30\1\1\14\63\1",
            "\30\1\1\14\63\1"
    };

    static final short[] DFA1_eot = DFA.unpackEncodedString(DFA1_eotS);
    static final short[] DFA1_eof = DFA.unpackEncodedString(DFA1_eofS);
    static final char[] DFA1_min = DFA.unpackEncodedStringToUnsignedChars(DFA1_minS);
    static final char[] DFA1_max = DFA.unpackEncodedStringToUnsignedChars(DFA1_maxS);
    static final short[] DFA1_accept = DFA.unpackEncodedString(DFA1_acceptS);
    static final short[] DFA1_special = DFA.unpackEncodedString(DFA1_specialS);
    static final short[][] DFA1_transition;

    static {
        int numStates = DFA1_transitionS.length;
        DFA1_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
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
            return "()* loopback of 46:3: ( prefix )*";
        }
    }
    static final String DFA10_eotS =
        "\15\uffff";
    static final String DFA10_eofS =
        "\2\uffff\7\11\4\uffff";
    static final String DFA10_minS =
        "\1\4\1\uffff\7\4\4\uffff";
    static final String DFA10_maxS =
        "\1\117\1\uffff\7\117\4\uffff";
    static final String DFA10_acceptS =
        "\1\uffff\1\1\7\uffff\1\5\1\2\1\3\1\4";
    static final String DFA10_specialS =
        "\15\uffff}>";
    static final String[] DFA10_transitionS = {
            "\30\11\1\1\41\11\1\2\10\11\1\3\1\4\1\5\1\6\1\7\1\10\3\11",
            "",
            "\30\11\1\12\63\11",
            "\30\11\1\13\63\11",
            "\30\11\1\13\63\11",
            "\30\11\1\13\63\11",
            "\30\11\1\13\63\11",
            "\30\11\1\14\63\11",
            "\30\11\1\14\63\11",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA10_eot = DFA.unpackEncodedString(DFA10_eotS);
    static final short[] DFA10_eof = DFA.unpackEncodedString(DFA10_eofS);
    static final char[] DFA10_min = DFA.unpackEncodedStringToUnsignedChars(DFA10_minS);
    static final char[] DFA10_max = DFA.unpackEncodedStringToUnsignedChars(DFA10_maxS);
    static final short[] DFA10_accept = DFA.unpackEncodedString(DFA10_acceptS);
    static final short[] DFA10_special = DFA.unpackEncodedString(DFA10_specialS);
    static final short[][] DFA10_transition;

    static {
        int numStates = DFA10_transitionS.length;
        DFA10_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA10_transition[i] = DFA.unpackEncodedString(DFA10_transitionS[i]);
        }
    }

    class DFA10 extends DFA {

        public DFA10(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 10;
            this.eot = DFA10_eot;
            this.eof = DFA10_eof;
            this.min = DFA10_min;
            this.max = DFA10_max;
            this.accept = DFA10_accept;
            this.special = DFA10_special;
            this.transition = DFA10_transition;
        }
        public String getDescription() {
            return "53:1: actualGraphLabel : ( COLON ( . )* | remP COLON ( . )* | valueLabel | nodeLabel | (~ COLON )+ );";
        }
    }
    static final String DFA14_eotS =
        "\14\uffff";
    static final String DFA14_eofS =
        "\1\1\1\uffff\11\1\1\uffff";
    static final String DFA14_minS =
        "\1\4\1\uffff\11\4\1\uffff";
    static final String DFA14_maxS =
        "\1\117\1\uffff\11\117\1\uffff";
    static final String DFA14_acceptS =
        "\1\uffff\1\2\11\uffff\1\1";
    static final String DFA14_specialS =
        "\14\uffff}>";
    static final String[] DFA14_transitionS = {
            "\37\1\1\uffff\25\1\1\5\1\6\1\11\1\7\1\10\1\1\1\2\1\3\1\4\1\12"+
            "\15\1",
            "",
            "\26\1\1\13\1\1\1\13\3\1\1\uffff\1\1\4\uffff\52\1",
            "\26\1\1\13\1\1\1\13\3\1\1\uffff\1\1\4\uffff\52\1",
            "\26\1\1\13\1\1\1\13\3\1\1\uffff\1\1\4\uffff\52\1",
            "\26\1\1\13\1\1\1\13\3\1\1\uffff\1\1\4\uffff\52\1",
            "\26\1\1\13\1\1\1\13\3\1\1\uffff\1\1\4\uffff\52\1",
            "\26\1\1\13\1\1\1\13\3\1\1\uffff\1\1\4\uffff\52\1",
            "\26\1\1\13\1\1\1\13\3\1\1\uffff\1\1\4\uffff\52\1",
            "\26\1\1\13\1\1\1\13\3\1\1\uffff\1\1\4\uffff\52\1",
            "\26\1\1\uffff\1\1\1\13\3\1\1\uffff\1\1\4\uffff\52\1",
            ""
    };

    static final short[] DFA14_eot = DFA.unpackEncodedString(DFA14_eotS);
    static final short[] DFA14_eof = DFA.unpackEncodedString(DFA14_eofS);
    static final char[] DFA14_min = DFA.unpackEncodedStringToUnsignedChars(DFA14_minS);
    static final char[] DFA14_max = DFA.unpackEncodedStringToUnsignedChars(DFA14_maxS);
    static final short[] DFA14_accept = DFA.unpackEncodedString(DFA14_acceptS);
    static final short[] DFA14_special = DFA.unpackEncodedString(DFA14_specialS);
    static final short[][] DFA14_transition;

    static {
        int numStates = DFA14_transitionS.length;
        DFA14_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
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
            return "()* loopback of 71:6: ( prefix )*";
        }
    }
    static final String DFA18_eotS =
        "\32\uffff";
    static final String DFA18_eofS =
        "\1\17\1\uffff\14\17\5\uffff\4\30\3\uffff";
    static final String DFA18_minS =
        "\1\4\1\uffff\14\4\5\uffff\4\33\3\uffff";
    static final String DFA18_maxS =
        "\1\117\1\uffff\14\117\5\uffff\1\35\1\36\1\37\1\117\3\uffff";
    static final String DFA18_acceptS =
        "\1\uffff\1\1\14\uffff\1\10\1\11\1\2\1\3\1\4\4\uffff\1\6\1\7\1\5";
    static final String DFA18_specialS =
        "\32\uffff}>";
    static final String[] DFA18_transitionS = {
            "\30\17\1\1\3\17\1\16\2\17\1\uffff\32\17\1\2\4\17\1\3\1\13\1"+
            "\14\1\15\1\5\1\6\1\7\1\10\1\11\1\12\1\4\2\17",
            "",
            "\26\17\1\uffff\1\17\1\20\3\17\1\uffff\1\17\4\uffff\52\17",
            "\26\17\1\uffff\1\17\1\21\3\17\1\uffff\1\17\4\uffff\52\17",
            "\26\17\1\uffff\1\17\1\22\3\17\1\uffff\1\17\4\uffff\52\17",
            "\26\17\1\uffff\1\17\1\23\3\17\1\uffff\1\17\4\uffff\52\17",
            "\26\17\1\uffff\1\17\1\24\3\17\1\uffff\1\17\4\uffff\52\17",
            "\26\17\1\uffff\1\17\1\25\3\17\1\uffff\1\17\4\uffff\52\17",
            "\26\17\1\uffff\1\17\1\26\3\17\1\uffff\1\17\4\uffff\52\17",
            "\26\17\1\uffff\1\17\1\27\3\17\1\uffff\1\17\4\uffff\52\17",
            "\26\17\1\uffff\1\17\1\27\3\17\1\uffff\1\17\4\uffff\52\17",
            "\26\17\1\uffff\1\17\1\30\3\17\1\uffff\1\17\4\uffff\52\17",
            "\26\17\1\uffff\1\17\1\30\3\17\1\uffff\1\17\4\uffff\52\17",
            "\26\17\1\uffff\1\17\1\30\3\17\1\uffff\1\17\4\uffff\52\17",
            "",
            "",
            "",
            "",
            "",
            "\1\30\1\uffff\1\31",
            "\1\30\2\uffff\1\31",
            "\1\30\3\uffff\1\31",
            "\1\30\62\uffff\2\31",
            "",
            "",
            ""
    };

    static final short[] DFA18_eot = DFA.unpackEncodedString(DFA18_eotS);
    static final short[] DFA18_eof = DFA.unpackEncodedString(DFA18_eofS);
    static final char[] DFA18_min = DFA.unpackEncodedStringToUnsignedChars(DFA18_minS);
    static final char[] DFA18_max = DFA.unpackEncodedStringToUnsignedChars(DFA18_maxS);
    static final short[] DFA18_accept = DFA.unpackEncodedString(DFA18_acceptS);
    static final short[] DFA18_special = DFA.unpackEncodedString(DFA18_specialS);
    static final short[][] DFA18_transition;

    static {
        int numStates = DFA18_transitionS.length;
        DFA18_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA18_transition[i] = DFA.unpackEncodedString(DFA18_transitionS[i]);
        }
    }

    class DFA18 extends DFA {

        public DFA18(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 18;
            this.eot = DFA18_eot;
            this.eof = DFA18_eof;
            this.min = DFA18_min;
            this.max = DFA18_max;
            this.accept = DFA18_accept;
            this.special = DFA18_special;
            this.transition = DFA18_transition;
        }
        public String getDescription() {
            return "73:1: actualRuleLabel : ( COLON ( . )* | remP COLON ( . )* | parP COLON | pathP COLON ( PLING )? regExpr | valueLabel | nodeLabel | attrLabel | negLabel | posLabel );";
        }
    }
 

    public static final BitSet FOLLOW_prefix_in_graphLabel177 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_actualGraphLabel_in_graphLabel180 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_forallP_in_prefix194 = new BitSet(new long[]{0x0000000014000000L});
    public static final BitSet FOLLOW_forallxP_in_prefix198 = new BitSet(new long[]{0x0000000014000000L});
    public static final BitSet FOLLOW_existsP_in_prefix202 = new BitSet(new long[]{0x0000000014000000L});
    public static final BitSet FOLLOW_EQUALS_in_prefix207 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_IDENT_in_prefix209 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_prefix213 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_newP_in_prefix223 = new BitSet(new long[]{0x0000000014000000L});
    public static final BitSet FOLLOW_delP_in_prefix227 = new BitSet(new long[]{0x0000000014000000L});
    public static final BitSet FOLLOW_notP_in_prefix231 = new BitSet(new long[]{0x0000000014000000L});
    public static final BitSet FOLLOW_useP_in_prefix235 = new BitSet(new long[]{0x0000000014000000L});
    public static final BitSet FOLLOW_cnewP_in_prefix239 = new BitSet(new long[]{0x0000000014000000L});
    public static final BitSet FOLLOW_EQUALS_in_prefix244 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_IDENT_in_prefix246 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_prefix250 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nestedP_in_prefix258 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_prefix260 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_actualGraphLabel271 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_remP_in_actualGraphLabel281 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_actualGraphLabel283 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_valueLabel_in_actualGraphLabel293 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nodeLabel_in_actualGraphLabel300 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_actualGraphLabel308 = new BitSet(new long[]{0xFFFFFFFFEFFFFFF2L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_intP_in_valueLabel326 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_valueLabel328 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_NUMBER_in_valueLabel330 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_realP_in_valueLabel345 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_valueLabel347 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_RNUMBER_in_valueLabel349 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_stringP_in_valueLabel364 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_valueLabel366 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_DQTEXT_in_valueLabel368 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_boolP_in_valueLabel383 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_valueLabel385 = new BitSet(new long[]{0x0000000000000000L,0x000000000000C000L});
    public static final BitSet FOLLOW_trueP_in_valueLabel388 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_falseP_in_valueLabel392 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeP_in_nodeLabel407 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_nodeLabel409 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_IDENT_in_nodeLabel411 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_flagP_in_nodeLabel426 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_nodeLabel428 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_IDENT_in_nodeLabel430 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_prefix_in_ruleLabel449 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_actualRuleLabel_in_ruleLabel452 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_actualRuleLabel467 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_remP_in_actualRuleLabel477 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_actualRuleLabel479 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_parP_in_actualRuleLabel489 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_actualRuleLabel491 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathP_in_actualRuleLabel498 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_actualRuleLabel500 = new BitSet(new long[]{0x0000193104000000L});
    public static final BitSet FOLLOW_PLING_in_actualRuleLabel502 = new BitSet(new long[]{0x0000193104000000L});
    public static final BitSet FOLLOW_regExpr_in_actualRuleLabel505 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_valueLabel_in_actualRuleLabel512 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nodeLabel_in_actualRuleLabel519 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attrLabel_in_actualRuleLabel526 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_negLabel_in_actualRuleLabel533 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_posLabel_in_actualRuleLabel540 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_intP_in_attrLabel555 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_attrLabel557 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_IDENT_in_attrLabel559 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_realP_in_attrLabel575 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_attrLabel577 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_IDENT_in_attrLabel579 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_stringP_in_attrLabel595 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_attrLabel597 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_IDENT_in_attrLabel599 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_boolP_in_attrLabel615 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_attrLabel617 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_IDENT_in_attrLabel619 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attrP_in_attrLabel635 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_attrLabel637 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_prodP_in_attrLabel652 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_attrLabel654 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_argP_in_attrLabel669 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_attrLabel671 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_DIGIT_in_attrLabel673 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_PLING_in_negLabel693 = new BitSet(new long[]{0xFFFFFFF6EFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_posLabel_in_negLabel695 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_wildcard_in_posLabel710 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_posLabel717 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_posLabel724 = new BitSet(new long[]{0x0000193104000000L});
    public static final BitSet FOLLOW_regExpr_in_posLabel726 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_RBRACE_in_posLabel728 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SQTEXT_in_posLabel735 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_posLabel743 = new BitSet(new long[]{0xFFFFFFC2EBFFFFF2L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_choice_in_regExpr775 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sequence_in_choice787 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_BAR_in_choice790 = new BitSet(new long[]{0x0000193104000000L});
    public static final BitSet FOLLOW_choice_in_choice793 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unary_in_sequence807 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_DOT_in_sequence810 = new BitSet(new long[]{0x0000193104000000L});
    public static final BitSet FOLLOW_sequence_in_sequence813 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_unary827 = new BitSet(new long[]{0x0000193104000000L});
    public static final BitSet FOLLOW_unary_in_unary829 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_unary836 = new BitSet(new long[]{0x0000060000000002L});
    public static final BitSet FOLLOW_STAR_in_unary839 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_unary844 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SQTEXT_in_atom859 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTCHAR_in_atom866 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_EQUALS_in_atom874 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_atom881 = new BitSet(new long[]{0x0000193104000000L});
    public static final BitSet FOLLOW_regExpr_in_atom883 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RPAR_in_atom885 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_wildcard_in_atom892 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUERY_in_wildcard904 = new BitSet(new long[]{0x0000400008000000L});
    public static final BitSet FOLLOW_IDENT_in_wildcard906 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_LSQUARE_in_wildcard909 = new BitSet(new long[]{0x0000993104000000L});
    public static final BitSet FOLLOW_HAT_in_wildcard911 = new BitSet(new long[]{0x0000993104000000L});
    public static final BitSet FOLLOW_atom_in_wildcard914 = new BitSet(new long[]{0x0003000000000000L});
    public static final BitSet FOLLOW_COMMA_in_wildcard917 = new BitSet(new long[]{0x0000993104000000L});
    public static final BitSet FOLLOW_atom_in_wildcard919 = new BitSet(new long[]{0x0003000000000000L});
    public static final BitSet FOLLOW_RSQUARE_in_wildcard923 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_57_in_newP1018 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_58_in_delP1028 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_59_in_cnewP1037 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_60_in_notP1047 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_61_in_useP1057 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_62_in_remP1067 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_63_in_forallP1075 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_64_in_forallxP1082 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_existsP1089 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_nestedP1096 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_67_in_parP1107 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_68_in_attrP1117 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_69_in_prodP1126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_70_in_argP1136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_71_in_intP1146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_72_in_realP1155 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_73_in_stringP1162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_74_in_boolP1171 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_75_in_typeP1181 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_76_in_flagP1190 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_77_in_pathP1199 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_78_in_trueP1209 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_79_in_falseP1217 = new BitSet(new long[]{0x0000000000000002L});

}
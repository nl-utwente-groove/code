// $ANTLR 3.1b1 Label.g 2010-04-23 10:01:15

package groove.view.parse;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;

@SuppressWarnings("all")              
public class LabelParser extends Parser {
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


        public LabelParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public LabelParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return LabelParser.tokenNames; }
    public String getGrammarFileName() { return "Label.g"; }

    
        private boolean isGraph;
        public void setIsGraph(boolean isGraph) {
            this.isGraph = isGraph;
        }


    public static class label_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start label
    // Label.g:52:1: label : ({...}? => graphLabel | {...}? => ruleLabel );
    public final LabelParser.label_return label() throws RecognitionException {
        LabelParser.label_return retval = new LabelParser.label_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        LabelParser.graphLabel_return graphLabel1 = null;

        LabelParser.ruleLabel_return ruleLabel2 = null;



        try {
            // Label.g:53:4: ({...}? => graphLabel | {...}? => ruleLabel )
            int alt1=2;
            alt1 = dfa1.predict(input);
            switch (alt1) {
                case 1 :
                    // Label.g:53:6: {...}? => graphLabel
                    {
                    root_0 = (Object)adaptor.nil();

                    if ( !( isGraph ) ) {
                        throw new FailedPredicateException(input, "label", " isGraph ");
                    }
                    pushFollow(FOLLOW_graphLabel_in_label188);
                    graphLabel1=graphLabel();

                    state._fsp--;

                    adaptor.addChild(root_0, graphLabel1.getTree());

                    }
                    break;
                case 2 :
                    // Label.g:54:6: {...}? => ruleLabel
                    {
                    root_0 = (Object)adaptor.nil();

                    if ( !( !isGraph ) ) {
                        throw new FailedPredicateException(input, "label", " !isGraph ");
                    }
                    pushFollow(FOLLOW_ruleLabel_in_label199);
                    ruleLabel2=ruleLabel();

                    state._fsp--;

                    adaptor.addChild(root_0, ruleLabel2.getTree());

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
    // $ANTLR end label

    public static class graphLabel_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start graphLabel
    // Label.g:57:1: graphLabel : ( prefix )* actualGraphLabel ;
    public final LabelParser.graphLabel_return graphLabel() throws RecognitionException {
        LabelParser.graphLabel_return retval = new LabelParser.graphLabel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        LabelParser.prefix_return prefix3 = null;

        LabelParser.actualGraphLabel_return actualGraphLabel4 = null;



        try {
            // Label.g:57:12: ( ( prefix )* actualGraphLabel )
            // Label.g:58:3: ( prefix )* actualGraphLabel
            {
            root_0 = (Object)adaptor.nil();

            // Label.g:58:3: ( prefix )*
            loop2:
            do {
                int alt2=2;
                alt2 = dfa2.predict(input);
                switch (alt2) {
            	case 1 :
            	    // Label.g:58:3: prefix
            	    {
            	    pushFollow(FOLLOW_prefix_in_graphLabel216);
            	    prefix3=prefix();

            	    state._fsp--;

            	    adaptor.addChild(root_0, prefix3.getTree());

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            pushFollow(FOLLOW_actualGraphLabel_in_graphLabel219);
            actualGraphLabel4=actualGraphLabel();

            state._fsp--;

            adaptor.addChild(root_0, actualGraphLabel4.getTree());

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
    // Label.g:60:1: prefix : ( ( forallP | forallxP | existsP ) ( EQUALS IDENT )? COLON | ( newP | delP | notP | useP | cnewP ) ( EQUALS IDENT )? COLON | nestedP COLON );
    public final LabelParser.prefix_return prefix() throws RecognitionException {
        LabelParser.prefix_return retval = new LabelParser.prefix_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQUALS8=null;
        Token IDENT9=null;
        Token COLON10=null;
        Token EQUALS16=null;
        Token IDENT17=null;
        Token COLON18=null;
        Token COLON20=null;
        LabelParser.forallP_return forallP5 = null;

        LabelParser.forallxP_return forallxP6 = null;

        LabelParser.existsP_return existsP7 = null;

        LabelParser.newP_return newP11 = null;

        LabelParser.delP_return delP12 = null;

        LabelParser.notP_return notP13 = null;

        LabelParser.useP_return useP14 = null;

        LabelParser.cnewP_return cnewP15 = null;

        LabelParser.nestedP_return nestedP19 = null;


        Object EQUALS8_tree=null;
        Object IDENT9_tree=null;
        Object COLON10_tree=null;
        Object EQUALS16_tree=null;
        Object IDENT17_tree=null;
        Object COLON18_tree=null;
        Object COLON20_tree=null;

        try {
            // Label.g:61:4: ( ( forallP | forallxP | existsP ) ( EQUALS IDENT )? COLON | ( newP | delP | notP | useP | cnewP ) ( EQUALS IDENT )? COLON | nestedP COLON )
            int alt7=3;
            switch ( input.LA(1) ) {
            case 63:
            case 64:
            case 65:
                {
                alt7=1;
                }
                break;
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
                {
                alt7=2;
                }
                break;
            case 66:
                {
                alt7=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }

            switch (alt7) {
                case 1 :
                    // Label.g:61:6: ( forallP | forallxP | existsP ) ( EQUALS IDENT )? COLON
                    {
                    root_0 = (Object)adaptor.nil();

                    // Label.g:61:6: ( forallP | forallxP | existsP )
                    int alt3=3;
                    switch ( input.LA(1) ) {
                    case 63:
                        {
                        alt3=1;
                        }
                        break;
                    case 64:
                        {
                        alt3=2;
                        }
                        break;
                    case 65:
                        {
                        alt3=3;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 3, 0, input);

                        throw nvae;
                    }

                    switch (alt3) {
                        case 1 :
                            // Label.g:61:8: forallP
                            {
                            pushFollow(FOLLOW_forallP_in_prefix233);
                            forallP5=forallP();

                            state._fsp--;


                            }
                            break;
                        case 2 :
                            // Label.g:61:19: forallxP
                            {
                            pushFollow(FOLLOW_forallxP_in_prefix238);
                            forallxP6=forallxP();

                            state._fsp--;

                            adaptor.addChild(root_0, forallxP6.getTree());

                            }
                            break;
                        case 3 :
                            // Label.g:61:30: existsP
                            {
                            pushFollow(FOLLOW_existsP_in_prefix242);
                            existsP7=existsP();

                            state._fsp--;

                            adaptor.addChild(root_0, existsP7.getTree());

                            }
                            break;

                    }

                    // Label.g:61:40: ( EQUALS IDENT )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==EQUALS) ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // Label.g:61:41: EQUALS IDENT
                            {
                            EQUALS8=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_prefix247); 
                            EQUALS8_tree = (Object)adaptor.create(EQUALS8);
                            adaptor.addChild(root_0, EQUALS8_tree);

                            IDENT9=(Token)match(input,IDENT,FOLLOW_IDENT_in_prefix249); 
                            IDENT9_tree = (Object)adaptor.create(IDENT9);
                            adaptor.addChild(root_0, IDENT9_tree);


                            }
                            break;

                    }

                    COLON10=(Token)match(input,COLON,FOLLOW_COLON_in_prefix253); 
                    COLON10_tree = (Object)adaptor.create(COLON10);
                    adaptor.addChild(root_0, COLON10_tree);


                    }
                    break;
                case 2 :
                    // Label.g:62:6: ( newP | delP | notP | useP | cnewP ) ( EQUALS IDENT )? COLON
                    {
                    root_0 = (Object)adaptor.nil();

                    // Label.g:62:6: ( newP | delP | notP | useP | cnewP )
                    int alt5=5;
                    switch ( input.LA(1) ) {
                    case 57:
                        {
                        alt5=1;
                        }
                        break;
                    case 58:
                        {
                        alt5=2;
                        }
                        break;
                    case 60:
                        {
                        alt5=3;
                        }
                        break;
                    case 61:
                        {
                        alt5=4;
                        }
                        break;
                    case 59:
                        {
                        alt5=5;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 5, 0, input);

                        throw nvae;
                    }

                    switch (alt5) {
                        case 1 :
                            // Label.g:62:8: newP
                            {
                            pushFollow(FOLLOW_newP_in_prefix263);
                            newP11=newP();

                            state._fsp--;

                            adaptor.addChild(root_0, newP11.getTree());

                            }
                            break;
                        case 2 :
                            // Label.g:62:15: delP
                            {
                            pushFollow(FOLLOW_delP_in_prefix267);
                            delP12=delP();

                            state._fsp--;

                            adaptor.addChild(root_0, delP12.getTree());

                            }
                            break;
                        case 3 :
                            // Label.g:62:22: notP
                            {
                            pushFollow(FOLLOW_notP_in_prefix271);
                            notP13=notP();

                            state._fsp--;

                            adaptor.addChild(root_0, notP13.getTree());

                            }
                            break;
                        case 4 :
                            // Label.g:62:29: useP
                            {
                            pushFollow(FOLLOW_useP_in_prefix275);
                            useP14=useP();

                            state._fsp--;

                            adaptor.addChild(root_0, useP14.getTree());

                            }
                            break;
                        case 5 :
                            // Label.g:62:36: cnewP
                            {
                            pushFollow(FOLLOW_cnewP_in_prefix279);
                            cnewP15=cnewP();

                            state._fsp--;

                            adaptor.addChild(root_0, cnewP15.getTree());

                            }
                            break;

                    }

                    // Label.g:62:44: ( EQUALS IDENT )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==EQUALS) ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // Label.g:62:45: EQUALS IDENT
                            {
                            EQUALS16=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_prefix284); 
                            EQUALS16_tree = (Object)adaptor.create(EQUALS16);
                            adaptor.addChild(root_0, EQUALS16_tree);

                            IDENT17=(Token)match(input,IDENT,FOLLOW_IDENT_in_prefix286); 
                            IDENT17_tree = (Object)adaptor.create(IDENT17);
                            adaptor.addChild(root_0, IDENT17_tree);


                            }
                            break;

                    }

                    COLON18=(Token)match(input,COLON,FOLLOW_COLON_in_prefix290); 
                    COLON18_tree = (Object)adaptor.create(COLON18);
                    adaptor.addChild(root_0, COLON18_tree);


                    }
                    break;
                case 3 :
                    // Label.g:63:6: nestedP COLON
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_nestedP_in_prefix298);
                    nestedP19=nestedP();

                    state._fsp--;

                    adaptor.addChild(root_0, nestedP19.getTree());
                    COLON20=(Token)match(input,COLON,FOLLOW_COLON_in_prefix300); 
                    COLON20_tree = (Object)adaptor.create(COLON20);
                    adaptor.addChild(root_0, COLON20_tree);


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
    // Label.g:65:1: actualGraphLabel : ( COLON ( . )* | remP COLON ( . )* | valueLabel | nodeLabel | (~ COLON )+ );
    public final LabelParser.actualGraphLabel_return actualGraphLabel() throws RecognitionException {
        LabelParser.actualGraphLabel_return retval = new LabelParser.actualGraphLabel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLON21=null;
        Token wildcard22=null;
        Token COLON24=null;
        Token wildcard25=null;
        Token set28=null;
        LabelParser.remP_return remP23 = null;

        LabelParser.valueLabel_return valueLabel26 = null;

        LabelParser.nodeLabel_return nodeLabel27 = null;


        Object COLON21_tree=null;
        Object wildcard22_tree=null;
        Object COLON24_tree=null;
        Object wildcard25_tree=null;
        Object set28_tree=null;

        try {
            // Label.g:66:4: ( COLON ( . )* | remP COLON ( . )* | valueLabel | nodeLabel | (~ COLON )+ )
            int alt11=5;
            alt11 = dfa11.predict(input);
            switch (alt11) {
                case 1 :
                    // Label.g:66:6: COLON ( . )*
                    {
                    root_0 = (Object)adaptor.nil();

                    COLON21=(Token)match(input,COLON,FOLLOW_COLON_in_actualGraphLabel311); 
                    COLON21_tree = (Object)adaptor.create(COLON21);
                    adaptor.addChild(root_0, COLON21_tree);

                    // Label.g:66:12: ( . )*
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
                    	    // Label.g:66:12: .
                    	    {
                    	    wildcard22=(Token)input.LT(1);
                    	    matchAny(input); 
                    	    wildcard22_tree = (Object)adaptor.create(wildcard22);
                    	    adaptor.addChild(root_0, wildcard22_tree);


                    	    }
                    	    break;

                    	default :
                    	    break loop8;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // Label.g:67:6: remP COLON ( . )*
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_remP_in_actualGraphLabel321);
                    remP23=remP();

                    state._fsp--;

                    adaptor.addChild(root_0, remP23.getTree());
                    COLON24=(Token)match(input,COLON,FOLLOW_COLON_in_actualGraphLabel323); 
                    COLON24_tree = (Object)adaptor.create(COLON24);
                    adaptor.addChild(root_0, COLON24_tree);

                    // Label.g:67:17: ( . )*
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( ((LA9_0>=NEW && LA9_0<=79)) ) {
                            alt9=1;
                        }
                        else if ( (LA9_0==EOF) ) {
                            alt9=2;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // Label.g:67:17: .
                    	    {
                    	    wildcard25=(Token)input.LT(1);
                    	    matchAny(input); 
                    	    wildcard25_tree = (Object)adaptor.create(wildcard25);
                    	    adaptor.addChild(root_0, wildcard25_tree);


                    	    }
                    	    break;

                    	default :
                    	    break loop9;
                        }
                    } while (true);


                    }
                    break;
                case 3 :
                    // Label.g:68:6: valueLabel
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_valueLabel_in_actualGraphLabel333);
                    valueLabel26=valueLabel();

                    state._fsp--;

                    adaptor.addChild(root_0, valueLabel26.getTree());

                    }
                    break;
                case 4 :
                    // Label.g:69:6: nodeLabel
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_nodeLabel_in_actualGraphLabel340);
                    nodeLabel27=nodeLabel();

                    state._fsp--;

                    adaptor.addChild(root_0, nodeLabel27.getTree());

                    }
                    break;
                case 5 :
                    // Label.g:70:6: (~ COLON )+
                    {
                    root_0 = (Object)adaptor.nil();

                    // Label.g:70:6: (~ COLON )+
                    int cnt10=0;
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( ((LA10_0>=NEW && LA10_0<=IDENT)||(LA10_0>=NUMBER && LA10_0<=79)) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // Label.g:70:7: ~ COLON
                    	    {
                    	    set28=(Token)input.LT(1);
                    	    if ( (input.LA(1)>=NEW && input.LA(1)<=IDENT)||(input.LA(1)>=NUMBER && input.LA(1)<=79) ) {
                    	        input.consume();
                    	        adaptor.addChild(root_0, (Object)adaptor.create(set28));
                    	        state.errorRecovery=false;
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt10 >= 1 ) break loop10;
                                EarlyExitException eee =
                                    new EarlyExitException(10, input);
                                throw eee;
                        }
                        cnt10++;
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
    // Label.g:72:1: valueLabel : ( intP COLON NUMBER -> ^( INT NUMBER ) | realP COLON RNUMBER -> ^( REAL RNUMBER ) | stringP COLON DQTEXT -> ^( STRING DQTEXT ) | boolP COLON ( trueP | falseP ) );
    public final LabelParser.valueLabel_return valueLabel() throws RecognitionException {
        LabelParser.valueLabel_return retval = new LabelParser.valueLabel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLON30=null;
        Token NUMBER31=null;
        Token COLON33=null;
        Token RNUMBER34=null;
        Token COLON36=null;
        Token DQTEXT37=null;
        Token COLON39=null;
        LabelParser.intP_return intP29 = null;

        LabelParser.realP_return realP32 = null;

        LabelParser.stringP_return stringP35 = null;

        LabelParser.boolP_return boolP38 = null;

        LabelParser.trueP_return trueP40 = null;

        LabelParser.falseP_return falseP41 = null;


        Object COLON30_tree=null;
        Object NUMBER31_tree=null;
        Object COLON33_tree=null;
        Object RNUMBER34_tree=null;
        Object COLON36_tree=null;
        Object DQTEXT37_tree=null;
        Object COLON39_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_RNUMBER=new RewriteRuleTokenStream(adaptor,"token RNUMBER");
        RewriteRuleTokenStream stream_DQTEXT=new RewriteRuleTokenStream(adaptor,"token DQTEXT");
        RewriteRuleTokenStream stream_NUMBER=new RewriteRuleTokenStream(adaptor,"token NUMBER");
        RewriteRuleSubtreeStream stream_realP=new RewriteRuleSubtreeStream(adaptor,"rule realP");
        RewriteRuleSubtreeStream stream_stringP=new RewriteRuleSubtreeStream(adaptor,"rule stringP");
        RewriteRuleSubtreeStream stream_intP=new RewriteRuleSubtreeStream(adaptor,"rule intP");
        try {
            // Label.g:73:4: ( intP COLON NUMBER -> ^( INT NUMBER ) | realP COLON RNUMBER -> ^( REAL RNUMBER ) | stringP COLON DQTEXT -> ^( STRING DQTEXT ) | boolP COLON ( trueP | falseP ) )
            int alt13=4;
            switch ( input.LA(1) ) {
            case 71:
                {
                alt13=1;
                }
                break;
            case 72:
                {
                alt13=2;
                }
                break;
            case 73:
                {
                alt13=3;
                }
                break;
            case 74:
                {
                alt13=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;
            }

            switch (alt13) {
                case 1 :
                    // Label.g:73:6: intP COLON NUMBER
                    {
                    pushFollow(FOLLOW_intP_in_valueLabel366);
                    intP29=intP();

                    state._fsp--;

                    stream_intP.add(intP29.getTree());
                    COLON30=(Token)match(input,COLON,FOLLOW_COLON_in_valueLabel368);  
                    stream_COLON.add(COLON30);

                    NUMBER31=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_valueLabel370);  
                    stream_NUMBER.add(NUMBER31);



                    // AST REWRITE
                    // elements: NUMBER
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 73:24: -> ^( INT NUMBER )
                    {
                        // Label.g:73:27: ^( INT NUMBER )
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
                    // Label.g:74:6: realP COLON RNUMBER
                    {
                    pushFollow(FOLLOW_realP_in_valueLabel385);
                    realP32=realP();

                    state._fsp--;

                    stream_realP.add(realP32.getTree());
                    COLON33=(Token)match(input,COLON,FOLLOW_COLON_in_valueLabel387);  
                    stream_COLON.add(COLON33);

                    RNUMBER34=(Token)match(input,RNUMBER,FOLLOW_RNUMBER_in_valueLabel389);  
                    stream_RNUMBER.add(RNUMBER34);



                    // AST REWRITE
                    // elements: RNUMBER
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 74:26: -> ^( REAL RNUMBER )
                    {
                        // Label.g:74:29: ^( REAL RNUMBER )
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
                    // Label.g:75:6: stringP COLON DQTEXT
                    {
                    pushFollow(FOLLOW_stringP_in_valueLabel404);
                    stringP35=stringP();

                    state._fsp--;

                    stream_stringP.add(stringP35.getTree());
                    COLON36=(Token)match(input,COLON,FOLLOW_COLON_in_valueLabel406);  
                    stream_COLON.add(COLON36);

                    DQTEXT37=(Token)match(input,DQTEXT,FOLLOW_DQTEXT_in_valueLabel408);  
                    stream_DQTEXT.add(DQTEXT37);



                    // AST REWRITE
                    // elements: DQTEXT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 75:27: -> ^( STRING DQTEXT )
                    {
                        // Label.g:75:30: ^( STRING DQTEXT )
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
                    // Label.g:76:6: boolP COLON ( trueP | falseP )
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_boolP_in_valueLabel423);
                    boolP38=boolP();

                    state._fsp--;

                    adaptor.addChild(root_0, boolP38.getTree());
                    COLON39=(Token)match(input,COLON,FOLLOW_COLON_in_valueLabel425); 
                    COLON39_tree = (Object)adaptor.create(COLON39);
                    adaptor.addChild(root_0, COLON39_tree);

                    // Label.g:76:18: ( trueP | falseP )
                    int alt12=2;
                    int LA12_0 = input.LA(1);

                    if ( (LA12_0==78) ) {
                        alt12=1;
                    }
                    else if ( (LA12_0==79) ) {
                        alt12=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 12, 0, input);

                        throw nvae;
                    }
                    switch (alt12) {
                        case 1 :
                            // Label.g:76:19: trueP
                            {
                            pushFollow(FOLLOW_trueP_in_valueLabel428);
                            trueP40=trueP();

                            state._fsp--;

                            adaptor.addChild(root_0, trueP40.getTree());

                            }
                            break;
                        case 2 :
                            // Label.g:76:27: falseP
                            {
                            pushFollow(FOLLOW_falseP_in_valueLabel432);
                            falseP41=falseP();

                            state._fsp--;

                            adaptor.addChild(root_0, falseP41.getTree());

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
    // Label.g:78:1: nodeLabel : ( typeP COLON IDENT -> ^( TYPE IDENT ) | flagP COLON IDENT -> ^( FLAG IDENT ) );
    public final LabelParser.nodeLabel_return nodeLabel() throws RecognitionException {
        LabelParser.nodeLabel_return retval = new LabelParser.nodeLabel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLON43=null;
        Token IDENT44=null;
        Token COLON46=null;
        Token IDENT47=null;
        LabelParser.typeP_return typeP42 = null;

        LabelParser.flagP_return flagP45 = null;


        Object COLON43_tree=null;
        Object IDENT44_tree=null;
        Object COLON46_tree=null;
        Object IDENT47_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_IDENT=new RewriteRuleTokenStream(adaptor,"token IDENT");
        RewriteRuleSubtreeStream stream_typeP=new RewriteRuleSubtreeStream(adaptor,"rule typeP");
        RewriteRuleSubtreeStream stream_flagP=new RewriteRuleSubtreeStream(adaptor,"rule flagP");
        try {
            // Label.g:79:4: ( typeP COLON IDENT -> ^( TYPE IDENT ) | flagP COLON IDENT -> ^( FLAG IDENT ) )
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==75) ) {
                alt14=1;
            }
            else if ( (LA14_0==76) ) {
                alt14=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // Label.g:79:6: typeP COLON IDENT
                    {
                    pushFollow(FOLLOW_typeP_in_nodeLabel447);
                    typeP42=typeP();

                    state._fsp--;

                    stream_typeP.add(typeP42.getTree());
                    COLON43=(Token)match(input,COLON,FOLLOW_COLON_in_nodeLabel449);  
                    stream_COLON.add(COLON43);

                    IDENT44=(Token)match(input,IDENT,FOLLOW_IDENT_in_nodeLabel451);  
                    stream_IDENT.add(IDENT44);



                    // AST REWRITE
                    // elements: IDENT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 79:24: -> ^( TYPE IDENT )
                    {
                        // Label.g:79:27: ^( TYPE IDENT )
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
                    // Label.g:80:6: flagP COLON IDENT
                    {
                    pushFollow(FOLLOW_flagP_in_nodeLabel466);
                    flagP45=flagP();

                    state._fsp--;

                    stream_flagP.add(flagP45.getTree());
                    COLON46=(Token)match(input,COLON,FOLLOW_COLON_in_nodeLabel468);  
                    stream_COLON.add(COLON46);

                    IDENT47=(Token)match(input,IDENT,FOLLOW_IDENT_in_nodeLabel470);  
                    stream_IDENT.add(IDENT47);



                    // AST REWRITE
                    // elements: IDENT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 80:24: -> ^( FLAG IDENT )
                    {
                        // Label.g:80:27: ^( FLAG IDENT )
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
    // Label.g:82:1: ruleLabel : ( prefix )* actualRuleLabel ;
    public final LabelParser.ruleLabel_return ruleLabel() throws RecognitionException {
        LabelParser.ruleLabel_return retval = new LabelParser.ruleLabel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        LabelParser.prefix_return prefix48 = null;

        LabelParser.actualRuleLabel_return actualRuleLabel49 = null;



        try {
            // Label.g:83:4: ( ( prefix )* actualRuleLabel )
            // Label.g:83:6: ( prefix )* actualRuleLabel
            {
            root_0 = (Object)adaptor.nil();

            // Label.g:83:6: ( prefix )*
            loop15:
            do {
                int alt15=2;
                alt15 = dfa15.predict(input);
                switch (alt15) {
            	case 1 :
            	    // Label.g:83:6: prefix
            	    {
            	    pushFollow(FOLLOW_prefix_in_ruleLabel489);
            	    prefix48=prefix();

            	    state._fsp--;

            	    adaptor.addChild(root_0, prefix48.getTree());

            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);

            pushFollow(FOLLOW_actualRuleLabel_in_ruleLabel492);
            actualRuleLabel49=actualRuleLabel();

            state._fsp--;

            adaptor.addChild(root_0, actualRuleLabel49.getTree());

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
    // Label.g:85:1: actualRuleLabel : ( COLON ( . )* | remP COLON ( . )* | parP COLON | pathP COLON ( PLING )? regExpr | valueLabel | nodeLabel | attrLabel | negLabel | posLabel );
    public final LabelParser.actualRuleLabel_return actualRuleLabel() throws RecognitionException {
        LabelParser.actualRuleLabel_return retval = new LabelParser.actualRuleLabel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLON50=null;
        Token wildcard51=null;
        Token COLON53=null;
        Token wildcard54=null;
        Token COLON56=null;
        Token COLON58=null;
        Token PLING59=null;
        LabelParser.remP_return remP52 = null;

        LabelParser.parP_return parP55 = null;

        LabelParser.pathP_return pathP57 = null;

        LabelParser.regExpr_return regExpr60 = null;

        LabelParser.valueLabel_return valueLabel61 = null;

        LabelParser.nodeLabel_return nodeLabel62 = null;

        LabelParser.attrLabel_return attrLabel63 = null;

        LabelParser.negLabel_return negLabel64 = null;

        LabelParser.posLabel_return posLabel65 = null;


        Object COLON50_tree=null;
        Object wildcard51_tree=null;
        Object COLON53_tree=null;
        Object wildcard54_tree=null;
        Object COLON56_tree=null;
        Object COLON58_tree=null;
        Object PLING59_tree=null;

        try {
            // Label.g:86:4: ( COLON ( . )* | remP COLON ( . )* | parP COLON | pathP COLON ( PLING )? regExpr | valueLabel | nodeLabel | attrLabel | negLabel | posLabel )
            int alt19=9;
            alt19 = dfa19.predict(input);
            switch (alt19) {
                case 1 :
                    // Label.g:86:6: COLON ( . )*
                    {
                    root_0 = (Object)adaptor.nil();

                    COLON50=(Token)match(input,COLON,FOLLOW_COLON_in_actualRuleLabel507); 
                    COLON50_tree = (Object)adaptor.create(COLON50);
                    adaptor.addChild(root_0, COLON50_tree);

                    // Label.g:86:12: ( . )*
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
                    	    // Label.g:86:12: .
                    	    {
                    	    wildcard51=(Token)input.LT(1);
                    	    matchAny(input); 
                    	    wildcard51_tree = (Object)adaptor.create(wildcard51);
                    	    adaptor.addChild(root_0, wildcard51_tree);


                    	    }
                    	    break;

                    	default :
                    	    break loop16;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // Label.g:87:6: remP COLON ( . )*
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_remP_in_actualRuleLabel517);
                    remP52=remP();

                    state._fsp--;

                    adaptor.addChild(root_0, remP52.getTree());
                    COLON53=(Token)match(input,COLON,FOLLOW_COLON_in_actualRuleLabel519); 
                    COLON53_tree = (Object)adaptor.create(COLON53);
                    adaptor.addChild(root_0, COLON53_tree);

                    // Label.g:87:17: ( . )*
                    loop17:
                    do {
                        int alt17=2;
                        int LA17_0 = input.LA(1);

                        if ( ((LA17_0>=NEW && LA17_0<=79)) ) {
                            alt17=1;
                        }
                        else if ( (LA17_0==EOF) ) {
                            alt17=2;
                        }


                        switch (alt17) {
                    	case 1 :
                    	    // Label.g:87:17: .
                    	    {
                    	    wildcard54=(Token)input.LT(1);
                    	    matchAny(input); 
                    	    wildcard54_tree = (Object)adaptor.create(wildcard54);
                    	    adaptor.addChild(root_0, wildcard54_tree);


                    	    }
                    	    break;

                    	default :
                    	    break loop17;
                        }
                    } while (true);


                    }
                    break;
                case 3 :
                    // Label.g:88:6: parP COLON
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_parP_in_actualRuleLabel529);
                    parP55=parP();

                    state._fsp--;

                    adaptor.addChild(root_0, parP55.getTree());
                    COLON56=(Token)match(input,COLON,FOLLOW_COLON_in_actualRuleLabel531); 
                    COLON56_tree = (Object)adaptor.create(COLON56);
                    adaptor.addChild(root_0, COLON56_tree);


                    }
                    break;
                case 4 :
                    // Label.g:89:6: pathP COLON ( PLING )? regExpr
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_pathP_in_actualRuleLabel538);
                    pathP57=pathP();

                    state._fsp--;

                    adaptor.addChild(root_0, pathP57.getTree());
                    COLON58=(Token)match(input,COLON,FOLLOW_COLON_in_actualRuleLabel540); 
                    COLON58_tree = (Object)adaptor.create(COLON58);
                    adaptor.addChild(root_0, COLON58_tree);

                    // Label.g:89:18: ( PLING )?
                    int alt18=2;
                    int LA18_0 = input.LA(1);

                    if ( (LA18_0==PLING) ) {
                        alt18=1;
                    }
                    switch (alt18) {
                        case 1 :
                            // Label.g:89:18: PLING
                            {
                            PLING59=(Token)match(input,PLING,FOLLOW_PLING_in_actualRuleLabel542); 
                            PLING59_tree = (Object)adaptor.create(PLING59);
                            adaptor.addChild(root_0, PLING59_tree);


                            }
                            break;

                    }

                    pushFollow(FOLLOW_regExpr_in_actualRuleLabel545);
                    regExpr60=regExpr();

                    state._fsp--;

                    adaptor.addChild(root_0, regExpr60.getTree());

                    }
                    break;
                case 5 :
                    // Label.g:90:6: valueLabel
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_valueLabel_in_actualRuleLabel552);
                    valueLabel61=valueLabel();

                    state._fsp--;

                    adaptor.addChild(root_0, valueLabel61.getTree());

                    }
                    break;
                case 6 :
                    // Label.g:91:6: nodeLabel
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_nodeLabel_in_actualRuleLabel559);
                    nodeLabel62=nodeLabel();

                    state._fsp--;

                    adaptor.addChild(root_0, nodeLabel62.getTree());

                    }
                    break;
                case 7 :
                    // Label.g:92:6: attrLabel
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_attrLabel_in_actualRuleLabel566);
                    attrLabel63=attrLabel();

                    state._fsp--;

                    adaptor.addChild(root_0, attrLabel63.getTree());

                    }
                    break;
                case 8 :
                    // Label.g:93:6: negLabel
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_negLabel_in_actualRuleLabel573);
                    negLabel64=negLabel();

                    state._fsp--;

                    adaptor.addChild(root_0, negLabel64.getTree());

                    }
                    break;
                case 9 :
                    // Label.g:94:6: posLabel
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_posLabel_in_actualRuleLabel580);
                    posLabel65=posLabel();

                    state._fsp--;

                    adaptor.addChild(root_0, posLabel65.getTree());

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
    // Label.g:96:1: attrLabel : ( intP COLON ( IDENT )? -> ^( INT IDENT ) | realP COLON ( IDENT )? -> ^( REAL IDENT ) | stringP COLON ( IDENT )? -> ^( STRING IDENT ) | boolP COLON ( IDENT )? -> ^( BOOL IDENT ) | attrP COLON -> ^( ATTR IDENT ) | prodP COLON -> ^( PROD IDENT ) | argP COLON ( DIGIT )+ -> ^( ARG IDENT ) );
    public final LabelParser.attrLabel_return attrLabel() throws RecognitionException {
        LabelParser.attrLabel_return retval = new LabelParser.attrLabel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token COLON67=null;
        Token IDENT68=null;
        Token COLON70=null;
        Token IDENT71=null;
        Token COLON73=null;
        Token IDENT74=null;
        Token COLON76=null;
        Token IDENT77=null;
        Token COLON79=null;
        Token COLON81=null;
        Token COLON83=null;
        Token DIGIT84=null;
        LabelParser.intP_return intP66 = null;

        LabelParser.realP_return realP69 = null;

        LabelParser.stringP_return stringP72 = null;

        LabelParser.boolP_return boolP75 = null;

        LabelParser.attrP_return attrP78 = null;

        LabelParser.prodP_return prodP80 = null;

        LabelParser.argP_return argP82 = null;


        Object COLON67_tree=null;
        Object IDENT68_tree=null;
        Object COLON70_tree=null;
        Object IDENT71_tree=null;
        Object COLON73_tree=null;
        Object IDENT74_tree=null;
        Object COLON76_tree=null;
        Object IDENT77_tree=null;
        Object COLON79_tree=null;
        Object COLON81_tree=null;
        Object COLON83_tree=null;
        Object DIGIT84_tree=null;
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
            // Label.g:97:4: ( intP COLON ( IDENT )? -> ^( INT IDENT ) | realP COLON ( IDENT )? -> ^( REAL IDENT ) | stringP COLON ( IDENT )? -> ^( STRING IDENT ) | boolP COLON ( IDENT )? -> ^( BOOL IDENT ) | attrP COLON -> ^( ATTR IDENT ) | prodP COLON -> ^( PROD IDENT ) | argP COLON ( DIGIT )+ -> ^( ARG IDENT ) )
            int alt25=7;
            switch ( input.LA(1) ) {
            case 71:
                {
                alt25=1;
                }
                break;
            case 72:
                {
                alt25=2;
                }
                break;
            case 73:
                {
                alt25=3;
                }
                break;
            case 74:
                {
                alt25=4;
                }
                break;
            case 68:
                {
                alt25=5;
                }
                break;
            case 69:
                {
                alt25=6;
                }
                break;
            case 70:
                {
                alt25=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 25, 0, input);

                throw nvae;
            }

            switch (alt25) {
                case 1 :
                    // Label.g:97:6: intP COLON ( IDENT )?
                    {
                    pushFollow(FOLLOW_intP_in_attrLabel595);
                    intP66=intP();

                    state._fsp--;

                    stream_intP.add(intP66.getTree());
                    COLON67=(Token)match(input,COLON,FOLLOW_COLON_in_attrLabel597);  
                    stream_COLON.add(COLON67);

                    // Label.g:97:17: ( IDENT )?
                    int alt20=2;
                    int LA20_0 = input.LA(1);

                    if ( (LA20_0==IDENT) ) {
                        alt20=1;
                    }
                    switch (alt20) {
                        case 1 :
                            // Label.g:97:17: IDENT
                            {
                            IDENT68=(Token)match(input,IDENT,FOLLOW_IDENT_in_attrLabel599);  
                            stream_IDENT.add(IDENT68);


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
                    // 97:24: -> ^( INT IDENT )
                    {
                        // Label.g:97:27: ^( INT IDENT )
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
                    // Label.g:98:6: realP COLON ( IDENT )?
                    {
                    pushFollow(FOLLOW_realP_in_attrLabel615);
                    realP69=realP();

                    state._fsp--;

                    stream_realP.add(realP69.getTree());
                    COLON70=(Token)match(input,COLON,FOLLOW_COLON_in_attrLabel617);  
                    stream_COLON.add(COLON70);

                    // Label.g:98:18: ( IDENT )?
                    int alt21=2;
                    int LA21_0 = input.LA(1);

                    if ( (LA21_0==IDENT) ) {
                        alt21=1;
                    }
                    switch (alt21) {
                        case 1 :
                            // Label.g:98:18: IDENT
                            {
                            IDENT71=(Token)match(input,IDENT,FOLLOW_IDENT_in_attrLabel619);  
                            stream_IDENT.add(IDENT71);


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
                    // 98:25: -> ^( REAL IDENT )
                    {
                        // Label.g:98:28: ^( REAL IDENT )
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
                    // Label.g:99:6: stringP COLON ( IDENT )?
                    {
                    pushFollow(FOLLOW_stringP_in_attrLabel635);
                    stringP72=stringP();

                    state._fsp--;

                    stream_stringP.add(stringP72.getTree());
                    COLON73=(Token)match(input,COLON,FOLLOW_COLON_in_attrLabel637);  
                    stream_COLON.add(COLON73);

                    // Label.g:99:20: ( IDENT )?
                    int alt22=2;
                    int LA22_0 = input.LA(1);

                    if ( (LA22_0==IDENT) ) {
                        alt22=1;
                    }
                    switch (alt22) {
                        case 1 :
                            // Label.g:99:20: IDENT
                            {
                            IDENT74=(Token)match(input,IDENT,FOLLOW_IDENT_in_attrLabel639);  
                            stream_IDENT.add(IDENT74);


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
                    // 99:27: -> ^( STRING IDENT )
                    {
                        // Label.g:99:30: ^( STRING IDENT )
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
                    // Label.g:100:6: boolP COLON ( IDENT )?
                    {
                    pushFollow(FOLLOW_boolP_in_attrLabel655);
                    boolP75=boolP();

                    state._fsp--;

                    stream_boolP.add(boolP75.getTree());
                    COLON76=(Token)match(input,COLON,FOLLOW_COLON_in_attrLabel657);  
                    stream_COLON.add(COLON76);

                    // Label.g:100:18: ( IDENT )?
                    int alt23=2;
                    int LA23_0 = input.LA(1);

                    if ( (LA23_0==IDENT) ) {
                        alt23=1;
                    }
                    switch (alt23) {
                        case 1 :
                            // Label.g:100:18: IDENT
                            {
                            IDENT77=(Token)match(input,IDENT,FOLLOW_IDENT_in_attrLabel659);  
                            stream_IDENT.add(IDENT77);


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
                    // 100:25: -> ^( BOOL IDENT )
                    {
                        // Label.g:100:28: ^( BOOL IDENT )
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
                    // Label.g:101:6: attrP COLON
                    {
                    pushFollow(FOLLOW_attrP_in_attrLabel675);
                    attrP78=attrP();

                    state._fsp--;

                    stream_attrP.add(attrP78.getTree());
                    COLON79=(Token)match(input,COLON,FOLLOW_COLON_in_attrLabel677);  
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
                    // 101:18: -> ^( ATTR IDENT )
                    {
                        // Label.g:101:21: ^( ATTR IDENT )
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
                    // Label.g:102:6: prodP COLON
                    {
                    pushFollow(FOLLOW_prodP_in_attrLabel692);
                    prodP80=prodP();

                    state._fsp--;

                    stream_prodP.add(prodP80.getTree());
                    COLON81=(Token)match(input,COLON,FOLLOW_COLON_in_attrLabel694);  
                    stream_COLON.add(COLON81);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 102:18: -> ^( PROD IDENT )
                    {
                        // Label.g:102:21: ^( PROD IDENT )
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
                    // Label.g:103:6: argP COLON ( DIGIT )+
                    {
                    pushFollow(FOLLOW_argP_in_attrLabel709);
                    argP82=argP();

                    state._fsp--;

                    stream_argP.add(argP82.getTree());
                    COLON83=(Token)match(input,COLON,FOLLOW_COLON_in_attrLabel711);  
                    stream_COLON.add(COLON83);

                    // Label.g:103:17: ( DIGIT )+
                    int cnt24=0;
                    loop24:
                    do {
                        int alt24=2;
                        int LA24_0 = input.LA(1);

                        if ( (LA24_0==DIGIT) ) {
                            alt24=1;
                        }


                        switch (alt24) {
                    	case 1 :
                    	    // Label.g:103:17: DIGIT
                    	    {
                    	    DIGIT84=(Token)match(input,DIGIT,FOLLOW_DIGIT_in_attrLabel713);  
                    	    stream_DIGIT.add(DIGIT84);


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt24 >= 1 ) break loop24;
                                EarlyExitException eee =
                                    new EarlyExitException(24, input);
                                throw eee;
                        }
                        cnt24++;
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
                    // 103:24: -> ^( ARG IDENT )
                    {
                        // Label.g:103:27: ^( ARG IDENT )
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
    // Label.g:105:1: negLabel : PLING posLabel ;
    public final LabelParser.negLabel_return negLabel() throws RecognitionException {
        LabelParser.negLabel_return retval = new LabelParser.negLabel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token PLING85=null;
        LabelParser.posLabel_return posLabel86 = null;


        Object PLING85_tree=null;

        try {
            // Label.g:106:4: ( PLING posLabel )
            // Label.g:106:6: PLING posLabel
            {
            root_0 = (Object)adaptor.nil();

            PLING85=(Token)match(input,PLING,FOLLOW_PLING_in_negLabel733); 
            PLING85_tree = (Object)adaptor.create(PLING85);
            adaptor.addChild(root_0, PLING85_tree);

            pushFollow(FOLLOW_posLabel_in_negLabel735);
            posLabel86=posLabel();

            state._fsp--;

            adaptor.addChild(root_0, posLabel86.getTree());

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
    // Label.g:108:1: posLabel : ( wildcard | EQUALS | LBRACE regExpr RBRACE | SQTEXT | (~ ( SQTEXT | LBRACE | RBRACE | QUERY | EQUALS | COLON | PLING ) )* );
    public final LabelParser.posLabel_return posLabel() throws RecognitionException {
        LabelParser.posLabel_return retval = new LabelParser.posLabel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQUALS88=null;
        Token LBRACE89=null;
        Token RBRACE91=null;
        Token SQTEXT92=null;
        Token set93=null;
        LabelParser.wildcard_return wildcard87 = null;

        LabelParser.regExpr_return regExpr90 = null;


        Object EQUALS88_tree=null;
        Object LBRACE89_tree=null;
        Object RBRACE91_tree=null;
        Object SQTEXT92_tree=null;
        Object set93_tree=null;

        try {
            // Label.g:109:4: ( wildcard | EQUALS | LBRACE regExpr RBRACE | SQTEXT | (~ ( SQTEXT | LBRACE | RBRACE | QUERY | EQUALS | COLON | PLING ) )* )
            int alt27=5;
            switch ( input.LA(1) ) {
            case QUERY:
                {
                alt27=1;
                }
                break;
            case EQUALS:
                {
                alt27=2;
                }
                break;
            case LBRACE:
                {
                alt27=3;
                }
                break;
            case SQTEXT:
                {
                alt27=4;
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
                alt27=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 27, 0, input);

                throw nvae;
            }

            switch (alt27) {
                case 1 :
                    // Label.g:109:6: wildcard
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_wildcard_in_posLabel750);
                    wildcard87=wildcard();

                    state._fsp--;

                    adaptor.addChild(root_0, wildcard87.getTree());

                    }
                    break;
                case 2 :
                    // Label.g:110:6: EQUALS
                    {
                    root_0 = (Object)adaptor.nil();

                    EQUALS88=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_posLabel757); 
                    EQUALS88_tree = (Object)adaptor.create(EQUALS88);
                    adaptor.addChild(root_0, EQUALS88_tree);


                    }
                    break;
                case 3 :
                    // Label.g:111:6: LBRACE regExpr RBRACE
                    {
                    root_0 = (Object)adaptor.nil();

                    LBRACE89=(Token)match(input,LBRACE,FOLLOW_LBRACE_in_posLabel764); 
                    LBRACE89_tree = (Object)adaptor.create(LBRACE89);
                    adaptor.addChild(root_0, LBRACE89_tree);

                    pushFollow(FOLLOW_regExpr_in_posLabel766);
                    regExpr90=regExpr();

                    state._fsp--;

                    adaptor.addChild(root_0, regExpr90.getTree());
                    RBRACE91=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_posLabel768); 
                    RBRACE91_tree = (Object)adaptor.create(RBRACE91);
                    adaptor.addChild(root_0, RBRACE91_tree);


                    }
                    break;
                case 4 :
                    // Label.g:112:6: SQTEXT
                    {
                    root_0 = (Object)adaptor.nil();

                    SQTEXT92=(Token)match(input,SQTEXT,FOLLOW_SQTEXT_in_posLabel775); 
                    SQTEXT92_tree = (Object)adaptor.create(SQTEXT92);
                    adaptor.addChild(root_0, SQTEXT92_tree);


                    }
                    break;
                case 5 :
                    // Label.g:113:6: (~ ( SQTEXT | LBRACE | RBRACE | QUERY | EQUALS | COLON | PLING ) )*
                    {
                    root_0 = (Object)adaptor.nil();

                    // Label.g:113:6: (~ ( SQTEXT | LBRACE | RBRACE | QUERY | EQUALS | COLON | PLING ) )*
                    loop26:
                    do {
                        int alt26=2;
                        int LA26_0 = input.LA(1);

                        if ( ((LA26_0>=NEW && LA26_0<=EMPTY)||LA26_0==IDENT||(LA26_0>=NUMBER && LA26_0<=DQTEXT)||LA26_0==DIGIT||(LA26_0>=BAR && LA26_0<=79)) ) {
                            alt26=1;
                        }


                        switch (alt26) {
                    	case 1 :
                    	    // Label.g:113:7: ~ ( SQTEXT | LBRACE | RBRACE | QUERY | EQUALS | COLON | PLING )
                    	    {
                    	    set93=(Token)input.LT(1);
                    	    if ( (input.LA(1)>=NEW && input.LA(1)<=EMPTY)||input.LA(1)==IDENT||(input.LA(1)>=NUMBER && input.LA(1)<=DQTEXT)||input.LA(1)==DIGIT||(input.LA(1)>=BAR && input.LA(1)<=79) ) {
                    	        input.consume();
                    	        adaptor.addChild(root_0, (Object)adaptor.create(set93));
                    	        state.errorRecovery=false;
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop26;
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
    // Label.g:115:1: regExpr : choice ;
    public final LabelParser.regExpr_return regExpr() throws RecognitionException {
        LabelParser.regExpr_return retval = new LabelParser.regExpr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        LabelParser.choice_return choice94 = null;



        try {
            // Label.g:116:4: ( choice )
            // Label.g:116:6: choice
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_choice_in_regExpr815);
            choice94=choice();

            state._fsp--;

            adaptor.addChild(root_0, choice94.getTree());

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
    // Label.g:118:1: choice : sequence ( BAR choice )? ;
    public final LabelParser.choice_return choice() throws RecognitionException {
        LabelParser.choice_return retval = new LabelParser.choice_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BAR96=null;
        LabelParser.sequence_return sequence95 = null;

        LabelParser.choice_return choice97 = null;


        Object BAR96_tree=null;

        try {
            // Label.g:119:4: ( sequence ( BAR choice )? )
            // Label.g:119:6: sequence ( BAR choice )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_sequence_in_choice827);
            sequence95=sequence();

            state._fsp--;

            adaptor.addChild(root_0, sequence95.getTree());
            // Label.g:119:15: ( BAR choice )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==BAR) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // Label.g:119:16: BAR choice
                    {
                    BAR96=(Token)match(input,BAR,FOLLOW_BAR_in_choice830); 
                    pushFollow(FOLLOW_choice_in_choice833);
                    choice97=choice();

                    state._fsp--;

                    adaptor.addChild(root_0, choice97.getTree());

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
    // Label.g:121:1: sequence : unary ( DOT sequence )? ;
    public final LabelParser.sequence_return sequence() throws RecognitionException {
        LabelParser.sequence_return retval = new LabelParser.sequence_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOT99=null;
        LabelParser.unary_return unary98 = null;

        LabelParser.sequence_return sequence100 = null;


        Object DOT99_tree=null;

        try {
            // Label.g:122:4: ( unary ( DOT sequence )? )
            // Label.g:122:6: unary ( DOT sequence )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_unary_in_sequence847);
            unary98=unary();

            state._fsp--;

            adaptor.addChild(root_0, unary98.getTree());
            // Label.g:122:12: ( DOT sequence )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==DOT) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // Label.g:122:13: DOT sequence
                    {
                    DOT99=(Token)match(input,DOT,FOLLOW_DOT_in_sequence850); 
                    pushFollow(FOLLOW_sequence_in_sequence853);
                    sequence100=sequence();

                    state._fsp--;

                    adaptor.addChild(root_0, sequence100.getTree());

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
    // Label.g:124:1: unary : ( MINUS unary | atom ( STAR | PLUS )? );
    public final LabelParser.unary_return unary() throws RecognitionException {
        LabelParser.unary_return retval = new LabelParser.unary_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token MINUS101=null;
        Token STAR104=null;
        Token PLUS105=null;
        LabelParser.unary_return unary102 = null;

        LabelParser.atom_return atom103 = null;


        Object MINUS101_tree=null;
        Object STAR104_tree=null;
        Object PLUS105_tree=null;

        try {
            // Label.g:125:4: ( MINUS unary | atom ( STAR | PLUS )? )
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==MINUS) ) {
                alt31=1;
            }
            else if ( (LA31_0==EQUALS||(LA31_0>=SQTEXT && LA31_0<=QUERY)||(LA31_0>=IDENTCHAR && LA31_0<=LPAR)) ) {
                alt31=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 31, 0, input);

                throw nvae;
            }
            switch (alt31) {
                case 1 :
                    // Label.g:125:6: MINUS unary
                    {
                    root_0 = (Object)adaptor.nil();

                    MINUS101=(Token)match(input,MINUS,FOLLOW_MINUS_in_unary867); 
                    MINUS101_tree = (Object)adaptor.create(MINUS101);
                    adaptor.addChild(root_0, MINUS101_tree);

                    pushFollow(FOLLOW_unary_in_unary869);
                    unary102=unary();

                    state._fsp--;

                    adaptor.addChild(root_0, unary102.getTree());

                    }
                    break;
                case 2 :
                    // Label.g:126:6: atom ( STAR | PLUS )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_atom_in_unary876);
                    atom103=atom();

                    state._fsp--;

                    adaptor.addChild(root_0, atom103.getTree());
                    // Label.g:126:11: ( STAR | PLUS )?
                    int alt30=3;
                    int LA30_0 = input.LA(1);

                    if ( (LA30_0==STAR) ) {
                        alt30=1;
                    }
                    else if ( (LA30_0==PLUS) ) {
                        alt30=2;
                    }
                    switch (alt30) {
                        case 1 :
                            // Label.g:126:12: STAR
                            {
                            STAR104=(Token)match(input,STAR,FOLLOW_STAR_in_unary879); 

                            }
                            break;
                        case 2 :
                            // Label.g:126:20: PLUS
                            {
                            PLUS105=(Token)match(input,PLUS,FOLLOW_PLUS_in_unary884); 

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
    // Label.g:128:1: atom : ( SQTEXT | ( IDENTCHAR )+ | EQUALS | LPAR regExpr RPAR | wildcard );
    public final LabelParser.atom_return atom() throws RecognitionException {
        LabelParser.atom_return retval = new LabelParser.atom_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SQTEXT106=null;
        Token IDENTCHAR107=null;
        Token EQUALS108=null;
        Token LPAR109=null;
        Token RPAR111=null;
        LabelParser.regExpr_return regExpr110 = null;

        LabelParser.wildcard_return wildcard112 = null;


        Object SQTEXT106_tree=null;
        Object IDENTCHAR107_tree=null;
        Object EQUALS108_tree=null;
        Object LPAR109_tree=null;
        Object RPAR111_tree=null;

        try {
            // Label.g:129:4: ( SQTEXT | ( IDENTCHAR )+ | EQUALS | LPAR regExpr RPAR | wildcard )
            int alt33=5;
            switch ( input.LA(1) ) {
            case SQTEXT:
                {
                alt33=1;
                }
                break;
            case IDENTCHAR:
                {
                alt33=2;
                }
                break;
            case EQUALS:
                {
                alt33=3;
                }
                break;
            case LPAR:
                {
                alt33=4;
                }
                break;
            case QUERY:
                {
                alt33=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 33, 0, input);

                throw nvae;
            }

            switch (alt33) {
                case 1 :
                    // Label.g:129:6: SQTEXT
                    {
                    root_0 = (Object)adaptor.nil();

                    SQTEXT106=(Token)match(input,SQTEXT,FOLLOW_SQTEXT_in_atom899); 
                    SQTEXT106_tree = (Object)adaptor.create(SQTEXT106);
                    adaptor.addChild(root_0, SQTEXT106_tree);


                    }
                    break;
                case 2 :
                    // Label.g:130:6: ( IDENTCHAR )+
                    {
                    root_0 = (Object)adaptor.nil();

                    // Label.g:130:6: ( IDENTCHAR )+
                    int cnt32=0;
                    loop32:
                    do {
                        int alt32=2;
                        int LA32_0 = input.LA(1);

                        if ( (LA32_0==IDENTCHAR) ) {
                            alt32=1;
                        }


                        switch (alt32) {
                    	case 1 :
                    	    // Label.g:130:6: IDENTCHAR
                    	    {
                    	    IDENTCHAR107=(Token)match(input,IDENTCHAR,FOLLOW_IDENTCHAR_in_atom906); 
                    	    IDENTCHAR107_tree = (Object)adaptor.create(IDENTCHAR107);
                    	    adaptor.addChild(root_0, IDENTCHAR107_tree);


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt32 >= 1 ) break loop32;
                                EarlyExitException eee =
                                    new EarlyExitException(32, input);
                                throw eee;
                        }
                        cnt32++;
                    } while (true);


                    }
                    break;
                case 3 :
                    // Label.g:131:6: EQUALS
                    {
                    root_0 = (Object)adaptor.nil();

                    EQUALS108=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_atom914); 
                    EQUALS108_tree = (Object)adaptor.create(EQUALS108);
                    adaptor.addChild(root_0, EQUALS108_tree);


                    }
                    break;
                case 4 :
                    // Label.g:132:6: LPAR regExpr RPAR
                    {
                    root_0 = (Object)adaptor.nil();

                    LPAR109=(Token)match(input,LPAR,FOLLOW_LPAR_in_atom921); 
                    LPAR109_tree = (Object)adaptor.create(LPAR109);
                    adaptor.addChild(root_0, LPAR109_tree);

                    pushFollow(FOLLOW_regExpr_in_atom923);
                    regExpr110=regExpr();

                    state._fsp--;

                    adaptor.addChild(root_0, regExpr110.getTree());
                    RPAR111=(Token)match(input,RPAR,FOLLOW_RPAR_in_atom925); 
                    RPAR111_tree = (Object)adaptor.create(RPAR111);
                    adaptor.addChild(root_0, RPAR111_tree);


                    }
                    break;
                case 5 :
                    // Label.g:133:6: wildcard
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_wildcard_in_atom932);
                    wildcard112=wildcard();

                    state._fsp--;

                    adaptor.addChild(root_0, wildcard112.getTree());

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
    // Label.g:135:1: wildcard : QUERY ( IDENT )? LSQUARE ( HAT )? atom ( COMMA atom )* RSQUARE -> ^( QUERY IDENT HAT ( atom )* ) ;
    public final LabelParser.wildcard_return wildcard() throws RecognitionException {
        LabelParser.wildcard_return retval = new LabelParser.wildcard_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token QUERY113=null;
        Token IDENT114=null;
        Token LSQUARE115=null;
        Token HAT116=null;
        Token COMMA118=null;
        Token RSQUARE120=null;
        LabelParser.atom_return atom117 = null;

        LabelParser.atom_return atom119 = null;


        Object QUERY113_tree=null;
        Object IDENT114_tree=null;
        Object LSQUARE115_tree=null;
        Object HAT116_tree=null;
        Object COMMA118_tree=null;
        Object RSQUARE120_tree=null;
        RewriteRuleTokenStream stream_HAT=new RewriteRuleTokenStream(adaptor,"token HAT");
        RewriteRuleTokenStream stream_QUERY=new RewriteRuleTokenStream(adaptor,"token QUERY");
        RewriteRuleTokenStream stream_IDENT=new RewriteRuleTokenStream(adaptor,"token IDENT");
        RewriteRuleTokenStream stream_LSQUARE=new RewriteRuleTokenStream(adaptor,"token LSQUARE");
        RewriteRuleTokenStream stream_RSQUARE=new RewriteRuleTokenStream(adaptor,"token RSQUARE");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_atom=new RewriteRuleSubtreeStream(adaptor,"rule atom");
        try {
            // Label.g:136:4: ( QUERY ( IDENT )? LSQUARE ( HAT )? atom ( COMMA atom )* RSQUARE -> ^( QUERY IDENT HAT ( atom )* ) )
            // Label.g:136:6: QUERY ( IDENT )? LSQUARE ( HAT )? atom ( COMMA atom )* RSQUARE
            {
            QUERY113=(Token)match(input,QUERY,FOLLOW_QUERY_in_wildcard944);  
            stream_QUERY.add(QUERY113);

            // Label.g:136:12: ( IDENT )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==IDENT) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // Label.g:136:12: IDENT
                    {
                    IDENT114=(Token)match(input,IDENT,FOLLOW_IDENT_in_wildcard946);  
                    stream_IDENT.add(IDENT114);


                    }
                    break;

            }

            LSQUARE115=(Token)match(input,LSQUARE,FOLLOW_LSQUARE_in_wildcard949);  
            stream_LSQUARE.add(LSQUARE115);

            // Label.g:136:27: ( HAT )?
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==HAT) ) {
                alt35=1;
            }
            switch (alt35) {
                case 1 :
                    // Label.g:136:27: HAT
                    {
                    HAT116=(Token)match(input,HAT,FOLLOW_HAT_in_wildcard951);  
                    stream_HAT.add(HAT116);


                    }
                    break;

            }

            pushFollow(FOLLOW_atom_in_wildcard954);
            atom117=atom();

            state._fsp--;

            stream_atom.add(atom117.getTree());
            // Label.g:136:37: ( COMMA atom )*
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( (LA36_0==COMMA) ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // Label.g:136:38: COMMA atom
            	    {
            	    COMMA118=(Token)match(input,COMMA,FOLLOW_COMMA_in_wildcard957);  
            	    stream_COMMA.add(COMMA118);

            	    pushFollow(FOLLOW_atom_in_wildcard959);
            	    atom119=atom();

            	    state._fsp--;

            	    stream_atom.add(atom119.getTree());

            	    }
            	    break;

            	default :
            	    break loop36;
                }
            } while (true);

            RSQUARE120=(Token)match(input,RSQUARE,FOLLOW_RSQUARE_in_wildcard963);  
            stream_RSQUARE.add(RSQUARE120);



            // AST REWRITE
            // elements: HAT, QUERY, atom, IDENT
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 137:6: -> ^( QUERY IDENT HAT ( atom )* )
            {
                // Label.g:137:9: ^( QUERY IDENT HAT ( atom )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_QUERY.nextNode(), root_1);

                adaptor.addChild(root_1, stream_IDENT.nextNode());
                adaptor.addChild(root_1, stream_HAT.nextNode());
                // Label.g:137:27: ( atom )*
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
    // Label.g:145:1: newP : 'new' ;
    public final LabelParser.newP_return newP() throws RecognitionException {
        LabelParser.newP_return retval = new LabelParser.newP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal121=null;

        Object string_literal121_tree=null;

        try {
            // Label.g:145:9: ( 'new' )
            // Label.g:145:11: 'new'
            {
            root_0 = (Object)adaptor.nil();

            string_literal121=(Token)match(input,57,FOLLOW_57_in_newP1058); 
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
    // $ANTLR end newP

    public static class delP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start delP
    // Label.g:146:1: delP : 'del' ;
    public final LabelParser.delP_return delP() throws RecognitionException {
        LabelParser.delP_return retval = new LabelParser.delP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal122=null;

        Object string_literal122_tree=null;

        try {
            // Label.g:146:9: ( 'del' )
            // Label.g:146:11: 'del'
            {
            root_0 = (Object)adaptor.nil();

            string_literal122=(Token)match(input,58,FOLLOW_58_in_delP1068); 
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
    // $ANTLR end delP

    public static class cnewP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start cnewP
    // Label.g:147:1: cnewP : 'cnew' ;
    public final LabelParser.cnewP_return cnewP() throws RecognitionException {
        LabelParser.cnewP_return retval = new LabelParser.cnewP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal123=null;

        Object string_literal123_tree=null;

        try {
            // Label.g:147:9: ( 'cnew' )
            // Label.g:147:11: 'cnew'
            {
            root_0 = (Object)adaptor.nil();

            string_literal123=(Token)match(input,59,FOLLOW_59_in_cnewP1077); 
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
    // $ANTLR end cnewP

    public static class notP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start notP
    // Label.g:148:1: notP : 'not' ;
    public final LabelParser.notP_return notP() throws RecognitionException {
        LabelParser.notP_return retval = new LabelParser.notP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal124=null;

        Object string_literal124_tree=null;

        try {
            // Label.g:148:9: ( 'not' )
            // Label.g:148:11: 'not'
            {
            root_0 = (Object)adaptor.nil();

            string_literal124=(Token)match(input,60,FOLLOW_60_in_notP1087); 
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
    // $ANTLR end notP

    public static class useP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start useP
    // Label.g:149:1: useP : 'use' ;
    public final LabelParser.useP_return useP() throws RecognitionException {
        LabelParser.useP_return retval = new LabelParser.useP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal125=null;

        Object string_literal125_tree=null;

        try {
            // Label.g:149:9: ( 'use' )
            // Label.g:149:11: 'use'
            {
            root_0 = (Object)adaptor.nil();

            string_literal125=(Token)match(input,61,FOLLOW_61_in_useP1097); 
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
    // $ANTLR end useP

    public static class remP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start remP
    // Label.g:150:1: remP : 'rem' ;
    public final LabelParser.remP_return remP() throws RecognitionException {
        LabelParser.remP_return retval = new LabelParser.remP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal126=null;

        Object string_literal126_tree=null;

        try {
            // Label.g:150:9: ( 'rem' )
            // Label.g:150:11: 'rem'
            {
            root_0 = (Object)adaptor.nil();

            string_literal126=(Token)match(input,62,FOLLOW_62_in_remP1107); 
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
    // $ANTLR end remP

    public static class forallP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start forallP
    // Label.g:152:1: forallP : 'forall' ;
    public final LabelParser.forallP_return forallP() throws RecognitionException {
        LabelParser.forallP_return retval = new LabelParser.forallP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal127=null;

        Object string_literal127_tree=null;

        try {
            // Label.g:152:9: ( 'forall' )
            // Label.g:152:11: 'forall'
            {
            root_0 = (Object)adaptor.nil();

            string_literal127=(Token)match(input,63,FOLLOW_63_in_forallP1115); 
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
    // $ANTLR end forallP

    public static class forallxP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start forallxP
    // Label.g:153:1: forallxP : 'forallx' ;
    public final LabelParser.forallxP_return forallxP() throws RecognitionException {
        LabelParser.forallxP_return retval = new LabelParser.forallxP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal128=null;

        Object string_literal128_tree=null;

        try {
            // Label.g:153:10: ( 'forallx' )
            // Label.g:153:12: 'forallx'
            {
            root_0 = (Object)adaptor.nil();

            string_literal128=(Token)match(input,64,FOLLOW_64_in_forallxP1122); 
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
    // $ANTLR end forallxP

    public static class existsP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start existsP
    // Label.g:154:1: existsP : 'exists' ;
    public final LabelParser.existsP_return existsP() throws RecognitionException {
        LabelParser.existsP_return retval = new LabelParser.existsP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal129=null;

        Object string_literal129_tree=null;

        try {
            // Label.g:154:9: ( 'exists' )
            // Label.g:154:11: 'exists'
            {
            root_0 = (Object)adaptor.nil();

            string_literal129=(Token)match(input,65,FOLLOW_65_in_existsP1129); 
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
    // $ANTLR end existsP

    public static class nestedP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start nestedP
    // Label.g:155:1: nestedP : 'nested' ;
    public final LabelParser.nestedP_return nestedP() throws RecognitionException {
        LabelParser.nestedP_return retval = new LabelParser.nestedP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal130=null;

        Object string_literal130_tree=null;

        try {
            // Label.g:155:9: ( 'nested' )
            // Label.g:155:11: 'nested'
            {
            root_0 = (Object)adaptor.nil();

            string_literal130=(Token)match(input,66,FOLLOW_66_in_nestedP1136); 
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
    // $ANTLR end nestedP

    public static class parP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start parP
    // Label.g:157:1: parP : 'par' ;
    public final LabelParser.parP_return parP() throws RecognitionException {
        LabelParser.parP_return retval = new LabelParser.parP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal131=null;

        Object string_literal131_tree=null;

        try {
            // Label.g:157:9: ( 'par' )
            // Label.g:157:11: 'par'
            {
            root_0 = (Object)adaptor.nil();

            string_literal131=(Token)match(input,67,FOLLOW_67_in_parP1147); 
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
    // $ANTLR end parP

    public static class attrP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start attrP
    // Label.g:159:1: attrP : 'attr' ;
    public final LabelParser.attrP_return attrP() throws RecognitionException {
        LabelParser.attrP_return retval = new LabelParser.attrP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal132=null;

        Object string_literal132_tree=null;

        try {
            // Label.g:159:9: ( 'attr' )
            // Label.g:159:11: 'attr'
            {
            root_0 = (Object)adaptor.nil();

            string_literal132=(Token)match(input,68,FOLLOW_68_in_attrP1157); 
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
    // $ANTLR end attrP

    public static class prodP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start prodP
    // Label.g:160:1: prodP : 'prod' ;
    public final LabelParser.prodP_return prodP() throws RecognitionException {
        LabelParser.prodP_return retval = new LabelParser.prodP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal133=null;

        Object string_literal133_tree=null;

        try {
            // Label.g:160:9: ( 'prod' )
            // Label.g:160:11: 'prod'
            {
            root_0 = (Object)adaptor.nil();

            string_literal133=(Token)match(input,69,FOLLOW_69_in_prodP1166); 
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
    // $ANTLR end prodP

    public static class argP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start argP
    // Label.g:161:1: argP : 'arg' ;
    public final LabelParser.argP_return argP() throws RecognitionException {
        LabelParser.argP_return retval = new LabelParser.argP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal134=null;

        Object string_literal134_tree=null;

        try {
            // Label.g:161:9: ( 'arg' )
            // Label.g:161:11: 'arg'
            {
            root_0 = (Object)adaptor.nil();

            string_literal134=(Token)match(input,70,FOLLOW_70_in_argP1176); 
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
    // $ANTLR end argP

    public static class intP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start intP
    // Label.g:162:1: intP : 'int' ;
    public final LabelParser.intP_return intP() throws RecognitionException {
        LabelParser.intP_return retval = new LabelParser.intP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal135=null;

        Object string_literal135_tree=null;

        try {
            // Label.g:162:9: ( 'int' )
            // Label.g:162:11: 'int'
            {
            root_0 = (Object)adaptor.nil();

            string_literal135=(Token)match(input,71,FOLLOW_71_in_intP1186); 
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
    // $ANTLR end intP

    public static class realP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start realP
    // Label.g:163:1: realP : 'real' ;
    public final LabelParser.realP_return realP() throws RecognitionException {
        LabelParser.realP_return retval = new LabelParser.realP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal136=null;

        Object string_literal136_tree=null;

        try {
            // Label.g:163:9: ( 'real' )
            // Label.g:163:11: 'real'
            {
            root_0 = (Object)adaptor.nil();

            string_literal136=(Token)match(input,72,FOLLOW_72_in_realP1195); 
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
    // $ANTLR end realP

    public static class stringP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start stringP
    // Label.g:164:1: stringP : 'string' ;
    public final LabelParser.stringP_return stringP() throws RecognitionException {
        LabelParser.stringP_return retval = new LabelParser.stringP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal137=null;

        Object string_literal137_tree=null;

        try {
            // Label.g:164:9: ( 'string' )
            // Label.g:164:11: 'string'
            {
            root_0 = (Object)adaptor.nil();

            string_literal137=(Token)match(input,73,FOLLOW_73_in_stringP1202); 
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
    // $ANTLR end stringP

    public static class boolP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start boolP
    // Label.g:165:1: boolP : 'bool' ;
    public final LabelParser.boolP_return boolP() throws RecognitionException {
        LabelParser.boolP_return retval = new LabelParser.boolP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal138=null;

        Object string_literal138_tree=null;

        try {
            // Label.g:165:9: ( 'bool' )
            // Label.g:165:11: 'bool'
            {
            root_0 = (Object)adaptor.nil();

            string_literal138=(Token)match(input,74,FOLLOW_74_in_boolP1211); 
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
    // $ANTLR end boolP

    public static class typeP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start typeP
    // Label.g:167:1: typeP : 'type' ;
    public final LabelParser.typeP_return typeP() throws RecognitionException {
        LabelParser.typeP_return retval = new LabelParser.typeP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal139=null;

        Object string_literal139_tree=null;

        try {
            // Label.g:167:9: ( 'type' )
            // Label.g:167:11: 'type'
            {
            root_0 = (Object)adaptor.nil();

            string_literal139=(Token)match(input,75,FOLLOW_75_in_typeP1221); 
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
    // $ANTLR end typeP

    public static class flagP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start flagP
    // Label.g:168:1: flagP : 'flag' ;
    public final LabelParser.flagP_return flagP() throws RecognitionException {
        LabelParser.flagP_return retval = new LabelParser.flagP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal140=null;

        Object string_literal140_tree=null;

        try {
            // Label.g:168:9: ( 'flag' )
            // Label.g:168:11: 'flag'
            {
            root_0 = (Object)adaptor.nil();

            string_literal140=(Token)match(input,76,FOLLOW_76_in_flagP1230); 
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
    // $ANTLR end flagP

    public static class pathP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start pathP
    // Label.g:169:1: pathP : 'path' ;
    public final LabelParser.pathP_return pathP() throws RecognitionException {
        LabelParser.pathP_return retval = new LabelParser.pathP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal141=null;

        Object string_literal141_tree=null;

        try {
            // Label.g:169:9: ( 'path' )
            // Label.g:169:11: 'path'
            {
            root_0 = (Object)adaptor.nil();

            string_literal141=(Token)match(input,77,FOLLOW_77_in_pathP1239); 
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
    // $ANTLR end pathP

    public static class trueP_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start trueP
    // Label.g:171:1: trueP : 'true' ;
    public final LabelParser.trueP_return trueP() throws RecognitionException {
        LabelParser.trueP_return retval = new LabelParser.trueP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal142=null;

        Object string_literal142_tree=null;

        try {
            // Label.g:171:9: ( 'true' )
            // Label.g:171:11: 'true'
            {
            root_0 = (Object)adaptor.nil();

            string_literal142=(Token)match(input,78,FOLLOW_78_in_trueP1249); 
            string_literal142_tree = (Object)adaptor.create(string_literal142);
            adaptor.addChild(root_0, string_literal142_tree);


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
    // Label.g:172:1: falseP : 'false' ;
    public final LabelParser.falseP_return falseP() throws RecognitionException {
        LabelParser.falseP_return retval = new LabelParser.falseP_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal143=null;

        Object string_literal143_tree=null;

        try {
            // Label.g:172:9: ( 'false' )
            // Label.g:172:11: 'false'
            {
            root_0 = (Object)adaptor.nil();

            string_literal143=(Token)match(input,79,FOLLOW_79_in_falseP1257); 
            string_literal143_tree = (Object)adaptor.create(string_literal143);
            adaptor.addChild(root_0, string_literal143_tree);


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
    protected DFA2 dfa2 = new DFA2(this);
    protected DFA11 dfa11 = new DFA11(this);
    protected DFA15 dfa15 = new DFA15(this);
    protected DFA19 dfa19 = new DFA19(this);
    static final String DFA1_eotS =
        "\37\uffff";
    static final String DFA1_eofS =
        "\1\36\36\uffff";
    static final String DFA1_minS =
        "\1\4\34\0\2\uffff";
    static final String DFA1_maxS =
        "\1\117\34\0\2\uffff";
    static final String DFA1_acceptS =
        "\35\uffff\1\1\1\2";
    static final String DFA1_specialS =
        "\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1\15\1"+
        "\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\1\27\1\30\1\31\1\32"+
        "\1\33\1\34\2\uffff}>";
    static final String[] DFA1_transitionS = {
            "\26\34\1\31\1\34\1\12\3\34\1\27\1\34\1\32\1\35\1\33\1\30\23"+
            "\34\1\4\1\5\1\10\1\6\1\7\1\13\1\1\1\2\1\3\1\11\1\22\1\24\1\25"+
            "\1\26\1\14\1\15\1\16\1\17\1\20\1\21\1\23\2\34",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            ""
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
            return "52:1: label : ({...}? => graphLabel | {...}? => ruleLabel );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA1_0 = input.LA(1);

                         
                        int index1_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_0==63) && (( !isGraph || isGraph ))) {s = 1;}

                        else if ( (LA1_0==64) && (( !isGraph || isGraph ))) {s = 2;}

                        else if ( (LA1_0==65) && (( !isGraph || isGraph ))) {s = 3;}

                        else if ( (LA1_0==57) && (( !isGraph || isGraph ))) {s = 4;}

                        else if ( (LA1_0==58) && (( !isGraph || isGraph ))) {s = 5;}

                        else if ( (LA1_0==60) && (( !isGraph || isGraph ))) {s = 6;}

                        else if ( (LA1_0==61) && (( !isGraph || isGraph ))) {s = 7;}

                        else if ( (LA1_0==59) && (( !isGraph || isGraph ))) {s = 8;}

                        else if ( (LA1_0==66) && (( !isGraph || isGraph ))) {s = 9;}

                        else if ( (LA1_0==COLON) && (( !isGraph || isGraph ))) {s = 10;}

                        else if ( (LA1_0==62) && (( !isGraph || isGraph ))) {s = 11;}

                        else if ( (LA1_0==71) && (( !isGraph || isGraph ))) {s = 12;}

                        else if ( (LA1_0==72) && (( !isGraph || isGraph ))) {s = 13;}

                        else if ( (LA1_0==73) && (( !isGraph || isGraph ))) {s = 14;}

                        else if ( (LA1_0==74) && (( !isGraph || isGraph ))) {s = 15;}

                        else if ( (LA1_0==75) && (( !isGraph || isGraph ))) {s = 16;}

                        else if ( (LA1_0==76) && (( !isGraph || isGraph ))) {s = 17;}

                        else if ( (LA1_0==67) && (( !isGraph || isGraph ))) {s = 18;}

                        else if ( (LA1_0==77) && (( !isGraph || isGraph ))) {s = 19;}

                        else if ( (LA1_0==68) && (( !isGraph || isGraph ))) {s = 20;}

                        else if ( (LA1_0==69) && (( !isGraph || isGraph ))) {s = 21;}

                        else if ( (LA1_0==70) && (( !isGraph || isGraph ))) {s = 22;}

                        else if ( (LA1_0==PLING) && (( !isGraph || isGraph ))) {s = 23;}

                        else if ( (LA1_0==QUERY) && (( !isGraph || isGraph ))) {s = 24;}

                        else if ( (LA1_0==EQUALS) && (( !isGraph || isGraph ))) {s = 25;}

                        else if ( (LA1_0==LBRACE) && (( !isGraph || isGraph ))) {s = 26;}

                        else if ( (LA1_0==SQTEXT) && (( !isGraph || isGraph ))) {s = 27;}

                        else if ( ((LA1_0>=NEW && LA1_0<=EMPTY)||LA1_0==IDENT||(LA1_0>=NUMBER && LA1_0<=DQTEXT)||LA1_0==DIGIT||(LA1_0>=BAR && LA1_0<=LABEL)||(LA1_0>=78 && LA1_0<=79)) && (( !isGraph || isGraph ))) {s = 28;}

                        else if ( (LA1_0==RBRACE) && ( isGraph )) {s = 29;}

                        else if ( (LA1_0==EOF) && ( !isGraph )) {s = 30;}

                         
                        input.seek(index1_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA1_1 = input.LA(1);

                         
                        int index1_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ( isGraph ) ) {s = 29;}

                        else if ( ( !isGraph ) ) {s = 30;}

                         
                        input.seek(index1_1);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA1_2 = input.LA(1);

                         
                        int index1_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ( isGraph ) ) {s = 29;}

                        else if ( ( !isGraph ) ) {s = 30;}

                         
                        input.seek(index1_2);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA1_3 = input.LA(1);

                         
                        int index1_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ( isGraph ) ) {s = 29;}

                        else if ( ( !isGraph ) ) {s = 30;}

                         
                        input.seek(index1_3);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA1_4 = input.LA(1);

                         
                        int index1_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ( isGraph ) ) {s = 29;}

                        else if ( ( !isGraph ) ) {s = 30;}

                         
                        input.seek(index1_4);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA1_5 = input.LA(1);

                         
                        int index1_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ( isGraph ) ) {s = 29;}

                        else if ( ( !isGraph ) ) {s = 30;}

                         
                        input.seek(index1_5);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA1_6 = input.LA(1);

                         
                        int index1_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ( isGraph ) ) {s = 29;}

                        else if ( ( !isGraph ) ) {s = 30;}

                         
                        input.seek(index1_6);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA1_7 = input.LA(1);

                         
                        int index1_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ( isGraph ) ) {s = 29;}

                        else if ( ( !isGraph ) ) {s = 30;}

                         
                        input.seek(index1_7);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA1_8 = input.LA(1);

                         
                        int index1_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ( isGraph ) ) {s = 29;}

                        else if ( ( !isGraph ) ) {s = 30;}

                         
                        input.seek(index1_8);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA1_9 = input.LA(1);

                         
                        int index1_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ( isGraph ) ) {s = 29;}

                        else if ( ( !isGraph ) ) {s = 30;}

                         
                        input.seek(index1_9);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA1_10 = input.LA(1);

                         
                        int index1_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ( isGraph ) ) {s = 29;}

                        else if ( ( !isGraph ) ) {s = 30;}

                         
                        input.seek(index1_10);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA1_11 = input.LA(1);

                         
                        int index1_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ( isGraph ) ) {s = 29;}

                        else if ( ( !isGraph ) ) {s = 30;}

                         
                        input.seek(index1_11);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA1_12 = input.LA(1);

                         
                        int index1_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ( isGraph ) ) {s = 29;}

                        else if ( ( !isGraph ) ) {s = 30;}

                         
                        input.seek(index1_12);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA1_13 = input.LA(1);

                         
                        int index1_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ( isGraph ) ) {s = 29;}

                        else if ( ( !isGraph ) ) {s = 30;}

                         
                        input.seek(index1_13);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA1_14 = input.LA(1);

                         
                        int index1_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ( isGraph ) ) {s = 29;}

                        else if ( ( !isGraph ) ) {s = 30;}

                         
                        input.seek(index1_14);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA1_15 = input.LA(1);

                         
                        int index1_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ( isGraph ) ) {s = 29;}

                        else if ( ( !isGraph ) ) {s = 30;}

                         
                        input.seek(index1_15);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA1_16 = input.LA(1);

                         
                        int index1_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ( isGraph ) ) {s = 29;}

                        else if ( ( !isGraph ) ) {s = 30;}

                         
                        input.seek(index1_16);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA1_17 = input.LA(1);

                         
                        int index1_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ( isGraph ) ) {s = 29;}

                        else if ( ( !isGraph ) ) {s = 30;}

                         
                        input.seek(index1_17);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA1_18 = input.LA(1);

                         
                        int index1_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ( isGraph ) ) {s = 29;}

                        else if ( ( !isGraph ) ) {s = 30;}

                         
                        input.seek(index1_18);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA1_19 = input.LA(1);

                         
                        int index1_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ( isGraph ) ) {s = 29;}

                        else if ( ( !isGraph ) ) {s = 30;}

                         
                        input.seek(index1_19);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA1_20 = input.LA(1);

                         
                        int index1_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ( isGraph ) ) {s = 29;}

                        else if ( ( !isGraph ) ) {s = 30;}

                         
                        input.seek(index1_20);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA1_21 = input.LA(1);

                         
                        int index1_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ( isGraph ) ) {s = 29;}

                        else if ( ( !isGraph ) ) {s = 30;}

                         
                        input.seek(index1_21);
                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA1_22 = input.LA(1);

                         
                        int index1_22 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ( isGraph ) ) {s = 29;}

                        else if ( ( !isGraph ) ) {s = 30;}

                         
                        input.seek(index1_22);
                        if ( s>=0 ) return s;
                        break;
                    case 23 : 
                        int LA1_23 = input.LA(1);

                         
                        int index1_23 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ( isGraph ) ) {s = 29;}

                        else if ( ( !isGraph ) ) {s = 30;}

                         
                        input.seek(index1_23);
                        if ( s>=0 ) return s;
                        break;
                    case 24 : 
                        int LA1_24 = input.LA(1);

                         
                        int index1_24 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ( isGraph ) ) {s = 29;}

                        else if ( ( !isGraph ) ) {s = 30;}

                         
                        input.seek(index1_24);
                        if ( s>=0 ) return s;
                        break;
                    case 25 : 
                        int LA1_25 = input.LA(1);

                         
                        int index1_25 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ( isGraph ) ) {s = 29;}

                        else if ( ( !isGraph ) ) {s = 30;}

                         
                        input.seek(index1_25);
                        if ( s>=0 ) return s;
                        break;
                    case 26 : 
                        int LA1_26 = input.LA(1);

                         
                        int index1_26 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ( isGraph ) ) {s = 29;}

                        else if ( ( !isGraph ) ) {s = 30;}

                         
                        input.seek(index1_26);
                        if ( s>=0 ) return s;
                        break;
                    case 27 : 
                        int LA1_27 = input.LA(1);

                         
                        int index1_27 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ( isGraph ) ) {s = 29;}

                        else if ( ( !isGraph ) ) {s = 30;}

                         
                        input.seek(index1_27);
                        if ( s>=0 ) return s;
                        break;
                    case 28 : 
                        int LA1_28 = input.LA(1);

                         
                        int index1_28 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ( isGraph ) ) {s = 29;}

                        else if ( ( !isGraph ) ) {s = 30;}

                         
                        input.seek(index1_28);
                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 1, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA2_eotS =
        "\20\uffff";
    static final String DFA2_eofS =
        "\2\uffff\12\1\1\uffff\3\1";
    static final String DFA2_minS =
        "\1\4\1\uffff\12\4\1\uffff\3\4";
    static final String DFA2_maxS =
        "\1\117\1\uffff\12\117\1\uffff\3\117";
    static final String DFA2_acceptS =
        "\1\uffff\1\2\12\uffff\1\1\3\uffff";
    static final String DFA2_specialS =
        "\20\uffff}>";
    static final String[] DFA2_transitionS = {
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

    static final short[] DFA2_eot = DFA.unpackEncodedString(DFA2_eotS);
    static final short[] DFA2_eof = DFA.unpackEncodedString(DFA2_eofS);
    static final char[] DFA2_min = DFA.unpackEncodedStringToUnsignedChars(DFA2_minS);
    static final char[] DFA2_max = DFA.unpackEncodedStringToUnsignedChars(DFA2_maxS);
    static final short[] DFA2_accept = DFA.unpackEncodedString(DFA2_acceptS);
    static final short[] DFA2_special = DFA.unpackEncodedString(DFA2_specialS);
    static final short[][] DFA2_transition;

    static {
        int numStates = DFA2_transitionS.length;
        DFA2_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
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
            return "()* loopback of 58:3: ( prefix )*";
        }
    }
    static final String DFA11_eotS =
        "\15\uffff";
    static final String DFA11_eofS =
        "\2\uffff\7\11\4\uffff";
    static final String DFA11_minS =
        "\1\4\1\uffff\7\4\4\uffff";
    static final String DFA11_maxS =
        "\1\117\1\uffff\7\117\4\uffff";
    static final String DFA11_acceptS =
        "\1\uffff\1\1\7\uffff\1\5\1\2\1\3\1\4";
    static final String DFA11_specialS =
        "\15\uffff}>";
    static final String[] DFA11_transitionS = {
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

    static final short[] DFA11_eot = DFA.unpackEncodedString(DFA11_eotS);
    static final short[] DFA11_eof = DFA.unpackEncodedString(DFA11_eofS);
    static final char[] DFA11_min = DFA.unpackEncodedStringToUnsignedChars(DFA11_minS);
    static final char[] DFA11_max = DFA.unpackEncodedStringToUnsignedChars(DFA11_maxS);
    static final short[] DFA11_accept = DFA.unpackEncodedString(DFA11_acceptS);
    static final short[] DFA11_special = DFA.unpackEncodedString(DFA11_specialS);
    static final short[][] DFA11_transition;

    static {
        int numStates = DFA11_transitionS.length;
        DFA11_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA11_transition[i] = DFA.unpackEncodedString(DFA11_transitionS[i]);
        }
    }

    class DFA11 extends DFA {

        public DFA11(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 11;
            this.eot = DFA11_eot;
            this.eof = DFA11_eof;
            this.min = DFA11_min;
            this.max = DFA11_max;
            this.accept = DFA11_accept;
            this.special = DFA11_special;
            this.transition = DFA11_transition;
        }
        public String getDescription() {
            return "65:1: actualGraphLabel : ( COLON ( . )* | remP COLON ( . )* | valueLabel | nodeLabel | (~ COLON )+ );";
        }
    }
    static final String DFA15_eotS =
        "\14\uffff";
    static final String DFA15_eofS =
        "\1\1\1\uffff\11\1\1\uffff";
    static final String DFA15_minS =
        "\1\4\1\uffff\11\4\1\uffff";
    static final String DFA15_maxS =
        "\1\117\1\uffff\11\117\1\uffff";
    static final String DFA15_acceptS =
        "\1\uffff\1\2\11\uffff\1\1";
    static final String DFA15_specialS =
        "\14\uffff}>";
    static final String[] DFA15_transitionS = {
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

    static final short[] DFA15_eot = DFA.unpackEncodedString(DFA15_eotS);
    static final short[] DFA15_eof = DFA.unpackEncodedString(DFA15_eofS);
    static final char[] DFA15_min = DFA.unpackEncodedStringToUnsignedChars(DFA15_minS);
    static final char[] DFA15_max = DFA.unpackEncodedStringToUnsignedChars(DFA15_maxS);
    static final short[] DFA15_accept = DFA.unpackEncodedString(DFA15_acceptS);
    static final short[] DFA15_special = DFA.unpackEncodedString(DFA15_specialS);
    static final short[][] DFA15_transition;

    static {
        int numStates = DFA15_transitionS.length;
        DFA15_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA15_transition[i] = DFA.unpackEncodedString(DFA15_transitionS[i]);
        }
    }

    class DFA15 extends DFA {

        public DFA15(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 15;
            this.eot = DFA15_eot;
            this.eof = DFA15_eof;
            this.min = DFA15_min;
            this.max = DFA15_max;
            this.accept = DFA15_accept;
            this.special = DFA15_special;
            this.transition = DFA15_transition;
        }
        public String getDescription() {
            return "()* loopback of 83:6: ( prefix )*";
        }
    }
    static final String DFA19_eotS =
        "\32\uffff";
    static final String DFA19_eofS =
        "\1\17\1\uffff\14\17\5\uffff\4\30\3\uffff";
    static final String DFA19_minS =
        "\1\4\1\uffff\14\4\5\uffff\4\33\3\uffff";
    static final String DFA19_maxS =
        "\1\117\1\uffff\14\117\5\uffff\1\35\1\36\1\37\1\117\3\uffff";
    static final String DFA19_acceptS =
        "\1\uffff\1\1\14\uffff\1\10\1\11\1\2\1\3\1\4\4\uffff\1\6\1\7\1\5";
    static final String DFA19_specialS =
        "\32\uffff}>";
    static final String[] DFA19_transitionS = {
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

    static final short[] DFA19_eot = DFA.unpackEncodedString(DFA19_eotS);
    static final short[] DFA19_eof = DFA.unpackEncodedString(DFA19_eofS);
    static final char[] DFA19_min = DFA.unpackEncodedStringToUnsignedChars(DFA19_minS);
    static final char[] DFA19_max = DFA.unpackEncodedStringToUnsignedChars(DFA19_maxS);
    static final short[] DFA19_accept = DFA.unpackEncodedString(DFA19_acceptS);
    static final short[] DFA19_special = DFA.unpackEncodedString(DFA19_specialS);
    static final short[][] DFA19_transition;

    static {
        int numStates = DFA19_transitionS.length;
        DFA19_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA19_transition[i] = DFA.unpackEncodedString(DFA19_transitionS[i]);
        }
    }

    class DFA19 extends DFA {

        public DFA19(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 19;
            this.eot = DFA19_eot;
            this.eof = DFA19_eof;
            this.min = DFA19_min;
            this.max = DFA19_max;
            this.accept = DFA19_accept;
            this.special = DFA19_special;
            this.transition = DFA19_transition;
        }
        public String getDescription() {
            return "85:1: actualRuleLabel : ( COLON ( . )* | remP COLON ( . )* | parP COLON | pathP COLON ( PLING )? regExpr | valueLabel | nodeLabel | attrLabel | negLabel | posLabel );";
        }
    }
 

    public static final BitSet FOLLOW_graphLabel_in_label188 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleLabel_in_label199 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_prefix_in_graphLabel216 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_actualGraphLabel_in_graphLabel219 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_forallP_in_prefix233 = new BitSet(new long[]{0x0000000014000000L});
    public static final BitSet FOLLOW_forallxP_in_prefix238 = new BitSet(new long[]{0x0000000014000000L});
    public static final BitSet FOLLOW_existsP_in_prefix242 = new BitSet(new long[]{0x0000000014000000L});
    public static final BitSet FOLLOW_EQUALS_in_prefix247 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_IDENT_in_prefix249 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_prefix253 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_newP_in_prefix263 = new BitSet(new long[]{0x0000000014000000L});
    public static final BitSet FOLLOW_delP_in_prefix267 = new BitSet(new long[]{0x0000000014000000L});
    public static final BitSet FOLLOW_notP_in_prefix271 = new BitSet(new long[]{0x0000000014000000L});
    public static final BitSet FOLLOW_useP_in_prefix275 = new BitSet(new long[]{0x0000000014000000L});
    public static final BitSet FOLLOW_cnewP_in_prefix279 = new BitSet(new long[]{0x0000000014000000L});
    public static final BitSet FOLLOW_EQUALS_in_prefix284 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_IDENT_in_prefix286 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_prefix290 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nestedP_in_prefix298 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_prefix300 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_actualGraphLabel311 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_remP_in_actualGraphLabel321 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_actualGraphLabel323 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_valueLabel_in_actualGraphLabel333 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nodeLabel_in_actualGraphLabel340 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_actualGraphLabel348 = new BitSet(new long[]{0xFFFFFFFFEFFFFFF2L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_intP_in_valueLabel366 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_valueLabel368 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_NUMBER_in_valueLabel370 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_realP_in_valueLabel385 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_valueLabel387 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_RNUMBER_in_valueLabel389 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_stringP_in_valueLabel404 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_valueLabel406 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_DQTEXT_in_valueLabel408 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_boolP_in_valueLabel423 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_valueLabel425 = new BitSet(new long[]{0x0000000000000000L,0x000000000000C000L});
    public static final BitSet FOLLOW_trueP_in_valueLabel428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_falseP_in_valueLabel432 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeP_in_nodeLabel447 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_nodeLabel449 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_IDENT_in_nodeLabel451 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_flagP_in_nodeLabel466 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_nodeLabel468 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_IDENT_in_nodeLabel470 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_prefix_in_ruleLabel489 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_actualRuleLabel_in_ruleLabel492 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_actualRuleLabel507 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_remP_in_actualRuleLabel517 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_actualRuleLabel519 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_parP_in_actualRuleLabel529 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_actualRuleLabel531 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathP_in_actualRuleLabel538 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_actualRuleLabel540 = new BitSet(new long[]{0x0000193104000000L});
    public static final BitSet FOLLOW_PLING_in_actualRuleLabel542 = new BitSet(new long[]{0x0000193104000000L});
    public static final BitSet FOLLOW_regExpr_in_actualRuleLabel545 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_valueLabel_in_actualRuleLabel552 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nodeLabel_in_actualRuleLabel559 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attrLabel_in_actualRuleLabel566 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_negLabel_in_actualRuleLabel573 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_posLabel_in_actualRuleLabel580 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_intP_in_attrLabel595 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_attrLabel597 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_IDENT_in_attrLabel599 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_realP_in_attrLabel615 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_attrLabel617 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_IDENT_in_attrLabel619 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_stringP_in_attrLabel635 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_attrLabel637 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_IDENT_in_attrLabel639 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_boolP_in_attrLabel655 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_attrLabel657 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_IDENT_in_attrLabel659 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attrP_in_attrLabel675 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_attrLabel677 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_prodP_in_attrLabel692 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_attrLabel694 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_argP_in_attrLabel709 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_COLON_in_attrLabel711 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_DIGIT_in_attrLabel713 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_PLING_in_negLabel733 = new BitSet(new long[]{0xFFFFFFF6EFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_posLabel_in_negLabel735 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_wildcard_in_posLabel750 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_posLabel757 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_posLabel764 = new BitSet(new long[]{0x0000193104000000L});
    public static final BitSet FOLLOW_regExpr_in_posLabel766 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_RBRACE_in_posLabel768 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SQTEXT_in_posLabel775 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_posLabel783 = new BitSet(new long[]{0xFFFFFFC2EBFFFFF2L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_choice_in_regExpr815 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sequence_in_choice827 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_BAR_in_choice830 = new BitSet(new long[]{0x0000193104000000L});
    public static final BitSet FOLLOW_choice_in_choice833 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unary_in_sequence847 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_DOT_in_sequence850 = new BitSet(new long[]{0x0000193104000000L});
    public static final BitSet FOLLOW_sequence_in_sequence853 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_unary867 = new BitSet(new long[]{0x0000193104000000L});
    public static final BitSet FOLLOW_unary_in_unary869 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_unary876 = new BitSet(new long[]{0x0000060000000002L});
    public static final BitSet FOLLOW_STAR_in_unary879 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_unary884 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SQTEXT_in_atom899 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTCHAR_in_atom906 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_EQUALS_in_atom914 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_atom921 = new BitSet(new long[]{0x0000193104000000L});
    public static final BitSet FOLLOW_regExpr_in_atom923 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RPAR_in_atom925 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_wildcard_in_atom932 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUERY_in_wildcard944 = new BitSet(new long[]{0x0000400008000000L});
    public static final BitSet FOLLOW_IDENT_in_wildcard946 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_LSQUARE_in_wildcard949 = new BitSet(new long[]{0x0000993104000000L});
    public static final BitSet FOLLOW_HAT_in_wildcard951 = new BitSet(new long[]{0x0000993104000000L});
    public static final BitSet FOLLOW_atom_in_wildcard954 = new BitSet(new long[]{0x0003000000000000L});
    public static final BitSet FOLLOW_COMMA_in_wildcard957 = new BitSet(new long[]{0x0000993104000000L});
    public static final BitSet FOLLOW_atom_in_wildcard959 = new BitSet(new long[]{0x0003000000000000L});
    public static final BitSet FOLLOW_RSQUARE_in_wildcard963 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_57_in_newP1058 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_58_in_delP1068 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_59_in_cnewP1077 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_60_in_notP1087 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_61_in_useP1097 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_62_in_remP1107 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_63_in_forallP1115 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_64_in_forallxP1122 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_existsP1129 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_nestedP1136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_67_in_parP1147 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_68_in_attrP1157 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_69_in_prodP1166 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_70_in_argP1176 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_71_in_intP1186 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_72_in_realP1195 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_73_in_stringP1202 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_74_in_boolP1211 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_75_in_typeP1221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_76_in_flagP1230 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_77_in_pathP1239 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_78_in_trueP1249 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_79_in_falseP1257 = new BitSet(new long[]{0x0000000000000002L});

}
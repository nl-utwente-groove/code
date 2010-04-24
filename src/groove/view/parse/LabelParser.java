// $ANTLR 3.1b1 Label.g 2010-04-23 15:26:07

package groove.view.parse;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;

@SuppressWarnings("all")              
public class LabelParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "NEW", "DEL", "NOT", "USE", "CNEW", "REM", "FORALL", "FORALLX", "EXISTS", "NESTED", "INT", "REAL", "STRING", "BOOL", "ATTR", "PROD", "ARG", "PAR", "TYPE", "FLAG", "PATH", "EMPTY", "ATOM", "TRUE", "FALSE", "EQUALS", "COLON", "DOLLAR", "PLING", "LBRACE", "RBRACE", "QUERY", "SQUOTE", "BSLASH", "DQTEXT", "BAR", "DOT", "MINUS", "STAR", "PLUS", "LPAR", "RPAR", "IDENTCHAR", "LSQUARE", "HAT", "COMMA", "RSQUARE", "DQUOTE", "UNDER", "LETTER", "DIGIT"
    };
    public static final int DOLLAR=31;
    public static final int STAR=42;
    public static final int LSQUARE=47;
    public static final int FORALLX=11;
    public static final int LETTER=53;
    public static final int DEL=5;
    public static final int DQTEXT=38;
    public static final int LBRACE=33;
    public static final int NEW=4;
    public static final int DQUOTE=51;
    public static final int IDENTCHAR=46;
    public static final int EQUALS=29;
    public static final int NOT=6;
    public static final int ATOM=26;
    public static final int EOF=-1;
    public static final int TYPE=22;
    public static final int HAT=48;
    public static final int UNDER=52;
    public static final int PLING=32;
    public static final int LPAR=44;
    public static final int ARG=20;
    public static final int COMMA=49;
    public static final int PATH=24;
    public static final int PROD=19;
    public static final int PAR=21;
    public static final int PLUS=43;
    public static final int DIGIT=54;
    public static final int EXISTS=12;
    public static final int DOT=40;
    public static final int ATTR=18;
    public static final int RBRACE=34;
    public static final int BOOL=17;
    public static final int FORALL=10;
    public static final int INT=14;
    public static final int RSQUARE=50;
    public static final int REM=9;
    public static final int SQUOTE=36;
    public static final int MINUS=41;
    public static final int TRUE=27;
    public static final int CNEW=8;
    public static final int FLAG=23;
    public static final int EMPTY=25;
    public static final int NESTED=13;
    public static final int COLON=30;
    public static final int REAL=15;
    public static final int QUERY=35;
    public static final int RPAR=45;
    public static final int USE=7;
    public static final int FALSE=28;
    public static final int BSLASH=37;
    public static final int BAR=39;
    public static final int STRING=16;

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
    // Label.g:56:1: label : ( prefix )* ( actualLabel )? ;
    public final LabelParser.label_return label() throws RecognitionException {
        LabelParser.label_return retval = new LabelParser.label_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        LabelParser.prefix_return prefix1 = null;

        LabelParser.actualLabel_return actualLabel2 = null;



        try {
            // Label.g:57:4: ( ( prefix )* ( actualLabel )? )
            // Label.g:57:6: ( prefix )* ( actualLabel )?
            {
            root_0 = (Object)adaptor.nil();

            // Label.g:57:6: ( prefix )*
            loop1:
            do {
                int alt1=2;
                alt1 = dfa1.predict(input);
                switch (alt1) {
            	case 1 :
            	    // Label.g:57:6: prefix
            	    {
            	    pushFollow(FOLLOW_prefix_in_label248);
            	    prefix1=prefix();

            	    state._fsp--;

            	    adaptor.addChild(root_0, prefix1.getTree());

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            // Label.g:57:14: ( actualLabel )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==REM||(LA2_0>=INT && LA2_0<=PATH)||LA2_0==COLON) ) {
                alt2=1;
            }
            else if ( ((LA2_0>=NEW && LA2_0<=CNEW)||(LA2_0>=FORALL && LA2_0<=NESTED)||(LA2_0>=EMPTY && LA2_0<=EQUALS)||(LA2_0>=DOLLAR && LA2_0<=LBRACE)||(LA2_0>=QUERY && LA2_0<=SQUOTE)||(LA2_0>=DQTEXT && LA2_0<=DIGIT)) && ( isGraph )) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // Label.g:57:14: actualLabel
                    {
                    pushFollow(FOLLOW_actualLabel_in_label251);
                    actualLabel2=actualLabel();

                    state._fsp--;

                    adaptor.addChild(root_0, actualLabel2.getTree());

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
    // $ANTLR end label

    public static class prefix_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start prefix
    // Label.g:60:1: prefix : ( ( FORALL | FORALLX | EXISTS ) ( EQUALS ident )? COLON | ( NEW | DEL | NOT | USE | CNEW ) ( EQUALS ident )? COLON | NESTED COLON );
    public final LabelParser.prefix_return prefix() throws RecognitionException {
        LabelParser.prefix_return retval = new LabelParser.prefix_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token FORALL3=null;
        Token FORALLX4=null;
        Token EXISTS5=null;
        Token EQUALS6=null;
        Token COLON8=null;
        Token NEW9=null;
        Token DEL10=null;
        Token NOT11=null;
        Token USE12=null;
        Token CNEW13=null;
        Token EQUALS14=null;
        Token COLON16=null;
        Token NESTED17=null;
        Token COLON18=null;
        LabelParser.ident_return ident7 = null;

        LabelParser.ident_return ident15 = null;


        Object FORALL3_tree=null;
        Object FORALLX4_tree=null;
        Object EXISTS5_tree=null;
        Object EQUALS6_tree=null;
        Object COLON8_tree=null;
        Object NEW9_tree=null;
        Object DEL10_tree=null;
        Object NOT11_tree=null;
        Object USE12_tree=null;
        Object CNEW13_tree=null;
        Object EQUALS14_tree=null;
        Object COLON16_tree=null;
        Object NESTED17_tree=null;
        Object COLON18_tree=null;

        try {
            // Label.g:61:4: ( ( FORALL | FORALLX | EXISTS ) ( EQUALS ident )? COLON | ( NEW | DEL | NOT | USE | CNEW ) ( EQUALS ident )? COLON | NESTED COLON )
            int alt7=3;
            switch ( input.LA(1) ) {
            case FORALL:
            case FORALLX:
            case EXISTS:
                {
                alt7=1;
                }
                break;
            case NEW:
            case DEL:
            case NOT:
            case USE:
            case CNEW:
                {
                alt7=2;
                }
                break;
            case NESTED:
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
                    // Label.g:61:6: ( FORALL | FORALLX | EXISTS ) ( EQUALS ident )? COLON
                    {
                    root_0 = (Object)adaptor.nil();

                    // Label.g:61:6: ( FORALL | FORALLX | EXISTS )
                    int alt3=3;
                    switch ( input.LA(1) ) {
                    case FORALL:
                        {
                        alt3=1;
                        }
                        break;
                    case FORALLX:
                        {
                        alt3=2;
                        }
                        break;
                    case EXISTS:
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
                            // Label.g:61:8: FORALL
                            {
                            FORALL3=(Token)match(input,FORALL,FOLLOW_FORALL_in_prefix269); 
                            FORALL3_tree = (Object)adaptor.create(FORALL3);
                            root_0 = (Object)adaptor.becomeRoot(FORALL3_tree, root_0);


                            }
                            break;
                        case 2 :
                            // Label.g:61:18: FORALLX
                            {
                            FORALLX4=(Token)match(input,FORALLX,FOLLOW_FORALLX_in_prefix274); 
                            FORALLX4_tree = (Object)adaptor.create(FORALLX4);
                            root_0 = (Object)adaptor.becomeRoot(FORALLX4_tree, root_0);


                            }
                            break;
                        case 3 :
                            // Label.g:61:29: EXISTS
                            {
                            EXISTS5=(Token)match(input,EXISTS,FOLLOW_EXISTS_in_prefix279); 
                            EXISTS5_tree = (Object)adaptor.create(EXISTS5);
                            root_0 = (Object)adaptor.becomeRoot(EXISTS5_tree, root_0);


                            }
                            break;

                    }

                    // Label.g:61:39: ( EQUALS ident )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==EQUALS) ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // Label.g:61:40: EQUALS ident
                            {
                            EQUALS6=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_prefix285); 
                            EQUALS6_tree = (Object)adaptor.create(EQUALS6);
                            adaptor.addChild(root_0, EQUALS6_tree);

                            pushFollow(FOLLOW_ident_in_prefix287);
                            ident7=ident();

                            state._fsp--;

                            adaptor.addChild(root_0, ident7.getTree());

                            }
                            break;

                    }

                    COLON8=(Token)match(input,COLON,FOLLOW_COLON_in_prefix291); 

                    }
                    break;
                case 2 :
                    // Label.g:62:6: ( NEW | DEL | NOT | USE | CNEW ) ( EQUALS ident )? COLON
                    {
                    root_0 = (Object)adaptor.nil();

                    // Label.g:62:6: ( NEW | DEL | NOT | USE | CNEW )
                    int alt5=5;
                    switch ( input.LA(1) ) {
                    case NEW:
                        {
                        alt5=1;
                        }
                        break;
                    case DEL:
                        {
                        alt5=2;
                        }
                        break;
                    case NOT:
                        {
                        alt5=3;
                        }
                        break;
                    case USE:
                        {
                        alt5=4;
                        }
                        break;
                    case CNEW:
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
                            // Label.g:62:8: NEW
                            {
                            NEW9=(Token)match(input,NEW,FOLLOW_NEW_in_prefix302); 
                            NEW9_tree = (Object)adaptor.create(NEW9);
                            root_0 = (Object)adaptor.becomeRoot(NEW9_tree, root_0);


                            }
                            break;
                        case 2 :
                            // Label.g:62:15: DEL
                            {
                            DEL10=(Token)match(input,DEL,FOLLOW_DEL_in_prefix307); 
                            DEL10_tree = (Object)adaptor.create(DEL10);
                            root_0 = (Object)adaptor.becomeRoot(DEL10_tree, root_0);


                            }
                            break;
                        case 3 :
                            // Label.g:62:22: NOT
                            {
                            NOT11=(Token)match(input,NOT,FOLLOW_NOT_in_prefix312); 
                            NOT11_tree = (Object)adaptor.create(NOT11);
                            root_0 = (Object)adaptor.becomeRoot(NOT11_tree, root_0);


                            }
                            break;
                        case 4 :
                            // Label.g:62:29: USE
                            {
                            USE12=(Token)match(input,USE,FOLLOW_USE_in_prefix317); 
                            USE12_tree = (Object)adaptor.create(USE12);
                            root_0 = (Object)adaptor.becomeRoot(USE12_tree, root_0);


                            }
                            break;
                        case 5 :
                            // Label.g:62:36: CNEW
                            {
                            CNEW13=(Token)match(input,CNEW,FOLLOW_CNEW_in_prefix322); 
                            CNEW13_tree = (Object)adaptor.create(CNEW13);
                            root_0 = (Object)adaptor.becomeRoot(CNEW13_tree, root_0);


                            }
                            break;

                    }

                    // Label.g:62:44: ( EQUALS ident )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==EQUALS) ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // Label.g:62:45: EQUALS ident
                            {
                            EQUALS14=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_prefix328); 
                            EQUALS14_tree = (Object)adaptor.create(EQUALS14);
                            adaptor.addChild(root_0, EQUALS14_tree);

                            pushFollow(FOLLOW_ident_in_prefix330);
                            ident15=ident();

                            state._fsp--;

                            adaptor.addChild(root_0, ident15.getTree());

                            }
                            break;

                    }

                    COLON16=(Token)match(input,COLON,FOLLOW_COLON_in_prefix334); 

                    }
                    break;
                case 3 :
                    // Label.g:63:6: NESTED COLON
                    {
                    root_0 = (Object)adaptor.nil();

                    NESTED17=(Token)match(input,NESTED,FOLLOW_NESTED_in_prefix342); 
                    NESTED17_tree = (Object)adaptor.create(NESTED17);
                    adaptor.addChild(root_0, NESTED17_tree);

                    COLON18=(Token)match(input,COLON,FOLLOW_COLON_in_prefix344); 
                    COLON18_tree = (Object)adaptor.create(COLON18);
                    root_0 = (Object)adaptor.becomeRoot(COLON18_tree, root_0);


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

    public static class actualLabel_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start actualLabel
    // Label.g:65:1: actualLabel : ( COLON a= ( ( . )* ) -> ^( ATOM $a) | 'rem' COLON ( options {greedy=true; } : . )* | PAR ( EQUALS DOLLAR number )? COLON | PATH COLON ( PLING )? regExpr | attrLabel | nodeLabel | {...}? => ruleLabel );
    public final LabelParser.actualLabel_return actualLabel() throws RecognitionException {
        LabelParser.actualLabel_return retval = new LabelParser.actualLabel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token a=null;
        Token COLON19=null;
        Token wildcard20=null;
        Token string_literal21=null;
        Token COLON22=null;
        Token wildcard23=null;
        Token PAR24=null;
        Token EQUALS25=null;
        Token DOLLAR26=null;
        Token COLON28=null;
        Token PATH29=null;
        Token COLON30=null;
        Token PLING31=null;
        LabelParser.number_return number27 = null;

        LabelParser.regExpr_return regExpr32 = null;

        LabelParser.attrLabel_return attrLabel33 = null;

        LabelParser.nodeLabel_return nodeLabel34 = null;

        LabelParser.ruleLabel_return ruleLabel35 = null;


        Object a_tree=null;
        Object COLON19_tree=null;
        Object wildcard20_tree=null;
        Object string_literal21_tree=null;
        Object COLON22_tree=null;
        Object wildcard23_tree=null;
        Object PAR24_tree=null;
        Object EQUALS25_tree=null;
        Object DOLLAR26_tree=null;
        Object COLON28_tree=null;
        Object PATH29_tree=null;
        Object COLON30_tree=null;
        Object PLING31_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");

        try {
            // Label.g:66:4: ( COLON a= ( ( . )* ) -> ^( ATOM $a) | 'rem' COLON ( options {greedy=true; } : . )* | PAR ( EQUALS DOLLAR number )? COLON | PATH COLON ( PLING )? regExpr | attrLabel | nodeLabel | {...}? => ruleLabel )
            int alt12=7;
            alt12 = dfa12.predict(input);
            switch (alt12) {
                case 1 :
                    // Label.g:66:6: COLON a= ( ( . )* )
                    {
                    COLON19=(Token)match(input,COLON,FOLLOW_COLON_in_actualLabel356);  
                    stream_COLON.add(COLON19);

                    // Label.g:66:14: ( ( . )* )
                    // Label.g:66:15: ( . )*
                    {
                    // Label.g:66:15: ( . )*
                    loop8:
                    do {
                        int alt8=2;
                        int LA8_0 = input.LA(1);

                        if ( ((LA8_0>=NEW && LA8_0<=DIGIT)) ) {
                            alt8=1;
                        }
                        else if ( (LA8_0==EOF) ) {
                            alt8=2;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // Label.g:66:15: .
                    	    {
                    	    wildcard20=(Token)input.LT(1);
                    	    matchAny(input); 

                    	    }
                    	    break;

                    	default :
                    	    break loop8;
                        }
                    } while (true);


                    }



                    // AST REWRITE
                    // elements: a
                    // token labels: a
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_a=new RewriteRuleTokenStream(adaptor,"token a",a);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 66:19: -> ^( ATOM $a)
                    {
                        // Label.g:66:22: ^( ATOM $a)
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ATOM, "ATOM"), root_1);

                        adaptor.addChild(root_1, stream_a.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 2 :
                    // Label.g:67:6: 'rem' COLON ( options {greedy=true; } : . )*
                    {
                    root_0 = (Object)adaptor.nil();

                    string_literal21=(Token)match(input,REM,FOLLOW_REM_in_actualLabel379); 
                    string_literal21_tree = (Object)adaptor.create(string_literal21);
                    root_0 = (Object)adaptor.becomeRoot(string_literal21_tree, root_0);

                    COLON22=(Token)match(input,COLON,FOLLOW_COLON_in_actualLabel382); 
                    // Label.g:67:20: ( options {greedy=true; } : . )*
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( ((LA9_0>=NEW && LA9_0<=DIGIT)) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // Label.g:67:51: .
                    	    {
                    	    wildcard23=(Token)input.LT(1);
                    	    matchAny(input); 
                    	    wildcard23_tree = (Object)adaptor.create(wildcard23);
                    	    adaptor.addChild(root_0, wildcard23_tree);


                    	    }
                    	    break;

                    	default :
                    	    break loop9;
                        }
                    } while (true);


                    }
                    break;
                case 3 :
                    // Label.g:68:6: PAR ( EQUALS DOLLAR number )? COLON
                    {
                    root_0 = (Object)adaptor.nil();

                    PAR24=(Token)match(input,PAR,FOLLOW_PAR_in_actualLabel412); 
                    PAR24_tree = (Object)adaptor.create(PAR24);
                    root_0 = (Object)adaptor.becomeRoot(PAR24_tree, root_0);

                    // Label.g:68:11: ( EQUALS DOLLAR number )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0==EQUALS) ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // Label.g:68:12: EQUALS DOLLAR number
                            {
                            EQUALS25=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_actualLabel416); 
                            DOLLAR26=(Token)match(input,DOLLAR,FOLLOW_DOLLAR_in_actualLabel419); 
                            pushFollow(FOLLOW_number_in_actualLabel422);
                            number27=number();

                            state._fsp--;

                            adaptor.addChild(root_0, number27.getTree());

                            }
                            break;

                    }

                    COLON28=(Token)match(input,COLON,FOLLOW_COLON_in_actualLabel426); 

                    }
                    break;
                case 4 :
                    // Label.g:69:6: PATH COLON ( PLING )? regExpr
                    {
                    root_0 = (Object)adaptor.nil();

                    PATH29=(Token)match(input,PATH,FOLLOW_PATH_in_actualLabel434); 
                    PATH29_tree = (Object)adaptor.create(PATH29);
                    root_0 = (Object)adaptor.becomeRoot(PATH29_tree, root_0);

                    COLON30=(Token)match(input,COLON,FOLLOW_COLON_in_actualLabel437); 
                    // Label.g:69:19: ( PLING )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==PLING) ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // Label.g:69:19: PLING
                            {
                            PLING31=(Token)match(input,PLING,FOLLOW_PLING_in_actualLabel440); 
                            PLING31_tree = (Object)adaptor.create(PLING31);
                            adaptor.addChild(root_0, PLING31_tree);


                            }
                            break;

                    }

                    pushFollow(FOLLOW_regExpr_in_actualLabel443);
                    regExpr32=regExpr();

                    state._fsp--;

                    adaptor.addChild(root_0, regExpr32.getTree());

                    }
                    break;
                case 5 :
                    // Label.g:70:6: attrLabel
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_attrLabel_in_actualLabel450);
                    attrLabel33=attrLabel();

                    state._fsp--;

                    adaptor.addChild(root_0, attrLabel33.getTree());

                    }
                    break;
                case 6 :
                    // Label.g:71:6: nodeLabel
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_nodeLabel_in_actualLabel457);
                    nodeLabel34=nodeLabel();

                    state._fsp--;

                    adaptor.addChild(root_0, nodeLabel34.getTree());

                    }
                    break;
                case 7 :
                    // Label.g:73:6: {...}? => ruleLabel
                    {
                    root_0 = (Object)adaptor.nil();

                    if ( !( isGraph ) ) {
                        throw new FailedPredicateException(input, "actualLabel", " isGraph ");
                    }
                    pushFollow(FOLLOW_ruleLabel_in_actualLabel473);
                    ruleLabel35=ruleLabel();

                    state._fsp--;

                    adaptor.addChild(root_0, ruleLabel35.getTree());

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
    // $ANTLR end actualLabel

    public static class ruleLabel_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start ruleLabel
    // Label.g:76:1: ruleLabel : ( wildcard | EQUALS | LBRACE regExpr RBRACE | sqText -> ^( ATOM sqText ) | PLING ruleLabel | ruleDefault -> ^( ATOM ruleDefault ) );
    public final LabelParser.ruleLabel_return ruleLabel() throws RecognitionException {
        LabelParser.ruleLabel_return retval = new LabelParser.ruleLabel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQUALS37=null;
        Token LBRACE38=null;
        Token RBRACE40=null;
        Token PLING42=null;
        LabelParser.wildcard_return wildcard36 = null;

        LabelParser.regExpr_return regExpr39 = null;

        LabelParser.sqText_return sqText41 = null;

        LabelParser.ruleLabel_return ruleLabel43 = null;

        LabelParser.ruleDefault_return ruleDefault44 = null;


        Object EQUALS37_tree=null;
        Object LBRACE38_tree=null;
        Object RBRACE40_tree=null;
        Object PLING42_tree=null;
        RewriteRuleSubtreeStream stream_ruleDefault=new RewriteRuleSubtreeStream(adaptor,"rule ruleDefault");
        RewriteRuleSubtreeStream stream_sqText=new RewriteRuleSubtreeStream(adaptor,"rule sqText");
        try {
            // Label.g:77:4: ( wildcard | EQUALS | LBRACE regExpr RBRACE | sqText -> ^( ATOM sqText ) | PLING ruleLabel | ruleDefault -> ^( ATOM ruleDefault ) )
            int alt13=6;
            switch ( input.LA(1) ) {
            case QUERY:
                {
                alt13=1;
                }
                break;
            case EQUALS:
                {
                alt13=2;
                }
                break;
            case LBRACE:
                {
                alt13=3;
                }
                break;
            case SQUOTE:
                {
                alt13=4;
                }
                break;
            case PLING:
                {
                alt13=5;
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
            case DOLLAR:
            case DQTEXT:
            case BAR:
            case DOT:
            case MINUS:
            case STAR:
            case PLUS:
            case LPAR:
            case RPAR:
            case IDENTCHAR:
            case LSQUARE:
            case HAT:
            case COMMA:
            case RSQUARE:
            case DQUOTE:
            case UNDER:
            case LETTER:
            case DIGIT:
                {
                alt13=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;
            }

            switch (alt13) {
                case 1 :
                    // Label.g:77:6: wildcard
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_wildcard_in_ruleLabel488);
                    wildcard36=wildcard();

                    state._fsp--;

                    adaptor.addChild(root_0, wildcard36.getTree());

                    }
                    break;
                case 2 :
                    // Label.g:78:6: EQUALS
                    {
                    root_0 = (Object)adaptor.nil();

                    EQUALS37=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_ruleLabel495); 
                    EQUALS37_tree = (Object)adaptor.create(EQUALS37);
                    adaptor.addChild(root_0, EQUALS37_tree);


                    }
                    break;
                case 3 :
                    // Label.g:79:6: LBRACE regExpr RBRACE
                    {
                    root_0 = (Object)adaptor.nil();

                    LBRACE38=(Token)match(input,LBRACE,FOLLOW_LBRACE_in_ruleLabel502); 
                    pushFollow(FOLLOW_regExpr_in_ruleLabel505);
                    regExpr39=regExpr();

                    state._fsp--;

                    adaptor.addChild(root_0, regExpr39.getTree());
                    RBRACE40=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_ruleLabel507); 

                    }
                    break;
                case 4 :
                    // Label.g:80:6: sqText
                    {
                    pushFollow(FOLLOW_sqText_in_ruleLabel515);
                    sqText41=sqText();

                    state._fsp--;

                    stream_sqText.add(sqText41.getTree());


                    // AST REWRITE
                    // elements: sqText
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 80:13: -> ^( ATOM sqText )
                    {
                        // Label.g:80:16: ^( ATOM sqText )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ATOM, "ATOM"), root_1);

                        adaptor.addChild(root_1, stream_sqText.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 5 :
                    // Label.g:81:6: PLING ruleLabel
                    {
                    root_0 = (Object)adaptor.nil();

                    PLING42=(Token)match(input,PLING,FOLLOW_PLING_in_ruleLabel530); 
                    PLING42_tree = (Object)adaptor.create(PLING42);
                    root_0 = (Object)adaptor.becomeRoot(PLING42_tree, root_0);

                    pushFollow(FOLLOW_ruleLabel_in_ruleLabel533);
                    ruleLabel43=ruleLabel();

                    state._fsp--;

                    adaptor.addChild(root_0, ruleLabel43.getTree());

                    }
                    break;
                case 6 :
                    // Label.g:82:6: ruleDefault
                    {
                    pushFollow(FOLLOW_ruleDefault_in_ruleLabel540);
                    ruleDefault44=ruleDefault();

                    state._fsp--;

                    stream_ruleDefault.add(ruleDefault44.getTree());


                    // AST REWRITE
                    // elements: ruleDefault
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 82:18: -> ^( ATOM ruleDefault )
                    {
                        // Label.g:82:21: ^( ATOM ruleDefault )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ATOM, "ATOM"), root_1);

                        adaptor.addChild(root_1, stream_ruleDefault.nextTree());

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
    // $ANTLR end ruleLabel

    public static class graphDefault_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start graphDefault
    // Label.g:85:1: graphDefault : (~ ( ':' ) )+ ;
    public final LabelParser.graphDefault_return graphDefault() throws RecognitionException {
        LabelParser.graphDefault_return retval = new LabelParser.graphDefault_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set45=null;

        Object set45_tree=null;

        try {
            // Label.g:86:4: ( (~ ( ':' ) )+ )
            // Label.g:86:6: (~ ( ':' ) )+
            {
            root_0 = (Object)adaptor.nil();

            // Label.g:86:6: (~ ( ':' ) )+
            int cnt14=0;
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( ((LA14_0>=NEW && LA14_0<=EQUALS)||(LA14_0>=DOLLAR && LA14_0<=DIGIT)) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // Label.g:86:7: ~ ( ':' )
            	    {
            	    set45=(Token)input.LT(1);
            	    if ( (input.LA(1)>=NEW && input.LA(1)<=EQUALS)||(input.LA(1)>=DOLLAR && input.LA(1)<=DIGIT) ) {
            	        input.consume();
            	        adaptor.addChild(root_0, (Object)adaptor.create(set45));
            	        state.errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt14 >= 1 ) break loop14;
                        EarlyExitException eee =
                            new EarlyExitException(14, input);
                        throw eee;
                }
                cnt14++;
            } while (true);


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
    // $ANTLR end graphDefault

    public static class ruleDefault_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start ruleDefault
    // Label.g:89:1: ruleDefault : ~ ( EQUALS | QUERY | PLING | SQUOTE | LBRACE | RBRACE | BSLASH | COLON ) (~ ( SQUOTE | LBRACE | RBRACE | BSLASH | COLON ) )* ;
    public final LabelParser.ruleDefault_return ruleDefault() throws RecognitionException {
        LabelParser.ruleDefault_return retval = new LabelParser.ruleDefault_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set46=null;
        Token set47=null;

        Object set46_tree=null;
        Object set47_tree=null;

        try {
            // Label.g:90:4: (~ ( EQUALS | QUERY | PLING | SQUOTE | LBRACE | RBRACE | BSLASH | COLON ) (~ ( SQUOTE | LBRACE | RBRACE | BSLASH | COLON ) )* )
            // Label.g:90:6: ~ ( EQUALS | QUERY | PLING | SQUOTE | LBRACE | RBRACE | BSLASH | COLON ) (~ ( SQUOTE | LBRACE | RBRACE | BSLASH | COLON ) )*
            {
            root_0 = (Object)adaptor.nil();

            set46=(Token)input.LT(1);
            if ( (input.LA(1)>=NEW && input.LA(1)<=FALSE)||input.LA(1)==DOLLAR||(input.LA(1)>=DQTEXT && input.LA(1)<=DIGIT) ) {
                input.consume();
                adaptor.addChild(root_0, (Object)adaptor.create(set46));
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            // Label.g:90:76: (~ ( SQUOTE | LBRACE | RBRACE | BSLASH | COLON ) )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( ((LA15_0>=NEW && LA15_0<=EQUALS)||(LA15_0>=DOLLAR && LA15_0<=PLING)||LA15_0==QUERY||(LA15_0>=DQTEXT && LA15_0<=DIGIT)) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // Label.g:90:77: ~ ( SQUOTE | LBRACE | RBRACE | BSLASH | COLON )
            	    {
            	    set47=(Token)input.LT(1);
            	    if ( (input.LA(1)>=NEW && input.LA(1)<=EQUALS)||(input.LA(1)>=DOLLAR && input.LA(1)<=PLING)||input.LA(1)==QUERY||(input.LA(1)>=DQTEXT && input.LA(1)<=DIGIT) ) {
            	        input.consume();
            	        adaptor.addChild(root_0, (Object)adaptor.create(set47));
            	        state.errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);


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
    // $ANTLR end ruleDefault

    public static class nodeLabel_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start nodeLabel
    // Label.g:93:1: nodeLabel : ( TYPE COLON ident | FLAG COLON ident );
    public final LabelParser.nodeLabel_return nodeLabel() throws RecognitionException {
        LabelParser.nodeLabel_return retval = new LabelParser.nodeLabel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TYPE48=null;
        Token COLON49=null;
        Token FLAG51=null;
        Token COLON52=null;
        LabelParser.ident_return ident50 = null;

        LabelParser.ident_return ident53 = null;


        Object TYPE48_tree=null;
        Object COLON49_tree=null;
        Object FLAG51_tree=null;
        Object COLON52_tree=null;

        try {
            // Label.g:94:4: ( TYPE COLON ident | FLAG COLON ident )
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==TYPE) ) {
                alt16=1;
            }
            else if ( (LA16_0==FLAG) ) {
                alt16=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;
            }
            switch (alt16) {
                case 1 :
                    // Label.g:94:6: TYPE COLON ident
                    {
                    root_0 = (Object)adaptor.nil();

                    TYPE48=(Token)match(input,TYPE,FOLLOW_TYPE_in_nodeLabel654); 
                    TYPE48_tree = (Object)adaptor.create(TYPE48);
                    root_0 = (Object)adaptor.becomeRoot(TYPE48_tree, root_0);

                    COLON49=(Token)match(input,COLON,FOLLOW_COLON_in_nodeLabel657); 
                    pushFollow(FOLLOW_ident_in_nodeLabel660);
                    ident50=ident();

                    state._fsp--;

                    adaptor.addChild(root_0, ident50.getTree());

                    }
                    break;
                case 2 :
                    // Label.g:95:6: FLAG COLON ident
                    {
                    root_0 = (Object)adaptor.nil();

                    FLAG51=(Token)match(input,FLAG,FOLLOW_FLAG_in_nodeLabel667); 
                    FLAG51_tree = (Object)adaptor.create(FLAG51);
                    root_0 = (Object)adaptor.becomeRoot(FLAG51_tree, root_0);

                    COLON52=(Token)match(input,COLON,FOLLOW_COLON_in_nodeLabel670); 
                    pushFollow(FOLLOW_ident_in_nodeLabel673);
                    ident53=ident();

                    state._fsp--;

                    adaptor.addChild(root_0, ident53.getTree());

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

    public static class attrLabel_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start attrLabel
    // Label.g:97:1: attrLabel : ( INT COLON ( number | ident )? | REAL COLON ( rnumber | ident )? | STRING COLON ( DQTEXT | ident )? | BOOL COLON ( TRUE | FALSE | ident )? | ATTR COLON | PROD COLON | ARG COLON number );
    public final LabelParser.attrLabel_return attrLabel() throws RecognitionException {
        LabelParser.attrLabel_return retval = new LabelParser.attrLabel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token INT54=null;
        Token COLON55=null;
        Token REAL58=null;
        Token COLON59=null;
        Token STRING62=null;
        Token COLON63=null;
        Token DQTEXT64=null;
        Token BOOL66=null;
        Token COLON67=null;
        Token TRUE68=null;
        Token FALSE69=null;
        Token ATTR71=null;
        Token COLON72=null;
        Token PROD73=null;
        Token COLON74=null;
        Token ARG75=null;
        Token COLON76=null;
        LabelParser.number_return number56 = null;

        LabelParser.ident_return ident57 = null;

        LabelParser.rnumber_return rnumber60 = null;

        LabelParser.ident_return ident61 = null;

        LabelParser.ident_return ident65 = null;

        LabelParser.ident_return ident70 = null;

        LabelParser.number_return number77 = null;


        Object INT54_tree=null;
        Object COLON55_tree=null;
        Object REAL58_tree=null;
        Object COLON59_tree=null;
        Object STRING62_tree=null;
        Object COLON63_tree=null;
        Object DQTEXT64_tree=null;
        Object BOOL66_tree=null;
        Object COLON67_tree=null;
        Object TRUE68_tree=null;
        Object FALSE69_tree=null;
        Object ATTR71_tree=null;
        Object COLON72_tree=null;
        Object PROD73_tree=null;
        Object COLON74_tree=null;
        Object ARG75_tree=null;
        Object COLON76_tree=null;

        try {
            // Label.g:98:4: ( INT COLON ( number | ident )? | REAL COLON ( rnumber | ident )? | STRING COLON ( DQTEXT | ident )? | BOOL COLON ( TRUE | FALSE | ident )? | ATTR COLON | PROD COLON | ARG COLON number )
            int alt21=7;
            switch ( input.LA(1) ) {
            case INT:
                {
                alt21=1;
                }
                break;
            case REAL:
                {
                alt21=2;
                }
                break;
            case STRING:
                {
                alt21=3;
                }
                break;
            case BOOL:
                {
                alt21=4;
                }
                break;
            case ATTR:
                {
                alt21=5;
                }
                break;
            case PROD:
                {
                alt21=6;
                }
                break;
            case ARG:
                {
                alt21=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;
            }

            switch (alt21) {
                case 1 :
                    // Label.g:98:6: INT COLON ( number | ident )?
                    {
                    root_0 = (Object)adaptor.nil();

                    INT54=(Token)match(input,INT,FOLLOW_INT_in_attrLabel687); 
                    INT54_tree = (Object)adaptor.create(INT54);
                    root_0 = (Object)adaptor.becomeRoot(INT54_tree, root_0);

                    COLON55=(Token)match(input,COLON,FOLLOW_COLON_in_attrLabel690); 
                    // Label.g:98:18: ( number | ident )?
                    int alt17=3;
                    int LA17_0 = input.LA(1);

                    if ( (LA17_0==DIGIT) ) {
                        alt17=1;
                    }
                    else if ( (LA17_0==LETTER) ) {
                        alt17=2;
                    }
                    switch (alt17) {
                        case 1 :
                            // Label.g:98:19: number
                            {
                            pushFollow(FOLLOW_number_in_attrLabel694);
                            number56=number();

                            state._fsp--;

                            adaptor.addChild(root_0, number56.getTree());

                            }
                            break;
                        case 2 :
                            // Label.g:98:28: ident
                            {
                            pushFollow(FOLLOW_ident_in_attrLabel698);
                            ident57=ident();

                            state._fsp--;

                            adaptor.addChild(root_0, ident57.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // Label.g:99:6: REAL COLON ( rnumber | ident )?
                    {
                    root_0 = (Object)adaptor.nil();

                    REAL58=(Token)match(input,REAL,FOLLOW_REAL_in_attrLabel707); 
                    REAL58_tree = (Object)adaptor.create(REAL58);
                    root_0 = (Object)adaptor.becomeRoot(REAL58_tree, root_0);

                    COLON59=(Token)match(input,COLON,FOLLOW_COLON_in_attrLabel710); 
                    // Label.g:99:19: ( rnumber | ident )?
                    int alt18=3;
                    int LA18_0 = input.LA(1);

                    if ( (LA18_0==DOT||LA18_0==DIGIT) ) {
                        alt18=1;
                    }
                    else if ( (LA18_0==LETTER) ) {
                        alt18=2;
                    }
                    switch (alt18) {
                        case 1 :
                            // Label.g:99:20: rnumber
                            {
                            pushFollow(FOLLOW_rnumber_in_attrLabel714);
                            rnumber60=rnumber();

                            state._fsp--;

                            adaptor.addChild(root_0, rnumber60.getTree());

                            }
                            break;
                        case 2 :
                            // Label.g:99:30: ident
                            {
                            pushFollow(FOLLOW_ident_in_attrLabel718);
                            ident61=ident();

                            state._fsp--;

                            adaptor.addChild(root_0, ident61.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // Label.g:100:6: STRING COLON ( DQTEXT | ident )?
                    {
                    root_0 = (Object)adaptor.nil();

                    STRING62=(Token)match(input,STRING,FOLLOW_STRING_in_attrLabel727); 
                    STRING62_tree = (Object)adaptor.create(STRING62);
                    root_0 = (Object)adaptor.becomeRoot(STRING62_tree, root_0);

                    COLON63=(Token)match(input,COLON,FOLLOW_COLON_in_attrLabel730); 
                    // Label.g:100:21: ( DQTEXT | ident )?
                    int alt19=3;
                    int LA19_0 = input.LA(1);

                    if ( (LA19_0==DQTEXT) ) {
                        alt19=1;
                    }
                    else if ( (LA19_0==LETTER) ) {
                        alt19=2;
                    }
                    switch (alt19) {
                        case 1 :
                            // Label.g:100:22: DQTEXT
                            {
                            DQTEXT64=(Token)match(input,DQTEXT,FOLLOW_DQTEXT_in_attrLabel734); 
                            DQTEXT64_tree = (Object)adaptor.create(DQTEXT64);
                            adaptor.addChild(root_0, DQTEXT64_tree);


                            }
                            break;
                        case 2 :
                            // Label.g:100:31: ident
                            {
                            pushFollow(FOLLOW_ident_in_attrLabel738);
                            ident65=ident();

                            state._fsp--;

                            adaptor.addChild(root_0, ident65.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // Label.g:101:6: BOOL COLON ( TRUE | FALSE | ident )?
                    {
                    root_0 = (Object)adaptor.nil();

                    BOOL66=(Token)match(input,BOOL,FOLLOW_BOOL_in_attrLabel747); 
                    BOOL66_tree = (Object)adaptor.create(BOOL66);
                    root_0 = (Object)adaptor.becomeRoot(BOOL66_tree, root_0);

                    COLON67=(Token)match(input,COLON,FOLLOW_COLON_in_attrLabel750); 
                    // Label.g:101:19: ( TRUE | FALSE | ident )?
                    int alt20=4;
                    switch ( input.LA(1) ) {
                        case TRUE:
                            {
                            alt20=1;
                            }
                            break;
                        case FALSE:
                            {
                            alt20=2;
                            }
                            break;
                        case LETTER:
                            {
                            alt20=3;
                            }
                            break;
                    }

                    switch (alt20) {
                        case 1 :
                            // Label.g:101:20: TRUE
                            {
                            TRUE68=(Token)match(input,TRUE,FOLLOW_TRUE_in_attrLabel754); 
                            TRUE68_tree = (Object)adaptor.create(TRUE68);
                            adaptor.addChild(root_0, TRUE68_tree);


                            }
                            break;
                        case 2 :
                            // Label.g:101:27: FALSE
                            {
                            FALSE69=(Token)match(input,FALSE,FOLLOW_FALSE_in_attrLabel758); 
                            FALSE69_tree = (Object)adaptor.create(FALSE69);
                            adaptor.addChild(root_0, FALSE69_tree);


                            }
                            break;
                        case 3 :
                            // Label.g:101:35: ident
                            {
                            pushFollow(FOLLOW_ident_in_attrLabel762);
                            ident70=ident();

                            state._fsp--;

                            adaptor.addChild(root_0, ident70.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 5 :
                    // Label.g:102:6: ATTR COLON
                    {
                    root_0 = (Object)adaptor.nil();

                    ATTR71=(Token)match(input,ATTR,FOLLOW_ATTR_in_attrLabel771); 
                    ATTR71_tree = (Object)adaptor.create(ATTR71);
                    root_0 = (Object)adaptor.becomeRoot(ATTR71_tree, root_0);

                    COLON72=(Token)match(input,COLON,FOLLOW_COLON_in_attrLabel774); 

                    }
                    break;
                case 6 :
                    // Label.g:103:6: PROD COLON
                    {
                    root_0 = (Object)adaptor.nil();

                    PROD73=(Token)match(input,PROD,FOLLOW_PROD_in_attrLabel782); 
                    PROD73_tree = (Object)adaptor.create(PROD73);
                    root_0 = (Object)adaptor.becomeRoot(PROD73_tree, root_0);

                    COLON74=(Token)match(input,COLON,FOLLOW_COLON_in_attrLabel785); 

                    }
                    break;
                case 7 :
                    // Label.g:104:6: ARG COLON number
                    {
                    root_0 = (Object)adaptor.nil();

                    ARG75=(Token)match(input,ARG,FOLLOW_ARG_in_attrLabel793); 
                    ARG75_tree = (Object)adaptor.create(ARG75);
                    root_0 = (Object)adaptor.becomeRoot(ARG75_tree, root_0);

                    COLON76=(Token)match(input,COLON,FOLLOW_COLON_in_attrLabel796); 
                    pushFollow(FOLLOW_number_in_attrLabel799);
                    number77=number();

                    state._fsp--;

                    adaptor.addChild(root_0, number77.getTree());

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

    public static class regExpr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start regExpr
    // Label.g:107:1: regExpr : choice ;
    public final LabelParser.regExpr_return regExpr() throws RecognitionException {
        LabelParser.regExpr_return retval = new LabelParser.regExpr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        LabelParser.choice_return choice78 = null;



        try {
            // Label.g:108:4: ( choice )
            // Label.g:108:6: choice
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_choice_in_regExpr814);
            choice78=choice();

            state._fsp--;

            adaptor.addChild(root_0, choice78.getTree());

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
    // Label.g:110:1: choice : sequence ( BAR choice )? ;
    public final LabelParser.choice_return choice() throws RecognitionException {
        LabelParser.choice_return retval = new LabelParser.choice_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BAR80=null;
        LabelParser.sequence_return sequence79 = null;

        LabelParser.choice_return choice81 = null;


        Object BAR80_tree=null;

        try {
            // Label.g:111:4: ( sequence ( BAR choice )? )
            // Label.g:111:6: sequence ( BAR choice )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_sequence_in_choice826);
            sequence79=sequence();

            state._fsp--;

            adaptor.addChild(root_0, sequence79.getTree());
            // Label.g:111:15: ( BAR choice )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==BAR) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // Label.g:111:16: BAR choice
                    {
                    BAR80=(Token)match(input,BAR,FOLLOW_BAR_in_choice829); 
                    BAR80_tree = (Object)adaptor.create(BAR80);
                    root_0 = (Object)adaptor.becomeRoot(BAR80_tree, root_0);

                    pushFollow(FOLLOW_choice_in_choice832);
                    choice81=choice();

                    state._fsp--;

                    adaptor.addChild(root_0, choice81.getTree());

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
    // Label.g:113:1: sequence : unary ( DOT sequence )? ;
    public final LabelParser.sequence_return sequence() throws RecognitionException {
        LabelParser.sequence_return retval = new LabelParser.sequence_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOT83=null;
        LabelParser.unary_return unary82 = null;

        LabelParser.sequence_return sequence84 = null;


        Object DOT83_tree=null;

        try {
            // Label.g:114:4: ( unary ( DOT sequence )? )
            // Label.g:114:6: unary ( DOT sequence )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_unary_in_sequence846);
            unary82=unary();

            state._fsp--;

            adaptor.addChild(root_0, unary82.getTree());
            // Label.g:114:12: ( DOT sequence )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==DOT) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // Label.g:114:13: DOT sequence
                    {
                    DOT83=(Token)match(input,DOT,FOLLOW_DOT_in_sequence849); 
                    DOT83_tree = (Object)adaptor.create(DOT83);
                    root_0 = (Object)adaptor.becomeRoot(DOT83_tree, root_0);

                    pushFollow(FOLLOW_sequence_in_sequence852);
                    sequence84=sequence();

                    state._fsp--;

                    adaptor.addChild(root_0, sequence84.getTree());

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
    // Label.g:116:1: unary : ( MINUS unary | atom ( STAR | PLUS )? );
    public final LabelParser.unary_return unary() throws RecognitionException {
        LabelParser.unary_return retval = new LabelParser.unary_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token MINUS85=null;
        Token STAR88=null;
        Token PLUS89=null;
        LabelParser.unary_return unary86 = null;

        LabelParser.atom_return atom87 = null;


        Object MINUS85_tree=null;
        Object STAR88_tree=null;
        Object PLUS89_tree=null;

        try {
            // Label.g:117:4: ( MINUS unary | atom ( STAR | PLUS )? )
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==MINUS) ) {
                alt25=1;
            }
            else if ( (LA25_0==EQUALS||(LA25_0>=QUERY && LA25_0<=SQUOTE)||LA25_0==LPAR||LA25_0==IDENTCHAR) ) {
                alt25=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 25, 0, input);

                throw nvae;
            }
            switch (alt25) {
                case 1 :
                    // Label.g:117:6: MINUS unary
                    {
                    root_0 = (Object)adaptor.nil();

                    MINUS85=(Token)match(input,MINUS,FOLLOW_MINUS_in_unary866); 
                    MINUS85_tree = (Object)adaptor.create(MINUS85);
                    adaptor.addChild(root_0, MINUS85_tree);

                    pushFollow(FOLLOW_unary_in_unary868);
                    unary86=unary();

                    state._fsp--;

                    adaptor.addChild(root_0, unary86.getTree());

                    }
                    break;
                case 2 :
                    // Label.g:118:6: atom ( STAR | PLUS )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_atom_in_unary875);
                    atom87=atom();

                    state._fsp--;

                    adaptor.addChild(root_0, atom87.getTree());
                    // Label.g:118:11: ( STAR | PLUS )?
                    int alt24=3;
                    int LA24_0 = input.LA(1);

                    if ( (LA24_0==STAR) ) {
                        alt24=1;
                    }
                    else if ( (LA24_0==PLUS) ) {
                        alt24=2;
                    }
                    switch (alt24) {
                        case 1 :
                            // Label.g:118:12: STAR
                            {
                            STAR88=(Token)match(input,STAR,FOLLOW_STAR_in_unary878); 
                            STAR88_tree = (Object)adaptor.create(STAR88);
                            root_0 = (Object)adaptor.becomeRoot(STAR88_tree, root_0);


                            }
                            break;
                        case 2 :
                            // Label.g:118:20: PLUS
                            {
                            PLUS89=(Token)match(input,PLUS,FOLLOW_PLUS_in_unary883); 
                            PLUS89_tree = (Object)adaptor.create(PLUS89);
                            root_0 = (Object)adaptor.becomeRoot(PLUS89_tree, root_0);


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
    // Label.g:120:1: atom : ( sqText -> ^( ATOM sqText ) | atomLabel -> ^( ATOM atomLabel ) | EQUALS | LPAR regExpr RPAR | wildcard );
    public final LabelParser.atom_return atom() throws RecognitionException {
        LabelParser.atom_return retval = new LabelParser.atom_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQUALS92=null;
        Token LPAR93=null;
        Token RPAR95=null;
        LabelParser.sqText_return sqText90 = null;

        LabelParser.atomLabel_return atomLabel91 = null;

        LabelParser.regExpr_return regExpr94 = null;

        LabelParser.wildcard_return wildcard96 = null;


        Object EQUALS92_tree=null;
        Object LPAR93_tree=null;
        Object RPAR95_tree=null;
        RewriteRuleSubtreeStream stream_sqText=new RewriteRuleSubtreeStream(adaptor,"rule sqText");
        RewriteRuleSubtreeStream stream_atomLabel=new RewriteRuleSubtreeStream(adaptor,"rule atomLabel");
        try {
            // Label.g:121:4: ( sqText -> ^( ATOM sqText ) | atomLabel -> ^( ATOM atomLabel ) | EQUALS | LPAR regExpr RPAR | wildcard )
            int alt26=5;
            switch ( input.LA(1) ) {
            case SQUOTE:
                {
                alt26=1;
                }
                break;
            case IDENTCHAR:
                {
                alt26=2;
                }
                break;
            case EQUALS:
                {
                alt26=3;
                }
                break;
            case LPAR:
                {
                alt26=4;
                }
                break;
            case QUERY:
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
                    // Label.g:121:6: sqText
                    {
                    pushFollow(FOLLOW_sqText_in_atom898);
                    sqText90=sqText();

                    state._fsp--;

                    stream_sqText.add(sqText90.getTree());


                    // AST REWRITE
                    // elements: sqText
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 121:13: -> ^( ATOM sqText )
                    {
                        // Label.g:121:16: ^( ATOM sqText )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ATOM, "ATOM"), root_1);

                        adaptor.addChild(root_1, stream_sqText.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 2 :
                    // Label.g:122:6: atomLabel
                    {
                    pushFollow(FOLLOW_atomLabel_in_atom913);
                    atomLabel91=atomLabel();

                    state._fsp--;

                    stream_atomLabel.add(atomLabel91.getTree());


                    // AST REWRITE
                    // elements: atomLabel
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 122:16: -> ^( ATOM atomLabel )
                    {
                        // Label.g:122:19: ^( ATOM atomLabel )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ATOM, "ATOM"), root_1);

                        adaptor.addChild(root_1, stream_atomLabel.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;retval.tree = root_0;
                    }
                    break;
                case 3 :
                    // Label.g:123:6: EQUALS
                    {
                    root_0 = (Object)adaptor.nil();

                    EQUALS92=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_atom928); 
                    EQUALS92_tree = (Object)adaptor.create(EQUALS92);
                    adaptor.addChild(root_0, EQUALS92_tree);


                    }
                    break;
                case 4 :
                    // Label.g:124:6: LPAR regExpr RPAR
                    {
                    root_0 = (Object)adaptor.nil();

                    LPAR93=(Token)match(input,LPAR,FOLLOW_LPAR_in_atom935); 
                    LPAR93_tree = (Object)adaptor.create(LPAR93);
                    adaptor.addChild(root_0, LPAR93_tree);

                    pushFollow(FOLLOW_regExpr_in_atom937);
                    regExpr94=regExpr();

                    state._fsp--;

                    adaptor.addChild(root_0, regExpr94.getTree());
                    RPAR95=(Token)match(input,RPAR,FOLLOW_RPAR_in_atom939); 
                    RPAR95_tree = (Object)adaptor.create(RPAR95);
                    adaptor.addChild(root_0, RPAR95_tree);


                    }
                    break;
                case 5 :
                    // Label.g:125:6: wildcard
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_wildcard_in_atom946);
                    wildcard96=wildcard();

                    state._fsp--;

                    adaptor.addChild(root_0, wildcard96.getTree());

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

    public static class atomLabel_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start atomLabel
    // Label.g:128:1: atomLabel : ( IDENTCHAR )+ ;
    public final LabelParser.atomLabel_return atomLabel() throws RecognitionException {
        LabelParser.atomLabel_return retval = new LabelParser.atomLabel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token IDENTCHAR97=null;

        Object IDENTCHAR97_tree=null;

        try {
            // Label.g:129:4: ( ( IDENTCHAR )+ )
            // Label.g:129:6: ( IDENTCHAR )+
            {
            root_0 = (Object)adaptor.nil();

            // Label.g:129:6: ( IDENTCHAR )+
            int cnt27=0;
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0==IDENTCHAR) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // Label.g:129:6: IDENTCHAR
            	    {
            	    IDENTCHAR97=(Token)match(input,IDENTCHAR,FOLLOW_IDENTCHAR_in_atomLabel961); 
            	    IDENTCHAR97_tree = (Object)adaptor.create(IDENTCHAR97);
            	    adaptor.addChild(root_0, IDENTCHAR97_tree);


            	    }
            	    break;

            	default :
            	    if ( cnt27 >= 1 ) break loop27;
                        EarlyExitException eee =
                            new EarlyExitException(27, input);
                        throw eee;
                }
                cnt27++;
            } while (true);


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
    // $ANTLR end atomLabel

    public static class wildcard_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start wildcard
    // Label.g:132:1: wildcard : QUERY ( ident )? LSQUARE ( HAT )? atom ( COMMA atom )* RSQUARE ;
    public final LabelParser.wildcard_return wildcard() throws RecognitionException {
        LabelParser.wildcard_return retval = new LabelParser.wildcard_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token QUERY98=null;
        Token LSQUARE100=null;
        Token HAT101=null;
        Token COMMA103=null;
        Token RSQUARE105=null;
        LabelParser.ident_return ident99 = null;

        LabelParser.atom_return atom102 = null;

        LabelParser.atom_return atom104 = null;


        Object QUERY98_tree=null;
        Object LSQUARE100_tree=null;
        Object HAT101_tree=null;
        Object COMMA103_tree=null;
        Object RSQUARE105_tree=null;

        try {
            // Label.g:133:4: ( QUERY ( ident )? LSQUARE ( HAT )? atom ( COMMA atom )* RSQUARE )
            // Label.g:133:6: QUERY ( ident )? LSQUARE ( HAT )? atom ( COMMA atom )* RSQUARE
            {
            root_0 = (Object)adaptor.nil();

            QUERY98=(Token)match(input,QUERY,FOLLOW_QUERY_in_wildcard980); 
            QUERY98_tree = (Object)adaptor.create(QUERY98);
            root_0 = (Object)adaptor.becomeRoot(QUERY98_tree, root_0);

            // Label.g:133:13: ( ident )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==LETTER) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // Label.g:133:13: ident
                    {
                    pushFollow(FOLLOW_ident_in_wildcard983);
                    ident99=ident();

                    state._fsp--;

                    adaptor.addChild(root_0, ident99.getTree());

                    }
                    break;

            }

            LSQUARE100=(Token)match(input,LSQUARE,FOLLOW_LSQUARE_in_wildcard986); 
            // Label.g:133:29: ( HAT )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==HAT) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // Label.g:133:29: HAT
                    {
                    HAT101=(Token)match(input,HAT,FOLLOW_HAT_in_wildcard989); 
                    HAT101_tree = (Object)adaptor.create(HAT101);
                    adaptor.addChild(root_0, HAT101_tree);


                    }
                    break;

            }

            pushFollow(FOLLOW_atom_in_wildcard992);
            atom102=atom();

            state._fsp--;

            adaptor.addChild(root_0, atom102.getTree());
            // Label.g:133:39: ( COMMA atom )*
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( (LA30_0==COMMA) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // Label.g:133:40: COMMA atom
            	    {
            	    COMMA103=(Token)match(input,COMMA,FOLLOW_COMMA_in_wildcard995); 
            	    pushFollow(FOLLOW_atom_in_wildcard998);
            	    atom104=atom();

            	    state._fsp--;

            	    adaptor.addChild(root_0, atom104.getTree());

            	    }
            	    break;

            	default :
            	    break loop30;
                }
            } while (true);

            RSQUARE105=(Token)match(input,RSQUARE,FOLLOW_RSQUARE_in_wildcard1002); 

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

    public static class sqText_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start sqText
    // Label.g:136:1: sqText : SQUOTE (~ ( SQUOTE | BSLASH ) | sqTextSpecial )* SQUOTE ;
    public final LabelParser.sqText_return sqText() throws RecognitionException {
        LabelParser.sqText_return retval = new LabelParser.sqText_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SQUOTE106=null;
        Token set107=null;
        Token SQUOTE109=null;
        LabelParser.sqTextSpecial_return sqTextSpecial108 = null;


        Object SQUOTE106_tree=null;
        Object set107_tree=null;
        Object SQUOTE109_tree=null;

        try {
            // Label.g:137:4: ( SQUOTE (~ ( SQUOTE | BSLASH ) | sqTextSpecial )* SQUOTE )
            // Label.g:137:6: SQUOTE (~ ( SQUOTE | BSLASH ) | sqTextSpecial )* SQUOTE
            {
            root_0 = (Object)adaptor.nil();

            SQUOTE106=(Token)match(input,SQUOTE,FOLLOW_SQUOTE_in_sqText1018); 
            // Label.g:137:14: (~ ( SQUOTE | BSLASH ) | sqTextSpecial )*
            loop31:
            do {
                int alt31=3;
                int LA31_0 = input.LA(1);

                if ( ((LA31_0>=NEW && LA31_0<=QUERY)||(LA31_0>=DQTEXT && LA31_0<=DIGIT)) ) {
                    alt31=1;
                }
                else if ( (LA31_0==BSLASH) ) {
                    alt31=2;
                }


                switch (alt31) {
            	case 1 :
            	    // Label.g:137:15: ~ ( SQUOTE | BSLASH )
            	    {
            	    set107=(Token)input.LT(1);
            	    if ( (input.LA(1)>=NEW && input.LA(1)<=QUERY)||(input.LA(1)>=DQTEXT && input.LA(1)<=DIGIT) ) {
            	        input.consume();
            	        adaptor.addChild(root_0, (Object)adaptor.create(set107));
            	        state.errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    }
            	    break;
            	case 2 :
            	    // Label.g:137:34: sqTextSpecial
            	    {
            	    pushFollow(FOLLOW_sqTextSpecial_in_sqText1031);
            	    sqTextSpecial108=sqTextSpecial();

            	    state._fsp--;

            	    adaptor.addChild(root_0, sqTextSpecial108.getTree());

            	    }
            	    break;

            	default :
            	    break loop31;
                }
            } while (true);

            SQUOTE109=(Token)match(input,SQUOTE,FOLLOW_SQUOTE_in_sqText1035); 

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
    // $ANTLR end sqText

    public static class sqTextSpecial_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start sqTextSpecial
    // Label.g:139:1: sqTextSpecial : BSLASH c= ( BSLASH | SQUOTE ) -> $c;
    public final LabelParser.sqTextSpecial_return sqTextSpecial() throws RecognitionException {
        LabelParser.sqTextSpecial_return retval = new LabelParser.sqTextSpecial_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token c=null;
        Token BSLASH110=null;
        Token BSLASH111=null;
        Token SQUOTE112=null;

        Object c_tree=null;
        Object BSLASH110_tree=null;
        Object BSLASH111_tree=null;
        Object SQUOTE112_tree=null;
        RewriteRuleTokenStream stream_SQUOTE=new RewriteRuleTokenStream(adaptor,"token SQUOTE");
        RewriteRuleTokenStream stream_BSLASH=new RewriteRuleTokenStream(adaptor,"token BSLASH");

        try {
            // Label.g:140:4: ( BSLASH c= ( BSLASH | SQUOTE ) -> $c)
            // Label.g:140:6: BSLASH c= ( BSLASH | SQUOTE )
            {
            BSLASH110=(Token)match(input,BSLASH,FOLLOW_BSLASH_in_sqTextSpecial1047);  
            stream_BSLASH.add(BSLASH110);

            // Label.g:140:15: ( BSLASH | SQUOTE )
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==BSLASH) ) {
                alt32=1;
            }
            else if ( (LA32_0==SQUOTE) ) {
                alt32=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                throw nvae;
            }
            switch (alt32) {
                case 1 :
                    // Label.g:140:16: BSLASH
                    {
                    BSLASH111=(Token)match(input,BSLASH,FOLLOW_BSLASH_in_sqTextSpecial1052);  
                    stream_BSLASH.add(BSLASH111);


                    }
                    break;
                case 2 :
                    // Label.g:140:23: SQUOTE
                    {
                    SQUOTE112=(Token)match(input,SQUOTE,FOLLOW_SQUOTE_in_sqTextSpecial1054);  
                    stream_SQUOTE.add(SQUOTE112);


                    }
                    break;

            }



            // AST REWRITE
            // elements: c
            // token labels: c
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleTokenStream stream_c=new RewriteRuleTokenStream(adaptor,"token c",c);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 140:31: -> $c
            {
                adaptor.addChild(root_0, stream_c.nextNode());

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
    // $ANTLR end sqTextSpecial

    public static class ident_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start ident
    // Label.g:169:1: ident : LETTER ( IDENTCHAR )* ;
    public final LabelParser.ident_return ident() throws RecognitionException {
        LabelParser.ident_return retval = new LabelParser.ident_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token LETTER113=null;
        Token IDENTCHAR114=null;

        Object LETTER113_tree=null;
        Object IDENTCHAR114_tree=null;

        try {
            // Label.g:170:4: ( LETTER ( IDENTCHAR )* )
            // Label.g:170:6: LETTER ( IDENTCHAR )*
            {
            root_0 = (Object)adaptor.nil();

            LETTER113=(Token)match(input,LETTER,FOLLOW_LETTER_in_ident1294); 
            LETTER113_tree = (Object)adaptor.create(LETTER113);
            adaptor.addChild(root_0, LETTER113_tree);

            // Label.g:170:13: ( IDENTCHAR )*
            loop33:
            do {
                int alt33=2;
                int LA33_0 = input.LA(1);

                if ( (LA33_0==IDENTCHAR) ) {
                    alt33=1;
                }


                switch (alt33) {
            	case 1 :
            	    // Label.g:170:13: IDENTCHAR
            	    {
            	    IDENTCHAR114=(Token)match(input,IDENTCHAR,FOLLOW_IDENTCHAR_in_ident1296); 
            	    IDENTCHAR114_tree = (Object)adaptor.create(IDENTCHAR114);
            	    adaptor.addChild(root_0, IDENTCHAR114_tree);


            	    }
            	    break;

            	default :
            	    break loop33;
                }
            } while (true);


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
    // $ANTLR end ident

    public static class number_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start number
    // Label.g:173:1: number : ( DIGIT )+ ;
    public final LabelParser.number_return number() throws RecognitionException {
        LabelParser.number_return retval = new LabelParser.number_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DIGIT115=null;

        Object DIGIT115_tree=null;

        try {
            // Label.g:174:4: ( ( DIGIT )+ )
            // Label.g:174:6: ( DIGIT )+
            {
            root_0 = (Object)adaptor.nil();

            // Label.g:174:6: ( DIGIT )+
            int cnt34=0;
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);

                if ( (LA34_0==DIGIT) ) {
                    alt34=1;
                }


                switch (alt34) {
            	case 1 :
            	    // Label.g:174:6: DIGIT
            	    {
            	    DIGIT115=(Token)match(input,DIGIT,FOLLOW_DIGIT_in_number1316); 
            	    DIGIT115_tree = (Object)adaptor.create(DIGIT115);
            	    adaptor.addChild(root_0, DIGIT115_tree);


            	    }
            	    break;

            	default :
            	    if ( cnt34 >= 1 ) break loop34;
                        EarlyExitException eee =
                            new EarlyExitException(34, input);
                        throw eee;
                }
                cnt34++;
            } while (true);


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
    // $ANTLR end number

    public static class rnumber_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start rnumber
    // Label.g:177:1: rnumber : ( ( DIGIT )+ ( DOT ( DIGIT )* )? | DOT ( DIGIT )+ ) ;
    public final LabelParser.rnumber_return rnumber() throws RecognitionException {
        LabelParser.rnumber_return retval = new LabelParser.rnumber_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DIGIT116=null;
        Token DOT117=null;
        Token DIGIT118=null;
        Token DOT119=null;
        Token DIGIT120=null;

        Object DIGIT116_tree=null;
        Object DOT117_tree=null;
        Object DIGIT118_tree=null;
        Object DOT119_tree=null;
        Object DIGIT120_tree=null;

        try {
            // Label.g:178:4: ( ( ( DIGIT )+ ( DOT ( DIGIT )* )? | DOT ( DIGIT )+ ) )
            // Label.g:178:6: ( ( DIGIT )+ ( DOT ( DIGIT )* )? | DOT ( DIGIT )+ )
            {
            root_0 = (Object)adaptor.nil();

            // Label.g:178:6: ( ( DIGIT )+ ( DOT ( DIGIT )* )? | DOT ( DIGIT )+ )
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( (LA39_0==DIGIT) ) {
                alt39=1;
            }
            else if ( (LA39_0==DOT) ) {
                alt39=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 39, 0, input);

                throw nvae;
            }
            switch (alt39) {
                case 1 :
                    // Label.g:178:7: ( DIGIT )+ ( DOT ( DIGIT )* )?
                    {
                    // Label.g:178:7: ( DIGIT )+
                    int cnt35=0;
                    loop35:
                    do {
                        int alt35=2;
                        int LA35_0 = input.LA(1);

                        if ( (LA35_0==DIGIT) ) {
                            alt35=1;
                        }


                        switch (alt35) {
                    	case 1 :
                    	    // Label.g:178:7: DIGIT
                    	    {
                    	    DIGIT116=(Token)match(input,DIGIT,FOLLOW_DIGIT_in_rnumber1333); 
                    	    DIGIT116_tree = (Object)adaptor.create(DIGIT116);
                    	    adaptor.addChild(root_0, DIGIT116_tree);


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt35 >= 1 ) break loop35;
                                EarlyExitException eee =
                                    new EarlyExitException(35, input);
                                throw eee;
                        }
                        cnt35++;
                    } while (true);

                    // Label.g:178:14: ( DOT ( DIGIT )* )?
                    int alt37=2;
                    int LA37_0 = input.LA(1);

                    if ( (LA37_0==DOT) ) {
                        alt37=1;
                    }
                    switch (alt37) {
                        case 1 :
                            // Label.g:178:15: DOT ( DIGIT )*
                            {
                            DOT117=(Token)match(input,DOT,FOLLOW_DOT_in_rnumber1337); 
                            DOT117_tree = (Object)adaptor.create(DOT117);
                            adaptor.addChild(root_0, DOT117_tree);

                            // Label.g:178:19: ( DIGIT )*
                            loop36:
                            do {
                                int alt36=2;
                                int LA36_0 = input.LA(1);

                                if ( (LA36_0==DIGIT) ) {
                                    alt36=1;
                                }


                                switch (alt36) {
                            	case 1 :
                            	    // Label.g:178:19: DIGIT
                            	    {
                            	    DIGIT118=(Token)match(input,DIGIT,FOLLOW_DIGIT_in_rnumber1339); 
                            	    DIGIT118_tree = (Object)adaptor.create(DIGIT118);
                            	    adaptor.addChild(root_0, DIGIT118_tree);


                            	    }
                            	    break;

                            	default :
                            	    break loop36;
                                }
                            } while (true);


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // Label.g:178:30: DOT ( DIGIT )+
                    {
                    DOT119=(Token)match(input,DOT,FOLLOW_DOT_in_rnumber1346); 
                    DOT119_tree = (Object)adaptor.create(DOT119);
                    adaptor.addChild(root_0, DOT119_tree);

                    // Label.g:178:34: ( DIGIT )+
                    int cnt38=0;
                    loop38:
                    do {
                        int alt38=2;
                        int LA38_0 = input.LA(1);

                        if ( (LA38_0==DIGIT) ) {
                            alt38=1;
                        }


                        switch (alt38) {
                    	case 1 :
                    	    // Label.g:178:34: DIGIT
                    	    {
                    	    DIGIT120=(Token)match(input,DIGIT,FOLLOW_DIGIT_in_rnumber1348); 
                    	    DIGIT120_tree = (Object)adaptor.create(DIGIT120);
                    	    adaptor.addChild(root_0, DIGIT120_tree);


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt38 >= 1 ) break loop38;
                                EarlyExitException eee =
                                    new EarlyExitException(38, input);
                                throw eee;
                        }
                        cnt38++;
                    } while (true);


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
    // $ANTLR end rnumber

    // Delegated rules


    protected DFA1 dfa1 = new DFA1(this);
    protected DFA12 dfa12 = new DFA12(this);
    static final String DFA1_eotS =
        "\25\uffff";
    static final String DFA1_eofS =
        "\1\4\2\uffff\1\15\1\uffff\10\15\1\uffff\1\15\1\uffff\5\15";
    static final String DFA1_minS =
        "\1\4\2\uffff\1\4\1\uffff\10\4\1\uffff\1\4\1\uffff\5\4";
    static final String DFA1_maxS =
        "\1\66\2\uffff\1\66\1\uffff\10\66\1\uffff\1\66\1\uffff\5\66";
    static final String DFA1_acceptS =
        "\1\uffff\2\2\1\uffff\1\2\10\uffff\1\2\1\uffff\1\1\5\uffff";
    static final String DFA1_specialS =
        "\1\0\2\uffff\1\16\1\uffff\1\13\1\6\1\7\1\5\1\17\1\14\1\12\1\4\1"+
        "\uffff\1\15\1\uffff\1\10\1\3\1\2\1\11\1\1}>";
    static final String[] DFA1_transitionS = {
            "\1\7\1\10\1\11\1\12\1\13\1\1\1\3\1\5\1\6\1\14\13\1\4\15\1\2"+
            "\1\1\1\15\2\2\1\uffff\2\2\1\uffff\21\15",
            "",
            "",
            "\31\15\1\16\1\17\2\15\2\uffff\1\15\2\uffff\21\15",
            "",
            "\31\15\1\16\1\17\2\15\2\uffff\1\15\2\uffff\21\15",
            "\31\15\1\16\1\17\2\15\2\uffff\1\15\2\uffff\21\15",
            "\31\15\1\20\1\17\2\15\2\uffff\1\15\2\uffff\21\15",
            "\31\15\1\20\1\17\2\15\2\uffff\1\15\2\uffff\21\15",
            "\31\15\1\20\1\17\2\15\2\uffff\1\15\2\uffff\21\15",
            "\31\15\1\20\1\17\2\15\2\uffff\1\15\2\uffff\21\15",
            "\31\15\1\20\1\17\2\15\2\uffff\1\15\2\uffff\21\15",
            "\32\15\1\17\2\15\2\uffff\1\15\2\uffff\21\15",
            "",
            "\32\15\1\uffff\2\15\2\uffff\1\15\2\uffff\17\15\1\21\1\15",
            "",
            "\32\15\1\uffff\2\15\2\uffff\1\15\2\uffff\17\15\1\22\1\15",
            "\32\15\1\17\2\15\2\uffff\1\15\2\uffff\10\15\1\23\10\15",
            "\32\15\1\17\2\15\2\uffff\1\15\2\uffff\10\15\1\24\10\15",
            "\32\15\1\17\2\15\2\uffff\1\15\2\uffff\10\15\1\23\10\15",
            "\32\15\1\17\2\15\2\uffff\1\15\2\uffff\10\15\1\24\10\15"
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
            return "()* loopback of 57:6: ( prefix )*";
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
                        if ( (LA1_0==REM||(LA1_0>=INT && LA1_0<=PATH)||LA1_0==COLON) ) {s = 1;}

                        else if ( (LA1_0==EQUALS||(LA1_0>=PLING && LA1_0<=LBRACE)||(LA1_0>=QUERY && LA1_0<=SQUOTE)) && ( isGraph )) {s = 2;}

                        else if ( (LA1_0==FORALL) ) {s = 3;}

                        else if ( (LA1_0==EOF) ) {s = 4;}

                        else if ( (LA1_0==FORALLX) ) {s = 5;}

                        else if ( (LA1_0==EXISTS) ) {s = 6;}

                        else if ( (LA1_0==NEW) ) {s = 7;}

                        else if ( (LA1_0==DEL) ) {s = 8;}

                        else if ( (LA1_0==NOT) ) {s = 9;}

                        else if ( (LA1_0==USE) ) {s = 10;}

                        else if ( (LA1_0==CNEW) ) {s = 11;}

                        else if ( (LA1_0==NESTED) ) {s = 12;}

                        else if ( ((LA1_0>=EMPTY && LA1_0<=FALSE)||LA1_0==DOLLAR||(LA1_0>=DQTEXT && LA1_0<=DIGIT)) && ( isGraph )) {s = 13;}

                         
                        input.seek(index1_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA1_20 = input.LA(1);

                         
                        int index1_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_20==EOF||(LA1_20>=NEW && LA1_20<=EQUALS)||(LA1_20>=DOLLAR && LA1_20<=PLING)||LA1_20==QUERY||(LA1_20>=DQTEXT && LA1_20<=RPAR)||(LA1_20>=LSQUARE && LA1_20<=DIGIT)) && ( isGraph )) {s = 13;}

                        else if ( (LA1_20==IDENTCHAR) ) {s = 20;}

                        else if ( (LA1_20==COLON) ) {s = 15;}

                         
                        input.seek(index1_20);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA1_18 = input.LA(1);

                         
                        int index1_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_18==IDENTCHAR) ) {s = 20;}

                        else if ( (LA1_18==COLON) ) {s = 15;}

                        else if ( (LA1_18==EOF||(LA1_18>=NEW && LA1_18<=EQUALS)||(LA1_18>=DOLLAR && LA1_18<=PLING)||LA1_18==QUERY||(LA1_18>=DQTEXT && LA1_18<=RPAR)||(LA1_18>=LSQUARE && LA1_18<=DIGIT)) && ( isGraph )) {s = 13;}

                         
                        input.seek(index1_18);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA1_17 = input.LA(1);

                         
                        int index1_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_17==IDENTCHAR) ) {s = 19;}

                        else if ( (LA1_17==COLON) ) {s = 15;}

                        else if ( (LA1_17==EOF||(LA1_17>=NEW && LA1_17<=EQUALS)||(LA1_17>=DOLLAR && LA1_17<=PLING)||LA1_17==QUERY||(LA1_17>=DQTEXT && LA1_17<=RPAR)||(LA1_17>=LSQUARE && LA1_17<=DIGIT)) && ( isGraph )) {s = 13;}

                         
                        input.seek(index1_17);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA1_12 = input.LA(1);

                         
                        int index1_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_12==COLON) ) {s = 15;}

                        else if ( (LA1_12==EOF||(LA1_12>=NEW && LA1_12<=EQUALS)||(LA1_12>=DOLLAR && LA1_12<=PLING)||LA1_12==QUERY||(LA1_12>=DQTEXT && LA1_12<=DIGIT)) && ( isGraph )) {s = 13;}

                         
                        input.seek(index1_12);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA1_8 = input.LA(1);

                         
                        int index1_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_8==EQUALS) ) {s = 16;}

                        else if ( (LA1_8==COLON) ) {s = 15;}

                        else if ( (LA1_8==EOF||(LA1_8>=NEW && LA1_8<=FALSE)||(LA1_8>=DOLLAR && LA1_8<=PLING)||LA1_8==QUERY||(LA1_8>=DQTEXT && LA1_8<=DIGIT)) && ( isGraph )) {s = 13;}

                         
                        input.seek(index1_8);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA1_6 = input.LA(1);

                         
                        int index1_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_6==EQUALS) ) {s = 14;}

                        else if ( (LA1_6==COLON) ) {s = 15;}

                        else if ( (LA1_6==EOF||(LA1_6>=NEW && LA1_6<=FALSE)||(LA1_6>=DOLLAR && LA1_6<=PLING)||LA1_6==QUERY||(LA1_6>=DQTEXT && LA1_6<=DIGIT)) && ( isGraph )) {s = 13;}

                         
                        input.seek(index1_6);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA1_7 = input.LA(1);

                         
                        int index1_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_7==EQUALS) ) {s = 16;}

                        else if ( (LA1_7==COLON) ) {s = 15;}

                        else if ( (LA1_7==EOF||(LA1_7>=NEW && LA1_7<=FALSE)||(LA1_7>=DOLLAR && LA1_7<=PLING)||LA1_7==QUERY||(LA1_7>=DQTEXT && LA1_7<=DIGIT)) && ( isGraph )) {s = 13;}

                         
                        input.seek(index1_7);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA1_16 = input.LA(1);

                         
                        int index1_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_16==LETTER) ) {s = 18;}

                        else if ( (LA1_16==EOF||(LA1_16>=NEW && LA1_16<=EQUALS)||(LA1_16>=DOLLAR && LA1_16<=PLING)||LA1_16==QUERY||(LA1_16>=DQTEXT && LA1_16<=UNDER)||LA1_16==DIGIT) && ( isGraph )) {s = 13;}

                         
                        input.seek(index1_16);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA1_19 = input.LA(1);

                         
                        int index1_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_19==COLON) ) {s = 15;}

                        else if ( (LA1_19==IDENTCHAR) ) {s = 19;}

                        else if ( (LA1_19==EOF||(LA1_19>=NEW && LA1_19<=EQUALS)||(LA1_19>=DOLLAR && LA1_19<=PLING)||LA1_19==QUERY||(LA1_19>=DQTEXT && LA1_19<=RPAR)||(LA1_19>=LSQUARE && LA1_19<=DIGIT)) && ( isGraph )) {s = 13;}

                         
                        input.seek(index1_19);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA1_11 = input.LA(1);

                         
                        int index1_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_11==EQUALS) ) {s = 16;}

                        else if ( (LA1_11==EOF||(LA1_11>=NEW && LA1_11<=FALSE)||(LA1_11>=DOLLAR && LA1_11<=PLING)||LA1_11==QUERY||(LA1_11>=DQTEXT && LA1_11<=DIGIT)) && ( isGraph )) {s = 13;}

                        else if ( (LA1_11==COLON) ) {s = 15;}

                         
                        input.seek(index1_11);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA1_5 = input.LA(1);

                         
                        int index1_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_5==EQUALS) ) {s = 14;}

                        else if ( (LA1_5==COLON) ) {s = 15;}

                        else if ( (LA1_5==EOF||(LA1_5>=NEW && LA1_5<=FALSE)||(LA1_5>=DOLLAR && LA1_5<=PLING)||LA1_5==QUERY||(LA1_5>=DQTEXT && LA1_5<=DIGIT)) && ( isGraph )) {s = 13;}

                         
                        input.seek(index1_5);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA1_10 = input.LA(1);

                         
                        int index1_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_10==EQUALS) ) {s = 16;}

                        else if ( (LA1_10==COLON) ) {s = 15;}

                        else if ( (LA1_10==EOF||(LA1_10>=NEW && LA1_10<=FALSE)||(LA1_10>=DOLLAR && LA1_10<=PLING)||LA1_10==QUERY||(LA1_10>=DQTEXT && LA1_10<=DIGIT)) && ( isGraph )) {s = 13;}

                         
                        input.seek(index1_10);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA1_14 = input.LA(1);

                         
                        int index1_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_14==EOF||(LA1_14>=NEW && LA1_14<=EQUALS)||(LA1_14>=DOLLAR && LA1_14<=PLING)||LA1_14==QUERY||(LA1_14>=DQTEXT && LA1_14<=UNDER)||LA1_14==DIGIT) && ( isGraph )) {s = 13;}

                        else if ( (LA1_14==LETTER) ) {s = 17;}

                         
                        input.seek(index1_14);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA1_3 = input.LA(1);

                         
                        int index1_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_3==EQUALS) ) {s = 14;}

                        else if ( (LA1_3==EOF||(LA1_3>=NEW && LA1_3<=FALSE)||(LA1_3>=DOLLAR && LA1_3<=PLING)||LA1_3==QUERY||(LA1_3>=DQTEXT && LA1_3<=DIGIT)) && ( isGraph )) {s = 13;}

                        else if ( (LA1_3==COLON) ) {s = 15;}

                         
                        input.seek(index1_3);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA1_9 = input.LA(1);

                         
                        int index1_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_9==EQUALS) ) {s = 16;}

                        else if ( (LA1_9==COLON) ) {s = 15;}

                        else if ( (LA1_9==EOF||(LA1_9>=NEW && LA1_9<=FALSE)||(LA1_9>=DOLLAR && LA1_9<=PLING)||LA1_9==QUERY||(LA1_9>=DQTEXT && LA1_9<=DIGIT)) && ( isGraph )) {s = 13;}

                         
                        input.seek(index1_9);
                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 1, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA12_eotS =
        "\27\uffff";
    static final String DFA12_eofS =
        "\2\uffff\14\16\2\uffff\1\16\4\uffff\2\16";
    static final String DFA12_minS =
        "\1\4\1\uffff\14\4\2\uffff\1\4\4\uffff\2\4";
    static final String DFA12_maxS =
        "\1\66\1\uffff\14\66\2\uffff\1\66\4\uffff\2\66";
    static final String DFA12_acceptS =
        "\1\uffff\1\1\14\uffff\1\7\1\2\1\uffff\1\3\1\4\1\5\1\6\2\uffff";
    static final String DFA12_specialS =
        "\1\15\1\uffff\1\1\1\6\1\7\1\14\1\4\1\10\1\17\1\0\1\3\1\16\1\5\1"+
        "\11\2\uffff\1\2\4\uffff\1\13\1\12}>";
    static final String[] DFA12_transitionS = {
            "\5\16\1\2\4\16\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\3\1\14\1\15"+
            "\1\4\5\16\1\1\3\16\1\uffff\2\16\1\uffff\21\16",
            "",
            "\32\16\1\17\2\16\2\uffff\1\16\2\uffff\21\16",
            "\31\16\1\20\1\21\2\16\2\uffff\1\16\2\uffff\21\16",
            "\32\16\1\22\2\16\2\uffff\1\16\2\uffff\21\16",
            "\32\16\1\23\2\16\2\uffff\1\16\2\uffff\21\16",
            "\32\16\1\23\2\16\2\uffff\1\16\2\uffff\21\16",
            "\32\16\1\23\2\16\2\uffff\1\16\2\uffff\21\16",
            "\32\16\1\23\2\16\2\uffff\1\16\2\uffff\21\16",
            "\32\16\1\23\2\16\2\uffff\1\16\2\uffff\21\16",
            "\32\16\1\23\2\16\2\uffff\1\16\2\uffff\21\16",
            "\32\16\1\23\2\16\2\uffff\1\16\2\uffff\21\16",
            "\32\16\1\24\2\16\2\uffff\1\16\2\uffff\21\16",
            "\32\16\1\24\2\16\2\uffff\1\16\2\uffff\21\16",
            "",
            "",
            "\32\16\1\uffff\1\25\1\16\2\uffff\1\16\2\uffff\21\16",
            "",
            "",
            "",
            "",
            "\32\16\1\uffff\2\16\2\uffff\1\16\2\uffff\20\16\1\26",
            "\32\16\1\21\2\16\2\uffff\1\16\2\uffff\20\16\1\26"
    };

    static final short[] DFA12_eot = DFA.unpackEncodedString(DFA12_eotS);
    static final short[] DFA12_eof = DFA.unpackEncodedString(DFA12_eofS);
    static final char[] DFA12_min = DFA.unpackEncodedStringToUnsignedChars(DFA12_minS);
    static final char[] DFA12_max = DFA.unpackEncodedStringToUnsignedChars(DFA12_maxS);
    static final short[] DFA12_accept = DFA.unpackEncodedString(DFA12_acceptS);
    static final short[] DFA12_special = DFA.unpackEncodedString(DFA12_specialS);
    static final short[][] DFA12_transition;

    static {
        int numStates = DFA12_transitionS.length;
        DFA12_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA12_transition[i] = DFA.unpackEncodedString(DFA12_transitionS[i]);
        }
    }

    class DFA12 extends DFA {

        public DFA12(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 12;
            this.eot = DFA12_eot;
            this.eof = DFA12_eof;
            this.min = DFA12_min;
            this.max = DFA12_max;
            this.accept = DFA12_accept;
            this.special = DFA12_special;
            this.transition = DFA12_transition;
        }
        public String getDescription() {
            return "65:1: actualLabel : ( COLON a= ( ( . )* ) -> ^( ATOM $a) | 'rem' COLON ( options {greedy=true; } : . )* | PAR ( EQUALS DOLLAR number )? COLON | PATH COLON ( PLING )? regExpr | attrLabel | nodeLabel | {...}? => ruleLabel );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA12_9 = input.LA(1);

                         
                        int index12_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA12_9==COLON) ) {s = 19;}

                        else if ( (LA12_9==EOF||(LA12_9>=NEW && LA12_9<=EQUALS)||(LA12_9>=DOLLAR && LA12_9<=PLING)||LA12_9==QUERY||(LA12_9>=DQTEXT && LA12_9<=DIGIT)) && ( isGraph )) {s = 14;}

                         
                        input.seek(index12_9);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA12_2 = input.LA(1);

                         
                        int index12_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA12_2==COLON) ) {s = 15;}

                        else if ( (LA12_2==EOF||(LA12_2>=NEW && LA12_2<=EQUALS)||(LA12_2>=DOLLAR && LA12_2<=PLING)||LA12_2==QUERY||(LA12_2>=DQTEXT && LA12_2<=DIGIT)) && ( isGraph )) {s = 14;}

                         
                        input.seek(index12_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA12_16 = input.LA(1);

                         
                        int index12_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA12_16==DOLLAR) ) {s = 21;}

                        else if ( (LA12_16==EOF||(LA12_16>=NEW && LA12_16<=EQUALS)||LA12_16==PLING||LA12_16==QUERY||(LA12_16>=DQTEXT && LA12_16<=DIGIT)) && ( isGraph )) {s = 14;}

                         
                        input.seek(index12_16);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA12_10 = input.LA(1);

                         
                        int index12_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA12_10==COLON) ) {s = 19;}

                        else if ( (LA12_10==EOF||(LA12_10>=NEW && LA12_10<=EQUALS)||(LA12_10>=DOLLAR && LA12_10<=PLING)||LA12_10==QUERY||(LA12_10>=DQTEXT && LA12_10<=DIGIT)) && ( isGraph )) {s = 14;}

                         
                        input.seek(index12_10);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA12_6 = input.LA(1);

                         
                        int index12_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA12_6==COLON) ) {s = 19;}

                        else if ( (LA12_6==EOF||(LA12_6>=NEW && LA12_6<=EQUALS)||(LA12_6>=DOLLAR && LA12_6<=PLING)||LA12_6==QUERY||(LA12_6>=DQTEXT && LA12_6<=DIGIT)) && ( isGraph )) {s = 14;}

                         
                        input.seek(index12_6);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA12_12 = input.LA(1);

                         
                        int index12_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA12_12==COLON) ) {s = 20;}

                        else if ( (LA12_12==EOF||(LA12_12>=NEW && LA12_12<=EQUALS)||(LA12_12>=DOLLAR && LA12_12<=PLING)||LA12_12==QUERY||(LA12_12>=DQTEXT && LA12_12<=DIGIT)) && ( isGraph )) {s = 14;}

                         
                        input.seek(index12_12);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA12_3 = input.LA(1);

                         
                        int index12_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA12_3==EQUALS) ) {s = 16;}

                        else if ( (LA12_3==EOF||(LA12_3>=NEW && LA12_3<=FALSE)||(LA12_3>=DOLLAR && LA12_3<=PLING)||LA12_3==QUERY||(LA12_3>=DQTEXT && LA12_3<=DIGIT)) && ( isGraph )) {s = 14;}

                        else if ( (LA12_3==COLON) ) {s = 17;}

                         
                        input.seek(index12_3);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA12_4 = input.LA(1);

                         
                        int index12_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA12_4==COLON) ) {s = 18;}

                        else if ( (LA12_4==EOF||(LA12_4>=NEW && LA12_4<=EQUALS)||(LA12_4>=DOLLAR && LA12_4<=PLING)||LA12_4==QUERY||(LA12_4>=DQTEXT && LA12_4<=DIGIT)) && ( isGraph )) {s = 14;}

                         
                        input.seek(index12_4);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA12_7 = input.LA(1);

                         
                        int index12_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA12_7==COLON) ) {s = 19;}

                        else if ( (LA12_7==EOF||(LA12_7>=NEW && LA12_7<=EQUALS)||(LA12_7>=DOLLAR && LA12_7<=PLING)||LA12_7==QUERY||(LA12_7>=DQTEXT && LA12_7<=DIGIT)) && ( isGraph )) {s = 14;}

                         
                        input.seek(index12_7);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA12_13 = input.LA(1);

                         
                        int index12_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA12_13==COLON) ) {s = 20;}

                        else if ( (LA12_13==EOF||(LA12_13>=NEW && LA12_13<=EQUALS)||(LA12_13>=DOLLAR && LA12_13<=PLING)||LA12_13==QUERY||(LA12_13>=DQTEXT && LA12_13<=DIGIT)) && ( isGraph )) {s = 14;}

                         
                        input.seek(index12_13);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA12_22 = input.LA(1);

                         
                        int index12_22 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA12_22==EOF||(LA12_22>=NEW && LA12_22<=EQUALS)||(LA12_22>=DOLLAR && LA12_22<=PLING)||LA12_22==QUERY||(LA12_22>=DQTEXT && LA12_22<=LETTER)) && ( isGraph )) {s = 14;}

                        else if ( (LA12_22==DIGIT) ) {s = 22;}

                        else if ( (LA12_22==COLON) ) {s = 17;}

                         
                        input.seek(index12_22);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA12_21 = input.LA(1);

                         
                        int index12_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA12_21==EOF||(LA12_21>=NEW && LA12_21<=EQUALS)||(LA12_21>=DOLLAR && LA12_21<=PLING)||LA12_21==QUERY||(LA12_21>=DQTEXT && LA12_21<=LETTER)) && ( isGraph )) {s = 14;}

                        else if ( (LA12_21==DIGIT) ) {s = 22;}

                         
                        input.seek(index12_21);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA12_5 = input.LA(1);

                         
                        int index12_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA12_5==COLON) ) {s = 19;}

                        else if ( (LA12_5==EOF||(LA12_5>=NEW && LA12_5<=EQUALS)||(LA12_5>=DOLLAR && LA12_5<=PLING)||LA12_5==QUERY||(LA12_5>=DQTEXT && LA12_5<=DIGIT)) && ( isGraph )) {s = 14;}

                         
                        input.seek(index12_5);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA12_0 = input.LA(1);

                         
                        int index12_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA12_0==COLON) ) {s = 1;}

                        else if ( (LA12_0==REM) ) {s = 2;}

                        else if ( (LA12_0==PAR) ) {s = 3;}

                        else if ( (LA12_0==PATH) ) {s = 4;}

                        else if ( (LA12_0==INT) ) {s = 5;}

                        else if ( (LA12_0==REAL) ) {s = 6;}

                        else if ( (LA12_0==STRING) ) {s = 7;}

                        else if ( (LA12_0==BOOL) ) {s = 8;}

                        else if ( (LA12_0==ATTR) ) {s = 9;}

                        else if ( (LA12_0==PROD) ) {s = 10;}

                        else if ( (LA12_0==ARG) ) {s = 11;}

                        else if ( (LA12_0==TYPE) ) {s = 12;}

                        else if ( (LA12_0==FLAG) ) {s = 13;}

                        else if ( ((LA12_0>=NEW && LA12_0<=CNEW)||(LA12_0>=FORALL && LA12_0<=NESTED)||(LA12_0>=EMPTY && LA12_0<=EQUALS)||(LA12_0>=DOLLAR && LA12_0<=LBRACE)||(LA12_0>=QUERY && LA12_0<=SQUOTE)||(LA12_0>=DQTEXT && LA12_0<=DIGIT)) && ( isGraph )) {s = 14;}

                         
                        input.seek(index12_0);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA12_11 = input.LA(1);

                         
                        int index12_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA12_11==COLON) ) {s = 19;}

                        else if ( (LA12_11==EOF||(LA12_11>=NEW && LA12_11<=EQUALS)||(LA12_11>=DOLLAR && LA12_11<=PLING)||LA12_11==QUERY||(LA12_11>=DQTEXT && LA12_11<=DIGIT)) && ( isGraph )) {s = 14;}

                         
                        input.seek(index12_11);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA12_8 = input.LA(1);

                         
                        int index12_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA12_8==COLON) ) {s = 19;}

                        else if ( (LA12_8==EOF||(LA12_8>=NEW && LA12_8<=EQUALS)||(LA12_8>=DOLLAR && LA12_8<=PLING)||LA12_8==QUERY||(LA12_8>=DQTEXT && LA12_8<=DIGIT)) && ( isGraph )) {s = 14;}

                         
                        input.seek(index12_8);
                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 12, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_prefix_in_label248 = new BitSet(new long[]{0x007FFFDBFFFFFFF2L});
    public static final BitSet FOLLOW_actualLabel_in_label251 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FORALL_in_prefix269 = new BitSet(new long[]{0x0000000060000000L});
    public static final BitSet FOLLOW_FORALLX_in_prefix274 = new BitSet(new long[]{0x0000000060000000L});
    public static final BitSet FOLLOW_EXISTS_in_prefix279 = new BitSet(new long[]{0x0000000060000000L});
    public static final BitSet FOLLOW_EQUALS_in_prefix285 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_ident_in_prefix287 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_COLON_in_prefix291 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_prefix302 = new BitSet(new long[]{0x0000000060000000L});
    public static final BitSet FOLLOW_DEL_in_prefix307 = new BitSet(new long[]{0x0000000060000000L});
    public static final BitSet FOLLOW_NOT_in_prefix312 = new BitSet(new long[]{0x0000000060000000L});
    public static final BitSet FOLLOW_USE_in_prefix317 = new BitSet(new long[]{0x0000000060000000L});
    public static final BitSet FOLLOW_CNEW_in_prefix322 = new BitSet(new long[]{0x0000000060000000L});
    public static final BitSet FOLLOW_EQUALS_in_prefix328 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_ident_in_prefix330 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_COLON_in_prefix334 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NESTED_in_prefix342 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_COLON_in_prefix344 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_actualLabel356 = new BitSet(new long[]{0x007FFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_REM_in_actualLabel379 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_COLON_in_actualLabel382 = new BitSet(new long[]{0x007FFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_PAR_in_actualLabel412 = new BitSet(new long[]{0x0000000060000000L});
    public static final BitSet FOLLOW_EQUALS_in_actualLabel416 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_DOLLAR_in_actualLabel419 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_number_in_actualLabel422 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_COLON_in_actualLabel426 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PATH_in_actualLabel434 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_COLON_in_actualLabel437 = new BitSet(new long[]{0x0000521920000000L});
    public static final BitSet FOLLOW_PLING_in_actualLabel440 = new BitSet(new long[]{0x0000521920000000L});
    public static final BitSet FOLLOW_regExpr_in_actualLabel443 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attrLabel_in_actualLabel450 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nodeLabel_in_actualLabel457 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleLabel_in_actualLabel473 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_wildcard_in_ruleLabel488 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_ruleLabel495 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_ruleLabel502 = new BitSet(new long[]{0x0000521920000000L});
    public static final BitSet FOLLOW_regExpr_in_ruleLabel505 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_RBRACE_in_ruleLabel507 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sqText_in_ruleLabel515 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLING_in_ruleLabel530 = new BitSet(new long[]{0x007FFFDBFFFFFFF0L});
    public static final BitSet FOLLOW_ruleLabel_in_ruleLabel533 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDefault_in_ruleLabel540 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_graphDefault564 = new BitSet(new long[]{0x007FFFFFBFFFFFF2L});
    public static final BitSet FOLLOW_set_in_ruleDefault584 = new BitSet(new long[]{0x007FFFC9BFFFFFF2L});
    public static final BitSet FOLLOW_set_in_ruleDefault618 = new BitSet(new long[]{0x007FFFC9BFFFFFF2L});
    public static final BitSet FOLLOW_TYPE_in_nodeLabel654 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_COLON_in_nodeLabel657 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_ident_in_nodeLabel660 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLAG_in_nodeLabel667 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_COLON_in_nodeLabel670 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_ident_in_nodeLabel673 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_attrLabel687 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_COLON_in_attrLabel690 = new BitSet(new long[]{0x0060000000000002L});
    public static final BitSet FOLLOW_number_in_attrLabel694 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ident_in_attrLabel698 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REAL_in_attrLabel707 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_COLON_in_attrLabel710 = new BitSet(new long[]{0x0060010000000002L});
    public static final BitSet FOLLOW_rnumber_in_attrLabel714 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ident_in_attrLabel718 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_attrLabel727 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_COLON_in_attrLabel730 = new BitSet(new long[]{0x0020004000000002L});
    public static final BitSet FOLLOW_DQTEXT_in_attrLabel734 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ident_in_attrLabel738 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_attrLabel747 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_COLON_in_attrLabel750 = new BitSet(new long[]{0x0020000018000002L});
    public static final BitSet FOLLOW_TRUE_in_attrLabel754 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_attrLabel758 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ident_in_attrLabel762 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ATTR_in_attrLabel771 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_COLON_in_attrLabel774 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PROD_in_attrLabel782 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_COLON_in_attrLabel785 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARG_in_attrLabel793 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_COLON_in_attrLabel796 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_number_in_attrLabel799 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_choice_in_regExpr814 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sequence_in_choice826 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_BAR_in_choice829 = new BitSet(new long[]{0x0000521920000000L});
    public static final BitSet FOLLOW_choice_in_choice832 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unary_in_sequence846 = new BitSet(new long[]{0x0000010000000002L});
    public static final BitSet FOLLOW_DOT_in_sequence849 = new BitSet(new long[]{0x0000521920000000L});
    public static final BitSet FOLLOW_sequence_in_sequence852 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_unary866 = new BitSet(new long[]{0x0000521920000000L});
    public static final BitSet FOLLOW_unary_in_unary868 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_unary875 = new BitSet(new long[]{0x00000C0000000002L});
    public static final BitSet FOLLOW_STAR_in_unary878 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_unary883 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sqText_in_atom898 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atomLabel_in_atom913 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_atom928 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_atom935 = new BitSet(new long[]{0x0000521920000000L});
    public static final BitSet FOLLOW_regExpr_in_atom937 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RPAR_in_atom939 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_wildcard_in_atom946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTCHAR_in_atomLabel961 = new BitSet(new long[]{0x0000400000000002L});
    public static final BitSet FOLLOW_QUERY_in_wildcard980 = new BitSet(new long[]{0x0020800000000000L});
    public static final BitSet FOLLOW_ident_in_wildcard983 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_LSQUARE_in_wildcard986 = new BitSet(new long[]{0x0001521920000000L});
    public static final BitSet FOLLOW_HAT_in_wildcard989 = new BitSet(new long[]{0x0001521920000000L});
    public static final BitSet FOLLOW_atom_in_wildcard992 = new BitSet(new long[]{0x0006000000000000L});
    public static final BitSet FOLLOW_COMMA_in_wildcard995 = new BitSet(new long[]{0x0001521920000000L});
    public static final BitSet FOLLOW_atom_in_wildcard998 = new BitSet(new long[]{0x0006000000000000L});
    public static final BitSet FOLLOW_RSQUARE_in_wildcard1002 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SQUOTE_in_sqText1018 = new BitSet(new long[]{0x007FFFFFFFFFFFF0L});
    public static final BitSet FOLLOW_set_in_sqText1022 = new BitSet(new long[]{0x007FFFFFFFFFFFF0L});
    public static final BitSet FOLLOW_sqTextSpecial_in_sqText1031 = new BitSet(new long[]{0x007FFFFFFFFFFFF0L});
    public static final BitSet FOLLOW_SQUOTE_in_sqText1035 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BSLASH_in_sqTextSpecial1047 = new BitSet(new long[]{0x0000003000000000L});
    public static final BitSet FOLLOW_BSLASH_in_sqTextSpecial1052 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SQUOTE_in_sqTextSpecial1054 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LETTER_in_ident1294 = new BitSet(new long[]{0x0000400000000002L});
    public static final BitSet FOLLOW_IDENTCHAR_in_ident1296 = new BitSet(new long[]{0x0000400000000002L});
    public static final BitSet FOLLOW_DIGIT_in_number1316 = new BitSet(new long[]{0x0040000000000002L});
    public static final BitSet FOLLOW_DIGIT_in_rnumber1333 = new BitSet(new long[]{0x0040010000000002L});
    public static final BitSet FOLLOW_DOT_in_rnumber1337 = new BitSet(new long[]{0x0040000000000002L});
    public static final BitSet FOLLOW_DIGIT_in_rnumber1339 = new BitSet(new long[]{0x0040000000000002L});
    public static final BitSet FOLLOW_DOT_in_rnumber1346 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_DIGIT_in_rnumber1348 = new BitSet(new long[]{0x0040000000000002L});

}
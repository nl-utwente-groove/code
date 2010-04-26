// $ANTLR 3.2 Sep 23, 2009 12:02:23 Label0.g 2010-04-26 17:42:31

package groove.view.parse;
import java.util.LinkedList;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;

public class Label0Parser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "NEW", "DEL", "NOT", "USE", "CNEW", "REM", "FORALL", "FORALLX", "EXISTS", "NESTED", "INT", "REAL", "STRING", "BOOL", "ATTR", "PROD", "ARG", "PAR", "TYPE", "FLAG", "PATH", "EMPTY", "ATOM", "TRUE", "FALSE", "CONSTRAINT", "EQUALS", "IDENT", "COLON", "LABEL", "NUMBER", "LBRACE", "RBRACE", "PLING", "QUERY", "SQUOTE", "BSLASH", "DOT", "BAR", "MINUS", "STAR", "PLUS", "LPAR", "RPAR", "LSQUARE", "HAT", "COMMA", "RSQUARE", "DQUOTE", "DOLLAR", "UNDER", "LETTER", "IDENTCHAR", "DIGIT", "'\\n'"
    };
    public static final int DOLLAR=53;
    public static final int STAR=44;
    public static final int LSQUARE=48;
    public static final int FORALLX=11;
    public static final int LETTER=55;
    public static final int DEL=5;
    public static final int LBRACE=35;
    public static final int NEW=4;
    public static final int IDENTCHAR=56;
    public static final int DQUOTE=52;
    public static final int EQUALS=30;
    public static final int NOT=6;
    public static final int ATOM=26;
    public static final int EOF=-1;
    public static final int TYPE=22;
    public static final int HAT=49;
    public static final int UNDER=54;
    public static final int T__58=58;
    public static final int PLING=37;
    public static final int LPAR=46;
    public static final int ARG=20;
    public static final int COMMA=50;
    public static final int PATH=24;
    public static final int PROD=19;
    public static final int PAR=21;
    public static final int IDENT=31;
    public static final int PLUS=45;
    public static final int DIGIT=57;
    public static final int EXISTS=12;
    public static final int DOT=41;
    public static final int ATTR=18;
    public static final int RBRACE=36;
    public static final int BOOL=17;
    public static final int NUMBER=34;
    public static final int FORALL=10;
    public static final int INT=14;
    public static final int RSQUARE=51;
    public static final int MINUS=43;
    public static final int REM=9;
    public static final int SQUOTE=39;
    public static final int TRUE=27;
    public static final int CNEW=8;
    public static final int FLAG=23;
    public static final int EMPTY=25;
    public static final int NESTED=13;
    public static final int COLON=32;
    public static final int REAL=15;
    public static final int LABEL=33;
    public static final int QUERY=38;
    public static final int RPAR=47;
    public static final int USE=7;
    public static final int FALSE=28;
    public static final int CONSTRAINT=29;
    public static final int BSLASH=40;
    public static final int BAR=42;
    public static final int STRING=16;

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
        return adaptor;
    }

    public String[] getTokenNames() { return Label0Parser.tokenNames; }
    public String getGrammarFileName() { return "Label0.g"; }


        private boolean isGraph;
        public void setIsGraph(boolean isGraph) {
            this.isGraph = isGraph;
        }
        
        private List<String> errors = new LinkedList<String>();
        public void displayRecognitionError(String[] tokenNames,
                                            RecognitionException e) {
            String hdr = getErrorHeader(e);
            String msg = getErrorMessage(e, tokenNames);
            errors.add(hdr + " " + msg);
        }
        public List<String> getErrors() {
            return errors;
        }


    public static class label_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "label"
    // Label0.g:70:1: label : ( prefixedLabel | specialLabel )? EOF ;
    public final Label0Parser.label_return label() throws RecognitionException {
        Label0Parser.label_return retval = new Label0Parser.label_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EOF3=null;
        Label0Parser.prefixedLabel_return prefixedLabel1 = null;

        Label0Parser.specialLabel_return specialLabel2 = null;


        Object EOF3_tree=null;

        try {
            // Label0.g:71:4: ( ( prefixedLabel | specialLabel )? EOF )
            // Label0.g:71:6: ( prefixedLabel | specialLabel )? EOF
            {
            root_0 = (Object)adaptor.nil();

            // Label0.g:71:6: ( prefixedLabel | specialLabel )?
            int alt1=3;
            alt1 = dfa1.predict(input);
            switch (alt1) {
                case 1 :
                    // Label0.g:71:7: prefixedLabel
                    {
                    pushFollow(FOLLOW_prefixedLabel_in_label257);
                    prefixedLabel1=prefixedLabel();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, prefixedLabel1.getTree());

                    }
                    break;
                case 2 :
                    // Label0.g:71:23: specialLabel
                    {
                    pushFollow(FOLLOW_specialLabel_in_label261);
                    specialLabel2=specialLabel();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, specialLabel2.getTree());

                    }
                    break;

            }

            EOF3=(Token)match(input,EOF,FOLLOW_EOF_in_label265); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // $ANTLR end "label"

    public static class prefixedLabel_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "prefixedLabel"
    // Label0.g:74:1: prefixedLabel : ( ( FORALL | FORALLX | EXISTS ) ( EQUALS IDENT )? COLON prefixedLabel | ( NEW | DEL | NOT | USE | CNEW ) ( EQUALS IDENT )? COLON prefixedLabel | actualLabel );
    public final Label0Parser.prefixedLabel_return prefixedLabel() throws RecognitionException {
        Label0Parser.prefixedLabel_return retval = new Label0Parser.prefixedLabel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token FORALL4=null;
        Token FORALLX5=null;
        Token EXISTS6=null;
        Token EQUALS7=null;
        Token IDENT8=null;
        Token COLON9=null;
        Token NEW11=null;
        Token DEL12=null;
        Token NOT13=null;
        Token USE14=null;
        Token CNEW15=null;
        Token EQUALS16=null;
        Token IDENT17=null;
        Token COLON18=null;
        Label0Parser.prefixedLabel_return prefixedLabel10 = null;

        Label0Parser.prefixedLabel_return prefixedLabel19 = null;

        Label0Parser.actualLabel_return actualLabel20 = null;


        Object FORALL4_tree=null;
        Object FORALLX5_tree=null;
        Object EXISTS6_tree=null;
        Object EQUALS7_tree=null;
        Object IDENT8_tree=null;
        Object COLON9_tree=null;
        Object NEW11_tree=null;
        Object DEL12_tree=null;
        Object NOT13_tree=null;
        Object USE14_tree=null;
        Object CNEW15_tree=null;
        Object EQUALS16_tree=null;
        Object IDENT17_tree=null;
        Object COLON18_tree=null;

        try {
            // Label0.g:75:4: ( ( FORALL | FORALLX | EXISTS ) ( EQUALS IDENT )? COLON prefixedLabel | ( NEW | DEL | NOT | USE | CNEW ) ( EQUALS IDENT )? COLON prefixedLabel | actualLabel )
            int alt6=3;
            alt6 = dfa6.predict(input);
            switch (alt6) {
                case 1 :
                    // Label0.g:75:6: ( FORALL | FORALLX | EXISTS ) ( EQUALS IDENT )? COLON prefixedLabel
                    {
                    root_0 = (Object)adaptor.nil();

                    // Label0.g:75:6: ( FORALL | FORALLX | EXISTS )
                    int alt2=3;
                    switch ( input.LA(1) ) {
                    case FORALL:
                        {
                        alt2=1;
                        }
                        break;
                    case FORALLX:
                        {
                        alt2=2;
                        }
                        break;
                    case EXISTS:
                        {
                        alt2=3;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 2, 0, input);

                        throw nvae;
                    }

                    switch (alt2) {
                        case 1 :
                            // Label0.g:75:8: FORALL
                            {
                            FORALL4=(Token)match(input,FORALL,FOLLOW_FORALL_in_prefixedLabel283); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            FORALL4_tree = (Object)adaptor.create(FORALL4);
                            root_0 = (Object)adaptor.becomeRoot(FORALL4_tree, root_0);
                            }

                            }
                            break;
                        case 2 :
                            // Label0.g:75:18: FORALLX
                            {
                            FORALLX5=(Token)match(input,FORALLX,FOLLOW_FORALLX_in_prefixedLabel288); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            FORALLX5_tree = (Object)adaptor.create(FORALLX5);
                            root_0 = (Object)adaptor.becomeRoot(FORALLX5_tree, root_0);
                            }

                            }
                            break;
                        case 3 :
                            // Label0.g:75:29: EXISTS
                            {
                            EXISTS6=(Token)match(input,EXISTS,FOLLOW_EXISTS_in_prefixedLabel293); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            EXISTS6_tree = (Object)adaptor.create(EXISTS6);
                            root_0 = (Object)adaptor.becomeRoot(EXISTS6_tree, root_0);
                            }

                            }
                            break;

                    }

                    // Label0.g:75:39: ( EQUALS IDENT )?
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==EQUALS) ) {
                        alt3=1;
                    }
                    switch (alt3) {
                        case 1 :
                            // Label0.g:75:40: EQUALS IDENT
                            {
                            EQUALS7=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_prefixedLabel299); if (state.failed) return retval;
                            IDENT8=(Token)match(input,IDENT,FOLLOW_IDENT_in_prefixedLabel302); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            IDENT8_tree = (Object)adaptor.create(IDENT8);
                            adaptor.addChild(root_0, IDENT8_tree);
                            }

                            }
                            break;

                    }

                    COLON9=(Token)match(input,COLON,FOLLOW_COLON_in_prefixedLabel306); if (state.failed) return retval;
                    pushFollow(FOLLOW_prefixedLabel_in_prefixedLabel309);
                    prefixedLabel10=prefixedLabel();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, prefixedLabel10.getTree());

                    }
                    break;
                case 2 :
                    // Label0.g:76:6: ( NEW | DEL | NOT | USE | CNEW ) ( EQUALS IDENT )? COLON prefixedLabel
                    {
                    root_0 = (Object)adaptor.nil();

                    // Label0.g:76:6: ( NEW | DEL | NOT | USE | CNEW )
                    int alt4=5;
                    switch ( input.LA(1) ) {
                    case NEW:
                        {
                        alt4=1;
                        }
                        break;
                    case DEL:
                        {
                        alt4=2;
                        }
                        break;
                    case NOT:
                        {
                        alt4=3;
                        }
                        break;
                    case USE:
                        {
                        alt4=4;
                        }
                        break;
                    case CNEW:
                        {
                        alt4=5;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 4, 0, input);

                        throw nvae;
                    }

                    switch (alt4) {
                        case 1 :
                            // Label0.g:76:8: NEW
                            {
                            NEW11=(Token)match(input,NEW,FOLLOW_NEW_in_prefixedLabel318); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            NEW11_tree = (Object)adaptor.create(NEW11);
                            root_0 = (Object)adaptor.becomeRoot(NEW11_tree, root_0);
                            }

                            }
                            break;
                        case 2 :
                            // Label0.g:76:15: DEL
                            {
                            DEL12=(Token)match(input,DEL,FOLLOW_DEL_in_prefixedLabel323); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            DEL12_tree = (Object)adaptor.create(DEL12);
                            root_0 = (Object)adaptor.becomeRoot(DEL12_tree, root_0);
                            }

                            }
                            break;
                        case 3 :
                            // Label0.g:76:22: NOT
                            {
                            NOT13=(Token)match(input,NOT,FOLLOW_NOT_in_prefixedLabel328); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            NOT13_tree = (Object)adaptor.create(NOT13);
                            root_0 = (Object)adaptor.becomeRoot(NOT13_tree, root_0);
                            }

                            }
                            break;
                        case 4 :
                            // Label0.g:76:29: USE
                            {
                            USE14=(Token)match(input,USE,FOLLOW_USE_in_prefixedLabel333); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            USE14_tree = (Object)adaptor.create(USE14);
                            root_0 = (Object)adaptor.becomeRoot(USE14_tree, root_0);
                            }

                            }
                            break;
                        case 5 :
                            // Label0.g:76:36: CNEW
                            {
                            CNEW15=(Token)match(input,CNEW,FOLLOW_CNEW_in_prefixedLabel338); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            CNEW15_tree = (Object)adaptor.create(CNEW15);
                            root_0 = (Object)adaptor.becomeRoot(CNEW15_tree, root_0);
                            }

                            }
                            break;

                    }

                    // Label0.g:76:44: ( EQUALS IDENT )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==EQUALS) ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // Label0.g:76:45: EQUALS IDENT
                            {
                            EQUALS16=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_prefixedLabel344); if (state.failed) return retval;
                            IDENT17=(Token)match(input,IDENT,FOLLOW_IDENT_in_prefixedLabel347); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            IDENT17_tree = (Object)adaptor.create(IDENT17);
                            adaptor.addChild(root_0, IDENT17_tree);
                            }

                            }
                            break;

                    }

                    COLON18=(Token)match(input,COLON,FOLLOW_COLON_in_prefixedLabel351); if (state.failed) return retval;
                    pushFollow(FOLLOW_prefixedLabel_in_prefixedLabel354);
                    prefixedLabel19=prefixedLabel();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, prefixedLabel19.getTree());

                    }
                    break;
                case 3 :
                    // Label0.g:77:6: actualLabel
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_actualLabel_in_prefixedLabel361);
                    actualLabel20=actualLabel();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, actualLabel20.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // $ANTLR end "prefixedLabel"

    public static class specialLabel_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "specialLabel"
    // Label0.g:80:1: specialLabel : ( REM COLON text | PAR ( EQUALS LABEL )? COLON | NESTED COLON IDENT | INT COLON ( NUMBER | IDENT )? | REAL COLON ( rnumber | IDENT )? | STRING COLON ( dqText | IDENT )? | BOOL COLON ( TRUE | FALSE | IDENT )? | ATTR COLON | PROD COLON | ARG COLON NUMBER );
    public final Label0Parser.specialLabel_return specialLabel() throws RecognitionException {
        Label0Parser.specialLabel_return retval = new Label0Parser.specialLabel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token REM21=null;
        Token COLON22=null;
        Token PAR24=null;
        Token EQUALS25=null;
        Token LABEL26=null;
        Token COLON27=null;
        Token NESTED28=null;
        Token COLON29=null;
        Token IDENT30=null;
        Token INT31=null;
        Token COLON32=null;
        Token set33=null;
        Token REAL34=null;
        Token COLON35=null;
        Token IDENT37=null;
        Token STRING38=null;
        Token COLON39=null;
        Token IDENT41=null;
        Token BOOL42=null;
        Token COLON43=null;
        Token set44=null;
        Token ATTR45=null;
        Token COLON46=null;
        Token PROD47=null;
        Token COLON48=null;
        Token ARG49=null;
        Token COLON50=null;
        Token NUMBER51=null;
        Label0Parser.text_return text23 = null;

        Label0Parser.rnumber_return rnumber36 = null;

        Label0Parser.dqText_return dqText40 = null;


        Object REM21_tree=null;
        Object COLON22_tree=null;
        Object PAR24_tree=null;
        Object EQUALS25_tree=null;
        Object LABEL26_tree=null;
        Object COLON27_tree=null;
        Object NESTED28_tree=null;
        Object COLON29_tree=null;
        Object IDENT30_tree=null;
        Object INT31_tree=null;
        Object COLON32_tree=null;
        Object set33_tree=null;
        Object REAL34_tree=null;
        Object COLON35_tree=null;
        Object IDENT37_tree=null;
        Object STRING38_tree=null;
        Object COLON39_tree=null;
        Object IDENT41_tree=null;
        Object BOOL42_tree=null;
        Object COLON43_tree=null;
        Object set44_tree=null;
        Object ATTR45_tree=null;
        Object COLON46_tree=null;
        Object PROD47_tree=null;
        Object COLON48_tree=null;
        Object ARG49_tree=null;
        Object COLON50_tree=null;
        Object NUMBER51_tree=null;

        try {
            // Label0.g:81:4: ( REM COLON text | PAR ( EQUALS LABEL )? COLON | NESTED COLON IDENT | INT COLON ( NUMBER | IDENT )? | REAL COLON ( rnumber | IDENT )? | STRING COLON ( dqText | IDENT )? | BOOL COLON ( TRUE | FALSE | IDENT )? | ATTR COLON | PROD COLON | ARG COLON NUMBER )
            int alt12=10;
            switch ( input.LA(1) ) {
            case REM:
                {
                alt12=1;
                }
                break;
            case PAR:
                {
                alt12=2;
                }
                break;
            case NESTED:
                {
                alt12=3;
                }
                break;
            case INT:
                {
                alt12=4;
                }
                break;
            case REAL:
                {
                alt12=5;
                }
                break;
            case STRING:
                {
                alt12=6;
                }
                break;
            case BOOL:
                {
                alt12=7;
                }
                break;
            case ATTR:
                {
                alt12=8;
                }
                break;
            case PROD:
                {
                alt12=9;
                }
                break;
            case ARG:
                {
                alt12=10;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }

            switch (alt12) {
                case 1 :
                    // Label0.g:81:6: REM COLON text
                    {
                    root_0 = (Object)adaptor.nil();

                    REM21=(Token)match(input,REM,FOLLOW_REM_in_specialLabel376); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    REM21_tree = (Object)adaptor.create(REM21);
                    root_0 = (Object)adaptor.becomeRoot(REM21_tree, root_0);
                    }
                    COLON22=(Token)match(input,COLON,FOLLOW_COLON_in_specialLabel379); if (state.failed) return retval;
                    pushFollow(FOLLOW_text_in_specialLabel382);
                    text23=text();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, text23.getTree());

                    }
                    break;
                case 2 :
                    // Label0.g:82:6: PAR ( EQUALS LABEL )? COLON
                    {
                    root_0 = (Object)adaptor.nil();

                    PAR24=(Token)match(input,PAR,FOLLOW_PAR_in_specialLabel389); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    PAR24_tree = (Object)adaptor.create(PAR24);
                    root_0 = (Object)adaptor.becomeRoot(PAR24_tree, root_0);
                    }
                    // Label0.g:82:11: ( EQUALS LABEL )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0==EQUALS) ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // Label0.g:82:12: EQUALS LABEL
                            {
                            EQUALS25=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_specialLabel393); if (state.failed) return retval;
                            LABEL26=(Token)match(input,LABEL,FOLLOW_LABEL_in_specialLabel396); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            LABEL26_tree = (Object)adaptor.create(LABEL26);
                            adaptor.addChild(root_0, LABEL26_tree);
                            }

                            }
                            break;

                    }

                    COLON27=(Token)match(input,COLON,FOLLOW_COLON_in_specialLabel400); if (state.failed) return retval;

                    }
                    break;
                case 3 :
                    // Label0.g:83:6: NESTED COLON IDENT
                    {
                    root_0 = (Object)adaptor.nil();

                    NESTED28=(Token)match(input,NESTED,FOLLOW_NESTED_in_specialLabel408); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    NESTED28_tree = (Object)adaptor.create(NESTED28);
                    root_0 = (Object)adaptor.becomeRoot(NESTED28_tree, root_0);
                    }
                    COLON29=(Token)match(input,COLON,FOLLOW_COLON_in_specialLabel411); if (state.failed) return retval;
                    IDENT30=(Token)match(input,IDENT,FOLLOW_IDENT_in_specialLabel414); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IDENT30_tree = (Object)adaptor.create(IDENT30);
                    adaptor.addChild(root_0, IDENT30_tree);
                    }

                    }
                    break;
                case 4 :
                    // Label0.g:85:6: INT COLON ( NUMBER | IDENT )?
                    {
                    root_0 = (Object)adaptor.nil();

                    INT31=(Token)match(input,INT,FOLLOW_INT_in_specialLabel425); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    INT31_tree = (Object)adaptor.create(INT31);
                    root_0 = (Object)adaptor.becomeRoot(INT31_tree, root_0);
                    }
                    COLON32=(Token)match(input,COLON,FOLLOW_COLON_in_specialLabel428); if (state.failed) return retval;
                    // Label0.g:85:18: ( NUMBER | IDENT )?
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( (LA8_0==IDENT||LA8_0==NUMBER) ) {
                        alt8=1;
                    }
                    switch (alt8) {
                        case 1 :
                            // Label0.g:
                            {
                            set33=(Token)input.LT(1);
                            if ( input.LA(1)==IDENT||input.LA(1)==NUMBER ) {
                                input.consume();
                                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set33));
                                state.errorRecovery=false;state.failed=false;
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return retval;}
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }


                            }
                            break;

                    }


                    }
                    break;
                case 5 :
                    // Label0.g:86:6: REAL COLON ( rnumber | IDENT )?
                    {
                    root_0 = (Object)adaptor.nil();

                    REAL34=(Token)match(input,REAL,FOLLOW_REAL_in_specialLabel445); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    REAL34_tree = (Object)adaptor.create(REAL34);
                    root_0 = (Object)adaptor.becomeRoot(REAL34_tree, root_0);
                    }
                    COLON35=(Token)match(input,COLON,FOLLOW_COLON_in_specialLabel448); if (state.failed) return retval;
                    // Label0.g:86:19: ( rnumber | IDENT )?
                    int alt9=3;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==NUMBER||LA9_0==DOT) ) {
                        alt9=1;
                    }
                    else if ( (LA9_0==IDENT) ) {
                        alt9=2;
                    }
                    switch (alt9) {
                        case 1 :
                            // Label0.g:86:20: rnumber
                            {
                            pushFollow(FOLLOW_rnumber_in_specialLabel452);
                            rnumber36=rnumber();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, rnumber36.getTree());

                            }
                            break;
                        case 2 :
                            // Label0.g:86:30: IDENT
                            {
                            IDENT37=(Token)match(input,IDENT,FOLLOW_IDENT_in_specialLabel456); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            IDENT37_tree = (Object)adaptor.create(IDENT37);
                            adaptor.addChild(root_0, IDENT37_tree);
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 6 :
                    // Label0.g:87:6: STRING COLON ( dqText | IDENT )?
                    {
                    root_0 = (Object)adaptor.nil();

                    STRING38=(Token)match(input,STRING,FOLLOW_STRING_in_specialLabel465); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    STRING38_tree = (Object)adaptor.create(STRING38);
                    root_0 = (Object)adaptor.becomeRoot(STRING38_tree, root_0);
                    }
                    COLON39=(Token)match(input,COLON,FOLLOW_COLON_in_specialLabel468); if (state.failed) return retval;
                    // Label0.g:87:21: ( dqText | IDENT )?
                    int alt10=3;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0==DQUOTE) ) {
                        alt10=1;
                    }
                    else if ( (LA10_0==IDENT) ) {
                        alt10=2;
                    }
                    switch (alt10) {
                        case 1 :
                            // Label0.g:87:22: dqText
                            {
                            pushFollow(FOLLOW_dqText_in_specialLabel472);
                            dqText40=dqText();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, dqText40.getTree());

                            }
                            break;
                        case 2 :
                            // Label0.g:87:31: IDENT
                            {
                            IDENT41=(Token)match(input,IDENT,FOLLOW_IDENT_in_specialLabel476); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            IDENT41_tree = (Object)adaptor.create(IDENT41);
                            adaptor.addChild(root_0, IDENT41_tree);
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 7 :
                    // Label0.g:88:6: BOOL COLON ( TRUE | FALSE | IDENT )?
                    {
                    root_0 = (Object)adaptor.nil();

                    BOOL42=(Token)match(input,BOOL,FOLLOW_BOOL_in_specialLabel485); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    BOOL42_tree = (Object)adaptor.create(BOOL42);
                    root_0 = (Object)adaptor.becomeRoot(BOOL42_tree, root_0);
                    }
                    COLON43=(Token)match(input,COLON,FOLLOW_COLON_in_specialLabel488); if (state.failed) return retval;
                    // Label0.g:88:19: ( TRUE | FALSE | IDENT )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( ((LA11_0>=TRUE && LA11_0<=FALSE)||LA11_0==IDENT) ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // Label0.g:
                            {
                            set44=(Token)input.LT(1);
                            if ( (input.LA(1)>=TRUE && input.LA(1)<=FALSE)||input.LA(1)==IDENT ) {
                                input.consume();
                                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set44));
                                state.errorRecovery=false;state.failed=false;
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return retval;}
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }


                            }
                            break;

                    }


                    }
                    break;
                case 8 :
                    // Label0.g:89:6: ATTR COLON
                    {
                    root_0 = (Object)adaptor.nil();

                    ATTR45=(Token)match(input,ATTR,FOLLOW_ATTR_in_specialLabel509); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ATTR45_tree = (Object)adaptor.create(ATTR45);
                    root_0 = (Object)adaptor.becomeRoot(ATTR45_tree, root_0);
                    }
                    COLON46=(Token)match(input,COLON,FOLLOW_COLON_in_specialLabel512); if (state.failed) return retval;

                    }
                    break;
                case 9 :
                    // Label0.g:90:6: PROD COLON
                    {
                    root_0 = (Object)adaptor.nil();

                    PROD47=(Token)match(input,PROD,FOLLOW_PROD_in_specialLabel520); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    PROD47_tree = (Object)adaptor.create(PROD47);
                    root_0 = (Object)adaptor.becomeRoot(PROD47_tree, root_0);
                    }
                    COLON48=(Token)match(input,COLON,FOLLOW_COLON_in_specialLabel523); if (state.failed) return retval;

                    }
                    break;
                case 10 :
                    // Label0.g:91:6: ARG COLON NUMBER
                    {
                    root_0 = (Object)adaptor.nil();

                    ARG49=(Token)match(input,ARG,FOLLOW_ARG_in_specialLabel531); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ARG49_tree = (Object)adaptor.create(ARG49);
                    root_0 = (Object)adaptor.becomeRoot(ARG49_tree, root_0);
                    }
                    COLON50=(Token)match(input,COLON,FOLLOW_COLON_in_specialLabel534); if (state.failed) return retval;
                    NUMBER51=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_specialLabel537); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    NUMBER51_tree = (Object)adaptor.create(NUMBER51);
                    adaptor.addChild(root_0, NUMBER51_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // $ANTLR end "specialLabel"

    public static class actualLabel_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "actualLabel"
    // Label0.g:94:1: actualLabel : ( TYPE COLON IDENT -> ^( ATOM ^( TYPE IDENT ) ) | FLAG COLON IDENT -> ^( ATOM ^( FLAG IDENT ) ) | COLON text -> ^( ATOM text ) | PATH COLON regExpr | ( graphDefault EOF )=>{...}? => graphLabel | ( ruleLabel EOF )=>{...}? => ruleLabel );
    public final Label0Parser.actualLabel_return actualLabel() throws RecognitionException {
        Label0Parser.actualLabel_return retval = new Label0Parser.actualLabel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TYPE52=null;
        Token COLON53=null;
        Token IDENT54=null;
        Token FLAG55=null;
        Token COLON56=null;
        Token IDENT57=null;
        Token COLON58=null;
        Token PATH60=null;
        Token COLON61=null;
        Label0Parser.text_return text59 = null;

        Label0Parser.regExpr_return regExpr62 = null;

        Label0Parser.graphLabel_return graphLabel63 = null;

        Label0Parser.ruleLabel_return ruleLabel64 = null;


        Object TYPE52_tree=null;
        Object COLON53_tree=null;
        Object IDENT54_tree=null;
        Object FLAG55_tree=null;
        Object COLON56_tree=null;
        Object IDENT57_tree=null;
        Object COLON58_tree=null;
        Object PATH60_tree=null;
        Object COLON61_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_IDENT=new RewriteRuleTokenStream(adaptor,"token IDENT");
        RewriteRuleTokenStream stream_FLAG=new RewriteRuleTokenStream(adaptor,"token FLAG");
        RewriteRuleTokenStream stream_TYPE=new RewriteRuleTokenStream(adaptor,"token TYPE");
        RewriteRuleSubtreeStream stream_text=new RewriteRuleSubtreeStream(adaptor,"rule text");
        try {
            // Label0.g:95:4: ( TYPE COLON IDENT -> ^( ATOM ^( TYPE IDENT ) ) | FLAG COLON IDENT -> ^( ATOM ^( FLAG IDENT ) ) | COLON text -> ^( ATOM text ) | PATH COLON regExpr | ( graphDefault EOF )=>{...}? => graphLabel | ( ruleLabel EOF )=>{...}? => ruleLabel )
            int alt13=6;
            alt13 = dfa13.predict(input);
            switch (alt13) {
                case 1 :
                    // Label0.g:95:6: TYPE COLON IDENT
                    {
                    TYPE52=(Token)match(input,TYPE,FOLLOW_TYPE_in_actualLabel552); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_TYPE.add(TYPE52);

                    COLON53=(Token)match(input,COLON,FOLLOW_COLON_in_actualLabel554); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_COLON.add(COLON53);

                    IDENT54=(Token)match(input,IDENT,FOLLOW_IDENT_in_actualLabel556); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_IDENT.add(IDENT54);



                    // AST REWRITE
                    // elements: IDENT, TYPE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 95:23: -> ^( ATOM ^( TYPE IDENT ) )
                    {
                        // Label0.g:95:26: ^( ATOM ^( TYPE IDENT ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ATOM, "ATOM"), root_1);

                        // Label0.g:95:33: ^( TYPE IDENT )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(stream_TYPE.nextNode(), root_2);

                        adaptor.addChild(root_2, stream_IDENT.nextNode());

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // Label0.g:96:6: FLAG COLON IDENT
                    {
                    FLAG55=(Token)match(input,FLAG,FOLLOW_FLAG_in_actualLabel575); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_FLAG.add(FLAG55);

                    COLON56=(Token)match(input,COLON,FOLLOW_COLON_in_actualLabel577); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_COLON.add(COLON56);

                    IDENT57=(Token)match(input,IDENT,FOLLOW_IDENT_in_actualLabel579); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_IDENT.add(IDENT57);



                    // AST REWRITE
                    // elements: FLAG, IDENT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 96:23: -> ^( ATOM ^( FLAG IDENT ) )
                    {
                        // Label0.g:96:26: ^( ATOM ^( FLAG IDENT ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ATOM, "ATOM"), root_1);

                        // Label0.g:96:33: ^( FLAG IDENT )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(stream_FLAG.nextNode(), root_2);

                        adaptor.addChild(root_2, stream_IDENT.nextNode());

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // Label0.g:97:6: COLON text
                    {
                    COLON58=(Token)match(input,COLON,FOLLOW_COLON_in_actualLabel598); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_COLON.add(COLON58);

                    pushFollow(FOLLOW_text_in_actualLabel600);
                    text59=text();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_text.add(text59.getTree());


                    // AST REWRITE
                    // elements: text
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 97:17: -> ^( ATOM text )
                    {
                        // Label0.g:97:20: ^( ATOM text )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ATOM, "ATOM"), root_1);

                        adaptor.addChild(root_1, stream_text.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // Label0.g:98:6: PATH COLON regExpr
                    {
                    root_0 = (Object)adaptor.nil();

                    PATH60=(Token)match(input,PATH,FOLLOW_PATH_in_actualLabel615); if (state.failed) return retval;
                    COLON61=(Token)match(input,COLON,FOLLOW_COLON_in_actualLabel618); if (state.failed) return retval;
                    pushFollow(FOLLOW_regExpr_in_actualLabel621);
                    regExpr62=regExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, regExpr62.getTree());

                    }
                    break;
                case 5 :
                    // Label0.g:99:6: ( graphDefault EOF )=>{...}? => graphLabel
                    {
                    root_0 = (Object)adaptor.nil();

                    if ( !(( isGraph )) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "actualLabel", " isGraph ");
                    }
                    pushFollow(FOLLOW_graphLabel_in_actualLabel640);
                    graphLabel63=graphLabel();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, graphLabel63.getTree());

                    }
                    break;
                case 6 :
                    // Label0.g:100:6: ( ruleLabel EOF )=>{...}? => ruleLabel
                    {
                    root_0 = (Object)adaptor.nil();

                    if ( !(( !isGraph )) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "actualLabel", " !isGraph ");
                    }
                    pushFollow(FOLLOW_ruleLabel_in_actualLabel659);
                    ruleLabel64=ruleLabel();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ruleLabel64.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // $ANTLR end "actualLabel"

    public static class text_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "text"
    // Label0.g:103:1: text : (~ '\\n' )* ;
    public final Label0Parser.text_return text() throws RecognitionException {
        Label0Parser.text_return retval = new Label0Parser.text_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set65=null;

        Object set65_tree=null;

        try {
            // Label0.g:104:4: ( (~ '\\n' )* )
            // Label0.g:104:6: (~ '\\n' )*
            {
            root_0 = (Object)adaptor.nil();

            // Label0.g:104:6: (~ '\\n' )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( ((LA14_0>=NEW && LA14_0<=DIGIT)) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // Label0.g:104:7: ~ '\\n'
            	    {
            	    set65=(Token)input.LT(1);
            	    if ( (input.LA(1)>=NEW && input.LA(1)<=DIGIT) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set65));
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // $ANTLR end "text"

    public static class graphLabel_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "graphLabel"
    // Label0.g:107:1: graphLabel : graphDefault -> ^( ATOM graphDefault ) ;
    public final Label0Parser.graphLabel_return graphLabel() throws RecognitionException {
        Label0Parser.graphLabel_return retval = new Label0Parser.graphLabel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Label0Parser.graphDefault_return graphDefault66 = null;


        RewriteRuleSubtreeStream stream_graphDefault=new RewriteRuleSubtreeStream(adaptor,"rule graphDefault");
        try {
            // Label0.g:108:4: ( graphDefault -> ^( ATOM graphDefault ) )
            // Label0.g:108:6: graphDefault
            {
            pushFollow(FOLLOW_graphDefault_in_graphLabel693);
            graphDefault66=graphDefault();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_graphDefault.add(graphDefault66.getTree());


            // AST REWRITE
            // elements: graphDefault
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 108:19: -> ^( ATOM graphDefault )
            {
                // Label0.g:108:22: ^( ATOM graphDefault )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ATOM, "ATOM"), root_1);

                adaptor.addChild(root_1, stream_graphDefault.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // $ANTLR end "graphLabel"

    public static class graphDefault_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "graphDefault"
    // Label0.g:111:1: graphDefault : (~ COLON )+ ;
    public final Label0Parser.graphDefault_return graphDefault() throws RecognitionException {
        Label0Parser.graphDefault_return retval = new Label0Parser.graphDefault_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set67=null;

        Object set67_tree=null;

        try {
            // Label0.g:112:4: ( (~ COLON )+ )
            // Label0.g:112:6: (~ COLON )+
            {
            root_0 = (Object)adaptor.nil();

            // Label0.g:112:6: (~ COLON )+
            int cnt15=0;
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( ((LA15_0>=NEW && LA15_0<=IDENT)||(LA15_0>=LABEL && LA15_0<=58)) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // Label0.g:112:7: ~ COLON
            	    {
            	    set67=(Token)input.LT(1);
            	    if ( (input.LA(1)>=NEW && input.LA(1)<=IDENT)||(input.LA(1)>=LABEL && input.LA(1)<=58) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set67));
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt15 >= 1 ) break loop15;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(15, input);
                        throw eee;
                }
                cnt15++;
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // $ANTLR end "graphDefault"

    public static class ruleLabel_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ruleLabel"
    // Label0.g:115:1: ruleLabel : ( wildcard | EQUALS | LBRACE regExpr RBRACE | sqText -> ^( ATOM sqText ) | PLING ruleLabel | ruleDefault -> ^( ATOM ruleDefault ) );
    public final Label0Parser.ruleLabel_return ruleLabel() throws RecognitionException {
        Label0Parser.ruleLabel_return retval = new Label0Parser.ruleLabel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EQUALS69=null;
        Token LBRACE70=null;
        Token RBRACE72=null;
        Token PLING74=null;
        Label0Parser.wildcard_return wildcard68 = null;

        Label0Parser.regExpr_return regExpr71 = null;

        Label0Parser.sqText_return sqText73 = null;

        Label0Parser.ruleLabel_return ruleLabel75 = null;

        Label0Parser.ruleDefault_return ruleDefault76 = null;


        Object EQUALS69_tree=null;
        Object LBRACE70_tree=null;
        Object RBRACE72_tree=null;
        Object PLING74_tree=null;
        RewriteRuleSubtreeStream stream_ruleDefault=new RewriteRuleSubtreeStream(adaptor,"rule ruleDefault");
        RewriteRuleSubtreeStream stream_sqText=new RewriteRuleSubtreeStream(adaptor,"rule sqText");
        try {
            // Label0.g:116:4: ( wildcard | EQUALS | LBRACE regExpr RBRACE | sqText -> ^( ATOM sqText ) | PLING ruleLabel | ruleDefault -> ^( ATOM ruleDefault ) )
            int alt16=6;
            switch ( input.LA(1) ) {
            case QUERY:
                {
                alt16=1;
                }
                break;
            case EQUALS:
                {
                alt16=2;
                }
                break;
            case LBRACE:
                {
                alt16=3;
                }
                break;
            case SQUOTE:
                {
                alt16=4;
                }
                break;
            case PLING:
                {
                alt16=5;
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
            case IDENT:
            case LABEL:
            case NUMBER:
            case DOT:
            case BAR:
            case MINUS:
            case STAR:
            case PLUS:
            case LPAR:
            case RPAR:
            case LSQUARE:
            case HAT:
            case COMMA:
            case RSQUARE:
            case DQUOTE:
            case DOLLAR:
            case UNDER:
            case LETTER:
            case IDENTCHAR:
            case DIGIT:
            case 58:
                {
                alt16=6;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;
            }

            switch (alt16) {
                case 1 :
                    // Label0.g:116:6: wildcard
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_wildcard_in_ruleLabel735);
                    wildcard68=wildcard();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, wildcard68.getTree());

                    }
                    break;
                case 2 :
                    // Label0.g:117:6: EQUALS
                    {
                    root_0 = (Object)adaptor.nil();

                    EQUALS69=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_ruleLabel742); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQUALS69_tree = (Object)adaptor.create(EQUALS69);
                    adaptor.addChild(root_0, EQUALS69_tree);
                    }

                    }
                    break;
                case 3 :
                    // Label0.g:118:6: LBRACE regExpr RBRACE
                    {
                    root_0 = (Object)adaptor.nil();

                    LBRACE70=(Token)match(input,LBRACE,FOLLOW_LBRACE_in_ruleLabel749); if (state.failed) return retval;
                    pushFollow(FOLLOW_regExpr_in_ruleLabel752);
                    regExpr71=regExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, regExpr71.getTree());
                    RBRACE72=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_ruleLabel754); if (state.failed) return retval;

                    }
                    break;
                case 4 :
                    // Label0.g:119:6: sqText
                    {
                    pushFollow(FOLLOW_sqText_in_ruleLabel762);
                    sqText73=sqText();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_sqText.add(sqText73.getTree());


                    // AST REWRITE
                    // elements: sqText
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 119:13: -> ^( ATOM sqText )
                    {
                        // Label0.g:119:16: ^( ATOM sqText )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ATOM, "ATOM"), root_1);

                        adaptor.addChild(root_1, stream_sqText.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 5 :
                    // Label0.g:120:6: PLING ruleLabel
                    {
                    root_0 = (Object)adaptor.nil();

                    PLING74=(Token)match(input,PLING,FOLLOW_PLING_in_ruleLabel777); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    PLING74_tree = (Object)adaptor.create(PLING74);
                    root_0 = (Object)adaptor.becomeRoot(PLING74_tree, root_0);
                    }
                    pushFollow(FOLLOW_ruleLabel_in_ruleLabel780);
                    ruleLabel75=ruleLabel();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ruleLabel75.getTree());

                    }
                    break;
                case 6 :
                    // Label0.g:121:6: ruleDefault
                    {
                    pushFollow(FOLLOW_ruleDefault_in_ruleLabel787);
                    ruleDefault76=ruleDefault();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ruleDefault.add(ruleDefault76.getTree());


                    // AST REWRITE
                    // elements: ruleDefault
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 121:18: -> ^( ATOM ruleDefault )
                    {
                        // Label0.g:121:21: ^( ATOM ruleDefault )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ATOM, "ATOM"), root_1);

                        adaptor.addChild(root_1, stream_ruleDefault.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // $ANTLR end "ruleLabel"

    public static class ruleDefault_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ruleDefault"
    // Label0.g:124:1: ruleDefault : ~ ( EQUALS | QUERY | PLING | SQUOTE | LBRACE | RBRACE | BSLASH | COLON ) (~ ( SQUOTE | LBRACE | RBRACE | BSLASH | COLON ) )* ;
    public final Label0Parser.ruleDefault_return ruleDefault() throws RecognitionException {
        Label0Parser.ruleDefault_return retval = new Label0Parser.ruleDefault_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set77=null;
        Token set78=null;

        Object set77_tree=null;
        Object set78_tree=null;

        try {
            // Label0.g:125:4: (~ ( EQUALS | QUERY | PLING | SQUOTE | LBRACE | RBRACE | BSLASH | COLON ) (~ ( SQUOTE | LBRACE | RBRACE | BSLASH | COLON ) )* )
            // Label0.g:125:6: ~ ( EQUALS | QUERY | PLING | SQUOTE | LBRACE | RBRACE | BSLASH | COLON ) (~ ( SQUOTE | LBRACE | RBRACE | BSLASH | COLON ) )*
            {
            root_0 = (Object)adaptor.nil();

            set77=(Token)input.LT(1);
            if ( (input.LA(1)>=NEW && input.LA(1)<=CONSTRAINT)||input.LA(1)==IDENT||(input.LA(1)>=LABEL && input.LA(1)<=NUMBER)||(input.LA(1)>=DOT && input.LA(1)<=58) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set77));
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            // Label0.g:126:6: (~ ( SQUOTE | LBRACE | RBRACE | BSLASH | COLON ) )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( ((LA17_0>=NEW && LA17_0<=IDENT)||(LA17_0>=LABEL && LA17_0<=NUMBER)||(LA17_0>=PLING && LA17_0<=QUERY)||(LA17_0>=DOT && LA17_0<=58)) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // Label0.g:126:6: ~ ( SQUOTE | LBRACE | RBRACE | BSLASH | COLON )
            	    {
            	    set78=(Token)input.LT(1);
            	    if ( (input.LA(1)>=NEW && input.LA(1)<=IDENT)||(input.LA(1)>=LABEL && input.LA(1)<=NUMBER)||(input.LA(1)>=PLING && input.LA(1)<=QUERY)||(input.LA(1)>=DOT && input.LA(1)<=58) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set78));
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // $ANTLR end "ruleDefault"

    public static class nodeLabel_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "nodeLabel"
    // Label0.g:129:1: nodeLabel : ( TYPE COLON IDENT | FLAG COLON IDENT );
    public final Label0Parser.nodeLabel_return nodeLabel() throws RecognitionException {
        Label0Parser.nodeLabel_return retval = new Label0Parser.nodeLabel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TYPE79=null;
        Token COLON80=null;
        Token IDENT81=null;
        Token FLAG82=null;
        Token COLON83=null;
        Token IDENT84=null;

        Object TYPE79_tree=null;
        Object COLON80_tree=null;
        Object IDENT81_tree=null;
        Object FLAG82_tree=null;
        Object COLON83_tree=null;
        Object IDENT84_tree=null;

        try {
            // Label0.g:130:4: ( TYPE COLON IDENT | FLAG COLON IDENT )
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==TYPE) ) {
                alt18=1;
            }
            else if ( (LA18_0==FLAG) ) {
                alt18=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;
            }
            switch (alt18) {
                case 1 :
                    // Label0.g:130:6: TYPE COLON IDENT
                    {
                    root_0 = (Object)adaptor.nil();

                    TYPE79=(Token)match(input,TYPE,FOLLOW_TYPE_in_nodeLabel884); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TYPE79_tree = (Object)adaptor.create(TYPE79);
                    root_0 = (Object)adaptor.becomeRoot(TYPE79_tree, root_0);
                    }
                    COLON80=(Token)match(input,COLON,FOLLOW_COLON_in_nodeLabel887); if (state.failed) return retval;
                    IDENT81=(Token)match(input,IDENT,FOLLOW_IDENT_in_nodeLabel890); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IDENT81_tree = (Object)adaptor.create(IDENT81);
                    adaptor.addChild(root_0, IDENT81_tree);
                    }

                    }
                    break;
                case 2 :
                    // Label0.g:131:6: FLAG COLON IDENT
                    {
                    root_0 = (Object)adaptor.nil();

                    FLAG82=(Token)match(input,FLAG,FOLLOW_FLAG_in_nodeLabel897); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    FLAG82_tree = (Object)adaptor.create(FLAG82);
                    root_0 = (Object)adaptor.becomeRoot(FLAG82_tree, root_0);
                    }
                    COLON83=(Token)match(input,COLON,FOLLOW_COLON_in_nodeLabel900); if (state.failed) return retval;
                    IDENT84=(Token)match(input,IDENT,FOLLOW_IDENT_in_nodeLabel903); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IDENT84_tree = (Object)adaptor.create(IDENT84);
                    adaptor.addChild(root_0, IDENT84_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // $ANTLR end "nodeLabel"

    public static class rnumber_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rnumber"
    // Label0.g:133:1: rnumber : ( NUMBER ( DOT ( NUMBER )? )? | DOT NUMBER );
    public final Label0Parser.rnumber_return rnumber() throws RecognitionException {
        Label0Parser.rnumber_return retval = new Label0Parser.rnumber_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token NUMBER85=null;
        Token DOT86=null;
        Token NUMBER87=null;
        Token DOT88=null;
        Token NUMBER89=null;

        Object NUMBER85_tree=null;
        Object DOT86_tree=null;
        Object NUMBER87_tree=null;
        Object DOT88_tree=null;
        Object NUMBER89_tree=null;

        try {
            // Label0.g:134:4: ( NUMBER ( DOT ( NUMBER )? )? | DOT NUMBER )
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==NUMBER) ) {
                alt21=1;
            }
            else if ( (LA21_0==DOT) ) {
                alt21=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;
            }
            switch (alt21) {
                case 1 :
                    // Label0.g:134:6: NUMBER ( DOT ( NUMBER )? )?
                    {
                    root_0 = (Object)adaptor.nil();

                    NUMBER85=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_rnumber917); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    NUMBER85_tree = (Object)adaptor.create(NUMBER85);
                    adaptor.addChild(root_0, NUMBER85_tree);
                    }
                    // Label0.g:134:13: ( DOT ( NUMBER )? )?
                    int alt20=2;
                    int LA20_0 = input.LA(1);

                    if ( (LA20_0==DOT) ) {
                        alt20=1;
                    }
                    switch (alt20) {
                        case 1 :
                            // Label0.g:134:14: DOT ( NUMBER )?
                            {
                            DOT86=(Token)match(input,DOT,FOLLOW_DOT_in_rnumber920); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            DOT86_tree = (Object)adaptor.create(DOT86);
                            adaptor.addChild(root_0, DOT86_tree);
                            }
                            // Label0.g:134:18: ( NUMBER )?
                            int alt19=2;
                            int LA19_0 = input.LA(1);

                            if ( (LA19_0==NUMBER) ) {
                                alt19=1;
                            }
                            switch (alt19) {
                                case 1 :
                                    // Label0.g:134:18: NUMBER
                                    {
                                    NUMBER87=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_rnumber922); if (state.failed) return retval;
                                    if ( state.backtracking==0 ) {
                                    NUMBER87_tree = (Object)adaptor.create(NUMBER87);
                                    adaptor.addChild(root_0, NUMBER87_tree);
                                    }

                                    }
                                    break;

                            }


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // Label0.g:135:6: DOT NUMBER
                    {
                    root_0 = (Object)adaptor.nil();

                    DOT88=(Token)match(input,DOT,FOLLOW_DOT_in_rnumber932); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    DOT88_tree = (Object)adaptor.create(DOT88);
                    adaptor.addChild(root_0, DOT88_tree);
                    }
                    NUMBER89=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_rnumber934); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    NUMBER89_tree = (Object)adaptor.create(NUMBER89);
                    adaptor.addChild(root_0, NUMBER89_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // $ANTLR end "rnumber"

    public static class regExpr_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "regExpr"
    // Label0.g:138:1: regExpr : ( choice | PLING regExpr );
    public final Label0Parser.regExpr_return regExpr() throws RecognitionException {
        Label0Parser.regExpr_return retval = new Label0Parser.regExpr_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token PLING91=null;
        Label0Parser.choice_return choice90 = null;

        Label0Parser.regExpr_return regExpr92 = null;


        Object PLING91_tree=null;

        try {
            // Label0.g:139:4: ( choice | PLING regExpr )
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( ((LA22_0>=EQUALS && LA22_0<=IDENT)||(LA22_0>=LABEL && LA22_0<=NUMBER)||(LA22_0>=QUERY && LA22_0<=SQUOTE)||LA22_0==MINUS||LA22_0==LPAR) ) {
                alt22=1;
            }
            else if ( (LA22_0==PLING) ) {
                alt22=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 22, 0, input);

                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // Label0.g:139:6: choice
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_choice_in_regExpr949);
                    choice90=choice();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, choice90.getTree());

                    }
                    break;
                case 2 :
                    // Label0.g:139:15: PLING regExpr
                    {
                    root_0 = (Object)adaptor.nil();

                    PLING91=(Token)match(input,PLING,FOLLOW_PLING_in_regExpr953); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    PLING91_tree = (Object)adaptor.create(PLING91);
                    root_0 = (Object)adaptor.becomeRoot(PLING91_tree, root_0);
                    }
                    pushFollow(FOLLOW_regExpr_in_regExpr956);
                    regExpr92=regExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, regExpr92.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // $ANTLR end "regExpr"

    public static class choice_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "choice"
    // Label0.g:141:1: choice : sequence ( BAR choice )? ;
    public final Label0Parser.choice_return choice() throws RecognitionException {
        Label0Parser.choice_return retval = new Label0Parser.choice_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BAR94=null;
        Label0Parser.sequence_return sequence93 = null;

        Label0Parser.choice_return choice95 = null;


        Object BAR94_tree=null;

        try {
            // Label0.g:142:4: ( sequence ( BAR choice )? )
            // Label0.g:142:6: sequence ( BAR choice )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_sequence_in_choice967);
            sequence93=sequence();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, sequence93.getTree());
            // Label0.g:142:15: ( BAR choice )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==BAR) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // Label0.g:142:16: BAR choice
                    {
                    BAR94=(Token)match(input,BAR,FOLLOW_BAR_in_choice970); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    BAR94_tree = (Object)adaptor.create(BAR94);
                    root_0 = (Object)adaptor.becomeRoot(BAR94_tree, root_0);
                    }
                    pushFollow(FOLLOW_choice_in_choice973);
                    choice95=choice();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, choice95.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // $ANTLR end "choice"

    public static class sequence_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "sequence"
    // Label0.g:144:1: sequence : unary ( DOT sequence )? ;
    public final Label0Parser.sequence_return sequence() throws RecognitionException {
        Label0Parser.sequence_return retval = new Label0Parser.sequence_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DOT97=null;
        Label0Parser.unary_return unary96 = null;

        Label0Parser.sequence_return sequence98 = null;


        Object DOT97_tree=null;

        try {
            // Label0.g:145:4: ( unary ( DOT sequence )? )
            // Label0.g:145:6: unary ( DOT sequence )?
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_unary_in_sequence987);
            unary96=unary();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, unary96.getTree());
            // Label0.g:145:12: ( DOT sequence )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==DOT) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // Label0.g:145:13: DOT sequence
                    {
                    DOT97=(Token)match(input,DOT,FOLLOW_DOT_in_sequence990); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    DOT97_tree = (Object)adaptor.create(DOT97);
                    root_0 = (Object)adaptor.becomeRoot(DOT97_tree, root_0);
                    }
                    pushFollow(FOLLOW_sequence_in_sequence993);
                    sequence98=sequence();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, sequence98.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // $ANTLR end "sequence"

    public static class unary_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "unary"
    // Label0.g:147:1: unary : ( MINUS unary | atom ( STAR | PLUS )? | EQUALS | LPAR regExpr RPAR ( STAR | PLUS )? | wildcard ( STAR | PLUS )? );
    public final Label0Parser.unary_return unary() throws RecognitionException {
        Label0Parser.unary_return retval = new Label0Parser.unary_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token MINUS99=null;
        Token STAR102=null;
        Token PLUS103=null;
        Token EQUALS104=null;
        Token LPAR105=null;
        Token RPAR107=null;
        Token STAR108=null;
        Token PLUS109=null;
        Token STAR111=null;
        Token PLUS112=null;
        Label0Parser.unary_return unary100 = null;

        Label0Parser.atom_return atom101 = null;

        Label0Parser.regExpr_return regExpr106 = null;

        Label0Parser.wildcard_return wildcard110 = null;


        Object MINUS99_tree=null;
        Object STAR102_tree=null;
        Object PLUS103_tree=null;
        Object EQUALS104_tree=null;
        Object LPAR105_tree=null;
        Object RPAR107_tree=null;
        Object STAR108_tree=null;
        Object PLUS109_tree=null;
        Object STAR111_tree=null;
        Object PLUS112_tree=null;

        try {
            // Label0.g:148:4: ( MINUS unary | atom ( STAR | PLUS )? | EQUALS | LPAR regExpr RPAR ( STAR | PLUS )? | wildcard ( STAR | PLUS )? )
            int alt28=5;
            switch ( input.LA(1) ) {
            case MINUS:
                {
                alt28=1;
                }
                break;
            case IDENT:
            case LABEL:
            case NUMBER:
            case SQUOTE:
                {
                alt28=2;
                }
                break;
            case EQUALS:
                {
                alt28=3;
                }
                break;
            case LPAR:
                {
                alt28=4;
                }
                break;
            case QUERY:
                {
                alt28=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 28, 0, input);

                throw nvae;
            }

            switch (alt28) {
                case 1 :
                    // Label0.g:148:6: MINUS unary
                    {
                    root_0 = (Object)adaptor.nil();

                    MINUS99=(Token)match(input,MINUS,FOLLOW_MINUS_in_unary1007); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    MINUS99_tree = (Object)adaptor.create(MINUS99);
                    root_0 = (Object)adaptor.becomeRoot(MINUS99_tree, root_0);
                    }
                    pushFollow(FOLLOW_unary_in_unary1010);
                    unary100=unary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unary100.getTree());

                    }
                    break;
                case 2 :
                    // Label0.g:149:6: atom ( STAR | PLUS )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_atom_in_unary1017);
                    atom101=atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, atom101.getTree());
                    // Label0.g:149:11: ( STAR | PLUS )?
                    int alt25=3;
                    int LA25_0 = input.LA(1);

                    if ( (LA25_0==STAR) ) {
                        alt25=1;
                    }
                    else if ( (LA25_0==PLUS) ) {
                        alt25=2;
                    }
                    switch (alt25) {
                        case 1 :
                            // Label0.g:149:12: STAR
                            {
                            STAR102=(Token)match(input,STAR,FOLLOW_STAR_in_unary1020); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            STAR102_tree = (Object)adaptor.create(STAR102);
                            root_0 = (Object)adaptor.becomeRoot(STAR102_tree, root_0);
                            }

                            }
                            break;
                        case 2 :
                            // Label0.g:149:20: PLUS
                            {
                            PLUS103=(Token)match(input,PLUS,FOLLOW_PLUS_in_unary1025); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            PLUS103_tree = (Object)adaptor.create(PLUS103);
                            root_0 = (Object)adaptor.becomeRoot(PLUS103_tree, root_0);
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // Label0.g:150:6: EQUALS
                    {
                    root_0 = (Object)adaptor.nil();

                    EQUALS104=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_unary1035); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    EQUALS104_tree = (Object)adaptor.create(EQUALS104);
                    adaptor.addChild(root_0, EQUALS104_tree);
                    }

                    }
                    break;
                case 4 :
                    // Label0.g:151:6: LPAR regExpr RPAR ( STAR | PLUS )?
                    {
                    root_0 = (Object)adaptor.nil();

                    LPAR105=(Token)match(input,LPAR,FOLLOW_LPAR_in_unary1042); if (state.failed) return retval;
                    pushFollow(FOLLOW_regExpr_in_unary1045);
                    regExpr106=regExpr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, regExpr106.getTree());
                    RPAR107=(Token)match(input,RPAR,FOLLOW_RPAR_in_unary1047); if (state.failed) return retval;
                    // Label0.g:151:26: ( STAR | PLUS )?
                    int alt26=3;
                    int LA26_0 = input.LA(1);

                    if ( (LA26_0==STAR) ) {
                        alt26=1;
                    }
                    else if ( (LA26_0==PLUS) ) {
                        alt26=2;
                    }
                    switch (alt26) {
                        case 1 :
                            // Label0.g:151:27: STAR
                            {
                            STAR108=(Token)match(input,STAR,FOLLOW_STAR_in_unary1051); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            STAR108_tree = (Object)adaptor.create(STAR108);
                            root_0 = (Object)adaptor.becomeRoot(STAR108_tree, root_0);
                            }

                            }
                            break;
                        case 2 :
                            // Label0.g:151:35: PLUS
                            {
                            PLUS109=(Token)match(input,PLUS,FOLLOW_PLUS_in_unary1056); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            PLUS109_tree = (Object)adaptor.create(PLUS109);
                            root_0 = (Object)adaptor.becomeRoot(PLUS109_tree, root_0);
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 5 :
                    // Label0.g:152:6: wildcard ( STAR | PLUS )?
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_wildcard_in_unary1066);
                    wildcard110=wildcard();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, wildcard110.getTree());
                    // Label0.g:152:15: ( STAR | PLUS )?
                    int alt27=3;
                    int LA27_0 = input.LA(1);

                    if ( (LA27_0==STAR) ) {
                        alt27=1;
                    }
                    else if ( (LA27_0==PLUS) ) {
                        alt27=2;
                    }
                    switch (alt27) {
                        case 1 :
                            // Label0.g:152:16: STAR
                            {
                            STAR111=(Token)match(input,STAR,FOLLOW_STAR_in_unary1069); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            STAR111_tree = (Object)adaptor.create(STAR111);
                            root_0 = (Object)adaptor.becomeRoot(STAR111_tree, root_0);
                            }

                            }
                            break;
                        case 2 :
                            // Label0.g:152:24: PLUS
                            {
                            PLUS112=(Token)match(input,PLUS,FOLLOW_PLUS_in_unary1074); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            PLUS112_tree = (Object)adaptor.create(PLUS112);
                            root_0 = (Object)adaptor.becomeRoot(PLUS112_tree, root_0);
                            }

                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // $ANTLR end "unary"

    public static class atom_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "atom"
    // Label0.g:155:1: atom : ( sqText -> ^( ATOM sqText ) | atomLabel -> ^( ATOM atomLabel ) );
    public final Label0Parser.atom_return atom() throws RecognitionException {
        Label0Parser.atom_return retval = new Label0Parser.atom_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Label0Parser.sqText_return sqText113 = null;

        Label0Parser.atomLabel_return atomLabel114 = null;


        RewriteRuleSubtreeStream stream_sqText=new RewriteRuleSubtreeStream(adaptor,"rule sqText");
        RewriteRuleSubtreeStream stream_atomLabel=new RewriteRuleSubtreeStream(adaptor,"rule atomLabel");
        try {
            // Label0.g:156:4: ( sqText -> ^( ATOM sqText ) | atomLabel -> ^( ATOM atomLabel ) )
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==SQUOTE) ) {
                alt29=1;
            }
            else if ( (LA29_0==IDENT||(LA29_0>=LABEL && LA29_0<=NUMBER)) ) {
                alt29=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 29, 0, input);

                throw nvae;
            }
            switch (alt29) {
                case 1 :
                    // Label0.g:156:6: sqText
                    {
                    pushFollow(FOLLOW_sqText_in_atom1092);
                    sqText113=sqText();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_sqText.add(sqText113.getTree());


                    // AST REWRITE
                    // elements: sqText
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 156:13: -> ^( ATOM sqText )
                    {
                        // Label0.g:156:16: ^( ATOM sqText )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ATOM, "ATOM"), root_1);

                        adaptor.addChild(root_1, stream_sqText.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // Label0.g:157:6: atomLabel
                    {
                    pushFollow(FOLLOW_atomLabel_in_atom1107);
                    atomLabel114=atomLabel();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_atomLabel.add(atomLabel114.getTree());


                    // AST REWRITE
                    // elements: atomLabel
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 157:16: -> ^( ATOM atomLabel )
                    {
                        // Label0.g:157:19: ^( ATOM atomLabel )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ATOM, "ATOM"), root_1);

                        adaptor.addChild(root_1, stream_atomLabel.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // $ANTLR end "atom"

    public static class atomLabel_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "atomLabel"
    // Label0.g:160:1: atomLabel : ( NUMBER | IDENT | LABEL );
    public final Label0Parser.atomLabel_return atomLabel() throws RecognitionException {
        Label0Parser.atomLabel_return retval = new Label0Parser.atomLabel_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set115=null;

        Object set115_tree=null;

        try {
            // Label0.g:161:4: ( NUMBER | IDENT | LABEL )
            // Label0.g:
            {
            root_0 = (Object)adaptor.nil();

            set115=(Token)input.LT(1);
            if ( input.LA(1)==IDENT||(input.LA(1)>=LABEL && input.LA(1)<=NUMBER) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set115));
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // $ANTLR end "atomLabel"

    public static class wildcard_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "wildcard"
    // Label0.g:164:1: wildcard : QUERY ( IDENT )? ( LSQUARE ( HAT )? atom ( COMMA atom )* RSQUARE )? ;
    public final Label0Parser.wildcard_return wildcard() throws RecognitionException {
        Label0Parser.wildcard_return retval = new Label0Parser.wildcard_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token QUERY116=null;
        Token IDENT117=null;
        Token LSQUARE118=null;
        Token HAT119=null;
        Token COMMA121=null;
        Token RSQUARE123=null;
        Label0Parser.atom_return atom120 = null;

        Label0Parser.atom_return atom122 = null;


        Object QUERY116_tree=null;
        Object IDENT117_tree=null;
        Object LSQUARE118_tree=null;
        Object HAT119_tree=null;
        Object COMMA121_tree=null;
        Object RSQUARE123_tree=null;

        try {
            // Label0.g:165:4: ( QUERY ( IDENT )? ( LSQUARE ( HAT )? atom ( COMMA atom )* RSQUARE )? )
            // Label0.g:165:6: QUERY ( IDENT )? ( LSQUARE ( HAT )? atom ( COMMA atom )* RSQUARE )?
            {
            root_0 = (Object)adaptor.nil();

            QUERY116=(Token)match(input,QUERY,FOLLOW_QUERY_in_wildcard1156); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            QUERY116_tree = (Object)adaptor.create(QUERY116);
            root_0 = (Object)adaptor.becomeRoot(QUERY116_tree, root_0);
            }
            // Label0.g:165:13: ( IDENT )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==IDENT) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // Label0.g:165:13: IDENT
                    {
                    IDENT117=(Token)match(input,IDENT,FOLLOW_IDENT_in_wildcard1159); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IDENT117_tree = (Object)adaptor.create(IDENT117);
                    adaptor.addChild(root_0, IDENT117_tree);
                    }

                    }
                    break;

            }

            // Label0.g:165:20: ( LSQUARE ( HAT )? atom ( COMMA atom )* RSQUARE )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==LSQUARE) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // Label0.g:165:21: LSQUARE ( HAT )? atom ( COMMA atom )* RSQUARE
                    {
                    LSQUARE118=(Token)match(input,LSQUARE,FOLLOW_LSQUARE_in_wildcard1163); if (state.failed) return retval;
                    // Label0.g:165:30: ( HAT )?
                    int alt31=2;
                    int LA31_0 = input.LA(1);

                    if ( (LA31_0==HAT) ) {
                        alt31=1;
                    }
                    switch (alt31) {
                        case 1 :
                            // Label0.g:165:30: HAT
                            {
                            HAT119=(Token)match(input,HAT,FOLLOW_HAT_in_wildcard1166); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            HAT119_tree = (Object)adaptor.create(HAT119);
                            adaptor.addChild(root_0, HAT119_tree);
                            }

                            }
                            break;

                    }

                    pushFollow(FOLLOW_atom_in_wildcard1169);
                    atom120=atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, atom120.getTree());
                    // Label0.g:165:40: ( COMMA atom )*
                    loop32:
                    do {
                        int alt32=2;
                        int LA32_0 = input.LA(1);

                        if ( (LA32_0==COMMA) ) {
                            alt32=1;
                        }


                        switch (alt32) {
                    	case 1 :
                    	    // Label0.g:165:41: COMMA atom
                    	    {
                    	    COMMA121=(Token)match(input,COMMA,FOLLOW_COMMA_in_wildcard1172); if (state.failed) return retval;
                    	    pushFollow(FOLLOW_atom_in_wildcard1175);
                    	    atom122=atom();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, atom122.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop32;
                        }
                    } while (true);

                    RSQUARE123=(Token)match(input,RSQUARE,FOLLOW_RSQUARE_in_wildcard1179); if (state.failed) return retval;

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // $ANTLR end "wildcard"

    public static class sqText_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "sqText"
    // Label0.g:168:1: sqText : SQUOTE (~ ( SQUOTE | BSLASH ) | sqTextSpecial )* SQUOTE ;
    public final Label0Parser.sqText_return sqText() throws RecognitionException {
        Label0Parser.sqText_return retval = new Label0Parser.sqText_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SQUOTE124=null;
        Token set125=null;
        Token SQUOTE127=null;
        Label0Parser.sqTextSpecial_return sqTextSpecial126 = null;


        Object SQUOTE124_tree=null;
        Object set125_tree=null;
        Object SQUOTE127_tree=null;

        try {
            // Label0.g:169:4: ( SQUOTE (~ ( SQUOTE | BSLASH ) | sqTextSpecial )* SQUOTE )
            // Label0.g:169:6: SQUOTE (~ ( SQUOTE | BSLASH ) | sqTextSpecial )* SQUOTE
            {
            root_0 = (Object)adaptor.nil();

            SQUOTE124=(Token)match(input,SQUOTE,FOLLOW_SQUOTE_in_sqText1197); if (state.failed) return retval;
            // Label0.g:169:14: (~ ( SQUOTE | BSLASH ) | sqTextSpecial )*
            loop34:
            do {
                int alt34=3;
                int LA34_0 = input.LA(1);

                if ( ((LA34_0>=NEW && LA34_0<=QUERY)||(LA34_0>=DOT && LA34_0<=58)) ) {
                    alt34=1;
                }
                else if ( (LA34_0==BSLASH) ) {
                    alt34=2;
                }


                switch (alt34) {
            	case 1 :
            	    // Label0.g:169:15: ~ ( SQUOTE | BSLASH )
            	    {
            	    set125=(Token)input.LT(1);
            	    if ( (input.LA(1)>=NEW && input.LA(1)<=QUERY)||(input.LA(1)>=DOT && input.LA(1)<=58) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set125));
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    }
            	    break;
            	case 2 :
            	    // Label0.g:169:34: sqTextSpecial
            	    {
            	    pushFollow(FOLLOW_sqTextSpecial_in_sqText1210);
            	    sqTextSpecial126=sqTextSpecial();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, sqTextSpecial126.getTree());

            	    }
            	    break;

            	default :
            	    break loop34;
                }
            } while (true);

            SQUOTE127=(Token)match(input,SQUOTE,FOLLOW_SQUOTE_in_sqText1214); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // $ANTLR end "sqText"

    public static class sqTextSpecial_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "sqTextSpecial"
    // Label0.g:171:1: sqTextSpecial : BSLASH ( BSLASH | SQUOTE ) ;
    public final Label0Parser.sqTextSpecial_return sqTextSpecial() throws RecognitionException {
        Label0Parser.sqTextSpecial_return retval = new Label0Parser.sqTextSpecial_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BSLASH128=null;
        Token set129=null;

        Object BSLASH128_tree=null;
        Object set129_tree=null;

        try {
            // Label0.g:172:4: ( BSLASH ( BSLASH | SQUOTE ) )
            // Label0.g:172:6: BSLASH ( BSLASH | SQUOTE )
            {
            root_0 = (Object)adaptor.nil();

            BSLASH128=(Token)match(input,BSLASH,FOLLOW_BSLASH_in_sqTextSpecial1226); if (state.failed) return retval;
            set129=(Token)input.LT(1);
            if ( (input.LA(1)>=SQUOTE && input.LA(1)<=BSLASH) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set129));
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // $ANTLR end "sqTextSpecial"

    public static class dqText_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "dqText"
    // Label0.g:175:1: dqText : DQUOTE (~ ( DQUOTE | BSLASH ) | dqTextSpecial )* DQUOTE ;
    public final Label0Parser.dqText_return dqText() throws RecognitionException {
        Label0Parser.dqText_return retval = new Label0Parser.dqText_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DQUOTE130=null;
        Token set131=null;
        Token DQUOTE133=null;
        Label0Parser.dqTextSpecial_return dqTextSpecial132 = null;


        Object DQUOTE130_tree=null;
        Object set131_tree=null;
        Object DQUOTE133_tree=null;

        try {
            // Label0.g:176:4: ( DQUOTE (~ ( DQUOTE | BSLASH ) | dqTextSpecial )* DQUOTE )
            // Label0.g:176:6: DQUOTE (~ ( DQUOTE | BSLASH ) | dqTextSpecial )* DQUOTE
            {
            root_0 = (Object)adaptor.nil();

            DQUOTE130=(Token)match(input,DQUOTE,FOLLOW_DQUOTE_in_dqText1248); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            DQUOTE130_tree = (Object)adaptor.create(DQUOTE130);
            root_0 = (Object)adaptor.becomeRoot(DQUOTE130_tree, root_0);
            }
            // Label0.g:176:14: (~ ( DQUOTE | BSLASH ) | dqTextSpecial )*
            loop35:
            do {
                int alt35=3;
                int LA35_0 = input.LA(1);

                if ( ((LA35_0>=NEW && LA35_0<=SQUOTE)||(LA35_0>=DOT && LA35_0<=RSQUARE)||(LA35_0>=DOLLAR && LA35_0<=58)) ) {
                    alt35=1;
                }
                else if ( (LA35_0==BSLASH) ) {
                    alt35=2;
                }


                switch (alt35) {
            	case 1 :
            	    // Label0.g:176:15: ~ ( DQUOTE | BSLASH )
            	    {
            	    set131=(Token)input.LT(1);
            	    if ( (input.LA(1)>=NEW && input.LA(1)<=SQUOTE)||(input.LA(1)>=DOT && input.LA(1)<=RSQUARE)||(input.LA(1)>=DOLLAR && input.LA(1)<=58) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set131));
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    }
            	    break;
            	case 2 :
            	    // Label0.g:176:34: dqTextSpecial
            	    {
            	    pushFollow(FOLLOW_dqTextSpecial_in_dqText1261);
            	    dqTextSpecial132=dqTextSpecial();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, dqTextSpecial132.getTree());

            	    }
            	    break;

            	default :
            	    break loop35;
                }
            } while (true);

            DQUOTE133=(Token)match(input,DQUOTE,FOLLOW_DQUOTE_in_dqText1265); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // $ANTLR end "dqText"

    public static class dqTextSpecial_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "dqTextSpecial"
    // Label0.g:178:1: dqTextSpecial : BSLASH ( BSLASH | DQUOTE ) ;
    public final Label0Parser.dqTextSpecial_return dqTextSpecial() throws RecognitionException {
        Label0Parser.dqTextSpecial_return retval = new Label0Parser.dqTextSpecial_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BSLASH134=null;
        Token set135=null;

        Object BSLASH134_tree=null;
        Object set135_tree=null;

        try {
            // Label0.g:179:4: ( BSLASH ( BSLASH | DQUOTE ) )
            // Label0.g:179:6: BSLASH ( BSLASH | DQUOTE )
            {
            root_0 = (Object)adaptor.nil();

            BSLASH134=(Token)match(input,BSLASH,FOLLOW_BSLASH_in_dqTextSpecial1277); if (state.failed) return retval;
            set135=(Token)input.LT(1);
            if ( input.LA(1)==BSLASH||input.LA(1)==DQUOTE ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set135));
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
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
    // $ANTLR end "dqTextSpecial"

    // $ANTLR start synpred1_Label0
    public final void synpred1_Label0_fragment() throws RecognitionException {   
        // Label0.g:99:6: ( graphDefault EOF )
        // Label0.g:99:7: graphDefault EOF
        {
        pushFollow(FOLLOW_graphDefault_in_synpred1_Label0629);
        graphDefault();

        state._fsp--;
        if (state.failed) return ;
        match(input,EOF,FOLLOW_EOF_in_synpred1_Label0631); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_Label0

    // $ANTLR start synpred2_Label0
    public final void synpred2_Label0_fragment() throws RecognitionException {   
        // Label0.g:100:6: ( ruleLabel EOF )
        // Label0.g:100:7: ruleLabel EOF
        {
        pushFollow(FOLLOW_ruleLabel_in_synpred2_Label0648);
        ruleLabel();

        state._fsp--;
        if (state.failed) return ;
        match(input,EOF,FOLLOW_EOF_in_synpred2_Label0650); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_Label0

    // Delegated rules

    public final boolean synpred2_Label0() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_Label0_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred1_Label0() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_Label0_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA1 dfa1 = new DFA1(this);
    protected DFA6 dfa6 = new DFA6(this);
    protected DFA13 dfa13 = new DFA13(this);
    static final String DFA1_eotS =
        "\31\uffff";
    static final String DFA1_eofS =
        "\1\23\6\uffff\1\26\1\uffff\11\26\5\uffff\2\26";
    static final String DFA1_minS =
        "\1\4\6\uffff\1\4\1\uffff\11\4\5\uffff\2\4";
    static final String DFA1_maxS =
        "\1\72\6\uffff\1\72\1\uffff\11\72\5\uffff\2\72";
    static final String DFA1_acceptS =
        "\1\uffff\6\1\1\uffff\1\1\11\uffff\1\1\1\3\1\2\2\1\2\uffff";
    static final String DFA1_specialS =
        "\1\14\6\uffff\1\7\1\uffff\1\13\1\4\1\1\1\0\1\12\1\6\1\10\1\11\1"+
        "\3\5\uffff\1\5\1\2}>";
    static final String[] DFA1_transitionS = {
            "\5\1\1\7\3\1\1\12\1\13\1\14\1\15\1\16\1\17\1\20\1\21\1\11\3"+
            "\1\5\22\1\3\1\22\1\1\2\22\1\4\1\10\1\6\1\2\1\5\1\10\22\22",
            "",
            "",
            "",
            "",
            "",
            "",
            "\34\25\1\24\2\25\2\10\2\25\2\10\22\25",
            "",
            "\32\25\1\27\1\25\1\24\2\25\2\10\2\25\2\10\22\25",
            "\34\25\1\24\2\25\2\10\2\25\2\10\22\25",
            "\34\25\1\24\2\25\2\10\2\25\2\10\22\25",
            "\34\25\1\24\2\25\2\10\2\25\2\10\22\25",
            "\34\25\1\24\2\25\2\10\2\25\2\10\22\25",
            "\34\25\1\24\2\25\2\10\2\25\2\10\22\25",
            "\34\25\1\24\2\25\2\10\2\25\2\10\22\25",
            "\34\25\1\24\2\25\2\10\2\25\2\10\22\25",
            "\34\25\1\24\2\25\2\10\2\25\2\10\22\25",
            "",
            "",
            "",
            "",
            "",
            "\34\25\1\uffff\1\30\1\25\2\10\2\25\2\10\22\25",
            "\34\25\1\24\2\25\2\10\2\25\2\10\22\25"
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
            return "71:6: ( prefixedLabel | specialLabel )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA1_12 = input.LA(1);

                         
                        int index1_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_12==COLON) ) {s = 20;}

                        else if ( ((LA1_12>=NEW && LA1_12<=IDENT)||(LA1_12>=LABEL && LA1_12<=NUMBER)||(LA1_12>=PLING && LA1_12<=QUERY)||(LA1_12>=DOT && LA1_12<=58)) && ((( !isGraph )||( isGraph )))) {s = 21;}

                        else if ( (LA1_12==EOF) && ((( !isGraph )||( isGraph )))) {s = 22;}

                        else if ( ((LA1_12>=LBRACE && LA1_12<=RBRACE)||(LA1_12>=SQUOTE && LA1_12<=BSLASH)) && (( isGraph ))) {s = 8;}

                         
                        input.seek(index1_12);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA1_11 = input.LA(1);

                         
                        int index1_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_11==COLON) ) {s = 20;}

                        else if ( ((LA1_11>=NEW && LA1_11<=IDENT)||(LA1_11>=LABEL && LA1_11<=NUMBER)||(LA1_11>=PLING && LA1_11<=QUERY)||(LA1_11>=DOT && LA1_11<=58)) && ((( !isGraph )||( isGraph )))) {s = 21;}

                        else if ( (LA1_11==EOF) && ((( !isGraph )||( isGraph )))) {s = 22;}

                        else if ( ((LA1_11>=LBRACE && LA1_11<=RBRACE)||(LA1_11>=SQUOTE && LA1_11<=BSLASH)) && (( isGraph ))) {s = 8;}

                         
                        input.seek(index1_11);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA1_24 = input.LA(1);

                         
                        int index1_24 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_24==EOF) && ((( !isGraph )||( isGraph )))) {s = 22;}

                        else if ( ((LA1_24>=NEW && LA1_24<=IDENT)||(LA1_24>=LABEL && LA1_24<=NUMBER)||(LA1_24>=PLING && LA1_24<=QUERY)||(LA1_24>=DOT && LA1_24<=58)) && ((( !isGraph )||( isGraph )))) {s = 21;}

                        else if ( (LA1_24==COLON) ) {s = 20;}

                        else if ( ((LA1_24>=LBRACE && LA1_24<=RBRACE)||(LA1_24>=SQUOTE && LA1_24<=BSLASH)) && (( isGraph ))) {s = 8;}

                         
                        input.seek(index1_24);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA1_17 = input.LA(1);

                         
                        int index1_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_17==COLON) ) {s = 20;}

                        else if ( ((LA1_17>=NEW && LA1_17<=IDENT)||(LA1_17>=LABEL && LA1_17<=NUMBER)||(LA1_17>=PLING && LA1_17<=QUERY)||(LA1_17>=DOT && LA1_17<=58)) && ((( !isGraph )||( isGraph )))) {s = 21;}

                        else if ( (LA1_17==EOF) && ((( !isGraph )||( isGraph )))) {s = 22;}

                        else if ( ((LA1_17>=LBRACE && LA1_17<=RBRACE)||(LA1_17>=SQUOTE && LA1_17<=BSLASH)) && (( isGraph ))) {s = 8;}

                         
                        input.seek(index1_17);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA1_10 = input.LA(1);

                         
                        int index1_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_10==COLON) ) {s = 20;}

                        else if ( ((LA1_10>=NEW && LA1_10<=IDENT)||(LA1_10>=LABEL && LA1_10<=NUMBER)||(LA1_10>=PLING && LA1_10<=QUERY)||(LA1_10>=DOT && LA1_10<=58)) && ((( !isGraph )||( isGraph )))) {s = 21;}

                        else if ( (LA1_10==EOF) && ((( !isGraph )||( isGraph )))) {s = 22;}

                        else if ( ((LA1_10>=LBRACE && LA1_10<=RBRACE)||(LA1_10>=SQUOTE && LA1_10<=BSLASH)) && (( isGraph ))) {s = 8;}

                         
                        input.seek(index1_10);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA1_23 = input.LA(1);

                         
                        int index1_23 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_23==LABEL) ) {s = 24;}

                        else if ( (LA1_23==EOF) && ((( !isGraph )||( isGraph )))) {s = 22;}

                        else if ( ((LA1_23>=NEW && LA1_23<=IDENT)||LA1_23==NUMBER||(LA1_23>=PLING && LA1_23<=QUERY)||(LA1_23>=DOT && LA1_23<=58)) && ((( !isGraph )||( isGraph )))) {s = 21;}

                        else if ( ((LA1_23>=LBRACE && LA1_23<=RBRACE)||(LA1_23>=SQUOTE && LA1_23<=BSLASH)) && (( isGraph ))) {s = 8;}

                         
                        input.seek(index1_23);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA1_14 = input.LA(1);

                         
                        int index1_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_14==COLON) ) {s = 20;}

                        else if ( ((LA1_14>=NEW && LA1_14<=IDENT)||(LA1_14>=LABEL && LA1_14<=NUMBER)||(LA1_14>=PLING && LA1_14<=QUERY)||(LA1_14>=DOT && LA1_14<=58)) && ((( !isGraph )||( isGraph )))) {s = 21;}

                        else if ( (LA1_14==EOF) && ((( !isGraph )||( isGraph )))) {s = 22;}

                        else if ( ((LA1_14>=LBRACE && LA1_14<=RBRACE)||(LA1_14>=SQUOTE && LA1_14<=BSLASH)) && (( isGraph ))) {s = 8;}

                         
                        input.seek(index1_14);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA1_7 = input.LA(1);

                         
                        int index1_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_7==COLON) ) {s = 20;}

                        else if ( ((LA1_7>=NEW && LA1_7<=IDENT)||(LA1_7>=LABEL && LA1_7<=NUMBER)||(LA1_7>=PLING && LA1_7<=QUERY)||(LA1_7>=DOT && LA1_7<=58)) && ((( !isGraph )||( isGraph )))) {s = 21;}

                        else if ( (LA1_7==EOF) && ((( !isGraph )||( isGraph )))) {s = 22;}

                        else if ( ((LA1_7>=LBRACE && LA1_7<=RBRACE)||(LA1_7>=SQUOTE && LA1_7<=BSLASH)) && (( isGraph ))) {s = 8;}

                         
                        input.seek(index1_7);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA1_15 = input.LA(1);

                         
                        int index1_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_15==COLON) ) {s = 20;}

                        else if ( ((LA1_15>=NEW && LA1_15<=IDENT)||(LA1_15>=LABEL && LA1_15<=NUMBER)||(LA1_15>=PLING && LA1_15<=QUERY)||(LA1_15>=DOT && LA1_15<=58)) && ((( !isGraph )||( isGraph )))) {s = 21;}

                        else if ( (LA1_15==EOF) && ((( !isGraph )||( isGraph )))) {s = 22;}

                        else if ( ((LA1_15>=LBRACE && LA1_15<=RBRACE)||(LA1_15>=SQUOTE && LA1_15<=BSLASH)) && (( isGraph ))) {s = 8;}

                         
                        input.seek(index1_15);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA1_16 = input.LA(1);

                         
                        int index1_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_16==COLON) ) {s = 20;}

                        else if ( ((LA1_16>=NEW && LA1_16<=IDENT)||(LA1_16>=LABEL && LA1_16<=NUMBER)||(LA1_16>=PLING && LA1_16<=QUERY)||(LA1_16>=DOT && LA1_16<=58)) && ((( !isGraph )||( isGraph )))) {s = 21;}

                        else if ( (LA1_16==EOF) && ((( !isGraph )||( isGraph )))) {s = 22;}

                        else if ( ((LA1_16>=LBRACE && LA1_16<=RBRACE)||(LA1_16>=SQUOTE && LA1_16<=BSLASH)) && (( isGraph ))) {s = 8;}

                         
                        input.seek(index1_16);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA1_13 = input.LA(1);

                         
                        int index1_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_13==COLON) ) {s = 20;}

                        else if ( ((LA1_13>=NEW && LA1_13<=IDENT)||(LA1_13>=LABEL && LA1_13<=NUMBER)||(LA1_13>=PLING && LA1_13<=QUERY)||(LA1_13>=DOT && LA1_13<=58)) && ((( !isGraph )||( isGraph )))) {s = 21;}

                        else if ( (LA1_13==EOF) && ((( !isGraph )||( isGraph )))) {s = 22;}

                        else if ( ((LA1_13>=LBRACE && LA1_13<=RBRACE)||(LA1_13>=SQUOTE && LA1_13<=BSLASH)) && (( isGraph ))) {s = 8;}

                         
                        input.seek(index1_13);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA1_9 = input.LA(1);

                         
                        int index1_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA1_9==EQUALS) ) {s = 23;}

                        else if ( (LA1_9==EOF) && ((( !isGraph )||( isGraph )))) {s = 22;}

                        else if ( ((LA1_9>=NEW && LA1_9<=CONSTRAINT)||LA1_9==IDENT||(LA1_9>=LABEL && LA1_9<=NUMBER)||(LA1_9>=PLING && LA1_9<=QUERY)||(LA1_9>=DOT && LA1_9<=58)) && ((( !isGraph )||( isGraph )))) {s = 21;}

                        else if ( (LA1_9==COLON) ) {s = 20;}

                        else if ( ((LA1_9>=LBRACE && LA1_9<=RBRACE)||(LA1_9>=SQUOTE && LA1_9<=BSLASH)) && (( isGraph ))) {s = 8;}

                         
                        input.seek(index1_9);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA1_0 = input.LA(1);

                         
                        int index1_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA1_0>=NEW && LA1_0<=CNEW)||(LA1_0>=FORALL && LA1_0<=EXISTS)||(LA1_0>=TYPE && LA1_0<=PATH)||LA1_0==COLON) ) {s = 1;}

                        else if ( (LA1_0==QUERY) && ((( !isGraph )||( isGraph )))) {s = 2;}

                        else if ( (LA1_0==EQUALS) && ((( !isGraph )||( isGraph )))) {s = 3;}

                        else if ( (LA1_0==LBRACE) && ((( !isGraph )||( isGraph )))) {s = 4;}

                        else if ( (LA1_0==SQUOTE) && ((( !isGraph )||( isGraph )))) {s = 5;}

                        else if ( (LA1_0==PLING) && ((( !isGraph )||( isGraph )))) {s = 6;}

                        else if ( (LA1_0==REM) ) {s = 7;}

                        else if ( (LA1_0==RBRACE||LA1_0==BSLASH) && (( isGraph ))) {s = 8;}

                        else if ( (LA1_0==PAR) ) {s = 9;}

                        else if ( (LA1_0==NESTED) ) {s = 10;}

                        else if ( (LA1_0==INT) ) {s = 11;}

                        else if ( (LA1_0==REAL) ) {s = 12;}

                        else if ( (LA1_0==STRING) ) {s = 13;}

                        else if ( (LA1_0==BOOL) ) {s = 14;}

                        else if ( (LA1_0==ATTR) ) {s = 15;}

                        else if ( (LA1_0==PROD) ) {s = 16;}

                        else if ( (LA1_0==ARG) ) {s = 17;}

                        else if ( ((LA1_0>=EMPTY && LA1_0<=CONSTRAINT)||LA1_0==IDENT||(LA1_0>=LABEL && LA1_0<=NUMBER)||(LA1_0>=DOT && LA1_0<=58)) && ((( !isGraph )||( isGraph )))) {s = 18;}

                        else if ( (LA1_0==EOF) ) {s = 19;}

                         
                        input.seek(index1_0);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 1, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA6_eotS =
        "\31\uffff";
    static final String DFA6_eofS =
        "\1\uffff\10\21\11\uffff\1\21\2\uffff\1\21\1\uffff\2\21";
    static final String DFA6_minS =
        "\11\4\11\uffff\1\4\2\uffff\1\4\1\uffff\2\4";
    static final String DFA6_maxS =
        "\11\72\11\uffff\1\72\2\uffff\1\72\1\uffff\2\72";
    static final String DFA6_acceptS =
        "\11\uffff\11\3\1\uffff\1\3\1\1\1\uffff\1\2\2\uffff";
    static final String DFA6_specialS =
        "\1\2\1\3\1\14\1\13\1\5\1\4\1\7\1\6\1\11\11\uffff\1\1\2\uffff\1"+
        "\12\1\uffff\1\10\1\0}>";
    static final String[] DFA6_transitionS = {
            "\1\4\1\5\1\6\1\7\1\10\1\17\1\1\1\2\1\3\11\17\3\11\5\17\1\13"+
            "\1\17\1\11\2\17\1\14\1\20\1\16\1\12\1\15\1\20\22\17",
            "\32\23\1\22\1\23\1\24\2\23\2\20\2\23\2\20\22\23",
            "\32\23\1\22\1\23\1\24\2\23\2\20\2\23\2\20\22\23",
            "\32\23\1\22\1\23\1\24\2\23\2\20\2\23\2\20\22\23",
            "\32\23\1\25\1\23\1\26\2\23\2\20\2\23\2\20\22\23",
            "\32\23\1\25\1\23\1\26\2\23\2\20\2\23\2\20\22\23",
            "\32\23\1\25\1\23\1\26\2\23\2\20\2\23\2\20\22\23",
            "\32\23\1\25\1\23\1\26\2\23\2\20\2\23\2\20\22\23",
            "\32\23\1\25\1\23\1\26\2\23\2\20\2\23\2\20\22\23",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\33\23\1\27\1\uffff\2\23\2\20\2\23\2\20\22\23",
            "",
            "",
            "\33\23\1\30\1\uffff\2\23\2\20\2\23\2\20\22\23",
            "",
            "\34\23\1\24\2\23\2\20\2\23\2\20\22\23",
            "\34\23\1\26\2\23\2\20\2\23\2\20\22\23"
    };

    static final short[] DFA6_eot = DFA.unpackEncodedString(DFA6_eotS);
    static final short[] DFA6_eof = DFA.unpackEncodedString(DFA6_eofS);
    static final char[] DFA6_min = DFA.unpackEncodedStringToUnsignedChars(DFA6_minS);
    static final char[] DFA6_max = DFA.unpackEncodedStringToUnsignedChars(DFA6_maxS);
    static final short[] DFA6_accept = DFA.unpackEncodedString(DFA6_acceptS);
    static final short[] DFA6_special = DFA.unpackEncodedString(DFA6_specialS);
    static final short[][] DFA6_transition;

    static {
        int numStates = DFA6_transitionS.length;
        DFA6_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
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
            return "74:1: prefixedLabel : ( ( FORALL | FORALLX | EXISTS ) ( EQUALS IDENT )? COLON prefixedLabel | ( NEW | DEL | NOT | USE | CNEW ) ( EQUALS IDENT )? COLON prefixedLabel | actualLabel );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA6_24 = input.LA(1);

                         
                        int index6_24 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA6_24==COLON) ) {s = 22;}

                        else if ( (LA6_24==EOF) && ((( !isGraph )||( isGraph )))) {s = 17;}

                        else if ( ((LA6_24>=NEW && LA6_24<=IDENT)||(LA6_24>=LABEL && LA6_24<=NUMBER)||(LA6_24>=PLING && LA6_24<=QUERY)||(LA6_24>=DOT && LA6_24<=58)) && ((( !isGraph )||( isGraph )))) {s = 19;}

                        else if ( ((LA6_24>=LBRACE && LA6_24<=RBRACE)||(LA6_24>=SQUOTE && LA6_24<=BSLASH)) && (( isGraph ))) {s = 16;}

                         
                        input.seek(index6_24);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA6_18 = input.LA(1);

                         
                        int index6_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA6_18==IDENT) ) {s = 23;}

                        else if ( (LA6_18==EOF) && ((( !isGraph )||( isGraph )))) {s = 17;}

                        else if ( ((LA6_18>=NEW && LA6_18<=EQUALS)||(LA6_18>=LABEL && LA6_18<=NUMBER)||(LA6_18>=PLING && LA6_18<=QUERY)||(LA6_18>=DOT && LA6_18<=58)) && ((( !isGraph )||( isGraph )))) {s = 19;}

                        else if ( ((LA6_18>=LBRACE && LA6_18<=RBRACE)||(LA6_18>=SQUOTE && LA6_18<=BSLASH)) && (( isGraph ))) {s = 16;}

                         
                        input.seek(index6_18);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA6_0 = input.LA(1);

                         
                        int index6_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA6_0==FORALL) ) {s = 1;}

                        else if ( (LA6_0==FORALLX) ) {s = 2;}

                        else if ( (LA6_0==EXISTS) ) {s = 3;}

                        else if ( (LA6_0==NEW) ) {s = 4;}

                        else if ( (LA6_0==DEL) ) {s = 5;}

                        else if ( (LA6_0==NOT) ) {s = 6;}

                        else if ( (LA6_0==USE) ) {s = 7;}

                        else if ( (LA6_0==CNEW) ) {s = 8;}

                        else if ( ((LA6_0>=TYPE && LA6_0<=PATH)||LA6_0==COLON) ) {s = 9;}

                        else if ( (LA6_0==QUERY) && ((( !isGraph )||( isGraph )))) {s = 10;}

                        else if ( (LA6_0==EQUALS) && ((( !isGraph )||( isGraph )))) {s = 11;}

                        else if ( (LA6_0==LBRACE) && ((( !isGraph )||( isGraph )))) {s = 12;}

                        else if ( (LA6_0==SQUOTE) && ((( !isGraph )||( isGraph )))) {s = 13;}

                        else if ( (LA6_0==PLING) && ((( !isGraph )||( isGraph )))) {s = 14;}

                        else if ( (LA6_0==REM||(LA6_0>=NESTED && LA6_0<=PAR)||(LA6_0>=EMPTY && LA6_0<=CONSTRAINT)||LA6_0==IDENT||(LA6_0>=LABEL && LA6_0<=NUMBER)||(LA6_0>=DOT && LA6_0<=58)) && ((( !isGraph )||( isGraph )))) {s = 15;}

                        else if ( (LA6_0==RBRACE||LA6_0==BSLASH) && (( isGraph ))) {s = 16;}

                         
                        input.seek(index6_0);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA6_1 = input.LA(1);

                         
                        int index6_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA6_1==EOF) && ((( !isGraph )||( isGraph )))) {s = 17;}

                        else if ( (LA6_1==EQUALS) ) {s = 18;}

                        else if ( ((LA6_1>=LBRACE && LA6_1<=RBRACE)||(LA6_1>=SQUOTE && LA6_1<=BSLASH)) && (( isGraph ))) {s = 16;}

                        else if ( ((LA6_1>=NEW && LA6_1<=CONSTRAINT)||LA6_1==IDENT||(LA6_1>=LABEL && LA6_1<=NUMBER)||(LA6_1>=PLING && LA6_1<=QUERY)||(LA6_1>=DOT && LA6_1<=58)) && ((( !isGraph )||( isGraph )))) {s = 19;}

                        else if ( (LA6_1==COLON) ) {s = 20;}

                         
                        input.seek(index6_1);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA6_5 = input.LA(1);

                         
                        int index6_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA6_5==EQUALS) ) {s = 21;}

                        else if ( (LA6_5==COLON) ) {s = 22;}

                        else if ( (LA6_5==EOF) && ((( !isGraph )||( isGraph )))) {s = 17;}

                        else if ( ((LA6_5>=NEW && LA6_5<=CONSTRAINT)||LA6_5==IDENT||(LA6_5>=LABEL && LA6_5<=NUMBER)||(LA6_5>=PLING && LA6_5<=QUERY)||(LA6_5>=DOT && LA6_5<=58)) && ((( !isGraph )||( isGraph )))) {s = 19;}

                        else if ( ((LA6_5>=LBRACE && LA6_5<=RBRACE)||(LA6_5>=SQUOTE && LA6_5<=BSLASH)) && (( isGraph ))) {s = 16;}

                         
                        input.seek(index6_5);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA6_4 = input.LA(1);

                         
                        int index6_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA6_4==EQUALS) ) {s = 21;}

                        else if ( (LA6_4==COLON) ) {s = 22;}

                        else if ( (LA6_4==EOF) && ((( !isGraph )||( isGraph )))) {s = 17;}

                        else if ( ((LA6_4>=NEW && LA6_4<=CONSTRAINT)||LA6_4==IDENT||(LA6_4>=LABEL && LA6_4<=NUMBER)||(LA6_4>=PLING && LA6_4<=QUERY)||(LA6_4>=DOT && LA6_4<=58)) && ((( !isGraph )||( isGraph )))) {s = 19;}

                        else if ( ((LA6_4>=LBRACE && LA6_4<=RBRACE)||(LA6_4>=SQUOTE && LA6_4<=BSLASH)) && (( isGraph ))) {s = 16;}

                         
                        input.seek(index6_4);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA6_7 = input.LA(1);

                         
                        int index6_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA6_7==EOF) && ((( !isGraph )||( isGraph )))) {s = 17;}

                        else if ( (LA6_7==EQUALS) ) {s = 21;}

                        else if ( ((LA6_7>=LBRACE && LA6_7<=RBRACE)||(LA6_7>=SQUOTE && LA6_7<=BSLASH)) && (( isGraph ))) {s = 16;}

                        else if ( ((LA6_7>=NEW && LA6_7<=CONSTRAINT)||LA6_7==IDENT||(LA6_7>=LABEL && LA6_7<=NUMBER)||(LA6_7>=PLING && LA6_7<=QUERY)||(LA6_7>=DOT && LA6_7<=58)) && ((( !isGraph )||( isGraph )))) {s = 19;}

                        else if ( (LA6_7==COLON) ) {s = 22;}

                         
                        input.seek(index6_7);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA6_6 = input.LA(1);

                         
                        int index6_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA6_6==EOF) && ((( !isGraph )||( isGraph )))) {s = 17;}

                        else if ( (LA6_6==EQUALS) ) {s = 21;}

                        else if ( ((LA6_6>=LBRACE && LA6_6<=RBRACE)||(LA6_6>=SQUOTE && LA6_6<=BSLASH)) && (( isGraph ))) {s = 16;}

                        else if ( ((LA6_6>=NEW && LA6_6<=CONSTRAINT)||LA6_6==IDENT||(LA6_6>=LABEL && LA6_6<=NUMBER)||(LA6_6>=PLING && LA6_6<=QUERY)||(LA6_6>=DOT && LA6_6<=58)) && ((( !isGraph )||( isGraph )))) {s = 19;}

                        else if ( (LA6_6==COLON) ) {s = 22;}

                         
                        input.seek(index6_6);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA6_23 = input.LA(1);

                         
                        int index6_23 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA6_23==EOF) && ((( !isGraph )||( isGraph )))) {s = 17;}

                        else if ( ((LA6_23>=NEW && LA6_23<=IDENT)||(LA6_23>=LABEL && LA6_23<=NUMBER)||(LA6_23>=PLING && LA6_23<=QUERY)||(LA6_23>=DOT && LA6_23<=58)) && ((( !isGraph )||( isGraph )))) {s = 19;}

                        else if ( ((LA6_23>=LBRACE && LA6_23<=RBRACE)||(LA6_23>=SQUOTE && LA6_23<=BSLASH)) && (( isGraph ))) {s = 16;}

                        else if ( (LA6_23==COLON) ) {s = 20;}

                         
                        input.seek(index6_23);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA6_8 = input.LA(1);

                         
                        int index6_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA6_8==EOF) && ((( !isGraph )||( isGraph )))) {s = 17;}

                        else if ( (LA6_8==EQUALS) ) {s = 21;}

                        else if ( ((LA6_8>=LBRACE && LA6_8<=RBRACE)||(LA6_8>=SQUOTE && LA6_8<=BSLASH)) && (( isGraph ))) {s = 16;}

                        else if ( ((LA6_8>=NEW && LA6_8<=CONSTRAINT)||LA6_8==IDENT||(LA6_8>=LABEL && LA6_8<=NUMBER)||(LA6_8>=PLING && LA6_8<=QUERY)||(LA6_8>=DOT && LA6_8<=58)) && ((( !isGraph )||( isGraph )))) {s = 19;}

                        else if ( (LA6_8==COLON) ) {s = 22;}

                         
                        input.seek(index6_8);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA6_21 = input.LA(1);

                         
                        int index6_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA6_21==IDENT) ) {s = 24;}

                        else if ( (LA6_21==EOF) && ((( !isGraph )||( isGraph )))) {s = 17;}

                        else if ( ((LA6_21>=NEW && LA6_21<=EQUALS)||(LA6_21>=LABEL && LA6_21<=NUMBER)||(LA6_21>=PLING && LA6_21<=QUERY)||(LA6_21>=DOT && LA6_21<=58)) && ((( !isGraph )||( isGraph )))) {s = 19;}

                        else if ( ((LA6_21>=LBRACE && LA6_21<=RBRACE)||(LA6_21>=SQUOTE && LA6_21<=BSLASH)) && (( isGraph ))) {s = 16;}

                         
                        input.seek(index6_21);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA6_3 = input.LA(1);

                         
                        int index6_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA6_3==EOF) && ((( !isGraph )||( isGraph )))) {s = 17;}

                        else if ( (LA6_3==EQUALS) ) {s = 18;}

                        else if ( ((LA6_3>=LBRACE && LA6_3<=RBRACE)||(LA6_3>=SQUOTE && LA6_3<=BSLASH)) && (( isGraph ))) {s = 16;}

                        else if ( ((LA6_3>=NEW && LA6_3<=CONSTRAINT)||LA6_3==IDENT||(LA6_3>=LABEL && LA6_3<=NUMBER)||(LA6_3>=PLING && LA6_3<=QUERY)||(LA6_3>=DOT && LA6_3<=58)) && ((( !isGraph )||( isGraph )))) {s = 19;}

                        else if ( (LA6_3==COLON) ) {s = 20;}

                         
                        input.seek(index6_3);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA6_2 = input.LA(1);

                         
                        int index6_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA6_2==EOF) && ((( !isGraph )||( isGraph )))) {s = 17;}

                        else if ( (LA6_2==EQUALS) ) {s = 18;}

                        else if ( ((LA6_2>=LBRACE && LA6_2<=RBRACE)||(LA6_2>=SQUOTE && LA6_2<=BSLASH)) && (( isGraph ))) {s = 16;}

                        else if ( ((LA6_2>=NEW && LA6_2<=CONSTRAINT)||LA6_2==IDENT||(LA6_2>=LABEL && LA6_2<=NUMBER)||(LA6_2>=PLING && LA6_2<=QUERY)||(LA6_2>=DOT && LA6_2<=58)) && ((( !isGraph )||( isGraph )))) {s = 19;}

                        else if ( (LA6_2==COLON) ) {s = 20;}

                         
                        input.seek(index6_2);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 6, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA13_eotS =
        "\20\uffff";
    static final String DFA13_eofS =
        "\20\uffff";
    static final String DFA13_minS =
        "\1\4\2\0\1\uffff\7\0\5\uffff";
    static final String DFA13_maxS =
        "\1\72\2\0\1\uffff\7\0\5\uffff";
    static final String DFA13_acceptS =
        "\3\uffff\1\3\7\uffff\1\5\1\1\1\6\1\2\1\4";
    static final String DFA13_specialS =
        "\1\0\1\1\1\2\1\uffff\1\3\1\4\1\5\1\6\1\7\1\10\1\11\5\uffff}>";
    static final String[] DFA13_transitionS = {
            "\22\12\1\1\1\2\1\4\5\12\1\6\1\12\1\3\2\12\1\7\1\13\1\11\1\5"+
            "\1\10\1\13\22\12",
            "\1\uffff",
            "\1\uffff",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA13_eot = DFA.unpackEncodedString(DFA13_eotS);
    static final short[] DFA13_eof = DFA.unpackEncodedString(DFA13_eofS);
    static final char[] DFA13_min = DFA.unpackEncodedStringToUnsignedChars(DFA13_minS);
    static final char[] DFA13_max = DFA.unpackEncodedStringToUnsignedChars(DFA13_maxS);
    static final short[] DFA13_accept = DFA.unpackEncodedString(DFA13_acceptS);
    static final short[] DFA13_special = DFA.unpackEncodedString(DFA13_specialS);
    static final short[][] DFA13_transition;

    static {
        int numStates = DFA13_transitionS.length;
        DFA13_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA13_transition[i] = DFA.unpackEncodedString(DFA13_transitionS[i]);
        }
    }

    class DFA13 extends DFA {

        public DFA13(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 13;
            this.eot = DFA13_eot;
            this.eof = DFA13_eof;
            this.min = DFA13_min;
            this.max = DFA13_max;
            this.accept = DFA13_accept;
            this.special = DFA13_special;
            this.transition = DFA13_transition;
        }
        public String getDescription() {
            return "94:1: actualLabel : ( TYPE COLON IDENT -> ^( ATOM ^( TYPE IDENT ) ) | FLAG COLON IDENT -> ^( ATOM ^( FLAG IDENT ) ) | COLON text -> ^( ATOM text ) | PATH COLON regExpr | ( graphDefault EOF )=>{...}? => graphLabel | ( ruleLabel EOF )=>{...}? => ruleLabel );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA13_0 = input.LA(1);

                         
                        int index13_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA13_0==TYPE) ) {s = 1;}

                        else if ( (LA13_0==FLAG) ) {s = 2;}

                        else if ( (LA13_0==COLON) ) {s = 3;}

                        else if ( (LA13_0==PATH) ) {s = 4;}

                        else if ( (LA13_0==QUERY) ) {s = 5;}

                        else if ( (LA13_0==EQUALS) ) {s = 6;}

                        else if ( (LA13_0==LBRACE) ) {s = 7;}

                        else if ( (LA13_0==SQUOTE) ) {s = 8;}

                        else if ( (LA13_0==PLING) ) {s = 9;}

                        else if ( ((LA13_0>=NEW && LA13_0<=PAR)||(LA13_0>=EMPTY && LA13_0<=CONSTRAINT)||LA13_0==IDENT||(LA13_0>=LABEL && LA13_0<=NUMBER)||(LA13_0>=DOT && LA13_0<=58)) ) {s = 10;}

                        else if ( (LA13_0==RBRACE||LA13_0==BSLASH) && ((synpred1_Label0()&&( isGraph )))) {s = 11;}

                         
                        input.seek(index13_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA13_1 = input.LA(1);

                         
                        int index13_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (true) ) {s = 12;}

                        else if ( ((synpred1_Label0()&&( isGraph ))) ) {s = 11;}

                        else if ( ((synpred2_Label0()&&( !isGraph ))) ) {s = 13;}

                         
                        input.seek(index13_1);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA13_2 = input.LA(1);

                         
                        int index13_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (true) ) {s = 14;}

                        else if ( ((synpred1_Label0()&&( isGraph ))) ) {s = 11;}

                        else if ( ((synpred2_Label0()&&( !isGraph ))) ) {s = 13;}

                         
                        input.seek(index13_2);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA13_4 = input.LA(1);

                         
                        int index13_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (true) ) {s = 15;}

                        else if ( ((synpred1_Label0()&&( isGraph ))) ) {s = 11;}

                        else if ( ((synpred2_Label0()&&( !isGraph ))) ) {s = 13;}

                         
                        input.seek(index13_4);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA13_5 = input.LA(1);

                         
                        int index13_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((synpred1_Label0()&&( isGraph ))) ) {s = 11;}

                        else if ( ((synpred2_Label0()&&( !isGraph ))) ) {s = 13;}

                         
                        input.seek(index13_5);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA13_6 = input.LA(1);

                         
                        int index13_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((synpred1_Label0()&&( isGraph ))) ) {s = 11;}

                        else if ( ((synpred2_Label0()&&( !isGraph ))) ) {s = 13;}

                         
                        input.seek(index13_6);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA13_7 = input.LA(1);

                         
                        int index13_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((synpred1_Label0()&&( isGraph ))) ) {s = 11;}

                        else if ( ((synpred2_Label0()&&( !isGraph ))) ) {s = 13;}

                         
                        input.seek(index13_7);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA13_8 = input.LA(1);

                         
                        int index13_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((synpred1_Label0()&&( isGraph ))) ) {s = 11;}

                        else if ( ((synpred2_Label0()&&( !isGraph ))) ) {s = 13;}

                         
                        input.seek(index13_8);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA13_9 = input.LA(1);

                         
                        int index13_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((synpred1_Label0()&&( isGraph ))) ) {s = 11;}

                        else if ( ((synpred2_Label0()&&( !isGraph ))) ) {s = 13;}

                         
                        input.seek(index13_9);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA13_10 = input.LA(1);

                         
                        int index13_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((synpred1_Label0()&&( isGraph ))) ) {s = 11;}

                        else if ( ((synpred2_Label0()&&( !isGraph ))) ) {s = 13;}

                         
                        input.seek(index13_10);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 13, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_prefixedLabel_in_label257 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_specialLabel_in_label261 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_label265 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FORALL_in_prefixedLabel283 = new BitSet(new long[]{0x0000000140000000L});
    public static final BitSet FOLLOW_FORALLX_in_prefixedLabel288 = new BitSet(new long[]{0x0000000140000000L});
    public static final BitSet FOLLOW_EXISTS_in_prefixedLabel293 = new BitSet(new long[]{0x0000000140000000L});
    public static final BitSet FOLLOW_EQUALS_in_prefixedLabel299 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_IDENT_in_prefixedLabel302 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_COLON_in_prefixedLabel306 = new BitSet(new long[]{0x07FFFFFFFFFFFFF0L});
    public static final BitSet FOLLOW_prefixedLabel_in_prefixedLabel309 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_prefixedLabel318 = new BitSet(new long[]{0x0000000140000000L});
    public static final BitSet FOLLOW_DEL_in_prefixedLabel323 = new BitSet(new long[]{0x0000000140000000L});
    public static final BitSet FOLLOW_NOT_in_prefixedLabel328 = new BitSet(new long[]{0x0000000140000000L});
    public static final BitSet FOLLOW_USE_in_prefixedLabel333 = new BitSet(new long[]{0x0000000140000000L});
    public static final BitSet FOLLOW_CNEW_in_prefixedLabel338 = new BitSet(new long[]{0x0000000140000000L});
    public static final BitSet FOLLOW_EQUALS_in_prefixedLabel344 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_IDENT_in_prefixedLabel347 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_COLON_in_prefixedLabel351 = new BitSet(new long[]{0x07FFFFFFFFFFFFF0L});
    public static final BitSet FOLLOW_prefixedLabel_in_prefixedLabel354 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_actualLabel_in_prefixedLabel361 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REM_in_specialLabel376 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_COLON_in_specialLabel379 = new BitSet(new long[]{0x03FFFFFFFFFFFFF0L});
    public static final BitSet FOLLOW_text_in_specialLabel382 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PAR_in_specialLabel389 = new BitSet(new long[]{0x0000000140000000L});
    public static final BitSet FOLLOW_EQUALS_in_specialLabel393 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_LABEL_in_specialLabel396 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_COLON_in_specialLabel400 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NESTED_in_specialLabel408 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_COLON_in_specialLabel411 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_IDENT_in_specialLabel414 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_specialLabel425 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_COLON_in_specialLabel428 = new BitSet(new long[]{0x0000000480000002L});
    public static final BitSet FOLLOW_set_in_specialLabel431 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REAL_in_specialLabel445 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_COLON_in_specialLabel448 = new BitSet(new long[]{0x0000020480000002L});
    public static final BitSet FOLLOW_rnumber_in_specialLabel452 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_specialLabel456 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_specialLabel465 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_COLON_in_specialLabel468 = new BitSet(new long[]{0x0010000080000002L});
    public static final BitSet FOLLOW_dqText_in_specialLabel472 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_specialLabel476 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_specialLabel485 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_COLON_in_specialLabel488 = new BitSet(new long[]{0x0000000098000002L});
    public static final BitSet FOLLOW_set_in_specialLabel491 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ATTR_in_specialLabel509 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_COLON_in_specialLabel512 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PROD_in_specialLabel520 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_COLON_in_specialLabel523 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARG_in_specialLabel531 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_COLON_in_specialLabel534 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_NUMBER_in_specialLabel537 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TYPE_in_actualLabel552 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_COLON_in_actualLabel554 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_IDENT_in_actualLabel556 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLAG_in_actualLabel575 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_COLON_in_actualLabel577 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_IDENT_in_actualLabel579 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_actualLabel598 = new BitSet(new long[]{0x03FFFFFFFFFFFFF0L});
    public static final BitSet FOLLOW_text_in_actualLabel600 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PATH_in_actualLabel615 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_COLON_in_actualLabel618 = new BitSet(new long[]{0x000048E6C0000000L});
    public static final BitSet FOLLOW_regExpr_in_actualLabel621 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_graphLabel_in_actualLabel640 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleLabel_in_actualLabel659 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_text675 = new BitSet(new long[]{0x03FFFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_graphDefault_in_graphLabel693 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_graphDefault717 = new BitSet(new long[]{0x07FFFFFEFFFFFFF2L});
    public static final BitSet FOLLOW_wildcard_in_ruleLabel735 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_ruleLabel742 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_ruleLabel749 = new BitSet(new long[]{0x000048E6C0000000L});
    public static final BitSet FOLLOW_regExpr_in_ruleLabel752 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_RBRACE_in_ruleLabel754 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sqText_in_ruleLabel762 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLING_in_ruleLabel777 = new BitSet(new long[]{0x07FFFFFFFFFFFFF0L});
    public static final BitSet FOLLOW_ruleLabel_in_ruleLabel780 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDefault_in_ruleLabel787 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_ruleDefault810 = new BitSet(new long[]{0x07FFFE66FFFFFFF2L});
    public static final BitSet FOLLOW_set_in_ruleDefault849 = new BitSet(new long[]{0x07FFFE66FFFFFFF2L});
    public static final BitSet FOLLOW_TYPE_in_nodeLabel884 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_COLON_in_nodeLabel887 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_IDENT_in_nodeLabel890 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLAG_in_nodeLabel897 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_COLON_in_nodeLabel900 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_IDENT_in_nodeLabel903 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NUMBER_in_rnumber917 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_DOT_in_rnumber920 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_NUMBER_in_rnumber922 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_rnumber932 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_NUMBER_in_rnumber934 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_choice_in_regExpr949 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLING_in_regExpr953 = new BitSet(new long[]{0x000048E6C0000000L});
    public static final BitSet FOLLOW_regExpr_in_regExpr956 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sequence_in_choice967 = new BitSet(new long[]{0x0000040000000002L});
    public static final BitSet FOLLOW_BAR_in_choice970 = new BitSet(new long[]{0x000048C6C0000000L});
    public static final BitSet FOLLOW_choice_in_choice973 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unary_in_sequence987 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_DOT_in_sequence990 = new BitSet(new long[]{0x000048C6C0000000L});
    public static final BitSet FOLLOW_sequence_in_sequence993 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_unary1007 = new BitSet(new long[]{0x000048C6C0000000L});
    public static final BitSet FOLLOW_unary_in_unary1010 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_unary1017 = new BitSet(new long[]{0x0000300000000002L});
    public static final BitSet FOLLOW_STAR_in_unary1020 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_unary1025 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_unary1035 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_unary1042 = new BitSet(new long[]{0x000048E6C0000000L});
    public static final BitSet FOLLOW_regExpr_in_unary1045 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_RPAR_in_unary1047 = new BitSet(new long[]{0x0000300000000002L});
    public static final BitSet FOLLOW_STAR_in_unary1051 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_unary1056 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_wildcard_in_unary1066 = new BitSet(new long[]{0x0000300000000002L});
    public static final BitSet FOLLOW_STAR_in_unary1069 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_unary1074 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sqText_in_atom1092 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atomLabel_in_atom1107 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_atomLabel0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUERY_in_wildcard1156 = new BitSet(new long[]{0x0001000080000002L});
    public static final BitSet FOLLOW_IDENT_in_wildcard1159 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_LSQUARE_in_wildcard1163 = new BitSet(new long[]{0x0002008680000000L});
    public static final BitSet FOLLOW_HAT_in_wildcard1166 = new BitSet(new long[]{0x0000008680000000L});
    public static final BitSet FOLLOW_atom_in_wildcard1169 = new BitSet(new long[]{0x000C000000000000L});
    public static final BitSet FOLLOW_COMMA_in_wildcard1172 = new BitSet(new long[]{0x0000008680000000L});
    public static final BitSet FOLLOW_atom_in_wildcard1175 = new BitSet(new long[]{0x000C000000000000L});
    public static final BitSet FOLLOW_RSQUARE_in_wildcard1179 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SQUOTE_in_sqText1197 = new BitSet(new long[]{0x07FFFFFFFFFFFFF0L});
    public static final BitSet FOLLOW_set_in_sqText1201 = new BitSet(new long[]{0x07FFFFFFFFFFFFF0L});
    public static final BitSet FOLLOW_sqTextSpecial_in_sqText1210 = new BitSet(new long[]{0x07FFFFFFFFFFFFF0L});
    public static final BitSet FOLLOW_SQUOTE_in_sqText1214 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BSLASH_in_sqTextSpecial1226 = new BitSet(new long[]{0x0000018000000000L});
    public static final BitSet FOLLOW_set_in_sqTextSpecial1229 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DQUOTE_in_dqText1248 = new BitSet(new long[]{0x07FFFFFFFFFFFFF0L});
    public static final BitSet FOLLOW_set_in_dqText1252 = new BitSet(new long[]{0x07FFFFFFFFFFFFF0L});
    public static final BitSet FOLLOW_dqTextSpecial_in_dqText1261 = new BitSet(new long[]{0x07FFFFFFFFFFFFF0L});
    public static final BitSet FOLLOW_DQUOTE_in_dqText1265 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BSLASH_in_dqTextSpecial1277 = new BitSet(new long[]{0x0010010000000000L});
    public static final BitSet FOLLOW_set_in_dqTextSpecial1280 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_graphDefault_in_synpred1_Label0629 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_synpred1_Label0631 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleLabel_in_synpred2_Label0648 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_synpred2_Label0650 = new BitSet(new long[]{0x0000000000000002L});

}
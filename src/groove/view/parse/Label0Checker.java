// $ANTLR 3.1b1 Label0Checker.g 2010-04-26 09:48:30

package groove.view.parse;
import java.util.List;
import java.util.LinkedList;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


@SuppressWarnings("all")              
public class Label0Checker extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "NEW", "DEL", "NOT", "USE", "CNEW", "REM", "FORALL", "FORALLX", "EXISTS", "NESTED", "INT", "REAL", "STRING", "BOOL", "ATTR", "PROD", "ARG", "PAR", "TYPE", "FLAG", "PATH", "EMPTY", "ATOM", "TRUE", "FALSE", "CONSTRAINT", "EQUALS", "IDENT", "COLON", "LABEL", "NUMBER", "LBRACE", "RBRACE", "PLING", "QUERY", "SQUOTE", "BSLASH", "DOT", "BAR", "MINUS", "STAR", "PLUS", "LPAR", "RPAR", "LSQUARE", "HAT", "COMMA", "RSQUARE", "DQUOTE", "DOLLAR", "UNDER", "LETTER", "IDENTCHAR", "DIGIT", "'\n'", "NUMER", "'\\n'"
    };
    public static final int DOLLAR=53;
    public static final int STAR=44;
    public static final int LSQUARE=48;
    public static final int FORALLX=11;
    public static final int NUMER=59;
    public static final int LETTER=55;
    public static final int DEL=5;
    public static final int LBRACE=35;
    public static final int NEW=4;
    public static final int DQUOTE=52;
    public static final int IDENTCHAR=56;
    public static final int EQUALS=30;
    public static final int NOT=6;
    public static final int ATOM=26;
    public static final int T__60=60;
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
    public static final int COLON=32;
    public static final int NESTED=13;
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


        public Label0Checker(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public Label0Checker(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return Label0Checker.tokenNames; }
    public String getGrammarFileName() { return "Label0Checker.g"; }

    
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
    
        String concat(CommonTree seq) {
            List children = seq.getChildren();
            if (children == null) {
                return seq.getText();
            } else {
                StringBuilder result = new StringBuilder();
                for (Object token: seq.getChildren()) {
                    result.append(((CommonTree) token).getText());
                }
                return result.toString();
            }
        }


    public static class label_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start label
    // Label0Checker.g:42:1: label : ( prefixedLabel | specialLabel );
    public final Label0Checker.label_return label() throws RecognitionException {
        Label0Checker.label_return retval = new Label0Checker.label_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        Label0Checker.prefixedLabel_return prefixedLabel1 = null;

        Label0Checker.specialLabel_return specialLabel2 = null;



        try {
            // Label0Checker.g:43:3: ( prefixedLabel | specialLabel )
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( ((LA1_0>=NEW && LA1_0<=CNEW)||(LA1_0>=FORALL && LA1_0<=EXISTS)||LA1_0==ATOM||LA1_0==EQUALS||(LA1_0>=PLING && LA1_0<=QUERY)||(LA1_0>=DOT && LA1_0<=PLUS)) ) {
                alt1=1;
            }
            else if ( (LA1_0==REM||(LA1_0>=INT && LA1_0<=PAR)) ) {
                alt1=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }
            switch (alt1) {
                case 1 :
                    // Label0Checker.g:43:5: prefixedLabel
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_prefixedLabel_in_label67);
                    prefixedLabel1=prefixedLabel();

                    state._fsp--;

                    adaptor.addChild(root_0, prefixedLabel1.getTree());

                    }
                    break;
                case 2 :
                    // Label0Checker.g:44:5: specialLabel
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_specialLabel_in_label73);
                    specialLabel2=specialLabel();

                    state._fsp--;

                    adaptor.addChild(root_0, specialLabel2.getTree());

                    }
                    break;

            }
            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end label

    public static class prefixedLabel_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start prefixedLabel
    // Label0Checker.g:47:1: prefixedLabel : ( ^( FORALL ( IDENT )? ( prefixedLabel )? ) | ^( FORALLX ( IDENT )? ( prefixedLabel )? ) | ^( EXISTS ( IDENT )? ( prefixedLabel )? ) | ^( NEW ( IDENT )? ( prefixedLabel )? ) | ^( DEL ( IDENT )? ( prefixedLabel )? ) | ^( NOT ( IDENT )? ( prefixedLabel )? ) | ^( USE ( IDENT )? ( prefixedLabel )? ) | ^( CNEW ( IDENT )? ( prefixedLabel )? ) | actualLabel );
    public final Label0Checker.prefixedLabel_return prefixedLabel() throws RecognitionException {
        Label0Checker.prefixedLabel_return retval = new Label0Checker.prefixedLabel_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree FORALL3=null;
        CommonTree IDENT4=null;
        CommonTree FORALLX6=null;
        CommonTree IDENT7=null;
        CommonTree EXISTS9=null;
        CommonTree IDENT10=null;
        CommonTree NEW12=null;
        CommonTree IDENT13=null;
        CommonTree DEL15=null;
        CommonTree IDENT16=null;
        CommonTree NOT18=null;
        CommonTree IDENT19=null;
        CommonTree USE21=null;
        CommonTree IDENT22=null;
        CommonTree CNEW24=null;
        CommonTree IDENT25=null;
        Label0Checker.prefixedLabel_return prefixedLabel5 = null;

        Label0Checker.prefixedLabel_return prefixedLabel8 = null;

        Label0Checker.prefixedLabel_return prefixedLabel11 = null;

        Label0Checker.prefixedLabel_return prefixedLabel14 = null;

        Label0Checker.prefixedLabel_return prefixedLabel17 = null;

        Label0Checker.prefixedLabel_return prefixedLabel20 = null;

        Label0Checker.prefixedLabel_return prefixedLabel23 = null;

        Label0Checker.prefixedLabel_return prefixedLabel26 = null;

        Label0Checker.actualLabel_return actualLabel27 = null;


        CommonTree FORALL3_tree=null;
        CommonTree IDENT4_tree=null;
        CommonTree FORALLX6_tree=null;
        CommonTree IDENT7_tree=null;
        CommonTree EXISTS9_tree=null;
        CommonTree IDENT10_tree=null;
        CommonTree NEW12_tree=null;
        CommonTree IDENT13_tree=null;
        CommonTree DEL15_tree=null;
        CommonTree IDENT16_tree=null;
        CommonTree NOT18_tree=null;
        CommonTree IDENT19_tree=null;
        CommonTree USE21_tree=null;
        CommonTree IDENT22_tree=null;
        CommonTree CNEW24_tree=null;
        CommonTree IDENT25_tree=null;

        try {
            // Label0Checker.g:48:3: ( ^( FORALL ( IDENT )? ( prefixedLabel )? ) | ^( FORALLX ( IDENT )? ( prefixedLabel )? ) | ^( EXISTS ( IDENT )? ( prefixedLabel )? ) | ^( NEW ( IDENT )? ( prefixedLabel )? ) | ^( DEL ( IDENT )? ( prefixedLabel )? ) | ^( NOT ( IDENT )? ( prefixedLabel )? ) | ^( USE ( IDENT )? ( prefixedLabel )? ) | ^( CNEW ( IDENT )? ( prefixedLabel )? ) | actualLabel )
            int alt18=9;
            switch ( input.LA(1) ) {
            case FORALL:
                {
                alt18=1;
                }
                break;
            case FORALLX:
                {
                alt18=2;
                }
                break;
            case EXISTS:
                {
                alt18=3;
                }
                break;
            case NEW:
                {
                alt18=4;
                }
                break;
            case DEL:
                {
                alt18=5;
                }
                break;
            case NOT:
                {
                alt18=6;
                }
                break;
            case USE:
                {
                alt18=7;
                }
                break;
            case CNEW:
                {
                alt18=8;
                }
                break;
            case ATOM:
            case EQUALS:
            case PLING:
            case QUERY:
            case DOT:
            case BAR:
            case MINUS:
            case STAR:
            case PLUS:
                {
                alt18=9;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // Label0Checker.g:48:5: ^( FORALL ( IDENT )? ( prefixedLabel )? )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    FORALL3=(CommonTree)match(input,FORALL,FOLLOW_FORALL_in_prefixedLabel87); 
                    FORALL3_tree = (CommonTree)adaptor.dupNode(FORALL3);

                    root_1 = (CommonTree)adaptor.becomeRoot(FORALL3_tree, root_1);



                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // Label0Checker.g:48:14: ( IDENT )?
                        int alt2=2;
                        int LA2_0 = input.LA(1);

                        if ( (LA2_0==IDENT) ) {
                            alt2=1;
                        }
                        switch (alt2) {
                            case 1 :
                                // Label0Checker.g:48:14: IDENT
                                {
                                _last = (CommonTree)input.LT(1);
                                IDENT4=(CommonTree)match(input,IDENT,FOLLOW_IDENT_in_prefixedLabel89); 
                                IDENT4_tree = (CommonTree)adaptor.dupNode(IDENT4);

                                adaptor.addChild(root_1, IDENT4_tree);


                                }
                                break;

                        }

                        // Label0Checker.g:48:21: ( prefixedLabel )?
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( ((LA3_0>=NEW && LA3_0<=CNEW)||(LA3_0>=FORALL && LA3_0<=EXISTS)||LA3_0==ATOM||LA3_0==EQUALS||(LA3_0>=PLING && LA3_0<=QUERY)||(LA3_0>=DOT && LA3_0<=PLUS)) ) {
                            alt3=1;
                        }
                        switch (alt3) {
                            case 1 :
                                // Label0Checker.g:48:21: prefixedLabel
                                {
                                _last = (CommonTree)input.LT(1);
                                pushFollow(FOLLOW_prefixedLabel_in_prefixedLabel92);
                                prefixedLabel5=prefixedLabel();

                                state._fsp--;

                                adaptor.addChild(root_1, prefixedLabel5.getTree());

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 2 :
                    // Label0Checker.g:49:5: ^( FORALLX ( IDENT )? ( prefixedLabel )? )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    FORALLX6=(CommonTree)match(input,FORALLX,FOLLOW_FORALLX_in_prefixedLabel101); 
                    FORALLX6_tree = (CommonTree)adaptor.dupNode(FORALLX6);

                    root_1 = (CommonTree)adaptor.becomeRoot(FORALLX6_tree, root_1);



                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // Label0Checker.g:49:15: ( IDENT )?
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( (LA4_0==IDENT) ) {
                            alt4=1;
                        }
                        switch (alt4) {
                            case 1 :
                                // Label0Checker.g:49:15: IDENT
                                {
                                _last = (CommonTree)input.LT(1);
                                IDENT7=(CommonTree)match(input,IDENT,FOLLOW_IDENT_in_prefixedLabel103); 
                                IDENT7_tree = (CommonTree)adaptor.dupNode(IDENT7);

                                adaptor.addChild(root_1, IDENT7_tree);


                                }
                                break;

                        }

                        // Label0Checker.g:49:22: ( prefixedLabel )?
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( ((LA5_0>=NEW && LA5_0<=CNEW)||(LA5_0>=FORALL && LA5_0<=EXISTS)||LA5_0==ATOM||LA5_0==EQUALS||(LA5_0>=PLING && LA5_0<=QUERY)||(LA5_0>=DOT && LA5_0<=PLUS)) ) {
                            alt5=1;
                        }
                        switch (alt5) {
                            case 1 :
                                // Label0Checker.g:49:22: prefixedLabel
                                {
                                _last = (CommonTree)input.LT(1);
                                pushFollow(FOLLOW_prefixedLabel_in_prefixedLabel106);
                                prefixedLabel8=prefixedLabel();

                                state._fsp--;

                                adaptor.addChild(root_1, prefixedLabel8.getTree());

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 3 :
                    // Label0Checker.g:50:5: ^( EXISTS ( IDENT )? ( prefixedLabel )? )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    EXISTS9=(CommonTree)match(input,EXISTS,FOLLOW_EXISTS_in_prefixedLabel115); 
                    EXISTS9_tree = (CommonTree)adaptor.dupNode(EXISTS9);

                    root_1 = (CommonTree)adaptor.becomeRoot(EXISTS9_tree, root_1);



                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // Label0Checker.g:50:14: ( IDENT )?
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==IDENT) ) {
                            alt6=1;
                        }
                        switch (alt6) {
                            case 1 :
                                // Label0Checker.g:50:14: IDENT
                                {
                                _last = (CommonTree)input.LT(1);
                                IDENT10=(CommonTree)match(input,IDENT,FOLLOW_IDENT_in_prefixedLabel117); 
                                IDENT10_tree = (CommonTree)adaptor.dupNode(IDENT10);

                                adaptor.addChild(root_1, IDENT10_tree);


                                }
                                break;

                        }

                        // Label0Checker.g:50:21: ( prefixedLabel )?
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( ((LA7_0>=NEW && LA7_0<=CNEW)||(LA7_0>=FORALL && LA7_0<=EXISTS)||LA7_0==ATOM||LA7_0==EQUALS||(LA7_0>=PLING && LA7_0<=QUERY)||(LA7_0>=DOT && LA7_0<=PLUS)) ) {
                            alt7=1;
                        }
                        switch (alt7) {
                            case 1 :
                                // Label0Checker.g:50:21: prefixedLabel
                                {
                                _last = (CommonTree)input.LT(1);
                                pushFollow(FOLLOW_prefixedLabel_in_prefixedLabel120);
                                prefixedLabel11=prefixedLabel();

                                state._fsp--;

                                adaptor.addChild(root_1, prefixedLabel11.getTree());

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 4 :
                    // Label0Checker.g:51:5: ^( NEW ( IDENT )? ( prefixedLabel )? )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    NEW12=(CommonTree)match(input,NEW,FOLLOW_NEW_in_prefixedLabel129); 
                    NEW12_tree = (CommonTree)adaptor.dupNode(NEW12);

                    root_1 = (CommonTree)adaptor.becomeRoot(NEW12_tree, root_1);



                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // Label0Checker.g:51:11: ( IDENT )?
                        int alt8=2;
                        int LA8_0 = input.LA(1);

                        if ( (LA8_0==IDENT) ) {
                            alt8=1;
                        }
                        switch (alt8) {
                            case 1 :
                                // Label0Checker.g:51:11: IDENT
                                {
                                _last = (CommonTree)input.LT(1);
                                IDENT13=(CommonTree)match(input,IDENT,FOLLOW_IDENT_in_prefixedLabel131); 
                                IDENT13_tree = (CommonTree)adaptor.dupNode(IDENT13);

                                adaptor.addChild(root_1, IDENT13_tree);


                                }
                                break;

                        }

                        // Label0Checker.g:51:18: ( prefixedLabel )?
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( ((LA9_0>=NEW && LA9_0<=CNEW)||(LA9_0>=FORALL && LA9_0<=EXISTS)||LA9_0==ATOM||LA9_0==EQUALS||(LA9_0>=PLING && LA9_0<=QUERY)||(LA9_0>=DOT && LA9_0<=PLUS)) ) {
                            alt9=1;
                        }
                        switch (alt9) {
                            case 1 :
                                // Label0Checker.g:51:18: prefixedLabel
                                {
                                _last = (CommonTree)input.LT(1);
                                pushFollow(FOLLOW_prefixedLabel_in_prefixedLabel134);
                                prefixedLabel14=prefixedLabel();

                                state._fsp--;

                                adaptor.addChild(root_1, prefixedLabel14.getTree());

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 5 :
                    // Label0Checker.g:52:5: ^( DEL ( IDENT )? ( prefixedLabel )? )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    DEL15=(CommonTree)match(input,DEL,FOLLOW_DEL_in_prefixedLabel143); 
                    DEL15_tree = (CommonTree)adaptor.dupNode(DEL15);

                    root_1 = (CommonTree)adaptor.becomeRoot(DEL15_tree, root_1);



                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // Label0Checker.g:52:11: ( IDENT )?
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( (LA10_0==IDENT) ) {
                            alt10=1;
                        }
                        switch (alt10) {
                            case 1 :
                                // Label0Checker.g:52:11: IDENT
                                {
                                _last = (CommonTree)input.LT(1);
                                IDENT16=(CommonTree)match(input,IDENT,FOLLOW_IDENT_in_prefixedLabel145); 
                                IDENT16_tree = (CommonTree)adaptor.dupNode(IDENT16);

                                adaptor.addChild(root_1, IDENT16_tree);


                                }
                                break;

                        }

                        // Label0Checker.g:52:18: ( prefixedLabel )?
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( ((LA11_0>=NEW && LA11_0<=CNEW)||(LA11_0>=FORALL && LA11_0<=EXISTS)||LA11_0==ATOM||LA11_0==EQUALS||(LA11_0>=PLING && LA11_0<=QUERY)||(LA11_0>=DOT && LA11_0<=PLUS)) ) {
                            alt11=1;
                        }
                        switch (alt11) {
                            case 1 :
                                // Label0Checker.g:52:18: prefixedLabel
                                {
                                _last = (CommonTree)input.LT(1);
                                pushFollow(FOLLOW_prefixedLabel_in_prefixedLabel148);
                                prefixedLabel17=prefixedLabel();

                                state._fsp--;

                                adaptor.addChild(root_1, prefixedLabel17.getTree());

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 6 :
                    // Label0Checker.g:53:5: ^( NOT ( IDENT )? ( prefixedLabel )? )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    NOT18=(CommonTree)match(input,NOT,FOLLOW_NOT_in_prefixedLabel157); 
                    NOT18_tree = (CommonTree)adaptor.dupNode(NOT18);

                    root_1 = (CommonTree)adaptor.becomeRoot(NOT18_tree, root_1);



                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // Label0Checker.g:53:11: ( IDENT )?
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( (LA12_0==IDENT) ) {
                            alt12=1;
                        }
                        switch (alt12) {
                            case 1 :
                                // Label0Checker.g:53:11: IDENT
                                {
                                _last = (CommonTree)input.LT(1);
                                IDENT19=(CommonTree)match(input,IDENT,FOLLOW_IDENT_in_prefixedLabel159); 
                                IDENT19_tree = (CommonTree)adaptor.dupNode(IDENT19);

                                adaptor.addChild(root_1, IDENT19_tree);


                                }
                                break;

                        }

                        // Label0Checker.g:53:18: ( prefixedLabel )?
                        int alt13=2;
                        int LA13_0 = input.LA(1);

                        if ( ((LA13_0>=NEW && LA13_0<=CNEW)||(LA13_0>=FORALL && LA13_0<=EXISTS)||LA13_0==ATOM||LA13_0==EQUALS||(LA13_0>=PLING && LA13_0<=QUERY)||(LA13_0>=DOT && LA13_0<=PLUS)) ) {
                            alt13=1;
                        }
                        switch (alt13) {
                            case 1 :
                                // Label0Checker.g:53:18: prefixedLabel
                                {
                                _last = (CommonTree)input.LT(1);
                                pushFollow(FOLLOW_prefixedLabel_in_prefixedLabel162);
                                prefixedLabel20=prefixedLabel();

                                state._fsp--;

                                adaptor.addChild(root_1, prefixedLabel20.getTree());

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 7 :
                    // Label0Checker.g:54:5: ^( USE ( IDENT )? ( prefixedLabel )? )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    USE21=(CommonTree)match(input,USE,FOLLOW_USE_in_prefixedLabel171); 
                    USE21_tree = (CommonTree)adaptor.dupNode(USE21);

                    root_1 = (CommonTree)adaptor.becomeRoot(USE21_tree, root_1);



                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // Label0Checker.g:54:11: ( IDENT )?
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( (LA14_0==IDENT) ) {
                            alt14=1;
                        }
                        switch (alt14) {
                            case 1 :
                                // Label0Checker.g:54:11: IDENT
                                {
                                _last = (CommonTree)input.LT(1);
                                IDENT22=(CommonTree)match(input,IDENT,FOLLOW_IDENT_in_prefixedLabel173); 
                                IDENT22_tree = (CommonTree)adaptor.dupNode(IDENT22);

                                adaptor.addChild(root_1, IDENT22_tree);


                                }
                                break;

                        }

                        // Label0Checker.g:54:18: ( prefixedLabel )?
                        int alt15=2;
                        int LA15_0 = input.LA(1);

                        if ( ((LA15_0>=NEW && LA15_0<=CNEW)||(LA15_0>=FORALL && LA15_0<=EXISTS)||LA15_0==ATOM||LA15_0==EQUALS||(LA15_0>=PLING && LA15_0<=QUERY)||(LA15_0>=DOT && LA15_0<=PLUS)) ) {
                            alt15=1;
                        }
                        switch (alt15) {
                            case 1 :
                                // Label0Checker.g:54:18: prefixedLabel
                                {
                                _last = (CommonTree)input.LT(1);
                                pushFollow(FOLLOW_prefixedLabel_in_prefixedLabel176);
                                prefixedLabel23=prefixedLabel();

                                state._fsp--;

                                adaptor.addChild(root_1, prefixedLabel23.getTree());

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 8 :
                    // Label0Checker.g:55:5: ^( CNEW ( IDENT )? ( prefixedLabel )? )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    CNEW24=(CommonTree)match(input,CNEW,FOLLOW_CNEW_in_prefixedLabel185); 
                    CNEW24_tree = (CommonTree)adaptor.dupNode(CNEW24);

                    root_1 = (CommonTree)adaptor.becomeRoot(CNEW24_tree, root_1);



                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // Label0Checker.g:55:12: ( IDENT )?
                        int alt16=2;
                        int LA16_0 = input.LA(1);

                        if ( (LA16_0==IDENT) ) {
                            alt16=1;
                        }
                        switch (alt16) {
                            case 1 :
                                // Label0Checker.g:55:12: IDENT
                                {
                                _last = (CommonTree)input.LT(1);
                                IDENT25=(CommonTree)match(input,IDENT,FOLLOW_IDENT_in_prefixedLabel187); 
                                IDENT25_tree = (CommonTree)adaptor.dupNode(IDENT25);

                                adaptor.addChild(root_1, IDENT25_tree);


                                }
                                break;

                        }

                        // Label0Checker.g:55:19: ( prefixedLabel )?
                        int alt17=2;
                        int LA17_0 = input.LA(1);

                        if ( ((LA17_0>=NEW && LA17_0<=CNEW)||(LA17_0>=FORALL && LA17_0<=EXISTS)||LA17_0==ATOM||LA17_0==EQUALS||(LA17_0>=PLING && LA17_0<=QUERY)||(LA17_0>=DOT && LA17_0<=PLUS)) ) {
                            alt17=1;
                        }
                        switch (alt17) {
                            case 1 :
                                // Label0Checker.g:55:19: prefixedLabel
                                {
                                _last = (CommonTree)input.LT(1);
                                pushFollow(FOLLOW_prefixedLabel_in_prefixedLabel190);
                                prefixedLabel26=prefixedLabel();

                                state._fsp--;

                                adaptor.addChild(root_1, prefixedLabel26.getTree());

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 9 :
                    // Label0Checker.g:56:5: actualLabel
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_actualLabel_in_prefixedLabel198);
                    actualLabel27=actualLabel();

                    state._fsp--;

                    adaptor.addChild(root_0, actualLabel27.getTree());

                    }
                    break;

            }
            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end prefixedLabel

    public static class specialLabel_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start specialLabel
    // Label0Checker.g:59:1: specialLabel : ( ^( REM text ) | ^( PAR ( LABEL )? ) | ^( INT ( number | IDENT )? ) | ^( REAL ( rnumber | IDENT )? ) | ^( STRING ( ^( DQUOTE text ) | IDENT )? ) | ^( BOOL ( bool | IDENT )? ) | ATTR | PROD | ^( ARG NUMBER ) );
    public final Label0Checker.specialLabel_return specialLabel() throws RecognitionException {
        Label0Checker.specialLabel_return retval = new Label0Checker.specialLabel_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree REM28=null;
        CommonTree PAR30=null;
        CommonTree LABEL31=null;
        CommonTree INT32=null;
        CommonTree IDENT34=null;
        CommonTree REAL35=null;
        CommonTree IDENT37=null;
        CommonTree STRING38=null;
        CommonTree DQUOTE39=null;
        CommonTree IDENT41=null;
        CommonTree BOOL42=null;
        CommonTree IDENT44=null;
        CommonTree ATTR45=null;
        CommonTree PROD46=null;
        CommonTree ARG47=null;
        CommonTree NUMBER48=null;
        Label0Checker.text_return text29 = null;

        Label0Checker.number_return number33 = null;

        Label0Checker.rnumber_return rnumber36 = null;

        Label0Checker.text_return text40 = null;

        Label0Checker.bool_return bool43 = null;


        CommonTree REM28_tree=null;
        CommonTree PAR30_tree=null;
        CommonTree LABEL31_tree=null;
        CommonTree INT32_tree=null;
        CommonTree IDENT34_tree=null;
        CommonTree REAL35_tree=null;
        CommonTree IDENT37_tree=null;
        CommonTree STRING38_tree=null;
        CommonTree DQUOTE39_tree=null;
        CommonTree IDENT41_tree=null;
        CommonTree BOOL42_tree=null;
        CommonTree IDENT44_tree=null;
        CommonTree ATTR45_tree=null;
        CommonTree PROD46_tree=null;
        CommonTree ARG47_tree=null;
        CommonTree NUMBER48_tree=null;

        try {
            // Label0Checker.g:60:3: ( ^( REM text ) | ^( PAR ( LABEL )? ) | ^( INT ( number | IDENT )? ) | ^( REAL ( rnumber | IDENT )? ) | ^( STRING ( ^( DQUOTE text ) | IDENT )? ) | ^( BOOL ( bool | IDENT )? ) | ATTR | PROD | ^( ARG NUMBER ) )
            int alt24=9;
            switch ( input.LA(1) ) {
            case REM:
                {
                alt24=1;
                }
                break;
            case PAR:
                {
                alt24=2;
                }
                break;
            case INT:
                {
                alt24=3;
                }
                break;
            case REAL:
                {
                alt24=4;
                }
                break;
            case STRING:
                {
                alt24=5;
                }
                break;
            case BOOL:
                {
                alt24=6;
                }
                break;
            case ATTR:
                {
                alt24=7;
                }
                break;
            case PROD:
                {
                alt24=8;
                }
                break;
            case ARG:
                {
                alt24=9;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 24, 0, input);

                throw nvae;
            }

            switch (alt24) {
                case 1 :
                    // Label0Checker.g:60:5: ^( REM text )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    REM28=(CommonTree)match(input,REM,FOLLOW_REM_in_specialLabel212); 
                    REM28_tree = (CommonTree)adaptor.dupNode(REM28);

                    root_1 = (CommonTree)adaptor.becomeRoot(REM28_tree, root_1);



                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        _last = (CommonTree)input.LT(1);
                        pushFollow(FOLLOW_text_in_specialLabel214);
                        text29=text();

                        state._fsp--;

                        adaptor.addChild(root_1, text29.getTree());

                        match(input, Token.UP, null); 
                    }adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 2 :
                    // Label0Checker.g:61:5: ^( PAR ( LABEL )? )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    PAR30=(CommonTree)match(input,PAR,FOLLOW_PAR_in_specialLabel222); 
                    PAR30_tree = (CommonTree)adaptor.dupNode(PAR30);

                    root_1 = (CommonTree)adaptor.becomeRoot(PAR30_tree, root_1);



                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // Label0Checker.g:61:11: ( LABEL )?
                        int alt19=2;
                        int LA19_0 = input.LA(1);

                        if ( (LA19_0==LABEL) ) {
                            alt19=1;
                        }
                        switch (alt19) {
                            case 1 :
                                // Label0Checker.g:61:11: LABEL
                                {
                                _last = (CommonTree)input.LT(1);
                                LABEL31=(CommonTree)match(input,LABEL,FOLLOW_LABEL_in_specialLabel224); 
                                LABEL31_tree = (CommonTree)adaptor.dupNode(LABEL31);

                                adaptor.addChild(root_1, LABEL31_tree);


                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 3 :
                    // Label0Checker.g:62:5: ^( INT ( number | IDENT )? )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    INT32=(CommonTree)match(input,INT,FOLLOW_INT_in_specialLabel233); 
                    INT32_tree = (CommonTree)adaptor.dupNode(INT32);

                    root_1 = (CommonTree)adaptor.becomeRoot(INT32_tree, root_1);



                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // Label0Checker.g:62:11: ( number | IDENT )?
                        int alt20=3;
                        int LA20_0 = input.LA(1);

                        if ( (LA20_0==NUMBER) ) {
                            alt20=1;
                        }
                        else if ( (LA20_0==IDENT) ) {
                            alt20=2;
                        }
                        switch (alt20) {
                            case 1 :
                                // Label0Checker.g:62:12: number
                                {
                                _last = (CommonTree)input.LT(1);
                                pushFollow(FOLLOW_number_in_specialLabel236);
                                number33=number();

                                state._fsp--;

                                adaptor.addChild(root_1, number33.getTree());

                                }
                                break;
                            case 2 :
                                // Label0Checker.g:62:21: IDENT
                                {
                                _last = (CommonTree)input.LT(1);
                                IDENT34=(CommonTree)match(input,IDENT,FOLLOW_IDENT_in_specialLabel240); 
                                IDENT34_tree = (CommonTree)adaptor.dupNode(IDENT34);

                                adaptor.addChild(root_1, IDENT34_tree);


                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 4 :
                    // Label0Checker.g:63:5: ^( REAL ( rnumber | IDENT )? )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    REAL35=(CommonTree)match(input,REAL,FOLLOW_REAL_in_specialLabel250); 
                    REAL35_tree = (CommonTree)adaptor.dupNode(REAL35);

                    root_1 = (CommonTree)adaptor.becomeRoot(REAL35_tree, root_1);



                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // Label0Checker.g:63:12: ( rnumber | IDENT )?
                        int alt21=3;
                        int LA21_0 = input.LA(1);

                        if ( (LA21_0==NUMBER||LA21_0==DOT) ) {
                            alt21=1;
                        }
                        else if ( (LA21_0==IDENT) ) {
                            alt21=2;
                        }
                        switch (alt21) {
                            case 1 :
                                // Label0Checker.g:63:13: rnumber
                                {
                                _last = (CommonTree)input.LT(1);
                                pushFollow(FOLLOW_rnumber_in_specialLabel253);
                                rnumber36=rnumber();

                                state._fsp--;

                                adaptor.addChild(root_1, rnumber36.getTree());

                                }
                                break;
                            case 2 :
                                // Label0Checker.g:63:23: IDENT
                                {
                                _last = (CommonTree)input.LT(1);
                                IDENT37=(CommonTree)match(input,IDENT,FOLLOW_IDENT_in_specialLabel257); 
                                IDENT37_tree = (CommonTree)adaptor.dupNode(IDENT37);

                                adaptor.addChild(root_1, IDENT37_tree);


                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 5 :
                    // Label0Checker.g:64:5: ^( STRING ( ^( DQUOTE text ) | IDENT )? )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    STRING38=(CommonTree)match(input,STRING,FOLLOW_STRING_in_specialLabel267); 
                    STRING38_tree = (CommonTree)adaptor.dupNode(STRING38);

                    root_1 = (CommonTree)adaptor.becomeRoot(STRING38_tree, root_1);



                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // Label0Checker.g:64:14: ( ^( DQUOTE text ) | IDENT )?
                        int alt22=3;
                        int LA22_0 = input.LA(1);

                        if ( (LA22_0==DQUOTE) ) {
                            alt22=1;
                        }
                        else if ( (LA22_0==IDENT) ) {
                            alt22=2;
                        }
                        switch (alt22) {
                            case 1 :
                                // Label0Checker.g:64:15: ^( DQUOTE text )
                                {
                                _last = (CommonTree)input.LT(1);
                                {
                                CommonTree _save_last_2 = _last;
                                CommonTree _first_2 = null;
                                CommonTree root_2 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                                DQUOTE39=(CommonTree)match(input,DQUOTE,FOLLOW_DQUOTE_in_specialLabel271); 
                                DQUOTE39_tree = (CommonTree)adaptor.dupNode(DQUOTE39);

                                root_2 = (CommonTree)adaptor.becomeRoot(DQUOTE39_tree, root_2);



                                if ( input.LA(1)==Token.DOWN ) {
                                    match(input, Token.DOWN, null); 
                                    _last = (CommonTree)input.LT(1);
                                    pushFollow(FOLLOW_text_in_specialLabel273);
                                    text40=text();

                                    state._fsp--;

                                    adaptor.addChild(root_2, text40.getTree());

                                    match(input, Token.UP, null); 
                                }adaptor.addChild(root_1, root_2);_last = _save_last_2;
                                }


                                }
                                break;
                            case 2 :
                                // Label0Checker.g:64:32: IDENT
                                {
                                _last = (CommonTree)input.LT(1);
                                IDENT41=(CommonTree)match(input,IDENT,FOLLOW_IDENT_in_specialLabel278); 
                                IDENT41_tree = (CommonTree)adaptor.dupNode(IDENT41);

                                adaptor.addChild(root_1, IDENT41_tree);


                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 6 :
                    // Label0Checker.g:65:5: ^( BOOL ( bool | IDENT )? )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    BOOL42=(CommonTree)match(input,BOOL,FOLLOW_BOOL_in_specialLabel288); 
                    BOOL42_tree = (CommonTree)adaptor.dupNode(BOOL42);

                    root_1 = (CommonTree)adaptor.becomeRoot(BOOL42_tree, root_1);



                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // Label0Checker.g:65:12: ( bool | IDENT )?
                        int alt23=3;
                        int LA23_0 = input.LA(1);

                        if ( ((LA23_0>=TRUE && LA23_0<=FALSE)) ) {
                            alt23=1;
                        }
                        else if ( (LA23_0==IDENT) ) {
                            alt23=2;
                        }
                        switch (alt23) {
                            case 1 :
                                // Label0Checker.g:65:13: bool
                                {
                                _last = (CommonTree)input.LT(1);
                                pushFollow(FOLLOW_bool_in_specialLabel291);
                                bool43=bool();

                                state._fsp--;

                                adaptor.addChild(root_1, bool43.getTree());

                                }
                                break;
                            case 2 :
                                // Label0Checker.g:65:20: IDENT
                                {
                                _last = (CommonTree)input.LT(1);
                                IDENT44=(CommonTree)match(input,IDENT,FOLLOW_IDENT_in_specialLabel295); 
                                IDENT44_tree = (CommonTree)adaptor.dupNode(IDENT44);

                                adaptor.addChild(root_1, IDENT44_tree);


                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 7 :
                    // Label0Checker.g:66:5: ATTR
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    ATTR45=(CommonTree)match(input,ATTR,FOLLOW_ATTR_in_specialLabel304); 
                    ATTR45_tree = (CommonTree)adaptor.dupNode(ATTR45);

                    adaptor.addChild(root_0, ATTR45_tree);


                    }
                    break;
                case 8 :
                    // Label0Checker.g:67:5: PROD
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    PROD46=(CommonTree)match(input,PROD,FOLLOW_PROD_in_specialLabel310); 
                    PROD46_tree = (CommonTree)adaptor.dupNode(PROD46);

                    adaptor.addChild(root_0, PROD46_tree);


                    }
                    break;
                case 9 :
                    // Label0Checker.g:68:5: ^( ARG NUMBER )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    ARG47=(CommonTree)match(input,ARG,FOLLOW_ARG_in_specialLabel317); 
                    ARG47_tree = (CommonTree)adaptor.dupNode(ARG47);

                    root_1 = (CommonTree)adaptor.becomeRoot(ARG47_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    NUMBER48=(CommonTree)match(input,NUMBER,FOLLOW_NUMBER_in_specialLabel319); 
                    NUMBER48_tree = (CommonTree)adaptor.dupNode(NUMBER48);

                    adaptor.addChild(root_1, NUMBER48_tree);


                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;

            }
            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end specialLabel

    public static class bool_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start bool
    // Label0Checker.g:71:1: bool : ( TRUE | FALSE );
    public final Label0Checker.bool_return bool() throws RecognitionException {
        Label0Checker.bool_return retval = new Label0Checker.bool_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree set49=null;

        CommonTree set49_tree=null;

        try {
            // Label0Checker.g:72:3: ( TRUE | FALSE )
            // Label0Checker.g:
            {
            root_0 = (CommonTree)adaptor.nil();

            _last = (CommonTree)input.LT(1);
            set49=(CommonTree)input.LT(1);
            if ( (input.LA(1)>=TRUE && input.LA(1)<=FALSE) ) {
                input.consume();

                set49_tree = (CommonTree)adaptor.dupNode(set49);

                adaptor.addChild(root_0, set49_tree);

                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

             

            }

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end bool

    public static class number_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start number
    // Label0Checker.g:75:1: number : NUMBER ;
    public final Label0Checker.number_return number() throws RecognitionException {
        Label0Checker.number_return retval = new Label0Checker.number_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree NUMBER50=null;

        CommonTree NUMBER50_tree=null;

        try {
            // Label0Checker.g:76:3: ( NUMBER )
            // Label0Checker.g:76:5: NUMBER
            {
            root_0 = (CommonTree)adaptor.nil();

            _last = (CommonTree)input.LT(1);
            NUMBER50=(CommonTree)match(input,NUMBER,FOLLOW_NUMBER_in_number352); 
            NUMBER50_tree = (CommonTree)adaptor.dupNode(NUMBER50);

            adaptor.addChild(root_0, NUMBER50_tree);


            }

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end number

    public static class rnumber_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start rnumber
    // Label0Checker.g:79:1: rnumber : ( NUMBER ( DOT ( NUMER )? )? | DOT NUMBER );
    public final Label0Checker.rnumber_return rnumber() throws RecognitionException {
        Label0Checker.rnumber_return retval = new Label0Checker.rnumber_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree NUMBER51=null;
        CommonTree DOT52=null;
        CommonTree NUMER53=null;
        CommonTree DOT54=null;
        CommonTree NUMBER55=null;

        CommonTree NUMBER51_tree=null;
        CommonTree DOT52_tree=null;
        CommonTree NUMER53_tree=null;
        CommonTree DOT54_tree=null;
        CommonTree NUMBER55_tree=null;

        try {
            // Label0Checker.g:80:3: ( NUMBER ( DOT ( NUMER )? )? | DOT NUMBER )
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==NUMBER) ) {
                alt27=1;
            }
            else if ( (LA27_0==DOT) ) {
                alt27=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 27, 0, input);

                throw nvae;
            }
            switch (alt27) {
                case 1 :
                    // Label0Checker.g:80:5: NUMBER ( DOT ( NUMER )? )?
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    NUMBER51=(CommonTree)match(input,NUMBER,FOLLOW_NUMBER_in_rnumber366); 
                    NUMBER51_tree = (CommonTree)adaptor.dupNode(NUMBER51);

                    adaptor.addChild(root_0, NUMBER51_tree);

                    // Label0Checker.g:80:12: ( DOT ( NUMER )? )?
                    int alt26=2;
                    int LA26_0 = input.LA(1);

                    if ( (LA26_0==DOT) ) {
                        alt26=1;
                    }
                    switch (alt26) {
                        case 1 :
                            // Label0Checker.g:80:13: DOT ( NUMER )?
                            {
                            _last = (CommonTree)input.LT(1);
                            DOT52=(CommonTree)match(input,DOT,FOLLOW_DOT_in_rnumber369); 
                            DOT52_tree = (CommonTree)adaptor.dupNode(DOT52);

                            adaptor.addChild(root_0, DOT52_tree);

                            // Label0Checker.g:80:17: ( NUMER )?
                            int alt25=2;
                            int LA25_0 = input.LA(1);

                            if ( (LA25_0==NUMER) ) {
                                alt25=1;
                            }
                            switch (alt25) {
                                case 1 :
                                    // Label0Checker.g:80:17: NUMER
                                    {
                                    _last = (CommonTree)input.LT(1);
                                    NUMER53=(CommonTree)match(input,NUMER,FOLLOW_NUMER_in_rnumber371); 
                                    NUMER53_tree = (CommonTree)adaptor.dupNode(NUMER53);

                                    adaptor.addChild(root_0, NUMER53_tree);


                                    }
                                    break;

                            }


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // Label0Checker.g:81:5: DOT NUMBER
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    DOT54=(CommonTree)match(input,DOT,FOLLOW_DOT_in_rnumber380); 
                    DOT54_tree = (CommonTree)adaptor.dupNode(DOT54);

                    adaptor.addChild(root_0, DOT54_tree);

                    _last = (CommonTree)input.LT(1);
                    NUMBER55=(CommonTree)match(input,NUMBER,FOLLOW_NUMBER_in_rnumber382); 
                    NUMBER55_tree = (CommonTree)adaptor.dupNode(NUMBER55);

                    adaptor.addChild(root_0, NUMBER55_tree);


                    }
                    break;

            }
            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end rnumber

    public static class actualLabel_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start actualLabel
    // Label0Checker.g:84:1: actualLabel : ( ^( PLING regExpr ) | regExpr );
    public final Label0Checker.actualLabel_return actualLabel() throws RecognitionException {
        Label0Checker.actualLabel_return retval = new Label0Checker.actualLabel_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree PLING56=null;
        Label0Checker.regExpr_return regExpr57 = null;

        Label0Checker.regExpr_return regExpr58 = null;


        CommonTree PLING56_tree=null;

        try {
            // Label0Checker.g:85:3: ( ^( PLING regExpr ) | regExpr )
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==PLING) ) {
                alt28=1;
            }
            else if ( (LA28_0==ATOM||LA28_0==EQUALS||LA28_0==QUERY||(LA28_0>=DOT && LA28_0<=PLUS)) ) {
                alt28=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 28, 0, input);

                throw nvae;
            }
            switch (alt28) {
                case 1 :
                    // Label0Checker.g:85:5: ^( PLING regExpr )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    PLING56=(CommonTree)match(input,PLING,FOLLOW_PLING_in_actualLabel396); 
                    PLING56_tree = (CommonTree)adaptor.dupNode(PLING56);

                    root_1 = (CommonTree)adaptor.becomeRoot(PLING56_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_regExpr_in_actualLabel398);
                    regExpr57=regExpr();

                    state._fsp--;

                    adaptor.addChild(root_1, regExpr57.getTree());

                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 2 :
                    // Label0Checker.g:86:5: regExpr
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_regExpr_in_actualLabel405);
                    regExpr58=regExpr();

                    state._fsp--;

                    adaptor.addChild(root_0, regExpr58.getTree());

                    }
                    break;

            }
            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end actualLabel

    public static class regExpr_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start regExpr
    // Label0Checker.g:89:1: regExpr : ( ^( BAR regExpr regExpr ) | ^( DOT regExpr regExpr ) | ^( MINUS regExpr ) | ^( STAR regExpr ) | ^( PLUS regExpr ) | EQUALS | ^( QUERY ( IDENT )? ( HAT )? ( atom )* ) | atom );
    public final Label0Checker.regExpr_return regExpr() throws RecognitionException {
        Label0Checker.regExpr_return retval = new Label0Checker.regExpr_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree BAR59=null;
        CommonTree DOT62=null;
        CommonTree MINUS65=null;
        CommonTree STAR67=null;
        CommonTree PLUS69=null;
        CommonTree EQUALS71=null;
        CommonTree QUERY72=null;
        CommonTree IDENT73=null;
        CommonTree HAT74=null;
        Label0Checker.regExpr_return regExpr60 = null;

        Label0Checker.regExpr_return regExpr61 = null;

        Label0Checker.regExpr_return regExpr63 = null;

        Label0Checker.regExpr_return regExpr64 = null;

        Label0Checker.regExpr_return regExpr66 = null;

        Label0Checker.regExpr_return regExpr68 = null;

        Label0Checker.regExpr_return regExpr70 = null;

        Label0Checker.atom_return atom75 = null;

        Label0Checker.atom_return atom76 = null;


        CommonTree BAR59_tree=null;
        CommonTree DOT62_tree=null;
        CommonTree MINUS65_tree=null;
        CommonTree STAR67_tree=null;
        CommonTree PLUS69_tree=null;
        CommonTree EQUALS71_tree=null;
        CommonTree QUERY72_tree=null;
        CommonTree IDENT73_tree=null;
        CommonTree HAT74_tree=null;

        try {
            // Label0Checker.g:90:3: ( ^( BAR regExpr regExpr ) | ^( DOT regExpr regExpr ) | ^( MINUS regExpr ) | ^( STAR regExpr ) | ^( PLUS regExpr ) | EQUALS | ^( QUERY ( IDENT )? ( HAT )? ( atom )* ) | atom )
            int alt32=8;
            switch ( input.LA(1) ) {
            case BAR:
                {
                alt32=1;
                }
                break;
            case DOT:
                {
                alt32=2;
                }
                break;
            case MINUS:
                {
                alt32=3;
                }
                break;
            case STAR:
                {
                alt32=4;
                }
                break;
            case PLUS:
                {
                alt32=5;
                }
                break;
            case EQUALS:
                {
                alt32=6;
                }
                break;
            case QUERY:
                {
                alt32=7;
                }
                break;
            case ATOM:
                {
                alt32=8;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                throw nvae;
            }

            switch (alt32) {
                case 1 :
                    // Label0Checker.g:90:5: ^( BAR regExpr regExpr )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    BAR59=(CommonTree)match(input,BAR,FOLLOW_BAR_in_regExpr419); 
                    BAR59_tree = (CommonTree)adaptor.dupNode(BAR59);

                    root_1 = (CommonTree)adaptor.becomeRoot(BAR59_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_regExpr_in_regExpr421);
                    regExpr60=regExpr();

                    state._fsp--;

                    adaptor.addChild(root_1, regExpr60.getTree());
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_regExpr_in_regExpr423);
                    regExpr61=regExpr();

                    state._fsp--;

                    adaptor.addChild(root_1, regExpr61.getTree());

                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 2 :
                    // Label0Checker.g:91:5: ^( DOT regExpr regExpr )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    DOT62=(CommonTree)match(input,DOT,FOLLOW_DOT_in_regExpr431); 
                    DOT62_tree = (CommonTree)adaptor.dupNode(DOT62);

                    root_1 = (CommonTree)adaptor.becomeRoot(DOT62_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_regExpr_in_regExpr433);
                    regExpr63=regExpr();

                    state._fsp--;

                    adaptor.addChild(root_1, regExpr63.getTree());
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_regExpr_in_regExpr435);
                    regExpr64=regExpr();

                    state._fsp--;

                    adaptor.addChild(root_1, regExpr64.getTree());

                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 3 :
                    // Label0Checker.g:92:5: ^( MINUS regExpr )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    MINUS65=(CommonTree)match(input,MINUS,FOLLOW_MINUS_in_regExpr443); 
                    MINUS65_tree = (CommonTree)adaptor.dupNode(MINUS65);

                    root_1 = (CommonTree)adaptor.becomeRoot(MINUS65_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_regExpr_in_regExpr445);
                    regExpr66=regExpr();

                    state._fsp--;

                    adaptor.addChild(root_1, regExpr66.getTree());

                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 4 :
                    // Label0Checker.g:93:5: ^( STAR regExpr )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    STAR67=(CommonTree)match(input,STAR,FOLLOW_STAR_in_regExpr453); 
                    STAR67_tree = (CommonTree)adaptor.dupNode(STAR67);

                    root_1 = (CommonTree)adaptor.becomeRoot(STAR67_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_regExpr_in_regExpr455);
                    regExpr68=regExpr();

                    state._fsp--;

                    adaptor.addChild(root_1, regExpr68.getTree());

                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 5 :
                    // Label0Checker.g:94:5: ^( PLUS regExpr )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    PLUS69=(CommonTree)match(input,PLUS,FOLLOW_PLUS_in_regExpr463); 
                    PLUS69_tree = (CommonTree)adaptor.dupNode(PLUS69);

                    root_1 = (CommonTree)adaptor.becomeRoot(PLUS69_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_regExpr_in_regExpr465);
                    regExpr70=regExpr();

                    state._fsp--;

                    adaptor.addChild(root_1, regExpr70.getTree());

                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 6 :
                    // Label0Checker.g:95:5: EQUALS
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    EQUALS71=(CommonTree)match(input,EQUALS,FOLLOW_EQUALS_in_regExpr472); 
                    EQUALS71_tree = (CommonTree)adaptor.dupNode(EQUALS71);

                    adaptor.addChild(root_0, EQUALS71_tree);


                    }
                    break;
                case 7 :
                    // Label0Checker.g:96:5: ^( QUERY ( IDENT )? ( HAT )? ( atom )* )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    QUERY72=(CommonTree)match(input,QUERY,FOLLOW_QUERY_in_regExpr479); 
                    QUERY72_tree = (CommonTree)adaptor.dupNode(QUERY72);

                    root_1 = (CommonTree)adaptor.becomeRoot(QUERY72_tree, root_1);



                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // Label0Checker.g:96:13: ( IDENT )?
                        int alt29=2;
                        int LA29_0 = input.LA(1);

                        if ( (LA29_0==IDENT) ) {
                            alt29=1;
                        }
                        switch (alt29) {
                            case 1 :
                                // Label0Checker.g:96:13: IDENT
                                {
                                _last = (CommonTree)input.LT(1);
                                IDENT73=(CommonTree)match(input,IDENT,FOLLOW_IDENT_in_regExpr481); 
                                IDENT73_tree = (CommonTree)adaptor.dupNode(IDENT73);

                                adaptor.addChild(root_1, IDENT73_tree);


                                }
                                break;

                        }

                        // Label0Checker.g:96:20: ( HAT )?
                        int alt30=2;
                        int LA30_0 = input.LA(1);

                        if ( (LA30_0==HAT) ) {
                            alt30=1;
                        }
                        switch (alt30) {
                            case 1 :
                                // Label0Checker.g:96:20: HAT
                                {
                                _last = (CommonTree)input.LT(1);
                                HAT74=(CommonTree)match(input,HAT,FOLLOW_HAT_in_regExpr484); 
                                HAT74_tree = (CommonTree)adaptor.dupNode(HAT74);

                                adaptor.addChild(root_1, HAT74_tree);


                                }
                                break;

                        }

                        // Label0Checker.g:96:25: ( atom )*
                        loop31:
                        do {
                            int alt31=2;
                            int LA31_0 = input.LA(1);

                            if ( (LA31_0==ATOM) ) {
                                alt31=1;
                            }


                            switch (alt31) {
                        	case 1 :
                        	    // Label0Checker.g:96:25: atom
                        	    {
                        	    _last = (CommonTree)input.LT(1);
                        	    pushFollow(FOLLOW_atom_in_regExpr487);
                        	    atom75=atom();

                        	    state._fsp--;

                        	    adaptor.addChild(root_1, atom75.getTree());

                        	    }
                        	    break;

                        	default :
                        	    break loop31;
                            }
                        } while (true);


                        match(input, Token.UP, null); 
                    }adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 8 :
                    // Label0Checker.g:97:5: atom
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_atom_in_regExpr495);
                    atom76=atom();

                    state._fsp--;

                    adaptor.addChild(root_0, atom76.getTree());

                    }
                    break;

            }
            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end regExpr

    public static class atom_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start atom
    // Label0Checker.g:100:1: atom : ^( ATOM ( ^( TYPE IDENT ) ^( FLAG IDENT ) | text ) ) ;
    public final Label0Checker.atom_return atom() throws RecognitionException {
        Label0Checker.atom_return retval = new Label0Checker.atom_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree ATOM77=null;
        CommonTree TYPE78=null;
        CommonTree IDENT79=null;
        CommonTree FLAG80=null;
        CommonTree IDENT81=null;
        Label0Checker.text_return text82 = null;


        CommonTree ATOM77_tree=null;
        CommonTree TYPE78_tree=null;
        CommonTree IDENT79_tree=null;
        CommonTree FLAG80_tree=null;
        CommonTree IDENT81_tree=null;

        try {
            // Label0Checker.g:101:3: ( ^( ATOM ( ^( TYPE IDENT ) ^( FLAG IDENT ) | text ) ) )
            // Label0Checker.g:101:5: ^( ATOM ( ^( TYPE IDENT ) ^( FLAG IDENT ) | text ) )
            {
            root_0 = (CommonTree)adaptor.nil();

            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
            ATOM77=(CommonTree)match(input,ATOM,FOLLOW_ATOM_in_atom509); 
            ATOM77_tree = (CommonTree)adaptor.dupNode(ATOM77);

            root_1 = (CommonTree)adaptor.becomeRoot(ATOM77_tree, root_1);



            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // Label0Checker.g:101:12: ( ^( TYPE IDENT ) ^( FLAG IDENT ) | text )
                int alt33=2;
                int LA33_0 = input.LA(1);

                if ( (LA33_0==TYPE) ) {
                    int LA33_1 = input.LA(2);

                    if ( (LA33_1==DOWN) ) {
                        alt33=1;
                    }
                    else if ( ((LA33_1>=UP && LA33_1<=NUMER)) ) {
                        alt33=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 33, 1, input);

                        throw nvae;
                    }
                }
                else if ( ((LA33_0>=UP && LA33_0<=PAR)||(LA33_0>=FLAG && LA33_0<=NUMER)) ) {
                    alt33=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 33, 0, input);

                    throw nvae;
                }
                switch (alt33) {
                    case 1 :
                        // Label0Checker.g:101:13: ^( TYPE IDENT ) ^( FLAG IDENT )
                        {
                        _last = (CommonTree)input.LT(1);
                        {
                        CommonTree _save_last_2 = _last;
                        CommonTree _first_2 = null;
                        CommonTree root_2 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                        TYPE78=(CommonTree)match(input,TYPE,FOLLOW_TYPE_in_atom513); 
                        TYPE78_tree = (CommonTree)adaptor.dupNode(TYPE78);

                        root_2 = (CommonTree)adaptor.becomeRoot(TYPE78_tree, root_2);



                        match(input, Token.DOWN, null); 
                        _last = (CommonTree)input.LT(1);
                        IDENT79=(CommonTree)match(input,IDENT,FOLLOW_IDENT_in_atom515); 
                        IDENT79_tree = (CommonTree)adaptor.dupNode(IDENT79);

                        adaptor.addChild(root_2, IDENT79_tree);


                        match(input, Token.UP, null); adaptor.addChild(root_1, root_2);_last = _save_last_2;
                        }

                        _last = (CommonTree)input.LT(1);
                        {
                        CommonTree _save_last_2 = _last;
                        CommonTree _first_2 = null;
                        CommonTree root_2 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                        FLAG80=(CommonTree)match(input,FLAG,FOLLOW_FLAG_in_atom519); 
                        FLAG80_tree = (CommonTree)adaptor.dupNode(FLAG80);

                        root_2 = (CommonTree)adaptor.becomeRoot(FLAG80_tree, root_2);



                        match(input, Token.DOWN, null); 
                        _last = (CommonTree)input.LT(1);
                        IDENT81=(CommonTree)match(input,IDENT,FOLLOW_IDENT_in_atom521); 
                        IDENT81_tree = (CommonTree)adaptor.dupNode(IDENT81);

                        adaptor.addChild(root_2, IDENT81_tree);


                        match(input, Token.UP, null); adaptor.addChild(root_1, root_2);_last = _save_last_2;
                        }


                        }
                        break;
                    case 2 :
                        // Label0Checker.g:101:43: text
                        {
                        _last = (CommonTree)input.LT(1);
                        pushFollow(FOLLOW_text_in_atom526);
                        text82=text();

                        state._fsp--;

                        adaptor.addChild(root_1, text82.getTree());

                        }
                        break;

                }


                match(input, Token.UP, null); 
            }adaptor.addChild(root_0, root_1);_last = _save_last_1;
            }


            }

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end atom

    public static class text_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start text
    // Label0Checker.g:104:1: text : tokenseq ->;
    public final Label0Checker.text_return text() throws RecognitionException {
        Label0Checker.text_return retval = new Label0Checker.text_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        Label0Checker.tokenseq_return tokenseq83 = null;


        RewriteRuleSubtreeStream stream_tokenseq=new RewriteRuleSubtreeStream(adaptor,"rule tokenseq");
        try {
            // Label0Checker.g:105:3: ( tokenseq ->)
            // Label0Checker.g:105:5: tokenseq
            {
            pushFollow(FOLLOW_tokenseq_in_text543);
            tokenseq83=tokenseq();

            state._fsp--;

            stream_tokenseq.add(tokenseq83.getTree());


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 105:14: ->
            {
                adaptor.addChild(root_0,  new CommonTree(new CommonToken(IDENT, concat((tokenseq83!=null?((CommonTree)tokenseq83.tree):null)))) );

            }

            retval.tree = root_0;
            }

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end text

    public static class tokenseq_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start tokenseq
    // Label0Checker.g:108:1: tokenseq : (~ '\\n' )* ;
    public final Label0Checker.tokenseq_return tokenseq() throws RecognitionException {
        Label0Checker.tokenseq_return retval = new Label0Checker.tokenseq_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree set84=null;

        CommonTree set84_tree=null;

        try {
            // Label0Checker.g:109:3: ( (~ '\\n' )* )
            // Label0Checker.g:109:5: (~ '\\n' )*
            {
            root_0 = (CommonTree)adaptor.nil();

            // Label0Checker.g:109:5: (~ '\\n' )*
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);

                if ( ((LA34_0>=NEW && LA34_0<=NUMER)) ) {
                    alt34=1;
                }


                switch (alt34) {
            	case 1 :
            	    // Label0Checker.g:109:5: ~ '\\n'
            	    {
            	    _last = (CommonTree)input.LT(1);
            	    set84=(CommonTree)input.LT(1);
            	    if ( (input.LA(1)>=NEW && input.LA(1)<=NUMER) ) {
            	        input.consume();

            	        set84_tree = (CommonTree)adaptor.dupNode(set84);

            	        adaptor.addChild(root_0, set84_tree);

            	        state.errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop34;
                }
            } while (true);


            }

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end tokenseq

    // Delegated rules


 

    public static final BitSet FOLLOW_prefixedLabel_in_label67 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_specialLabel_in_label73 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FORALL_in_prefixedLabel87 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_prefixedLabel89 = new BitSet(new long[]{0x00003E6044001DF8L});
    public static final BitSet FOLLOW_prefixedLabel_in_prefixedLabel92 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FORALLX_in_prefixedLabel101 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_prefixedLabel103 = new BitSet(new long[]{0x00003E6044001DF8L});
    public static final BitSet FOLLOW_prefixedLabel_in_prefixedLabel106 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EXISTS_in_prefixedLabel115 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_prefixedLabel117 = new BitSet(new long[]{0x00003E6044001DF8L});
    public static final BitSet FOLLOW_prefixedLabel_in_prefixedLabel120 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NEW_in_prefixedLabel129 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_prefixedLabel131 = new BitSet(new long[]{0x00003E6044001DF8L});
    public static final BitSet FOLLOW_prefixedLabel_in_prefixedLabel134 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DEL_in_prefixedLabel143 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_prefixedLabel145 = new BitSet(new long[]{0x00003E6044001DF8L});
    public static final BitSet FOLLOW_prefixedLabel_in_prefixedLabel148 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_in_prefixedLabel157 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_prefixedLabel159 = new BitSet(new long[]{0x00003E6044001DF8L});
    public static final BitSet FOLLOW_prefixedLabel_in_prefixedLabel162 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_USE_in_prefixedLabel171 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_prefixedLabel173 = new BitSet(new long[]{0x00003E6044001DF8L});
    public static final BitSet FOLLOW_prefixedLabel_in_prefixedLabel176 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CNEW_in_prefixedLabel185 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_prefixedLabel187 = new BitSet(new long[]{0x00003E6044001DF8L});
    public static final BitSet FOLLOW_prefixedLabel_in_prefixedLabel190 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_actualLabel_in_prefixedLabel198 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REM_in_specialLabel212 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_text_in_specialLabel214 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PAR_in_specialLabel222 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_LABEL_in_specialLabel224 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_INT_in_specialLabel233 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_number_in_specialLabel236 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IDENT_in_specialLabel240 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_REAL_in_specialLabel250 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rnumber_in_specialLabel253 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IDENT_in_specialLabel257 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_in_specialLabel267 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DQUOTE_in_specialLabel271 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_text_in_specialLabel273 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IDENT_in_specialLabel278 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BOOL_in_specialLabel288 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_bool_in_specialLabel291 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IDENT_in_specialLabel295 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ATTR_in_specialLabel304 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PROD_in_specialLabel310 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARG_in_specialLabel317 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NUMBER_in_specialLabel319 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_bool0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NUMBER_in_number352 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NUMBER_in_rnumber366 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_DOT_in_rnumber369 = new BitSet(new long[]{0x0800000000000002L});
    public static final BitSet FOLLOW_NUMER_in_rnumber371 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_rnumber380 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_NUMBER_in_rnumber382 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLING_in_actualLabel396 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_regExpr_in_actualLabel398 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_regExpr_in_actualLabel405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BAR_in_regExpr419 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_regExpr_in_regExpr421 = new BitSet(new long[]{0x00003E6044001DF0L});
    public static final BitSet FOLLOW_regExpr_in_regExpr423 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOT_in_regExpr431 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_regExpr_in_regExpr433 = new BitSet(new long[]{0x00003E6044001DF0L});
    public static final BitSet FOLLOW_regExpr_in_regExpr435 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MINUS_in_regExpr443 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_regExpr_in_regExpr445 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STAR_in_regExpr453 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_regExpr_in_regExpr455 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_in_regExpr463 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_regExpr_in_regExpr465 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EQUALS_in_regExpr472 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUERY_in_regExpr479 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_regExpr481 = new BitSet(new long[]{0x00023E6044001DF8L});
    public static final BitSet FOLLOW_HAT_in_regExpr484 = new BitSet(new long[]{0x00003E6044001DF8L});
    public static final BitSet FOLLOW_atom_in_regExpr487 = new BitSet(new long[]{0x00003E6044001DF8L});
    public static final BitSet FOLLOW_atom_in_regExpr495 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ATOM_in_atom509 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_TYPE_in_atom513 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_atom515 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FLAG_in_atom519 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_atom521 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_text_in_atom526 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_tokenseq_in_text543 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_tokenseq560 = new BitSet(new long[]{0x0FFFFFFFFFFFFFF2L});

}
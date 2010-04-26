// $ANTLR 3.2 Sep 23, 2009 12:02:23 Label0Checker.g 2010-04-26 22:28:42

package groove.view.parse;
import java.util.List;
import java.util.LinkedList;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


public class Label0Checker extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "NEW", "DEL", "NOT", "USE", "CNEW", "REM", "FORALL", "FORALLX", "EXISTS", "NESTED", "INT", "REAL", "STRING", "BOOL", "ATTR", "PROD", "ARG", "PAR", "TYPE", "FLAG", "PATH", "EMPTY", "ATOM", "TRUE", "FALSE", "CONSTRAINT", "MINUS", "STAR", "PLUS", "DOT", "BAR", "HAT", "EQUALS", "LBRACE", "RBRACE", "LPAR", "RPAR", "LSQUARE", "RSQUARE", "PLING", "QUERY", "COLON", "COMMA", "SQUOTE", "DQUOTE", "DOLLAR", "UNDER", "BSLASH", "IDENT", "LABEL", "NUMBER", "LETTER", "IDENTCHAR", "DIGIT", "'\n'", "'\\n'"
    };
    public static final int DOLLAR=49;
    public static final int STAR=31;
    public static final int FORALLX=11;
    public static final int LSQUARE=41;
    public static final int LETTER=55;
    public static final int DEL=5;
    public static final int LBRACE=37;
    public static final int NEW=4;
    public static final int DQUOTE=48;
    public static final int IDENTCHAR=56;
    public static final int EQUALS=36;
    public static final int NOT=6;
    public static final int ATOM=26;
    public static final int EOF=-1;
    public static final int TYPE=22;
    public static final int HAT=35;
    public static final int UNDER=50;
    public static final int T__58=58;
    public static final int PLING=43;
    public static final int ARG=20;
    public static final int LPAR=39;
    public static final int COMMA=46;
    public static final int PATH=24;
    public static final int T__59=59;
    public static final int PROD=19;
    public static final int IDENT=52;
    public static final int PAR=21;
    public static final int PLUS=32;
    public static final int DIGIT=57;
    public static final int EXISTS=12;
    public static final int DOT=33;
    public static final int ATTR=18;
    public static final int RBRACE=38;
    public static final int NUMBER=54;
    public static final int BOOL=17;
    public static final int FORALL=10;
    public static final int INT=14;
    public static final int SQUOTE=47;
    public static final int REM=9;
    public static final int MINUS=30;
    public static final int RSQUARE=42;
    public static final int TRUE=27;
    public static final int CNEW=8;
    public static final int FLAG=23;
    public static final int EMPTY=25;
    public static final int NESTED=13;
    public static final int COLON=45;
    public static final int REAL=15;
    public static final int LABEL=53;
    public static final int QUERY=44;
    public static final int RPAR=40;
    public static final int USE=7;
    public static final int FALSE=28;
    public static final int CONSTRAINT=29;
    public static final int BSLASH=51;
    public static final int BAR=34;
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

    // $ANTLR start "label"
    // Label0Checker.g:42:1: label : ( quantLabel | roleLabel | specialLabel );
    public final Label0Checker.label_return label() throws RecognitionException {
        Label0Checker.label_return retval = new Label0Checker.label_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        Label0Checker.quantLabel_return quantLabel1 = null;

        Label0Checker.roleLabel_return roleLabel2 = null;

        Label0Checker.specialLabel_return specialLabel3 = null;



        try {
            // Label0Checker.g:43:3: ( quantLabel | roleLabel | specialLabel )
            int alt1=3;
            switch ( input.LA(1) ) {
            case FORALL:
            case FORALLX:
            case EXISTS:
                {
                alt1=1;
                }
                break;
            case NEW:
            case DEL:
            case NOT:
            case USE:
            case CNEW:
            case ATOM:
            case MINUS:
            case STAR:
            case PLUS:
            case DOT:
            case BAR:
            case EQUALS:
            case PLING:
            case QUERY:
                {
                alt1=2;
                }
                break;
            case REM:
            case INT:
            case REAL:
            case STRING:
            case BOOL:
            case ATTR:
            case PROD:
            case ARG:
            case PAR:
                {
                alt1=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // Label0Checker.g:43:5: quantLabel
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_quantLabel_in_label67);
                    quantLabel1=quantLabel();

                    state._fsp--;

                    adaptor.addChild(root_0, quantLabel1.getTree());

                    }
                    break;
                case 2 :
                    // Label0Checker.g:44:5: roleLabel
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_roleLabel_in_label73);
                    roleLabel2=roleLabel();

                    state._fsp--;

                    adaptor.addChild(root_0, roleLabel2.getTree());

                    }
                    break;
                case 3 :
                    // Label0Checker.g:45:5: specialLabel
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_specialLabel_in_label79);
                    specialLabel3=specialLabel();

                    state._fsp--;

                    adaptor.addChild(root_0, specialLabel3.getTree());

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
    // $ANTLR end "label"

    public static class quantLabel_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "quantLabel"
    // Label0Checker.g:48:1: quantLabel : ^( quantPrefix ( IDENT )? ) ;
    public final Label0Checker.quantLabel_return quantLabel() throws RecognitionException {
        Label0Checker.quantLabel_return retval = new Label0Checker.quantLabel_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree IDENT5=null;
        Label0Checker.quantPrefix_return quantPrefix4 = null;


        CommonTree IDENT5_tree=null;

        try {
            // Label0Checker.g:49:3: ( ^( quantPrefix ( IDENT )? ) )
            // Label0Checker.g:49:5: ^( quantPrefix ( IDENT )? )
            {
            root_0 = (CommonTree)adaptor.nil();

            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
            pushFollow(FOLLOW_quantPrefix_in_quantLabel93);
            quantPrefix4=quantPrefix();

            state._fsp--;

            root_1 = (CommonTree)adaptor.becomeRoot(quantPrefix4.getTree(), root_1);


            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // Label0Checker.g:49:19: ( IDENT )?
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==IDENT) ) {
                    alt2=1;
                }
                switch (alt2) {
                    case 1 :
                        // Label0Checker.g:49:19: IDENT
                        {
                        _last = (CommonTree)input.LT(1);
                        IDENT5=(CommonTree)match(input,IDENT,FOLLOW_IDENT_in_quantLabel95); 
                        IDENT5_tree = (CommonTree)adaptor.dupNode(IDENT5);

                        adaptor.addChild(root_1, IDENT5_tree);


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
    // $ANTLR end "quantLabel"

    public static class quantPrefix_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "quantPrefix"
    // Label0Checker.g:52:1: quantPrefix : ( FORALL | FORALLX | EXISTS );
    public final Label0Checker.quantPrefix_return quantPrefix() throws RecognitionException {
        Label0Checker.quantPrefix_return retval = new Label0Checker.quantPrefix_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree set6=null;

        CommonTree set6_tree=null;

        try {
            // Label0Checker.g:53:3: ( FORALL | FORALLX | EXISTS )
            // Label0Checker.g:
            {
            root_0 = (CommonTree)adaptor.nil();

            _last = (CommonTree)input.LT(1);
            set6=(CommonTree)input.LT(1);
            if ( (input.LA(1)>=FORALL && input.LA(1)<=EXISTS) ) {
                input.consume();

                set6_tree = (CommonTree)adaptor.dupNode(set6);

                adaptor.addChild(root_0, set6_tree);

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
    // $ANTLR end "quantPrefix"

    public static class roleLabel_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "roleLabel"
    // Label0Checker.g:56:1: roleLabel : ( ^( rolePrefix ( ( IDENT )? actualLabel )? ) | actualLabel );
    public final Label0Checker.roleLabel_return roleLabel() throws RecognitionException {
        Label0Checker.roleLabel_return retval = new Label0Checker.roleLabel_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree IDENT8=null;
        Label0Checker.rolePrefix_return rolePrefix7 = null;

        Label0Checker.actualLabel_return actualLabel9 = null;

        Label0Checker.actualLabel_return actualLabel10 = null;


        CommonTree IDENT8_tree=null;

        try {
            // Label0Checker.g:57:3: ( ^( rolePrefix ( ( IDENT )? actualLabel )? ) | actualLabel )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( ((LA5_0>=NEW && LA5_0<=CNEW)) ) {
                alt5=1;
            }
            else if ( (LA5_0==ATOM||(LA5_0>=MINUS && LA5_0<=BAR)||LA5_0==EQUALS||(LA5_0>=PLING && LA5_0<=QUERY)) ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // Label0Checker.g:57:5: ^( rolePrefix ( ( IDENT )? actualLabel )? )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_rolePrefix_in_roleLabel132);
                    rolePrefix7=rolePrefix();

                    state._fsp--;

                    root_1 = (CommonTree)adaptor.becomeRoot(rolePrefix7.getTree(), root_1);


                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // Label0Checker.g:57:18: ( ( IDENT )? actualLabel )?
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( (LA4_0==ATOM||(LA4_0>=MINUS && LA4_0<=BAR)||LA4_0==EQUALS||(LA4_0>=PLING && LA4_0<=QUERY)||LA4_0==IDENT) ) {
                            alt4=1;
                        }
                        switch (alt4) {
                            case 1 :
                                // Label0Checker.g:57:19: ( IDENT )? actualLabel
                                {
                                // Label0Checker.g:57:19: ( IDENT )?
                                int alt3=2;
                                int LA3_0 = input.LA(1);

                                if ( (LA3_0==IDENT) ) {
                                    alt3=1;
                                }
                                switch (alt3) {
                                    case 1 :
                                        // Label0Checker.g:57:19: IDENT
                                        {
                                        _last = (CommonTree)input.LT(1);
                                        IDENT8=(CommonTree)match(input,IDENT,FOLLOW_IDENT_in_roleLabel135); 
                                        IDENT8_tree = (CommonTree)adaptor.dupNode(IDENT8);

                                        adaptor.addChild(root_1, IDENT8_tree);


                                        }
                                        break;

                                }

                                _last = (CommonTree)input.LT(1);
                                pushFollow(FOLLOW_actualLabel_in_roleLabel138);
                                actualLabel9=actualLabel();

                                state._fsp--;

                                adaptor.addChild(root_1, actualLabel9.getTree());

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 2 :
                    // Label0Checker.g:58:5: actualLabel
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_actualLabel_in_roleLabel147);
                    actualLabel10=actualLabel();

                    state._fsp--;

                    adaptor.addChild(root_0, actualLabel10.getTree());

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
    // $ANTLR end "roleLabel"

    public static class rolePrefix_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rolePrefix"
    // Label0Checker.g:61:1: rolePrefix : ( NEW | DEL | NOT | USE | CNEW );
    public final Label0Checker.rolePrefix_return rolePrefix() throws RecognitionException {
        Label0Checker.rolePrefix_return retval = new Label0Checker.rolePrefix_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree set11=null;

        CommonTree set11_tree=null;

        try {
            // Label0Checker.g:62:3: ( NEW | DEL | NOT | USE | CNEW )
            // Label0Checker.g:
            {
            root_0 = (CommonTree)adaptor.nil();

            _last = (CommonTree)input.LT(1);
            set11=(CommonTree)input.LT(1);
            if ( (input.LA(1)>=NEW && input.LA(1)<=CNEW) ) {
                input.consume();

                set11_tree = (CommonTree)adaptor.dupNode(set11);

                adaptor.addChild(root_0, set11_tree);

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
    // $ANTLR end "rolePrefix"

    public static class specialLabel_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "specialLabel"
    // Label0Checker.g:65:1: specialLabel : ( ^( REM text ) | ^( PAR ( LABEL )? ) | ^( INT ( NUMBER | IDENT )? ) | ^( REAL ( rnumber | IDENT )? ) | ^( STRING ( ^( DQUOTE text ) | IDENT )? ) | ^( BOOL ( TRUE | FALSE | IDENT )? ) | ATTR | PROD | ^( ARG NUMBER ) );
    public final Label0Checker.specialLabel_return specialLabel() throws RecognitionException {
        Label0Checker.specialLabel_return retval = new Label0Checker.specialLabel_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree REM12=null;
        CommonTree PAR14=null;
        CommonTree LABEL15=null;
        CommonTree INT16=null;
        CommonTree set17=null;
        CommonTree REAL18=null;
        CommonTree IDENT20=null;
        CommonTree STRING21=null;
        CommonTree DQUOTE22=null;
        CommonTree IDENT24=null;
        CommonTree BOOL25=null;
        CommonTree set26=null;
        CommonTree ATTR27=null;
        CommonTree PROD28=null;
        CommonTree ARG29=null;
        CommonTree NUMBER30=null;
        Label0Checker.text_return text13 = null;

        Label0Checker.rnumber_return rnumber19 = null;

        Label0Checker.text_return text23 = null;


        CommonTree REM12_tree=null;
        CommonTree PAR14_tree=null;
        CommonTree LABEL15_tree=null;
        CommonTree INT16_tree=null;
        CommonTree set17_tree=null;
        CommonTree REAL18_tree=null;
        CommonTree IDENT20_tree=null;
        CommonTree STRING21_tree=null;
        CommonTree DQUOTE22_tree=null;
        CommonTree IDENT24_tree=null;
        CommonTree BOOL25_tree=null;
        CommonTree set26_tree=null;
        CommonTree ATTR27_tree=null;
        CommonTree PROD28_tree=null;
        CommonTree ARG29_tree=null;
        CommonTree NUMBER30_tree=null;

        try {
            // Label0Checker.g:66:3: ( ^( REM text ) | ^( PAR ( LABEL )? ) | ^( INT ( NUMBER | IDENT )? ) | ^( REAL ( rnumber | IDENT )? ) | ^( STRING ( ^( DQUOTE text ) | IDENT )? ) | ^( BOOL ( TRUE | FALSE | IDENT )? ) | ATTR | PROD | ^( ARG NUMBER ) )
            int alt11=9;
            switch ( input.LA(1) ) {
            case REM:
                {
                alt11=1;
                }
                break;
            case PAR:
                {
                alt11=2;
                }
                break;
            case INT:
                {
                alt11=3;
                }
                break;
            case REAL:
                {
                alt11=4;
                }
                break;
            case STRING:
                {
                alt11=5;
                }
                break;
            case BOOL:
                {
                alt11=6;
                }
                break;
            case ATTR:
                {
                alt11=7;
                }
                break;
            case PROD:
                {
                alt11=8;
                }
                break;
            case ARG:
                {
                alt11=9;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }

            switch (alt11) {
                case 1 :
                    // Label0Checker.g:66:5: ^( REM text )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    REM12=(CommonTree)match(input,REM,FOLLOW_REM_in_specialLabel190); 
                    REM12_tree = (CommonTree)adaptor.dupNode(REM12);

                    root_1 = (CommonTree)adaptor.becomeRoot(REM12_tree, root_1);



                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        _last = (CommonTree)input.LT(1);
                        pushFollow(FOLLOW_text_in_specialLabel192);
                        text13=text();

                        state._fsp--;

                        adaptor.addChild(root_1, text13.getTree());

                        match(input, Token.UP, null); 
                    }adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 2 :
                    // Label0Checker.g:67:5: ^( PAR ( LABEL )? )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    PAR14=(CommonTree)match(input,PAR,FOLLOW_PAR_in_specialLabel200); 
                    PAR14_tree = (CommonTree)adaptor.dupNode(PAR14);

                    root_1 = (CommonTree)adaptor.becomeRoot(PAR14_tree, root_1);



                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // Label0Checker.g:67:11: ( LABEL )?
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==LABEL) ) {
                            alt6=1;
                        }
                        switch (alt6) {
                            case 1 :
                                // Label0Checker.g:67:11: LABEL
                                {
                                _last = (CommonTree)input.LT(1);
                                LABEL15=(CommonTree)match(input,LABEL,FOLLOW_LABEL_in_specialLabel202); 
                                LABEL15_tree = (CommonTree)adaptor.dupNode(LABEL15);

                                adaptor.addChild(root_1, LABEL15_tree);


                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 3 :
                    // Label0Checker.g:68:5: ^( INT ( NUMBER | IDENT )? )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    INT16=(CommonTree)match(input,INT,FOLLOW_INT_in_specialLabel211); 
                    INT16_tree = (CommonTree)adaptor.dupNode(INT16);

                    root_1 = (CommonTree)adaptor.becomeRoot(INT16_tree, root_1);



                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // Label0Checker.g:68:11: ( NUMBER | IDENT )?
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==IDENT||LA7_0==NUMBER) ) {
                            alt7=1;
                        }
                        switch (alt7) {
                            case 1 :
                                // Label0Checker.g:
                                {
                                _last = (CommonTree)input.LT(1);
                                set17=(CommonTree)input.LT(1);
                                if ( input.LA(1)==IDENT||input.LA(1)==NUMBER ) {
                                    input.consume();

                                    set17_tree = (CommonTree)adaptor.dupNode(set17);

                                    adaptor.addChild(root_1, set17_tree);

                                    state.errorRecovery=false;
                                }
                                else {
                                    MismatchedSetException mse = new MismatchedSetException(null,input);
                                    throw mse;
                                }


                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 4 :
                    // Label0Checker.g:69:5: ^( REAL ( rnumber | IDENT )? )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    REAL18=(CommonTree)match(input,REAL,FOLLOW_REAL_in_specialLabel228); 
                    REAL18_tree = (CommonTree)adaptor.dupNode(REAL18);

                    root_1 = (CommonTree)adaptor.becomeRoot(REAL18_tree, root_1);



                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // Label0Checker.g:69:12: ( rnumber | IDENT )?
                        int alt8=3;
                        int LA8_0 = input.LA(1);

                        if ( (LA8_0==DOT||LA8_0==NUMBER) ) {
                            alt8=1;
                        }
                        else if ( (LA8_0==IDENT) ) {
                            alt8=2;
                        }
                        switch (alt8) {
                            case 1 :
                                // Label0Checker.g:69:13: rnumber
                                {
                                _last = (CommonTree)input.LT(1);
                                pushFollow(FOLLOW_rnumber_in_specialLabel231);
                                rnumber19=rnumber();

                                state._fsp--;

                                adaptor.addChild(root_1, rnumber19.getTree());

                                }
                                break;
                            case 2 :
                                // Label0Checker.g:69:23: IDENT
                                {
                                _last = (CommonTree)input.LT(1);
                                IDENT20=(CommonTree)match(input,IDENT,FOLLOW_IDENT_in_specialLabel235); 
                                IDENT20_tree = (CommonTree)adaptor.dupNode(IDENT20);

                                adaptor.addChild(root_1, IDENT20_tree);


                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 5 :
                    // Label0Checker.g:70:5: ^( STRING ( ^( DQUOTE text ) | IDENT )? )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    STRING21=(CommonTree)match(input,STRING,FOLLOW_STRING_in_specialLabel245); 
                    STRING21_tree = (CommonTree)adaptor.dupNode(STRING21);

                    root_1 = (CommonTree)adaptor.becomeRoot(STRING21_tree, root_1);



                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // Label0Checker.g:70:14: ( ^( DQUOTE text ) | IDENT )?
                        int alt9=3;
                        int LA9_0 = input.LA(1);

                        if ( (LA9_0==DQUOTE) ) {
                            alt9=1;
                        }
                        else if ( (LA9_0==IDENT) ) {
                            alt9=2;
                        }
                        switch (alt9) {
                            case 1 :
                                // Label0Checker.g:70:15: ^( DQUOTE text )
                                {
                                _last = (CommonTree)input.LT(1);
                                {
                                CommonTree _save_last_2 = _last;
                                CommonTree _first_2 = null;
                                CommonTree root_2 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                                DQUOTE22=(CommonTree)match(input,DQUOTE,FOLLOW_DQUOTE_in_specialLabel249); 
                                DQUOTE22_tree = (CommonTree)adaptor.dupNode(DQUOTE22);

                                root_2 = (CommonTree)adaptor.becomeRoot(DQUOTE22_tree, root_2);



                                if ( input.LA(1)==Token.DOWN ) {
                                    match(input, Token.DOWN, null); 
                                    _last = (CommonTree)input.LT(1);
                                    pushFollow(FOLLOW_text_in_specialLabel251);
                                    text23=text();

                                    state._fsp--;

                                    adaptor.addChild(root_2, text23.getTree());

                                    match(input, Token.UP, null); 
                                }adaptor.addChild(root_1, root_2);_last = _save_last_2;
                                }


                                }
                                break;
                            case 2 :
                                // Label0Checker.g:70:32: IDENT
                                {
                                _last = (CommonTree)input.LT(1);
                                IDENT24=(CommonTree)match(input,IDENT,FOLLOW_IDENT_in_specialLabel256); 
                                IDENT24_tree = (CommonTree)adaptor.dupNode(IDENT24);

                                adaptor.addChild(root_1, IDENT24_tree);


                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 6 :
                    // Label0Checker.g:71:5: ^( BOOL ( TRUE | FALSE | IDENT )? )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    BOOL25=(CommonTree)match(input,BOOL,FOLLOW_BOOL_in_specialLabel266); 
                    BOOL25_tree = (CommonTree)adaptor.dupNode(BOOL25);

                    root_1 = (CommonTree)adaptor.becomeRoot(BOOL25_tree, root_1);



                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // Label0Checker.g:71:12: ( TRUE | FALSE | IDENT )?
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( ((LA10_0>=TRUE && LA10_0<=FALSE)||LA10_0==IDENT) ) {
                            alt10=1;
                        }
                        switch (alt10) {
                            case 1 :
                                // Label0Checker.g:
                                {
                                _last = (CommonTree)input.LT(1);
                                set26=(CommonTree)input.LT(1);
                                if ( (input.LA(1)>=TRUE && input.LA(1)<=FALSE)||input.LA(1)==IDENT ) {
                                    input.consume();

                                    set26_tree = (CommonTree)adaptor.dupNode(set26);

                                    adaptor.addChild(root_1, set26_tree);

                                    state.errorRecovery=false;
                                }
                                else {
                                    MismatchedSetException mse = new MismatchedSetException(null,input);
                                    throw mse;
                                }


                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 7 :
                    // Label0Checker.g:72:5: ATTR
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    ATTR27=(CommonTree)match(input,ATTR,FOLLOW_ATTR_in_specialLabel286); 
                    ATTR27_tree = (CommonTree)adaptor.dupNode(ATTR27);

                    adaptor.addChild(root_0, ATTR27_tree);


                    }
                    break;
                case 8 :
                    // Label0Checker.g:73:5: PROD
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    PROD28=(CommonTree)match(input,PROD,FOLLOW_PROD_in_specialLabel292); 
                    PROD28_tree = (CommonTree)adaptor.dupNode(PROD28);

                    adaptor.addChild(root_0, PROD28_tree);


                    }
                    break;
                case 9 :
                    // Label0Checker.g:74:5: ^( ARG NUMBER )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    ARG29=(CommonTree)match(input,ARG,FOLLOW_ARG_in_specialLabel299); 
                    ARG29_tree = (CommonTree)adaptor.dupNode(ARG29);

                    root_1 = (CommonTree)adaptor.becomeRoot(ARG29_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    NUMBER30=(CommonTree)match(input,NUMBER,FOLLOW_NUMBER_in_specialLabel301); 
                    NUMBER30_tree = (CommonTree)adaptor.dupNode(NUMBER30);

                    adaptor.addChild(root_1, NUMBER30_tree);


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
    // $ANTLR end "specialLabel"

    public static class rnumber_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rnumber"
    // Label0Checker.g:77:1: rnumber : ( NUMBER ( DOT ( NUMBER )? )? | DOT NUMBER );
    public final Label0Checker.rnumber_return rnumber() throws RecognitionException {
        Label0Checker.rnumber_return retval = new Label0Checker.rnumber_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree NUMBER31=null;
        CommonTree DOT32=null;
        CommonTree NUMBER33=null;
        CommonTree DOT34=null;
        CommonTree NUMBER35=null;

        CommonTree NUMBER31_tree=null;
        CommonTree DOT32_tree=null;
        CommonTree NUMBER33_tree=null;
        CommonTree DOT34_tree=null;
        CommonTree NUMBER35_tree=null;

        try {
            // Label0Checker.g:78:3: ( NUMBER ( DOT ( NUMBER )? )? | DOT NUMBER )
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==NUMBER) ) {
                alt14=1;
            }
            else if ( (LA14_0==DOT) ) {
                alt14=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // Label0Checker.g:78:5: NUMBER ( DOT ( NUMBER )? )?
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    NUMBER31=(CommonTree)match(input,NUMBER,FOLLOW_NUMBER_in_rnumber315); 
                    NUMBER31_tree = (CommonTree)adaptor.dupNode(NUMBER31);

                    adaptor.addChild(root_0, NUMBER31_tree);

                    // Label0Checker.g:78:12: ( DOT ( NUMBER )? )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0==DOT) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // Label0Checker.g:78:13: DOT ( NUMBER )?
                            {
                            _last = (CommonTree)input.LT(1);
                            DOT32=(CommonTree)match(input,DOT,FOLLOW_DOT_in_rnumber318); 
                            DOT32_tree = (CommonTree)adaptor.dupNode(DOT32);

                            adaptor.addChild(root_0, DOT32_tree);

                            // Label0Checker.g:78:17: ( NUMBER )?
                            int alt12=2;
                            int LA12_0 = input.LA(1);

                            if ( (LA12_0==NUMBER) ) {
                                alt12=1;
                            }
                            switch (alt12) {
                                case 1 :
                                    // Label0Checker.g:78:17: NUMBER
                                    {
                                    _last = (CommonTree)input.LT(1);
                                    NUMBER33=(CommonTree)match(input,NUMBER,FOLLOW_NUMBER_in_rnumber320); 
                                    NUMBER33_tree = (CommonTree)adaptor.dupNode(NUMBER33);

                                    adaptor.addChild(root_0, NUMBER33_tree);


                                    }
                                    break;

                            }


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // Label0Checker.g:79:5: DOT NUMBER
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    DOT34=(CommonTree)match(input,DOT,FOLLOW_DOT_in_rnumber329); 
                    DOT34_tree = (CommonTree)adaptor.dupNode(DOT34);

                    adaptor.addChild(root_0, DOT34_tree);

                    _last = (CommonTree)input.LT(1);
                    NUMBER35=(CommonTree)match(input,NUMBER,FOLLOW_NUMBER_in_rnumber331); 
                    NUMBER35_tree = (CommonTree)adaptor.dupNode(NUMBER35);

                    adaptor.addChild(root_0, NUMBER35_tree);


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
    // $ANTLR end "rnumber"

    public static class actualLabel_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "actualLabel"
    // Label0Checker.g:82:1: actualLabel : ( ^( PLING regExpr ) | regExpr );
    public final Label0Checker.actualLabel_return actualLabel() throws RecognitionException {
        Label0Checker.actualLabel_return retval = new Label0Checker.actualLabel_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree PLING36=null;
        Label0Checker.regExpr_return regExpr37 = null;

        Label0Checker.regExpr_return regExpr38 = null;


        CommonTree PLING36_tree=null;

        try {
            // Label0Checker.g:83:3: ( ^( PLING regExpr ) | regExpr )
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==PLING) ) {
                alt15=1;
            }
            else if ( (LA15_0==ATOM||(LA15_0>=MINUS && LA15_0<=BAR)||LA15_0==EQUALS||LA15_0==QUERY) ) {
                alt15=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;
            }
            switch (alt15) {
                case 1 :
                    // Label0Checker.g:83:5: ^( PLING regExpr )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    PLING36=(CommonTree)match(input,PLING,FOLLOW_PLING_in_actualLabel345); 
                    PLING36_tree = (CommonTree)adaptor.dupNode(PLING36);

                    root_1 = (CommonTree)adaptor.becomeRoot(PLING36_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_regExpr_in_actualLabel347);
                    regExpr37=regExpr();

                    state._fsp--;

                    adaptor.addChild(root_1, regExpr37.getTree());

                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 2 :
                    // Label0Checker.g:84:5: regExpr
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_regExpr_in_actualLabel354);
                    regExpr38=regExpr();

                    state._fsp--;

                    adaptor.addChild(root_0, regExpr38.getTree());

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
    // $ANTLR end "actualLabel"

    public static class regExpr_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "regExpr"
    // Label0Checker.g:87:1: regExpr : ( ^( BAR regExpr regExpr ) | ^( DOT regExpr regExpr ) | ^( MINUS regExpr ) | ^( STAR regExpr ) | ^( PLUS regExpr ) | EQUALS | ^( QUERY ( IDENT )? ( HAT )? ( atom )* ) | atom );
    public final Label0Checker.regExpr_return regExpr() throws RecognitionException {
        Label0Checker.regExpr_return retval = new Label0Checker.regExpr_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree BAR39=null;
        CommonTree DOT42=null;
        CommonTree MINUS45=null;
        CommonTree STAR47=null;
        CommonTree PLUS49=null;
        CommonTree EQUALS51=null;
        CommonTree QUERY52=null;
        CommonTree IDENT53=null;
        CommonTree HAT54=null;
        Label0Checker.regExpr_return regExpr40 = null;

        Label0Checker.regExpr_return regExpr41 = null;

        Label0Checker.regExpr_return regExpr43 = null;

        Label0Checker.regExpr_return regExpr44 = null;

        Label0Checker.regExpr_return regExpr46 = null;

        Label0Checker.regExpr_return regExpr48 = null;

        Label0Checker.regExpr_return regExpr50 = null;

        Label0Checker.atom_return atom55 = null;

        Label0Checker.atom_return atom56 = null;


        CommonTree BAR39_tree=null;
        CommonTree DOT42_tree=null;
        CommonTree MINUS45_tree=null;
        CommonTree STAR47_tree=null;
        CommonTree PLUS49_tree=null;
        CommonTree EQUALS51_tree=null;
        CommonTree QUERY52_tree=null;
        CommonTree IDENT53_tree=null;
        CommonTree HAT54_tree=null;

        try {
            // Label0Checker.g:88:3: ( ^( BAR regExpr regExpr ) | ^( DOT regExpr regExpr ) | ^( MINUS regExpr ) | ^( STAR regExpr ) | ^( PLUS regExpr ) | EQUALS | ^( QUERY ( IDENT )? ( HAT )? ( atom )* ) | atom )
            int alt19=8;
            switch ( input.LA(1) ) {
            case BAR:
                {
                alt19=1;
                }
                break;
            case DOT:
                {
                alt19=2;
                }
                break;
            case MINUS:
                {
                alt19=3;
                }
                break;
            case STAR:
                {
                alt19=4;
                }
                break;
            case PLUS:
                {
                alt19=5;
                }
                break;
            case EQUALS:
                {
                alt19=6;
                }
                break;
            case QUERY:
                {
                alt19=7;
                }
                break;
            case ATOM:
                {
                alt19=8;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 19, 0, input);

                throw nvae;
            }

            switch (alt19) {
                case 1 :
                    // Label0Checker.g:88:5: ^( BAR regExpr regExpr )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    BAR39=(CommonTree)match(input,BAR,FOLLOW_BAR_in_regExpr368); 
                    BAR39_tree = (CommonTree)adaptor.dupNode(BAR39);

                    root_1 = (CommonTree)adaptor.becomeRoot(BAR39_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_regExpr_in_regExpr370);
                    regExpr40=regExpr();

                    state._fsp--;

                    adaptor.addChild(root_1, regExpr40.getTree());
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_regExpr_in_regExpr372);
                    regExpr41=regExpr();

                    state._fsp--;

                    adaptor.addChild(root_1, regExpr41.getTree());

                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 2 :
                    // Label0Checker.g:89:5: ^( DOT regExpr regExpr )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    DOT42=(CommonTree)match(input,DOT,FOLLOW_DOT_in_regExpr380); 
                    DOT42_tree = (CommonTree)adaptor.dupNode(DOT42);

                    root_1 = (CommonTree)adaptor.becomeRoot(DOT42_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_regExpr_in_regExpr382);
                    regExpr43=regExpr();

                    state._fsp--;

                    adaptor.addChild(root_1, regExpr43.getTree());
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_regExpr_in_regExpr384);
                    regExpr44=regExpr();

                    state._fsp--;

                    adaptor.addChild(root_1, regExpr44.getTree());

                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 3 :
                    // Label0Checker.g:90:5: ^( MINUS regExpr )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    MINUS45=(CommonTree)match(input,MINUS,FOLLOW_MINUS_in_regExpr392); 
                    MINUS45_tree = (CommonTree)adaptor.dupNode(MINUS45);

                    root_1 = (CommonTree)adaptor.becomeRoot(MINUS45_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_regExpr_in_regExpr394);
                    regExpr46=regExpr();

                    state._fsp--;

                    adaptor.addChild(root_1, regExpr46.getTree());

                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 4 :
                    // Label0Checker.g:91:5: ^( STAR regExpr )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    STAR47=(CommonTree)match(input,STAR,FOLLOW_STAR_in_regExpr402); 
                    STAR47_tree = (CommonTree)adaptor.dupNode(STAR47);

                    root_1 = (CommonTree)adaptor.becomeRoot(STAR47_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_regExpr_in_regExpr404);
                    regExpr48=regExpr();

                    state._fsp--;

                    adaptor.addChild(root_1, regExpr48.getTree());

                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 5 :
                    // Label0Checker.g:92:5: ^( PLUS regExpr )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    PLUS49=(CommonTree)match(input,PLUS,FOLLOW_PLUS_in_regExpr412); 
                    PLUS49_tree = (CommonTree)adaptor.dupNode(PLUS49);

                    root_1 = (CommonTree)adaptor.becomeRoot(PLUS49_tree, root_1);



                    match(input, Token.DOWN, null); 
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_regExpr_in_regExpr414);
                    regExpr50=regExpr();

                    state._fsp--;

                    adaptor.addChild(root_1, regExpr50.getTree());

                    match(input, Token.UP, null); adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 6 :
                    // Label0Checker.g:93:5: EQUALS
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    EQUALS51=(CommonTree)match(input,EQUALS,FOLLOW_EQUALS_in_regExpr421); 
                    EQUALS51_tree = (CommonTree)adaptor.dupNode(EQUALS51);

                    adaptor.addChild(root_0, EQUALS51_tree);


                    }
                    break;
                case 7 :
                    // Label0Checker.g:94:5: ^( QUERY ( IDENT )? ( HAT )? ( atom )* )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                    QUERY52=(CommonTree)match(input,QUERY,FOLLOW_QUERY_in_regExpr428); 
                    QUERY52_tree = (CommonTree)adaptor.dupNode(QUERY52);

                    root_1 = (CommonTree)adaptor.becomeRoot(QUERY52_tree, root_1);



                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // Label0Checker.g:94:13: ( IDENT )?
                        int alt16=2;
                        int LA16_0 = input.LA(1);

                        if ( (LA16_0==IDENT) ) {
                            alt16=1;
                        }
                        switch (alt16) {
                            case 1 :
                                // Label0Checker.g:94:13: IDENT
                                {
                                _last = (CommonTree)input.LT(1);
                                IDENT53=(CommonTree)match(input,IDENT,FOLLOW_IDENT_in_regExpr430); 
                                IDENT53_tree = (CommonTree)adaptor.dupNode(IDENT53);

                                adaptor.addChild(root_1, IDENT53_tree);


                                }
                                break;

                        }

                        // Label0Checker.g:94:20: ( HAT )?
                        int alt17=2;
                        int LA17_0 = input.LA(1);

                        if ( (LA17_0==HAT) ) {
                            alt17=1;
                        }
                        switch (alt17) {
                            case 1 :
                                // Label0Checker.g:94:20: HAT
                                {
                                _last = (CommonTree)input.LT(1);
                                HAT54=(CommonTree)match(input,HAT,FOLLOW_HAT_in_regExpr433); 
                                HAT54_tree = (CommonTree)adaptor.dupNode(HAT54);

                                adaptor.addChild(root_1, HAT54_tree);


                                }
                                break;

                        }

                        // Label0Checker.g:94:25: ( atom )*
                        loop18:
                        do {
                            int alt18=2;
                            int LA18_0 = input.LA(1);

                            if ( (LA18_0==ATOM) ) {
                                alt18=1;
                            }


                            switch (alt18) {
                        	case 1 :
                        	    // Label0Checker.g:94:25: atom
                        	    {
                        	    _last = (CommonTree)input.LT(1);
                        	    pushFollow(FOLLOW_atom_in_regExpr436);
                        	    atom55=atom();

                        	    state._fsp--;

                        	    adaptor.addChild(root_1, atom55.getTree());

                        	    }
                        	    break;

                        	default :
                        	    break loop18;
                            }
                        } while (true);


                        match(input, Token.UP, null); 
                    }adaptor.addChild(root_0, root_1);_last = _save_last_1;
                    }


                    }
                    break;
                case 8 :
                    // Label0Checker.g:95:5: atom
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_atom_in_regExpr444);
                    atom56=atom();

                    state._fsp--;

                    adaptor.addChild(root_0, atom56.getTree());

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
    // $ANTLR end "regExpr"

    public static class atom_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "atom"
    // Label0Checker.g:98:1: atom : ^( ATOM ( ^( TYPE IDENT ) ^( FLAG IDENT ) | text ) ) ;
    public final Label0Checker.atom_return atom() throws RecognitionException {
        Label0Checker.atom_return retval = new Label0Checker.atom_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree ATOM57=null;
        CommonTree TYPE58=null;
        CommonTree IDENT59=null;
        CommonTree FLAG60=null;
        CommonTree IDENT61=null;
        Label0Checker.text_return text62 = null;


        CommonTree ATOM57_tree=null;
        CommonTree TYPE58_tree=null;
        CommonTree IDENT59_tree=null;
        CommonTree FLAG60_tree=null;
        CommonTree IDENT61_tree=null;

        try {
            // Label0Checker.g:99:3: ( ^( ATOM ( ^( TYPE IDENT ) ^( FLAG IDENT ) | text ) ) )
            // Label0Checker.g:99:5: ^( ATOM ( ^( TYPE IDENT ) ^( FLAG IDENT ) | text ) )
            {
            root_0 = (CommonTree)adaptor.nil();

            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            CommonTree root_1 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
            ATOM57=(CommonTree)match(input,ATOM,FOLLOW_ATOM_in_atom458); 
            ATOM57_tree = (CommonTree)adaptor.dupNode(ATOM57);

            root_1 = (CommonTree)adaptor.becomeRoot(ATOM57_tree, root_1);



            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // Label0Checker.g:99:12: ( ^( TYPE IDENT ) ^( FLAG IDENT ) | text )
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==TYPE) ) {
                    int LA20_1 = input.LA(2);

                    if ( (LA20_1==DOWN) ) {
                        alt20=1;
                    }
                    else if ( ((LA20_1>=UP && LA20_1<=58)) ) {
                        alt20=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 20, 1, input);

                        throw nvae;
                    }
                }
                else if ( ((LA20_0>=UP && LA20_0<=PAR)||(LA20_0>=FLAG && LA20_0<=58)) ) {
                    alt20=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 20, 0, input);

                    throw nvae;
                }
                switch (alt20) {
                    case 1 :
                        // Label0Checker.g:99:13: ^( TYPE IDENT ) ^( FLAG IDENT )
                        {
                        _last = (CommonTree)input.LT(1);
                        {
                        CommonTree _save_last_2 = _last;
                        CommonTree _first_2 = null;
                        CommonTree root_2 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                        TYPE58=(CommonTree)match(input,TYPE,FOLLOW_TYPE_in_atom462); 
                        TYPE58_tree = (CommonTree)adaptor.dupNode(TYPE58);

                        root_2 = (CommonTree)adaptor.becomeRoot(TYPE58_tree, root_2);



                        match(input, Token.DOWN, null); 
                        _last = (CommonTree)input.LT(1);
                        IDENT59=(CommonTree)match(input,IDENT,FOLLOW_IDENT_in_atom464); 
                        IDENT59_tree = (CommonTree)adaptor.dupNode(IDENT59);

                        adaptor.addChild(root_2, IDENT59_tree);


                        match(input, Token.UP, null); adaptor.addChild(root_1, root_2);_last = _save_last_2;
                        }

                        _last = (CommonTree)input.LT(1);
                        {
                        CommonTree _save_last_2 = _last;
                        CommonTree _first_2 = null;
                        CommonTree root_2 = (CommonTree)adaptor.nil();_last = (CommonTree)input.LT(1);
                        FLAG60=(CommonTree)match(input,FLAG,FOLLOW_FLAG_in_atom468); 
                        FLAG60_tree = (CommonTree)adaptor.dupNode(FLAG60);

                        root_2 = (CommonTree)adaptor.becomeRoot(FLAG60_tree, root_2);



                        match(input, Token.DOWN, null); 
                        _last = (CommonTree)input.LT(1);
                        IDENT61=(CommonTree)match(input,IDENT,FOLLOW_IDENT_in_atom470); 
                        IDENT61_tree = (CommonTree)adaptor.dupNode(IDENT61);

                        adaptor.addChild(root_2, IDENT61_tree);


                        match(input, Token.UP, null); adaptor.addChild(root_1, root_2);_last = _save_last_2;
                        }


                        }
                        break;
                    case 2 :
                        // Label0Checker.g:99:43: text
                        {
                        _last = (CommonTree)input.LT(1);
                        pushFollow(FOLLOW_text_in_atom475);
                        text62=text();

                        state._fsp--;

                        adaptor.addChild(root_1, text62.getTree());

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
    // $ANTLR end "atom"

    public static class text_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "text"
    // Label0Checker.g:102:1: text : tokenseq ->;
    public final Label0Checker.text_return text() throws RecognitionException {
        Label0Checker.text_return retval = new Label0Checker.text_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        Label0Checker.tokenseq_return tokenseq63 = null;


        RewriteRuleSubtreeStream stream_tokenseq=new RewriteRuleSubtreeStream(adaptor,"rule tokenseq");
        try {
            // Label0Checker.g:103:3: ( tokenseq ->)
            // Label0Checker.g:103:5: tokenseq
            {
            _last = (CommonTree)input.LT(1);
            pushFollow(FOLLOW_tokenseq_in_text492);
            tokenseq63=tokenseq();

            state._fsp--;

            stream_tokenseq.add(tokenseq63.getTree());


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 103:14: ->
            {
                adaptor.addChild(root_0,  new CommonTree(new CommonToken(IDENT, concat((tokenseq63!=null?((CommonTree)tokenseq63.tree):null)))) );

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
    // $ANTLR end "text"

    public static class tokenseq_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "tokenseq"
    // Label0Checker.g:106:1: tokenseq : (~ '\\n' )* ;
    public final Label0Checker.tokenseq_return tokenseq() throws RecognitionException {
        Label0Checker.tokenseq_return retval = new Label0Checker.tokenseq_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree set64=null;

        CommonTree set64_tree=null;

        try {
            // Label0Checker.g:107:3: ( (~ '\\n' )* )
            // Label0Checker.g:107:5: (~ '\\n' )*
            {
            root_0 = (CommonTree)adaptor.nil();

            // Label0Checker.g:107:5: (~ '\\n' )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( ((LA21_0>=NEW && LA21_0<=58)) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // Label0Checker.g:107:5: ~ '\\n'
            	    {
            	    _last = (CommonTree)input.LT(1);
            	    set64=(CommonTree)input.LT(1);
            	    if ( (input.LA(1)>=NEW && input.LA(1)<=58) ) {
            	        input.consume();

            	        set64_tree = (CommonTree)adaptor.dupNode(set64);

            	        adaptor.addChild(root_0, set64_tree);

            	        state.errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop21;
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
    // $ANTLR end "tokenseq"

    // Delegated rules


 

    public static final BitSet FOLLOW_quantLabel_in_label67 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_roleLabel_in_label73 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_specialLabel_in_label79 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_quantPrefix_in_quantLabel93 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_quantLabel95 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_quantPrefix0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rolePrefix_in_roleLabel132 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_roleLabel135 = new BitSet(new long[]{0x00001817C4000000L});
    public static final BitSet FOLLOW_actualLabel_in_roleLabel138 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_actualLabel_in_roleLabel147 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_rolePrefix0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REM_in_specialLabel190 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_text_in_specialLabel192 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PAR_in_specialLabel200 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_LABEL_in_specialLabel202 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_INT_in_specialLabel211 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_specialLabel213 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_REAL_in_specialLabel228 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rnumber_in_specialLabel231 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IDENT_in_specialLabel235 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_in_specialLabel245 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DQUOTE_in_specialLabel249 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_text_in_specialLabel251 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IDENT_in_specialLabel256 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BOOL_in_specialLabel266 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_specialLabel268 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ATTR_in_specialLabel286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PROD_in_specialLabel292 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARG_in_specialLabel299 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_NUMBER_in_specialLabel301 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NUMBER_in_rnumber315 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_DOT_in_rnumber318 = new BitSet(new long[]{0x0040000000000002L});
    public static final BitSet FOLLOW_NUMBER_in_rnumber320 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_rnumber329 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_NUMBER_in_rnumber331 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLING_in_actualLabel345 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_regExpr_in_actualLabel347 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_regExpr_in_actualLabel354 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BAR_in_regExpr368 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_regExpr_in_regExpr370 = new BitSet(new long[]{0x00001817C4000000L});
    public static final BitSet FOLLOW_regExpr_in_regExpr372 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOT_in_regExpr380 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_regExpr_in_regExpr382 = new BitSet(new long[]{0x00001817C4000000L});
    public static final BitSet FOLLOW_regExpr_in_regExpr384 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MINUS_in_regExpr392 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_regExpr_in_regExpr394 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STAR_in_regExpr402 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_regExpr_in_regExpr404 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_in_regExpr412 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_regExpr_in_regExpr414 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EQUALS_in_regExpr421 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUERY_in_regExpr428 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_regExpr430 = new BitSet(new long[]{0x0000181FC4000008L});
    public static final BitSet FOLLOW_HAT_in_regExpr433 = new BitSet(new long[]{0x00001817C4000008L});
    public static final BitSet FOLLOW_atom_in_regExpr436 = new BitSet(new long[]{0x00001817C4000008L});
    public static final BitSet FOLLOW_atom_in_regExpr444 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ATOM_in_atom458 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_TYPE_in_atom462 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_atom464 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FLAG_in_atom468 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_atom470 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_text_in_atom475 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_tokenseq_in_text492 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_tokenseq509 = new BitSet(new long[]{0x07FFFFFFFFFFFFF2L});

}
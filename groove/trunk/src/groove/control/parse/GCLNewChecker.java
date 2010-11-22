// $ANTLR 3.2 Sep 23, 2009 12:02:23 GCLNewChecker.g 2010-11-22 18:12:27

package groove.control.parse;
import groove.control.*;
import groove.trans.Rule;
import groove.trans.SPORule;
import java.util.LinkedList;
import java.util.Stack;
import java.util.HashSet;
import java.util.HashMap;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


public class GCLNewChecker extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "PROGRAM", "BLOCK", "FUNCTIONS", "FUNCTION", "CALL", "DO_WHILE", "DO_UNTIL", "VAR", "ARG", "LCURLY", "RCURLY", "ID", "LPAR", "RPAR", "ALAP", "WHILE", "UNTIL", "DO", "IF", "ELSE", "TRY", "CHOICE", "OR", "SEMI", "BAR", "TRUE", "PLUS", "STAR", "SHARP", "ANY", "OTHER", "DOT", "COMMA", "NODE", "BOOL", "STRING", "INT", "REAL", "OUT", "DONT_CARE", "FALSE", "STRING_LIT", "INT_LIT", "REAL_LIT", "IntegerNumber", "NonIntegerNumber", "QUOTE", "EscapeSequence", "BSLASH", "CR", "NL", "AMP", "NOT", "MINUS", "ML_COMMENT", "SL_COMMENT", "WS"
    };
    public static final int REAL_LIT=47;
    public static final int FUNCTION=7;
    public static final int DO_UNTIL=10;
    public static final int STAR=31;
    public static final int INT_LIT=46;
    public static final int FUNCTIONS=6;
    public static final int WHILE=19;
    public static final int IntegerNumber=48;
    public static final int STRING_LIT=45;
    public static final int AMP=55;
    public static final int DO=21;
    public static final int NOT=56;
    public static final int ALAP=18;
    public static final int ID=15;
    public static final int EOF=-1;
    public static final int IF=22;
    public static final int ML_COMMENT=58;
    public static final int QUOTE=50;
    public static final int LPAR=16;
    public static final int ARG=12;
    public static final int COMMA=36;
    public static final int NonIntegerNumber=49;
    public static final int DO_WHILE=9;
    public static final int PLUS=30;
    public static final int VAR=11;
    public static final int NL=54;
    public static final int DOT=35;
    public static final int CHOICE=25;
    public static final int SHARP=32;
    public static final int OTHER=34;
    public static final int NODE=37;
    public static final int ELSE=23;
    public static final int BOOL=38;
    public static final int LCURLY=13;
    public static final int INT=40;
    public static final int MINUS=57;
    public static final int SEMI=27;
    public static final int TRUE=29;
    public static final int TRY=24;
    public static final int REAL=41;
    public static final int DONT_CARE=43;
    public static final int ANY=33;
    public static final int WS=60;
    public static final int OUT=42;
    public static final int UNTIL=20;
    public static final int BLOCK=5;
    public static final int OR=26;
    public static final int RCURLY=14;
    public static final int SL_COMMENT=59;
    public static final int PROGRAM=4;
    public static final int RPAR=17;
    public static final int CALL=8;
    public static final int FALSE=44;
    public static final int CR=53;
    public static final int EscapeSequence=51;
    public static final int BSLASH=52;
    public static final int BAR=28;
    public static final int STRING=39;

    // delegates
    // delegators


        public GCLNewChecker(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public GCLNewChecker(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return GCLNewChecker.tokenNames; }
    public String getGrammarFileName() { return "GCLNewChecker.g"; }


        /** Helper class to convert AST trees to namespace. */
        private GCLHelper helper;
        /** Builder for control automata. */
        private CtrlFactory builder;
        
        public void displayRecognitionError(String[] tokenNames,
                                            RecognitionException e) {
            String hdr = getErrorHeader(e);
            String msg = getErrorMessage(e, tokenNames);
            helper.addError(hdr + " " + msg);
        }
        
        public List<String> getErrors() {
            return helper.getErrors();
        }

        /**
         * Runs the lexer and parser on a given input character stream,
         * with a (presumably empty) namespace.
         * @return the resulting syntax tree
         */
        public Tree run(MyTree tree, NamespaceNew namespace) throws RecognitionException {
            this.helper = new GCLHelper(this, namespace);
            this.builder = new CtrlFactory();
            MyTreeAdaptor treeAdaptor = new MyTreeAdaptor();
            setTreeAdaptor(treeAdaptor);
            setTreeNodeStream(treeAdaptor.createTreeNodeStream(tree));
            return (Tree) program().getTree();
        }


    public static class program_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "program"
    // GCLNewChecker.g:53:1: program : ^( PROGRAM functions block ) ;
    public final GCLNewChecker.program_return program() throws RecognitionException {
        GCLNewChecker.program_return retval = new GCLNewChecker.program_return();
        retval.start = input.LT(1);

        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree PROGRAM1=null;
        GCLNewChecker.functions_return functions2 = null;

        GCLNewChecker.block_return block3 = null;


        MyTree PROGRAM1_tree=null;

        try {
            // GCLNewChecker.g:54:3: ( ^( PROGRAM functions block ) )
            // GCLNewChecker.g:54:6: ^( PROGRAM functions block )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            PROGRAM1=(MyTree)match(input,PROGRAM,FOLLOW_PROGRAM_in_program57); 


            if ( _first_0==null ) _first_0 = PROGRAM1;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_functions_in_program59);
            functions2=functions();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = functions2.tree;
            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_block_in_program61);
            block3=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block3.tree;

            match(input, Token.UP, null); _last = _save_last_1;
            }


            retval.tree = (MyTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (MyTree)adaptor.getParent(retval.tree);
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "program"

    public static class functions_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "functions"
    // GCLNewChecker.g:57:1: functions : ^( FUNCTIONS ( function )* ) ;
    public final GCLNewChecker.functions_return functions() throws RecognitionException {
        GCLNewChecker.functions_return retval = new GCLNewChecker.functions_return();
        retval.start = input.LT(1);

        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree FUNCTIONS4=null;
        GCLNewChecker.function_return function5 = null;


        MyTree FUNCTIONS4_tree=null;

        try {
            // GCLNewChecker.g:58:3: ( ^( FUNCTIONS ( function )* ) )
            // GCLNewChecker.g:58:5: ^( FUNCTIONS ( function )* )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            FUNCTIONS4=(MyTree)match(input,FUNCTIONS,FOLLOW_FUNCTIONS_in_functions77); 


            if ( _first_0==null ) _first_0 = FUNCTIONS4;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // GCLNewChecker.g:58:17: ( function )*
                loop1:
                do {
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==FUNCTION) ) {
                        alt1=1;
                    }


                    switch (alt1) {
                	case 1 :
                	    // GCLNewChecker.g:58:17: function
                	    {
                	    _last = (MyTree)input.LT(1);
                	    pushFollow(FOLLOW_function_in_functions79);
                	    function5=function();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = function5.tree;

                	    retval.tree = (MyTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (MyTree)adaptor.getParent(retval.tree);
                	    }
                	    break;

                	default :
                	    break loop1;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }_last = _save_last_1;
            }


            retval.tree = (MyTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (MyTree)adaptor.getParent(retval.tree);
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "functions"

    public static class function_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "function"
    // GCLNewChecker.g:60:1: function : ^( FUNCTION ID block ) ;
    public final GCLNewChecker.function_return function() throws RecognitionException {
        GCLNewChecker.function_return retval = new GCLNewChecker.function_return();
        retval.start = input.LT(1);

        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree FUNCTION6=null;
        MyTree ID7=null;
        GCLNewChecker.block_return block8 = null;


        MyTree FUNCTION6_tree=null;
        MyTree ID7_tree=null;

        try {
            // GCLNewChecker.g:61:3: ( ^( FUNCTION ID block ) )
            // GCLNewChecker.g:61:5: ^( FUNCTION ID block )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            FUNCTION6=(MyTree)match(input,FUNCTION,FOLLOW_FUNCTION_in_function92); 


            if ( _first_0==null ) _first_0 = FUNCTION6;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            ID7=(MyTree)match(input,ID,FOLLOW_ID_in_function94); 
             
            if ( _first_1==null ) _first_1 = ID7;
            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_block_in_function96);
            block8=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block8.tree;

            match(input, Token.UP, null); _last = _save_last_1;
            }


            retval.tree = (MyTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (MyTree)adaptor.getParent(retval.tree);
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "function"

    public static class block_return extends TreeRuleReturnScope {
        public CtrlAut aut;
        MyTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "block"
    // GCLNewChecker.g:64:1: block returns [ CtrlAut aut ] : ^( BLOCK ( stat )* ) ;
    public final GCLNewChecker.block_return block() throws RecognitionException {
        GCLNewChecker.block_return retval = new GCLNewChecker.block_return();
        retval.start = input.LT(1);

        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree BLOCK9=null;
        GCLNewChecker.stat_return stat10 = null;


        MyTree BLOCK9_tree=null;

        try {
            // GCLNewChecker.g:65:3: ( ^( BLOCK ( stat )* ) )
            // GCLNewChecker.g:65:5: ^( BLOCK ( stat )* )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            BLOCK9=(MyTree)match(input,BLOCK,FOLLOW_BLOCK_in_block118); 


            if ( _first_0==null ) _first_0 = BLOCK9; retval.aut = builder.buildTrue();
                     helper.openScope(); 
                   

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // GCLNewChecker.g:69:8: ( stat )*
                loop2:
                do {
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( (LA2_0==BLOCK||LA2_0==CALL||LA2_0==VAR||(LA2_0>=ALAP && LA2_0<=UNTIL)||LA2_0==IF||(LA2_0>=TRY && LA2_0<=CHOICE)||LA2_0==TRUE||LA2_0==STAR||(LA2_0>=ANY && LA2_0<=OTHER)) ) {
                        alt2=1;
                    }


                    switch (alt2) {
                	case 1 :
                	    // GCLNewChecker.g:69:10: stat
                	    {
                	    _last = (MyTree)input.LT(1);
                	    pushFollow(FOLLOW_stat_in_block138);
                	    stat10=stat();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = stat10.tree;
                	     retval.aut = builder.buildSeq(retval.aut, (stat10!=null?stat10.aut:null)); 

                	    retval.tree = (MyTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (MyTree)adaptor.getParent(retval.tree);
                	    }
                	    break;

                	default :
                	    break loop2;
                    }
                } while (true);

                 helper.closeScope(); 

                match(input, Token.UP, null); 
            }_last = _save_last_1;
            }


            retval.tree = (MyTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (MyTree)adaptor.getParent(retval.tree);
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "block"

    public static class stat_return extends TreeRuleReturnScope {
        public CtrlAut aut;
        MyTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "stat"
    // GCLNewChecker.g:76:1: stat returns [ CtrlAut aut ] : ( block | var_decl | ^( ALAP s= stat ) | ^( WHILE c= stat s= stat ) | ^( UNTIL stat stat ) | ^( TRY s1= stat (s2= stat )? ) | ^( IF c= stat s1= stat (s2= stat )? ) | ^( CHOICE s1= stat (s2= stat )* ) | ^( STAR s= stat ) | rule | ANY | OTHER | TRUE );
    public final GCLNewChecker.stat_return stat() throws RecognitionException {
        GCLNewChecker.stat_return retval = new GCLNewChecker.stat_return();
        retval.start = input.LT(1);

        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree ALAP13=null;
        MyTree WHILE14=null;
        MyTree UNTIL15=null;
        MyTree TRY18=null;
        MyTree IF19=null;
        MyTree CHOICE20=null;
        MyTree STAR21=null;
        MyTree ANY23=null;
        MyTree OTHER24=null;
        MyTree TRUE25=null;
        GCLNewChecker.stat_return s = null;

        GCLNewChecker.stat_return c = null;

        GCLNewChecker.stat_return s1 = null;

        GCLNewChecker.stat_return s2 = null;

        GCLNewChecker.block_return block11 = null;

        GCLNewChecker.var_decl_return var_decl12 = null;

        GCLNewChecker.stat_return stat16 = null;

        GCLNewChecker.stat_return stat17 = null;

        GCLNewChecker.rule_return rule22 = null;


        MyTree ALAP13_tree=null;
        MyTree WHILE14_tree=null;
        MyTree UNTIL15_tree=null;
        MyTree TRY18_tree=null;
        MyTree IF19_tree=null;
        MyTree CHOICE20_tree=null;
        MyTree STAR21_tree=null;
        MyTree ANY23_tree=null;
        MyTree OTHER24_tree=null;
        MyTree TRUE25_tree=null;

        try {
            // GCLNewChecker.g:77:3: ( block | var_decl | ^( ALAP s= stat ) | ^( WHILE c= stat s= stat ) | ^( UNTIL stat stat ) | ^( TRY s1= stat (s2= stat )? ) | ^( IF c= stat s1= stat (s2= stat )? ) | ^( CHOICE s1= stat (s2= stat )* ) | ^( STAR s= stat ) | rule | ANY | OTHER | TRUE )
            int alt6=13;
            switch ( input.LA(1) ) {
            case BLOCK:
                {
                alt6=1;
                }
                break;
            case VAR:
                {
                alt6=2;
                }
                break;
            case ALAP:
                {
                alt6=3;
                }
                break;
            case WHILE:
                {
                alt6=4;
                }
                break;
            case UNTIL:
                {
                alt6=5;
                }
                break;
            case TRY:
                {
                alt6=6;
                }
                break;
            case IF:
                {
                alt6=7;
                }
                break;
            case CHOICE:
                {
                alt6=8;
                }
                break;
            case STAR:
                {
                alt6=9;
                }
                break;
            case CALL:
                {
                alt6=10;
                }
                break;
            case ANY:
                {
                alt6=11;
                }
                break;
            case OTHER:
                {
                alt6=12;
                }
                break;
            case TRUE:
                {
                alt6=13;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // GCLNewChecker.g:77:5: block
                    {
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_stat192);
                    block11=block();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = block11.tree;
                     retval.aut = (block11!=null?block11.aut:null); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 2 :
                    // GCLNewChecker.g:79:5: var_decl
                    {
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_var_decl_in_stat204);
                    var_decl12=var_decl();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = var_decl12.tree;
                     retval.aut = builder.buildTrue(); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 3 :
                    // GCLNewChecker.g:81:5: ^( ALAP s= stat )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    ALAP13=(MyTree)match(input,ALAP,FOLLOW_ALAP_in_stat217); 


                    if ( _first_0==null ) _first_0 = ALAP13;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat221);
                    s=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s.tree;

                    match(input, Token.UP, null); _last = _save_last_1;
                    }

                     retval.aut = builder.buildAlap((s!=null?s.aut:null)); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 4 :
                    // GCLNewChecker.g:83:5: ^( WHILE c= stat s= stat )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    WHILE14=(MyTree)match(input,WHILE,FOLLOW_WHILE_in_stat235); 


                    if ( _first_0==null ) _first_0 = WHILE14;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat239);
                    c=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = c.tree;
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat243);
                    s=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s.tree;

                    match(input, Token.UP, null); _last = _save_last_1;
                    }

                     retval.aut = builder.buildWhileDo((c!=null?c.aut:null), (s!=null?s.aut:null)); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 5 :
                    // GCLNewChecker.g:85:5: ^( UNTIL stat stat )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    UNTIL15=(MyTree)match(input,UNTIL,FOLLOW_UNTIL_in_stat257); 


                    if ( _first_0==null ) _first_0 = UNTIL15;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat259);
                    stat16=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat16.tree;
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat261);
                    stat17=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat17.tree;

                    match(input, Token.UP, null); _last = _save_last_1;
                    }

                     retval.aut = builder.buildUntilDo((c!=null?c.aut:null), (s!=null?s.aut:null)); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 6 :
                    // GCLNewChecker.g:87:5: ^( TRY s1= stat (s2= stat )? )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    TRY18=(MyTree)match(input,TRY,FOLLOW_TRY_in_stat275); 


                    if ( _first_0==null ) _first_0 = TRY18;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat279);
                    s1=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s1.tree;
                    // GCLNewChecker.g:87:19: (s2= stat )?
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==BLOCK||LA3_0==CALL||LA3_0==VAR||(LA3_0>=ALAP && LA3_0<=UNTIL)||LA3_0==IF||(LA3_0>=TRY && LA3_0<=CHOICE)||LA3_0==TRUE||LA3_0==STAR||(LA3_0>=ANY && LA3_0<=OTHER)) ) {
                        alt3=1;
                    }
                    switch (alt3) {
                        case 1 :
                            // GCLNewChecker.g:87:20: s2= stat
                            {
                            _last = (MyTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat284);
                            s2=stat();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = s2.tree;

                            retval.tree = (MyTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (MyTree)adaptor.getParent(retval.tree);
                            }
                            break;

                    }


                    match(input, Token.UP, null); _last = _save_last_1;
                    }

                     retval.aut = builder.buildTryElse((s1!=null?s1.aut:null), (s2!=null?s2.aut:null)); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 7 :
                    // GCLNewChecker.g:89:5: ^( IF c= stat s1= stat (s2= stat )? )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    IF19=(MyTree)match(input,IF,FOLLOW_IF_in_stat300); 


                    if ( _first_0==null ) _first_0 = IF19;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat304);
                    c=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = c.tree;
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat308);
                    s1=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s1.tree;
                    // GCLNewChecker.g:89:25: (s2= stat )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==BLOCK||LA4_0==CALL||LA4_0==VAR||(LA4_0>=ALAP && LA4_0<=UNTIL)||LA4_0==IF||(LA4_0>=TRY && LA4_0<=CHOICE)||LA4_0==TRUE||LA4_0==STAR||(LA4_0>=ANY && LA4_0<=OTHER)) ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // GCLNewChecker.g:89:26: s2= stat
                            {
                            _last = (MyTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat313);
                            s2=stat();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = s2.tree;

                            retval.tree = (MyTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (MyTree)adaptor.getParent(retval.tree);
                            }
                            break;

                    }


                    match(input, Token.UP, null); _last = _save_last_1;
                    }

                     retval.aut = builder.buildIfThenElse((c!=null?c.aut:null), (s1!=null?s1.aut:null), (s2!=null?s2.aut:null)); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 8 :
                    // GCLNewChecker.g:91:5: ^( CHOICE s1= stat (s2= stat )* )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    CHOICE20=(MyTree)match(input,CHOICE,FOLLOW_CHOICE_in_stat330); 


                    if ( _first_0==null ) _first_0 = CHOICE20;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat342);
                    s1=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s1.tree;
                     retval.aut = (s1!=null?s1.aut:null); 
                    // GCLNewChecker.g:94:8: (s2= stat )*
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0==BLOCK||LA5_0==CALL||LA5_0==VAR||(LA5_0>=ALAP && LA5_0<=UNTIL)||LA5_0==IF||(LA5_0>=TRY && LA5_0<=CHOICE)||LA5_0==TRUE||LA5_0==STAR||(LA5_0>=ANY && LA5_0<=OTHER)) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // GCLNewChecker.g:94:10: s2= stat
                    	    {
                    	    _last = (MyTree)input.LT(1);
                    	    pushFollow(FOLLOW_stat_in_stat364);
                    	    s2=stat();

                    	    state._fsp--;

                    	     
                    	    if ( _first_1==null ) _first_1 = s2.tree;
                    	     retval.aut = builder.buildOr(retval.aut, (s2!=null?s2.aut:null)); 

                    	    retval.tree = (MyTree)_first_0;
                    	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                    	        retval.tree = (MyTree)adaptor.getParent(retval.tree);
                    	    }
                    	    break;

                    	default :
                    	    break loop5;
                        }
                    } while (true);


                    match(input, Token.UP, null); _last = _save_last_1;
                    }


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 9 :
                    // GCLNewChecker.g:98:5: ^( STAR s= stat )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    STAR21=(MyTree)match(input,STAR,FOLLOW_STAR_in_stat399); 


                    if ( _first_0==null ) _first_0 = STAR21;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat403);
                    s=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s.tree;

                    match(input, Token.UP, null); _last = _save_last_1;
                    }

                     retval.aut = builder.buildStar((s!=null?s.aut:null)); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 10 :
                    // GCLNewChecker.g:100:5: rule
                    {
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_rule_in_stat416);
                    rule22=rule();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = rule22.tree;

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 11 :
                    // GCLNewChecker.g:101:5: ANY
                    {
                    _last = (MyTree)input.LT(1);
                    ANY23=(MyTree)match(input,ANY,FOLLOW_ANY_in_stat422); 
                     
                    if ( _first_0==null ) _first_0 = ANY23;

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 12 :
                    // GCLNewChecker.g:102:5: OTHER
                    {
                    _last = (MyTree)input.LT(1);
                    OTHER24=(MyTree)match(input,OTHER,FOLLOW_OTHER_in_stat428); 
                     
                    if ( _first_0==null ) _first_0 = OTHER24;

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 13 :
                    // GCLNewChecker.g:103:5: TRUE
                    {
                    _last = (MyTree)input.LT(1);
                    TRUE25=(MyTree)match(input,TRUE,FOLLOW_TRUE_in_stat434); 
                     
                    if ( _first_0==null ) _first_0 = TRUE25;
                     retval.aut = builder.buildTrue(); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "stat"

    public static class rule_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rule"
    // GCLNewChecker.g:107:1: rule : ^( CALL ID ( arg )* ) ;
    public final GCLNewChecker.rule_return rule() throws RecognitionException {
        GCLNewChecker.rule_return retval = new GCLNewChecker.rule_return();
        retval.start = input.LT(1);

        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree CALL26=null;
        MyTree ID27=null;
        GCLNewChecker.arg_return arg28 = null;


        MyTree CALL26_tree=null;
        MyTree ID27_tree=null;

        try {
            // GCLNewChecker.g:108:3: ( ^( CALL ID ( arg )* ) )
            // GCLNewChecker.g:108:5: ^( CALL ID ( arg )* )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            CALL26=(MyTree)match(input,CALL,FOLLOW_CALL_in_rule454); 


            if ( _first_0==null ) _first_0 = CALL26;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            ID27=(MyTree)match(input,ID,FOLLOW_ID_in_rule456); 
             
            if ( _first_1==null ) _first_1 = ID27;
            // GCLNewChecker.g:108:15: ( arg )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==ARG) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // GCLNewChecker.g:108:15: arg
            	    {
            	    _last = (MyTree)input.LT(1);
            	    pushFollow(FOLLOW_arg_in_rule458);
            	    arg28=arg();

            	    state._fsp--;

            	     
            	    if ( _first_1==null ) _first_1 = arg28.tree;

            	    retval.tree = (MyTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (MyTree)adaptor.getParent(retval.tree);
            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);


            match(input, Token.UP, null); _last = _save_last_1;
            }

             helper.checkCall(CALL26_tree); 

            retval.tree = (MyTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (MyTree)adaptor.getParent(retval.tree);
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rule"

    public static class var_decl_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "var_decl"
    // GCLNewChecker.g:112:1: var_decl : ^( VAR type ( ID )+ ) ;
    public final GCLNewChecker.var_decl_return var_decl() throws RecognitionException {
        GCLNewChecker.var_decl_return retval = new GCLNewChecker.var_decl_return();
        retval.start = input.LT(1);

        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree VAR29=null;
        MyTree ID31=null;
        GCLNewChecker.type_return type30 = null;


        MyTree VAR29_tree=null;
        MyTree ID31_tree=null;

        try {
            // GCLNewChecker.g:113:2: ( ^( VAR type ( ID )+ ) )
            // GCLNewChecker.g:113:4: ^( VAR type ( ID )+ )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            VAR29=(MyTree)match(input,VAR,FOLLOW_VAR_in_var_decl481); 


            if ( _first_0==null ) _first_0 = VAR29;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_type_in_var_decl483);
            type30=type();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = type30.tree;
            // GCLNewChecker.g:114:7: ( ID )+
            int cnt8=0;
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==ID) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // GCLNewChecker.g:114:9: ID
            	    {
            	    _last = (MyTree)input.LT(1);
            	    ID31=(MyTree)match(input,ID,FOLLOW_ID_in_var_decl493); 
            	     
            	    if ( _first_1==null ) _first_1 = ID31;
            	     helper.declareVar(ID31_tree, (type30!=null?((MyTree)type30.tree):null)); 

            	    retval.tree = (MyTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (MyTree)adaptor.getParent(retval.tree);
            	    }
            	    break;

            	default :
            	    if ( cnt8 >= 1 ) break loop8;
                        EarlyExitException eee =
                            new EarlyExitException(8, input);
                        throw eee;
                }
                cnt8++;
            } while (true);


            match(input, Token.UP, null); _last = _save_last_1;
            }


            retval.tree = (MyTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (MyTree)adaptor.getParent(retval.tree);
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "var_decl"

    public static class type_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "type"
    // GCLNewChecker.g:120:1: type : ( NODE | BOOL | STRING | INT | REAL );
    public final GCLNewChecker.type_return type() throws RecognitionException {
        GCLNewChecker.type_return retval = new GCLNewChecker.type_return();
        retval.start = input.LT(1);

        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree set32=null;

        MyTree set32_tree=null;

        try {
            // GCLNewChecker.g:122:3: ( NODE | BOOL | STRING | INT | REAL )
            // GCLNewChecker.g:
            {
            _last = (MyTree)input.LT(1);
            set32=(MyTree)input.LT(1);
            if ( (input.LA(1)>=NODE && input.LA(1)<=REAL) ) {
                input.consume();


                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            retval.tree = (MyTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (MyTree)adaptor.getParent(retval.tree); 

            }

             helper.checkType(((MyTree)retval.tree)); 
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "type"

    public static class arg_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "arg"
    // GCLNewChecker.g:125:1: arg : ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) ;
    public final GCLNewChecker.arg_return arg() throws RecognitionException {
        GCLNewChecker.arg_return retval = new GCLNewChecker.arg_return();
        retval.start = input.LT(1);

        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree ARG33=null;
        MyTree OUT34=null;
        MyTree ID35=null;
        MyTree DONT_CARE36=null;
        GCLNewChecker.literal_return literal37 = null;


        MyTree ARG33_tree=null;
        MyTree OUT34_tree=null;
        MyTree ID35_tree=null;
        MyTree DONT_CARE36_tree=null;

        try {
            // GCLNewChecker.g:126:2: ( ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) )
            // GCLNewChecker.g:126:4: ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            ARG33=(MyTree)match(input,ARG,FOLLOW_ARG_in_arg568); 


            if ( _first_0==null ) _first_0 = ARG33;
            match(input, Token.DOWN, null); 
            // GCLNewChecker.g:127:7: ( ( OUT )? ID | DONT_CARE | literal )
            int alt10=3;
            switch ( input.LA(1) ) {
            case ID:
            case OUT:
                {
                alt10=1;
                }
                break;
            case DONT_CARE:
                {
                alt10=2;
                }
                break;
            case TRUE:
            case FALSE:
            case STRING_LIT:
            case INT_LIT:
            case REAL_LIT:
                {
                alt10=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }

            switch (alt10) {
                case 1 :
                    // GCLNewChecker.g:127:9: ( OUT )? ID
                    {
                    // GCLNewChecker.g:127:9: ( OUT )?
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==OUT) ) {
                        alt9=1;
                    }
                    switch (alt9) {
                        case 1 :
                            // GCLNewChecker.g:127:9: OUT
                            {
                            _last = (MyTree)input.LT(1);
                            OUT34=(MyTree)match(input,OUT,FOLLOW_OUT_in_arg579); 
                             
                            if ( _first_1==null ) _first_1 = OUT34;

                            retval.tree = (MyTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (MyTree)adaptor.getParent(retval.tree);
                            }
                            break;

                    }

                    _last = (MyTree)input.LT(1);
                    ID35=(MyTree)match(input,ID,FOLLOW_ID_in_arg582); 
                     
                    if ( _first_1==null ) _first_1 = ID35;
                     helper.checkVarArg(ARG33_tree); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 2 :
                    // GCLNewChecker.g:128:9: DONT_CARE
                    {
                    _last = (MyTree)input.LT(1);
                    DONT_CARE36=(MyTree)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg594); 
                     
                    if ( _first_1==null ) _first_1 = DONT_CARE36;
                     helper.checkDontCareArg(ARG33_tree); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);
                    }
                    break;
                case 3 :
                    // GCLNewChecker.g:129:9: literal
                    {
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_literal_in_arg606);
                    literal37=literal();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = literal37.tree;
                     helper.checkConstArg(ARG33_tree); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);
                    }
                    break;

            }


            match(input, Token.UP, null); _last = _save_last_1;
            }


            retval.tree = (MyTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (MyTree)adaptor.getParent(retval.tree);
            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "arg"

    public static class literal_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "literal"
    // GCLNewChecker.g:134:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final GCLNewChecker.literal_return literal() throws RecognitionException {
        GCLNewChecker.literal_return retval = new GCLNewChecker.literal_return();
        retval.start = input.LT(1);

        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree set38=null;

        MyTree set38_tree=null;

        try {
            // GCLNewChecker.g:135:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // GCLNewChecker.g:
            {
            _last = (MyTree)input.LT(1);
            set38=(MyTree)input.LT(1);
            if ( input.LA(1)==TRUE||(input.LA(1)>=FALSE && input.LA(1)<=REAL_LIT) ) {
                input.consume();


                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            retval.tree = (MyTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (MyTree)adaptor.getParent(retval.tree); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "literal"

    // Delegated rules


 

    public static final BitSet FOLLOW_PROGRAM_in_program57 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_functions_in_program59 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_program61 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTIONS_in_functions77 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_function_in_functions79 = new BitSet(new long[]{0x0000000000000088L});
    public static final BitSet FOLLOW_FUNCTION_in_function92 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_function94 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_function96 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block118 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_block138 = new BitSet(new long[]{0x00000006A35C0928L});
    public static final BitSet FOLLOW_block_in_stat192 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat204 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat217 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat221 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WHILE_in_stat235 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat239 = new BitSet(new long[]{0x00000006A35C0928L});
    public static final BitSet FOLLOW_stat_in_stat243 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UNTIL_in_stat257 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat259 = new BitSet(new long[]{0x00000006A35C0928L});
    public static final BitSet FOLLOW_stat_in_stat261 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TRY_in_stat275 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat279 = new BitSet(new long[]{0x00000006A35C0928L});
    public static final BitSet FOLLOW_stat_in_stat284 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_stat300 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat304 = new BitSet(new long[]{0x00000006A35C0928L});
    public static final BitSet FOLLOW_stat_in_stat308 = new BitSet(new long[]{0x00000006A35C0928L});
    public static final BitSet FOLLOW_stat_in_stat313 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CHOICE_in_stat330 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat342 = new BitSet(new long[]{0x00000006A35C0928L});
    public static final BitSet FOLLOW_stat_in_stat364 = new BitSet(new long[]{0x00000006A35C0928L});
    public static final BitSet FOLLOW_STAR_in_stat399 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat403 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_stat416 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_stat422 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_stat428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_stat434 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CALL_in_rule454 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule456 = new BitSet(new long[]{0x0000000000001008L});
    public static final BitSet FOLLOW_arg_in_rule458 = new BitSet(new long[]{0x0000000000001008L});
    public static final BitSet FOLLOW_VAR_in_var_decl481 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_var_decl483 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_ID_in_var_decl493 = new BitSet(new long[]{0x0000000000008008L});
    public static final BitSet FOLLOW_set_in_type0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARG_in_arg568 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OUT_in_arg579 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_ID_in_arg582 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg594 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_literal_in_arg606 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_literal0 = new BitSet(new long[]{0x0000000000000002L});

}
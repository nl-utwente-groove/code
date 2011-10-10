// $ANTLR 3.4 CtrlBuilder.g 2011-07-31 12:53:55

package groove.control.parse;
import groove.control.*;
import groove.trans.Rule;
import java.util.Set;
import java.util.HashSet;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


@SuppressWarnings({"all", "warnings", "unchecked"})
public class CtrlBuilder extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ALAP", "AMP", "ANY", "ARG", "ARGS", "ASTERISK", "BAR", "BLOCK", "BOOL", "BSLASH", "CALL", "CHOICE", "COMMA", "DO", "DONT_CARE", "DOT", "DO_UNTIL", "DO_WHILE", "ELSE", "EscapeSequence", "FALSE", "FUNCTION", "FUNCTIONS", "ID", "IF", "INT", "INT_LIT", "IntegerNumber", "LCURLY", "LPAR", "MINUS", "ML_COMMENT", "NODE", "NOT", "NonIntegerNumber", "OR", "OTHER", "OUT", "PLUS", "PROGRAM", "QUOTE", "RCURLY", "REAL", "REAL_LIT", "RPAR", "SEMI", "SHARP", "SL_COMMENT", "STAR", "STRING", "STRING_LIT", "TRUE", "TRY", "UNTIL", "VAR", "WHILE", "WS"
    };

    public static final int EOF=-1;
    public static final int ALAP=4;
    public static final int AMP=5;
    public static final int ANY=6;
    public static final int ARG=7;
    public static final int ARGS=8;
    public static final int ASTERISK=9;
    public static final int BAR=10;
    public static final int BLOCK=11;
    public static final int BOOL=12;
    public static final int BSLASH=13;
    public static final int CALL=14;
    public static final int CHOICE=15;
    public static final int COMMA=16;
    public static final int DO=17;
    public static final int DONT_CARE=18;
    public static final int DOT=19;
    public static final int DO_UNTIL=20;
    public static final int DO_WHILE=21;
    public static final int ELSE=22;
    public static final int EscapeSequence=23;
    public static final int FALSE=24;
    public static final int FUNCTION=25;
    public static final int FUNCTIONS=26;
    public static final int ID=27;
    public static final int IF=28;
    public static final int INT=29;
    public static final int INT_LIT=30;
    public static final int IntegerNumber=31;
    public static final int LCURLY=32;
    public static final int LPAR=33;
    public static final int MINUS=34;
    public static final int ML_COMMENT=35;
    public static final int NODE=36;
    public static final int NOT=37;
    public static final int NonIntegerNumber=38;
    public static final int OR=39;
    public static final int OTHER=40;
    public static final int OUT=41;
    public static final int PLUS=42;
    public static final int PROGRAM=43;
    public static final int QUOTE=44;
    public static final int RCURLY=45;
    public static final int REAL=46;
    public static final int REAL_LIT=47;
    public static final int RPAR=48;
    public static final int SEMI=49;
    public static final int SHARP=50;
    public static final int SL_COMMENT=51;
    public static final int STAR=52;
    public static final int STRING=53;
    public static final int STRING_LIT=54;
    public static final int TRUE=55;
    public static final int TRY=56;
    public static final int UNTIL=57;
    public static final int VAR=58;
    public static final int WHILE=59;
    public static final int WS=60;

    // delegates
    public TreeParser[] getDelegates() {
        return new TreeParser[] {};
    }

    // delegators


    public CtrlBuilder(TreeNodeStream input) {
        this(input, new RecognizerSharedState());
    }
    public CtrlBuilder(TreeNodeStream input, RecognizerSharedState state) {
        super(input, state);
    }

protected TreeAdaptor adaptor = new CommonTreeAdaptor();

public void setTreeAdaptor(TreeAdaptor adaptor) {
    this.adaptor = adaptor;
}
public TreeAdaptor getTreeAdaptor() {
    return adaptor;
}
    public String[] getTokenNames() { return CtrlBuilder.tokenNames; }
    public String getGrammarFileName() { return "CtrlBuilder.g"; }


        /** Builder for control automata. */
        private CtrlFactory builder;
        /** Namespace used for building the automaton. */
        private Namespace namespace;

        /**
         * Runs the builder on a given, checked syntax tree.
         */
        public CtrlAut run(MyTree tree, Namespace namespace) throws RecognitionException {
            this.builder = CtrlFactory.instance();
            this.namespace = namespace;
            MyTreeAdaptor treeAdaptor = new MyTreeAdaptor();
            setTreeAdaptor(treeAdaptor);
            setTreeNodeStream(treeAdaptor.createTreeNodeStream(tree));
            return program().aut;
        }


    public static class program_return extends TreeRuleReturnScope {
        public CtrlAut aut;
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "program"
    // CtrlBuilder.g:37:1: program returns [ CtrlAut aut ] : ^( PROGRAM functions block ) ;
    public final CtrlBuilder.program_return program() throws RecognitionException {
        CtrlBuilder.program_return retval = new CtrlBuilder.program_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree PROGRAM1=null;
        CtrlBuilder.functions_return functions2 =null;

        CtrlBuilder.block_return block3 =null;


        MyTree PROGRAM1_tree=null;

        try {
            // CtrlBuilder.g:38:3: ( ^( PROGRAM functions block ) )
            // CtrlBuilder.g:38:5: ^( PROGRAM functions block )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            PROGRAM1=(MyTree)match(input,PROGRAM,FOLLOW_PROGRAM_in_program59); 


            if ( _first_0==null ) _first_0 = PROGRAM1;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_functions_in_program61);
            functions2=functions();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = functions2.tree;


            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_block_in_program63);
            block3=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block3.tree;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


             retval.aut = (block3!=null?block3.aut:null); 

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
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "program"


    public static class functions_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "functions"
    // CtrlBuilder.g:42:1: functions : ^( FUNCTIONS ( function )* ) ;
    public final CtrlBuilder.functions_return functions() throws RecognitionException {
        CtrlBuilder.functions_return retval = new CtrlBuilder.functions_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree FUNCTIONS4=null;
        CtrlBuilder.function_return function5 =null;


        MyTree FUNCTIONS4_tree=null;

        try {
            // CtrlBuilder.g:43:3: ( ^( FUNCTIONS ( function )* ) )
            // CtrlBuilder.g:43:5: ^( FUNCTIONS ( function )* )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            FUNCTIONS4=(MyTree)match(input,FUNCTIONS,FOLLOW_FUNCTIONS_in_functions84); 


            if ( _first_0==null ) _first_0 = FUNCTIONS4;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // CtrlBuilder.g:43:17: ( function )*
                loop1:
                do {
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==FUNCTION) ) {
                        alt1=1;
                    }


                    switch (alt1) {
                	case 1 :
                	    // CtrlBuilder.g:43:17: function
                	    {
                	    _last = (MyTree)input.LT(1);
                	    pushFollow(FOLLOW_function_in_functions86);
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
            }
            _last = _save_last_1;
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
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "functions"


    public static class function_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "function"
    // CtrlBuilder.g:45:1: function : ^( FUNCTION ID block ) ;
    public final CtrlBuilder.function_return function() throws RecognitionException {
        CtrlBuilder.function_return retval = new CtrlBuilder.function_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree FUNCTION6=null;
        MyTree ID7=null;
        CtrlBuilder.block_return block8 =null;


        MyTree FUNCTION6_tree=null;
        MyTree ID7_tree=null;

        try {
            // CtrlBuilder.g:46:3: ( ^( FUNCTION ID block ) )
            // CtrlBuilder.g:46:5: ^( FUNCTION ID block )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            FUNCTION6=(MyTree)match(input,FUNCTION,FOLLOW_FUNCTION_in_function99); 


            if ( _first_0==null ) _first_0 = FUNCTION6;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            ID7=(MyTree)match(input,ID,FOLLOW_ID_in_function101); 
             
            if ( _first_1==null ) _first_1 = ID7;


            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_block_in_function103);
            block8=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block8.tree;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


             namespace.addFunctionBody((ID7!=null?ID7.getText():null), (block8!=null?block8.aut:null)); 

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
        	// do for sure before leaving
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
    // CtrlBuilder.g:50:1: block returns [ CtrlAut aut ] : ^( BLOCK ( stat )* ) ;
    public final CtrlBuilder.block_return block() throws RecognitionException {
        CtrlBuilder.block_return retval = new CtrlBuilder.block_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree BLOCK9=null;
        CtrlBuilder.stat_return stat10 =null;


        MyTree BLOCK9_tree=null;

        try {
            // CtrlBuilder.g:51:3: ( ^( BLOCK ( stat )* ) )
            // CtrlBuilder.g:51:5: ^( BLOCK ( stat )* )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            BLOCK9=(MyTree)match(input,BLOCK,FOLLOW_BLOCK_in_block131); 


            if ( _first_0==null ) _first_0 = BLOCK9;
             retval.aut = builder.buildTrue(); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // CtrlBuilder.g:53:8: ( stat )*
                loop2:
                do {
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( (LA2_0==ALAP||LA2_0==ANY||LA2_0==BLOCK||(LA2_0 >= CALL && LA2_0 <= CHOICE)||LA2_0==IF||LA2_0==OTHER||LA2_0==STAR||(LA2_0 >= TRUE && LA2_0 <= WHILE)) ) {
                        alt2=1;
                    }


                    switch (alt2) {
                	case 1 :
                	    // CtrlBuilder.g:53:10: stat
                	    {
                	    _last = (MyTree)input.LT(1);
                	    pushFollow(FOLLOW_stat_in_block151);
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


                match(input, Token.UP, null); 
            }
            _last = _save_last_1;
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
        	// do for sure before leaving
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
    // CtrlBuilder.g:59:1: stat returns [ CtrlAut aut ] : ( block | var_decl | ^( ALAP s= stat ) | ^( WHILE c= stat s= stat ) | ^( UNTIL c= stat s= stat ) | ^( TRY s1= stat (s2= stat )? ) | ^( IF c= stat s1= stat (s2= stat )? ) | ^( CHOICE s1= stat (s2= stat )* ) | ^( STAR s= stat ) | rule | ANY | OTHER | TRUE );
    public final CtrlBuilder.stat_return stat() throws RecognitionException {
        CtrlBuilder.stat_return retval = new CtrlBuilder.stat_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree ALAP13=null;
        MyTree WHILE14=null;
        MyTree UNTIL15=null;
        MyTree TRY16=null;
        MyTree IF17=null;
        MyTree CHOICE18=null;
        MyTree STAR19=null;
        MyTree ANY21=null;
        MyTree OTHER22=null;
        MyTree TRUE23=null;
        CtrlBuilder.stat_return s =null;

        CtrlBuilder.stat_return c =null;

        CtrlBuilder.stat_return s1 =null;

        CtrlBuilder.stat_return s2 =null;

        CtrlBuilder.block_return block11 =null;

        CtrlBuilder.var_decl_return var_decl12 =null;

        CtrlBuilder.rule_return rule20 =null;


        MyTree ALAP13_tree=null;
        MyTree WHILE14_tree=null;
        MyTree UNTIL15_tree=null;
        MyTree TRY16_tree=null;
        MyTree IF17_tree=null;
        MyTree CHOICE18_tree=null;
        MyTree STAR19_tree=null;
        MyTree ANY21_tree=null;
        MyTree OTHER22_tree=null;
        MyTree TRUE23_tree=null;

        try {
            // CtrlBuilder.g:60:3: ( block | var_decl | ^( ALAP s= stat ) | ^( WHILE c= stat s= stat ) | ^( UNTIL c= stat s= stat ) | ^( TRY s1= stat (s2= stat )? ) | ^( IF c= stat s1= stat (s2= stat )? ) | ^( CHOICE s1= stat (s2= stat )* ) | ^( STAR s= stat ) | rule | ANY | OTHER | TRUE )
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
                    // CtrlBuilder.g:60:5: block
                    {
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_stat196);
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
                    // CtrlBuilder.g:62:5: var_decl
                    {
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_var_decl_in_stat208);
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
                    // CtrlBuilder.g:64:5: ^( ALAP s= stat )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    ALAP13=(MyTree)match(input,ALAP,FOLLOW_ALAP_in_stat221); 


                    if ( _first_0==null ) _first_0 = ALAP13;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat225);
                    s=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = builder.buildAlap((s!=null?s.aut:null)); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 4 :
                    // CtrlBuilder.g:66:5: ^( WHILE c= stat s= stat )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    WHILE14=(MyTree)match(input,WHILE,FOLLOW_WHILE_in_stat239); 


                    if ( _first_0==null ) _first_0 = WHILE14;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat243);
                    c=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = c.tree;


                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat247);
                    s=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = builder.buildWhileDo((c!=null?c.aut:null), (s!=null?s.aut:null)); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 5 :
                    // CtrlBuilder.g:68:5: ^( UNTIL c= stat s= stat )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    UNTIL15=(MyTree)match(input,UNTIL,FOLLOW_UNTIL_in_stat261); 


                    if ( _first_0==null ) _first_0 = UNTIL15;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat265);
                    c=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = c.tree;


                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat269);
                    s=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = builder.buildUntilDo((c!=null?c.aut:null), (s!=null?s.aut:null)); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 6 :
                    // CtrlBuilder.g:70:5: ^( TRY s1= stat (s2= stat )? )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    TRY16=(MyTree)match(input,TRY,FOLLOW_TRY_in_stat283); 


                    if ( _first_0==null ) _first_0 = TRY16;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat287);
                    s1=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s1.tree;


                    // CtrlBuilder.g:70:19: (s2= stat )?
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==ALAP||LA3_0==ANY||LA3_0==BLOCK||(LA3_0 >= CALL && LA3_0 <= CHOICE)||LA3_0==IF||LA3_0==OTHER||LA3_0==STAR||(LA3_0 >= TRUE && LA3_0 <= WHILE)) ) {
                        alt3=1;
                    }
                    switch (alt3) {
                        case 1 :
                            // CtrlBuilder.g:70:20: s2= stat
                            {
                            _last = (MyTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat292);
                            s2=stat();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = s2.tree;


                            retval.tree = (MyTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (MyTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = builder.buildTryElse((s1!=null?s1.aut:null), (s2!=null?s2.aut:null)); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 7 :
                    // CtrlBuilder.g:72:5: ^( IF c= stat s1= stat (s2= stat )? )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    IF17=(MyTree)match(input,IF,FOLLOW_IF_in_stat308); 


                    if ( _first_0==null ) _first_0 = IF17;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat312);
                    c=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = c.tree;


                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat316);
                    s1=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s1.tree;


                    // CtrlBuilder.g:72:25: (s2= stat )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==ALAP||LA4_0==ANY||LA4_0==BLOCK||(LA4_0 >= CALL && LA4_0 <= CHOICE)||LA4_0==IF||LA4_0==OTHER||LA4_0==STAR||(LA4_0 >= TRUE && LA4_0 <= WHILE)) ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // CtrlBuilder.g:72:26: s2= stat
                            {
                            _last = (MyTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat321);
                            s2=stat();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = s2.tree;


                            retval.tree = (MyTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (MyTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = builder.buildIfThenElse((c!=null?c.aut:null), (s1!=null?s1.aut:null), (s2!=null?s2.aut:null)); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 8 :
                    // CtrlBuilder.g:74:5: ^( CHOICE s1= stat (s2= stat )* )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    CHOICE18=(MyTree)match(input,CHOICE,FOLLOW_CHOICE_in_stat338); 


                    if ( _first_0==null ) _first_0 = CHOICE18;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat350);
                    s1=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s1.tree;


                     retval.aut = (s1!=null?s1.aut:null); 

                    // CtrlBuilder.g:77:8: (s2= stat )*
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0==ALAP||LA5_0==ANY||LA5_0==BLOCK||(LA5_0 >= CALL && LA5_0 <= CHOICE)||LA5_0==IF||LA5_0==OTHER||LA5_0==STAR||(LA5_0 >= TRUE && LA5_0 <= WHILE)) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // CtrlBuilder.g:77:10: s2= stat
                    	    {
                    	    _last = (MyTree)input.LT(1);
                    	    pushFollow(FOLLOW_stat_in_stat372);
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


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 9 :
                    // CtrlBuilder.g:81:5: ^( STAR s= stat )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    STAR19=(MyTree)match(input,STAR,FOLLOW_STAR_in_stat407); 


                    if ( _first_0==null ) _first_0 = STAR19;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat411);
                    s=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = builder.buildStar((s!=null?s.aut:null)); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 10 :
                    // CtrlBuilder.g:83:5: rule
                    {
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_rule_in_stat424);
                    rule20=rule();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = rule20.tree;


                     retval.aut = builder.buildCall((rule20!=null?((MyTree)rule20.tree):null).getCtrlCall(), namespace); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 11 :
                    // CtrlBuilder.g:85:5: ANY
                    {
                    _last = (MyTree)input.LT(1);
                    ANY21=(MyTree)match(input,ANY,FOLLOW_ANY_in_stat436); 
                     
                    if ( _first_0==null ) _first_0 = ANY21;


                     retval.aut = builder.buildAny(namespace); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 12 :
                    // CtrlBuilder.g:87:5: OTHER
                    {
                    _last = (MyTree)input.LT(1);
                    OTHER22=(MyTree)match(input,OTHER,FOLLOW_OTHER_in_stat448); 
                     
                    if ( _first_0==null ) _first_0 = OTHER22;


                     retval.aut = builder.buildOther(namespace); 
                        

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 13 :
                    // CtrlBuilder.g:90:5: TRUE
                    {
                    _last = (MyTree)input.LT(1);
                    TRUE23=(MyTree)match(input,TRUE,FOLLOW_TRUE_in_stat460); 
                     
                    if ( _first_0==null ) _first_0 = TRUE23;


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
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "stat"


    public static class rule_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "rule"
    // CtrlBuilder.g:94:1: rule : ^( CALL ID ( ^( ARGS ( arg )* ) )? ) ;
    public final CtrlBuilder.rule_return rule() throws RecognitionException {
        CtrlBuilder.rule_return retval = new CtrlBuilder.rule_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree CALL24=null;
        MyTree ID25=null;
        MyTree ARGS26=null;
        CtrlBuilder.arg_return arg27 =null;


        MyTree CALL24_tree=null;
        MyTree ID25_tree=null;
        MyTree ARGS26_tree=null;

        try {
            // CtrlBuilder.g:95:3: ( ^( CALL ID ( ^( ARGS ( arg )* ) )? ) )
            // CtrlBuilder.g:95:5: ^( CALL ID ( ^( ARGS ( arg )* ) )? )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            CALL24=(MyTree)match(input,CALL,FOLLOW_CALL_in_rule480); 


            if ( _first_0==null ) _first_0 = CALL24;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            ID25=(MyTree)match(input,ID,FOLLOW_ID_in_rule482); 
             
            if ( _first_1==null ) _first_1 = ID25;


            // CtrlBuilder.g:95:15: ( ^( ARGS ( arg )* ) )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==ARGS) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // CtrlBuilder.g:95:16: ^( ARGS ( arg )* )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_2 = _last;
                    MyTree _first_2 = null;
                    _last = (MyTree)input.LT(1);
                    ARGS26=(MyTree)match(input,ARGS,FOLLOW_ARGS_in_rule486); 


                    if ( _first_1==null ) _first_1 = ARGS26;
                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // CtrlBuilder.g:95:23: ( arg )*
                        loop7:
                        do {
                            int alt7=2;
                            int LA7_0 = input.LA(1);

                            if ( (LA7_0==ARG) ) {
                                alt7=1;
                            }


                            switch (alt7) {
                        	case 1 :
                        	    // CtrlBuilder.g:95:23: arg
                        	    {
                        	    _last = (MyTree)input.LT(1);
                        	    pushFollow(FOLLOW_arg_in_rule488);
                        	    arg27=arg();

                        	    state._fsp--;

                        	     
                        	    if ( _first_2==null ) _first_2 = arg27.tree;


                        	    retval.tree = (MyTree)_first_0;
                        	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        	        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                        	    }
                        	    break;

                        	default :
                        	    break loop7;
                            }
                        } while (true);


                        match(input, Token.UP, null); 
                    }
                    _last = _save_last_2;
                    }


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            match(input, Token.UP, null); 
            _last = _save_last_1;
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
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "rule"


    public static class var_decl_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "var_decl"
    // CtrlBuilder.g:98:1: var_decl : ^( VAR type ( ID )+ ) ;
    public final CtrlBuilder.var_decl_return var_decl() throws RecognitionException {
        CtrlBuilder.var_decl_return retval = new CtrlBuilder.var_decl_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree VAR28=null;
        MyTree ID30=null;
        CtrlBuilder.type_return type29 =null;


        MyTree VAR28_tree=null;
        MyTree ID30_tree=null;

        try {
            // CtrlBuilder.g:99:2: ( ^( VAR type ( ID )+ ) )
            // CtrlBuilder.g:99:4: ^( VAR type ( ID )+ )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            VAR28=(MyTree)match(input,VAR,FOLLOW_VAR_in_var_decl507); 


            if ( _first_0==null ) _first_0 = VAR28;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_type_in_var_decl509);
            type29=type();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = type29.tree;


            // CtrlBuilder.g:99:16: ( ID )+
            int cnt9=0;
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==ID) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // CtrlBuilder.g:99:16: ID
            	    {
            	    _last = (MyTree)input.LT(1);
            	    ID30=(MyTree)match(input,ID,FOLLOW_ID_in_var_decl511); 
            	     
            	    if ( _first_1==null ) _first_1 = ID30;


            	    retval.tree = (MyTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (MyTree)adaptor.getParent(retval.tree);

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


            match(input, Token.UP, null); 
            _last = _save_last_1;
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
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "var_decl"


    public static class type_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "type"
    // CtrlBuilder.g:102:1: type : ( NODE | BOOL | STRING | INT | REAL );
    public final CtrlBuilder.type_return type() throws RecognitionException {
        CtrlBuilder.type_return retval = new CtrlBuilder.type_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree set31=null;

        MyTree set31_tree=null;

        try {
            // CtrlBuilder.g:103:3: ( NODE | BOOL | STRING | INT | REAL )
            // CtrlBuilder.g:
            {
            _last = (MyTree)input.LT(1);
            set31=(MyTree)input.LT(1);

            if ( input.LA(1)==BOOL||input.LA(1)==INT||input.LA(1)==NODE||input.LA(1)==REAL||input.LA(1)==STRING ) {
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
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "type"


    public static class arg_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arg"
    // CtrlBuilder.g:106:1: arg : ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) ;
    public final CtrlBuilder.arg_return arg() throws RecognitionException {
        CtrlBuilder.arg_return retval = new CtrlBuilder.arg_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree ARG32=null;
        MyTree OUT33=null;
        MyTree ID34=null;
        MyTree DONT_CARE35=null;
        CtrlBuilder.literal_return literal36 =null;


        MyTree ARG32_tree=null;
        MyTree OUT33_tree=null;
        MyTree ID34_tree=null;
        MyTree DONT_CARE35_tree=null;

        try {
            // CtrlBuilder.g:107:2: ( ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) )
            // CtrlBuilder.g:107:4: ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            ARG32=(MyTree)match(input,ARG,FOLLOW_ARG_in_arg558); 


            if ( _first_0==null ) _first_0 = ARG32;
            match(input, Token.DOWN, null); 
            // CtrlBuilder.g:107:11: ( ( OUT )? ID | DONT_CARE | literal )
            int alt11=3;
            switch ( input.LA(1) ) {
            case ID:
            case OUT:
                {
                alt11=1;
                }
                break;
            case DONT_CARE:
                {
                alt11=2;
                }
                break;
            case FALSE:
            case INT_LIT:
            case REAL_LIT:
            case STRING_LIT:
            case TRUE:
                {
                alt11=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;

            }

            switch (alt11) {
                case 1 :
                    // CtrlBuilder.g:107:13: ( OUT )? ID
                    {
                    // CtrlBuilder.g:107:13: ( OUT )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0==OUT) ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // CtrlBuilder.g:107:13: OUT
                            {
                            _last = (MyTree)input.LT(1);
                            OUT33=(MyTree)match(input,OUT,FOLLOW_OUT_in_arg562); 
                             
                            if ( _first_1==null ) _first_1 = OUT33;


                            retval.tree = (MyTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (MyTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                    _last = (MyTree)input.LT(1);
                    ID34=(MyTree)match(input,ID,FOLLOW_ID_in_arg565); 
                     
                    if ( _first_1==null ) _first_1 = ID34;


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // CtrlBuilder.g:107:23: DONT_CARE
                    {
                    _last = (MyTree)input.LT(1);
                    DONT_CARE35=(MyTree)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg569); 
                     
                    if ( _first_1==null ) _first_1 = DONT_CARE35;


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // CtrlBuilder.g:107:35: literal
                    {
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_literal_in_arg573);
                    literal36=literal();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = literal36.tree;


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            match(input, Token.UP, null); 
            _last = _save_last_1;
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
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "arg"


    public static class literal_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "literal"
    // CtrlBuilder.g:110:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final CtrlBuilder.literal_return literal() throws RecognitionException {
        CtrlBuilder.literal_return retval = new CtrlBuilder.literal_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree set37=null;

        MyTree set37_tree=null;

        try {
            // CtrlBuilder.g:111:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // CtrlBuilder.g:
            {
            _last = (MyTree)input.LT(1);
            set37=(MyTree)input.LT(1);

            if ( input.LA(1)==FALSE||input.LA(1)==INT_LIT||input.LA(1)==REAL_LIT||(input.LA(1) >= STRING_LIT && input.LA(1) <= TRUE) ) {
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
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "literal"

    // Delegated rules


 

    public static final BitSet FOLLOW_PROGRAM_in_program59 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_functions_in_program61 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_block_in_program63 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTIONS_in_functions84 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_function_in_functions86 = new BitSet(new long[]{0x0000000002000008L});
    public static final BitSet FOLLOW_FUNCTION_in_function99 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_function101 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_block_in_function103 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block131 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_block151 = new BitSet(new long[]{0x0F9001001000C858L});
    public static final BitSet FOLLOW_block_in_stat196 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat208 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat221 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat225 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WHILE_in_stat239 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat243 = new BitSet(new long[]{0x0F9001001000C850L});
    public static final BitSet FOLLOW_stat_in_stat247 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UNTIL_in_stat261 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat265 = new BitSet(new long[]{0x0F9001001000C850L});
    public static final BitSet FOLLOW_stat_in_stat269 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TRY_in_stat283 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat287 = new BitSet(new long[]{0x0F9001001000C858L});
    public static final BitSet FOLLOW_stat_in_stat292 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_stat308 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat312 = new BitSet(new long[]{0x0F9001001000C850L});
    public static final BitSet FOLLOW_stat_in_stat316 = new BitSet(new long[]{0x0F9001001000C858L});
    public static final BitSet FOLLOW_stat_in_stat321 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CHOICE_in_stat338 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat350 = new BitSet(new long[]{0x0F9001001000C858L});
    public static final BitSet FOLLOW_stat_in_stat372 = new BitSet(new long[]{0x0F9001001000C858L});
    public static final BitSet FOLLOW_STAR_in_stat407 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat411 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_stat424 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_stat436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_stat448 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_stat460 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CALL_in_rule480 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule482 = new BitSet(new long[]{0x0000000000000108L});
    public static final BitSet FOLLOW_ARGS_in_rule486 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_arg_in_rule488 = new BitSet(new long[]{0x0000000000000088L});
    public static final BitSet FOLLOW_VAR_in_var_decl507 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_var_decl509 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_var_decl511 = new BitSet(new long[]{0x0000000008000008L});
    public static final BitSet FOLLOW_ARG_in_arg558 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OUT_in_arg562 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_arg565 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg569 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_literal_in_arg573 = new BitSet(new long[]{0x0000000000000008L});

}
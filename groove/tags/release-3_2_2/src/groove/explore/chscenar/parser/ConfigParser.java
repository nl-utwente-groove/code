// $ANTLR 3.1b1 ../../src/groove/explore/chscenar/parser/Config.g 2008-07-08 15:23:46

package groove.explore.chscenar.parser;
import groove.explore.chscenar.*;
import java.util.ArrayList;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;

public class ConfigParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ID", "WS", "LINE_COMMENT", "'RULE'", "'::ALLOW'", "'::DENY'", "':STRATEGY'", "':RESULT'", "':ACCEPTOR'", "','", "'.'"
    };
    public static final int WS=5;
    public static final int LINE_COMMENT=6;
    public static final int T__12=12;
    public static final int T__11=11;
    public static final int T__14=14;
    public static final int T__13=13;
    public static final int T__10=10;
    public static final int ID=4;
    public static final int EOF=-1;
    public static final int T__9=9;
    public static final int T__8=8;
    public static final int T__7=7;

    // delegates
    // delegators


        public ConfigParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public ConfigParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return ConfigParser.tokenNames; }
    public String getGrammarFileName() { return "../../src/groove/explore/chscenar/parser/Config.g"; }


    	Class<?> getClass(String name, ScenarioChecker.Component c) {
    	    try {
    		return ScenarioChecker.getClass(name, c);
    	    } catch (ClassNotFoundException e) {
    		System.err.println("Class " + name + " not found. Aborting");
    		System.exit(1);
    		return null;
    	    }
    	}



    public static class prog_return extends ParserRuleReturnScope {
        public AllowRule value;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start prog
    // ../../src/groove/explore/chscenar/parser/Config.g:32:1: prog returns [AllowRule value] : (r= rule )+ ;
    public final ConfigParser.prog_return prog() throws RecognitionException {
        ConfigParser.prog_return retval = new ConfigParser.prog_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        ConfigParser.rule_return r = null;



        try {
            // ../../src/groove/explore/chscenar/parser/Config.g:33:2: ( (r= rule )+ )
            // ../../src/groove/explore/chscenar/parser/Config.g:34:2: (r= rule )+
            {
            root_0 = (Object)adaptor.nil();

            retval.value = new AllowRuleUnion();
            // ../../src/groove/explore/chscenar/parser/Config.g:35:2: (r= rule )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==7) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // ../../src/groove/explore/chscenar/parser/Config.g:35:3: r= rule
            	    {
            	    pushFollow(FOLLOW_rule_in_prog64);
            	    r=rule();

            	    state._fsp--;

            	    adaptor.addChild(root_0, r.getTree());
            	    ((AllowRuleUnion) retval.value).addRule((r!=null?r.value:null));

            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
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
    // $ANTLR end prog

    public static class rule_return extends ParserRuleReturnScope {
        public AllowRuleImpl value;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start rule
    // ../../src/groove/explore/chscenar/parser/Config.g:38:1: rule returns [AllowRuleImpl value] : 'RULE' allowRule[$value] ( denyRule[$value] )* ;
    public final ConfigParser.rule_return rule() throws RecognitionException {
        ConfigParser.rule_return retval = new ConfigParser.rule_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal1=null;
        ConfigParser.allowRule_return allowRule2 = null;

        ConfigParser.denyRule_return denyRule3 = null;


        Object string_literal1_tree=null;

        try {
            // ../../src/groove/explore/chscenar/parser/Config.g:39:2: ( 'RULE' allowRule[$value] ( denyRule[$value] )* )
            // ../../src/groove/explore/chscenar/parser/Config.g:39:4: 'RULE' allowRule[$value] ( denyRule[$value] )*
            {
            root_0 = (Object)adaptor.nil();

            string_literal1=(Token)match(input,7,FOLLOW_7_in_rule85); 
            string_literal1_tree = (Object)adaptor.create(string_literal1);
            adaptor.addChild(root_0, string_literal1_tree);

            retval.value = new AllowRuleImpl();
            pushFollow(FOLLOW_allowRule_in_rule91);
            allowRule2=allowRule(retval.value);

            state._fsp--;

            adaptor.addChild(root_0, allowRule2.getTree());
            // ../../src/groove/explore/chscenar/parser/Config.g:40:20: ( denyRule[$value] )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==9) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // ../../src/groove/explore/chscenar/parser/Config.g:40:20: denyRule[$value]
            	    {
            	    pushFollow(FOLLOW_denyRule_in_rule94);
            	    denyRule3=denyRule(retval.value);

            	    state._fsp--;

            	    adaptor.addChild(root_0, denyRule3.getTree());

            	    }
            	    break;

            	default :
            	    break loop2;
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
    // $ANTLR end rule

    public static class allowRule_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start allowRule
    // ../../src/groove/explore/chscenar/parser/Config.g:42:1: allowRule[AllowRuleImpl r] : '::ALLOW' set= configSet ;
    public final ConfigParser.allowRule_return allowRule(AllowRuleImpl r) throws RecognitionException {
        ConfigParser.allowRule_return retval = new ConfigParser.allowRule_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal4=null;
        ConfigParser.configSet_return set = null;


        Object string_literal4_tree=null;

        try {
            // ../../src/groove/explore/chscenar/parser/Config.g:43:2: ( '::ALLOW' set= configSet )
            // ../../src/groove/explore/chscenar/parser/Config.g:43:4: '::ALLOW' set= configSet
            {
            root_0 = (Object)adaptor.nil();

            string_literal4=(Token)match(input,8,FOLLOW_8_in_allowRule107); 
            string_literal4_tree = (Object)adaptor.create(string_literal4);
            adaptor.addChild(root_0, string_literal4_tree);

            pushFollow(FOLLOW_configSet_in_allowRule113);
            set=configSet();

            state._fsp--;

            adaptor.addChild(root_0, set.getTree());
            r.setAllowed((set!=null?set.value:null)); 

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
    // $ANTLR end allowRule

    public static class denyRule_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start denyRule
    // ../../src/groove/explore/chscenar/parser/Config.g:47:1: denyRule[AllowRuleImpl r] : '::DENY' set= configSet ;
    public final ConfigParser.denyRule_return denyRule(AllowRuleImpl r) throws RecognitionException {
        ConfigParser.denyRule_return retval = new ConfigParser.denyRule_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal5=null;
        ConfigParser.configSet_return set = null;


        Object string_literal5_tree=null;

        try {
            // ../../src/groove/explore/chscenar/parser/Config.g:48:2: ( '::DENY' set= configSet )
            // ../../src/groove/explore/chscenar/parser/Config.g:48:4: '::DENY' set= configSet
            {
            root_0 = (Object)adaptor.nil();

            string_literal5=(Token)match(input,9,FOLLOW_9_in_denyRule128); 
            string_literal5_tree = (Object)adaptor.create(string_literal5);
            adaptor.addChild(root_0, string_literal5_tree);

            pushFollow(FOLLOW_configSet_in_denyRule133);
            set=configSet();

            state._fsp--;

            adaptor.addChild(root_0, set.getTree());
            r.addForbidden((set!=null?set.value:null));

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
    // $ANTLR end denyRule

    public static class configSet_return extends ParserRuleReturnScope {
        public SRASetImpl value;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start configSet
    // ../../src/groove/explore/chscenar/parser/Config.g:52:1: configSet returns [SRASetImpl value] : sc= strComp rc= resComp ac= accComp ;
    public final ConfigParser.configSet_return configSet() throws RecognitionException {
        ConfigParser.configSet_return retval = new ConfigParser.configSet_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        ConfigParser.strComp_return sc = null;

        ConfigParser.resComp_return rc = null;

        ConfigParser.accComp_return ac = null;



        try {
            // ../../src/groove/explore/chscenar/parser/Config.g:53:2: (sc= strComp rc= resComp ac= accComp )
            // ../../src/groove/explore/chscenar/parser/Config.g:53:4: sc= strComp rc= resComp ac= accComp
            {
            root_0 = (Object)adaptor.nil();

            retval.value = new SRASetImpl();
            pushFollow(FOLLOW_strComp_in_configSet156);
            sc=strComp();

            state._fsp--;

            adaptor.addChild(root_0, sc.getTree());
            pushFollow(FOLLOW_resComp_in_configSet160);
            rc=resComp();

            state._fsp--;

            adaptor.addChild(root_0, rc.getTree());
            pushFollow(FOLLOW_accComp_in_configSet164);
            ac=accComp();

            state._fsp--;

            adaptor.addChild(root_0, ac.getTree());

            	    for (Class c : (sc!=null?sc.value:null)) {
            	        retval.value.addStrategy(c);
            	    }
            	    for (Class c : (rc!=null?rc.value:null)) {
            	        retval.value.addResult(c);
            	    }
            	    for (Class c : (ac!=null?ac.value:null)) {
            	        retval.value.addAcceptor(c);
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
    // $ANTLR end configSet

    public static class strComp_return extends ParserRuleReturnScope {
        public ArrayList<Class<?>> value;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start strComp
    // ../../src/groove/explore/chscenar/parser/Config.g:67:1: strComp returns [ArrayList<Class<?>> value] : ':STRATEGY' l= listClass[ScenarioChecker.Component.STRATEGY] ;
    public final ConfigParser.strComp_return strComp() throws RecognitionException {
        ConfigParser.strComp_return retval = new ConfigParser.strComp_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal6=null;
        ConfigParser.listClass_return l = null;


        Object string_literal6_tree=null;

        try {
            // ../../src/groove/explore/chscenar/parser/Config.g:68:2: ( ':STRATEGY' l= listClass[ScenarioChecker.Component.STRATEGY] )
            // ../../src/groove/explore/chscenar/parser/Config.g:68:4: ':STRATEGY' l= listClass[ScenarioChecker.Component.STRATEGY]
            {
            root_0 = (Object)adaptor.nil();

            string_literal6=(Token)match(input,10,FOLLOW_10_in_strComp182); 
            string_literal6_tree = (Object)adaptor.create(string_literal6);
            adaptor.addChild(root_0, string_literal6_tree);

            pushFollow(FOLLOW_listClass_in_strComp186);
            l=listClass(ScenarioChecker.Component.STRATEGY);

            state._fsp--;

            adaptor.addChild(root_0, l.getTree());

            	retval.value = (l!=null?l.value:null);
            	

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
    // $ANTLR end strComp

    public static class resComp_return extends ParserRuleReturnScope {
        public ArrayList<Class<?>> value;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start resComp
    // ../../src/groove/explore/chscenar/parser/Config.g:74:1: resComp returns [ArrayList<Class<?>> value] : ':RESULT' l= listClass[ScenarioChecker.Component.RESULT] ;
    public final ConfigParser.resComp_return resComp() throws RecognitionException {
        ConfigParser.resComp_return retval = new ConfigParser.resComp_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal7=null;
        ConfigParser.listClass_return l = null;


        Object string_literal7_tree=null;

        try {
            // ../../src/groove/explore/chscenar/parser/Config.g:75:2: ( ':RESULT' l= listClass[ScenarioChecker.Component.RESULT] )
            // ../../src/groove/explore/chscenar/parser/Config.g:75:4: ':RESULT' l= listClass[ScenarioChecker.Component.RESULT]
            {
            root_0 = (Object)adaptor.nil();

            string_literal7=(Token)match(input,11,FOLLOW_11_in_resComp206); 
            string_literal7_tree = (Object)adaptor.create(string_literal7);
            adaptor.addChild(root_0, string_literal7_tree);

            pushFollow(FOLLOW_listClass_in_resComp210);
            l=listClass(ScenarioChecker.Component.RESULT);

            state._fsp--;

            adaptor.addChild(root_0, l.getTree());

            	retval.value = (l!=null?l.value:null);
            	

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
    // $ANTLR end resComp

    public static class accComp_return extends ParserRuleReturnScope {
        public ArrayList<Class<?>> value;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start accComp
    // ../../src/groove/explore/chscenar/parser/Config.g:81:1: accComp returns [ArrayList<Class<?>> value] : ':ACCEPTOR' l= listClass[ScenarioChecker.Component.ACCEPTOR] ;
    public final ConfigParser.accComp_return accComp() throws RecognitionException {
        ConfigParser.accComp_return retval = new ConfigParser.accComp_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal8=null;
        ConfigParser.listClass_return l = null;


        Object string_literal8_tree=null;

        try {
            // ../../src/groove/explore/chscenar/parser/Config.g:82:2: ( ':ACCEPTOR' l= listClass[ScenarioChecker.Component.ACCEPTOR] )
            // ../../src/groove/explore/chscenar/parser/Config.g:82:4: ':ACCEPTOR' l= listClass[ScenarioChecker.Component.ACCEPTOR]
            {
            root_0 = (Object)adaptor.nil();

            string_literal8=(Token)match(input,12,FOLLOW_12_in_accComp232); 
            string_literal8_tree = (Object)adaptor.create(string_literal8);
            adaptor.addChild(root_0, string_literal8_tree);

            pushFollow(FOLLOW_listClass_in_accComp236);
            l=listClass(ScenarioChecker.Component.ACCEPTOR);

            state._fsp--;

            adaptor.addChild(root_0, l.getTree());

            	retval.value = (l!=null?l.value:null);
            	

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
    // $ANTLR end accComp

    public static class listClass_return extends ParserRuleReturnScope {
        public ArrayList<Class<?>> value;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start listClass
    // ../../src/groove/explore/chscenar/parser/Config.g:88:1: listClass[ScenarioChecker.Component comp] returns [ArrayList<Class<?>> value] : cn= className ( ',' cn= className )* ;
    public final ConfigParser.listClass_return listClass(ScenarioChecker.Component comp) throws RecognitionException {
        ConfigParser.listClass_return retval = new ConfigParser.listClass_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal9=null;
        ConfigParser.className_return cn = null;


        Object char_literal9_tree=null;

        try {
            // ../../src/groove/explore/chscenar/parser/Config.g:89:2: (cn= className ( ',' cn= className )* )
            // ../../src/groove/explore/chscenar/parser/Config.g:90:2: cn= className ( ',' cn= className )*
            {
            root_0 = (Object)adaptor.nil();

            retval.value = new ArrayList<Class<?>>();
            pushFollow(FOLLOW_className_in_listClass264);
            cn=className();

            state._fsp--;

            adaptor.addChild(root_0, cn.getTree());

            	    retval.value.add(getClass((cn!=null?cn.value:null), comp));
            	
            // ../../src/groove/explore/chscenar/parser/Config.g:94:2: ( ',' cn= className )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==13) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // ../../src/groove/explore/chscenar/parser/Config.g:94:3: ',' cn= className
            	    {
            	    char_literal9=(Token)match(input,13,FOLLOW_13_in_listClass270); 
            	    char_literal9_tree = (Object)adaptor.create(char_literal9);
            	    adaptor.addChild(root_0, char_literal9_tree);

            	    pushFollow(FOLLOW_className_in_listClass274);
            	    cn=className();

            	    state._fsp--;

            	    adaptor.addChild(root_0, cn.getTree());

            	    	    retval.value.add(getClass((cn!=null?cn.value:null), comp));
            	    	

            	    }
            	    break;

            	default :
            	    break loop3;
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
    // $ANTLR end listClass

    public static class className_return extends ParserRuleReturnScope {
        public String value;
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start className
    // ../../src/groove/explore/chscenar/parser/Config.g:100:1: className returns [String value] : id= ID ( '.' id= ID )* ;
    public final ConfigParser.className_return className() throws RecognitionException {
        ConfigParser.className_return retval = new ConfigParser.className_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token id=null;
        Token char_literal10=null;

        Object id_tree=null;
        Object char_literal10_tree=null;

        try {
            // ../../src/groove/explore/chscenar/parser/Config.g:101:2: (id= ID ( '.' id= ID )* )
            // ../../src/groove/explore/chscenar/parser/Config.g:101:4: id= ID ( '.' id= ID )*
            {
            root_0 = (Object)adaptor.nil();

            id=(Token)match(input,ID,FOLLOW_ID_in_className297); 
            id_tree = (Object)adaptor.create(id);
            adaptor.addChild(root_0, id_tree);

            retval.value = (id!=null?id.getText():null);
            // ../../src/groove/explore/chscenar/parser/Config.g:102:2: ( '.' id= ID )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==14) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // ../../src/groove/explore/chscenar/parser/Config.g:102:3: '.' id= ID
            	    {
            	    char_literal10=(Token)match(input,14,FOLLOW_14_in_className303); 
            	    char_literal10_tree = (Object)adaptor.create(char_literal10);
            	    adaptor.addChild(root_0, char_literal10_tree);

            	    id=(Token)match(input,ID,FOLLOW_ID_in_className306); 
            	    id_tree = (Object)adaptor.create(id);
            	    adaptor.addChild(root_0, id_tree);

            	    retval.value = retval.value+'.'+(id!=null?id.getText():null);

            	    }
            	    break;

            	default :
            	    break loop4;
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
    // $ANTLR end className

    // Delegated rules


 

    public static final BitSet FOLLOW_rule_in_prog64 = new BitSet(new long[]{0x0000000000000082L});
    public static final BitSet FOLLOW_7_in_rule85 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_allowRule_in_rule91 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_denyRule_in_rule94 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_8_in_allowRule107 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_configSet_in_allowRule113 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_9_in_denyRule128 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_configSet_in_denyRule133 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_strComp_in_configSet156 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_resComp_in_configSet160 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_accComp_in_configSet164 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_10_in_strComp182 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_listClass_in_strComp186 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_11_in_resComp206 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_listClass_in_resComp210 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_12_in_accComp232 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_listClass_in_accComp236 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_className_in_listClass264 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_13_in_listClass270 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_className_in_listClass274 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_ID_in_className297 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_14_in_className303 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_className306 = new BitSet(new long[]{0x0000000000004002L});

}
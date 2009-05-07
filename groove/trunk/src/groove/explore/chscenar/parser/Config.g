grammar Config;

options {
  output=AST;
  k=2;
}
@lexer::header {
package groove.explore.chscenar.parser;
import groove.explore.chscenar.*;
}

@header {
package groove.explore.chscenar.parser;
import groove.explore.chscenar.*;
import java.util.ArrayList;
}

@members {
	Class<?> getClass(String name, ScenarioChecker.Component c) {
	    try {
		return ScenarioChecker.getClass(name, c);
	    } catch (ClassNotFoundException e) {
		System.err.println("Class " + name + " not found. Aborting");
		System.exit(1);
		return null;
	    }
	}

}

 
prog returns [AllowRule value] 
	:
	{$value = new AllowRuleUnion();}
	(r=rule {((AllowRuleUnion) $value).addRule($r.value);})+ 
	;

rule returns [AllowRuleImpl value] 
	: 'RULE'  {$value = new AllowRuleImpl();}
	allowRule[$value] denyRule[$value]*;

allowRule[AllowRuleImpl r] 
	: '::ALLOW' 
	set=configSet {$r.setAllowed($set.value); }
	;

denyRule[AllowRuleImpl r] 
	: '::DENY'
	set=configSet {$r.addForbidden($set.value);}
	;

configSet returns [SRASetImpl value] 
	: {$value = new SRASetImpl();}
	sc=strComp rc=resComp ac=accComp {
	    for (Class c : $sc.value) {
	        $value.addStrategy(c);
	    }
	    for (Class c : $rc.value) {
	        $value.addResult(c);
	    }
	    for (Class c : $ac.value) {
	        $value.addAcceptor(c);
	    }	
	}
	;
	
strComp returns [ArrayList<Class<?>> value]
	:	':STRATEGY' l=listClass[ScenarioChecker.Component.STRATEGY]
	{
	$value = $l.value;
	}
	;

resComp	returns [ArrayList<Class<?>> value] 
	:	':RESULT' l=listClass[ScenarioChecker.Component.RESULT] 
	{
	$value = $l.value;
	}
	;
	
accComp	returns [ArrayList<Class<?>> value] 
	:	':ACCEPTOR' l=listClass[ScenarioChecker.Component.ACCEPTOR] 
	{
	$value = $l.value;
	}
	;

listClass[ScenarioChecker.Component comp] returns [ArrayList<Class<?>> value] 
	:
	{$value = new ArrayList<Class<?>>();}
	cn=className {
	    $value.add(getClass($cn.value, $comp));
	}
	(',' cn=className {
	    $value.add(getClass($cn.value, $comp));
	}
	)*
	;

className returns [String value]
	: id=ID {$value = $id.text;}
	('.'id=ID {$value = $value+'.'+$id.text;} )* 
	;
                
/* 
stat:   expr NEWLINE {System.out.println($expr.value);}
    |   ID '=' expr NEWLINE
        {memory.put($ID.text, new Integer($expr.value));}
    |   NEWLINE
    ;

expr returns [int value]
    :   e=multExpr {$value = $e.value;}
        (   '+' e=multExpr {$value += $e.value;}
        |   '-' e=multExpr {$value -= $e.value;}
        )*
    ;

multExpr returns [int value]
    :   e=atom {$value = $e.value;} ('*' e=atom {$value *= $e.value;})*
    ; 

atom returns [int value]
    :   INT {$value = Integer.parseInt($INT.text);}
    |   ID
        {
        Integer v = (Integer)memory.get($ID.text);
        if ( v!=null ) $value = v.intValue();
        else System.err.println("undefined variable "+$ID.text);
        }
    |   '(' e=expr ')' {$value = $e.value;}
    ;

*/

ID  :	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')* ;
WS  :   (' '|'\t'| '\r'? '\n')+ {skip();} ;
LINE_COMMENT
    : '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    ;

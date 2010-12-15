grammar GCLNew;

options {
	output=AST;
	k=4;
	ASTLabelType = CommonTree;
}

tokens {
  ARG;
  ARGS;
	BLOCK;
	CALL;
  DO_WHILE;
  DO_UNTIL;
	FUNCTION;
	FUNCTIONS;
	PROGRAM;
	VAR;
}

@lexer::header {
package groove.control.parse;
import groove.control.*;
import java.util.LinkedList;
}

@header {
package groove.control.parse;
import groove.control.*;
import java.util.LinkedList;
}

@members {
    /** Lexer for the GCL language. */
    private static GCLNewLexer lexer = new GCLNewLexer(null);
    /** Helper class to convert AST trees to namespace. */
    private GCLHelper helper;
    
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
    public MyTree run(CharStream input, NamespaceNew namespace) throws RecognitionException {
        this.helper = new GCLHelper(this, namespace);
        lexer.setCharStream(input);
        setTokenStream(new CommonTokenStream(lexer));
        setTreeAdaptor(new MyTreeAdaptor());
        return (MyTree) program().getTree();
    }
}

// PARSER rules

program
  : (function|stat)*
    -> ^(PROGRAM ^(FUNCTIONS function*) ^(BLOCK stat*))
  ;

block
	: LCURLY stat* RCURLY -> ^(BLOCK stat*);

function
  : FUNCTION^ ID LPAR! RPAR! block
    { helper.declareFunction($FUNCTION.tree); }
  ;

stat
	: block
	| ALAP^ stat
	| WHILE^ LPAR! cond RPAR! stat
	| UNTIL^ LPAR! cond RPAR! stat
	| DO stat 
	  ( WHILE LPAR cond RPAR -> ^(BLOCK stat ^(WHILE cond stat))
	  | UNTIL LPAR cond RPAR -> ^(BLOCK stat ^(UNTIL cond stat))
	  )
  | IF^ LPAR! cond RPAR! stat ( (ELSE) => ELSE! stat )?
  | TRY^ stat ( (ELSE) => ELSE! stat )?
  | CHOICE^ stat ( (OR) => OR! stat)+
	| expr SEMI!
	| var_decl SEMI!
  ;

cond
	: cond_atom 
	  ( (BAR cond_atom)+ -> ^(CHOICE cond_atom cond_atom+)
	  | -> cond_atom
	  )
	;

cond_atom
	: TRUE | call ;

expr	
	: expr2
	  ( (BAR expr2)+ -> ^(CHOICE expr2 expr2+)
	  | -> expr2
	  )
	;

expr2
  : e=expr_atom
    ( PLUS -> ^(BLOCK $e ^(STAR $e))
    | ASTERISK -> ^(STAR $e)
    | -> $e
    )
  | SHARP expr_atom -> ^(ALAP expr_atom)
  ;

expr_atom
	: ANY
	| OTHER
	| LPAR! expr RPAR!
	| call
	; 

call
	: rule_name arg_list?
	  -> ^(CALL rule_name arg_list?)
	;

/** Returns a flattened rule name. */
rule_name
  : ids+=ID (DOT ids+=ID)*
    -> { helper.toRuleName($ids) }
  ;

var_decl
	: var_type ID (COMMA ID)* -> ^(VAR var_type ID+)
	;

var_type
	: NODE
	| BOOL
	| STRING
	| INT
	| REAL
	;
	
arg_list
	: LPAR (arg (COMMA arg)*)? RPAR
	  -> ^(ARGS arg*)
	;

arg
	: OUT ID -> ^(ARG OUT ID)
	| ID -> ^(ARG ID)
	| DONT_CARE -> ^(ARG DONT_CARE)
	| literal -> ^(ARG literal)
	;

literal
	: TRUE
	| FALSE
	| STRING_LIT
	| INT_LIT
	| REAL_LIT
	;

// LEXER rules

ALAP     : 'alap';
ANY		   : 'any';
BOOL     : 'bool';
CHOICE   : 'choice';
DO       : 'do';
ELSE     : 'else';
FALSE    : 'false';
FUNCTION : 'function';
IF       : 'if';
INT      : 'int';
NODE     : 'node';
OR       : 'or';
OTHER    : 'other';
OUT	     : 'out';
REAL     : 'real';
STAR     : 'string';
STRING   : 'star';
TRY      : 'try';
TRUE     : 'true';
UNTIL    : 'until';
WHILE    : 'while';

INT_LIT
  : IntegerNumber 
  ;

fragment
IntegerNumber
  : '0' 
  | '1'..'9' ('0'..'9')*     
  ;

REAL_LIT
  : NonIntegerNumber
  ;

fragment
NonIntegerNumber
    :   ('0' .. '9')+ '.' ('0' .. '9')*
    |   '.' ( '0' .. '9' )+
    ;

STRING_LIT
// @after{ setText(toUnquoted($text)); }
  : QUOTE 
    ( EscapeSequence
    | ~( BSLASH | QUOTE | '\r' | '\n'  )        
    )* 
    QUOTE 
  ;

fragment
EscapeSequence 
  : BSLASH
    ( QUOTE
      BSLASH 
    )          
  ;    

ID  : ('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'-')*;

AMP       : '&' ;
DOT       : '.' ;
NOT       : '!' ;
BAR       : '|' ;
SHARP     : '#' ;
PLUS      : '+' ;
ASTERISK  : '*' ;
DONT_CARE	: '_' ;
MINUS     : '-' ;
QUOTE     : '"' ;
BSLASH    : '\\';
COMMA     : ',' ;
SEMI      : ';' ;
LPAR      : '(' ;
RPAR      : ')' ;
LCURLY    : '{' ;
RCURLY    : '}' ;

ML_COMMENT : '/*' ( options {greedy=false;} : . )* '*/' { $channel=HIDDEN; };
SL_COMMENT : '//' ( options {greedy=false;} : . )* '\n' { $channel=HIDDEN; };

WS  :   (   ' '
        |   '\t'
        |   '\r'
        |   '\n'
        )+
        { $channel=HIDDEN; }
    ;    

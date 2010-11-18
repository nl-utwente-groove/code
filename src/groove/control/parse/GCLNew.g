grammar GCLNew;

options {
	output=AST;
	k=4;
	ASTLabelType = CommonTree;
}

tokens {
	PROGRAM;
	BLOCK;
	FUNCTIONS;
	FUNCTION;
	CALL;
  DO_WHILE;
  DO_UNTIL;
	VAR;
	ARG;
}

@lexer::header {
package groove.control.parse;
import groove.control.*;
import java.util.LinkedList;
}

@lexer::members{
    /** Strips the outer (double) quotes and unescapes all characters in a string.
     * Returns a new {@link CommonTree} with {@link GCLNewParser#ID} root token
     * and the stripped string as text.
     */
    private String toUnquoted(String text) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\\') {
                i++;
                c = text.charAt(i);
                result.append(c);
            } else if (c != '"') {
                result.append(c);
            }
        }
        return result.toString();
    }
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
    public Tree run(CharStream input, NamespaceNew namespace) throws RecognitionException {
        this.helper = new GCLHelper(this, namespace);
        lexer.setCharStream(input);
        return (Tree) program().getTree();
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
    | STAR -> ^(STAR $e)
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
	: rule_name (LPAR arg_list? RPAR)?
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
	: arg (COMMA! arg)*
	;

arg
	: OUT ID -> ^(ARG OUT ID)
	| ID -> ^(ARG ID)
	| DONT_CARE -> ^(ARG DONT_CARE)
	| literal -> ^(ARG literal)
	;

literal
	: TRUE -> ^(BOOL TRUE)
	| FALSE -> ^(BOOL FALSE)
	| STRING_LIT -> ^(STRING STRING_LIT)
	| INT_LIT -> ^(INT INT_LIT)
	| REAL_LIT -> ^(REAL REAL_LIT)
	;

// LEXER rules

ALAP     :	'alap';
WHILE    :	'while';
DO       :	'do';
UNTIL    :	'until';
IF       :	'if';
ELSE     :	'else';
CHOICE   :	'choice';
OR       :	'or';
TRY      :	'try';
FUNCTION :	'function';
TRUE     :	'true';
FALSE    :	'false';
OTHER    : 'other';
ANY		   : 'any';
NODE     : 'node';
BOOL     : 'bool';
STRING   : 'string';
INT      : 'int';
REAL     : 'real';
OUT	     :	'out';

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
@after{ setText(toUnquoted($text)); }
  : QUOTE 
    ( EscapeSequence
    | ~( BSLASH | QUOTE | CR | NL  )        
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

CR        : '\r';
NL        : '\n';
AMP       : '&' ;
DOT       : '.' ;
NOT       : '!' ;
BAR       : '|' ;
SHARP     : '#' ;
PLUS      : '+' ;
STAR     	: '*' ;
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

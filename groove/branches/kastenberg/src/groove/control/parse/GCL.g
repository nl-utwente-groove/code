grammar GCL;

options {
	output=AST;
	k=2;
}

tokens {
	PROGRAM;
	BLOCK;
	FUNCTIONS;
	FUNCTION;
	CALL;
	DO;
	VAR;
	PARAM;
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
}

// PARSER rules

program : (function|statement)* -> ^(PROGRAM ^(FUNCTIONS function*) ^(BLOCK statement*));

block	: '{' statement*  '}' -> ^(BLOCK statement*);

function : FUNCTION IDENTIFIER '(' ')' block -> ^(FUNCTION IDENTIFIER block);

condition
	: conditionliteral (OR^ condition)?
	;
	
statement 
	: ALAP block -> ^(ALAP block)
	| WHILE '(' condition ')' DO? block -> ^(WHILE condition block)
	| UNTIL '(' condition ')' DO? block -> ^(UNTIL condition block)
	| DO block WHILE '(' condition ')' -> ^(DO block condition)
	| TRY block (ELSE block)? -> ^(TRY block+)
	| IF '(' condition ')' block (ELSE block)? -> ^(IF condition block+)
    | CHOICE block (CH_OR block)* -> ^(CHOICE block+)
	| expression ';' -> expression
	| var_declaration ';' -> var_declaration 
    ;

conditionliteral
	: TRUE | call ;

expression	
	: expression2 (OR^ expression)?
	;

expression2
    : expression_atom (PLUS^ | STAR^)?
    | SHARP^ expression_atom
    ;

expression_atom
	: ANY
	| OTHER
	| rule
	| '('! expression ')'!
	| call
	; 

call
	: IDENTIFIER '(' var_list? ')' -> ^(CALL IDENTIFIER var_list?);

rule 	: IDENTIFIER -> ^(CALL IDENTIFIER);

var_declaration
	: var_type IDENTIFIER (',' IDENTIFIER)* -> ^(VAR var_type IDENTIFIER)+
	;

var_type
	: NODE_TYPE
	;
	
var_list
	: variable (COMMA! var_list)?
	;
	
variable
	: OUT IDENTIFIER -> ^(PARAM OUT IDENTIFIER)
	| IDENTIFIER -> ^(PARAM IDENTIFIER)
	| DONT_CARE -> ^(PARAM DONT_CARE)
	;

// LEXER rules

ALAP 	:	'alap';
WHILE	:	'while';
DO		:	'do';
UNTIL   :	'until';
IF		:	'if';
ELSE	:	'else';
CHOICE	:	'choice';
CH_OR 	:	'or';
TRY		:	'try';
FUNCTION:	'function';
TRUE	:	'true';
OTHER	:	'other';
ANY		:	'any';
NODE_TYPE : 'node';
OUT		:	'out';


IDENTIFIER 	: ('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9'|'-'|'_'|'.')*;

AND 	:	 '&';
COMMA 	:	 ',' ;
DOT 	:	 '.' ;
NOT 	:	 '!';
OR 	:	 '|';
SHARP 	:	 '#' ;
PLUS 	:	 '+' ;
STAR 	:	 '*' ;
DONT_CARE	: '_';

ML_COMMENT : '/*' ( options {greedy=false;} : . )* '*/' { $channel=HIDDEN; };
SL_COMMENT : '//' ( options {greedy=false;} : . )* '\n' { $channel=HIDDEN; };

WS  :   (   ' '
        |   '\t'
        |   '\r'
        |   '\n'
        )+
        { $channel=HIDDEN; }
    ;    
    

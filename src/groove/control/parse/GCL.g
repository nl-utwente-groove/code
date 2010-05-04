grammar GCL;

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

	CommonTree concat(CommonTree seq) {
        String result;
        List children = seq.getChildren();
        if (children == null) {
            result = seq.getText();
        } else {
            StringBuilder builder = new StringBuilder();
            for (Object token: seq.getChildren()) {
                builder.append(((CommonTree) token).getText());
            }
            result = builder.toString();
        }
        return new CommonTree(new CommonToken(IDENTIFIER, result));
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
	| ifstatement
    | CHOICE block (CH_OR block)* -> ^(CHOICE block+)
	| expression ';' -> expression
	| var_declaration ';' -> var_declaration 
    ;

ifstatement
    : IF '(' condition ')' block (ELSE elseblock)? -> ^(IF condition block elseblock?)
    | TRY block (ELSE elseblock)? -> ^(TRY block elseblock?)
    ;
    
elseblock
    : block
    | ifstatement -> ^(BLOCK ifstatement)
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
	| '('! expression ')'!
	| call
	; 

call
	: ruleName ('(' var_list? ')')? -> ^(CALL { concat($ruleName.tree) } var_list?);

ruleName : IDENTIFIER (DOT IDENTIFIER)*;

var_declaration
	: var_type IDENTIFIER (',' IDENTIFIER)* -> ^(VAR var_type IDENTIFIER)+
	;

var_type
	: NODE_TYPE
	| BOOL_TYPE
	| STRING_TYPE
	| INT_TYPE
	| REAL_TYPE
	;
	
var_list
	: variable (COMMA! var_list)?
	;
	
variable
	: OUT IDENTIFIER -> ^(PARAM OUT IDENTIFIER)
	| IDENTIFIER -> ^(PARAM IDENTIFIER)
	| DONT_CARE -> ^(PARAM DONT_CARE)
	| literal -> ^(PARAM literal)
	;
	
literal
	: TRUE -> BOOL_TYPE TRUE
	| FALSE -> BOOL_TYPE FALSE
	| dqText -> STRING_TYPE dqText
	| integer -> INT_TYPE { concat($integer.tree) }
	| real -> REAL_TYPE { concat($real.tree) }
	;

dqText
   : QUOTE dqContent QUOTE -> { concat($dqContent.tree) }
   ;

dqContent
   : dqTextChar*
   ;

dqTextChar
   : ~(QUOTE|BSLASH)
   | BSLASH (BSLASH|QUOTE)
   ;

real
	: MINUS? n1=NUMBER? DOT n2=NUMBER?;
	
integer
	: MINUS? NUMBER;

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
FALSE	:	'false';
OTHER	:	'other';
ANY		:	'any';
NODE_TYPE : 'node';
BOOL_TYPE : 'bool';
STRING_TYPE : 'string';
INT_TYPE : 'int';
REAL_TYPE : 'real';
OUT		:	'out';


AND 	:	 '&';
COMMA 	:	 ',' ;
DOT 	:	 '.' ;
NOT 	:	 '!';
OR 	:	 '|';
SHARP 	:	 '#' ;
PLUS 	:	 '+' ;
STAR 	:	 '*' ;
DONT_CARE	: '_';
MINUS : '-';
QUOTE   : '"';
BSLASH : '\\';

IDENTIFIER 	: ('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*;
//STRING : QUOTE (options {greedy=false;} : .)* QUOTE ;
//{ setText(getText().substring(1, getText().length()-1)); };
NUMBER : ('0'..'9')+;


ML_COMMENT : '/*' ( options {greedy=false;} : . )* '*/' { $channel=HIDDEN; };
SL_COMMENT : '//' ( options {greedy=false;} : . )* '\n' { $channel=HIDDEN; };

WS  :   (   ' '
        |   '\t'
        |   '\r'
        |   '\n'
        )+
        { $channel=HIDDEN; }
    ;    
    

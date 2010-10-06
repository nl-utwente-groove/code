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
	VAR;
	ARG;
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
        return new CommonTree(new CommonToken(ID, result));
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
  ;

stat
	: block
	| ALAP stat -> ^(ALAP stat)
	| WHILE^ LPAR! cond RPAR! stat
	| UNTIL^ LPAR! cond RPAR! stat
	| DO stat WHILE LPAR cond RPAR -> ^(BLOCK stat ^(WHILE cond stat))
  | IF^ LPAR! cond RPAR! stat ( (ELSE) => ELSE! stat )?
  | TRY^ stat ( (ELSE) => ELSE! stat )?
  | CHOICE^ stat ((CH_OR) => CH_OR! stat)+
	| expr SEMI!
	| var_decl SEMI!
  ;

cond
	: cond_atom 
	  ( (OR cond_atom)+ -> ^(CHOICE cond_atom cond_atom+)
	  | -> cond_atom
	  )
	;

cond_atom
	: TRUE | call ;

expr	
	: expr2
	  ( (OR expr2)+ -> ^(CHOICE expr2 expr2+)
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
	  -> ^(CALL { concat($rule_name.tree) } arg_list?)
	;

rule_name
  : ID (DOT ID)*
  ;

var_decl
	: var_type ID (COMMA ID)* -> ^(VAR var_type ID+)
	;

var_type
	: NODE_TYPE
	| BOOL_TYPE
	| STRING_TYPE
	| INT_TYPE
	| REAL_TYPE
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
	: TRUE -> ^(BOOL_TYPE TRUE)
	| FALSE -> ^(BOOL_TYPE FALSE)
	| STRING -> ^(STRING_TYPE { toUnquoted($STRING.text) } )
	| integer -> ^(INT_TYPE { concat($integer.tree) } )
	| real -> ^(REAL_TYPE { concat($real.tree) } )
	;

real
	: MINUS? NUMBER? DOT NUMBER?;
	
integer
	: MINUS? NUMBER;

// LEXER rules

ALAP        :	'alap';
WHILE       :	'while';
DO          :	'do';
UNTIL       :	'until';
IF          :	'if';
ELSE        :	'else';
CHOICE      :	'choice';
CH_OR       :	'or';
TRY         :	'try';
FUNCTION    :	'function';
TRUE        :	'true';
FALSE       :	'false';
OTHER       : 'other';
ANY		      : 'any';
NODE_TYPE   : 'node';
BOOL_TYPE   : 'bool';
STRING_TYPE : 'string';
INT_TYPE    : 'int';
REAL_TYPE   : 'real';
OUT	        :	'out';

AND       : '&' ;
DOT       : '.' ;
NOT       : '!' ;
OR        : '|' ;
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

ID 	: ('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'-')*;

NUMBER : ('0'..'9')+;

STRING : QUOTE (~(QUOTE|BSLASH) | BSLASH(QUOTE|BSLASH))* QUOTE ;

ML_COMMENT : '/*' ( options {greedy=false;} : . )* '*/' { $channel=HIDDEN; };
SL_COMMENT : '//' ( options {greedy=false;} : . )* '\n' { $channel=HIDDEN; };

WS  :   (   ' '
        |   '\t'
        |   '\r'
        |   '\n'
        )+
        { $channel=HIDDEN; }
    ;    

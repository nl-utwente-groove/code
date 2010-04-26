grammar Label0;

options {
  language = Java;
  output = AST;
}

tokens {
  NEW='new';
  DEL='del';
  NOT='not';
  USE='use';
  CNEW='cnew';
  REM='rem';
  
  FORALL='forall';
  FORALLX='forallx';
  EXISTS='exists';
  NESTED='nested';
  
  INT='int';
  REAL='real';
  STRING='string';
  BOOL='bool';
  ATTR='attr';
  PROD='prod';
  ARG='arg';
  
  PAR='par';
  
  TYPE='type';
  FLAG='flag';
  PATH='path';
  EMPTY;
  ATOM;
  
  TRUE='true';
  FALSE='false';
  
  CONSTRAINT;
}

@lexer::header {
package groove.view.parse;
}

@header {
package groove.view.parse;
import java.util.LinkedList;
}

@members {
    private boolean isGraph;
    public void setIsGraph(boolean isGraph) {
        this.isGraph = isGraph;
    }
    
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

label
   : (prefixedLabel | specialLabel)? EOF!
   ;

prefixedLabel
   : ( FORALL^ | FORALLX^ | EXISTS^ ) (EQUALS! IDENT)? COLON! prefixedLabel
   | ( NEW^ | DEL^ | NOT^ | USE^ | CNEW^ ) (EQUALS! IDENT)? COLON! prefixedLabel
   | actualLabel
   ;

specialLabel
   : REM^ COLON! text
   | PAR^ (EQUALS! LABEL)? COLON!
   | NESTED^ COLON! IDENT
   // attribute-related labels
   | INT^ COLON! (NUMBER | IDENT)?
   | REAL^ COLON! (rnumber | IDENT)?
   | STRING^ COLON! (dqText | IDENT)?
   | BOOL^ COLON! (TRUE | FALSE | IDENT)?
   | ATTR^ COLON!
   | PROD^ COLON!
   | ARG^ COLON! NUMBER
   ;

actualLabel
   : TYPE COLON IDENT -> ^(ATOM ^(TYPE IDENT))
   | FLAG COLON IDENT -> ^(ATOM ^(FLAG IDENT))
   | COLON text -> ^(ATOM text)
   | PATH! COLON! regExpr
   | (graphDefault EOF) => { isGraph }? => graphLabel
   | (ruleLabel EOF) => { !isGraph }? => ruleLabel
   ;

text
   : (~'\n')*
   ;

graphLabel
   : graphDefault -> ^(ATOM graphDefault)
   ;

graphDefault
   : (~COLON)+
   ;

ruleLabel
   : wildcard
   | EQUALS
   | LBRACE! regExpr RBRACE!
   | sqText -> ^(ATOM sqText)
   | PLING^ ruleLabel
   | ruleDefault -> ^(ATOM ruleDefault)
   ;

ruleDefault
   : ~(EQUALS | QUERY | PLING | SQUOTE | LBRACE | RBRACE | BSLASH | COLON) 
     ~(SQUOTE | LBRACE | RBRACE | BSLASH | COLON)*
   ;

nodeLabel
   : TYPE^ COLON! IDENT
   | FLAG^ COLON! IDENT;
   
rnumber
   : NUMBER (DOT NUMBER?)?
   | DOT NUMBER
   ;

regExpr
   : choice | PLING^ regExpr;

choice
   : sequence (BAR^ choice)? ;

sequence
   : unary (DOT^ sequence)? ;

unary
   : MINUS^ unary
   | atom (STAR^ | PLUS^)?
   | EQUALS
   | LPAR! regExpr RPAR! (STAR^ | PLUS^)?
   | wildcard (STAR^ | PLUS^)?
   ;

atom
   : sqText -> ^(ATOM sqText)
   | atomLabel -> ^(ATOM atomLabel)
   ;

atomLabel
   : NUMBER | IDENT | LABEL
   ;
   
wildcard
   : QUERY^ IDENT? (LSQUARE! HAT? atom (COMMA! atom)* RSQUARE!)?
   ;

sqText
   : SQUOTE! (~(SQUOTE|BSLASH) | sqTextSpecial)* SQUOTE!;

sqTextSpecial
   : BSLASH! (BSLASH|SQUOTE)
   ;

dqText
   : DQUOTE^ (~(DQUOTE|BSLASH) | dqTextSpecial)* DQUOTE!;

dqTextSpecial
   : BSLASH! (BSLASH|DQUOTE)
   ;

MINUS  : '-';
STAR   : '*';
PLUS   : '+';
DOT    : '.';
BAR    : '|';
HAT    : '^';
EQUALS : '=';
LBRACE : '{';
RBRACE : '}';
LPAR   : '(';
RPAR   : ')';
LSQUARE : '[';
RSQUARE : ']';
PLING  : '!';
QUERY  : '?'; 
COLON  : ':' ;
COMMA  : ',' ;
SQUOTE : '\'' ;
DQUOTE : '"' ;
DOLLAR : '$';
UNDER  : '_';
BSLASH : '\\' ;

IDENT
   : LETTER IDENTCHAR* 
   ;
   
NUMBER
   : DIGIT+
   ;

LABEL
   : IDENTCHAR+
   ;

fragment IDENTCHAR
   : LETTER | DIGIT | DOLLAR | UNDER;

fragment LETTER : 'a'..'z'|'A'..'Z' ;
fragment DIGIT  : '0'..'9' ;

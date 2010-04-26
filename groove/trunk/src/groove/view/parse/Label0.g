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
  
  MINUS  = '-';
  STAR   = '*';
  PLUS   = '+';
  DOT    = '.';
  BAR    = '|';
  HAT    = '^';
  EQUALS = '=';
  LBRACE = '{';
  RBRACE = '}';
  LPAR   = '(';
  RPAR   = ')';
  LSQUARE = '[';
  RSQUARE = ']';
  PLING  = '!';
  QUERY  = '?'; 
  COLON  = ':' ;
  COMMA  = ',' ;
  SQUOTE = '\'' ;
  DQUOTE = '"' ;
  DOLLAR = '$';
  UNDER  = '_';
  BSLASH = '\\' ;
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
   : quantLabel EOF!
   | specialLabel EOF!
   ;

quantLabel
   : quantPrefix
     ( EQUALS IDENT COLON
       ( rolePrefix COLON actualLabel
         -> ^(rolePrefix IDENT actualLabel)
       | actualLabel
         -> ^(USE IDENT actualLabel)
       | -> ^(quantPrefix IDENT)
       )
     | COLON
         -> quantPrefix
     )
   | roleLabel
   ;

quantPrefix
   : FORALL | FORALLX | EXISTS
   ;

roleLabel
   : rolePrefix
     ( EQUALS IDENT COLON actualLabel
         -> ^(rolePrefix IDENT actualLabel)
     | COLON 
       ( actualLabel
         -> ^(rolePrefix actualLabel)
       | -> rolePrefix
       )
     )
   | actualLabel
   ;

rolePrefix
   : NEW | DEL | NOT | USE | CNEW
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
   : PLING^
     ( simpleRuleLabel
     | LBRACE! regExpr RBRACE!
     )
   | simpleRuleLabel
   | LBRACE!
     ( PLING^ unary
     | regExpr
     )
     RBRACE!
   ;

simpleRuleLabel
   : wildcard
   | EQUALS
   | sqText -> ^(ATOM sqText)
   | ruleDefault -> ^(ATOM ruleDefault)
   ;

ruleDefault
   : ~(EQUALS | QUERY | PLING | SQUOTE | LBRACE | RBRACE | BSLASH | COLON) 
     ~(SQUOTE | LBRACE | RBRACE | BSLASH | COLON)*
   ;

rnumber
   : NUMBER (DOT NUMBER?)?
   | DOT NUMBER
   ;

regExpr
   : choice
   ;

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

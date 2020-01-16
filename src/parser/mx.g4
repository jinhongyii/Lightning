//adapted from official java grammar file
grammar mx;

//lexer part
//keyword
INT:'int';
BOOL: 'bool';
STRING:'string';
VOID:'void';
IF:'if';
ELSE:'else';
FOR:'for';
WHILE:'while';
BREAK:'break';
CONTINUE:'continue';
RETURN:'return';
NEW:'new';
CLASS:'class';
THIS:'this';


STRING_LITERAL:     '"' (~["\\\r\n]| '\\' ["n\\])* '"';
BOOL_LITERAL:       'true'
            |       'false'
            ;
NULL_LITERAL:       'null';
DECIMAL_LITERAL
    : [1-9] [0-9]*
    | '0'
    ;
IDENTIFIER
    : [a-zA-Z_] [a-zA-Z_0-9]*
    ;

// Whitespace and comments
WS:                 [ \t\r\n\u000C]+ -> channel(HIDDEN);
COMMENT:            '/*' .*? '*/'    -> channel(HIDDEN);
LINE_COMMENT:       '//' ~[\r\n]*    -> channel(HIDDEN);


//parser part
compilationUnit:(classDeclaration| funcDeclaration|variableDeclaration|';')*;
classDeclaration: CLASS IDENTIFIER classBody;
classBody: '{' classBodyDeclaration* '}';
classBodyDeclaration:variableDeclaration ';'
                    | methodDeclaration;
methodDeclaration: (typeTypeOrVoid)? IDENTIFIER parameters block;
variableDeclaration:typeType variableDecorator(',' variableDecorator)* ;
variableDecorator: IDENTIFIER ( '=' expression)?;
typeType: (classType | primitiveType) ('[' ']')*;
typeTypeOrVoid: typeType
                | VOID;
classType:IDENTIFIER;
primitiveType:BOOL
              | INT
              | STRING;
funcDeclaration:typeTypeOrVoid IDENTIFIER parameters block;
parameters: '(' parameterList? ')';
parameterList: parameter (',' parameter)*;
parameter: typeType IDENTIFIER;
block:'{' blockStatement '}' ;
blockStatement
    : statement *
    ;
//oneLineStatement: variableDeclaration';' | statement;
statement
    : blockLabel=block                                  #blockStmt
    | IF '('expression')' statement (ELSE statement)?   #ifStmt
    | FOR '(' forControl ')' statement                  #forStmt
    | WHILE '('expression')' statement                  #whileStmt
    | RETURN expression? ';'                            #returnStmt
    | BREAK ';'                                         #breakStmt
    | CONTINUE ';'                                      #continueStmt
    | ';'                                               #semiStmt
    | statementExpression=expression ';'                #exprStmt
    | variableDeclaration ';'                           #variableDeclStmt
    ;

expression
    : primary                                    #primaryExpr
    | expression bop='.' IDENTIFIER              #memberExpr
    | expression '[' expression ']'              #arrayExpr
    |  expression '(' expressionList? ')'        #methodCallExpr
    | NEW creator                                #newExpr
    | expression postfix=('++' | '--')           #postfixExpr
    | prefix=('+'|'-'|'++'|'--') expression      #prefixExpr
    | prefix=('~'|'!') expression                #prefixExpr
    | expression bop=('*'|'/'|'%') expression    #binaryOpExpr
    | expression bop=('+'|'-') expression        #binaryOpExpr
    | expression bop=('<<' | '>>>' | '>>') expression #binaryOpExpr
    | expression bop=('<=' | '>=' | '>' | '<') expression #binaryOpExpr
    | expression bop=('==' | '!=') expression   #binaryOpExpr
    | expression bop='&' expression             #binaryOpExpr
    | expression bop='^' expression             #binaryOpExpr
    | expression bop='|' expression             #binaryOpExpr
    | expression bop='&&' expression            #binaryOpExpr
    | expression bop='||' expression            #binaryOpExpr
    | <assoc=right> expression bop='=' expression   #binaryOpExpr

;

forControl
    : forinit=expression? ';' forcond=expression? ';' forUpdate=expression?
    ;


expressionList
    : expression (',' expression)*
    ;

primary
    : '(' expression ')' #parenthesizedExpr
    | THIS               #thisExpr
    | literal            #literalExpr
    | IDENTIFIER         #nameExpr
    ;
literal
    : DECIMAL_LITERAL
    | STRING_LITERAL
    | BOOL_LITERAL
    | NULL_LITERAL
    ;

creator
    : (classType |primitiveType) ('[' expression']')+ ('['']')* #arrayCreator
      | (classType |primitiveType) ('(' ')' )?  #constructorCreator
    ;

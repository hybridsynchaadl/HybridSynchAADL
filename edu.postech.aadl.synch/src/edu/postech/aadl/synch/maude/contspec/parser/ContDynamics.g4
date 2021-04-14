grammar ContDynamics;

// x(t) = (-dotx * t) + x(0);
// d/dt(x) = x + y

continuousdynamics 
	: (assignment)* EOF
	;
	
assignment
	: target EQUAL simple_expression (SEMICOLON)?
	;

target
	: DERIV LPAREN value_variable RPAREN 			#ODE
	| value_variable LPAREN value_variable RPAREN 	#ContFunc
	;
	
simple_expression
	: (unary_operator)? term_expression (term_operator term_expression)*
	;

term_expression
	: factor_expression (factor_operator factor_expression)*
	;
	
factor_expression
	: value_expression (value_operator value_expression)?
	;
	
value_expression
	: value_constant					
	| value_variable			
	| LPAREN simple_expression RPAREN	
	;


value_variable
   : VALID_ID_START+ zero='(0)'?
   ;

value_constant
    : CONSTANT_PROPERTY
    ;

 
VALID_ID_START
   : ('a' .. 'z') | ('A' .. 'Z') | '_'
   ;


CONSTANT_PROPERTY
   : VALID_ID_CHAR* '::' VALID_ID_CHAR*
   | ('0' .. '9')+ ('.' ('0' .. '9')+)?
   ;
   
VALID_ID_CHAR
   : VALID_ID_START | ('0' .. '9') 
   ;
	
unary_operator
	: MINUS
	;
	
term_operator
	: PLUS | MINUS
	;
	
factor_operator
	: MUL | DIVIDE 
	;
	
value_operator
	: POWER
	;
   
   
// Keywords
EQUAL          	: '=';
NOTEQUAL       	: '!=';
LESSTHAN       	: '<';
LESSOREQUAL    	: '<=';
GREATERTHAN    	: '>';
GREATEROREQUAL 	: '>=';
PLUS 			: '+';
MINUS			: '-';
MUL				: '*';
POWER			: '^';
DIVIDE 			: '/';
LPAREN			: '(';
RPAREN 			: ')';
DERIV			: 'd/dt';	
SEMICOLON		: ';';
AND				: 'and';
OR				: 'or';
XOR				: 'xor';


WS
   : [ \r\n\t] + -> skip
   ;
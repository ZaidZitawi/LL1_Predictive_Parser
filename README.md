# LL(1) Parser

This project implements an LL(1) Parser, a top-down parser for a subset of context-free grammars. The LL(1) parser reads input from left to right and produces a leftmost derivation with a lookahead of one token.
The parser uses a parsing table to decide which rule to apply based on the current input token and the top of the stack.

## Features

- Parses LL(1) grammars.
- Provides a step-by-step derivation for the input string.
- Show the token table.
- Provide errors for both lexical errors and syntax errors.
- The ability to upload files or write them on your own using real-time editor.


##  Modula-2 Grammar

module-decl       → module-heading declarations block name .

module-heading    → module name ;

block             → begin stmt-list end

declarations      → const-decl var-decl procedure-decl

const-decl        → const const-list | ε

const-list        → name = value ; const-list | ε

var-decl          → var var-list | ε

var-list          → var-item ; var-list | ε

var-item          → name-list : data-type

name-list         → name more-names

more-names        → , name-list | ε

data-type         → integer | real | char

procedure-decl    → procedure-heading declarations block name ; procedure-decl | ε

procedure-heading → procedure name ;

stmt-list         → statement ; stmt-list | ε

statement         → ass-stmt | read-stmt | write-stmt | if-stmt | while-stmt
                   | loop-stmt | exit-stmt | call-stmt | block | ε

ass-stmt          → name := exp

exp               → term exp-prime

exp-prime         → add-oper term exp-prime | ε

term              → factor term-prime

term-prime        → mul-oper factor term-prime | ε

factor            → ( exp ) | name-value

add-oper          → + | -

mul-oper          → * | / | mod | div

read-stmt         → readint ( name-list ) | readreal ( name-list )
                   | readchar ( name-list ) | readln

write-stmt        → writeint ( write-list ) | writereal ( write-list )
                   | writechar ( write-list ) | writeln

write-list        → write-item more-write-value

more-write-value  → , write-list | ε

write-item        → name | value

if-stmt           → if condition then stmt-list else-part end

else-part         → else stmt-list | ε

while-stmt        → while condition do stmt-list end

loop-stmt         → loop stmt-list until condition

exit-stmt         → exit

call-stmt         → call name

condition         → name-value relational-oper name-value

relational-oper   → = | |= | < | <= | > | >=

name-value        → name | value

value             → integer-value | real-value

## Quick sight
### Correct File
![image](https://github.com/ZaidZitawi/LL1_Predictive_Parser/assets/111902956/b92e8fe2-4410-4fcd-89dc-d938d7dec28e)
![image](https://github.com/ZaidZitawi/LL1_Predictive_Parser/assets/111902956/24739155-39e8-4a81-a673-62987d82fd4d)
### Error File
![image](https://github.com/ZaidZitawi/LL1_Predictive_Parser/assets/111902956/9027b2e6-7a7c-424b-a847-61a87779a99b)






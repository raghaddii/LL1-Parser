# LL1-Parser
implement LL(1) parser to introduce three functions : nullable, first and follow beside building parsing table. Finally test the code 

### The grammar :
PROGRAM → STMTS

STMTS → STMT| STMT ; STMTS

STMT → id = EXPR

EXPR → EXPR+TERM|EXPR-TERM|TERM

TERM → TERM * POWER | TERM / POWER | POWER

POWER →  POWER^FACTOR | FACTOR

FACTOR → ( EXPR ) | id | INTEGER

INTEGER → num | plus num | minus mun

### Re-write the grammar such that it can be parsed by an LL(1) parser :
PROGRAM → STMTS
STMTS → STMT S’
S’ →   # |  ; STMTS
STMT → id = EXPR
EXPR → TERM E’
E’ →  +TERM E’ |-TERM E’| #
TERM →  POWER T’
T’ →  * POWER T’ | / POWER T’ | #
POWER →  FACTOR P’
P’ →   ^FACTOR P’
FACTOR → ( EXPR ) | id | INTEGER
INTEGER → num | plus num | minus mun


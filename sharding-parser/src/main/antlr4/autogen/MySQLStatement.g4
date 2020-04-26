
grammar MySQLStatement;

import Symbol, SQLStatement;

execute
    : (use
    | insert
    | update
    | delete
    | select
    ) SEMI_?
    ;

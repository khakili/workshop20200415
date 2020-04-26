
grammar SQLStatement;

import Symbol, Keyword, Literals;

use
    : USE schemaName
    ;
    
schemaName
    : identifier
    ;
    
insert
    : INSERT INTO? tableName columnNames? VALUES assignmentValues
    ;
update
    : UPDATE tableName SET updateField+ WHERE whereCondition
    ;
delete
    : DELETE FROM tableName WHERE whereCondition
    ;
select
    : SELECT ((columnNames)?|(ASTERISK_)?) FROM tableName WHERE whereCondition
    ;
assignmentValues
    : LP_ assignmentValue (COMMA_ assignmentValue)* RP_
    ;

assignmentValue
    : identifier
    ;
    
columnNames
    : LP_ columnName (COMMA_ columnName)* RP_
    ;

columnName
    : identifier
    ;
columnValue
    : identifier
    ;
tableName
    : identifier
    ;
updateField
    : columnName equal columnValue(COMMA_ columnName equal columnValue)*
    ;
whereCondition
    : columnName equal columnValue(COMMA_ columnName equal columnValue)*
    ;
equal
    : EQ_
    ;
identifier
    : IDENTIFIER_ | STRING_ | NUMBER_
    ;
    
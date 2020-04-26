/*
 * @(#)InsertSqlVisitor.java 1.0 2020/4/26
 *
 * Copyright (c) 2019 JDD. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package shardingsphere.workshop.parser.engine.visitor;

import autogen.MySQLStatementBaseVisitor;
import autogen.MySQLStatementParser;
import com.google.common.collect.Lists;
import shardingsphere.workshop.parser.statement.ASTNode;
import shardingsphere.workshop.parser.statement.segment.*;
import shardingsphere.workshop.parser.statement.statement.InsertStatement;

import java.util.List;

/**
 * TODO
 *
 * @author like175
 * @version 1.0
 * @since 1.0
 */
public class InsertSqlVisitor extends MySQLStatementBaseVisitor<ASTNode> {
    @Override
    public ASTNode visitInsert(MySQLStatementParser.InsertContext ctx) {
        TableNameSegment tableNameSegment = (TableNameSegment) visit(ctx.tableName());
        ColumnNamesSegment columnNamesSegment = (ColumnNamesSegment) visit(ctx.columnNames());
        AssignmentValuesSegment assignmentValuesSegment = (AssignmentValuesSegment) visit(ctx.assignmentValues());
        InsertStatement insertStatement = new InsertStatement(tableNameSegment,columnNamesSegment,assignmentValuesSegment);
        return insertStatement;
    }

    @Override
    public ASTNode visitTableName(MySQLStatementParser.TableNameContext ctx) {
        IdentifierSegment identifierSegment = new IdentifierSegment(ctx.getText());
        return new TableNameSegment(identifierSegment);
    }

    @Override
    public ASTNode visitColumnNames(MySQLStatementParser.ColumnNamesContext ctx) {
        List<MySQLStatementParser.ColumnNameContext>  list = ctx.columnName();
        List<ColumnNameSegment> columnNamesSegments = Lists.newArrayList();
        for (MySQLStatementParser.ColumnNameContext columnNameContext : list) {
            ColumnNameSegment columnNameSegment = new ColumnNameSegment(new IdentifierSegment(columnNameContext.getText()));
            columnNamesSegments.add(columnNameSegment);
        }
        ColumnNamesSegment columnNamesSegment = new ColumnNamesSegment(columnNamesSegments);
        return columnNamesSegment;
    }

    @Override
    public ASTNode visitAssignmentValues(MySQLStatementParser.AssignmentValuesContext ctx) {
        List<MySQLStatementParser.AssignmentValueContext>  list = ctx.assignmentValue();
        List<AssignmentValueSegment> assignmentValueSegments = Lists.newArrayList();
        for (MySQLStatementParser.AssignmentValueContext assignmentValueContext : list) {
            AssignmentValueSegment assignmentValueSegment = new AssignmentValueSegment(new IdentifierSegment(assignmentValueContext.getText()));
            assignmentValueSegments.add(assignmentValueSegment);
        }
        AssignmentValuesSegment assignmentValuesSegment = new AssignmentValuesSegment(assignmentValueSegments);
        return assignmentValuesSegment;
    }

}

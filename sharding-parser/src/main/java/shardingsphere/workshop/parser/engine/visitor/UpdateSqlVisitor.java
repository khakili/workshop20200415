/*
 * @(#)UpdateSqlVisitor.java 1.0 2020/4/26
 *
 * Copyright (c) 2019 JDD. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package shardingsphere.workshop.parser.engine.visitor;

import autogen.MySQLStatementBaseVisitor;
import autogen.MySQLStatementParser;
import com.google.common.collect.Maps;
import shardingsphere.workshop.parser.statement.ASTNode;
import shardingsphere.workshop.parser.statement.segment.IdentifierSegment;
import shardingsphere.workshop.parser.statement.segment.TableNameSegment;
import shardingsphere.workshop.parser.statement.segment.UpdateFieldSegment;
import shardingsphere.workshop.parser.statement.segment.WhereConditionSegment;
import shardingsphere.workshop.parser.statement.statement.UpdateStatement;

import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @author like175
 * @version 1.0
 * @since 1.0
 */
public class UpdateSqlVisitor extends MySQLStatementBaseVisitor<ASTNode> {
    @Override
    public ASTNode visitUpdate(MySQLStatementParser.UpdateContext ctx) {
        TableNameSegment tableNameSegment = (TableNameSegment) visit(ctx.tableName());
        UpdateFieldSegment updateFieldSegment = (UpdateFieldSegment) visit(ctx.updateField());
        WhereConditionSegment whereConditionSegment = (WhereConditionSegment) visit(ctx.whereCondition());
        UpdateStatement updateStatement = new UpdateStatement(tableNameSegment, updateFieldSegment, whereConditionSegment);
        return updateStatement;
    }

    @Override
    public ASTNode visitTableName(MySQLStatementParser.TableNameContext ctx) {
        IdentifierSegment identifierSegment = new IdentifierSegment(ctx.getText());
        return new TableNameSegment(identifierSegment);
    }

    @Override
    public ASTNode visitUpdateField(MySQLStatementParser.UpdateFieldContext ctx) {
        Map<IdentifierSegment, IdentifierSegment> identifierSegmentIdentifierSegmentMap = generateParam(ctx.columnName(), ctx.columnValue());
        UpdateFieldSegment updateFieldSegment = new UpdateFieldSegment(identifierSegmentIdentifierSegmentMap);
        return updateFieldSegment;
    }

    @Override
    public ASTNode visitWhereCondition(MySQLStatementParser.WhereConditionContext ctx) {
        Map<IdentifierSegment, IdentifierSegment> identifierSegmentIdentifierSegmentMap = generateParam(ctx.columnName(), ctx.columnValue());
        WhereConditionSegment whereConditionSegment = new WhereConditionSegment(identifierSegmentIdentifierSegmentMap);
        return whereConditionSegment;
    }

    /**
     * 组装Segment Param
     * @param columnNameContexts
     * @param columnValueContexts
     * @return
     */
    private Map<IdentifierSegment, IdentifierSegment> generateParam(List<MySQLStatementParser.ColumnNameContext> columnNameContexts,
                                                                    List<MySQLStatementParser.ColumnValueContext> columnValueContexts) {
        Map<IdentifierSegment, IdentifierSegment> identifierSegmentIdentifierSegmentMap = Maps.newHashMap();
        for (int i = 0; i < columnNameContexts.size(); i++) {
            identifierSegmentIdentifierSegmentMap.put(
                    new IdentifierSegment(columnNameContexts.get(i).getText()),
                    new IdentifierSegment(columnValueContexts.get(i).getText()));
        }
        return identifierSegmentIdentifierSegmentMap;
    }
}

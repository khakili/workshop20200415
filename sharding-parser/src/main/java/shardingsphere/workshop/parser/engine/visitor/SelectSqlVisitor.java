/*
 * @(#)SelectSqlVisitor.java 1.0 2020/4/26
 *
 * Copyright (c) 2019 JDD. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package shardingsphere.workshop.parser.engine.visitor;

import autogen.MySQLStatementBaseVisitor;
import autogen.MySQLStatementParser;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import shardingsphere.workshop.parser.statement.ASTNode;
import shardingsphere.workshop.parser.statement.segment.ColumnNameSegment;
import shardingsphere.workshop.parser.statement.segment.IdentifierSegment;
import shardingsphere.workshop.parser.statement.segment.TableNameSegment;
import shardingsphere.workshop.parser.statement.segment.WhereConditionSegment;
import shardingsphere.workshop.parser.statement.statement.SelectStatment;

import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @author like175
 * @version 1.0
 * @since 1.0
 */
public class SelectSqlVisitor extends MySQLStatementBaseVisitor<ASTNode> {
    @Override
    public ASTNode visitSelect(MySQLStatementParser.SelectContext ctx) {
        List<ColumnNameSegment> columnNameSegments = Lists.newArrayList();
        List<MySQLStatementParser.ColumnNameContext> list = ctx.columnName();
        for (MySQLStatementParser.ColumnNameContext columnNameContext : list) {
            IdentifierSegment identifierSegment = new IdentifierSegment(columnNameContext.getText());
            columnNameSegments.add(new ColumnNameSegment(identifierSegment));
        }
        TableNameSegment tableNameSegment = (TableNameSegment) visit(ctx.tableName());
        WhereConditionSegment whereConditionSegment = (WhereConditionSegment) visit(ctx.whereCondition());
        SelectStatment selectStatment = new SelectStatment(columnNameSegments, tableNameSegment, whereConditionSegment);
        return selectStatment;
    }

    @Override
    public ASTNode visitColumnName(MySQLStatementParser.ColumnNameContext ctx) {
        IdentifierSegment identifierSegment = new IdentifierSegment(ctx.getText());
        ColumnNameSegment columnNameSegment = new ColumnNameSegment(identifierSegment);
        return columnNameSegment;
    }

    @Override
    public ASTNode visitTableName(MySQLStatementParser.TableNameContext ctx) {
        IdentifierSegment identifierSegment = new IdentifierSegment(ctx.getText());
        return new TableNameSegment(identifierSegment);
    }

    @Override
    public ASTNode visitWhereCondition(MySQLStatementParser.WhereConditionContext ctx) {
        Map<IdentifierSegment, IdentifierSegment> identifierSegmentIdentifierSegmentMap = generateParam(ctx.columnName(), ctx.columnValue());
        WhereConditionSegment whereConditionSegment = new WhereConditionSegment(identifierSegmentIdentifierSegmentMap);
        return whereConditionSegment;
    }

    /**
     * 组装Segment Param
     *
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

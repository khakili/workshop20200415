/*
 * @(#)SelectStatment.java 1.0 2020/4/26
 *
 * Copyright (c) 2019 JDD. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package shardingsphere.workshop.parser.statement.statement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import shardingsphere.workshop.parser.statement.ASTNode;
import shardingsphere.workshop.parser.statement.segment.ColumnNameSegment;
import shardingsphere.workshop.parser.statement.segment.ColumnNamesSegment;
import shardingsphere.workshop.parser.statement.segment.TableNameSegment;
import shardingsphere.workshop.parser.statement.segment.WhereConditionSegment;

import java.util.List;

/**
 * TODO
 *
 * @author like175
 * @version 1.0
 * @since 1.0
 */
@RequiredArgsConstructor
@Getter
public class SelectStatment implements ASTNode {
    /**
     *
     */
    private final List<ColumnNameSegment> columnNameSegments;
    /**
     *
     */
    private final TableNameSegment tableNameSegment;
    /**
     *
     */
    private final WhereConditionSegment whereConditionSegment;
}

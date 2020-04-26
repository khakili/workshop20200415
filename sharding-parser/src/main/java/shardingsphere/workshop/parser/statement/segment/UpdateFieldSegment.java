/*
 * @(#)UpdateFieldSegment.java 1.0 2020/4/26
 *
 * Copyright (c) 2019 JDD. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package shardingsphere.workshop.parser.statement.segment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import shardingsphere.workshop.parser.statement.ASTNode;

import java.util.Map;

/**
 * TODO
 *
 * @author like175
 * @version 1.0
 * @since 1.0
 */
@RequiredArgsConstructor
@Getter
public class UpdateFieldSegment implements ASTNode {
    private final Map<IdentifierSegment,IdentifierSegment> identifierSegmentIdentifierSegmentMap;
}

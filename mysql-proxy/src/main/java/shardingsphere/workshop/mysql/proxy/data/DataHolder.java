/*
 * @(#)DataHolder.java 1.0 2020/4/27
 *
 * Copyright (c) 2019 JDD. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package shardingsphere.workshop.mysql.proxy.data;

import com.google.common.collect.Lists;
import shardingsphere.workshop.parser.statement.segment.*;
import shardingsphere.workshop.parser.statement.statement.DeleteStatment;
import shardingsphere.workshop.parser.statement.statement.InsertStatement;
import shardingsphere.workshop.parser.statement.statement.SelectStatment;
import shardingsphere.workshop.parser.statement.statement.UpdateStatement;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数据维护类
 *
 * @author like175
 * @version 1.0
 * @since 1.0
 */
public class DataHolder {
    private static final List<Map<String,String>> DATA_HOLDER = new ArrayList<>();
    public static int getSize(){
        return DATA_HOLDER.size();
    }

    static{
        Map<String,String> map = new HashMap<>();
        map.put("abcd","1");
        map.put("fff","222");
        DATA_HOLDER.add(map);
        DATA_HOLDER.add(map);

    }
    public static List<Map<String,String>> select(SelectStatment selectStatment){
        List<Map<String,String>> result = Lists.newArrayList();
        //需要取出的列
        List<ColumnNameSegment> columnNameSegments = selectStatment.getColumnNameSegments();
        //条件
        WhereConditionSegment whereConditionSegment = selectStatment.getWhereConditionSegment();
        DATA_HOLDER.forEach(item->{
            whereConditionSegment.getIdentifierSegmentIdentifierSegmentMap().entrySet().forEach(entry->{
                String key = entry.getKey().getValue();
                if(item.containsKey(key)){
                    String value = item.get(key);
                    if(Objects.equals(value,entry.getValue().getValue())){
                        result.add(item);
                    }
                    return;
                }
                return;
            });
        });
        List<Map<String,String>> copyResult = Lists.newArrayList();
        columnNameSegments.forEach(item->{
            if(item.getColumnName().getValue().equals("*")){
                copyResult.addAll(result);
                return ;
            }
            String column = item.getColumnName().getValue();
            result.forEach(rs->{
                if(rs.containsKey(column)){
                    copyResult.add(rs);
                }
            });
        });
        return copyResult;
    }

    public static int insert(InsertStatement insertStatement) throws SQLException {
        ColumnNamesSegment columnNamesSegment = insertStatement.getColumnNamesSegment();
        AssignmentValuesSegment assignmentValueSegment = insertStatement.getAssignmentValuesSegment();
        if(columnNamesSegment.getColumnNameSegments().size()!=assignmentValueSegment.getAssignmentValueSegments().size()){
            throw new SQLException("sql error");
        }
        Map<String,String> map = new HashMap<>();
        for (int i = 0; i < columnNamesSegment.getColumnNameSegments().size(); i++) {
            map.put(columnNamesSegment.getColumnNameSegments().get(i).getColumnName().getValue(),
                    assignmentValueSegment.getAssignmentValueSegments().get(i).getAssignmentValue().getValue());
        }
        DATA_HOLDER.add(map);
        return 1;
    }

    public static int delete(DeleteStatment deleteStatment){
        AtomicInteger count = new AtomicInteger();
        Iterator<Map<String,String>> iterator =  DATA_HOLDER.iterator();
        while (iterator.hasNext()){
            Map<String,String> item = iterator.next();
            deleteStatment.getWhereConditionSegment().getIdentifierSegmentIdentifierSegmentMap().entrySet().forEach(entry->{
                String key = entry.getKey().getValue();
                if(item.containsKey(key)){
                    String value = item.get(key);
                    if(Objects.equals(value,entry.getValue().getValue())){
                        iterator.remove();
                        count.getAndIncrement();
                    }
                    return;
                }
                return;
            });
        }
        return count.get();
    }

    public static int update(UpdateStatement updateStatement){
        return 0;
    }
}

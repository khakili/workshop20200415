/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package shardingsphere.workshop.parser.engine;

import org.junit.Assert;
import org.junit.Test;
import shardingsphere.workshop.parser.statement.segment.IdentifierSegment;
import shardingsphere.workshop.parser.statement.statement.InsertStatement;
import shardingsphere.workshop.parser.statement.statement.UpdateStatement;
import shardingsphere.workshop.parser.statement.statement.UseStatement;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public final class ParseEngineTest {
    
    @Test
    public void testParse() {
        String sql = "use sharding_db";
        UseStatement useStatement = (UseStatement) ParseEngine.parse(sql);
        assertThat(useStatement.getSchemeName().getIdentifier().getValue(), is("sharding_db"));
    }
    @Test
    public void testInsert(){
        String sql = "INSERT INTO runoob_tbl (runoob_title, runoob_author) VALUES (\"学习 PHP\", \"菜鸟教程\")";
        InsertStatement insertStatement = (InsertStatement)ParseEngine.parseInsert(sql);
        Assert.assertEquals(insertStatement.getTableNameSegment().getTableName().getValue(),"runoob_tbl");
        Assert.assertEquals(insertStatement.getAssignmentValuesSegment().getAssignmentValueSegments().size(),2);
        Assert.assertEquals(insertStatement.getColumnNamesSegment().getColumnNameSegments().size(),2);
    }

    @Test
    public void testUpdate(){
        String sql = "UPDATE runoob_tbl SET runoob_title='学习 C++' WHERE runoob_id=3;";
        UpdateStatement updateStatement = (UpdateStatement)ParseEngine.parseUpdate(sql);
        Assert.assertEquals(updateStatement.getTableNameSegment().getTableName().getValue(),"runoob_tbl");
        Assert.assertEquals(updateStatement.getUpdateFieldSegment().getIdentifierSegmentIdentifierSegmentMap().get(new IdentifierSegment("runoob_title")).getValue(),"'学习 C++'");
        Assert.assertEquals(updateStatement.getWhereConditionSegment().getIdentifierSegmentIdentifierSegmentMap().get(new IdentifierSegment("runoob_id")).getValue(),"3");
    }

    @Test
    public void testIdentifierSegmentEqual(){
       IdentifierSegment identifierSegment =  new IdentifierSegment("runoob_title");
       IdentifierSegment identifierSegment1 = new IdentifierSegment("runoob_title");
       Assert.assertEquals(identifierSegment,identifierSegment1);
    }


}

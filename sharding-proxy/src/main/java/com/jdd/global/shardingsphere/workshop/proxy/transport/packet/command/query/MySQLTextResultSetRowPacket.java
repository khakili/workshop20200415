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

package com.jdd.global.shardingsphere.workshop.proxy.transport.packet.command.query;

import com.jdd.global.shardingsphere.workshop.proxy.transport.MySQLPacketPayload;
import com.jdd.global.shardingsphere.workshop.proxy.transport.packet.MySQLPacket;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * Text result set row packet for MySQL.
 *
 * @see <a href="https://dev.mysql.com/doc/internals/en/com-query-response.html#packet-ProtocolText::ResultsetRow">ResultsetRow</a>
 */
@RequiredArgsConstructor
@Getter
public final class MySQLTextResultSetRowPacket implements MySQLPacket {
    
    private static final int NULL = 0xfb;
    
    private final int sequenceId;
    
    private final List<Object> data;
    
    @Override
    public void write(final MySQLPacketPayload payload) {
        for (Object each : data) {
            if (null == each) {
                payload.writeInt1(NULL);
            } else {
                if (each instanceof byte[]) {
                    payload.writeBytesLenenc((byte[]) each);
                } else if ((each instanceof Timestamp) && (0 == ((Timestamp) each).getNanos())) {
                    payload.writeStringLenenc(each.toString().split("\\.")[0]);
                } else if (each instanceof BigDecimal) {
                    payload.writeStringLenenc(((BigDecimal) each).toPlainString());
                } else if (each instanceof Boolean) {
                    payload.writeBytesLenenc((Boolean) each ? new byte[]{1} : new byte[]{0});
                } else {
                    payload.writeStringLenenc(each.toString());
                }
            }
        }
    }
}
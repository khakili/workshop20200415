
package shardingsphere.workshop.mysql.proxy.todo;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shardingsphere.workshop.mysql.proxy.data.DataHolder;
import shardingsphere.workshop.mysql.proxy.fixture.MySQLAuthenticationHandler;
import shardingsphere.workshop.mysql.proxy.fixture.packet.MySQLErrPacketFactory;
import shardingsphere.workshop.mysql.proxy.fixture.packet.MySQLOKPacket;
import shardingsphere.workshop.mysql.proxy.fixture.packet.MySQLPacketPayload;
import shardingsphere.workshop.mysql.proxy.fixture.packet.constant.MySQLColumnType;
import shardingsphere.workshop.mysql.proxy.todo.packet.MySQLEofPacket;
import shardingsphere.workshop.mysql.proxy.todo.packet.MySQLColumnDefinition41Packet;
import shardingsphere.workshop.mysql.proxy.todo.packet.MySQLFieldCountPacket;
import shardingsphere.workshop.mysql.proxy.todo.packet.MySQLTextResultSetRowPacket;
import shardingsphere.workshop.parser.engine.ParseEngine;
import shardingsphere.workshop.parser.statement.statement.DeleteStatment;
import shardingsphere.workshop.parser.statement.statement.InsertStatement;
import shardingsphere.workshop.parser.statement.statement.SelectStatment;
import shardingsphere.workshop.parser.statement.statement.UpdateStatement;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Frontend channel inbound handler.
 */
@RequiredArgsConstructor
@Slf4j
public final class FrontendChannelInboundHandler extends ChannelInboundHandlerAdapter {

    private final MySQLAuthenticationHandler authHandler = new MySQLAuthenticationHandler();

    private boolean authorized;

    @Override
    public void channelActive(final ChannelHandlerContext context) {
        authHandler.handshake(context);
    }

    @Override
    public void channelRead(final ChannelHandlerContext context, final Object message) {
        if (!authorized) {
            authorized = auth(context, (ByteBuf) message);
            return;
        }
        try (MySQLPacketPayload payload = new MySQLPacketPayload((ByteBuf) message)) {
            executeCommand(context, payload);
        } catch (final Exception ex) {
            log.error("Exception occur: ", ex);
            context.writeAndFlush(MySQLErrPacketFactory.newInstance(1, ex));
        }
    }

    @Override
    public void channelInactive(final ChannelHandlerContext context) {
        context.fireChannelInactive();
    }

    private boolean auth(final ChannelHandlerContext context, final ByteBuf message) {
        try (MySQLPacketPayload payload = new MySQLPacketPayload(message)) {
            return authHandler.auth(context, payload);
        } catch (final Exception ex) {
            log.error("Exception occur: ", ex);
            context.write(MySQLErrPacketFactory.newInstance(1, ex));
        }
        return false;
    }

    private void executeCommand(final ChannelHandlerContext context, final MySQLPacketPayload payload) {
        Preconditions.checkState(0x03 == payload.readInt1(), "only support COM_QUERY command type");
        // TODO 1. Read SQL from payload, then system.out it
        // TODO 2. Return mock MySQLPacket to client (header: MySQLFieldCountPacket + MySQLColumnDefinition41Packet + MySQLEofPacket, content: MySQLTextResultSetRowPacket
        // TODO 3. Parse SQL, return actual data according to SQLStatement
//        context.write(new MySQLFieldCountPacket(1, 1));
//        context.write(new MySQLColumnDefinition41Packet(2, 0, "sharding_db", "t_order", "t_order", "order_id", "order_id", 100, MySQLColumnType.MYSQL_TYPE_STRING,0));
//        context.write(new MySQLEofPacket(3));
//        context.write(new MySQLTextResultSetRowPacket(4, ImmutableList.of(100)));
//        context.write(new MySQLEofPacket(5));
//        context.flush();
        try {
            handle(context, payload);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private void handle(final ChannelHandlerContext context, final MySQLPacketPayload payload)throws Exception {
        String sql = payload.readStringEOF();
        log.info("server accept SQL:{}", sql);
        if (sql.startsWith("insert") || sql.startsWith("INSERT")) {
            //insert
            InsertStatement insertStatement = (InsertStatement) ParseEngine.parseInsert(sql);
            try {
                int rs = DataHolder.insert(insertStatement);
                context.write(new MySQLOKPacket(1, rs, DataHolder.getSize()));
            } catch (SQLException e) {
                context.write(MySQLErrPacketFactory.newInstance(1, e));
            }
            context.flush();
            return;
        }
        if (sql.startsWith("update") || sql.startsWith("UPDATE")) {
            //update
            UpdateStatement updateStatement = (UpdateStatement) ParseEngine.parseUpdate(sql);
            int rs = DataHolder.update(updateStatement);
            context.write(new MySQLOKPacket(1, rs, DataHolder.getSize()));
            context.flush();
            return;
        }
        if (sql.startsWith("delete") || sql.startsWith("DELETE")) {
            //delete
            DeleteStatment deleteStatment = (DeleteStatment) ParseEngine.parseDelete(sql);
            int rs = DataHolder.delete(deleteStatment);
            context.write(new MySQLOKPacket(1, rs, DataHolder.getSize()));
            context.flush();
            return;
        }
        if (sql.startsWith("select") || sql.startsWith("SELECT")) {
            //select
            SelectStatment selectStatment = (SelectStatment) ParseEngine.parseSelect(sql);
            List<Map<String, String>> list = DataHolder.select(selectStatment);
            if (list.size() == 0) {
                context.write(new MySQLFieldCountPacket(1, 1));
                context.write(new MySQLColumnDefinition41Packet(2, 0, "sharding_db", "t_order", "t_order", "empty_result", "empty_result", 100, MySQLColumnType.MYSQL_TYPE_STRING, 0));
                context.write(new MySQLEofPacket(3));
                context.write(new MySQLTextResultSetRowPacket(4, ImmutableList.of(0)));
                context.write(new MySQLEofPacket(5));
                context.flush();
                return;
            }
            int max = 0;
            for (Map<String, String> stringStringMap : list) {
                int temp = stringStringMap.keySet().size();
                max = temp>max?temp:max;
            }
            context.write(new MySQLFieldCountPacket(1, max));
            AtomicInteger i = new AtomicInteger(2);
            list.get(0).keySet().forEach(key -> {
                context.write(new MySQLColumnDefinition41Packet(i.get(), 0, "sharding_db", "t_order", "t_order",
                        key, key, 100, MySQLColumnType.MYSQL_TYPE_STRING, 0));
                i.getAndIncrement();
            });
            context.write(new MySQLEofPacket(i.get()));
            list.forEach(item -> {
                List<Object> values = Lists.newArrayList();
                item.values().forEach(value -> {
                    values.add(value);
                });
                context.write(new MySQLTextResultSetRowPacket(i.incrementAndGet(), values));
            });
            context.write(new MySQLEofPacket(i.incrementAndGet()));
            context.flush();
            return;
        }

        context.write(new MySQLFieldCountPacket(1, 1));
        context.write(new MySQLColumnDefinition41Packet(2, 0, "sharding_db", "t_order", "t_order", "order_id", "order_id", 100, MySQLColumnType.MYSQL_TYPE_STRING, 0));
        context.write(new MySQLEofPacket(3));
        context.write(new MySQLTextResultSetRowPacket(4, ImmutableList.of(100)));
        context.write(new MySQLEofPacket(5));
        context.flush();
    }

}

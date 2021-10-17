package io.netty.example.paas.link;

import io.netty.channel.ChannelHandlerContext;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 长链接管理
 */
public class LinkManager {
    // 连接管理(channelId -> ctx)
    private Map<String, ChannelHandlerContext> ctxMap = new HashMap<>();

    // 缓存
    private static final int MAX_BUFFER_SIZE = 10240;
    private Map<String, ByteBuffer> bufferMap = new ConcurrentHashMap<>();

    /**
     * 添加长链接
     * @param ctx
     * @return
     */
    public boolean addChannelHandler(ChannelHandlerContext ctx) {
        String uniqueId = getUniqueId(ctx);
        ctxMap.put(uniqueId, ctx);
        return true;
    }

    /**
     * 添加收到的长链接缓存，连接是本地化的
     * @param ctx
     * @return
     */
    public boolean addChannelBuffer(ChannelHandlerContext ctx, ByteBuffer byteBuffer) {

        // 添加缓存
        String uniqueId = getUniqueId(ctx);
        ByteBuffer curBuffer;
        if (bufferMap.containsKey(uniqueId)) {
            ByteBuffer oldBuffer = bufferMap.get(uniqueId);
            oldBuffer.put(byteBuffer);

            curBuffer = oldBuffer;
        } else {
            ByteBuffer newByteBuffer = ByteBuffer.allocate(MAX_BUFFER_SIZE);
            bufferMap.put(uniqueId, newByteBuffer);

            newByteBuffer.put(byteBuffer);
            curBuffer = newByteBuffer;
        }

        // 同步解析协议，操作当前连接通道的buffer缓存
        analyzeProtocolAndDispatch(ctx, curBuffer);

        return true;
    }

    /**
     * 获取channel上下文的唯一ID
     * @param ctx
     * @return
     */
    private String getUniqueId(ChannelHandlerContext ctx) {
        return ctx.channel().id().asLongText();
    }

    private void analyzeProtocolAndDispatch(ChannelHandlerContext ctx, ByteBuffer curBuffer) {
        // 解析LWP协议

        // 分派处理，远程RPC调用
        // 如Dubbo泛化调用: https://dubbo.apache.org/zh/docs/advanced/generic-reference/

    }

}

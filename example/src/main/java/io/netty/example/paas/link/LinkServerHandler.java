package io.netty.example.paas.link;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

/**
 * Handler implementation for the echo server.
 * Handler是复用的，关键是协议的解析，以及客户端channel的识别和推送时使用
 */
@Sharable
public class LinkServerHandler extends ChannelInboundHandlerAdapter {
    // 长链接管理
    private LinkManager linkManager;

    public void setLinkManager(LinkManager linkManager) {
        this.linkManager = linkManager;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        linkManager.addChannelHandler(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            ByteBuf byteBuf = (ByteBuf) msg;
            System.out.println("Receive from client: " + ctx.channel().remoteAddress() + " msg：" + byteBuf.toString(CharsetUtil.UTF_8));

            // 解析协议对应的处理
            linkManager.addChannelBuffer(ctx, byteBuf.nioBuffer());
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
//        ctx.flush();
        //发送消息给客户端
        ctx.writeAndFlush(Unpooled.copiedBuffer("server accepted", CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

}

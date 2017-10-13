package com.konsung.network;

import android.os.Handler;

import com.konsung.utils.UiUtils;

import java.net.InetSocketAddress;
import java.nio.ByteOrder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author ouyangfan
 * @version 0.0.1
 * 使用netty 框架的服务器类
 */
public class EchoServer {
    // 端口号.final一旦赋值不能更改
    private final int port;
    private Handler handler;
    // 单例模式
    private static EchoServer echoServerInstance;

    /**
     * 构造器，带端口号和handler数据处理
     * @param port 端口号
     * @param handler handler数据处理
     */
    private EchoServer(int port, Handler handler) {
        this.port = port;
        this.handler = handler;
    }

    /**
     * 单例模式
     * @param port 端口号
     * @param handler 数据处理
     * @return EchoServer类实例
     */
    public static EchoServer getEchoServerInstance(int port, Handler handler) {
        if (echoServerInstance == null) {
            echoServerInstance = new EchoServer(port, handler);
        }
        return echoServerInstance;
    }

    /**
     * 启动方法
     * @throws Exception 异常
     */
    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group).channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            // 进行数据分包处理,这里的参数是根据具体协议来指定的
                            channel.pipeline().addLast("decoder", new
                                    LengthFieldBasedFrameDecoder(ByteOrder
                                    .LITTLE_ENDIAN, Integer.MAX_VALUE, 1, 2, -3, 0, false));
                            channel.pipeline().addLast(new EchoServerDecoder(handler));
                            channel.pipeline().addLast("encoder", new EchoServerEncoder());
                        }
                    });
            ChannelFuture future = bootstrap.bind().sync();
            System.out.println(EchoServer.class.getName() + " started and listen on "
                    + future.channel().localAddress());
            UiUtils.initDeviceConfig(); //当收到AppDevice发来的数据后，才发生设备配置命令。
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}

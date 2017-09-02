package com.benberi.cadesim.server;

import com.benberi.cadesim.server.codec.ServerChannelHandler;
import com.benberi.cadesim.server.codec.util.PacketDecoder;
import com.benberi.cadesim.server.codec.util.PacketEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Rawr Server
 *
 * RawrServer is a small server ran by one thread to run small games
 * such as the blockade simulator for low-players games
 */
public class CadeServer extends ServerBootstrap implements Runnable {


    /**
     * The logger
     */
    private Logger logger = Logger.getLogger("Server Main");

    /**
     * The executor
     */
    private EventLoopGroup worker = new NioEventLoopGroup(1);

    private boolean loaded = false;

    /**
     * The server context
     */
    private ServerContext context;

    private GameServerBootstrap bootstrap;

    public CadeServer(ServerContext context, GameServerBootstrap bootstrap) {
        super();
        this.context = context;
        this.bootstrap = bootstrap;
        group(worker);
        channel(NioServerSocketChannel.class);
        option(ChannelOption.SO_BACKLOG, 100);
        childOption(ChannelOption.TCP_NODELAY, true);

        childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline p = socketChannel.pipeline();
                p.addLast("encoder", new PacketEncoder());
                p.addLast("decoder", new PacketDecoder());
                p.addLast("handler", new ServerChannelHandler(context));
            }
        });
    }

    public void run() {
        try {
            int port = context.getConfiguration().getPort();
            ChannelFuture f = bind(port).sync();
            if (f.isSuccess()) {
                bootstrap.startServices();
            }
            else {
                logger.warning("Could not bind the server on port " + port + ". Cause: " + f.cause().getMessage());
                System.exit(0);
            }

            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
        }
    }

    public boolean isLoaded() {
        return this.loaded;
    }
}

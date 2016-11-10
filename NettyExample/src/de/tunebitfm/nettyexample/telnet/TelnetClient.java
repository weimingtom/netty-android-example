package de.tunebitfm.nettyexample.telnet;

import android.util.Log;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

public class TelnetClient {
    public static final String LOG_TAG = "TelnetClient";
    static final boolean SSL = false;
    static final String HOST = "10.0.2.2";
    static final int PORT = Integer.parseInt(SSL? "8992" : "8023");

    ChannelFuture lastWriteFuture = null;
	public final EventLoopGroup eventLoop = new NioEventLoopGroup(1);
    private EventLoopGroup group = new NioEventLoopGroup();
    private Channel channel = null;
    
    Channel getChannel() {
		return channel;
	}

	void setChannel(Channel channel) {
		this.channel = channel;
	}

	private Bootstrap bootstrap;

    public TelnetClient() {
    	
    }

    public void connect() { 
    	this.eventLoop.execute(new Runnable() {
            @Override
            public void run() {
                doConnect();
            }
        });
    }

    private void doConnect() {
        try {
            final SslContext sslCtx;
            if (SSL) {
                sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            } else {
                sslCtx = null;
            }
            
            bootstrap = new Bootstrap();
            bootstrap.group(group)
            	.channel(NioSocketChannel.class)
            	.handler(new TelnetClientInitializer(sslCtx));

            this.channel = bootstrap.connect(HOST, PORT).sync().channel();
        } catch(Throwable throwable) {
            Log.e(LOG_TAG, "Uups. An error occurred while trying to connect.", throwable);
        }
    }

    public void sendMessage(String message) {
    	lastWriteFuture = this.channel.writeAndFlush(message + "\r\n");
        if ("bye".equals(message.toLowerCase())) {
            try {
				this.channel.closeFuture().sync();
	            if (lastWriteFuture != null) {
	                lastWriteFuture.sync();
	            }
            } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

}

package com.zhicheng.wukongcharge.communicate;

import org.apache.log4j.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TestCommunication {
	public static void main(String[] args) throws Exception {
		final Logger log = Logger.getLogger(TestCommunication.class);
		//服务类
		ServerBootstrap bootstrap = new ServerBootstrap();
		
		//boss线程监听端口， work线程负责读写
		EventLoopGroup boss = new NioEventLoopGroup();
		EventLoopGroup worker = new NioEventLoopGroup();
		
		//定义工作组
		bootstrap.group(boss, worker);
		
		
		//设置管道channel
		bootstrap.channel(NioServerSocketChannel.class);
		
		//添加handler，管道中的处理器，通过ChannelInitializer来构造
		bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				//此方法每次客户端连接都会调用，是为通道初始化的方法
				
				//获得通道channel中的管道链（执行链、handler链）
//				ChannelPipeline pipeline = ch.pipeline();
				ch.pipeline().addLast(new TomdaChargerDecoder());
				ch.pipeline().addLast(new TomdaChargerEncoder());
				ch.pipeline().addLast("tomdaChargerServerHandler", new TomdaChargerServerHandler());
				log.info("成功连接");
				System.out.println("成功连接");
			}
		});
			bootstrap.option(ChannelOption.SO_BACKLOG, 2048);
			bootstrap.bind(6068).sync();
	}
}

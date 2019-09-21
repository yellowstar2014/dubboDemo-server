package com.dubbo.RpcServer;

import com.dubbo.registry.IRegisterCenter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yellow
 * @date 2019/9/19 13:56
 * 温馨提醒:
 * 代码千万行，
 * 注释第一行。
 * 命名不规范，
 * 同事两行泪。
 */
public class RpcServer {

    private IRegisterCenter registerCenter;
    private String serviceAddress;
    private Map<String,Object> handlerMap=new HashMap<>();//ioc容器

    public RpcServer(IRegisterCenter registerCenter, String serviceAddress) {
        this.registerCenter = registerCenter;
        this.serviceAddress = serviceAddress;
    }

    /**
     * //服务名称和实例对象的对应关系进行绑定
     * @param interfaceClass 服务名称
     * @param object 实例对象
     */
    public void bind(Class<?> interfaceClass,Object object){
        handlerMap.put(interfaceClass.getName(),object);
    }

    /**
     * 发布服务和监听端口
     */
    public void publisher(){
        //1.服务发布
        for (String serviceName:handlerMap.keySet()){
            //注册服务名称和地址
            registerCenter.register(serviceName,serviceAddress);
        }
        //2.启动一个监听 Netty
        //通过netty的方式进行连接和发送数据
        //配置服务端的 NIO 线程池,用于网络事件处理，实质上他们就是 Reactor 线程组
        //bossGroup 用于服务端接受客户端连接，workerGroup 用于进行 SocketChannel 网络读写
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {

            //启动netty的服务
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);//指定通道类型为NioServerSocketChannel，一种异步模式，OIO阻塞模式为OioServerSocketChannel
            //设置childHandler执行所有的连接请求
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4,0,4));
                    pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                    pipeline.addLast("encoder", new ObjectEncoder());
                    pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                    //使用netty写到最后就是写Handler的代码,做IO数据读写交互
                    pipeline.addLast(new RpcServerHandler(handlerMap));
                }
            });
            bootstrap.option(ChannelOption.SO_BACKLOG,128).childOption(ChannelOption.SO_KEEPALIVE,true);
            //通过netty进行监听 端口
            String[] addrs = serviceAddress.split(":");
            String ip = addrs[0];
            int port = Integer.parseInt(addrs[1]);
            ChannelFuture future = bootstrap.bind(ip,port).sync();//调用 bind 方法绑定监听ip、端口，调用 sync 方法同步等待绑定操作完成
            System.out.println("netty服务端（"+ip+":"+port+"）启动成功，等待需调用接口的客户端的请求连接：");
            future.channel().closeFuture().sync();//下面会进行阻塞，等待服务器连接关闭之后 退出，程序结束
            System.out.println("netty服务端关闭");
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            System.out.println("server exit");
            //优雅退出，释放NIO线程组
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }






    }
}

package com.dubbo.RpcServer;

import com.dubbo.api.bean.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**业务处理
 * @author yellow
 * @date 2019/9/19 16:11
 * 温馨提醒:
 * 代码千万行，
 * 注释第一行。
 * 命名不规范，
 * 同事两行泪。
 */
public class RpcServerHandler extends ChannelInboundHandlerAdapter {
    private Map<String,Object> handlerMap=new HashMap<>();

    public RpcServerHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    /**
     * 收到客户端消息，自动触发
     * @param ctx  可以用来向客户端发送数据
     * @param msg   接收到客户端发来的数据
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //获得客户端传输过来的数据
        RpcRequest rpcRequest=(RpcRequest)msg;
        Object result = new Object();
        System.out.println("服务端接收到客户端的接口调用请求是:"+result.toString());
        if (handlerMap.containsKey(rpcRequest.getClassName())){
            //通过java反射机制，用子类对象进行方法调用执行
            Object object = handlerMap.get(rpcRequest.getClassName());
            Method method = object.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getTypes());
            result = method.invoke(object,rpcRequest.getParams());
        }
        //写给客户端result结果
        System.out.println("服务端给客户端返回接口调用的结果数据是:"+result.toString());
        ctx.write(result);
        ctx.flush();
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        /**当发生异常时，关闭 ChannelHandlerContext，释放和它相关联的句柄等资源 */
        ctx.close();
    }
}

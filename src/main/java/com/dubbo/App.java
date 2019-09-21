package com.dubbo;

import com.dubbo.api.IDemoService;
import com.dubbo.RpcServer.RpcServer;
import com.dubbo.registry.IRegisterCenter;
import com.dubbo.registry.RegisterCenterImpl;
import com.dubbo.service.DemoServiceImp;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        //根据你的服务名称  实例化对应的服务
        IDemoService demoService = new DemoServiceImp();
        IRegisterCenter registerCenter = new RegisterCenterImpl();
        //服务发布，监听端口
        RpcServer rpcServer = new RpcServer(registerCenter,"127.0.0.1:8080");
        //服务端需要考虑的事情是:服务名称  绑定  对应的实例对象
        rpcServer.bind(IDemoService.class,demoService);//服务名称和实例对象的对应关系进行绑定
        rpcServer.publisher();//对外发布服务
    }
}

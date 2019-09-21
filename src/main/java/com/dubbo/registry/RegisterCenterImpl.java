package com.dubbo.registry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.io.IOException;

/**
 * @author yellow
 * @date 2019/9/18 16:06
 * 温馨提醒:
 * 代码千万行，
 * 注释第一行。
 * 命名不规范，
 * 同事两行泪。
 */
public class RegisterCenterImpl implements IRegisterCenter {

    private CuratorFramework curatorFramework;

    {
        //根据ZkConfig中的字符串初始化curatorFramework
        curatorFramework= CuratorFrameworkFactory.builder().
                connectString(ZkConfig.CONNECTION_STR).sessionTimeoutMs(4000).
                retryPolicy(new ExponentialBackoffRetry(1000,10)).build();
        curatorFramework.start();
    }

    /**
     *
     * @param serviceName   如com.demo.IDemoService
     * @param serviceAddress 如127.0.0.1:8080
     */
    @Override
    public void register(String serviceName, String serviceAddress) {

        // /registrys/com.demo.IDemoService
        String servicePath=ZkConfig.ZK_REGISTER_PATH+"/"+serviceName;
        try {
            //判断 /registrys/com.demo.IDemoService是否存在，不存在则创建
            if (curatorFramework.checkExists().forPath(servicePath)==null){
                //不存在的话,创建服务名称节点，因为服务名称不会变动，所以设置成持久模式
                curatorFramework.create().creatingParentsIfNeeded().
                        withMode(CreateMode.PERSISTENT).forPath(servicePath,"0".getBytes());

            }//end if

            //到此， /registrys/com.demo.IDemoService 肯定存在，接下来创建服务url地址节点，因为url频繁变动，所以设置成临时模式
            String addressPath = servicePath + "/" + serviceAddress;
            String rsNode = curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(addressPath,"0".getBytes());
            System.out.println("向注册中心Zookeeper注册服务成功，注册的服务是:"+rsNode);

        }catch (Exception ex){

        }
    }

    public static void main(String[] args) throws IOException{
        IRegisterCenter iRegisterCenter=new RegisterCenterImpl();
        iRegisterCenter.register("com.demo.yellow","127.0.0.1:9090");
        //System.in.read();

    }
}

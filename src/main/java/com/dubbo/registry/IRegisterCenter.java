package com.dubbo.registry;

/**
 * @author yellow
 * @date 2019/9/18 15:48
 * 温馨提醒:
 * 代码千万行，
 * 注释第一行。
 * 命名不规范，
 * 同事两行泪。
 */
public interface IRegisterCenter {

    /**
     *
     * @param serviceName   如com.demo.IDemoService
     * @param serviceAddress 如127.0.0.1:8080
     *    将serviceName 与 serviceAddress绑定在一起注册zk内
     */
    void register(String serviceName, String serviceAddress);
}

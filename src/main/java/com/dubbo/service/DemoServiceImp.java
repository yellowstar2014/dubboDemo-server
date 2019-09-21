package com.dubbo.service;

import com.dubbo.api.IDemoService;

/**
 * @author yellow
 * @date 2019/9/18 15:33
 * 温馨提醒:
 * 代码千万行，
 * 注释第一行。
 * 命名不规范，
 * 同事两行泪。
 */
public class DemoServiceImp implements IDemoService {
    @Override
    public String sayHello(String msg) {
        return "Message from server:I am "+msg;
    }
}

package com.study.activemq.le1.helloworld.spring;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * spring 监听mq消息示例
 *
 * @author Hash
 * @since 2020/8/16
 */
@Component
public class HelloConsumer {

    /**
     * destination 为目标，可以是 queue 或 topic
     * containerFactory 为mq连接工厂bean名称，一般在有多个mq连接时使用
     *
     * @author Hash
     * @param email 接收消息对象
     */
    @JmsListener(destination = "mailbox", containerFactory = "myFactory")
    public void receive(Email email) {
        System.out.println(Thread.currentThread().getName() + " receive :" + email);
    }
}

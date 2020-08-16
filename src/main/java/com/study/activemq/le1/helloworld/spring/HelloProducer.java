package com.study.activemq.le1.helloworld.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;

import javax.annotation.PostConstruct;

/**
 * spring 生产者示例
 *
 * @author Hash
 * @since 2020/8/16
 */
@SpringBootApplication
public class HelloProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    /**
     * 发送消息到mq中
     *
     * @author Hash
     */
    @PostConstruct
    public void sendMessage() {
        System.out.println("sending an email message.");
        jmsTemplate.convertAndSend("mailbox", new Email("info@example.com", "Hello"));

        jmsMessagingTemplate.convertAndSend("mailbox", new Email("info@example.com", "Hello"));
    }

    public static void main(String[] args) {
        SpringApplication.run(HelloProducer.class, args);
    }
}

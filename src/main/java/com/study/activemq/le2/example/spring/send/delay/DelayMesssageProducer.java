package com.study.activemq.le2.example.spring.send.delay;

import org.apache.activemq.ScheduledMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.core.JmsTemplate;

import javax.annotation.PostConstruct;

/**
 * @author Hash
 * @since 2020/8/16
 */
@SpringBootApplication
public class DelayMesssageProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    @PostConstruct
    public void sendMessage() {

        // jms 2.9 的实现才能实现该方式
        // jmsTemplate.setDeliveryDelay(20000L);
        // jmsTemplate.convertAndSend("delayQueue", "message with delay -1 ");

        // 发送延时消息
        jmsTemplate.convertAndSend("delayQueue", "message with delay", message -> {
            // 延时 5秒，投递3次，间隔10秒 (投递次数=重复次数+默认的一次)
            message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 6 * 1000L); // 延时
            message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_PERIOD, 2 * 1000L); // 投递间隔
            message.setIntProperty(ScheduledMessage.AMQ_SCHEDULED_REPEAT, 5); // 重复次数

            return message;
        });

        System.out.println("Sending an delay message.");
    }

    public static void main(String[] args) {
        SpringApplication.run(DelayMesssageProducer.class, args);
    }
}

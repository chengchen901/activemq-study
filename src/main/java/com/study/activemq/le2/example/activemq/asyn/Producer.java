package com.study.activemq.le2.example.activemq.asyn;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQMessageProducer;
import org.apache.activemq.AsyncCallback;

import javax.jms.*;

/**
 * @author Hash
 * @since 2020/8/16
 */
public class Producer {
    public static void main(String[] args) {
        String brokerUrl = "tcp://192.168.3.12:61616";
        String destinationUrl = "queue1";
        new ProducerThread(brokerUrl, destinationUrl).start();
    }
}

/**
 * 生产者线程
 *
 * @author Hash
 * @since 2020/8/15
 */
class ProducerThread extends Thread {
    /** mq url地址*/
    private String brokerUrl;

    /** 消费目标 Topic or Queue*/
    private String destinationUrl;

    public ProducerThread(String brokerUrl, String destinationUrl) {
        this.brokerUrl = brokerUrl;
        this.destinationUrl = destinationUrl;
    }

    @Override
    public void run() {
        // 1.创建连接工厂
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(this.brokerUrl);

        Connection connection = null;
        Session session = null;
        try {
            // 2.创建链接
            connection = connectionFactory.createConnection();
            // 一定要start
            connection.start();

            // 3.创建会话（可以创建一个或多个session）
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // 4.创建消息发送目标（Topic or Queue）
            Destination destination = session.createQueue(this.destinationUrl);

            // 5.创建消息生成者
            MessageProducer producer = session.createProducer(destination);
            // 设置推送模式（持久化 / 不持久化）
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);

            // 6.创建一条文本消息
            String text = "Hello world! From: " + Thread.currentThread().getName() + " : " + System.currentTimeMillis();
            TextMessage message = session.createTextMessage(text);

            // 7.通过producer发送消息
            System.out.println("Sent message: " + text);
            // 异步方式发送某条消息
            ((ActiveMQMessageProducer) producer).send(message, new AsyncCallback() {
                @Override
                public void onSuccess() {
                    try {
                        System.out.println(Thread.currentThread().getName() + " 异步发送完成：messageId: "
                                + message.getJMSMessageID() + " " + text);
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onException(JMSException exception) {
                    System.out.println("异步发送消息失败，text：" + text);
                    exception.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 8.清理、关闭连接
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
package com.study.activemq.le1.helloworld.activemq.topic;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 发布订阅 - 1个生产者对多个消费者
 *
 * @author Hash
 * @since 2020/8/15
 */
public class Producer {
    public static void main(String[] args) {
        String brokerUrl = "tcp://192.168.3.12:61616";
        String destinationUrl = "queue1";
        new ProducerThread(brokerUrl, destinationUrl).start();
    }
}

/**
 * 发布订阅 - 1个生产者对多个消费者 线程实现类
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
            // 2.创建连接对象
            connection = connectionFactory.createConnection();
            // 一定要start
            connection.start();

            // 3.创建会话
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // 4.创建发布目标 topic
            Destination destination = session.createTopic(this.destinationUrl);

            // 5.创建生产者消费
            MessageProducer producer = session.createProducer(destination);
            // 设置递送模式（持久化 / 非持久化）
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            // 6.创建一个文本消息
            String text = "Hello topic! From: " + Thread.currentThread().getName() + " : "
                    + System.currentTimeMillis();
            TextMessage message = session.createTextMessage(text);

            // 7.通过producer发送消息
            System.out.println("Sent message: " + text);
            producer.send(message);
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            // 8.关闭连接
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

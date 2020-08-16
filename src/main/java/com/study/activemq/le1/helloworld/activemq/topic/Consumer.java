package com.study.activemq.le1.helloworld.activemq.topic;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 非持久订阅者
 * 非持久订阅只有当客户端处于连接状态才能收到发送到某个主题的消息
 * 而当客户端处于离线状态，这个时间段发到主题的消息它永远不会收到
 *
 * @author Hash
 * @since 2020/8/15
 */
public class Consumer {
    public static void main(String[] args) {
        String brokerUrl = "tcp://192.168.3.12:61616";
        String destinationUrl = "queue1";
        new ConsumerThread(brokerUrl, destinationUrl).start();
        new ConsumerThread(brokerUrl, destinationUrl).start();
    }
}

/**
 * 消费者实现类
 *
 * @author Hash
 * @since 2020/8/15
 */
class ConsumerThread extends Thread {
    /** mq url地址*/
    private String brokerUrl;

    /** 消费目标 Topic or Queue*/
    private String destinationUrl;

    public ConsumerThread(String brokerUrl, String destinationUrl) {
        this.brokerUrl = brokerUrl;
        this.destinationUrl = destinationUrl;
    }

    @Override
    public void run() {
        // http://activemq.apache.org/connection-configuration-uri.html
        // 1.创建连接工程
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(this.brokerUrl);

        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;
        try {
            // 2.创建连接对象
            connection = connectionFactory.createConnection();
            // 一定要启动
            connection.start();

            // 3.创建会话（可以创建一个或多个session）
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // 4.创建消费消息目标（Topic or Queue）
            Destination destination = session.createTopic(this.destinationUrl);

            // 5.创建消费者
            // http://activemq.apache.org/destination-options.html
            consumer = session.createConsumer(destination);

            // 6.接收消息（没有消息就继续等待）
            Message message = consumer.receive();
            if (message instanceof TextMessage) {
                System.out.println("接收到文本消息：" + ((TextMessage) message).getText());
            } else {
                System.out.println(message);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            // 7.关闭资源
            if (consumer != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }

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
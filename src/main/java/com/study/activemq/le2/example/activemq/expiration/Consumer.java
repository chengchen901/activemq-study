package com.study.activemq.le2.example.activemq.expiration;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 消费者
 *
 * @author Hash
 * @since 2020/8/16
 */
public class Consumer {
    public static void main(String[] args) {
        String brokerUrl = "tcp://192.168.3.12:61616";
        String destinationUrl = "ExpirationTestQueue";
        new ConsumerThread(brokerUrl, destinationUrl).start();
    }
}

/**
 * 消费者实现线程类
 *
 * @author Hash
 * @since 2020/8/16
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
            Destination destination = session.createQueue(this.destinationUrl);

            // 5.创建消费者
            // http://activemq.apache.org/destination-options.html
            consumer = session.createConsumer(destination);

            // 6.异步接收消息
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    if (message instanceof  TextMessage) {
                        try {
                            System.out.println(Thread.currentThread().getName() + " 收到文本消息：" + ((TextMessage) message).getText());
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println(message);
                    }
                }
            });
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            // 7.关闭资源
            /*if (consumer != null) {
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
            }*/
        }
    }
}

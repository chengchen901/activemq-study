package com.study.activemq.le2.example.activemq.delay;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ScheduledMessage;

import javax.jms.*;

/**
 * 前提：修改activemq.xml
 * 添加 schedulerSupport="true" 配置
 * <broker xmlns="http://activemq.apache.org/schema/core" brokerName="localhost" dataDirectory="${activemq.data}" schedulerSupport="true">
 *
 * @author Hash
 * @since 2020/8/16
 */
public class Producer {
    public static void main(String[] args) {
        String brokerUrl = "tcp://192.168.3.12:61616";
        String destinationUrl = "delayQueue";
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
        connectionFactory.setUseAsyncSend(true);

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

            // 6.创建文本消息
            // 延时、调度消息
            // 【不可用，这是JMS2.0中的方法】设置producer发送的消息的延迟递送时间
            // producer.setDeliveryDelay(60000L);
            // ActiveMQ 中的方案
            // http://activemq.apache.org/delay-and-schedule-message-delivery.html

            // 延时 5秒
            TextMessage message = session.createTextMessage("Delay message - 1!");
            message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 5 * 1000L);

            // 延时 5秒，投递3次，间隔2秒 (投递次数=重复次数+默认的一次)
            TextMessage message2 = session.createTextMessage("Delay message  - 2!");
            message2.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 5 * 1000L); // 延时
            message2.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_PERIOD, 2 * 1000L); // 投递间隔
            message2.setIntProperty(ScheduledMessage.AMQ_SCHEDULED_REPEAT, 2); // 重复次数

            // CRON表达式说明
            // .-------------------------       minute (0 - 59)
            // |    .--------------------       hour (0 - 23)
            // |    |   .----------------       day of month (1 - 31)
            // |    |   |   .------------       month (1 - 12) - 1 = January
            // |    |   |   |   .--------       day of week (0 - 7) (Sunday = 0 or 7)
            // |    |   |   |   |
            // *    *   *   *   *
            // CRON 表达式的方式
            TextMessage message3 = session.createTextMessage("Delay message - 3!");
            message3.setStringProperty(ScheduledMessage.AMQ_SCHEDULED_CRON, "29 * * * *");

            // CRON 表达式的方式 以及 和上面参数的组合，CRON表达式指定开始时间
            TextMessage message4 = session.createTextMessage("Delay message - 4!");
            message4.setStringProperty(ScheduledMessage.AMQ_SCHEDULED_CRON, "30 * * * *");
            message4.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 1000);
            message4.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_PERIOD, 1000);
            message4.setIntProperty(ScheduledMessage.AMQ_SCHEDULED_REPEAT, 9);

            // 7、发送消息
            producer.send(message);
            producer.send(message2);
            producer.send(message3);
            producer.send(message4);

            System.out.println("Sent delay message: ok");
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
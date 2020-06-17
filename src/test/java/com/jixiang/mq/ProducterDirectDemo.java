package com.jixiang.mq;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 
 * 默认发送，直接将消息发送到某个队列，默认交换机type为direct
 * 
 * @author
 * @date 2019/01/10 11:17:10
 */
public class ProducterDirectDemo {
    public static void main(String[] args) throws IOException, TimeoutException {

        String queneName = "zcj";
        Connection connection = null;
        Channel channel = null;
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("192.168.48.133");
            factory.setPort(15672);
            factory.setUsername("guest");
            factory.setPassword("guest");
            factory.setVirtualHost("test_vhosts");
            // 创建与RabbitMQ服务器的TCP连接
            connection = factory.newConnection();
            // 创建一个频道
            channel = connection.createChannel();
            // 声明默认的队列
            channel.queueDeclare(queneName, true, false, true, null);

            channel.basicPublish("", queneName, null, UUID.randomUUID().toString().getBytes());

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (channel != null) {
                channel.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }
}
package com.losyn.chat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

/**
 * tcp模式下:
 * 如果是同一台机器测试,请注意在
 * TCPPING 元素下修改 initial_hosts的配置端口:
 * 例如:"${jgroups.tcpping.initial_hosts:192.168.19.100[7800],192.168.19.100[7801]}
 * 如果是多台机器测试,请注意在
 * TCPPING 元素下修改 initial_hosts的ip,端口随意:
 * 例如:"${jgroups.tcpping.initial_hosts:192.168.19.100[7800],192.168.19.178[7800]}
 *
 * udp模式下:
 * 同一台机器的不同端口(端口是动态的)可通信.
 * 不同机器之间的ip多播可能会受到一些因素限制而造成节点之间无法彼此发现.
 * </pre>
 */
public class SimpleChat extends ReceiverAdapter {
    private static final String DEFULT_CONFIG_XML = "jgroups-chat-udp.xml";
    // 配置文件.
    private String confXml;
    // 集群名称.
    private static final String CLUSTER_NAME = "mychart";
    // 字符编码
    private static final Charset CHARSET = Charset.defaultCharset();
    // 节点通道.
    private JChannel channel = null;
    public SimpleChat() {
        this.confXml = DEFULT_CONFIG_XML;
    }
    public SimpleChat(String confXml) {
        this.confXml = confXml;
    }

    /**
     * 发送消息
     */
    public void start() {
        try {
            InputStream cfg = SimpleChat.class.getClassLoader().getResourceAsStream(confXml);
            channel = new JChannel(cfg);
            //channel = new JChannel(cfg);
            //连接到集群
            channel.connect(CLUSTER_NAME);
            channel.setDiscardOwnMessages(true);
            //指定Receiver用来收消息和得到View改变的通知
            channel.setReceiver(this);
        }catch (IOException e){
            System.out.println("启动Chat失败！");
            e.printStackTrace();
        }catch (Exception e){
            System.out.println("启动Chat失败！");
            e.printStackTrace();
        }
    }

    public void sendMessage(Address dst, String text){
        try {
            //Message构造函数的第一个参数代表目的地地址，这里传null代表要发消息给集群内的所有地址
            //第二个参数表示源地址，传null即可，框架会自动赋值
            //第三个参数line会被序列化成byte[]然后发送，推荐自己序列化而不是用java自带的序列化
            Message msg = new Message(dst, null, text.getBytes());
            //发消息到集群
            channel.send(msg);
        } catch (Exception e) {
            System.out.println("Chat发送消息失败！");
            e.printStackTrace();
        }
    }

    @Override
    public void receive(Message msg) {
        String s = new String(msg.getBuffer());
        String line = msg.getSrc() + ":" + s;
        System.out.println(line);
    }

    @Override
    public void viewAccepted(View view) {
        System.out.println("A client has changed！" + view.toString());

    }
}
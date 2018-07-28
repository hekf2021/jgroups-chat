package com.losyn.chat;

import java.util.Scanner;

/**
 * 客户端01
 */
public class ChatClient01 {
    private static final String CONFIG_XML = "jgroups-chat-tcp.xml";
    public static void main(String[] args) {
        SimpleChat chat01 = new SimpleChat(CONFIG_XML);
        chat01.start();
        scannerChat(chat01);
    }

    private static void scannerChat(SimpleChat chat01){
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            System.out.flush();
            String line = scanner.next();
            if (line.startsWith("quit") || line.startsWith("exit")) {
                System.exit(-1);
                break;
            }
            chat01.sendMessage(null, line);
        }
    }
}

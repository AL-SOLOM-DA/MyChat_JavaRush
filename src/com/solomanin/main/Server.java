package com.solomanin.main;

import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public void senBroadcastMessage(Message message){
        for (Connection connection : connectionMap.values()) {
            try {
                connection.send(message);
            } catch (IOException e) {
                System.out.println("Сообщение не отправлено. Повторите попытку.");
            }
        }
    }

    public static void main(String[] args)  {
       ConsoleHelper.writeMessage("Введите номер порта:");
       int port = ConsoleHelper.readInt();
       try(ServerSocket serverSocket = new ServerSocket(port)){
           System.out.println("Сервер запущен..");
           while (true){
               Socket socket = serverSocket.accept();
               new Handler(socket).start();
           }
       } catch (IOException e){
           System.out.println("Network error!");
       }


    }

    private static class Handler extends Thread{
        private Socket socket;

        public Handler(Socket socket){
            this.socket = socket;
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {

            while (true){
                connection.send(new Message(MessageType.NAME_REQUEST));
                Message message = connection.receive();
                String name = message.getData();
                if(message.getType()==MessageType.USER_NAME
                        && name!=""
                        && !connectionMap.containsKey(name)){
                    connectionMap.put(name, connection);
                    connection.send(new Message(MessageType.NAME_ACCEPTED));
                    return name;
                } else {
                    continue;
                }
            }
        }

        private void notifyUsers(Connection connection, String userName) throws IOException{
            for (String name : connectionMap.keySet()){
                if(name!=userName){
                    connection.send(new Message(MessageType.USER_ADDED, name));
                }
            }
        }

    }

}
